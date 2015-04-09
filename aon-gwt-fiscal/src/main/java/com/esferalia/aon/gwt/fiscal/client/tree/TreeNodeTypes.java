package com.esferalia.aon.gwt.fiscal.client.tree;

import com.esferalia.aon.occam.api.model.Enterprise;
import com.esferalia.aon.occam.api.model.fiscal.FiscalActivity;
import com.esferalia.aon.occam.api.model.fiscal.Mod131;
import com.esferalia.aon.occam.api.model.fiscal.Mod202;

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
	public static TreeNodeTypes<Enterprise> FISCAL_MODELS = new TreeNodeTypes<Enterprise>() {
		@Override
		TreeNode<Enterprise> getInstance() {
			return new FiscalModelsTreeNode();
		}
	};
	public static TreeNodeTypes<Mod131> MODEL_131 = new TreeNodeTypes<Mod131>() {
		@Override
		TreeNode<Mod131> getInstance() {
			return new Model131TreeNode();
		}
	};
	public static TreeNodeTypes<Mod202> MODEL_202 = new TreeNodeTypes<Mod202>() {
		@Override
		TreeNode<Mod202> getInstance() {
			return new Model202TreeNode();
		}
	};

	abstract TreeNode<T> getInstance();
}
