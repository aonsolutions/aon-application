package com.esferalia.aon.gwt.common.client.widget.solutions;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.occam.api.model.Status;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuItem;

public class AonStatusSelect extends HTMLPanel {

	// ------------------------------------------------- ScheduledCommand (Status)
	
		class ActiveCommand implements ScheduledCommand {

			@Override
			public void execute() {
				status = Status.ACTIVE;
				addStatusInput();
			}
		}
		
		class InactiveCommand implements ScheduledCommand {

			@Override
			public void execute() {
				status = Status.INACTIVE;
				addStatusInput();
			}
		}
		
		class StatusContextMenu extends AonContextMenu {

			public StatusContextMenu() {

				addMenuItem(Status.ACTIVE.getDescription(), new ActiveCommand(), AON.CSS.aonIconCircleGreen(), "active");
				addMenuItem(Status.INACTIVE.getDescription(), new InactiveCommand(), AON.CSS.aonIconCircleRed(), "inactive");
			}
			
			private MenuItem addMenuItem(String title, ScheduledCommand command, String iconStyle, String debugId) {
				MenuItem item = addItem(title, command, iconStyle, AON.AON_ICON_CMD_BUTTON, AON.AON_CMD_BUTTON);
				item.getElement().getStyle().setProperty("padding", ".5rem");
				item.ensureDebugId(debugId);
				return item;
			}

		}

		private static final String EMPTY_STRING = "";
		
		private StatusContextMenu statusContextMenu;
		private Status status;
		
		public AonStatusSelect(Status status) {
			super(EMPTY_STRING);
			addStyleName(AON.CSS.aonItemFlex());
			getElement().getStyle().setProperty("border", "1px solid lightgray");
			getElement().getStyle().setProperty("border-radius", "10px");
			getElement().getStyle().setProperty("padding", "5px");
			getElement().getStyle().setProperty("cursor", "pointer");
			
			this.status = status;

			statusContextMenu = new StatusContextMenu();
			
			addDomHandler(event -> {
				NativeEvent nativeEvent = event.getNativeEvent();
				statusContextMenu.setPopupPosition(nativeEvent.getClientX(), nativeEvent.getClientY());
				statusContextMenu.getElement().getStyle().setProperty("z-index", "2");
				statusContextMenu.show();
			}, ClickEvent.getType());
			
			addStatusInput();
			
		}

		private void addStatusInput() {
			clear();
			
			HTMLPanel circleStatus = new HTMLPanel(EMPTY_STRING);
			circleStatus.setStyleName(status == Status.ACTIVE ? AON.AON_CIRCLE_GREEN : AON.AON_CIRCLE_RED);
			add(circleStatus);
			
			Label statusLabel = new Label(status.getDescription());
			add(statusLabel);
			
			AonToolbarSmallButton arrowDown = new AonToolbarSmallButton("", AON.CSS.aonIconDown());
			add(arrowDown);
		}

		public Status getValue() {
			return this.status;
		}
}
