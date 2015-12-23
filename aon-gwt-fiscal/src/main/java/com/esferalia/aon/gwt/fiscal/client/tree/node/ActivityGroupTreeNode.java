package com.esferalia.aon.gwt.fiscal.client.tree.node;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.client.tree.FiscalTree;
import com.esferalia.aon.occam.api.model.Enterprise;
import com.esferalia.aon.occam.api.model.fiscal.FiscalActivity;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasTreeItems;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.TreeItem;

public class ActivityGroupTreeNode extends TreeNode<Enterprise> {

	
	@Override
	public Enterprise getTreeObject() {
		return (Enterprise) getUserObject();
	}

	@Override
	public void select(final FiscalTree fiscalTree) {
		fiscalTree.renderGenericContent(this);
	}
	
	@Override
	public ActivityGroupTreeNode render(HasTreeItems parent
			,Enterprise enterprise) {
    	InlineLabel label = new InlineLabel();
    	label.setText(AON.MSG.moduleActivities());
    	label.addStyleName(AON.AON_CSS.aonIconModules());
    	label.addStyleName(AON.AON_CSS.aonTreeIconNode() );
    	setWidget(label);
    	setUserObject(enterprise);
    	parent.addItem(this);
    	FiscalTree.FISCAL_SERVICE.getFiscalActivities(FiscalTree.getCurrentDomainName()
    		, enterprise.getDomain(), new AsyncCallback<LinkedList<FiscalActivity>>() {
			
			@Override
			public void onSuccess(LinkedList<FiscalActivity> result) {
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
								.render(ActivityGroupTreeNode.this, fa.getYear());
						currentYearRendered = currentYearRendered || (fa.getYear() == FiscalTree.CURRENT_YEAR);
					}
					TreeNodeTypes.FISCAL_ACTIVITY
						.getInstance()
						.render(yearNode,fa);
					yearNode.setState((fa.getYear() == FiscalTree.CURRENT_YEAR));
				} 
				if (!currentYearRendered) {
					TreeNodeTypes.FISCAL_ACTIVITY_YEAR
						.getInstance()
						.render(ActivityGroupTreeNode.this, FiscalTree.CURRENT_YEAR);
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
