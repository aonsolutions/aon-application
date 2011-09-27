package com.esferalia.aon.ui.payroll.event.contract;

import javax.faces.event.AbortProcessingException;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.ContractLeave;
import com.esferalia.aon.payroll.ContractLeaveDetail;
import com.esferalia.aon.payroll.dao.IPayrollAlias;
import com.esferalia.aon.payroll.enumeration.LeaveReportType;
import com.esferalia.aon.ui.payroll.controller.contract.ContractLeaveController;

public class ContractLeaveControllerListener extends ControllerAdapter{

	
	@Override
	public void beforeBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		getController(event).buildLeaveReport(true);
	}
	
	@Override
	public void beforeBeanReset(ControllerEvent event)
			throws ControllerListenerException {
		getController(event).buildLeaveReport(true);
	}
		
	@Override
	public void beforeBeanUpdated(ControllerEvent event)
			throws ControllerListenerException {
		ContractLeaveDetail detail = getController(event).getReport();
		try {
			IManagerBean bean = BeanManager.getManagerBean(ContractLeave.class);
			if(detail.getType()==LeaveReportType.LEAVE || detail.getType()==LeaveReportType.DISCHARGE){
				bean.update(completeMaster(getController(event).getReport().getContractLeave(), detail));
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException();
		} 
	}
	
	@Override
	public void beforeBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		ContractLeaveDetail detail = getController(event).getReport();
		try {
			IManagerBean bean = BeanManager.getManagerBean(ContractLeave.class);
			ContractLeave leave;
			if(detail.getType()==LeaveReportType.LEAVE){
				getController(event).calculateBases();
				leave = (ContractLeave) bean.insert(completeMaster(getController(event).getReport().getContractLeave(), detail));
				detail.setContractLeave(leave);
			} else if(detail.getType()==LeaveReportType.CONFIRM){
				leave = (ContractLeave) bean.get(detail.getContractLeave().getId());
				detail.setContractLeave(leave);
			} else if(detail.getType()==LeaveReportType.DISCHARGE){
				leave = (ContractLeave) bean.get(detail.getContractLeave().getId());
				leave.setEndDate(detail.getDate());
				leave = (ContractLeave) bean.update(leave);
				detail.setContractLeave(leave);
			}
			completeTO(getController(event));
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException();
		}
	}
	
	@Override
	public void beforeBeanRemoved(ControllerEvent event)
			throws ControllerListenerException {
		ContractLeaveDetail detail = (ContractLeaveDetail) getController(event).getTo(); 
		try {
			if(detail.getType()==LeaveReportType.LEAVE && hasMoreLines(detail.getContractLeave())){
				String msg = "No se puede borrar una baja que tiene confirmaciones.";
				AonUtil.addErrorMessage(msg);
				throw new ControllerListenerException(msg);
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al buscar los partes.";
			AonUtil.addErrorMessage(msg);
			throw new ControllerListenerException(msg);
		}
	}

	@Override
	public void afterBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		getController(event).initialize();
	}
	
	@Override
	public void afterBeanUpdated(ControllerEvent event)
	throws ControllerListenerException {
		getController(event).initialize();
	}
	
	@Override
	public void afterBeanRemoved(ControllerEvent event)
			throws ControllerListenerException {
		ContractLeaveDetail detail = (ContractLeaveDetail) getController(event).getTo(); 
		if(detail.getType()==LeaveReportType.LEAVE){
			try {
				removeMaster(detail.getContractLeave());
				getController().onReset(null);
			} catch (ManagerBeanException e) {
				String msg = "Error al borrar la cabecera.";
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			}
		}
		getController(event).initialize();
	}
	
	private boolean hasMoreLines(ContractLeave leave) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(ContractLeaveDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IPayrollAlias.CONTRACT_LEAVE_DETAIL_CONTRACT_LEAVE_ID), leave.getId());
		if(bean.getList(criteria).size()>1){
			return true;
		}
		return false;
	}
	
	private void removeMaster(ContractLeave leave) throws ManagerBeanException{
		IManagerBean bean = BeanManager.getManagerBean(ContractLeave.class);
		bean.remove(leave);
		
	}
	
	private ITransferObject completeMaster(ContractLeave leave, ContractLeaveDetail detail) {
		leave.setContract(((ContractLeaveController)getController()).getContract());
		if(detail.getType()==LeaveReportType.LEAVE){
			leave.setStartDate(detail.getDate());
		} else if(detail.getType()==LeaveReportType.DISCHARGE){
			leave.setEndDate(detail.getDate());
		}
		return leave;
	}

	private void completeTO(ContractLeaveController controller) {
		ContractLeaveDetail detail = controller.getReport();
		ContractLeaveDetail to = (ContractLeaveDetail) controller.getTo();
		to.setContractLeave(detail.getContractLeave());
		to.setCias(detail.getCias());
		to.setCollegeNumber(detail.getCollegeNumber());
		to.setType(detail.getType());
		to.setConfirmOrder(detail.getConfirmOrder());
		to.setDate(detail.getDate());
	}
	
	private ContractLeaveController getController(ControllerEvent event){
		return (ContractLeaveController) event.getController();
	}
	
	
    
}
