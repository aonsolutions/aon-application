package com.esferalia.aon.ui.payroll.event.contract;


import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.faces.event.AbortProcessingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.esferalia.aon.payroll.IrpfData;
import com.esferalia.aon.payroll.dao.IPayrollAlias;
import com.esferalia.aon.ui.payroll.controller.contract.IrpfDataController;

public class IrpfDataControllerListener extends ControllerAdapter{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(IrpfDataControllerListener.class.getName());
	
	
	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		completeCurrent();
	}
	
	@Override
	public void afterBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		closePrevious();
	}
	
	@Override
	public void afterBeanUpdated(ControllerEvent event)
			throws ControllerListenerException {
		((IrpfDataController)getController()).onCalculateIrpf(null);
	}
	
	private void closePrevious() {
		try {
			if(getController().getRowCount()>1){
				IrpfData data = (IrpfData) getController().getTo();
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(getController().getFieldName(IPayrollAlias.IRPF_DATA_CONTRACT_ID), data.getContract().getId());
				criteria.addOrder(getController().getFieldName(IPayrollAlias.IRPF_DATA_START_DATE), false);
				List<ITransferObject>  list = getController().getManagerBean().getList(criteria);
				IrpfData preData = (IrpfData) list.get(1);
				Calendar cal = Calendar.getInstance();
				cal.setTime(new Date());
				cal.add(Calendar.DAY_OF_MONTH, -1);
				preData.setEndDate(cal.getTime());
				getController().getManagerBean().update(preData);
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al finalizar el modelo anterior";
			LOGGER.error(msg);
			throw new AbortProcessingException(msg);
		}
	}

	private void completeCurrent() {
		((IrpfData) getController().getTo()).setStartDate(new Date());
	}
		
}
