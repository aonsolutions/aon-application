package com.code.aon.academy;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.EvaluationObservationDB;

@Entity
@Table(name="evaluation_observation")
public class EvaluationObservation extends EvaluationObservationDB {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

}
