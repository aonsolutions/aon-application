package com.code.aon.ui.company.controller;

import org.richfaces.model.TreeNode;
import org.richfaces.model.TreeNodeImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.company.Enterprise;
import com.code.aon.ui.company.util.EnterpriseTreeData;
import com.code.aon.ui.company.util.EnterpriseTreeType;

public class EnterpriseTree {

	private final static Logger LOGGER = LoggerFactory.getLogger(EnterpriseTree.class);
	
	private TreeNode<EnterpriseTreeData> rootNode;
	
	public TreeNode<EnterpriseTreeData> getRootNode() {
		return rootNode;
	}

	public void loadTree( Enterprise enterprise ) {
		rootNode = new TreeNodeImpl<EnterpriseTreeData>();
		TreeNodeImpl<EnterpriseTreeData> enterpriseNode = new TreeNodeImpl<EnterpriseTreeData>();
		EnterpriseTreeData etd = new EnterpriseTreeData(enterprise.getId(), enterprise.getRegistry().getFullName(), EnterpriseTreeType.ENTERPRISE);
		enterpriseNode.setData(etd);
		rootNode.addChild( etd.getType().toString() + etd.getId(), enterpriseNode );
	}

}
