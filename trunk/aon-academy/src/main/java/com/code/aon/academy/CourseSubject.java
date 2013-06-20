package com.code.aon.academy;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.esferalia.aon.entity.master.CourseSubjectDB;

/**
 * The Class CourseSubject.
 */
@Entity
@Table(name="course_subject")
public class CourseSubject extends CourseSubjectDB {

	private static final long serialVersionUID = 1L;

}
