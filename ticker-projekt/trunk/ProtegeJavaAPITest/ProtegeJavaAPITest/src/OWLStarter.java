
import com.hp.hpl.jena.util.FileUtils;
import com.sun.corba.se.spi.oa.OAInvocationInfo;
import edu.stanford.smi.protegex.owl.inference.reasoner.exception.ProtegeReasonerException;
import edu.stanford.smi.protegex.owl.model.OWLCardinality;
import edu.stanford.smi.protegex.owl.model.OWLMaxCardinality;
import edu.stanford.smi.protegex.owl.model.OWLObjectProperty;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import edu.stanford.smi.protege.exception.OntologyLoadException;
import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protegex.owl.inference.pellet.ProtegePelletJenaReasoner;
import edu.stanford.smi.protegex.owl.inference.protegeowl.ReasonerManager;
import edu.stanford.smi.protegex.owl.inference.reasoner.ProtegeReasoner;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLNamedClass;
import java.util.ArrayList;
import java.util.Collection;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * 
 * @author Julian
 */
public class OWLStarter {

    /**
     * @param args
     * @throws OntologyLoadException
     * @throws FileNotFoundException
     */
    public static void main(String[] args) throws OntologyLoadException,
            FileNotFoundException,
            ProtegeReasonerException {
        //Logger.getRootLogger().setLevel(Level.OFF);

        // Ontology laden
        InputStream ontologyStream = new FileInputStream(new File(
                "alpha-ontology\\tickerprojekt.owl"));
        JenaOWLModel owlModel = ProtegeOWL.createJenaOWLModelFromInputStream(ontologyStream);

        // Default-Namespace setzen
        owlModel.getNamespaceManager().setDefaultNamespace(
                "http://smartweb.semanticweb.org/ontology/sportevent#");

        OWLNamedClass employee = owlModel.createOWLNamedClass("Employee");
        OWLIndividual hendrik = employee.createOWLIndividual("Hendrik");

        OWLNamedClass company = owlModel.createOWLNamedClass("Company");
        OWLIndividual ibm = company.createOWLIndividual("IBM");
        OWLIndividual microsoft = company.createOWLIndividual("Microsoft");
        
        OWLNamedClass something = owlModel.createOWLNamedClass("Something");
        OWLIndividual someIndivdual = something.createOWLIndividual("someIndividual");

        OWLObjectProperty ceoOfProp = owlModel.createOWLObjectProperty("ceoOf");
        ceoOfProp.setDomain(employee);
        ceoOfProp.setRange(company);

        //OWLCardinality propCardinality = owlModel.createOWLCardinality(ceoOfProp, 1, company);
        //employee.addSuperclass(propCardinality);

        hendrik.addPropertyValue(ceoOfProp, ibm);
        hendrik.addPropertyValue(ceoOfProp, microsoft);
        hendrik.addPropertyValue(ceoOfProp, someIndivdual);

        //saving
        owlModel.save(new File("test.owl").toURI(), FileUtils.langXMLAbbrev, new ArrayList());

        //runs example with the Pellet reasoner (accessed through Jena)
        //runExample(owlModel, createPelletJenaReasoner(owlModel));

    }

    /*
     * Do something with the reasoner
     */
    private static void runExample(OWLModel owlModel, ProtegeReasoner reasoner) throws ProtegeReasonerException {
        reasoner.computeInconsistentConcepts();
        reasoner.computeInferredHierarchy();
        reasoner.computeInferredIndividualTypes();

        //producing conflict:
        System.out.println("Anzahl der inkonsistenten Classen:" + reasoner.getOWLModel().getInconsistentClasses().size());
    }

    private static ProtegeReasoner createPelletJenaReasoner(OWLModel owlModel) {
        // Get the reasoner manager and obtain a reasoner for the OWL model. 
        ReasonerManager reasonerManager = ReasonerManager.getInstance();
        //Get an instance of the Protege Pellet reasoner
        return reasonerManager.createProtegeReasoner(owlModel, ProtegePelletJenaReasoner.class);
    }
}
