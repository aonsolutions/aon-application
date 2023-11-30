package com.esferalia.aon.gwt.fiscal.client.accounting;

import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonAccountPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonAccountPanel.AonAccountPanelCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.fiscal.client.MainEntryPoint;
import com.esferalia.aon.gwt.fiscal.client.accounting.panel.AccountModulePanel;
import com.esferalia.aon.gwt.fiscal.shared.IRequestParamsNames;
import com.esferalia.aon.gwt.fiscal.shared.JsonParams;
import com.esferalia.aon.occam.api.model.Account;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;

public class AccountModule extends MainEntryPoint {

	private static final String ACC_ACCOUNT_REPORT_PRINT = "/aon_gwt_fiscal/roms/AccountReportExcelPrint";
	
	private static final Logger LOGGER = Logger.getLogger(AccountModule.class.getName());
	static {
		LOGGER.addHandler( new ConsoleLogHandler() );
	}

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
		AccountModulePanel accountPanel = new AccountModulePanel( options );
		
		AonToolbar toolbar = new AonToolbar( "PLAN GENERAL CONTABLE" );
		FormPanel diskForm = new FormPanel("_blank");
		diskForm.setMethod(FormPanel.METHOD_POST);
		Hidden accountEntryParamsHidden = new Hidden(IRequestParamsNames.ACCOUNT_PARAMS);
		Hidden domainIdHidden = new Hidden(IRequestParamsNames.DOMAIN_ID);
		Hidden domainNameHidden= new Hidden(IRequestParamsNames.DOMAIN_NAME);
		Hidden userHidden = new Hidden(IRequestParamsNames.USER);
		FlowPanel formFlowPanel = new FlowPanel();
		diskForm.add(formFlowPanel);
		formFlowPanel.add(accountEntryParamsHidden);
		formFlowPanel.add(domainIdHidden);
		formFlowPanel.add(domainNameHidden);
		formFlowPanel.add(userHidden);
		toolbar.add(diskForm);

		final AonToolbarButton reset = new AonToolbarButton( AON.MSG.newAction(), AON.CSS.aonIconAdd() ,AonButton.AON_ACCESSKEY_RESET);
		reset.addClickHandler(event -> showAccountDialog( options, accountPanel ));
		toolbar.add(reset);

		final AonToolbarButton excel = new AonToolbarButton( AON.MSG.export() , AON.CSS.aonIconExcel());
		excel.addClickHandler(event -> {
			diskForm.setAction(GWT.getHostPageBaseURL() + ACC_ACCOUNT_REPORT_PRINT);
			accountEntryParamsHidden.setValue(JsonParams.convert(accountPanel.getWidgetParams( options )));
			domainIdHidden.setValue(String.valueOf(getCurrentDomain()));
			domainNameHidden.setValue(getCurrentDomainName());
			userHidden.setValue(getCurrentUser());
			diskForm.submit();
		});
		toolbar.add(excel);
		
		dockLayoutPanel.addNorth(toolbar, AonToolbar.HEIGTH);
		
		dockLayoutPanel.add( accountPanel );
		RootLayoutPanel root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");
		root.add(dockLayoutPanel);
	}

	private void showAccountDialog(AccountModuleOptions options, AccountModulePanel accountModulePanel) {
		final AonCustomDialog dialog = new AonCustomDialog();
		dialog.setCaption(AON.MSG.account());
		final AonAccountPanel accountPanel = new AonAccountPanel( options.getDomainName(), options.getDomain(), options.getUser(), null, new AonAccountPanelCallback() {
			
			@Override
			public void onCancel() {
				dialog.hide();
			}
			
			@Override
			public void onAccept(Account result) {
				dialog.hide();
				accountModulePanel.onSearch(options);
			}
		});
		
		dialog.add( accountPanel );
		dialog.center();
		dialog.show();
	}
	
}
