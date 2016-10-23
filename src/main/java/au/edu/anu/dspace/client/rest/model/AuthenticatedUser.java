/**
 * 
 */
package au.edu.anu.dspace.client.rest.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Rahul Khanna
 *
 */
@XmlRootElement
public class AuthenticatedUser {

	private boolean okay;
	private boolean authenticated;
	private String email;
	@XmlElement(name = "fullname")
	private String fullName;
	private String token;

	public boolean isOkay() {
		return okay;
	}

	public void setOkay(boolean okay) {
		this.okay = okay;
	}

	public boolean isAuthenticated() {
		return authenticated;
	}

	public void setAuthenticated(boolean authenticated) {
		this.authenticated = authenticated;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	@Override
	public String toString() {
		return String.format("AuthenticatedUser [okay=%s, authenticated=%s, email=%s, fullName=%s, token=%s]", okay,
				authenticated, email, fullName, token);
	}
	
}
