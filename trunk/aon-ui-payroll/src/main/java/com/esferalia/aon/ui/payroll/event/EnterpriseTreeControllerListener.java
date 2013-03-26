package com.esferalia.aon.ui.payroll.event;

import com.code.aon.ui.company.controller.EnterpriseController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.ui.payroll.controller.EnterpriseTree;

/**
 * Listener added to the EnterpriseController
 * 
 */
public class EnterpriseTreeControllerListener extends ControllerAdapter {

	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		EnterpriseController controller = (EnterpriseController) event.getController();
		if ( controller.isTreeView() ) {
			EnterpriseTree tree = (EnterpriseTree) AonUtil.getRegisteredBean(ICompanyConstants.ENTERPRISE_TREE_CONTROLLER_NAME);
			tree.loadTree();			
		}
	}

	@Override
	public void afterEditSearch(ControllerEvent event)
			throws ControllerListenerException {
		EnterpriseTree tree = (EnterpriseTree) AonUtil.getRegisteredBean(ICompanyConstants.ENTERPRISE_TREE_CONTROLLER_NAME);
		tree.setContract(null);
	}
	
}
