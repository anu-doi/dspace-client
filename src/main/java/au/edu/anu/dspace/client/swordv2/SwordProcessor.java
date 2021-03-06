package au.edu.anu.dspace.client.swordv2;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

import javax.xml.namespace.QName;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;
import org.swordapp.client.Deposit;
import org.swordapp.client.DepositReceipt;
import org.swordapp.client.EntryPart;
import org.swordapp.client.SWORDClient;
import org.swordapp.client.SWORDCollection;
import org.swordapp.client.SWORDWorkspace;
import org.swordapp.client.ServiceDocument;
import org.swordapp.client.SwordResponse;
import org.swordapp.client.UriRegistry;

import au.edu.anu.dspace.client.WorkflowException;
import au.edu.anu.dspace.client.swordv2.data.BitstreamInfo;
import au.edu.anu.dspace.client.swordv2.data.SwordMetadata;
import au.edu.anu.dspace.client.swordv2.data.SwordRequestData;
import au.edu.anu.dspace.client.swordv2.data.SwordRequestDataProvider;
import au.edu.anu.dspace.client.swordv2.tasks.AddToMediaResourceTask;
import au.edu.anu.dspace.client.swordv2.tasks.ChangeInProgressTask;
import au.edu.anu.dspace.client.swordv2.tasks.DepositTask;
import au.edu.anu.dspace.client.swordv2.tasks.Md5CalcTask;
import au.edu.anu.dspace.client.utils.ProgressInputStream;
import au.edu.anu.dspace.client.utils.StdOutHandler;

public class SwordProcessor {

	private StdOutHandler stdOutHandler = new StdOutHandler();
	
	private final SWORDClient swordClient;
	private final SwordServerInfo serverInfo;
	private final SwordRequestDataProvider provider;

	public SwordProcessor(SWORDClient swordClient, SwordServerInfo serverInfo, SwordRequestDataProvider provider) {
		this.swordClient = Objects.requireNonNull(swordClient);
		this.serverInfo = Objects.requireNonNull(serverInfo);
		this.provider = Objects.requireNonNull(provider);
	}

	/**
	 * Performs the following in order:
	 * 
	 * <ol>
	 * <li>Retrieves Service Document from a Swordv2 Service
	 * <li>Iterates over the SwordRequestData collection returned by provider
	 * and creates and submits Sword requests
	 * </ol>
	 * 
	 * @throws WorkflowException
	 */
	public void process() throws WorkflowException {
		// retrieve service document
		ServiceDocument sd = retrieveServiceDoc();

		// check if there are any sword requests to be processed
		int nTotal = provider.getSwordRequests().size();
		if (nTotal == 0) {
			print("No Sword Requests to be processed. Exiting.");
			return;
		}
		
		displaySwordRequests();

		validateSwordRequests(sd);

		int nSuccess = 0;
		int nErrors = 0;
		for (SwordRequestData iData : provider.getSwordRequests()) {
			try {
				String editLink = null;
				String editMediaLink = null;

				if ((iData.getCollectionName() != null)
						&& (iData.getMetadata() != null && !iData.getMetadata().isEmpty())) {
					// create a resource if collection name and title is
					// provided
					SWORDCollection collection = getCollectionNamed(sd, iData.getCollectionName());
					Deposit metadataDeposit = createMetadataDeposit(iData.getMetadata());
					DepositReceipt depositReceipt = depositResource(collection, metadataDeposit,
							iData.getMetadata().getTitle());
					editLink = depositReceipt.getEditLink().getHref();
					editMediaLink = depositReceipt.getEditMediaLink().getHref();
				} else if (iData.getEditMediaLink() != null) {
					// read resource url from command line parameters.
					editMediaLink = iData.getEditMediaLink();
					if (iData.getMetadata() != null) {
						Deposit metadataDeposit = createMetadataDeposit(iData.getMetadata());
						DepositReceipt depositReceipt = depositResource(editMediaLink, metadataDeposit,
								iData.getMetadata().getTitle());
					}
				}

				// submit files to media link url
				if (editMediaLink != null) {
					Map<BitstreamInfo, SwordResponse> bsDepositResponses = depositFiles(editMediaLink,
							iData.getBitstreams());

					// check for errors when uploading bitstreams
					Set<BitstreamInfo> erroredBitstreams = new LinkedHashSet<>(bsDepositResponses.size());
					for (Entry<BitstreamInfo, SwordResponse> resp : bsDepositResponses.entrySet()) {
						if (resp.getValue() == null || resp.getValue().getStatusCode() != 201) {
							erroredBitstreams.add(resp.getKey());
						}
					}
					if (!erroredBitstreams.isEmpty()) {
						throw new WorkflowException("Error uploading files: " + erroredBitstreams.toString());
					}
				}

				// change in progress flag if required
				if (editLink != null && !iData.isInProgress()) {
					changeInProgress(editLink);
				}

				// let provider know that the data was successfully uploaded
				provider.updateRequestStatus(iData, true);
				nSuccess++;
			} catch (WorkflowException e) {
				nErrors++;
				print(e.getMessage());
				e.printStackTrace();
			}
		}

		String summaryMsg = "%d success. %d errors. %d total.";
		stdOutHandler.println("**************");
		stdOutHandler.println(summaryMsg, nSuccess, nErrors, nTotal);
		stdOutHandler.println("**************");
		if (nErrors > 0) {
			throw new WorkflowException();
		}
	}

