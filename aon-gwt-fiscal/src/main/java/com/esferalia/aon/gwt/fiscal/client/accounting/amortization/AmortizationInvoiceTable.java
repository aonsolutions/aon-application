package com.esferalia.aon.gwt.fiscal.client.accounting.amortization;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessageDialog;
import com.esferalia.aon.gwt.fiscal.client.accounting.amortization.AmortizationPanel.AmortizationPanelCallback;
import com.esferalia.aon.gwt.fiscal.client.invoice.AonInvoiceCheckedEvent;
import com.esferalia.aon.gwt.fiscal.client.invoice.AonInvoiceCheckedHandler;
import com.esferalia.aon.gwt.fiscal.client.invoice.AonInvoiceUncheckedEvent;
import com.esferalia.aon.gwt.fiscal.client.invoice.AonInvoiceUncheckedHandler;
import com.esferalia.aon.gwt.fiscal.client.invoice.HasInvoiceCheckedHandlers;
import com.esferalia.aon.gwt.fiscal.client.invoice.HasInvoiceUncheckedHandlers;
import com.esferalia.aon.occam.api.model.accounting.AmortizationInvoice;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ScrollPanel;

class AmortizationInvoiceTable extends ScrollPanel implements HasInvoiceCheckedHandlers, HasInvoiceUncheckedHandlers{
	
	FlowPanel containerPanel = new FlowPanel();
	AonDisplayGrid grid = new AonDisplayGrid();
	
	AmortizationInvoiceTable(AmortizationModuleOptions opts, AmortizationPanelCallback callback) {
		setStyleName(AON.CSS.aonScrollArea());
		
		setWidget(containerPanel);
		
		grid.addStyleName(AON.CSS.aonMarginTop());
		grid.addStyleName(AON.CSS.aonBlockCenter());
		containerPanel.add(grid);
		AmortizationInvoiceTableRow.fillHeader( grid.addHeaderRow() );
		onSearch(opts, callback);
	}
	
	private void onSearch(AmortizationModuleOptions opts, AmortizationPanelCallback callback) {
		Integer domain = callback.getAmortization().getDomain();
		Integer id = callback.getAmortization().getId();
		AmortizationModule.SERVICE.getInvoices(opts.getOccam(), domain, id, new AsyncCallback<LinkedList<AmortizationInvoice>>() {
			
			@Override
			public void onSuccess(LinkedList<AmortizationInvoice> amis) {
				AonCollectionUtils.stream(amis)
					.map( ami -> new AmortizationInvoiceTableRow(opts, ami) )
					.forEach(row -> grid.add( row ));
			}
			
			@Override
			public void onFailure(Throwable e) {
				AonMessageDialog.error( "Error inexperado: " + e.getMessage());
			}
		});
	}
	
	@Override
	public HandlerRegistration addInvoiceCheckedHandler(AonInvoiceCheckedHandler handler) {
		return super.addHandler(handler, AonInvoiceCheckedEvent.getType());
	}

	@Override
	public HandlerRegistration addInvoiceUncheckedHandler(AonInvoiceUncheckedHandler handler) {
		return super.addHandler(handler, AonInvoiceUncheckedEvent.getType());
	}
	
}
