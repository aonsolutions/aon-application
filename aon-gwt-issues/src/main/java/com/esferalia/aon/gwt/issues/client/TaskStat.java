package com.esferalia.aon.gwt.issues.client;

import com.esferalia.aon.gwt.api.client.incidence.Incidence;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.shared.AonData;
import com.esferalia.aon.gwt.issues.client.css.AonGwtIssuesResources;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;

public class TaskStat implements EntryPoint {
	
	AonData aonData;
	private Incidence incidence;

	public TaskStat(AonData aonData) {
		this.aonData = aonData;
	}
	
	public TaskStat() {

	}
	
	@Override
	public void onModuleLoad() {
		GWT.<AonGwtIssuesResources> create(AonGwtIssuesResources.class).css().ensureInjected();
		RootLayoutPanel root = RootLayoutPanel.get(aonData.getRootPanel());
		incidence = new Incidence(GWT.getModuleBaseURL(), aonData.getMd5(),
				aonData.getUser().getLogin(), aonData.getUser().getLogin(), aonData.getDomain().getName(), aonData.getDomain().getId());
		root.add(new StatPanel(incidence));
	}
}
