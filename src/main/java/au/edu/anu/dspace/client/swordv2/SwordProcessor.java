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
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

import javax.xml.namespace.QName;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

public class SwordProcessor {
	private static final Logger log = LoggerFactory.getLogger(SwordProcessor.class);

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
		
		int nTotal = provider.getSwordRequests().size();
		if (nTotal == 0) {
			log.info("No Sword Requests to be processed.");
		}

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
					DepositReceipt depositReceipt = depositResource(collection, metadataDeposit, iData.getMetadata().getTitle());
					editLink = depositReceipt.getEditLink().getHref();
					editMediaLink = depositReceipt.getEditMediaLink().getHref();
				} else if (iData.getEditMediaLink() != null) {
					// read resource url from command line parameters.
					editMediaLink = iData.getEditMediaLink();
					Deposit metadataDeposit = createMetadataDeposit(iData.getMetadata());
					DepositReceipt depositReceipt = depositResource(editMediaLink, metadataDeposit, iData.getMetadata().getTitle());
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
				log.error(e.getMessage(), e);
			}
		}

		String summaryMsg = "{} success. {} errors. {} total.";
		if (nErrors > 0) {
			log.warn(summaryMsg, nSuccess, nErrors, nTotal);
			throw new WorkflowException();
		} else {
			log.info(summaryMsg, nSuccess, nErrors, nTotal);
		}
	}


	private ServiceDocument retrieveServiceDoc() throws WorkflowException {
		ServiceDocument sd = null;
		try {
			log.info("Retrieving service document from {}...", this.serverInfo.getServiceDocUrl());
			sd = swordClient.getServiceDocument(this.serverInfo.getServiceDocUrl(), this.serverInfo.createAuth());
			log.info("Retrieved service document from {}", this.serverInfo.getServiceDocUrl());
			log.info("Sword version: {}, Max upload size: {}. Available workspaces: {}", sd.getVersion(),
					Long.valueOf(sd.getMaxUploadSize()), Integer.valueOf(sd.getWorkspaces().size()));
			for (SWORDWorkspace iWorkspace : sd.getWorkspaces()) {
				log.info("Workspace [{}] contains {} collections.", iWorkspace.getTitle(), Integer.valueOf(iWorkspace.getCollections().size()));
			}
		} catch (Exception e) {
			log.error("Error retrieving service document from {}", this.serverInfo.getServiceDocUrl());
			throw new WorkflowException(e);
		}
		return sd;
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
		// check that collections specified in all requests exist and user has submit permissions 
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
			log.info("Creating item: title=[{}]...", title);
			depositReceipt = depTask.call();
			log.info("Created at {} with status code {}.", depositReceipt.getLocation(),
					Integer.valueOf(depositReceipt.getStatusCode()));
		} catch (Exception e) {
			throw new WorkflowException(e);
		}
		return depositReceipt;
	}
	
	private DepositReceipt depositResource(String editUri, Deposit resource, String title) throws WorkflowException {
		DepositTask depTask = new DepositTask(swordClient, this.serverInfo, editUri, resource);
		DepositReceipt depositReceipt = null;
		try {
			log.info("Creating item: title=[{}]...", title);
			depositReceipt = depTask.call();
			log.info("Created at {} with status code {}.", depositReceipt.getLocation(),
					Integer.valueOf(depositReceipt.getStatusCode()));
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
					log.info("Calculating MD5 file={} size={} ...", bitstreamInfo.getFilename(), fmtSize);
					bitstream = createInputStream(bitstreamInfo.getFile());
					Md5CalcTask md5Task = new Md5CalcTask(bitstream);
					String md5 = Hex.encodeHexString(md5Task.call()).toLowerCase();
					IOUtils.closeQuietly(bitstream);

					log.info("Uploading file={} size={}:MD5={} [{}/{}]...", new Object[] { bitstreamInfo.getFilename(),
							fmtSize, md5, Integer.valueOf(count), Integer.valueOf(bitstreams.size()) });

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
					for (int attempt = 0, maxTries = 3; ; attempt++) {
						try {
							AddToMediaResourceTask atmrTask = new AddToMediaResourceTask(swordClient, this.serverInfo,
									editMediaLink, bsDeposit);
							bsDepositReceipt = atmrTask.call();
							break;
						} catch (Exception e) {
							if (attempt < maxTries) {
								log.warn("Attempt to upload {} failed. Retrying...", bitstreamInfo.getFilename());
								Thread.sleep(10000L);
							} else {
								throw e;
							}
						}
					}
					responses.put(bitstreamInfo, bsDepositReceipt);
					log.info("Uploaded {}. Status:{}, MD5:{}", bitstreamInfo.getFilename(),
							Integer.valueOf(bsDepositReceipt.getStatusCode()), bsDepositReceipt.getContentMD5());
				} catch (Exception e) {
					responses.put(bitstreamInfo, null);
					log.error("Unable to upload " + bitstreamInfo.getFilepath() + ". " + e.getMessage(), e);
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
			log.error("Unable to change In Progress flag at {}", editLink);
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
						if (iColl != null && iColl.getTitle() != null && iColl.getHref().toASCIIString().endsWith("/" + collectionName)) {
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
				logAllCollections(sd);
				throw new WorkflowException("Submitter doesn't have access to collection [" + collectionName
						+ "] or collection doesn't exist.");
			}
		}
		return collection;
	}


	private void logAllCollections(ServiceDocument sd) {
		for (SWORDWorkspace iWorkspace : sd.getWorkspaces()) {
			for (SWORDCollection iCollection : iWorkspace.getCollections()) {
				if (iCollection.getTitle() != null && iCollection.getTitle().trim().length() > 0) {
					log.info("\t{} {}", iCollection.getTitle(), iCollection.getHref().toString());
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

	private InputStream createInputStream(Path file) throws IOException {
		long totalBytes = Files.size(file);
		ProgressInputStream fileStream = new ProgressInputStream(Files.newInputStream(file), totalBytes);
		fileStream.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals("percentComplete")) {
					int oldValue = ((Integer) evt.getOldValue()).intValue();
					int newValue = ((Integer) evt.getNewValue()).intValue();
					if (newValue > oldValue) {
						System.out.print(newValue + "%\r");
						if (newValue == 100) {
							System.out.println();
						}
					}
				}
			}
		});
		return fileStream;
	}
}