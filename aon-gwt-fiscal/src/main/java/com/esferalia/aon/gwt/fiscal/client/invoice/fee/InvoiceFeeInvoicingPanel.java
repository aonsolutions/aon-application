package com.esferalia.aon.gwt.fiscal.client.invoice.fee;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomTextArea;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonSeriesSuggestBox;
import com.esferalia.aon.occam.api.model.Series;
import com.esferalia.aon.occam.api.model.finance.FeeBillingParams;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.SimplePanel;

class InvoiceFeeInvoicingPanel extends SimplePanel implements Focusable {
	interface InvoiceFeeInvoicingPanelCallback {
		void onAccept( FeeBillingParams params );
		void onCancel();
	}
	private AonSeriesSuggestBox seriesBox;
	private AonCustomDateBox issueDateBox;
	private AonCustomTextArea commentsBox;
	
	InvoiceFeeInvoicingPanel( InvoiceFeeModuleOptions opts, FeeBillingParams params ,InvoiceFeeInvoicingPanelCallback callback) {
		
		FlowPanel mainPanel = new FlowPanel();
		mainPanel.setStyleName(AON.CSS.aonPadding());
		setWidget( mainPanel );
		
		
		seriesBox = new AonSeriesSuggestBox(opts);
		seriesBox.addSelectionHandler(e -> 
			params.setInvoiceSeries(seriesBox.getSeries().map(Series::getCode).orElse(null) ));
		
		issueDateBox = new AonCustomDateBox("");
		issueDateBox.addValueChangeHandler(e -> params.setInvoiceDate(issueDateBox.getValue()));
		
		commentsBox = new AonCustomTextArea("");
		commentsBox.addValueChangeHandler(e -> params.setInvoiceComments(commentsBox.getValue()));
		
		AonDisplayTable tab = new AonDisplayTable();
		tab.addStyleName(AON.CSS.aonMarginTop());
		tab.addStyleName(AON.CSS.aonMarginBottom());
		tab.addStyleName(AON.CSS.aonBlockCenter());
		tab.addLabelWidgetRow(AON.MSG.series(), seriesBox);
		tab.addLabelWidgetRow(AON.MSG.issueDate(), issueDateBox);
		tab.addLabelWidgetRow(AON.MSG.comments(), commentsBox);
		mainPanel.add(tab);
		
		FlowPanel buttonsPanel = new FlowPanel();
		buttonsPanel.setStyleName(AON.CSS.aonBlockCenter());
		buttonsPanel.setStyleName(AON.CSS.aonTextCenter());
		buttonsPanel.addStyleName(AON.CSS.aonPaddingTop());

    	final Button okButton = new Button();
    	okButton.setStyleName(AON.CSS.aonOkButton());
    	okButton.setText( AON.MSG.accept());
    	okButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				okButton.setEnabled(false);
				callback.onAccept( params );
			}
		});
    	buttonsPanel.add(okButton);
    	
    	final Button cancelButton = new Button();
    	cancelButton.setStyleName(AON.CSS.aonCancelButton());
    	cancelButton.addStyleName(AON.CSS.aonMarginLeft());
    	cancelButton.setText( AON.MSG.cancelAction());
    	cancelButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				cancelButton.setEnabled(false);
				callback.onCancel();
			}
		});
    	buttonsPanel.add(cancelButton);
    	mainPanel.add(buttonsPanel);
	}

	@Override
	public int getTabIndex() {
		return seriesBox.getTabIndex();
	}

	@Override
	public void setAccessKey(char key) {
		seriesBox.setAccessKey(key);
	}

	@Override
	public void setFocus(boolean focus) {
		seriesBox.setFocus(focus);
	}

	@Override
	public void setTabIndex(int index) {
		seriesBox.setTabIndex(index);
	}

}
