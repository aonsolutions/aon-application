package com.code.aon.marketing;

import java.util.Date;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import com.code.aon.AonVersion;
import com.code.aon.config.IScopable;
import com.esferalia.aon.entity.master.SurveyDB;

@Entity
@Table(name = "survey")
public class Survey extends SurveyDB implements IScopable {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

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