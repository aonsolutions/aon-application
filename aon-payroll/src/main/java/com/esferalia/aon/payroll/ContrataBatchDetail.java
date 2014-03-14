package com.esferalia.aon.payroll;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.code.aon.common.AonVersion;
import com.esferalia.aon.entity.master.ContrataBatchDetailDB;

@Entity
@Table(name="contrata_batch_detail")
public class ContrataBatchDetail extends ContrataBatchDetailDB {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
}

