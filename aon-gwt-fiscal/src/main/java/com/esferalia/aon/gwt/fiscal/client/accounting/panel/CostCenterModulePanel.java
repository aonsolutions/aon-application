package com.esferalia.aon.gwt.fiscal.client.accounting.panel;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountModuleOptions;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;

public class CostCenterModulePanel extends DockLayoutPanel{

	private SimpleLayoutPanel centerPanel;
	private CostCenterPanel costCenterPanel;
	
	public CostCenterModulePanel(AccountModuleOptions options) {
		super(Unit.PX);
		addStyleName(AON.CSS.aonScrollArea());
		addStyleName(AON.CSS.aonMarginBottom());
		
		centerPanel = new SimpleLayoutPanel();
		add(centerPanel);
		onSearch( options );
	}
	
	public void onSearch( AccountModuleOptions options ) {
		costCenterPanel = new CostCenterPanel(options);
		centerPanel.setWidget(costCenterPanel);
	}

	public void createCostCenter(String description) {
		costCenterPanel.createCostCenter(description);
	}
	
}
