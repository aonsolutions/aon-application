package com.esferalia.aon.gwt.fiscal.client.tree;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.client.tree.FiscalTree.NewMod200Command;
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
	public void select(final FiscalTree fiscalTree) {
		if (widget ==null) {
			widget = new ActivityForm();
		}
		widget.select( this.getTreeObject() );
		widget.setCallback(new TreeNodeCallback<FiscalActivity>() {

			@Override
			public void delete(FiscalActivity t) {
				// TODO Auto-generated method stub
			}

			@Override
			public void changeLabel(FiscalActivity fa) {
				setLabel(fa);
			}
			@Override
			public void newMod202() {
				NewMod200Command newMod200Command =  fiscalTree.getNewMod200Command();
				newMod200Command.execute();
			}
			
		});
		fiscalTree.toolbar.setVisibleCopyButton(false);
		fiscalTree.toolbar.setVisibleDraftButton(false);
		fiscalTree.toolbar.setVisiblePasteButton(false);
    	fiscalTree.toolbar.addListener(this);
		fiscalTree.content.setWidget(widget);
	}
	
	@Override
	public FiscalActivity getTreeObject() {
		return (FiscalActivity) this.getUserObject();
	}

	@Override
	public ActivityTreeNode render(HasTreeItems parent, FiscalTree fiscalTree, FiscalActivity fa) {
		Label label = setLabel(fa);
    	this.setUserObject(fa);
    	parent.addItem(this);
    	linkContextMenu( label, fiscalTree);
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

	private void linkContextMenu(Label label, final FiscalTree fiscalPanel) {
        final PopupPanel popupPanel = new PopupPanel();
        popupPanel.hide();
        popupPanel.setAutoHideEnabled(true);
        popupPanel.setStyleName(AON.AON_CSS.aonContextMenuPopup());
		Command deleteActivityCommand = new Command() {
			@Override
			public void execute() {
				if (Window.confirm( AON.MSG.deleteAction() )) {
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
    	popup.setStyleName(AON.AON_CSS.aonContextMenu());
        MenuItem deleteItem = new MenuItem(AON.MSG.deleteAction(), true, deleteActivityCommand);
        deleteItem.addStyleName(AON.AON_CSS.aonIconDelete());
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
