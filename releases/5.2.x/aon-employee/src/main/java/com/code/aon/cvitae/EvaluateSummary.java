package com.code.aon.cvitae;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.code.aon.common.ITransferObject;
import com.code.aon.cvitae.enumeration.LanguageLevel;

@Entity
@Table(name="cv_evaluate_summary")
public class EvaluateSummary implements ITransferObject{

	private Integer id;

	private String strengths;
	private String weaknesses;
	private LanguageLevel profile;
	private String comments;

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
	 * @return the strengths
	 */
	public String getStrengths() {
		return strengths;
	}

	/**
	 * @param strengths the strengths to set
	 */
	public void setStrengths(String strengths) {
		this.strengths = strengths;
	}

	/**
	 * @return the weaknesses
	 */
	public String getWeaknesses() {
		return weaknesses;
	}

	/**
	 * @param weaknesses the weaknesses to set
	 */
	public void setWeaknesses(String weaknesses) {
		this.weaknesses = weaknesses;
	}

	/**
	 * @return the profile
	 */
	public LanguageLevel getProfile() {
		return profile;
	}

	/**
	 * @param profile the profile to set
	 */
	public void setProfile(LanguageLevel profile) {
		this.profile = profile;
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

	@ManyToOne
    @JoinColumn(name="curriculum", nullable = false)
	public Curriculum getCurriculum() {
		return curriculum;
	}

	public void setCurriculum(Curriculum curriculum) {
		this.curriculum = curriculum;
	}
}
