package de.unipotsdam.ontologypersistence.owl.access;

import java.io.IOException;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.tdb.TDB;
import com.hp.hpl.jena.tdb.TDBFactory;
import com.hp.hpl.jena.vocabulary.ReasonerVocabulary;

import de.unipotsdam.ontologypersistence.console.util.LogStream;
import de.unipotsdam.ontologypersistence.owl.ontology.OntObjectProperties;
import de.unipotsdam.ontologypersistence.owl.ontology.OntClasses;
import de.unipotsdam.ontologypersistence.owl.queries.OntologyQueries;
import de.unipotsdam.ontologypersistence.owl.reasoning.ModelChangeListener;
import de.unipotsdam.ontologypersistence.owl.reasoning.RuleFactory;
import de.unipotsdam.ontologypersistence.owl.reasoning.SimpleRulesReasoner;

public class OntologyManager {

	static final Logger logger = LogManager.getLogger(OntologyManager.class.getName());
	static LogStream logStream = new LogStream(logger, Level.TRACE);

	private OntologyAccess util;
	private OntModel m;
	private SimpleRulesReasoner rulesReasoner;
	private OntologyQueries queries;
	private ModelChangeListener modelChangedListener;
	private Dataset dataset;

	/**
	 * should be singleton
	 */
	public OntologyManager() {
		this.queries = new OntologyQueries(getM());
		this.util = new OntologyAccess(getM(), getQueries(), this);
	}

	public void startReasoning() {
		// init simple Rules Reasoner
		initReasoner();
		rulesReasoner.switchOnDebug();
		initRulesFactory(rulesReasoner);

		// apply rules whenever the model is changed
		this.modelChangedListener = new ModelChangeListener(rulesReasoner, this);

		// defaultmäßif ist derReasoner angeschaltet
		registerReasoner();
	}

	public Dataset getDataset() {
		return dataset;
	}

	public OntModel getM() {
		return this.m;
	}

	public void close() {
		dataset.commit();
		dataset.end();
	}

	public void unregisterReasoner() {
		m.unregister(modelChangedListener);
	}

	public void registerReasoner() {
		m.register(modelChangedListener);
	}

