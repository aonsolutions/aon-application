package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomSuggestBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomTextArea;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.payroll.client.AgreementDraft.TypeListBox;
import com.esferalia.aon.gwt.payroll.shared.Bonus;
import com.esferalia.aon.gwt.payroll.shared.ContractConcept;
import com.esferalia.aon.gwt.payroll.shared.ContractConceptCalc;
import com.esferalia.aon.gwt.payroll.shared.ContractConceptCalc.ContractConceptCalcType;
import com.esferalia.aon.gwt.payroll.shared.ContractConcepts;
import com.esferalia.aon.gwt.payroll.shared.Deduction;
import com.esferalia.aon.gwt.payroll.shared.Payment;
import com.esferalia.aon.gwt.payroll.shared.Salary;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.Widget;

public abstract class EmployeeContractPaymentEditor extends AonCustomDialog {

	// ----------------------------------------- UiFields

	HTMLPanel messagePanel;
	
	DeckPanel deckPanel;

	// PAYMENT

	HTMLPanel paymentTable;

	HTMLPanel paymentTypePanel;

	AonCustomSuggestBox paymentConceptSB;

	AonCustomTextBox paymentDescriptionTB;

	AonCustomTextArea paymentExpressionTB;
	
	AonCustomListBox paymentSalaryTypeLB;

	AonCustomListBox paymentTaxedTypeLB;

	AonCustomTextBox paymentTaxedExpression;

	AonCustomListBox paymentQuoteTypeLB;

	AonCustomTextBox paymentQuoteExpression;
	
	HTMLPanel paymentMonthPanel;

	AonCustomListBox paymentMonthLB;

	AonCustomDateBox paymentStartDateBx;

	AonCustomDateBox paymentEndDateBx;

	// DEDUCTION

	HTMLPanel deductionTable;

	AonCustomListBox deductionTypeLB;

	AonCustomSuggestBox deductionConceptSB;

	AonCustomTextBox deductionDescriptionTB;

	AonCustomTextArea deductionExpressionTB;

	AonCustomDateBox deductionStartDateBx;

	AonCustomDateBox deductionEndDateBx;
	
	// COST

	HTMLPanel costTable;

	AonCustomListBox costTypeLB;

	AonCustomSuggestBox costCodeSB;

	AonCustomTextBox costDescriptionTB;

	AonCustomTextArea costExpressionTB;

	AonCustomDateBox costStartDateBx;

	AonCustomDateBox costEndDateBx;

	// BONUS

	HTMLPanel bonusTable;

	HTMLPanel bonusTypePanel;

	AonCustomSuggestBox bonusDescriptionSB;

	AonCustomTextArea bonusExpressionTB;

	AonCustomDateBox bonusStartDateBx;

	AonCustomDateBox bonusEndDateBx;
	
	// EMBARGO

	HTMLPanel embargoTable;

	AonCustomTextBox embargoDescriptionTB;

	AonCustomTextArea embargoExpressionTB;
	
	DoubleBox embargoAmountDB;

	AonCustomDateBox embargoStartDateBx;

	AonCustomDateBox embargoEndDateBx;

	HTMLPanel buttonsPanel;

	// ----------------------------------------- Variables

	private DomainEnterprisesServiceAsync enterpriseService = DomainEnterprisesServiceAsync.newInstance();
	private ContractConcepts contractConcepts;
	private ContractConcept selectedConcept;
	private ContractConceptCalcType paymentType;
	private ContractConceptCalc payment;

	private TypeListBox<Payment.Type> paymentTypeLB;
	private TypeListBox<Bonus.Type> bonusTypeLB;
	
	private Date contractStartDate;
	private Date contractEndDate;
	
	private Button acceptDialog;

	// ----------------------------------------- Constructor

	protected EmployeeContractPaymentEditor(ContractConceptCalcType paymentType, Date contractStartDate, Date contractEndDate) {
		setCaption(getCaption(paymentType));
		setWidget(createUi());
		showCloseButton(true);
		getFooterButtons();
		this.paymentType = paymentType;
		
		this.contractStartDate = contractStartDate;
		this.contractEndDate = contractEndDate;

		enterpriseService.getAllConcepts(new AsyncCallback<ContractConcepts>() {

			@Override
			public void onSuccess(ContractConcepts contractConceptsIn) {
				contractConcepts = contractConceptsIn;

				providedDeckPanel();
				showDialog();
			}

			@Override
			public void onFailure(Throwable caught) {
				// Failure
			}

		});
	}

