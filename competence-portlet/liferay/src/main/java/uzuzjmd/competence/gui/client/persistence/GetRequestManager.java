package uzuzjmd.competence.gui.client.persistence;

import java.util.ArrayList;
import java.util.Map;

import org.fusesource.restygwt.client.JsonCallback;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.Resource;

import uzuzjmd.competence.gui.client.Controller;
import uzuzjmd.competence.gui.client.LmsContextFactory;
import uzuzjmd.competence.gui.client.competenceSelection.CompetenceSelectionTree;
import uzuzjmd.competence.gui.client.competenceSelection.CompetenceSelectionWidget;
import uzuzjmd.competence.gui.client.progressView.ProgressEntry;
import uzuzjmd.competence.gui.client.shared.JsonUtil;
import uzuzjmd.competence.gui.client.tabs.ProgressTab;

import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.gwtext.client.widgets.tree.TreeNode;

public class GetRequestManager {
	
	public GetRequestManager() {
		GWT.log("init PostRequestManager");
	}
	
	public void setCompetenceSelected(final TreeNode node, String selectedFilter, final CompetenceSelectionTree competenceSelectionTree) {
		GWT.log("START setting selected Competences");
		
		
		LmsContextFactory contextFactory = Controller.contextFactory;
		if (selectedFilter != null) {
			Resource resource = new Resource(contextFactory.getServerURL()
					+ "/competences/json/" + selectedFilter + "/"
					+ contextFactory.getCourseId());
			resource.get().send(new JsonCallback() {

				@Override
				public void onSuccess(Method arg0, JSONValue arg1) {
					ArrayList<String> list = new ArrayList<String>();
					convertToList(arg1, list);
					competenceSelectionTree.doSelect(node, list);
				}

				private void convertToList(JSONValue arg1,
						ArrayList<String> list) {
					JSONArray jsonArray = (JSONArray) arg1;
					if (jsonArray != null) {
						int len = jsonArray.size();
						for (int i = 0; i < len; i++) {
							list.add(jsonArray.get(i).toString());
						}
					}
				}

				@Override
				public void onFailure(Method arg0, Throwable arg1) {
					GWT.log("could not get selected competences from server");
				}
			});
		}
		
		GWT.log("STOP setting selected Competences");
	}
	
	public void showProgressEntries(final LmsContextFactory contextFactory,
			final boolean firstShow, final VerticalPanel progressPlaceHolder, final FocusPanel warningPlaceholder, CompetenceSelectionWidget competenceSelectionWidget, final ProgressTab progressTab) {
		
		GWT.log("START updating progress entries");
		
		
		progressPlaceHolder.clear();		
		GWT.log("Initiating progress entries");
		Resource resource = new Resource(contextFactory.getServerURL()
				+ "/competences/json/link/progress/"
				+ contextFactory.getCourseId());
		resource.addQueryParams("competences",
				competenceSelectionWidget.getSelectedCompetences()).get()
				.send(new JsonCallback() {

					@Override
					public void onSuccess(Method arg0, JSONValue arg1) {
						Map<String, String> userProgressMap = JsonUtil
								.toMap(arg1);
						GWT.log("addin progress entries to Progress Tab");
						for (String userName : userProgressMap.keySet()) {
							progressPlaceHolder.add(new ProgressEntry(userName,
									Integer.valueOf(userProgressMap
											.get(userName)), contextFactory));
						}
						GWT.log("finished adding progress entries to progress tab");
						if (!firstShow) {
							Alert alert = new Alert("Erfolgreich gefiltert",
									AlertType.SUCCESS);
							warningPlaceholder.add(alert);		
							progressTab.setAlert(alert);
						}
					}

					@Override
					public void onFailure(Method arg0, Throwable arg1) {
						GWT.log("could not get progress map from server for course context"
								+ contextFactory.getCourseId());

						Alert alert = new Alert(
								"Es gab Probleme bei der Datenbank, kontaktieren Sie einen Entwickler",
								AlertType.ERROR);
						warningPlaceholder.add(alert);
						progressTab.setAlert(alert);
					}
				});
		GWT.log("Initiated progress entries");
	}
}
