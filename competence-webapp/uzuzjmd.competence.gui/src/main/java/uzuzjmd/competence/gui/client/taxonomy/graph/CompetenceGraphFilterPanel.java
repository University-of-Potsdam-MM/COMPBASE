package uzuzjmd.competence.gui.client.taxonomy.graph;

import uzuzjmd.competence.gui.client.shared.widgets.taxonomy.CompetenceSelectionWidget;
import uzuzjmd.competence.gui.client.taxonomy.GraphTab;

import com.github.gwtbootstrap.client.ui.Button;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class CompetenceGraphFilterPanel extends Composite {

	private static CompetenceGraphFilterPanelUiBinder uiBinder = GWT
			.create(CompetenceGraphFilterPanelUiBinder.class);
	@UiField
	Button cancelButton;

	@UiField
	SimplePanel filteredCompetencesPlaceholder;

	private CompetenceSelectionWidget filterCompetencesWidget;
	private PopupPanel parent;
	private GraphTab graphTab;

	interface CompetenceGraphFilterPanelUiBinder extends
			UiBinder<Widget, CompetenceGraphFilterPanel> {
	}

	public CompetenceGraphFilterPanel(GraphTab graphtab, PopupPanel parent) {
		initWidget(uiBinder.createAndBindUi(this));
		this.parent = parent;
		this.graphTab = graphtab;

		filterCompetencesWidget = new CompetenceSelectionWidget(null,
				"coursecontextnofilter/", "  Kompetenzen filtern", false);
		filteredCompetencesPlaceholder.add(filterCompetencesWidget);

	}

	@UiHandler("cancelButton")
	void onCancelButtonClick(ClickEvent event) {
		parent.hide();
	}

	@UiHandler("filterButton")
	void onFilterButtonClick(ClickEvent event) {
		parent.hide();
		graphTab.reload(filterCompetencesWidget.getSelectedCompetences());
	}

	public void reload() {
		filterCompetencesWidget.reload();
	}
}