	private ServiceDocument retrieveServiceDoc() throws WorkflowException {
		ServiceDocument sd = null;
		try {
			for (int i = 0; sd == null && i < 10; i++) {
				if (i > 0) {
					print("Service document retrieval failed. Retrying in 5 seconds...");
					Thread.sleep(5000L);
				}
				print("Retrieving service document from %s...", this.serverInfo.getServiceDocUrl());
				sd = swordClient.getServiceDocument(this.serverInfo.getServiceDocUrl(), this.serverInfo.createAuth());
			}
			print("Retrieved service document from %s", this.serverInfo.getServiceDocUrl());
			print("Sword version: %s, Max upload size: %d. Available workspaces: %d", sd.getVersion(),
					sd.getMaxUploadSize(), sd.getWorkspaces().size());
			for (SWORDWorkspace iWorkspace : sd.getWorkspaces()) {
				print("Workspace [%s] contains %d collections.", iWorkspace.getTitle(),
						iWorkspace.getCollections().size());
			}
			logAllCollections(sd);
		} catch (Exception e) {
			print("Error retrieving service document from %s", this.serverInfo.getServiceDocUrl());
			throw new WorkflowException(e);
		}
		return sd;
	}

	private void displaySwordRequests() {
		int i = 1;
		for (SwordRequestData iData : provider.getSwordRequests()) {
			print("Item %d/%d: %s", i, provider.getSwordRequests().size(), (iData.getMetadata() != null && iData.getMetadata().getTitle() != null) ? iData.getMetadata().getTitle() : "[No Title]");
			print("\tTarget Collection: %s", iData.getCollectionName());
			if (iData.getMetadata() != null) {
				for (Entry<String, List<String>> entry : iData.getMetadata().entrySet()) {
					if (entry.getValue().size() == 1) {
						print("\t%s: %s", entry.getKey(), truncateLongValue(entry.getValue().get(0), 80));
					} else if (entry.getValue().size() > 1) {
						print("\t%s:", entry.getKey());
						for (String value : entry.getValue()) {
							print("\t\t%s", truncateLongValue(value, 80));
						}
					}
				}
			}
			
			int bi = 1;
			print("\tBITSTREAMS: %d", iData.getBitstreams().size());
			for (BitstreamInfo bitstreamInfo : iData.getBitstreams()) {
				print("\t\t%d: %s, %d bytes", bi, bitstreamInfo.getFilepath(), bitstreamInfo.getSize());
				bi++;
			}
			
			i++;
		}
	}
	
	private String truncateLongValue(String val, int nChars) {
		final String ellipsis = "...";
		if (val.length() <= nChars) {
			return val;
		} else {
			StringBuilder truncated = new StringBuilder(val.substring(0, nChars - ellipsis.length()));
			truncated.append(ellipsis);
			return truncated.toString();
		}
		
	}

	/**
	 * Checks that each sword request is for a collection that exists and that
	 * the user that'll upload using Sword has write access to that Collection.
	 * 
	 * @param sd
	 *            service document with list of collections to which the user's
	 *            got write access
	 * @throws WorkflowException
	 *             if service document doesn't contain a collection specified by
	 *             a swordrequest
	 */
	private void validateSwordRequests(ServiceDocument sd) throws WorkflowException {
		List<String> validationIssues = new ArrayList<>();
		// check that collections specified in all requests exist and user has
		// submit permissions
		for (SwordRequestData iData : provider.getSwordRequests()) {
			try {
				getCollectionNamed(sd, iData.getCollectionName());
			} catch (WorkflowException e) {
				if (!validationIssues.contains(e.getMessage())) {
					validationIssues.add(e.getMessage());
				}
			}
		}

		if (!validationIssues.isEmpty()) {
			print("Validation results:");
			for (String issue : validationIssues) {
				print("\t%s", issue);
			}
			throw new WorkflowException(validationIssues.toString());
		}
	}

