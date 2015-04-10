package com.esferalia.aon.gwt.common.client.widget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class DetailPanel extends ResizeComposite {

	interface Binder extends UiBinder<Widget, DetailPanel> { }
	private static final Binder binder = GWT.create(Binder.class);

	
	@UiField SimpleLayoutPanel panel;
	
	public DetailPanel() {
		initWidget(binder.createAndBindUi(this));
		
	}
	
	
	public void setWidget(Widget w){
		panel.setWidget(null);
		panel.setWidget(w);
	}
}
