import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.util.DefaultPrefixManager;

import util.Stop;
import util.Timeable;
import algorithm.phase.BaseConsistencyCheck;
import algorithm.phase.ConnectionPhase;
import algorithm.phase.TickerMergePhase;

public class OntologyMerger implements Timeable {

	public static File LOAD_FILE = new File(
			"C:\\Users\\hgessner\\Dropbox\\Uni\\Wissensrepräsentation und Reasoning\\Richtig Ticken\\Ontologien\\WPV-Test-Ontologien\\Proof-of-concept-Ticker.owl");
	public static File SAVE_FILE = new File(
			"C:\\Users\\hgessner\\Dropbox\\Uni\\Wissensrepräsentation und Reasoning\\Richtig Ticken\\Ontologien\\WPV-Test-Ontologien\\Proof-of-concept-Result.owl");

	public static void main(String[] args) {
		parseLoadAndSaveFile(args);
		Stop.watch("Merge-Vorgang", new OntologyMerger());
	}

	public static void parseLoadAndSaveFile(String args[]) {
		if (args.length < 4)
			printUsageAndExit();

		String from = args[0];
		String fromPath = args[1];
		String to = args[2];
		String toPath = args[3];

		if (!from.equals("-from") || !to.equals("-to"))
			printUsageAndExit();

		File fromFile = new File(fromPath);
		File toFile = new File(toPath);

		if (!fromFile.isFile())
			printUsageAndExit();

		LOAD_FILE = fromFile;
		SAVE_FILE = toFile;
	}

	private static void printUsageAndExit() {
		System.out.println("Benutzung: -from <loadpath> -to <savepath>");
		System.exit(1);
	}

	@Override
	public void time() {
		try {
			OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
			final OWLOntology ontology = manager
					.loadOntologyFromOntologyDocument(LOAD_FILE);

			OWLDataFactory dataFactory = manager.getOWLDataFactory();
			String defaultIri = "http://www.semanticweb.org/ontologies/2011/8/Ontology1315487699842.owl#";
			PrefixManager prefixManager = new DefaultPrefixManager(defaultIri);

			BaseConsistencyCheck check = new BaseConsistencyCheck(ontology,
					dataFactory, prefixManager, manager);
			Stop.watch("Basis-Konsistenzüberprüfung", check);
			ConnectionPhase connection = new ConnectionPhase(
					check.getOntology());
			Stop.watch("Verknüpfungsschaffung", connection);
			TickerMergePhase merge = new TickerMergePhase(check.getOntology());
			Stop.watch("TickerMessage-Verknüpfung", merge);

			// Ontologie speichern
			OutputStream output = new FileOutputStream(SAVE_FILE);
			check.getOntology().getOntologyManager()
					.saveOntology(check.getOntology().getOntology(), output);
			output.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
