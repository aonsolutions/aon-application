package com.esferalia.aon.gwt.fiscal.client.tree;

import com.esferalia.aon.occam.api.model.fiscal.FiscalActivity;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasTreeItems;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TreeItem;

public class ActivityYearTreeNode extends TreeNode<Integer> {

	private HTMLPanel widget;

	@Override
	public Integer getTreeObject() {
		return (Integer) getUserObject();
	}

	@Override
	public void select(FiscalTree fiscalTree) {
		if (widget == null) {
			widget = new HTMLPanel(getTreeObject().toString());
		}
		fiscalTree.content.setWidget(widget);
	}

	@Override
	public ActivityYearTreeNode render(HasTreeItems parent,
			final FiscalTree fiscalTree, final Integer year) {
		InlineLabel label = new InlineLabel();
		label.setText(year.toString());
		label.addStyleName(FiscalTree.AON_RESOURCES.css().aonIconPointGreen());
		label.addStyleName(FiscalTree.AON_RESOURCES.css().aonTreeIconNode());
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
		popupPanel.setStyleName(FiscalTree.AON_RESOURCES.css()
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
		popup.setStyleName(FiscalTree.AON_RESOURCES.css().aonContextMenu());
		MenuItem addItem = new MenuItem(FiscalTree.MSG.newAction(), true,
				newActivityCommand);
		addItem.addStyleName(FiscalTree.AON_RESOURCES.css().aonIconReset());
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
