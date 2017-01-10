package com.esferalia.aon.gwt.fiscal.client.accounting.wizard;

import java.util.Date;
import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.AccountBox;
import com.esferalia.aon.gwt.common.client.widget.ConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.ConfirmDialog.ConfirmDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryModule;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryModule.IAccountEntryModuleCallback;
import com.esferalia.aon.gwt.fiscal.client.accounting.panel.SessionLog;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountEntryDetail;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.IAccountEntryWrapper;
import com.esferalia.aon.occam.api.model.SalaryEntry;
import com.esferalia.aon.occam.api.model.type.AccountEntryType;
import com.esferalia.aon.watson.mutable.MutableBoolean;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;


public class SalaryPanel extends WizardContentBase<SalaryEntry> {
	private static DateTimeFormat DATE_FORMAT = DateTimeFormat.getFormat("dd/MM/yyyy");
	
	static final String BACKGROUND_COLOR = "#efdcc3";
	
	private FlexTable flexTable;
	private SessionLog workingLog;
	
	private TextBox concept;
	private DoubleBox  moneySalary;
	private AccountBox moneySalaryAccount;
	private DoubleBox  inKindSalary;
	private AccountBox inKindSalaryAccount;
	private DoubleBox  allowances;
	private AccountBox allowancesAccount;
	private DoubleBox  salaryCompensations;
	private AccountBox salaryCompensationsAccount;
	private DoubleBox  salaryDedAdvPayment;
	private AccountBox salaryDedAdvPaymentAccount;
	private DoubleBox  salaryDedSeize;
	private AccountBox salaryDedSeizeAccount;
	private DoubleBox  salaryOtherDeductions;
	private AccountBox salaryOtherDeductionsAccount;
	//private DoubleBox  totalAccrued;
	private DoubleBox  irpf;
	private AccountBox irpfAccount;
	private DoubleBox  inKindIrpf;
	private AccountBox inKindIrpfAccount;
	private DoubleBox  employeeSocialInsurance;
	private AccountBox employeeSocialInsuranceAccount;
	private DoubleBox  companySocialInsurance;
	private AccountBox companySocialInsuranceAccount;
	private DoubleBox  netSalary;
	private AccountBox netSalaryAccount;
	
	private FlowPanel monthResultPanel = new  FlowPanel();
	private ListBox monthListBox;
	private MutableBoolean monthListBoxInitialized = new MutableBoolean(false);
	private LinkedList<SalaryEntry> salaryEntries;
	
	private SalaryEntry salaryEntry;
	
	public SalaryPanel(final IAccountEntryModuleCallback callback) {
		setCallback(callback);
		
		SplitLayoutPanel rootPanel = new SplitLayoutPanel(4);
		
		workingLog = new SessionLog();
		rootPanel.addSouth(workingLog, 150);

		rootPanel.addEast(createExtraPanel(callback), 380);
		
		ScrollPanel centerPanel = new ScrollPanel();
		centerPanel.setStyleName(AON.AON_CSS.aonInvoicePanel());
		centerPanel.getElement().getStyle().setBackgroundColor(SalaryPanel.BACKGROUND_COLOR);
		createFlexTable();
		centerPanel.setWidget(flexTable);
		rootPanel.add(centerPanel);

		initWidget(rootPanel);
	}

	@Override
	public SalaryEntry getWrapper() {
		return salaryEntry;
	}

	@Override
	public void setWrapper(SalaryEntry wrapper) {
		this.salaryEntry = wrapper;
	}

	private void createFlexTable() {
		int row = 0;
		flexTable = new FlexTable();
		flexTable.setStyleName(AON.AON_CSS.aonMarginTop());
		flexTable.addStyleName(AON.AON_CSS.aonMarginLeft());
		flexTable.addStyleName(AON.AON_CSS.aonWidth90Percent());
		
		String col0Width = AON.AON_CSS.aonWidth170(); 
		String col1Width = AON.AON_CSS.aonWidth140();
		String col2Width = AON.AON_CSS.aonWidth70();
		
		Label label = new Label(AON.MSG.concept());
		label.setStyleName(AON.AON_CSS.aonInnerLabel());
		flexTable.getCellFormatter().setStyleName(row, 0, col0Width);
		flexTable.getCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonNowrap());
		flexTable.setWidget(row, 0, label);
		