	protected EmployeeContractPaymentEditor(ContractConceptCalcType paymentType, ContractConceptCalc selectedPayment, Date contractStartDate, Date contractEndDate) {
		setCaption(getCaption(paymentType));
		setWidget(createUi());
		showCloseButton(true);
		getFooterButtons();
		this.paymentType = paymentType;
		this.payment = selectedPayment;
		
		this.contractStartDate = contractStartDate;
		this.contractEndDate = contractEndDate;

		enterpriseService.getAllConcepts(new AsyncCallback<ContractConcepts>() {

			@Override
			public void onSuccess(ContractConcepts contractConceptsIn) {
				contractConcepts = contractConceptsIn;

				providedDeckPanel();
				fillDeckPanel();
				showDialog();
			}

			@Override
			public void onFailure(Throwable caught) {
				// Failure
			}

		});
	}

	// ----------------------------------------- Caption

	private String getCaption(ContractConceptCalcType paymentType) {
		switch (paymentType) {
		case PAYMENT:
			return "Asistente Pagos";
		case DEDUCTION:
			return "Asistente Deduciones";
		case COST:
			return "Asistente Costes";
		case BONUS:
			return "Asistente Bonus";
		case EMBARGO:
			return "Asistente Embargo";
		default:
			return null;
		}
	}

	private Widget createUi() {
		HTMLPanel root = new HTMLPanel("");
		root.getElement().getStyle().setProperty("display", "flex");
		root.getElement().getStyle().setProperty("flexDirection", "column");
		root.getElement().getStyle().setProperty("gap", ".5rem");
		root.getElement().getStyle().setProperty("margin", "1rem 0");

		messagePanel = new HTMLPanel("");
		root.add(messagePanel);

		HTMLPanel formPanel = new HTMLPanel("");
		formPanel.getElement().getStyle().setProperty("width", "40rem");
		formPanel.getElement().getStyle().setProperty("padding", ".7rem");
		formPanel.getElement().getStyle().setProperty("display", "flex");
		formPanel.getElement().getStyle().setProperty("flexDirection", "column");
		formPanel.getElement().getStyle().setProperty("gap", ".5rem");
		root.add(formPanel);

		deckPanel = new DeckPanel();
		formPanel.add(deckPanel);

		buildPaymentTable();
		buildDeductionTable();
		buildCostTable();
		buildBonusTable();
		buildEmbargoTable();

		buttonsPanel = new HTMLPanel("");
		buttonsPanel.getElement().getStyle().setProperty("width", "100%");
		buttonsPanel.getElement().getStyle().setProperty("display", "flex");
		buttonsPanel.getElement().getStyle().setProperty("justifyContent", "flex-end");
		buttonsPanel.getElement().getStyle().setProperty("gap", "3em");
		formPanel.add(buttonsPanel);

		return root;
	}

	private void buildPaymentTable() {
		paymentTable = createColumnPanel();
		paymentTypePanel = new HTMLPanel("");
		paymentTypePanel.getElement().getStyle().setProperty("width", "100%");
		paymentTable.add(createLabeledPanel("Tipo", paymentTypePanel));

		paymentConceptSB = new AonCustomSuggestBox("Concepto");
		paymentDescriptionTB = new AonCustomTextBox("Descripcion");
		paymentExpressionTB = new AonCustomTextArea("Expresion");
		paymentSalaryTypeLB = new AonCustomListBox("Tipo Nomina/Recibo");
		paymentTaxedTypeLB = new AonCustomListBox("Tributa");
		paymentTaxedExpression = new AonCustomTextBox("");
		paymentQuoteTypeLB = new AonCustomListBox("Cotiza");
		paymentQuoteExpression = new AonCustomTextBox("");
		paymentMonthPanel = new HTMLPanel("");
		paymentMonthPanel.getElement().getStyle().setProperty("width", "100%");
		paymentMonthLB = new AonCustomListBox("Mes Cobro");
		paymentMonthPanel.add(paymentMonthLB);
		paymentStartDateBx = new AonCustomDateBox("Fecha Inicio");
		paymentEndDateBx = new AonCustomDateBox("Fecha Fin");

		paymentTable.add(paymentConceptSB);
		paymentTable.add(paymentDescriptionTB);
		paymentTable.add(paymentExpressionTB);
		paymentTable.add(paymentSalaryTypeLB);

		HTMLPanel taxedPanel = createRowPanel();
		taxedPanel.add(paymentTaxedTypeLB);
		taxedPanel.add(paymentTaxedExpression);
		paymentTable.add(taxedPanel);

		HTMLPanel quotePanel = createRowPanel();
		quotePanel.add(paymentQuoteTypeLB);
		quotePanel.add(paymentQuoteExpression);
		paymentTable.add(quotePanel);

		HTMLPanel datesPanel = createRowPanel();
		datesPanel.add(paymentMonthPanel);
		datesPanel.add(paymentStartDateBx);
		datesPanel.add(paymentEndDateBx);
		paymentTable.add(datesPanel);

		deckPanel.add(paymentTable);
	}

