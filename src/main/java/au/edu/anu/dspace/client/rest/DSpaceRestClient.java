package au.edu.anu.dspace.client.rest;

import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Objects;

import javax.ws.rs.HttpMethod;
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

		Response r = submitGetRequest(wt.request(MediaType.APPLICATION_JSON_TYPE));

		validateResponse(wt.getUri(), r, Status.OK);
		String entity = r.readEntity(String.class);
		log.debug("{} {} returned {}", HttpMethod.GET, wt.getUri().toString(), entity);
		if (!entity.equals("REST api is running.")) {
			throw new RestClientException();
		}
	}

	public String login(String email, String password) throws RestClientException {
		String authToken;
		
		WebTarget wt = client.target(getBaseUri()).path("login");
		Entity<UserCredentials> authEntity = createAuthEntity(email, password);
		Response r = submitPostRequest(wt.request(MediaType.APPLICATION_JSON_TYPE), authEntity);

		validateResponse(wt.getUri(), r, Status.OK);
		authToken = r.readEntity(String.class);
		log.debug("{} {} returned {}", HttpMethod.POST, wt.getUri().toString(), authToken);
		return authToken;
	}
	
	public void logout(String authToken) throws RestClientException {
		WebTarget wt = client.target(getBaseUri()).path("logout");
		Builder requestBuilder = addAuthTokenHeader(wt.request(), authToken);
		Response r = submitPostRequest(requestBuilder, null);
		validateResponse(wt.getUri(), r, Status.OK);
		log.debug("{} {} returned {}", HttpMethod.POST, wt.getUri().toString(), r.getStatus());
	}
	
	public AuthenticatedUser getUserStatus(String authToken) throws RestClientException {
		WebTarget wt = client.target(getBaseUri()).path("status");
		Builder reqBuilder = addAuthTokenHeader(wt.request(MediaType.APPLICATION_JSON_TYPE), authToken);
		Response r = submitGetRequest(reqBuilder);
		validateResponse(wt.getUri(), r, Status.OK);
		
		AuthenticatedUser authenticatedUser;
		try {
			r.bufferEntity();
			authenticatedUser = r.readEntity(AuthenticatedUser.class);
			log.debug("{} {} returned {}", HttpMethod.GET, wt.getUri().toString(), r.readEntity(String.class));
		} catch (ProcessingException e) {
			String entityAsStr = r.readEntity(String.class);
			throw new RestClientException(String.format("Unable to parse: %s", entityAsStr), e);
		}
		return authenticatedUser;
	}
	
	public List<MetadataEntry> getItemMetadata(String authToken, int id) throws RestClientException {
		WebTarget wt = client.target(getBaseUri()).path("items").path(Integer.toString(id)).path("metadata");
		Builder reqBuilder = addAuthTokenHeader(wt.request(MediaType.APPLICATION_JSON_TYPE), authToken);
		Response r = submitGetRequest(reqBuilder);
		validateResponse(wt.getUri(), r, Status.OK);
		
		List<MetadataEntry> metadata;
		try {
			r.bufferEntity();
			metadata = r.readEntity(new GenericType<List<MetadataEntry>>() {});
			log.debug("{} {} returned {}", HttpMethod.GET, wt.getUri().toString(), r.readEntity(String.class));
		} catch (ProcessingException e) {
			String entityAsStr = r.readEntity(String.class);
			throw new RestClientException(String.format("Unable to parse: %s", entityAsStr), e);
		}
		return metadata;
	}
	
	public void addBitstream(String authToken, int id, String name, String description, Path filepath) throws RestClientException, IOException {
		WebTarget wt = client.target(getBaseUri()).path("items").path(Integer.toString(id)).path("bitstreams");
		wt = wt.queryParam("name", name);
		wt = wt.queryParam("description", description);
		Builder reqBuilder = addAuthTokenHeader(wt.request(MediaType.APPLICATION_OCTET_STREAM_TYPE), authToken);
		Entity<InputStream> bitstreamEntity;
		bitstreamEntity = Entity.entity(Files.newInputStream(filepath, StandardOpenOption.READ), MediaType.APPLICATION_OCTET_STREAM_TYPE);
		Response r = submitPostRequest(reqBuilder, bitstreamEntity);

		try {
			r.bufferEntity();
			r.readEntity(String.class);
			log.debug("{} {} returned {}", HttpMethod.GET, wt.getUri().toString(), r.readEntity(String.class));
		} catch (ProcessingException e) {
			String entityAsStr = r.readEntity(String.class);
			throw new RestClientException(String.format("Unable to parse: %s", entityAsStr), e);
		}
	}

	public DSpaceObject getHandle(String authToken, String handle) throws RestClientException {
		WebTarget wt = client.target(getBaseUri()).path("handle").path(handle);
		Builder reqBuilder = addAuthTokenHeader(wt.request(MediaType.APPLICATION_JSON_TYPE), authToken);
		Response r = submitGetRequest(reqBuilder);
		validateResponse(wt.getUri(), r, Status.OK);
		
		DSpaceObject dspaceObj;
		try {
			r.bufferEntity();
			dspaceObj = r.readEntity(DSpaceObject.class);
			if (dspaceObj.getType() == DSpaceObject.Type.COMMUNITY) {
				dspaceObj = r.readEntity(Community.class);
			} else if (dspaceObj.getType() == DSpaceObject.Type.COLLECTION) {
				dspaceObj = r.readEntity(Collection.class);
			} else if (dspaceObj.getType() == DSpaceObject.Type.ITEM) {
				dspaceObj = r.readEntity(Item.class);
			} else if (dspaceObj.getType() == DSpaceObject.Type.BITSTREAM) {
				dspaceObj = r.readEntity(Bitstream.class);
			}
			log.debug("{} {} returned {}", HttpMethod.GET, wt.getUri().toString(), r.readEntity(String.class));
		} catch (ProcessingException e) {
			String entityAsStr = r.readEntity(String.class);
			throw new RestClientException(String.format("Unable to parse: %s", entityAsStr), e);
		}
		return dspaceObj;
	}
	
	public URI getBaseUri() throws RestClientException {
		return baseUri;
	}

	private Response submitGetRequest(Builder b) throws RestClientException {
		Response r = null;
		try {
			r = b.get();
		} catch (ProcessingException e) {
			createRestClientException(e);
		}
		return Objects.requireNonNull(r);
	}
	
	private Response submitPostRequest(Builder b, Entity<?> entity) throws RestClientException {
		Response r = null;
		try {
			r = b.post(entity);
		} catch (ProcessingException e) {
			createRestClientException(e);
		}
		return Objects.requireNonNull(r);
	}
	
	private void createRestClientException(ProcessingException e) throws RestClientException {
		if (e.getCause() instanceof ConnectException) {
			throw new RestClientException(e.getMessage(), e.getCause());
		}
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
