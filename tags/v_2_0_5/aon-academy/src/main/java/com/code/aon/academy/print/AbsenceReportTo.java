package com.code.aon.academy.print;

import java.util.List;

import com.code.aon.academy.Course;
import com.code.aon.academy.CourseAlumn;
import com.code.aon.common.ITransferObject;
import com.code.aon.company.resources.Employee;

public class AbsenceReportTo implements ITransferObject{
	
	private Course course;

	private List<CourseAlumn> courseAlumns;
	
	private Employee instructor;

	public Course getCourse() {
		return course;
	}

	public void setCourse(Course course) {
		this.course = course;
	}

	public List<CourseAlumn> getCourseAlumns() {
		return courseAlumns;
	}

	public void setCourseAlumns(List<CourseAlumn> courseAlumns) {
		this.courseAlumns = courseAlumns;
	}

	public Employee getInstructor() {
		return instructor;
	}

	public void setInstructor(Employee instructor) {
		this.instructor = instructor;
	}
}