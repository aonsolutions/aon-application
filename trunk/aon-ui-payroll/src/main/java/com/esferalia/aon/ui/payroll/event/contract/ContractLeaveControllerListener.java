package com.esferalia.aon.ui.payroll.event.contract;

import java.util.Date;
import java.util.List;

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
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractData;
import com.esferalia.aon.payroll.ContractLeave;
import com.esferalia.aon.payroll.ContractLeaveDetail;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.ContractLeaveStatus;
import com.esferalia.aon.payroll.enumeration.LeaveReportType;
import com.esferalia.aon.payroll.enumeration.OccupationType;
import com.esferalia.aon.ui.payroll.controller.contract.ContractLeaveController;

public class ContractLeaveControllerListener extends ControllerAdapter{
	
	@Override
	public void beforeBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		ContractLeaveController controller = (ContractLeaveController)this.getController();
		controller.createLeaveReportSuggest();
	}
	
	@Override
	public void beforeBeanReset(ControllerEvent event)
			throws ControllerListenerException {
		
	}
		
	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		
	}
	
	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		ContractLeaveDetail detail = (ContractLeaveDetail) this.getController().getTo();
		try {
			IManagerBean bean = BeanManager.getManagerBean(ContractLeave.class);
			ContractLeave leave;
			if(detail.getType()==LeaveReportType.LEAVE){
				detail.setStatus(ContractLeaveStatus.PENDING);
				manageContractOccupation(detail.getContractLeave().getContract(), detail.getContractLeave().getStartDate(), detail.getContractLeave().getEndDate());
			} else if(detail.getType()==LeaveReportType.CONFIRM){
				leave = (ContractLeave) bean.get(detail.getContractLeave().getId());
				detail.setContractLeave(leave);
			} else if(detail.getType()==LeaveReportType.DISCHARGE){
				completeMaster(detail);
				bean.restoreNullSubPOJOs(detail.getContractLeave());
				leave = (ContractLeave) bean.update(detail.getContractLeave());
				detail.setContractLeave(leave);
				manageContractOccupation(leave.getContract(), leave.getStartDate(), leave.getEndDate());
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
		try {
			if(detail.getType()==LeaveReportType.LEAVE || detail.getType()==LeaveReportType.DISCHARGE){
				IManagerBean bean = BeanManager.getManagerBean(ContractLeave.class);
				completeMaster(detail);
				ContractLeave leave = (ContractLeave) bean.update(detail.getContractLeave());
				((ContractLeaveController)this.getController()).initialize();
				manageContractOccupation(leave.getContract(), leave.getStartDate(), leave.getEndDate());
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException();
		} 
	}

	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		
	}
	
	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		
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
	
	private void manageContractOccupation(Contract contract, Date startDate, Date endDate) throws ManagerBeanException{
		IManagerBean bean = BeanManager.getManagerBean(ContractData.class);
		if(startDate != null && endDate == null) {
			ContractData data = new ContractData();
			data.setContract(contract);
			data.setName(ContextVariable.OCCUPATION.getName());
			data.setExpression(OccupationType.C.getValue());
			data.setStartDate(startDate);
			bean.insert(data);
		} else if(startDate != null && endDate != null) {
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_CONTRACT_ID), contract.getId());
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_START_DATE), startDate);
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_NAME), ContextVariable.OCCUPATION.getName());
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_EXPRESSION), OccupationType.C.getValue());
			List<ITransferObject> list = bean.getList(criteria);
			if(!list.isEmpty()){
				ContractData data = (ContractData) list.get(0);
				data.setEndDate(endDate);
				bean.update(data);
			}
		}
	}
    
}
