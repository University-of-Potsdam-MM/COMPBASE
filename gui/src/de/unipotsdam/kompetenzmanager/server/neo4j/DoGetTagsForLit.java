package de.unipotsdam.kompetenzmanager.server.neo4j;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.RelationshipIndex;

import de.unipotsdam.kompetenzmanager.shared.Graph;
import de.unipotsdam.kompetenzmanager.shared.GraphNode;
import de.unipotsdam.kompetenzmanager.shared.Literature;
import de.unipotsdam.kompetenzmanager.shared.LiteratureEntry;

public class DoGetTagsForLit extends DoNeo implements Do {

	private Literature literature;

	public DoGetTagsForLit(GraphDatabaseService graphDB, Index<Node> nodeIndex,
			RelationshipIndex relIndex, Literature literature) {
		super(graphDB, nodeIndex, relIndex);
		this.literature = literature;

	}

	@Override
	public synchronized Graph doit() {
		Graph graph = new Graph();
		getTheConnectedGraph(graph);
//		getTheRelationShipInTheGraph(graph);
		return graph;
	}

	private synchronized void getTheRelationShipInTheGraph(Graph graph) {
	for (GraphNode graphNode : graph.nodes) {
			for (GraphNode graphNode2 : graph.nodes) {
				if (!graphNode.equals(graphNode2)) {
					DoFindShortestPath doFindShortestPathBetweenResults = new DoFindShortestPath(
							this.graphDb, nodeIndex, this.relIndex,
							graphNode2.label, graphNode.label);
					graph.mergeWith(doFindShortestPathBetweenResults.doit());
				}
			}
		}
	}

	private synchronized void getTheConnectedGraph(Graph graph) {
		for (LiteratureEntry literatureEntry : literature.literatureEntries) {
			Node litNode = getLitNode(literatureEntry);
			Iterable<Relationship> rels = litNode
					.getRelationships(RelTypes.isTagOf);
			if (rels.iterator().hasNext()) {
				for (Relationship relationship : rels) {
					Node graphNode = relationship.getOtherNode(litNode);
					DoFindShortestPath doFindShortestPath = new DoFindShortestPath(
							this.graphDb, nodeIndex, this.relIndex, "rootnode",
							(String) graphNode.getProperty(NODE_VALUE));
					graph.mergeWith(doFindShortestPath.doit());
				}
			}

		}
	}

}