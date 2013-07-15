package com.esferalia.aon.gwt.payroll.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

public class ResultsPanel extends ResizeComposite {

	interface Binder extends UiBinder<Widget, ResultsPanel> {
	}

	private static final Binder binder = GWT.create(Binder.class);

	@UiField
	HTMLPanel htmlPanel;

	@UiField
	ScrollPanel scrollPanel;

	public ResultsPanel() {
		initWidget(binder.createAndBindUi(this));
	}
	
	public void clear() {
		htmlPanel.clear();
	}

	public void setHTML(String html) {
		htmlPanel.add(new HTML(html));
	}

	public void addHTML(String html) {
		htmlPanel.add(new HTML(html));
		scrollPanel.setVerticalScrollPosition(scrollPanel
				.getMaximumVerticalScrollPosition());
	}

}
