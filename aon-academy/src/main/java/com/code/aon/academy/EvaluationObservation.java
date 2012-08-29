package com.code.aon.academy;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.esferalia.aon.entity.master.EvaluationObservationDB;

@Entity
@Table(name="evaluation_observation")
public class EvaluationObservation extends EvaluationObservationDB {

	private static final long serialVersionUID = 1L;

}
