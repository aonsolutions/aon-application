package com.esferalia.aon.ui.payroll.event.contract;

import javax.faces.event.AbortProcessingException;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.payroll.ContractLeave;
import com.esferalia.aon.payroll.ContractLeaveDetail;
import com.esferalia.aon.payroll.enumeration.ContractLeaveStatus;
import com.esferalia.aon.payroll.enumeration.LeaveReportType;
import com.esferalia.aon.ui.payroll.controller.contract.ContractLeaveController;

public class ContractLeaveControllerListener extends ControllerAdapter{
	
	@Override
	public void beforeBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		ContractLeaveController controller = (ContractLeaveController)this.getController();
		controller.createLeaveReportSuggest();
	}
	
	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		ContractLeaveDetail detail = (ContractLeaveDetail) this.getController().getTo();
		try {
			IManagerBean bean = BeanManager.getManagerBean(ContractLeave.class);
			ContractLeave leave;
			if(detail.getType()==LeaveReportType.LEAVE){
				detail.setStatus(ContractLeaveStatus.PENDING);
			} else if(detail.getType()==LeaveReportType.CONFIRM){
				leave = (ContractLeave) bean.get(detail.getContractLeave().getId());
				detail.setContractLeave(leave);
			} else if(detail.getType()==LeaveReportType.DISCHARGE){
				completeMaster(detail);
				bean.restoreNullSubPOJOs(detail.getContractLeave());
				leave = (ContractLeave) bean.update(detail.getContractLeave());
				detail.setContractLeave(leave);
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al guardar los datos. ";
			throw new ControllerListenerException(msg + "(" + e + ")");
		}
	}
	
	@Override
	public void beforeBeanRemoved(ControllerEvent event)
			throws ControllerListenerException {
		ContractLeaveDetail detail = (ContractLeaveDetail) this.getController().getTo(); 
		try {
			if(detail.getType()==LeaveReportType.LEAVE && hasMoreLines(detail.getContractLeave())){
				String msg = "No se puede borrar una baja que tiene confirmaciones.";
				throw new ControllerListenerException(msg);
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al buscar los partes.";
			throw new ControllerListenerException(msg);
		}
	}

	@Override
	public void afterBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		((ContractLeaveController)this.getController()).initialize();
	}
	
	@Override
	public void afterBeanUpdated(ControllerEvent event)
	throws ControllerListenerException {
		ContractLeaveDetail detail = (ContractLeaveDetail) this.getController().getTo();
		if(detail.getType()==LeaveReportType.LEAVE || detail.getType()==LeaveReportType.DISCHARGE){
			completeMaster(detail);
			((ContractLeaveController)this.getController()).initialize();
		}
	}
	
	@Override
	public void afterBeanRemoved(ControllerEvent event)
			throws ControllerListenerException {
		ContractLeaveDetail detail = (ContractLeaveDetail) this.getController().getTo(); 
		try {
			if( detail.getType()==LeaveReportType.LEAVE ){
				removeMaster(detail.getContractLeave());
				((ContractLeaveController)this.getController()).initialize();
			} else if( detail.getType()==LeaveReportType.DISCHARGE ){
				detail.getContractLeave().setEndDate(null);
				detail.getContractLeave().setDischargeCause(null);
				updateMaster(detail.getContractLeave());
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al borrar la baja.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
		this.getController().onCancel(null);
	}
	
	private boolean hasMoreLines(ContractLeave leave) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(ContractLeaveDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_LEAVE_DETAIL_CONTRACT_LEAVE_ID), leave.getId());
		if( bean.getList(criteria).size() > 1 ) {
			return true;
		}
		return false;
	}
	
	private void removeMaster(ContractLeave leave) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(ContractLeave.class);
		bean.remove(leave);
	}
	
	private void updateMaster(ContractLeave leave) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(ContractLeave.class);
		bean.update(leave);
	}
	
	private void completeMaster(ContractLeaveDetail detail) {
		detail.getContractLeave().setContract(((ContractLeaveController)getController()).getContract());
		if(detail.getType()==LeaveReportType.LEAVE){
			detail.getContractLeave().setStartDate(detail.getDate());
		} else if(detail.getType()==LeaveReportType.DISCHARGE){
			detail.getContractLeave().setEndDate(detail.getDate());
		}
	}
	
}
