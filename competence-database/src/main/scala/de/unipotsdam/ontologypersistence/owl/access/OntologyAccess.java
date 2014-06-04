package de.unipotsdam.ontologypersistence.owl.access;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.util.iterator.Filter;

import de.unipotsdam.ontologypersistence.owl.ontology.OntObjectProperties;
import de.unipotsdam.ontologypersistence.owl.ontology.OntClasses;
import de.unipotsdam.ontologypersistence.owl.queries.OntologyQueries;

public class OntologyAccess {

	/**
	 * init Logger
	 */
	static final Logger logger = LogManager.getLogger(OntologyAccess.class.getName());
	private FileUtil fileUtil;

	private OntologyManager manager;
	private OntologyQueries queries;

	/**
	 * The QueriesObject and Model are dependencies
	 * 
	 * @param m
	 * @param queries
	 * @param compOntologyManager
	 */
	public OntologyAccess(OntModel m, OntologyQueries queries, OntologyManager compOntologyManager) {
		this.queries = queries;
		this.fileUtil = new FileUtil(m);
		this.manager = compOntologyManager;
	}

	/**
	 * creates the individual, if not exists
	 * 
	 * @param classNameWithoutPrefix
	 * @param individualName
	 * @return
	 */
	public Individual createIndividualForString(OntClass ontClass, String individualName) {
		return manager.getM().createIndividual(encode(individualName), ontClass);
	}

	/**
	 * creates the individual, if not exists
	 * 
	 * @param paper
	 * @param individualName
	 * @return
	 */
	public Individual createIndividualForStringWithDefinition(OntClass ontClass, String individualName, String definition) {

		Individual individual = manager.getM().createIndividual(encode(individualName), ontClass);
		individual.addLiteral(manager.getM().createProperty("definition"), definition);
		return individual;
	}

	/**
	 * @param domain
	 * @param range
	 * @param propertyName
	 * @return
	 */
	public ObjectProperty createObjectProperty(OntClasses domain, OntClasses range, OntObjectProperties propertyName) {

		OntClass ontClass1 = getClass(domain);
		OntClass ontclass2 = getClass(range);
		return createObjectProperty(ontClass1, ontclass2, propertyName.name());
	}

	/**
	 * creates and object porperty or reuses one
	 * 
	 * @param m
	 * @param Domain
	 * @param Range
	 * @param propertyName
	 * @return
	 */
	private ObjectProperty createObjectProperty(OntClass domain, OntClass range, String propertyName) {

		ObjectProperty property = manager.getM().createObjectProperty(encode(propertyName));
		property.setDomain(domain);
		property.setRange(range);
		return property;
	}

	/**
	 * links the objectProperty to
	 * 
	 * @param individual
	 * @param individual2
	 * @param compObjectProperties
	 */
	public ObjectProperty createObjectPropertyWithIndividual(Individual domainIndividual, Individual rangeIndividual, OntObjectProperties compObjectProperties) {

		ObjectProperty result = getObjectPropertyForString(compObjectProperties.name());
		domainIndividual.addProperty(result.asObjectProperty(), rangeIndividual);
		return result;
	}

	/**
	 * delete the link
	 * 
	 * @param individual
	 * @param individual2
	 * @param compObjectProperties
	 */
	public ObjectProperty deleteObjectPropertyWithIndividual(Individual domainIndividual, Individual rangeIndividual, OntObjectProperties compObjectProperties) {
		ObjectProperty result = getObjectPropertyForString(compObjectProperties.name());
		domainIndividual.removeProperty(result.asObjectProperty(), rangeIndividual);
		return result;
	}

	/**
	 * checks if relationship exists
	 * 
	 * @param individual
	 * @param individual2
	 * @param compObjectProperties
	 */
	public Boolean existsObjectPropertyWithIndividual(Individual domainIndividual, final Individual rangeIndividual, OntObjectProperties compObjectProperties) {
		if ((domainIndividual == null) || (rangeIndividual == null) || (compObjectProperties == null)) {
			return false;
		}

		ObjectProperty result = getObjectPropertyForString(compObjectProperties.name());
		ExtendedIterator<Statement> linkedIndividuals = domainIndividual.listProperties(result.asProperty()).filterKeep(new Filter<Statement>() {
			@Override
			public boolean accept(Statement o) {
				return o.getResource().getLocalName().equals(rangeIndividual.asResource().getLocalName());
			}
		});
		Boolean exists = !linkedIndividuals.toSet().isEmpty();
		return exists;
	}

	/**
	 * creates class or returns class if exists
	 * 
	 * @param model
	 * @param ontClass
	 */
	public OntClass createOntClass(OntClasses ontClass) {
		return createOntClassForString(ontClass.name());
	}

