package com.esferalia.aon.gwt.common.client.widget.solutions;

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

public class AonCustomPopup extends PopupPanel implements HasClickHandlers {
	private DockLayoutPanel dockLayoutPanel;
	private Label caption;
	private Button closeButton;
	private FlowPanel dialogBar;
	
	public AonCustomPopup() {
		this(true);
	}

	public AonCustomPopup( boolean showCloseButton) {
		setGlassEnabled(true);
		setAnimationEnabled(false);
		dockLayoutPanel = new DockLayoutPanel(Unit.PX);	
		
		dialogBar = new FlowPanel ();
		dialogBar.setStyleName(AON.CSS.aonCustomDialogPanel());
		FlowPanel header = new FlowPanel();
		header.setStyleName(AON.CSS.aonCustomDialogHeader());
		FlowPanel title = new FlowPanel();
		title.setStyleName(AON.CSS.aonCustomDialogTitle());
		caption = new Label();
		title.add(caption);
		header.add(title);
		
		if (showCloseButton) {
			closeButton = new Button();
			closeButton.setStyleName(AON.CSS.aonCustomDialogClose());
			closeButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					hide();
					ClickEvent.fireNativeEvent(event.getNativeEvent(), AonCustomPopup.this);
				}
			});
			header.add(closeButton);
		}
		
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
	
	public void hideHeader() {
		dockLayoutPanel.setWidgetSize(dialogBar, 0);
		dockLayoutPanel.remove(dialogBar);
	}

	@Override
	public void add(IsWidget child) {
		dockLayoutPanel.add(child);
	}
	@Override
	public void add(Widget child) {
		dockLayoutPanel.add(child);
	}
	
	public Button getCloseButton() {
		return closeButton;
	}
	
	@Override
	public HandlerRegistration addClickHandler(ClickHandler handler) {
		return super.addHandler(handler, ClickEvent.getType());
	}
	
}
