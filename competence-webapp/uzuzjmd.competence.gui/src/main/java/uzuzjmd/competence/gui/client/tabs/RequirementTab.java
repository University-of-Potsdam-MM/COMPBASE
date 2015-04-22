package uzuzjmd.competence.gui.client.tabs;

import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.Resource;
import org.fusesource.restygwt.client.TextCallback;

import uzuzjmd.competence.gui.client.LmsContextFactory;
import uzuzjmd.competence.gui.client.competenceSelection.CompetenceSelectionWidget;
import uzuzjmd.competence.gui.client.competenceSelection.ContextSelectionFilter;
import uzuzjmd.competence.gui.client.competenceSelection.SelectedFilter;

import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.base.HtmlWidget;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class RequirementTab extends CompetenceTab {

	interface RequirementTabUiBinder extends UiBinder<Widget, RequirementTab> {
	}

	private static RequirementTabUiBinder uiBinder = GWT
			.create(RequirementTabUiBinder.class);

	private CompetenceSelectionWidget competenceSelectionWidget;

	@UiField
	SimplePanel tabExplainationPanel;

	@UiField
	VerticalPanel competenceSelectionAndRequirementPanel;
	@UiField
	HorizontalPanel buttonPanel;
	@UiField
	Button submitButton;
	@UiField
	Button deleteContextButton;
	@UiField
	TextArea requirementTextAreaWidget;

	@UiField
	Panel competenceSelectionPanelPlaceholder;

	@UiField
	SimplePanel hrDividerPanel;
	@UiField
	SimplePanel hrDividerPanel2;
	@UiField
	FocusPanel warningPlaceHolder;

	private HtmlWidget alert;

	public RequirementTab(final LmsContextFactory contextFactory) {
		initWidget(uiBinder.createAndBindUi(this));
		String infoText = "Wählen Sie mit STRG-Click die Kompetenzen aus, die für diesen Kurs erfüllt sein müssen! Setzen Sie zusätzlich ein Häckchen, wenn diese als verpflichtend für den Scheinerwerb gelten! Beschreiben Sie die Anforderungen, die Sie für den Kompetenzerwerb stellen und klicken Sie auf abschicken!";
		fillInfoTab(infoText, tabExplainationPanel);

		initHrLines(hrDividerPanel);
		initHrLines(hrDividerPanel2);

		competenceSelectionWidget = new CompetenceSelectionWidget(
				contextFactory, SelectedFilter.selected,
				ContextSelectionFilter.course, null, true);
		competenceSelectionPanelPlaceholder.add(competenceSelectionWidget);
		initRequirementTextfield(contextFactory);

	}

	private void initRequirementTextfield(final LmsContextFactory contextFactory) {
		GWT.log("Initiating requirement textfield");
		Resource resource = new Resource(contextFactory.getServerURL()
				+ "/competences/json/coursecontext/requirements/"
				+ contextFactory.getCourseId());
		resource.get().send(new TextCallback() {

			@Override
			public void onSuccess(Method arg0, String arg1) {
				requirementTextAreaWidget.setText(arg1);
			}

			@Override
			public void onFailure(Method arg0, Throwable arg1) {
				GWT.log("could not get requirements for course");
			}
		});
		GWT.log("Initiated requirement textfield");
	}

	public RequirementTab(String firstName) {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiHandler("deleteContextButton")
	void onDeleteContextButtonClick(ClickEvent event) {
		competenceSelectionWidget.handleDeleteClick();
		alert = new Alert("Die Verknüpfungen wurden erfolgreich gelöscht",
				AlertType.SUCCESS);
		warningPlaceHolder.add(alert);
	}

	@UiHandler("submitButton")
	void onSubmitButtonClick(ClickEvent event) {
		competenceSelectionWidget.handleSubmit(requirementTextAreaWidget
				.getText());
		alert = new Alert("Die Verknüpfungen wurden erfolgreich erstellt",
				AlertType.SUCCESS);
		warningPlaceHolder.add(alert);
	}

	@UiHandler("warningPlaceHolder")
	void onWarningPlaceHolderClick(ClickEvent event) {
		warningPlaceHolder.remove(alert);
	}

	@UiHandler("warningPlaceHolder")
	void onWarningPlaceHolderMouseOut(MouseOutEvent event) {
		warningPlaceHolder.remove(alert);
	}
}
