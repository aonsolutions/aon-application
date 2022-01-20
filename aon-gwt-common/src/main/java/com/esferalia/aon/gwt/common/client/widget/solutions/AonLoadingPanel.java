package com.esferalia.aon.gwt.common.client.widget.solutions;

import com.esferalia.aon.gwt.common.client.AON;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;

public class AonLoadingPanel extends FlowPanel{
	String message;

	public AonLoadingPanel() {
		this("");
	}
	
	public AonLoadingPanel(String message) {
		super();
		this.message = message;
		
		this.getElement().getStyle().setProperty("display", "none");
		this.getElement().getStyle().setProperty("justify-content", "center");		
		this.getElement().getStyle().setProperty("position", "fixed");
		this.getElement().getStyle().setProperty("top", "0");
		this.getElement().getStyle().setProperty("bottom", "0");
		this.getElement().getStyle().setProperty("left", "0");
		this.getElement().getStyle().setProperty("right", "0");
		this.getElement().getStyle().setProperty("background", "radial-gradient(rgba(20, 20, 20, 0.8), rgba(0, 0, 0, 0.8))");
		
		FlexTable loadingTable = new FlexTable();
		loadingTable.getElement().getStyle().setProperty("margin", "auto");
		Label spinLabel = new Label();
		spinLabel.addStyleName(AON.AON_CSS.aonDisplayBlock());
		spinLabel.addStyleName(AON.CSS.aonLoader());
		spinLabel.getElement().getStyle().setWidth(25, Unit.PX);
		spinLabel.getElement().getStyle().setHeight(25, Unit.PX);
		Label loadingMsgLabel = new InlineLabel(message != null ? message : "");
		loadingMsgLabel.getElement().getStyle().setFontSize(2, Unit.EM);
		loadingMsgLabel.getElement().getStyle().setColor("white");
		loadingTable.setWidget(0, 0, spinLabel);
		loadingTable.setWidget(0, 1, loadingMsgLabel);		
		this.add(loadingTable);
		
	}
	
	public void show() {
		this.getElement().getStyle().setProperty("display", "flex");
	}
	
	public void hide() {
		this.getElement().getStyle().setProperty("display", "none");
	}
	
	
	
	
}
