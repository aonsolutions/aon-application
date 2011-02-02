package com.code.aon.academy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.aon.common.ITransferObject;

@Entity
@Table(name="evaluation_observation")
public class EvaluationObservation implements ITransferObject{

	/** The alumn. */
	private CourseAlumn alumn;

	/** The comments. */
	private String comments;

	/** The evaluation. */
	private int evaluation;
	
	/** The id. */
	private Integer id;
	
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

	@Transient
	public String getCommentsHead(){
		return ((this.comments.length() > 100)?comments.substring(0, 100):comments);
	}
}