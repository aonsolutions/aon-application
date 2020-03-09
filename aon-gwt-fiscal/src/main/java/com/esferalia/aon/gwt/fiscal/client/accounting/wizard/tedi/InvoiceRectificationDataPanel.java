package com.esferalia.aon.gwt.fiscal.client.accounting.wizard.tedi;

import java.util.Date;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.common.client.widget.ErrorPanel;
import com.esferalia.aon.gwt.common.client.widget.IntegerBox;
import com.esferalia.aon.gwt.fiscal.client.FinanceService;
import com.esferalia.aon.gwt.fiscal.client.FinanceServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.FinanceServiceAsyncDecorator;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.finance.InvoiceRectificationData;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;

public class InvoiceRectificationDataPanel extends SimplePanel implements Focusable {
	
	public static interface InvoiceRectificationDataPanelCallback {
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
	
	private DateBoxEx issueDate; 
	
	
	public void show(final String domainName,final int domain
			, final String user
			, final AonConfiguration config
			, final InvoiceRectificationData data
			, final InvoiceRectificationDataPanelCallback callback) {
		setWidth("500px");
		setHeight("200px");
		
		
		FlowPanel rootPanel = new FlowPanel();
		
		final ErrorPanel errorPanel = new ErrorPanel();
		rootPanel.add(errorPanel);
		
		FlowPanel tablePanel = new FlowPanel();
		tablePanel.setStyleName(AON.AON_CSS.aonScrollArea());
		
		KeyUpHandler keyUpHandler = new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
					callback.onCancel();	
				}
			}
		};

		
		FlexTable table = new FlexTable();
		table.setStyleName(AON.AON_CSS.aonPanelGrid());
		table.addStyleName(AON.AON_CSS.aonWidthAll());
		int row = 0;
		
		table.setWidget(row,0,new InlineLabel(AON.MSG.rectifyInvoiceDate()));
		table.getCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonPanelGridOdd());
		issueDate = new DateBoxEx();
		issueDate.setValue(data.getIssueDate());
		issueDate.getTextBox().addKeyUpHandler( keyUpHandler);
		issueDate.addValueChangeHandler(new ValueChangeHandler<Date>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				data.setIssueDate(event.getValue());
			}
		});
		
		table.setWidget(row,1,issueDate);
		table.getCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
		row++;

		if (data.isSales()) {
			table.setWidget(row,0,new InlineLabel(AON.MSG.series()));
			table.getCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonPanelGridOdd());
			
			FlowPanel panel = new FlowPanel();
			final ListBox seriesBox = new ListBox();
			final IntegerBox number = new IntegerBox();
			
			seriesBox.addKeyUpHandler(keyUpHandler);
			seriesBox.addItem(" ---- ", "");
			seriesBox.addChangeHandler(new ChangeHandler() {
				@Override
				public void onChange(ChangeEvent event) {
					data.setSeries(seriesBox.getSelectedIndex() == 0 ? null : seriesBox.getSelectedValue());
					initializeFinanceService();
					financeService.getInvoiceNextNumber(
							 domainName
							,domain
							,user
							,new Byte[]{data.getType().value()}
							 , data.getSeries()
							, new AsyncCallback<Integer>() {

								@Override
								public void onFailure(Throwable caught) {
									errorPanel.showError(caught.getMessage());
								}

								@Override
								public void onSuccess(Integer result) {
									number.setValue(result,false,true);
									data.setNumber(result);
								}
							});
				}
			});

			int i = 0;
			if (config.getInvoiceRectificationSalesSeries() != null) {
				for (String series : config.getInvoiceRectificationSalesSeries()) {
					seriesBox.addItem(series,series);
					if (i == 0) {
						seriesBox.setSelectedIndex(1);
						data.setSeries(series);
						initializeFinanceService();
						financeService.getInvoiceNextNumber(
								 domainName
								,domain
								,user
								,new Byte[]{data.getType().value()}
								 , data.getSeries()
								, new AsyncCallback<Integer>() {

									@Override
									public void onFailure(Throwable caught) {
										errorPanel.showError(caught.getMessage());
									}

									@Override
									public void onSuccess(Integer result) {
										number.setValue(result,false,true);
										data.setNumber(result);
									}
								});
					}
					i++;
				}
			}
			panel.add(seriesBox);
			
			number.setStyleName(AON.AON_CSS.aonMarginLeft5());
			number.addStyleName(AON.AON_CSS.aonInputText());
			number.addKeyUpHandler(keyUpHandler);
			number.addValueChangeHandler(new ValueChangeHandler<Integer>() {
				
				@Override
				public void onValueChange(ValueChangeEvent<Integer> event) {
					data.setNumber(event.getValue());
				}
			});
			number.setVisibleLength(8);
			number.setMaxLength(8);
			panel.add(number);

			table.setWidget(row,1,panel);
			table.getCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
			row++;
		} else {
			table.setWidget(row,0,new InlineLabel(AON.MSG.number()));
			table.getCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonPanelGridOdd());
			TextBox referenceCode = new TextBox();
			referenceCode.setStyleName(AON.AON_CSS.aonInputText());
			referenceCode.addKeyUpHandler(keyUpHandler);
			referenceCode.addValueChangeHandler(new ValueChangeHandler<String>() {
				@Override
				public void onValueChange(ValueChangeEvent<String> event) {
					data.setReferenceCode(event.getValue());
				}
			});
			referenceCode.setVisibleLength(15); 
			referenceCode.setMaxLength(32);
			table.setWidget(row,1,referenceCode);
			table.getCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
			row++;
		}
		
		table.setWidget(row,0,new InlineLabel(AON.MSG.reason()));
		table.getCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonPanelGridOdd());
		final TextArea commentsBox = new TextArea();		
		commentsBox.setValue(data.getCause());
		commentsBox.setCharacterWidth(25);
		commentsBox.setStyleName(AON.AON_CSS.aonInputText());
		commentsBox.addKeyUpHandler( keyUpHandler);
		commentsBox.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				data.setCause(event.getValue());
			}
		});
		table.setWidget(row,1,commentsBox);
		table.getCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
		row++;

		table.setWidget(row,0,new InlineLabel());
		table.getCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonPanelGridOdd());
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
		table.getCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
		row++;

		tablePanel.add( table );
		rootPanel.add( tablePanel );
		
		FlowPanel buttons = new FlowPanel();
    	buttons.setStyleName(AON.AON_CSS.aonTextCenter());
    	
    	final Button okButton = new Button();
    	okButton.setStyleName(AON.AON_CSS.aonConfirmDialogOkButton());
    	okButton.setText( AON.MSG.accept());
    	okButton.addKeyUpHandler( keyUpHandler);
    	okButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				okButton.setEnabled(false);
				callback.onAccept(data);
			}
		});
    	
    	buttons.add(okButton);
    	
    	final Button cancelButton = new Button();
    	cancelButton.setStyleName(AON.AON_CSS.aonConfirmDialogCancelButton());
    	cancelButton.addStyleName(AON.AON_CSS.aonMarginLeft());
    	cancelButton.setText( AON.MSG.cancelAction());
    	cancelButton.addKeyUpHandler( keyUpHandler);
    	cancelButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				cancelButton.setEnabled(false);
				callback.onCancel();
			}
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
