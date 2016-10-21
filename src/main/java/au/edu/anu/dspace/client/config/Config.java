package au.edu.anu.dspace.client.config;

import java.util.Properties;

public class Config extends Properties {
	private static final long serialVersionUID = 1L;
	
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
}
