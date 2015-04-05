package com.esferalia.aon.gwt.fiscal.client.tree;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.occam.api.model.fiscal.FiscalActivity;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HasTreeItems;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ActivityYearTreeNode extends TreeNode<Integer> {

	private VerticalPanel widget;

	@Override
	public Integer getTreeObject() {
		return (Integer) getUserObject();
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
				label.addStyleName(AON.AON_CSS.aonIconModule());
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
	public ActivityYearTreeNode render(HasTreeItems parent,
			final FiscalTree fiscalTree, final Integer year) {
		InlineLabel label = new InlineLabel();
		label.setText(year.toString());
		label.addStyleName(AON.AON_CSS.aonTreeYear());
		setWidget(label);
		setUserObject(year);
		parent.addItem(this);
		if ( year >= FiscalTree.CURRENT_YEAR) {
			linkContextMenu(label, fiscalTree);
		}
		return this;
	}

	private void linkContextMenu(Label label, final FiscalTree fiscalTree) {
		final PopupPanel popupPanel = new PopupPanel();
		popupPanel.hide();
		popupPanel.setAutoHideEnabled(true);
		popupPanel.setStyleName(AON.AON_CSS
				.aonContextMenuPopup());
		Command newActivityCommand = new Command() {
			@Override
			public void execute() {
				TreeItem newAct = TreeNodeTypes.FISCAL_ACTIVITY.getInstance()
						.render(ActivityYearTreeNode.this, fiscalTree
							, new FiscalActivity()
								.setDomain(fiscalTree.enterprise.getDomain())
								.setYear(getTreeObject())
								.setEpigraph(AonStringUtils.EMPTY)
								.setDescription("NUEVA ACTIVIDAD"));
				setState(true);
				fiscalTree.tree.setSelectedItem(newAct, true);
				popupPanel.hide();
			}
		};
		MenuBar popup = new MenuBar(true);
		popup.setAnimationEnabled(true);
		popup.setStyleName(AON.AON_CSS.aonContextMenu());
		MenuItem addItem = new MenuItem(AON.MSG.newAction(), true,
				newActivityCommand);
		addItem.addStyleName(AON.AON_CSS.aonIconReset());
		popup.addItem(addItem);
		popupPanel.setWidget(popup);
		label.sinkEvents(Event.ONCONTEXTMENU);
		label.addHandler(new ContextMenuHandler() {
			@Override
			public void onContextMenu(ContextMenuEvent event) {
				event.preventDefault();
				event.stopPropagation();
				popupPanel.setPopupPosition(
						event.getNativeEvent().getClientX(), event
								.getNativeEvent().getClientY());
				popupPanel.show();
			}

		}, ContextMenuEvent.getType());
	}

}
