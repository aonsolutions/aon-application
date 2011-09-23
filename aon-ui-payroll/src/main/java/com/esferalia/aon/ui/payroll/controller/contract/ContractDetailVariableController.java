package com.esferalia.aon.ui.payroll.controller.contract;

import java.util.Date;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.ui.form.BasicController;

public abstract class ContractDetailVariableController extends BasicController implements IVariablesHandler {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ContractDetailVariableController.class.getName());
	
	private List<SelectItem> concepts;
	private boolean modalPanelVisible;
	private boolean searchCurrent;
	private Date inactiveDate;
	
	private ContractDetailVariableHandler handler;
	
	public ContractDetailVariableHandler getHandler() {
		if(handler==null){
			handler = new ContractDetailVariableHandler(this);
		}
		return handler;
	}
	public void setHandler(ContractDetailVariableHandler handler) {
		this.handler = handler;
	}
	public Date getInactiveDate() {
		return inactiveDate;
	}
	public void setInactiveDate(Date inactiveDate) {
		this.inactiveDate = inactiveDate;
	}
	public boolean isSearchCurrent() {
		return searchCurrent;
	}
	public void setSearchCurrent(boolean searchCurrent) {
		this.searchCurrent = searchCurrent;
	}
	public boolean isModalPanelVisible() {
		return modalPanelVisible;
	}
	public void setModalPanelVisible(boolean modalPanelVisible) {
		this.modalPanelVisible = modalPanelVisible;
	}

	public void onEdit(ActionEvent event) {
		super.onSelect(event);
		reset(true);
		getHandler().initializeVariables(event);
	}
	
	public void onSave(ActionEvent event) {
		super.onAccept(event);
		reset(false);
	}
	
	@Override
	public void onSearch(ActionEvent event) {
		completeCiteria();
		super.onSearch(event);
	}
	
	@Override
	public void onCancel(ActionEvent event) {
		super.onCancel(event);
		reset(false);
	}

	@Override
	public void onRemove(ActionEvent event) {
		super.onRemove(event);
		reset(false);
	}
	
	@Override
	public void onReset(ActionEvent event) {
		super.onReset(event);
		reset(true);
	}
	
	public void reset(boolean panelVisible) {
		setModalPanelVisible(panelVisible);
		setConcepts(null);
	}
	
	public void onTypeChange(ActionEvent event) {
		setConcepts(null);
	}
	
	public List<SelectItem> getConcepts() {
		if (concepts == null) {
			initialiceConcepts();
		}
		return concepts;
		
	}
	public void setConcepts(List<SelectItem> concepts) {
		this.concepts = concepts;
	}

	protected abstract void initialiceConcepts();
	protected abstract void completeCiteria();
	
	
}
