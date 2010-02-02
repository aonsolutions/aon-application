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
import com.code.aon.cvitae.enumeration.LanguageEnum;

@Entity
@Table(name="cv_languages")
public class Language implements ITransferObject {
	
	private Integer id;
	
	private LanguageEnum language;
	
	private LanguageLevel spoken;
	
	private LanguageLevel wrote;
	
	private LanguageLevel read;
	
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

	public LanguageEnum getLanguage() {
		return language;
	}

	public void setLanguage(LanguageEnum language) {
		this.language = language;
	}

	public LanguageLevel getSpoken() {
		return spoken;
	}

	public void setSpoken(LanguageLevel spoken) {
		this.spoken = spoken;
	}

	public LanguageLevel getWrote() {
		return wrote;
	}

	public void setWrote(LanguageLevel wrote) {
		this.wrote = wrote;
	}

	@Column(name="read_level")
	public LanguageLevel getRead() {
		return read;
	}

	public void setRead(LanguageLevel read) {
		this.read = read;
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
