package com.code.aon.ui.department.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.common.tree.event.TreeSelectionEvent;
import com.code.aon.common.tree.event.TreeSelectionListener;
import com.code.aon.common.tree.jsf.AbstractTreeModel;
import com.code.aon.common.tree.jsf.TreeDepartment;
import com.code.aon.department.Department;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.menu.jsf.MenuEvent;
import com.code.aon.ui.util.AonUtil;

public class DepartmentController extends BasicController implements TreeSelectionListener {
	
	public DepartmentController() {
		TreeDepartment tree = (TreeDepartment)AonUtil.getRegisteredBean("tree");
		tree.getTreeModel().addTreeSelectionListener(this);
	}
	
	@SuppressWarnings("unused")
	public void onReset(MenuEvent event){
		init();
	}
	
	@Override
	public void onCancel(ActionEvent event) {
		init();
	}
	
	private void init(){
		super.onReset(null);
		this.setNew(false);
		TreeDepartment tree = (TreeDepartment)AonUtil.getRegisteredBean("tree");
		tree.getTreeModel().setSelectedNode(tree.getTreeModel().getRoot().getIdentifier());
	}

	public void valueChanged(TreeSelectionEvent event) {
		AbstractTreeModel treeModel =(AbstractTreeModel)event.getSource();
		if(!treeModel.getSelectedNode().equals(treeModel.getRoot())){
			Department selectedDepartment = (Department)treeModel.getSelectedNode().getUserObject();
			if(isNew()){
				((Department)this.getTo()).setParent(selectedDepartment);
			}else{
				((Department)this.getTo()).setId(selectedDepartment.getId());
				((Department)this.getTo()).setDescription(selectedDepartment.getDescription());
				((Department)this.getTo()).setParent(selectedDepartment.getParent());
			}
		}else{
			if(isNew()){
				((Department)this.getTo()).getParent().setDescription(treeModel.getSelectedNode().getDescription());
			}else{
				((Department)this.getTo()).setDescription(treeModel.getSelectedNode().getDescription());
			}
		}
	}
}
