package com.code.aon.ui.employee.event;

import com.code.aon.company.Enterprise;
import com.code.aon.employee.Contract;
import com.code.aon.ui.company.controller.EnterpriseTree;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.employee.controller.ContractController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;

/**
 * Listener added to the CompanyController
 * 
 */
public class ContractControllerListener extends ControllerAdapter {

	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		ContractController controller = (ContractController) event.getController();
		controller.setEnterprise(new Enterprise());
	}

	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		ContractController controller = (ContractController) event.getController();
		Contract contract = (Contract) controller.getTo();
		controller.setEnterprise(contract.getWorkPlace().getEnterprise());
		EnterpriseTree tree = getTreeController();
		tree.setCurrentNode(tree.getTreeData(contract));	
	}
	
	@Override
	public void afterBeanCanceled(ControllerEvent event)
			throws ControllerListenerException {
		EnterpriseTree tree = getTreeController();
		Contract contract = (Contract) event.getController().getTo();
		tree.setCurrentNode(tree.getTreeData(contract.getWorkPlace()));
	}	

	private EnterpriseTree getTreeController() {
		return (EnterpriseTree) AonUtil.getRegisteredBean(ICompanyConstants.ENTERPRISE_TREE_CONTROLLER_NAME);
	}	
}
