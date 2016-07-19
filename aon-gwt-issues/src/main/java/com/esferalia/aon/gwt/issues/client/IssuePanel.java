package com.esferalia.aon.gwt.issues.client;

import com.esferalia.aon.gwt.api.client.incidence.JsIssue;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MaximizeEvent;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MinimizeEvent;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;


public class IssuePanel extends Composite{
	
	interface Binder extends UiBinder<Widget, IssuePanel> {}
	
	private static Binder binder = GWT.create(Binder.class);

	@UiField VerticalPanel headerVPanel;
	@UiField Label userLogged;
	@UiField Label typeLabel;
	@UiField Label priorityLabel;
	@UiField VerticalPanel labelsVPanel;
	@UiField VerticalPanel usersVPanel;
	@UiField FlowPanel historialVPanel;
	@UiField Button sendButton;
	@UiField Button duplicatedButton;
	@UiField Button closedButton;
	@UiField Button commentButton;
	@UiField TextArea commentTextArea;
	@UiField Button typeButton;
	@UiField Button priorityButton;
	@UiField Button tagButton;
	@UiField Button userButton;

	@UiField SplitLayoutPanel dockLayoutPanel;
	@UiField MinimizePanel footPanel;

	public IssuePanel(Issues parent, JsIssue issue) {
		initWidget(binder.createAndBindUi(this));		
	}
	
	@UiHandler("footPanel")
	void onFootMinimize(MinimizeEvent event) {
		closeFootPanel();
	}
	
	@UiHandler("footPanel")
	void onFootMaximize(MaximizeEvent event) {
		dockLayoutPanel.setWidgetSize(footPanel, Window.getClientHeight() / 2);
		dockLayoutPanel.animate(500);
	}
	
	private void closeFootPanel() {
		dockLayoutPanel.setWidgetSize(footPanel, 30);
		dockLayoutPanel.animate(500);
	}
	
}