	private void initReasoner() {
		try {
			rulesReasoner = new SimpleRulesReasoner(this, false);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public OntModel createBaseOntology() {
		begin();
		// m = this.util.initializeOntologyModel();
		initClasses();
		initObjectProperties();
		// init rulesReasoning and add standard rules
		// rulesReasoner = initRulesReasoning();

		logger.info("Base Ontology created");
		logger.setLevel(Level.DEBUG);
		// m.write(logStream);

		// TODO create Restrictions

		close();
		return getM();
	}

	private void initObjectProperties() {
		getUtil().createObjectProperty(OntClasses.Learner, OntClasses.Competence, OntObjectProperties.LearnerOf);
		getUtil().createObjectProperty(OntClasses.Competence, OntClasses.Learner, OntObjectProperties.LearnerOfInverse);
		getUtil().createObjectProperty(OntClasses.Catchword, OntClasses.Competence, OntObjectProperties.CatchwordOf);
		getUtil().createObjectProperty(OntClasses.Competence, OntClasses.Catchword, OntObjectProperties.CatchwordOfInverse);
		getUtil().createObjectProperty(OntClasses.Evidence, OntClasses.Competence, OntObjectProperties.EvidencOf);
		getUtil().createObjectProperty(OntClasses.Competence, OntClasses.Evidence, OntObjectProperties.EvidencOfInverse);
		getUtil().createObjectProperty(OntClasses.Operator, OntClasses.Competence, OntObjectProperties.OperatorOf);
		getUtil().createObjectProperty(OntClasses.Operator, OntClasses.Competence, OntObjectProperties.OperatorOfInverse);
		getUtil().createObjectProperty(OntClasses.DescriptionElement, OntClasses.CompetenceDescription, OntObjectProperties.DescriptionElementOf);
		getUtil().createObjectProperty(OntClasses.CompetenceDescription, OntClasses.DescriptionElement, OntObjectProperties.DescriptionElementOfInverse);
		getUtil().createObjectProperty(OntClasses.CompetenceDescription, OntClasses.Competence, OntObjectProperties.CompetenceDescriptionOf);
		getUtil().createObjectProperty(OntClasses.Competence, OntClasses.CompetenceDescription, OntObjectProperties.CompetenceDescriptionOfInverse);
		getUtil().createObjectProperty(OntClasses.Competence, OntClasses.CompetenceSpec, OntObjectProperties.SpecifiedBy);
		getUtil().createObjectProperty(OntClasses.CompetenceSpec, OntClasses.Competence, OntObjectProperties.SpecifiedByInverse);
		getUtil().createObjectProperty(OntClasses.Competence, OntClasses.Competence, OntObjectProperties.SimilarTo);
		getUtil().createObjectProperty(OntClasses.CourseContext, OntClasses.Competence, OntObjectProperties.CourseContextOf);
		getUtil().createObjectProperty(OntClasses.CourseContext, OntClasses.Competence, OntObjectProperties.CompulsoryOf);
		getUtil().createObjectProperty(OntClasses.AbstractEvidenceLink, OntClasses.CourseContext, OntObjectProperties.LinkOfCourseContext);
		getUtil().createObjectProperty(OntClasses.EvidenceActivity, OntClasses.AbstractEvidenceLink, OntObjectProperties.ActivityOf);
		getUtil().createObjectProperty(OntClasses.User, OntClasses.AbstractEvidenceLink, OntObjectProperties.UserOfLink);
		getUtil().createObjectProperty(OntClasses.AbstractEvidenceLink, OntClasses.User, OntObjectProperties.createdBy);
		getUtil().createObjectProperty(OntClasses.User, OntClasses.Comment, OntObjectProperties.UserOfComment);
		getUtil().createObjectProperty(OntClasses.Comment, OntClasses.AbstractEvidenceLink, OntObjectProperties.CommentOf);
		getUtil().createObjectProperty(OntClasses.Role, OntClasses.User, OntObjectProperties.RoleOf);
		getUtil().createObjectProperty(OntClasses.Competence, OntClasses.Competence, OntObjectProperties.PrerequisiteOf);
		getUtil().createObjectProperty(OntClasses.Competence, OntClasses.Competence, OntObjectProperties.NotPrerequisiteOf);
		getUtil().createObjectProperty(OntClasses.User, OntClasses.Competence, OntObjectProperties.NotAllowedToView);

		// getM().getObjectProperty(
		// MagicStrings.PREFIX + CompObjectProperties.SimilarTo)
		// .addProperty(RDF.type, OWL2.ReflexiveProperty);
	}

	private void initClasses() {
		for (OntClasses compOntClass : OntClasses.values()) {
			getUtil().createOntClass(compOntClass);
		}
	}

	/**
	 * Also creates a database, if it does not exist already If there already
	 * exist one, Nullpointer is thrown
	 * 
	 * @return
	 */
	public void begin() {
		dataset = TDBFactory.createDataset(MagicStrings.TDBLocation);
		dataset.begin(ReadWrite.WRITE);
		Model tdb = dataset.getDefaultModel();
		setM(ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_MICRO_RULE_INF, tdb));

	}

	private SimpleRulesReasoner initRulesFactory(SimpleRulesReasoner rulesReasoner) {
		RuleFactory factory = new RuleFactory();
		for (String ruleString : factory.getRuleStringss()) {
			rulesReasoner.addRuleAsString(ruleString);
		}
		return rulesReasoner;
	}

	private void initializeOntologyModelInMemory() {
		setM(ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_MICRO_RULE_INF));

	}

	public OntologyAccess getUtil() {
		return util;
	}

	public void setM(OntModel m) {
		this.m = m;
	}

	public SimpleRulesReasoner getRulesReasoner() {
		return rulesReasoner;
	}

	public OntologyQueries getQueries() {
		return queries;
	}

	public void switchOffDebugg() {
		rulesReasoner.getReasoner().setParameter(ReasonerVocabulary.PROPtraceOn, false);
		SimpleRulesReasoner.logger.setLevel(Level.ERROR);
		SimpleRulesReasoner.logStream = new LogStream(logger, Level.ERROR);
	}

	public void sync() {
		TDB.sync(dataset);
	}

}
