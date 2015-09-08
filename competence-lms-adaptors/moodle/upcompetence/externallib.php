<?php

// Moodle is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// Moodle is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with Moodle.  If not, see <http://www.gnu.org/licenses/>.

/**
 * External Web Service Template
 *
 * @package    localwstemplate
 * @copyright  2011 Moodle Pty Ltd (http://moodle.com)
 * @license    http://www.gnu.org/copyleft/gpl.html GNU GPL v3 or later
 */
require_once($CFG->libdir . "/externallib.php");
require_once($CFG->dirroot . '/config.php');
require_once($CFG->libdir . "/accesslib.php");

class local_competence_external extends external_api {#


    /**
     * get courses for a given user-email
     *
     * @return array Array of course objects
     * @since Moodle 2.5
     */

    public static function get_courses_for_user($useremail) {

        global $DB, $CFG, $USER;

        if ($USER->email == $useremail) {

            $query = 'SELECT c.id, c.fullname FROM {user} u INNER JOIN {user_enrolments} ue ON ue.userid = u.id INNER JOIN {enrol} e ON e.id = ue.enrolid INNER JOIN {course} c ON e.courseid = c.id WHERE u.email = ?';
            $result = $DB->get_records_sql($query, array($useremail));
            $mapper = function ($arrayElement) {
                return array('courseid' => $arrayElement->id, 'name' => $arrayElement->fullname);
            };
            $result_mapped = array_map($mapper, $result);
            return $result_mapped;
        } else {
            return array();
        }
    }

    /**
     * Returns description of method parameters
     *
     * @return external_function_parameters
     * @since Moodle 2.5
     */
    public static function get_evidences_for_course_parameters() {
        return new external_function_parameters(
                array(
            'courseId' => new external_value(PARAM_INT, 'id of course'),
                )
        );
    }

    /**
     * 
     * @return \external_multiple_structure
     */
    public static function get_evidences_for_course_returns() {
        return new external_multiple_structure(
                new external_single_structure(
                array(
            'shortname' => new external_value(PARAM_TEXT, 'multilang compatible name, course unique'),
            'url' => new external_value(PARAM_TEXT, 'multilang compatible name, course unique'),
            "username" => new external_value(PARAM_TEXT, 'multilang compatible name, course unique'),
            "userId" => new external_value(PARAM_TEXT, 'multilang compatible name, course unique'),
            'changed' => new external_value(PARAM_TEXT, 'multilang compatible name, course unique'),
            'course' => new external_value(PARAM_TEXT, 'multilang compatible name, course unique'),
            'activityTyp' => new external_value(PARAM_TEXT, 'multilang compatible name, course unique'),
            'email' => new external_value(PARAM_TEXT, 'multilang compatible name, course unique')
                )
                )
        );
    }

    /**
     * returns a list of activities for a user that can be interpreted as evidences
     * 
     * The complete list of course activities can only be loaded if you are a teacher!
     *
     * @return array Array of course objects
     * @since Moodle 2.5
     */
    public static function get_evidences_for_course($courseId) {
        global $DB, $CFG, $USER;


        $userId = $USER->id;
        $roles = get_user_roles_in_course($userId, $courseId);

        if (strpos($roles, 'Teacher') !== FALSE) {

            $query = 'SELECT {log}.*,firstname,lastname,email,lastaccess FROM {log} , {user} INNER JOIN {role_assignments} ra ON ra.userid = {user}.id INNER JOIN {context} ct ON ct.id = ra.contextid INNER JOIN {course} c ON c.id = ct.instanceid INNER JOIN {role} r ON r.id = ra.roleid INNER JOIN {course_categories} cc ON cc.id = c.category WHERE {log}.userid = {user}.id AND {log}.course= ? AND r.id =5 ORDER BY time DESC';
            $result = $DB->get_records_sql($query, array($courseId));
            $mapper = function ($arrayElement) {
                $actual_link = 'http://localhost/moodle'; // to be changed  
                return array(
                    'shortname' => $arrayElement->module . $arrayElement->info . " am " . $arrayElement->lastaccess,
                    'url' => $actual_link . "/mod/" . $arrayElement->module . "/" . $arrayElement->url,
                    'username' => $arrayElement->firstname . " " . $arrayElement->lastname,
                    "userId" => $arrayElement->userid,
                    'changed' => $arrayElement->lastaccess,
                    'course' => $arrayElement->course,
                    'activityTyp' => $arrayElement->module,
                    'email' => $arrayElement->email
                );
                //TODO finish
            };
            $result_mapped = array_map($mapper, $result);
            return $result_mapped;
        } else {
            return array();
        }
    }

    /**
     * Returns description of method parameters
     *
     * @return external_function_parameters
     * @since Moodle 2.5
     */
    public static function get_courses_for_user_parameters() {
        return new external_function_parameters(
                array(
            'user' => new external_value(PARAM_TEXT, 'multilang compatible name'),
            'password' => new external_value(PARAM_TEXT, 'multilang compatible name'),
                )
        );
    }

    /**
     * 
     * @return \external_multiple_structure
     */
    public static function get_courses_for_user_returns() {
        return new external_multiple_structure(
                new external_single_structure(
                array(
            'courseid' => new external_value(PARAM_INT, 'id of course'),
            'name' => new external_value(PARAM_TEXT, 'multilang compatible name, course unique'),
                )
                )
        );
    }

    public static function user_exists_parameters() {
        return new external_function_parameters(
                array(
            'user' => new external_value(PARAM_TEXT, 'multilang compatible email'),
                )
        );
    }

    public static function user_exists_returns() {
        return new external_value(PARAM_BOOL, 'flag if user exists');
    }

    public static function user_exists($useremail) {
        global $DB, $USER;

        if ($USER->email == $useremail) {
            return true;
        }
        return false;
    }

}