	private void buildDeductionTable() {
		deductionTable = createColumnPanel();
		deductionTypeLB = new AonCustomListBox("Tipo");
		deductionConceptSB = new AonCustomSuggestBox("Concepto");
		deductionDescriptionTB = new AonCustomTextBox("Descripcion");
		deductionExpressionTB = new AonCustomTextArea("Expresion");
		deductionStartDateBx = new AonCustomDateBox("Fecha Inicio");
		deductionEndDateBx = new AonCustomDateBox("Fecha Fin");

		deductionTable.add(deductionTypeLB);
		deductionTable.add(deductionConceptSB);
		deductionTable.add(deductionDescriptionTB);
		deductionTable.add(deductionExpressionTB);

		HTMLPanel datesPanel = createRowPanel();
		datesPanel.add(deductionStartDateBx);
		datesPanel.add(deductionEndDateBx);
		deductionTable.add(datesPanel);

		deckPanel.add(deductionTable);
	}

	private void buildCostTable() {
		costTable = createColumnPanel();
		costTypeLB = new AonCustomListBox("Tipo");
		costCodeSB = new AonCustomSuggestBox("Codigo");
		costDescriptionTB = new AonCustomTextBox("Descripcion");
		costExpressionTB = new AonCustomTextArea("Expresion");
		costStartDateBx = new AonCustomDateBox("Fecha Inicio");
		costEndDateBx = new AonCustomDateBox("Fecha Fin");

		costTable.add(costTypeLB);
		costTable.add(costCodeSB);
		costTable.add(costDescriptionTB);
		costTable.add(costExpressionTB);

		HTMLPanel datesPanel = createRowPanel();
		datesPanel.add(costStartDateBx);
		datesPanel.add(costEndDateBx);
		costTable.add(datesPanel);

		deckPanel.add(costTable);
	}

	private void buildBonusTable() {
		bonusTable = createColumnPanel();
		bonusTypePanel = new HTMLPanel("");
		bonusTypePanel.getElement().getStyle().setProperty("width", "100%");
		bonusDescriptionSB = new AonCustomSuggestBox("Descripcion");
		bonusExpressionTB = new AonCustomTextArea("Expresion");
		bonusStartDateBx = new AonCustomDateBox("Fecha Inicio");
		bonusEndDateBx = new AonCustomDateBox("Fecha Fin");

		bonusTable.add(createLabeledPanel("Tipo", bonusTypePanel));
		bonusTable.add(bonusDescriptionSB);
		bonusTable.add(bonusExpressionTB);

		HTMLPanel datesPanel = createRowPanel();
		datesPanel.add(bonusStartDateBx);
		datesPanel.add(bonusEndDateBx);
		bonusTable.add(datesPanel);

		deckPanel.add(bonusTable);
	}

	private void buildEmbargoTable() {
		embargoTable = createColumnPanel();
		embargoDescriptionTB = new AonCustomTextBox("Descripcion");
		embargoExpressionTB = new AonCustomTextArea("Expresion");
		embargoAmountDB = new DoubleBox();
		embargoAmountDB.setWidth("100%");
		embargoStartDateBx = new AonCustomDateBox("Fecha Inicio");
		embargoEndDateBx = new AonCustomDateBox("Fecha Fin");

		embargoTable.add(embargoDescriptionTB);
		embargoTable.add(embargoExpressionTB);
		embargoTable.add(createLabeledPanel("Importe", embargoAmountDB));

		HTMLPanel datesPanel = createRowPanel();
		datesPanel.add(embargoStartDateBx);
		datesPanel.add(embargoEndDateBx);
		embargoTable.add(datesPanel);

		deckPanel.add(embargoTable);
	}

	private HTMLPanel createColumnPanel() {
		HTMLPanel panel = new HTMLPanel("");
		panel.getElement().getStyle().setProperty("display", "flex");
		panel.getElement().getStyle().setProperty("flexDirection", "column");
		panel.getElement().getStyle().setProperty("gap", ".5rem");
		return panel;
	}

	private HTMLPanel createRowPanel() {
		HTMLPanel panel = new HTMLPanel("");
		panel.getElement().getStyle().setProperty("display", "flex");
		panel.getElement().getStyle().setProperty("justifyContent", "space-between");
		panel.getElement().getStyle().setProperty("gap", ".5rem");
		return panel;
	}

	private HTMLPanel createLabeledPanel(String title, Widget content) {
		HTMLPanel panel = createColumnPanel();
		Label label = new Label(title);
		label.getElement().getStyle().setProperty("fontWeight", "bold");
		panel.add(label);
		panel.add(content);
		return panel;
	}

