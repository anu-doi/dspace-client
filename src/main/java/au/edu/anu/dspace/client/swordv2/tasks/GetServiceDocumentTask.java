package au.edu.anu.dspace.client.swordv2.tasks;

import au.edu.anu.dspace.client.swordv2.SwordServerInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.swordapp.client.SWORDClient;
import org.swordapp.client.ServiceDocument;

public class GetServiceDocumentTask extends AbstractSwordTask<ServiceDocument> {
	private static Logger log = LoggerFactory.getLogger(GetServiceDocumentTask.class);

	public GetServiceDocumentTask(SWORDClient swordClient, SwordServerInfo serverInfo) {
		super(swordClient, serverInfo);
	}

	public ServiceDocument call() throws Exception {
		ServiceDocument sd = this.client.getServiceDocument(this.serverInfo.getServiceDocUrl(), createAuth(true));
		return sd;
	}
}
