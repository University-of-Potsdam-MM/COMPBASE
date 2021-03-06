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

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import uzuzjmd.competence.evidence.service.EvidenceService;
import uzuzjmd.competence.evidence.service.EvidenceServiceProxy;
import uzuzjmd.competence.shared.dto.UserCourseListResponse;
import uzuzjmd.competence.shared.dto.UserTree;

/**
 * implements the lms service that provides access to the lms activities
 * 
 * @author Julian Dehne
 * 
 */
@Path("/lms")
public class EvidenceServiceRestServerImpl implements
		EvidenceService {

	Logger logger = LogManager.getLogger(this.getClass());
	private EvidenceService evidenceService;

	public EvidenceServiceRestServerImpl() {
		evidenceService = new EvidenceServiceProxy();
	}

	@Override
	@Path("/activities/usertree/xml/{course}")
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public UserTree[] getUserTree(
			@PathParam("course") String course,
			@QueryParam("lmsSystem") String lmsSystem,
			@QueryParam("organization") String organization,
			@QueryParam("user") String user,
			@QueryParam("password") String password) {
		user = checkLoginisEmail(user);
		logger.debug("start query activities for user "
				+ user + " and course " + course);
		UserTree[] result = evidenceService.getUserTree(
				course, lmsSystem, organization, user,
				password);
		logger.debug("queried activities from the user: "
				+ result);
		return result;
	}

	@POST
	@Path("/activities/usertree/json/add/{course}")
	@Override
	@Consumes(MediaType.APPLICATION_JSON)
	public void addUserTree(
			@PathParam("course") String course,
			@QueryParam("usertree") List<UserTree> usertree,
			@QueryParam("lmsSystem") String lmssystem,
			@QueryParam("organization") String organization) {
		evidenceService.addUserTree(course, usertree,
				lmssystem, organization);
	}

	@Path("/activities/usertree/xml/crossdomain/{course}")
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public Response getUserTreeCrossDomain(
			@PathParam("course") String course,
			@QueryParam("lmsSystem") String lmsSystem,
			@QueryParam("organization") String organization,
			@QueryParam("user") String user,
			@QueryParam("password") String password) {
		if (course == null || course.equals("0")) {
			Response response = Response.status(200)
					.entity(new UserTree[0]).build();
			return response;
		}

		logger.debug("start query activities for user "
				+ user + " and course " + course);
		user = checkLoginisEmail(user);
		UserTree[] result = evidenceService.getUserTree(
				course, lmsSystem, organization, user,
				password);
		logger.debug("queried activities from the user: "
				+ result);
		Response response = Response.status(200)
				.entity(result).build();
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
	@Produces({ MediaType.APPLICATION_XML,
			MediaType.APPLICATION_JSON })
	@Override
	public String[] getLMSSystems() {
		return evidenceService.getLMSSystems();
	}

	@Path("/courses/{lmsSystem}/{userEmail}")
	@Produces(MediaType.APPLICATION_JSON)
	@GET
	@Override
	public UserCourseListResponse getCourses(
			@PathParam("lmsSystem") String lmsSystem,
			@QueryParam("organization") String organization,
			@PathParam("userEmail") String username,
			@QueryParam("password") String password) {
		username = checkLoginisEmail(username);
		UserCourseListResponse result = evidenceService
				.getCourses(lmsSystem, organization,
						username, password);
		return result;
	}

	private String checkLoginisEmail(String username) {
		if (username.contains("@")) {
			username = username.split("@")[0];
		}
		return username;
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/courses/add/{lmsSystem}/{userEmail}")
	@Override
	public void addCourses(@QueryParam("user") String user,
			@BeanParam UserCourseListResponse usertree,
			@QueryParam("lmsSystem") String lmssystem,
			@QueryParam("organization") String organization) {
		evidenceService.addCourses(user, usertree,
				lmssystem, organization);
	}

	@Path("/user/exists")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Override
	public Boolean exists(
			@QueryParam(value = "user") String user,
			@QueryParam(value = "password") String password,
			@QueryParam("lmsSystem") String lmsSystem,
			@QueryParam("organization") String organization) {
		user = checkLoginisEmail(user);
		Boolean result = evidenceService.exists(user,
				password, lmsSystem, organization);
		return result;
	}

}