	// ----------------------------------------- DeckPanel

	private void providedDeckPanel() {
		switch (this.paymentType) {
			case PAYMENT:
				providedPayment();
				deckPanel.showWidget(deckPanel.getWidgetIndex(paymentTable));
				break;
			case DEDUCTION:
				providedDeduction();
				deckPanel.showWidget(deckPanel.getWidgetIndex(deductionTable));
				break;
			case COST:
				providedCost();
				deckPanel.showWidget(deckPanel.getWidgetIndex(costTable));
				break;
			case BONUS:
				providedBonus();
				deckPanel.showWidget(deckPanel.getWidgetIndex(bonusTable));
				break;
			case EMBARGO:
				deckPanel.showWidget(deckPanel.getWidgetIndex(embargoTable));
				break;
			default:
				break;
		}
	}

	private void initializeSalaryType(ListBox listBox) {
		listBox.clear();
		listBox.addItem("N\u00f3mina", Salary.Type.SALARY.ordinal() + "");
		listBox.addItem("Extra", Salary.Type.EXTRA.ordinal() + "");
		listBox.addItem("Finiquito", Salary.Type.SETTLE.ordinal() + "");
		listBox.addItem("Atraso", Salary.Type.DELAY.ordinal() + "");
	}

	// ----------------------------------------- Provided Payment

	private void providedPayment() {
		initializePaymentType();
		initializeSB(paymentConceptSB, paymentDescriptionTB, paymentExpressionTB);
		initializeTaxed();
		initializeQuote();
		initializeMonth(paymentMonthLB.getListBox());
		initializeSalaryType(paymentSalaryTypeLB.getListBox());
		paymentMonthPanel.setVisible(false);
	}

	private void initializePaymentType() {
		paymentTypeLB = new TypeListBox<>(Payment.Type.class, 10);
		paymentTypeLB.setSelected(Payment.Type.DEFAULT);
		paymentTypeLB.addStyleName("aon-selectOneMenu");
		paymentTypeLB.getElement().getStyle().setWidth(100, Unit.PCT);
//		paymentTypeLB.addChangeHandler(e -> {
//			paymentMonthPanel.setVisible(paymentTypeLB.getSelected().equals(Payment.Type.CRA_0004) || paymentTypeLB.getSelected().equals(Payment.Type.CRA_0005));
//		});
		paymentTypePanel.clear();
		paymentTypePanel.add(paymentTypeLB);
	}

	private void initializeTaxed() {
		paymentTaxedTypeLB.clearItems();
		paymentTaxedTypeLB.addItem("Importe integro", "FULL");
		paymentTaxedTypeLB.addItem("Exento", "NONE");
		paymentTaxedTypeLB.addItem("Personalizado", "CUSTOM");
		paymentTaxedTypeLB.addItem("Ingreso a Cuenta", "IRPF_CTA_ESP");

		paymentTaxedTypeLB.addChangeHandler(e -> {
			int selected = paymentTaxedTypeLB.getSelectedIndex();
			switch (selected) {
			case 0:
				paymentTaxedExpression.setEnable(false);
				paymentTaxedExpression.setValue("_P");
				break;
			case 1:
				paymentTaxedExpression.setEnable(false);
				paymentTaxedExpression.setValue("0.00");
				break;
			case 3:
				paymentTaxedExpression.setEnable(false);
				paymentTaxedExpression.setValue("BASE_CTA_ESP=( isdef BASE_CTA_ESP ? BASE_CTA_ESP : 0.00 ) + _P; _P");
				break;
			default:
				paymentTaxedExpression.setEnable(true);
				paymentTaxedExpression.setValue("");
				break;
			}
		});
		
		DomEvent.fireNativeEvent(Document.get().createChangeEvent(), paymentTaxedTypeLB.getListBox());
	}

	private void initializeQuote() {
		paymentQuoteTypeLB.clearItems();
		paymentQuoteTypeLB.addItem("Importe integro", "FULL");
		paymentQuoteTypeLB.addItem("Exento", "NONE");
		paymentQuoteTypeLB.addItem("Prorratear", "PRORRAT");
		paymentQuoteTypeLB.addItem("Personalizado", "CUSTOM");

		paymentQuoteTypeLB.addChangeHandler(e -> {
			int selected = paymentQuoteTypeLB.getSelectedIndex();
			switch (selected) {
			case 0:
				paymentQuoteExpression.setEnable(false);
				paymentQuoteExpression.setValue("_P");
				break;
			case 1:
				paymentQuoteExpression.setEnable(false);
				paymentQuoteExpression.setValue("0.00");
				break;
			case 2:
				paymentQuoteExpression.setEnable(false);
				paymentQuoteExpression.setValue("PRORRATEAR()");
				break;
			default:
				paymentQuoteExpression.setEnable(true);
				paymentQuoteExpression.setValue("");
				break;
			}
		});
		
		DomEvent.fireNativeEvent(Document.get().createChangeEvent(), paymentQuoteTypeLB.getListBox());
	}
	
