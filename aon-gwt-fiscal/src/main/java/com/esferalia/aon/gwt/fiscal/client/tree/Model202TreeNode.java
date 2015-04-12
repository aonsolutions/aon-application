package com.esferalia.aon.gwt.fiscal.client.tree;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.occam.api.model.fiscal.Mod202;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.HasTreeItems;
import com.google.gwt.user.client.ui.InlineLabel;

public class Model202TreeNode extends TreeNode<Mod202> {

	
	
	private Model202Form widget;

	@Override
	public void select(FiscalTree fiscalTree) {
		if (widget ==null) {
			widget = new Model202Form();
		}
		widget.select( this.getTreeObject() );
		widget.setCallback(new TreeNodeCallback<Mod202>() {

			@Override
			public void delete(Mod202 mod202) {
				 getParentItem().removeItem(Model202TreeNode.this);
			}
			@Override
			public void changeLabel(Mod202 mod202) {
				setLabel(mod202);
			}
			
		});
		fiscalTree.content.setWidget(widget);
	}
	
	@Override
	public Mod202 getTreeObject() {
		return (Mod202) this.getUserObject();
	}

	@Override
	public Model202TreeNode render(HasTreeItems parent,FiscalTree fiscalTree, Mod202 mod202) {
    	this.setUserObject(mod202);
    	parent.addItem(this);
    	setLabel(mod202);
    	fiscalTree.toolbar.addListener(this);
		return this;
	}
	
	@Override
	public void onNewButtonClick(ClickEvent event) {
		NativeEvent nativeEvent = event.getNativeEvent();
		newContextMenu.setPopupPosition(nativeEvent.getClientX(),
				nativeEvent.getClientY());
		newContextMenu.show();
	}

	public void setLabel(Mod202 mod202) {
		InlineLabel label = new InlineLabel();
		label.setText(AON.MSG.fiscalModelType( mod202.getModel())
				+ " - "
				+ mod202.getYear()
				+ " - " 
				+ AON.MSG.fiscalPeriod(mod202.getPeriod())
				+ (mod202.isReplacement()? " - Sust.":"") 
				);
		label.addStyleName(AON.AON_CSS.aonTreeIconNode() );
		label.addStyleName(AON.AON_CSS.aonIconModule() );
		this.setWidget(label);	
	}

}
