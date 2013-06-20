package com.code.aon.fiscal;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.esferalia.aon.entity.master.FiscalBatchDetailDB;

@Entity
@Table(name="fs_batch_detail")
public class FiscalBatchDetail extends FiscalBatchDetailDB {
	
	private static final long serialVersionUID = 1L;
	
}
