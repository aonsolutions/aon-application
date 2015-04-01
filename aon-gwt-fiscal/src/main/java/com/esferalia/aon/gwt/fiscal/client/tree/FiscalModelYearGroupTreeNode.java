package com.esferalia.aon.gwt.fiscal.client.tree;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.occam.api.model.Enterprise;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;
import com.esferalia.aon.occam.api.model.fiscal.Mod131;
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
			widget.setStyleName(AON.AON_CSS.aonFiscalTreeList());
			Label title = new Label(getText());
			title.setStyleName(AON.AON_CSS.aonFiscalTreeTitle());
			widget.add(title);
			for (int i = 0; i < getChildCount() ; i++) {
				final TreeItem item = getChild(i); 
				InlineLabel label =  new InlineLabel(getChild(i).getText());
				label.setStyleName(AON.AON_CSS.aonFiscalTreeItem());
				label.addStyleName(AON.AON_CSS.aonIconPointGreen() );
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
    	label.setText(AON.MSG.fiscalModels());
    	label.addStyleName(AON.AON_CSS.aonIconModel());
    	label.addStyleName(AON.AON_CSS.aonTreeIconNode() );
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
    					if (fm.getModel() == FiscalModelType.M131) {
    						Mod131 mod131 = new Mod131();
    						mod131.setId(fm.getId());
    						mod131.setModel(fm.getModel());
    						mod131.setYear(fm.getYear());
    						mod131.setPeriod(fm.getPeriod());
    						mod131.setReplacement(fm.isReplacement());
    						TreeNodeTypes.MODEL_131
    							.getInstance()
    							.render(modelNode,fiscalTree, mod131);
    					} else {
        					TreeNodeTypes.FISCAL_MODEL
    							.getInstance()
    							.render(modelNode,fiscalTree, fm);
    					}
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
