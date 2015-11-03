package uzuzjmd.competence.shared.dto;

public class CompetenceForActivityData {
	private String competence;
	private Activity activity;

	public String getCompetence() {
		return competence;
	}

	public void setCompetence(String competence) {
		this.competence = competence;
	}

	public Activity getActivity() {
		return activity;
	}

	public void setActivity(Activity activity) {
		this.activity = activity;
	}

	public CompetenceForActivityData(String competence,
			Activity activity) {
		super();
		this.competence = competence;
		this.activity = activity;
	}
}
