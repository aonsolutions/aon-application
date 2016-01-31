package com.esferalia.aon.gwt.fiscal.client.tree.node;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.client.tree.FiscalTree;
import com.esferalia.aon.gwt.fiscal.client.tree.ITreeNodeCallback;
import com.esferalia.aon.gwt.fiscal.client.tree.content.ActivityForm;
import com.esferalia.aon.occam.api.model.fiscal.FiscalActivity;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.user.client.ui.HasTreeItems;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;

public class ActivityTreeNode extends TreeNode<FiscalActivity> {

	private ActivityForm widget;
	
	@Override
	public void select(final FiscalTree fiscalTree) {
		if (widget ==null) {
			widget = new ActivityForm();
		}
		widget.select( this.getTreeObject() );
		widget.setCallback(new ITreeNodeCallback<FiscalActivity>() {

			@Override
			public void delete(FiscalActivity t) {
			}

			@Override
			public void changeLabel(FiscalActivity fa) {
				setLabel(fa);
			}
		});
		fiscalTree.setContent(widget);
	}
	
	@Override
	public FiscalActivity getTreeObject() {
		return (FiscalActivity) this.getUserObject();
	}

	@Override
	public ActivityTreeNode render(HasTreeItems parent, FiscalActivity fa) {
		setLabel(fa);
    	this.setUserObject(fa);
    	parent.addItem(this);
		return this;
	}
	
	private Label setLabel(FiscalActivity fa) {
    	InlineLabel label = new InlineLabel();
    	label.setText((AonStringUtils.isBlank(fa.getEpigraph())?AonStringUtils.EMPTY:fa.getEpigraph() + " - ") 
    			+ AonStringUtils.abbreviate(fa.getDescription(), 40));
    	label.addStyleName(AON.AON_CSS.aonIconModule());
    	label.addStyleName(AON.AON_CSS.aonTreeIconNode() );
    	this.setWidget(label);
		return label;
	}
}
