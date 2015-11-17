package com.esferalia.aon.gwt.template.client.marketplace;

import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.template.client.marketplace.tree.TreeNode;
import com.esferalia.aon.gwt.template.client.marketplace.tree.TreeNodeTypes;
import com.esferalia.aon.gwt.template.shared.Ecommerce;
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
	
	Integer domainId;
	String login;
	
	public Marketplace() {
		splitLayoutPanel = new SplitLayoutPanel();
		sidebar = new ScrollPanel();
		tree = new Tree();
		content = new SimpleLayoutPanel();
	}
	
	public Marketplace(Integer domainId){
		setDomainId(domainId);
		splitLayoutPanel = new SplitLayoutPanel();
		sidebar = new ScrollPanel();
		tree = new Tree();
		content = new SimpleLayoutPanel();
		
		Widget ui = binder.createAndBindUi(this);
		RootLayoutPanel.get("rootPanel").add(ui);
		load();
	}
	
	public Marketplace(Integer domainId, String login){
		setDomainId(domainId);
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
		TreeNodeTypes.ORDERS.getInstance().render(amazon, Ecommerce.AMAZON);
		//TreeNodeTypes.PRODUCTS.getInstance().render(amazon, getDomainId());

		//TreeNode<Ecommerce> ebay = TreeNodeTypes.ECOMMERCE.getInstance().render(tree, Ecommerce.EBAY);
		//TreeNodeTypes.ORDERS.getInstance().render(ebay, Ecommerce.EBAY);
		//TreeNodeTypes.PRODUCTS.getInstance().render(ebay, getDomainId());
		
		TreeNodeTypes.PRODUCT_TEMPLATES.getInstance().render(tree, getDomainId());
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

	public Integer getDomainId(){
		return domainId;
	}
	
	public void setDomainId(Integer domainId){
		this.domainId = domainId;
	}
	
	public String getLogin(){
		return login;
	}
	public void setLogin(String login){
		this.login = login;
	}
}
