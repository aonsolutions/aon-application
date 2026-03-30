package com.esferalia.aon.gwt.fiscal.client.accounting.wizard.tedi;

import java.util.Date;
import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonAccountBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog.AonConfirmDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDoubleBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextButton;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryModule.IAccountEntryModuleCallback;
import com.esferalia.aon.gwt.fiscal.client.accounting.ISelectionCallback;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountEntryDetail;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.IAccountEntryWrapper;
import com.esferalia.aon.occam.api.model.SalaryEntry;
import com.esferalia.aon.occam.api.model.type.AccountEntryType;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;


public class SalaryEntryPanel extends WizardContentBase<SalaryEntry> {
	private static final DateTimeFormat DATE_FORMAT = DateTimeFormat.getFormat("dd/MM/yyyy");
	
	static final String BACKGROUND_COLOR = "#EEEEEE";
	
	private FlowPanel centerPanel; 
	private FlexTable flexTable;
	
	private AonTextBox concept;
	private AonDoubleBox  moneySalary;
	private AonAccountBox moneySalaryAccount;
	private AonDoubleBox  inKindSalary;
	private AonAccountBox inKindSalaryAccount;
	private AonDoubleBox  allowances;
	private AonAccountBox allowancesAccount;
	private AonDoubleBox  salaryCompensations;
	private AonAccountBox salaryCompensationsAccount;
	private AonDoubleBox  salaryDedAdvPayment;
	private AonAccountBox salaryDedAdvPaymentAccount;
	private AonDoubleBox  salaryDedSeize;
	private AonAccountBox salaryDedSeizeAccount;
	private AonDoubleBox  salaryDedInKind;
	private AonAccountBox salaryDedInKindAccount;
	private AonDoubleBox  salaryOtherDeductions;
	private AonAccountBox salaryOtherDeductionsAccount;
	private AonDoubleBox  irpf;
	private AonAccountBox irpfAccount;
	private AonDoubleBox  inKindIrpf;
	private AonAccountBox inKindIrpfAccount;
	private AonDoubleBox  employeeSocialInsurance;
	private AonAccountBox employeeSocialInsuranceAccount;
	private AonDoubleBox  companySocialInsurance;
	private AonAccountBox companySocialInsuranceAccount;
	private AonDoubleBox  netSalary;
	private AonAccountBox netSalaryAccount;
	
	private SalaryEntry salaryEntry;
	
