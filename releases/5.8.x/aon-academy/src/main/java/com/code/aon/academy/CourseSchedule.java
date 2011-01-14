package com.code.aon.academy;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.enumeration.WeekDay;

/**
 * The Class CourseSchedule.
 */
@Entity
@Table(name="course_schedule")
public class CourseSchedule implements ITransferObject {
	
	/** The id. */
	private Integer id;
	
	/** The course. */
	private Course course;
	
	/** The day. */
	private WeekDay day;
	
	/** The start time. */
	private Date startTime;
	
	/** The end time. */
	private Date endTime;

	
	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
	@Id
	@GeneratedValue
	@Column(nullable=false)
	public Integer getId() {
		return id;
	}

	/**
	 * Sets the id.
	 * 
	 * @param id the id
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * Gets the course.
	 * 
	 * @return the course
	 */
	@ManyToOne
	@JoinColumn( name="course",nullable=false )
	public Course getCourse() {
		return course;
	}

	/**
	 * Sets the course.
	 * 
	 * @param course the course
	 */
	public void setCourse(Course course) {
		this.course = course;
	}
	
	/**
	 * Gets the day.
	 * 
	 * @return the day
	 */
	@Column(name="day_of_week")
	public WeekDay getDay() {
		return day;
	}

	/**
	 * Sets the day.
	 * 
	 * @param day the day
	 */
	public void setDay(WeekDay day) {
		this.day = day;
	}

	/**
	 * Gets the start time.
	 * 
	 * @return the start time
	 */
	@Column(name="start_time")
	public Date getStartTime() {
		return startTime;
	}

	/**
	 * Sets the start time.
	 * 
	 * @param startTime the start time
	 */
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	/**
	 * Gets the end time.
	 * 
	 * @return the end time
	 */
	@Column(name="end_time")
	public Date getEndTime() {
		return endTime;
	}

	/**
	 * Sets the end time.
	 * 
	 * @param endTime the end time
	 */
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
}