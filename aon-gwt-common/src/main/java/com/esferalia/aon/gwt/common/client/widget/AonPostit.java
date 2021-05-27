package com.esferalia.aon.gwt.common.client.widget;

import com.esferalia.aon.gwt.common.client.AON;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

public class AonPostit extends FlowPanel {
	
	FlowPanel headerPanel;
	FlowPanel contentPanel;
	
	public AonPostit() {
		super();
		setStyleName(AON.AON_CSS.aonPostit());
		headerPanel = new FlowPanel();
		headerPanel.setStyleName(AON.AON_CSS.aonPostitHeader());
		contentPanel = new FlowPanel();
		contentPanel.setStyleName(AON.AON_CSS.aonPostitContent());
		add(headerPanel);
		add(contentPanel);
		RootPanel.get().add(this);
	}
	
	public void show(String caption,String content) {
		Label header = new Label(caption);
		header.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				hide();
			}
		});
		headerPanel.add(header);
		contentPanel.add(new Label(content));
	}
	public void hide() {
		RootPanel.get().remove(this);
	}
}
