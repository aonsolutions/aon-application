package com.esferalia.aon.gwt.fiscal.client.accounting.amortization;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.fiscal.client.accounting.amortization.AmortizationPanel.AmortizationPanelCallback;
import com.esferalia.aon.gwt.fiscal.client.invoice.console.InvoiceConsoleSelectionHandler;
import com.esferalia.aon.occam.api.model.finance.InvoiceConsoleParams;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;

class AmortizationInvoiceSelectionPanel extends DockLayoutPanel {

	private final SimpleLayoutPanel contentPanel;
	
	AmortizationInvoiceSelectionPanel(AmortizationModuleOptions opts, AmortizationPanelCallback callback) {
		super(Unit.PX);
		
		AonToolbar toolbar = new AonToolbar(AON.MSG.addInvoice());
		
		AonToolbarButton linkButton = new AonToolbarButton(AON.MSG.linkInvoice(), AON.CSS.aonIconLink());
		linkButton.setVisible(false);
		toolbar.add(linkButton);
		
		InvoiceConsoleSelectionHandler selectionHandler = new InvoiceConsoleSelectionHandler();
		selectionHandler.addValueChangeHandler(e -> linkButton.setVisible( !selectionHandler.isEmpty() ) );
		linkButton.addClickHandler( event -> onLinkInvoice( opts, callback, selectionHandler) );
		toolbar.add(selectionHandler);

		addNorth(toolbar, AonToolbar.HEIGTH);


		AmortizationInvoiceSelectionFilterPanel filterPanel = new AmortizationInvoiceSelectionFilterPanel(opts );
		addNorth(filterPanel, 80);
		filterPanel.addValueChangeHandler( event -> search(opts, callback, selectionHandler, event.getValue() ) );
		
		contentPanel = new SimpleLayoutPanel();
		add(contentPanel);
		
		search(opts, callback, selectionHandler, filterPanel.getWidgetParams( opts ));
		
	}

	private void search(AmortizationModuleOptions opts, AmortizationPanelCallback callback, InvoiceConsoleSelectionHandler selectionHandler, InvoiceConsoleParams params) {
		contentPanel.clear();
		AmortizationInvoiceSelectionTable table = new AmortizationInvoiceSelectionTable(opts, callback, params);
		table.addInvoiceCheckedHandler( event -> selectionHandler.select( event.getInvoice() ) );
		table.addInvoiceUncheckedHandler( event -> selectionHandler.unselect( event.getInvoice() ) );
		contentPanel.setWidget(table);
	}

	private void onLinkInvoice(AmortizationModuleOptions opts, AmortizationPanelCallback callback, InvoiceConsoleSelectionHandler selectionHandler) {
		AonConfirmDialog.showConfirm( 
			AON.MSG.linkInvoice()
			,AON.MSG.confirmLinkInvoice()
			,() -> doLinkInvoice(opts, callback, selectionHandler)
		);
	}
	
	private void doLinkInvoice(AmortizationModuleOptions opts, AmortizationPanelCallback callback, InvoiceConsoleSelectionHandler selectionHandler) {
		Integer[] ids = selectionHandler.stream()
			.map( invoice -> invoice.getId() )
			.toArray(Integer[]::new);
		AmortizationModule.SERVICE.linkInvoices(opts.getOccam()
			,opts.getDomain()
			,callback.getAmortization().getId()
			,ids 
			,new AsyncCallback<Void>() {

				@Override
				public void onSuccess(Void arg0) {
					callback.refresh();
				};
				
				@Override
				public void onFailure(Throwable arg0) {
					callback.showError( arg0.getMessage() );
				}

		});
	}
}
