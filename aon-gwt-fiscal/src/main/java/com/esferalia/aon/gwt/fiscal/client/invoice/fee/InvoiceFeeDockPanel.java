package com.esferalia.aon.gwt.fiscal.client.invoice.fee;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDockLayout;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.fiscal.client.invoice.console.InvoiceConsoleTextPanel;
import com.esferalia.aon.gwt.fiscal.client.invoice.fee.InvoiceFeeTable.InvoiceFeeTableCallback;
import com.esferalia.aon.occam.api.model.finance.FeeBillingParams;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;

public class InvoiceFeeDockPanel extends AonDockLayout {

	private FlowPanel messagePanel = new FlowPanel();
	private SimpleLayoutPanel tableContainer;

	public InvoiceFeeDockPanel(final InvoiceFeeModuleOptions opts) {
		super( "Panel Facturaci\u00f3n de Cuotas" );
		
		InvoiceFeeFilter invoiceFeeFilter = new InvoiceFeeFilter( opts );
		invoiceFeeFilter.addAonErrorHandler(event -> AonMessagePanel.showError(messagePanel, event.getMessage()));
		invoiceFeeFilter.addAonSearchHandler(event -> onSearch( opts, invoiceFeeFilter.getWidgetParams(opts) ) );
		invoiceFeeFilter.addAonResetHandler(e -> invoiceFeeFilter.resetFilter() );
		invoiceFeeFilter.setPlaceholder("Clientes: busque por nombre, nif o alias");
		setSearchFilter(invoiceFeeFilter);
		
		this.hideToolbarFilterMessages();

		HTMLPanel container = new HTMLPanel("");
		container.addStyleName(AON.CSS.aonFlexColumn());
		container.add(messagePanel);
		tableContainer = new SimpleLayoutPanel();
		tableContainer.setHeight("100%");
		tableContainer.getElement().getStyle().setProperty("margin-left", "1rem");
		container.add(tableContainer);
		this.add(container);
		
		onSearch( opts, invoiceFeeFilter.getWidgetParams(opts)	);
	}
	
	private void onSearch(InvoiceFeeModuleOptions opts, FeeBillingParams params) {
		params.setDryRun(true);
		tableContainer.clear();
		
		DockLayoutPanel dock = new DockLayoutPanel(Unit.PX);
		
		InvoiceFeeTable tab = new InvoiceFeeTable(opts, params, new InvoiceFeeTableCallback() {
			@Override public void onSearchStart() { AonMessagePanel.showLoading(messagePanel, "Cargando panel de facturaci\u00f3n ..."); }
			@Override public void onSearchEnd() {AonMessagePanel.hideMessage(messagePanel); }
			@Override public void onError(String message) {AonMessagePanel.showError(messagePanel, message ); }
		});
		tab.addAonErrorHandler(event -> AonMessagePanel.showError(messagePanel, event.getMessage()));
		dock.addWest(tab, 500);
		
		SimpleLayoutPanel eastPanel = new SimpleLayoutPanel();
		dock.add(eastPanel);
		
		tab.addSelectionHandler(event -> eastPanel.setWidget( new InvoiceConsoleTextPanel( event.getSelectedItem() ) ));
		
		tableContainer.setWidget( dock );
	}
	
}
