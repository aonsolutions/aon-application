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

/**
 * The Class Absence.
 */
@Entity
@Table(name="absence")
public class Absence implements ITransferObject{

	/** The id. */
	private Integer id;

	/** The course_alumn. */
	private CourseAlumn courseAlumn;
	
	/** The evaluation. */
	private int evaluation;
	
	/** The date. */
	private Date absenceDate;
	
	/** The comments. */
	private String comments;

	/**
	 * @return the id
	 */
	@Id
	@GeneratedValue
	@Column(nullable=false)
	public Integer getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return the absenceDate
	 */
	@Column(name="absence_date", nullable=false)
	public Date getAbsenceDate() {
		return absenceDate;
	}

	/**
	 * @param absenceDate the absenceDate to set
	 */
	public void setAbsenceDate(Date absenceDate) {
		this.absenceDate = absenceDate;
	}

	/**
	 * @return the comments
	 */
	public String getComments() {
		return comments;
	}

	/**
	 * @param comments the comments to set
	 */
	public void setComments(String comments) {
		this.comments = comments;
	}

	/**
	 * @return the courseAlumn
	 */
	@ManyToOne
	@JoinColumn( name="course_alumn",nullable=false )
	public CourseAlumn getCourseAlumn() {
		return courseAlumn;
	}

	/**
	 * @param courseAlumn the courseAlumn to set
	 */
	public void setCourseAlumn(CourseAlumn courseAlumn) {
		this.courseAlumn = courseAlumn;
	}

	/**
	 * @return the evaluation
	 */
	public int getEvaluation() {
		return evaluation;
	}

	/**
	 * @param evaluation the evaluation to set
	 */
	public void setEvaluation(int evaluation) {
		this.evaluation = evaluation;
	}

}
