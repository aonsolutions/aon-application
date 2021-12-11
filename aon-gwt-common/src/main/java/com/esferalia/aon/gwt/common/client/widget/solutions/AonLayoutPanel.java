package com.esferalia.aon.gwt.common.client.widget.solutions;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public class AonLayoutPanel extends DockLayoutPanel {

	private FlowPanel dummyNorthPanel;
	private AonFloatingMessage msgWidget;

	public AonLayoutPanel(Unit unit) {
		super(unit);
		dummyNorthPanel = new FlowPanel();
		addNorth(dummyNorthPanel, 0);
	}

	public AonLayoutPanel() {
		this(Unit.PX);	
	}
	
	public void showErrorPanel(String msg) {
		this.showErrorPanel(msg, null );	
	}
	public void showErrorPanel(String msg, Widget beforeWidget) {
		msgWidget = AonFloatingMessage.error(msg);
		showMessagesPanel(beforeWidget);
	}
	
	public void showInfoPanel(String msg) {
		this.showInfoPanel(msg, null );	
	}
	public void showInfoPanel(String msg, Widget beforeWidget) {
		msgWidget = AonFloatingMessage.info(msg);
		showMessagesPanel(beforeWidget);
	}

	public void showMessagesPanel(Widget beforeWidget) {
		hideInfoPanel();
		insertNorth(msgWidget, 60 , (beforeWidget == null ? dummyNorthPanel :  beforeWidget) );
		forceLayout();
		msgWidget.addCloseHandler(event -> hideErrorPanel());
	}

	public void hideErrorPanel() {
		hideMessqagesPanel();
	}
	public void hideInfoPanel() {
		hideMessqagesPanel();
	}
	
	public void hideMessqagesPanel() {
		if (msgWidget != null) {
			remove(msgWidget);
		}
		forceLayout();
	}
	

}
