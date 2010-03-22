package com.code.aon.ui.department.event;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.tree.IEmployeeConstants;
import com.code.aon.common.tree.ITreeNode;
import com.code.aon.common.tree.event.TreeSelectionListener;
import com.code.aon.common.tree.jsf.AbstractTreeModel;
import com.code.aon.common.tree.jsf.DefaultMutableTreeNode;
import com.code.aon.common.tree.jsf.TreeDepartment;
import com.code.aon.department.Department;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;

public class DepartmentControllerListener extends ControllerAdapter {
	
	private static final Logger LOGGER = Logger.getLogger(DepartmentControllerListener.class.getName());
	
	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		if(event.getController().isNew()){
			TreeDepartment tree = (TreeDepartment)AonUtil.getRegisteredBean("tree");
			AbstractTreeModel treeModel = tree.getTreeModel();
			if(!treeModel.getSelectedNode().equals(treeModel.getRoot())){
				Department selectedDepartment = (Department)treeModel.getSelectedNode().getUserObject();
				((Department)event.getController().getTo()).setParent(selectedDepartment);
			}else{
				((Department)event.getController().getTo()).setParent(null);
			}
		}
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		TreeDepartment tree = (TreeDepartment)AonUtil.getRegisteredBean("tree");
		AbstractTreeModel treeModel = tree.getTreeModel();
		String id = treeModel.getSelectedNodeId();
		int i = treeModel.getSelectedNode().getChildCount();
		Department department = (Department)event.getController().getTo();
		Department newDepartment = new Department(department.getId(), department.getParent(), department.getDescription());
		ITreeNode newNode = new DefaultMutableTreeNode(newDepartment, IEmployeeConstants.BRANCH_TYPE, department.getDescription(), id + ":" + i, false, IEmployeeConstants.DEPARTMENT_ICON_OPEN, IEmployeeConstants.DEPARTMENT_ICON_CLOSED );
		treeModel.addNode2Map(newNode);
		treeModel.getSelectedNode().getChildren().add(newNode);
		treeModel.setSelectedNode(id + ":" + i);
	}
	
	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		TreeDepartment tree = (TreeDepartment)AonUtil.getRegisteredBean("tree");
		AbstractTreeModel treeModel = tree.getTreeModel();
		treeModel.getSelectedNode().setDescription(((Department)event.getController().getTo()).getDescription());
		((Department)treeModel.getSelectedNode().getUserObject()).setDescription(((Department)event.getController().getTo()).getDescription());
	}
	
	@Override
	public void beforeBeanRemoved(ControllerEvent event) throws ControllerListenerException {
		TreeDepartment tree = (TreeDepartment)AonUtil.getRegisteredBean("tree");
		AbstractTreeModel treeModel = tree.getTreeModel();
		int i = treeModel.getSelectedNode().getChildCount();
		if( i > 0){
			removeChilds(treeModel.getSelectedNode());
		}
	}
	
	@Override
	public void afterBeanRemoved(ControllerEvent event) throws ControllerListenerException {
		TreeDepartment tree = (TreeDepartment)AonUtil.getRegisteredBean("tree");
		AbstractTreeModel treeModel = tree.getTreeModel();
		String parentId = treeModel.getSelectedNodeId().substring(0, treeModel.getSelectedNodeId().lastIndexOf(":"));
		int i = treeModel.getSelectedNode().getChildCount();
		if(i > 0){
			removeChildsFromTree(treeModel.getSelectedNode(), treeModel);
		}
		treeModel.removeNodeFromMap(treeModel.getSelectedNodeId());
		TreeSelectionListener[] l = treeModel.getTreeSelectionListeners();
		tree.init();
		treeModel = tree.getTreeModel();
        for (int j = 0; j < l.length; j++) {
			treeModel.addTreeSelectionListener( l[j] );
		}
		treeModel.setSelectedNode(parentId);
	}
	
	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		TreeDepartment tree = (TreeDepartment)AonUtil.getRegisteredBean("tree");
		AbstractTreeModel treeModel = tree.getTreeModel();
		if(treeModel.getSelectedNode().equals(treeModel.getRoot())){
			((Department)event.getController().getTo()).getParent().setDescription(treeModel.getSelectedNode().getDescription());
		}else{
			((Department)event.getController().getTo()).setParent((Department)treeModel.getSelectedNode().getUserObject());
		}
	}

	private void removeChilds(ITreeNode selectedNode) {
		try {
			IManagerBean departmeBean = BeanManager.getManagerBean(Department.class);
			Iterator iter = selectedNode.getChildren().iterator();
			while(iter.hasNext()){
				ITreeNode node = (ITreeNode)iter.next();
				if(node.getChildCount() > 0){
					removeChilds(node);
					departmeBean.remove((Department)node.getUserObject());
				}else{
					departmeBean.remove((Department)node.getUserObject());
				}
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "error deleting childs for node= " + selectedNode.getIdentifier(), e);
		}
	}
	
	private void removeChildsFromTree(ITreeNode selectedNode, AbstractTreeModel treeModel) {
		Iterator iter = selectedNode.getChildren().iterator();
		while(iter.hasNext()){
			ITreeNode node = (ITreeNode)iter.next();
			if(node.getChildCount() > 0){
				removeChildsFromTree(node, treeModel);
			}else{
				treeModel.removeNodeFromMap(node.getIdentifier());
			}
		}
	}
}