	private String getTaxedQuoteType(String expression) {
		if(AonStringUtils.isBlank(expression)) { 
			return "CUSTOM";
		}
		if(AonStringUtils.contains(expression, "BASE_CTA_ESP")) {
			return "IRPF_CTA_ESP";
		}
		switch (expression) {
			case "_P":
				return "FULL";
			case "0.00":
				return "NONE";
			case "PRORRATEAR()":
				return "PRORRAT";
			default:
				return "CUSTOM";
		}
	}

	// ----------------------------------------- Provided Deduction

	private void providedDeduction() {
		initializeDeductionType();
		initializeSB(deductionConceptSB, deductionDescriptionTB, deductionExpressionTB);
	}

	private void initializeDeductionType() {
		deductionTypeLB.clearItems();
		deductionTypeLB.addItem("-", "");
		for (Deduction.Type deductionType : Deduction.Type.values())
			deductionTypeLB.addItem(deductionType.getDescription(), deductionType.ordinal() + "");
	}

	// ----------------------------------------- Provided Cost

	private void providedCost() {
		initializeCostType();
		initializeSB(costCodeSB, costDescriptionTB, costExpressionTB);
	}

	private void initializeCostType() {
		costTypeLB.clearItems();
		costTypeLB.addItem("-", "");
		for (Deduction.Type costType : Deduction.Type.values())
			costTypeLB.addItem(costType.getDescription(), costType.ordinal() + "");
	}

	// ----------------------------------------- Provided Bonus

	private void providedBonus() {
		initializeBonusType();
		initializeSB(bonusDescriptionSB, null, bonusExpressionTB);
	}

	private void initializeBonusType() {
		bonusTypeLB = new TypeListBox<>(Bonus.Type.class, 10);
		bonusTypeLB.addStyleName("aon-selectOneMenu");
		bonusTypeLB.getElement().getStyle().setWidth(100, Unit.PCT);
		bonusTypePanel.clear();
		bonusTypePanel.add(bonusTypeLB);
	}

	// ----------------------------------------- MonthList

	private void initializeMonth(ListBox listBox) {
		listBox.clear();
		listBox.addItem("Todos", "");
		listBox.addItem("Enero", "0");
		listBox.addItem("Febrero", "1");
		listBox.addItem("Marzo", "2");
		listBox.addItem("Abril", "3");
		listBox.addItem("Mayo", "4");
		listBox.addItem("Junio", "5");
		listBox.addItem("Julio", "6");
		listBox.addItem("Agosto", "7");
		listBox.addItem("Septiembre", "8");
		listBox.addItem("Octubre", "9");
		listBox.addItem("Noviembre", "10");
		listBox.addItem("Diciembre", "11");
	}

	// ----------------------------------------- SuggestBox

	private void initializeSB(AonCustomSuggestBox customSuggestBox, AonCustomTextBox descriptionTB,
			AonCustomTextArea expressionTB) {
		SuggestBox suggestBox = customSuggestBox.getSuggestBox();
		List<String> conceptsSuggest = new ArrayList<>();
		for (ContractConcept contractConcept : getConcepts()) {
			String suggestDisplay = AonStringUtils.isBlank(contractConcept.getCode()) ? ""
					: contractConcept.getCode() + " - ";
			suggestDisplay += contractConcept.getDescription();
			conceptsSuggest.add(suggestDisplay);
		}

		MultiWordSuggestOracle orclConcepts = (MultiWordSuggestOracle) suggestBox.getSuggestOracle();
		orclConcepts.clear();
		orclConcepts.addAll(conceptsSuggest);
		customSuggestBox.setAutoSelectEnabled(false);
		
		suggestBox.addSelectionHandler(e -> {
			selectedConcept = getConcept(customSuggestBox);
			
//			Window.alert("suggestBox : " + suggestBox.getValue() + "\nselectedConcept : " + selectedConcept);

			if (null != selectedConcept) {
				ListBox typeLB = getTypeListBox();
				if(null != selectedConcept.getType()) setSelectedValueLB(typeLB, selectedConcept.getType().toString());
				DomEvent.fireNativeEvent(Document.get().createChangeEvent(), typeLB);
				if (descriptionTB != null)
					descriptionTB.setValue(selectedConcept.getDescription());
				expressionTB.setValue(selectedConcept.getExpression());
			}
		});
	}

