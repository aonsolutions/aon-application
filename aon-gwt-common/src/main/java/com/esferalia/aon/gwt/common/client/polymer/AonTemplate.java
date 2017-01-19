package com.esferalia.aon.gwt.common.client.polymer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class AonTemplate implements EntryPoint {
	
	interface Binder extends UiBinder<Widget, AonTemplate> {

	}

	@UiField DockLayoutPanel dockLayoutPanel;
	@UiField DockLayoutPanel contentDockLayoutPanel;
	@UiField SimpleLayoutPanel toolbar;
	@UiField SimpleLayoutPanel westContent;
	@UiField SimpleLayoutPanel northContent;
	@UiField SimpleLayoutPanel content;
	@UiField SimpleLayoutPanel southContent;
		
	public static native String getCurrentDomainName()
	/*-{
		return $wnd.getCurrentDomainName();
	}-*/;

	public static native int getCurrentDomain()
	/*-{
		return $wnd.getCurrentDomain();
	}-*/;
	
	public AonTemplate() {

	}
	
	@Override
	public void onModuleLoad() {
		AON.ensureInjected();
		Binder binder = GWT.create(Binder.class);
		Widget ui = binder.createAndBindUi(this);
		RootLayoutPanel root = RootLayoutPanel.get("rootPanel");
		root.add(ui);
	}
	
	protected void setToolbar(Widget widget){
		toolbar.setWidget(widget);
	}
	
	protected void setWestContent(Widget widget){
		westContent.setWidget(widget);
	}
	
	protected void setNorthContent(Widget widget){
		northContent.setWidget(widget);
	}
	
	protected void setContent(Widget widget) {
		content.setWidget(widget);
	}
	
	protected void setSouthContent(Widget widget) {
		content.setWidget(widget);
	}
	
	// ------------------- GETS 
	
	protected DockLayoutPanel getDockLayoutPanel() {
		return dockLayoutPanel;
	}
	
	protected DockLayoutPanel getContentDockLayoutPanel() {
		return contentDockLayoutPanel;
	}
	
	protected SimpleLayoutPanel getToolbar() {
		return toolbar;
	}

	protected SimpleLayoutPanel getWestContent() {
		return westContent;
	}
	
	protected SimpleLayoutPanel getNorthContent() {
		return northContent;
	}
	
	protected SimpleLayoutPanel getContent() {
		return content;
	}
	
	protected SimpleLayoutPanel getSouthContent() {
		return southContent;
	}
}
