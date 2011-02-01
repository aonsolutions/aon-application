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
 * The Class CourseEvaluation.
 */
@Entity
@Table(name="course_evaluation")
public class CourseEvaluation implements ITransferObject {
	
	/** The id. */
	private Integer id;
	
	/** The course. */
	private Course course;
	
	/** The QualitySkill. */
	private QualitySkill qualitySkill;
	
	/** The evaluation. */
	private double evaluation;
	
	/** The evaluation. */
	private int quantity;
	
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
	 * @return the qualitySkill
	 */
	@ManyToOne
	@JoinColumn( name="quality_skill",nullable=false )
	public QualitySkill getQualitySkill() {
		return qualitySkill;
	}

	/**
	 * @param qualitySkill the qualitySkill to set
	 */
	public void setQualitySkill(QualitySkill qualitySkill) {
		this.qualitySkill = qualitySkill;
	}

	/**
	 * @return the evaluation
	 */
	public double getEvaluation() {
		return evaluation;
	}

	/**
	 * @param evaluation the evaluation to set
	 */
	public void setEvaluation(double evaluation) {
		this.evaluation = evaluation;
	}

	/**
	 * @return the quantity
	 */
	public int getQuantity() {
		return quantity;
	}

	/**
	 * @param quantity the quantity to set
	 */
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	
	
}
