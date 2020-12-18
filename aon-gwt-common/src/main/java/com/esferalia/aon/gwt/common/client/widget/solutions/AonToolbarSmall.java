package com.esferalia.aon.gwt.common.client.widget.solutions;

import com.esferalia.aon.gwt.common.client.AON;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class AonToolbarSmall extends FlowPanel {

	public static final int HEIGTH = 50;
	
	private FlowPanel buttonContainer;
	private FlowPanel buttonPanel;
	private FlowPanel messagePanel;
	private Label title;
	
	public AonToolbarSmall( String name ) {
		super();
		setStyleName(AON.CSS.aonToolbarSmall());
		
		FlowPanel innerToolbar = new FlowPanel();
		innerToolbar.setStyleName(AON.CSS.aonToolbarSmallInner());
		
		FlowPanel titlePanel = new FlowPanel();
		titlePanel.addStyleName(AON.CSS.aonToolbarSmallTitleContainer());
		title = new Label( name );
		title.setStyleName(AON.CSS.aonToolbarSmallTitle());
		titlePanel.add( title );
		innerToolbar.add(titlePanel);

		messagePanel = new FlowPanel();
		messagePanel.setStyleName(AON.CSS.aonToolbarSmallMessageContainer());
		innerToolbar.add(messagePanel);
		
		buttonContainer = new FlowPanel();
		buttonContainer.setStyleName(AON.CSS.aonToolbarSmallButtonContainer());
		innerToolbar.add(buttonContainer);
		
		super.add(innerToolbar);
	}
	
	public AonToolbarSmall() {
		super();
		setStyleName(AON.CSS.aonToolbarSmall());
		
		FlowPanel innerToolbar = new FlowPanel();
		innerToolbar.setStyleName(AON.CSS.aonToolbarSmallInner());

		buttonPanel = new FlowPanel();
		innerToolbar.add(buttonPanel);
		
		messagePanel = new FlowPanel();
		messagePanel.setStyleName(AON.CSS.aonToolbarSmallMessageContainer());
		innerToolbar.add(messagePanel);
		
		buttonContainer = new FlowPanel();
		buttonContainer.setStyleName(AON.CSS.aonToolbarSmallButtonContainer());
		innerToolbar.add(buttonContainer);
		
		super.add(innerToolbar);
	}

	@Override
	public void add(IsWidget child) {
		buttonContainer.add(child);
	}
	@Override
	public void add(Widget w) {
		buttonContainer.add(w);
	}
	
	public void showInfoMessage( String msg ) {
		hideMessages();
		InlineLabel infoMsg = new InlineLabel( msg );
		infoMsg.setStyleName(AON.CSS.aonToolbarMessage());
		infoMsg.addStyleName(AON.CSS.aonToolbarInfoMessage());
		messagePanel.add(infoMsg);
	}
	
	public void showErrorMessage( String msg ) {
		hideMessages();
		InlineLabel errorMsg = new InlineLabel( msg );
		errorMsg.setStyleName(AON.CSS.aonToolbarMessage());
		errorMsg.addStyleName(AON.CSS.aonToolbarErrorMessage());
		messagePanel.add(errorMsg);
	}

	public void hideMessages( ) {
		messagePanel.clear();		
	}
	
	public void setTitle(String name) {
		title.setText(name);
	}
	
	public void setCenterWidget(Widget widget) {
		messagePanel.add(widget);
	}	
	
	public void setLeftWidget(Widget widget) {
		buttonPanel.add(widget);
	}	
	
}
