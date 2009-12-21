package com.code.aon.academy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.code.aon.common.ITransferObject;

/**
 * The Class CourseObservation.
 */
@Entity
@Table(name="course_observation")
public class CourseObservation implements ITransferObject {
	
	/** The id. */
	private Integer id;
	
	/** The course. */
	private Course course;
	
	/** The observation. */
	private String observation;
	
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
	 * @return the observation
	 */
	public String getObservation() {
		return observation;
	}

	/**
	 * @param observation the observation to set
	 */
	public void setObservation(String observation) {
		this.observation = observation;
	}

	
	
}
