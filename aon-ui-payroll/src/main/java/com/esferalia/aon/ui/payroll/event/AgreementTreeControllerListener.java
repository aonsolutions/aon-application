package com.esferalia.aon.ui.payroll.event;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.Agreement;
import com.esferalia.aon.ui.payroll.controller.AgreementTree;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;

public class AgreementTreeControllerListener extends ControllerAdapter{
	
	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		AgreementTree tree = (AgreementTree) AonUtil.getRegisteredBean(IPayrollConstants.AGREEMENT_TREE_CONTROLLER_NAME);
		ITransferObject to = event.getController().getTo();
		tree.getCurrentNode().setLabel(((Agreement) to).getDescription() );
	}

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		AgreementTree tree = (AgreementTree) AonUtil.getRegisteredBean(IPayrollConstants.AGREEMENT_TREE_CONTROLLER_NAME);
		try {
			tree.setCurrentNode(null);
			tree.setCurrentTreeNode(null);
			tree.loadTree();
		} catch (ManagerBeanException e) {
			String msg = "Imposible cargar el árbol de convenios. [" + e.getMessage() + "]";
			AonUtil.addErrorMessage(msg);
			throw new ControllerListenerException(msg,e);
		}
	}

}
