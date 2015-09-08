package uzuzjmd.competence.evidence.service;

import java.util.List;

import javax.ws.rs.core.Response;

import uzuzjmd.competence.shared.dto.UserCourseListResponse;
import uzuzjmd.competence.shared.dto.UserTree;

public interface EvidenceService {

	/**
	 * 
	 * Gets the (possible) activities of the user in the given System.
	 * 
	 * Get all the activities of this user in given the context (may be course
	 * id or group id)
	 * 
	 * It produces a xml structure which shows which user could do which
	 * activities.
	 * 
	 * (Mögliche) Aktivitäten der User in dem Kontext
	 * 
	 * 
	 * Liefert eine XML-Baum zurück, der zeigt, welcher User in dem Context
	 * welche Aktivität durchführen kann.
	 * 
	 * @param course
	 * @param lmssystem
	 * @param organization
	 * @return
	 */

	public abstract UserTree[] getUserTree(String course, String lmssystem, String organization);

	/**
	 * Add the activites users can do (from end application)
	 * 
	 * The activities of the user are persisted inmemory for UI interfaces to
	 * link them to competences.
	 * 
	 * Die Aktivitäten die ein User in dem gegebenen Kontext machen kann (von
	 * Schnittstellen aus ansprechbar).
	 * 
	 * Diese Aktivitäten werden aktuell Inmemory für eine Abfrage aus einer UI
	 * festgehalten.
	 * 
	 * 
	 * 
	 * 
	 * @param course
	 * @param usertree
	 * @param lmssystem
	 * @param organization
	 */
	public abstract void addUserTree(String course, List<UserTree> usertree, String lmssystem, String organization);

	/**
	 * 
	 * Gets the (possible) activities of the user in the given System.
	 * 
	 * Get all the activities of this user in given the context (may be course
	 * id or group id)
	 * 
	 * It produces a xml structure which shows which user could do which
	 * activities.
	 * 
	 * (Mögliche) Aktivitäten der User in dem Kontext
	 * 
	 * 
	 * Liefert eine XML-Baum zurück, der zeigt, welcher User in dem Context
	 * welche Aktivität durchführen kann.
	 * 
	 * @param course
	 * @param lmssystem
	 * @param organization
	 * @return
	 */

	public abstract Response getUserTreeCrossDomain(String course, String lmssystem, String organization);

	/**
	 * Get all the organizations the system is available for.
	 * 
	 * (university for default)
	 * 
	 * @return
	 */
	public abstract String[] getOrganizations();

	/**
	 * Liefert alle Systeme, an die die Datenbank angeschlossen ist
	 * 
	 * Get all the (LMS) systems the database is connected to
	 * 
	 * @return
	 */
	public abstract String[] getLMSSystems();

	/**
	 * Liefert Kurse (oder andere Kontexte)
	 * 
	 * Liefert alle Kurse und anderen Kontexte des Systems, bei denen der
	 * übergebene Nutzer beteiligt ist.
	 * 
	 * Get all the courses (or other contexts) the user is inscribed in the
	 * given context.
	 * 
	 * 
	 * @param user
	 * @param lmsSystem
	 * @param organization
	 * @return
	 */
	public abstract UserCourseListResponse getCourses(String user, String lmsSystem, String userPassword, String organization);

	/**
	 * Fügt Kurse zu der Datenbank hinzu, damit UIs zwischen verschiedenen
	 * Systemen und anderen Kontexten unterschieden können.
	 * 
	 * Adds courses (or other contexts) for the given system to the database in
	 * order for UIs to distinguish links between systems and contexts.
	 * 
	 * @param user
	 * @param usertree
	 * @param lmssystem
	 * @param organization
	 */
	public abstract void addCourses(String user, UserCourseListResponse usertree, String lmssystem, String organization);

	/**
	 * 
	 * Checks if a user exists in the given system. Could be implemented by ldap
	 * or simple user password login.
	 * 
	 * 
	 * @param user
	 * @param password
	 * @param lmsSystem
	 * @param organization
	 * @return
	 */
	public abstract Boolean exists(String user, String password, String lmsSystem, String organization);

}
