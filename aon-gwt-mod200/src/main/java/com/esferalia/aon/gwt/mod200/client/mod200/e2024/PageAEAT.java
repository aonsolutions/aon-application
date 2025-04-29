package com.esferalia.aon.gwt.mod200.client.mod200.e2024;

import com.esferalia.aon.gwt.mod200.client.mod200.Model200AdmonPanel;
import com.esferalia.aon.gwt.mod200.client.mod200.e2024.Model2002024.Model2002024PageCallback;

public class PageAEAT extends PageAbs {
	
	Model200AdmonPanel admonPanel;

	public PageAEAT(Model2002024PageCallback callback) {		
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

