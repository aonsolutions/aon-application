package com.code.aon.academy.print;

import java.util.List;

import com.code.aon.academy.Absence;
import com.code.aon.academy.CourseAlumn;
import com.code.aon.academy.EvaluationObservation;
import com.code.aon.common.ITransferObject;

public class ReportMark implements ITransferObject {

	private CourseAlumn courseAlumn;
	
	private List<ReportMarkTo> marks;

	private List<Absence> absences;

	private List<EvaluationObservation> observations;

	private String courseSchedule;
	
	public CourseAlumn getCourseAlumn() {
		return courseAlumn;
	}

	public void setCourseAlumn(CourseAlumn courseAlumn) {
		this.courseAlumn = courseAlumn;
	}

	/**
	 * @return the marks
	 */
	public List<ReportMarkTo> getMarks() {
		return marks;
	}

	/**
	 * @param marks the marks to set
	 */
	public void setMarks(List<ReportMarkTo> marks) {
		this.marks = marks;
	}

	/**
	 * @return the absences
	 */
	public List<Absence> getAbsences() {
		return absences;
	}

	/**
	 * @param absences the absences to set
	 */
	public void setAbsences(List<Absence> absences) {
		this.absences = absences;
	}

	/**
	 * @return the observations
	 */
	public List<EvaluationObservation> getObservations() {
		return observations;
	}

	/**
	 * @param observations the observations to set
	 */
	public void setObservations(List<EvaluationObservation> observations) {
		this.observations = observations;
	}

	/**
	 * @return the courseSchedule
	 */
	public String getCourseSchedule() {
		return courseSchedule;
	}

	/**
	 * @param courseSchedule the courseSchedule to set
	 */
	public void setCourseSchedule(String courseSchedule) {
		this.courseSchedule = courseSchedule;
	}

	
}