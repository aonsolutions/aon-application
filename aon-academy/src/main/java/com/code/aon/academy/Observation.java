package com.code.aon.academy;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.ObservationDB;

/**
 * The Class Observation.
 */
@Entity
@Table(name="observation")
public class Observation extends ObservationDB {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

}
