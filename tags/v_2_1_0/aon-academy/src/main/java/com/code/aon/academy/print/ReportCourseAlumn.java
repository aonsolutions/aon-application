package com.code.aon.academy.print;

import java.util.Date;

import com.code.aon.academy.CourseAlumn;
import com.code.aon.common.ITransferObject;

public class ReportCourseAlumn implements ITransferObject {

	private CourseAlumn courseAlumn;
	
	private String previousCourse;
	
	private String phone;
	
	private String cellular;
	
	private Date birthDate;

	public CourseAlumn getCourseAlumn() {
		return courseAlumn;
	}

	public void setCourseAlumn(CourseAlumn courseAlumn) {
		this.courseAlumn = courseAlumn;
	}

	public String getPreviousCourse() {
		return previousCourse;
	}

	public void setPreviousCourse(String previousCourse) {
		this.previousCourse = previousCourse;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getCellular() {
		return cellular;
	}

	public void setCellular(String cellular) {
		this.cellular = cellular;
	}

	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}
}