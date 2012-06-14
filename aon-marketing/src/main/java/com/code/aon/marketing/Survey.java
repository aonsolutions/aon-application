package com.code.aon.marketing;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.esferalia.aon.entity.master.SurveyDB;

@Entity
@Table(name = "survey")
public class Survey extends SurveyDB {

	private static final long serialVersionUID = 1L;

	private List<SurveyQuestion> questions;	
	
    public Survey() {
    	setCreationDate( new Date() );
    	setActive( true );
    }
  
	@OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE,
			CascadeType.REMOVE }, mappedBy = "survey")
	@org.hibernate.annotations.Cascade( {
			org.hibernate.annotations.CascadeType.SAVE_UPDATE,
			org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
	public List<SurveyQuestion> getQuestions() {
		return this.questions;
	}
	public void setQuestions( List<SurveyQuestion> questions ) {
		this.questions = questions;
	}
}