package com.esferalia.aon.gwt.fiscal.client.accounting;

import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonInvestAssetPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonInvestAssetPanel.AonInvestAssetPanelCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.fiscal.client.MainEntryPoint;
import com.esferalia.aon.gwt.fiscal.client.accounting.panel.InvestAssetModulePanel;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.ui.DockLayoutPanel;

public class InvestAssetModule extends MainEntryPoint {
	
	private static final Logger LOGGER = Logger.getLogger(InvestAssetModule.class.getName());
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
		InvestAssetModulePanel investAssetPanel = new InvestAssetModulePanel( options );
		
		AonToolbar toolbar = new AonToolbar( "BIENES AFECTO O DE INVERSION" );
		
		final AonToolbarButton newButton = new AonToolbarButton( AON.MSG.newAction(), AON.CSS.aonIconAdd());
		newButton.addClickHandler(e -> showAccountDialog(investAssetPanel, options));
		toolbar.add(newButton);

		dockLayoutPanel.addNorth(toolbar, AonToolbar.HEIGTH);
		
		dockLayoutPanel.add( investAssetPanel );
		RootLayoutPanel root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");
		root.add(dockLayoutPanel);
	}

	private void showAccountDialog(InvestAssetModulePanel investAssetPanel, AccountModuleOptions options) {
		final AonCustomDialog dialog = new AonCustomDialog();
		dialog.setCaption(AON.MSG.investAssetPanel());
		final AonInvestAssetPanel accountPanel = new AonInvestAssetPanel( options.getDomainName(), options.getDomain(), options.getUser(), new AonInvestAssetPanelCallback() {
			
			@Override
			public void onCancel() {
				dialog.hide();
			}
			
			@Override
			public void onAccept() {
				dialog.hide();
				investAssetPanel.onSearch(options);
			}
		}) {

			@Override
			protected void onResize() {
				dialog.showLoaded();
			}};
		
		dialog.add( accountPanel );
		dialog.showLoaded();
	}
	
}
