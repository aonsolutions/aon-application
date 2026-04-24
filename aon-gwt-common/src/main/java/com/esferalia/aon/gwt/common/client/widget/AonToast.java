package com.esferalia.aon.gwt.common.client.widget;

import com.esferalia.aon.gwt.common.client.AON;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

public class AonToast extends FlowPanel {
	
	FlowPanel headerPanel;
	FlowPanel contentPanel;
	
	public AonToast() {
		super();
		setStyleName(AON.AON_CSS.aonToast());
		headerPanel = new FlowPanel();
		Button close = new Button();
		close.setStyleName(AON.AON_CSS.aonWidgetClose());
		close.addClickHandler(event -> hide());
		headerPanel.setStyleName(AON.AON_CSS.aonToastHeader());
		headerPanel.add(close);
		contentPanel = new FlowPanel();
		contentPanel.setStyleName(AON.AON_CSS.aonToastContent());
		add(headerPanel);
		add(contentPanel);
		RootPanel.get().add(this);
	}
	
	public void show(String caption,String content) {
		show(caption,  new Label(content));
	}
	public void show(String caption,Widget content) {
		Label header = new Label(caption);
		header.addClickHandler(event -> hide());
		headerPanel.add(header);
		contentPanel.add(content);
	}
	public void hide() {
		RootPanel.get().remove(this);
	}
}
