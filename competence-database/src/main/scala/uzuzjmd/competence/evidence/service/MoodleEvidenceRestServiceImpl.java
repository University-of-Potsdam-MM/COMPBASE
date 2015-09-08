package uzuzjmd.competence.evidence.service;

import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.jws.WebService;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.NotImplementedException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import uzuzjmd.competence.evidence.model.LMSSystems;
import uzuzjmd.competence.evidence.service.moodle.MoodleContentResponseList;
import uzuzjmd.competence.evidence.service.moodle.MoodleEvidence;
import uzuzjmd.competence.evidence.service.moodle.SimpleMoodleService;
import uzuzjmd.competence.evidence.service.rest.mapper.Evidence2Tree;
import uzuzjmd.competence.shared.dto.UserCourseListResponse;
import uzuzjmd.competence.shared.dto.UserTree;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/*
 * durch einen Moodle-Webservice oder PHP-Implementation vielleicht eleganter
 * gelöst werden. Es wird vor allem die Kurs-Log-Tabelle von Moodle ausgelesen, um die letzten Aktivitäten zu bekommen
 * 
 * @author Julian Dehne
 * 
 */
@WebService(endpointInterface = "uzuzjmd.competence.evidence.service.EvidenceService")
public class MoodleEvidenceRestServiceImpl extends AbstractEvidenceService {

	public static LoadingCache<String, UserTree[]> cacheImpl;
	private String adminLogin;
	private String adminLoginPassword;
	Logger logger = LogManager.getLogger(MoodleEvidenceRestServiceImpl.class.getName());
	private Thread cacheThread;

	public MoodleEvidenceRestServiceImpl(String adminLogin, String adminLoginPassword) {
		this.adminLogin = adminLogin;
		this.adminLoginPassword = adminLoginPassword;
		initCache();
	}

	public UserTree[] getCachedUserTree(String course) {
		try {
			return cacheImpl.get(course);
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		throw new Error("Cache not working");
	}

	public MoodleContentResponseList getCourseContents(String courseId) {
		SimpleMoodleService moodleService = new SimpleMoodleService(adminLogin, adminLoginPassword);
		return moodleService.getMoodleContents(courseId);
	}

	@Override
	public UserTree[] getUserTree(String course, String lmsSystem, String organization) {
		// MoodleEvidence[] moodleEvidences =
		// this.getUserEvidencesforMoodleCourse(course);
		SimpleMoodleService simpleService = new SimpleMoodleService(adminLogin, adminLoginPassword);
		MoodleEvidence[] moodleEvidences = simpleService.getMoodleEvidenceList(course).toArray(new MoodleEvidence[0]);
		MoodleContentResponseList listMoodleContent = this.getCourseContents(course);

		Evidence2Tree mapper = new Evidence2Tree(listMoodleContent, moodleEvidences);
		UserTree[] result = mapper.getUserTrees().toArray(new UserTree[0]);
		return result;
	}

	private void initCache() {
		if (cacheImpl == null) {
			cacheImpl = CacheBuilder.newBuilder().maximumSize(1000).build(new CacheLoader<String, UserTree[]>() {
				public UserTree[] load(final String key) {
					return getUserTree(key, null, null);
				}
			});
		}
		if (cacheThread == null) {
			cacheThread = new Thread(new Runnable() {
				@Override
				public void run() {
					while (true) {
						for (String key : cacheImpl.asMap().keySet()) {
							cacheImpl.refresh(key);
						}
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			});
			cacheThread.start();
		}

	}

	@Override
	public Response getUserTreeCrossDomain(String course, String lmsSystem, String organization) {
		throw new Error("decorator called");
	}

	@Override
	public UserCourseListResponse getCourses(String user, String lmsSystem, String userPassword, String organization) {
		if (!LMSSystems.moodle.toString().equals(lmsSystem)) {
			return new UserCourseListResponse();
		}
		SimpleMoodleService simpleService = new SimpleMoodleService(adminLogin, adminLoginPassword);
		UserCourseListResponse result = simpleService.getMoodleCourseList(user, userPassword);
		return result;
	}

	@Override
	public Boolean exists(String username, String password, String lmsSystem, String organization) {
		SimpleMoodleService simpleService = new SimpleMoodleService(adminLogin, adminLoginPassword);
		return simpleService.isUserExist(username);
	}

	@Override
	public void addUserTree(String course, List<UserTree> usertree, String lmssystem, String organization) {
		throw new NotImplementedException();

	}

	@Override
	public void addCourses(String user, UserCourseListResponse usertree, String lmssystem, String organization) {
		throw new NotImplementedException();
	}

}
