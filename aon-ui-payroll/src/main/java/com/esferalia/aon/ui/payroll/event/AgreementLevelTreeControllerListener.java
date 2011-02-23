package com.esferalia.aon.ui.payroll.event;

import java.io.Serializable;

import javax.faces.event.AbortProcessingException;

import org.richfaces.model.TreeNode;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.AgreementLevel;
import com.esferalia.aon.ui.payroll.controller.AgreementTree;
import com.esferalia.aon.ui.payroll.controller.AgreementTreeData;
import com.esferalia.aon.ui.payroll.controller.AgreementTreeType;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;

public class AgreementLevelTreeControllerListener extends ControllerAdapter{

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
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		AgreementTree tree = (AgreementTree) AonUtil.getRegisteredBean(IPayrollConstants.AGREEMENT_TREE_CONTROLLER_NAME);
		ITransferObject to = event.getController().getTo();
		if (tree.isAgreementSelected()) {
			AgreementLevel al = (AgreementLevel) to;
			Serializable id = AgreementTreeType.AGREEMENT_LEVEL.toString() + al.getId();
			TreeNode<AgreementTreeData> childTreeNode = tree.getCurrentTreeNode().getChild(id);	
			childTreeNode.getData().setLabel(((AgreementLevel) to).getDescription() );
		}
		if (tree.isAgreementLevelSelected()) {
			tree.getCurrentNode().setLabel(((AgreementLevel) to).getDescription() );
		}
	}
	
	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		AgreementLevel al = (AgreementLevel) event.getController().getTo();
		AgreementTree tree = (AgreementTree) AonUtil.getRegisteredBean(IPayrollConstants.AGREEMENT_TREE_CONTROLLER_NAME);
		tree.select(al);
	}
	
}
