package uzuzjmd.competence.shared.dto;

public class Activity {

	public Activity() {
	}

	public Activity(String shortname, String url) {
		this.shortname = shortname;
		this.url = url;

	}

	private String shortname; // zur Anzeige
	private String url; // die web referenzierbare url der Aktivität

	public String getShortname() {
		return shortname;
	}

	public void setShortname(String shortname) {
		this.shortname = shortname;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
