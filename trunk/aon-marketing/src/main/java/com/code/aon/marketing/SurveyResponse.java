package com.code.aon.marketing;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.esferalia.aon.entity.master.SurveyResponseDB;

@Entity
@Table(name="survey_response")
public class SurveyResponse extends SurveyResponseDB {

	private static final long serialVersionUID = 1L;

	private List<SurveyResponseDetail> details;	
	
	@OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE,
			CascadeType.REMOVE }, mappedBy = "surveyResponse")
	@org.hibernate.annotations.Cascade( {
			org.hibernate.annotations.CascadeType.SAVE_UPDATE,
			org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
	public List<SurveyResponseDetail> getDetails() {
		return details;
	}
	public void setDetails(List<SurveyResponseDetail> details) {
		this.details = details;
	}
}