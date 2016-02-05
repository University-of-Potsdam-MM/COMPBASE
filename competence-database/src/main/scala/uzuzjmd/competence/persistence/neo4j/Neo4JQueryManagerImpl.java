package uzuzjmd.competence.persistence.neo4j;

import com.google.common.collect.Sets;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import uzuzjmd.competence.exceptions.DataFieldNotInitializedException;
import uzuzjmd.competence.persistence.dao.*;
import uzuzjmd.competence.persistence.ontology.Edge;
import uzuzjmd.competence.persistence.ontology.Label;
import uzuzjmd.competence.service.rest.dto.CompetenceTreeFilterData;

import java.util.*;

/**
 * Created by dehne on 04.12.2015.
 */
public class Neo4JQueryManagerImpl extends Neo4JQueryManager {

    public Neo4JQueryManagerImpl() {
    }

    public ArrayList<String> getLabelsForNode(String id) throws Exception {
        String query = "MATCH (e{id:'" + id + "'}) return labels(e)";
        ArrayList<ArrayList<String>> resultString = issueNeo4JRequestArrayListArrayList(query);
        if (resultString == null) {
            return new ArrayList<String>();
        }
        try {
            return resultString.iterator().next();
        } catch (NullPointerException e) {
            return null;
        }
    }

    /**
     * This is used for standard daos who have normal label
     *
     * @param id
     * @param labelName
     * @throws Exception
     */
    public void setLabelForNode(String id, String labelName) throws Exception {
        String query = "MATCH (e{id:'" + id + "'}) set e:" + labelName + " return e";
        ArrayList<String> resultString = issueSingleStatementRequest(new RequestableImpl<ArrayList<String>>(), query);
    }

    public ArrayList<HashMap<String, String>> createOrUpdateUniqueNode(HashMap<String, String> props) throws Exception {
        logger.debug("Entering createOrUpdateUniqueNode with props:" + implode(props));
        List<Neo4jQueryStatement> states = new ArrayList<>();
        states.add(new Neo4jQueryStatement());
        states.get(states.size() - 1).setQueryType(Neo4jQuery.queryType.MERGE);
        states.get(states.size() - 1).setVar("n");
        if (props.containsKey("clazz") && props.get("clazz") != null) {
            states.get(states.size() - 1).setGroup(props.get("clazz"));
        }
        /*else {
            states.get(states.size() - 1).setGroup("unknown");
        }*/
        if (props.containsKey("id")) {
            states.get(states.size() - 1).addArgument("id", props.get("id"));
        }
        for (Map.Entry<String, String> kvp :
                props.entrySet()) {
            if (kvp.getKey().contains("clazz") || kvp.getKey().contains("id")) {
                continue;
            } else {
                states.add(new Neo4jQueryStatement());
                states.get(states.size() - 1).setQueryType(Neo4jQuery.queryType.SET);
                states.get(states.size() - 1).setVar("n");
                states.get(states.size() - 1).addArgument(kvp.getKey(), kvp.getValue());
            }
        }
        states.add(new Neo4jQueryStatement());
        states.get(states.size() - 1).setQueryType(Neo4jQuery.queryType.RETURN);
        states.get(states.size() - 1).setVar("n");

        logger.debug("Leaving createOrUpdateUniqueNode");
        return issueSingleStatementRequest(new RequestableImpl<ArrayList<HashMap<String, String>>>()
                , Neo4jQuery.statesToQuery(states));
    }


    private static String implode(Map<String, String> map) {

        boolean first = true;
        StringBuilder sb = new StringBuilder();

        for (Map.Entry<String, String> e : map.entrySet()) {
            if (!first) sb.append(", ");
            sb.append(" " + e.getKey() + " : '" + e.getValue() + "' ");
            first = false;
        }

        return sb.toString();
    }


    public void createRelationShip(String domainId, Edge edge, String rangeId) throws Exception {
        String query = "MATCH (n {id:'" + domainId + "'}), (n2{id:'" + rangeId + "'}) CREATE UNIQUE (n)-[r:" + edge.toString() + "]->(n2) return n,r,n2";
        issueNeo4JRequestStrings(query);
    }



    public void deleteNode(String id) throws Exception {
        String query = "MATCH (n {id:'" + id + "'}) DETACH DELETE n";
        issueNeo4JRequestStrings(query);
    }

