package com.esferalia.aon.gwt.fiscal.client.invoice.fee;

import java.util.Comparator;
import java.util.Optional;
import java.util.stream.IntStream;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.event.AonErrorEvent;
import com.esferalia.aon.gwt.common.client.widget.event.AonErrorHandler;
import com.esferalia.aon.gwt.common.client.widget.event.HasAonErrorHandlers;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomerSuggestBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonInvoicingGroupSuggestBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonItemSuggestBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonSearchFilter;
import com.esferalia.aon.occam.api.model.finance.FeeBillingParams;
import com.esferalia.aon.occam.api.model.finance.InvoicingGroup;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.type.BillingPeriod;
import com.esferalia.aon.occam.api.model.type.Month;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.Pair;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Focusable;

class InvoiceFeeFilter extends AonSearchFilter implements HasAonErrorHandlers, Focusable {
	
	private AonCustomListBox monthListBox = new AonCustomListBox(AON.MSG.invoicingDate());
	private AonCustomListBox yearListBox = new AonCustomListBox("");
	private AonCustomListBox periocityListBox = new AonCustomListBox(AON.MSG.periodicity());
	private AonInvoicingGroupSuggestBox invoicingGroupBox;
	private AonCustomerSuggestBox customerBox;
	private AonItemSuggestBox itemBox;
	
//	private AonCustomListBox segmentListBox;
//	private AonCustomSuggestBox conceptSuggestBox;
//	private AonCustomSuggestBox productCategorySuggestBox;
//	private AonCustomSuggestBox productTagSuggestBox;
//	private AonCustomTextBox quantityTextBox;
//	private AonCustomTextBox priceTextBox;
//	private AonCustomTextBox discountTextBox;
//
//	private AonCustomSuggestBox sellerSuggestBox;
//	private AonCustomSuggestBox workplaceSuggestBox;
//	private AonCustomSuggestBox projectSuggestBox;
//	private AonCustomListBox sort = new AonCustomListBox("Ordenar Por");
//	private AonCustomListBox asc = new AonCustomListBox("Orden");
	
	
	public InvoiceFeeFilter(InvoiceFeeModuleOptions opts) {
		super(false , true); 
		// Period
		FlowPanel periodItemPanel = new FlowPanel();
		periodItemPanel.addStyleName(AON.CSS.aonItemFlex());
		periodItemPanel.add( createMonthListBox( ));
		periodItemPanel.add( createYearListBox ( opts ));
		addFilterWidget(periodItemPanel);
		
		// Periodicity
		addFilterWidget(createPeriodicityListBox( ));

		// InvoicingGroup
		addFilterWidget(createInvoicingGroupBox( opts ));

		// Customer
		addFilterWidget(createCustomerBox( opts ));

		// Item
		addFilterWidget(createItemBox( opts ));
//		
//		// Product Category
//		createProductCategorySuggestBox();
//		feeDockLayout.addFilterWidget(productCategorySuggestBox);
//		
//		// Product Tag
//		createProductTagSuggestBox();
//		feeDockLayout.addFilterWidget(productTagSuggestBox);
//		
//		// Quantity
//		quantityTextBox = new AonCustomTextBox("Cantidad");
//		quantityTextBox.addValueChangeHandler(e -> onSearch());
//		feeDockLayout.addFilterWidget(quantityTextBox);
//
//		// Price
//		priceTextBox = new AonCustomTextBox("Precio");
//		priceTextBox.addValueChangeHandler(e -> onSearch());
//		feeDockLayout.addFilterWidget(priceTextBox);
//		
//		// Discount
//		discountTextBox = new AonCustomTextBox("Descuento");
//		discountTextBox.addValueChangeHandler(e -> onSearch());
//		feeDockLayout.addFilterWidget(discountTextBox);
//		
//		// Seller
//		createSellerSuggestBox();
//		feeDockLayout.addFilterWidget(sellerSuggestBox);
//		
//		// Workplace
//		createWorkplaceSuggestBox();
//		feeDockLayout.addFilterWidget(workplaceSuggestBox);
//		
//		// Invoicing Group
//		createInvoicingGroupSuggestBox();
//		feeDockLayout.addFilterWidget(invoicingGroupSuggestBox);
//		
//		// Project
//		createProjectSuggestBox();
//		feeDockLayout.addFilterWidget(projectSuggestBox);
//		
//		// Segment
//		if (opt.getConfiguration().hasSegments()) {
//			segmentListBox = new AonCustomListBox("Segmento");
//			segmentListBox.addItem("-", "");
//			segmentListBox.addItem("SIN SEGMENTO", "-1");
//			
//			for (Segment ea : opt.getConfiguration().getSegments()) {
//				segmentListBox.addItem(ea.getName(), AonNumberUtils.toString( ea.getId()));
//			}
//			segmentListBox.addChangeHandler(event -> onSearch());
//			
//			feeDockLayout.addFilterWidget(segmentListBox);
//		}
//		
//		
//		sort.addItem("Cliente", "name");
//		sort.addItem("F. Facturaci\u00f3n", "date");
//		sort.getListBox().addChangeHandler(event -> onSearch());
//		
//		asc.addItem("Ascendente", "true");
//		asc.addItem("Descendete", "false");
//		asc.getListBox().addChangeHandler(event -> onSearch());
//		
//		feeDockLayout.addSortWidget(sort);
//		feeDockLayout.addSortWidget(asc);
	}

