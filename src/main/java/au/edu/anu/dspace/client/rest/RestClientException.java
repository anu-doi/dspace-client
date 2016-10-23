/**
 * 
 */
package au.edu.anu.dspace.client.rest;

/**
 * @author Rahul Khanna
 *
 */
public class RestClientException extends Exception {

	private static final long serialVersionUID = 2853550695241641119L;

	public RestClientException() {
		super();
	}

	public RestClientException(String message, Throwable cause) {
		super(message, cause);
	}

	public RestClientException(String message) {
		super(message);
	}

	public RestClientException(Throwable cause) {
		super(cause);
	}

	
}
