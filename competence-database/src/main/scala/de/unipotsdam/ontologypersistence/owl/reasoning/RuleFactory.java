package de.unipotsdam.ontologypersistence.owl.reasoning;

import java.util.HashSet;
import java.util.Set;

import de.unipotsdam.ontologypersistence.owl.ontology.OntObjectProperties;

public class RuleFactory {

	private HashSet<String> result;

	public RuleFactory() {
		result = new HashSet<String>();
		// generateInverseRules();
		// result.add(catchwordOfConclusions());
		// result.add(getCompulsoryInheritance());
		result.add(getPrerequisiteConclusion());
		result.add(getNotRequireTransition1());
		result.add(getNotRequireTransition2());
		result.add(getNotRequireTransition3());
		result.add(getNotAllowedCreationRule());
	}

	private void generateInverseRules() {
		for (OntObjectProperties prop : OntObjectProperties.values()) {
			if (!prop.name().endsWith("Inverse")) {
				result.add("[operatorInverse" + prop.hashCode() + ": (?a comp:" + prop.name() + " ?b) -> (?b comp:" + prop.name() + "Inverse ?a)]");
			} else if (prop.name().endsWith("Inverse")) {
				result.add("[operatorInverse" + prop.hashCode() + ": (?a comp:" + prop.name() + "Inverse ?b) -> (?b comp:" + prop.name() + " ?a)]");
			}
		}
	}

	public String getCompulsoryInheritance() {
		return "[compulsoryInheritance: (?a comp:CompulsoryOf ?b) (?b rdf:type ?d) notEqual(?d,comp:Competence) (?d rdfs:subClassOf comp:Competence) (?d rdfs:subClassOf ?e) (?f rdf:type ?e)  (?e rdfs:subClassOf comp:Competence) "
				+ "-> (?a comp:CompulsoryOf ?f)]";
	}

	public Set<String> getRuleStringss() {
		return result;
	}

	public String getOperatorInverse() {
		return "[operatorInverse: (?a comp:LearnerOf ?b) -> (?b comp:LearnerOfInverse ?a)]";
	}

	public String catchwordOfConclusions() {
		return "[catchwordTransition: (?a rdfs:subClassOf comp:Catchword)" + " (?a rdfs:subClassOf ?b) " + "-> (?b rdfs:subClassOf comp:Catchword)]";
	}

	public String getPrerequisiteConclusion() {
		return "[requireTransition: (?a comp:PrerequisiteOf ?b) (?b comp:PrerequisiteOf ?c) -> (?a comp:PrerequisiteOf ?c)]";
	}

	public String getNotRequireTransition1() {
		return "[notrequireTransition1: (?a comp:NotPrerequisiteOf ?b) (?b comp:PrerequisiteOf ?c) (?a comp:PrerequisiteOf ?c) -> remove(2)]";
	}

	public String getNotRequireTransition2() {
		return "[notrequireTransition1: (?a comp:PrerequisiteOf ?b) (?b comp:NotPrerequisiteOf ?c) (?a comp:PrerequisiteOf ?c) -> remove(0)]";
	}

	public String getNotRequireTransition3() {
		return "[notrequireTransition1: (?a comp:PrerequisiteOf ?b) (?b comp:PrerequisiteOf ?c) (?a comp:NotPrerequisiteOf ?c) -> remove(1)]";
	}

	public String getNotAllowedCreationRule() {
		return "[notallowedcreation: (?a comp:PrerequisiteOf ?b) (?user rdf:type comp:User) -> (?user comp:NotAllowedToView ?a)]";
	}

	public String getNotAllowedLinkRule() {
		return "[notallowedlink: (?user comp:NotAllowedToView ?a) (?a comp:PrerequisiteOf ?b) (?user rdf:type comp:User) (?user comp:UserOfLink ?a) -> remove(0)]";
	}
}

// create inverse relationships

// [operatorInverse2: (?a comp:CatchwordOf ?b) -> (?b comp:CatchwordOfInverse
// ?a)]

// tests
// [opinheritanceClassUP2: (?a rdfs:range comp:Competence) -> ]

// inheritance for classes
// [opinheritanceClassUP: (?a rdfs:range comp:Competence)
// (?a rdfs:domain comp:Learner)
// (?competence rdfs:subClassOf comp:Competence)
// (?learnerOf rdfs:subClassOf comp:Learner)
// ->
// (?a rdfs:range ?competence)
// (?a rdfs:domain ?learnerOf)]

// [opinheritanceClassDOWN: (?a comp:CatchwordOf ?b) -> (?b
// comp:CatchwordOfInverse ?a)]

// inheritance for indviduals
// [opinheritenceUp: (?a comp:LearnerOf ?competence) (?competence
// rdfs:subClassOf ?c) notEqual(?c, owl:Thing) notEqual(?c, rdfs:Resource)
// -> (?a comp:LearnerOf ?c)]
// [opinheritenceDOWN: (?a comp:LearnerOf ?competence) (?c rdfs:subClassOf
// ?competence) -> (?a comp:LearnerOf ?c)]