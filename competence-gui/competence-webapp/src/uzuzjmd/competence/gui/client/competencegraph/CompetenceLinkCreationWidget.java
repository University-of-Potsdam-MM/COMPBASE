package uzuzjmd.competence.gui.client.competencegraph;

import java.util.List;

import org.fusesource.restygwt.client.Resource;

import uzuzjmd.competence.gui.client.Competence_webapp;
import uzuzjmd.competence.gui.client.competenceSelection.CompetenceSelectionWidget;

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

public class CompetenceLinkCreationWidget extends Composite {

	private static CompetenceLinkCreationWidgetUiBinder uiBinder = GWT
			.create(CompetenceLinkCreationWidgetUiBinder.class);
	@UiField
	SimplePanel requiredKompetenzesPlaceholder;
	@UiField
	SimplePanel followingCompetences;
	@UiField
	SimplePanel requiredKompetenzesPlaceholder2;
	@UiField
	SimplePanel followingCompetences2;

	@UiField
	Button submitButton;
	@UiField
	Button cancelButton;
	@UiField FocusPanel warningPlaceholderPanel;

	private CompetenceSelectionWidget requiredCompetenceSelectionWidget;
	private CompetenceSelectionWidget followingCompetenceSelectionWidget;
	private PopupPanel parent;

	interface CompetenceLinkCreationWidgetUiBinder extends
			UiBinder<Widget, CompetenceLinkCreationWidget> {
	}

	public CompetenceLinkCreationWidget(PopupPanel parent) {
		initWidget(uiBinder.createAndBindUi(this));
		this.parent = parent;
		requiredCompetenceSelectionWidget = new CompetenceSelectionWidget(
				Competence_webapp.contextFactory, null, "coursecontext/",
				"Vorausgesetzte Kompetenzen");
		requiredKompetenzesPlaceholder.add(requiredCompetenceSelectionWidget);
		requiredKompetenzesPlaceholder2.add(requiredCompetenceSelectionWidget);

		followingCompetenceSelectionWidget = new CompetenceSelectionWidget(
				Competence_webapp.contextFactory, null, "coursecontext/",
				"Nachfolgende Kompetenzen");
		followingCompetences.add(followingCompetenceSelectionWidget);
		followingCompetences2.add(followingCompetenceSelectionWidget);
	}

	@UiHandler("submitButton")
	void onSubmitButtonClick(ClickEvent event) {
		List<String> requiredCompetences = requiredCompetenceSelectionWidget
				.getSelectedCompetences();
		List<String> followingCompetences = followingCompetenceSelectionWidget
				.getSelectedCompetences();
		Resource resource = new Resource(
				Competence_webapp.contextFactory.getServerURL()
						+ "/competences/json/prerequisite/create/"
						+ Competence_webapp.contextFactory.getCourseId());
		if (requiredCompetences.isEmpty()) {
			warningPlaceholderPanel.clear();
			warningPlaceholderPanel.add(new  Alert("Es wurden keine Kompetenzen als Voraussetzung ausgewählt",
			 AlertType.WARNING));						
		} else {
			for (String linkedCompetence : followingCompetences) {
				if (requiredCompetences.contains(linkedCompetence)) {
					warningPlaceholderPanel.clear();
					warningPlaceholderPanel.add(new  Alert("Eine Kompetenz darf keine Voraussetzung für sich selber sein",
					 AlertType.WARNING));
				} else {
					try {
						resource.addQueryParam("linkedCompetence",
								linkedCompetence)
								.addQueryParams("selectedCompetences",
										requiredCompetences).post()
								.send(new RequestCallback() {

									@Override
									public void onResponseReceived(
											Request request, Response response) {
										parent.hide();
									}

									@Override
									public void onError(Request request,
											Throwable exception) {
										PopupPanel popupPanel = new PopupPanel(
												true);
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
		}

	}

	@UiHandler("cancelButton")
	void onCancelButtonClick(ClickEvent event) {
		parent.hide();
		requiredCompetenceSelectionWidget.reload();
		followingCompetenceSelectionWidget.reload();
	}
	@UiHandler("warningPlaceholderPanel")
	void onWarningPlaceholderPanelClick(ClickEvent event) {
		warningPlaceholderPanel.clear();
	}
	@UiHandler("warningPlaceholderPanel")
	void onWarningPlaceholderPanelMouseOut(MouseOutEvent event) {
		warningPlaceholderPanel.clear();
	}
}