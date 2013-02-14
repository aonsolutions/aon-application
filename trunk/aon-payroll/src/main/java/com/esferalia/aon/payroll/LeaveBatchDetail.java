package com.esferalia.aon.payroll;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.esferalia.aon.entity.master.LeaveBatchDetailDB;

@Entity
@Table(name="leave_batch_detail")
public class LeaveBatchDetail extends LeaveBatchDetailDB {
	
	private static final long serialVersionUID = 1L;

}
