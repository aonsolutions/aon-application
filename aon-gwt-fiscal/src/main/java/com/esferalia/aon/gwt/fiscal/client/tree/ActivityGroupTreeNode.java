package com.esferalia.aon.gwt.fiscal.client.tree;

import java.util.ArrayList;

import com.esferalia.aon.occam.api.model.Enterprise;
import com.esferalia.aon.occam.api.model.fiscal.FiscalActivity;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasTreeItems;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ActivityGroupTreeNode extends TreeNode<Enterprise> {

	private VerticalPanel widget;
	
	@Override
	public Enterprise getTreeObject() {
		return (Enterprise) getUserObject();
	}

	@Override
	public void select(final FiscalTree fiscalPanel) {
		if (widget ==null) {
			widget = new VerticalPanel( );
			widget.add(new Label(FiscalTree.MSG.moduleActivities()));
			for (int i = 0; i < getChildCount() ; i++) {
				final TreeItem item = getChild(i); 
				Label label =  new Label(getChild(i).getText());
				label.addClickHandler( new ClickHandler() {
					
					@Override
					public void onClick(ClickEvent event) {
						fiscalPanel.tree.setSelectedItem(item);
					}
				});
				widget.add(label);
			}
		}
		fiscalPanel.content.setWidget(widget);
	}
	
	@Override
	public ActivityGroupTreeNode render(HasTreeItems parent, final FiscalTree fiscalPanel,Enterprise enterprise) {
    	InlineLabel label = new InlineLabel();
    	label.setText(FiscalTree.MSG.moduleActivities());
    	label.addStyleName(FiscalTree.AON_RESOURCES.css().aonIconActivities());
    	label.addStyleName(FiscalTree.AON_RESOURCES.css().aonTreeIconNode() );
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
								.render(ActivityGroupTreeNode.this,fiscalPanel, fa.getYear());
						currentYearRendered = currentYearRendered || (fa.getYear() == FiscalTree.CURRENT_YEAR);
					}
					TreeNodeTypes.FISCAL_ACTIVITY
						.getInstance()
						.render(yearNode,fiscalPanel, fa);
					yearNode.setState(true);
				} 
				if (!currentYearRendered) {
					TreeNodeTypes.FISCAL_ACTIVITY_YEAR
						.getInstance()
						.render(ActivityGroupTreeNode.this,fiscalPanel, FiscalTree.CURRENT_YEAR);
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
