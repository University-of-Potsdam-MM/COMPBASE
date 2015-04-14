package uzuzjmd.competence.gui.client.persistence;

import java.util.List;

import org.fusesource.restygwt.client.Resource;

import uzuzjmd.competence.gui.client.Controller;
import uzuzjmd.competence.gui.client.competenceSelection.CompetenceSelectionTree;

import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.ui.PopupPanel;

public class PostRequestManager {
	
	public PostRequestManager() {
		GWT.log("init PostRequestManager");
	}
	
	public void sendRequirementLinkToServer(List<String> requiredCompetences,
			List<String> followingCompetences, final PopupPanel parent) {
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
								// graphtab.reload(new LinkedList<String>());
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
				GWT.log("ERROR sending sendRequirementLinkToServer to server");			
			}
		}
	}
	
	
	public void sendNonCompulsoryNodesToServer(final String requirementText, final CompetenceSelectionTree competenceTree) {
		if (!competenceTree.convertSelectedTreeToList().isEmpty()) {
			Resource resource = new Resource(Controller.contextFactory.getServerURL()
					+ "/competences/json/coursecontext/create/"
					+ Controller.contextFactory.getCourseId() + "/false");
			try {
				resource.addQueryParam("requirements", requirementText)
						.addQueryParams("competences",
								competenceTree.convertSelectedTreeToList())
						.post().send(new RequestCallback() {
							@Override
							public void onError(Request request,
									Throwable exception) {								
								GWT.log("ERROR with sending non-compulsory nodes to server");
							}

							@Override
							public void onResponseReceived(Request request,
									Response response) {
								GWT.log("successfully send non compulsory competences to server");
								sendCompulsoryNodesToServer(requirementText, competenceTree);
							}
						});
			} catch (RequestException e) {
				GWT.log("ERROR with sending nonCompulsoryNodesToServer");				
			}
		} else {
			sendCompulsoryNodesToServer(requirementText, competenceTree);
		}

	}
	

	private void sendCompulsoryNodesToServer(final String requirementText, CompetenceSelectionTree competenceTree) {
		if (!competenceTree.getCheckedNodes().isEmpty()) {
			Resource resourceCompulsory = new Resource(
					Controller.contextFactory.getServerURL()
							+ "/competences/json/coursecontext/create/"
							+ Controller.contextFactory.getCourseId() + "/true");
			try {
				resourceCompulsory
						.addQueryParam("requirements", requirementText)
						.addQueryParams("competences",
								competenceTree.getCheckedNodes()).post()
						.send(new OkFeedBack(competenceTree));
			} catch (RequestException e) {
				GWT.log("ERROR with sending compulsory Nodes to server");
				
			}
		} else {
			GWT.log("not sending compulsory nodes because non selected");
			competenceTree.reloadTree();
			Controller.reloadController.reload();
		}
	}				
	
	public void deleteRequirements(String competenceSelected, final PopupPanel parent) {
		Resource resource = new Resource(
				Controller.contextFactory.getServerURL()
						+ "/competences/json/prerequisite/delete/"
						+ Controller.contextFactory.getCourseId());
		try {
			resource.addQueryParam("linkedCompetence", competenceSelected)
					.post().send(new RequestCallback() {

						@Override
						public void onResponseReceived(Request request,
								Response response) {
							parent.hide();
//							graphTab.reload(new LinkedList<String>());
						}

						@Override
						public void onError(Request request, Throwable exception) {
							GWT.log(exception.getMessage());
						}
					});
		} catch (RequestException e) { 
			GWT.log("ERROR with deleting requirement");			
		}
	}
	
	
	private class OkFeedBack implements RequestCallback {
		
		private CompetenceSelectionTree competenceTree;

		public OkFeedBack( CompetenceSelectionTree competenceTree) {
			this.competenceTree =  competenceTree;
		}
		
		@Override
		public void onError(Request request, Throwable exception) {		
			GWT.log("ERROR with okfeedback");
		}

		@Override
		public void onResponseReceived(Request request, Response response) {
			GWT.log(response.getStatusText());
			competenceTree.reloadTree();
			Controller.reloadController.reload();
		}
	}
	
	
	
	
	public void handleDeleteClick(CompetenceSelectionTree competenceTree) {
		Resource resourceCompulsory = new Resource(
				Controller.contextFactory.getServerURL()
						+ "/competences/json/coursecontext/delete/"
						+ Controller.contextFactory.getCourseId());
		try {
			resourceCompulsory.post().send(new OkFeedBack(competenceTree));
		} catch (RequestException e) {
			GWT.log("ERROR with handle delete click");
		}

	}
	
	
	
	
}
