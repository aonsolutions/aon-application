package com.code.aon.academy;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.AcademicYearDB;

/**
 * The Class AcademicYear.
 */
@Entity
@Table(name="academic_year")
public class AcademicYear extends AcademicYearDB {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

}