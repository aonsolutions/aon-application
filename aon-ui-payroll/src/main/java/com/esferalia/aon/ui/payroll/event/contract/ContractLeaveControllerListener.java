package com.esferalia.aon.ui.payroll.event.contract;

import java.util.Date;
import java.util.List;

import javax.faces.event.AbortProcessingException;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.LinesController;
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
		loadContractLeave(event);
	}
	
	@Override
	public void beforeBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		updateFinalBases(event);
	}
	
	@Override
	public void beforeBeanUpdated(ControllerEvent event)
			throws ControllerListenerException {
		updateFinalBases(event);
	}
	
	@Override
	public void beforeBeanRemoved(ControllerEvent event)
			throws ControllerListenerException {
		ContractLeave leave = (ContractLeave) event.getController().getTo();
		removeDetail(leave,LeaveReportType.LEAVE);
		removeDetail(leave,LeaveReportType.DISCHARGE);
	}
	
	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		loadContractLeave(event);
		ContractLeaveController controller = (ContractLeaveController) event.getController();
		ContractLeave leave = (ContractLeave) controller.getTo();
		controller.setEditBases(leave.getDailyCgcBase()!=null || leave.getDailyCgpBase()!=null || leave.getDailyRegBase()!=null);
		updateTempBases(event);
	}
	
	@Override
	public void afterBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		ContractLeaveController controller = (ContractLeaveController) event.getController();
		ContractLeave leave = (ContractLeave) controller.getTo();
		insertOrUpdateDetail(leave,LeaveReportType.LEAVE,leave.getStartDate());
		insertOrUpdateDetail(leave,LeaveReportType.DISCHARGE, leave.getEndDate());
		loadContractLeave(event);
		updateTempBases(event);
	}
	
	@Override
	public void afterBeanUpdated(ControllerEvent event)
			throws ControllerListenerException {
		ContractLeaveController controller = (ContractLeaveController) event.getController();
		ContractLeave leave = (ContractLeave) controller.getTo();
		insertOrUpdateDetail(leave,LeaveReportType.LEAVE,leave.getStartDate());
		insertOrUpdateDetail(leave,LeaveReportType.DISCHARGE, leave.getEndDate());
		loadContractLeave(event);
		LinesController lines = (LinesController) AonUtil.getRegisteredBean("contractLeaveDetail");
		lines.initializeModel();
	}
	
	private void loadContractLeave(ControllerEvent event) {
		ContractLeaveController controller = (ContractLeaveController) event.getController();
		controller.setLeave(obtainDetail((ContractLeave) controller.getTo(),LeaveReportType.LEAVE));
		controller.setDischarge(obtainDetail((ContractLeave) controller.getTo(),LeaveReportType.DISCHARGE));
	}
	
	private void updateFinalBases(ControllerEvent event) {
		ContractLeaveController controller = (ContractLeaveController) event.getController();
		ContractLeave leave = (ContractLeave) controller.getTo();
		if(!controller.isEditBases()){
			leave.setDailyCgcBase(null);
			leave.setDailyCgpBase(null);
			leave.setDailyRegBase(null);
		}
	}
	
	private void updateTempBases(ControllerEvent event) {
		ContractLeaveController controller = (ContractLeaveController) event.getController();
		ContractLeave leave = (ContractLeave) controller.getTo();
		if(controller.isEditBases()){
			controller.setDailyCgcBase(leave.getDailyCgcBase());
			controller.setDailyCgpBase(leave.getDailyCgpBase());
			controller.setDailyRegBase(leave.getDailyRegBase());
		} else {
			controller.calculateBases(leave, leave.getContract());
		}
	}
	
	private ContractLeaveDetail obtainDetail(ContractLeave contractLeave, LeaveReportType type) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(ContractLeaveDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_LEAVE_DETAIL_TYPE), type);
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_LEAVE_DETAIL_CONTRACT_LEAVE_ID), contractLeave.getId());
			List<ITransferObject> list = bean.getList(criteria);
			if(!list.isEmpty()){
				return (ContractLeaveDetail) list.get(0);
			} else {
				ContractLeaveDetail detail = new ContractLeaveDetail();
				detail.setContractLeave(contractLeave);
				detail.setType(type);
				detail.setStatus(ContractLeaveStatus.PENDING);
				return detail;
			}
		} catch (ManagerBeanException e) {
			String msg = "No se han podido obtener los datos del parte IT";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		}
	}
	
	private void insertOrUpdateDetail(ContractLeave contractLeave, LeaveReportType type, Date date) {
		try {
			if(contractLeave.getStartDate()!=null){				
				
				ContractLeaveController controller = (ContractLeaveController) this.getController();
				ContractLeaveDetail detailToUpdate = null; 
				if(type==LeaveReportType.LEAVE){
					detailToUpdate = controller.getLeave();
				} else if(type==LeaveReportType.DISCHARGE){
					detailToUpdate = controller.getDischarge();
				}
				detailToUpdate.setDate(date);
				IManagerBean bean = BeanManager.getManagerBean(ContractLeaveDetail.class);
				if(type==LeaveReportType.DISCHARGE && detailToUpdate.getId()!=null && date==null){
					bean.remove(detailToUpdate);
				} else {
					if(type!=LeaveReportType.DISCHARGE || date!=null){
						bean.insertOrUpdate(detailToUpdate);
					}
				}
			}
		} catch (ManagerBeanException e) {
			String msg = "No se ha podido actualizar el parte IT";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		}
	}
	
	private void removeDetail(ContractLeave contractLeave, LeaveReportType type) {
		try {
			ContractLeaveDetail detailToRemove = obtainDetail(contractLeave, type);
			if(detailToRemove!=null && detailToRemove.getId()!=null){
				IManagerBean bean = BeanManager.getManagerBean(ContractLeaveDetail.class);
				bean.remove(detailToRemove);
			}
		} catch (ManagerBeanException e) {
			String msg = "No se ha podido borrar el parte IT";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		}
	}
	
	
		
}
