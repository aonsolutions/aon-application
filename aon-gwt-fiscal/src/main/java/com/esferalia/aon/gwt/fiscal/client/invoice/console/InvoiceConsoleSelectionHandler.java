package com.esferalia.aon.gwt.fiscal.client.invoice.console;


import java.util.HashMap;
import java.util.logging.Logger;
import java.util.stream.Stream;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;

public class InvoiceConsoleSelectionHandler extends FlowPanel implements HasValueChangeHandlers<Integer>{
	
	private static final Logger LOGGER = Logger.getLogger(InvoiceConsoleSelectionHandler.class.getName());
	
	private final HashMap<Integer,Invoice> selectedInvoices = new HashMap<>();
	
	private final InlineLabel selectedLabel = new InlineLabel();
	
	public InvoiceConsoleSelectionHandler() {
		this.add(selectedLabel);
		refresh();
	}

	public void clean() {
		selectedInvoices.clear();	
		refresh();
		fireEvent( new ValueChangeEvent<Integer>( selectedCount() ) {} );
	}
	
	public void refresh() {
		selectedLabel.setText( AON.MSG.selectedItem( selectedCount() ) );
	}
	
	public int selectedCount() {
		return AonCollectionUtils.size( selectedInvoices );
	}

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Integer> handler) {
		return super.addHandler(handler, ValueChangeEvent.getType());
	}

	public void select(Invoice invoice) {
		LOGGER.info("Marking as selected invoice: " + invoice);
		if (invoice != null) {
			selectedInvoices.put(invoice.getId(), invoice);
			refresh();
			fireEvent( new ValueChangeEvent<Integer>( selectedCount() ) {} );
		}
	}

	public void unselect(Invoice invoice) {
		LOGGER.info("Marking as unselected invoice: " + invoice );
		if (invoice != null) {
			selectedInvoices.remove(invoice.getId());
			refresh();
			fireEvent( new ValueChangeEvent<Integer>( selectedCount() ) {} );
		}
	}

	public Stream<Invoice> stream() {
		return AonCollectionUtils.valuesStream( selectedInvoices );
	}

	public boolean isEmpty() {
		return AonCollectionUtils.isEmpty( selectedInvoices );
	}
	public boolean isNotEmpty() {
		return !isEmpty();
	}

}
