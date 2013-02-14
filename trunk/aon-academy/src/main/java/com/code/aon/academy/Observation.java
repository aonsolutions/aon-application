package com.code.aon.academy;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.esferalia.aon.entity.master.ObservationDB;

/**
 * The Class Observation.
 */
@Entity
@Table(name="observation")
public class Observation extends ObservationDB {
	
	private static final long serialVersionUID = 1L;

}
