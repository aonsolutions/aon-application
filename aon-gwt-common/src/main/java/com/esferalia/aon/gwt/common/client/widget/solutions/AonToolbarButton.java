package com.esferalia.aon.gwt.common.client.widget.solutions;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickHandler;

public class AonToolbarButton extends AonButton {

	public AonToolbarButton(String toolTip) {
		super(toolTip);
		addStyleName(AON.CSS.aonToolbarButton());
	}

	public AonToolbarButton(String toolTip, String iconStyle) {
		super(toolTip,iconStyle);
		addStyleName(AON.CSS.aonToolbarButton());
	}
	
	public AonToolbarButton(String toolTip, String iconStyle, char accesskey) {
		super(toolTip,iconStyle,accesskey);
		addStyleName(AON.CSS.aonToolbarButton());
	}
	
	public AonToolbarButton(String toolTip, String iconStyle, boolean visible, ClickHandler clickHandler) {
		super(toolTip, iconStyle);
		addStyleName(AON.CSS.aonToolbarButton());
		setVisible(visible);
		addClickHandler(clickHandler);
	}
	
	@Override
	public void setText(String text) {
		if ( AonStringUtils.isNotBlank(text)) {
			setWidth("auto");
			getElement().getStyle().setPaddingLeft(33.0, Unit.PX);
			getElement().getStyle().setProperty("background-position", "7px");
			getElement().getStyle().setProperty("background-repeat", "no-repeat");
			getElement().getStyle().setProperty("background-color", "transparent");
			getElement().getStyle().setProperty("text-transform", "uppercase");
			getElement().getStyle().setProperty("font-weight", "bold");
			getElement().getStyle().setProperty("font-size", "smaller");
		}
		super.setText(text);
	}
}
