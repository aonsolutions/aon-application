package com.code.aon.marketing;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.SurveyResponseDB;

@Entity
@Table(name="survey_response")
public class SurveyResponse extends SurveyResponseDB {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

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