package de.unipotsdam.ontologypersistence.owl.queries;

import java.util.concurrent.ConcurrentLinkedQueue;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;

import de.unipotsdam.ontologypersistence.owl.access.MagicStrings;
import de.unipotsdam.ontologypersistence.owl.ontology.OntObjectProperties;

public class OntologyQueries {

	private OntModel m;
	private String queryprefix;

	public OntologyQueries(OntModel m) {
		this.m = m;
		queryprefix = "PREFIX comp: <" + MagicStrings.PREFIX + ">\n" + "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
				+ "PREFIX owl:<http://www.w3.org/2002/07/owl#>\n" + "PREFIX dc:<http://purl.org/dc/elements/1.1/>\n";
	}

	/**
	 * 
	 * This only works if the ObjectProperty is registered within the Class
	 * specified by the String and the ObjectProperty is either point "Range" or
	 * "Domain" to the Class specified by the String
	 * 
	 * @param string
	 *            Classname
	 * @param compObjectProperties
	 *            ObjectPropertyClassifier
	 * @return relatedClasses
	 */
	public synchronized ConcurrentLinkedQueue<OntClass> getRelatedClassesForOntClass(String domainClass, final OntObjectProperties compObjectProperties) {
		ConcurrentLinkedQueue<OntClass> result = new ConcurrentLinkedQueue<OntClass>();

		getClassesForRange(domainClass, compObjectProperties, result);
		getClassesForDomain(domainClass, compObjectProperties, result);
		for (OntClass ontclass : result) {
			result.addAll(ontclass.listSubClasses().toSet());
		}

		result.remove(m.getOntClass("http://www.w3.org/2002/07/owl#Nothing"));

		return result;
	}

	private void getClassesForDomain(String domainClass, final OntObjectProperties compObjectProperties, ConcurrentLinkedQueue<OntClass> result) {
		// Create a new query for range
		String queryString = queryprefix + "SELECT ?class " + "WHERE {" + "comp:" + compObjectProperties.name() + " rdfs:range  comp:" + domainClass + ".\n" + "comp:" + compObjectProperties.name()
				+ " rdfs:domain ?class" + ".\n" + "FILTER (?class != owl:Thing)" + ".\n" + "FILTER (?class != rdfs:Resource)" + "\n" + "}";

		sparql(result, queryString, "?class", OntClass.class);
	}

	private void getClassesForRange(String domainClass, final OntObjectProperties compObjectProperties, ConcurrentLinkedQueue<OntClass> result) {
		// Create a new query
		String queryString = queryprefix + "SELECT ?class " + "WHERE {" + "comp:" + compObjectProperties.name() + " rdfs:range ?class" + ".\n" + "comp:" + compObjectProperties.name()
				+ " rdfs:domain comp:" + domainClass + ".\n" + "FILTER (?class != owl:Thing)" + ".\n" + "FILTER (?class != rdfs:Resource)" + "\n" + "}";

		sparql(result, queryString, "?class", OntClass.class);

	}

	/**
	 * Iterates over all the object properties of Type specified and then
	 * iterates over all the individuals over all the individuals linked to them
	 * as range or domain except the individuals of Type OntClass specified
	 * 
	 * @param compObjectProperties
	 * @param ontClass
	 * @return
	 */
	public ConcurrentLinkedQueue<Individual> getRelatedIndividuals(final OntObjectProperties compObjectProperties, String rangeIndividualName) {
		ConcurrentLinkedQueue<Individual> result = new ConcurrentLinkedQueue<Individual>();
		// rangeIndividualName = CompOntologyAccess.encode(rangeIndividualName);

		// Create a new query
		String queryString = queryprefix + "SELECT ?individual " + "WHERE {" + "?individual  comp:" + compObjectProperties.name() + " comp:" + rangeIndividualName + "}";

		sparql(result, queryString, "?individual", Individual.class);
		return result;
	}

	public ConcurrentLinkedQueue<Individual> getRelatedIndividualsDomainGiven(String domainIndividual, final OntObjectProperties compObjectProperties) {
		ConcurrentLinkedQueue<Individual> result = new ConcurrentLinkedQueue<Individual>();
		// domainIndividual = CompOntologyAccess.encode(domainIndividual);

		// Create a new query
		String queryString = queryprefix + "SELECT ?individual " + "WHERE {" + " comp:" + domainIndividual + " comp:" + compObjectProperties.name() + " ?individual" + "}";

		sparql(result, queryString, "?individual", Individual.class);
		return result;
	}

	/**
	 * Assumes, that the query only returns only a set of one type which is
	 * specified by resultClass
	 * 
	 * @param result
	 * @param queryString
	 * @param variable
	 * @param resultClass
	 */
	private synchronized <T extends RDFNode> void sparql(ConcurrentLinkedQueue<T> result, String queryString, String variable, Class<T> resultClass) {
		Query query = QueryFactory.create(queryString);

		// Execute the query and obtain results
		QueryExecution qe = QueryExecutionFactory.create(query, m);
		ResultSet results = qe.execSelect();
		for (; results.hasNext();) {
			QuerySolution soln = results.nextSolution();
			result.add(soln.getResource(variable).as((Class<T>) resultClass));
		}

		// Important - free up resources used running the query
		qe.close();
	}
}
