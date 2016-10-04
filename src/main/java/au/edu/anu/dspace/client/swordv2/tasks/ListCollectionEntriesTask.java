package au.edu.anu.dspace.client.swordv2.tasks;

import au.edu.anu.dspace.client.swordv2.SwordServerInfo;
import org.swordapp.client.CollectionEntries;
import org.swordapp.client.SWORDClient;
import org.swordapp.client.SWORDCollection;

public class ListCollectionEntriesTask extends AbstractSwordTask<CollectionEntries> {
	private SWORDCollection collection;
	private String url;

	public ListCollectionEntriesTask(SWORDClient swordClient, SwordServerInfo serverInfo, SWORDCollection collection) {
		super(swordClient, serverInfo);
		this.collection = collection;
	}

	public ListCollectionEntriesTask(SWORDClient swordClient, SwordServerInfo serverInfo, String url) {
		super(swordClient, serverInfo);
		this.url = url;
	}

	public CollectionEntries call() throws Exception {
		CollectionEntries collEntries = null;
		if (this.collection != null) {
			collEntries = this.client.listCollection(this.collection, createAuth(true));
		} else if (this.url != null) {
			collEntries = this.client.listCollection(this.url, createAuth(true));
		}
		return collEntries;
	}
}
