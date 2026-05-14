package com.esferalia.aon.gwt.fiscal.client.invoice.console;


import static com.esferalia.aon.gwt.fiscal.client.EntryPointUtils.getCurrentDomain;
import static com.esferalia.aon.gwt.fiscal.client.EntryPointUtils.getCurrentDomainName;
import static com.esferalia.aon.gwt.fiscal.client.EntryPointUtils.getCurrentUser;
import static com.esferalia.aon.gwt.fiscal.client.EntryPointUtils.getRootPanel;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.Wnd;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.fiscal.client.invoice.InvoiceModuleOptions;
import com.esferalia.aon.gwt.fiscal.client.invoice.console.InvoiceConsoleToolbar.ToolbarAsyncCallback;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceConsoleParams;
import com.esferalia.aon.occam.api.model.invoice.InvoiceConsoleAnalysis;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;

public class InvoiceConsoleModule  implements EntryPoint {
	
	private static final Logger LOGGER = Logger.getLogger( InvoiceConsoleModule.class.getName() );  

	private static final String AON_INVOICE_CONSOLE_FILTER_PANEL_STYLE = "aon-invoice-console-filter-panel";

	private static final String AON_INVOICE_CONSOLE_TOOLBAR_STYLE = "aon-invoice-console-toolbar";

	private static final String AON_INVOICE_CONSOLE_PANEL_STYLE = "aon-invoice-console-panel";

