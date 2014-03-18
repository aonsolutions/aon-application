package com.code.aon.academy;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.AbsenceDB;

/**
 * The Class Absence.
 */
@Entity
@Table(name="absence")
public class Absence extends AbsenceDB {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
    public Absence() {
    	setEvaluation(1);
    	setAbsenceDate( new Date() );
    }	

}