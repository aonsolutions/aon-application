package com.esferalia.aon.gwt.common.client.widget.solutions;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;

public class AonButtonBadge extends FlowPanel {
	
	private AonButton button;
	private HTMLPanel badge;

	public AonButtonBadge(String toolTip, boolean badget) {
		button = new AonButton(toolTip);
		button.addStyleName(AON.CSS.aonToolbarButton());
		this.add(button);
		
		if(badget) {
			createBadge();
			this.add(badge);
		}
		
		getElement().getStyle().setProperty("position", "relative");
	}

	public AonButtonBadge(String toolTip, String iconStyle, boolean badget) {
		button = new AonButton(toolTip, iconStyle);
		button.addStyleName(AON.CSS.aonToolbarButton());
		this.add(button);
		
		if(badget) {
			createBadge();
			this.add(badge);
		}
		
		getElement().getStyle().setProperty("position", "relative");
	}
	
	public AonButtonBadge(String toolTip, String iconStyle, char accesskey, boolean badget) {
		button = new AonButton(toolTip, iconStyle, accesskey);
		button.addStyleName(AON.CSS.aonToolbarButton());
		this.add(button);
		
		if(badget) {
			createBadge();
			this.add(badge);
		}
		
		getElement().getStyle().setProperty("position", "relative");
	}
	
	private void createBadge() {
		badge = new HTMLPanel("");
		badge.getElement().getStyle().setProperty("position", "absolute");
		badge.getElement().getStyle().setProperty("top", "20px");
		badge.getElement().getStyle().setProperty("right", "5px");
		badge.getElement().getStyle().setProperty("padding", "4px");
		badge.getElement().getStyle().setProperty("border-radius", "50%");
		badge.getElement().getStyle().setProperty("background-color", "#ff5757");
		badge.getElement().getStyle().setProperty("color", "white");
	}
	
	public void setText(String text) {
		if ( AonStringUtils.isNotBlank(text)) {
			button.setWidth("auto");
			button.getElement().getStyle().setPaddingLeft(33.0, Unit.PX);
			button.getElement().getStyle().setProperty("background-position", "7px");
			button.getElement().getStyle().setProperty("background-repeat", "no-repeat");
			button.getElement().getStyle().setProperty("background-color", "transparent");
			button.getElement().getStyle().setProperty("text-transform", "uppercase");
			button.getElement().getStyle().setProperty("font-weight", "bold");
			button.getElement().getStyle().setProperty("font-size", "smaller");
		}
		button.setText(text);
	}
	
	public void addClickHandler(ClickHandler clickHandler) {
		button.addClickHandler(clickHandler);
	}

	public void addBagde() {
		if(this.getWidgetCount() == 1) {
			createBadge();
			this.add(badge);
		}
	}

	public void removeBadge() {
		if(this.getWidgetCount() == 2) remove(1);
	}
	
}