	private ContractConcept getConcept(AonCustomSuggestBox suggestBox) {
		Set<ContractConcept> concepts = getConcepts();
		String code = null;
		String description = null;

		if (suggestBox.getValue().contains(" - ") && !this.paymentType.equals(ContractConceptCalcType.BONUS)) {
			code = suggestBox.getValue().split(" - ")[0];
			description = suggestBox.getValue().split(" - ")[1];

			for (ContractConcept contractConcept : concepts)
				if (isIdentical(contractConcept, code, description))
					return contractConcept;
		} else {
			code = suggestBox.getValue();
			description = suggestBox.getValue();

			for (ContractConcept contractConcept : concepts)
				if (isSimilar(contractConcept, code, description))
					return contractConcept;
		}

		return null;
	}

	private boolean isIdentical(ContractConcept contractConcept, String code, String description) {
		return AonStringUtils.equalsIgnoreCase(code, contractConcept.getCode())
				&& AonStringUtils.equalsIgnoreCase(description, contractConcept.getDescription());
	}

	private boolean isSimilar(ContractConcept contractConcept, String code, String description) {
		return (AonStringUtils.equalsIgnoreCase(code, contractConcept.getCode())
				&& AonStringUtils.isBlank(contractConcept.getDescription()))
				|| (AonStringUtils.equalsIgnoreCase(description, contractConcept.getDescription())
						&& AonStringUtils.isBlank(contractConcept.getCode()));
	}

	private Set<ContractConcept> getConcepts() {
		switch (this.paymentType) {
		case PAYMENT:
			return contractConcepts.getPaymentConcepts();
		case DEDUCTION:
			return contractConcepts.getDeductionConcepts();
		case COST:
			return contractConcepts.getCostConcepts();
		case BONUS:
			return contractConcepts.getBonusConcepts();
		default:
			return new HashSet<>();
		}
	}
	
	private ListBox getTypeListBox() {
		switch (this.paymentType) {
		case PAYMENT:
			return paymentTypeLB;
		case DEDUCTION:
			return deductionTypeLB.getListBox();
		case COST:
			return costTypeLB.getListBox();
		case BONUS:
			return bonusTypeLB;
		default:
			return null;
		}
	}

	// ----------------------------------------- Fill Payment

	private void fillDeckPanel() {
		switch (this.paymentType) {
		case PAYMENT:
			fillPayment();
			break;
		case DEDUCTION:
			fillDeduction();
			break;
		case COST:
			fillCost();
			break;
		case BONUS:
			fillBonus();
			break;
		case EMBARGO:
			fillEmbargo();
			break;
		default:
			break;
		}
	}

	private void fillPayment() {
		paymentTypeLB.setSelected(this.payment.getType());
		paymentConceptSB.setValue(this.payment.getName());
		paymentDescriptionTB.setValue(this.payment.getDescription());
		paymentExpressionTB.setValue(this.payment.getExpression());
		setSelectedValueLB(paymentSalaryTypeLB.getListBox(), this.payment.getSalaryType().ordinal() + "");
		setSelectedValueLB(paymentTaxedTypeLB.getListBox(), getTaxedQuoteType(this.payment.getIrpfExpression()));
		paymentTaxedExpression.setValue(this.payment.getIrpfExpression());
		setSelectedValueLB(paymentQuoteTypeLB.getListBox(), getTaxedQuoteType(this.payment.getQuoteExpression()));
		paymentQuoteExpression.setValue(this.payment.getQuoteExpression());
		if(this.payment.getType() != null /*&& (this.payment.getType().equals(Payment.Type.CRA_0004) || this.payment.getType().equals(Payment.Type.CRA_0005))*/) {
			paymentMonthPanel.setVisible(true);
			setSelectedValueLB(paymentMonthLB.getListBox(), null == this.payment.getMonth() ? "" : this.payment.getMonth().toString());
		} else
			paymentMonthPanel.setVisible(false);
		
		paymentStartDateBx.setValue(this.payment.getStartDate());
		paymentEndDateBx.setValue(this.payment.getEndDate());
	}

	private void fillDeduction() {
		setSelectedValueLB(deductionTypeLB.getListBox(), this.payment.getCodeType());
		deductionConceptSB.setValue(this.payment.getName());
		deductionDescriptionTB.setValue(this.payment.getDescription());
		deductionExpressionTB.setValue(this.payment.getExpression());
		deductionStartDateBx.setValue(this.payment.getStartDate());
		deductionEndDateBx.setValue(this.payment.getEndDate());
	}

