package com.esferalia.aon.payroll;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.LeaveBatchDetailDB;

@Entity
@Table(name="leave_batch_detail")
public class LeaveBatchDetail extends LeaveBatchDetailDB {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

}
