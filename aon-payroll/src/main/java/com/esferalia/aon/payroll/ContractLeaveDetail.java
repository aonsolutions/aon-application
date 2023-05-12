package com.esferalia.aon.payroll;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.ContractLeaveDetailDB;
import com.esferalia.aon.payroll.enumeration.LeaveReportType;

@Entity
@Table(name="contract_leave_detail")
public class ContractLeaveDetail extends ContractLeaveDetailDB {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Transient
	public boolean isLeave(){
		return getType()==LeaveReportType.LEAVE;
	}
	
	@Transient
	public boolean isConfirm(){
		return getType()==LeaveReportType.CONFIRM;
	}
	
	@Transient
	public boolean isDischarge(){
		return getType()==LeaveReportType.DISCHARGE;
	}
	
}

