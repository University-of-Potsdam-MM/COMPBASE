package uzuzjmd.competence.gui.client.tabs;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.fusesource.restygwt.client.Resource;

import uzuzjmd.competence.gui.client.Competence_webapp;
import uzuzjmd.competence.gui.client.ContextFactory;
import uzuzjmd.competence.gui.client.competenceSelection.CompetenceSelectionWidget;
import uzuzjmd.competence.gui.client.linkView.ActivityTree;
import uzuzjmd.competence.gui.shared.Evidence;

import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.base.HtmlWidget;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class LinkEvidenceTab extends CompetenceTab {

	private static LinkEvidenceTabUiBinder uiBinder = GWT
			.create(LinkEvidenceTabUiBinder.class);
	@UiField
	SimplePanel HrPanelContainer;
	@UiField
	HTMLPanel activityPlaceholder;
	@UiField
	SimplePanel hrPanelContainer2;
	@UiField
	HorizontalPanel buttonContainer;
	@UiField
	SimplePanel tabExplainationPanel;
	@UiField
	Panel competenceSelectionPanelPlaceholder;
	@UiField
	Button submitButton;
	@UiField
	FocusPanel warningPlaceholder;
	private CompetenceSelectionWidget competenceSelectionWidget;
	private ContextFactory contextFactory;

	final HashMap<String, String> activityMapToUrl = new HashMap<String, String>();
	final HashMap<String, String> activityMapToUser = new HashMap<String, String>();
	private ActivityTree activityPanel;
	private HtmlWidget alert;

	interface LinkEvidenceTabUiBinder extends UiBinder<Widget, LinkEvidenceTab> {
	}

	public LinkEvidenceTab(ContextFactory contextFactory) {
		initWidget(uiBinder.createAndBindUi(this));
		this.contextFactory = contextFactory;

		String infoText = "Ordnen Sie die Kursaktivitäten den Kompetenzen zu! Dies ermöglicht eine Übersicht über die erreichten Kompetenzen pro Teilnehmer.";
		fillInfoTab(infoText, tabExplainationPanel);
		initHrLines(HrPanelContainer);
		initCompetenceSelectionWidget(contextFactory);
		initMoodleEvidenceWidget(contextFactory);

	}

	private void initMoodleEvidenceWidget(ContextFactory contextFactory) {
		String moodleEvidenceUrl = getMoodleEvidenceServerUrl();
		activityPanel = new ActivityTree(moodleEvidenceUrl, "Aktivitäten",
				"activityView", 650, 180, "Aktivitäten", contextFactory);
		activityPlaceholder.add(activityPanel);
	}

	private void initCompetenceSelectionWidget(ContextFactory contextFactory) {
		competenceSelectionWidget = new CompetenceSelectionWidget(
				contextFactory, null, "coursecontext/");

		competenceSelectionPanelPlaceholder.add(competenceSelectionWidget);
	}

	private String getMoodleEvidenceServerUrl() {
		String moodleEvidenceUrl = contextFactory.getEvidenceServerURL()
				+ "/moodle/activities/usertree/xml/crossdomain/"
				+ contextFactory.getMoodleCourseId();
		return moodleEvidenceUrl;
	}

	@UiHandler("submitButton")
	void onSubmitButtonClick(ClickEvent event) {
		GWT.log("clicking submit button");
		warningPlaceholder.clear();

		List<String> competences = this.competenceSelectionWidget
				.getSelectedCompetences();
		List<Evidence> evidences = this.activityPanel.getSelectedEvidences();
		if (competences.isEmpty() || evidences.isEmpty()) {
			Window.alert("competences are empty");
			alert = new Alert(
					"Es müssen Kompetenzen und Aktivitäten ausgewählt werden!",
					AlertType.ERROR);
			warningPlaceholder.add(alert);
		} else {
			for (Evidence evidence : activityPanel.getSelectedEvidences()) {
				createAbstractEvidenceLink(competences, evidence,
						contextFactory);
			}
		}
	}

	private void createAbstractEvidenceLink(List<String> competences,
			Evidence evidence, ContextFactory contextFactory2) {
		List<String> activityPairs = new LinkedList<String>();
		activityPairs.add(evidence.getShortname() + "," + evidence.getUrl());
		String linkedUser = evidence.getUserId();
		String createLink = contextFactory.getServerURL()
				+ "/competences/json/link/create/"
				+ contextFactory2.getCourseId() + "/"
				+ contextFactory2.getUser() + "/" + contextFactory2.getRole()
				+ "/" + linkedUser;
		Resource resource = new Resource(createLink);
		try {
			resource.addQueryParams("competences", competences)
					.addQueryParams("evidences", activityPairs).post()
					.send(new OkFeedBack());
		} catch (RequestException e) {
			GWT.log(e.getMessage());
		}
	}

	private class OkFeedBack implements RequestCallback {
		@Override
		public void onError(Request request, Throwable exception) {
			// TODO Auto-generated method stub
			GWT.log(exception.getMessage());
			alert = new Alert(
					"Ein Serverfehler ist aufgetreten, kontaktieren Sie einen Admin!",
					AlertType.ERROR);
			warningPlaceholder.add(alert);
		}

		@Override
		public void onResponseReceived(Request request, Response response) {
			GWT.log(response.getStatusText());
			alert = new Alert("Die Kompetenzen wurden erfolgreich verknüpft!",
					AlertType.SUCCESS);
			warningPlaceholder.add(alert);
			GWT.log("erfolgreich Kompetenzen verknüpft");
			Competence_webapp.reloadController.reload();
		}
	}

	@UiHandler("warningPlaceholder")
	void onWarningPlaceholderClick(ClickEvent event) {
		warningPlaceholder.remove(alert);
	}

	@UiHandler("warningPlaceholder")
	void onWarningPlaceholderMouseOut(MouseOutEvent event) {
		warningPlaceholder.remove(alert);
	}

	public void reload() {
		competenceSelectionWidget.reload();
	}
}