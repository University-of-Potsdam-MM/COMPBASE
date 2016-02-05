package uzuzjmd.competence.shared.dto;

/**
 * Created by dehne on 05.02.2016.
 */
public class RecommendedActivityResult {
    private RecommendedCourseResult courseResult;
    private String url;

    public RecommendedActivityResult(RecommendedCourseResult courseResult, String url) {
        this.courseResult = courseResult;
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public RecommendedCourseResult getCourseResult() {
        return courseResult;
    }

    public void setCourseResult(RecommendedCourseResult courseResult) {
        this.courseResult = courseResult;
    }
}
