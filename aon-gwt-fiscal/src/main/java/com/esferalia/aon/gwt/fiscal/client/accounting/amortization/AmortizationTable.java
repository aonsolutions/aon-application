package com.esferalia.aon.gwt.fiscal.client.accounting.amortization;

import java.util.LinkedList;
import java.util.function.Supplier;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDoubleLabel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonFlexTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonFlexTable.AonFlexTableRow;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessageDialog;
import com.esferalia.aon.occam.api.model.accounting.Amortization;
import com.esferalia.aon.occam.api.model.eccounting.AmortizationParams;
import com.esferalia.aon.watson.mutable.MutableBoolean;
import com.esferalia.aon.watson.mutable.MutableInt;
import com.esferalia.aon.watson.util.AonCollectionUtils;
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

class AmortizationTable extends ScrollPanel implements HasSelectionHandlers<Amortization>{
	
	private static final String[] COLUMN_WIDTHS = new String[] {
			"100px","100px","300px","300px","120px","auto" };

	private FlowPanel containerPanel = new FlowPanel();
	private AonFlexTable grid = new AonFlexTable(COLUMN_WIDTHS, AON.CSS.aonBlockCenter());
	
	private static final int LIMIT = 50;
	private final MutableInt offset = new MutableInt(0);
	private final MutableBoolean moreData = new MutableBoolean(true);
	private final MutableBoolean searchEnabled = new MutableBoolean( true );
	private int lastScrollPos = 0;	
	
	AmortizationTable(AmortizationModuleOptions opts, AmortizationParams params ) {
		setStyleName(AON.CSS.aonScrollArea());
		addStyleName(AON.CSS.aonMarginTop());
		
		setWidget(containerPanel);
		
		containerPanel.add(grid);
		fillHeader( );
		
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
				
				AonCollectionUtils.stream(list).forEach( am -> {
					AonFlexTableRow row = grid.addRow();
					fillRow(row, am);
					row.addClickHandler(event -> SelectionEvent.fire(AmortizationTable.this, am ));
				});
				
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
			
			private AonFlexTableRow fillRow(AonFlexTableRow row, Amortization am) {
				String initialDate = ensure(am.getInitialDate(), () -> AON.DATE_FORMAT.format(am.getInitialDate()), AonStringUtils.EMPTY);
				Label initialDateLabel = new Label(initialDate);
				String deadline = ensure(am.getDeadline(), () -> AON.DATE_FORMAT.format(am.getDeadline()), AonStringUtils.EMPTY);
				Label deadlineLabel = new Label(deadline);
				String description = ensure(am.getDescription(), am::getDescription, AonStringUtils.EMPTY);
				description = AonStringUtils.abbreviate(description, 60);
				Label descriptionLabel = new Label(description);
				String fixedAssetAccount = ensure(am.getFixedAssetAccount(), () -> am.getFixedAssetAccount().getFullName(), AonStringUtils.EMPTY);
				fixedAssetAccount = AonStringUtils.abbreviate(fixedAssetAccount, 40);
				Label fixedAssetAccountLabel = new Label(fixedAssetAccount);
				String comments = ensure(am.getComments(), am::getComments, AonStringUtils.EMPTY);
				Label commentsLabel = new Label();
				commentsLabel.setTitle(comments);
				commentsLabel.setText(AonStringUtils.abbreviate(comments, 30));
				return row
					.addCell(initialDateLabel)
					.addCell(deadlineLabel)
					.addCell(descriptionLabel)
					.addCell(fixedAssetAccountLabel,AON.CSS.aonNowrap())
					.addCell(new AonDoubleLabel(am.getAmount()), AON.CSS.aonTextRight())
					.addCell(commentsLabel,AON.CSS.aonNowrap())
				;
			}
			
			private <T> T ensure(Object nullable, Supplier<T>  supplier, T defaultValue) {
				return (nullable == null) 
					? defaultValue
					: supplier.get();
			}
		});
	}
	
	private void fillHeader() {
		grid
			.addHeaderCell(new Label(AON.MSG.from()),AON.CSS.aonNowrap())
			.addHeaderCell(new Label(AON.MSG.until()),AON.CSS.aonNowrap())
			.addHeaderCell(new Label(AON.MSG.description()))
			.addHeaderCell(new Label(AON.MSG.fixedAssetAccount()),AON.CSS.aonNowrap())
			.addHeaderCell(new Label(AON.MSG.amount()),AON.CSS.aonTextRight())
			.addHeaderCell(new Label(AON.MSG.comments()),AON.CSS.aonNowrap())
		;
	}
	
	
	
	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<Amortization> handler) {
		return super.addHandler(handler, SelectionEvent.getType());
	}
	
}
