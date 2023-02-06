package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarSmallButton;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.client.AgreementDraft.TypeListBox;
import com.esferalia.aon.gwt.payroll.client.FxDialog.IContextProvider;
import com.esferalia.aon.gwt.payroll.shared.AgreementInfo.AgreementExtra;
import com.esferalia.aon.gwt.payroll.shared.ContextDescriptor;
import com.esferalia.aon.gwt.payroll.shared.ContractConcept;
import com.esferalia.aon.gwt.payroll.shared.ContractConcepts;
import com.esferalia.aon.gwt.payroll.shared.Payment;
import com.esferalia.aon.gwt.payroll.shared.Payment.Type;
import com.esferalia.aon.gwt.payroll.shared.Result;
import com.esferalia.aon.gwt.payroll.shared.SpecialExpresion;
import com.esferalia.aon.gwt.payroll.shared.Variable;
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
import com.google.gwt.user.client.ui.DisclosurePanel;
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
	
	// ----------------------------------------- ContextProvider
	
	public class ContextProvider implements IContextProvider {

		@Override
		public boolean isEditable(String name) {
			for (Payment payment : allPayments)
				if (AonStringUtils.equals(payment.getName(), name))
					return false;
			return true;
		}

		@Override
		public void getContext(AsyncCallback<ContextDescriptor> callback) {
			employeeService.getAgreementContext(fxLevel, startDate, DateUtils.getLastDayOfYear(startDate), callback);
		}

		@Override
		public void eval(String expression, List<Variable> vars, AsyncCallback<List<Result>> callback) {
			employeeService.evalAgreement(expression, startDate, fxLevel, callback);
		}

	}

	// ----------------------------------------- UiFields

	// PAYMENT

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
	DisclosurePanel advancePanel;
	
	@UiField
	HTMLPanel paymentTaxedPanel;

	@UiField
	ListBox paymentTaxedTypeLB;

	@UiField
	TextBox paymentTaxedExpression;
	
	@UiField
	HTMLPanel enterpriseTaxedPanel;
	
	@UiField
	Button enterpriseTaxed;
	
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
	HTMLPanel buttonsPanel;

	// ----------------------------------------- Variables

	private DomainEnterprisesServiceAsync enterpriseService = DomainEnterprisesServiceAsync.newInstance();
	private DomainEmployeesServiceAsync employeeService = DomainEmployeesServiceAsync.newInstance();
	private ContractConcepts contractConcepts;
	private ContractConcept selectedConcept;
	private Payment payment;
	private AgreementExtra extra;
	private AgreementExtra associatedExtra;
	private Payment associatedPayment;

	private TypeListBox<Payment.Type> paymentTypeLB;
	
	private Set<Payment> allPayments;
	
	private Button acceptDialog;
	
	private ContextProvider contextProvider;
	
	private int fxLevel = 0;
	private Date startDate;

	// ----------------------------------------- Constructor

	protected AgreementPaymentEditor(Payment payment, AgreementExtra extra, Set<Payment> allPayments, Date startDate) {
		setCaption("Devengo");
		
		expresssionVisibilityBtn = new AonToolbarSmallButton("Editar expresi\u00f3n", AON.CSS.aonIconFx());
		
		setWidget(binder.createAndBindUi(this));
		showCloseButton(true);
		getFooterButtons();
		
		this.startDate = startDate;
		
		this.payment = payment;
		this.extra = extra;
		
		this.allPayments = allPayments;
		
		advancePanel.setAnimationEnabled(true);
		advancePanel.setOpen(false);
		advancePanel.addOpenHandler(e -> {
			showHideAdvanceOptions();
			this.setPopupPosition(this.getAbsoluteLeft(), this.getAbsoluteTop() - 80);
		});
		advancePanel.addCloseHandler(e -> {
			showHideAdvanceOptions();
			this.setPopupPosition(this.getAbsoluteLeft(), this.getAbsoluteTop() + 80);
		});
		
		getEnableDisableButton(enterpriseTaxed, false);
		enterpriseTaxed.addClickHandler(e -> {
			Boolean oldValue = isActiveToggleButton(enterpriseTaxed);
			Boolean value = !oldValue;
			getEnableDisableButton(enterpriseTaxed, value);
			
			if(value) paymentTaxedExpression.setValue("BASE_CTA_ESP=_P");
			else checkQuoteAndTaxedByCra();
		});
		enterpriseTaxedPanel.setVisible(false);
		
		initializeSeniorityPanel();
		
		initializeExtraPanel();
		
		enterpriseService.getAllConcepts(new AsyncCallback<ContractConcepts>() {

			@Override
			public void onSuccess(ContractConcepts contractConceptsIn) {
				contractConcepts = contractConceptsIn;

				providedPayment();
				fillPayment();
				
				checkDatesPanelShown();
				
				if(null != extra && !extra.isDeleted()) fillExtra();
				
				showHideAdvanceOptions();
				showDialog();
			}

			@Override
			public void onFailure(Throwable caught) {
				// Failure
			}

		});
	}

	private void checkDatesPanelShown() {
		Date compareDate1 = new Date(1970 - 1900, 0, 1);
		Date compareDate2 = new Date(2010 - 1900, 0, 1);
		
		periodPaymentPanel.setVisible((!payment.getStartDate().equals(compareDate1) && !payment.getStartDate().equals(compareDate2)) || payment.getEndDate() != null);
	}

	private void initializeSeniorityPanel() {
		seniorityPanel.setVisible(false);
		
		seniorityType.clear();
		seniorityType.addItem("Autom\u00e1tico", "");
		seniorityType.addItem("Inicio mes", "INICIO_MES(INICIO_ANTIGUEDAD)");
		seniorityType.addItem("Inicio a\u00f1o", "INICIO_A\u00d1O(INICIO_ANTIGUEDAD)");
	}

	private void initializeExtraPanel() {
		initializeExtraListBoxes();
		extraPanel.setVisible(null != extra || payment.getType().equals(Type.CRA_0004) || payment.getType().equals(Type.CRA_0005));
		extraIssueDate.getElement().setPropertyString("placeholder", "dd/mm");
	}
	
	private void initializeExtraListBoxes() {
		
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
		
		extraStartDateYear.clear();
		extraStartDateYear.addItem("A\u00f1o en curso", "");
		extraStartDateYear.addItem("A\u00f1o anterior", " -1");
		
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
		
		extraEndDateYear.clear();
		extraEndDateYear.addItem("A\u00f1o en curso", "");
		extraEndDateYear.addItem("A\u00f1o anterior", " -1");
		
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
		paymentTypeLB.addChangeHandler(e -> {
			initializeTaxed();
			checkQuoteAndTaxedByCra();
		});
		paymentTypePanel.clear();
		paymentTypePanel.add(paymentTypeLB);
	}
	
	private void checkQuoteAndTaxedByCra() {
		Type craType = paymentTypeLB.getSelected();
		
		if(paymentTypeLB.getSelected().ordinal() >= 13 && paymentTypeLB.getSelected().ordinal() <= 26)
			enterpriseTaxedPanel.setVisible(true);
		else {
			enterpriseTaxedPanel.setVisible(false);
			getEnableDisableButton(enterpriseTaxed, false);
		}
		
		if(craType.isBBCCIncluded() && !craType.isBBCCExcluded()) {
			
			// Importe integro
			paymentTaxedTypeLB.setSelectedIndex(0);
			DomEvent.fireNativeEvent(Document.get().createChangeEvent(), paymentTaxedTypeLB);
			paymentTaxedTypeLB.setVisible(false);
			
			paymentQuoteTypeLB.setSelectedIndex(0);
			DomEvent.fireNativeEvent(Document.get().createChangeEvent(), paymentQuoteTypeLB);
			paymentQuoteTypeLB.setVisible(false);
			
		} else if(!craType.isBBCCIncluded() && craType.isBBCCExcluded()) {
			
			// Exento
			paymentTaxedTypeLB.setSelectedIndex(1);
			DomEvent.fireNativeEvent(Document.get().createChangeEvent(), paymentTaxedTypeLB);
			paymentTaxedTypeLB.setVisible(false);
			
			paymentQuoteTypeLB.setSelectedIndex(1);
			DomEvent.fireNativeEvent(Document.get().createChangeEvent(), paymentQuoteTypeLB);
			paymentQuoteTypeLB.setVisible(false);
			
		} else if(craType.isBBCCIncluded() && craType.isBBCCExcluded()) {
			
			// Personalizado
			paymentTaxedTypeLB.setSelectedIndex(2);
			DomEvent.fireNativeEvent(Document.get().createChangeEvent(), paymentTaxedTypeLB);
			paymentTaxedTypeLB.setVisible(true);
			
			paymentQuoteTypeLB.setSelectedIndex(3);
			DomEvent.fireNativeEvent(Document.get().createChangeEvent(), paymentQuoteTypeLB);
			paymentQuoteTypeLB.setVisible(true);
			
		}
	}

	private void initializeTaxed() {
		paymentTaxedTypeLB.clear();
		paymentTaxedTypeLB.addItem("Importe integro", "FULL");
		paymentTaxedTypeLB.addItem("Exento", "NONE");
		paymentTaxedTypeLB.addItem("Personalizado", "CUSTOM");
		
		paymentTaxedTypeLB.addChangeHandler(e -> {
			int selected = paymentTaxedTypeLB.getSelectedIndex();
			switch (selected) {
			case 0:
				paymentTaxedExpression.setEnabled(false);
				paymentTaxedExpression.setValue("Importe integro");
				break;
			case 1:
				paymentTaxedExpression.setEnabled(false);
				paymentTaxedExpression.setValue("Exento");
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
				paymentQuoteExpression.setValue("Exento");
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
	
	public void setContextProvider(ContextProvider context) {
//		this.contextProvider = context;
	}

	private void fillPayment() {
		paymentTypeLB.setSelected(this.payment.getType());
		DomEvent.fireNativeEvent(Document.get().createChangeEvent(), paymentTypeLB);
		
		paymentConceptSB.setValue(this.payment.getName());
		paymentDescriptionTB.setValue(this.payment.getDescription());
		paymentExpressionTB.setValue(getParsedExpression(this.payment.getExpression()));
		checkSeniorityExpresion();
		expresssionVisibilityBtn.addClickHandler(e -> {
			if(null == contextProvider)
				contextProvider = new ContextProvider();
			
			final FxDialog fxDialog = new FxDialog(contextProvider);
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
		if(AonStringUtils.isNotBlank(irpfExpression) && AonStringUtils.equalsIgnoreCase(irpfExpression, "_P")) irpfExpression = "Importe integro";
		if(AonStringUtils.isNotBlank(irpfExpression) && AonStringUtils.equalsIgnoreCase(irpfExpression, "0.00")) irpfExpression = "Exento";
		paymentTaxedExpression.setValue(irpfExpression);
		
		setSelectedValueLB(paymentQuoteTypeLB, getTaxedQuoteType(this.payment.getQuoteExpression()));
		
		String quoteExpression = this.payment.getQuoteExpression();
		if(AonStringUtils.isNotBlank(quoteExpression) && AonStringUtils.equalsIgnoreCase(quoteExpression, "_P")) quoteExpression = "Importe integro";
		if(AonStringUtils.isNotBlank(quoteExpression) && AonStringUtils.equalsIgnoreCase(quoteExpression, "0.00")) quoteExpression = "Exento";
		paymentQuoteExpression.setValue(quoteExpression);
		
		startDateBx.setValue(this.payment.getStartDate());
		endDateBx.setValue(this.payment.getEndDate());
		
		getEnableDisableButton(enterpriseTaxed, AonStringUtils.isNotBlank(irpfExpression) && AonStringUtils.equalsIgnoreCase(irpfExpression, "BASE_CTA_ESP=_P"));
		if(isActiveToggleButton(enterpriseTaxed)) paymentTaxedExpression.setValue("BASE_CTA_ESP=_P");
	}

	private String getParsedExpression(String expression) {
		return SpecialExpresion.parse(expression).getInput();
	}
	
	private String getExpression(String expression) {
		return SpecialExpresion.parse(expression).getExpression();
	}
	
	private void checkSeniorityExpresion() {
		String expression = this.payment.getExpression();
		seniorityPanel.setVisible(AonStringUtils.isNotBlank(expression) && expression.contains("ANTIG") && expression.contains("EDAD"));
		
		setSelectedValueLB(seniorityType, getSeniority());
	}
	
	private void fillExtra() {
		
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
			}
			onAccept(payment, extra, associatedPayment, associatedExtra);
		});

		buttonsPanel.add(acceptDialog);
	}

	private void showHideAdvanceOptions() {
		expresssionVisibilityBtn.setVisible(advancePanel.isOpen());
		paymentExpressionTB.setValue(advancePanel.isOpen() ? getExpression(this.payment.getExpression()) : getParsedExpression(this.payment.getExpression()));
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
		if(AonStringUtils.isNotBlank(irpfExpression) && AonStringUtils.equalsIgnoreCase(irpfExpression, "Importe integro")) irpfExpression = "_P";
		if(AonStringUtils.isNotBlank(irpfExpression) && AonStringUtils.equalsIgnoreCase(irpfExpression, "Exento")) irpfExpression = "0.00";
		
		String quoteExpression = paymentQuoteExpression.getValue();
		if(AonStringUtils.isNotBlank(quoteExpression) && AonStringUtils.equalsIgnoreCase(quoteExpression, "Importe integro")) quoteExpression = "_P";
		if(AonStringUtils.isNotBlank(quoteExpression) && AonStringUtils.equalsIgnoreCase(quoteExpression, "Exento")) quoteExpression = "0.00";
		
		if(payment.getType().ordinal() >= 13 && payment.getType().ordinal() <= 26 && isActiveToggleButton(enterpriseTaxed))
			irpfExpression = "BASE_CTA_ESP=_P";
			
		payment.setIrpfExpression(irpfExpression);
		payment.setQuoteExpression(quoteExpression);
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
		
		extra.setDeleted(AonStringUtils.isBlank(extraIssueDate.getValue()));
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
	
	// ----------------------------------------- ToogleButton
	
	private void getEnableDisableButton(Button button, boolean disabled) {
		button.removeStyleName(disabled ? AON.AON_ICON_DISABLE : AON.AON_ICON_ENABLE);
		button.removeStyleName(AON.AON_NO_MARGIN);
		button.removeStyleName(AON.AON_EDIT_DATA_TABLE_BUTTON);
		
		button.setStyleName(!disabled ? AON.AON_ICON_DISABLE : AON.AON_ICON_ENABLE );
		button.setStyleName(AON.AON_NO_MARGIN, true);
		button.setStyleName(AON.AON_EDIT_DATA_TABLE_BUTTON, true);
	}
	
	private boolean isActiveToggleButton(Button button) {
		return AonStringUtils.containsIgnoreCase(button.getStyleName(), AON.AON_ICON_ENABLE);
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
