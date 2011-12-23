package com.esferalia.aon.payroll;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.esferalia.aon.entity.master.FanBatchDB;

@Entity
@Table(name="fan_batch")
public class FanBatch extends FanBatchDB {
	
	private static final long serialVersionUID = 1L;

    @Temporal(TemporalType.TIMESTAMP)
	public Date getDate() {
		return super.getDate();
	}
	
}
