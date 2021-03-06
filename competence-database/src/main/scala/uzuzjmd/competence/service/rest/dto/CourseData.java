package uzuzjmd.competence.service.rest.dto;

import java.util.List;

/**
 * This wraps a pair of course and competences in order to exchange competences linked to a course
 */
public class CourseData {
	private String course;
	private List<String> competences;
	
	public CourseData(String course, List<String> competences) {
		this.course = course;
		this.competences = competences;
	}

	public String getCourse() {
		return course;
	}

	public void setCourse(String course) {
		this.course = course;
	}

	public List<String> getCompetences() {
		return competences;
	}

	public void setCompetences(List<String> competences) {
		this.competences = competences;
	}
	
}
