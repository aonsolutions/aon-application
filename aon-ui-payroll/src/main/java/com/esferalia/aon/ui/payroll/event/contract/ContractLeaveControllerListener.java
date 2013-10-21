package com.esferalia.aon.ui.payroll.event.contract;

import java.util.Date;
import java.util.List;

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

public class ContractLeaveControllerListener extends ControllerAdapter{
	
	
	@Override
	public void beforeBeanRemoved(ControllerEvent event)
			throws ControllerListenerException {
		ContractLeave leave = (ContractLeave) event.getController().getTo();
		removeDetail(leave,LeaveReportType.LEAVE);
		removeDetail(leave,LeaveReportType.DISCHARGE);
	}
	
	@Override
	public void afterBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		ContractLeave leave = (ContractLeave) event.getController().getTo();
		insertOrUpdateDetail(leave,LeaveReportType.LEAVE,leave.getStartDate());
		insertOrUpdateDetail(leave,LeaveReportType.DISCHARGE, leave.getEndDate());
	}
	
	@Override
	public void afterBeanUpdated(ControllerEvent event)
			throws ControllerListenerException {
		ContractLeave leave = (ContractLeave) event.getController().getTo();
		insertOrUpdateDetail(leave,LeaveReportType.LEAVE,leave.getStartDate());
		insertOrUpdateDetail(leave,LeaveReportType.DISCHARGE, leave.getEndDate());
		LinesController lines = (LinesController) AonUtil.getRegisteredBean("contractLeaveDetail");
		lines.initializeModel();
	}
	
	private void insertOrUpdateDetail(ContractLeave contractLeave, LeaveReportType type, Date date) {
		try {
			if(contractLeave.getStartDate()!=null){
				IManagerBean bean = BeanManager.getManagerBean(ContractLeaveDetail.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_LEAVE_DETAIL_TYPE), type);
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_LEAVE_DETAIL_CONTRACT_LEAVE_ID), contractLeave.getId());
				List<ITransferObject> list = bean.getList(criteria);
				ContractLeaveDetail detailToUpdate;
				if(!list.isEmpty()){
					detailToUpdate = (ContractLeaveDetail) list.get(0);
				} else {
					detailToUpdate = new ContractLeaveDetail();
					detailToUpdate.setContractLeave(contractLeave);
					detailToUpdate.setType(type);
					detailToUpdate.setStatus(ContractLeaveStatus.PENDING);
				}
				detailToUpdate.setDate(date);
				if(type==LeaveReportType.DISCHARGE && detailToUpdate.getId()!=null && date==null){
					bean.remove(detailToUpdate);
				} else {
					if(type!=LeaveReportType.DISCHARGE || date!=null){
						bean.insertOrUpdate(detailToUpdate);
					}
				}
			}
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void removeDetail(ContractLeave contractLeave, LeaveReportType type) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(ContractLeaveDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_LEAVE_DETAIL_TYPE), type);
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_LEAVE_DETAIL_CONTRACT_LEAVE_ID), contractLeave.getId());
			List<ITransferObject> list = bean.getList(criteria);
			if(!list.isEmpty()){
				bean.remove(list.get(0));
			}
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
		
}