	private static final CommonServiceAsync COMMON_SERVICE;
	static {
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw);
	}

	static final InvoiceConsoleServiceAsync INVOICE_SERVICE;
	static {
		InvoiceConsoleServiceAsync fiscalServiceRaw = GWT.create(InvoiceConsoleService.class);
		INVOICE_SERVICE = new InvoiceConsoleAsyncDecorator(fiscalServiceRaw);
	}
	private static final int FILTER_WIDTH = 300;

	private AonLayoutPanel aonLayoutPanel;
	private InvoiceConsoleToolbar toolbar;
	private SimpleLayoutPanel content;
	private InvoiceConsoleFootPanel footPanel;
	
	private final InvoiceConsoleSelectionHandler selectionHandler = new InvoiceConsoleSelectionHandler();

	@Override
	public void onModuleLoad() {
		RootLayoutPanel root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");
		InvoiceModuleOptions opts = new InvoiceModuleOptions();
		opts.setParentWidget(root);
		opts.setDomainName(getCurrentDomainName());
		opts.setDomain(getCurrentDomain());
		opts.setUser(getCurrentUser());
		this.onModuleLoad( opts );
	}
	
	public void onModuleLoad( InvoiceModuleOptions opts ) {
		AON.ensureInjected();
		COMMON_SERVICE.getAonConfiguration(opts.getOccam(),new AsyncCallback<AonConfiguration>() {
			@Override
			public void onSuccess(AonConfiguration result) {
				opts.setConfiguration(result);
				loadModule( opts );
			}

			@Override
			public void onFailure(Throwable caught) {
				Window.alert( AON.MSG.loadError("Operation Report"));
			}
		});

	}
	
	private void loadModule( InvoiceModuleOptions opts ) {
		AonLayoutPanel aonLayoutPanel = new AonLayoutPanel(Unit.PX);
		aonLayoutPanel.addStyleName(AON_INVOICE_CONSOLE_PANEL_STYLE);
		
		toolbar = new InvoiceConsoleToolbar(opts, selectionHandler);
		toolbar.addStyleName(AON_INVOICE_CONSOLE_TOOLBAR_STYLE);
		
		
		aonLayoutPanel.addNorth(toolbar, AonToolbar.HEIGTH);
		
		InvoiceConsoleFilter filterPanel = new InvoiceConsoleFilter(opts);
		filterPanel.addStyleName(AON_INVOICE_CONSOLE_FILTER_PANEL_STYLE);
		filterPanel.addValueChangeHandler(e -> search(opts, e.getValue()) );
		aonLayoutPanel.addWest(filterPanel, FILTER_WIDTH);
		
		aonLayoutPanel.addAttachHandler(e -> {
			Wnd.consoleLog("InvoiceConsoleModule attached, adjusting layout...");
			
			Wnd.getCSSOptionalVariable(toolbar, "height-adjust")
			.map( AonNumberUtils::toInteger ).filter(Objects::nonNull)
			.ifPresent( height -> aonLayoutPanel.setWidgetSize(toolbar, height) );

			Wnd.getCSSOptionalVariable(filterPanel, "width-adjust")
			.map( AonNumberUtils::toInteger ).filter(Objects::nonNull)
			.ifPresent( width -> aonLayoutPanel.setWidgetSize(filterPanel, width) );
		
		});

		toolbar.addClickHandlerToRefresh(e -> search(opts, filterPanel.getWidgetParams(opts) ) );
		toolbar.addClickHandlerToWizard(e -> analyze(opts, filterPanel.getWidgetParams(opts) ) );
		
		toolbar.addClickHandlerToShowFilter(e -> {
			aonLayoutPanel.setWidgetHidden(filterPanel, false);
			
			int filterWidth =
			Wnd.getCSSOptionalVariable(filterPanel, "width-adjust")
			.map( AonNumberUtils::toInteger ).filter(Objects::nonNull).orElse(FILTER_WIDTH);

			aonLayoutPanel.setWidgetSize(filterPanel, filterWidth);
			aonLayoutPanel.animate(200); 
		});
		
		toolbar.addClickHandlerToHideFilter(e -> {
			aonLayoutPanel.setWidgetSize(filterPanel, 0);
			aonLayoutPanel.animate(200);
		});
		
		footPanel = new InvoiceConsoleFootPanel();
		footPanel.addMinimizeHandler(event -> closeFootPanel());
		footPanel.addMaximizeHandler(event -> openFootPanel());
		footPanel.addOpenIfNeededHandler( event -> openFootPanelIfNeeded());
		aonLayoutPanel.addSouth(footPanel, 30);

		content = new SimpleLayoutPanel();
		content.setStyleName(AON.CSS.aonSelector());
		aonLayoutPanel.add(content);
		opts.getParentWidget().add(aonLayoutPanel);

		filterPanel.initialize(opts);
		
	}
	
	private void closeFootPanel() {
		aonLayoutPanel.setWidgetSize(footPanel, 30);
		aonLayoutPanel.animate(500);
	}
	private void openFootPanel() {
		int effectiveHeigth = 3;
		aonLayoutPanel.setWidgetSize(footPanel, Window.getClientHeight() / effectiveHeigth);
		aonLayoutPanel.animate(500);
	}
	private void openFootPanelIfNeeded() {
		if (aonLayoutPanel.getWidgetSize(footPanel) <= 30) {
			openFootPanel();
		}
	}
	
	private void search(InvoiceModuleOptions opts, InvoiceConsoleParams params) {
		content.clear();
		selectionHandler.clean();
		toolbar.refresh( opts );
		InvoiceConsoleTable table = new InvoiceConsoleTable(opts, params, new ToolbarAsyncCallback() {
			@Override public void onStartRunning() 	{ toolbar.startRun(AON.MSG.loading()); }
			@Override public void onEndRunning() 	{ toolbar.endRun();  }
		});
		content.setWidget(table);
		table.addInvoiceCheckedHandler(e -> selectionHandler.select( e.getInvoice() ) );
		table.addInvoiceUncheckedHandler(e -> selectionHandler.unselect( e.getInvoice() ) );
		table.addInvoiceRecordHandler(e -> record(opts, e.getInvoice() ));
	}
	private void record(InvoiceModuleOptions opts, Invoice inv) {
		toolbar.startRun(AON.MSG.recording());
		INVOICE_SERVICE.record(opts.getOccam(), inv, new AsyncCallback<AccountEntry>() {
			@Override
			public void onSuccess(AccountEntry result) {
				InvoiceConsoleAccountEntryPanel icaep = (InvoiceConsoleAccountEntryPanel) 
					footPanel.addWidget( "Asiento", InvoiceConsoleAccountEntryPanel::new );
				icaep.paint( opts, inv, result );
				toolbar.endRun();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				String msg = "Error analizando facturas";
				LOGGER.log( Level.SEVERE, msg, caught );
				toolbar.endRun();
				toolbar.showErrorMessage( msg + " [" + caught.getMessage() + "]" );
			}
		});
	}
	private void analyze(InvoiceModuleOptions opts, InvoiceConsoleParams params) {
		toolbar.startRun(AON.MSG.analyzing());
		INVOICE_SERVICE.analyze(opts.getOccam(), params, new AsyncCallback<InvoiceConsoleAnalysis>() {
			@Override
			public void onSuccess(InvoiceConsoleAnalysis result) {
				InvoiceConsoleAnalysisPanel icap = (InvoiceConsoleAnalysisPanel) 
					footPanel.addWidget( "An\u00E1lisis", () -> new InvoiceConsoleAnalysisPanel());
				icap.paint( opts, result );
				toolbar.endRun();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				String msg = "Error analizando facturas";
				LOGGER.log( Level.SEVERE, msg, caught );
				toolbar.endRun();
				toolbar.showErrorMessage( msg + " [" + caught.getMessage() + "]" );
			}
		});
	}

	public static void run() {
		GWT.runAsync(InvoiceConsoleModule.class, new RunAsyncCallback() {
			
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(AON.MSG.loadError("InvoiceConsoleModule"));
			}
			
			@Override
			public void onSuccess() {
				InvoiceConsoleModule operationReport = new InvoiceConsoleModule();
				operationReport.onModuleLoad();
			}
		});
	}
		
}
