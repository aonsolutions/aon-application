package com.esferalia.aon.gwt.fiscal.client.accounting;

import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.fiscal.client.MainEntryPoint;
import com.esferalia.aon.gwt.fiscal.client.accounting.panel.CostCenterModulePanel;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.ui.DockLayoutPanel;

public class CostCenterModule extends MainEntryPoint {
	
	// ----- LOGGER
	
	private static final Logger LOGGER = Logger.getLogger(CostCenterModule.class.getName());
	static { LOGGER.addHandler( new ConsoleLogHandler() ); }

	@Override
	public void onModuleLoad() {
		RootLayoutPanel root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");
		AccountModuleOptions options = new AccountModuleOptions();
		options.setParentWidget(root);
		options.setDomainName(getCurrentDomainName());
		options.setDomain(getCurrentDomain());
		options.setUser(getCurrentUser());
		this.onModuleLoad( options );
	}
	
	public void onModuleLoad( final AccountModuleOptions options ) {
		
		AON.ensureInjected();
		DockLayoutPanel dockLayoutPanel = new DockLayoutPanel(Unit.PX);
		CostCenterModulePanel accountPanel = new CostCenterModulePanel( options );
		
		AonToolbar toolbar = new AonToolbar( "CENTRO DE COSTES" );

		final AonToolbarButton addButton = new AonToolbarButton( AON.MSG.newAction(), AON.CSS.aonIconAdd());
		addButton.addClickHandler(e -> {
			new CostCenterDialog() {
				
				@Override
				protected void onAccept(String description) {
					accountPanel.createCostCenter(description);
				}
			};
		});
		toolbar.add(addButton);

		dockLayoutPanel.addNorth(toolbar, AonToolbar.HEIGTH);
		
		dockLayoutPanel.add( accountPanel );
		RootLayoutPanel root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");
		root.add(dockLayoutPanel);
	}
	
}
