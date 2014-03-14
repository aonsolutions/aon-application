package com.esferalia.aon.payroll;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.code.aon.common.AonVersion;
import com.esferalia.aon.entity.master.ContractBatchDetailDB;

@Entity
@Table(name="contract_batch_detail")
public class ContractBatchDetail extends ContractBatchDetailDB{

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
}


