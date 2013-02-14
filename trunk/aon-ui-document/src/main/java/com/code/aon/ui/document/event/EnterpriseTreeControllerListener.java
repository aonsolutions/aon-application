package com.code.aon.ui.document.event;

import static com.code.aon.ui.document.controller.IDocumentConstants.ENTERPRISE_DOCUMENT_SEARCH;
import static com.code.aon.ui.document.controller.IDocumentConstants.ENTERPRISE_TREE_CONTROLLER_NAME;

import com.code.aon.ui.company.controller.EnterpriseController;
import com.code.aon.ui.document.tree.EnterpriseTree;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;

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
			EnterpriseDocumentSearchListener search = (EnterpriseDocumentSearchListener) AonUtil.getRegisteredBean(ENTERPRISE_DOCUMENT_SEARCH);
			search.setShowOpened(Boolean.FALSE.toString());
			search.setEnterprise(controller.getEnterprise());
			EnterpriseTree tree = (EnterpriseTree) AonUtil.getRegisteredBean(ENTERPRISE_TREE_CONTROLLER_NAME);
			tree.loadTree();			
			
		}
	}
	
}