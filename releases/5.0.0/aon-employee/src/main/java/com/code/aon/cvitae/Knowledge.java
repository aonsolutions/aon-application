package com.code.aon.cvitae;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.code.aon.common.ITransferObject;
import com.code.aon.cvitae.enumeration.KnowledgeLastUse;
import com.code.aon.cvitae.enumeration.KnowledgeLevel;
import com.code.aon.cvitae.enumeration.KnowledgeExperience;

@Entity
@Table(name="cv_knowledge")
public class Knowledge implements ITransferObject{

	private Integer id;
	
	private String name;
	
	private KnowledgeLevel level;
	
	private KnowledgeExperience experience;
	
	private KnowledgeLastUse lastUse;
	
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

	@Column(length=64)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public KnowledgeLevel getLevel() {
		return level;
	}

	public void setLevel(KnowledgeLevel level) {
		this.level = level;
	}

	public KnowledgeExperience getExperience() {
		return experience;
	}

	public void setExperience(KnowledgeExperience experience) {
		this.experience = experience;
	}

	@Column(name="lastuse")
	public KnowledgeLastUse getLastUse() {
		return lastUse;
	}

	public void setLastUse(KnowledgeLastUse lastUse) {
		this.lastUse = lastUse;
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
