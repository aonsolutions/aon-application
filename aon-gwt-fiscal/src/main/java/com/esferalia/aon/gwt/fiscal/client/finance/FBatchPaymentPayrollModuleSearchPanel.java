package com.esferalia.aon.gwt.fiscal.client.finance;

import java.util.Date;
import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonSearchPanelButton;
import com.esferalia.aon.gwt.fiscal.client.FinanceService;
import com.esferalia.aon.gwt.fiscal.client.FinanceServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.FinanceServiceAsyncDecorator;
import com.esferalia.aon.gwt.fiscal.client.finance.FBatchPaymentPayrollModule.FBATCH_TYPE;
import com.esferalia.aon.occam.api.model.FBatchParams;
import com.esferalia.aon.occam.api.model.finance.FBatch;
import com.esferalia.aon.occam.api.model.registry.RegistryBank;
import com.esferalia.aon.occam.api.model.type.FBatchStatus;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;


public class FBatchPaymentPayrollModuleSearchPanel extends SimpleLayoutPanel implements Focusable, HasValueChangeHandlers<FBatchParams>{
	
	// Variables
	
	private static FinanceServiceAsync FINANCE_SERVICE;

	private TextBox description;
	
	private AonDateBox fromIssueDate;
	private AonDateBox toIssueDate;
	
	private ListBox bank;
	private ListBox type;
	
	private ListBox status;
	private ListBox confidential;
	
	private AonSearchPanelButton cleanButton;
	private AonSearchPanelButton refreshButton;
	
	public static interface IFBatchPanelCallback {
		boolean isSelected(FBatch finance);
	}
	
	// -------------------------------------------------------------------
	// -----------------------  CONSTRUCTOR  -----------------------------
	// -------------------------------------------------------------------
	
