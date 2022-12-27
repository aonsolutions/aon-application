package com.esferalia.aon.gwt.common.client.polymer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.occam.api.model.Occam;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class AonTemplate2 implements EntryPoint {
	
	interface Binder extends UiBinder<Widget, AonTemplate2> {

	}

	@UiField DockLayoutPanel dockLayoutPanel;
	@UiField SimpleLayoutPanel toolbar;
	@UiField SimpleLayoutPanel westContent;
	@UiField SimpleLayoutPanel content;
	
	public static native Boolean isAonSolutions()
	/*-{
		var newAon = $wnd.localStorage.getItem("aon_solutions"); 
		return newAon ? true : false;
	}-*/;
	
	public static native String getToken()
	/*-{
		return $wnd.localStorage.getItem("aon_session_id");
	}-*/;
	
	public static native String getCurrentUser()
	/*-{
		return $wnd.getCurrentUser();
	}-*/;
	
	
		
	public static native String getCurrentDomainName()
	/*-{
		return $wnd.getCurrentDomainName();
	}-*/;

	public static native int getCurrentDomain()
	/*-{
		return $wnd.getCurrentDomain();
	}-*/;
	
	public static native String getRootPanel()
	/*-{
		return $wnd.localStorage.getItem("rootPanel");
	}-*/;
	
	public Occam getOccam() {
		return new Occam()
			.setDomain(getCurrentDomain())
			.setDomainName(getCurrentDomainName())
			.setUser(getCurrentUser());
	}
	
	public AonTemplate2() {

	}
	
	@Override
	public void onModuleLoad() {
		AON.ensureInjected();
		Binder binder = GWT.create(Binder.class);
		Widget ui = binder.createAndBindUi(this);
		RootLayoutPanel root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");
		root.add(ui);
	}
	
	protected void setToolbar(Widget widget){
		toolbar.setWidget(widget);
	}
	
	protected void setWestContent(Widget widget){
		westContent.setWidget(widget);
	}
	
	protected void setContent(Widget widget) {
		content.setWidget(widget);
	}
	
	// ------------------- GETS 
	
	protected DockLayoutPanel getDockLayoutPanel() {
		return dockLayoutPanel;
	}
	
	protected SimpleLayoutPanel getToolbar() {
		return toolbar;
	}

	protected SimpleLayoutPanel getWestContent() {
		return westContent;
	}

	
	protected SimpleLayoutPanel getContent() {
		return content;
	}

}