	private void fillCost() {
		setSelectedValueLB(costTypeLB.getListBox(), this.payment.getCodeType());
		costCodeSB.setValue(this.payment.getName());
		costDescriptionTB.setValue(this.payment.getDescription());
		costExpressionTB.setValue(this.payment.getExpression());
		costStartDateBx.setValue(this.payment.getStartDate());
		costEndDateBx.setValue(this.payment.getEndDate());
	}

	private void fillBonus() {
		if(AonStringUtils.isNotBlank(this.payment.getCodeType()))
			bonusTypeLB.setSelected(Bonus.Type.values()[Integer.parseInt(this.payment.getCodeType())]);
		bonusDescriptionSB.setValue(this.payment.getDescription());
		bonusExpressionTB.setValue(this.payment.getExpression());
		bonusStartDateBx.setValue(this.payment.getStartDate());
		bonusEndDateBx.setValue(this.payment.getEndDate());
	}
	
	private void fillEmbargo() {
		embargoDescriptionTB.setValue(this.payment.getDescription());
		embargoExpressionTB.setValue(this.payment.getExpression());
		embargoAmountDB.setValue(this.payment.getAmount());
		embargoStartDateBx.setValue(this.payment.getStartDate());
		embargoEndDateBx.setValue(this.payment.getEndDate());
	}

	// ----------------------------------------- ShowDialog

	private void showDialog() {
		Scheduler.get().scheduleDeferred(() -> {
			center();
			show();
		});
	}

	// ----------------------------------------- FooterButtons

	private void getFooterButtons() {
		buttonsPanel.clear();

		acceptDialog = new Button();
		acceptDialog.setStyleName(AON.CSS.aonOkButtonSmall());
		acceptDialog.setText(AON.MSG.accept());
		acceptDialog.addClickHandler(e -> {
			checkPaymentDates(accept -> {
				if(accept) {
					hide();
					onAccept(createContractConceptCalc());
				}
			});
		});

		buttonsPanel.add(acceptDialog);
	}
	
	private void checkPaymentDates(Consumer<Boolean> accept) {
		if(this.paymentType.equals(ContractConceptCalcType.PAYMENT)) {
			checkPaymentDates(paymentStartDateBx, paymentEndDateBx, accept);
		} else if(this.paymentType.equals(ContractConceptCalcType.DEDUCTION)) {
			checkPaymentDates(deductionStartDateBx, deductionEndDateBx, accept);
		} else if(this.paymentType.equals(ContractConceptCalcType.COST)) {
			checkPaymentDates(costStartDateBx, costEndDateBx, accept);
		} else if(this.paymentType.equals(ContractConceptCalcType.BONUS)) {
			checkPaymentDates(bonusStartDateBx, bonusEndDateBx, accept);
		} else if(this.paymentType.equals(ContractConceptCalcType.EMBARGO)) {
			checkPaymentDates(embargoStartDateBx, embargoEndDateBx, accept);
		} else
			accept.accept(false);
	}
	
	private void checkPaymentDates(AonCustomDateBox startDateBx, AonCustomDateBox endDateBx, Consumer<Boolean> accept) {
		Date paymentStart = startDateBx.getValue(); 
		Date paymentEnd = endDateBx.getValue();
		
		if(null == paymentStart) {
			AonMessagePanel.showError(messagePanel, "La fecha inicio del devengo no esta definida");
			accept.accept(false);
		} else if(null != paymentStart && null != paymentEnd && paymentEnd.before(paymentStart)) {
			AonMessagePanel.showError(messagePanel, "La fecha fin del devengo es anterior a la fecha de inicio del devengo");
			accept.accept(false);
		} else if(null != paymentStart && paymentStart.before(contractStartDate)) {
			AonDialog warningDialog = new AonDialog("Fecha inicio", new HTMLPanel("La fecha de inicio del devengo es anterior a la fecha de inicio del contrato. \u00BFDesea continuar igualmente?"));
			warningDialog.confirm(new AonAcceptDialogCallback() {
				
				@Override
				public void onCancel() { accept.accept(false); }
				
				@Override
				public void onAccept() { accept.accept(true); }
			});
		} else if(null != paymentEnd && null != contractEndDate && paymentEnd.after(contractEndDate)) {
			AonDialog warningDialog = new AonDialog("Fecha fin", new HTMLPanel("La fecha fin del devengo es posterior a la fecha fin del contrato. \u00BFDesea continuar igualmente?"));
			warningDialog.confirm(new AonAcceptDialogCallback() {
				
				@Override
				public void onCancel() { accept.accept(false); }
				
				@Override
				public void onAccept() { accept.accept(true); }
			});
		} else 
			accept.accept(true);
	}

	public void setSaveButton() {
		acceptDialog.setText("Grabar");
	}
	