		concept = new TextBox();
		concept.setStyleName(AON.AON_CSS.aonInputText());
		concept.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				getWrapper().setConcept(concept.getValue());
				valueChanged();
			}
		});
		concept.setVisibleLength(25); 
		concept.setMaxLength(32);
		flexTable.setWidget(row, 1, concept);
		flexTable.getFlexCellFormatter().setColSpan(row, 1, 3);
		row++;
		
		
		flexTable.getRowFormatter().setStyleName(row, AON.AON_CSS.aonBorderTop());
		label = new Label(AON.MSG.moneySalary());
		label.setStyleName(AON.AON_CSS.aonInnerLabel());
		flexTable.getCellFormatter().setStyleName(row, 0, col0Width);
		flexTable.getCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonNowrap());
		flexTable.setWidget(row, 0, label);
		moneySalary = new DoubleBox();
		moneySalary.setValue(0.0);
		moneySalary.addValueChangeHandler( new ValueChangeHandler<Double>() {
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				getWrapper().setMoneySalary(event.getValue());
				valueChanged();
			}
		});
		flexTable.setWidget(row, 1, moneySalary);
		flexTable.getCellFormatter().setStyleName(row, 1, col1Width);
		label = new Label(AON.MSG.accountAbr());
		label.setStyleName(AON.AON_CSS.aonInnerLabel());
		flexTable.getCellFormatter().setStyleName(row, 2, col2Width);
		flexTable.getCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonNowrap());
		flexTable.setWidget(row, 2, label);
		moneySalaryAccount = createAccountBox();
		moneySalaryAccount.addSelectionHandler( new SelectionHandler<Account>() {
			@Override
			public void onSelection(SelectionEvent<Account> event) {
				getWrapper().setMoneySalaryAccount(event.getSelectedItem());
				valueChanged();
			}
		});
		flexTable.setWidget(row, 3, moneySalaryAccount);
		flexTable.getCellFormatter().setStyleName(row, 3, AON.AON_CSS.aonWidthAuto());
		row++;
		

		label = new Label(AON.MSG.inKindSalary());
		label.setStyleName(AON.AON_CSS.aonInnerLabel());
		flexTable.getCellFormatter().setStyleName(row, 0, col0Width);
		flexTable.getCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonNowrap());
		flexTable.setWidget(row, 0, label);
		inKindSalary = new DoubleBox();
		inKindSalary.setValue(0.0);
		inKindSalary.addValueChangeHandler( new ValueChangeHandler<Double>() {
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				getWrapper().setInKindSalary(event.getValue());
				valueChanged();
			}
		});
		flexTable.setWidget(row, 1, inKindSalary);
		flexTable.getCellFormatter().setStyleName(row, 1, col1Width);
		label = new Label(AON.MSG.accountAbr());
		label.setStyleName(AON.AON_CSS.aonInnerLabel());
		flexTable.getCellFormatter().setStyleName(row, 2, col2Width);
		flexTable.getCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonNowrap());
		flexTable.setWidget(row, 2, label);
		inKindSalaryAccount = createAccountBox();
		inKindSalaryAccount.addSelectionHandler( new SelectionHandler<Account>() {
			@Override
			public void onSelection(SelectionEvent<Account> event) {
				getWrapper().setInKindSalaryAccount(event.getSelectedItem());
				valueChanged();
			}
		});
		flexTable.setWidget(row, 3, inKindSalaryAccount);
		flexTable.getCellFormatter().setStyleName(row, 3, AON.AON_CSS.aonWidthAuto());
		row++;
		

		label = new Label(AON.MSG.allowances());
		label.setStyleName(AON.AON_CSS.aonInnerLabel());
		flexTable.getCellFormatter().setStyleName(row, 0, col0Width);
		flexTable.getCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonNowrap());
		flexTable.setWidget(row, 0, label);
		allowances = new DoubleBox();
		allowances.setValue(0.0);
		allowances.addValueChangeHandler( new ValueChangeHandler<Double>() {
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				getWrapper().setAllowance(event.getValue());
				valueChanged();
			}
		});
		flexTable.setWidget(row, 1, allowances);
		flexTable.getCellFormatter().setStyleName(row, 1, col1Width);
		label = new Label(AON.MSG.accountAbr());
		label.setStyleName(AON.AON_CSS.aonInnerLabel());
		flexTable.getCellFormatter().setStyleName(row, 2, col2Width);
		flexTable.getCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonNowrap());
		flexTable.setWidget(row, 2, label);
		allowancesAccount = createAccountBox();
		allowancesAccount.addSelectionHandler( new SelectionHandler<Account>() {
			@Override
			public void onSelection(SelectionEvent<Account> event) {
				getWrapper().setAllowanceAccount(event.getSelectedItem());
				valueChanged();
			}
		});
		flexTable.setWidget(row, 3, allowancesAccount);
		flexTable.getCellFormatter().setStyleName(row, 3, AON.AON_CSS.aonWidthAuto());
		row++;

		label = new Label(AON.MSG.salaryCompensations());
		label.setStyleName(AON.AON_CSS.aonInnerLabel());
		flexTable.getCellFormatter().setStyleName(row, 0, col0Width);
		flexTable.getCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonNowrap());
		flexTable.setWidget(row, 0, label);
		salaryCompensations = new DoubleBox();
		salaryCompensations.setValue(0.0);
		salaryCompensations.addValueChangeHandler( new ValueChangeHandler<Double>() {
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				getWrapper().setSalaryCompensation(event.getValue());
				valueChanged();
			}
		});
		flexTable.setWidget(row, 1, salaryCompensations);
		flexTable.getCellFormatter().setStyleName(row, 1, col1Width);
		label = new Label(AON.MSG.accountAbr());
		label.setStyleName(AON.AON_CSS.aonInnerLabel());
		flexTable.getCellFormatter().setStyleName(row, 2, col2Width);
		flexTable.getCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonNowrap());
		flexTable.setWidget(row, 2, label);
		salaryCompensationsAccount = createAccountBox();
		salaryCompensationsAccount.addSelectionHandler( new SelectionHandler<Account>() {
			@Override
			public void onSelection(SelectionEvent<Account> event) {
				getWrapper().setSalaryCompensationAccount(event.getSelectedItem());
				valueChanged();
			}
		});
		flexTable.setWidget(row, 3, salaryCompensationsAccount);
		flexTable.getCellFormatter().setStyleName(row, 3, AON.AON_CSS.aonWidthAuto());
		row++;
		
		label = new Label(AON.MSG.advance());
		label.setStyleName(AON.AON_CSS.aonInnerLabel());
		flexTable.getCellFormatter().setStyleName(row, 0, col0Width);
		flexTable.getCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonNowrap());
		flexTable.setWidget(row, 0, label);
		salaryDedAdvPayment = new DoubleBox();
		salaryDedAdvPayment.setValue(0.0);
		salaryDedAdvPayment.addValueChangeHandler( new ValueChangeHandler<Double>() {
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				getWrapper().setSalaryDedAdvPayment(event.getValue());
				valueChanged();
			}
		});
		flexTable.setWidget(row, 1, salaryDedAdvPayment);
		flexTable.getCellFormatter().setStyleName(row, 1, col1Width);
		label = new Label(AON.MSG.accountAbr());
		label.setStyleName(AON.AON_CSS.aonInnerLabel());
		flexTable.getCellFormatter().setStyleName(row, 2, col2Width);
		flexTable.getCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonNowrap());
		flexTable.setWidget(row, 2, label);
		salaryDedAdvPaymentAccount = createAccountBox();
		salaryDedAdvPaymentAccount.addSelectionHandler( new SelectionHandler<Account>() {
			@Override
			public void onSelection(SelectionEvent<Account> event) {
				getWrapper().setSalaryDedAdvPaymentAccount(event.getSelectedItem());
				valueChanged();
			}
		});
		flexTable.setWidget(row, 3, salaryDedAdvPaymentAccount);
		flexTable.getCellFormatter().setStyleName(row, 3, AON.AON_CSS.aonWidthAuto());
		row++;
		
		
		label = new Label(AON.MSG.seize());
		label.setStyleName(AON.AON_CSS.aonInnerLabel());
		flexTable.getCellFormatter().setStyleName(row, 0, col0Width);
		flexTable.getCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonNowrap());
		flexTable.setWidget(row, 0, label);
		salaryDedSeize = new DoubleBox();
		salaryDedSeize.setValue(0.0);
		salaryDedSeize.addValueChangeHandler( new ValueChangeHandler<Double>() {
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				getWrapper().setSalaryDedSeize(event.getValue());
				valueChanged();
			}
		});
		flexTable.setWidget(row, 1, salaryDedSeize);
		flexTable.getCellFormatter().setStyleName(row, 1, col1Width);
		label = new Label(AON.MSG.accountAbr());
		label.setStyleName(AON.AON_CSS.aonInnerLabel());
		flexTable.getCellFormatter().setStyleName(row, 2, col2Width);
		flexTable.getCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonNowrap());
		flexTable.setWidget(row, 2, label);
		salaryDedSeizeAccount = createAccountBox();
		salaryDedSeizeAccount.addSelectionHandler( new SelectionHandler<Account>() {
			@Override
			public void onSelection(SelectionEvent<Account> event) {
				getWrapper().setSalaryDedSeizeAccount(event.getSelectedItem());
				valueChanged();
			}
		});
		flexTable.setWidget(row, 3, salaryDedSeizeAccount);
		flexTable.getCellFormatter().setStyleName(row, 3, AON.AON_CSS.aonWidthAuto());
		row++;

		label = new Label(AON.MSG.salaryOtherDeductions());
		label.setStyleName(AON.AON_CSS.aonInnerLabel());
		flexTable.getCellFormatter().setStyleName(row, 0, col0Width);
		flexTable.getCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonNowrap());
		flexTable.setWidget(row, 0, label);
		salaryOtherDeductions = new DoubleBox();
		salaryOtherDeductions.setValue(0.0);
		salaryOtherDeductions.addValueChangeHandler( new ValueChangeHandler<Double>() {
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				getWrapper().setSalaryOtherDeductions(event.getValue());
				valueChanged();
			}
		});
		flexTable.setWidget(row, 1, salaryOtherDeductions);
		flexTable.getCellFormatter().setStyleName(row, 1, col1Width);
		label = new Label(AON.MSG.accountAbr());
		label.setStyleName(AON.AON_CSS.aonInnerLabel());
		flexTable.getCellFormatter().setStyleName(row, 2, col2Width);
		flexTable.getCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonNowrap());
		flexTable.setWidget(row, 2, label);
		salaryOtherDeductionsAccount = createAccountBox();
		salaryOtherDeductionsAccount.addSelectionHandler( new SelectionHandler<Account>() {
			@Override
			public void onSelection(SelectionEvent<Account> event) {
				getWrapper().setSalaryOtherDeductionsAccount(event.getSelectedItem());
				valueChanged();
			}
		});
		flexTable.setWidget(row, 3, salaryOtherDeductionsAccount);
		flexTable.getCellFormatter().setStyleName(row, 3, AON.AON_CSS.aonWidthAuto());
		row++;

