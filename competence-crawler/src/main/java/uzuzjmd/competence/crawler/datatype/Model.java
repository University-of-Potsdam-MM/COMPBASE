package uzuzjmd.competence.crawler.datatype;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import uzuzjmd.competence.crawler.exception.NoDomainFoundException;
import uzuzjmd.competence.crawler.exception.NoHochschuleException;
import uzuzjmd.competence.crawler.solr.SolrConnector;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by carl on 06.01.16.
 */
public class Model {
    static private final Logger logger = LogManager.getLogger(Model.class.getName());
    private StichwortVar stichwortVar;
    private VarMeta varMeta;
    private HashMap<String, SolrDocumentList> stichwortResult;
    private String delimiter = ";";
    private Urls urls;

   public Model() {
       stichwortVar = new StichwortVar();
       varMeta = new VarMeta();
       stichwortResult = new HashMap<>();
       urls = new Urls();
   }

    public void addDate(String stichwort, String variable, String[] metas){
        logger.debug("Entering addDate with stichwort " + stichwort + " variable " + variable
            + " metas " + Arrays.toString(metas));
        if (stichwort.length() > 0) {
            stichwortVar.addElement(stichwort, variable);
        }
        for (int i = 0; i < metas.length; i++) {
            varMeta.addElement(variable, metas[i]);
        }
        logger.debug("Leaving addDate");
    }

    public String[] toNeo4JQuery() {
        logger.debug("Entering toNeo4JQuery");

        String[] result = (String[]) ArrayUtils.addAll( ArrayUtils.addAll(new String[]{deleteModelInNeo4J()},  stichwortVar.toNeo4JQuery()), varMeta.toNeo4JQuery());
        logger.debug("Leaving toNeo4JQuery");
        return result;
    }

    public String[] toNeo4JQueryStichVar() {
        return stichwortVar.toNeo4JQuery();
    }
    public String[] toNeo4JQueryVarMeta() {
        return varMeta.toNeo4JQuery();
    }

    public String deleteModelInNeo4J() {
        return "MATCH (n:Stichwort),(m:Meta),(o:Variable) DETACH DELETE m,n,o";
    }

    public void insertSynonyms() {
        logger.debug("Entering insertSynonyms");
        stichwortVar.insertSynonym();
        logger.debug("Leaving insertSynonyms");
    }


    public void scoreStichwort(SolrConnector connector) throws IOException, SolrServerException {
        logger.debug("Entering scoreStichwort with SolrConnector:" + connector.getServerUrl());
        for (String key :
                stichwortVar.getElements().keySet()) {
            QueryResponse response = connector.connectToSolr(key);
            SolrDocumentList solrList = response.getResults();
            logger.debug("Key:" + key + " got " + solrList.getNumFound() + " Results");
            stichwortResult.put(key, solrList);
        }
        logger.debug("Leaving scoreStichwort");
    }

    public void scoreVariable(SolrConnector connector) throws IOException, SolrServerException {
        logger.debug("Entering scoreVariable with SolrConnector:" + connector.getServerUrl());
        HashMap<String, String> varStich = varMeta.toSolrQuery(stichwortVar);
        for (String key: varStich.keySet()) {
            QueryResponse response = connector.connectToSolr(varStich.get(key));
            SolrDocumentList solrList = response.getResults();
            logger.debug("Key:" + key + " got " + solrList.getNumFound() + " Results");
            varMeta.setSolrResult(solrList, key);
        }
        logger.debug("Leaving scoreVariable");
    }


    public void stichwortVarToCsv(String filepath) throws IOException {
        logger.debug("Entering stichwortVarToCsv with filepath:" + filepath);
        List<String> lines = new ArrayList<String>();
        lines.add("Stichwort" + delimiter + "Variable");
        for (String key: stichwortVar.getElements().keySet()) {
            lines.add(key + delimiter + stichwortVar.getElements().get(key));
        }
        Path file = Paths.get(filepath);
        Files.write(file, lines, Charset.forName("UTF-8"));

        logger.debug("Leaving stichwortVarToCsv");
    }

