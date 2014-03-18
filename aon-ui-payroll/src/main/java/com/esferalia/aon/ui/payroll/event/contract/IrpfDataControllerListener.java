package com.esferalia.aon.ui.payroll.event.contract;


import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.faces.event.AbortProcessingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.IrpfData;
import com.esferalia.aon.payroll.enumeration.DisabilityLevel;
import com.esferalia.aon.payroll.enumeration.FamilySituation;
import com.esferalia.aon.ui.payroll.controller.contract.IrpfDataController;

public class IrpfDataControllerListener extends ControllerAdapter{
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(IrpfDataControllerListener.class.getName());
	
	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		IrpfData data = ((IrpfData) getController().getTo());
		if(data.getFamilySituation()!=FamilySituation.MARRIED){
			data.setSpouseDocument(null);
		}
		completeHandicap();
	}

	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		Date date = obtainStartDate();
		((IrpfData) getController().getTo()).setStartDate(date);
		((IrpfData) getController().getTo()).setIssueDate(date);
	}
	
	@Override
	public void beforeBeanUpdated(ControllerEvent event)
	throws ControllerListenerException {
		IrpfData data = ((IrpfData) getController().getTo());
		if(data.getFamilySituation()!=FamilySituation.MARRIED){
			data.setSpouseDocument(null);
		}
		completeHandicap();
	}
	
	@Override
	public void afterBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		updatePreviousData(true);
	}
	
	@Override
	public void beforeBeanRemoved(ControllerEvent event)
			throws ControllerListenerException {
		updatePreviousData(false);
	}
	
	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		completeHandicap();
	}
	
	private void updatePreviousData(boolean closeDate) {
		try {
			if(getController().getRowCount()>1){
				IrpfData data = (IrpfData) getController().getTo();
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(getController().getFieldName(IEntityAlias.IRPF_DATA_CONTRACT_ID), data.getContract().getId());
				criteria.addOrder(getController().getFieldName(IEntityAlias.IRPF_DATA_START_DATE), false);
				List<ITransferObject>  list = getController().getManagerBean().getList(criteria);
				IrpfData preData = (IrpfData) list.get(1);
				if(closeDate){
					Calendar cal = Calendar.getInstance();
					cal.setTime(data.getStartDate());
					cal.add(Calendar.DAY_OF_MONTH, -1);
					preData.setEndDate(cal.getTime());
				} else {
					preData.setEndDate(null);
				}
				getController().getManagerBean().update(preData);
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al finalizar el modelo anterior";
			LOGGER.error(msg);
			throw new AbortProcessingException(msg);
		}
	}

	private void completeHandicap() {
		IrpfDataController controller = (IrpfDataController) getController();
		IrpfData data = ((IrpfData) getController().getTo());
		if(controller.getDisabilityLevel()==DisabilityLevel.GT_EQ_33_LT_65){
			if(data.isDependence()){
				data.setDisabilityLevel(DisabilityLevel.GT_EQ_33_LT_65_DEPENDENCE);
			} else {
				data.setDisabilityLevel(DisabilityLevel.GT_EQ_33_LT_65);
			}
		} else if(controller.getDisabilityLevel()==DisabilityLevel.GT_EQ_33_LT_65_DEPENDENCE){
			data.setDisabilityLevel(DisabilityLevel.GT_EQ_33_LT_65);
			data.setDependence(true);
		} else if(controller.getDisabilityLevel()==DisabilityLevel.GT_EQ_65){
			data.setDisabilityLevel(DisabilityLevel.GT_EQ_65);
			data.setDependence(false);
		}
	}
	
	private Date obtainStartDate() {
		try {
			if(this.getController().getRowCount()==0){
				 Contract contract =  (Contract) ((IrpfDataController)this.getController()).getMasterController().getTo();
				 return contract.getStartDate();
			}
		} catch (ManagerBeanException e) {
			String msg = "";
			AonUtil.addErrorMessage(msg);
			AonUtil.addErrorMessage(e.getMessage());
			LOGGER.error(msg);
			LOGGER.error(e.getMessage());
		}
		return new Date();
	}
	
}
