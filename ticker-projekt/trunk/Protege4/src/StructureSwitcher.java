import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Set;

import org.semanticweb.HermiT.Reasoner.ReasonerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.model.RemoveAxiom;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.DefaultPrefixManager;

public class StructureSwitcher {

	public static void main(String[] args) throws Exception {
		new StructureSwitcher();
	}

	private StructureSwitcher() throws OWLOntologyCreationException,
			OWLOntologyStorageException, FileNotFoundException {
		// Ontologie laden
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology ontology = manager
				.loadOntologyFromOntologyDocument(OntologyMerger.LOAD_FILE);
		OWLDataFactory dataFactory = manager.getOWLDataFactory();
		String defaultIri = "http://www.semanticweb.org/ontologies/2011/8/Ontology1315487699842.owl#";
		PrefixManager prefixManager = new DefaultPrefixManager(defaultIri);
		// Reasoner erstellen
		OWLReasoner reasoner = new ReasonerFactory().createReasoner(ontology);
		reasoner.precomputeInferences();
		// Zeiten erstellen, aber DataProperties anhängen
		// Before- / After-Beziehungen nicht vergessen!
		OWLObjectProperty tsBefore = dataFactory.getOWLObjectProperty(
				"tsBefore", prefixManager);
		OWLDataProperty tsEventTime = dataFactory.getOWLDataProperty(
				"tsEventTime", prefixManager);
		OWLClass timeClass = dataFactory.getOWLClass("Time", prefixManager);
		OWLNamedIndividual lastCreated = null;
		for (int count = 0; count <= 150; count++) {
			OWLNamedIndividual current = dataFactory.getOWLNamedIndividual(
					Integer.toString(count), prefixManager);
			OWLAxiom subclassOf = dataFactory.getOWLClassAssertionAxiom(
					timeClass, current);
			OWLAxiom eventTime = dataFactory.getOWLDataPropertyAssertionAxiom(
					tsEventTime, current, count);

			manager.applyChange(new AddAxiom(ontology, subclassOf));
			manager.applyChange(new AddAxiom(ontology, eventTime));

			if (lastCreated != null) {
				OWLAxiom beforeAxiom = dataFactory
						.getOWLObjectPropertyAssertionAxiom(tsBefore,
								lastCreated, current);
				manager.applyChange(new AddAxiom(ontology, beforeAxiom));
			}

			lastCreated = current;
		}
		// Durch alle TimeIntervals durchgehen
		// Bei jedem TimeInterval von DataProperty auf ObjectProperty umstellen
		OWLObjectProperty tsMinute = dataFactory.getOWLObjectProperty(
				"tsMinute", prefixManager);
		OWLClass tickerMessageClass = dataFactory.getOWLClass("TickerMessage",
				prefixManager);
		for (Node<OWLNamedIndividual> timeInterval : reasoner.getInstances(
				tickerMessageClass, false)) {
			OWLNamedIndividual representative = timeInterval
					.getRepresentativeElement();
			Set<OWLLiteral> eventTimes = reasoner.getDataPropertyValues(
					representative, tsEventTime);
			if (eventTimes.size() != 1)
				throw new RuntimeException("False Anzahl an Minuten bei "
						+ representative + ": " + eventTimes.size());

			OWLLiteral eventTime = eventTimes.toArray(new OWLLiteral[1])[0];
			int minute = eventTime.parseInteger();

			OWLNamedIndividual minuteIndividual = dataFactory
					.getOWLNamedIndividual(Integer.toString(minute),
							prefixManager);
			OWLAxiom newAxiom = dataFactory.getOWLObjectPropertyAssertionAxiom(
					tsMinute, representative, minuteIndividual);
			OWLAxiom oldAxiom = dataFactory.getOWLDataPropertyAssertionAxiom(
					tsEventTime, representative, minute);

			manager.applyChange(new AddAxiom(ontology, newAxiom));
			manager.applyChange(new RemoveAxiom(ontology, oldAxiom));
		}
		// Ergebnis speichern
		manager.saveOntology(ontology, new FileOutputStream(
				OntologyMerger.SAVE_FILE));
	}
}
