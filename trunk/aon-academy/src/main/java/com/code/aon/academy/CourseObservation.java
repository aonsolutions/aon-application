package com.code.aon.academy;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.esferalia.aon.entity.master.CourseObservationDB;

/**
 * The Class CourseObservation.
 */
@Entity
@Table(name="course_observation")
public class CourseObservation extends CourseObservationDB {
	
	private static final long serialVersionUID = 1L;

}