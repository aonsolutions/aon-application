package com.esferalia.aon.gwt.common.client.widget.solutions;


import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.MenuItemSeparator;
import com.google.gwt.user.client.ui.PopupPanel;

public class AonContextMenu extends PopupPanel{

	class ContextCommand implements ScheduledCommand{
		
		private ScheduledCommand cmd;
		
		public ContextCommand(ScheduledCommand cmd ) {
			this.cmd = cmd;
		}
		
		@Override
		public void execute() {
			AonContextMenu.this.hide();
			cmd.execute();
		}
	}
	
	private MenuBar menuBar;
	
	public AonContextMenu() {
		super();
		menuBar = new MenuBar(true /*Vertical*/);
		add(menuBar);
		setStyleName("gwt-MenuBarPopup");
		setAutoHideEnabled(true);
	}
	
	public MenuItemSeparator addSeparator() {
		return menuBar.addSeparator();
	}
	
	public MenuItem addItem(SafeHtml html, MenuBar popup) {
		return menuBar.addItem(html, popup);
	}

	public MenuItem addItem(String text, MenuBar popup) {
		return menuBar.addItem(text, popup);
	}

	public MenuItemSeparator addSeparator(MenuItemSeparator separator) {
		return menuBar.addSeparator(separator);
	}

	public MenuItem addItem(String text, ScheduledCommand cmd) {
		return menuBar.addItem(text, new ContextCommand(cmd));
	}
	
	public MenuItem addItem(SafeHtml html, ScheduledCommand cmd) {
		return menuBar.addItem(html, new ContextCommand(cmd));
	}

	public MenuItem addItem(String text, boolean asHTML, MenuBar popup) {
		return menuBar.addItem(text, asHTML, popup);
	}

	public MenuItem addItem(String text, MenuBar popup, String ...styles) {
		return menuBar.addItem( getHTML(text, styles), true, popup);
	}

	public MenuItem addItem(String text, ScheduledCommand cmd, String ...styles) {
		return menuBar.addItem( getHTML(text, styles), true, new ContextCommand(cmd));
	}
	
	
	static String getHTML(String text, String ...styles ) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("<span class='");
		for (String style : styles)
			buffer.append(style + ' ' );
		buffer.append("' >");
		buffer.append(text);
		buffer.append("</span>");
		
		return buffer.toString();
	}
	
}