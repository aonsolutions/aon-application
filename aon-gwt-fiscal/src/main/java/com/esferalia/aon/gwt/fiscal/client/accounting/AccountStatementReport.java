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
import com.esferalia.aon.gwt.fiscal.client.accounting.panel.StatementPanelReport;
import com.esferalia.aon.gwt.fiscal.shared.IRequestParamsNames;
import com.esferalia.aon.gwt.fiscal.shared.JsonParams;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.IAccountEntryWrapper;
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

public class AccountStatementReport extends MainEntryPoint {
	
	private static final String ACC_STATEMENT_REPORT_PRINT = "/aon_gwt_fiscal/roms/AccountStatementReportExcelPrint";
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
		final AonToolbarButton filterButton = new AonToolbarButton("", AON.CSS.aonToolbarFilterContainer());
		StatementPanelReport panel = new StatementPanelReport(options, true) {
			@Override
			public void closeFilterPanel() {
				super.closeFilterPanel();
	        	filterButton.setHTML("<span class='material-symbols-outlined'>filter_alt</span>");
			}
			
			@Override
			public void openFilterPanel() {
				super.openFilterPanel();
	        	filterButton.setHTML("<span class='material-symbols-outlined'>filter_alt_off</span>");
			}
		};
		
		AonToolbar toolbar = new AonToolbar("Extracto de cuentas");
 
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
		
		final AonToolbarButton pdfPrint = new AonToolbarButton(AON.MSG.print(),AON.CSS.aonIconExcel());
		pdfPrint.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				diskForm.setAction(GWT.getHostPageBaseURL() + ACC_STATEMENT_REPORT_PRINT);
				accountReportParamsHidden.setValue(JsonParams.convert(panel.getWidgetParams(options)));
				domainIdHidden.setValue(String.valueOf(options.getDomain()));
				domainNameHidden.setValue(options.getDomainName());
				userHidden.setValue(options.getUser());
				diskForm.submit();
			}
		});
		buttonContainer.add(pdfPrint);
		
		filterButton.setHTML("<span class='material-icons'>filter_alt_off</span>");
		
		filterButton.addClickHandler(new ClickHandler() {

		    @Override
		    public void onClick(ClickEvent event) {
		        if (panel.isFilterPanelOpened()) {
		            panel.closeFilterPanel();
		        } else {
		            panel.openFilterPanel();

		        }
		    }
		});
		
		toolbar.add(buttonContainer);
		toolbar.add(filterButton);
		dockLayoutPanel.addNorth(toolbar, AonToolbar.HEIGTH);
		dockLayoutPanel.add( panel );
		
		if (!options.isAccountingGuest()) {
			panel.addSelectionHandler(new AccountEntrySelectionHandler() {
				
				@Override
				public void onSelection(AccountEntrySelectionEvent event) {
					AccountEntry entry = event.getSelectedItem();  
					showEntry(entry.getDomain(), entry.getId(), event.getCallback());
					
				}
			});
		}
		
		
		RootLayoutPanel root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");
		root.add(dockLayoutPanel);
	}

	private void showEntry(int domain,Integer entryId, ModuleCallback callback) {
		AonCustomPopup entryDialog = new AonCustomPopup();
		entryDialog.setWidth((Window.getClientWidth() - 100) + "px");
		entryDialog.setHeight((Window.getClientHeight() - 100) + "px");
		entryDialog.setAnimationEnabled(true);
		entryDialog.setGlassEnabled(true);
		entryDialog.setModal(true);
		entryDialog.setCaption(AON.MSG.accountEntries());
		AccountEntryModule module = new AccountEntryModule();
		module.onModuleLoad(new AccountEntryModuleOptions()
			.setParentWidget(entryDialog)
			.setDomainName(getCurrentDomainName())
			.setUser(getCurrentUser())
			.setDomain(domain)
			.setAccountEntryId(entryId)
			.setSessionLogTabVisible(false)
			.setJournalTabVisible(false)
			.setExtraInfoTabVisible(false)
			.setExternalCallback(new ModuleCallback() {
				private static final long serialVersionUID = -4973164342320754303L;

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
