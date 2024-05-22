package com.esferalia.aon.gwt.marketing.client.commercial;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonContextMenu;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarSmallButton;
import com.esferalia.aon.occam.api.model.type.SellerStatus;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuItem;

public class SellerStatusSelect extends HTMLPanel {
	
	// ------------------------------------------------- ScheduledCommand (Status)
	
	class ActiveCommand implements ScheduledCommand {

		@Override
		public void execute() {
			sellerStatus = SellerStatus.ACTIVE;
			addStatusInput();
		}
	}
	
	class InactiveCommand implements ScheduledCommand {

		@Override
		public void execute() {
			sellerStatus = SellerStatus.INACTIVE;
			addStatusInput();
		}
	}
	
	class StatusContextMenu extends AonContextMenu {

		public StatusContextMenu() {

			addMenuItem("Activo", new ActiveCommand(), AON.CSS.aonIconCircleGreen(), "active");
			addMenuItem("Inactivo", new InactiveCommand(), AON.CSS.aonIconCircleRed(), "inactive");
		}
		
		private MenuItem addMenuItem(String title, ScheduledCommand command, String iconStyle, String debugId) {
			MenuItem item = addItem(title, command, iconStyle, AON.AON_ICON_CMD_BUTTON, AON.AON_CMD_BUTTON);
			item.ensureDebugId(debugId);
			return item;
		}

	}

	private static final String EMPTY_STRING = "";
	
	private StatusContextMenu statusContextMenu;
	private SellerStatus sellerStatus;
	
	public SellerStatusSelect(SellerStatus sellerStatus) {
		super(EMPTY_STRING);
		addStyleName(AON.CSS.aonItemFlex());
		getElement().getStyle().setProperty("border", "1px solid lightgray");
		getElement().getStyle().setProperty("border-radius", "10px");
		getElement().getStyle().setProperty("padding", "5px");
		getElement().getStyle().setProperty("cursor", "pointer");
		
		this.sellerStatus = sellerStatus;

		statusContextMenu = new StatusContextMenu();
		
		addDomHandler(event -> {
			NativeEvent nativeEvent = event.getNativeEvent();
			statusContextMenu.setPopupPosition(nativeEvent.getClientX(), nativeEvent.getClientY());
			statusContextMenu.show();
		}, ClickEvent.getType());
		
		addStatusInput();
		
	}

	private void addStatusInput() {
		clear();
		
		HTMLPanel circleStatus = new HTMLPanel(EMPTY_STRING);
		circleStatus.setStyleName(sellerStatus == SellerStatus.ACTIVE ? AON.AON_CIRCLE_GREEN : AON.AON_CIRCLE_RED);
		add(circleStatus);
		
		Label status = new Label(sellerStatus == SellerStatus.ACTIVE ? "Activo" : "Inactivo");
		add(status);
		
		AonToolbarSmallButton arrowDown = new AonToolbarSmallButton("", AON.CSS.aonIconDown());
		add(arrowDown);
	}

	public SellerStatus getValue() {
		return this.sellerStatus;
	}

}