	public SalaryEntryPanel(final IAccountEntryModuleCallback callback) {
		setCallback(callback);
		
		SplitLayoutPanel rootPanel = new SplitLayoutPanel(4);
		
		rootPanel.addEast(createExtraPanel(callback), 380);
		
		ScrollPanel scrollPanel = new ScrollPanel();
		scrollPanel.setStyleName(AON.CSS.aonMarginBottom());
		scrollPanel.getElement().getStyle().setProperty("min-height", "200px");
		scrollPanel.getElement().getStyle().setBackgroundColor(SalaryEntryPanel.BACKGROUND_COLOR);
		createFlexTable();
		scrollPanel.setWidget(flexTable);
		rootPanel.add(scrollPanel);

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
		flexTable.setStyleName(AON.CSS.aonMarginTop());
		flexTable.addStyleName(AON.CSS.aonMarginLeft());
		flexTable.addStyleName(AON.CSS.aonWidthAlmostAll());
		
		Label label = new Label(AON.MSG.concept());
		label.setStyleName(AON.CSS.aonInnerLabel());
		flexTable.getCellFormatter().addStyleName(row, 0, AON.CSS.aonNowrap());
		flexTable.setWidget(row, 0, label);
		
		concept = new AonTextBox();
		concept.addValueChangeHandler(event -> {
			getWrapper().setConcept(concept.getValue());
			valueChanged();
		});
		concept.setVisibleLength(25); 
		concept.setMaxLength(64);
		flexTable.setWidget(row, 1, concept);
		flexTable.getFlexCellFormatter().setColSpan(row, 1, 3);
		row++;
		
		
		flexTable.getRowFormatter().setStyleName(row, AON.CSS.aonBorderTop());
		label = new Label(AON.MSG.moneySalary());
		label.setStyleName(AON.CSS.aonInnerLabel());
		flexTable.getCellFormatter().addStyleName(row, 0, AON.CSS.aonNowrap());
		flexTable.setWidget(row, 0, label);
		moneySalary = new AonDoubleBox();
		moneySalary.setValue(0.0);
		moneySalary.addValueChangeHandler( event -> {
			getWrapper().setMoneySalary(event.getValue());
			valueChanged();
		});
		flexTable.setWidget(row, 1, moneySalary);
		label = new Label(AON.MSG.accountAbr());
		label.setStyleName(AON.CSS.aonInnerLabel());
		flexTable.getCellFormatter().addStyleName(row, 2, AON.CSS.aonNowrap());
		flexTable.setWidget(row, 2, label);
		moneySalaryAccount = createAccountBox();
		moneySalaryAccount.addSelectionHandler( event -> {
			getWrapper().setMoneySalaryAccount(event.getSelectedItem());
			valueChanged();
		});
		flexTable.setWidget(row, 3, moneySalaryAccount);
		flexTable.getCellFormatter().setStyleName(row, 3, AON.CSS.aonWidthAuto());
		row++;
		

		label = new Label(AON.MSG.inKindSalary());
		label.setStyleName(AON.CSS.aonInnerLabel());
		flexTable.getCellFormatter().addStyleName(row, 0, AON.CSS.aonNowrap());
		flexTable.setWidget(row, 0, label);
		inKindSalary = new AonDoubleBox();
		inKindSalary.setValue(0.0);
		inKindSalary.addValueChangeHandler( event -> {
			getWrapper().setInKindSalary(event.getValue());
			valueChanged();
		});
		flexTable.setWidget(row, 1, inKindSalary);
		label = new Label(AON.MSG.accountAbr());
		label.setStyleName(AON.CSS.aonInnerLabel());
		flexTable.getCellFormatter().addStyleName(row, 2, AON.CSS.aonNowrap());
		flexTable.setWidget(row, 2, label);
		inKindSalaryAccount = createAccountBox();
		inKindSalaryAccount.addSelectionHandler( event -> {
			getWrapper().setInKindSalaryAccount(event.getSelectedItem());
			valueChanged();
		});
		flexTable.setWidget(row, 3, inKindSalaryAccount);
		flexTable.getCellFormatter().setStyleName(row, 3, AON.CSS.aonWidthAuto());
		row++;
		

		label = new Label(AON.MSG.allowances());
		label.setStyleName(AON.CSS.aonInnerLabel());
		flexTable.getCellFormatter().addStyleName(row, 0, AON.CSS.aonNowrap());
		flexTable.setWidget(row, 0, label);
		allowances = new AonDoubleBox();
		allowances.setValue(0.0);
		allowances.addValueChangeHandler( event -> {
			getWrapper().setAllowance(event.getValue());
			valueChanged();
		});
		flexTable.setWidget(row, 1, allowances);
		label = new Label(AON.MSG.accountAbr());
		label.setStyleName(AON.CSS.aonInnerLabel());
		flexTable.getCellFormatter().addStyleName(row, 2, AON.CSS.aonNowrap());
		flexTable.setWidget(row, 2, label);
		allowancesAccount = createAccountBox();
		allowancesAccount.addSelectionHandler( event -> {
			getWrapper().setAllowanceAccount(event.getSelectedItem());
			valueChanged();
		});
		flexTable.setWidget(row, 3, allowancesAccount);
		flexTable.getCellFormatter().setStyleName(row, 3, AON.CSS.aonWidthAuto());
		row++;

		label = new Label(AON.MSG.salaryCompensations());
		label.setStyleName(AON.CSS.aonInnerLabel());
		flexTable.getCellFormatter().addStyleName(row, 0, AON.CSS.aonNowrap());
		flexTable.setWidget(row, 0, label);
		salaryCompensations = new AonDoubleBox();
		salaryCompensations.setValue(0.0);
		salaryCompensations.addValueChangeHandler( event -> {
			getWrapper().setSalaryCompensation(event.getValue());
			valueChanged();
		});
		flexTable.setWidget(row, 1, salaryCompensations);
		label = new Label(AON.MSG.accountAbr());
		label.setStyleName(AON.CSS.aonInnerLabel());
		flexTable.getCellFormatter().addStyleName(row, 2, AON.CSS.aonNowrap());
		flexTable.setWidget(row, 2, label);
		salaryCompensationsAccount = createAccountBox();
		salaryCompensationsAccount.addSelectionHandler( event -> {
			getWrapper().setSalaryCompensationAccount(event.getSelectedItem());
			valueChanged();
		});
		flexTable.setWidget(row, 3, salaryCompensationsAccount);
		flexTable.getCellFormatter().setStyleName(row, 3, AON.CSS.aonWidthAuto());
		row++;
		
		label = new Label(AON.MSG.advance());
		label.setStyleName(AON.CSS.aonInnerLabel());
		flexTable.getCellFormatter().addStyleName(row, 0, AON.CSS.aonNowrap());
		flexTable.setWidget(row, 0, label);
		salaryDedAdvPayment = new AonDoubleBox();
		salaryDedAdvPayment.setValue(0.0);
		salaryDedAdvPayment.addValueChangeHandler( event -> {
			getWrapper().setSalaryDedAdvPayment(event.getValue());
			valueChanged();
		});
		flexTable.setWidget(row, 1, salaryDedAdvPayment);
		label = new Label(AON.MSG.accountAbr());
		label.setStyleName(AON.CSS.aonInnerLabel());
		flexTable.getCellFormatter().addStyleName(row, 2, AON.CSS.aonNowrap());
		flexTable.setWidget(row, 2, label);
		salaryDedAdvPaymentAccount = createAccountBox();
		salaryDedAdvPaymentAccount.addSelectionHandler( event -> {
			getWrapper().setSalaryDedAdvPaymentAccount(event.getSelectedItem());
			valueChanged();
		});
		flexTable.setWidget(row, 3, salaryDedAdvPaymentAccount);
		flexTable.getCellFormatter().setStyleName(row, 3, AON.CSS.aonWidthAuto());
		row++;
		
		
		label = new Label(AON.MSG.seize());
		label.setStyleName(AON.CSS.aonInnerLabel());
		flexTable.getCellFormatter().addStyleName(row, 0, AON.CSS.aonNowrap());
		flexTable.setWidget(row, 0, label);
		salaryDedSeize = new AonDoubleBox();
		salaryDedSeize.setValue(0.0);
		salaryDedSeize.addValueChangeHandler( event -> {
			getWrapper().setSalaryDedSeize(event.getValue());
			valueChanged();
		});
		flexTable.setWidget(row, 1, salaryDedSeize);
		label = new Label(AON.MSG.accountAbr());
		label.setStyleName(AON.CSS.aonInnerLabel());
		flexTable.getCellFormatter().addStyleName(row, 2, AON.CSS.aonNowrap());
		flexTable.setWidget(row, 2, label);
		salaryDedSeizeAccount = createAccountBox();
		salaryDedSeizeAccount.addSelectionHandler( event -> {
			getWrapper().setSalaryDedSeizeAccount(event.getSelectedItem());
			valueChanged();
		});
		flexTable.setWidget(row, 3, salaryDedSeizeAccount);
		flexTable.getCellFormatter().setStyleName(row, 3, AON.CSS.aonWidthAuto());
		row++;
// ***
		label = new Label(AON.MSG.salaryDeductionsInKind());
		label.setStyleName(AON.CSS.aonInnerLabel());
		flexTable.getCellFormatter().addStyleName(row, 0, AON.CSS.aonNowrap());
		flexTable.setWidget(row, 0, label);
		salaryDedInKind = new AonDoubleBox();
		salaryDedInKind.setValue(0.0);
		salaryDedInKind.addValueChangeHandler( event -> {
			getWrapper().setSalaryDedInKind(event.getValue());
			valueChanged();
		});
		flexTable.setWidget(row, 1, salaryDedInKind);
		label = new Label(AON.MSG.accountAbr());
		label.setStyleName(AON.CSS.aonInnerLabel());
		flexTable.getCellFormatter().addStyleName(row, 2, AON.CSS.aonNowrap());
		flexTable.setWidget(row, 2, label);
		salaryDedInKindAccount = createAccountBox();
		salaryDedInKindAccount.addSelectionHandler( event -> {
			getWrapper().setSalaryDedInKindAccount(event.getSelectedItem());
			valueChanged();
		});
		flexTable.setWidget(row, 3, salaryDedInKindAccount);
		flexTable.getCellFormatter().setStyleName(row, 3, AON.CSS.aonWidthAuto());
		row++;
// ***
		label = new Label(AON.MSG.salaryOtherDeductions());
		label.setStyleName(AON.CSS.aonInnerLabel());
		flexTable.getCellFormatter().addStyleName(row, 0, AON.CSS.aonNowrap());
		flexTable.setWidget(row, 0, label);
		salaryOtherDeductions = new AonDoubleBox();
		salaryOtherDeductions.setValue(0.0);
		salaryOtherDeductions.addValueChangeHandler( event -> {
			getWrapper().setSalaryOtherDeductions(event.getValue());
			valueChanged();
		});
		flexTable.setWidget(row, 1, salaryOtherDeductions);
		label = new Label(AON.MSG.accountAbr());
		label.setStyleName(AON.CSS.aonInnerLabel());
		flexTable.getCellFormatter().addStyleName(row, 2, AON.CSS.aonNowrap());
		flexTable.setWidget(row, 2, label);
		salaryOtherDeductionsAccount = createAccountBox();
		salaryOtherDeductionsAccount.addSelectionHandler( event -> {
			getWrapper().setSalaryOtherDeductionsAccount(event.getSelectedItem());
			valueChanged();
		});
		flexTable.setWidget(row, 3, salaryOtherDeductionsAccount);
		flexTable.getCellFormatter().setStyleName(row, 3, AON.CSS.aonWidthAuto());
		row++;

		label = new Label(AON.MSG.irpf());
		label.setStyleName(AON.CSS.aonInnerLabel());
		flexTable.getCellFormatter().addStyleName(row, 0, AON.CSS.aonNowrap());
		flexTable.setWidget(row, 0, label);
		irpf = new AonDoubleBox();
		irpf.setValue(0.0);
		irpf.addValueChangeHandler( event -> {
			getWrapper().setIrpf(event.getValue());
			valueChanged();
		});
		flexTable.setWidget(row, 1, irpf);
		label = new Label(AON.MSG.accountAbr());
		label.setStyleName(AON.CSS.aonInnerLabel());
		flexTable.getCellFormatter().addStyleName(row, 2, AON.CSS.aonNowrap());
		flexTable.setWidget(row, 2, label);
		irpfAccount = createAccountBox();
		irpfAccount.addSelectionHandler( event -> {
			getWrapper().setIrpfAccount(event.getSelectedItem());
			valueChanged();
		});
		flexTable.setWidget(row, 3, irpfAccount);
		flexTable.getCellFormatter().setStyleName(row, 3, AON.CSS.aonWidthAuto());
		row++;

		label = new Label(AON.MSG.inKindIrpf());
		label.setStyleName(AON.CSS.aonInnerLabel());
		flexTable.getCellFormatter().addStyleName(row, 0, AON.CSS.aonNowrap());
		flexTable.setWidget(row, 0, label);
		inKindIrpf = new AonDoubleBox();
		inKindIrpf.setValue(0.0);
		inKindIrpf.addValueChangeHandler( event -> {
			getWrapper().setInKindIrpf(event.getValue());
			valueChanged();
		});
		flexTable.setWidget(row, 1, inKindIrpf);
		label = new Label(AON.MSG.accountAbr());
		label.setStyleName(AON.CSS.aonInnerLabel());
		flexTable.getCellFormatter().addStyleName(row, 2, AON.CSS.aonNowrap());
		flexTable.setWidget(row, 2, label);
		inKindIrpfAccount = createAccountBox();
		inKindIrpfAccount.addSelectionHandler( event -> {
			getWrapper().setInKindIrpfAccount(event.getSelectedItem());
			valueChanged();
		});
		flexTable.setWidget(row, 3, inKindIrpfAccount);
		flexTable.getCellFormatter().setStyleName(row, 3, AON.CSS.aonWidthAuto());
		row++;

		label = new Label(AON.MSG.employeeSocialInsurance());
		label.setStyleName(AON.CSS.aonInnerLabel());
		flexTable.getCellFormatter().addStyleName(row, 0, AON.CSS.aonNowrap());
		flexTable.setWidget(row, 0, label);
		employeeSocialInsurance = new AonDoubleBox();
		employeeSocialInsurance.setValue(0.0);
		employeeSocialInsurance.addValueChangeHandler( event -> {
			getWrapper().setEmployeeSocialInsurance(event.getValue());
			valueChanged();
		});
		flexTable.setWidget(row, 1, employeeSocialInsurance);
		label = new Label(AON.MSG.accountAbr());
		label.setStyleName(AON.CSS.aonInnerLabel());
		flexTable.getCellFormatter().addStyleName(row, 2, AON.CSS.aonNowrap());
		flexTable.setWidget(row, 2, label);
		employeeSocialInsuranceAccount = createAccountBox();
		employeeSocialInsuranceAccount.addSelectionHandler( event -> {
			getWrapper().setEmployeeSocialInsuranceAccount(event.getSelectedItem());
			valueChanged();
		});
		flexTable.setWidget(row, 3, employeeSocialInsuranceAccount);
		flexTable.getCellFormatter().setStyleName(row, 3, AON.CSS.aonWidthAuto());
		row++;

		label = new Label(AON.MSG.companySocialInsurance());
		label.setStyleName(AON.CSS.aonInnerLabel());
		flexTable.getCellFormatter().addStyleName(row, 0, AON.CSS.aonNowrap());
		flexTable.setWidget(row, 0, label);
		companySocialInsurance = new AonDoubleBox();
		companySocialInsurance.setValue(0.0);
		companySocialInsurance.addValueChangeHandler( event -> {
			getWrapper().setCompanySocialInsurance(event.getValue());
			valueChanged();
		});
		flexTable.setWidget(row, 1, companySocialInsurance);
		label = new Label(AON.MSG.accountAbr());
		label.setStyleName(AON.CSS.aonInnerLabel());
		flexTable.getCellFormatter().addStyleName(row, 2, AON.CSS.aonNowrap());
		flexTable.setWidget(row, 2, label);
		companySocialInsuranceAccount = createAccountBox();
		companySocialInsuranceAccount.addSelectionHandler( event -> {
			getWrapper().setCompanySocialInsuranceAccount(event.getSelectedItem());
			valueChanged();
		});
		flexTable.setWidget(row, 3, companySocialInsuranceAccount);
		flexTable.getCellFormatter().setStyleName(row, 3, AON.CSS.aonWidthAuto());
		row++;

		label = new Label(AON.MSG.netSalary());
		label.setStyleName(AON.CSS.aonInnerLabel());
		flexTable.getCellFormatter().addStyleName(row, 0, AON.CSS.aonNowrap());
		flexTable.setWidget(row, 0, label);
		netSalary = new AonDoubleBox();
		netSalary.setEnabled(false);
		
		flexTable.setWidget(row, 1, netSalary);
		label = new Label(AON.MSG.accountAbr());
		label.setStyleName(AON.CSS.aonInnerLabel());
		flexTable.getCellFormatter().addStyleName(row, 2, AON.CSS.aonNowrap());
		flexTable.setWidget(row, 2, label);
		netSalaryAccount = createAccountBox();
		netSalaryAccount.addSelectionHandler( event -> {
			getWrapper().setNetSalaryAccount(event.getSelectedItem());
			valueChanged();
		});
		flexTable.setWidget(row, 3, netSalaryAccount);
		flexTable.getCellFormatter().setStyleName(row, 3, AON.CSS.aonWidthAuto());
	}

