package uzuzjmd.competence.persistence.neo4j;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import uzuzjmd.competence.config.MagicStrings;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.LinkedHashMap;



/**
 * Created by carl on 16.12.15.
 */
public class RequestableImpl<T> implements Requestable<T>{

    final String txUri = MagicStrings.NEO4JURL + "/db/data/transaction/commit";
    static Logger logger = LogManager.getLogger(RequestableImpl.class.getName());

    @Override
    public T doRequest(String queryType) throws Exception {
        logger.debug("Entering doRequest with queryType:" + queryType);
         LinkedHashMap<String, ArrayList<LinkedHashMap<String, ArrayList<LinkedHashMap<String, T>>>>> result = getLinkedHashMapFromRest(queryType);

        if (!result.get("errors").isEmpty()) {
            logger.error(result.get("errors").get(0).toString());
            throw new Exception(result.get("errors").get(0).toString());
        }
        logger.debug("Leaving doRequest");
        if (result.get("results").get(0).get("data").isEmpty()) {
            return null;
        } else {
            return extractValues(result);
        }
    }

    protected T extractValues(LinkedHashMap<String, ArrayList<LinkedHashMap<String, ArrayList<LinkedHashMap<String, T>>>>> result) {
        ArrayList rows =  result.get("results").get(0).get("data");
        if (rows.size() == 1) {
            return (T) ((LinkedHashMap)rows.get(0)).get("row");
        }
        ArrayList<LinkedHashMap<String, T>> temp1 = result.get("results").get(0).get("data");
        ArrayList<T> myResult = new ArrayList<T>();
        for (LinkedHashMap<String, T> stringTLinkedHashMap : temp1) {
            LinkedHashMap<String, ArrayList<T>> temp2 = (LinkedHashMap<String, ArrayList<T>>)  stringTLinkedHashMap;
            myResult.add(temp2.get("row").get(0));
        }
        return (T) myResult;
    }

    protected LinkedHashMap<String, ArrayList<LinkedHashMap<String, ArrayList<LinkedHashMap<String, T>>>>> getLinkedHashMapFromRest(String payload) {
        Client client2 = ClientBuilder.newClient();
        WebTarget target2 = client2.target(txUri);
        return target2.request(
                MediaType.APPLICATION_JSON).post(
                Entity.entity(payload,
                        MediaType.APPLICATION_JSON), LinkedHashMap.class);
    }
}
