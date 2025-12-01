package com.esferalia.aon.gwt.fiscal.client.invoice.fee;

import java.util.Comparator;
import java.util.Optional;
import java.util.stream.IntStream;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.event.AonErrorEvent;
import com.esferalia.aon.gwt.common.client.widget.event.AonErrorHandler;
import com.esferalia.aon.gwt.common.client.widget.event.AonResetEvent;
import com.esferalia.aon.gwt.common.client.widget.event.AonResetHandler;
import com.esferalia.aon.gwt.common.client.widget.event.AonSearchEvent;
import com.esferalia.aon.gwt.common.client.widget.event.AonSearchHandler;
import com.esferalia.aon.gwt.common.client.widget.event.HasAonErrorHandlers;
import com.esferalia.aon.gwt.common.client.widget.event.HasAonResetHandlers;
import com.esferalia.aon.gwt.common.client.widget.event.HasAonSearchHandlers;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomerSuggestBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonInvoicingGroupSuggestBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonItemSuggestBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonSearchPanelButton;
import com.esferalia.aon.gwt.fiscal.client.invoice.InvoiceModuleOptions;
import com.esferalia.aon.occam.api.model.finance.FeeBillingParams;
import com.esferalia.aon.occam.api.model.finance.InvoicingGroup;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.type.BillingPeriod;
import com.esferalia.aon.occam.api.model.type.Month;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.Pair;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;

class InvoiceFeeFilter extends FlowPanel implements HasAonErrorHandlers, HasAonSearchHandlers, HasAonResetHandlers, Focusable {
	
