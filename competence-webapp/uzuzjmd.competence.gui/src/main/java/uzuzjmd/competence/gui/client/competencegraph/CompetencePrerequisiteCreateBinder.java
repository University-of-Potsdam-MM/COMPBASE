package uzuzjmd.competence.gui.client.competencegraph;

import java.util.LinkedList;
import java.util.List;

import org.fusesource.restygwt.client.Resource;

import uzuzjmd.competence.gui.client.Controller;
import uzuzjmd.competence.gui.client.competenceSelection.CompetenceSelectionWidget;
import uzuzjmd.competence.gui.client.competenceSelection.ContextSelectionFilter;
import uzuzjmd.competence.gui.client.tabs.GraphTab;

import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.Button;
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
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class CompetencePrerequisiteCreateBinder extends Composite {

	private static CompetencePrerequisiteCreateBinderUiBinder uiBinder = GWT
			.create(CompetencePrerequisiteCreateBinderUiBinder.class);

	@UiField
	SimplePanel requiredKompetenzesPlaceholder2;
	private CompetenceSelectionWidget requiredCompetenceSelectionWidget;

	@UiField
	Button submitButton;
	@UiField
	Button cancelButton;
	@UiField
	FocusPanel warningPlaceholderPanel;

	private PopupPanel parent;
	private GraphTab graphtab;

	private String followingCompetence;

	interface CompetencePrerequisiteCreateBinderUiBinder extends
			UiBinder<Widget, CompetencePrerequisiteCreateBinder> {
	}

	public CompetencePrerequisiteCreateBinder(PopupPanel parent,
			GraphTab graphtab, String followingCompetence) {
		initWidget(uiBinder.createAndBindUi(this));
		this.parent = parent;
		requiredCompetenceSelectionWidget = new CompetenceSelectionWidget(
				Controller.contextFactory, null,
				ContextSelectionFilter.university,
				"Vorausgesetzte Kompetenzen", false);
		requiredKompetenzesPlaceholder2.add(requiredCompetenceSelectionWidget);
		this.followingCompetence = followingCompetence;
	}

	@UiHandler("submitButton")
	void onSubmitButtonClick(ClickEvent event) {
		List<String> requiredCompetences = requiredCompetenceSelectionWidget
				.getSelectedCompetences();

		List<String> followingCompetences = new LinkedList<String>();
		followingCompetences.add(followingCompetence);

		if (requiredCompetences.isEmpty()) {
			warningPlaceholderPanel.clear();
			warningPlaceholderPanel.add(new Alert(
					"Es wurden keine Kompetenzen als Voraussetzung ausgewählt",
					AlertType.WARNING));
		} else {
			Boolean isNoSelfReferenced = checkForSelfReference(
					requiredCompetences, followingCompetences);
			if (!isNoSelfReferenced) {
				warningPlaceholderPanel.clear();
				warningPlaceholderPanel
						.add(new Alert(
								"Eine Kompetenz darf keine Voraussetzung für sich selber sein",
								AlertType.WARNING));
			} else {
				sendRequirementLinkToServer(requiredCompetences,
						followingCompetences);
			}
		}

	}

	private Boolean checkForSelfReference(List<String> requiredCompetences,
			List<String> followingCompetences) {
		Boolean isNoSelfReferenced = true;
		for (String linkedCompetence : followingCompetences) {
			if (requiredCompetences.contains(linkedCompetence)) {
				isNoSelfReferenced = false;
			}
		}
		return isNoSelfReferenced;
	}

	private void sendRequirementLinkToServer(List<String> requiredCompetences,
			List<String> followingCompetences) {
		for (String linkedCompetence : followingCompetences) {
			try {
				Resource resource = new Resource(
						Controller.contextFactory.getServerURL()
								+ "/competences/json/prerequisite/create/"
								+ Controller.contextFactory.getCourseId());
				resource.addQueryParam("linkedCompetence", linkedCompetence)
						.addQueryParams("selectedCompetences",
								requiredCompetences).post()
						.send(new RequestCallback() {

							@Override
							public void onResponseReceived(Request request,
									Response response) {
								parent.hide();
								graphtab.reload(new LinkedList<String>());
							}

							@Override
							public void onError(Request request,
									Throwable exception) {
								PopupPanel popupPanel = new PopupPanel(true);
								popupPanel
										.add(new Alert(
												"Es gab einen Fehler beim Speichern. Kontaktieren Sie einen Entwickler!",
												AlertType.ERROR));
							};
						});
			} catch (RequestException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@UiHandler("cancelButton")
	void onCancelButtonClick(ClickEvent event) {
		parent.hide();
		requiredCompetenceSelectionWidget.reload();
	}

	@UiHandler("warningPlaceholderPanel")
	void onWarningPlaceholderPanelClick(ClickEvent event) {
		warningPlaceholderPanel.clear();
	}

	@UiHandler("warningPlaceholderPanel")
	void onWarningPlaceholderPanelMouseOut(MouseOutEvent event) {
		warningPlaceholderPanel.clear();
	}

	public void setCompetenceSelected(String nodeId) {
		followingCompetence = nodeId;
	}

}
