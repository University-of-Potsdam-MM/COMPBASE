package uzuzjmd.competence.monopersistence.daos;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import uzuzjmd.competence.persistence.neo4j.Neo4JQueryManagerImpl;
import uzuzjmd.competence.persistence.ontology.CompObjectProperties;
import uzuzjmd.competence.persistence.ontology.CompOntClass;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by dehne on 11.01.2016.
 */
public abstract class DaoAbstractImpl implements Dao {

    private final String id;
    protected final Neo4JQueryManagerImpl queryManager = new Neo4JQueryManagerImpl();
    static Logger logger = LogManager.getLogger(DaoAbstractImpl.class.getName());


    public DaoAbstractImpl(String id) {
        this.id = id;
    }

    @Override
    public void setFullDao(HashMap<String, String> props) {
        logger.debug("Entering hashMapToIndividual with properties");
        String logMessage = "Created/Updated Individual {";
        for (String key :
                props.keySet()) {
            logMessage += key + ":" + props.get(key) + "; ";
            try {
                Field f = getClass().getDeclaredField(key);
                if (f.get(this).getClass().getName().equals(String.class.getName())) {
                    f.set(this, props.get(f.getName()));
                } else {
                    try {
                        f.set(this, convert(f.get(this).getClass(), props.get(key)));
                    } catch (IllegalAccessException e) {
                        logger.warn("Can't convert a field from database to Individual");
                        logger.warn("fieldClass: " + f.get(this).getClass().getName() + " Property:" + props.get(key));
                    }
                }
            } catch (NoSuchFieldException e) {
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        logger.info(logMessage + "}");
        logger.debug("Leaving hashMapToIndividual");
    }

    @Override
    public <T extends Dao> T getFullDao(HashMap<String, String> props) {
        setFullDao(props);
        return (T) this;
    }

    @Override
    public <T extends Dao> T getFullDao() throws Exception {
        return (T) queryManager.getDao(id, this.getClass());
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Boolean exists() throws Exception {
        return queryManager.exists(id);
    }

    @Override
    public CompOntClass getLabel() {
        return CompOntClass.valueOf(this.getClass().getSimpleName());
    }

    @Override
    public void createEdgeWith(CompObjectProperties edge, Dao range) throws Exception {
        if(!this.getId().equals(range.getId())) {
            queryManager.createRelationShip(this.getId(), edge, range.getId());
        }
    }

    @Override
    public void createEdgeWith(Dao domain, CompObjectProperties edge) throws Exception {
        if(!this.getId().equals(domain.getId())) {
            queryManager.createRelationShip(domain.getId(), edge, this.getId());
        }
    }

    @Override
    public void deleteEdgeWith(Dao otherNode, CompObjectProperties edge) throws Exception {
        queryManager.deleteRelationShip(otherNode.getId(), this.getId(), edge);
        queryManager.deleteRelationShip(this.getId(), otherNode.getId(), edge);
    }

    @Override
    public Boolean hasEdge(Dao domain, CompObjectProperties edge) throws Exception {
        return queryManager.existsRelationShip(domain.getId(), this.getId(), edge);
    }

    @Override
    public Boolean hasEdge(CompObjectProperties edge, Dao range) throws Exception {
        return queryManager.existsRelationShip(this.getId(), range.getId(), edge);
    }

    @Override
    public void persist() throws Exception {
        HashMap<String, String> propHashMap = this.toHashMap();
        propHashMap.put("id", getId());
        propHashMap.put("clazz", getLabel().toString());
        queryManager.createOrUpdateUniqueNode(propHashMap);
    }

    @Override
    public void delete() throws Exception {
        queryManager.deleteNode(getId());
    }

    @Override
    public <T extends Dao> List<T> getAssociatedDaosAsDomain(CompObjectProperties edge, Class<T> clazz) throws Exception {
        List<String> nodeIds = queryManager.getAssociatedNodeIdsAsDomain(getId(), edge);
        return instantiateDaos(clazz, nodeIds);
    }

    @Override
    public <T extends Dao> List<T> getAssociatedDaosAsRange(CompObjectProperties edge, Class<T> clazz) throws Exception {
        List<String> nodeIds = queryManager.getAssociatedNodeIdsAsRange(edge, getId());
        return instantiateDaos(clazz, nodeIds);
    }

    private <T extends Dao> List<T> instantiateDaos(Class<T> clazz, List<String> nodeIds) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        if (nodeIds == null || nodeIds.isEmpty()) {
            return new LinkedList<>();
        }

        List<T> resultList = new ArrayList<T>();
        for (String nodeId : nodeIds) {
            T result = clazz.getDeclaredConstructor(String.class).newInstance(nodeId);
            resultList.add(result);
        }
        return resultList;
    }

    @Override
    public List<String> getAssociatedDaoIdsAsDomain(CompObjectProperties edge) throws Exception {
        return  queryManager.getAssociatedNodeIdsAsDomain(getId(), edge);
    }

    @Override
    public List<String> getAssociatedDaoIdsAsRange(CompObjectProperties edge) throws Exception {
        return queryManager.getAssociatedNodeIdsAsRange(edge, getId());
    }

    protected HashMap<String, String> toHashMap() {
        logger.debug("Entering toHashMap");
        HashMap<String, String> result = new HashMap<String, String>();
        String logMes = "{";
        for (Field prop :
                getClass().getDeclaredFields()) {
            logMes += prop.getName() + ":";
            try {
                if (!(prop.get(this) == null)) {
                    if (!((prop.get(this).getClass().getName().contains("Neo4J")) || (prop.get(this).getClass().getName().contains("Logger")))) {
                        result.put(prop.getName(), prop.get(this).toString());
                        logMes += prop.get(this).toString() + ";";
                    }
                }
            } catch (IllegalAccessException e) {
                logger.error(e.getMessage());
                e.printStackTrace();
            }
        }
        logger.debug("Leaving toHashMap with HashMap:" + logMes + "}");
        return result;
    }


    static <T> T convert(Class<T> klazz, String arg) {
        logger.debug("Entering static convert with klazz:" + klazz.getName() + " arg:" + arg);
        Exception cause = null;
        T ret = null;
        try {
            ret = klazz.cast(
                    klazz.getDeclaredMethod("valueOf", String.class)
                            .invoke(null, arg)
            );
        } catch (NoSuchMethodException e) {
            cause = e;
        } catch (IllegalAccessException e) {
            cause = e;
        } catch (InvocationTargetException e) {
            cause = e;
        }
        if (cause == null) {
            return ret;
        } else {
            logger.error(cause.getMessage());
            throw new IllegalArgumentException(cause);
        }
    }

    public <T extends Dao> List<T> listSuperClasses(Class<T> competenceClass) throws Exception {
        return queryManager.listSuperClasses(competenceClass, this.getId());
    }

    public <T extends Dao> List<T> listSubClasses(Class<T> competenceClass) throws Exception {
        return queryManager.listSubClasses(competenceClass, this.getId());
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Dao) {
            ((Dao) o).getId().equals(this.getId());
        }
        return false;
    }

    @Override
    public <P extends Dao> P getAssociatedDaoAsDomain(CompObjectProperties edge, Class<P> clazz) throws Exception {
        List<P> result = getAssociatedDaosAsDomain(edge, clazz);
        if (result.isEmpty()) {
            throw new Exception("Did not find any associated node for: " + edge.name() + " and id: "+this.getId());
        }
        return result.iterator().next();
    }

    @Override
    public <T extends Dao> T getAssociatedDaoAsRange(CompObjectProperties edge, Class<T> clazz) throws Exception {
        List<T> result = getAssociatedDaosAsRange(edge, clazz);
        if (result.isEmpty()) {
            throw new Exception("Did not find any associated node for: " + edge.name() + " and id: "+this.getId());
        }
        return result.iterator().next();
    }


}