/**
 * 
 */
package au.edu.anu.dspace.client.swordv2.tasks;

import org.swordapp.client.SWORDClient;
import org.swordapp.client.SwordResponse;

import au.edu.anu.dspace.client.swordv2.SwordServerInfo;

/**
 * @author Rahul Khanna
 *
 */
public class ChangeInProgressTask extends AbstractSwordTask<SwordResponse> {

	private String editUrl;

	public ChangeInProgressTask(SWORDClient swordClient, SwordServerInfo serverInfo, String editUrl) {
		super(swordClient, serverInfo);
		this.editUrl = editUrl;
	}

	@Override
	public SwordResponse call() throws Exception {
		SwordResponse sr = this.client.complete(this.editUrl, createAuth(false));
		return sr;
	}

}
