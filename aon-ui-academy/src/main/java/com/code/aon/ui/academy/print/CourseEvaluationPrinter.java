package com.code.aon.ui.academy.print;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.code.aon.academy.Course;
import com.code.aon.academy.CourseEvaluation;
import com.code.aon.academy.CourseObservation;
import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.academy.controller.CourseController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;

public class CourseEvaluationPrinter implements ICollectionProvider{

	private static final Logger LOGGER = Logger.getLogger(CourseEvaluationPrinter.class.getName());
	
	private static final String COURSE_CONTROLLER_NAME = "course";
	private static final String COURSE_EVALUATION_CONTROLLER_NAME = "courseEvaluation";
	private static final String COURSE_OBSERVATION_CONTROLLER_NAME = "courseObservation";

	@SuppressWarnings("rawtypes")
	public Collection getCollection() {
		List<ReportCourseEvaluation> reportCourseEvaluationList = new LinkedList<ReportCourseEvaluation>();
		CourseController courseController = (CourseController)FormUtil.getController(COURSE_CONTROLLER_NAME);
		Course course = (Course)courseController.getTo();
		ReportCourseEvaluation reportCourseEvaluation = new ReportCourseEvaluation();
		reportCourseEvaluation.setCourse(course);
		reportCourseEvaluation.setCourseEvaluations(obtainCourseEvaluations(course));
		reportCourseEvaluation.setCourseObservations(obtainCourseObservations(course));
		reportCourseEvaluationList.add(reportCourseEvaluation);
		return reportCourseEvaluationList;
	}

	@SuppressWarnings("rawtypes")
	public Collection getCollection(boolean forceRefresh) throws ManagerBeanException {
		return getCollection();
	}
	
	private List<CourseEvaluation> obtainCourseEvaluations(Course course){
		try {
			IController courseEvaluationController = FormUtil.getController(COURSE_EVALUATION_CONTROLLER_NAME);
			return (List<CourseEvaluation>)courseEvaluationController.getModel().getWrappedData();
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining course evaluations", e);
		}
		return null;
	}

	private List<CourseObservation> obtainCourseObservations(Course course){
		try {
			IController courseObservationController = FormUtil.getController(COURSE_OBSERVATION_CONTROLLER_NAME);
			return (List<CourseObservation>)courseObservationController.getModel().getWrappedData();
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining course observations", e);
		}
		return null;
	}
	
	public class ReportCourseEvaluation implements ITransferObject {

		private Course course;
		private List<CourseEvaluation> courseEvaluations;
		private List<CourseObservation> courseObservations;

		public Course getCourse() {
			return course;
		}
		public void setCourse(Course course) {
			this.course = course;
		}
		public List<CourseEvaluation> getCourseEvaluations() {
			return courseEvaluations;
		}
		public void setCourseEvaluations(List<CourseEvaluation> courseEvaluations) {
			this.courseEvaluations = courseEvaluations;
		}
		public List<CourseObservation> getCourseObservations() {
			return courseObservations;
		}
		public void setCourseObservations(List<CourseObservation> courseObservations) {
			this.courseObservations = courseObservations;
		}
	}
}