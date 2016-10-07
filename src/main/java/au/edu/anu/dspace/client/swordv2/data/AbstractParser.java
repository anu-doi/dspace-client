/**
 * 
 */
package au.edu.anu.dspace.client.swordv2.data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Rahul Khanna
 *
 */
public abstract class AbstractParser implements SwordRequestDataProvider  {

	protected final List<SwordRequestData> swordRequests = new ArrayList<SwordRequestData>();
	
	protected boolean isDryRun = false;
	

	@Override
	public List<SwordRequestData> getSwordRequests() {
		return swordRequests;
	}

	@Override
	public boolean isDryRun() {
		return isDryRun;
	}
	
}
