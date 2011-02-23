package com.esferalia.aon.ui.payroll.controller;


import java.io.Serializable;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.ajax4jsf.model.DataComponentState;
import org.apache.commons.lang.ObjectUtils;
import org.richfaces.component.UITree;
import org.richfaces.event.NodeSelectedEvent;
import org.richfaces.model.TreeNode;
import org.richfaces.model.TreeNodeImpl;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.Agreement;
import com.esferalia.aon.payroll.AgreementLevel;

public class AgreementTree {

	private TreeNode<AgreementTreeData> rootNode;
	private AgreementTreeData currentNode;
	private TreeNode<AgreementTreeData> currentTreeNode;
	private DataComponentState state;
	private ITransferObject added;
	private ITransferObject removed;

	public TreeNode<AgreementTreeData> getRootNode() {
		return rootNode;
	}
	public void setRootNode(TreeNode<AgreementTreeData> rootNode) {
		this.rootNode = rootNode;
	}
	
	public AgreementTreeData getCurrentNode() {
		return currentNode;
	}
	public void setCurrentNode(AgreementTreeData currentNode) {
		this.currentNode = currentNode;
	}
	
	public ITransferObject getAdded() {
		return added;
	}
	public void setAdded(ITransferObject added) {
		this.added = added;
	}

	public ITransferObject getRemoved() {
		return removed;
	}
	public void setRemoved(ITransferObject removed) {
		this.removed = removed;
	}

	public TreeNode<AgreementTreeData> getCurrentTreeNode() {
		return currentTreeNode;
	}
	public void setCurrentTreeNode(TreeNode<AgreementTreeData> currentTreeNode) {
		this.currentTreeNode = currentTreeNode;
	}

	public void onSelectAgreement(ActionEvent event) {
			// NADA DE MOMENTO
	}

	@SuppressWarnings("unchecked")
	public void onSelectAgreementLevel(ActionEvent event) {
		try {
			Serializable selectedID = getCurrentNode().getId();
			IController controller = FormUtil.getController(IPayrollConstants.AGREEMENT_LEVEL_CONTROLLER_NAME);
			List<ITransferObject> list = (List<ITransferObject>) controller.getModel().getWrappedData();
			int i = 0;
			for (ITransferObject to : list ) {
				AgreementLevel a = (AgreementLevel) to;
				if (a.getId().equals(selectedID)) {
					break;
				}
				i++;
			}
			controller.getModel().setRowIndex(i);
			controller.onSelect(null);
		} catch (ManagerBeanException e) {
			String msg = "Imposible cargar el convenio. [" + e.getMessage() + "]";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		}
	}

	public void loadTree() throws ManagerBeanException {
		setRootNode( new TreeNodeImpl<AgreementTreeData>());
		IController controller = FormUtil.getController(IPayrollConstants.AGREEMENT_CONTROLLER_NAME);
		Agreement a = (Agreement) controller.getTo();
		AgreementTreeData atd = getTreeData(a);	
		TreeNodeImpl<AgreementTreeData> node = new TreeNodeImpl<AgreementTreeData>();
		node.setData(atd);
		loadAgreementLevels(node,a);
		getRootNode().addChild( atd.getType().toString() + atd.getId(), node );
		setCurrentNode(atd);
		setCurrentTreeNode(node);
	}

	@SuppressWarnings("unchecked")
	private void loadAgreementLevels(TreeNodeImpl<AgreementTreeData> parent, Agreement agreement) throws ManagerBeanException {
		IController controller = FormUtil.getController(IPayrollConstants.AGREEMENT_LEVEL_CONTROLLER_NAME);
		List<ITransferObject> list = (List<ITransferObject>) controller.getModel().getWrappedData();
		for (ITransferObject to : list ) {
			AgreementLevel a = (AgreementLevel) to;
			AgreementTreeData atd = getTreeData(a);	
			TreeNodeImpl<AgreementTreeData> node = new TreeNodeImpl<AgreementTreeData>();
			node.setData(atd);
			parent.addChild( atd.getType().toString() + atd.getId(), node );
		}
	}

	public AgreementTreeData getTreeData(AgreementLevel a) {
		return new AgreementTreeData( a.getId(), a.getDescription(), AgreementTreeType.AGREEMENT_LEVEL);
	}
	public AgreementTreeData getTreeData( Agreement a ) {
		return new AgreementTreeData( a.getId(), a.getDescription(), AgreementTreeType.AGREEMENT);
	}

	public Boolean adviseNodeSelected(UITree tree) {
		boolean selected = false;
		if ( tree.isRowAvailable() ) {
			AgreementTreeData etd = (AgreementTreeData) tree.getRowData();
			selected = ObjectUtils.equals(etd, getCurrentNode());
		}
		return selected;
	}	
	
	public Boolean adviseNodeOpened(UITree tree) {
		boolean selected = false;
		if ( tree.isRowAvailable() ) {
			AgreementTreeData parentData = getCurrentTreeNode().getParent().getData();
			AgreementTreeData etd = (AgreementTreeData) tree.getRowData();
			selected = ObjectUtils.equals(etd, getCurrentNode()) || ObjectUtils.equals(etd, parentData);
		}
		return selected;
	}		

	@SuppressWarnings("unchecked")
	public void processSelection(NodeSelectedEvent event) {
		UITree tree = (UITree) event.getComponent();
		currentTreeNode = tree.getModelTreeNode();
		AgreementTreeData atd = currentTreeNode.getData();
		setCurrentNode( atd );
		if (isAgreementSelected()) {
			IController controller = FormUtil.getController(IPayrollConstants.AGREEMENT_LEVEL_CONTROLLER_NAME);
			if (controller.getTo() != null) {
				controller.onCancel(null);
			}
		}
	}
	
	public DataComponentState getState() {
		return state;
	}
	public void setState(DataComponentState state) {
		this.state = state;
	}

	public boolean isAgreementSelected(){
		return (getCurrentNode() != null && getCurrentNode().getType() == AgreementTreeType.AGREEMENT);
	}
	public boolean isAgreementLevelSelected(){
		return (getCurrentNode() != null && getCurrentNode().getType() == AgreementTreeType.AGREEMENT_LEVEL);
	}
	public void select(AgreementLevel al) {
		Serializable id  = AgreementTreeType.AGREEMENT_LEVEL.toString() + al.getId();
		TreeNode<AgreementTreeData> node = getCurrentTreeNode().getChild(id);
		if (node != null) {
			setCurrentTreeNode(node);
			setCurrentNode(node.getData());
		}
	}

}
