package com.esferalia.aon.gwt.fiscal.client.tree.node;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.client.tree.DefaultTreeNodeCallBack;
import com.esferalia.aon.gwt.fiscal.client.tree.FiscalTree;
import com.esferalia.aon.gwt.fiscal.client.tree.content.EnterpriseMatrixPanel;
import com.esferalia.aon.gwt.fiscal.client.tree.content.EnterpriseYear;
import com.google.gwt.user.client.ui.HasTreeItems;
import com.google.gwt.user.client.ui.InlineLabel;

public class YearTreeNode extends TreeNode<EnterpriseYear> {


	@Override
	public EnterpriseYear getTreeObject() {
		return (EnterpriseYear) getUserObject();
	}

	@Override
	public void select(final FiscalTree fiscalTree) {
		EnterpriseMatrixPanel widget = new EnterpriseMatrixPanel();
		widget.select(getTreeObject());
		widget.setCallback(new DefaultTreeNodeCallBack<EnterpriseYear>());
		fiscalTree.setContent(widget);
//		VerticalPanel widget = new VerticalPanel( );
//		widget.setStyleName(AON.AON_CSS.aonFiscalTreeList());
//		widget.add(FiscalTree.renderBreadcrumb(fiscalTree, this));
//		for (int i = 0; i < getChildCount() ; i++) {
//			final TreeItem item = getChild(i);
//			InlineLabel label =  new InlineLabel("\u2022 " +getChild(i).getText());
//			label.setStyleName(AON.AON_CSS.aonFiscalTreeItem());
//			label.addClickHandler( new ClickHandler() {
//				
//				@Override
//				public void onClick(ClickEvent event) {
//					item.setState(true);
//					getTree().setSelectedItem(item);
//				}
//			});
//			widget.add(label);
//		}
//		fiscalTree.setContent(widget);
	}

	@Override
	public YearTreeNode render(HasTreeItems parent,final EnterpriseYear enterpriseYear) {
		InlineLabel label = new InlineLabel();
		label.setText(AON.MSG.fiscalYear() + " " + enterpriseYear.getYear().toString());
		label.addStyleName(AON.AON_CSS.aonTreeModel());
		setWidget(label);
		setUserObject(enterpriseYear);
		parent.addItem(this);
		return this;
	}

}
