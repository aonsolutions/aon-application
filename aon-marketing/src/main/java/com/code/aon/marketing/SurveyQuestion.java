package com.code.aon.marketing;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.esferalia.aon.entity.master.SurveyQuestionDB;

@Entity
@Table(name="survey_question")
public class SurveyQuestion extends SurveyQuestionDB {

	private static final long serialVersionUID = 1L;
	
}