	/**
	 * creates class or returns class if exists
	 * 
	 * @param model
	 * @param ontClass
	 */
	public OntClass createOntClassForString(String string, String... definitions) {
		if (string.equals("")) {
			return null;
		}
		OntClass paper = null;
		try {
			paper = manager.getM().createClass(encode(string));
		} catch (NullPointerException e) {
			System.out.println("und deine mudda");
		}
		if (definitions != null && definitions.length > 0) {
			paper.addLiteral(manager.getM().createProperty(encode("definition")), definitions[0]);
		}
		return paper;
	}

	/**
	 * convenienceMethod for adding the Prefix that specifies the individual for
	 * singleton classes
	 * 
	 * @param ontclass
	 * @return
	 */
	public Individual createSingleTonIndividual(OntClass ontclass) {
		String singletonstring = MagicStrings.SINGLETONPREFIX + ontclass.getURI().substring(MagicStrings.PREFIX.length(), ontclass.getURI().length());
		return createIndividualForString(ontclass, singletonstring);
	}

	// /**
	// * convenience method for accessing the underlying individual but only
	// * String given
	// *
	// * @param ontclass
	// * @return
	// */
	//
	// public Individual createSingleTonIndividual(String ontclass) {
	// return createIndividualForString(getOntClassForString(ontclass),
	// MagicStrings.SINGLETONPREFIX + ontclass);
	// }

	public OntClass createSingleTonIndividualWithClass(String classname, String... definitions) {
		OntClass classOnt = createOntClassForString(classname, definitions);
		createSingleTonIndividual(classOnt);
		return classOnt;
	}

	public Individual createSingleTonIndividualWithClass2(String classname, String... definitions) {
		OntClass classOnt = createOntClassForString(classname, definitions);
		return createSingleTonIndividual(classOnt);
	}

	public OntResult accessSingletonResource(String classname, String... definitions) {
		OntClass classOnt = createOntClassForString(classname, definitions);
		Individual individual = createSingleTonIndividual(classOnt);
		return new OntResult(individual, classOnt);
	}

	public OntResult accessSingletonResourceWithClass(OntClasses compOntClass) {
		OntClass classOnt = createOntClass(compOntClass);
		Individual individual = createSingleTonIndividual(classOnt);
		return new OntResult(individual, classOnt);
	}

	// /**
	// * creates/gets the corresponding ontclass for the given individual
	// * (assuming the ontclass is named as the individual without the
	// * singletonprefix) wirkt erstmal hacky
	// *
	// * @param individual
	// * @return
	// */
	// @Deprecated
	// public OntClass createOntClassForIndividual(Individual individual) {
	// String typeClass =
	// individual.getLocalName().substring(MagicStrings.SINGLETONPREFIX.length());
	// return createOntClassForString(typeClass);
	// }

	public OntClass getClass(OntClasses compOntClass) {
		return createOntClassForString(compOntClass.name());
	}

	// /**
	// * creates/gets the corresponding ontclass for the given individual
	// * (assuming the ontclass is named as the individual without the
	// * singletonprefix) wirkt erstmal hacky
	// *
	// * @param individual
	// * @return
	// */
	// @Deprecated
	// public OntClass createOntClassForIndividual(Individual individual) {
	// String typeClass =
	// individual.getLocalName().substring(MagicStrings.SINGLETONPREFIX.length());
	// return createOntClassForString(typeClass);
	// }

	public static String encode(String string) {
		if (string.startsWith(MagicStrings.PREFIX) || string.equals("")) {
			throw new Error("OntClass should not start with Prefix or empty string");
		}

		/**
		 * control character werden nicht akzeptiert und leerzeichen sind auch
		 * nicht gut
		 */
		//
		string = string.trim().replaceAll("[^a-zA-ZäöüÄÖÜß1-9]", "_").replaceAll("[\u0000-\u001f]", "").replaceAll("\\.", "__").replaceAll("[\n\r]", "").replaceAll("[\n]", "");
		return (MagicStrings.PREFIX + string).replaceAll("_", "");
	}

	public FileUtil getFileUtil() {
		return fileUtil;
	}

	/**
	 * returns null if individual does not exist
	 * 
	 * @param indivString
	 * @return
	 */
	public Individual getIndividualForString(String indivString) {
		return manager.getM().getIndividual(encode(indivString));
	}

	private ObjectProperty getObjectPropertyForString(String objectProperty) {
		return manager.getM().createObjectProperty(encode(objectProperty));
	}

	/**
	 * returns null if class does not exist
	 * 
	 * @param className
	 * @return
	 */
	public OntClass getOntClassForString(String className) {
		manager.sync();
		OntClass paper = manager.getM().getOntClass(encode(className));
		return paper;
	}

	/**
	 * In order to use the queries directly
	 * 
	 * @return
	 */
	public OntologyQueries getQueries() {
		return queries;
	}

}
