package uzuzjmd.competence.main;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import uzuzjmd.competence.config.MagicStrings;
import uzuzjmd.competence.evidence.service.rest.EvidenceServiceRestServerImpl;
import uzuzjmd.competence.service.rest.CompetenceServiceImpl;
import uzuzjmd.competence.util.CrossOriginResourceSharingFilter;

import javax.ws.rs.ProcessingException;
import java.io.IOException;
import java.net.BindException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * starts the rest server as grizzly standalone java program
 */
public class RestServer {

    static Logger logger = LogManager
            .getLogger(RestServer.class.getName());

    public static void main(String[] args)
            throws IllegalArgumentException,
            NullPointerException, IOException,
            ProcessingException, URISyntaxException {
        startServer();
    }

    public static void startServer() throws IOException,
            ProcessingException, URISyntaxException, BindException {
        logger.debug("Entering startServer");
        System.out
                .println("usage is java - jar *.version.jar");
        System.out
                .println("plz configure evidenceserver.properties");

        ResourceConfig resourceConfig = new ResourceConfig(
                CompetenceServiceImpl.class,
                EvidenceServiceRestServerImpl.class);
        resourceConfig.register(JacksonFeature.class);

        // add CORS header filter
        resourceConfig.register(CrossOriginResourceSharingFilter.class);



        final HttpServer server = GrizzlyHttpServerFactory.createHttpServer(new URI(
                        MagicStrings.RESTURLCompetence),
                resourceConfig);

        // register shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                logger.info("Stopping server..");
                server.shutdownNow();
                ;
            }
        }, "shutdownHook"));

        // run
        try {
            server.start();
            System.out
                    .println("publishing competence server to to "
                            + MagicStrings.RESTURLCompetence);
            System.out
                    .println("Test this with: "
                            + MagicStrings.RESTURLCompetence
                            + "/competences/competencetree/university");
            logger.info("Press CTRL^C to exit..");
            Thread.currentThread().join();
        } catch (Exception e) {
            logger.error(
                    "There was an error while starting Grizzly HTTP server.", e);
        }
        logger.info("Published HTTP Server to "
                + MagicStrings.RESTURLCompetence);
        Logger logJena = Logger
                .getLogger("com.hp.hpl.jena");
        logJena.setLevel(Level.WARN);
        logger.debug("Leaving startServer");

    }

}
