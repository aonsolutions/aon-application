package com.esferalia.aon.gwt.fiscal.client.tree;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.occam.api.model.Enterprise;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;
import com.esferalia.aon.occam.api.model.fiscal.Mod202;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasTreeItems;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;

public class FiscalModelsTreeNode extends TreeNode<Enterprise> {
	
	@Override
	public Enterprise getTreeObject() {
		return (Enterprise) getUserObject();
	}

	@Override
	public void select(final FiscalTree fiscalTree) {
		VerticalPanel widget = new VerticalPanel( );
		widget.setStyleName(AON.AON_CSS.aonFiscalTreeList());
		widget.add(FiscalTree.renderBreadcrumb(fiscalTree, this));
		for (int i = 0; i < getChildCount() ; i++) {
			final TreeItem item = getChild(i); 
			InlineLabel label =  new InlineLabel(getChild(i).getText());
			label.setStyleName(AON.AON_CSS.aonFiscalTreeItem());
			label.addStyleName(AON.AON_CSS.aonIconPointGreen() );
			label.addClickHandler( new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					item.setState(true);
					getTree().setSelectedItem(item);
				}
			});
			widget.add(label);
		}
		fiscalTree.content.setWidget(widget);
	}


	@Override
	public TreeNode<Enterprise> render(HasTreeItems parent, final FiscalTree fiscalTree
			,Enterprise enterprise) {
    	InlineLabel label = new InlineLabel();
    	label.setText(AON.MSG.fiscalModels());
    	label.addStyleName(AON.AON_CSS.aonIconModel());
    	label.addStyleName(AON.AON_CSS.aonTreeIconNode() );
    	setWidget(label);
    	setUserObject(enterprise);
    	parent.addItem(this);
    	fiscalTree.toolbar.addListener(this);
    	setNewContextMenu(fiscalTree.newContextMenu);
    	FiscalTree.FISCAL_SERVICE.getFiscalModels(FiscalTree.getCurrentDomainName()
        		, enterprise.getDomain(), new AsyncCallback<LinkedList<FiscalModel>>() {
    			
    			@Override
    			public void onSuccess(LinkedList<FiscalModel> result) {
    				for (FiscalModel fm : result) {
    					
    					if (fm.getModel() == FiscalModelType.M202) {
    						Mod202 mod202 = new Mod202();
    						mod202.setId(fm.getId());
    						mod202.setModel(fm.getModel());
    						mod202.setYear(fm.getYear());
    						mod202.setPeriod(fm.getPeriod());
    						mod202.setReplacement(fm.isReplacement());
    						TreeNodeTypes.MODEL_202
    							.getInstance()
    							.render(FiscalModelsTreeNode.this, fiscalTree
    									, mod202);
    					}
    				}
    				setState(true);
    			}
    			
    			@Override
    			public void onFailure(Throwable caught) {
    			}
    		});
    	
    	
    	return this;
	}

	@Override
	public void onNewButtonClick(ClickEvent event) {
		NativeEvent nativeEvent = event.getNativeEvent();
		newContextMenu.setPopupPosition(nativeEvent.getClientX(),
				nativeEvent.getClientY());
		newContextMenu.show();
	}
	
}