    /**
     * delete Relationship between domainID and RangeID
     *
     * @param domainId
     * @param rangeId
     * @param edge
     */
    public void deleteRelationShip(String domainId, String rangeId, Edge edge) throws Exception {
        String query = "MATCH (a{id:'" + domainId + "'})-[r:" + edge.toString() + "]->(b{id:'" + rangeId + "'}) DELETE r";
        issueNeo4JRequestStrings(query);
    }


    /**
     * @param domainId
     * @param rangeId
     * @param edge
     * @return
     */
    public Boolean existsRelationShip(String domainId, String rangeId, Edge edge) throws Exception {
        String query = "MATCH (a{id:'" + domainId + "'})-[r:" + edge.toString() + "]->(b{id:'" + rangeId + "'}) return r";
        return existMatches(query);
    }

    private Boolean existMatches(String query) throws Exception {
        ArrayList<String> result = issueNeo4JRequestStrings(query);
        if (result == null) {
            return false;
        }
        return !result.isEmpty();
    }


    /**
     * checks if relationship exists but a singleton classNode is given instead of the individual
     *
     * @param domainClassNodeId
     * @param rangeId
     * @param edge
     */
    public Boolean existsRelationShipWithSuperClassGiven(String domainClassNodeId, String rangeId, Edge edge) throws Exception {
        String query = "MATCH (a)-[r:individualOf]->(b{id:'" + domainClassNodeId + "'}), (a)-[r2:" + edge.toString() + "]->(c{id:'" + rangeId + "'}) return a";
        return existMatches(query);
    }



    public List<String> getAssociatedNodeIdsAsRange(Edge edge, String rangeIndividualId) throws Exception {
        String query2 = "MATCH (b)-[r:" + edge.toString() + "]->(a {id:'" + rangeIndividualId + "'}) RETURN b.id";
        return issueNeo4JRequestStrings(query2);
    }


    public List<String> getAssociatedNodeIdsAsDomain(String domainIndividual, Edge edge) throws Exception {
        String query2 = "MATCH (a {id:'" + domainIndividual + "'})-[r:" + edge.toString() + "]->(b) RETURN b.id";
        return issueNeo4JRequestStrings(query2);
    }


    /**
     *
     * @param clazz
     * @return
     * @throws Exception
     */
    public List<String> getAllInstanceDefinitions(Label clazz) throws Exception {
        String query = "MATCH (a:" + clazz.name() + ") return a.id";
        ArrayList<String> result = issueNeo4JRequestStrings(query);
        return result;
    }

    /**
     *
     * @param start
     * @param end
     * @return
     * @throws Exception
     */
    public List<String> getShortestSubClassPath(String start, String end) throws Exception {
        String query = "MATCH p=(a{id:'" + start + "'})-[r:subClassOf*]->(b{id:'" + end + "'}) return EXTRACT (n IN nodes(p)|n.definition) AS ids";
        return issueNeo4JRequestStrings(query);
    }

    /**
     *
     * @param start
     * @param end
     * @param clazz
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T extends Dao>  List<T> getShortestSubClassPath(String start, String end, Class<T> clazz) throws Exception {
        String query = "MATCH p=(a{id:'" + start + "'})-[r:subClassOf*]->(b{id:'" + end + "'}) return EXTRACT n IN nodes(p)";
        List<T> resultDaos = new ArrayList<T>();
        ArrayList<HashMap<String,String>> result = issueNeo4JRequestArrayOfHashMap(query);
        for (HashMap<String, String> stringStringHashMap : result) {
            resultDaos.add((T) clazz.newInstance().getFullDao(stringStringHashMap));
        }
        return resultDaos;
    }


      /**
     * removes a propety in a node
     *
     * @param id
     * @param prop
     * @throws Exception
     */
    public void removePropertyInNode(String id, String prop) throws Exception {
        String query = "MATCH (n { id: '" + id + "' }) REMOVE n." + prop + "  RETURN n";
        issueNeo4JRequestStrings(query);
    }

    /**
     * sets a propety in a node
     *
     * @param id
     * @param prop
     * @param value
     * @throws Exception
     */
    public void setPropertyInNode(String id, String prop, Object value) throws Exception {
        String query = "MATCH (n { id: '" + id + "' }) SET n." + prop + " = '" + value + "'  RETURN n";
        issueNeo4JRequestStrings(query);
    }

    public String getPropertyInNode(String id, String prop) throws Exception {
        String query = "MATCH (n { id: '" + id + "' })  RETURN n." + prop;
        if (issueNeo4JRequestStrings(query).isEmpty()) {
            throw new DataFieldNotInitializedException();
        }
        return issueNeo4JRequestStrings(query).get(0);
    }

