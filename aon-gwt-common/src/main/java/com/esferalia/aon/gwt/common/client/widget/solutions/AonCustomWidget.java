package com.esferalia.aon.gwt.common.client.widget.solutions;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class AonCustomWidget<W extends Widget> extends HTMLPanel {

	private static final String EMPTY_STRING = "";
	
	private HTMLPanel titleLabel;
	private final W widget;
	
	public AonCustomWidget(String title, W widget) {
		super(EMPTY_STRING);
		this.widget = widget;
		addStyleName(AON.CSS.aonFlexColumn2());
		addStyleName(AON.CSS.aonCustomTextBox());

		titleLabel = new HTMLPanel(AonStringUtils.isNotBlank(title) ? title : EMPTY_STRING);
		titleLabel.addStyleName(AON.CSS.aonCustomTextBoxTitle());
		add(titleLabel);
		
		HTMLPanel widgetPanel = new HTMLPanel(EMPTY_STRING);
		widgetPanel.addStyleName(AON.CSS.aonItemFlex());
		widgetPanel.addStyleName(AON.CSS.aonFlexBetween());
		widgetPanel.getElement().getStyle().setProperty("align-items", "center");
		
		widgetPanel.add(this.widget);
		add(widgetPanel);
	}

	public void setVisibleTitle(String title) {
		if	(null != titleLabel) 
			titleLabel.getElement().setInnerHTML(title);
	}

	public W getWidget() {
		return this.widget;
	}

	@Override
	protected void onEnsureDebugId(String baseID) {
		super.onEnsureDebugId(baseID);
		this.widget.ensureDebugId(baseID + "Widget");
	}

}
