package com.code.aon.marketing;


import java.util.LinkedList;
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

	@OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE,
			CascadeType.REMOVE }, mappedBy = "question")
	@org.hibernate.annotations.Cascade( {
			org.hibernate.annotations.CascadeType.SAVE_UPDATE,
			org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
	private List<QuestionValue> values = new LinkedList<QuestionValue>();	
	
    public Question() {
    	setActive(true);
    }
	@Transient
	public String getDescription() {
		return StringUtils.defaultString(getAlias(), getText());
	}

	public List<QuestionValue> getValues() {
		return this.values;
	}
	public void setValues( List<QuestionValue> values ) {
		this.values = values;
	}
	
}