	private AonCustomListBox monthListBox = new AonCustomListBox(AON.MSG.invoicingMonth());
	private AonCustomListBox yearListBox = new AonCustomListBox(AON.MSG.invoicingYear());
	private AonCustomListBox confidentialListBox = new AonCustomListBox(AON.MSG.confidential());
	private AonCustomListBox periocityListBox = new AonCustomListBox(AON.MSG.periodicity());
	private AonInvoicingGroupSuggestBox invoicingGroupBox;
	private AonCustomerSuggestBox customerBox;
	private AonItemSuggestBox itemBox;
	private AonSearchPanelButton searchButton;
	private AonSearchPanelButton editButton;
	
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
	
	
	public InvoiceFeeFilter(InvoiceModuleOptions opts) {
		setStyleName(AON.CSS.aonWidthAlmostAll());
		addStyleName(AON.CSS.aonBlockCenter());
		
		createMonthListBox();
		createYearListBox ( opts );
		createPeriodicityListBox( );
		createInvoicingGroupBox( opts );
		createCustomerBox( opts );
		createItemBox( opts );
        
		FlowPanel mainTab = new FlowPanel();
		mainTab.addStyleName(AON.CSS.aonBlockCenter());
		mainTab.addStyleName(AON.CSS.aonWidthAlmostAll());
		mainTab.addStyleName(AON.CSS.aonDisplayFlex());
		mainTab.addStyleName(AON.CSS.aonAlignItemsCenter());
		
		mainTab.add(monthListBox);
		mainTab.add(yearListBox);
		if (opts.getConfiguration().getUser().hasConfidentialityRole()) {
			createConfidentialListBox();
			mainTab.add(confidentialListBox);
		}
		mainTab.add(periocityListBox);
		mainTab.add(invoicingGroupBox);
		mainTab.add(customerBox);
		mainTab.add(itemBox);
		
		Label growLabel = new Label();
		growLabel.setStyleName(AON.CSS.aonFlexGrow1());
		mainTab.add(growLabel);

        FlowPanel buttonPanel = new FlowPanel();
        buttonPanel.setWidth("80px");
        buttonPanel.addStyleName(AON.CSS.aonAlignItemsCenter());
        buttonPanel.getElement().getStyle().setProperty("justify-content", "flex-end");

        searchButton = new AonSearchPanelButton(AON.MSG.searchAction(),AON.CSS.aonIconSearch());
        searchButton.addStyleName(AON.CSS.aonMarginRight());		
        searchButton.addDomHandler(e -> fireSearch(), ClickEvent.getType()); 
        buttonPanel.add(searchButton);
		mainTab.add(buttonPanel);
		
		editButton = new AonSearchPanelButton(AON.MSG.editSearch(),AON.CSS.aonIconEdit());
        editButton.setEnabled(false);
        editButton.setVisible(false);
        editButton.addStyleName(AON.CSS.aonMarginRight());		
        editButton.addDomHandler(e -> fireReset(), ClickEvent.getType()); 
        buttonPanel.add(editButton);


        ScrollPanel scrollPanel = new ScrollPanel();
		scrollPanel.addStyleName(AON.CSS.aonWidthAll());
		scrollPanel.setWidget(mainTab);
		this.add( scrollPanel );
		
//		
//		// Product Category
//		createProductCategorySuggestBox();
//		feeDockLayout.addFilterWidget(productCategorySuggestBox);
//		
//		// Product Tag
//		createProductTagSuggestBox();
//		feeDockLayout.addFilterWidget(productTagSuggestBox);
//		
//		// Seller
//		createSellerSuggestBox();
//		feeDockLayout.addFilterWidget(sellerSuggestBox);
//		
//		// Workplace
//		createWorkplaceSuggestBox();
//		feeDockLayout.addFilterWidget(workplaceSuggestBox);
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

	FeeBillingParams getWidgetParams(InvoiceModuleOptions opts) {
		SecurityLevel securityLevel = SecurityLevel.OFFICIAL;
		if (opts.getConfiguration().getUser().hasConfidentialityRole()) {
			securityLevel = SecurityLevel.safeValueOf( AonNumberUtils.toInteger(confidentialListBox.getValue()));
		}
		return new FeeBillingParams()
			.setDomainId(opts.getDomain())
			.setMonth( Month.safeValueOf( AonNumberUtils.toInteger( monthListBox.getValue() )).orElse(null) )
			.setYear(AonNumberUtils.toInteger( yearListBox.getValue() ))
			.setPeriod( BillingPeriod.safeValueOf( AonNumberUtils.toInteger(periocityListBox.getValue())).orElse(null) )
			.setInvoicingGroup( invoicingGroupBox.getInvoicingGroup().map(InvoicingGroup::getId).orElse(null) )
			.setCustomer( customerBox.getCustomer().map(Registry::getId).orElse(null) )
			.setItem( itemBox.getItem().map(Item::getId).orElse(null) )
			.setSecurityLevel(securityLevel)
			.setDryRun(true)
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
	
	@Override
	public HandlerRegistration addAonErrorHandler(AonErrorHandler handler) {
		return super.addHandler(handler, AonErrorEvent.getType());
	}
	public void fireError(String message) {
		
		AonErrorEvent.fire( InvoiceFeeFilter.this, message );
	}
	
	@Override
	public HandlerRegistration addAonSearchHandler(AonSearchHandler handler) {
		return super.addHandler(handler, AonSearchEvent.getType());
	}

	protected void fireSearch() {
		manageWidgtes( false );
		AonSearchEvent.fire( InvoiceFeeFilter.this ); 
	}

	@Override
	public HandlerRegistration addAonResetHandler(AonResetHandler handler) {
		return super.addHandler(handler, AonResetEvent.getType());
	}
	
	protected void fireReset() {
		manageWidgtes( true );
		AonResetEvent.fire( InvoiceFeeFilter.this );
	}

	private void manageWidgtes(boolean editing) {
		monthListBox.setEnabled(editing);
		yearListBox.setEnabled(editing);
		periocityListBox.setEnabled(editing);
		confidentialListBox.setEnabled(editing);
		invoicingGroupBox.setEnabled(editing);
		customerBox.setEnabled(editing);
		itemBox.setEnabled(editing);
		searchButton.setEnabled(editing);
		searchButton.setVisible(editing);
		editButton.setEnabled(!editing);
		editButton.setVisible(!editing);
	}

	private AonCustomListBox createMonthListBox() {
		monthListBox.addStyleName(AON.CSS.aonAlignItemsCenter());
		monthListBox.setWidth("150px");
		monthListBox.addItem("-", "");
		AonCollectionUtils.stream(Month.values())
			.forEach(month -> monthListBox.addItem(month.getName(), AonNumberUtils.toString(month.ordinal())));
		return monthListBox;
	}

	private AonCustomListBox createYearListBox( InvoiceModuleOptions opts ) {
		yearListBox.addStyleName(AON.CSS.aonAlignItemsCenter());
		yearListBox.addStyleName(AON.CSS.aonNowrap());
		yearListBox.setWidth("80px");
		yearListBox.addItem("-----", "");
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
		return yearListBox;
	}
	
	private AonCustomListBox createPeriodicityListBox() {
		periocityListBox.addStyleName(AON.CSS.aonAlignItemsCenter());
		periocityListBox.setWidth("100px");
		periocityListBox.addItem("-----", "");
		AonCollectionUtils.stream(BillingPeriod.values())
			.forEach(bp-> periocityListBox.addItem(bp.getDescription(), AonNumberUtils.toString(bp.ordinal())));
		return periocityListBox;
	}

	private AonCustomListBox createConfidentialListBox() {
		confidentialListBox.addStyleName(AON.CSS.aonAlignItemsCenter());
		confidentialListBox.setWidth("100px");
		AonCollectionUtils.stream(SecurityLevel.values())
			.forEach(sl-> confidentialListBox.addItem(sl.getName(), AonNumberUtils.toString(sl.ordinal())));
		confidentialListBox.getListBox().setSelectedIndex(0);
		return confidentialListBox;
	}

	private AonInvoicingGroupSuggestBox createInvoicingGroupBox(InvoiceModuleOptions opts) {
		invoicingGroupBox = new AonInvoicingGroupSuggestBox(opts);
		invoicingGroupBox.setWidth("200px");
		invoicingGroupBox.addStyleName(AON.CSS.aonAlignItemsCenter());
		return invoicingGroupBox;
	}
	
	private AonCustomerSuggestBox createCustomerBox(InvoiceModuleOptions opts) {
		customerBox = new AonCustomerSuggestBox(opts);
		customerBox.setWidth("200px");
		customerBox.addStyleName(AON.CSS.aonWidth300());
		customerBox.addStyleName(AON.CSS.aonAlignItemsCenter());
		return customerBox;
	}
	
	private AonItemSuggestBox createItemBox(InvoiceModuleOptions opts) {
		itemBox = new AonItemSuggestBox(opts);
		itemBox.setWidth("200px");
		itemBox.addStyleName(AON.CSS.aonWidth300());
		itemBox.addStyleName(AON.CSS.aonAlignItemsCenter());
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
