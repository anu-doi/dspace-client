/**
 * 
 */
package au.edu.anu.dspace.client.rest.model;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @author Rahul Khanna
 *
 */
@XmlRootElement
@JsonIgnoreProperties(ignoreUnknown = true)
public class DSpaceObject {

	public enum Type {
		COMMUNITY, COLLECTION, ITEM, BITSTREAM;
		
		@JsonValue
		public String getTypeValue() {
			return this.toString().toLowerCase();
		}
	}
	
	protected int id;
	protected String name;
	protected String handle;
	protected Type type;
	protected String link;
	protected List<String> expand;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getHandle() {
		return handle;
	}

	public void setHandle(String handle) {
		this.handle = handle;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public List<String> getExpand() {
		return expand;
	}

	public void setExpand(List<String> expand) {
		this.expand = expand;
	}

	@Override
	public String toString() {
		return String.format("DSpaceObject [id=%s, name=%s, handle=%s, type=%s, link=%s, expand=%s]", id, name, handle,
				type, link, expand);
	}

}