	protected void valueChanged() {
		netSalary.setValue( getWrapper().getNetSalary(), false, true );
		getWrapper().getAccountEntry().setDetails( getEntryDetails() );
		innerPaintEntry();
	}

	private AonAccountBox createAccountBox() {
		 return new AonAccountBox(getCallback().getOccam());
	}

	@Override
	public void reset(final AccountEntry base,final ISelectionCallback cbk) {
		if (base == null) {
			getCallback().getModule().onError("[ERROR INTERNO] No hay un apunte base del que crear la factura");
		}
		SalaryEntry ai = new SalaryEntry()
			.setConcept("N\u00F3minas")
			.setMoneySalaryAccount( getCallback().getConfiguration().accounting().getDefaultSalary() )
			.setInKindSalaryAccount(getCallback().getConfiguration().accounting().getDefaultSalaryInKind())
			.setAllowanceAccount(getCallback().getConfiguration().accounting().getDefaultAllowance())
			.setSalaryCompensationAccount(getCallback().getConfiguration().accounting().getDefaultCompensation())
			.setSalaryDedAdvPaymentAccount(getCallback().getConfiguration().accounting().getSalaryDedAdvPayment())
			.setSalaryDedSeizeAccount(getCallback().getConfiguration().accounting().getSalaryDedSeize())
			.setSalaryDedInKindAccount(getCallback().getConfiguration().accounting().getSalaryDedInKind())
			.setSalaryOtherDeductionsAccount(getCallback().getConfiguration().accounting().getSalaryOtherDeductions())
			.setIrpfAccount(getCallback().getConfiguration().accounting().getSalaryChargedRet())
			.setInKindIrpfAccount(getCallback().getConfiguration().accounting().getSalaryChargedRetInKind())
			.setEmployeeSocialInsuranceAccount(	getCallback().getConfiguration().accounting().getDefaultSocialInsurance())
			.setCompanySocialInsuranceAccount(getCallback().getConfiguration().accounting().getDefaultCompanySocIns())
			.setNetSalaryAccount(getCallback().getConfiguration().accounting().getDefaultPendingSalary())
		;
		ai.setAccountEntry(new AccountEntry()
			.setEntryType(AccountEntryType.SALARY)
			.setPeriod(base.getPeriod())
			.setDomain(getCallback().getOccam().getDomain())
			.setConfidential(base.isConfidential())
			.setEntryDate(base.getEntryDate())
			.setActivity(base.getActivity())
			.setJournal(null));
		centerPanel.clear();
		select(null, ai, cbk);
	}
	public void preselect(final Integer id,final SalaryEntry wrp,final ISelectionCallback cbk) {
		AccountEntry existing = getWrapper().getAccountEntry();
		wrp.getAccountEntry().setSecurityLevel(existing.getSecurityLevel());
		wrp.getAccountEntry().setPeriod(existing.getPeriod());
		wrp.getAccountEntry().setDomain(existing.getDomain());
		wrp.getAccountEntry().setEntryType(existing.getEntryType());
		wrp.getAccountEntry().setActivity(existing.getActivity());
		getCallback().getModule().changeEntryDate(wrp.getAccountEntry().getEntryDate());
		wrp.setConcept("N\u00F3minas " + DATE_FORMAT.format(wrp.getAccountEntry().getEntryDate()));
		wrp.setMoneySalaryAccount(getWrapper().getMoneySalaryAccount());
		wrp.setInKindSalaryAccount(getWrapper().getInKindSalaryAccount());
		wrp.setAllowanceAccount(getWrapper().getAllowanceAccount());
		wrp.setSalaryCompensationAccount(getWrapper().getSalaryCompensationAccount());
		wrp.setSalaryDedAdvPaymentAccount(getWrapper().getSalaryDedAdvPaymentAccount());
		wrp.setSalaryDedSeizeAccount(getWrapper().getSalaryDedSeizeAccount());
		wrp.setSalaryOtherDeductionsAccount(getWrapper().getSalaryOtherDeductionsAccount());
		wrp.setSalaryDedInKindAccount(getWrapper().getSalaryDedInKindAccount());
		wrp.setIrpfAccount(getWrapper().getIrpfAccount());
		wrp.setInKindIrpfAccount(getWrapper().getInKindIrpfAccount());
		wrp.setEmployeeSocialInsuranceAccount(getWrapper().getEmployeeSocialInsuranceAccount());
		wrp.setCompanySocialInsuranceAccount(getWrapper().getCompanySocialInsuranceAccount());
		wrp.setNetSalaryAccount(getWrapper().getNetSalaryAccount());
		setWrapper( wrp );
		populate();
		valueChanged();
	}
	
