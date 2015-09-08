package uzuzjmd.competence.evidence.service.moodle;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import uzuzjmd.competence.owl.access.MagicStrings;
import uzuzjmd.competence.shared.dto.UserCourseListResponse;

/**
 * DTOs für den Moodle REST-Service
 * 
 * @author julian
 * 
 */
public class SimpleMoodleService {
	private Token mooodleStandardInterfaceToken;
	private String errorCode;
	private Token competenceInterfaceToken;

	public SimpleMoodleService(String username, String userpassword) {
		mooodleStandardInterfaceToken = initToken(username, userpassword, "moodle_mobile_app");
		competenceInterfaceToken = initToken(username, userpassword, "upcompetence");
	}

	private Token initToken(String username, String userpassword, String serviceShortName) {
		String connectionPath = MagicStrings.MOODLEURL + "/login/token.php?username=" + username + "&password=" + userpassword + "&service=" + serviceShortName;
		return sendRequest(connectionPath, Token.class);
	}

	private <T> T sendRequest(String url, Class<T> responseTyp) {
		Client client = ClientBuilder.newClient();
		WebTarget webResource = client.target(url);
		T response = webResource.request(MediaType.APPLICATION_JSON).get(responseTyp);
		return response;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public MoodleContentResponseList getMoodleContents(String courseId) {

		String moodleRestBase = getMoodleRestBase();

		String requestString = MagicStrings.MOODLEURL + moodleRestBase + "core_course_get_contents&courseid=" + courseId;

		return sendRequest(requestString, MoodleContentResponseList.class);
	}

	public UserCourseListResponse getMoodleCourseList(String userEmail, String userPassword) {
		String moodleRestBase = getMoodleCompetenceRestBase();
		String requestString = MagicStrings.MOODLEURL + moodleRestBase + "local_upcompetence_get_courses_for_user&user=" + userEmail + "&password=" + userPassword;
		return sendRequest(requestString, UserCourseListResponse.class);
	}

	private String getMoodleRestBase() {
		String moodleRestBase = "/webservice/rest/server.php?moodlewsrestformat=json&wstoken=" + mooodleStandardInterfaceToken.get("token") + "&wsfunction=";
		return moodleRestBase;
	}

	private String getMoodleCompetenceRestBase() {
		String moodleRestBase = "/webservice/rest/server.php?moodlewsrestformat=json&wstoken=" + competenceInterfaceToken.get("token") + "&wsfunction=";
		return moodleRestBase;
	}

	public boolean isUserExist(String userEmail) {
		String moodleRestBase = getMoodleCompetenceRestBase();
		String requestString = MagicStrings.MOODLEURL + moodleRestBase + "local_upcompetence_user_exists&user=" + userEmail;
		return sendRequest(requestString, Boolean.class);
	}

	public MoodleEvidenceList getMoodleEvidenceList(String courseId) {
		if (courseId == null || courseId.equals("undefined")) {
			throw new WebApplicationException(new Exception("courseId is null or undefined when getting Moodle evidences"));
		}

		String moodleRestBase = getMoodleCompetenceRestBase();
		String requestString = MagicStrings.MOODLEURL + moodleRestBase + "local_upcompetence_get_evidences_for_course&courseId=" + courseId;
		return sendRequest(requestString, MoodleEvidenceList.class);
	}

}