	protected void resetFilter() {
		monthListBox.setValue("");
		yearListBox.setValue("");
		periocityListBox.setValue("");
		invoicingGroupBox.setValue(null, false);
		customerBox.setValue(null, false);
		itemBox.setValue(null, false);
		
//		conceptSuggestBox.setValue(null);
//		productCategorySuggestBox.setValue(null);
//		sellerSuggestBox.setValue(null);
//		workplaceSuggestBox.setValue(null);
//		projectSuggestBox.setValue(null);
//		if(null != segmentListBox)
//			segmentListBox.setValue("");
		fireSearch();
	}
	
	FeeBillingParams getWidgetParams(InvoiceFeeModuleOptions opts) {
		return new FeeBillingParams()
			.setDomainId(opts.getDomain())
			.setMonth( Month.safeValueOf( AonNumberUtils.toInteger( monthListBox.getValue() )).orElse(null) )
			.setYear(AonNumberUtils.toInteger( yearListBox.getValue() ))
			.setPeriod( BillingPeriod.safeValueOf( AonNumberUtils.toInteger(periocityListBox.getValue())).orElse(null) )
			.setInvoicingGroup( invoicingGroupBox.getInvoicingGroup().map(InvoicingGroup::getId).orElse(null) )
			.setCustomer( customerBox.getCustomer().map(Registry::getId).orElse(null) )
			.setItem( itemBox.getItem().map(Item::getId).orElse(null) )
		;
		
//		params.setProductCategory(null != productCategorySuggestions.get(productCategorySuggestBox.getValue()) ? productCategorySuggestions.get(productCategorySuggestBox.getValue()) : null);
//		params.setSeller(null != sellerSuggestions.get(sellerSuggestBox.getValue()) ? sellerSuggestions.get(sellerSuggestBox.getValue()).getId() : null);
//		params.setWorkplace(null != workplaceSuggestions.get(workplaceSuggestBox.getValue()) ? workplaceSuggestions.get(workplaceSuggestBox.getValue()).getDescription() : null);
//		params.setProject(null != projectSuggestions.get(projectSuggestBox.getValue()) ? projectSuggestions.get(projectSuggestBox.getValue()).getId() : null);
//		params.setSegment(segmentListBox != null && !AonStringUtils.isBlank(segmentListBox.getValue()) ? AonNumberUtils.toInteger( segmentListBox.getValue()) : null);
//		
//		params.setLimit(limit);
//		params.setOffset(offset.getValue());
	}
	
	public void fireError(String message) {
		AonErrorEvent.fire( InvoiceFeeFilter.this, message );
	}

	@Override
	public HandlerRegistration addAonErrorHandler(AonErrorHandler handler) {
		return super.addHandler(handler, AonErrorEvent.getType());
	}
	
	private AonCustomListBox createMonthListBox() {
		monthListBox.addItem("-", "");
		AonCollectionUtils.stream(Month.values())
			.forEach(month -> monthListBox.addItem(month.getName(), AonNumberUtils.toString(month.ordinal())));
		monthListBox.addChangeHandler(e -> fireSearch());
		return monthListBox;
	}

	private AonCustomListBox createYearListBox( InvoiceFeeModuleOptions opts ) {
		yearListBox.addItem("-", "");
		InvoiceFeeModule.SERVICE.getFeeYearRange(opts.getOccam(), opts.getDomain(), new AsyncCallback<Pair<Integer, Integer>>() {
	
			@Override
			public void onSuccess(Pair<Integer, Integer> pair) {
				Optional.ofNullable(pair)
					.ifPresent( p -> IntStream.rangeClosed(p.getLeft(), p.getRight())
					.boxed()
					.sorted(Comparator.reverseOrder())
					.map( AonNumberUtils::toString )
					.forEach(year -> yearListBox.addItem(year, year))
				);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				fireError("Error al calcular el rango de a\u00F1os: " + caught.getMessage());
			}
		});
		yearListBox.addChangeHandler(e -> fireSearch());
		return yearListBox;
	}
	
	private AonCustomListBox createPeriodicityListBox() {
		periocityListBox.addItem("-", "");
		AonCollectionUtils.stream(BillingPeriod.values())
			.forEach(bp-> periocityListBox.addItem(bp.getDescription(), AonNumberUtils.toString(bp.ordinal())));
		periocityListBox.addChangeHandler(e -> fireSearch());
		return periocityListBox;
	}

	private AonInvoicingGroupSuggestBox createInvoicingGroupBox(InvoiceFeeModuleOptions opts) {
		invoicingGroupBox = new AonInvoicingGroupSuggestBox(opts);
		invoicingGroupBox.addSelectionHandler(e -> fireSearch());
		return invoicingGroupBox;
	}
	
	private AonCustomerSuggestBox createCustomerBox(InvoiceFeeModuleOptions opts) {
		customerBox = new AonCustomerSuggestBox(opts);
		customerBox.addSelectionHandler(e -> fireSearch());
		return customerBox;
	}

	private AonItemSuggestBox createItemBox(InvoiceFeeModuleOptions opts) {
		itemBox = new AonItemSuggestBox(opts);
		itemBox.addSelectionHandler(e -> fireSearch());
		return itemBox;
	}

	@Override
	public int getTabIndex() {
		return monthListBox.getTabIndex();
	}

	@Override
	public void setAccessKey(char key) {
		monthListBox.setAccessKey(key);
	}

	@Override
	public void setFocus(boolean focus) {
		monthListBox.setFocus(focus);
	}

	@Override
	public void setTabIndex(int tabIndex) {
		monthListBox.setTabIndex(tabIndex);
	}
}
