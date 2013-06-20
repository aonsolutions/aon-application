package com.code.aon.academy;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang.time.DateUtils;

import com.esferalia.aon.entity.master.CourseScheduleDB;

/**
 * The Class CourseSchedule.
 */
@Entity
@Table(name="course_schedule")
public class CourseSchedule extends CourseScheduleDB {
	
	private static final long serialVersionUID = 1L;
	
	public CourseSchedule() {
		Date startTime = new Date();
		startTime = DateUtils.setHours(startTime, 0);
		startTime = DateUtils.setMinutes(startTime, 0);
		setStartTime( startTime );		
		Date endTime = new Date();
		endTime = DateUtils.setHours(endTime, 0);
		endTime = DateUtils.setMinutes(endTime, 0);
		setEndTime( endTime );
	}

}
