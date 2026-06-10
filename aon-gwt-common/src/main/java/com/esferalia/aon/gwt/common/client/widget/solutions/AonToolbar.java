package com.esferalia.aon.gwt.common.client.widget.solutions;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.Timer;

public class AonToolbar extends FlowPanel {

	public static final int HEIGTH = 50;
	public static final int DEFAULT_DELAY = 3000;
	
	private FlowPanel buttonContainer;
	private FlowPanel messagePanel;
	private FlowPanel filterPanel;
	private FlowPanel searchPanel;
	private FlowPanel titlePanel;
	
	public AonToolbar( ) {
		this( AonStringUtils.EMPTY);
	}
	
	public AonToolbar( String name ) {
		super();
		setStyleName(AON.CSS.aonToolbar());
		
		FlowPanel innerToolbar = new FlowPanel();
		innerToolbar.setStyleName(AON.CSS.aonToolbarInner());

		buttonContainer = new FlowPanel();
		buttonContainer.setStyleName(AON.CSS.aonToolbarButtonContainer());
		innerToolbar.add(buttonContainer);
		
		messagePanel = new FlowPanel();
		messagePanel.setStyleName(AON.CSS.aonToolbarMessageContainer());
		innerToolbar.add(messagePanel);
		
		filterPanel = new FlowPanel();
		filterPanel.setStyleName(AON.CSS.aonToolbarFilterContainer());
		innerToolbar.add(filterPanel);
		
		FlowPanel searchTitleToolbar = new FlowPanel();
		searchTitleToolbar.addStyleName(AON.CSS.aonItemFlex());
		
		searchPanel = new FlowPanel();
		searchPanel.setStyleName(AON.CSS.aonToolbarSearchContainer());
		searchPanel.setVisible(false);
		searchTitleToolbar.add(searchPanel);
		
		titlePanel = new FlowPanel();
		titlePanel.addStyleName(AON.CSS.aonToolbarTitleContainer());
		titlePanel.addStyleName(AON.CSS.aonItemFlex());
		setTitle(name != null ? name : "");
		searchTitleToolbar.add(titlePanel);
		
		innerToolbar.add(searchTitleToolbar);

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
	
	public void addStart(Widget w) {
		buttonContainer.insert(w, 0);
	}
	
	public void showInfoMessage( String msg ) {
		showInfoMessage(msg, 0);
	}
	public void showInfoMessage( String msg, int delayMillis ) {
		hideMessages();
		InlineLabel infoMsg = new InlineLabel( msg );
		infoMsg.setStyleName(AON.CSS.aonToolbarMessage());
		infoMsg.addStyleName(AON.CSS.aonToolbarInfoMessage());
		messagePanel.add(infoMsg);
		if (delayMillis > 0) {
			new Timer() {
			    @Override
			    public void run() {
			    	hideMessages();
			    }
			}.schedule(delayMillis);
		}
	}
	
	public void showErrorMessage( String msg ) {
		hideMessages();
		InlineLabel errorMsg = new InlineLabel( msg );
		errorMsg.setStyleName(AON.CSS.aonToolbarMessage());
		errorMsg.addStyleName(AON.CSS.aonToolbarErrorMessage());
		messagePanel.add(errorMsg);
	}
	
	public void showSearchPanel(Widget widget) {
		searchPanel.add(widget);
		searchPanel.setVisible(true);
	}
	
	public void showFilterPanel (Widget widget) {
		filterPanel.add(widget);
	}

	public void hideMessages( ) {
		messagePanel.clear();		
	}
	
	public FlowPanel getMessagePanel() {
		return messagePanel;
	}
	
	public FlowPanel getFilterPanel() {
		return filterPanel;
	}
	
	public FlowPanel getButtonContainer() {
		return buttonContainer;
	}
	
	public FlowPanel getSearchPanel() {
		return searchPanel;
	}
	
	@Override
	public void setTitle(String name) {
		titlePanel.clear();
		name = AonStringUtils.lowerCase(name);
		Label title = new Label( AonStringUtils.abbreviate(name, 80));
		title.setTitle(name);
		title.setStyleName(AON.CSS.aonToolbarTitle());
		titlePanel.add( title );
	}
	
	public void setTitle(Widget widget) {
		titlePanel.clear();
		titlePanel.add( widget );
	}
	
	public void addFilterButton(Widget widget) {
		titlePanel.add(widget);
	}
	
	public void addTitleButton(Widget widget) {
		titlePanel.insert(widget, 0);
	}
	
	@Override
	public int getWidgetIndex(Widget child) {
		return buttonContainer.getWidgetIndex(child);
	}
	
}
