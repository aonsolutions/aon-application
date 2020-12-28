package com.esferalia.aon.gwt.fiscal.client.accounting;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.ModuleCallback;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomPopup;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.fiscal.client.AccountEntrySelectionEvent;
import com.esferalia.aon.gwt.fiscal.client.AccountEntrySelectionHandler;
import com.esferalia.aon.gwt.fiscal.client.MainEntryPoint;
import com.esferalia.aon.gwt.fiscal.client.accounting.PrintReportDialog.IPrintReportDialogCallback;
import com.esferalia.aon.gwt.fiscal.client.accounting.panel.TrialBalancePanelReport;
import com.esferalia.aon.gwt.fiscal.shared.IRequestParamsNames;
import com.esferalia.aon.gwt.fiscal.shared.JsonParams;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountingReportParams;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.IAccountEntryWrapper;
import com.esferalia.aon.occam.api.model.ReportMetadata;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;

public class AccountTrialBalanceReport extends MainEntryPoint {
	
	private static final String ACC_TRIAL_BALANCE_REPORT_PRINT = "/aon_gwt_fiscal/roms/AccountTrialBalanceReportExcelPrint";
	private static final String ACC_TRIAL_BALANCE_REPORT_PDF_PRINT  = "/aon_gwt_fiscal/roms/AccountTrialBalanceReportPDFPrint";
	private static final String ACC_LEDGER_REPORT_EXCEL_PRINT = "/aon_gwt_fiscal/roms/AccountLedgerReportExcelPrint";
	private static CommonServiceAsync commonService;
	
	@Override
	public void onModuleLoad() {
		RootLayoutPanel root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");
		AccountingReportModuleOptions options = new AccountingReportModuleOptions();
		options.setParentWidget(root);
		options.setDomainName(getCurrentDomainName());
		options.setDomain(getCurrentDomain());
		options.setUser(getCurrentUser());
		this.onModuleLoad( options );
	}
	
	public void onModuleLoad( final AccountingReportModuleOptions options ) {
		if (options.getConfiguration() == null) {
			CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
			commonService = new CommonServiceAsyncDecorator(commonServiceRaw);
			commonService.getAonConfiguration(options.getDomainName(), options.getDomain(), options.getUser()
				,new AsyncCallback<AonConfiguration>() {
					@Override
					public void onSuccess(AonConfiguration result) {
						loadModule(options.setConfiguration(result));
					}
	
					@Override
					public void onFailure(Throwable caught) {
						Window.alert("Error al leer la configuraci\u00F3n");
					}
				});
		} else {
			loadModule(options);
		}
	}
	
