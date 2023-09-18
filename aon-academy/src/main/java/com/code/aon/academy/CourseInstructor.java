package com.code.aon.academy;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.CourseInstructorDB;

/**
 * The Class CourseInstructor.
 */
@Entity
@Table(name="course_instructor")
public class CourseInstructor extends CourseInstructorDB {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

}