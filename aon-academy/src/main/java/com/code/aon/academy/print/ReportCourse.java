package com.code.aon.academy.print;

import com.code.aon.academy.Course;
import com.code.aon.common.ITransferObject;

public class ReportCourse implements ITransferObject {

	private Course course;
	
	private int alumnCount;

	public int getAlumnCount() {
		return alumnCount;
	}

	public void setAlumnCount(int alumnCount) {
		this.alumnCount = alumnCount;
	}

	public Course getCourse() {
		return course;
	}

	public void setCourse(Course course) {
		this.course = course;
	}
}
