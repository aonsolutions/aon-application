package com.esferalia.aon.gwt.callcenter.client.callcenter;


import com.esferalia.aon.occam.api.model.callcenter.Issue;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;

public class NoticeForm extends ResizeComposite {

	interface PageBinder extends UiBinder<Widget, NoticeForm> {
	}

	private static final PageBinder pageBinder = GWT.create(PageBinder.class);
	
	Issue currentIssue;
	
	@UiField Panel basePanel;

	@UiField(provided = true) FlexTable table;
	
	public NoticeForm(Issue issue) {
		table = new FlexTable();
		
		Widget ui = pageBinder.createAndBindUi(this);
		initWidget(ui);
	}
	
	
	
}
