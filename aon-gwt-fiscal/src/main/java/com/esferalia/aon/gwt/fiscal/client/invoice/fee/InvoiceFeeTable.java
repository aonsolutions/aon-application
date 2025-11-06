package com.esferalia.aon.gwt.fiscal.client.invoice.fee;

import java.util.LinkedList;
import java.util.function.Supplier;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.event.AonErrorEvent;
import com.esferalia.aon.gwt.common.client.widget.event.AonErrorHandler;
import com.esferalia.aon.gwt.common.client.widget.event.HasAonErrorHandlers;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid.AonDisplayGridRow;
import com.esferalia.aon.occam.api.model.finance.FeeBillingParams;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.watson.mutable.MutableInt;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;

class InvoiceFeeTable extends ScrollPanel implements HasAonErrorHandlers, HasSelectionHandlers<Invoice> {
	
	public interface InvoiceFeeTableCallback {
		void onSearchStart();
		void onSearchEnd();
		void onError( String message);
	}

	private SimplePanel container;
	private AonDisplayGrid grid;
	
	private static final int LIMIT = 100;
	private final MutableInt offset = new MutableInt(0);
	
	private int lastScrollPos = 0;
	private final MutableInt searchEnabled = new MutableInt( 0 );
	private final MutableInt moreData = new MutableInt(0);

	InvoiceFeeTable(InvoiceFeeModuleOptions opts, FeeBillingParams params, InvoiceFeeTableCallback callback){
		container = new SimplePanel();
		this.setWidget(container);
		
		grid = new AonDisplayGrid();
		container.setWidget(grid);
		
		this.getElement().getStyle().setProperty("margin", "0 1rem");
		this.addScrollHandler(e -> {
			// ------------------------------------ Ignore scroll up.
			int oldScrollPos = lastScrollPos;
			lastScrollPos = this.getVerticalScrollPosition();
			if (oldScrollPos >= lastScrollPos) {
				return;
			}
			// -----------------------------------------------------
			if (isSearchEnabled()) {
				int maxScrollTop = this.getWidget().getOffsetHeight() - this.getOffsetHeight();
				if (lastScrollPos >= maxScrollTop) {
					disableSearch();
					onSearch( opts, params, callback );
				}
			}
		});
		paintHeader();
		enableMoreData();
		onSearch( opts, params, callback );
	}
	
	private void enableMoreData() {
		moreData.setValue(0);
	}
	private boolean isSearchEnabled() {
		return (searchEnabled.getValue() == 0 );
	}
	private void disableSearch() {
		searchEnabled.setValue(-1);
	}
	private void enableSearch() {
		searchEnabled.setValue(0);
	}
	private boolean isMoreData() {
		return (moreData.getValue() == 0 );
	}
	private void disableMoreData() {
		moreData.setValue(-1);
	}
	
	private void onSearch(InvoiceFeeModuleOptions opts, FeeBillingParams params, InvoiceFeeTableCallback callback) {
		if (!isMoreData()) return;
		params.setOffset(offset.intValue());
		params.setLimit(LIMIT);
		paintTable(opts, params, callback);
	}

	private void paintTable(InvoiceFeeModuleOptions opts, FeeBillingParams params, InvoiceFeeTableCallback callback) {
		callback.onSearchStart();
		InvoiceFeeModule.SERVICE.getInvoices(opts.getOccam(), params, new AsyncCallback<LinkedList<Invoice>>() {
	
				@Override
				public void onSuccess(LinkedList<Invoice> feeList) {
					boolean something = false;
					for( Invoice inv : feeList) {
						AonDisplayGridRow row = paintRow(inv);
						row.addClickHandler(e -> SelectionEvent.fire( InvoiceFeeTable.this, inv ));
						
						// Select first element by default
						if (!something) {
							SelectionEvent.fire( InvoiceFeeTable.this, inv );
						}
						
						something = true;
					}
					
					if (feeList.size() < LIMIT) {
						disableMoreData();
					} else {
						offset.setValue(offset.intValue() + feeList.size() - 1);
						enableMoreData();
					}
					
					if (!something) {
						FlowPanel line = new FlowPanel();
						InlineLabel label = new InlineLabel(AON.MSG.noData());
						line.add(label);
						container.clear();
						container.add(line);
						disableMoreData();
					}
					enableSearch();
					callback.onSearchEnd();
				}
				
				@Override
				public void onFailure(Throwable caught) {
					callback.onError("Error cargando panel de facturaci\u00f3n: " + caught.getMessage());
				}
			}
		);
	}

	private void paintHeader() {
		grid.addHeaderRow()
			.addCell(new Label("Doc.Tit."),AON.CSS.aonWidthAuto())
			.addCell(new Label("Nombre/raz\u00F3n social"),AON.CSS.aonWidth300(),AON.CSS.aonNowrap())
			.addCell(new Label("Fec. Fac."),AON.CSS.aonWidth80(),AON.CSS.aonNowrap())
			.addCell(new Label("Total"),AON.CSS.aonWidth80(),AON.CSS.aonNowrap())
		;
	}
	
	public void fireError(String message) {
		AonErrorEvent.fire( InvoiceFeeTable.this, message );
	}

	@Override
	public HandlerRegistration addAonErrorHandler(AonErrorHandler handler) {
		return super.addHandler(handler, AonErrorEvent.getType());
	}
	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<Invoice> handler) {
		return super.addHandler(handler, SelectionEvent.getType());
	}
	
	private AonDisplayGridRow paintRow(Invoice inv) {
		return grid.addRow()
			.addCell(new Label(ensure(inv.getRegistryDocument(), inv::getRegistryDocument, AonStringUtils.EMPTY)))
			.addCell(new Label(ensure(inv.getRegistryName(), () -> AonStringUtils.abbreviate(inv.getRegistryName(),25), AonStringUtils.EMPTY)))
			.addCell(new Label(ensure(inv.getIssueDate(), () -> AON.DATE_FORMAT.format(inv.getIssueDate()), AonStringUtils.EMPTY)))
			.addCell(new Label( AON.FMT.format(inv.getTotal())), AON.CSS.aonTextRight() )
		;
	}
	
	private <T> T ensure(Object nullable, Supplier<T>  supplier, T defaultValue) {
		return (nullable == null) 
			? defaultValue
			: supplier.get();
	}
}
