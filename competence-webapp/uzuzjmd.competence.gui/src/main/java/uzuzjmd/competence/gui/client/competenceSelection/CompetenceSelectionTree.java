package uzuzjmd.competence.gui.client.competenceSelection;

import java.util.ArrayList;

import org.fusesource.restygwt.client.JsonCallback;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.Resource;

import uzuzjmd.competence.gui.client.LmsContextFactory;
import uzuzjmd.competence.gui.client.shared.widgets.CheckableTreePanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.RootPanel;
import com.gwtext.client.core.Connection;
import com.gwtext.client.widgets.tree.MultiSelectionModel;
import com.gwtext.client.widgets.tree.TreeNode;
import com.gwtext.client.widgets.tree.XMLTreeLoader;
import com.gwtext.client.widgets.tree.event.TreePanelListenerAdapter;

public class CompetenceSelectionTree extends CheckableTreePanel {

	private SelectedFilter selectedFilter;

	@Deprecated
	public CompetenceSelectionTree(String databaseConnectionString,
			String rootLabel, String className, Integer width, Integer height,
			String title, LmsContextFactory contextFactory) {
		super(databaseConnectionString, rootLabel, className, width, height,
				title, contextFactory, null);
		GWT.log("setting showchecked to: " + false);
		super.showChecked = false;
		treePanel.addListener(new MyTreePanelLister());
	}

	public CompetenceSelectionTree(String dataString,
			LmsContextFactory contextFactory, SelectedFilter selectedFilter2,
			Boolean showChecked) {
		// TODO Auto-generated constructor stub
		super(dataString, "Kompetenzen", "competenceView", RootPanel.get(
				"rootContainer").getOffsetWidth() - 300, 350, "Kompetenzen",
				contextFactory, showChecked);
		this.selectedFilter = selectedFilter2;
		treePanel.addListener(new MyTreePanelLister());
	}

	// public void setSelectedFilter(String selectedFilter) {
	// this.selectedFilter = selectedFilter;
	// }

	private void setCompetenceSelected(final TreeNode node) {
		if (selectedFilter != null) {
			Resource resource = new Resource(contextFactory.getServerURL()
					+ "/competences/json/" + selectedFilter.toString() + "/"
					+ contextFactory.getCourseId());
			resource.get().send(new JsonCallback() {

				@Override
				public void onSuccess(Method arg0, JSONValue arg1) {
					ArrayList<String> list = new ArrayList<String>();
					convertToList(arg1, list);
					doSelect(node, list);
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
	}

	private void doSelect(TreeNode node, ArrayList<String> list) {
		MultiSelectionModel selectionModel = (MultiSelectionModel) getTreePanel()
				.getSelectionModel();
		TreeNode found = treePanel.getNodeById(node.getId());
		for (String selected : list) {
			selected = cleanString(selected);
			String nodeString = cleanString(found.getText());
			if (selected.equals(nodeString)) {
				selectionModel.select(node, true);
			}
		}

		for (int i = 0; i < node.getChildNodes().length; i++) {
			TreeNode child = (TreeNode) node.getChildNodes()[i];
			doSelect(child, list);
		}

	}

	private String cleanString(String selected) {
		return selected.trim().toLowerCase().replace("\"", "");
	}

	@Override
	protected XMLTreeLoader initXMLLoader() {
		final XMLTreeLoader loader = new XMLTreeLoader();
		loader.setDataUrl(databaseConnectionString);
		loader.setMethod(Connection.GET);
		loader.setRootTag("competenceRoot");
		loader.setFolderTitleMapping("@name");
		loader.setFolderTag("competence");
		loader.setLeafTitleMapping("@name");
		loader.setLeafTag("competence");
		// loader.setQtipMapping("@treetipp");
		loader.setDisabledMapping("@disabled");
		GWT.log(getShowChecked() + "");
		if (getShowChecked()) {
			loader.setCheckedMapping("isCompulsory");
		}
		return loader;
	}

	private class MyTreePanelLister extends TreePanelListenerAdapter {
		@Override
		public void onLoad(TreeNode node) {
			setCompetenceSelected(node);
		}
	}

	@Override
	protected Boolean getShowChecked() {
		return super.showChecked;
	}

}
