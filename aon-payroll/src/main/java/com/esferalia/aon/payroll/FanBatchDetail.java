package com.esferalia.aon.payroll;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.esferalia.aon.entity.master.FanBatchDetailDB;

@Entity
@Table(name="fan_batch_detail")
public class FanBatchDetail extends FanBatchDetailDB {
	
	private static final long serialVersionUID = 1L;

}