	@Override
	public void select(final Integer id,final IAccountEntryWrapper wrp,final ISelectionCallback cbk) {
		getCallback().getModule().onClearSessionLog();
		if (id != null) {
			Window.alert("No se puede modificar");
		} else {
			if (wrp != null) {
				setWrapper( (SalaryEntry) wrp);
				getCallback().getModule().onPreview(getWrapper());
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
		salaryDedInKind.setValue(getWrapper().getSalaryDedInKind());
		setAccount(salaryDedInKindAccount,getWrapper().getSalaryDedInKindAccount());
		salaryOtherDeductions.setValue(getWrapper().getSalaryOtherDeductions());
		setAccount(salaryOtherDeductionsAccount,getWrapper().getSalaryOtherDeductionsAccount());		
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
		getCallback().getModule().refreshIdLabel();
	}

	private void setAccount(AonAccountBox accountBox, Account account) {
		if (account != null) {
			accountBox.setValue(account.getId(),account.getCode(),account.getDescription(),false);
		} else {
			accountBox.setValue(null,null,null,false);
		}
	}

	private void innerPaintEntry() {
		getCallback().getModule().onPreview(getWrapper() );
	}
	
	@Override
	public boolean isUpdatable() {
		return (true);
	}
	
	@Override
	public String getNoUpdatableCause() {
		return null;
	}
	
	@Override
	public boolean isAttachmentManagementEnabled() {
		return false;
	}
	@Override
	public boolean hasAttachment() {
		return false;
	}
	@Override
	public void removeAttach(final AsyncCallback<IAccountEntryWrapper> cbk) {
		// nothing
	}
	@Override
	public void addAttach(final AsyncCallback<IAccountEntryWrapper> cbk) {
		// nothing
	}
	
	@Override
	public void manageWidgets(boolean canRemove, boolean canEdit) {
		// nothing
	}

	private boolean check(double d, Account account) {
		return (AonMathUtils.isNotZero( d )  
			&& (account == null || account.getId() == null));
	}
	private LinkedList<AccountEntryDetail> getEntryDetails() {
		if (check(getWrapper().getMoneySalary(),getWrapper().getMoneySalaryAccount())) {
			String msg = "Debe indicar una cuenta contable para el valor 'Remuneraciones monetarias'";
			getCallback().getModule().onError(msg);
		} else if (check(getWrapper().getInKindSalary(),getWrapper().getInKindSalaryAccount())) {
			String msg = "Debe indicar una cuenta contable para el valor 'Remuneraciones en especie'";		
			getCallback().getModule().onError(msg);
		} else if (check(getWrapper().getAllowance(),getWrapper().getAllowanceAccount())) {
			String msg = "Debe indicar una cuenta contable para el valor 'Dietas'";
			getCallback().getModule().onError(msg);
		} else if (check(getWrapper().getSalaryCompensation(),getWrapper().getSalaryCompensationAccount())) {
			String msg = "Debe indicar una cuenta contable para el valor 'Indemnizaciones'";		
			getCallback().getModule().onError(msg);
		} else if (check(getWrapper().getSalaryDedAdvPayment(),getWrapper().getSalaryDedAdvPaymentAccount())) {
			String msg = "Debe indicar una cuenta contable para el valor 'Anticipo'";		
			getCallback().getModule().onError(msg);
		} else if (check(getWrapper().getSalaryDedSeize(),getWrapper().getSalaryDedSeizeAccount())) {
			String msg = "Debe indicar una cuenta contable para el valor 'Embargo'";		
			getCallback().getModule().onError(msg);
		} else if (check(getWrapper().getSalaryOtherDeductions(),getWrapper().getSalaryOtherDeductionsAccount())) {
			String msg = "Debe indicar una cuenta contable para el valor 'Otras deducciones'";		
			getCallback().getModule().onError(msg);
		} else if (check(getWrapper().getSalaryOtherDeductions(),getWrapper().getSalaryDedInKindAccount())) {
			String msg = "Debe indicar una cuenta contable para el valor 'Deducciones en especie'";		
			getCallback().getModule().onError(msg);
		} else if (check(getWrapper().getCompanySocialInsurance(),getWrapper().getCompanySocialInsuranceAccount())) {
			String msg = "Debe indicar una cuenta contable para el valor 'Seg.Social Empresa'";			
			getCallback().getModule().onError(msg);
		} else if (check(getWrapper().getIrpf(),getWrapper().getIrpfAccount())) {	
			String msg = "Debe indicar una cuenta contable para el valor 'I.R.P.F.'";
			getCallback().getModule().onError(msg);
		} else if (check(getWrapper().getInKindIrpf(),getWrapper().getInKindIrpfAccount())) {
			String msg = "Debe indicar una cuenta contable para el valor 'I.R.P.F. en especie'";		
			getCallback().getModule().onError(msg);
		} else if (check(getWrapper().getTotalSocialInsurance(),getWrapper().getEmployeeSocialInsuranceAccount())) {	
			String msg = "Debe indicar una cuenta contable para el valor 'Seg.Social Empleado'";
			getCallback().getModule().onError(msg);
		}
		
		LinkedList<AccountEntryDetail> list = new LinkedList<>();
		if (getWrapper().getMoneySalary() != 0 
			&& getWrapper().getMoneySalaryAccount() != null 
			&& getWrapper().getMoneySalaryAccount().getId() != null) {
			list.add( new AccountEntryDetail()
				.setAccountId(getWrapper().getMoneySalaryAccount().getId())
				.setAccountCode(getWrapper().getMoneySalaryAccount().getCode())
				.setAccountDescription(getWrapper().getMoneySalaryAccount().getDescription())
				.setBalancingAccountId(null)
				.setConcept(getWrapper().getConcept())
				.setDebit(getWrapper().getMoneySalary())
			);
		}

		if (getWrapper().getInKindSalary() != 0 
			&& getWrapper().getInKindSalaryAccount() != null 
			&& getWrapper().getInKindSalaryAccount().getId() != null) {
			list.add(new AccountEntryDetail()
				.setAccountId(getWrapper().getInKindSalaryAccount().getId())
				.setAccountCode(getWrapper().getInKindSalaryAccount().getCode())
				.setAccountDescription(getWrapper().getInKindSalaryAccount().getDescription())
				.setBalancingAccountId(null)
				.setConcept(getWrapper().getConcept())
				.setDebit(getWrapper().getInKindSalary())
			);
		}

		if (getWrapper().getAllowance() != 0 
			&& getWrapper().getAllowanceAccount() != null 
			&& getWrapper().getAllowanceAccount().getId() != null) {
			list.add(new AccountEntryDetail()
				.setAccountId(getWrapper().getAllowanceAccount().getId())
				.setAccountCode(getWrapper().getAllowanceAccount().getCode())
				.setAccountDescription(getWrapper().getAllowanceAccount().getDescription())
				.setBalancingAccountId(null)
				.setConcept(getWrapper().getConcept())
				.setDebit(getWrapper().getAllowance())
			);
		}

		if (getWrapper().getSalaryDedAdvPayment() != 0 
			&& getWrapper().getSalaryDedAdvPaymentAccount() != null 
			&& getWrapper().getSalaryDedAdvPaymentAccount().getId() != null) {
			list.add(new AccountEntryDetail()
				.setAccountId(getWrapper().getSalaryDedAdvPaymentAccount().getId())
				.setAccountCode(getWrapper().getSalaryDedAdvPaymentAccount().getCode())
				.setAccountDescription(getWrapper().getSalaryDedAdvPaymentAccount().getDescription())
				.setBalancingAccountId(null)
				.setConcept(getWrapper().getConcept())
				.setCredit(getWrapper().getSalaryDedAdvPayment())
			);
		}

		if (getWrapper().getSalaryDedSeize() != 0 
			&& getWrapper().getSalaryDedSeizeAccount() != null 
			&& getWrapper().getSalaryDedSeizeAccount().getId() != null) {
			list.add(new AccountEntryDetail()
				.setAccountId(getWrapper().getSalaryDedSeizeAccount().getId())
				.setAccountCode(getWrapper().getSalaryDedSeizeAccount().getCode())
				.setAccountDescription(getWrapper().getSalaryDedSeizeAccount().getDescription())
				.setBalancingAccountId(null)
				.setConcept(getWrapper().getConcept())
				.setCredit(getWrapper().getSalaryDedSeize())
			);
		}

		if (getWrapper().getSalaryDedInKind() != 0 
			&& getWrapper().getSalaryDedInKindAccount() != null 
			&& getWrapper().getSalaryDedInKindAccount().getId() != null) {
			list.add(new AccountEntryDetail()
				.setAccountId(getWrapper().getSalaryDedInKindAccount().getId())
				.setAccountCode(getWrapper().getSalaryDedInKindAccount().getCode())
				.setAccountDescription(getWrapper().getSalaryDedInKindAccount().getDescription())
				.setBalancingAccountId(null)
				.setConcept(getWrapper().getConcept())
				.setCredit(getWrapper().getSalaryDedInKind())
			);
		}
		
		if (getWrapper().getSalaryOtherDeductions() != 0 
			&& getWrapper().getSalaryOtherDeductionsAccount() != null 
			&& getWrapper().getSalaryOtherDeductionsAccount().getId() != null) {
			list.add(new AccountEntryDetail()
				.setAccountId(getWrapper().getSalaryOtherDeductionsAccount().getId())
				.setAccountCode(getWrapper().getSalaryOtherDeductionsAccount().getCode())
				.setAccountDescription(getWrapper().getSalaryOtherDeductionsAccount().getDescription())
				.setBalancingAccountId(null)
				.setConcept(getWrapper().getConcept())
				.setCredit(getWrapper().getSalaryOtherDeductions())
			);
		}
		
		if (getWrapper().getSalaryCompensation() != 0 
				&& getWrapper().getSalaryCompensationAccount() != null 
				&& getWrapper().getSalaryCompensationAccount().getId() != null) {
				list.add(new AccountEntryDetail()
					.setAccountId(getWrapper().getSalaryCompensationAccount().getId())
					.setAccountCode(getWrapper().getSalaryCompensationAccount().getCode())
					.setAccountDescription(getWrapper().getSalaryCompensationAccount().getDescription())
					.setBalancingAccountId(null)
					.setConcept(getWrapper().getConcept())
					.setDebit(getWrapper().getSalaryCompensation())
				);
			}

		
		
		if (getWrapper().getCompanySocialInsurance() != 0 
			&& getWrapper().getCompanySocialInsuranceAccount() != null 
			&& getWrapper().getCompanySocialInsuranceAccount().getId() != null) {
			list.add(new AccountEntryDetail()
				.setAccountId(getWrapper().getCompanySocialInsuranceAccount().getId())
				.setAccountCode(getWrapper().getCompanySocialInsuranceAccount().getCode())
				.setAccountDescription(getWrapper().getCompanySocialInsuranceAccount().getDescription())
				.setBalancingAccountId(null)
				.setConcept(getWrapper().getConcept())
				.setDebit(getWrapper().getCompanySocialInsurance())
			);
		}

		if (getWrapper().getIrpf() != 0 
			&& getWrapper().getIrpfAccount() != null 
			&& getWrapper().getIrpfAccount().getId() != null) {
			list.add(new AccountEntryDetail()
				.setAccountId(getWrapper().getIrpfAccount().getId())
				.setAccountCode(getWrapper().getIrpfAccount().getCode())
				.setAccountDescription(getWrapper().getIrpfAccount().getDescription())
				.setBalancingAccountId(null)
				.setConcept(getWrapper().getConcept())
				.setCredit(getWrapper().getIrpf())
			);
		}

		if (getWrapper().getInKindIrpf() != 0 
			&& getWrapper().getInKindIrpfAccount() != null 
			&& getWrapper().getInKindIrpfAccount().getId() != null) {
			list.add(new AccountEntryDetail()
				.setAccountId(getWrapper().getInKindIrpfAccount().getId())
				.setAccountCode(getWrapper().getInKindIrpfAccount().getCode())
				.setAccountDescription(getWrapper().getInKindIrpfAccount().getDescription())
				.setBalancingAccountId(null)
				.setConcept(getWrapper().getConcept())
				.setCredit(getWrapper().getInKindIrpf())
			);
		}

		if (getWrapper().getTotalSocialInsurance() != 0 
			&& getWrapper().getEmployeeSocialInsuranceAccount() != null 
			&& getWrapper().getEmployeeSocialInsuranceAccount().getId() != null) {
			list.add(new AccountEntryDetail()
				.setAccountId(getWrapper().getEmployeeSocialInsuranceAccount().getId())
				.setAccountCode(getWrapper().getEmployeeSocialInsuranceAccount().getCode())
				.setAccountDescription(getWrapper().getEmployeeSocialInsuranceAccount().getDescription())
				.setBalancingAccountId(null)
				.setConcept(getWrapper().getConcept())
				.setCredit(getWrapper().getTotalSocialInsurance())
			);
		}

		if (getWrapper().getNetSalary() != 0
			&& getWrapper().getNetSalaryAccount() != null 
			&& getWrapper().getNetSalaryAccount().getId() != null) {
			list.add(new AccountEntryDetail()
				.setAccountId(getWrapper().getNetSalaryAccount().getId())
				.setAccountCode(getWrapper().getNetSalaryAccount().getCode())
				.setAccountDescription(getWrapper().getNetSalaryAccount().getDescription())
				.setBalancingAccountId(null)
				.setConcept(getWrapper().getConcept())
				.setCredit(getWrapper().getNetSalary())
			);
		}
		return list;		
	}
	
	private ScrollPanel createExtraPanel(final IAccountEntryModuleCallback callback) {
		ScrollPanel extraPanel = new ScrollPanel();
		extraPanel.setStyleName(AON.CSS.aonBorderLeft());
		extraPanel.addStyleName(AON.CSS.aonMarginBottom());
		extraPanel.getElement().getStyle().setProperty("min-height", "200px");
		extraPanel.getElement().getStyle().setBackgroundColor(BACKGROUND_COLOR);
		final FlowPanel flexContainer = new  FlowPanel();
		flexContainer.addStyleName(AON.CSS.aonWidthAlmostAll());
		flexContainer.addStyleName(AON.CSS.aonBlockCenter());
		extraPanel.add(flexContainer);
		
		Label label = new Label(AON.MSG.importSalaryAction());
		label.setStyleName(AON.CSS.aonFontLarger());
		label.addStyleName(AON.CSS.aonBold());
		label.addStyleName(AON.CSS.aonNowrap());
		label.addStyleName(AON.CSS.aonTextCenter());
		flexContainer.add(label);

		final FlowPanel filterPanel = new  FlowPanel();
		filterPanel.setStyleName(AON.CSS.aonSearchPanel());
		filterPanel.addStyleName(AON.CSS.aonTextCenter());
		
		AonTextButton filter = new AonTextButton(AON.MSG.searchAction(),AON.CSS.aonIconSearch());
		filterPanel.add(filter);
		filter.addStyleName(AON.CSS.aonMarginTop());
		filter.addClickHandler(event -> fillCenterPanel(centerPanel));
		flexContainer.add(filterPanel);

		centerPanel = new  FlowPanel();
		centerPanel.setStyleName(AON.CSS.aonWidthAll());
		centerPanel.addStyleName(AON.CSS.aonFlexBlock());
		flexContainer.add(centerPanel);
		
		
		return extraPanel;
	}

	protected void fillCenterPanel(final FlowPanel centerPanel) {
		centerPanel.clear();
		Integer period = getWrapper().getAccountEntry().getPeriod();
		Date start = new Date();
		Date end = new Date();
		for( AccountPeriod ap : getCallback().getConfiguration().accounting().getPeriods() ) {
			if (AonNumberUtils.equals(ap.getId(), period)) {
				start = ap.getInitiationDate();
				end = ap.getDeadline();
			}
		}
		final AonDisplayGrid tab = new AonDisplayGrid();
		tab.addStyleName(AON.CSS.aonMarginTop());
		
		centerPanel.add(tab);
		getAccountEntryService().getSalaryEntries(
			getCallback().getOccam().getDomainName()
			,getCallback().getOccam().getDomain()
			,getCallback().getOccam().getUser() 
			,start
			, end
			, new AsyncCallback<LinkedList<SalaryEntry>>() {
					
			@Override
			public void onSuccess(LinkedList<SalaryEntry> result) {
				if (result == null || result.isEmpty()) {
					centerPanel.add(new Label(AON.MSG.noData()));
				} else {
					for (final SalaryEntry entry : result ) {
						
						boolean hasEntry = entry.getAccountEntry().getId() != null;
												
						
						String date = DATE_FORMAT.format(entry.getAccountEntry().getEntryDate());
						InlineLabel dateLabel = new InlineLabel(date);
						dateLabel.setStyleName(AON.CSS.aonLabelWithIcon());
						dateLabel.addStyleName(hasEntry?AON.CSS.aonIconChecked():AON.CSS.aonIconCheck());
						dateLabel.addStyleName(AON.CSS.aonMarginLeft());
						dateLabel.addStyleName(AON.CSS.aonBold());
						
						final AonTextButton reportButton = new AonTextButton(entry.getSalaryCount()+" n\u00F3minas");
						reportButton.setTitle(AON.MSG.informationBreakdown());
						reportButton.addClickHandler(event -> 
							getAccountEntryService().getSalaryFormatted(
								getCallback().getOccam().getDomainName()
								,getCallback().getOccam().getDomain()
								,getCallback().getOccam().getUser()
								,entry.getAccountEntry().getEntryDate()
								,entry.getAccountEntry().getEntryDate()
								,new AsyncCallback<String>() {

									@Override
									public void onSuccess(String result) {
										getCallback().getModule().addExtraInfo(result);	
									}
		
									@Override
									public void onFailure(Throwable caught) {
										// Nothing
									}
								}
							)
						);

						AonTextButton viewButton = new AonTextButton("Existe Apunte",AON.CSS.aonIconSearch());
						final AonTableButton removeButton = new AonTableButton(AON.MSG.deleteAction() + " apunte",AON.CSS.aonIconDelete());
						final AonTextButton importButton = new AonTextButton(AON.MSG.importAction(), AON.CSS.aonIconImport());
						
						if (hasEntry) {
							viewButton.setTitle( "Apunte: " + AON.MSG.preview());
							viewButton.addStyleName(AON.CSS.aonMarginLeft());
							viewButton.addStyleName(AON.CSS.aonColorRed());
							viewButton.addClickHandler(event -> getCallback().getModule().onPreview( entry ));

							removeButton.addClickHandler(event -> {
								removeButton.setEnabled(false);
								AonConfirmDialog cd = new AonConfirmDialog();
								cd.confirm(AON.MSG.confirmDeleteAction(), new AonConfirmDialogCallback() {

									@Override
									public void onCancel() {
										removeButton.setEnabled(true);
									}

									@Override
									public void onAccept() {
										getAccountEntryService().deleteAccountEntry(
											getCallback().getOccam().getDomainName()
											, getCallback().getOccam().getDomain()
											, getCallback().getOccam().getUser()
											, entry.getAccountEntry().getId(),
											new AsyncCallback<Void>() {

												@Override
												public void onSuccess(Void result) {
													removeButton.setEnabled(true);
													fillCenterPanel(centerPanel);
												}
	
												@Override
												public void onFailure(Throwable caught) {
													removeButton.setEnabled(true);
												}
											}
										);
									}
								});
							});
						} else {
							importButton.setTitle(AON.MSG.importAction());
							importButton.addStyleName(AON.CSS.aonMarginLeft());
							importButton.addClickHandler(event -> preselect(null, entry, new ISelectionCallback() {
								
								@Override
								public void onSuccess() {
									innerPaintEntry();
								}

								@Override
								public void onFailure() {
									// Nothing
								}
							}));
						}
						tab.addRow()
							.addCell(dateLabel)
							.addCell(reportButton)
							.addCell((hasEntry)?viewButton:importButton)
							.addCell((hasEntry)?removeButton:new Label());
					}
				}
			}
			
			@Override
			public void onFailure(Throwable caught) {
				getCallback().getModule().onError(caught.getMessage());							
			}
		});
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

	public void entryDateChanged(Date entryDate) {
		getWrapper().getAccountEntry().setEntryDate(entryDate);
	}
	public void activityChanged(Integer activty) {
		getWrapper().getAccountEntry().setActivity(activty);
	}
	public void confidentialChanged(boolean confidential) {
		getWrapper().getAccountEntry().setConfidential(confidential);
	}
}
