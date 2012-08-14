package com.code.aon.academy;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.esferalia.aon.entity.master.CourseScheduleDB;

/**
 * The Class CourseSchedule.
 */
@Entity
@Table(name="course_schedule")
public class CourseSchedule extends CourseScheduleDB {
	
	private static final long serialVersionUID = 1L;

}
