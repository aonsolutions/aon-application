package com.esferalia.aon.gwt.fiscal.client.tree;

import com.esferalia.aon.occam.api.model.Enterprise;
import com.esferalia.aon.occam.api.model.fiscal.FiscalActivity;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;
import com.esferalia.aon.occam.api.model.fiscal.Mod131;

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
	public static TreeNodeTypes<Enterprise> FISCAL_MODEL_YEAR_GROUP = new TreeNodeTypes<Enterprise>() {
		@Override
		TreeNode<Enterprise> getInstance() {
			return new FiscalModelYearGroupTreeNode();
		}
	};
	public static TreeNodeTypes<Integer> FISCAL_MODEL_YEAR = new TreeNodeTypes<Integer>() {
		@Override
		TreeNode<Integer> getInstance() {
			return new FiscalModelYearTreeNode();
		}
	};
	public static TreeNodeTypes<FiscalModelType> FISCAL_MODEL_GROUP = new TreeNodeTypes<FiscalModelType>() {
		@Override
		TreeNode<FiscalModelType> getInstance() {
			return new FiscalModelGroupTreeNode();
		}
	};
	public static TreeNodeTypes<FiscalModel> FISCAL_MODEL = new TreeNodeTypes<FiscalModel>() {
		@Override
		TreeNode<FiscalModel> getInstance() {
			return new FiscalModelTreeNode();
		}
	};
	public static TreeNodeTypes<Mod131> MODEL_131 = new TreeNodeTypes<Mod131>() {
		@Override
		TreeNode<Mod131> getInstance() {
			return new Model131TreeNode();
		}
	};

	abstract TreeNode<T> getInstance();
}
