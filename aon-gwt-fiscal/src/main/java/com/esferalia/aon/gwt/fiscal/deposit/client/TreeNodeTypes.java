package com.esferalia.aon.gwt.fiscal.deposit.client;

import com.esferalia.aon.occam.api.model.Enterprise;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2Deposit;

public abstract class TreeNodeTypes<T> {

	public static TreeNodeTypes<Enterprise> ENTERPRISE = new TreeNodeTypes<Enterprise>() {
		@Override
		public TreeNode<Enterprise> getInstance() {
			return new EnterpriseTreeNode();
		}
	};

	public static TreeNodeTypes<Integer> YEAR = new TreeNodeTypes<Integer>() {
		@Override
		public TreeNode<Integer> getInstance() {
			return new YearTreeNode();
		}
	};

	public static TreeNodeTypes<D2Deposit> DIGITAL_DEPOSIT = new TreeNodeTypes<D2Deposit>() {
		@Override
		public TreeNode<D2Deposit> getInstance() {
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
