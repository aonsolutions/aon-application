package com.code.aon.academy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.code.aon.common.ITransferObject;

@Entity
@Table(name="mark")
public class Mark implements ITransferObject{

	/** The id. */
	private Integer id;
	
	/** The courseAcademicSkill. */
	private CourseAcademicSkill subject;
	
	/** The customer. */
	private CourseAlumn alumn;

	/** The evaluation. */
	private int evaluation;
	
	/** The mark. */
	private double mark;

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
	 * @return the alumn
	 */
	@ManyToOne
	@JoinColumn( name="alumn",nullable=false )
	public CourseAlumn getAlumn() {
		return alumn;
	}

	/**
	 * @param alumn the alumn to set
	 */
	public void setAlumn(CourseAlumn alumn) {
		this.alumn = alumn;
	}

	/**
	 * @return the mark
	 */
	public double getMark() {
		return mark;
	}

	/**
	 * @param mark the mark to set
	 */
	public void setMark(double mark) {
		this.mark = mark;
	}

	/**
	 * @return the subject
	 */
	@ManyToOne
	@JoinColumn( name="subject",nullable=false )
	public CourseAcademicSkill getSubject() {
		return subject;
	}

	/**
	 * @param subject the subject to set
	 */
	public void setSubject(CourseAcademicSkill subject) {
		this.subject = subject;
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
