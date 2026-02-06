package com.esferalia.aon.gwt.fiscal.client.invoice.console;

import java.util.LinkedList;
import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid.AonDisplayGridRow;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessageDialog;
import com.esferalia.aon.gwt.fiscal.client.invoice.AonInvoiceEvents.AonInvoiceCheckedEvent;
import com.esferalia.aon.gwt.fiscal.client.invoice.AonInvoiceEvents.AonInvoiceCheckedHandler;
import com.esferalia.aon.gwt.fiscal.client.invoice.AonInvoiceEvents.AonInvoiceRecordEvent;
import com.esferalia.aon.gwt.fiscal.client.invoice.AonInvoiceEvents.AonInvoiceRecordHandler;
import com.esferalia.aon.gwt.fiscal.client.invoice.AonInvoiceEvents.AonInvoiceUncheckedEvent;
import com.esferalia.aon.gwt.fiscal.client.invoice.AonInvoiceEvents.AonInvoiceUncheckedHandler;
import com.esferalia.aon.gwt.fiscal.client.invoice.AonInvoiceEvents.HasInvoiceCheckedHandlers;
import com.esferalia.aon.gwt.fiscal.client.invoice.AonInvoiceEvents.HasInvoiceRecordHandlers;
import com.esferalia.aon.gwt.fiscal.client.invoice.AonInvoiceEvents.HasInvoiceUncheckedHandlers;
import com.esferalia.aon.gwt.fiscal.client.invoice.InvoiceModuleOptions;
import com.esferalia.aon.gwt.fiscal.client.invoice.console.InvoiceConsoleToolbar.ToolbarAsyncCallback;
import com.esferalia.aon.occam.api.model.finance.InvoiceConsole;
import com.esferalia.aon.occam.api.model.finance.InvoiceConsoleParams;
import com.esferalia.aon.watson.mutable.MutableBoolean;
import com.esferalia.aon.watson.mutable.MutableInt;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ScrollPanel;

public class InvoiceConsoleTable extends ScrollPanel implements HasInvoiceCheckedHandlers, HasInvoiceUncheckedHandlers, HasInvoiceRecordHandlers {
	
	private static final Logger LOGGER = Logger.getLogger(InvoiceConsoleTable.class.getName());   

	FlowPanel containerPanel = new FlowPanel();
	AonDisplayGrid grid = new AonDisplayGrid();
	
	private static final int LIMIT = 50;
	private final MutableInt offset = new MutableInt(0);
	private final MutableBoolean moreData = new MutableBoolean(true);
	private final MutableBoolean searchEnabled = new MutableBoolean( true );
	private int lastScrollPos = 0;	
	
	public InvoiceConsoleTable(InvoiceModuleOptions opts, InvoiceConsoleParams params) {
		this(opts, params, null);
	}
	
	InvoiceConsoleTable(InvoiceModuleOptions opts, InvoiceConsoleParams params, ToolbarAsyncCallback toolbarCallback) {
		setStyleName(AON.CSS.aonScrollArea());
		
		setWidget(containerPanel);
		
		grid.addStyleName(AON.CSS.aonMarginTop());
		grid.addStyleName(AON.CSS.aonBlockCenter());
		containerPanel.add(grid);
		InvoiceConsoleTableRow.fillHeader( grid.addHeaderRow() );
		
		addScrollHandler(event -> {
			// ------------------------------------ Ignore scroll up.
			int oldScrollPos = lastScrollPos;
			lastScrollPos = getVerticalScrollPosition();
			if (oldScrollPos >= lastScrollPos) {
				return;
			}
			// -----------------------------------------------------
			if (isSearchEnabled()) {
				int maxScrollTop = getWidget().getOffsetHeight() - getOffsetHeight();
				if (lastScrollPos >= maxScrollTop) {
					disableSearch();
					onSearch(opts, params, toolbarCallback);
				}
			}
		});
		onSearch(opts, params, toolbarCallback);
	}
	
	private boolean isSearchEnabled() {
		return searchEnabled.isTrue();
	}
	private void disableSearch() {
		searchEnabled.setValue(false);
	}
	private void enableSearch() {
		searchEnabled.setValue(true);
	}
	private boolean isMoreData() {
		return moreData.isTrue();
	}
	private void disableMoreData() {
		moreData.setValue(false);
	}
	private void enableMoreData() {
		moreData.setValue(true);
	}
	
	private void onSearch(InvoiceModuleOptions opts, InvoiceConsoleParams params, ToolbarAsyncCallback toolbarCallback) {
		if (!isMoreData()) return;
		params.setOffset( offset.getValue() );
		params.setLimit( LIMIT );
		if (toolbarCallback != null) toolbarCallback.onStartRunning();
		InvoiceConsoleModule.INVOICE_SERVICE.getInvoices(opts.getOccam(), params, new AsyncCallback<LinkedList<InvoiceConsole>>() {
			
			@Override
			public void onSuccess(LinkedList<InvoiceConsole> invoices) {
				int size = AonCollectionUtils.size(invoices);
				AonCollectionUtils.stream(invoices).forEach(this::addRow);
				offset.add( size );
				enableMoreData();
				if ( size < params.getLimit() ) {
					FlowPanel line = new FlowPanel();
					line.setStyleName(AON.CSS.aonTextCenter());
					if (offset.getValue() > 0) {
						line.add(new InlineLabel(AON.MSG.noMoreData()));
					} else {
						line.add(new InlineLabel(AON.MSG.noData()));
					}
					containerPanel.add(line);
					disableMoreData();
				}
				enableSearch();
				if (toolbarCallback != null) toolbarCallback.onEndRunning();
			}
			
			@Override
			public void onFailure(Throwable e) {
				AonMessageDialog.error( "Error inexperado: " + e.getMessage());
				if (toolbarCallback != null) toolbarCallback.onEndRunning();
			}
			
			public AonDisplayGridRow addRow(InvoiceConsole invConsole) {
				InvoiceConsoleTableRow row = new InvoiceConsoleTableRow(opts, invConsole);
				grid.add( row );
				row.addInvoiceCheckedHandler(e -> {
					LOGGER.info("InvoiceConsoleTable: Invoice checked: " + e.getInvoice().getId());
					AonInvoiceCheckedEvent.fire(InvoiceConsoleTable.this, e.getInvoice());
				});
				row.addInvoiceUncheckedHandler(e -> {
					LOGGER.info("InvoiceConsoleTable: Invoice unchecked: " + e.getInvoice().getId());
					AonInvoiceUncheckedEvent.fire(InvoiceConsoleTable.this, e.getInvoice());
				});
				row.addInvoiceRecordHandler(e -> {
					LOGGER.info("InvoiceConsoleTable: Invoice record requested: " + e.getInvoice().getId());
					AonInvoiceRecordEvent.fire(InvoiceConsoleTable.this, e.getInvoice());
				});
				return row;
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
	
	@Override
	public HandlerRegistration addInvoiceRecordHandler(AonInvoiceRecordHandler handler) {
		return super.addHandler(handler, AonInvoiceRecordEvent.getType());
	}
}
