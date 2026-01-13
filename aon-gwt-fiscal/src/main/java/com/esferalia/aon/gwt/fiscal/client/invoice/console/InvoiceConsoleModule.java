package com.esferalia.aon.gwt.fiscal.client.invoice.console;


import static com.esferalia.aon.gwt.fiscal.client.EntryPointUtils.getCurrentDomain;
import static com.esferalia.aon.gwt.fiscal.client.EntryPointUtils.getCurrentDomainName;
import static com.esferalia.aon.gwt.fiscal.client.EntryPointUtils.getCurrentUser;
import static com.esferalia.aon.gwt.fiscal.client.EntryPointUtils.getRootPanel;

import java.util.HashMap;
import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.fiscal.client.invoice.AonInvoiceUncheckedEvent;
import com.esferalia.aon.gwt.fiscal.client.invoice.InvoiceModuleOptions;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceConsoleParams;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;

public class InvoiceConsoleModule  implements EntryPoint {
	private static final Logger LOGGER = Logger.getLogger(InvoiceConsoleModule.class.getName());
	
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

	private InvoiceConsoleToolbar toolbar;
	private SimpleLayoutPanel content;
	private final HashMap<Integer,Invoice> selectedInvoices = new HashMap<>();

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
	
	private class InvoiceConsoleToolbar extends AonToolbar {
		private final AonToolbarButton showFilter;
		private final AonToolbarButton hideFilter;
		
		private final InlineLabel selectedLabel;
		
		InvoiceConsoleToolbar() {
			super("Monitor de facturas");
			
			showFilter = new AonToolbarButton(AON.MSG.showFilter(), AON.CSS.aonIconFilterOn());
			showFilter.setVisible(false);
			this.add(showFilter);

			hideFilter = new AonToolbarButton(AON.MSG.hideFilter(), AON.CSS.aonIconFilterOff());
			hideFilter.setVisible(true);
			this.add(hideFilter);
			
			showFilter.addClickHandler(e -> {
				hideFilter.setVisible(true);
				showFilter.setVisible(false);
			});
			hideFilter.addClickHandler(e -> {
				hideFilter.setVisible(false);
				showFilter.setVisible(true);
			});

			selectedLabel = new InlineLabel();
			this.add(selectedLabel);
			
			refresh();
		}
		
		void addClickHandlerToShowFilter( ClickHandler handler ) {
			showFilter.addClickHandler(handler);
		}
		void addClickHandlerToHideFilter( ClickHandler handler ) {
			hideFilter.addClickHandler(handler);
		}

		public void refresh() {
			LOGGER.info("Refreshing toolbar. Selected invoices: " + AonCollectionUtils.size( selectedInvoices ) );
			selectedLabel.setText( AON.MSG.selectedItem( AonCollectionUtils.size( selectedInvoices ) ) );
		}
		
	}
	
	private void loadModule( InvoiceModuleOptions opts ) {
		AonLayoutPanel aonLayoutPanel = new AonLayoutPanel(Unit.PX);
		toolbar = new InvoiceConsoleToolbar();
		
		aonLayoutPanel.addNorth(toolbar, AonToolbar.HEIGTH);
		InvoiceConsoleFilter filterPanel = new InvoiceConsoleFilter(opts);
		filterPanel.addValueChangeHandler(e -> search(opts, e.getValue()) );
		aonLayoutPanel.addWest(filterPanel, FILTER_WIDTH);
		
		toolbar.addClickHandlerToShowFilter(e -> {
			aonLayoutPanel.setWidgetHidden(filterPanel, false);
			aonLayoutPanel.setWidgetSize(filterPanel, FILTER_WIDTH);
			aonLayoutPanel.animate(200); 
		});
		
		toolbar.addClickHandlerToHideFilter(e -> {
			aonLayoutPanel.setWidgetSize(filterPanel, 0);
			aonLayoutPanel.animate(200);
		});
		
		content = new SimpleLayoutPanel();
		content.setStyleName(AON.CSS.aonSelector());
		aonLayoutPanel.add(content);
		opts.getParentWidget().add(aonLayoutPanel);
		
		filterPanel.initialize(opts);
	}
	
	
	private void search(InvoiceModuleOptions opts, InvoiceConsoleParams params) {
		content.clear();
		selectedInvoices.clear();
		toolbar.refresh();
		InvoiceConsoleTable table = new InvoiceConsoleTable(opts, params );
		content.setWidget(table);
		
		table.addInvoiceCheckedHandler(e -> markAsSelected( e.getInvoice() ) );
		table.addInvoiceUncheckedHandler(e -> markAsUnselected( e.getInvoice() ) );
	}

	private void markAsUnselected(Invoice invoice) {
		LOGGER.info("Marking as unselected invoice: " + invoice );
		if (invoice != null) {
			selectedInvoices.remove(invoice.getId());
			toolbar.refresh();			
		}
	}

	private void markAsSelected(Invoice invoice) {
		LOGGER.info("Marking as selected invoice: " + invoice);
		if (invoice != null) {
			selectedInvoices.put(invoice.getId(), invoice);
			toolbar.refresh();
		}
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
