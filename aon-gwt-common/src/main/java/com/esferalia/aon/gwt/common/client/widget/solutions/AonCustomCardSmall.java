package com.esferalia.aon.gwt.common.client.widget.solutions;

import com.esferalia.aon.gwt.common.client.AON;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class AonCustomCardSmall extends HTMLPanel {

	private static final String EMPTY_STRING = "";
	private Widget rightWidget;
	
	public AonCustomCardSmall(String title) {
		super(EMPTY_STRING);
		addStyleName(AON.CSS.aonCustomCardSmall());

		createTitle(title);
	}
	
	public AonCustomCardSmall(String title, Widget rightWidget) {
		super(EMPTY_STRING);
		addStyleName(AON.CSS.aonCustomCardSmall());
		
		this.rightWidget = rightWidget;

		createTitle(title, this.rightWidget);
	}
	
	public AonCustomCardSmall(Widget widget) {
		super(EMPTY_STRING);
		addStyleName(AON.CSS.aonCustomCardSmall());

		createTitle(widget);
	}
	
	public AonCustomCardSmall(Widget widget, Widget rightWidget) {
		super(EMPTY_STRING);
		addStyleName(AON.CSS.aonCustomCardSmall());
		
		this.rightWidget = rightWidget;

		createTitle(widget, this.rightWidget);
	}

	private void createTitle(String title, Widget rightWidget) {
		HTMLPanel toolbarPanel = new HTMLPanel(EMPTY_STRING);
		toolbarPanel.getElement().getStyle().setProperty("display", "flex");
		toolbarPanel.getElement().getStyle().setProperty("align-items", "center");
		toolbarPanel.getElement().getStyle().setProperty("justify-content", "space-between");
		
		HTMLPanel titlePanel = new HTMLPanel(EMPTY_STRING);
		titlePanel.getElement().getStyle().setProperty("display", "flex");
		titlePanel.getElement().getStyle().setProperty("align-items", "center");
		titlePanel.getElement().getStyle().setProperty("gap", "0.5rem");
		titlePanel.getElement().getStyle().setProperty("height", "2rem");
		
		HTMLPanel titleLabel = new HTMLPanel(title);
		titleLabel.getElement().getStyle().setProperty("font-size", "1rem");
		titleLabel.getElement().getStyle().setProperty("font-weight", "700");
		titleLabel.getElement().getStyle().setProperty("color", "#5f6368");
		titlePanel.add(titleLabel);
		
		rightWidget.addStyleName(AON.CSS.aonCustomCardButton());
		
		toolbarPanel.add(titlePanel);
		toolbarPanel.add(rightWidget);
		
		add(toolbarPanel);
	}

	private void createTitle(String title) {
		HTMLPanel titlePanel = new HTMLPanel(EMPTY_STRING);
		titlePanel.getElement().getStyle().setProperty("display", "flex");
		titlePanel.getElement().getStyle().setProperty("align-items", "center");
		titlePanel.getElement().getStyle().setProperty("gap", "0.5rem");
		titlePanel.getElement().getStyle().setProperty("margin-bottom", "1rem");
		titlePanel.getElement().getStyle().setProperty("height", "2.6rem");
		
		HTMLPanel titleLabel = new HTMLPanel(title);
		titleLabel.getElement().getStyle().setProperty("font-size", "1rem");
		titleLabel.getElement().getStyle().setProperty("font-weight", "700");
		titleLabel.getElement().getStyle().setProperty("color", "#5f6368");
		titlePanel.add(titleLabel);
		
		add(titlePanel);
	}
	
	private void createTitle(Widget widget, Widget rightWidget) {
		HTMLPanel toolbarPanel = new HTMLPanel(EMPTY_STRING);
		toolbarPanel.getElement().getStyle().setProperty("display", "flex");
		toolbarPanel.getElement().getStyle().setProperty("align-items", "center");
		toolbarPanel.getElement().getStyle().setProperty("justify-content", "space-between");
		
		HTMLPanel titlePanel = new HTMLPanel(EMPTY_STRING);
		titlePanel.getElement().getStyle().setProperty("display", "flex");
		titlePanel.getElement().getStyle().setProperty("align-items", "center");
		titlePanel.getElement().getStyle().setProperty("gap", "0.5rem");
		titlePanel.getElement().getStyle().setProperty("height", "2rem");
		
		titlePanel.add(widget);
		
		rightWidget.addStyleName(AON.CSS.aonCustomCardButton());
		
		toolbarPanel.add(titlePanel);
		toolbarPanel.add(rightWidget);
		
		add(toolbarPanel);
	}

	private void createTitle(Widget widget) {
		HTMLPanel titlePanel = new HTMLPanel(EMPTY_STRING);
		titlePanel.getElement().getStyle().setProperty("display", "flex");
		titlePanel.getElement().getStyle().setProperty("align-items", "center");
		titlePanel.getElement().getStyle().setProperty("gap", "0.5rem");
		titlePanel.getElement().getStyle().setProperty("margin-bottom", "1rem");
		titlePanel.getElement().getStyle().setProperty("height", "2.6rem");
		
		titlePanel.add(widget);
		
		add(titlePanel);
	}
	
	public void setToolbarWidgetShown() {
		if(null != this.rightWidget)
			this.rightWidget.removeStyleName(AON.CSS.aonCustomCardButton());
	}

}
