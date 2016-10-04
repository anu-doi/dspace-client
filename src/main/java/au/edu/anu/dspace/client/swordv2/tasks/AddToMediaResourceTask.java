package au.edu.anu.dspace.client.swordv2.tasks;

import au.edu.anu.dspace.client.swordv2.SwordServerInfo;
import org.swordapp.client.Deposit;
import org.swordapp.client.SWORDClient;
import org.swordapp.client.SwordResponse;

public class AddToMediaResourceTask extends AbstractSwordTask<SwordResponse> {
	private String editMediaUrl;
	private Deposit deposit;

	public AddToMediaResourceTask(SWORDClient swordClient, SwordServerInfo serverInfo, String editMediaUrl,
			Deposit deposit) {
		super(swordClient, serverInfo);
		this.editMediaUrl = editMediaUrl;
		this.deposit = deposit;
	}

	public SwordResponse call() throws Exception {
		SwordResponse sr = this.client.addToMediaResource(this.editMediaUrl, this.deposit, createAuth(true));
		return sr;
	}
}
