package editor;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLLogicalEntity;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.util.DefaultPrefixManager;

import com.clarkparsia.pellet.owlapiv3.PelletReasoner;
import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;

public class CommandInterpreter {

	public static final String DEFAULT_IRI = "http://www.semanticweb.org/ontologies/2011/8/Ontology1315487699842.owl#";

	private final OWLOntologyManager ontologyManager;
	private final OWLOntology ontology;
	private final OWLDataFactory dataFactory;
	private final PrefixManager prefixManager;

	public CommandInterpreter(File ontologyFile)
			throws OWLOntologyCreationException {
		ontologyManager = OWLManager.createOWLOntologyManager();
		ontology = ontologyManager
				.loadOntologyFromOntologyDocument(ontologyFile);
		dataFactory = ontologyManager.getOWLDataFactory();
		prefixManager = new DefaultPrefixManager(DEFAULT_IRI);

		PelletReasoner reasoner = PelletReasonerFactory.getInstance()
				.createNonBufferingReasoner(ontology);

		CommandParser.setSingleton(new CommandParser(dataFactory,
				prefixManager, reasoner));
	}

	public void interpret(String[] commands, int minute, String tickerName) {
		Iterable<TimeIntervalEvent> events = CommandParser.getSingleton()
				.processAsTimeIntervalEvents(commands, minute);
		Ticker ticker = this.createMatchingTickerMessage(events, tickerName,
				minute);
		this.applyToOntology(events, ticker);
	}

	private Ticker createMatchingTickerMessage(
			Iterable<TimeIntervalEvent> events, String tickerName, int minute) {
		OWLNamedIndividual tickerIndividual = dataFactory
				.getOWLNamedIndividual(tickerName, prefixManager);
		CommandParser.getSingleton().ensureExists(tickerIndividual);

		return new Ticker(tickerIndividual, events, minute, tickerName);
	}

	private void applyToOntology(Iterable<TimeIntervalEvent> events,
			Ticker ticker) {
		for (TimeIntervalEvent event : events) {
			for (OWLAxiom axiom : event.applyTo(dataFactory, prefixManager)) {
				AddAxiom addAxiom = new AddAxiom(ontology, axiom);
				ontologyManager.applyChange(addAxiom);
			}
		}
		for (OWLAxiom axiom : ticker.applyTo(dataFactory, prefixManager)) {
			AddAxiom addAxiom = new AddAxiom(ontology, axiom);
			ontologyManager.applyChange(addAxiom);
		}
	}

	public Iterable<String> getTickerNames() {
		Collection<String> tickerNames = new ArrayList<String>();
		Set<OWLNamedIndividual> tickers = CommandParser.getSingleton()
				.getTickers();
		for (OWLNamedIndividual ticker : tickers) {
			String tickerName = getRenderName(ticker);
			tickerNames.add(tickerName);
		}
		return tickerNames;
	}

	public static String getRenderName(OWLLogicalEntity individual) {
		String name = individual.toString().substring(1)
				.substring(DEFAULT_IRI.length());
		name = name.substring(0, name.length() - 1);
		return name;
	}

	public void saveOntology() throws OWLOntologyStorageException {
		ontologyManager.saveOntology(ontology);
	}
}