    public void stichwortResultToCsv(String filepath) throws IOException {
        logger.debug("Entering stichwortResultToCsv with filepath:" + filepath);
        Collection<String> lines = new Vector<>();
        lines.add("Stichwort" + delimiter + "URL" + delimiter + "SolrScore");
        for (String key: stichwortResult.keySet()) {
            int sizeOfStichwortResult = (int) stichwortResult.get(key).getNumFound();
            for (int i = 0; i < sizeOfStichwortResult && i<10000; i++) {
                try {
                    SolrDocument doc = stichwortResult.get(key).get(i);
                    lines.add(key + delimiter + doc.getFieldValue("id") + delimiter + doc.getFieldValue("score"));
                } catch (IndexOutOfBoundsException e) {
                     logger.error("more then 10000 elements found");
                }
            }
        }
        Path file = Paths.get(filepath);
        Files.write(file, lines, Charset.forName("UTF-8"));
        logger.debug("Leaving stichwortResultToCsv");
    }

    public void varMetaResultToCsv(String filepath) throws IOException, URISyntaxException {
        logger.debug("Entering varMetaResultToCsv with filepath:" + filepath);
        List<String> lines = new ArrayList<>();
        HashMap<String, String> varStich = stichwortVar.toVarStichwort();
        lines.add("Variable" + delimiter + "Metavariable" + delimiter + "Stichworte"
                + delimiter + "Hochschule" + delimiter
                + "Content" + delimiter + "SolrScore" + delimiter + "URL" + delimiter +
                "Depth" + delimiter + "Lat" + delimiter + "Lon");
        for (String key: varMeta.getElements().keySet()) {
            int sizeOfStichwortResult = (int) varMeta.getElements().get(key).documentList.getNumFound();
            for (int i = 0; i < sizeOfStichwortResult && i<10000; i++) {
                SolrDocument doc = varMeta.getElements().get(key).documentList.get(i);

                //Get Domain
                URI uri = new URI(((String) doc.getFieldValue("url")).replace(" ", ""));
                String domain = uri.getHost();
                String[] tempStrings = domain.split("\\.");
                if (tempStrings.length >= 0) {
                    domain = tempStrings[tempStrings.length - 2];
                } else {
                    logger.debug(uri.getHost() + " Has no dot");
                }

                urls.addDomain(domain, uri.getHost());

                //concatenate result
                try {
                    if (
                           // (domain.contains("qualitaetspakt-lehre")) ||
                            urls.isHochschule(domain) ||
                            Integer.parseInt((String) doc.getFieldValue("pageDepth")) <= 3) {
                        String hochschulname;
                        String latLon = "";
                        try {
                            UrlHochschule urlh = urls.getHochschule(domain);
                            hochschulname = urlh.Hochschulname;
                            latLon = urlh.lat + delimiter + urlh.lon;
                        } catch (NoDomainFoundException e) {
                            hochschulname = "";
                        } catch (NoHochschuleException e) {
                            hochschulname = domain;
                        }
                        lines.add(key + delimiter
                                + "\"" + StringUtils.join(varMeta.getElements().get(key).metaVar, ";") + "\"" + delimiter
                                + "\"" + varStich.get(key) + "\"" + delimiter
                                + "\"" + hochschulname + "\"" + delimiter
                                + "\"" + doc.getFieldValue("content").toString().replace("\"", "'") + "\"" + delimiter
                                + doc.getFieldValue("score") + delimiter
                                + doc.getFieldValue("url") + delimiter
                                + doc.getFieldValue("pageDepth") + delimiter
                                + latLon
                        );
                    }
                } catch (Exception e) {
                    logger.warn("Something went wrong " + domain + " pageDepth " + doc.getFieldValue("pageDepth") + " so far" );
                    logger.warn(e.getMessage());
                }
            }
        }

        //urls.validateDomains();

        Path file = Paths.get(filepath);
        Files.write(file, lines, Charset.forName("UTF-8"));

        logger.debug("Leaving varMetaResultToCsv");
    }

    public int stichwortVarSize() {
        return stichwortVar.getElements().size();
    }

    public int varMetaSize() {
        return varMeta.getElements().size();
    }
}
