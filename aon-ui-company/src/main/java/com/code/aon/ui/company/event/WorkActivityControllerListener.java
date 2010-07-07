package com.code.aon.ui.company.event;

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
public class WorkActivityControllerListener extends ControllerAdapter {

	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		WorkActivity workActivity = (WorkActivity) event.getController().getTo();
		EnterpriseTree tree = getTreeController();
		tree.setCurrentNode(tree.getTreeData(workActivity));
	}
	
	@Override
	public void afterBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		getTreeController().loadTree();
	}

	@Override
	public void afterBeanCanceled(ControllerEvent event)
			throws ControllerListenerException {
		WorkActivity workActivity = (WorkActivity) event.getController().getTo();
		EnterpriseTree tree = getTreeController();
		tree.setCurrentNode(tree.getTreeData(workActivity.getWorkPlace()));
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
