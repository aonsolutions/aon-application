package com.esferalia.aon.ui.payroll.controller.agreement;


import java.util.List;

import javax.faces.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.form.BasicController;
import com.esferalia.aon.ui.payroll.controller.IVariablesHandler;

public class AgreementController extends BasicController implements IVariablesHandler{

	private static final Logger LOGGER = LoggerFactory.getLogger(AgreementController.class.getName());
	private static final String FORM_TREE_SUFFIX = FORM_SUFFIX + "Tree";
	
	private boolean modalPanelVisible;
	private AgreementVariablesHandler handler;

	private String formAction;
	
	public void setFormAction(String formAction) {
		this.formAction = formAction;
	}

	@Override
	public String formAction() {
		if(isNew()){
			return super.formAction();
		}
		return (formAction != null) ? formAction : getBeanName()+FORM_TREE_SUFFIX;	
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
	public AgreementVariablesHandler getHandler() {
		if (handler == null) {
			handler = new AgreementVariablesHandler(this);
		}
		return handler;
	}
	public void setHandler(AgreementVariablesHandler handler) {
		this.handler = handler;
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
