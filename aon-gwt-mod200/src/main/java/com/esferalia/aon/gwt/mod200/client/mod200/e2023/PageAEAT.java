package com.esferalia.aon.gwt.mod200.client.mod200.e2023;

import com.esferalia.aon.gwt.mod200.client.mod200.Model200AdmonPanel;
import com.esferalia.aon.gwt.mod200.client.mod200.e2023.Model2002023.Model2002023PageCallback;

public class PageAEAT extends PageAbs {
	
	Model200AdmonPanel admonPanel;

	public PageAEAT(Model2002023PageCallback callback) {		
		super();
		admonPanel = new Model200AdmonPanel(callback);		
		initWidget(admonPanel);
	}

	@Override
	protected void initializeTable() {				
	}
	
	@Override
	protected void setEnabled() {
		admonPanel.manageLinks();
	}
	
	protected void cleanViewers() {
		admonPanel.cleanViewers();
	}
	
}
