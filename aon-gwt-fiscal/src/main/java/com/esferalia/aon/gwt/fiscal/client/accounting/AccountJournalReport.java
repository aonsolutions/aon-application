package com.esferalia.aon.gwt.fiscal.client.accounting;

import java.util.logging.Logger;

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
import com.esferalia.aon.gwt.fiscal.client.accounting.panel.JournalPanel;
import com.esferalia.aon.gwt.fiscal.client.accounting.panel.JournalPanelReport;
import com.esferalia.aon.gwt.fiscal.shared.IRequestParamsNames;
import com.esferalia.aon.gwt.fiscal.shared.JsonParams;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountEntryParams;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.IAccountEntryWrapper;
import com.esferalia.aon.occam.api.model.ReportMetadata;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;

public class AccountJournalReport extends MainEntryPoint {
	
	private static final Logger LOGGER = Logger.getLogger(JournalPanel.class.getName());
	static {
		LOGGER.addHandler( new ConsoleLogHandler() );
	}
	private static CommonServiceAsync commonService;
	
	private static final String ACC_JORNAL_REPORT_EXCEL_PRINT = "/aon_gwt_fiscal/roms/AccountJournalReportExcelPrint";
	private static final String ACC_JORNAL_REPORT_PDF_PRINT = "/aon_gwt_fiscal/roms/AccountJournalReportPDFPrint";
	private static final String ACC_JORNAL_FLAT_REPORT_PRINT = "/aon_gwt_fiscal/roms/AccountJournalFlatReportExcelPrint";
	
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
		JournalPanelReport panel = new JournalPanelReport( options );
		
		AonToolbar toolbar = new AonToolbar(AON.MSG.journalBook());
		FormPanel diskForm = new FormPanel("_blank");
		diskForm.setMethod(FormPanel.METHOD_POST);
		Hidden accountEntryParamsHidden = new Hidden(IRequestParamsNames.ACCOUNT_ENTRY_PARAMS);
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

		final AonToolbarButton pdf = new AonToolbarButton(AON.MSG.printPDF(), AON.CSS.aonIconPdf());
		pdf.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				AccountEntryParams params = panel.getWidgetParams( options );
				ReportMetadata metadata = new ReportMetadata().setTitle("Listado diario de movimientos");
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

								diskForm.setAction(GWT.getHostPageBaseURL() + ACC_JORNAL_REPORT_PDF_PRINT);
								accountEntryParamsHidden.setValue(JsonParams.convert(params));
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
		pdf.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
			}
		});
		toolbar.add(pdf);

		final AonToolbarButton print = new AonToolbarButton(AON.MSG.printExcel(),AON.CSS.aonIconExcel());
		print.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				diskForm.setAction(GWT.getHostPageBaseURL() + ACC_JORNAL_REPORT_EXCEL_PRINT);
				accountEntryParamsHidden.setValue(JsonParams.convert(panel.getWidgetParams(options)));
				domainIdHidden.setValue(String.valueOf(options.getDomain()));
				domainNameHidden.setValue(options.getDomainName());
				userHidden.setValue(options.getUser());
				diskForm.submit();
			}
		});
		toolbar.add(print);

		final AonToolbarButton excel = new AonToolbarButton(AON.MSG.export(),AON.CSS.aonIconDownload());
		excel.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				diskForm.setAction(GWT.getHostPageBaseURL() + ACC_JORNAL_FLAT_REPORT_PRINT);
				accountEntryParamsHidden.setValue(JsonParams.convert(panel.getWidgetParams(options)));
				domainIdHidden.setValue(String.valueOf(options.getDomain()));
				domainNameHidden.setValue(options.getDomainName());
				userHidden.setValue(options.getUser());
				diskForm.submit();
			}
		});
		toolbar.add(excel);

		dockLayoutPanel.addNorth(toolbar, AonToolbar.HEIGTH);
		dockLayoutPanel.add( panel );
		
		if (!options.isGuest()) {
			panel.addSelectionHandler(new AccountEntrySelectionHandler() {
				
				@Override
				public void onSelection(AccountEntrySelectionEvent event) {
					AccountEntry entry = event.getSelectedItem();
					showEntry(options,entry.getDomain(), entry.getId(), event.getCallback());
					
				}
			});
		}
		
		RootLayoutPanel root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");
		root.add(dockLayoutPanel);
	}
	
	private void showEntry(AccountingReportModuleOptions options, int domain,Integer entryId, ModuleCallback moduleCallback) {
		AonCustomPopup entryDialog = new AonCustomPopup();
		entryDialog.setWidth((Window.getClientWidth() - 100) + "px");
		entryDialog.setHeight((Window.getClientHeight() - 100) + "px");
		entryDialog.setAnimationEnabled(true);
		entryDialog.setGlassEnabled(true);
		entryDialog.setModal(true);
		entryDialog.setCaption(AON.MSG.accountEntries());
		AccountEntryModuleTEDI module = new AccountEntryModuleTEDI();
		module.onModuleLoad(
			new AccountEntryModuleOptions()
			.setParentWidget(entryDialog)
			.setDomainName(options.getDomainName())
			.setUser(options.getUser())
			.setDomain(domain)
			.setAccountEntryId(entryId)
			.setJournalTabVisible(false)
			.setExtraInfoTabVisible(false)
			.setBalancesSectionVisible(false)
			.setStatementTabVisible(false)
			.setExternalCallback(new ModuleCallback() {
				@Override
				public void onRemove(IAccountEntryWrapper removed) {
					entryDialog.hide();
					moduleCallback.onRemove(removed);
				}
				
				@Override
				public void onFailure(Throwable caught) {
					moduleCallback.onFailure(caught);
				}
				
				@Override
				public void onExit() {
					entryDialog.hide();
					moduleCallback.onExit();
				}
				
				@Override
				public void onChange(IAccountEntryWrapper changed) {
					entryDialog.hide();
					moduleCallback.onChange(changed);
				}
			})
		);
		entryDialog.center();
		entryDialog.show();
	}
 
}
