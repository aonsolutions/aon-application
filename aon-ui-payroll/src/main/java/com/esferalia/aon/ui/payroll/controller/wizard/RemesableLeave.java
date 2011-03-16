package com.esferalia.aon.ui.payroll.controller.wizard;

import java.io.Serializable;

import com.esferalia.aon.payroll.ContractLeave;
import com.esferalia.aon.payroll.ContractLeaveDetail;
import com.esferalia.aon.payroll.LeaveBatch;
//import com.esferalia.aon.payroll.PayrollException;
//import com.esferalia.aon.payroll.core.enumeration.TipoOperacionIT;
//import com.esferalia.aon.payroll.core.it.IParteConfirmacionIT;
//import com.esferalia.aon.payroll.core.it.IParteIT;
//import com.esferalia.aon.payroll.core.it.IParteITDAO;
//import com.esferalia.aon.payroll.core.it.ParteITDAOFactory;
//import com.esferalia.aon.payroll.core.remesa.IRemesaParteIT;
import com.esferalia.aon.payroll.enumeration.LeaveReportType;

public class RemesableLeave implements Serializable {
	
	private static final long serialVersionUID = 3177644348509018028L;

	private LeaveReportType reportType;
	private boolean selected;
	private ContractLeave leave;
	private ContractLeaveDetail leaveDetail;
	private LeaveBatch leaveBatch;
	
	public LeaveReportType getReportType() {
		return reportType;
	}
	public void setReportType(LeaveReportType reportType) {
		this.reportType = reportType;
	}
	
	public boolean isSelected() {
		return selected;
	}
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
	public ContractLeave getLeave() {
		return leave;
	}
	public void setLeave(ContractLeave leave) {
		this.leave = leave;
	}
	public ContractLeaveDetail getLeaveDetail() {
		return leaveDetail;
	}
	public void setLeaveDetail(ContractLeaveDetail leaveDetail) {
		this.leaveDetail = leaveDetail;
	}
	
	public boolean isLeave() {
		return getReportType() == LeaveReportType.LEAVE;
	}
	public boolean isDischarge() {
		return getReportType() == LeaveReportType.DISCHARGE;
	}
	public boolean isConfirm() {
		return getReportType() == LeaveReportType.CONFIRM;
	}
	
	public LeaveBatch getLeaveBatch() {
		return leaveBatch;
	}
	public void setLeaveBatch(LeaveBatch leaveBatch) {
		this.leaveBatch = leaveBatch;
	}
		
}
