package com.esferalia.aon.payroll;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.esferalia.aon.entity.master.FanBatchDB;

@Entity
@Table(name="fan_batch")
public class FanBatch extends FanBatchDB {
	
	private static final long serialVersionUID = 1L;
	
}
