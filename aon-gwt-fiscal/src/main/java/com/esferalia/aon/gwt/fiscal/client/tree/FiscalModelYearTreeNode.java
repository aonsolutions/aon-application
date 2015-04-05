package com.esferalia.aon.gwt.fiscal.client.tree;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HasTreeItems;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;

public class FiscalModelYearTreeNode extends TreeNode<Integer> {

	private VerticalPanel widget;

	@Override
	public Integer getTreeObject() {
		return (Integer) getUserObject();
	}

	@Override
	public void select(final FiscalTree fiscalTree) {
		if (widget ==null) {
			widget = new VerticalPanel( );
			widget.setStyleName(AON.AON_CSS.aonFiscalTreeList());
			widget.add(FiscalTree.renderBreadcrumb(fiscalTree, this));
			for (int i = 0; i < getChildCount() ; i++) {
				final TreeItem item = getChild(i); 
				InlineLabel label =  new InlineLabel(getChild(i).getText());
				label.setStyleName(AON.AON_CSS.aonFiscalTreeItem());
		    	String className = FiscalModelGroupTreeNode.STYLE_MAP.get(item.getUserObject());
		    	if (AonStringUtils.isNotBlank(className)) {
		    		label.addStyleName(className);
		    	} else {
		    		label.addStyleName(AON.AON_CSS.aonIconPointOrange() );
		    	}
				label.addClickHandler( new ClickHandler() {
					
					@Override
					public void onClick(ClickEvent event) {
						item.setState(true);
						fiscalTree.tree.setSelectedItem(item);
					}
				});
				widget.add(label);
			}
		}
		fiscalTree.content.setWidget(widget);
		
	}

	@Override
	public FiscalModelYearTreeNode render(HasTreeItems parent,
			final FiscalTree fiscalTree, final Integer year) {
		InlineLabel label = new InlineLabel();
		label.setText(year.toString());
		label.addStyleName(AON.AON_CSS.aonTreeYear());
		setWidget(label);
		setUserObject(year);
		parent.addItem(this);
		return this;
	}

}
