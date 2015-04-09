package com.esferalia.aon.gwt.fiscal.client.tree;

import java.util.ArrayList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.occam.api.model.Enterprise;
import com.esferalia.aon.occam.api.model.fiscal.FiscalActivity;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasTreeItems;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ActivityGroupTreeNode extends TreeNode<Enterprise> {

	private VerticalPanel widget;
	
	@Override
	public Enterprise getTreeObject() {
		return (Enterprise) getUserObject();
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
				label.addStyleName(AON.AON_CSS.aonIconModule());
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
		fiscalTree.toolbar.setVisibleCopyButton(false);
		fiscalTree.toolbar.setVisibleDraftButton(false);
		fiscalTree.toolbar.setVisiblePasteButton(false);
    	fiscalTree.toolbar.addListener(this);
		fiscalTree.content.setWidget(widget);
	}
	
	@Override
	public ActivityGroupTreeNode render(HasTreeItems parent,
			final FiscalTree fiscalTree
			,Enterprise enterprise) {
    	InlineLabel label = new InlineLabel();
    	label.setText(AON.MSG.moduleActivities());
    	label.addStyleName(AON.AON_CSS.aonIconModules());
    	label.addStyleName(AON.AON_CSS.aonTreeIconNode() );
    	setWidget(label);
    	setUserObject(enterprise);
    	parent.addItem(this);
    	FiscalTree.FISCAL_SERVICE.getFiscalActivities(FiscalTree.getCurrentDomainName()
    		, enterprise.getDomain(), new AsyncCallback<ArrayList<FiscalActivity>>() {
			
			@Override
			public void onSuccess(ArrayList<FiscalActivity> result) {
				boolean currentYearRendered = false;
				for (FiscalActivity fa : result) {
					TreeItem yearNode = null; 
					for (int i = 0 ; i < getChildCount() ; i++) {
						TreeItem item = getChild(i);
						if (item instanceof ActivityYearTreeNode) {
							ActivityYearTreeNode node = (ActivityYearTreeNode) item;
							Integer year = node.getTreeObject(); 
							if (year.intValue() == fa.getYear().intValue()) {
								yearNode = getChild(i);
								break;
							}
						}
						//Integer year = (Integer);
					}
					if (yearNode == null) {
						yearNode = TreeNodeTypes.FISCAL_ACTIVITY_YEAR
								.getInstance()
								.render(ActivityGroupTreeNode.this, fiscalTree, fa.getYear());
						currentYearRendered = currentYearRendered || (fa.getYear() == FiscalTree.CURRENT_YEAR);
					}
					TreeNodeTypes.FISCAL_ACTIVITY
						.getInstance()
						.render(yearNode,fiscalTree, fa);
					yearNode.setState((fa.getYear() == FiscalTree.CURRENT_YEAR));
				} 
				if (!currentYearRendered) {
					TreeNodeTypes.FISCAL_ACTIVITY_YEAR
						.getInstance()
						.render(ActivityGroupTreeNode.this, fiscalTree, FiscalTree.CURRENT_YEAR);
				}
				setState(true);
			}
			
			@Override
			public void onFailure(Throwable caught) {
			}
		});
		return this;
	}

}