	private ContractConceptCalc createContractConceptCalc() {
		switch (this.paymentType) {
			case PAYMENT:
				return createPayment();
			case DEDUCTION:
				return createDeduction();
			case COST:
				return createCost();
			case BONUS:
				return createBonus();
			case EMBARGO:
				return createEmbargo();
			default:
				return null;
		}
	}

	private ContractConceptCalc createPayment() {
		if(null == this.payment)
			this.payment = new ContractConceptCalc();
		
		payment.setType(paymentTypeLB.getSelected());
		if (null != selectedConcept) {
			payment.setConceptId(selectedConcept.getId());
			payment.setName(selectedConcept.getCode());
		} else
			payment.setName(paymentConceptSB.getValue());
		
		payment.setDescription(paymentDescriptionTB.getValue());
		payment.setExpression(paymentExpressionTB.getValue());
		payment.setSalaryType(Salary.Type.values()[Integer.parseInt(paymentSalaryTypeLB.getValue())]);
		payment.setIrpfExpression(paymentTaxedExpression.getValue());
		payment.setQuoteExpression(paymentQuoteExpression.getValue());
		payment.setMonth(AonStringUtils.isBlank(paymentMonthLB.getValue()) ? null
				: Short.parseShort(paymentMonthLB.getValue()));
		payment.setStartDate(paymentStartDateBx.getValue());
		payment.setEndDate(paymentEndDateBx.getValue());
		payment.setContractConceptCalcType(ContractConceptCalcType.PAYMENT);

		return payment;
	}

	private ContractConceptCalc createDeduction() {
		if(null == this.payment)
			this.payment = new ContractConceptCalc();

		payment.setCodeType(deductionTypeLB.getValue());
		if (null != selectedConcept) {
			payment.setConceptId(selectedConcept.getId());
			payment.setName(selectedConcept.getCode());
		} else
			payment.setName(deductionConceptSB.getValue());
		
		payment.setDescription(deductionDescriptionTB.getValue());
		payment.setExpression(deductionExpressionTB.getValue());
		payment.setStartDate(deductionStartDateBx.getValue());
		payment.setEndDate(deductionEndDateBx.getValue());
		payment.setContractConceptCalcType(ContractConceptCalcType.DEDUCTION);

		return payment;
	}

	private ContractConceptCalc createCost() {
		if(null == this.payment)
			this.payment = new ContractConceptCalc();

		payment.setCodeType(costTypeLB.getValue());
		if (null != selectedConcept) {
			payment.setConceptId(selectedConcept.getId());
			payment.setName(selectedConcept.getCode());
		} else
			payment.setName(costCodeSB.getValue());
		
		payment.setDescription(costDescriptionTB.getValue());
		payment.setExpression(costExpressionTB.getValue());
		payment.setStartDate(costStartDateBx.getValue());
		payment.setEndDate(costEndDateBx.getValue());
		payment.setContractConceptCalcType(ContractConceptCalcType.COST);

		return payment;
	}

	private ContractConceptCalc createBonus() {
		if(null == this.payment)
			this.payment = new ContractConceptCalc();

		if (null != selectedConcept)
			payment.setConceptId(selectedConcept.getId());
		
		payment.setCodeType(bonusTypeLB.getSelected().ordinal() + "");
		payment.setDescription(bonusDescriptionSB.getValue());
		payment.setExpression(bonusExpressionTB.getValue());
		payment.setStartDate(bonusStartDateBx.getValue());
		payment.setEndDate(bonusEndDateBx.getValue());
		payment.setContractConceptCalcType(ContractConceptCalcType.BONUS);

		return payment;
	}
	
	private ContractConceptCalc createEmbargo() {
		if(null == this.payment)
			this.payment = new ContractConceptCalc();

		payment.setDescription(embargoDescriptionTB.getValue());
		payment.setExpression(embargoExpressionTB.getValue());
		payment.setAmount(embargoAmountDB.getValue());
		payment.setStartDate(embargoStartDateBx.getValue());
		payment.setEndDate(embargoEndDateBx.getValue());
		payment.setContractConceptCalcType(ContractConceptCalcType.EMBARGO);

		return payment;
	}

	// ----------------------------------------- Auxiliar methods

	private void setSelectedValueLB(ListBox lBox, String str) {
		String text = str;
		int indexToFind = 0;
		for (int i = 0; i < lBox.getItemCount(); i++) {
			if (lBox.getValue(i).equals(text)) {
				indexToFind = i;
				break;
			}
		}
		lBox.setSelectedIndex(indexToFind);
	}

	// ----------------------------------------- AbstractMehtods

	protected abstract void onAccept(ContractConceptCalc contractConceptCalc);

}
