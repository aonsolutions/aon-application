package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.payroll.client.AgreementDraft.TypeListBox;
import com.esferalia.aon.gwt.payroll.shared.AgreementInfo.AgreementExtra;
import com.esferalia.aon.gwt.payroll.shared.ContractConcept;
import com.esferalia.aon.gwt.payroll.shared.ContractConcepts;
import com.esferalia.aon.gwt.payroll.shared.Payment;
import com.esferalia.aon.gwt.payroll.shared.Payment.Type;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
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
	HTMLPanel extraPanel;
	
	@UiField
	ListBox extraType;

	@UiField
	TextBox extraStartDate;
	
	@UiField
	ListBox extraStartDateYear;

	@UiField
	TextBox extraEndDate;
	
	@UiField
	ListBox extraEndDateYear;

	@UiField
	TextBox extraIssueDate;

	@UiField
	HTMLPanel buttonsPanel;

	// ----------------------------------------- Variables

	private DomainEnterprisesServiceAsync enterpriseService = DomainEnterprisesServiceAsync.newInstance();
	private ContractConcepts contractConcepts;
	private ContractConcept selectedConcept;
	private Payment payment;
	private AgreementExtra extra;

	private TypeListBox<Payment.Type> paymentTypeLB;
	
	private Button acceptDialog;

	// ----------------------------------------- Constructor

	protected AgreementPaymentEditor(Payment payment, AgreementExtra extra) {
		setCaption("Devengo");
		setWidget(binder.createAndBindUi(this));
		showCloseButton(true);
		getFooterButtons();
		this.payment = payment;
		this.extra = extra;
		
		initializeExtraPanel();
		
		enterpriseService.getAllConcepts(new AsyncCallback<ContractConcepts>() {

			@Override
			public void onSuccess(ContractConcepts contractConceptsIn) {
				contractConcepts = contractConceptsIn;

				providedPayment();
				fillPayment();
				if(null != extra) fillExtra();
				showDialog();
			}

			@Override
			public void onFailure(Throwable caught) {
				// Failure
			}

		});
	}

	private void initializeExtraPanel() {
		initializeExtraListBoxes();
		
		extraPanel.setVisible(null != extra || payment.getType().equals(Type.CRA_0004) || payment.getType().equals(Type.CRA_0005));
		
		extraStartDate.getElement().setPropertyString("placeholder", "dd/mm");
		extraEndDate.getElement().setPropertyString("placeholder", "dd/mm");
		extraIssueDate.getElement().setPropertyString("placeholder", "dd/mm");
	}
	
	private void initializeExtraListBoxes() {
		extraType.clear();
		extraType.addItem("Anual", "Anual");
		extraType.addItem("Semestral", "Semestral");
		extraType.addItem("Prorrateada", "Prorrateada");
		
		extraStartDateYear.clear();
		extraStartDateYear.addItem("A\u00f1o en curso", "");
		extraStartDateYear.addItem("A\u00f1o anterior", " -1");
		
		extraEndDateYear.clear();
		extraEndDateYear.addItem("A\u00f1o en curso", "");
		extraEndDateYear.addItem("A\u00f1o anterior", " -1");
		
		extraType.addChangeHandler(e -> updateExtraDates());
		
		extraIssueDate.addValueChangeHandler(e -> updateExtraDates());
	}

	private void updateExtraDates() {
		String extraTypeValue = extraType.getSelectedValue();
		if(AonStringUtils.equalsIgnoreCase(extraTypeValue, "Prorrateada")) {
			extraStartDate.setValue("");
			extraStartDate.setEnabled(false);
			extraStartDateYear.setEnabled(false);
			
			extraEndDate.setValue("");
			extraEndDate.setEnabled(false);
			extraEndDateYear.setEnabled(false);
			
			extraIssueDate.setValue("");
			extraIssueDate.setEnabled(false);
		} else {
			extraStartDateYear.setEnabled(true);
			extraEndDateYear.setEnabled(true);
			extraStartDate.setEnabled(true);
			extraEndDate.setEnabled(true);
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
			extraStartDate.setValue("01/07");
			extraStartDateYear.setSelectedIndex(1);
			
			extraEndDate.setValue("30/06");
			extraEndDateYear.setSelectedIndex(0);
		} else if(AonStringUtils.equalsIgnoreCase(extraTypeValue, "Semestral")) {
			extraStartDate.setValue("01/01");
			extraStartDateYear.setSelectedIndex(0);
			
			extraEndDate.setValue("30/06");
			extraEndDateYear.setSelectedIndex(0);
		}
		
	}

	private void updateWinterValues() {
		String extraTypeValue = extraType.getSelectedValue();
		if(AonStringUtils.equalsIgnoreCase(extraTypeValue, "Anual")) {
			extraStartDate.setValue("01/01");
			extraStartDateYear.setSelectedIndex(0);
			
			extraEndDate.setValue("31/12");
			extraEndDateYear.setSelectedIndex(0);
		} else if(AonStringUtils.equalsIgnoreCase(extraTypeValue, "Semestral")) {
			extraStartDate.setValue("01/07");
			extraStartDateYear.setSelectedIndex(0);
			
			extraEndDate.setValue("31/12");
			extraEndDateYear.setSelectedIndex(0);
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

	private void fillPayment() {
		paymentTypeLB.setSelected(this.payment.getType());
		paymentConceptSB.setValue(this.payment.getName());
		paymentDescriptionTB.setValue(this.payment.getDescription());
		paymentExpressionTB.setValue(this.payment.getExpression());
		setSelectedValueLB(paymentTaxedTypeLB, getTaxedQuoteType(this.payment.getIrpfExpression()));
		paymentTaxedExpression.setValue(this.payment.getIrpfExpression());
		setSelectedValueLB(paymentQuoteTypeLB, getTaxedQuoteType(this.payment.getQuoteExpression()));
		paymentQuoteExpression.setValue(this.payment.getQuoteExpression());
	}
	
	private void fillExtra() {
		setSelectedValueLB(extraType, getExtraType(extra));
		DomEvent.fireNativeEvent(Document.get().createChangeEvent(), extraType);
		
		extraStartDate.setValue((AonStringUtils.isNotBlank(extra.getStartDate()) && AonStringUtils.containsIgnoreCase(extra.getStartDate(), "-1")) ? extra.getStartDate().split(" ")[0] : extra.getStartDate());
		extraStartDateYear.setSelectedIndex((AonStringUtils.isNotBlank(extra.getStartDate()) && AonStringUtils.containsIgnoreCase(extra.getStartDate(), "-1")) ? 1 : 0);
		extraEndDate.setValue((AonStringUtils.isNotBlank(extra.getEndDate()) && AonStringUtils.containsIgnoreCase(extra.getEndDate(), "-1")) ? extra.getEndDate().split(" ")[0] : extra.getEndDate());
		extraEndDateYear.setSelectedIndex((AonStringUtils.isNotBlank(extra.getEndDate()) && AonStringUtils.containsIgnoreCase(extra.getEndDate(), "-1")) ? 1 : 0);
		
		extraIssueDate.setValue(extra.getIssueDate());
	}
	
	private String getExtraType(AgreementExtra extra) {
		if(null == extra || AonStringUtils.isBlank(extra.getIssueDate())) return "Prorrateada";
		if(AonStringUtils.containsIgnoreCase(extra.getStartDate(), "-1")) return "Anual";
		
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
			createPayment();
			if(payment.getType().equals(Type.CRA_0004) || payment.getType().equals(Type.CRA_0005))
				createExtra();
			onAccept(payment, extra);
		});

		buttonsPanel.add(acceptDialog);
	}

	public void setSaveButton() {
		acceptDialog.setText("Grabar");
	}
	
	private void createPayment() {
		if(null == this.payment) {
			this.payment = new Payment();
			Random rand = new Random();
			int newPaymentId = rand.nextInt(1000) * -1;
			this.payment.setId(newPaymentId);
		}
		
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
		payment.setMonth(null);
	}
	
	private void createExtra() {
		Window.alert("createExtra start");
		if(null == this.extra) {
			this.extra = new AgreementExtra();
			Random rand = new Random();
			int newExtraId = rand.nextInt(1000) * -1;
			this.extra.setId(newExtraId);
			this.extra.setAgreementPayment(payment.getId());
		}
		
		String extraTypeValue = extraType.getSelectedValue();
		extra.setDeleted(AonStringUtils.equalsIgnoreCase(extraTypeValue, "Prorrateada"));
		if(!extra.isDeleted()) {
			extra.setStartDate(extraStartDate.getValue() + extraStartDateYear.getSelectedValue());
			extra.setEndDate(extraEndDate.getValue() + extraEndDateYear.getSelectedValue());
			extra.setIssueDate(extraIssueDate.getValue());
		}
		
		if(AonStringUtils.isNotBlank(extraIssueDate.getValue())) {
			String issueDate = extraIssueDate.getValue();
			Short month = Short.parseShort(issueDate.split("/")[1]);
			month--;
			payment.setMonth(month);
		} else
			payment.setMonth(null);
		
		Window.alert("createExtra end");
		
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

	protected abstract void onAccept(Payment payment, AgreementExtra extra);

}
