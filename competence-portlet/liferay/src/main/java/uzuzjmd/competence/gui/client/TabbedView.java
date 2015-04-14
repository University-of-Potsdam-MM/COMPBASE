package uzuzjmd.competence.gui.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class TabbedView extends Composite {

	private static TabbedViewUiBinder uiBinder = GWT
			.create(TabbedViewUiBinder.class);
	@UiField
	TabLayoutPanel tabPanel;
	@UiField
	HTMLPanel requirementTabPlaceholder;
	@UiField
	HTMLPanel linkTabPlaceholder;
	@UiField
	HTMLPanel progressTabPlaceholder;
//	@UiField
//	HTMLPanel pathTabPlaceholder;

	interface TabbedViewUiBinder extends UiBinder<Widget, TabbedView> {
	}

	public TabbedView() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public TabbedView(String firstName) {
		GWT.log("start initiating TabbedView");
		initWidget(uiBinder.createAndBindUi(this));
		GWT.log("stop initiating TabbedView");
	}

//	@Override
//	protected void onLoad() {
//		GWT.log("loading tabbed view xx");
//		super.onLoad();
//		Date date = new Date();
//		GWT.log("attaching Tabbed View at: " + date.toString());		
//		GWT.log(Controller.contextFactory.getUser());
//		
//		super.onAttach();
//		GWT.log("finished Tabbed View");
//		
//		GWT.log("initiating graphTab");
//		initGraph();
//		GWT.log("finished initiating graph tab");
//	}
//
//	public void initGraph() {
//		GWT.log("start initializing graph");
//		ReloadController.graphTab = new GraphTab();
//		pathTabPlaceholder.add(ReloadController.graphTab);
//	}

}
