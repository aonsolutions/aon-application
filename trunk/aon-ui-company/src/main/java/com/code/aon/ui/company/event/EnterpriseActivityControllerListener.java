package com.code.aon.ui.company.event;

import com.code.aon.company.EnterpriseActivity;
import com.code.aon.company.WorkActivity;
import com.code.aon.ui.company.controller.EnterpriseTree;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;

/**
 * Listener added to the WorkAcitivityController
 * 
 */
public class EnterpriseActivityControllerListener extends ControllerAdapter {

	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		EnterpriseActivity enterpriseActivity = (EnterpriseActivity) event.getController().getTo();
		EnterpriseTree tree = getTreeController();
		tree.setCurrentNode(tree.getTreeData(enterpriseActivity));
	}
	
	@Override
	public void afterBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		getTreeController().loadTree();
	}

	@Override
	public void afterBeanCanceled(ControllerEvent event)
			throws ControllerListenerException {
		EnterpriseTree tree = getTreeController();
		tree.setCurrentNode(tree.getEnterpriseNode().getData());
	}

	@Override
	public void afterBeanRemoved(ControllerEvent event)
			throws ControllerListenerException {
		getTreeController().loadTree();
	}

	private EnterpriseTree getTreeController() {
		return (EnterpriseTree) AonUtil.getRegisteredBean(ICompanyConstants.ENTERPRISE_TREE_CONTROLLER_NAME);
	}
	
}
