package uzuzjmd.competence.gui.client.course.activity;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.fusesource.restygwt.client.Resource;

import uzuzjmd.competence.gui.client.context.LmsContextFactory;
import uzuzjmd.competence.gui.client.shared.widgets.CompetenceTab;
import uzuzjmd.competence.gui.client.shared.widgets.taxonomy.CompetenceSelectionWidget;
import uzuzjmd.competence.gui.client.viewcontroller.Controller;
import uzuzjmd.competence.service.rest.client.api.RestUrlFactory;

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

	final HashMap<String, String> activityMapToUrl = new HashMap<String, String>();
	final HashMap<String, String> activityMapToUser = new HashMap<String, String>();
	private ActivityTree activityPanel;
	private HtmlWidget alert;

	interface LinkEvidenceTabUiBinder extends UiBinder<Widget, LinkEvidenceTab> {
	}

	public LinkEvidenceTab() {
		initWidget(uiBinder.createAndBindUi(this));
		String infoText = "";
		if (Controller.contextFactory.getRole().equals("teacher")) {
			infoText = "Ordnen Sie die Aktivitäten den Kompetenzen zu! Dies ermöglicht eine Übersicht über die erreichten Kompetenzen pro Teilnehmer.";
		} else {
			infoText = "Ordnen Sie die Aktivitäten den Kompetenzen zu! Dies ermöglicht eine Übersicht über ihre erreichten Kompetenzen und die der anderen Teilnehmer. Das Häckchen bedeutet, dass die Kompetenzen für das Bestehen des Kurses verpflichtend sind!";
		}
		infoText += "Wichtig: Wenn die Kompetenzauswahl LEER ist, müssen sie in dem Anforderungstab Kompetenzen auswählen";
		fillInfoTab(infoText, tabExplainationPanel);
		initHrLines(HrPanelContainer);
		initCompetenceSelectionWidget();
		if (Controller.contextFactory.getMode().equals("moodle")
				|| Controller.contextFactory.getMode().equals("liferay")) {
			reloadActivityWidget();
		}
	}

	public void reloadActivityWidget() {
		activityPlaceholder.clear();
		activityPanel = new ActivityTree(
				RestUrlFactory.computeActivityRestURL(), "Aktivitäten",
				"activityView", 655, 180, "Aktivitäten",
				Controller.contextFactory);
		activityPlaceholder.add(activityPanel);
	}

	private void initCompetenceSelectionWidget() {
		competenceSelectionWidget = new CompetenceSelectionWidget(null,
				"coursecontext/", "Kurskompetenzen", true);

		competenceSelectionPanelPlaceholder.add(competenceSelectionWidget);
	}

	@UiHandler("submitButton")
	void onSubmitButtonClick(ClickEvent event) {
		GWT.log("clicking submit button");
		warningPlaceholder.clear();

		List<String> competences = this.competenceSelectionWidget
				.getSelectedCompetences();
		List<Evidence> evidences = this.activityPanel.getSelectedEvidences();
		if (competences.isEmpty() || evidences.isEmpty()) {
			alert = new Alert(
					"Es müssen Kompetenzen und Aktivitäten ausgewählt werden!",
					AlertType.ERROR);
			warningPlaceholder.add(alert);
		} else {
			for (Evidence evidence : activityPanel.getSelectedEvidences()) {
				createAbstractEvidenceLink(competences, evidence,
						Controller.contextFactory);
			}
		}
	}

	private void createAbstractEvidenceLink(List<String> competences,
			Evidence evidence, LmsContextFactory contextFactory2) {
		List<String> activityPairs = new LinkedList<String>();
		activityPairs.add(evidence.getShortname() + "," + evidence.getUrl());
		String linkedUser = evidence.getUserId();
		String createLink = Controller.contextFactory.getServerURL()
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
			Controller.reloadController.reload();
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
		reloadActivityWidget();
		competenceSelectionWidget.reload();
	}
}