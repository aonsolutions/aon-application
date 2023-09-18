package com.esferalia.aon.payroll;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.Certifica2BatchDetailDB;

@Entity
@Table(name="certifica2_batch_detail")
public class Certifica2BatchDetail extends Certifica2BatchDetailDB {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
}


