package uzuzjmd.competence.shared.dto;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;

/**
 * XMl-mappbare DTOs für den eigenen REST-Service (Evidenzen)
 * 
 * @author julian
 * 
 */
@XmlSeeAlso(AbstractTreeEntry.class)
public class ActivityEntry extends AbstractTreeEntry {

	private String url;

	public ActivityEntry() {
	}

	public ActivityEntry(String name, String qtip, String icon, String url) {
		super(name, qtip, icon);
		this.url = url;
	}

	@XmlElement(name = "moodleUrl")
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
