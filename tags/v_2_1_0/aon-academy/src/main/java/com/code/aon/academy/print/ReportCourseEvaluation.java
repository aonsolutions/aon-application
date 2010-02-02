package com.code.aon.academy.print;

import java.util.List;

import com.code.aon.academy.Course;
import com.code.aon.academy.CourseEvaluation;
import com.code.aon.academy.CourseObservation;
import com.code.aon.common.ITransferObject;

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

	/**
	 * @return the courseEvaluations
	 */
	public List<CourseEvaluation> getCourseEvaluations() {
		return courseEvaluations;
	}

	/**
	 * @param courseEvaluations the courseEvaluations to set
	 */
	public void setCourseEvaluations(List<CourseEvaluation> courseEvaluations) {
		this.courseEvaluations = courseEvaluations;
	}

	/**
	 * @return the courseObservations
	 */
	public List<CourseObservation> getCourseObservations() {
		return courseObservations;
	}

	/**
	 * @param courseObservations the courseObservations to set
	 */
	public void setCourseObservations(List<CourseObservation> courseObservations) {
		this.courseObservations = courseObservations;
	}


}