/**
 * 
 */
package au.edu.anu.dspace.client.rest;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import au.edu.anu.dspace.client.JobHandler;
import au.edu.anu.dspace.client.config.Config;
import au.edu.anu.dspace.client.rest.model.DSpaceObject;
import au.edu.anu.dspace.client.rest.model.MetadataEntry;

/**
 * @author Rahul Khanna
 *
 */
public class RestHandler implements JobHandler {
	
	private DSpaceRestClient restClient;

	public RestHandler() {
	}
	
	/* (non-Javadoc)
	 * @see au.edu.anu.dspace.client.JobHandler#handle(au.edu.anu.dspace.client.config.Config, java.lang.String[])
	 */
	@Override
	public int handle(Config config, String[] args) {

		String restBaseUri = config.getProperty("rest.baseUri");
		String username = config.getProperty("rest.username");
		String password = config.getProperty("rest.password");
		
		if (restClient == null) {
			Client client = ClientBuilder.newClient();
			restClient = new DSpaceRestClient(client, restBaseUri);
		}
		
		String authToken = null;
		String handle;
		try {
			switch (args[0]) {
			
			case "checkservice":
				checkRestService();
				break;
				
			case "handle":
				handle = args[1];
				authToken = restClient.login(username, password);
				getHandle(authToken, handle);
				break;
				
			case "getmetadata":
				authToken = restClient.login(username, password);
				getMetadata(authToken, args[1]);
				break;
				
			case "addbitstream":
				authToken = restClient.login(username, password);
				Path fileToUpload = Paths.get(args[1]);
				addBitstream(authToken, args[1], fileToUpload.getFileName().toString(), null, fileToUpload);
				break;
			}
			return 0;
		} catch (RestClientException | IOException e) {
			print(e.getMessage());
			e.printStackTrace();
			return 1;
		} finally {
			if (authToken != null) {
				try {
					restClient.logout(authToken);
				} catch (RestClientException e) {
					print("Unable to logoff");
				}
			}
		}
	}

	private void checkRestService() throws RestClientException {
		restClient.checkService();
		print("DSpace REST Service available at %s", restClient.getBaseUri().toString());
	}

	private void getHandle(String authToken, String handle) throws RestClientException {
		DSpaceObject dspaceObject = restClient.getHandle(authToken, handle);
		print(dspaceObject.toString());
	}
	
	private void getMetadata(String authToken, String handleOrId) throws RestClientException {
		int id;
		if (handleOrId.contains("/")) {
			String handle = handleOrId;
			DSpaceObject dspaceObject = restClient.getHandle(authToken, handle);
			id = dspaceObject.getId();
		} else {
			id = Integer.parseInt(handleOrId);
		}
		List<MetadataEntry> itemMetadata = restClient.getItemMetadata(authToken, id);
		for (MetadataEntry metadataEntry : itemMetadata) {
			print("%s: %s", metadataEntry.getKey(), metadataEntry.getValue());
		}
	}

	private void addBitstream(String authToken, String handleOrId, String filename, String description, Path filepath)
			throws RestClientException, IOException {
		int id;
		if (handleOrId.contains("/")) {
			String handle = handleOrId;
			DSpaceObject dspaceObject = restClient.getHandle(authToken, handle);
			id = dspaceObject.getId();
		} else {
			id = Integer.parseInt(handleOrId);
		}
		restClient.addBitstream(authToken, id, filename, description, filepath);
	}

	private void print(String format, Object... args) {
		System.out.format(format, args);
		System.out.println();
	}
}
