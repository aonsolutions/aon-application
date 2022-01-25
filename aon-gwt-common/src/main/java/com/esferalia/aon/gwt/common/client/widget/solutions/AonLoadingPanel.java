package com.esferalia.aon.gwt.common.client.widget.solutions;

import com.esferalia.aon.gwt.common.client.AON;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Position;
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
		
		this.getElement().getStyle().setDisplay(Display.NONE);
		this.getElement().getStyle().setProperty("justify-content", "center");
		this.getElement().getStyle().setPosition(Position.FIXED);
		this.getElement().getStyle().setTop(0, Unit.PX);
		this.getElement().getStyle().setBottom(0, Unit.PX);
		this.getElement().getStyle().setLeft(0, Unit.PX);
		this.getElement().getStyle().setRight(0, Unit.PX);
		this.getElement().getStyle().setZIndex(999);
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
		this.ensureDebugId("LoadingPanel");
	}
	
	public void show() {
		this.getElement().getStyle().setDisplay(Display.FLEX);
	}
	
	public void hide() {
		this.getElement().getStyle().setDisplay(Display.NONE);
	}
	
	
	
	
}
