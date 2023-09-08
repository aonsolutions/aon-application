package com.esferalia.aon.gwt.common.client.widget.solutions;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;

public class AonIcon extends FlowPanel {
	
	public AonIcon(String icon) {
		super();
		String html = "<i class=\"material-icons\">" + icon + "</i>";
		HTMLPanel panel = new HTMLPanel(html);
		add(panel);
	}
	
	public void setIcon(String icon) {
		remove(0);
		String html = "<i class=\"material-icons\">" + icon + "</i>";
		HTMLPanel panel = new HTMLPanel(html);
		add(panel);
	}
	
	public void onClick(ClickHandler clickHandler) {
		this.addDomHandler(clickHandler, ClickEvent.getType());
	}
}
