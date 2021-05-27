package com.esferalia.aon.gwt.fiscal.client.accounting.utilities;

import com.esferalia.aon.gwt.common.client.AON;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class CustomPopup extends PopupPanel implements HasClickHandlers {
	private DockLayoutPanel dockLayoutPanel;
	private Label caption;
	private Button closeButton;

	public CustomPopup() {
		setGlassEnabled(true);
		setAnimationEnabled(false);
		dockLayoutPanel = new DockLayoutPanel(Unit.PX);	
		
		FlowPanel dialogBar = new FlowPanel ();
		dialogBar.setStyleName(AON.AON_CSS.customDialogPanel());
		FlowPanel header = new FlowPanel();
		header.setStyleName(AON.AON_CSS.customDialogHeader());
		FlowPanel title = new FlowPanel();
		title.setStyleName(AON.AON_CSS.customDialogTitle());
		caption = new Label();
		title.add(caption);
		closeButton = new Button();
		closeButton.setStyleName(AON.AON_CSS.customDialogClose());
		closeButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide();
				ClickEvent.fireNativeEvent(event.getNativeEvent(), CustomPopup.this);
			}
		});
		title.add(closeButton);
		header.add(title);
		dialogBar.add(header);
		dockLayoutPanel.addNorth(dialogBar, 27);
		
		super.add(dockLayoutPanel);
	}

	public String getCaption(){
		return caption.getText();
	}

	public void setCaption(String captionText) {
		caption.setText(captionText);
	}

	@Override
	public void add(IsWidget child) {
		dockLayoutPanel.add(child);
	}
	@Override
	public void add(Widget child) {
		dockLayoutPanel.add(child);
	}
	
	@Override
	public HandlerRegistration addClickHandler(ClickHandler handler) {
		return super.addHandler(handler, ClickEvent.getType());
	}
	
}
