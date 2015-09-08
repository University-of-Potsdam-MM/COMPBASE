package uzuzjmd.competence.evidence.service.rest;

import java.util.List;

import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import uzuzjmd.competence.evidence.service.EvidenceService;
import uzuzjmd.competence.evidence.service.EvidenceServiceProxy;
import uzuzjmd.competence.shared.dto.UserCourseListResponse;
import uzuzjmd.competence.shared.dto.UserTree;

/**
 * Implementiert den Moodle-RestService mit Grizzly. Dies ist die Hauptklasse
 * für die Rest-Services, die auf Moodle oder Liferay zugreifen
 * 
 * @author Julian Dehne
 * 
 */
@Path("/lms")
public class EvidenceServiceRestServerImpl implements EvidenceService {

	private EvidenceService evidenceService;

	public EvidenceServiceRestServerImpl() {
		evidenceService = new EvidenceServiceProxy();
	}

	@Override
	@Path("/activities/usertree/xml/{course}")
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public UserTree[] getUserTree(@PathParam("course") String course, @QueryParam("lmsSystem") String lmsSystem, @QueryParam("organization") String organization) {
		return evidenceService.getUserTree(course, lmsSystem, organization);
	}

	@POST
	@Path("/activities/usertree/json/add/{course}")
	@Override
	@Consumes(MediaType.APPLICATION_JSON)
	public void addUserTree(@PathParam("course") String course, @QueryParam("usertree") List<UserTree> usertree, @QueryParam("lmsSystem") String lmssystem,
			@QueryParam("organization") String organization) {
		evidenceService.addUserTree(course, usertree, lmssystem, organization);
	}

	@Path("/activities/usertree/xml/crossdomain/{course}")
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public Response getUserTreeCrossDomain(@PathParam("course") String course, @QueryParam("lmsSystem") String lmsSystem, @QueryParam("organization") String organization) {
		if (course == null || course.equals("0")) {
			return Response.status(404).build();
		}
		Response response = Response.status(200).entity(evidenceService.getUserTree(course, lmsSystem, organization)).build();
		// Response response =
		// Response.status(200).entity(moodleServiceImpl.getCachedUserTree(course)).build();
		return response;
	}

	@Path("/organizations")
	@GET
	@Produces(MediaType.APPLICATION_XML)
	@Override
	public String[] getOrganizations() {
		return evidenceService.getOrganizations();
	}

	@Path("/systems")
	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Override
	public String[] getLMSSystems() {
		return evidenceService.getLMSSystems();
	}

	@Path("/courses/{lmsSystem}/{userEmail}/{userPassword}")
	@Produces(MediaType.APPLICATION_JSON)
	@GET
	@Override
	public UserCourseListResponse getCourses(@PathParam("userEmail") String useremail, @PathParam("lmsSystem") String lmsSystem, @PathParam("userPassword") String userPassword,
			@QueryParam("organization") String organization) {
		UserCourseListResponse result = evidenceService.getCourses(useremail, lmsSystem, userPassword, organization);
		return result;
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/courses/add/{lmsSystem}/{userEmail}")
	@Override
	public void addCourses(@QueryParam("user") String user, @BeanParam UserCourseListResponse usertree, @QueryParam("lmsSystem") String lmssystem, @QueryParam("organization") String organization) {
		evidenceService.addCourses(user, usertree, lmssystem, organization);
	}

	@Path("/user/exists")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Override
	public Boolean exists(@QueryParam(value = "user") String user, @QueryParam(value = "password") String password, @QueryParam("lmsSystem") String lmsSystem,
			@QueryParam("organization") String organization) {
		Boolean result = evidenceService.exists(user, password, lmsSystem, organization);
		return result;
	}

}
