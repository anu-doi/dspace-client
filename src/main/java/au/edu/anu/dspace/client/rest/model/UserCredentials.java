/**
 * 
 */
package au.edu.anu.dspace.client.rest.model;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Rahul Khanna
 *
 */
@XmlRootElement
public class UserCredentials {

	private String email;
	private String password;

	public UserCredentials(String email, String password) {
		super();
		this.email = email;
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public String getPassword() {
		return password;
	}

	@Override
	public String toString() {
		return String.format("UserCredentials [email=%s, password=%s]", email, "****");
	}

	
}
