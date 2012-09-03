package com.code.aon.commercial;


import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;

import com.esferalia.aon.entity.master.QuestionDB;

@Entity
@Table(name = "question")
public class Question extends QuestionDB{

	private static final long serialVersionUID = 1L;

	private List<QuestionValue> values;	
	
    public Question() {
    	setActive(true);
    }
	@Transient
	public String getDescription() {
		return StringUtils.defaultString(getAlias(), getText());
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