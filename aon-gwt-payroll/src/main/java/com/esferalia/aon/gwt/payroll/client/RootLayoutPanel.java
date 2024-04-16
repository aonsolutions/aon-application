package com.esferalia.aon.gwt.payroll.client;

import static com.google.gwt.dom.client.Style.Unit.PX;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;

public class RootLayoutPanel extends LayoutPanel {

	public static native String getAonSolutions()
	/*-{
		return $wnd.localStorage.getItem("aon_solutions");
	}-*/;
	
	Boolean newAon = getAonSolutions() != null;
	 
	public static RootLayoutPanel get(String id) {
		
		RootLayoutPanel rootLayoutPanel = new RootLayoutPanel();
		RootPanel rootPanel = RootPanel.get(id);
		if ( rootPanel == null )
			rootPanel = RootPanel.get();
		
		rootPanel.add(rootLayoutPanel);
		return rootLayoutPanel;
	}

	private RootLayoutPanel() {
		Window.addResizeHandler(new ResizeHandler() {
			public void onResize(ResizeEvent event) {
				fillParent();
				RootLayoutPanel.this.onResize();
			}
		});
	}

	@Override
	protected void onLoad() {
		super.onLoad();
		fillParent();
	}

	private void fillParent() {
		
		Element elem = getElement();
		Element parent = elem.getParentElement();
		
		int top = !isNewAon() ? parent.getAbsoluteTop() : 0;
		int left = !isNewAon() ? parent.getAbsoluteLeft() : 0;
		
		Style style = elem.getStyle();
		style.setPosition(Position.ABSOLUTE);
		style.setLeft(left, PX);
		style.setTop(top, PX);
		style.setRight(0, PX);
		style.setBottom(0, PX);
	}
	
	public Boolean isNewAon() {
		return newAon;
	}
	
	public void setNewAon(Boolean newAon) {
		this.newAon = newAon;
	}

}