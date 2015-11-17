package com.esferalia.aon.gwt.template.client.marketplace.tree;

import com.esferalia.aon.gwt.template.shared.Ecommerce;


public abstract class TreeNodeTypes<T> {

	public static TreeNodeTypes<Ecommerce> ECOMMERCE = new TreeNodeTypes<Ecommerce>() {
		@Override
		public TreeNode<Ecommerce> getInstance() {
			return new EcommerceTreeNode();
		}
	};
	
	public static TreeNodeTypes<Ecommerce> ORDERS = new TreeNodeTypes<Ecommerce>() {
		@Override
		public TreeNode<Ecommerce> getInstance() {
			return new OrdersTreeNode();
		}
	};
	
	public static TreeNodeTypes<Integer> PRODUCTS = new TreeNodeTypes<Integer>() {
		@Override
		public TreeNode<Integer> getInstance() {
			return new ProductsTreeNode();
		}
	};
	
	public static TreeNodeTypes<Integer> PRODUCT_TEMPLATES = new TreeNodeTypes<Integer>() {
		@Override
		public TreeNode<Integer> getInstance() {
			return new ProductTemplatesTreeNode();
		}
	};
	
	public abstract TreeNode<T> getInstance();
}
