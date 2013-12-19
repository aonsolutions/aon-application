package com.esferalia.aon.gwt.fiscal.client;


import static com.google.gwt.dom.client.Style.Unit.PX;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;

public final class RootLayoutPanel extends LayoutPanel {

	public static RootLayoutPanel get(String id) {
		RootLayoutPanel rootLayoutPanel = new RootLayoutPanel();
		RootPanel.get(id).add(rootLayoutPanel);
		return rootLayoutPanel;
	}

	private RootLayoutPanel() {
		Window.addResizeHandler(new ResizeHandler() {
			public void onResize(ResizeEvent event) {
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
		
		int top = parent.getOffsetTop();
		int left = parent.getOffsetLeft();
		
		Style style = elem.getStyle();
		style.setPosition(Position.ABSOLUTE);
		style.setLeft(left, PX);
		style.setTop(top, PX);
		style.setRight(0, PX);
		style.setBottom(0, PX);
	}
	
}