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
 * The Class CourseInstructor.
 */
@Entity
@Table(name="course_academicskill")
public class CourseAcademicSkill implements ITransferObject {
	
	/** The id. */
	private Integer id;
	
	/** The course. */
	private Course course;
	
	/** The AcademicSkill. */
	private AcademicSkill academicSkill;
	
	/** The weight. */
	private Integer weight;

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
	 * @return the academicSkill
	 */
	@ManyToOne
	@JoinColumn( name="academic_skill",nullable=false )
	public AcademicSkill getAcademicSkill() {
		return academicSkill;
	}

	/**
	 * @param academicSkill the academicSkill to set
	 */
	public void setAcademicSkill(AcademicSkill academicSkill) {
		this.academicSkill = academicSkill;
	}

	@Column(nullable=false)
	public Integer getWeight() {
		return weight;
	}

	public void setWeight(Integer weight) {
		this.weight = weight;
	}

}
