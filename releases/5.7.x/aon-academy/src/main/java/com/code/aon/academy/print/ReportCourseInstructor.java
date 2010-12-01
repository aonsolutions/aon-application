package com.code.aon.academy.print;

import com.code.aon.academy.CourseInstructor;
import com.code.aon.common.ITransferObject;

public class ReportCourseInstructor implements ITransferObject {

	private CourseInstructor courseInstructor;
	
	private String courseSchedule;
	
	private Double courseHours;

	public CourseInstructor getCourseInstructor() {
		return courseInstructor;
	}

	public void setCourseInstructor(CourseInstructor courseInstructor) {
		this.courseInstructor = courseInstructor;
	}

	public String getCourseSchedule() {
		return courseSchedule;
	}

	public void setCourseSchedule(String courseSchedule) {
		this.courseSchedule = courseSchedule;
	}

	public Double getCourseHours() {
		return courseHours;
	}

	public void setCourseHours(Double courseHours) {
		this.courseHours = courseHours;
	}

}