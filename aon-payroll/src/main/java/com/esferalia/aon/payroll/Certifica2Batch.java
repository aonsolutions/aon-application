package com.esferalia.aon.payroll;


import javax.persistence.Entity;
import javax.persistence.Table;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.Certifica2BatchDB;

@Entity
@Table(name = "certifica2_batch")
public class Certifica2Batch extends Certifica2BatchDB {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
}
