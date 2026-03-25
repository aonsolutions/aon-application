package com.esferalia.aon.gwt.mod200.client.mod200.e2025;

import com.esferalia.aon.gwt.mod200.client.mod200.Model200AdmonPanel;
import com.esferalia.aon.gwt.mod200.client.mod200.e2025.Model2002025.Model2002025PageCallback;

public class PageAEAT extends PageAbs {
	
	Model200AdmonPanel admonPanel;

	public PageAEAT(Model2002025PageCallback callback) {		
		super();
		admonPanel = new Model200AdmonPanel(callback);		
		initWidget(admonPanel);
	}

	@Override
	protected void initializeTable() {
		// DO NOTHING
	}
	
	@Override
	protected void setEnabled() {
		admonPanel.manageLinks();
	}
	
	protected void cleanViewers() {
		admonPanel.cleanViewers();
	}
	
}