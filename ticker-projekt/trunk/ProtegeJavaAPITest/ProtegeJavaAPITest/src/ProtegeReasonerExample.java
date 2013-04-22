
import java.util.Collection;
import java.util.Iterator;

import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protegex.owl.inference.dig.DefaultProtegeDIGReasoner;
import edu.stanford.smi.protegex.owl.inference.pellet.ProtegePelletJenaReasoner;
import edu.stanford.smi.protegex.owl.inference.pellet.ProtegePelletOWLAPIReasoner;
import edu.stanford.smi.protegex.owl.inference.protegeowl.ReasonerManager;
import edu.stanford.smi.protegex.owl.inference.reasoner.ProtegeReasoner;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;


/**
 * Example on how to interact with different reasoners using the Protege OWL API.
 * This class shows how to create an instance of a reasoner, query it for inferred
 * information.
 * 
 * @author Tania Tudorache <tudorache@stanford.edu>
 *
 * @author Julian Dehne 
 * Mit der Powershell kann man Pellet starten, indem man .\Pellet dig in dem 
 * Pellet Root Verzeichnis ausführt.
 * 
 */
public class ProtegeReasonerExample { 

	public static void main(String[] args) { 

		final String ONTOLOGY_URL = "http://www.co-ode.org/ontologies/pizza/2007/02/12/pizza.owl";

		try {
			// Load the ontology from the specified URL
			OWLModel owlModel = ProtegeOWL.createJenaOWLModelFromURI(ONTOLOGY_URL);

			//runs example with the DIG reasoner -assumes reasoner runs on http://localhost:8081
			runExample(owlModel, createDIGReasoner(owlModel));

			//runs example with the Pellet reasoner (accessed through Jena)
			runExample(owlModel, createPelletJenaReasoner(owlModel));

			//runs example with the Pellet reasoner (accessed through OWL-API)
			runExample(owlModel, createPelletOWLAPIReasoner(owlModel));

		} catch (Exception e) {	
			e.printStackTrace();
		} 		
	} 


	private static void runExample(OWLModel owlModel, ProtegeReasoner reasoner) {

		if (reasoner == null) {
			System.out.println("Skipping.. reasoner is null.");
			return;
		}

		System.out.println("\n ============ Running example with reasoner " + reasoner.getClass().getSimpleName() + " ===================\n");

		try { 
			// Get the VegetarianPizza OWLNamedClass from the OWLModel 
			OWLNamedClass vegetarianPizza = owlModel.getOWLNamedClass("VegetarianPizza"); 
			if(vegetarianPizza != null) { 
				// Get the number of asserted subclasses of VegetarianPizza 
				Collection assertedSubclasses = vegetarianPizza.getNamedSubclasses(); 
				System.out.println("Number of asserted VegetarianPizzas: " + assertedSubclasses.size()); 

				// Now get the inferred subclasses of VegetarianPizza 
				Collection inferredSubclasses = reasoner.getSubclasses(vegetarianPizza); 
				System.out.println("Number of inferred VegetarianPizzas: " + inferredSubclasses.size()); 
				System.out.println("VegetarianPizzas:"); 
				for(Iterator it = inferredSubclasses.iterator(); it.hasNext();) { 
					OWLNamedClass curClass = (OWLNamedClass) it.next(); 
					System.out.println("\t" + curClass.getName()); 
				} 


				//	We can classify the whole ontology, which will put the
				// inferred class hierarchy information directly into the Protégé-OWL model. 
				System.out.println("Classifying taxonomy..."); 
				reasoner.classifyTaxonomy(); 
				System.out.println("...Classified taxonomy!"); 

				// We can then use the methods on OWLNamedClass for getting inferred
				// information, without having to make separate queries to the
				// reasoner, as this information is now in the OWLModel. 
				System.out.println("Inferred subclasses of VegetarianPizza:"); 
				inferredSubclasses = vegetarianPizza.getInferredSubclasses(); 
				for(Iterator it = inferredSubclasses.iterator(); it.hasNext();) { 
					OWLNamedClass curClass = (OWLNamedClass) it.next(); 
					System.out.println("\t" + curClass.getName()); 
				}

				System.out.println("Descendand classes of VegetarianPizza:"); 
				inferredSubclasses = reasoner.getDescendantClasses(vegetarianPizza); 
				for(Iterator it = inferredSubclasses.iterator(); it.hasNext();) { 
					OWLNamedClass curClass = (OWLNamedClass) it.next(); 
					System.out.println("\t" + curClass.getName()); 
				}
			} 
			else { 
				System.out.println("Could not find VegetarianPizza"); 
			} 
		} 
		catch(Exception e) { 
			e.printStackTrace(); 
		} 
	}


	private static DefaultProtegeDIGReasoner createDIGReasoner(OWLModel owlModel) {
		final String REASONER_URL = "http://localhost:8081";

		// Get the reasoner manager and obtain a reasoner for the OWL model. 
		ReasonerManager reasonerManager = ReasonerManager.getInstance();

		DefaultProtegeDIGReasoner reasoner = (DefaultProtegeDIGReasoner) reasonerManager.createProtegeReasoner(owlModel, reasonerManager.getDefaultDIGReasonerClass());			 
		// Set the reasoner URL and test the connection - only for DIG 
		reasoner.setURL(REASONER_URL);

		if (!reasoner.isConnected()) {
			System.out.println("Reasoner not connected!");		
		}

		return reasoner;
	}


	private static ProtegeReasoner createPelletJenaReasoner(OWLModel owlModel) {

		// Get the reasoner manager and obtain a reasoner for the OWL model. 
		ReasonerManager reasonerManager = ReasonerManager.getInstance();

		//Get an instance of the Protege Pellet reasoner
		ProtegeReasoner reasoner = reasonerManager.createProtegeReasoner(owlModel, ProtegePelletJenaReasoner.class);

		return reasoner;
	}


	private static ProtegeReasoner createPelletOWLAPIReasoner(OWLModel owlModel) {
		// Get the reasoner manager and obtain a reasoner for the OWL model. 
		ReasonerManager reasonerManager = ReasonerManager.getInstance();

		//Get an instance of the Protege Pellet reasoner
		ProtegeReasoner reasoner = reasonerManager.createProtegeReasoner(owlModel, ProtegePelletOWLAPIReasoner.class);

		return reasoner;
	}

}