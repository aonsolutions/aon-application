package com.esferalia.aon.gwt.fiscal.client.accounting.wizard.tedi;

import java.util.Date;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonErrorPanel;
import com.esferalia.aon.gwt.fiscal.client.FinanceService;
import com.esferalia.aon.gwt.fiscal.client.FinanceServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.FinanceServiceAsyncDecorator;
import com.esferalia.aon.gwt.fiscal.client.accounting.wizard.tedi.InvoicePanel.InvoicePanelCallback;
import com.esferalia.aon.occam.api.model.finance.InvoiceRectificationData;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;

class InvoiceRectificationDataPanel extends SimplePanel implements Focusable {
	
	static interface InvoiceRectificationDataPanelCallback {
		void onAccept(InvoiceRectificationData data);
		void onCancel();
	}

	static FinanceServiceAsync financeService;
	
	private static void initializeFinanceService() {
		if (financeService == null) {
			FinanceServiceAsync financeServiceRaw = GWT.create(FinanceService.class);
			financeService = new FinanceServiceAsyncDecorator(financeServiceRaw);
		}
	}
	
	private AonDateBox issueDate; 
	
	void show(final InvoicePanelCallback invoiceCallback
			, final InvoiceRectificationData data
			, final InvoiceRectificationDataPanelCallback callback) {
		setWidth("500px");
		setHeight("200px");
		
		
		FlowPanel rootPanel = new FlowPanel();
		
		final AonErrorPanel errorPanel = new AonErrorPanel();
		rootPanel.add(errorPanel);
		
		FlowPanel tablePanel = new FlowPanel();
		tablePanel.setStyleName(AON.CSS.aonScrollArea());
		
		KeyUpHandler keyUpHandler = event -> {
			if (event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
				callback.onCancel();	
			}
		};
		
		FlexTable table = new FlexTable();
		table.setStyleName(AON.CSS.aonTable());
		int row = 0;
		
		table.setWidget(row,0,new InlineLabel(AON.MSG.rectifyInvoiceDate()));
		table.getCellFormatter().setStyleName(row, 0, AON.CSS.aonTableLabel());
		issueDate = new AonDateBox();
		issueDate.setValue(data.getIssueDate());
		issueDate.getTextBox().addKeyUpHandler( keyUpHandler);
		issueDate.addValueChangeHandler(new ValueChangeHandler<Date>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				data.setIssueDate(event.getValue());
			}
		});
		
		table.setWidget(row,1,issueDate);
		row++;

//		if (data.isSales()) {
//			table.setWidget(row,0,new InlineLabel(AON.MSG.series()));
//			table.getCellFormatter().setStyleName(row, 0, AON.CSS.aonTableLabel());
//			
//			FlowPanel panel = new FlowPanel();
//			final ListBox seriesBox = new ListBox();
//			final AonIntegerBox number = new AonIntegerBox();
//			
//			seriesBox.addKeyUpHandler(keyUpHandler);
//			seriesBox.addItem(" ---- ", "");
//			seriesBox.addChangeHandler(new ChangeHandler() {
//				@Override
//				public void onChange(ChangeEvent event) {
//					data.setSeries(seriesBox.getSelectedIndex() == 0 ? null : seriesBox.getSelectedValue());
//					initializeFinanceService();
//					financeService.getInvoiceNextNumber(
//						 invoiceCallback.getOccam().getDomainName()
//						,invoiceCallback.getOccam().getDomain()
//						,invoiceCallback.getOccam().getUser()
//						,new Byte[]{data.getType().value()}
//						,data.getSeries()
//						, new AsyncCallback<Integer>() {
//
//							@Override
//							public void onFailure(Throwable caught) {
//								errorPanel.showError(caught.getMessage());
//							}
//
//							@Override
//							public void onSuccess(Integer result) {
//								number.setValue(result,false,true);
//								data.setNumber(result);
//							}
//						});
//				}
//			});
//
//			int i = 0;
//			 
//			if (invoiceCallback.getConfiguration() != null && invoiceCallback.getConfiguration().getInvoiceRectificationSalesSeries() != null) {
//				for (String series : invoiceCallback.getConfiguration().getInvoiceRectificationSalesSeries()) {
//					seriesBox.addItem(series,series);
//					if (i == 0) {
//						seriesBox.setSelectedIndex(1);
//						data.setSeries(series);
//						initializeFinanceService();
//						financeService.getInvoiceNextNumber(
//							 invoiceCallback.getOccam().getDomainName()
//							,invoiceCallback.getOccam().getDomain()
//							,invoiceCallback.getOccam().getUser()
//							,new Byte[]{data.getType().value()}
//							 , data.getSeries()
//							, new AsyncCallback<Integer>() {
//
//								@Override
//								public void onFailure(Throwable caught) {
//									errorPanel.showError(caught.getMessage());
//								}
//
//								@Override
//								public void onSuccess(Integer result) {
//									number.setValue(result,false,true);
//									data.setNumber(result);
//								}
//							});
//					}
//					i++;
//				}
//			}
//			panel.add(seriesBox);
//			
//			number.setStyleName(AON.CSS.aonMarginLeftSep());
//			number.addStyleName(AON.CSS.aonInputText());
//			number.addKeyUpHandler(keyUpHandler);
//			number.addValueChangeHandler(event -> data.setNumber(event.getValue()));
//			number.setVisibleLength(8);
//			number.setMaxLength(8);
//			panel.add(number);
//
//			table.setWidget(row,1,panel);
//			row++;
//		} else {
			table.setWidget(row,0,new InlineLabel(AON.MSG.invoiceNumber()));
			table.getCellFormatter().setStyleName(row, 0, AON.CSS.aonTableLabel());
			TextBox referenceCode = new TextBox();
			referenceCode.setStyleName(AON.CSS.aonInputText());
			referenceCode.addKeyUpHandler(keyUpHandler);
			referenceCode.addValueChangeHandler(event -> data.setReferenceCode(event.getValue()));
			referenceCode.setVisibleLength(15); 
			referenceCode.setMaxLength(32);
			table.setWidget(row,1,referenceCode);
			row++;
