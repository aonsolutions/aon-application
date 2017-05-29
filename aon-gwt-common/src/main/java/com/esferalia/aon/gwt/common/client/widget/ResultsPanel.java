package com.esferalia.aon.gwt.common.client.widget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class ResultsPanel extends ResizeComposite implements ProvidesResize {



	interface Binder extends UiBinder<SimpleLayoutPanel, ResultsPanel> {
	}

	private static final Binder binder = GWT.create(Binder.class);


	@UiField 
	SimplePanel centerPanel;

	public ResultsPanel() {
		initWidget(binder.createAndBindUi(this));
	}
	
	public Widget getWidget(){
		return centerPanel.getWidget();
	}

	public Widget getChild() {
		return centerPanel.getWidget();
	}

	@Override
	public void setWidget(Widget child) {
		centerPanel.setWidget(child);
	}
	
	public void setHTML(String html) {
		// htmlPanel.add(new HTML(html));
	}

	public void addHTML(String html) {
		// htmlPanel.add(new HTML(html));
	}

	// ------------------------------------------------------------- UIHandlers

}
