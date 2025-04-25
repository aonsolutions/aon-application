package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarSmallButton;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.client.AgreementDraft.TypeListBox;
import com.esferalia.aon.gwt.payroll.client.FxDialog.IContextProvider;
import com.esferalia.aon.gwt.payroll.shared.AgreementInfo.AgreementExtra;
import com.esferalia.aon.gwt.payroll.shared.ContextDescriptor;
import com.esferalia.aon.gwt.payroll.shared.Payment;
import com.esferalia.aon.gwt.payroll.shared.Payment.Type;
import com.esferalia.aon.gwt.payroll.shared.Result;
import com.esferalia.aon.gwt.payroll.shared.Salary;
import com.esferalia.aon.gwt.payroll.shared.Variable;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style.TextTransform;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ListBox;
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

	@UiField
	HTMLPanel messagePanel;
	
	// PAYMENT

	@UiField
	HTMLPanel paymentTable;

	@UiField
	HTMLPanel paymentTypePanel;

	@UiField
	TextBox paymentConceptCodeTB;

	@UiField
	TextBox paymentDescriptionTB;
	
	@UiField (provided = true)
	AonToolbarSmallButton expresssionVisibilityBtn;

	@UiField (provided = true)
	ExpressionCodeArea paymentExpressionCA;
	
	@UiField
	DisclosurePanel advancePanel;
	
	@UiField
	ListBox paymentSalaryTypeLB;
	
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
	ListBox monthPaymentLB;
	
	@UiField
	DateBoxEx startDateBx;
	
	@UiField
	DateBoxEx endDateBx;
	
	@UiField
	HTMLPanel extraPanel;

	@UiField
	HTMLPanel extraPeriodPanel;
	
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

	private DomainEmployeesServiceAsync employeeService = DomainEmployeesServiceAsync.newInstance();
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
	
	private List<String> contextVariables;

	// ----------------------------------------- Constructor

	protected AgreementPaymentEditor(Payment payment, AgreementExtra extra, Set<Payment> allPayments, Date startDate, List<String> contextVariables) {
		setCaption("Devengo");
		
		expresssionVisibilityBtn = new AonToolbarSmallButton("Editar expresi\u00f3n", AON.CSS.aonIconFx());
		paymentExpressionCA = new ExpressionCodeArea(true);
		
		setWidget(binder.createAndBindUi(this));
		showCloseButton(true);
		getFooterButtons();
		
		this.startDate = startDate == null ? new Date() : startDate;
		
		this.payment = payment;
		this.extra = extra;
		
		this.allPayments = allPayments;
		this.contextVariables = contextVariables;
		
		advancePanel.setAnimationEnabled(true);
		advancePanel.setOpen(false);
		advancePanel.addOpenHandler(e -> {
			showHideAdvanceOptions();
			this.setPopupPosition(this.getAbsoluteLeft(), this.getAbsoluteTop() - 80);
//			paymentExpressionCA.setAdvancedMode(!paymentExpressionCA.getAdvancedMode());
		});
		advancePanel.addCloseHandler(e -> {
			showHideAdvanceOptions();
			this.setPopupPosition(this.getAbsoluteLeft(), this.getAbsoluteTop() + 80);
//			paymentExpressionCA.setAdvancedMode(!paymentExpressionCA.getAdvancedMode());
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
		
		initializePaymentSalaryTypeLB();
		
		initializeMonthPayment();
		
		providedPayment();
		fillPayment();
		checkDatesPanelShown();
		
		if(null != extra && !extra.isDeleted()) fillExtra();
		extraPeriodPanel.setVisible(null != extra && !extra.isDeleted());
		
		showHideAdvanceOptions();
		
		showDialog();
	}

	private void initializeMonthPayment() {
		monthPaymentLB.addStyleName("aon-selectOneMenu");
		monthPaymentLB.getElement().getStyle().setWidth(100, Unit.PCT);
		monthPaymentLB.setHeight("1.5rem");
		monthPaymentLB.getElement().getStyle().setProperty("border", "1px solid rgb(137, 136, 136)");
		
		monthPaymentLB.clear();
		monthPaymentLB.addItem("Todos", "");
		monthPaymentLB.addItem("Enero", "0");
		monthPaymentLB.addItem("Febrero", "1");
		monthPaymentLB.addItem("Marzo", "2");
		monthPaymentLB.addItem("Abril", "3");
		monthPaymentLB.addItem("Mayo", "4");
		monthPaymentLB.addItem("Junio", "5");
		monthPaymentLB.addItem("Julio", "6");
		monthPaymentLB.addItem("Agosto", "7");
		monthPaymentLB.addItem("Septiembre", "8");
		monthPaymentLB.addItem("Octubre", "9");
		monthPaymentLB.addItem("Noviembre", "10");
		monthPaymentLB.addItem("Diciembre", "11");
		
	}

	private void initializePaymentSalaryTypeLB() {
		paymentSalaryTypeLB.addStyleName("aon-selectOneMenu");
		paymentSalaryTypeLB.getElement().getStyle().setWidth(100, Unit.PCT);
		paymentSalaryTypeLB.setHeight("1.5rem");
		paymentSalaryTypeLB.getElement().getStyle().setProperty("border", "1px solid rgb(137, 136, 136)");
		
		paymentSalaryTypeLB.clear();
		paymentSalaryTypeLB.addItem("N\u00f3mina", Salary.Type.SALARY.ordinal() + "");
		paymentSalaryTypeLB.addItem("Extra", Salary.Type.EXTRA.ordinal() + "");
		paymentSalaryTypeLB.addItem("Finiquito", Salary.Type.SETTLE.ordinal() + "");
		paymentSalaryTypeLB.addItem("Atraso", Salary.Type.DELAY.ordinal() + "");
	}

	private void checkDatesPanelShown() {
		Date compareDate1 = new Date(1970 - 1900, 0, 1);
		Date compareDate2 = new Date(2010 - 1900, 0, 1);
		
		periodPaymentPanel.setVisible((null != payment.getStartDate() && !payment.getStartDate().equals(compareDate1) && !payment.getStartDate().equals(compareDate2)) || payment.getEndDate() != null);
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
		extraIssueDate.addValueChangeHandler(e -> {
			extraPeriodPanel.setVisible(AonStringUtils.isNotBlank(extraIssueDate.getValue()));
		});
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
			
			paymentQuoteTypeLB.setSelectedIndex(0);
			DomEvent.fireNativeEvent(Document.get().createChangeEvent(), paymentQuoteTypeLB);
			
		} else if(!craType.isBBCCIncluded() && craType.isBBCCExcluded()) {
			
			// Exento
			paymentTaxedTypeLB.setSelectedIndex(0);
			DomEvent.fireNativeEvent(Document.get().createChangeEvent(), paymentTaxedTypeLB);
			
			paymentQuoteTypeLB.setSelectedIndex(1);
			DomEvent.fireNativeEvent(Document.get().createChangeEvent(), paymentQuoteTypeLB);
			
		} else if(craType.isBBCCIncluded() && craType.isBBCCExcluded()) {
			
			// Personalizado
			paymentTaxedTypeLB.setSelectedIndex(2);
			DomEvent.fireNativeEvent(Document.get().createChangeEvent(), paymentTaxedTypeLB);
			
			paymentQuoteTypeLB.setSelectedIndex(3);
			DomEvent.fireNativeEvent(Document.get().createChangeEvent(), paymentQuoteTypeLB);
			
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
				paymentTaxedExpression.setValue("BASE_CTA_ESP=( isdef BASE_CTA_ESP ? BASE_CTA_ESP : 0.00 ) + _P; _P");
				break;
			default:
				paymentTaxedExpression.setEnabled(true);
				paymentTaxedExpression.setValue("");
				break;
			}
		});
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

	// ----------------------------------------- Fill Payment

	private void fillPayment() {
		paymentTypeLB.setSelected(this.payment.getType());
		
		paymentConceptCodeTB.getElement().getStyle().setTextTransform(TextTransform.UPPERCASE);
		paymentConceptCodeTB.setValue(this.payment.getName());
		paymentConceptCodeTB.addValueChangeHandler(e -> checkPaymentCode());
		
		paymentDescriptionTB.setValue(this.payment.getDescription());
		paymentExpressionCA.setText(this.payment.getExpression());
		checkSeniorityExpresion();
		expresssionVisibilityBtn.addClickHandler(e -> {
			if(null == contextProvider)
				contextProvider = new ContextProvider();
			
			final FxDialog fxDialog = new FxDialog(contextProvider);
			fxDialog.setExpression(this.payment.getExpression());
			fxDialog.getElement().getStyle().setProperty("z-index", "70");
			fxDialog.center();
			fxDialog.show();
		
			fxDialog.addCloseHandler(ev -> {
				if (fxDialog.isAccepted()) {
					payment.setExpression(fxDialog.getExpression());
					paymentExpressionCA.setText(payment.getExpression());
					paymentExpressionCA.setValue(payment.getExpression());
				}
			});
		});
		
		setSelectedValueLB(paymentSalaryTypeLB, this.payment.getSalaryType().ordinal() + "");
		
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
		
		setSelectedValueLB(monthPaymentLB, this.payment.getMonth() == null ? "" : this.payment.getMonth().toString());
		startDateBx.setValue(this.payment.getStartDate());
		endDateBx.setValue(this.payment.getEndDate());
		
		getEnableDisableButton(enterpriseTaxed, AonStringUtils.isNotBlank(irpfExpression) && AonStringUtils.equalsIgnoreCase(irpfExpression, "BASE_CTA_ESP=_P"));
		if(isActiveToggleButton(enterpriseTaxed)) paymentTaxedExpression.setValue("BASE_CTA_ESP=_P");
		
		DomEvent.fireNativeEvent(Document.get().createChangeEvent(), paymentTypeLB);
	}
	
	private void checkPaymentCode() {
		String newCode = paymentConceptCodeTB.getValue();
		if(!AonStringUtils.isBlank(newCode)) {
			String cleanNewCode = newCode.replaceAll(" ", "_").trim().toUpperCase();
			
			Optional<Payment> existingCodePayment = allPayments.stream().filter(p -> AonStringUtils.equals(p.getName(), cleanNewCode)).findFirst();
			if(existingCodePayment.isPresent()) {
				paymentConceptCodeTB.setValue("");
				AonMessagePanel.showError(messagePanel, "Ya existe un concepto con este c\u00f3digo para este convenio. Elija otro nombre para el c\u00f3digo");
			} else if(contextVariables.contains(cleanNewCode)) {
				paymentConceptCodeTB.setValue("");
				AonMessagePanel.showError(messagePanel, "No se puede usar el nombre de una variable de contexto como c\u00f3digo de un concepto. Elija otro nombre para el c\u00f3digo");
			} else {
				paymentConceptCodeTB.setValue(cleanNewCode);
			}
				
		} else AonMessagePanel.showError(messagePanel, "El c\u00f3digo del concepto es obligatorio");
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
	}
	
	private void createPayment() {
		payment.setType(paymentTypeLB.getSelected());
		payment.setName(paymentConceptCodeTB.getValue());
		payment.setDescription(paymentDescriptionTB.getValue());
		payment.setExpression(paymentExpressionCA.getText());
		
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
		
		payment.setMonth(AonStringUtils.isBlank(monthPaymentLB.getSelectedValue()) ? null : Short.parseShort(monthPaymentLB.getSelectedValue()));
		payment.setStartDate(startDateBx.getValue());
		payment.setEndDate(endDateBx.getValue());
		
		payment.setSalaryType(Salary.Type.values()[Integer.parseInt(paymentSalaryTypeLB.getSelectedValue())]);
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
			extra.setStartDate( (startMonth.length() == 2 ? "01/" : "") + startMonth + extraStartDateYear.getSelectedValue());
			
			String endMonth = extraEndDateMonth.getSelectedValue();
			if(AonStringUtils.equalsIgnoreCase(endMonth, "06")) endMonth = "30/06";
			else if(AonStringUtils.equalsIgnoreCase(endMonth, "12")) endMonth = "31/12";
			extra.setEndDate(  (endMonth.length() == 2 ? (getMaxDayMonth(endMonth) + "/") : "") + endMonth + extraEndDateYear.getSelectedValue());
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
	
	private String getMaxDayMonth(String endMonth) {
		switch (endMonth) {
		case "01":
		case "03":
		case "05":
		case "07":
		case "08":
		case "10":
		case "12":
			return "31";
		case "02": 
			return "28";
		case "04": 
		case "06": 
		case "09": 
		case "11": 
			return "30";
		default:
			return "30";
		}
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
