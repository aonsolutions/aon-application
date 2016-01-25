package com.esferalia.aon.gwt.fiscal.client.tree.node;

import com.esferalia.aon.gwt.fiscal.client.tree.content.EnterpriseYear;
import com.esferalia.aon.occam.api.model.Enterprise;
import com.esferalia.aon.occam.api.model.fiscal.FiscalActivity;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;

public abstract class TreeNodeTypes<T> {

	public static TreeNodeTypes<Enterprise> ENTERPRISE = new TreeNodeTypes<Enterprise>() {
		@Override
		public TreeNode<Enterprise> getInstance() {
			return new EnterpriseTreeNode();
		}
	};

	public static TreeNodeTypes<Enterprise> ENTERPRISE_DATA = new TreeNodeTypes<Enterprise>() {
		@Override
		public TreeNode<Enterprise> getInstance() {
			return new EnterpriseDataTreeNode();
		}
	};

	public static TreeNodeTypes<Enterprise> FISCAL_ACTIVITY_GROUP = new TreeNodeTypes<Enterprise>() {
		@Override
		public TreeNode<Enterprise> getInstance() {
			return new ActivityGroupTreeNode();
		}
	};
	public static TreeNodeTypes<Integer> FISCAL_ACTIVITY_YEAR = new TreeNodeTypes<Integer>() {
		@Override
		public TreeNode<Integer> getInstance() {
			return new ActivityYearTreeNode();
		}
	};
	public static TreeNodeTypes<FiscalActivity> FISCAL_ACTIVITY = new TreeNodeTypes<FiscalActivity>() {
		@Override
		public TreeNode<FiscalActivity> getInstance() {
			return new ActivityTreeNode();
		}
	};
	public static TreeNodeTypes<EnterpriseYear> FISCAL_MODELS = new TreeNodeTypes<EnterpriseYear>() {
		@Override
		public TreeNode<EnterpriseYear> getInstance() {
			return new FiscalModelsTreeNode();
		}
	};
	public static TreeNodeTypes<EnterpriseYear> YEAR = new TreeNodeTypes<EnterpriseYear>() {
		@Override
		public TreeNode<EnterpriseYear> getInstance() {
			return new YearTreeNode();
		}
	};
	public static TreeNodeTypes<FiscalModelType> MODEL = new TreeNodeTypes<FiscalModelType>() {
		@Override
		public TreeNode<FiscalModelType> getInstance() {
			return new ModelTreeNode();
		}
	};
	public static TreeNodeTypes<Mod2002013TreeObject> CORPORATE_TAX_2013 = new TreeNodeTypes<Mod2002013TreeObject>() {
		@Override
		public TreeNode<Mod2002013TreeObject> getInstance() {
			return new Model2002013TreeNode();
		}
	};
	public static TreeNodeTypes<Mod2002014TreeObject> CORPORATE_TAX_2014 = new TreeNodeTypes<Mod2002014TreeObject>() {
		@Override
		public TreeNode<Mod2002014TreeObject> getInstance() {
			return new Model2002014TreeNode();
		}
	};
	
	public abstract TreeNode<T> getInstance();
}
