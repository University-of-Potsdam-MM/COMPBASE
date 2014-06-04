package de.unipotsdam.ontologypersistence.main;

import java.io.IOException;

import org.apache.jena.fuseki.EmbeddedFusekiServer;

import de.unipotsdam.ontologypersistence.owl.access.OntologyManager;

public class FusekiServer {

	public static void main(String[] args) throws IOException {
		startServer();
	}

	public static void startServer() {
		OntologyManager compOntologyManager = new OntologyManager();
		startFuseki(compOntologyManager);
	}

	public static void startFuseki(OntologyManager compOntologyManager) {
		compOntologyManager.begin();

		EmbeddedFusekiServer server = EmbeddedFusekiServer.create(3030,
				compOntologyManager.getDataset().asDatasetGraph(), "comp");
		server.start();
	}
}
