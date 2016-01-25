package com.esferalia.aon.gwt.fiscal.deposit.client;

import com.esferalia.aon.occam.api.model.Enterprise;

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

	public static TreeNodeTypes<EnterpriseYear> YEAR = new TreeNodeTypes<EnterpriseYear>() {
		@Override
		public TreeNode<EnterpriseYear> getInstance() {
			return new YearTreeNode();
		}
	};

	public static TreeNodeTypes<D2DepositTreeObject> DIGITAL_DEPOSIT = new TreeNodeTypes<D2DepositTreeObject>() {
		@Override
		public TreeNode<D2DepositTreeObject> getInstance() {
			return new DigitalDepositTreeNode();
		}
	};
	
	public static TreeNodeTypes<Integer> DIGITAL_DEPOSIT_FREETEXT = new TreeNodeTypes<Integer>() {
		@Override
		public TreeNode<Integer> getInstance() {
			return new DigitalDepositFreeTextTreeNode();
		}
	};
	
	public abstract TreeNode<T> getInstance();
}
