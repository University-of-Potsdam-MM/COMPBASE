package uzuzjmd.owl.util;

import java.io.IOException;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import uzuzjmd.console.util.LogStream;
import uzuzjmd.owl.competence.ontology.CompObjectProperties;
import uzuzjmd.owl.competence.ontology.CompOntClass;
import uzuzjmd.owl.competence.queries.CompetenceQueries;
import uzuzjmd.owl.reasoning.jena.ModelChangeListener;
import uzuzjmd.owl.reasoning.jena.RuleFactory;
import uzuzjmd.owl.reasoning.jena.SimpleRulesReasoner;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.tdb.TDBFactory;
import com.hp.hpl.jena.vocabulary.OWL2;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.ReasonerVocabulary;

public class CompOntologyManager {

	static final Logger logger = LogManager.getLogger(CompOntologyManager.class
			.getName());
	static LogStream logStream = new LogStream(logger, Level.TRACE);

	private CompOntologyAccess util;
	private OntModel m;
	private SimpleRulesReasoner rulesReasoner;
	private CompetenceQueries queries;
	private ModelChangeListener modelChangedListener;

	public CompOntologyManager() {
		// initialize Model
		initializeOntologyModelInMemory();

		// initializeOntologyModel();
		this.queries = new CompetenceQueries(getM());
		this.util = new CompOntologyAccess(getM(), getQueries());

		// init simple Rules Reasoner
		initReasoner();
		initRulesFactory(rulesReasoner);
		// switchOnDebug();

		// apply rules whenever the model is changed
		this.modelChangedListener = new ModelChangeListener(rulesReasoner);

		// defaultmäßif ist derReasoner angeschaltet
		registerReasoner();

	}

	public void unregisterReasoner() {
		m.unregister(modelChangedListener);
	}

	public void registerReasoner() {
		m.register(modelChangedListener);
	}

	private void initReasoner() {
		try {
			rulesReasoner = new SimpleRulesReasoner(m, false);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private SimpleRulesReasoner initRulesReasoning() {
		rulesReasoner.addRuleAsString(
				"(?a comp:LearnerOf ?b) -> (?b comp:LearnerOfInverse ?a)",
				"operatorInverse");
		rulesReasoner.reason();
		return rulesReasoner;
	}

	public OntModel createBaseOntology() {
		// m = this.util.initializeOntologyModel();
		initClasses();
		initObjectProperties();
		// init rulesReasoning and add standard rules
		// rulesReasoner = initRulesReasoning();

		logger.info("Base Ontology created");
		logger.setLevel(Level.DEBUG);
		m.write(logStream);

		// TODO create Restrictions

		return getM();
	}

	private void initObjectProperties() {
		getUtil().createObjectProperty(CompOntClass.Learner,
				CompOntClass.Competence, CompObjectProperties.LearnerOf);
		getUtil().createObjectProperty(CompOntClass.Competence,
				CompOntClass.Learner, CompObjectProperties.LearnerOfInverse);
		getUtil().createObjectProperty(CompOntClass.Catchword,
				CompOntClass.Competence, CompObjectProperties.CatchwordOf);
		getUtil()
				.createObjectProperty(CompOntClass.Competence,
						CompOntClass.Catchword,
						CompObjectProperties.CatchwordOfInverse);
		getUtil().createObjectProperty(CompOntClass.Evidence,
				CompOntClass.Competence, CompObjectProperties.EvidencOf);
		getUtil().createObjectProperty(CompOntClass.Competence,
				CompOntClass.Evidence, CompObjectProperties.EvidencOfInverse);
		getUtil().createObjectProperty(CompOntClass.Operator,
				CompOntClass.Competence, CompObjectProperties.OperatorOf);
		getUtil()
				.createObjectProperty(CompOntClass.Operator,
						CompOntClass.Competence,
						CompObjectProperties.OperatorOfInverse);
		getUtil().createObjectProperty(CompOntClass.DescriptionElement,
				CompOntClass.CompetenceDescription,
				CompObjectProperties.DescriptionElementOf);
		getUtil().createObjectProperty(CompOntClass.CompetenceDescription,
				CompOntClass.DescriptionElement,
				CompObjectProperties.DescriptionElementOfInverse);
		getUtil().createObjectProperty(CompOntClass.CompetenceDescription,
				CompOntClass.Competence,
				CompObjectProperties.CompetenceDescriptionOf);
		getUtil().createObjectProperty(CompOntClass.Competence,
				CompOntClass.CompetenceDescription,
				CompObjectProperties.CompetenceDescriptionOfInverse);
		getUtil().createObjectProperty(CompOntClass.Competence,
				CompOntClass.CompetenceSpec, CompObjectProperties.SpecifiedBy);
		getUtil().createObjectProperty(CompOntClass.CompetenceSpec,
				CompOntClass.Competence,
				CompObjectProperties.SpecifiedByInverse);
		getUtil().createObjectProperty(CompOntClass.Competence,
				CompOntClass.Competence, CompObjectProperties.SimilarTo);
		getM().getObjectProperty(
				MagicStrings.PREFIX + CompObjectProperties.SimilarTo)
				.addProperty(RDF.type, OWL2.ReflexiveProperty);
	}

	private void initClasses() {
		for (CompOntClass compOntClass : CompOntClass.values()) {
			getUtil().createOntClass(compOntClass);
		}
	}

	/**
	 * Also creates a database, if it does not exist already If there already
	 * exist one, Nullpointer is thrown
	 * 
	 * @return
	 */
	private void initializeOntologyModel() {
		Dataset dataset = TDBFactory.createDataset(MagicStrings.TDBLocation);
		Model tdb = dataset.getDefaultModel();
		setM(ModelFactory.createOntologyModel(
				OntModelSpec.OWL_MEM_MICRO_RULE_INF, tdb));

	}

	private SimpleRulesReasoner initRulesFactory(
			SimpleRulesReasoner rulesReasoner) {
		RuleFactory factory = new RuleFactory();
		for (String ruleString : factory.getRuleStringss()) {
			rulesReasoner.addRuleAsString(ruleString);
		}
		return rulesReasoner;
	}

	private void initializeOntologyModelInMemory() {
		setM(ModelFactory
				.createOntologyModel(OntModelSpec.OWL_MEM_MICRO_RULE_INF));

	}

	public CompOntologyAccess getUtil() {
		return util;
	}

	public OntModel getM() {
		return m;
	}

	public void setM(OntModel m) {
		this.m = m;
	}

	public SimpleRulesReasoner getRulesReasoner() {
		return rulesReasoner;
	}

	public CompetenceQueries getQueries() {
		return queries;
	}

	public void switchOffDebugg() {
		rulesReasoner.getReasoner().setParameter(
				ReasonerVocabulary.PROPtraceOn, false);
		SimpleRulesReasoner.logger.setLevel(Level.ERROR);
	}

	public void switchOnDebug() {
		rulesReasoner.getReasoner().setParameter(
				ReasonerVocabulary.PROPtraceOn, true);
		SimpleRulesReasoner.logger.setLevel(Level.DEBUG);
	}

}