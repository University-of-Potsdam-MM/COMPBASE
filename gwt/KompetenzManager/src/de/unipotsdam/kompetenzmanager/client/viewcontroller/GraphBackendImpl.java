package de.unipotsdam.kompetenzmanager.client.viewcontroller;

import java.util.Collection;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import de.unipotsdam.kompetenzmanager.client.ShowCompetenceBinder2;
import de.unipotsdam.kompetenzmanager.shared.Graph;
import de.unipotsdam.kompetenzmanager.shared.GraphLiteraturePair;
import de.unipotsdam.kompetenzmanager.shared.GraphNode;
import de.unipotsdam.kompetenzmanager.shared.Literature;
import de.unipotsdam.kompetenzmanager.shared.LiteratureEntry;

public class GraphBackendImpl implements GraphBackendAsync {
	
	private ShowCompetenceBinder2 showCompetenceBinder;

	public GraphBackendImpl(ShowCompetenceBinder2 showCompetenceBinder) {
		this.showCompetenceBinder = showCompetenceBinder;
	}

	/**
	 * Create a remote service proxy to talk to the server-side service.
	 */
	private final GraphBackendAsync graphBackEnd = GWT
			.create(GraphBackend.class);

	@Override
	public void getFullGraph(AsyncCallback<Graph> graph) {
		graphBackEnd.getFullGraph(new GraphUpdater<Graph>(showCompetenceBinder));
	}

	@Override
	public void addNode(GraphNode sourceNode, GraphNode newNode,
			String kantenLabel, AsyncCallback<Graph> callback) {
		if (callback == null) {
			callback = new GraphUpdater<Graph>(showCompetenceBinder);
		}
		graphBackEnd.addNode(sourceNode, newNode, kantenLabel, callback);
	}

	@Override
	public void findShortestPath(String keyword, AsyncCallback<Graph> callback) {
		if (callback == null) {
			callback = new GraphUpdater<Graph>(showCompetenceBinder);
		}
		graphBackEnd.findShortestPath(keyword, new GraphUpdater<Graph>(showCompetenceBinder));
	}

	@Override
	public void removeNode(GraphNode targetNode, AsyncCallback<Graph> callback) {
		if (callback == null) {
			callback = new GraphUpdater<Graph>(showCompetenceBinder);
		}
		graphBackEnd.removeNode(targetNode, callback);
	}

	@Override
	public void findShortestPath(String fromNode, String toNode,
			AsyncCallback<Graph> graphUpdater) {
		graphBackEnd.findShortestPath(fromNode, toNode, graphUpdater);		
	}

	@Override
	public void expandNode(String nodeName, AsyncCallback<Graph> graphUpdater) {
		graphBackEnd.expandNode(nodeName, graphUpdater);		
	}

	@Override
	public void addNode(Graph graph, GraphNode sourceNode, GraphNode newNode,
			String kantenLabel, AsyncCallback<Graph> callback) {
		graphBackEnd.addNode(graph, sourceNode, newNode, kantenLabel, callback);
		
	}

	@Override
	public void findShortestPath(Graph graph, String keyword,
			AsyncCallback<Graph> callback) {
		graphBackEnd.findShortestPath(graph, keyword, callback);
		
	}

	@Override
	public void removeNode(Graph graph, GraphNode targetNode,
			AsyncCallback<Graph> callback) {
		graphBackEnd.removeNode(graph, targetNode, callback);
		
	}

	@Override
	public void findShortestPath(Graph graph, String fromNode, String toNode,
			AsyncCallback<Graph> graphUpdater) {
		graphBackEnd.findShortestPath(graph, fromNode, toNode, graphUpdater);
		
	}

	@Override
	public void expandNode(Graph graph, String nodeName,
			AsyncCallback<Graph> graphUpdater) {
		graphBackEnd.expandNode(graph, nodeName, graphUpdater);		
	}

	@Override
	public void connectNodes(Collection<String> graphNodes, String toNode,
			AsyncCallback<Graph> graphUpdater) {
		graphBackEnd.connectNodes(graphNodes, toNode, graphUpdater);
		
	}

	@Override
	public void connectNodes(Graph graph, Collection<String> graphNodes,
			String toNode, AsyncCallback<Graph> graphUpdater) {
		graphBackEnd.connectNodes(graph, graphNodes, toNode, graphUpdater);
		
	}

	@Override
	public void getLiteratureForTags(Graph graph,
			AsyncCallback<Literature> literature) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void getTagsforLiterature(Literature literature,
			AsyncCallback<Graph> graph) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addOrUpdateLiteratureEntry(Literature literatureStored, LiteratureEntry literatureEntry,
			AsyncCallback<Literature> literature) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeLiteratureEntry(Literature literatureStored, LiteratureEntry literatureEntry,
			AsyncCallback<Literature> lierature) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void getFullLiterature(AsyncCallback<Literature> literature) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void connectLiteratureToGraph(Literature literature, Graph graph,
			AsyncCallback<GraphLiteraturePair> callback) {
		// TODO Auto-generated method stub
		
	}

}