package uzuzjmd.competence.shared.dto;

/**
 * Created by dehne on 05.02.2016.
 */
public class RecommendedCourseResult {
    private String courseId;
    private String url;

    public RecommendedCourseResult(String courseId, String url) {
        this.courseId = courseId;
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }
}