    public Boolean exists(String id) throws Exception {
        String query = "MATCH (n{id:'" + id + "'}) return n";
        return !(issueNeo4JRequestStrings(query) == null || issueNeo4JRequestStrings(query).isEmpty());
    }

    public SelfAssessment getSelfAssessment(Competence competence, User user) throws Exception {
        String query = "MATCH (c:User{id:'"+user.getId()+"'}) MATCH (b:SelfAssessment)-[r1:AssessmentOfCompetence]->(a:Competence{id:'"+competence.getId()+"'}) MATCH (b)-[r2:AssessmentOfUser]->(c:User) return b";
        ArrayList<HashMap<String, String>> result = issueNeo4JRequestHashMap(query);
        if (result == null) {
            return new SelfAssessment(competence, user, 0, false);
        }
        HashMap<String, String> result2 = result.iterator().next();
        if (result2 == null) {
            return null;
        } else {
           return new SelfAssessment(result2.get("id")).getFullDao(result2);
        }
    }


    /**
     * returns a list in the form
     * 1 -> 2
     * 1 -> 3
     * 3 -> 4
     * 1 -> 3
     * []
     * []
     * @param label
     * @return
     */
    public List<ArrayList<String>> getSubClassTriples(String label, CompetenceTreeFilterData filterData) throws Exception {
        String courseId = filterData.getCourse();
        List<String> operators = filterData.getSelectedOperatorsArray();
        List<String> catchwords = filterData.getSelectedCatchwordArray();
        String futherMatches = "";
        for (String catchword : catchwords) {
            futherMatches +="MATCH (c:Catchword{id:'"+catchword+"'})-[r33:CatchwordOf]->(p)";
        }
        for (String operator : operators) {
            futherMatches +="MATCH (c:Operator{id:'"+operator+"'})-[r44:OperatorOf]->(p)";
        }

        String query = "MATCH tree = (p:"+label+")-[:subClassOf*1..5]->(c:"+label+")"+futherMatches+"MATCH (x:CourseContext{id:'"+courseId+"'})-[r33:CourseContextOf]->(p) return extract(n IN filter(x in nodes(tree) WHERE length(nodes(tree)) = 2)|n.id) ORDER BY length(tree) ";
        return issueNeo4JRequestArrayListArrayList(query);
    }

    public HashSet<Competence> getSuggestedCompetenceRequirements(String competenceId, Class<Competence> competenceClass, LearningProjectTemplate learningProject) throws Exception {
        String query = "MATCH (b:LearningProjectTemplate{id:'"+learningProject.getId()+"'})-[r1:LearningProjectTemplateOf]->(c) MATCH (c:Competence)-[r2:SuggestedCompetencePrerequisiteOf]->(a:Competence{id:'"+competenceId+"'}) return c.id";
        ArrayList<String> result = issueNeo4JRequestStrings(query);
        return getCompetencesFromResultSet(learningProject, result);
    }

    private HashSet<Competence> getCompetencesFromResultSet(LearningProjectTemplate learningProject, ArrayList<String> result) {
        if (result == null || result.isEmpty()) {
            return new HashSet<>();
        }
        HashSet<Competence> result2 = new HashSet<>();
        for (String s : result) {
            if (learningProject != null) {
                result2.add(new Competence(s, learningProject));
            } else {
                result2.add(new Competence(s));
            }
        }
        return result2;
    }

    public HashSet<Competence> getRecommendedCompetencesForUser(String user) throws Exception {
        String query1 = "MATCH (a:SelfAssessment)-[r1:AssessmentOfUser]->(n:User{id:"+user+"}) MATCH (a)-[r2:AssessmentOfCompetence]->(b) MATCH (b)-[r3:SuggestedCompetencePrerequisite]->(c) WHERE a.assessmentIndex = '3' OR a.assessmentIndex = '4' RETURN c.id LIMIT 25";
        String query2 = "MATCH (l:LearningProjectTemplate)-[r2:LearningProjectTemplateOf]->(a:Competence) MATCH (u:User{id:"+user+"})-[r3:UserOfLearningProjectTemplate]->(l) MATCH (s:SelfAssessment)-[r4:AssessmentOfCompetence]->(a) MATCH (s)-[r1:AssessmentOfUser]->(u) WHERE NOT(s.assessmentIndex='1') AND NOT(s.assessmentIndex='2')  return a.id";
        // add more queries here
        ArrayList<String> result = issueNeo4JRequestStrings(query1, query2);
        return getCompetencesFromResultSet(null, result);
    }
}
