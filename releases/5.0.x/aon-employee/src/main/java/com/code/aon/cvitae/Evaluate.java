package com.code.aon.cvitae;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.code.aon.common.ITransferObject;
import com.code.aon.cvitae.enumeration.EvaluationLevel;

@Entity
@Table(name="cv_evaluate")
public class Evaluate implements ITransferObject{

	private Integer id;

	private EvaluateType evaluateType;
	private EvaluationLevel value;

	private Curriculum curriculum;

	@Id
	@GeneratedValue
	@Column(nullable=false)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return the type
	 */
	@OneToOne
    @JoinColumn(name="type", nullable = false)
	public EvaluateType getEvaluateType() {
		return evaluateType;
	}

	/**
	 * @param type the type to set
	 */
	public void setEvaluateType(EvaluateType type) {
		this.evaluateType = type;
	}

	/**
	 * @return the value
	 */
	public EvaluationLevel getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(EvaluationLevel value) {
		this.value = value;
	}

	@ManyToOne
    @JoinColumn(name="curriculum", nullable = false)
	public Curriculum getCurriculum() {
		return curriculum;
	}

	public void setCurriculum(Curriculum curriculum) {
		this.curriculum = curriculum;
	}
}
