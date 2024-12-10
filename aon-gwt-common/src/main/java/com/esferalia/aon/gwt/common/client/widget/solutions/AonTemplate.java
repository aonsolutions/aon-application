package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.occam.api.model.Occam;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class AonTemplate implements EntryPoint {

	AonCustomDockLayout dockPanel;
	SimpleLayoutPanel content;
	String title;

	public AonTemplate() {

	}
	
	public AonTemplate(String title) {
		this.title = title;
	}
	
	@Override
	public void onModuleLoad() {
		dockPanel = new AonCustomDockLayout(getTitle(), false) {
			
			@Override
			protected void onClearFilter() {
				
			}
		};
		content = new SimpleLayoutPanel();
		dockPanel.add(content);
		RootLayoutPanel root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");
		root.add(dockPanel);
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public AonCustomDockLayout getPanel() {
		return dockPanel;
	}
	
	// TOOLBAR
	
	public AonToolbar getToolbar() {
		return getPanel().getToolbar();
	}
	
	public void addToolbarSearchBox(Widget advancedSearch, Consumer<String> onValueChangeFunction) {
		getPanel().addToolbarSearchBox(advancedSearch, onValueChangeFunction);
	}
	
	// -----
	
	protected SimpleLayoutPanel getContent() {
		return content;
	}
	
	protected void setContent(Widget widget) {
		content.setWidget(widget);
	}
	
	public Occam getOccam() {
		return new Occam()
			.setDomain(getCurrentDomain())
			.setDomainName(getCurrentDomainName())
			.setUser(getCurrentUser());
	}
	
	// ----- JS functions
	
	public static native Boolean isAonSolutions()
	/*-{
		var newAon = $wnd.localStorage.getItem("aon_solutions"); 
		return newAon ? true : false;
	}-*/;
	
	public static native String getToken()
	/*-{
		return $wnd.localStorage.getItem("aon_session_id");
	}-*/;	
		
	public static native String getCurrentDomainName()
	/*-{
		return $wnd.getCurrentDomainName();
	}-*/;

	public static native int getCurrentDomain()
	/*-{
		return $wnd.getCurrentDomain();
	}-*/;
	
	public static native String getCurrentUser()
	/*-{
		return $wnd.getCurrentUser();
	}-*/;
	
	public static native String getRootPanel()
	/*-{
		return $wnd.localStorage.getItem("rootPanel");
	}-*/;
}
