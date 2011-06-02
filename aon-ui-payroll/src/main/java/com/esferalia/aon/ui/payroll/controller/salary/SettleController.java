package com.esferalia.aon.ui.payroll.controller.salary;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;
import com.esferalia.aon.ui.payroll.controller.contract.ContractController;

public class SettleController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SettleController.class);
	
	private SettleParams params;
	
	public SettleParams getParams() {
		return params;
	}
	public void setParams(SettleParams params) {
		this.params = params;
	}
	
	
	public void onInitialize(ActionEvent event){
		setParams(new SettleParams());
		try {
			IManagerBean bean = BeanManager.getManagerBean(Contract.class);
			getParams().setContract((Contract) bean.createNewTo());
		} catch (ManagerBeanException e) {
			e.printStackTrace();
			String msg = "Imposible lanzar el mantenimiento (" + e.getMessage() +")";
			LOGGER.error(msg);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		}
	}
	
	public void onSelectContract(ActionEvent event){
		ContractController controller = (ContractController) FormUtil.getController(IPayrollConstants.CONTRACT_CONTROLLER);
		controller.onSelect(event);
		getParams().setContract((Contract) controller.getTo());
		getParams().setSeniorityDate(((Contract) controller.getTo()).getSeniorityDate());
	}
	
	public void onContractChanged( LookupChangeEvent event ){
		if (event.getNewValue() != null && !event.getNewValue().equals("")) {
			getParams().setSeniorityDate(((Contract) event.getNewValue()).getSeniorityDate());
		}
		if (event.getNewValue() == null || event.getNewValue().equals("")) {
			onInitialize(null);
		}
	}
	
	public void onDismissCauseChanged(ActionEvent event){
		getParams().setDaysPerYear(getParams().getDismissCause().getCompensationDaysPerYear());
	}
	
	public void onSeniorityDateChanged(ActionEvent event){
		
	}

	public void onGenerate(ActionEvent event){
		AonUtil.addErrorMessage("sin implementar");
	}
}
