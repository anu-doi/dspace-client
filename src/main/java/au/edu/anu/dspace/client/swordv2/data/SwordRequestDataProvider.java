package au.edu.anu.dspace.client.swordv2.data;

import java.util.List;

public interface SwordRequestDataProvider {

	public List<SwordRequestData> getSwordRequests();
	
	public void updateRequestStatus(SwordRequestData data, boolean isSuccess);
}
