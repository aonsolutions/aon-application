package com.esferalia.aon.gwt.common.client.widget.solutions;

import com.esferalia.aon.gwt.common.client.AON;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.DockLayoutPanel;

public abstract class AonDockLayout extends DockLayoutPanel {

	private final String title;
	private AonToolbar toolbar;
	
	protected AonDockLayout(String title) {
		super(Unit.PX);
		AON.ensureInjected();
		this.title = title;
		resetWidget();
	}
	
	protected void resetWidget() {
		clear();
		toolbar = new AonToolbar(this.title);
		toolbar.addStyleName(AON.CSS.aonNoBorderToolbar());
		addNorth(toolbar, AonToolbar.HEIGTH);
	}
	
	protected void setSearchFilter(AonSearchFilter filter) {
		toolbar.showSearchPanel(filter);
	}
	
	public AonToolbar getToolbar() {
		return toolbar;
	}

	public void hideToolbarFilterMessages() {
		getToolbar().getMessagePanel().getElement().getStyle().setDisplay(Display.NONE);
		getToolbar().getFilterPanel().getElement().getStyle().setDisplay(Display.NONE);
	}

}
