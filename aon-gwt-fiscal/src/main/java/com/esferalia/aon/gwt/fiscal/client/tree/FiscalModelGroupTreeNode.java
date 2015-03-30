package com.esferalia.aon.gwt.fiscal.client.tree;

import java.util.HashMap;
import java.util.Map;

import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HasTreeItems;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;

public class FiscalModelGroupTreeNode extends TreeNode<FiscalModelType> {

	static Map<FiscalModelType, String> STYLE_MAP = new HashMap<FiscalModelType, String>();
	static {
		STYLE_MAP.put(FiscalModelType.M111,FiscalTree.AON_RESOURCES.css().aonIconM111() );
		STYLE_MAP.put(FiscalModelType.M115,FiscalTree.AON_RESOURCES.css().aonIconM115() );
		STYLE_MAP.put(FiscalModelType.M123,FiscalTree.AON_RESOURCES.css().aonIconM123() );
		STYLE_MAP.put(FiscalModelType.M130,FiscalTree.AON_RESOURCES.css().aonIconM130() );
		STYLE_MAP.put(FiscalModelType.M131,FiscalTree.AON_RESOURCES.css().aonIconM131() );
		STYLE_MAP.put(FiscalModelType.M303_RS,FiscalTree.AON_RESOURCES.css().aonIconM303());
	}

	private VerticalPanel widget;

	@Override
	public FiscalModelType getTreeObject() {
		return (FiscalModelType) getUserObject();
	}

	@Override
	public void select(final FiscalTree fiscalTree) {
		if (widget ==null) {
			widget = new VerticalPanel( );
			widget.setStyleName(FiscalTree.AON_RESOURCES.css().aonFiscalTreeList());
			Label title = new Label( getText());
			title.setStyleName(FiscalTree.AON_RESOURCES.css().aonFiscalTreeTitle());
			widget.add(title);
			for (int i = 0; i < getChildCount() ; i++) {
				final TreeItem item = getChild(i); 
				InlineLabel label =  new InlineLabel(getChild(i).getText());
				label.setStyleName(FiscalTree.AON_RESOURCES.css().aonFiscalTreeItem());
	    		label.addStyleName(FiscalTree.AON_RESOURCES.css().aonIconModule() );
				label.addClickHandler( new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						fiscalTree.tree.setSelectedItem(item);
					}
				});
				widget.add(label);
			}
		}
		fiscalTree.content.setWidget(widget);
	}

	@Override
	public FiscalModelGroupTreeNode render(HasTreeItems parent,
			final FiscalTree fiscalTree, final FiscalModelType modelType) {
		InlineLabel label = new InlineLabel();
		label.setText(FiscalTree.MSG.fiscalModelType( modelType) );
    	String className = STYLE_MAP.get(modelType);
    	if (AonStringUtils.isNotBlank(className)) {
    		label.addStyleName(className);
    	} else {
    		label.addStyleName(FiscalTree.AON_RESOURCES.css().aonIconPointOrange() );
    	}
		label.addStyleName(className);
		label.addStyleName(FiscalTree.AON_RESOURCES.css().aonTreeIconNode());
		setWidget(label);
		setUserObject(modelType);
		parent.addItem(this);
		return this;
	}

}