	private Deposit createMetadataDeposit(SwordMetadata metadata) throws WorkflowException {
		Deposit resourceDeposit = new Deposit();
		EntryPart ep = new EntryPart();

		for (Entry<String, List<String>> metadataEntry : metadata.entrySet()) {
			for (String value : metadataEntry.getValue()) {
				QName qName = createQName(metadataEntry.getKey());
				ep.addSimpleExtension(qName, value);
			}
		}

		resourceDeposit.setEntryPart(ep);
		resourceDeposit.setInProgress(true);
		return resourceDeposit;
	}

	private DepositReceipt depositResource(SWORDCollection collection, Deposit resource, String title)
			throws WorkflowException {
		DepositTask depTask = new DepositTask(swordClient, this.serverInfo, collection, resource);
		DepositReceipt depositReceipt = null;
		try {
			print("Creating item: title=[%s]...", title);
			depositReceipt = depTask.call();
			print("Created at %s with status code %d.", depositReceipt.getLocation(),
					depositReceipt.getStatusCode());
		} catch (Exception e) {
			throw new WorkflowException(e);
		}
		return depositReceipt;
	}

	private DepositReceipt depositResource(String editUri, Deposit resource, String title) throws WorkflowException {
		DepositTask depTask = new DepositTask(swordClient, this.serverInfo, editUri, resource);
		DepositReceipt depositReceipt = null;
		try {
			print("Creating item: title=[%s]...", title);
			depositReceipt = depTask.call();
			print("Created at %s with status code %d.", depositReceipt.getLocation(),
					depositReceipt.getStatusCode());
		} catch (Exception e) {
			throw new WorkflowException(e);
		}
		return depositReceipt;
	}

	private Map<BitstreamInfo, SwordResponse> depositFiles(String editMediaLink, Collection<BitstreamInfo> bitstreams) {
		Map<BitstreamInfo, SwordResponse> responses;
		if (bitstreams != null) {
			responses = new LinkedHashMap<>(bitstreams.size());
			int count = 1;
			for (BitstreamInfo bitstreamInfo : bitstreams) {
				InputStream bitstream = null;
				try {
					long sizeBytes = bitstreamInfo.getSize();
					String fmtSize = MessageFormat.format("{0,number,#,000} KB",
							new Object[] { Long.valueOf(sizeBytes / 1024L) });

					// calculate MD5
					print("Calculating MD5 file=%s size=%s ...", bitstreamInfo.getFilename(), fmtSize);
					bitstream = createInputStream(bitstreamInfo.getFile());
					Md5CalcTask md5Task = new Md5CalcTask(bitstream);
					String md5 = Hex.encodeHexString(md5Task.call()).toLowerCase();
					IOUtils.closeQuietly(bitstream);

					print("Uploading file=%s size=%s:MD5=%s [%d/%d]...", new Object[] { bitstreamInfo.getFilename(),
							fmtSize, md5, count, bitstreams.size() });

					// create bitstream deposit object
					Deposit bsDeposit = new Deposit();
					bsDeposit.setFilename(bitstreamInfo.getFilename());
					bsDeposit.setContentLength(bitstreamInfo.getSize());
					bsDeposit.setInProgress(true);
					bsDeposit.setMd5(md5);
					String mimeType = Files.probeContentType(bitstreamInfo.getFile());
					if (mimeType != null) {
						bsDeposit.setMimeType(mimeType);
					} else {
						bsDeposit.setMimeType("application/octet-stream");
					}
					bsDeposit.setPackaging(UriRegistry.PACKAGE_BINARY);
					bitstream = createInputStream(bitstreamInfo.getFile());
					bsDeposit.setFile(bitstream);

					SwordResponse bsDepositReceipt = null;
					for (int attempt = 0, maxTries = 3;; attempt++) {
						try {
							AddToMediaResourceTask atmrTask = new AddToMediaResourceTask(swordClient, this.serverInfo,
									editMediaLink, bsDeposit);
							bsDepositReceipt = atmrTask.call();
							break;
						} catch (Exception e) {
							if (attempt < maxTries) {
								print("Attempt to upload %s failed. Error: %s Retrying...", bitstreamInfo.getFilename(), e.getMessage());
								Thread.sleep(10000L);
							} else {
								throw e;
							}
						}
					}
					responses.put(bitstreamInfo, bsDepositReceipt);
					print("Uploaded %s. Status:%d, MD5:%s", bitstreamInfo.getFilename(),
							bsDepositReceipt.getStatusCode(), bsDepositReceipt.getContentMD5());
					
				} catch (Exception e) {
					responses.put(bitstreamInfo, null);
					print("Unable to upload %s: %s", bitstreamInfo.getFilepath(), e.getMessage());
					e.printStackTrace();
				} finally {
					IOUtils.closeQuietly(bitstream);
					count++;
				}
			}
		} else {
			responses = new LinkedHashMap<>(0);
		}
		return responses;
	}

