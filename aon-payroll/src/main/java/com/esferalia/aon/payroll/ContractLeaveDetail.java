package com.esferalia.aon.payroll;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.esferalia.aon.entity.master.ContractLeaveDetailDB;
import com.esferalia.aon.payroll.enumeration.LeaveReportType;

@Entity
@Table(name="contract_leave_detail")
public class ContractLeaveDetail extends ContractLeaveDetailDB {
	
	private static final long serialVersionUID = 1L;

	@Transient
	public boolean isConfirm(){
		return getType()==LeaveReportType.CONFIRM;
	}
	
}

