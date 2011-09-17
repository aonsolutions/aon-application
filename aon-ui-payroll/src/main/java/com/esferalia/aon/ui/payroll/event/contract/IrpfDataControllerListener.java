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
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.esferalia.aon.payroll.IrpfData;
import com.esferalia.aon.payroll.dao.IPayrollAlias;
import com.esferalia.aon.payroll.enumeration.DisabilityLevel;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;
import com.esferalia.aon.ui.payroll.controller.contract.IrpfDataController;

public class IrpfDataControllerListener extends ControllerAdapter{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(IrpfDataControllerListener.class.getName());
	
	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		completeCurrent();
		completeHandicap();
	}

	@Override
	public void beforeBeanUpdated(ControllerEvent event)
	throws ControllerListenerException {
		completeHandicap();
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
	
	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		IrpfDataController controller = (IrpfDataController) getController();
		IrpfData data = ((IrpfData) getController().getTo());
		if(data.getDisabilityLevel()==DisabilityLevel.GT_EQ_33_LT_65_DEPENDENCE){
			controller.setDisabilityLevel(DisabilityLevel.GT_EQ_33_LT_65);
		}
		filterIrpfRegularization();
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
		((IrpfData) getController().getTo()).setIssueDate(new Date());
	}
	
	private void completeHandicap() {
		IrpfDataController controller = (IrpfDataController) getController();
		IrpfData data = ((IrpfData) getController().getTo());
		data.setDisabilityLevel(controller.getDisabilityLevel());
		if(controller.getDisabilityLevel()==DisabilityLevel.GT_EQ_33_LT_65 && data.isDependence()){
			data.setDisabilityLevel(DisabilityLevel.GT_EQ_33_LT_65_DEPENDENCE);
		}
		
	}
	
	private void filterIrpfRegularization() {
		LinesController regController = (LinesController) FormUtil.getController(IPayrollConstants.IRPF_REGULARIZATION_CONTROLLER_NAME);
		IrpfData data = ((IrpfData) getController().getTo());
		if(data!=null){
			try {
				regController.initializeModel();
				regController.getCriteria().addGreaterThanOrEqualExpression(
						regController.getFieldName(
								IPayrollAlias.IRPF_REGULARIZATION_EFFECTIVE_DATE), data.getStartDate());
				if(data.getEndDate()!=null){
					regController.getCriteria().addLessThanOrEqualExpression(
							regController.getFieldName(
									IPayrollAlias.IRPF_REGULARIZATION_EFFECTIVE_DATE), data.getStartDate());
				}
				regController.getCriteria().addOrder(regController.getFieldName(
						IPayrollAlias.IRPF_REGULARIZATION_EFFECTIVE_DATE));
				regController.onSearch(null);
				if(regController.getRowCount()>0){
					regController.getModel().setRowIndex(0);
					regController.onSelect(null);
				} else {
					regController.onReset(null);
				}
			} catch (ManagerBeanException e) {
				String msg = "Error al filtrar los datos de regularizacion";
				LOGGER.error(msg);
			}
		}
	}
		
}
