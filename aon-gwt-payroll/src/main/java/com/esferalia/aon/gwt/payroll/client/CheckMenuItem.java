package com.esferalia.aon.gwt.payroll.client;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.MenuItem;

public class CheckMenuItem extends MenuItem {

	private static final String STYLENAME_CHECKED_ITEM = "aon-MenuItemCheckYes";

	private boolean checked;

	public CheckMenuItem(final String text, final ScheduledCommand cmd) {
		super(text, (ScheduledCommand) null);
		setScheduledCommand(new ScheduledCommand() {

			@Override
			public void execute() {
				checked = !checked;
				setCheckedStyle(checked);
				cmd.execute();
			}
		});
	}
	
	public boolean isChecked() {
		return checked;
	}
	
	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	protected void setCheckedStyle(boolean selected) {
		if (selected) {
			addStyleName(STYLENAME_CHECKED_ITEM);
		} else {
			removeStyleName(STYLENAME_CHECKED_ITEM);
		}
	}

}
