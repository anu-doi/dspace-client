package au.edu.anu.dspace.client.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.dspace.client.rest.model.AuthenticatedUser;
import au.edu.anu.dspace.client.rest.model.Bitstream;
import au.edu.anu.dspace.client.rest.model.Collection;
import au.edu.anu.dspace.client.rest.model.Community;
import au.edu.anu.dspace.client.rest.model.DSpaceObject;
import au.edu.anu.dspace.client.rest.model.Item;
import au.edu.anu.dspace.client.rest.model.MetadataEntry;
import au.edu.anu.dspace.client.rest.model.UserCredentials;

public class DSpaceRestClient {

	private static final Logger log = LoggerFactory.getLogger(DSpaceRestClient.class);
	
	private Client client;
	private URI baseUri;

	public DSpaceRestClient(Client client, String baseUri) {
		super();
		this.client = client;
		if (!this.client.getConfiguration().isRegistered(JacksonFeature.class)) {
			this.client.register(new JacksonFeature());
		}
		try {
			this.baseUri = new URI(baseUri);
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException(String.format("Invalid DSpace REST Root URI: %s", baseUri));
		}
	}
	
	public void checkService() throws RestClientException {
		WebTarget wt = client.target(getBaseUri()).path("test");

		Response r = wt.request(MediaType.APPLICATION_JSON_TYPE).get();

		validateResponse(wt.getUri(), r, Status.OK);
		String entity = r.readEntity(String.class);
		if (!entity.equals("REST api is running.")) {
			throw new RestClientException();
		}
	}

	public String login(String email, String password) throws RestClientException {
		String authToken;
		
		WebTarget wt = client.target(getBaseUri()).path("login");
		Entity<UserCredentials> authEntity = createAuthEntity(email, password);
		Response r = wt.request(MediaType.APPLICATION_JSON_TYPE).post(authEntity);

		validateResponse(wt.getUri(), r, Status.OK);
		authToken = r.readEntity(String.class);
		return authToken;
	}
	
	public void logout(String authToken) throws RestClientException {
		WebTarget wt = client.target(getBaseUri()).path("logout");
		Builder requestBuilder = addAuthTokenHeader(wt.request(), authToken);
		Response response = requestBuilder.post(null);
		validateResponse(wt.getUri(), response, Status.OK);
	}
	
	public AuthenticatedUser getUserStatus(String authToken) throws RestClientException {
		WebTarget wt = client.target(getBaseUri()).path("status");
		Builder requestBuilder = addAuthTokenHeader(wt.request(MediaType.APPLICATION_JSON_TYPE), authToken);
		Response response = requestBuilder.get();
		validateResponse(wt.getUri(), response, Status.OK);
		
		AuthenticatedUser authenticatedUser;
		try {
			response.bufferEntity();
			authenticatedUser = response.readEntity(AuthenticatedUser.class);
		} catch (ProcessingException e) {
			String entityAsStr = response.readEntity(String.class);
			throw new RestClientException(String.format("Unable to parse: %s", entityAsStr), e);
		}
		return authenticatedUser;
	}
	
	public List<MetadataEntry> getItemMetadata(String authToken, int id) throws RestClientException {
		WebTarget wt = client.target(getBaseUri()).path("items").path(Integer.toString(id)).path("metadata");
		Builder reqBuilder = addAuthTokenHeader(wt.request(MediaType.APPLICATION_JSON_TYPE), authToken);
		Response response = reqBuilder.get();
		validateResponse(wt.getUri(), response, Status.OK);
		
		List<MetadataEntry> metadata;
		try {
			response.bufferEntity();
			metadata = response.readEntity(new GenericType<List<MetadataEntry>>() {});
		} catch (ProcessingException e) {
			String entityAsStr = response.readEntity(String.class);
			throw new RestClientException(String.format("Unable to parse: %s", entityAsStr), e);
		}
		return metadata;
	}
	
	public DSpaceObject getHandle(String authToken, String handle) throws RestClientException {
		WebTarget wt = client.target(getBaseUri()).path("handle").path(handle);
		Builder requestBuilder = addAuthTokenHeader(wt.request(MediaType.APPLICATION_JSON_TYPE), authToken);
		Response response = requestBuilder.get();
		validateResponse(wt.getUri(), response, Status.OK);
		
		DSpaceObject dspaceObj;
		try {
			response.bufferEntity();
			dspaceObj = response.readEntity(DSpaceObject.class);
			if (dspaceObj.getType() == DSpaceObject.Type.COMMUNITY) {
				dspaceObj = response.readEntity(Community.class);
			} else if (dspaceObj.getType() == DSpaceObject.Type.COLLECTION) {
				dspaceObj = response.readEntity(Collection.class);
			} else if (dspaceObj.getType() == DSpaceObject.Type.ITEM) {
				dspaceObj = response.readEntity(Item.class);
			} else if (dspaceObj.getType() == DSpaceObject.Type.BITSTREAM) {
				dspaceObj = response.readEntity(Bitstream.class);
			}
		} catch (ProcessingException e) {
			String entityAsStr = response.readEntity(String.class);
			throw new RestClientException(String.format("Unable to parse: %s", entityAsStr), e);
		}
		return dspaceObj;
	}
	

	public URI getBaseUri() throws RestClientException {
		return baseUri;
	}

	private Entity<UserCredentials> createAuthEntity(String email, String password) {
		UserCredentials userCredentials = new UserCredentials(email, password);
		Entity<UserCredentials> entity = Entity.json(userCredentials);
		return entity;
	}

	private Builder addAuthTokenHeader(Builder requestBuilder, String authToken) {
		return requestBuilder.header("rest-dspace-token", authToken);
	}

	private void validateResponse(URI uri, Response response, Status expectedStatus) throws RestClientException {
		if (response == null) {
			throw new RestClientException(String.format("No response recieved from %s", uri.toString()));
		}
		if (!response.getStatusInfo().equals(expectedStatus)) {
			throw new RestClientException(String.format("Return code from %s is %s(%s). Expected %s(%s)", uri.toString(),
					response.getStatusInfo().getReasonPhrase(), response.getStatusInfo().getStatusCode(),
					expectedStatus.getReasonPhrase(), expectedStatus.getStatusCode()));
		}
	}
	
}
