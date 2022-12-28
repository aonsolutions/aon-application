package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.payroll.client.AgreementDraft.TypeListBox;
import com.esferalia.aon.gwt.payroll.shared.Bonus;
import com.esferalia.aon.gwt.payroll.shared.ContractConcept;
import com.esferalia.aon.gwt.payroll.shared.ContractConceptCalc;
import com.esferalia.aon.gwt.payroll.shared.ContractConceptCalc.ContractConceptCalcType;
import com.esferalia.aon.gwt.payroll.shared.ContractConcepts;
import com.esferalia.aon.gwt.payroll.shared.Deduction;
import com.esferalia.aon.gwt.payroll.shared.Payment;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public abstract class EmployeeContractPaymentEditor extends AonCustomDialog {

	// ----------------------------------------- UiBinder

	interface Binder extends UiBinder<Widget, EmployeeContractPaymentEditor> {}

	private static final Binder binder = GWT.create(Binder.class);

	// ----------------------------------------- UiFields

	@UiField
	DeckPanel deckPanel;

	// PAYMENT

	@UiField
	HTMLPanel paymentTable;

	@UiField
	HTMLPanel paymentTypePanel;

	@UiField
	SuggestBox paymentConceptSB;

	@UiField
	TextBox paymentDescriptionTB;

	@UiField
	TextArea paymentExpressionTB;

	@UiField
	ListBox paymentTaxedTypeLB;

	@UiField
	TextBox paymentTaxedExpression;

	@UiField
	ListBox paymentQuoteTypeLB;

	@UiField
	TextBox paymentQuoteExpression;
	
	@UiField
	HTMLPanel paymentMonthPanel;

	@UiField
	ListBox paymentMonthLB;

	@UiField
	DateBoxEx paymentStartDateBx;

	@UiField
	DateBoxEx paymentEndDateBx;

	// DEDUCTION

	@UiField
	HTMLPanel deductionTable;

	@UiField
	ListBox deductionTypeLB;

	@UiField
	SuggestBox deductionConceptSB;

	@UiField
	TextBox deductionDescriptionTB;

	@UiField
	TextArea deductionExpressionTB;

	@UiField
	DateBoxEx deductionStartDateBx;

	@UiField
	DateBoxEx deductionEndDateBx;

	// COST

	@UiField
	HTMLPanel costTable;

	@UiField
	ListBox costTypeLB;

	@UiField
	SuggestBox costCodeSB;

	@UiField
	TextBox costDescriptionTB;

	@UiField
	TextArea costExpressionTB;

	@UiField
	DateBoxEx costStartDateBx;

	@UiField
	DateBoxEx costEndDateBx;

	// BONUS

	@UiField
	HTMLPanel bonusTable;

	@UiField
	HTMLPanel bonusTypePanel;

	@UiField
	SuggestBox bonusDescriptionSB;

	@UiField
	TextArea bonusExpressionTB;

	@UiField
	DateBoxEx bonusStartDateBx;

	@UiField
	DateBoxEx bonusEndDateBx;
	
	// EMBARGO

	@UiField
	HTMLPanel embargoTable;

	@UiField
	TextBox embargoDescriptionTB;

	@UiField
	TextArea embargoExpressionTB;
	
	@UiField
	DoubleBox embargoAmountDB;

	@UiField
	DateBoxEx embargoStartDateBx;

	@UiField
	DateBoxEx embargoEndDateBx;

	@UiField
	HTMLPanel buttonsPanel;

	// ----------------------------------------- Variables

	private DomainEnterprisesServiceAsync enterpriseService = DomainEnterprisesServiceAsync.newInstance();
	private ContractConcepts contractConcepts;
	private ContractConcept selectedConcept;
	private ContractConceptCalcType paymentType;
	private ContractConceptCalc payment;

	private TypeListBox<Payment.Type> paymentTypeLB;
	private TypeListBox<Bonus.Type> bonusTypeLB;
	
	private Button acceptDialog;

	// ----------------------------------------- Constructor

	protected EmployeeContractPaymentEditor(ContractConceptCalcType paymentType) {
		setCaption(getCaption(paymentType));
		setWidget(binder.createAndBindUi(this));
		showCloseButton(true);
		getFooterButtons();
		this.paymentType = paymentType;

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

	protected EmployeeContractPaymentEditor(ContractConceptCalcType paymentType, ContractConceptCalc selectedPayment) {
		setCaption(getCaption(paymentType));
		setWidget(binder.createAndBindUi(this));
		showCloseButton(true);
		getFooterButtons();
		this.paymentType = paymentType;
		this.payment = selectedPayment;

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

	// ----------------------------------------- Provided Payment

	private void providedPayment() {
		initializePaymentType();
		initializeSB(paymentConceptSB, paymentDescriptionTB, paymentExpressionTB);
		initializeTaxed();
		initializeQuote();
		initializeMonth(paymentMonthLB);
		paymentMonthPanel.setVisible(false);
	}

	private void initializePaymentType() {
		paymentTypeLB = new TypeListBox<>(Payment.Type.class, 10);
		paymentTypeLB.setSelected(Payment.Type.DEFAULT);
		paymentTypeLB.addStyleName("aon-selectOneMenu");
		paymentTypeLB.getElement().getStyle().setWidth(100, Unit.PCT);
		paymentTypeLB.addChangeHandler(e -> {
			paymentMonthPanel.setVisible(paymentTypeLB.getSelected().equals(Payment.Type.CRA_0004) || paymentTypeLB.getSelected().equals(Payment.Type.CRA_0005));
		});
		paymentTypePanel.clear();
		paymentTypePanel.add(paymentTypeLB);
	}

	private void initializeTaxed() {
		paymentTaxedTypeLB.clear();
		paymentTaxedTypeLB.addItem("Importe integro", "FULL");
		paymentTaxedTypeLB.addItem("Exento", "NONE");
		paymentTaxedTypeLB.addItem("Personalizado", "CUSTOM");
		paymentTaxedTypeLB.addItem("Ingreso a Cuenta", "IRPF_CTA_ESP");

		paymentTaxedTypeLB.addChangeHandler(e -> {
			int selected = paymentTaxedTypeLB.getSelectedIndex();
			switch (selected) {
			case 0:
				paymentTaxedExpression.setEnabled(false);
				paymentTaxedExpression.setValue("_P");
				break;
			case 1:
				paymentTaxedExpression.setEnabled(false);
				paymentTaxedExpression.setValue("0.00");
				break;
			case 3:
				paymentTaxedExpression.setEnabled(false);
				paymentTaxedExpression.setValue("BASE_CTA_ESP=_P");
				break;
			default:
				paymentTaxedExpression.setEnabled(true);
				paymentTaxedExpression.setValue("");
				break;
			}
		});
		
		DomEvent.fireNativeEvent(Document.get().createChangeEvent(), paymentTaxedTypeLB);
	}

	private void initializeQuote() {
		paymentQuoteTypeLB.clear();
		paymentQuoteTypeLB.addItem("Importe integro", "FULL");
		paymentQuoteTypeLB.addItem("Exento", "NONE");
		paymentQuoteTypeLB.addItem("Prorratear", "PRORRAT");
		paymentQuoteTypeLB.addItem("Personalizado", "CUSTOM");

		paymentQuoteTypeLB.addChangeHandler(e -> {
			int selected = paymentQuoteTypeLB.getSelectedIndex();
			switch (selected) {
			case 0:
				paymentQuoteExpression.setEnabled(false);
				paymentQuoteExpression.setValue("_P");
				break;
			case 1:
				paymentQuoteExpression.setEnabled(false);
				paymentQuoteExpression.setValue("0.00");
				break;
			case 2:
				paymentQuoteExpression.setEnabled(false);
				paymentQuoteExpression.setValue("PRORRATEAR()");
				break;
			default:
				paymentQuoteExpression.setEnabled(true);
				paymentQuoteExpression.setValue("");
				break;
			}
		});
		
		DomEvent.fireNativeEvent(Document.get().createChangeEvent(), paymentQuoteTypeLB);
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
		deductionTypeLB.clear();
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
		costTypeLB.clear();
		costTypeLB.addItem("-", "");
		for (Deduction.Type costType : Deduction.Type.values())
			costTypeLB.addItem(costType.getDescription(), costType.ordinal() + "");
	}

	// ----------------------------------------- Provided Bonus

	private void providedBonus() {
		initializeBonusType();
		initializeSB(bonusDescriptionSB, (TextBox) bonusDescriptionSB.getValueBox(), bonusExpressionTB);
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
		listBox.addItem("-", "");
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

	private void initializeSB(SuggestBox suggestBox, TextBox descriptionTB, TextArea expressionTB) {
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
		suggestBox.setAutoSelectEnabled(false);
		
		suggestBox.addSelectionHandler(e -> {
			selectedConcept = getConcept(suggestBox);
			
//			Window.alert("suggestBox : " + suggestBox.getValue() + "\nselectedConcept : " + selectedConcept);

			if (null != selectedConcept) {
				ListBox typeLB = getTypeListBox();
				if(null != selectedConcept.getType()) setSelectedValueLB(typeLB, selectedConcept.getType().toString());
				DomEvent.fireNativeEvent(Document.get().createChangeEvent(), typeLB);
				descriptionTB.setValue(selectedConcept.getDescription());
				expressionTB.setValue(selectedConcept.getExpression());
			}
		});
	}

	private ContractConcept getConcept(SuggestBox suggestBox) {
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
			return deductionTypeLB;
		case COST:
			return costTypeLB;
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
		setSelectedValueLB(paymentTaxedTypeLB, getTaxedQuoteType(this.payment.getIrpfExpression()));
		paymentTaxedExpression.setValue(this.payment.getIrpfExpression());
		setSelectedValueLB(paymentQuoteTypeLB, getTaxedQuoteType(this.payment.getQuoteExpression()));
		paymentQuoteExpression.setValue(this.payment.getQuoteExpression());
		if(this.payment.getType() != null && (this.payment.getType().equals(Payment.Type.CRA_0004) || this.payment.getType().equals(Payment.Type.CRA_0005))) {
			paymentMonthPanel.setVisible(true);
			setSelectedValueLB(paymentMonthLB, this.payment.getMonth() + "");
		} else
			paymentMonthPanel.setVisible(false);
		
		paymentStartDateBx.setValue(this.payment.getStartDate());
		paymentEndDateBx.setValue(this.payment.getEndDate());
	}

	private void fillDeduction() {
		setSelectedValueLB(deductionTypeLB, this.payment.getCodeType());
		deductionConceptSB.setValue(this.payment.getName());
		deductionDescriptionTB.setValue(this.payment.getDescription());
		deductionExpressionTB.setValue(this.payment.getExpression());
		deductionStartDateBx.setValue(this.payment.getStartDate());
		deductionEndDateBx.setValue(this.payment.getEndDate());
	}

	private void fillCost() {
		setSelectedValueLB(costTypeLB, this.payment.getCodeType());
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
			hide();
			onAccept(createContractConceptCalc());
		});

		buttonsPanel.add(acceptDialog);
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
		payment.setIrpfExpression(paymentTaxedExpression.getValue());
		payment.setQuoteExpression(paymentQuoteExpression.getValue());
		payment.setMonth(AonStringUtils.isBlank(paymentMonthLB.getSelectedValue()) ? null
				: Short.parseShort(paymentMonthLB.getSelectedValue()));
		payment.setStartDate(paymentStartDateBx.getValue());
		payment.setEndDate(paymentEndDateBx.getValue());
		payment.setContractConceptCalcType(ContractConceptCalcType.PAYMENT);

		return payment;
	}

	private ContractConceptCalc createDeduction() {
		if(null == this.payment)
			this.payment = new ContractConceptCalc();

		payment.setCodeType(deductionTypeLB.getSelectedValue());
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

		payment.setCodeType(costTypeLB.getSelectedValue());
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
