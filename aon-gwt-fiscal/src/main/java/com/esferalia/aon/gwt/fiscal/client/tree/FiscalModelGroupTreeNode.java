package com.esferalia.aon.gwt.fiscal.client.tree;

import java.util.HashMap;
import java.util.Map;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HasTreeItems;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;

public class FiscalModelGroupTreeNode extends TreeNode<FiscalModelType> {

	static Map<FiscalModelType, String> STYLE_MAP = new HashMap<FiscalModelType, String>();
	static {
		STYLE_MAP.put(FiscalModelType.M111,AON.AON_CSS.aonIconM111() );
		STYLE_MAP.put(FiscalModelType.M115,AON.AON_CSS.aonIconM115() );
		STYLE_MAP.put(FiscalModelType.M123,AON.AON_CSS.aonIconM123() );
		STYLE_MAP.put(FiscalModelType.M130,AON.AON_CSS.aonIconM130() );
		STYLE_MAP.put(FiscalModelType.M131,AON.AON_CSS.aonIconM131() );
		STYLE_MAP.put(FiscalModelType.M303_RS,AON.AON_CSS.aonIconM303());
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
			widget.setStyleName(AON.AON_CSS.aonFiscalTreeList());
			widget.add(FiscalTree.renderBreadcrumb(fiscalTree, this));
			for (int i = 0; i < getChildCount() ; i++) {
				final TreeItem item = getChild(i); 
				InlineLabel label =  new InlineLabel(getChild(i).getText());
				label.setStyleName(AON.AON_CSS.aonFiscalTreeItem());
	    		label.addStyleName(AON.AON_CSS.aonIconModule() );
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
		label.setText(AON.MSG.fiscalModelType( modelType) );
    	String className = STYLE_MAP.get(modelType);
    	if (AonStringUtils.isNotBlank(className)) {
    		label.addStyleName(className);
    	} else {
    		label.addStyleName(AON.AON_CSS.aonIconPointOrange() );
    	}
		label.addStyleName(className);
		label.addStyleName(AON.AON_CSS.aonTreeIconNode());
		setWidget(label);
		setUserObject(modelType);
		parent.addItem(this);
		return this;
	}

}
