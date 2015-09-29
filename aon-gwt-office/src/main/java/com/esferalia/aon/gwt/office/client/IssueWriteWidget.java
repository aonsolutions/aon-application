package com.esferalia.aon.gwt.office.client;

import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.gwt.office.client.values.issues.IssueValue;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class IssueWriteWidget extends Composite {

	interface Listener {

		void onCommentButtonClick(IssueValue issueValue);

	}

	private static IssueWriteWidgetUiBinder uiBinder = GWT
			.create(IssueWriteWidgetUiBinder.class);

	interface IssueWriteWidgetUiBinder extends
			UiBinder<Widget, IssueWriteWidget> {
	}

	@UiField
	TabPanel tabPanel;
	@UiField
	TextArea commentTextArea;
	@UiField
	TextBox title;
	@UiField
	HorizontalPanel hPanel;

	@UiField
	Button commentButton;

	private List<Listener> listeners;

	public IssueWriteWidget() {
		initWidget(uiBinder.createAndBindUi(this));

		tabPanel.selectTab(0);
		this.listeners = new LinkedList<Listener>();
	}

	public void addListener(Listener listener) {
		listeners.add(listener);
	}

	public void removeListener(Listener listener) {
		listeners.remove(listener);
	}

	@UiHandler("commentButton")
	void onCommentButtonClick(ClickEvent event) {

		if (commentTextArea.getText().isEmpty() == false) {
			IssueValue issueValue = new IssueValue();
			issueValue.setTitle(title.getText());
			issueValue.setBody(commentTextArea.getText());
			addCommentButtonClickListener(issueValue);
		}
	}
	
	private void addCommentButtonClickListener(IssueValue issueValue) {
		
		for (Listener listener : listeners) 
			listener.onCommentButtonClick(issueValue);
	}

}
