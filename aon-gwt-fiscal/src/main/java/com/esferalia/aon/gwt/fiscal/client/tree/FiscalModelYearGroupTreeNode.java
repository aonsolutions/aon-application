package com.esferalia.aon.gwt.fiscal.client.tree;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.Enterprise;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasTreeItems;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;

public class FiscalModelYearGroupTreeNode extends TreeNode<Enterprise> {
	
	private VerticalPanel widget;

	@Override
	public Enterprise getTreeObject() {
		return (Enterprise) getUserObject();
	}

	@Override
	public void select(final FiscalTree fiscalTree) {
		if (widget ==null) {
			widget = new VerticalPanel( );
			widget.setStyleName(FiscalTree.AON_RESOURCES.css().aonFiscalTreeList());
			Label title = new Label(getText());
			title.setStyleName(FiscalTree.AON_RESOURCES.css().aonFiscalTreeTitle());
			widget.add(title);
			for (int i = 0; i < getChildCount() ; i++) {
				final TreeItem item = getChild(i); 
				InlineLabel label =  new InlineLabel(getChild(i).getText());
				label.setStyleName(FiscalTree.AON_RESOURCES.css().aonFiscalTreeItem());
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
	public TreeNode<Enterprise> render(HasTreeItems parent,
			final FiscalTree fiscalTree, Enterprise enterprise) {
    	InlineLabel label = new InlineLabel();
    	label.setText(FiscalTree.MSG.fiscalModels());
    	label.addStyleName(FiscalTree.AON_RESOURCES.css().aonIconModel());
    	label.addStyleName(FiscalTree.AON_RESOURCES.css().aonTreeIconNode() );
    	setWidget(label);
    	setUserObject(enterprise);
    	parent.addItem(this);
		
    	
    	FiscalTree.FISCAL_SERVICE.getFiscalModels(FiscalTree.getCurrentDomainName()
        		, enterprise.getDomain(), new AsyncCallback<LinkedList<FiscalModel>>() {
    			
    			@Override
    			public void onSuccess(LinkedList<FiscalModel> result) {
    				boolean currentYearRendered = false;
    				for (FiscalModel fm : result) {
    					TreeItem yearNode = null; 
    					for (int i = 0 ; i < getChildCount() ; i++) {
    						TreeItem item = getChild(i);
    						if (item instanceof FiscalModelYearTreeNode) {
    							FiscalModelYearTreeNode node = (FiscalModelYearTreeNode) item;
    							Integer year = node.getTreeObject(); 
    							if (year.intValue() == fm.getYear().intValue()) {
    								yearNode = getChild(i);
    								break;
    							}
    						}
    					}
    					if (yearNode == null) {
    						yearNode = TreeNodeTypes.FISCAL_MODEL_YEAR
    								.getInstance()
    								.render(FiscalModelYearGroupTreeNode.this,fiscalTree, fm.getYear());
    						currentYearRendered = currentYearRendered || (fm.getYear() == FiscalTree.CURRENT_YEAR);
    					}
    					TreeItem modelNode = null;
    					for (int i = 0 ; i < yearNode.getChildCount() ; i++) {
    						TreeItem item = yearNode.getChild(i);
    						if (item instanceof FiscalModelGroupTreeNode) {
    							FiscalModelGroupTreeNode node = (FiscalModelGroupTreeNode) item;
    							FiscalModelType type = node.getTreeObject(); 
    							if (type == fm.getModel()) {
    								modelNode = yearNode.getChild(i);
    								break;
    							}
    						}
    					}
    					if (modelNode == null) {
    						modelNode = TreeNodeTypes.FISCAL_MODEL_GROUP
    								.getInstance()
    								.render(yearNode,fiscalTree, fm.getModel());
    					}
    					TreeNodeTypes.FISCAL_MODEL
    						.getInstance()
    						.render(modelNode,fiscalTree, fm);
    					yearNode.setState((fm.getYear() == FiscalTree.CURRENT_YEAR));
    				} 
    				if (!currentYearRendered) {
    					TreeNodeTypes.FISCAL_MODEL_YEAR
    						.getInstance()
    						.render(FiscalModelYearGroupTreeNode.this,fiscalTree, FiscalTree.CURRENT_YEAR);
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
