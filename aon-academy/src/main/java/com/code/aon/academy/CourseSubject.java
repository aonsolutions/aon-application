package com.code.aon.academy;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.CourseSubjectDB;

/**
 * The Class CourseSubject.
 */
@Entity
@Table(name="course_subject")
public class CourseSubject extends CourseSubjectDB {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

}
