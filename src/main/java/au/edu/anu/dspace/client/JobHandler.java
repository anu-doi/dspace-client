package au.edu.anu.dspace.client;

import au.edu.anu.dspace.client.config.Config;

public interface JobHandler {
	public int handle(Config config, String[] args);
}
