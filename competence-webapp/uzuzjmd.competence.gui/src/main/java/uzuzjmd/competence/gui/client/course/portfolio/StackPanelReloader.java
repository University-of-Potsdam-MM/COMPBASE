package uzuzjmd.competence.gui.client.course.portfolio;

import java.util.List;

import org.fusesource.restygwt.client.JsonCallback;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.Resource;

import uzuzjmd.competence.gui.client.course.portfolio.EvidenceStackPanel.CompetenceLinksMapCodec;
import uzuzjmd.competence.gui.client.viewcontroller.Controller;
import uzuzjmd.competence.service.rest.client.dto.CompetenceLinksMap;
import uzuzjmd.competence.service.rest.client.dto.CompetenceLinksView;

import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DecoratedStackPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;

class StackPanelReloader {
	private DecoratedStackPanel decoratedStackPanel;
	private String username;
	public Boolean commentEntryWasSuccess;
	public String commentEntryIdLastUpdated = "";
	private int selected;

	public StackPanelReloader(DecoratedStackPanel decoratedStackPanel,
			String username, Boolean commentEntryWasSuccess) {
		this.decoratedStackPanel = decoratedStackPanel;
		this.username = username;
		this.commentEntryWasSuccess = commentEntryWasSuccess;
	}

	public void reload() {
		selected = decoratedStackPanel.getSelectedIndex();
		this.decoratedStackPanel.clear();
		loadEvidencesFromServer(username);
	}

	public void setCommentEntryWasSuccess(Boolean commentEntryWasSuccess) {
		this.commentEntryWasSuccess = commentEntryWasSuccess;
	}

	private void loadEvidencesFromServer(final String userName) {
		Resource resource = new Resource(
				Controller.contextFactory.getServerURL()
						+ "/competences/json/link/overview/" + userName);
		resource.get().send(new MyJsonCallBack(this));
	}

	class MyJsonCallBack implements JsonCallback {

		private StackPanelReloader stackPanelReloader;

		public MyJsonCallBack(StackPanelReloader stackPanelReloader) {
			this.stackPanelReloader = stackPanelReloader;
		}

		@Override
		public void onSuccess(Method arg0, JSONValue arg1) {
			CompetenceLinksMapCodec codec = GWT
					.create(CompetenceLinksMapCodec.class);
			CompetenceLinksMap competenceLinksMap = codec.decode(arg1);
			GWT.log(competenceLinksMap.getMapUserCompetenceLinks().size() + "");
			for (String competenceName : competenceLinksMap
					.getMapUserCompetenceLinks().keySet()) {
				List<CompetenceLinksView> links = competenceLinksMap
						.getMapUserCompetenceLinks().get(competenceName);

				GWT.log(links.size() + "");
				Panel panel = new SimplePanel();
				// panel.setStylePrimaryName("accordion-group");
				EvidenceLinkWidget evidenceWidget = new EvidenceLinkWidget(
						links, stackPanelReloader, username);
				panel.add(evidenceWidget);
				decoratedStackPanel.add(panel,
						"<div class=\"stackTitlePicture\"></div>"
								+ "<div class=\"stackTitle\">" + competenceName
								+ "</div>", true);
				decoratedStackPanel.showStack(selected);
			}
		}

		@Override
		public void onFailure(Method arg0, Throwable arg1) {
			// TODO Auto-generated method stub
			Window.alert("wuff:-(1");
		}

	}
}