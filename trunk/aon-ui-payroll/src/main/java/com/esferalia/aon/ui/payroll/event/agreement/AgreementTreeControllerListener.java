package com.esferalia.aon.ui.payroll.event.agreement;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.Agreement;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;
import com.esferalia.aon.ui.payroll.controller.agreement.AgreementLevelController;
import com.esferalia.aon.ui.payroll.controller.agreement.AgreementPaymentController;
import com.esferalia.aon.ui.payroll.controller.agreement.AgreementTree;

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
			updateLinesModel();
		} catch (ManagerBeanException e) {
			String msg = "Imposible cargar el árbol de convenios. [" + e.getMessage() + "]";
			AonUtil.addErrorMessage(msg);
			throw new ControllerListenerException(msg,e);
		}
	}

	private void updateLinesModel() throws ManagerBeanException {
		AgreementPaymentController controller = (AgreementPaymentController) AonUtil.getRegisteredBean(IPayrollConstants.AGREEMENT_PAYMENT_CONTROLLER_NAME);
		controller.initialize();
		AgreementLevelController levelController = (AgreementLevelController) AonUtil.getRegisteredBean(IPayrollConstants.AGREEMENT_LEVEL_CONTROLLER_NAME);
		levelController.clearCriteria();
		levelController.getCriteria().addEqualExpression(levelController.getFieldName(IEntityAlias.AGREEMENT_LEVEL_AGREEMENT_ID), ((Agreement)getController().getTo()).getId());
		levelController.onSearch(null);
	}

}
