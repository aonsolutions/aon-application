package com.esferalia.aon.ui.payroll.controller.contract;

import java.util.Date;

import javax.faces.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractEmbargo;
import com.esferalia.aon.payroll.calculator.sql.SQLContractSalaryCalculatorContext;
import com.esferalia.aon.salary.calculator.ISalaryCalculatorContext;

public class ContractEmbargoController extends BasicController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ContractEmbargoController.class.getName());

	private boolean modalPanelVisible;
	
	public boolean isModalPanelVisible() {
		return modalPanelVisible;
	}
	public void setModalPanelVisible(boolean modalPanelVisible) {
		this.modalPanelVisible = modalPanelVisible;
	}

	public void onEdit(ActionEvent event) {
		super.onSelect(event);
		reset(true);
	}

	public void onReset(ActionEvent event) {
		super.onReset(event);
		reset(true);
	}
	
	public void onSave(ActionEvent event) {
		IController master = FormUtil.getController("contract");
		Contract contract = (Contract) master.getTo();
		ContractEmbargo ce  = (ContractEmbargo) getTo();
		ce.setContract(contract);
		super.onAccept(event);
		reset(false);
	}

	@Override
	public void onCancel(ActionEvent event) {
		super.onCancel(event);
		reset(false);
	}
	
	public void reset(boolean panelVisible) {
		setModalPanelVisible(panelVisible);
	}
	
	private void searchEmbargo(){
		//TODO buscar lo relacionado con los embargos: saldado, pendiente, estimacion fecha final
	}
	
	public double getEmbargedAmount(){
		return 0.0;
	}
	
	public double getPendingAmount(){
		return 0.0;
	}
	
	public Date getEstimatedEndDate(){
		return null;
	}
}
