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
		hideErrorPanel();
		msgWidget = AonFloatingMessage.error(msg);
		insertNorth(msgWidget, 60 , (beforeWidget == null ? dummyNorthPanel :  beforeWidget) );
		forceLayout();
		msgWidget.addCloseHandler(event -> hideErrorPanel());
	}
	
	public void hideErrorPanel() {
		if (msgWidget != null) {
			remove(msgWidget);
		}
		forceLayout();
	}
	

}