//		label = new Label(AON.MSG.totalAccrued());
//		label.setStyleName(AON.AON_CSS.aonInnerLabel());
//		flexTable.getCellFormatter().setStyleName(row, 0, col0Width);
//		flexTable.getCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonNowrap());
//		flexTable.setWidget(row, 0, label);
//		totalAccrued = new DoubleBox();
//		totalAccrued.setEnabled(false);
//		flexTable.setWidget(row, 1, totalAccrued);
//		flexTable.getFlexCellFormatter().setColSpan(row, 1, 3);
//		row++;

		label = new Label(AON.MSG.irpf());
		label.setStyleName(AON.AON_CSS.aonInnerLabel());
		flexTable.getCellFormatter().setStyleName(row, 0, col0Width);
		flexTable.getCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonNowrap());
		flexTable.setWidget(row, 0, label);
		irpf = new DoubleBox();
		irpf.setValue(0.0);
		irpf.addValueChangeHandler( new ValueChangeHandler<Double>() {
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				getWrapper().setIrpf(event.getValue());
				valueChanged();
			}
		});
		flexTable.setWidget(row, 1, irpf);
		flexTable.getCellFormatter().setStyleName(row, 1, col1Width);
		label = new Label(AON.MSG.accountAbr());
		label.setStyleName(AON.AON_CSS.aonInnerLabel());
		flexTable.getCellFormatter().setStyleName(row, 2, col2Width);
		flexTable.getCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonNowrap());
		flexTable.setWidget(row, 2, label);
		irpfAccount = createAccountBox();
		irpfAccount.addSelectionHandler( new SelectionHandler<Account>() {
			@Override
			public void onSelection(SelectionEvent<Account> event) {
				getWrapper().setIrpfAccount(event.getSelectedItem());
				valueChanged();
			}
		});
		flexTable.setWidget(row, 3, irpfAccount);
		flexTable.getCellFormatter().setStyleName(row, 3, AON.AON_CSS.aonWidthAuto());
		row++;

		label = new Label(AON.MSG.inKindIrpf());
		label.setStyleName(AON.AON_CSS.aonInnerLabel());
		flexTable.getCellFormatter().setStyleName(row, 0, col0Width);
		flexTable.getCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonNowrap());
		flexTable.setWidget(row, 0, label);
		inKindIrpf = new DoubleBox();
		inKindIrpf.setValue(0.0);
		inKindIrpf.addValueChangeHandler( new ValueChangeHandler<Double>() {
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				getWrapper().setInKindIrpf(event.getValue());
				valueChanged();
			}
		});
		flexTable.setWidget(row, 1, inKindIrpf);
		flexTable.getCellFormatter().setStyleName(row, 1, col1Width);
		label = new Label(AON.MSG.accountAbr());
		label.setStyleName(AON.AON_CSS.aonInnerLabel());
		flexTable.getCellFormatter().setStyleName(row, 2, col2Width);
		flexTable.getCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonNowrap());
		flexTable.setWidget(row, 2, label);
		inKindIrpfAccount = createAccountBox();
		inKindIrpfAccount.addSelectionHandler( new SelectionHandler<Account>() {
			@Override
			public void onSelection(SelectionEvent<Account> event) {
				getWrapper().setInKindIrpfAccount(event.getSelectedItem());
				valueChanged();
			}
		});
		flexTable.setWidget(row, 3, inKindIrpfAccount);
		flexTable.getCellFormatter().setStyleName(row, 3, AON.AON_CSS.aonWidthAuto());
		row++;

		label = new Label(AON.MSG.employeeSocialInsurance());
		label.setStyleName(AON.AON_CSS.aonInnerLabel());
		flexTable.getCellFormatter().setStyleName(row, 0, col0Width);
		flexTable.getCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonNowrap());
		flexTable.setWidget(row, 0, label);
		employeeSocialInsurance = new DoubleBox();
		employeeSocialInsurance.setValue(0.0);
		employeeSocialInsurance.addValueChangeHandler( new ValueChangeHandler<Double>() {
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				getWrapper().setEmployeeSocialInsurance(event.getValue());
				valueChanged();
			}
		});
		flexTable.setWidget(row, 1, employeeSocialInsurance);
		flexTable.getCellFormatter().setStyleName(row, 1, col1Width);
		label = new Label(AON.MSG.accountAbr());
		label.setStyleName(AON.AON_CSS.aonInnerLabel());
		flexTable.getCellFormatter().setStyleName(row, 2, col2Width);
		flexTable.getCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonNowrap());
		flexTable.setWidget(row, 2, label);
		employeeSocialInsuranceAccount = createAccountBox();
		employeeSocialInsuranceAccount.addSelectionHandler( new SelectionHandler<Account>() {
			@Override
			public void onSelection(SelectionEvent<Account> event) {
				getWrapper().setEmployeeSocialInsuranceAccount(event.getSelectedItem());
				valueChanged();
			}
		});
		flexTable.setWidget(row, 3, employeeSocialInsuranceAccount);
		flexTable.getCellFormatter().setStyleName(row, 3, AON.AON_CSS.aonWidthAuto());
		row++;

		label = new Label(AON.MSG.companySocialInsurance());
		label.setStyleName(AON.AON_CSS.aonInnerLabel());
		flexTable.getCellFormatter().setStyleName(row, 0, col0Width);
		flexTable.getCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonNowrap());
		flexTable.setWidget(row, 0, label);
		companySocialInsurance = new DoubleBox();
		companySocialInsurance.setValue(0.0);
		companySocialInsurance.addValueChangeHandler( new ValueChangeHandler<Double>() {
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				getWrapper().setCompanySocialInsurance(event.getValue());
				valueChanged();
			}
		});
		flexTable.setWidget(row, 1, companySocialInsurance);
		flexTable.getCellFormatter().setStyleName(row, 1, col1Width);
		label = new Label(AON.MSG.accountAbr());
		label.setStyleName(AON.AON_CSS.aonInnerLabel());
		flexTable.getCellFormatter().setStyleName(row, 2, col2Width);
		flexTable.getCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonNowrap());
		flexTable.setWidget(row, 2, label);
		companySocialInsuranceAccount = createAccountBox();
		companySocialInsuranceAccount.addSelectionHandler( new SelectionHandler<Account>() {
			@Override
			public void onSelection(SelectionEvent<Account> event) {
				getWrapper().setCompanySocialInsuranceAccount(event.getSelectedItem());
				valueChanged();
			}
		});
		flexTable.setWidget(row, 3, companySocialInsuranceAccount);
		flexTable.getCellFormatter().setStyleName(row, 3, AON.AON_CSS.aonWidthAuto());
		row++;

		label = new Label(AON.MSG.netSalary());
		label.setStyleName(AON.AON_CSS.aonInnerLabel());
		flexTable.getCellFormatter().setStyleName(row, 0, col0Width);
		flexTable.getCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonNowrap());
		flexTable.setWidget(row, 0, label);
		netSalary = new DoubleBox();
		netSalary.setEnabled(false);
		
		flexTable.setWidget(row, 1, netSalary);
		flexTable.getCellFormatter().setStyleName(row, 1, col1Width);
		label = new Label(AON.MSG.accountAbr());
		label.setStyleName(AON.AON_CSS.aonInnerLabel());
		flexTable.getCellFormatter().setStyleName(row, 2, col2Width);
		flexTable.getCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonNowrap());
		flexTable.setWidget(row, 2, label);
		netSalaryAccount = createAccountBox();
		netSalaryAccount.addSelectionHandler( new SelectionHandler<Account>() {
			@Override
			public void onSelection(SelectionEvent<Account> event) {
				getWrapper().setNetSalaryAccount(event.getSelectedItem());
				valueChanged();
			}
		});
		flexTable.setWidget(row, 3, netSalaryAccount);
		flexTable.getCellFormatter().setStyleName(row, 3, AON.AON_CSS.aonWidthAuto());
		row++;
	}

	protected void valueChanged() {
//		totalAccrued.setValue( getWrapper().getTotalAccrued(), false, true );
		netSalary.setValue( getWrapper().getNetSalary(), false, true );
		getWrapper().getAccountEntry().setDetails( getEntryDetails() );
		_paintEntry();
	}

	private AccountBox createAccountBox() {
		AccountBox ab = new AccountBox(AccountEntryModule.getCurrentDomainName(), AccountEntryModule.getCurrentDomain());
		ab.addSelectionHandler(new SelectionHandler<Account>() {
			@Override
			public void onSelection(SelectionEvent<Account> event) {
				Account acc = event.getSelectedItem();
				callback.getModule().onBalance(acc);
			}
		});
		return ab;
	}

	@Override
	public void reset(final AccountEntry base,final ISelectionCallback cbk) {
		if (base == null) {
			getCallback().getModule().onError("[ERROR INTERNO] No hay un apunte base del que crear la factura");
		}
		SalaryEntry ai = new SalaryEntry()
			.setConcept("N\u00F3minas")
			.setMoneySalaryAccount( callback.getModule().getConfiguration().getDefaultSalary() )
			.setInKindSalaryAccount(callback.getModule().getConfiguration().getDefaultSalaryInKind())
			.setAllowanceAccount(callback.getModule().getConfiguration().getDefaultAllowance())
			.setSalaryCompensationAccount(callback.getModule().getConfiguration().getDefaultCompensation())
			.setSalaryDedAdvPaymentAccount(callback.getModule().getConfiguration().getSalaryDedAdvPayment())
			.setSalaryDedSeizeAccount(callback.getModule().getConfiguration().getSalaryDedSeize())
			.setSalaryOtherDeductionsAccount(callback.getModule().getConfiguration().getSalaryOtherDeductions())
			.setIrpfAccount(callback.getModule().getConfiguration().getSalaryChargedRet())
			.setInKindIrpfAccount(callback.getModule().getConfiguration().getSalaryChargedRetInKind())
			.setEmployeeSocialInsuranceAccount(	callback.getModule().getConfiguration().getDefaultSocialInsurance())
			.setCompanySocialInsuranceAccount(callback.getModule().getConfiguration().getDefaultCompanySocIns())
			.setNetSalaryAccount(callback.getModule().getConfiguration().getDefaultPendingSalary())
		;
		ai.setAccountEntry(new AccountEntry()
			.setEntryType(AccountEntryType.SALARY)
			.setPeriod(base.getPeriod())
			.setDomain(AccountEntryModule.getCurrentDomain())
			.setConfidential(base.isConfidential())
			.setEntryDate(base.getEntryDate())
			.setActivity(base.getActivity()));
		initializeMonthList();
		select(null, ai, cbk);
	}
	public void preselect(final Integer id,final SalaryEntry wrp,final ISelectionCallback cbk) {
		AccountEntry existing = getWrapper().getAccountEntry();
		wrp.getAccountEntry().setSecurityLevel(existing.getSecurityLevel());
		wrp.getAccountEntry().setPeriod(existing.getPeriod());
		wrp.getAccountEntry().setDomain(existing.getDomain());
		wrp.getAccountEntry().setEntryType(existing.getEntryType());
		wrp.getAccountEntry().setActivity(existing.getActivity());
		callback.getModule().changeEntryDate(wrp.getAccountEntry().getEntryDate());
		wrp.setConcept("N\u00F3minas " + DATE_FORMAT.format(wrp.getAccountEntry().getEntryDate()));
		wrp.setMoneySalaryAccount(getWrapper().getMoneySalaryAccount());
		wrp.setInKindSalaryAccount(getWrapper().getInKindSalaryAccount());
		wrp.setAllowanceAccount(getWrapper().getAllowanceAccount());
		wrp.setSalaryCompensationAccount(getWrapper().getSalaryCompensationAccount());
		wrp.setSalaryDedAdvPaymentAccount(getWrapper().getSalaryDedAdvPaymentAccount());
		wrp.setSalaryDedSeizeAccount(getWrapper().getSalaryDedSeizeAccount());
		wrp.setSalaryOtherDeductionsAccount(getWrapper().getSalaryOtherDeductionsAccount());
		wrp.setIrpfAccount(getWrapper().getIrpfAccount());
		wrp.setInKindIrpfAccount(getWrapper().getInKindIrpfAccount());
		wrp.setEmployeeSocialInsuranceAccount(getWrapper().getEmployeeSocialInsuranceAccount());
		wrp.setCompanySocialInsuranceAccount(getWrapper().getCompanySocialInsuranceAccount());
		wrp.setNetSalaryAccount(getWrapper().getNetSalaryAccount());
		setWrapper( (SalaryEntry) wrp);
		getCallback().getModule().onBalance(getWrapper().getAccountEntry());
		populate();
		valueChanged();
	}
	
	@Override
	public void select(final Integer id,final IAccountEntryWrapper wrp,final ISelectionCallback cbk) {
		workingLog.clear();
		if (id != null) {
			Window.alert("No se puede modificar");
		} else {
			if (wrp != null) {
				setWrapper( (SalaryEntry) wrp);
				getCallback().getModule().onBalance(getWrapper().getAccountEntry());
				populate();
				if (cbk != null) {
					cbk.onSuccess();
				}
			} else {
				getCallback().getModule().onError("Asiento no encontrado");
			}
		}
	}		

	private void populate() {
		concept.setValue(getWrapper().getConcept());
		moneySalary.setValue(getWrapper().getMoneySalary());
		setAccount(moneySalaryAccount,getWrapper().getMoneySalaryAccount());
		inKindSalary.setValue(getWrapper().getInKindSalary());
		setAccount(inKindSalaryAccount,getWrapper().getInKindSalaryAccount());
		allowances.setValue(getWrapper().getAllowance());
		setAccount(allowancesAccount,getWrapper().getAllowanceAccount());		
		salaryCompensations.setValue(getWrapper().getSalaryCompensation());
		setAccount(salaryCompensationsAccount,getWrapper().getSalaryCompensationAccount());
		salaryDedAdvPayment.setValue(getWrapper().getSalaryDedAdvPayment());
		setAccount(salaryDedAdvPaymentAccount,getWrapper().getSalaryDedAdvPaymentAccount());		
		salaryDedSeize.setValue(getWrapper().getSalaryDedSeize());
		setAccount(salaryDedSeizeAccount,getWrapper().getSalaryDedSeizeAccount());		
		salaryOtherDeductions.setValue(getWrapper().getSalaryOtherDeductions());
		setAccount(salaryOtherDeductionsAccount,getWrapper().getSalaryOtherDeductionsAccount());		
//		totalAccrued.setValue(getWrapper().getTotalAccrued());
		irpf.setValue(getWrapper().getIrpf());
		setAccount(irpfAccount,getWrapper().getIrpfAccount());
		inKindIrpf.setValue(getWrapper().getInKindIrpf());
		setAccount(inKindIrpfAccount,getWrapper().getInKindIrpfAccount());
		employeeSocialInsurance.setValue(getWrapper().getEmployeeSocialInsurance());
		setAccount(employeeSocialInsuranceAccount,getWrapper().getEmployeeSocialInsuranceAccount());
		companySocialInsurance.setValue(getWrapper().getCompanySocialInsurance());
		setAccount(companySocialInsuranceAccount,getWrapper().getCompanySocialInsuranceAccount());
		netSalary.setValue(getWrapper().getNetSalary());
		setAccount(netSalaryAccount,getWrapper().getNetSalaryAccount());
	}

	private void setAccount(AccountBox accountBox, Account account) {
		if (account != null) {
			accountBox.setValue(account.getId(),account.getCode(),account.getDescription(),false);
		} else {
			accountBox.setValue(null,null,null,false);
		}
	}

	private void _paintEntry() {
		onLog( getWrapper() );
	}
	
	public void onLog(IAccountEntryWrapper wrapper) {
		workingLog.clear();
		workingLog.addPreview(wrapper);			
	}
	
	public void onLog(IAccountEntryWrapper[] wrappers) {
		workingLog.clear();
		for (int i = (wrappers.length - 1); i>=0; i--) {
			workingLog.addPreview(wrappers[i]);
		}
	}
	@Override
	public boolean isUpdatable() {
		return (true);
	}
	
	@Override
	public void manageWidgets(boolean canRemove, boolean canEdit) {
	}

	private LinkedList<AccountEntryDetail> getEntryDetails() {
		if (getWrapper().getMoneySalary()  != 0 && (getWrapper().getMoneySalaryAccount() == null ||  getWrapper().getMoneySalaryAccount().getId() == null)) {
			String msg = "Debe indicar una cuenta contable para el valor 'Remuneraciones monetarias'";
			callback.getModule().onError(msg);
		}
		if (getWrapper().getInKindSalary()   != 0 && (getWrapper().getInKindSalaryAccount() == null || getWrapper().getInKindSalaryAccount().getId() == null)) {
			String msg = "Debe indicar una cuenta contable para el valor 'Remuneraciones en especie'";		
			callback.getModule().onError(msg);
		}
		if (getWrapper().getAllowance() != 0 	&& (getWrapper().getAllowanceAccount() == null || getWrapper().getAllowanceAccount().getId() == null)) {
			String msg = "Debe indicar una cuenta contable para el valor 'Dietas'";
			callback.getModule().onError(msg);
		}
		if (getWrapper().getSalaryCompensation() != 0 && (getWrapper().getSalaryCompensationAccount() == null || getWrapper().getSalaryCompensationAccount().getId() == null)) {
			String msg = "Debe indicar una cuenta contable para el valor 'Indemnizaciones'";		
			callback.getModule().onError(msg);
		}

		if (getWrapper().getSalaryDedAdvPayment() != 0 && (getWrapper().getSalaryDedAdvPaymentAccount() == null || getWrapper().getSalaryDedAdvPaymentAccount().getId() == null)) {
			String msg = "Debe indicar una cuenta contable para el valor 'Anticipo'";		
			callback.getModule().onError(msg);
		}
		
		if (getWrapper().getSalaryDedSeize() != 0 && (getWrapper().getSalaryDedSeizeAccount() == null || getWrapper().getSalaryDedSeizeAccount().getId() == null)) {
			String msg = "Debe indicar una cuenta contable para el valor 'Embargo'";		
			callback.getModule().onError(msg);
		}

		if (getWrapper().getSalaryOtherDeductions() != 0 && (getWrapper().getSalaryOtherDeductionsAccount() == null || getWrapper().getSalaryOtherDeductionsAccount().getId() == null)) {
			String msg = "Debe indicar una cuenta contable para el valor 'Otras deducciones'";		
			callback.getModule().onError(msg);
		}

		if (getWrapper().getCompanySocialInsurance() != 0	&& (getWrapper().getCompanySocialInsuranceAccount() == null || getWrapper().getCompanySocialInsuranceAccount().getId() == null)) {
			String msg = "Debe indicar una cuenta contable para el valor 'Seg.Social Empresa'";			
			callback.getModule().onError(msg);
		}
		if (getWrapper().getIrpf() != 0	&& (getWrapper().getIrpfAccount() == null || getWrapper().getIrpfAccount().getId() == null)) {	
			String msg = "Debe indicar una cuenta contable para el valor 'I.R.P.F.'";
			callback.getModule().onError(msg);
		}
		if (getWrapper().getInKindIrpf() != 0 && (getWrapper().getInKindIrpfAccount() == null || getWrapper().getInKindIrpfAccount().getId() == null)) {
			String msg = "Debe indicar una cuenta contable para el valor 'I.R.P.F. en especie'";		
			callback.getModule().onError(msg);
		}
		if (getWrapper().getTotalSocialInsurance() != 0 && (getWrapper().getEmployeeSocialInsuranceAccount() == null || getWrapper().getEmployeeSocialInsuranceAccount().getId() == null)) {	
			String msg = "Debe indicar una cuenta contable para el valor 'Seg.Social Empleado'";
			callback.getModule().onError(msg);
		}
		
		LinkedList<AccountEntryDetail> list = new LinkedList<AccountEntryDetail>();
		if (getWrapper().getMoneySalary() != 0 
			&& getWrapper().getMoneySalaryAccount() != null 
			&& getWrapper().getMoneySalaryAccount().getId() != null) {
			list.add( new AccountEntryDetail()
				.setAccount(getWrapper().getMoneySalaryAccount().getId())
				.setAccountCode(getWrapper().getMoneySalaryAccount().getCode())
				.setAccountDescription(getWrapper().getMoneySalaryAccount().getDescription())
				.setBalancingAccount(null)
				.setConcept(getWrapper().getConcept())
				.setDebit(getWrapper().getMoneySalary())
			);
		}

		if (getWrapper().getInKindSalary() != 0 
			&& getWrapper().getInKindSalaryAccount() != null 
			&& getWrapper().getInKindSalaryAccount().getId() != null) {
			list.add(new AccountEntryDetail()
				.setAccount(getWrapper().getInKindSalaryAccount().getId())
				.setAccountCode(getWrapper().getInKindSalaryAccount().getCode())
				.setAccountDescription(getWrapper().getInKindSalaryAccount().getDescription())
				.setBalancingAccount(null)
				.setConcept(getWrapper().getConcept())
				.setDebit(getWrapper().getInKindSalary())
			);
		}

		if (getWrapper().getAllowance() != 0 
			&& getWrapper().getAllowanceAccount() != null 
			&& getWrapper().getAllowanceAccount().getId() != null) {
			list.add(new AccountEntryDetail()
				.setAccount(getWrapper().getAllowanceAccount().getId())
				.setAccountCode(getWrapper().getAllowanceAccount().getCode())
				.setAccountDescription(getWrapper().getAllowanceAccount().getDescription())
				.setBalancingAccount(null)
				.setConcept(getWrapper().getConcept())
				.setDebit(getWrapper().getAllowance())
			);
		}

		if (getWrapper().getSalaryDedAdvPayment() != 0 
			&& getWrapper().getSalaryDedAdvPaymentAccount() != null 
			&& getWrapper().getSalaryDedAdvPaymentAccount().getId() != null) {
			list.add(new AccountEntryDetail()
				.setAccount(getWrapper().getSalaryDedAdvPaymentAccount().getId())
				.setAccountCode(getWrapper().getSalaryDedAdvPaymentAccount().getCode())
				.setAccountDescription(getWrapper().getSalaryDedAdvPaymentAccount().getDescription())
				.setBalancingAccount(null)
				.setConcept(getWrapper().getConcept())
				.setCredit(getWrapper().getSalaryDedAdvPayment())
			);
		}

		if (getWrapper().getSalaryDedSeize() != 0 
			&& getWrapper().getSalaryDedSeizeAccount() != null 
			&& getWrapper().getSalaryDedSeizeAccount().getId() != null) {
			list.add(new AccountEntryDetail()
				.setAccount(getWrapper().getSalaryDedSeizeAccount().getId())
				.setAccountCode(getWrapper().getSalaryDedSeizeAccount().getCode())
				.setAccountDescription(getWrapper().getSalaryDedSeizeAccount().getDescription())
				.setBalancingAccount(null)
				.setConcept(getWrapper().getConcept())
				.setCredit(getWrapper().getSalaryDedSeize())
			);
		}

		if (getWrapper().getSalaryOtherDeductions() != 0 
			&& getWrapper().getSalaryOtherDeductionsAccount() != null 
			&& getWrapper().getSalaryOtherDeductionsAccount().getId() != null) {
			list.add(new AccountEntryDetail()
				.setAccount(getWrapper().getSalaryOtherDeductionsAccount().getId())
				.setAccountCode(getWrapper().getSalaryOtherDeductionsAccount().getCode())
				.setAccountDescription(getWrapper().getSalaryOtherDeductionsAccount().getDescription())
				.setBalancingAccount(null)
				.setConcept(getWrapper().getConcept())
				.setCredit(getWrapper().getSalaryOtherDeductions())
			);
		}
		
		if (getWrapper().getSalaryCompensation() != 0 
				&& getWrapper().getSalaryCompensationAccount() != null 
				&& getWrapper().getSalaryCompensationAccount().getId() != null) {
				list.add(new AccountEntryDetail()
					.setAccount(getWrapper().getSalaryCompensationAccount().getId())
					.setAccountCode(getWrapper().getSalaryCompensationAccount().getCode())
					.setAccountDescription(getWrapper().getSalaryCompensationAccount().getDescription())
					.setBalancingAccount(null)
					.setConcept(getWrapper().getConcept())
					.setDebit(getWrapper().getSalaryCompensation())
				);
			}

		
		
		if (getWrapper().getCompanySocialInsurance() != 0 
			&& getWrapper().getCompanySocialInsuranceAccount() != null 
			&& getWrapper().getCompanySocialInsuranceAccount().getId() != null) {
			list.add(new AccountEntryDetail()
				.setAccount(getWrapper().getCompanySocialInsuranceAccount().getId())
				.setAccountCode(getWrapper().getCompanySocialInsuranceAccount().getCode())
				.setAccountDescription(getWrapper().getCompanySocialInsuranceAccount().getDescription())
				.setBalancingAccount(null)
				.setConcept(getWrapper().getConcept())
				.setDebit(getWrapper().getCompanySocialInsurance())
			);
		}

		if (getWrapper().getIrpf() != 0 
			&& getWrapper().getIrpfAccount() != null 
			&& getWrapper().getIrpfAccount().getId() != null) {
			list.add(new AccountEntryDetail()
				.setAccount(getWrapper().getIrpfAccount().getId())
				.setAccountCode(getWrapper().getIrpfAccount().getCode())
				.setAccountDescription(getWrapper().getIrpfAccount().getDescription())
				.setBalancingAccount(null)
				.setConcept(getWrapper().getConcept())
				.setCredit(getWrapper().getIrpf())
			);
		}

		if (getWrapper().getInKindIrpf() != 0 
			&& getWrapper().getInKindIrpfAccount() != null 
			&& getWrapper().getInKindIrpfAccount().getId() != null) {
			list.add(new AccountEntryDetail()
				.setAccount(getWrapper().getInKindIrpfAccount().getId())
				.setAccountCode(getWrapper().getInKindIrpfAccount().getCode())
				.setAccountDescription(getWrapper().getInKindIrpfAccount().getDescription())
				.setBalancingAccount(null)
				.setConcept(getWrapper().getConcept())
				.setCredit(getWrapper().getInKindIrpf())
			);
		}

		if (getWrapper().getTotalSocialInsurance() != 0 
			&& getWrapper().getEmployeeSocialInsuranceAccount() != null 
			&& getWrapper().getEmployeeSocialInsuranceAccount().getId() != null) {
			list.add(new AccountEntryDetail()
				.setAccount(getWrapper().getEmployeeSocialInsuranceAccount().getId())
				.setAccountCode(getWrapper().getEmployeeSocialInsuranceAccount().getCode())
				.setAccountDescription(getWrapper().getEmployeeSocialInsuranceAccount().getDescription())
				.setBalancingAccount(null)
				.setConcept(getWrapper().getConcept())
				.setCredit(getWrapper().getTotalSocialInsurance())
			);
		}

		if (getWrapper().getNetSalary() != 0
			&& getWrapper().getNetSalaryAccount() != null 
			&& getWrapper().getNetSalaryAccount().getId() != null) {
			list.add(new AccountEntryDetail()
				.setAccount(getWrapper().getNetSalaryAccount().getId())
				.setAccountCode(getWrapper().getNetSalaryAccount().getCode())
				.setAccountDescription(getWrapper().getNetSalaryAccount().getDescription())
				.setBalancingAccount(null)
				.setConcept(getWrapper().getConcept())
				.setCredit(getWrapper().getNetSalary())
			);
		}
		return list;		
	}
	
	private void initializeMonthList() {
		monthResultPanel.clear();
		monthListBox.clear();
		monthListBox.addItem(" ------ ", (String) null);
		monthListBoxInitialized.setValue(false);
	}

	private ScrollPanel createExtraPanel(final IAccountEntryModuleCallback callback) {
		ScrollPanel extraPanel = new ScrollPanel();
		extraPanel.setStyleName(AON.AON_CSS.aonInvoicePanelEast());
		extraPanel.getElement().getStyle().setBackgroundColor(BACKGROUND_COLOR);
		
		final FlowPanel flexContainer = new  FlowPanel();
		flexContainer.setStyleName(AON.AON_CSS.aonFlexContainer());
		extraPanel.add(flexContainer);

		
		FlowPanel labelPanel = new  FlowPanel();
		labelPanel.setStyleName(AON.AON_CSS.aonInvoicePanelInner());
		labelPanel.addStyleName(AON.AON_CSS.aonNowrap());
		InlineLabel label = new InlineLabel(AON.MSG.importSalaryAction());
		label.setStyleName(AON.AON_CSS.aonWidthAll());
		label.addStyleName(AON.AON_CSS.aonMarginAuto());
		label.addStyleName(AON.AON_CSS.aonInvoiceLabel());
		label.addStyleName(AON.AON_CSS.aonTextCenter());
		labelPanel.add(label);
		flexContainer.add(labelPanel);
		
		FlowPanel monthPanel = new  FlowPanel();
		monthPanel.setStyleName(AON.AON_CSS.aonInvoicePanelInner());
		monthPanel.addStyleName(AON.AON_CSS.aonNowrap());
		monthPanel.addStyleName(AON.AON_CSS.aonMarginTop());
		InlineLabel monthLabel = new InlineLabel(AON.MSG.selectAction());
		monthLabel.setStyleName(AON.AON_CSS.aonInnerLabel());
		monthLabel.addStyleName(AON.AON_CSS.aonWidth70());
		monthPanel.add(monthLabel);
		monthResultPanel.clear();

		monthListBox = new ListBox();
		monthListBox.setStyleName(AON.AON_CSS.aonWidth170() );
		monthListBox.addItem(" ------ ", (String) null);
		monthListBox.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				if (!monthListBoxInitialized.getValue()) {
					Integer period = getWrapper().getAccountEntry().getPeriod();
					Date start = new Date();
					Date end = new Date();
					for( AccountPeriod ap : callback.getModule().getConfiguration().getPeriods() ) {
						if (AonNumberUtils.equals(ap.getId(), period)) {
							start = ap.getInitiationDate();
							end = ap.getDeadline();
						}
					}
					monthListBoxInitialized.setValue(true);
					monthListBox.addItem(" --- Cargando --- ", (String) null);
					getFiscalService().getSalaryEntries(AccountEntryModule.getCurrentDomainName()
							,AccountEntryModule.getCurrentDomain() ,start, end
							, new AsyncCallback<LinkedList<SalaryEntry>>() {
								
						@Override
						public void onSuccess(LinkedList<SalaryEntry> result) {
							monthListBox.removeItem(1);
							salaryEntries = result;
							for (SalaryEntry entry : result ) {
								String date = DATE_FORMAT.format(entry.getAccountEntry().getEntryDate()); 
								monthListBox.addItem(
										date + " ("+entry.getSalaryCount()+" n\u00F3minas) " 
										+ (entry.getAccountEntry().getId() == null?"":" [Apunte]")
										,date);
							}
						}
						
						@Override
						public void onFailure(Throwable caught) {
							monthListBox.removeItem(1);
							monthListBoxInitialized.setValue(false);							
						}
					});
					
				}		
			}
		});
		monthListBox.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(final ChangeEvent changeEvent) {
				monthResultPanel.clear();
				int i = monthListBox.getSelectedIndex(); 
				if (i > 0) {
					FlowPanel flowPanel = new FlowPanel();
					flowPanel.setStyleName(AON.AON_CSS.aonWidthAll());
					final SalaryEntry entry = salaryEntries.get(i-1);
					FlowPanel reportPanel = new FlowPanel();
					reportPanel.setStyleName(AON.AON_CSS.aonWidthAll());
					reportPanel.addStyleName(AON.AON_CSS.aonTextCenter());
					reportPanel.addStyleName(AON.AON_CSS.aonPadding());
					reportPanel.addStyleName(AON.AON_CSS.aonMargin());
					reportPanel.addStyleName(AON.AON_CSS.aonBorderTop());
					
					final Button reportButton = new Button(AON.MSG.informationBreakdown());
					reportButton.setStyleName(AON.AON_CSS.aonIconInfo());
					reportButton.addStyleName(AON.AON_CSS.aonIconCommandButton());
					reportButton.addClickHandler(new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {
							getFiscalService().getSalaryFormatted(
								AccountEntryModule.getCurrentDomainName()
								, AccountEntryModule.getCurrentDomain()
								, entry.getAccountEntry().getEntryDate()
								, entry.getAccountEntry().getEntryDate()
								,new AsyncCallback<String>() {

								@Override
								public void onSuccess(String result) {
									callback.getModule().addExtraInfo(result);	
								}

								@Override
								public void onFailure(Throwable caught) {
								}
							});
									
						}
					});
					reportPanel.add(reportButton);
					flowPanel.add( reportPanel );
					
					if ( entry.getAccountEntry().getId() != null ) {
						FlowPanel entryPanel = new FlowPanel();
						entryPanel.setStyleName(AON.AON_CSS.aonWidthAll());
						entryPanel.addStyleName(AON.AON_CSS.aonMargin());
						entryPanel.addStyleName(AON.AON_CSS.aonBorderTop());
						Label label = new Label("Existe un apunte contable de n\u00F3minas en la fecha indicada. Si desea continuar, debe borrarlo.");
						label.setStyleName(AON.AON_CSS.aonBold());
						label.addStyleName(AON.AON_CSS.aonColorRed());
						label.addStyleName(AON.AON_CSS.aonMarginBottom());
						entryPanel.add(label);
						
						FlowPanel buttonsPanel = new FlowPanel();
						buttonsPanel.setStyleName(AON.AON_CSS.aonTextCenter());
						buttonsPanel.addStyleName(AON.AON_CSS.aonPadding());
						
						Button viewButton = new Button(AON.MSG.preview());
						viewButton.setStyleName(AON.AON_CSS.aonIconLoupe());
						viewButton.addStyleName(AON.AON_CSS.aonIconCommandButton());
						viewButton.addStyleName(AON.AON_CSS.aonMarginRight());
						viewButton.addClickHandler(new ClickHandler() {
							@Override
							public void onClick(ClickEvent event) {
								workingLog.addPreview(entry);
							}
						});
						buttonsPanel.add(viewButton);
						
						final Button removeButton = new Button(AON.MSG.deleteAction());
						removeButton.setStyleName(AON.AON_CSS.aonIconDelete());
						removeButton.addStyleName(AON.AON_CSS.aonIconCommandButton());
						removeButton.addClickHandler(new ClickHandler() {
							@Override
							public void onClick(ClickEvent event) {
								removeButton.setEnabled(false);
								ConfirmDialog cd = new ConfirmDialog();
								cd.confirm(AON.MSG.confirmDeleteAction(), new ConfirmDialogCallback() {

									@Override
									public void onCancel() {
										removeButton.setEnabled(true);
									}

									@Override
									public void onAccept() {
										getFiscalService().deleteAccountEntry(
												AccountEntryModule.getCurrentDomainName()
												, AccountEntryModule.getCurrentDomain()
												, entry.getAccountEntry().getId(),
												new AsyncCallback<Void>() {

											@Override
											public void onSuccess(Void result) {
												removeButton.setEnabled(true);
												initializeMonthList();
												onChange(changeEvent);
											}

											@Override
											public void onFailure(Throwable caught) {
												removeButton.setEnabled(true);
											}
										});
									}
								});
							}
						});

						buttonsPanel.add(removeButton);
						entryPanel.add(buttonsPanel);
						flowPanel.add( entryPanel );
					} else {
						preselect(null, entry, new ISelectionCallback() {

							@Override
							public void onSuccess() {
								_paintEntry();
							}

							@Override
							public void onFailure() {
							}
						});
					}
					monthResultPanel.add(flowPanel);
				}
			}
		});

		monthPanel.add(monthListBox);
		flexContainer.add(monthPanel);
		flexContainer.add(monthResultPanel);
		
//		Button importButton = new Button(AON.MSG.importAction());
//		importButton.setStyleName(AON.AON_CSS.aonIconWizard());
//		importButton.addStyleName(AON.AON_CSS.aonIconCommandButton());
//		importButton.addClickHandler(new ClickHandler() {
//			
//			@Override
//			public void onClick(ClickEvent event) {
//				// TODO Auto-generated method stub
//				
//			}
//		});
//		extraContainer.add(importButton);
		return extraPanel;
	}

	@Override
	public int getTabIndex() {
		return concept.getTabIndex();
	}

	@Override
	public void setAccessKey(char key) {
		concept.setAccessKey(key);
	}

	@Override
	public void setTabIndex(int index) {
		concept.setTabIndex(index);
	}
	
	@Override
	public void setFocus(boolean b) {
		concept.setFocus(b);
	}
}
