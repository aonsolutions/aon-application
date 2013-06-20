package com.esferalia.aon.ui.payroll.event.agreement;

import javax.faces.event.AbortProcessingException;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.AgreementLevel;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;
import com.esferalia.aon.ui.payroll.controller.agreement.AgreementTree;

public class AgreementLevelTreeControllerListener extends ControllerAdapter{

	
	@Override
	public void afterModelInitialized(ControllerEvent event) throws ControllerListenerException {
		loadTree();
		if (event.getController().getTo() != null) {
			AgreementLevel al = (AgreementLevel) event.getController().getTo();
			AgreementTree tree = (AgreementTree) AonUtil.getRegisteredBean(IPayrollConstants.AGREEMENT_TREE_CONTROLLER_NAME);
			tree.select(al);
		}
	}
	
	@Override
	public void afterBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		loadTree();
	}
	
	@Override
	public void afterBeanCanceled(ControllerEvent event)
			throws ControllerListenerException {
		loadTree();
	}
	
	@Override
	public void afterBeanRemoved(ControllerEvent event)
			throws ControllerListenerException {
		loadTree();
	}
	
	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		reloadLevelNode();
	}
	
	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		AgreementLevel al = (AgreementLevel) event.getController().getTo();
		AgreementTree tree = (AgreementTree) AonUtil.getRegisteredBean(IPayrollConstants.AGREEMENT_TREE_CONTROLLER_NAME);
		tree.select(al);
	}
	
	private void loadTree() {
		try {
			AgreementTree tree = (AgreementTree) AonUtil.getRegisteredBean(IPayrollConstants.AGREEMENT_TREE_CONTROLLER_NAME);
			tree.loadTree();
		} catch (ManagerBeanException e) {
			String msg = "Imposible cargar el convenio. [" + e.getMessage() + "]";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		}
	}
	
	private void reloadLevelNode() {
		try {
			AgreementTree tree = (AgreementTree) AonUtil.getRegisteredBean(IPayrollConstants.AGREEMENT_TREE_CONTROLLER_NAME);
			tree.reloadLevelNode();
		} catch (ManagerBeanException e) {
			String msg = "Imposible cargar el convenio. [" + e.getMessage() + "]";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		}
	}
	
}
