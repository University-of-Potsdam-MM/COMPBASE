package uzuzjmd.competence.evidence.service.moodle;

import javax.ws.rs.core.MediaType;

import uzuzjmd.competence.owl.access.MagicStrings;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class SimpleMoodleService {
	private Token token;

	public SimpleMoodleService(String moodledatabaseurl, String moodledb,
			String adminname, String adminpassword) {
		Client client = Client.create();
		WebResource webResource = client.resource(MagicStrings.MOODLEURL
				+ "login/token.php?username=" + adminname + "&password="
				+ adminpassword + "&service=moodle_mobile_app");
		ClientResponse response = webResource
				.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
		if (response.getStatus() != 200) {
			throw new RuntimeException("Failed : HTTP error code : "
					+ response.getStatus());
		}
		token = response.getEntity(Token.class);
	}

	public MoodleContentResponseList getMoodleContents(String courseId) {
		Client client = Client.create();
		String moodleRestBase = getMoodleRestBase();
		WebResource webResource = client.resource(MagicStrings.MOODLEURL
				+ moodleRestBase + "core_course_get_contents&courseid="
				+ courseId);
		return webResource.accept(MediaType.APPLICATION_JSON).get(
				MoodleContentResponseList.class);

	}

	private String getMoodleRestBase() {
		String moodleRestBase = "webservice/rest/server.php?moodlewsrestformat=json&wstoken="
				+ token.get("token") + "&wsfunction=";
		return moodleRestBase;
	}
}