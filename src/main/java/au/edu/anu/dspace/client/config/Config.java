package au.edu.anu.dspace.client.config;

import java.util.Properties;

public class Config extends Properties {
	private static final long serialVersionUID = 1L;
	
	public static final String PROPERTY_USERNAME = "username";
	public static final String PROPERTY_PASSWORD = "password";

	// sword specific
	public static final String PROPERTY_SERVICEDOCURL =  "serviceDocUrl";
	public static final String PROPERTY_ONBEHALFOF = "onbehalfof";

	public Config() {
		super();
	}

	@Override
	public String getProperty(String key) {
		if (System.getProperty(key) != null) {
			return System.getProperty(key);
		} else {
			return super.getProperty(key);
		}
	}
	
	@Override
	public synchronized String toString() {
		// replace password with asterisks
		String string = super.toString().replace(getProperty(PROPERTY_PASSWORD), "****");
		return string;
	}
}
