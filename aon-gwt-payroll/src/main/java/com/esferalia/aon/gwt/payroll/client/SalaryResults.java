package com.esferalia.aon.gwt.payroll.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;

public class SalaryResults extends Composite implements RequiresResize {

	private static final int RESULTS_LIMIT = 100;

	static interface Binder extends UiBinder<Widget, SalaryResults> {

	}

	private static final Binder binder = GWT.create(Binder.class);
	

	@UiField
	Button clearButton;

	@UiField
	DockLayoutPanel dockLayoutPanel;

	@UiField
	SalaryResultsGrid salaryResultsGrid;
	
	private ListDataProvider<JsSalaryResult> listDataProvider;
	
	public SalaryResults() {
		
		initWidget(binder.createAndBindUi(this));
		 
		salaryResultsGrid.setPageSize(RESULTS_LIMIT);

	}
	
	// ------------------------------------------------------------------------

	
	@Override
	public void onResize() {
		dockLayoutPanel.onResize();
	}
	
	// ------------------------------------------------------------ @UiHandlers
	
	@UiHandler("clearButton")
	void onClickClearButton(ClickEvent event){
		listDataProvider.getList().clear();
	}
	
	// ------------------------------------------------------------------------
	
	public void setDataProvider(ListDataProvider<JsSalaryResult> listDataProvider){
		this.listDataProvider = listDataProvider;
		listDataProvider.addDataDisplay(salaryResultsGrid);
	}

	// ------------------------------------------------------ SalaryResultsGrid  
	
	public HandlerRegistration addSelectionHandler(
			SelectionHandler<JsSalaryResult> handler) {
		return salaryResultsGrid.addSelectionHandler(handler);
	}
	
	
	
	
	
	

}
