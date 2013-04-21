import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.util.DefaultPrefixManager;

import com.clarkparsia.pellet.owlapiv3.PelletReasoner;
import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;

public class Distinctor {

	private PelletReasoner reasoner;
	private OWLDataFactory dataFactory;
	private OWLOntologyManager manager;
	private OWLOntology ontology;

	public static void main(String[] args) throws Exception {
		OntologyMerger.parseLoadAndSaveFile(args);
		new Distinctor();
	}

	private Distinctor() throws Exception {
		manager = OWLManager.createOWLOntologyManager();
		ontology = manager
				.loadOntologyFromOntologyDocument(OntologyMerger.LOAD_FILE);

		dataFactory = manager.getOWLDataFactory();
		String defaultIri = "http://www.semanticweb.org/ontologies/2011/8/Ontology1315487699842.owl#";
		PrefixManager prefixManager = new DefaultPrefixManager(defaultIri);

		reasoner = PelletReasonerFactory.getInstance()
				.createNonBufferingReasoner(ontology);
		manager.addOntologyChangeListener(reasoner);

		// Alle Spieler distinct machen
		OWLClass playerClass = dataFactory.getOWLClass("Player", prefixManager);
		makeIndividualsDistinct(playerClass);

		OutputStream output = new FileOutputStream(OntologyMerger.SAVE_FILE);
		manager.saveOntology(ontology, output);
		output.close();
	}

	private void makeIndividualsDistinct(OWLClass instanceType) {
		NodeSet<OWLNamedIndividual> individuals = reasoner.getInstances(
				instanceType, false);
		Set<OWLNamedIndividual> distinctIndividuals = new HashSet<OWLNamedIndividual>();
		for (Node<OWLNamedIndividual> individual : individuals)
			distinctIndividuals.add(individual.getRepresentativeElement());
		OWLAxiom axiom = dataFactory
				.getOWLDifferentIndividualsAxiom(distinctIndividuals);
		AddAxiom add = new AddAxiom(ontology, axiom);
		manager.applyChange(add);
	}
}
