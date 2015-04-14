package uzuzjmd.competence.gui.client.tabs;

import uzuzjmd.competence.gui.client.LmsContextFactory;
import uzuzjmd.competence.gui.client.competenceSelection.CompetenceSelectionWidget;
import uzuzjmd.competence.gui.client.persistence.GetRequestManager;

import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.Button;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ProgressTab extends CompetenceTab {

	private static ProgressTabUiBinder uiBinder = GWT
			.create(ProgressTabUiBinder.class);

	@UiField
	SimplePanel HrPanelContainer;
	@UiField
	VerticalPanel progressPlaceHolder;
	@UiField
	SimplePanel hrPanelContainer2;
	@UiField
	SimplePanel tabExplainationPanel;
	@UiField
	Panel competenceSelectionPanelPlaceholder;
	@UiField
	FocusPanel warningPlaceholder;
	@UiField
	Button filterButton;
	
	private CompetenceSelectionWidget competenceSelectionWidget;
	private LmsContextFactory contextFactory;
	
	private Alert alert;

	interface ProgressTabUiBinder extends UiBinder<Widget, ProgressTab> {
	}

	public ProgressTab(final LmsContextFactory contextFactory) {
		initWidget(uiBinder.createAndBindUi(this));

		this.contextFactory = contextFactory;
		String infoText = "Hier können Sie die Zuordnung von den Kompetenzen und den Teilnehmern einsehen. Die Balken zeigen an, wie viele der ausgewählten Kompetenzen mit einer Aktivität eines Teilnehmers verknüpft wurden.";
		infoText+= "Wenn keine Fortschrittsbalken angezeigt werden, müssen sie in dem Zuordung-Tab Aktivitäten zuordnen";
		fillInfoTab(infoText, tabExplainationPanel);
		initHrLines(HrPanelContainer);

		competenceSelectionWidget = new CompetenceSelectionWidget(
				contextFactory, null, "coursecontext/");

		competenceSelectionPanelPlaceholder.add(competenceSelectionWidget);

		showProgressEntries(contextFactory, true);

	}

	public void showProgressEntries(final LmsContextFactory contextFactory,
			final boolean firstShow) {
		GetRequestManager getRequestManager = new GetRequestManager();
		getRequestManager.showProgressEntries(contextFactory, firstShow, progressPlaceHolder, warningPlaceholder, competenceSelectionWidget, this);
	}

	@UiHandler("filterButton")
	void onFilterButtonClick(ClickEvent event) {
		showProgressEntries(contextFactory, false);
	}

	@UiHandler("warningPlaceholder")
	void onWarningPlaceholderMouseOut(MouseOutEvent event) {
		warningPlaceholder.remove(alert);
	}

	@UiHandler("warningPlaceholder")
	void onWarningPlaceholderClick(ClickEvent event) {
		warningPlaceholder.remove(alert);
	}

	public void reload() {
		competenceSelectionWidget.reload();
		showProgressEntries(contextFactory, false);
	}
	
	public void setAlert(Alert alert1) {
		alert = alert1;
	}
}
