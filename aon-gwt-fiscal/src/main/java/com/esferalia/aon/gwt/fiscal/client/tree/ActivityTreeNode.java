package com.esferalia.aon.gwt.fiscal.client.tree;

import com.esferalia.aon.occam.api.model.fiscal.FiscalActivity;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasTreeItems;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;

public class ActivityTreeNode extends TreeNode<FiscalActivity> {

	private ActivityForm widget;
	
	@Override
	public void select(FiscalTree fiscalPanel) {
		if (widget ==null) {
			widget = new ActivityForm();
		}
		widget.select(this );
		fiscalPanel.content.setWidget(widget);
	}
	
	@Override
	public FiscalActivity getTreeObject() {
		return (FiscalActivity) this.getUserObject();
	}

	@Override
	public ActivityTreeNode render(HasTreeItems parent, FiscalTree fiscalPanel, FiscalActivity fa) {
    	InlineLabel label = new InlineLabel();
    	label.setText((AonStringUtils.isBlank(fa.getEpigraph())?AonStringUtils.EMPTY:fa.getEpigraph() + " - ") 
    			+ AonStringUtils.abbreviate(fa.getDescription(), 40));
    	label.addStyleName(FiscalTree.AON_RESOURCES.css().aonIconModule());
    	label.addStyleName(FiscalTree.AON_RESOURCES.css().aonTreeIconNode() );
    	this.setWidget(label);
    	this.setUserObject(fa);
    	parent.addItem(this);
    	linkContextMenu( label, fiscalPanel);
		return this;
	}
	
	private void linkContextMenu(Label label, final FiscalTree fiscalPanel) {
        final PopupPanel popupPanel = new PopupPanel();
        popupPanel.hide();
        popupPanel.setAutoHideEnabled(true);
        popupPanel.setStyleName(FiscalTree.AON_RESOURCES.css().aonContextMenuPopup());
		Command deleteActivityCommand = new Command() {
			@Override
			public void execute() {
				if (Window.confirm( FiscalTree.MSG.deleteAction() )) {
					if (getTreeObject().getId() != null) {
						FiscalTree.FISCAL_SERVICE.delete(FiscalTree.getCurrentDomainName(), getTreeObject()
								,new AsyncCallback<Void>() {
				
									@Override
									public void onSuccess(Void v) {
										fiscalPanel.tree.setSelectedItem(getParentItem(),true);
										getParentItem().removeItem(ActivityTreeNode.this);							
									}
									@Override
									public void onFailure(Throwable caught) {
										// TODO Auto-generated method stub
									}
				
						});
					} else {
						fiscalPanel.tree.setSelectedItem(getParentItem(),true);
						getParentItem().removeItem(ActivityTreeNode.this);							
					}
				}
				popupPanel.hide();
			}
		};
    	MenuBar popup = new MenuBar(true);
    	popup.setAnimationEnabled(true);
    	popup.setStyleName(FiscalTree.AON_RESOURCES.css().aonContextMenu());
        MenuItem deleteItem = new MenuItem(FiscalTree.MSG.deleteAction(), true, deleteActivityCommand);
        deleteItem.addStyleName(FiscalTree.AON_RESOURCES.css().aonIconDelete());
        popup.addItem(deleteItem);
        popupPanel.setWidget(popup);
        label.sinkEvents(Event.ONCONTEXTMENU);
        label.addHandler(new ContextMenuHandler() {
			@Override
			public void onContextMenu(ContextMenuEvent event) {
				event.preventDefault();
				event.stopPropagation();
				popupPanel.setPopupPosition(event.getNativeEvent()
						.getClientX(), event.getNativeEvent()
						.getClientY());
				popupPanel.show();
			}
			
		}, ContextMenuEvent.getType());
	}
	
}