//		}
		
		table.setWidget(row,0,new InlineLabel(AON.MSG.reason()));
		table.getCellFormatter().setStyleName(row, 0, AON.CSS.aonTableLabel());
		final TextArea commentsBox = new TextArea();		
		commentsBox.setValue(data.getCause());
		commentsBox.setCharacterWidth(25);
		commentsBox.setStyleName(AON.CSS.aonInputText());
		commentsBox.addKeyUpHandler( keyUpHandler);
		commentsBox.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				data.setCause(event.getValue());
			}
		});
		table.setWidget(row,1,commentsBox);
		row++;

		table.setWidget(row,0,new InlineLabel());
		table.getCellFormatter().setStyleName(row, 0, AON.CSS.aonTableLabel());
		final CheckBox settleFinance = new CheckBox(AON.MSG.settleFinances());		
		settleFinance.setValue(data.isSettleFinances());
		settleFinance.addKeyUpHandler( keyUpHandler);
		settleFinance.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				data.setSettleFinances(settleFinance.getValue());
			}
		});
		table.setWidget(row,1,settleFinance);

		tablePanel.add( table );
		rootPanel.add( tablePanel );
		
		FlowPanel buttons = new FlowPanel();
    	buttons.setStyleName(AON.CSS.aonTextCenter());
    	
    	final Button okButton = new Button();
    	okButton.setStyleName(AON.CSS.aonOkButton());
    	okButton.setText( AON.MSG.accept());
    	okButton.addKeyUpHandler( keyUpHandler);
    	okButton.addClickHandler(event -> {
			okButton.setEnabled(false);
			callback.onAccept(data);
		});
    	
    	buttons.add(okButton);
    	
    	final Button cancelButton = new Button();
    	cancelButton.setStyleName(AON.CSS.aonCancelButton());
    	cancelButton.addStyleName(AON.CSS.aonMarginLeft());
    	cancelButton.setText( AON.MSG.cancelAction());
    	cancelButton.addKeyUpHandler( keyUpHandler);
    	cancelButton.addClickHandler(event -> {
			cancelButton.setEnabled(false);
			callback.onCancel();
		});
    	buttons.add(cancelButton);
    	rootPanel.add(buttons);
		setWidget(rootPanel);
		issueDate.setFocus(true);
	}

	@Override
	public int getTabIndex() {
		return issueDate.getTabIndex();
	}

	@Override
	public void setAccessKey(char key) {
		issueDate.setAccessKey(key);
	}

	@Override
	public void setFocus(boolean focused) {
		issueDate.getTextBox().selectAll();
		issueDate.setFocus(focused);
		issueDate.hideDatePicker();
	}

	@Override
	public void setTabIndex(int index) {
		issueDate.setTabIndex(index);
	}

}
