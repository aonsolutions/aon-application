package com.esferalia.aon.gwt.common.client.widget.solutions;

import com.esferalia.aon.gwt.common.client.AON;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

public class AonFloatingMessage extends FlowPanel implements HasCloseHandlers<AonFloatingMessage> {
	
	private AonFloatingMessage( String caption, String message) {
		super();
		setStyleName(AON.CSS.aonFloatingMessage());
		FlowPanel headerPanel = new FlowPanel();

		Label header = new Label( caption );
		header.setStyleName(AON.CSS.aonFlexGrow1());
		headerPanel.insert(header, 0);
		
		Label close = new Label();
		close.setStyleName(AON.CSS.aonIconLabel());
		close.addStyleName(AON.CSS.aonIconCloseWhite() );
		close.addClickHandler( event ->  CloseEvent.fire(AonFloatingMessage.this, null ));
		headerPanel.setStyleName(AON.CSS.aonFloatingMessageHeader());
		headerPanel.add(close);
		add(headerPanel);
		
		FlowPanel contentPanel = new FlowPanel();
		contentPanel.setStyleName(AON.CSS.aonFloatingMessageContent());
		Label errorLabel = new Label(message);
		contentPanel.add(errorLabel);
		add(contentPanel);
	}
	
	public static AonFloatingMessage error(String message) {
		AonFloatingMessage msg = new AonFloatingMessage(AON.MSG.error(),message);
		msg.addStyleName(AON.CSS.aonMessageError());
		msg.addStyleName(AON.CSS.aonBold());
		msg.addStyleName(AON.CSS.aonFlexGrow1());
		return msg;
	}

	public static AonFloatingMessage info(String message) {
		AonFloatingMessage msg = new AonFloatingMessage(AON.MSG.information(),message);
		msg.addStyleName(AON.CSS.aonMessageInfo());
		msg.addStyleName(AON.CSS.aonBold());
		msg.addStyleName(AON.CSS.aonFlexGrow1());
		return msg;
	}
	
	@Override
	public HandlerRegistration addCloseHandler(CloseHandler<AonFloatingMessage> handler) {
		return super.addHandler(handler, CloseEvent.getType());
	}
	
}
