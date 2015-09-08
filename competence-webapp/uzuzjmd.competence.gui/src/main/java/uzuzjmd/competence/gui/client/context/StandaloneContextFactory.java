package uzuzjmd.competence.gui.client.context;

import java.util.HashMap;

import org.fusesource.restygwt.client.JsonCallback;
import org.fusesource.restygwt.client.JsonEncoderDecoder;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.Resource;

import uzuzjmd.competence.gui.client.viewcontroller.ContextSelectionController;
import uzuzjmd.competence.gui.client.viewcontroller.Controller;
import uzuzjmd.competence.service.rest.client.dto.UserCourseListResponse;

import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONValue;

public class StandaloneContextFactory extends LmsContextFactory {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public interface CourseListCodec extends
			JsonEncoderDecoder<UserCourseListResponse> {
	}

	private HashMap<String, Long> coursesHashmap;

	public StandaloneContextFactory(String serverUrl, String evidenceUrl) {
		super();
		this.serverUrl = serverUrl;
		this.evidenceServerUrl = evidenceUrl;
		this.coursesHashmap = new HashMap<String, Long>();
		this.coursesHashmap.put("university", 0l);
		setCourseContext("university");
		setRole("teacher");
	}

	public HashMap<String, Long> getCoursesHashmap() {
		return coursesHashmap;
	}

	public void updateCourses() {
		if (super.validUserLoggedIn) {
			Resource resource = new Resource(
					Controller.contextFactory.getEvidenceServerURL()
							+ "/lms/courses/moodle/" + getUser() + "/"
							+ getPassword());
			resource.get().send(new JsonCallback() {

				@Override
				public void onSuccess(Method method, JSONValue response) {
					GWT.log("received coursesList for User"
							+ response.toString());
					// CourseListCodec codec =
					// GWT.create(CourseListCodec.class);
					// GWT.log(codec.toString());
					// UserCourseListResponse courseListResponse = codec
					// .decode(response);

					// for (UserCourseListItem userCourseListItem :
					// courseListResponse) {
					// coursesHashmap.put(userCourseListItem.getName(),
					// userCourseListItem.getCourseid());
					// }

					for (int i = 0; i < response.isArray().size(); i++) {
						Long courseId = Math.round(response.isArray().get(i)
								.isObject().get("courseid").isNumber()
								.doubleValue());
						String name = response.isArray().get(i).isObject()
								.get("name").toString().replaceAll("\"", "");
						coursesHashmap.put(name, courseId);
					}

					ContextSelectionController
							.initContextController(coursesHashmap.keySet());
					Controller.reloadController.reload();

					setCourseContext(ContextSelectionController
							.getSelectedCourseContext());

				}

				@Override
				public void onFailure(Method method, Throwable exception) {

				}
			});
		}
	}

	public String getRoleFromContext() {
		return Role.teacher.toString();
	}

	public String getCompetenceServerURL() {
		return serverUrl;
	}

	@Override
	public Long getCourseId() {
		GWT.log("using standalone context factory");
		GWT.log("course context is: " + getCourseContext());
		GWT.log("course id is: " + coursesHashmap.get(getCourseContext()));
		return coursesHashmap.get(getCourseContext());
	}
}
