package com.esferalia.aon.gwt.payroll.client;


import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.PopupPanel;

public class ContextMenu extends PopupPanel{

	class ContextCommand implements ScheduledCommand{
		
		private ScheduledCommand cmd;
		
		public ContextCommand(ScheduledCommand cmd ) {
			this.cmd = cmd;
		}
		
		@Override
		public void execute() {
			cmd.execute();
			ContextMenu.this.hide();
		}
	}
	
	private MenuBar menuBar;
	
	public ContextMenu() {
		super();
		menuBar = new MenuBar(true /*Vertical*/);
		add(menuBar);
		setStyleName("gwt-MenuBarPopup");
		setAutoHideEnabled(true);
	}
	
	public void addItem(String text, ScheduledCommand cmd) {
		menuBar.addItem(text, new ContextCommand(cmd));
	}
	
	
}