package com.esferalia.aon.ui.payroll.event.contract;

import java.util.Calendar;
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
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.payroll.ContractLeave;
import com.esferalia.aon.payroll.ContractLeaveDetail;
import com.esferalia.aon.payroll.enumeration.ContractLeaveStatus;
import com.esferalia.aon.payroll.enumeration.LeaveReportType;

public class ContractLeaveDetailControllerListener extends ControllerAdapter{
	
	
	@Override
	public void beforeModelInitialized(ControllerEvent event)
			throws ControllerListenerException {
		try {
			event.getController().getCriteria().addNotEqualExpression(event.getController().getFieldName(IEntityAlias.CONTRACT_LEAVE_DETAIL_TYPE), LeaveReportType.LEAVE);
			event.getController().getCriteria().addNotEqualExpression(event.getController().getFieldName(IEntityAlias.CONTRACT_LEAVE_DETAIL_TYPE), LeaveReportType.DISCHARGE);
		} catch (ManagerBeanException e) {
			// no se filtra
		}
	}
	
	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		ContractLeaveDetail lastLeave = getLastReport((ContractLeave) ((LinesController)event.getController()).getMasterController().getTo());
		ContractLeaveDetail detail = (ContractLeaveDetail) event.getController().getTo();
		detail.setStatus(ContractLeaveStatus.PENDING);
		detail.setType(LeaveReportType.CONFIRM);
		detail.setConfirmOrder((lastLeave==null || lastLeave.getConfirmOrder()==null)?1:lastLeave.getConfirmOrder()+1);
		detail.setCias((lastLeave==null || lastLeave.getCias()==null)?null:lastLeave.getCias());
		detail.setCollegeNumber((lastLeave==null || lastLeave.getCollegeNumber()==null)?null:lastLeave.getCollegeNumber());
		detail.setDate(getConfirmSuggestedDate(lastLeave.getContractLeave().getStartDate(), detail.getConfirmOrder()));
	}
	
	public ContractLeaveDetail getLastReport(ContractLeave leave){
		try {
			IManagerBean bean = BeanManager.getManagerBean(ContractLeaveDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_LEAVE_DETAIL_CONTRACT_LEAVE_ID), leave.getId());
			criteria.addOrder(bean.getFieldName(IEntityAlias.CONTRACT_LEAVE_DETAIL_DATE), false);
			List<ITransferObject> list = bean.getList(criteria);
			if( !list.isEmpty() ){
				return (ContractLeaveDetail) list.get(0);
			}
		} catch (ManagerBeanException e) {
			// NADA, no se sugiere ninguna informacion
		}
		return null;
	}		
	
	public Date getConfirmSuggestedDate(Date startDate, Integer confirmReportNumber) {
		--confirmReportNumber;
		if(startDate==null){
			return new Date();
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(startDate);
		cal.add(Calendar.DAY_OF_YEAR, 3 + (((confirmReportNumber==null?0:confirmReportNumber)  * 7)) );
		return cal.getTime();
	}
	
}