	public FBatchPaymentPayrollModuleSearchPanel(final FinanceModuleOptions opt, FBATCH_TYPE fbatchType) {
		FinanceServiceAsync financeServiceRaw = GWT.create(FinanceService.class);
		FINANCE_SERVICE = new FinanceServiceAsyncDecorator(financeServiceRaw);
		
		setStyleName(AON.CSS.aonSearchPanel());
		addStyleName(AON.CSS.aonScrollArea());
		addStyleName(AON.CSS.aonMarginBottom());
		addStyleName(AON.CSS.aonMarginLeft());
		addStyleName(AON.CSS.aonMarginRight());
		addStyleName(AON.CSS.aonBlockCenter());
		
		description = new TextBox();
		description.setStyleName(AON.CSS.aonInputText());
		description.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> arg0) {
				search(opt);
			}
		});
		
		fromIssueDate = new AonDateBox();
		fromIssueDate.addValueChangeHandler(new ValueChangeHandler<Date>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Date> arg0) {
				search(opt);
			}
		});
		
		toIssueDate = new AonDateBox();
		toIssueDate.addValueChangeHandler(new ValueChangeHandler<Date>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Date> arg0) {
				search(opt);
			}
		});
		
		bank = new ListBox();
		bank.addItem("-");
		bank.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				search(opt);
			}
		});
		
		FINANCE_SERVICE.getCompanyBanks(opt.getDomainName(), opt.getDomain(), opt.getUser(), new AsyncCallback<LinkedList<RegistryBank>>() {
			
			@Override
			public void onSuccess(LinkedList<RegistryBank> companyBanks) {
				companyBanks.stream().filter(companyBank -> companyBank.isActive()).forEach(companyBank -> {
					bank.addItem("(" + companyBank.getAlias() + ") " + companyBank.getBankAccount().toString(), companyBank.getId().toString());
				});
			}
			
			@Override
			public void onFailure(Throwable arg0) {
				// TODO Auto-generated method stub
			}
		});
		
		type = new ListBox();
		if(FBATCH_TYPE.PAYROLL_PAYMENT == fbatchType)
			type.addItem("SEPA 34-14 N\u00f3mina (XML)", "10");
		else if(FBATCH_TYPE.PAYMENT == fbatchType) {
			type.addItem("VISA", "0");
			type.addItem("SEPA 34-14 (XML)", "9");
		}
		type.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				search(opt);
			}
		});
		
		status = new ListBox();
		status.addItem("-");
		for(int i=0; i < FBatchStatus.values().length; i++) {
			FBatchStatus fBatchStatus = FBatchStatus.values()[i];
			status.addItem(fBatchStatus.getDescription(), i + "");
		}
		status.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				search(opt);
			}
		});
		
		confidential = new ListBox();
		confidential.addItem("Todo", "");
		confidential.addItem("No Confidencial", "0");
		confidential.addItem("Confidencial", "1");
		confidential.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				search(opt);
			}
		});
		
		cleanButton = new AonSearchPanelButton(AON.MSG.clean(), AON.CSS.aonIconClear());
		cleanButton.addStyleName(AON.CSS.aonMarginLeft());
		cleanButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				initialize(opt);
				search(opt);
			}
		});

		refreshButton = new AonSearchPanelButton(AON.MSG.refresh(), AON.CSS.aonIconSearch());
		refreshButton.addStyleName(AON.CSS.aonMarginLeft());
		refreshButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				search(opt);
			}
		});

		FlexTable tab = new FlexTable();
		tab.getElement().getStyle().setPadding(5, Unit.PX);
		
		tab.getColumnFormatter().setWidth(0, "110px");
		tab.getColumnFormatter().setWidth(1, "250px");
		tab.getColumnFormatter().setWidth(2, "110px");
		tab.getColumnFormatter().setWidth(3, "250px");
		tab.getColumnFormatter().setWidth(4, "auto");
		
		int row = 0;
		int col = 0;
		
		// --- First row
		
		tab.getCellFormatter().setStyleName(row, col, AON.CSS.aonSearchPanelLabel());
		tab.setWidget(row, col, new Label(AON.MSG.description()));
		++col;
		
		tab.setWidget(row, col, description);
		++col;
		
		tab.getCellFormatter().setStyleName(row, col, AON.CSS.aonSearchPanelLabel());
		tab.setWidget(row, col, new Label(AON.MSG.date()));
		++col;
		
		FlowPanel issueDatePanel = new FlowPanel();
		issueDatePanel.setStyleName(AON.CSS.aonNowrap());
		issueDatePanel.add(fromIssueDate);
		InlineLabel dueTo = new InlineLabel(AON.MSG.to());
		dueTo.setStyleName(AON.CSS.aonItalic());
		dueTo.addStyleName(AON.CSS.aonMarginRight());
		dueTo.addStyleName(AON.CSS.aonMarginLeft());
		issueDatePanel.add(dueTo);
		issueDatePanel.add(toIssueDate);
		tab.setWidget(row, col, issueDatePanel);
		++col;
		
		tab.setWidget(row, col, new InlineLabel());
		++col;
		
		++row;
		col = 0;
		
		// --- Second row
		
		tab.getCellFormatter().setStyleName(row, col, AON.CSS.aonSearchPanelLabel());
		tab.setWidget(row, col, new Label(AON.MSG.bankAccount()));
		++col;
		
		tab.setWidget(row, col, bank);
		++col;
		
		tab.getCellFormatter().setStyleName(row, col, AON.CSS.aonSearchPanelLabel());
		tab.setWidget(row, col, new Label(AON.MSG.type()));
		++col;
		
		tab.setWidget(row, col, type);
		++col;
		
		tab.setWidget(row, col, new InlineLabel());
		++col;
		
		++row;
		col = 0;
		
		// --- Third row
		
		tab.getCellFormatter().setStyleName(row, col, AON.CSS.aonSearchPanelLabel());
		tab.setWidget(row, col, new Label(AON.MSG.status()));
		++col;
		
		tab.setWidget(row, col, status);
		++col;
		
		tab.getCellFormatter().setStyleName(row, col, AON.CSS.aonSearchPanelLabel());
		tab.setWidget(row, col, new Label(AON.MSG.confidential()));
		++col;
		
		tab.setWidget(row, col, confidential);
		++col;
		
		FlowPanel buttonsPanel = new FlowPanel();
		buttonsPanel.setStyleName(AON.CSS.aonNowrap());
		buttonsPanel.add( cleanButton );
		buttonsPanel.add( refreshButton );
		tab.setWidget(row, col, buttonsPanel);
		
		
		setWidget(tab);
		initialize(opt);
	}
	
	@Override
	public int getTabIndex() {
		return description.getTabIndex();
	}

	@Override
	public void setAccessKey(char key) {
		description.setAccessKey(key);;
	}

	@Override
	public void setFocus(boolean focused) {
		description.setFocus(true);
	}

	@Override
	public void setTabIndex(int index) {
		fromIssueDate.setTabIndex(index);
	}
	
	// -------------------------------------------------------------------
	// ---------------------  SEARCH & PARAMS  ---------------------------
	// -------------------------------------------------------------------

	private void search(final FinanceModuleOptions opt) {
		ValueChangeEvent.<FBatchParams>fire( FBatchPaymentPayrollModuleSearchPanel.this, getParams( opt ) ); 
	}

	public void initialize(final FinanceModuleOptions opt) {
		description.setValue(null);
		fromIssueDate.setValue(null);
		toIssueDate.setValue(null);
		bank.setSelectedIndex(0);
		type.setSelectedIndex(0);
		status.setSelectedIndex(0);
		confidential.setSelectedIndex(0);
	}
	
	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<FBatchParams> handler) {
		return super.addHandler(handler, ValueChangeEvent.getType());
	}

	public FBatchParams getParams( final FinanceModuleOptions opt) {
		return new FBatchParams()
			.setDomain(opt.getDomain())
			.setDomainName(opt.getDomainName())
			.setDescription(description.getValue())
			.setFromIssueDate(fromIssueDate.getValue())
			.setToIssueDate(toIssueDate.getValue())
			.setRbank(bank.getSelectedIndex() == 0 ? null : Integer.parseInt(bank.getSelectedValue()))
			.setType((byte) Integer.parseInt(type.getSelectedValue()))
			.setStatus(status.getSelectedIndex() == 0 ? null : (byte) Integer.parseInt(status.getSelectedValue()))
			.setConfidential(confidential.getSelectedIndex() == 0 ? null : AonStringUtils.equalsIgnoreCase(confidential.getSelectedValue(), "1"))
			;
	}
	
}
