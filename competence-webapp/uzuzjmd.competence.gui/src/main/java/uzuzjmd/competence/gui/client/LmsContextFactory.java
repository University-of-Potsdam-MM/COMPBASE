package uzuzjmd.competence.gui.client;

public class LmsContextFactory implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long courseId;
	private String serverUrl;
	private String evidenceServerUrl;
	private String role;
	private String userName;
	private String organization;

	public LmsContextFactory() {
		this.courseId = getCourseIdFromContext();
		this.serverUrl = getCompetenceServerURL();
		this.evidenceServerUrl = getEvidenceServerUrlFromContext();
		this.role = getRoleFromContext();
		this.userName = getUserFromContext();
		setOrganization("university");
	}

	public LmsContextFactory(long courseId, String serverUrl,
			String evidenceServerUrl, String role, String userName) {
		super();
		this.courseId = courseId;
		if (serverUrl == null) {
			this.serverUrl = getCompetenceServerURL();
		} else {
			this.serverUrl = serverUrl;
		}
		if (evidenceServerUrl == null) {
			this.evidenceServerUrl = getEvidenceServerUrlFromContext();
		} else {
			this.evidenceServerUrl = evidenceServerUrl;
		}
		this.role = role;
		this.userName = userName;
		setOrganization("university");
	}

	public String getUserFromContext() {
		return "student student";
	}

	public String getRoleFromContext() {
		return Role.teacher.toString();
	}

	public String getEvidenceServerUrlFromContext() {
		return "http://localhost:8083";
	}

	public String getCompetenceServerURL() {
		return "http://localhost:8084";
	}

	public int getCourseIdFromContext() {
		return 10184;
	}

	public String getCourseId() {
		return "n" + courseId;
	}

	public String getMoodleCourseId() {
		return courseId + "";
	}

	public String getServerURL() {
		return serverUrl;
	}

	public String getEvidenceServerURL() {
		return evidenceServerUrl;
	}

	public String getRole() {
		return role;
	}

	public String getUser() {
		return userName;
	}

	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

}
