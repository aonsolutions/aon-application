package com.code.aon.academy;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.QualificationDB;

@Entity
@Table(name="qualification")
public class Qualification extends QualificationDB {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;	

}
