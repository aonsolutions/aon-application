package com.esferalia.aon.gwt.common.client.polymer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDockLayout;
import com.esferalia.aon.occam.api.model.Occam;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class AonTemplate3 implements EntryPoint {

	private AonCustomDockLayout dockLayoutPanel;
	private SimpleLayoutPanel westContent;
	private SimpleLayoutPanel content;

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

	public Occam getOccam() {
		return new Occam().setDomain(getCurrentDomain()).setDomainName(getCurrentDomainName())
				.setUser(getCurrentUser());
	}

	public AonTemplate3() {

	}

	@Override
	public void onModuleLoad() {
		AON.ensureInjected();

		dockLayoutPanel = new AonCustomDockLayout("Template") {

			@Override
			protected void onClearFilter() {
				// TODO Auto-generated method stub

			}
		};

		westContent = new SimpleLayoutPanel();
		content = new SimpleLayoutPanel();

		RootLayoutPanel root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");
		root.add(dockLayoutPanel);

		dockLayoutPanel.hideSearchWidget();
		
		dockLayoutPanel.addWest(westContent, 0);
		
		dockLayoutPanel.add(content);

	}

	protected void setToolbar(Widget widget) {
		dockLayoutPanel.addToolbarButton(widget);
	}

	protected void setWestContent(Widget widget) {
		westContent.setWidget(widget);
	}

	protected void setContent(Widget widget) {
		content.setWidget(widget);
	}

	// ------------------- GETS

	protected AonCustomDockLayout getDockLayoutPanel() {
		return dockLayoutPanel;
	}

	protected SimpleLayoutPanel getWestContent() {
		return westContent;
	}

	protected SimpleLayoutPanel getContent() {
		return content;
	}
}
