package com.code.aon.registry;


import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.QuestionDB;

@Entity
@Table(name = "question")
public class Question extends QuestionDB{

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private List<QuestionValue> values;	
	
    public Question() {
    	setActive(true);
    }
	@Transient
	public String getDescription() {
		return StringUtils.defaultIfEmpty(getAlias(), getText());
	}

	@OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE,
			CascadeType.REMOVE }, mappedBy = "question")
	@org.hibernate.annotations.Cascade( {
			org.hibernate.annotations.CascadeType.SAVE_UPDATE,
			org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
	public List<QuestionValue> getValues() {
		return this.values;
	}
	public void setValues( List<QuestionValue> values ) {
		this.values = values;
	}
	
}