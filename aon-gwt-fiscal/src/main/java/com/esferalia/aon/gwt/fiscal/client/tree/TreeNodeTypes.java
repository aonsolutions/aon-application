package com.esferalia.aon.gwt.fiscal.client.tree;

import com.esferalia.aon.occam.api.model.Enterprise;
import com.esferalia.aon.occam.api.model.fiscal.FiscalActivity;

public abstract class TreeNodeTypes<T> {

	public static TreeNodeTypes<Enterprise> ENTERPRISE = new TreeNodeTypes<Enterprise>() {
		@Override
		TreeNode<Enterprise> getInstance() {
			return new EnterpriseTreeNode();
		}
	};

	public static TreeNodeTypes<Enterprise> ENTERPRISE_DATA = new TreeNodeTypes<Enterprise>() {
		@Override
		TreeNode<Enterprise> getInstance() {
			return new EnterpriseDataTreeNode();
		}
	};

	public static TreeNodeTypes<Enterprise> FISCAL_ACTIVITY_GROUP = new TreeNodeTypes<Enterprise>() {
		@Override
		TreeNode<Enterprise> getInstance() {
			return new ActivityGroupTreeNode();
		}
	};
	public static TreeNodeTypes<Integer> FISCAL_ACTIVITY_YEAR = new TreeNodeTypes<Integer>() {
		@Override
		TreeNode<Integer> getInstance() {
			return new ActivityYearTreeNode();
		}
	};
	public static TreeNodeTypes<FiscalActivity> FISCAL_ACTIVITY = new TreeNodeTypes<FiscalActivity>() {
		@Override
		TreeNode<FiscalActivity> getInstance() {
			return new ActivityTreeNode();
		}
	};

	abstract TreeNode<T> getInstance();
}
