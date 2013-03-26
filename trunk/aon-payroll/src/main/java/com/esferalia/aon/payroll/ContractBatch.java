package com.esferalia.aon.payroll;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.esferalia.aon.entity.master.ContractBatchDB;

@Entity
@Table(name="contract_batch")
public class ContractBatch extends ContractBatchDB {
	
	private static final long serialVersionUID = 1L;
	
}

