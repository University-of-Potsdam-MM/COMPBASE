package uzuzjmd.competence.shared.dto;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;

/**
 * XMl-mappbare DTOs für den eigenen REST-Service (Evidenzen)
 * 
 * @author julian
 * 
 */
@XmlSeeAlso(AbstractTreeEntry.class)
public class ActivityTyp extends AbstractTreeEntry {

	private List<ActivityEntry> activities;

	public ActivityTyp() {

	}

	public ActivityTyp(String name, String qtip, String icon) {
		super(name, qtip, icon);
		// TODO Auto-generated constructor stub
	}

	public ActivityTyp(String name, String qtip, String icon,
			List<ActivityEntry> activities) {
		super(name, qtip, icon);
		this.activities = activities;
	}

	@XmlElement(name = "activityEntry")
	public List<ActivityEntry> getActivities() {
		return activities;
	}

	public void setActivities(List<ActivityEntry> activities) {
		this.activities = activities;
	}
}
