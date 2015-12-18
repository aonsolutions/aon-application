package com.esferalia.aon.gwt.template.client.marketplace;

import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.template.client.JsTemplates;
import com.esferalia.aon.gwt.template.client.marketplace.tree.TreeNode;
import com.esferalia.aon.gwt.template.client.marketplace.tree.TreeNodeTypes;
import com.esferalia.aon.gwt.template.shared.Ecommerce;
import com.esferalia.aon.occam.api.model.Domain;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;


public class Marketplace extends Composite {

	final IMarketplaceAsync impl = GWT.create(IMarketplace.class);

	interface Binder extends UiBinder<Widget, Marketplace> {

	}
	
	private static final Binder binder = GWT.create(Binder.class);

	@UiField
	SplitLayoutPanel splitLayoutPanel;
	@UiField
	ScrollPanel sidebar;
	@UiField
	Tree tree;
	@UiField
	SimpleLayoutPanel content;
	
	String login;
	
	public Marketplace(){
		splitLayoutPanel = new SplitLayoutPanel();
		sidebar = new ScrollPanel();
		tree = new Tree();
		content = new SimpleLayoutPanel();
		
		Widget ui = binder.createAndBindUi(this);
		RootLayoutPanel.get("rootPanel").add(ui);
		load();
	}
	
	public Marketplace(String login){
		splitLayoutPanel = new SplitLayoutPanel();
		sidebar = new ScrollPanel();
		tree = new Tree();
		content = new SimpleLayoutPanel();
		setLogin(login);
		Widget ui = binder.createAndBindUi(this);
		RootLayoutPanel.get("rootPanel").add(ui);
		load();
	}
	
	private void load() {
		TreeNode<Ecommerce> amazon = TreeNodeTypes.ECOMMERCE.getInstance().render(tree, Ecommerce.AMAZON);
		amazon.setState(true);
		TreeNode<Ecommerce> amazonOrders = TreeNodeTypes.ORDERS.getInstance().render(amazon, Ecommerce.AMAZON);
		amazonOrders.select(this);
		//TreeNodeTypes.PRODUCTS.getInstance().render(amazon, getDomainId());

		//TreeNode<Ecommerce> ebay = TreeNodeTypes.ECOMMERCE.getInstance().render(tree, Ecommerce.EBAY);
		//TreeNodeTypes.ORDERS.getInstance().render(ebay, Ecommerce.EBAY);
		//TreeNodeTypes.PRODUCTS.getInstance().render(ebay, getDomainId());
		
		TreeNodeTypes.PRODUCT_TEMPLATES.getInstance().render(tree, getDomain().getId());
		
		final TreeNode<Integer> productTemplateValues = TreeNodeTypes.PRODUCT_TEMPLATE_VALUES.getInstance().render(tree, null);
		productTemplateValues.setState(true);
		
	}
	
	public void setContent(Widget widget) {
		this.content.setWidget(widget);
	}
	
	@UiHandler("tree")
	void onTreeSelecction(SelectionEvent<TreeItem> event) {
		TreeNode<?> node = (TreeNode<?>) event.getSelectedItem();
		node.select(this);
		sidebar.scrollToLeft();
	}

	public IMarketplaceAsync getImpl() {
		return impl;
	}

	public String getLogin(){
		return login;
	}
	public void setLogin(String login){
		this.login = login;
	}

	private Domain getDomain() {
		return new Domain().setId(JsTemplates.getCurrentDomain()).setName(JsTemplates.getCurrentDomainName());
	}
	
}