	public void loadModule( final AccountingReportModuleOptions options ) {
		AON.ensureInjected();
		DockLayoutPanel dockLayoutPanel = new DockLayoutPanel(Unit.PX);
		TrialBalancePanelReport panel = new TrialBalancePanelReport(options);

		if (!options.isGuest()) {
			panel.addSelectionHandler(new AccountEntrySelectionHandler() {
				
				@Override
				public void onSelection(AccountEntrySelectionEvent event) {
					AccountEntry entry = event.getSelectedItem();  
					showEntry(options, entry.getId(), event.getCallback());
					
				}
			});
		}
		
		
		AonToolbar toolbar = new AonToolbar(AON.MSG.trialBalabce());
		
		FormPanel diskForm = new FormPanel("_blank");
		diskForm.setMethod(FormPanel.METHOD_POST);
		Hidden accountReportParamsHidden = new Hidden(IRequestParamsNames.ACCOUNT_REPORT_PARAMS);
		Hidden domainIdHidden = new Hidden(IRequestParamsNames.DOMAIN_ID);
		Hidden domainNameHidden= new Hidden(IRequestParamsNames.DOMAIN_NAME);
		Hidden userHidden = new Hidden(IRequestParamsNames.USER);
		FlowPanel formFlowPanel = new FlowPanel();
		diskForm.add(formFlowPanel);
		formFlowPanel.add(accountReportParamsHidden);
		formFlowPanel.add(domainIdHidden);
		formFlowPanel.add(domainNameHidden);
		formFlowPanel.add(userHidden);
		toolbar.add(diskForm);

		FlowPanel buttonContainer = new FlowPanel();
		
		final AonToolbarButton pdfReport = new AonToolbarButton( AON.MSG.printPDF(),AON.CSS.aonIconPdf() );
		pdfReport.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				AccountingReportParams params = panel.getWidgetParams(options);
				ReportMetadata metadata = new ReportMetadata().setTitle("Balance de sumas y saldos");
				PrintReportDialog dialog = new PrintReportDialog(metadata
						, new IPrintReportDialogCallback() {
							
							@Override
							public void onError(String msg) {
								Window.alert(msg);
							}
							
							@Override
							public void onCancel() {}
							
							@Override
							public void onAccept(ReportMetadata metadata) {
								params.setTitle(metadata.getTitle());
								params.setSubject(metadata.getSubject());
								params.setShowCover(metadata.isShowCover());
								params.setPageOffset(metadata.getPageOffset());
								params.setPageOffsetText(metadata.getPageOffsetText());
								params.setHideFilter(metadata.isHideFilter());
								params.setHeaderText(metadata.getHeaderText());
								params.setHideDateTimeOnFooter(metadata.isHideDateTimeOnFooter());
								params.setFooterText(metadata.getFooterText());

								diskForm.setAction(GWT.getHostPageBaseURL() + ACC_TRIAL_BALANCE_REPORT_PDF_PRINT);
								accountReportParamsHidden.setValue(JsonParams.convert(params));
								domainIdHidden.setValue(String.valueOf(options.getDomain()));
								domainNameHidden.setValue(options.getDomainName());
								userHidden.setValue(options.getUser());
								diskForm.submit();
							}
						});
				dialog.center();
				dialog.show();
			}
		});
		buttonContainer.add(pdfReport);

		final AonToolbarButton print = new AonToolbarButton( AON.MSG.printExcel(),AON.CSS.aonIconExcel() );
		print.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				diskForm.setAction(GWT.getHostPageBaseURL() + ACC_TRIAL_BALANCE_REPORT_PRINT);
				accountReportParamsHidden.setValue(JsonParams.convert(panel.getWidgetParams(options)));
				domainIdHidden.setValue(String.valueOf(options.getDomain()));
				domainNameHidden.setValue(options.getDomainName());
				userHidden.setValue(options.getUser());
				diskForm.submit();
			}
		});
		buttonContainer.add(print);

		final AonToolbarButton ledger = new AonToolbarButton( "Desglose (Mayor)",AON.CSS.aonIconList() );
		ledger.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				diskForm.setAction(GWT.getHostPageBaseURL() + ACC_LEDGER_REPORT_EXCEL_PRINT);
				accountReportParamsHidden.setValue(JsonParams.convert(panel.getWidgetParams(options)));
				domainIdHidden.setValue(String.valueOf(options.getDomain()));
				domainNameHidden.setValue(options.getDomainName());
				userHidden.setValue(options.getUser());
				diskForm.submit();
			}
		});
		buttonContainer.add(ledger);

		
		toolbar.add(buttonContainer);
		
		dockLayoutPanel.addNorth(toolbar, AonToolbar.HEIGTH);
		dockLayoutPanel.add( panel );
		
		RootLayoutPanel root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");
		root.add(dockLayoutPanel);
	}

	private void showEntry(final AccountingReportModuleOptions options,Integer entryId, ModuleCallback callback) {
		AonCustomPopup entryDialog = new AonCustomPopup();
		entryDialog.setWidth((Window.getClientWidth() - 100) + "px");
		entryDialog.setHeight((Window.getClientHeight() - 100) + "px");
		entryDialog.setAnimationEnabled(true);
		entryDialog.setGlassEnabled(true);
		entryDialog.setModal(true);
		entryDialog.setCaption(AON.MSG.accountEntries());
		AccountEntryModuleTEDI module = new AccountEntryModuleTEDI();
		module.onModuleLoad(new AccountEntryModuleOptions()
			.setParentWidget(entryDialog)
			.setDomainName(options.getDomainName())
			.setUser(options.getUser())
			.setDomain(options.getDomain())
			.setAccountEntryId(entryId)
			.setSessionLogTabVisible(false)
			.setJournalTabVisible(false)
			.setExtraInfoTabVisible(false)
			.setBalancesSectionVisible(false)
			.setStatementTabVisible(false)
			.setExternalCallback(new ModuleCallback() {
				@Override
				public void onRemove(IAccountEntryWrapper removed) {
					entryDialog.hide();
					callback.onRemove(removed);
				}
				
				@Override
				public void onFailure(Throwable caught) {}
				
				@Override
				public void onExit() {
					entryDialog.hide();
				}
				
				@Override
				public void onChange(IAccountEntryWrapper changed) {
					entryDialog.hide();
					callback.onRemove(changed);
				}
			})
		);
		entryDialog.center();
		entryDialog.show();
	}
}
