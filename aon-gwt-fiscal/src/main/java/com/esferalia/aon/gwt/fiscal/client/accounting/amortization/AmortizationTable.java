package com.esferalia.aon.gwt.fiscal.client.accounting.amortization;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid.AonDisplayGridRow;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessageDialog;
import com.esferalia.aon.occam.api.model.accounting.Amortization;
import com.esferalia.aon.occam.api.model.eccounting.AmortizationParams;
import com.esferalia.aon.watson.mutable.MutableBoolean;
import com.esferalia.aon.watson.mutable.MutableInt;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ScrollPanel;

class AmortizationTable extends ScrollPanel implements HasSelectionHandlers<Amortization>{
	
	private FlowPanel containerPanel = new FlowPanel();
	private AonDisplayGrid grid = new AonDisplayGrid();
	
	private static final int LIMIT = 50;
	private final MutableInt offset = new MutableInt(0);
	private final MutableBoolean moreData = new MutableBoolean(true);
	private final MutableBoolean searchEnabled = new MutableBoolean( true );
	private int lastScrollPos = 0;	
	
	AmortizationTable(AmortizationModuleOptions opts, AmortizationParams params ) {
		setStyleName(AON.CSS.aonScrollArea());
		
		setWidget(containerPanel);
		
		grid.addStyleName(AON.CSS.aonMarginTop());
		grid.addStyleName(AON.CSS.aonBlockCenter());
		containerPanel.add(grid);
		AmortizationTableRow.fillHeader( grid.addHeaderRow() );
		
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
					onSearch(opts, params);
				}
			}
		});
		onSearch(opts, params);
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
	
	private void onSearch(AmortizationModuleOptions opts, AmortizationParams params) {
		if (!isMoreData()) return;
		params.setOffset( offset.getValue() );
		params.setLimit( LIMIT );
		AmortizationModule.SERVICE.get(opts.getOccam(), params, new AsyncCallback<LinkedList<Amortization>>() {
			
			@Override
			public void onSuccess(LinkedList<Amortization> list) {
				int size = AonCollectionUtils.size(list);
				AonCollectionUtils.stream(list).forEach(this::addRow);
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
			}
			
			@Override
			public void onFailure(Throwable e) {
				AonMessageDialog.error( "Error inexperado: " + e.getMessage());
			}
			
			public AonDisplayGridRow addRow(Amortization amortization) {
				AmortizationTableRow row = new AmortizationTableRow(opts, amortization);
				grid.add( row );
				row.addClickHandler(event -> SelectionEvent.fire(AmortizationTable.this, amortization ));
				return row;
			}
		});
	}
	
	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<Amortization> handler) {
		return super.addHandler(handler, SelectionEvent.getType());
	}
}
