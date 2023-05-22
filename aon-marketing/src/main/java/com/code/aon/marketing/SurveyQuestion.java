package com.code.aon.marketing;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.SurveyQuestionDB;

@Entity
@Table(name="survey_question")
public class SurveyQuestion extends SurveyQuestionDB {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
}