	private SwordResponse changeInProgress(String editLink) throws WorkflowException {
		SwordResponse response = null;
		try {
			ChangeInProgressTask cipTask = new ChangeInProgressTask(swordClient, serverInfo, editLink);
			response = cipTask.call();
		} catch (Exception e) {
			print("Unable to change In Progress flag at %s", editLink);
			throw new WorkflowException(e);
		}
		return response;
	}

	/**
	 * Get the collection with the specified name.
	 * 
	 * @param sd
	 *            ServiceDocument
	 * @param collectionName
	 *            Name of collection to retrieve
	 * @return Collection as SWORDCollection
	 * @throws WorkflowException
	 */
	private SWORDCollection getCollectionNamed(ServiceDocument sd, String collectionName) throws WorkflowException {
		SWORDCollection collection = null;
		if (collectionName != null) {
			if (collectionName.matches("^\\d{4,}/\\d+$")) {
				// look for collection with specified handle
				for (SWORDWorkspace iWorkspace : sd.getWorkspaces()) {
					for (SWORDCollection iColl : iWorkspace.getCollections()) {
						if (iColl != null && iColl.getTitle() != null
								&& iColl.getHref().toASCIIString().endsWith("/" + collectionName)) {
							collection = iColl;
							break;
						}
					}
				}
			} else {
				// look for collection with specified name
				for (SWORDWorkspace iWorkspace : sd.getWorkspaces()) {
					for (SWORDCollection iColl : iWorkspace.getCollections()) {
						if (iColl != null && iColl.getTitle() != null
								&& iColl.getTitle().equalsIgnoreCase(collectionName)) {
							collection = iColl;
							break;
						}
					}
				}
			}

			if (collection == null) {
				throw new WorkflowException(String.format("Collection [%s] not found", collectionName));
			}
		}
		return collection;
	}

	private void logAllCollections(ServiceDocument sd) {
		for (SWORDWorkspace iWorkspace : sd.getWorkspaces()) {
			for (SWORDCollection iCollection : iWorkspace.getCollections()) {
				if (iCollection.getTitle() != null && iCollection.getTitle().trim().length() > 0) {
					print("\t%s <%s>", iCollection.getTitle(), iCollection.getHref().toString());
				}
			}
		}
	}

	private QName createQName(String fqFieldname) {
		QName term = null;

		if (fqFieldname.indexOf('.') == -1) {
			term = new QName(UriRegistry.DC_NAMESPACE, fqFieldname);
		} else {
			String[] parts = fqFieldname.split("\\.", 2);
			String namespaceName = parts[0];
			String field = parts[1];

			if (namespaceName.equalsIgnoreCase("dc")) {
				term = new QName(UriRegistry.DC_NAMESPACE, field);
			} else if (namespaceName.equalsIgnoreCase("dcterms")) {
				term = new QName(UriRegistry.DC_NAMESPACE, field);
			} else if (namespaceName.equalsIgnoreCase("anu")) {
				term = new QName("http://anu.edu.au/dspace/", field);
			}
		}
		return term;
	}

	private void print(String format, Object... args) {
		System.out.format(format, args);
		System.out.println();
	}

	private InputStream createInputStream(Path file) throws IOException {
		long totalBytes = Files.size(file);
		ProgressInputStream fileStream = new ProgressInputStream(Files.newInputStream(file), totalBytes);
		fileStream.addPropertyChangeListener(new PropertyChangeListener() {
			
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals("percentComplete")) {
					int oldValue = ((Integer) evt.getOldValue()).intValue();
					int newValue = ((Integer) evt.getNewValue()).intValue();
					if (newValue > oldValue) {
						stdOutHandler.eraseLastPrinted();
						stdOutHandler.print("%d%%", newValue);
						// System.out.print(newValue + "%\r");
						if (newValue == 100) {
							// System.out.println();
							stdOutHandler.println();
						}
					}
				}
			}
		});
		return fileStream;
	}

}