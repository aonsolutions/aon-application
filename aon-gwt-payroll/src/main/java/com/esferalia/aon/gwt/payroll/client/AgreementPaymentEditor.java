package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarSmall;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarSmallButton;
import com.esferalia.aon.gwt.payroll.client.AgreementDraft.TypeListBox;
import com.esferalia.aon.gwt.payroll.client.FxDialog.IContextProvider;
import com.esferalia.aon.gwt.payroll.shared.AgreementInfo.AgreementExtra;
import com.esferalia.aon.gwt.payroll.shared.ContractConcept;
import com.esferalia.aon.gwt.payroll.shared.ContractConcepts;
import com.esferalia.aon.gwt.payroll.shared.Payment;
import com.esferalia.aon.gwt.payroll.shared.Payment.Type;
import com.esferalia.aon.gwt.payroll.shared.SpecialExpresion;
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
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public abstract class AgreementPaymentEditor extends AonCustomDialog {

	// ----------------------------------------- UiBinder

	interface Binder extends UiBinder<Widget, AgreementPaymentEditor> {}

	private static final Binder binder = GWT.create(Binder.class);

	// ----------------------------------------- UiFields

	// PAYMENT
	
	@UiField (provided = true)
	AonToolbarSmall toolbar;

	@UiField
	HTMLPanel paymentTable;

	@UiField
	HTMLPanel paymentTypePanel;

	@UiField
	SuggestBox paymentConceptSB;

	@UiField
	TextBox paymentDescriptionTB;
	
	@UiField (provided = true)
	AonToolbarSmallButton expresssionVisibilityBtn;

	@UiField
	TextArea paymentExpressionTB;
	
	@UiField
	HTMLPanel paymentTaxedPanel;

	@UiField
	ListBox paymentTaxedTypeLB;

	@UiField
	TextBox paymentTaxedExpression;
	
	@UiField
	HTMLPanel paymentQuotePanel;

	@UiField
	ListBox paymentQuoteTypeLB;

	@UiField
	TextBox paymentQuoteExpression;
	
	@UiField
	HTMLPanel seniorityPanel;
	
	@UiField
	ListBox seniorityType;
	
	@UiField
	HTMLPanel periodPaymentPanel;
	
	@UiField
	DateBoxEx startDateBx;
	
	@UiField
	DateBoxEx endDateBx;
	
	@UiField
	HTMLPanel extraPanel;
	
	@UiField
	ListBox extraType;

	@UiField
	ListBox extraStartDateMonth;
	
	@UiField
	ListBox extraStartDateYear;

	@UiField
	ListBox extraEndDateMonth;
	
	@UiField
	ListBox extraEndDateYear;

	@UiField
	TextBox extraIssueDate;

	@UiField
	HTMLPanel extraAssociatedPanel;
	
	@UiField
	ListBox extraStartDateMonthAssociated;
	
	@UiField
	ListBox extraStartDateYearAssociated;

	@UiField
	ListBox extraEndDateMonthAssociated;
	
	@UiField
	ListBox extraEndDateYearAssociated;

	@UiField
	TextBox extraIssueDateAssociated;
	
	@UiField
	ListBox extraAssociated;

	@UiField
	HTMLPanel buttonsPanel;

	// ----------------------------------------- Variables

	private DomainEnterprisesServiceAsync enterpriseService = DomainEnterprisesServiceAsync.newInstance();
	private ContractConcepts contractConcepts;
	private ContractConcept selectedConcept;
	private Payment payment;
	private AgreementExtra extra;
	private AgreementExtra associatedExtra;
	private Payment associatedPayment;

	private TypeListBox<Payment.Type> paymentTypeLB;
	
	private Set<Payment> allPayments;
	private Set<AgreementExtra> allExtras;
	
	private Button acceptDialog;
	
	private IContextProvider context;
	
	private boolean showAdvance = false;

	// ----------------------------------------- Constructor

	protected AgreementPaymentEditor(Payment payment, AgreementExtra extra, Set<Payment> allPayments, Set<AgreementExtra> allExtras) {
		setCaption("Devengo");
		createToolbar();
		
		expresssionVisibilityBtn = new AonToolbarSmallButton("Editar expresi\u00f3n", AON.CSS.aonIconEdit());
		
		// Remove this line when FxDialog is fixed
		expresssionVisibilityBtn.setVisible(false);
		
		setWidget(binder.createAndBindUi(this));
		showCloseButton(true);
		getFooterButtons();
		
		this.payment = payment;
		this.extra = extra;
		
		this.allPayments = allPayments;
		this.allExtras = allExtras;
		
		initializeSeniorityPanel();
		
		initializeExtraPanel();
		if(payment.getType().equals(Payment.Type.CRA_0004) || payment.getType().equals(Payment.Type.CRA_0005))
			initializeExtraAssociatedPanel();
		
		enterpriseService.getAllConcepts(new AsyncCallback<ContractConcepts>() {

			@Override
			public void onSuccess(ContractConcepts contractConceptsIn) {
				contractConcepts = contractConceptsIn;

				providedPayment();
				fillPayment();
				if(null != extra) fillExtra();
				else {
					setSelectedValueLB(extraType, getExtraType(extra));
					DomEvent.fireNativeEvent(Document.get().createChangeEvent(), extraType);
				}

				DomEvent.fireNativeEvent(Document.get().createChangeEvent(), extraAssociated);	
				showHideAdvanceOptions();
				showDialog();
			}

			@Override
			public void onFailure(Throwable caught) {
				// Failure
			}

		});
	}

	private void createToolbar() {
		toolbar = new AonToolbarSmall("");
		
		AonToolbarSmallButton advanceBtn = new AonToolbarSmallButton("Mostras opciones avanzadas", AON.CSS.aonIconSettings());
		advanceBtn.addClickHandler(e -> {
			showAdvance = !showAdvance;
			advanceBtn.setTitle(showAdvance ? "Ocultar opciones avanzadas" : "Mostras opciones avanzadas");
			showHideAdvanceOptions();
			showDialog();
		});
		
		toolbar.add(advanceBtn);
	}

	private void showHideAdvanceOptions() {
		paymentTaxedPanel.setVisible(showAdvance);
		paymentQuotePanel.setVisible(showAdvance);
		seniorityPanel.setVisible(showAdvance);
		periodPaymentPanel.setVisible(showAdvance);
	}

	private void initializeSeniorityPanel() {
		seniorityPanel.setVisible(false);
		
		seniorityType.clear();
		seniorityType.addItem("Autom\u00e1tico", "");
		seniorityType.addItem("Inicio mes", "INICIO_MES(INICIO_ANTIGUEDAD)");
		seniorityType.addItem("Inicio a\u00f1o", "INICIO_A\u00d1O(INICIO_ANTIGUEDAD)");
	}

	private void initializeExtraAssociatedPanel() {
		extraAssociated.clear();
		extraAssociated.addItem("Extra sin asociar", "");
		extraAssociated.addItem("Crear extra asociada", "new");
		if(null != extra)
			allExtras.stream().filter(extraIt -> !extraIt.getId().equals(extra.getId())).forEach(extraIt -> {
				Payment paymentIt = getPaymentById(extraIt.getAgreementPayment());
				extraAssociated.addItem(paymentIt.getDescription(), extraIt.getId().toString());
			});
		else
			allPayments.stream().filter(paymentIt -> (payment.getId() != null && paymentIt.getId() != null  && !paymentIt.getId().equals(payment.getId())) && !paymentIt.isDeleted() && (paymentIt.getType().equals(Type.CRA_0004) || paymentIt.getType().equals(Type.CRA_0005))).forEach(paymentIt -> {
				extraAssociated.addItem(paymentIt.getDescription(), paymentIt.getId().toString());
			});
		
		extraAssociated.addChangeHandler(e -> fillExtraAssociated());
	}
	
	private void fillExtraAssociated() {
		if(AonStringUtils.isBlank(extraAssociated.getSelectedValue())) {
			extraAssociatedPanel.setVisible(false);
			associatedExtra = null;
		} else if(AonStringUtils.equalsIgnoreCase(extraAssociated.getSelectedValue(), "new")) { 
			extraAssociatedPanel.setVisible(true);
			
			if(null == extra) createExtra();
			
			if(!AonStringUtils.equalsIgnoreCase(extraType.getSelectedValue(), "Prorrateada")) {
				associatedExtra = new AgreementExtra();
				associatedExtra.setIssueDate(AonStringUtils.containsIgnoreCase(extraIssueDate.getValue(), "12") ? "30/06" : "31/12");
				if(AonStringUtils.equalsIgnoreCase(extraType.getSelectedValue(), "Anual")) {
					associatedExtra.setStartDate(AonStringUtils.containsIgnoreCase(extraIssueDate.getValue(), "12") ? "01/07 -1" : "01/01");
					associatedExtra.setEndDate(AonStringUtils.containsIgnoreCase(extraIssueDate.getValue(), "12") ? "30/06" : "31/12");
				} else {
					associatedExtra.setStartDate(AonStringUtils.containsIgnoreCase(extraIssueDate.getValue(), "12") ? "01/01" : "01/07");
					associatedExtra.setEndDate(AonStringUtils.containsIgnoreCase(extraIssueDate.getValue(), "12") ? "30/06" : "31/12");
				}
			}
			
		}else {
			extraAssociatedPanel.setVisible(true);
			
			if(null == extra) createExtra();
			
			int extraAssociatedId = Integer.parseInt(extraAssociated.getSelectedValue());
			Optional<AgreementExtra> extraAssoc = allExtras.stream().filter(extraIt -> extraIt.getId().equals(extraAssociatedId)).findFirst();
			
			if(extraAssoc.isPresent())
				associatedExtra = extraAssoc.get();
			else {
				associatedExtra = new AgreementExtra();
				associatedExtra.setIssueDate(AonStringUtils.containsIgnoreCase(extraIssueDate.getValue(), "12") ? "30/06" : "31/12");
				if(AonStringUtils.equalsIgnoreCase(extraType.getSelectedValue(), "Anual")) {
					associatedExtra.setStartDate(AonStringUtils.containsIgnoreCase(extraIssueDate.getValue(), "12") ? "01/07 -1" : "01/01");
					associatedExtra.setEndDate(AonStringUtils.containsIgnoreCase(extraIssueDate.getValue(), "12") ? "30/06" : "31/12");
				} else {
					associatedExtra.setStartDate(AonStringUtils.containsIgnoreCase(extraIssueDate.getValue(), "12") ? "01/01" : "01/07");
					associatedExtra.setEndDate(AonStringUtils.containsIgnoreCase(extraIssueDate.getValue(), "12") ? "30/06" : "31/12");
				}
			}
			
		}
		
		if(null != associatedExtra && !AonStringUtils.equalsIgnoreCase(extraType.getSelectedValue(), "Prorrateada")) {
			String startMonth = associatedExtra.getStartDate();
			if(AonStringUtils.isNotBlank(startMonth) && AonStringUtils.containsIgnoreCase(startMonth, "-1")) startMonth = startMonth.split(" ")[0];
			if(AonStringUtils.containsIgnoreCase(startMonth, "/")) startMonth = startMonth.split("/")[1];
			if(startMonth.length() < 2) startMonth = AonStringUtils.leftPad(startMonth, 2, '0');
			
			setSelectedValueLB(extraStartDateMonthAssociated, startMonth);
			extraStartDateMonthAssociated.setEnabled(false);
			extraStartDateYearAssociated.setSelectedIndex((AonStringUtils.isNotBlank(extra.getStartDate()) && AonStringUtils.containsIgnoreCase(extra.getStartDate(), "-1")) ? 1 : 0);
			extraStartDateYearAssociated.setEnabled(false);
			
			String endMonth = associatedExtra.getEndDate();
			if(AonStringUtils.isNotBlank(endMonth) && AonStringUtils.containsIgnoreCase(endMonth, "-1")) endMonth = endMonth.split(" ")[0];
			if(AonStringUtils.containsIgnoreCase(endMonth, "/")) endMonth = endMonth.split("/")[1];
			if(endMonth.length() < 2) endMonth = AonStringUtils.leftPad(endMonth, 2, '0');
			
			setSelectedValueLB(extraEndDateMonthAssociated, endMonth);
			extraEndDateMonthAssociated.setEnabled(false);
			extraEndDateYearAssociated.setSelectedIndex((AonStringUtils.isNotBlank(extra.getEndDate()) && AonStringUtils.containsIgnoreCase(extra.getEndDate(), "-1")) ? 1 : 0);
			extraEndDateYearAssociated.setEnabled(false);
			
			extraIssueDateAssociated.setValue(associatedExtra.getIssueDate());
			extraIssueDateAssociated.setEnabled(false);
		}
	}

	private Payment getPaymentById(Integer paymentId) {
		return allPayments.stream().filter(agreementPayment -> agreementPayment.getId().equals(paymentId)).findFirst().get();
	}

	private void initializeExtraPanel() {
		initializeExtraListBoxes();
		extraPanel.setVisible(null != extra || payment.getType().equals(Type.CRA_0004) || payment.getType().equals(Type.CRA_0005));
		extraIssueDate.getElement().setPropertyString("placeholder", "dd/mm");
	}
	
	private void initializeExtraListBoxes() {
		extraType.clear();
		extraType.addItem("Anual", "Anual");
		extraType.addItem("Semestral", "Semestral");
		extraType.addItem("Prorrateada", "Prorrateada");
		
		extraStartDateMonth.clear();
		extraStartDateMonth.addItem("Ene.", "01");
		extraStartDateMonth.addItem("Feb.", "02");
		extraStartDateMonth.addItem("Mar.", "03");
		extraStartDateMonth.addItem("Abr.", "04");
		extraStartDateMonth.addItem("May.", "05");
		extraStartDateMonth.addItem("Jun.", "06");
		extraStartDateMonth.addItem("Jul.", "07");
		extraStartDateMonth.addItem("Ago.", "08");
		extraStartDateMonth.addItem("Sep.", "09");
		extraStartDateMonth.addItem("Oct.", "10");
		extraStartDateMonth.addItem("Nov.", "11");
		extraStartDateMonth.addItem("Dic.", "12");
		
		extraStartDateMonthAssociated.clear();
		extraStartDateMonthAssociated.addItem("Ene.", "01");
		extraStartDateMonthAssociated.addItem("Feb.", "02");
		extraStartDateMonthAssociated.addItem("Mar.", "03");
		extraStartDateMonthAssociated.addItem("Abr.", "04");
		extraStartDateMonthAssociated.addItem("May.", "05");
		extraStartDateMonthAssociated.addItem("Jun.", "06");
		extraStartDateMonthAssociated.addItem("Jul.", "07");
		extraStartDateMonthAssociated.addItem("Ago.", "08");
		extraStartDateMonthAssociated.addItem("Sep.", "09");
		extraStartDateMonthAssociated.addItem("Oct.", "10");
		extraStartDateMonthAssociated.addItem("Nov.", "11");
		extraStartDateMonthAssociated.addItem("Dic.", "12");
		
		extraStartDateYear.clear();
		extraStartDateYear.addItem("A\u00f1o en curso", "");
		extraStartDateYear.addItem("A\u00f1o anterior", " -1");
		
		extraStartDateYearAssociated.clear();
		extraStartDateYearAssociated.addItem("A\u00f1o en curso", "");
		extraStartDateYearAssociated.addItem("A\u00f1o anterior", " -1");
		
		extraEndDateMonth.clear();
		extraEndDateMonth.addItem("Ene.", "01");
		extraEndDateMonth.addItem("Feb.", "02");
		extraEndDateMonth.addItem("Mar.", "03");
		extraEndDateMonth.addItem("Abr.", "04");
		extraEndDateMonth.addItem("May.", "05");
		extraEndDateMonth.addItem("Jun.", "06");
		extraEndDateMonth.addItem("Jul.", "07");
		extraEndDateMonth.addItem("Ago.", "08");
		extraEndDateMonth.addItem("Sep.", "09");
		extraEndDateMonth.addItem("Oct.", "10");
		extraEndDateMonth.addItem("Nov.", "11");
		extraEndDateMonth.addItem("Dic.", "12");
		
		extraEndDateMonthAssociated.clear();
		extraEndDateMonthAssociated.addItem("Ene.", "01");
		extraEndDateMonthAssociated.addItem("Feb.", "02");
		extraEndDateMonthAssociated.addItem("Mar.", "03");
		extraEndDateMonthAssociated.addItem("Abr.", "04");
		extraEndDateMonthAssociated.addItem("May.", "05");
		extraEndDateMonthAssociated.addItem("Jun.", "06");
		extraEndDateMonthAssociated.addItem("Jul.", "07");
		extraEndDateMonthAssociated.addItem("Ago.", "08");
		extraEndDateMonthAssociated.addItem("Sep.", "09");
		extraEndDateMonthAssociated.addItem("Oct.", "10");
		extraEndDateMonthAssociated.addItem("Nov.", "11");
		extraEndDateMonthAssociated.addItem("Dic.", "12");
		
		extraEndDateYear.clear();
		extraEndDateYear.addItem("A\u00f1o en curso", "");
		extraEndDateYear.addItem("A\u00f1o anterior", " -1");
		
		extraEndDateYearAssociated.clear();
		extraEndDateYearAssociated.addItem("A\u00f1o en curso", "");
		extraEndDateYearAssociated.addItem("A\u00f1o anterior", " -1");
		
		extraType.addChangeHandler(e -> updateExtraDates());
		
		extraIssueDate.addValueChangeHandler(e -> updateExtraDates());
	}

	private void updateExtraDates() {
		String extraTypeValue = extraType.getSelectedValue();
		
		if(AonStringUtils.equalsIgnoreCase(extraTypeValue, "Prorrateada")) {
			extraStartDateMonth.setEnabled(false);
			extraStartDateYear.setEnabled(false);
			
			extraEndDateMonth.setEnabled(false);
			extraEndDateYear.setEnabled(false);
			
			extraIssueDate.setValue("");
			extraIssueDate.setEnabled(false);
			
			extraIssueDateAssociated.setValue("");
		} else {
			extraStartDateYear.setEnabled(true);
			extraEndDateYear.setEnabled(true);
			extraStartDateMonth.setEnabled(true);
			extraEndDateMonth.setEnabled(true);
			extraIssueDate.setEnabled(true);
			
			String issueDate = extraIssueDate.getValue();
			if(AonStringUtils.isBlank(issueDate)) return;
		
			int issueMonth = Integer.parseInt(issueDate.split("/")[1]);
		
			switch (issueMonth) {
				case 12:
					updateWinterValues();
					break;
				case 7:
					updateSummerValues();
					break;
				case 6:
					updateSummerValues();
					break;
				default:
					break;
			}
		}
	}

	private void updateSummerValues() {
		String extraTypeValue = extraType.getSelectedValue();
		if(AonStringUtils.equalsIgnoreCase(extraTypeValue, "Anual")) {
			setSelectedValueLB(extraStartDateMonth, "07");
			extraStartDateYear.setSelectedIndex(1);
			
			setSelectedValueLB(extraEndDateMonth, "06");
			extraEndDateYear.setSelectedIndex(0);

			setSelectedValueLB(extraStartDateMonthAssociated, "01");
			extraStartDateYearAssociated.setSelectedIndex(0);
			
			setSelectedValueLB(extraEndDateMonthAssociated, "12");
			extraEndDateYearAssociated.setSelectedIndex(0);
			
			extraIssueDateAssociated.setValue("31/12");
		} else if(AonStringUtils.equalsIgnoreCase(extraTypeValue, "Semestral")) {
			setSelectedValueLB(extraStartDateMonth, "01");
			extraStartDateYear.setSelectedIndex(0);
			
			setSelectedValueLB(extraEndDateMonth, "06");
			extraEndDateYear.setSelectedIndex(0);
			
			setSelectedValueLB(extraStartDateMonthAssociated, "07");
			extraStartDateYearAssociated.setSelectedIndex(0);
			
			setSelectedValueLB(extraEndDateMonthAssociated, "12");
			extraEndDateYearAssociated.setSelectedIndex(0);
			
			extraIssueDateAssociated.setValue("31/12");
		}
		
	}

	private void updateWinterValues() {
		String extraTypeValue = extraType.getSelectedValue();
		if(AonStringUtils.equalsIgnoreCase(extraTypeValue, "Anual")) {
			setSelectedValueLB(extraStartDateMonth, "01");
			extraStartDateYear.setSelectedIndex(0);
			
			setSelectedValueLB(extraEndDateMonth, "12");
			extraEndDateYear.setSelectedIndex(0);
			
			setSelectedValueLB(extraStartDateMonthAssociated, "07");
			extraStartDateYearAssociated.setSelectedIndex(1);
			
			setSelectedValueLB(extraEndDateMonthAssociated, "06");
			extraEndDateYearAssociated.setSelectedIndex(0);
			
			extraIssueDateAssociated.setValue("30/06");
		} else if(AonStringUtils.equalsIgnoreCase(extraTypeValue, "Semestral")) {
			setSelectedValueLB(extraStartDateMonth, "07");
			extraStartDateYear.setSelectedIndex(0);
			
			setSelectedValueLB(extraEndDateMonth, "12");
			extraEndDateYear.setSelectedIndex(0);
			
			setSelectedValueLB(extraStartDateMonthAssociated, "01");
			extraStartDateYearAssociated.setSelectedIndex(0);
			
			setSelectedValueLB(extraEndDateMonthAssociated, "06");
			extraEndDateYearAssociated.setSelectedIndex(0);
			
			extraIssueDateAssociated.setValue("30/06");
		}
	}

	// ----------------------------------------- Provided Payment

	private void providedPayment() {
		initializePaymentType();
		initializeSB(paymentConceptSB, paymentDescriptionTB, paymentExpressionTB);
		initializeTaxed();
		initializeQuote();
	}

	private void initializePaymentType() {
		paymentTypeLB = new TypeListBox<>(Payment.Type.class, 10);
		paymentTypeLB.setSelected(Payment.Type.DEFAULT);
		paymentTypeLB.addStyleName("aon-selectOneMenu");
		paymentTypeLB.getElement().getStyle().setWidth(100, Unit.PCT);
		paymentTypeLB.setHeight("1.5rem");
		paymentTypeLB.addChangeHandler(e -> initializeTaxed());
		paymentTypePanel.clear();
		paymentTypePanel.add(paymentTypeLB);
	}

	private void initializeTaxed() {
		paymentTaxedTypeLB.clear();
		paymentTaxedTypeLB.addItem("Importe integro", "FULL");
		paymentTaxedTypeLB.addItem("Exento", "NONE");
		paymentTaxedTypeLB.addItem("Personalizado", "CUSTOM");
		
		if(paymentTypeLB.getSelected().ordinal() >= 13 && paymentTypeLB.getSelected().ordinal() <= 26)
			paymentTaxedTypeLB.addItem("Ingreso a Cuenta", "IRPF_CTA_ESP");

		paymentTaxedTypeLB.addChangeHandler(e -> {
			int selected = paymentTaxedTypeLB.getSelectedIndex();
			switch (selected) {
			case 0:
				paymentTaxedExpression.setEnabled(false);
				paymentTaxedExpression.setValue("Importe integro");
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
				paymentQuoteExpression.setValue("Importe integro");
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

	// ----------------------------------------- SuggestBox

	private void initializeSB(SuggestBox suggestBox, TextBox descriptionTB, TextArea expressionTB) {
		List<String> conceptsSuggest = new ArrayList<>();
		for (ContractConcept contractConcept : contractConcepts.getPaymentConcepts()) {
			String suggestDisplay = AonStringUtils.isBlank(contractConcept.getCode()) ? ""
					: contractConcept.getCode() + " - ";
			suggestDisplay += contractConcept.getDescription();
			conceptsSuggest.add(suggestDisplay);
		}

		MultiWordSuggestOracle orclConcepts = (MultiWordSuggestOracle) suggestBox.getSuggestOracle();
		orclConcepts.clear();
		orclConcepts.addAll(conceptsSuggest);
		suggestBox.setAutoSelectEnabled(false);
		suggestBox.addValueChangeHandler(value -> {
			selectedConcept = getConcept(suggestBox);

			if (null != selectedConcept) {
				descriptionTB.setValue(selectedConcept.getDescription());
				expressionTB.setValue(selectedConcept.getExpression());
			}
		});
	}

	private ContractConcept getConcept(SuggestBox suggestBox) {
		Set<ContractConcept> concepts = contractConcepts.getPaymentConcepts();
		String code = null;
		String description = null;

		if (suggestBox.getValue().contains(" - ")) {
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

	// ----------------------------------------- Fill Payment
	
	public void setContextProvider(IContextProvider context) {
		this.context = context;
	}

	private void fillPayment() {
		paymentTypeLB.setSelected(this.payment.getType());
		paymentConceptSB.setValue(this.payment.getName());
		paymentDescriptionTB.setValue(this.payment.getDescription());
		paymentExpressionTB.setValue(getParsedExpression(this.payment.getExpression()));
		checkSeniorityExpresion();
		expresssionVisibilityBtn.addClickHandler(e -> {
			final FxDialog fxDialog = new FxDialog(context);
			fxDialog.setExpression(getExpression(this.payment.getExpression()));
			fxDialog.center();
			fxDialog.show();
		
			fxDialog.addCloseHandler(ev -> {
				if (fxDialog.isAccepted()) {
					payment.setExpression(fxDialog.getExpression());
					paymentExpressionTB.setValue(getParsedExpression(payment.getExpression()));
				}
			});
		});
		setSelectedValueLB(paymentTaxedTypeLB, getTaxedQuoteType(this.payment.getIrpfExpression()));
		String irpfExpression = this.payment.getIrpfExpression();
		paymentTaxedExpression.setValue(AonStringUtils.isNotBlank(irpfExpression) && AonStringUtils.equalsIgnoreCase(irpfExpression, "_P") ? "Importe integro" : irpfExpression);
		
		setSelectedValueLB(paymentQuoteTypeLB, getTaxedQuoteType(this.payment.getQuoteExpression()));
		String quoteExpression = this.payment.getQuoteExpression();
		paymentQuoteExpression.setValue(AonStringUtils.isNotBlank(quoteExpression) && AonStringUtils.equalsIgnoreCase(quoteExpression, "_P") ? "Importe integro" : quoteExpression);
		
		startDateBx.setValue(this.payment.getStartDate());
		endDateBx.setValue(this.payment.getEndDate());
	}

	private String getParsedExpression(String expression) {
		expression = AonStringUtils.isBlank(expression) ? expression : expression.replaceAll("HIDE\\(.*\\); ", "");
		return SpecialExpresion.parse(expression).getInput();
	}
	
	private String getExpression(String expression) {
		expression = AonStringUtils.isBlank(expression) ? expression : expression.replaceAll("HIDE\\(.*\\); ", "");
		return SpecialExpresion.parse(expression).getExpression();
	}
	
	private void checkSeniorityExpresion() {
		String expression = this.payment.getExpression();
		seniorityPanel.setVisible(AonStringUtils.isNotBlank(expression) && expression.contains("ANTIG") && expression.contains("EDAD"));
		
		setSelectedValueLB(seniorityType, getSeniority());
	}
	
	private void fillExtra() {
		setSelectedValueLB(extraType, getExtraType(extra));
		DomEvent.fireNativeEvent(Document.get().createChangeEvent(), extraType);
		
		String startMonth = extra.getStartDate();
		if(AonStringUtils.isNotBlank(startMonth)) {
			if(AonStringUtils.containsIgnoreCase(startMonth, "-1")) startMonth = startMonth.split(" ")[0];
			if(AonStringUtils.containsIgnoreCase(startMonth, "/")) startMonth = startMonth.split("/")[1];
			if(startMonth.length() < 2) startMonth = AonStringUtils.leftPad(startMonth, 2, '0');
		}
		
		setSelectedValueLB(extraStartDateMonth, startMonth);
		extraStartDateYear.setSelectedIndex((AonStringUtils.isNotBlank(extra.getStartDate()) && AonStringUtils.containsIgnoreCase(extra.getStartDate(), "-1")) ? 1 : 0);
		
		String endMonth = extra.getEndDate();
		if(AonStringUtils.isNotBlank(startMonth)) {
			if(AonStringUtils.containsIgnoreCase(endMonth, "-1")) endMonth = endMonth.split(" ")[0];
			if(AonStringUtils.containsIgnoreCase(endMonth, "/")) endMonth = endMonth.split("/")[1];
			if(endMonth.length() < 2) endMonth = AonStringUtils.leftPad(endMonth, 2, '0');
		}
		
		setSelectedValueLB(extraEndDateMonth, endMonth);
		extraEndDateYear.setSelectedIndex((AonStringUtils.isNotBlank(extra.getEndDate()) && AonStringUtils.containsIgnoreCase(extra.getEndDate(), "-1")) ? 1 : 0);
		
		extraIssueDate.setValue(extra.getIssueDate());
		
		chekExtraAssociated();
	}
	
	private void chekExtraAssociated() {
		String issueDate = extra.getIssueDate();
		if(AonStringUtils.isBlank(issueDate) || extra.isDeleted()) extraAssociated.setSelectedIndex(0);
		else {
			if(AonStringUtils.containsIgnoreCase(issueDate, "6") || AonStringUtils.containsIgnoreCase(issueDate, "7")) {
				AgreementExtra winterExtra = findWinterExtra();
				if(null == winterExtra) extraAssociated.setSelectedIndex(0);
				else setSelectedValueLB(extraAssociated, null == winterExtra ? null : winterExtra.getId().toString());
			} else if(AonStringUtils.containsIgnoreCase(issueDate, "12")) {
				AgreementExtra summerExtra = findSummerExtra();
				if(null == summerExtra) extraAssociated.setSelectedIndex(0);
				else setSelectedValueLB(extraAssociated, null == summerExtra ? null : summerExtra.getId().toString());
			}
		}
	}

	private AgreementExtra findSummerExtra() {
		String expression = payment.getExpression();
		if(expression.contains(" ")) {
			String[] expressions = expression.split(" ");
			expression = "";
			for(int i=0; i<expressions.length; i++)
				if(i!=0) expression+= expressions[i] + " ";
		}
		
		for(AgreementExtra extraIt : allExtras)
			if((AonStringUtils.containsIgnoreCase(extraIt.getIssueDate(), "6") || AonStringUtils.containsIgnoreCase(extraIt.getIssueDate(), "7")) && isSameExpression(extraIt, expression))
				return extraIt;
		
		return null;
	}

	private AgreementExtra findWinterExtra() {
		String expression = payment.getExpression();
		if(expression.contains(" ")) {
			String[] expressions = expression.split(" ");
			expression = "";
			for(int i=0; i<expressions.length; i++)
				if(i!=0) expression+= expressions[i] + " ";
		}
		
		for(AgreementExtra extraIt : allExtras)
			if(AonStringUtils.containsIgnoreCase(extraIt.getIssueDate(), "12") && isSameExpression(extraIt, expression))
				return extraIt;
		
		return null;
	}

	private boolean isSameExpression(AgreementExtra extraIt, String expression) {
		Optional<Payment> paymentIt = allPayments.stream().filter(payment -> payment.getId().equals(extraIt.getAgreementPayment())).findFirst();
		return paymentIt.isPresent() && AonStringUtils.containsIgnoreCase(paymentIt.get().getExpression(), expression.trim()) ? true : false;
	}

	private String getExtraType(AgreementExtra extra) {
		if(null == extra || AonStringUtils.isBlank(extra.getIssueDate()) || extra.isDeleted()) return "Prorrateada";
		if(AonStringUtils.containsIgnoreCase(extra.getStartDate(), "-1")) return "Anual";
		
		try {
			int startMonth = Integer.parseInt(extra.getStartDate().split("/")[1]);
			int endMonth = Integer.parseInt(extra.getEndDate().split("/")[1]);
			
			switch (endMonth - startMonth) {
				case 11:
					return "Anual";
				case 5:
					return "Semestral";
				default:
					return "Prorrateada";
			}
		} catch (Exception e) {
			return "Prorrateada";
		}
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

		Button closeBtnDialog = new Button();
		closeBtnDialog.setStyleName(AON.CSS.aonCancelButtonSmall());
		closeBtnDialog.setText( AON.MSG.cancelAction());
		closeBtnDialog.addClickHandler(e -> hide());
		
		buttonsPanel.add(closeBtnDialog);

		acceptDialog = new Button();
		acceptDialog.setStyleName(AON.CSS.aonOkButtonSmall());
		acceptDialog.setText(AON.MSG.accept());
		acceptDialog.addClickHandler(e -> {
			hide();
			// Seniority
			if(seniorityPanel.isVisible()) onSeniority(seniorityType.getSelectedValue());
			
			// Payment & extra
			createPayment();
			
			if(payment.getType().equals(Type.CRA_0004) || payment.getType().equals(Type.CRA_0005)) {
				createExtra();
				createAssociatedExtra();
			}
			onAccept(payment, extra, associatedPayment, associatedExtra);
		});

		buttonsPanel.add(acceptDialog);
	}
	
	private void createPayment() {
		if(null == this.payment || null == this.payment.getId()) {
			this.payment = new Payment();
			Random rand = new Random();
			int newPaymentId = rand.nextInt(1000) * -1;
			if(newPaymentId > 0) newPaymentId = newPaymentId * -1;
			this.payment.setId(newPaymentId);
		}
		
		payment.setType(paymentTypeLB.getSelected());
		if (null != selectedConcept) {
			payment.setConceptId(selectedConcept.getId());
			payment.setName(selectedConcept.getCode());
		} else {
			payment.setConceptId(null);
			payment.setName(paymentConceptSB.getValue());
		}
		
		payment.setDescription(paymentDescriptionTB.getValue());
		payment.setExpression(paymentExpressionTB.getValue());
		
		String irpfExpression = paymentTaxedExpression.getValue();
		String quoteExpression = paymentQuoteExpression.getValue();
		
		payment.setIrpfExpression(AonStringUtils.isNotBlank(irpfExpression) && AonStringUtils.equalsIgnoreCase(irpfExpression, "Importe integro") ? "_P" : irpfExpression);
		payment.setQuoteExpression(AonStringUtils.isNotBlank(quoteExpression) && AonStringUtils.equalsIgnoreCase(quoteExpression, "Importe integro") ? "_P" : quoteExpression);
		payment.setMonth(null);
		
		payment.setStartDate(startDateBx.getValue());
		payment.setEndDate(endDateBx.getValue());
	}
	
	private void createExtra() {
		if(null == this.extra || null == this.extra.getId()) {
			this.extra = new AgreementExtra();
			Random rand = new Random();
			int newExtraId = rand.nextInt(1000) * -1;
			this.extra.setId(newExtraId);
			this.extra.setAgreementPayment(payment.getId());
		}
		
		String extraTypeValue = extraType.getSelectedValue();
		extra.setDeleted(AonStringUtils.equalsIgnoreCase(extraTypeValue, "Prorrateada"));
		if(!extra.isDeleted()) {
			String startMonth = extraStartDateMonth.getSelectedValue();
			if(AonStringUtils.equalsIgnoreCase(startMonth, "01")) startMonth = "01/01";
			else if(AonStringUtils.equalsIgnoreCase(startMonth, "07")) startMonth = "01/07";
			extra.setStartDate(startMonth + extraStartDateYear.getSelectedValue());
			
			String endMonth = extraEndDateMonth.getSelectedValue();
			if(AonStringUtils.equalsIgnoreCase(endMonth, "06")) endMonth = "30/06";
			else if(AonStringUtils.equalsIgnoreCase(endMonth, "12")) endMonth = "31/12";
			extra.setEndDate(endMonth + extraEndDateYear.getSelectedValue());
			extra.setIssueDate(extraIssueDate.getValue());
		}
		
		if(AonStringUtils.isNotBlank(extraIssueDate.getValue())) {
			String issueDate = extraIssueDate.getValue();
			Short month = Short.parseShort(issueDate.split("/")[1]);
			month--;
			payment.setMonth(month);
		} else
			payment.setMonth(null);
	}
	
	private void createAssociatedExtra() {
		String extraAssociatedValue = extraAssociated.getSelectedValue();
		if(!AonStringUtils.isBlank(extraAssociatedValue) && AonStringUtils.equalsIgnoreCase(extraAssociatedValue, "new")) {
			createAssociatedPayment();
			
			this.associatedExtra = new AgreementExtra();
			String extraTypeValue = extraType.getSelectedValue();
			associatedExtra.setDeleted(AonStringUtils.equalsIgnoreCase(extraTypeValue, "Prorrateada"));
			if(!associatedExtra.isDeleted()) {
				Random rand = new Random();
				int newExtraId = rand.nextInt(1000) * -1;
				this.associatedExtra.setId(newExtraId);
				this.associatedExtra.setAgreementPayment(associatedPayment.getId());
	
				String startMonth = extraStartDateMonthAssociated.getSelectedValue();
				if(AonStringUtils.equalsIgnoreCase(startMonth, "01")) startMonth = "01/01";
				else if(AonStringUtils.equalsIgnoreCase(startMonth, "07")) startMonth = "01/07";
				associatedExtra.setStartDate(startMonth + extraStartDateYearAssociated.getSelectedValue());
				
				String endMonth = extraEndDateMonthAssociated.getSelectedValue();
				if(AonStringUtils.equalsIgnoreCase(endMonth, "06")) endMonth = "30/06";
				else if(AonStringUtils.equalsIgnoreCase(endMonth, "12")) endMonth = "31/12";
				associatedExtra.setEndDate(endMonth + extraEndDateYearAssociated.getSelectedValue());
				associatedExtra.setIssueDate(extraIssueDateAssociated.getValue());
			}
			
			if(AonStringUtils.isNotBlank(extraIssueDateAssociated.getValue())) {
				String issueDate = extraIssueDateAssociated.getValue();
				Short month = Short.parseShort(issueDate.split("/")[1]);
				month--;
				associatedPayment.setMonth(month);
			} else
				associatedPayment.setMonth(null);
			
		} else if(!AonStringUtils.isBlank(extraAssociatedValue)) {
			if(null == associatedExtra.getId()) {
				Random rand = new Random();
				int newExtraId = rand.nextInt(1000) * -1;
				this.associatedExtra.setId(newExtraId);
			}
			
			String extraTypeValue = extraType.getSelectedValue();
			associatedExtra.setDeleted(AonStringUtils.equalsIgnoreCase(extraTypeValue, "Prorrateada"));
			
			if(!associatedExtra.isDeleted()) {
				String startMonth = extraStartDateMonthAssociated.getSelectedValue();
				if(AonStringUtils.equalsIgnoreCase(startMonth, "01")) startMonth = "01/01";
				else if(AonStringUtils.equalsIgnoreCase(startMonth, "07")) startMonth = "01/07";
				associatedExtra.setStartDate(startMonth + extraStartDateYearAssociated.getSelectedValue());
				
				String endMonth = extraEndDateMonthAssociated.getSelectedValue();
				if(AonStringUtils.equalsIgnoreCase(endMonth, "06")) endMonth = "30/06";
				else if(AonStringUtils.equalsIgnoreCase(endMonth, "12")) endMonth = "31/12";
				associatedExtra.setEndDate(endMonth + extraEndDateYearAssociated.getSelectedValue());
				associatedExtra.setIssueDate(extraIssueDateAssociated.getValue());
			}
			
			if(null == associatedExtra.getAgreementPayment() && !AonStringUtils.isBlank(extraAssociated.getSelectedValue())) {
				int extraAssociatedId = Integer.parseInt(extraAssociated.getSelectedValue());
				
				Optional<Payment> paymentAux = allPayments.stream().filter(paymentIt -> null !=  paymentIt.getId() && paymentIt.getId().equals(extraAssociatedId)).findFirst();
				
				if(paymentAux.isPresent()) {
					associatedExtra.setAgreementPayment(paymentAux.get().getId());
					paymentAux.get().setExpression(paymentExpressionTB.getText());
					
					if(!AonStringUtils.equalsIgnoreCase(extraTypeValue, "Prorrateada")) {
						String issueDate = extraIssueDateAssociated.getValue();
						Short month = Short.parseShort(issueDate.split("/")[1]);
						month--;
						paymentAux.get().setMonth(month);
					} else
						paymentAux.get().setMonth(null);
				}
			}
		}
	}

	private void createAssociatedPayment() {
		this.associatedPayment = new Payment();
		Random rand = new Random();
		int newPaymentId = rand.nextInt(1000) * -1;
		if(newPaymentId > 0) newPaymentId = newPaymentId * -1;
		this.associatedPayment.setId(newPaymentId);
		this.associatedPayment.setModify(true);
		
		associatedPayment.setType(paymentTypeLB.getSelected());
		if (null != selectedConcept) {
			associatedPayment.setConceptId(selectedConcept.getId());
			associatedPayment.setName(selectedConcept.getCode());
		} else
			associatedPayment.setName(paymentConceptSB.getValue());
		
		associatedPayment.setDescription(AonStringUtils.containsIgnoreCase(payment.getDescription(), "verano") ? "PAGA NAVIDAD" : "PAGA VERNAO");
		associatedPayment.setExpression(paymentExpressionTB.getValue());
		
		String irpfExpression = paymentTaxedExpression.getValue();
		String quoteExpression = paymentQuoteExpression.getValue();
		
		payment.setIrpfExpression(AonStringUtils.isNotBlank(irpfExpression) && AonStringUtils.equalsIgnoreCase(irpfExpression, "Importe integro") ? "_P" : irpfExpression);
		payment.setQuoteExpression(AonStringUtils.isNotBlank(quoteExpression) && AonStringUtils.equalsIgnoreCase(quoteExpression, "Importe integro") ? "_P" : quoteExpression);
		associatedPayment.setMonth(null);
		
		payment.setStartDate(startDateBx.getValue());
		payment.setEndDate(endDateBx.getValue());
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

	protected abstract void onAccept(Payment payment, AgreementExtra extra, Payment associatedPayment, AgreementExtra associatedExtra);
	protected abstract void onSeniority(String seniorityExpression);
	protected abstract String getSeniority();

}
