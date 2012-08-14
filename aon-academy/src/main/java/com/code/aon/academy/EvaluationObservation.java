package com.code.aon.academy;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.esferalia.aon.entity.master.EvaluationObservationDB;

@Entity
@Table(name="evaluation_observation")
public class EvaluationObservation extends EvaluationObservationDB {

	private static final long serialVersionUID = 1L;

	@Transient
	public String getCommentsHead(){
		return ((getComments().length() > 100)?getComments().substring(0, 100):getComments());
	}


}
