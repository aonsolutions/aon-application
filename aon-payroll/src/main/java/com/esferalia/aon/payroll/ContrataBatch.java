package com.esferalia.aon.payroll;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.code.aon.common.AonVersion;
import com.esferalia.aon.entity.master.ContrataBatchDB;

@Entity
@Table(name="contrata_batch")
public class ContrataBatch extends ContrataBatchDB {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
}

