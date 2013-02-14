package com.esferalia.aon.ui.payroll.controller.agreement;

import java.util.List;

import javax.faces.event.ActionEvent;

import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.form.LinesController;
import com.esferalia.aon.ui.payroll.controller.IVariablesHandler;

public class AgreementLevelController extends LinesController implements IVariablesHandler{
	
	private boolean modalPanelVisible;
	
	private AgreementLevelVariableHandler handler;
	
	public AgreementLevelVariableHandler getHandler() {
		if(handler==null){
			handler = new AgreementLevelVariableHandler(this);
		}
		return handler;
	}
	public void setHandler(AgreementLevelVariableHandler handler) {
		this.handler = handler;
	}
	public boolean isModalPanelVisible() {
		return modalPanelVisible;
	}
	public void setModalPanelVisible(boolean modalPanelVisible) {
		this.modalPanelVisible = modalPanelVisible;
	}
	
	
	public void onShowVariables( ActionEvent event ) {
		this.initializeVariables(event);
	}
	
	@Override
	public void onSelect(ActionEvent event) {
		super.onSelect(event);
		initializeVariables(event);
	}
	
	public void reloadData( ActionEvent event ) {
		initializeVariables(event);
	}
	
	
	@Override
	public List<?> expressionContext(Object suggest) {
		return getHandler().expressionContext(suggest);
	}
	@Override
	public IManagerBean getVariableManagerBean() throws ManagerBeanException {
		return getHandler().getVariableManagerBean();
	}
	@Override
	public void initializeVariables(ActionEvent event) {
		getHandler().initializeVariables(event);
	}
	@Override
	public void resetVariable() {
		getHandler().resetVariable();
	}
	
	
}
