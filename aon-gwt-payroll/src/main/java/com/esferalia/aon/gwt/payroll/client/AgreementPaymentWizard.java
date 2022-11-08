package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.CustomDataGrid;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.client.AgreementDraft.ContextProvider;
import com.esferalia.aon.gwt.payroll.client.AgreementDraft.TypeListBox;
import com.esferalia.aon.gwt.payroll.shared.Extra;
import com.esferalia.aon.gwt.payroll.shared.Payment;
import com.esferalia.aon.gwt.payroll.shared.Payment.Type;
import com.esferalia.aon.gwt.payroll.shared.Salary;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionModel.AbstractSelectionModel;

public abstract class AgreementPaymentWizard extends AonCustomDialog {

	// -------------------------------------------- GtzdoWizard implementation

	private class GtzdoWizardImplementation extends GtzdoWizard {

		@Override
		protected void onPaymentTypeChange(String paymentTypeValue) {
			if (!AonStringUtils.equalsIgnoreCase(paymentTypeValue, "MEJORA_IT")) {
				showFirstPage();
				// Fire PaymentType
				setSelectedValueLB(AgreementPaymentWizard.this.paymentType, paymentTypeValue);
				DomEvent.fireNativeEvent(Document.get().createChangeEvent(), AgreementPaymentWizard.this.paymentType);
			}
		}

		@Override
		protected void onShowPaymentAviables(String gtzdoAboutTypeValue) {
			if (AonStringUtils.equalsIgnoreCase(gtzdoAboutTypeValue, "CALCULADO"))
				enableOrDisablePayments();
			else if (null != paymentsDataGridPanel) // Provied null before loading
				paymentsDataGridPanel.setVisible(false);
		}

		@Override
		protected void onDescriptionChange(String description) {
			paymentDescription.setValue(description);
			paymentDescriptionPos.setValue(55);
		}

		@Override
		protected String getExpression() {
			return paymentExpression.getValue();
		}

		@Override
		protected void setExpression(String expression) {
			paymentExpression.setValue(expression, true);
		}

		@Override
		protected String getDescription(Integer row) {
			if (null == row)
				return null == paymentDescriptionPos.getValue() ? paymentDescription.getValue()
						: "[" + paymentDescriptionPos.getValue() + "] " + paymentDescription.getValue();

			Integer pos = paymentDescriptionPos.getValue();
			if (null != pos)
				pos += row;

			return null == paymentDescriptionPos.getValue() ? paymentDescription.getValue()
					: "[" + pos + "] " + paymentDescription.getValue();
		}

	}

	// -------------------------------------------- UiBinder

	interface AgreementPaymentDialogBinder extends UiBinder<Widget, AgreementPaymentWizard> {
	}

	private static final AgreementPaymentDialogBinder binder = GWT.create(AgreementPaymentDialogBinder.class);

	// -------------------------------------------- PaymentsSelectionModel

	private class PaymentsSelectionModel extends AbstractSelectionModel<Payment> {

		private Timer synchronizer = new Timer() {
			@Override
			public void run() {
				fireSelectionChangeEvent();
			}
		};

		public PaymentsSelectionModel(ProvidesKey<Payment> keyProvider) {
			super(keyProvider);
			paymentExpression.addKeyUpHandler(e -> synchronizer.schedule(2000));
		}

		@Override
		public boolean isSelected(Payment payment) {
			String expression = paymentExpression.getValue();

			if (AonStringUtils.isBlank(expression))
				return false;

			String variable = getVariableName(payment);

			if (AonStringUtils.isBlank(variable))
				return false;

			return expression.contains(variable);
		}

		@Override
		public void setSelected(Payment payment, boolean selected) {
			String expression = paymentExpression.getValue();

			String variable = getVariableName(payment);
			if (AonStringUtils.isBlank(variable))
				return;

			if (selected) {
				if (AonStringUtils.isBlank(expression))
					expression += variable;
				else
					expression += " + " + variable;
			} else {
				// first of all remove variable.
				if (expression.contains(variable + " + "))
					expression = expression.replace(variable + " + ", "");
				else if (expression.contains(" + " + variable))
					expression = expression.replace(" + " + variable, "");
				else
					expression = expression.replace(variable, "");

				if (AonStringUtils.isNotBlank(expression))
					expression = expression.trim();
			}

			paymentExpression.setValue(expression, true);
			if (AonStringUtils.equalsIgnoreCase(paymentType.getSelectedValue(), "MEJORA_IT"))
				gtzdoWizard.checkTableFormula();

			fireSelectionChangeEvent();
		}

	}

	// -------------------------------------------- PaymentsSelectionModel.Methods

	private static String getVariableName(Payment payment) {
		String name = payment.getName();
		if (AonStringUtils.isNotBlank(name))
			return name;

		String description = payment.getDescription();
		if (AonStringUtils.isNotBlank(description))
			return description.toUpperCase().replaceAll("\\s", "_").replaceAll("[\u00c1]*", "A")
					.replaceAll("[\u00c9]*", "E").replaceAll("[\u00cd]*", "I").replaceAll("[\u00d3]*", "O")
					.replaceAll("[\u00da]*", "U").replaceAll("[\u00dc]*", "U").replaceAll("[\u00d1]*", "N")
					.replaceAll("\\W", "");
		return null;
	}

	// -------------------------------------------- UiField

	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String visibilityDisabled();
	}

	@UiField
	HTMLPanel container;

	@UiField
	HTMLPanel firstPage;

	@UiField
	HTMLPanel paymentTypePanel;

	@UiField
	ListBox paymentType;

	@UiField
	HTMLPanel extraPanel;

	@UiField
	Label extraLabel;

	@UiField
	TextBox extraName;

	@UiField
	HTMLPanel normalFieldsPanel;

	@UiField
	HTMLPanel periodicityPanel;

	@UiField
	ListBox periodicityType;

//	@UiField
//	HTMLPanel extraPayDatePanel;
//
//	@UiField
//	TextBox extraPayDate;
//
//	@UiField
//	HTMLPanel extraPayCalcPanel;
//
//	@UiField
//	ListBox extraPayCalc;

	@UiField
	HTMLPanel extraPayTypePanel;

	@UiField
	ListBox extraPayType;

	@UiField
	HTMLPanel partialityPanel;

	@UiField
	Button partialityButton;
	
	@UiField
	HTMLPanel extraFieldsPanel;
	
	@UiField
	TextBox extraIssueDate;
	
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
	ListBox extraPayType2;

	@UiField
	HTMLPanel weekDaysPanel;

	@UiField
	CheckBox mondayCB;

	@UiField
	CheckBox tuesdayCB;

	@UiField
	CheckBox wednesdayCB;

	@UiField
	CheckBox thursdayCB;

	@UiField
	CheckBox fridayCB;

	@UiField
	CheckBox saturdayCB;

	@UiField
	CheckBox sundayCB;

	@UiField(provided = true)
	GtzdoWizard gtzdoWizard;

	@UiField
	HTMLPanel paymentsDataGridPanel;

	@UiField(provided = true)
	DataGrid<Payment> paymentsDataGrid;

	@UiField
	TextBox paymentConcept;

	@UiField
	HTMLPanel paymentCRAPanel;

	@UiField
	IntegerBox paymentDescriptionPos;

	@UiField
	TextBox paymentDescription;

	@UiField
	Label paymentExpressionTitle;

	@UiField(provided = true)
	TextBox paymentExpression;

	@UiField
	HTMLPanel taxedPanel;

	@UiField
	ListBox taxedTypeLB;

	@UiField
	TextBox taxedExpression;

	@UiField
	Button taxedFxButton;

	@UiField
	HTMLPanel quotePanel;

	@UiField
	ListBox quoteTypeLB;

	@UiField
	TextBox quoteExpression;

	@UiField
	Button quoteFxButton;

	@UiField
	HTMLPanel monthPanel;

	@UiField
	ListBox monthLB;

	@UiField
	HTMLPanel monthStartPanel;

	@UiField
	ListBox monthStartLB;

	@UiField
	HTMLPanel monthEndPanel;

	@UiField
	ListBox monthEndLB;

	@UiField
	HTMLPanel buttonsPanel;

	// -------------------------------------------- Variables

	private Button acceptBtnDialog;

	private Payment payment;
	private Payment paymentExtra;
	private Extra extra;

	TypeListBox<Payment.Type> paymentTypeListBox;

	private boolean hasPartiality = true;

	private List<Payment> payments = Collections.emptyList();
	private PaymentsSelectionModel paymentsSelectionModel;
	private ContextProvider contextProvider;

	// -------------------------------------------- Constructor

	protected AgreementPaymentWizard(Set<Payment> aviablePayments, ContextProvider contextProvider) {
		setCaption("Asistente Conceptos");

		paymentExpression = new TextBox();
		paymentExpression.setEnabled(false);
		providePaymentsDataGrid();

		gtzdoWizard = new GtzdoWizardImplementation();

		setWidget(binder.createAndBindUi(this));

		showCloseButton(true);
		setAvailablePayments(aviablePayments);
		initFooterButtons();

		this.contextProvider = contextProvider;

		// Hide extra default
		extraPanel.getElement().getStyle().setDisplay(Display.NONE);
		extraFieldsPanel.getElement().getStyle().setDisplay(Display.NONE);

		// Hide taxed defaul
		UIObject.setVisible(taxedPanel.getElement(), false);

		// Hide quote defaul
		UIObject.setVisible(quotePanel.getElement(), false);

		// Hide month defaul
		UIObject.setVisible(monthPanel.getElement(), false);

		// Hide month defaul
		UIObject.setVisible(monthStartPanel.getElement(), false);

		// Hide month defaul
		UIObject.setVisible(monthEndPanel.getElement(), false);

		// Hide weekDays panel default
		UIObject.setVisible(weekDaysPanel.getElement(), false);

		// Init Payment
		payment = new Payment();
		paymentExtra = new Payment();
		extra = new Extra();

//		extraPayDate.getElement().setPropertyString("placeholder", "dd/mm");
		paymentDescriptionPos.getElement().setAttribute("type", "number");

		initExtraPanel();
		initPaymentListBox();
//		initExtraPayCalc();
		initExtraPayType();
		initPartialityButton();
		initPaymentCRAType();
		initTaxedAndQuoteLB();
		initMonthLB();
		showFirstPage();

		// Fire SALARIO_BASE
		DomEvent.fireNativeEvent(Document.get().createChangeEvent(), paymentType);

		showDialog();
	}
	
	// -------------------------------------------- Extra panel

	private void initExtraPanel() {
		initializeExtraListBoxes();
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
		
		extraType.addChangeHandler(e -> updateExtraDates());
		
		extraIssueDate.addValueChangeHandler(e -> updateExtraDates());
		
		extraPayType2.clear();
		extraPayType2.addItem("CALCULADO", "CALCULADO");
		extraPayType2.addItem("VARIABLE", "VARIABLE");
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
		} else if(AonStringUtils.equalsIgnoreCase(extraTypeValue, "Semestral")) {
			setSelectedValueLB(extraStartDateMonth, "01");
			extraStartDateYear.setSelectedIndex(0);
			
			setSelectedValueLB(extraEndDateMonth, "06");
			extraEndDateYear.setSelectedIndex(0);
		}		
	}

	private void updateWinterValues() {
		String extraTypeValue = extraType.getSelectedValue();
		if(AonStringUtils.equalsIgnoreCase(extraTypeValue, "Anual")) {
			setSelectedValueLB(extraStartDateMonth, "01");
			extraStartDateYear.setSelectedIndex(0);
			
			setSelectedValueLB(extraEndDateMonth, "12");
			extraEndDateYear.setSelectedIndex(0);
		} else if(AonStringUtils.equalsIgnoreCase(extraTypeValue, "Semestral")) {
			setSelectedValueLB(extraStartDateMonth, "07");
			extraStartDateYear.setSelectedIndex(0);
			
			setSelectedValueLB(extraEndDateMonth, "12");
			extraEndDateYear.setSelectedIndex(0);
		}
	}

	// -------------------------------------------- ProvidePaymentsDataGrid

	protected void providePaymentsDataGrid() {
		payments = Collections.emptyList();

		ProvidesKey<Payment> keyProvider = HasIdKeyProvider.getKeyProvider();
		paymentsDataGrid = new CustomDataGrid<>(10, keyProvider);

		paymentsDataGrid.setAutoHeaderRefreshDisabled(true);

		paymentsSelectionModel = new PaymentsSelectionModel(keyProvider);
		paymentsDataGrid.setSelectionModel(paymentsSelectionModel,
				DefaultSelectionEventManager.<Payment>createCheckboxManager(0));

		// Checkbox column. This table will uses a checkbox column for selection.
		Column<Payment, Boolean> checkColumn = new Column<Payment, Boolean>(new CheckboxCell()) {
			@Override
			public Boolean getValue(Payment payment) {
				return paymentsSelectionModel.isSelected(payment);
			}
		};

		paymentsDataGrid.addColumn(checkColumn);
		paymentsDataGrid.setColumnWidth(checkColumn, "40px");

		Column<Payment, String> nameColumn = new Column<Payment, String>(new TextCell()) {
			@Override
			public String getValue(com.esferalia.aon.gwt.payroll.shared.Payment payment) {
				if (AonStringUtils.isBlank(payment.getName()))
					return payment.getDescription();
				return null == payment.getDescription() ? " (" + payment.getName() + ")"
						: payment.getDescription() + " (" + payment.getName() + ")";
			}
		};
		paymentsDataGrid.addColumn(nameColumn);

		paymentsDataGrid.addStyleName(AON.AON_WIDTH_ALL);
		paymentsDataGrid.getElement().getStyle().setPropertyPx("minHeight", 95);
		paymentsDataGrid.setWidth("100%");

		new ListDataProvider<Payment>(Collections.emptyList()).addDataDisplay(paymentsDataGrid);
	}

	// -------------------------------------------- Initialize View

	private void setAvailablePayments(Set<Payment> paymentsIn) {
		payments = paymentsIn.stream().collect(Collectors.toList());
	}

	// -------------------------------------------- Initialize view

	private void initPaymentListBox() {
		paymentType.clear();
		paymentType.addItem("MANUAL", "MANUAL");
		paymentType.addItem("SALARIO_BASE", "SALARIO_BASE");
		paymentType.addItem("PLUS_SALARIAL", "PLUS_SALARIAL");
		paymentType.addItem("PLUS_EXTRA_SALARIAL", "PLUS_EXTRA_SALARIAL");
		paymentType.addItem("PAGA_EXTRA", "PAGA_EXTRA");
		paymentType.addItem("MEJORA_IT", "MEJORA_IT");

		periodicityType.clear();
		periodicityType.addItem("-", "-1");
	}

//	private void initExtraPayCalc() {
//		extraPayCalc.clear();
//		extraPayCalc.addItem("A\u00D1O ACTUAL", "A\u00D1O ACTUAL");
//		extraPayCalc.addItem("A\u00D1O ANTERIOR", "A\u00D1O ANTERIOR");
//		extraPayCalc.addItem("\u00DALTIMOS 12 MESES", "\u00DALTIMOS 12 MESES");
//	}

	private void initExtraPayType() {
		extraPayType.clear();
		extraPayType.addItem("CALCULADO", "CALCULADO");
		extraPayType.addItem("VARIABLE", "VARIABLE");
	}

	private void initPeriodicityType(String paymentType) {
		if (AonStringUtils.equalsIgnoreCase(paymentType, "SALARIO_BASE")) {
			periodicityType.clear();
			periodicityType.addItem("ANUAL", " / PAGAS * DIAS_TRABAJADOS / DIAS_MES");
			periodicityType.addItem("MENSUAL", " * DIAS_TRABAJADOS / DIAS_MES");
			periodicityType.addItem("DIARIO", " * DIAS_TRABAJADOS");
			periodicityType.addItem("HORAS", " * HORAS_TRABAJADAS");
			periodicityType.addItem("PEONADAS", " * JORNADAS_REALES");
		}

		if (AonStringUtils.equalsIgnoreCase(paymentType, "PLUS_SALARIAL")
				|| AonStringUtils.equalsIgnoreCase(paymentType, "PLUS_EXTRA_SALARIAL")) {
			periodicityType.clear();
			periodicityType.addItem("MENSUAL", " * DIAS_TRABAJADOS / DIAS_MES");
			periodicityType.addItem("DIAS TRABAJADOS", " * DIAS_TRABAJADOS");
			periodicityType.addItem("DIAS EFECTIVOS", " * DIAS_EFECTIVOS");
			periodicityType.addItem("HORAS TRABAJADAS", " * HORAS_TRABAJADAS");
			periodicityType.addItem("FIJO", "FIJO");
		}

		if (AonStringUtils.equalsIgnoreCase(paymentType, "PAGA_EXTRA")) {
			periodicityType.clear();
			periodicityType.addItem("ANUAL", "ANUAL");
			periodicityType.addItem("PRORRATEO", "PRORRATEO");
		}

		if (AonStringUtils.equalsIgnoreCase(paymentType, "MANUAL")) {
			periodicityType.clear();
			periodicityType.addItem("ANUAL", " / PAGAS * DIAS_TRABAJADOS / DIAS_MES");
			periodicityType.addItem("MENSUAL", " * DIAS_TRABAJADOS / DIAS_MES");
			periodicityType.addItem("DIARIO", " * DIAS_TRABAJADOS");
			periodicityType.addItem("HORAS", " * HORAS_TRABAJADAS");
			periodicityType.addItem("PEONADAS", " * JORNADAS_REALES");
			periodicityType.addItem("DIAS EFECTIVOS", " * DIAS_EFECTIVOS");
			periodicityType.addItem("DIAS SEMANA", " * DIAS_SEMANA");
			periodicityType.addItem("FIJO", "FIJO");
			periodicityType.addItem("MANUAL", "MANUAL");
		}
	}

	private void initPaymentCRAType() {
		paymentTypeListBox = new TypeListBox<>(Payment.Type.class, 10);
		paymentTypeListBox.setSelected(Payment.Type.DEFAULT);
		paymentTypeListBox.addStyleName("aon-selectOneMenu");
		paymentTypeListBox.getElement().getStyle().setWidth(100, Unit.PCT);
		paymentTypeListBox.setEnabled(false);
		paymentTypeListBox.addStyleName(style.visibilityDisabled());
		paymentTypeListBox.addChangeHandler(e -> {
			checkPeriodicityValues();
			enableOrDisableTaxAndQuote();
			enableOrDisableMonth();
			enableOrDisableMonthStartEnd();
			createUpdatePayment();
			checkProrrat0005();
		});
		paymentCRAPanel.add(paymentTypeListBox);
	}

	private void checkPeriodicityValues() {
		if (AonStringUtils.equalsIgnoreCase("MANUAL", paymentType.getSelectedValue())) {
			com.esferalia.aon.gwt.payroll.shared.Payment.Type type = getType();
			int periodicityCount = periodicityType.getItemCount();

			if (type != com.esferalia.aon.gwt.payroll.shared.Payment.Type.CRA_0005 && periodicityCount > 9)
				periodicityType.removeItem(periodicityType.getItemCount() - 1);
			else if (type == com.esferalia.aon.gwt.payroll.shared.Payment.Type.CRA_0005 && periodicityCount == 9)
				periodicityType.addItem("PRORRATEAR", "PRORRATEAR");
		}
	}

	private void enableOrDisableMonth() {
		com.esferalia.aon.gwt.payroll.shared.Payment.Type type = getType();
		UIObject.setVisible(monthPanel.getElement(),
				type == com.esferalia.aon.gwt.payroll.shared.Payment.Type.CRA_0005);
	}

	private void enableOrDisableMonthStartEnd() {
		com.esferalia.aon.gwt.payroll.shared.Payment.Type type = getType();
		UIObject.setVisible(monthStartPanel.getElement(),
				type == com.esferalia.aon.gwt.payroll.shared.Payment.Type.CRA_0005);
		UIObject.setVisible(monthEndPanel.getElement(),
				type == com.esferalia.aon.gwt.payroll.shared.Payment.Type.CRA_0005);
	}

	private void enableOrDisableTaxAndQuote() {
		if (taxEditableAndQuoteNone()) {
			UIObject.setVisible(taxedPanel.getElement(), true);
			UIObject.setVisible(quotePanel.getElement(), true);
			showCRA0000();
		} else {
			if (taxEditableAndQuoteFull()) {
				UIObject.setVisible(taxedPanel.getElement(), true);
				UIObject.setVisible(quotePanel.getElement(), false);
				showCRA0013();
			} else if (quoteEditable()) {
				UIObject.setVisible(taxedPanel.getElement(), false);
				UIObject.setVisible(quotePanel.getElement(), true);
				showCRADefault();
			} else if (taxAndQuoteFull() || taxAndQuoteNone()) {
				UIObject.setVisible(taxedPanel.getElement(), false);
				UIObject.setVisible(quotePanel.getElement(), false);
				showCRADefault();
			} else {
				UIObject.setVisible(taxedPanel.getElement(), true);
				UIObject.setVisible(quotePanel.getElement(), true);
				showCRADefault();
			}
		}
	}

	private void showCRA0000() {
		quoteTypeLB.setEnabled(false);
		quoteExpression.setEnabled(false);
		quoteFxButton.setVisible(false);
		quoteTypeLB.setSelectedIndex(1);
		DomEvent.fireNativeEvent(Document.get().createChangeEvent(), quoteTypeLB);
		quoteExpression.setValue("0,00");
	}

	private void showCRA0013() {
		taxedTypeLB.setEnabled(false);
		taxedExpression.setEnabled(false);
		taxedFxButton.setVisible(false);
		taxedTypeLB.setSelectedIndex(1);
		DomEvent.fireNativeEvent(Document.get().createChangeEvent(), taxedTypeLB);
		taxedExpression.setValue("0,00");
	}

	private void showCRADefault() {
		quoteTypeLB.setEnabled(true);
		quoteExpression.setEnabled(true);
		quoteFxButton.setVisible(true);
		quoteTypeLB.setSelectedIndex(0);
		DomEvent.fireNativeEvent(Document.get().createChangeEvent(), quoteTypeLB);
		taxedTypeLB.setEnabled(true);
		taxedExpression.setEnabled(true);
		taxedFxButton.setVisible(true);
		taxedTypeLB.setSelectedIndex(0);
		DomEvent.fireNativeEvent(Document.get().createChangeEvent(), quoteTypeLB);

	}

	private boolean taxEditableAndQuoteFull() {
		com.esferalia.aon.gwt.payroll.shared.Payment.Type type = getType();
		return (type == com.esferalia.aon.gwt.payroll.shared.Payment.Type.CRA_0013);
	}

	private boolean taxEditableAndQuoteNone() {
		com.esferalia.aon.gwt.payroll.shared.Payment.Type type = getType();
		return (type == com.esferalia.aon.gwt.payroll.shared.Payment.Type.CRA_0000);
	}

	private boolean quoteEditable() {
		com.esferalia.aon.gwt.payroll.shared.Payment.Type type = getType();
		return (type == com.esferalia.aon.gwt.payroll.shared.Payment.Type.CRA_0005);
	}

	private boolean taxAndQuoteFull() {
		com.esferalia.aon.gwt.payroll.shared.Payment.Type type = getType();
		return (type.isBBCCIncluded() && !type.isBBCCExcluded());
	}

	private boolean taxAndQuoteNone() {
		com.esferalia.aon.gwt.payroll.shared.Payment.Type type = getType();
		return (!type.isBBCCIncluded() && type.isBBCCExcluded());
	}

	private void initTaxedAndQuoteLB() {
		taxedTypeLB.clear();
		taxedTypeLB.addItem("Importe integro", "FULL");
		taxedTypeLB.addItem("Exento", "NONE");
		taxedTypeLB.addItem("Personalizado", "CUSTOM");
		taxedTypeLB.addItem("Ingreso a Cuenta", "IRPF_CTA_ESP");

		taxedTypeLB.addChangeHandler(e -> {
			int selected = taxedTypeLB.getSelectedIndex();
			switch (selected) {
			case 0:
				taxedFxButton.setVisible(false);
				taxedExpression.setEnabled(false);
				taxedExpression.setValue("_P");
				break;
			case 1:
				taxedFxButton.setVisible(false);
				taxedExpression.setEnabled(false);
				taxedExpression.setValue("0.00");
				break;
			case 3:
				taxedFxButton.setVisible(false);
				taxedExpression.setEnabled(false);
				taxedExpression.setValue("BASE_CTA_ESP=_P");
				break;
			default:
				taxedFxButton.setVisible(true);
				taxedExpression.setEnabled(true);
				taxedExpression.setValue("");
				break;
			}
		});

		taxedFxButton.addClickHandler(e -> {
			final FxDialog fxDialog = new FxDialog(contextProvider);
			fxDialog.setExpression(taxedExpression.getValue());
			fxDialog.center();
			fxDialog.show();

			fxDialog.addCloseHandler(ev -> {
				taxedExpression.setFocus(true);
				if (fxDialog.isAccepted()) {
					taxedExpression.setText(fxDialog.getExpression());
				}
			});
		});

		quoteTypeLB.clear();
		quoteTypeLB.addItem("Importe integro", "FULL");
		quoteTypeLB.addItem("Exento", "NONE");
		quoteTypeLB.addItem("Prorratear", "PRORRAT");
		quoteTypeLB.addItem("Personalizado", "CUSTOM");

		quoteTypeLB.addChangeHandler(e -> {
			int selected = quoteTypeLB.getSelectedIndex();
			switch (selected) {
			case 0:
				quoteFxButton.setVisible(false);
				quoteExpression.setEnabled(false);
				quoteExpression.setValue("_P");
				break;
			case 1:
				quoteFxButton.setVisible(false);
				quoteExpression.setEnabled(false);
				quoteExpression.setValue("0.00");
				break;
			case 2:
				quoteFxButton.setVisible(false);
				quoteExpression.setEnabled(false);
				quoteExpression.setValue("PRORRATEAR()");
				break;
			default:
				quoteFxButton.setVisible(true);
				quoteExpression.setEnabled(true);
				quoteExpression.setValue("");
				break;
			}
		});

		quoteFxButton.addClickHandler(e -> {
			final FxDialog fxDialog = new FxDialog(contextProvider);
			fxDialog.setExpression(quoteExpression.getValue());
			fxDialog.center();
			fxDialog.show();

			fxDialog.addCloseHandler(ev -> {
				quoteExpression.setFocus(true);
				if (fxDialog.isAccepted()) {
					quoteExpression.setText(fxDialog.getExpression());
				}
			});
		});

		taxedTypeLB.setSelectedIndex(0);
		quoteTypeLB.setSelectedIndex(0);
		DomEvent.fireNativeEvent(Document.get().createChangeEvent(), taxedTypeLB);
		DomEvent.fireNativeEvent(Document.get().createChangeEvent(), quoteTypeLB);

	}

	private void initMonthLB() {
		monthLB.clear();
		monthLB.addItem("-", "");
		monthLB.addItem("Enero", "0");
		monthLB.addItem("Febrero", "1");
		monthLB.addItem("Marzo", "2");
		monthLB.addItem("Abril", "3");
		monthLB.addItem("Mayo", "4");
		monthLB.addItem("Junio", "5");
		monthLB.addItem("Julio", "6");
		monthLB.addItem("Agosto", "7");
		monthLB.addItem("Septiembre", "8");
		monthLB.addItem("Octubre", "9");
		monthLB.addItem("Noviembre", "10");
		monthLB.addItem("Diciembre", "11");

		monthStartLB.clear();
		monthStartLB.addItem("Enero", "ENERO");
		monthStartLB.addItem("Febrero", "FEBRERO");
		monthStartLB.addItem("Marzo", "MARZO");
		monthStartLB.addItem("Abril", "ABRIL");
		monthStartLB.addItem("Mayo", "MAYO");
		monthStartLB.addItem("Junio", "JUNIO");
		monthStartLB.addItem("Julio", "JULIO");
		monthStartLB.addItem("Agosto", "AGOSTO");
		monthStartLB.addItem("Septiembre", "SEPTIEMBRE");
		monthStartLB.addItem("Octubre", "OCTUBRE");
		monthStartLB.addItem("Noviembre", "NOVIEMBRE");
		monthStartLB.addItem("Diciembre", "DICIEMBRE");
		monthStartLB.addChangeHandler(e -> createUpdatePayment());

		monthEndLB.clear();
		monthEndLB.addItem("Enero", "ENERO");
		monthEndLB.addItem("Febrero", "FEBRERO");
		monthEndLB.addItem("Marzo", "MARZO");
		monthEndLB.addItem("Abril", "ABRIL");
		monthEndLB.addItem("Mayo", "MAYO");
		monthEndLB.addItem("Junio", "JUNIO");
		monthEndLB.addItem("Julio", "JULIO");
		monthEndLB.addItem("Agosto", "AGOSTO");
		monthEndLB.addItem("Septiembre", "SEPTIEMBRE");
		monthEndLB.addItem("Octubre", "OCTUBRE");
		monthEndLB.addItem("Noviembre", "NOVIEMBRE");
		monthEndLB.addItem("Diciembre", "DICIEMBRE");
		monthEndLB.addChangeHandler(e -> createUpdatePayment());

	}

	private Short getMonth() {
		com.esferalia.aon.gwt.payroll.shared.Payment.Type type = getType();
		return type == com.esferalia.aon.gwt.payroll.shared.Payment.Type.CRA_0005 && monthLB.getSelectedIndex() != 0
				? Short.parseShort(monthLB.getSelectedValue())
				: null;
	}

	private void initPartialityButton() {
		getEnableDisableButton(partialityButton, hasPartiality);
	}

	private void setExtraPayFieldsVisible(boolean visible) {
//		extraPayDatePanel.setVisible(visible);
//		extraPayCalcPanel.setVisible(visible);
		extraPayTypePanel.setVisible(visible);
	}

	// -------------------------------------------- ToggleButton.Methods

	private void getEnableDisableButton(Button button, boolean disabled) {
		button.removeStyleName(disabled ? AON.AON_ICON_DISABLE : AON.AON_ICON_ENABLE);
		button.removeStyleName(AON.AON_NO_MARGIN);
		button.removeStyleName(AON.AON_EDIT_DATA_TABLE_BUTTON);

		button.setStyleName(!disabled ? AON.AON_ICON_DISABLE : AON.AON_ICON_ENABLE);
		button.setStyleName(AON.AON_NO_MARGIN, true);
		button.setStyleName(AON.AON_EDIT_DATA_TABLE_BUTTON, true);
	}

	private boolean isActiveToggleButton(Button button) {
		return AonStringUtils.containsIgnoreCase(button.getStyleName(), AON.AON_ICON_ENABLE);
	}

	// -------------------------------------------- UiHandler

	@UiHandler("paymentType")
	void onPaymentTypeChange(ChangeEvent event) {
		paymentTypeListBox.setEnabled(AonStringUtils.equalsIgnoreCase(paymentType.getSelectedValue(), "MANUAL"));

		if (AonStringUtils.equalsIgnoreCase(paymentType.getSelectedValue(), "MEJORA_IT")) {
			showGtzdoPage();
			gtzdoWizard.setPaymentType(paymentType.getSelectedValue());
			paymentTypeListBox.setSelected(Payment.Type.CRA_0055);
			enableOrDisablePayments();

			paymentConcept.setValue("GARANTIZADO");
			paymentExpressionTitle.setText("Expresion (Garantizado)");
			paymentExpression.setValue("", true);

			gtzdoWizard.fireGtzdoType();
			gtzdoWizard.fireGtzdoAboutType();
		} else {
			paymentExpressionTitle.setText("Expresion");

			showFirstPage();

			// Init Periodicity
			initPeriodicityType(paymentType.getSelectedValue());

			// Extra panel
			checkExtraPanel();
			checIfExtra();

			createUpdatePayment();
			checkPartialityButton();
			
			// ExtraPayType
			DomEvent.fireNativeEvent(Document.get().createChangeEvent(), extraPayType);
			DomEvent.fireNativeEvent(Document.get().createChangeEvent(), extraPayType2);
			enableOrDisablePayments();
		}
	}

	@UiHandler({ "periodicityType", "extraName" })
	void onPeriodicityTypeChange(ChangeEvent event) {
		enableOrDisableMonthStartEnd();
		createUpdatePayment();
		checkPartialityButton();
		checkIfWeekDays();
		checkManual();
		checkProrrat0005();
	}

	@UiHandler({ "mondayCB", "tuesdayCB", "wednesdayCB", "thursdayCB", "fridayCB", "saturdayCB", "sundayCB" })
	void onWeekDaysValueChange(ValueChangeEvent<Boolean> event) {
		createUpdatePayment();
		checkPartialityButton();
	}

//	@UiHandler("extraPayDate")
//	void onExtraPayDateChange(ChangeEvent event) {
//		String issueValue = extraPayDate.getValue();
//		if (matchIssueValue(issueValue)) {
//			issueValue = parseIssueValue(issueValue);
//			extraPayDate.setValue(issueValue);
//			if (null == issueValue) {
//				extraPayDate.getElement().getStyle().setBorderColor("red");
//				extraPayDate.setTitle("La fecha introducida no es correcta");
//				extraPayDate.setValue("");
//			} else {
//				extraPayDate.getElement().getStyle().setBorderColor("black");
//				extraPayDate.setTitle("");
//			}
//		} else {
//			extraPayDate.getElement().getStyle().setBorderColor("red");
//			extraPayDate.setTitle("La fecha introducida no es correcta");
//			extraPayDate.setValue("");
//		}
//	}

	@UiHandler("extraPayType")
	void onExtraPayTypeChange(ChangeEvent event) {
		if (AonStringUtils.equalsIgnoreCase(extraPayType.getSelectedValue(), "VARIABLE")) {
			paymentsDataGridPanel.getElement().getStyle().setDisplay(Display.NONE);

			partialityPanel.getElement().getStyle().clearDisplay();

			hasPartiality = false;
			partialityButton.setEnabled(true);
			getEnableDisableButton(partialityButton, hasPartiality);
			partialityButton.removeStyleName(style.visibilityDisabled());

			String extraNameValue = extraName.getValue();
			paymentExpression.setValue(
					AonStringUtils.isNotBlank(extraNameValue) ? extraNameValue.replaceAll("\\s", "_").toUpperCase()
							: "SIN_DEFINIR",
					true);

		} else {
			paymentsDataGridPanel.getElement().getStyle().clearDisplay();
			partialityPanel.getElement().getStyle().setDisplay(Display.NONE);
			paymentExpression.setValue("", true);
		}
	}
	
	@UiHandler("extraPayType2")
	void onExtraPayType2Change(ChangeEvent event) {
		if (AonStringUtils.equalsIgnoreCase(extraPayType2.getSelectedValue(), "VARIABLE")) {
			paymentsDataGridPanel.getElement().getStyle().setDisplay(Display.NONE);

			partialityPanel.getElement().getStyle().clearDisplay();

			hasPartiality = false;
			partialityButton.setEnabled(true);
			getEnableDisableButton(partialityButton, hasPartiality);
			partialityButton.removeStyleName(style.visibilityDisabled());

			String extraNameValue = extraName.getValue();
			paymentExpression.setValue(
					AonStringUtils.isNotBlank(extraNameValue) ? extraNameValue.replaceAll("\\s", "_").toUpperCase()
							: "SIN_DEFINIR",
					true);

		} else {
			paymentsDataGridPanel.getElement().getStyle().clearDisplay();
			partialityPanel.getElement().getStyle().setDisplay(Display.NONE);
			paymentExpression.setValue("", true);
		}
	}

	@UiHandler("partialityButton")
	void onPartialityButtonClick(ClickEvent event) {
		Boolean oldValue = isActiveToggleButton(partialityButton);
		Boolean value = !oldValue;
		hasPartiality = value;
		getEnableDisableButton(partialityButton, value);
		checkPartiality();
	}

	@UiHandler("paymentConcept")
	void onPaymentConceptChange(ValueChangeEvent<String> event) {
		createUpdatePayment();
	}

	@UiHandler("paymentExpression")
	void onPaymentExpressionChange(ValueChangeEvent<String> event) {
//		String expression = paymentExpression.getValue();
//		acceptBtnDialog.setEnabled(!AonStringUtils.isBlank(expression));
//
//		if (!AonStringUtils.isBlank(expression)) {
//			acceptBtnDialog.getElement().getStyle().setVisibility(Visibility.VISIBLE);
//			acceptBtnDialog.getElement().getStyle().setDisplay(Display.BLOCK);
//		}
	}

	// -------------------------------------------- UiHandler auxiliar method

	private void checIfExtra() {
		if (AonStringUtils.equalsIgnoreCase(paymentType.getSelectedValue(), "PAGA_EXTRA")) {
			paymentTypeListBox.setSelected(Payment.Type.CRA_0004);
			setExtraPayFieldsVisible(true);
			periodicityType.setEnabled(false);
			paymentExpression.setEnabled(true);
			normalFieldsPanel.getElement().getStyle().setDisplay(Display.NONE);
			extraFieldsPanel.getElement().getStyle().clearDisplay();

			extraType.setSelectedIndex(2);
			DomEvent.fireNativeEvent(Document.get().createChangeEvent(), extraType);
		} else {
			paymentTypeListBox.setSelected(Payment.Type.CRA_0001);
			setExtraPayFieldsVisible(false);
			periodicityType.setEnabled(true);
			paymentExpression.setEnabled(false);
			extraFieldsPanel.getElement().getStyle().setDisplay(Display.NONE);
			normalFieldsPanel.getElement().getStyle().clearDisplay();
		}
		
	}

	private void checkIfWeekDays() {
		String periodicityTypeValue = periodicityType.getSelectedValue();
		UIObject.setVisible(weekDaysPanel.getElement(),
				AonStringUtils.containsIgnoreCase(periodicityTypeValue, "DIAS_SEMANA"));
	}

	private void checkManual() {
		paymentExpression.setEnabled(AonStringUtils.equalsIgnoreCase(periodicityType.getSelectedValue(), "MANUAL") || 
				AonStringUtils.equalsIgnoreCase(paymentType.getSelectedValue(), "PAGA_EXTRA"));
	}

	private void checkProrrat0005() {
		boolean enabled = getType() == com.esferalia.aon.gwt.payroll.shared.Payment.Type.CRA_0005
				&& AonStringUtils.containsIgnoreCase(periodicityType.getSelectedValue(), "PRORRATEAR");

		if (enabled) {
			monthLB.setEnabled(false);
			monthLB.getElement().getStyle().setVisibility(Visibility.VISIBLE);
			monthLB.getElement().getStyle().setDisplay(Display.BLOCK);
		} else {
			monthLB.setEnabled(true);
			monthLB.getElement().getStyle().clearVisibility();
			monthLB.getElement().getStyle().clearDisplay();
		}

		if (Boolean.TRUE.equals(enabled))
			monthLB.setSelectedIndex(0);

		if (enabled) {
			quoteTypeLB.setEnabled(false);
			quoteTypeLB.getElement().getStyle().setVisibility(Visibility.VISIBLE);
			quoteTypeLB.getElement().getStyle().setDisplay(Display.BLOCK);
		} else {
			quoteTypeLB.setEnabled(true);
			quoteTypeLB.getElement().getStyle().clearVisibility();
			quoteTypeLB.getElement().getStyle().clearDisplay();
		}

		if (Boolean.TRUE.equals(enabled))
			quoteTypeLB.setSelectedIndex(0);

		DomEvent.fireNativeEvent(Document.get().createChangeEvent(), monthLB);
		DomEvent.fireNativeEvent(Document.get().createChangeEvent(), quoteTypeLB);
		DomEvent.fireNativeEvent(Document.get().createChangeEvent(), monthStartLB);
	}

	private void checkExtraPanel() {
		if (AonStringUtils.equalsIgnoreCase(paymentType.getSelectedValue(), "SALARIO_BASE"))
			extraPanel.getElement().getStyle().setDisplay(Display.NONE);
		else {
			if (AonStringUtils.equalsIgnoreCase(paymentType.getSelectedValue(), "MANUAL"))
				extraLabel.setText("Descripci\u00F3n");
			if (AonStringUtils.equalsIgnoreCase(paymentType.getSelectedValue(), "PLUS_SALARIAL"))
				extraLabel.setText("Descripci\u00F3n plus salarial");
			if (AonStringUtils.equalsIgnoreCase(paymentType.getSelectedValue(), "PLUS_EXTRA_SALARIAL"))
				extraLabel.setText("Descripci\u00F3n plus extra salarial");
			if (AonStringUtils.equalsIgnoreCase(paymentType.getSelectedValue(), "PAGA_EXTRA"))
				extraLabel.setText("Descripci\u00F3n paga extra");

			extraPanel.getElement().getStyle().clearDisplay();
			extraName.setFocus(true);
		}
	}

	// -------------------------------------------- DeckPanel.Methods

	private void showFirstPage() {
		firstPage.setVisible(true);
		gtzdoWizard.setVisible(false);
		container.setHeight("580px");
		centerDialog();
	}

	private void showGtzdoPage() {
		firstPage.setVisible(false);
		gtzdoWizard.setVisible(true);
		container.setHeight("580px");
		centerDialog();
	}

	// -------------------------------------------- Payment

	private void createUpdatePayment() {
		createPaymentConcept();
		createPaymentDescriptionPos();
		createPaymentDescription();
		createPaymentExpression();
		createQuoteExpression();
	}

	private void createPaymentConcept() {
		if (AonStringUtils.isBlank(paymentConcept.getValue())
				|| AonStringUtils.equalsIgnoreCase(paymentConcept.getValue(), "SIN_DEFINIR")) {
			String conceptName = getConceptName();
			paymentConcept.setValue(conceptName);

			paymentConcept.setEnabled(!AonStringUtils.equalsIgnoreCase(conceptName, "SALARIO_BASE"));
		}
	}

	private String getConceptName() {
		String paymentTypeValue = paymentType.getSelectedValue();

		if (AonStringUtils.equalsIgnoreCase(paymentTypeValue, "SALARIO_BASE"))
			return "SALARIO_BASE";
		else if (AonStringUtils.containsIgnoreCase(paymentTypeValue, "MANUAL")) {
			String nameValue = getParsedConcept();
			return AonStringUtils.isNotBlank(nameValue) ? nameValue.toUpperCase() : "SIN_DEFINIR";
		} else if (AonStringUtils.containsIgnoreCase(paymentTypeValue, "PLUS_EXTRA")) {
			String extraNameValue = getParsedConcept();
			return AonStringUtils.isNotBlank(extraNameValue) ? "PLUS_XS_" + extraNameValue.toUpperCase()
					: "PLUS_XS_SIN_DEFINIR";
		} else if (AonStringUtils.containsIgnoreCase(paymentTypeValue, "PLUS")) {
			String extraNameValue = getParsedConcept();
			return AonStringUtils.isNotBlank(extraNameValue) ? "PLUS_" + extraNameValue.toUpperCase()
					: "PLUS_SIN_DEFINIR";
		} else if (AonStringUtils.containsIgnoreCase(paymentTypeValue, "PAGA_EXTRA")) {
			String extraNameValue = getParsedConcept();
			return AonStringUtils.isNotBlank(extraNameValue)
					? "EXTRA_" + extraNameValue.replaceAll("\\s", "_").toUpperCase()
					: "EXTRA_SIN_DEFINIR";
		} else
			return "SIN_DEFINIR";
	}

	private String getParsedConcept() {
		String nameValue = extraName.getValue();
		if (AonStringUtils.isNotBlank(nameValue))
			nameValue = nameValue.replaceAll("\\s", "_");
		return nameValue;
	}

	private void createPaymentDescriptionPos() {
		String paymenteDescriptionPosValue = "";

		String paymentTypeValue = paymentType.getSelectedValue();
		String periodicityTypeValue = periodicityType.getSelectedItemText();

		if (AonStringUtils.equalsIgnoreCase(paymentTypeValue, "SALARIO_BASE")) {
			switch (periodicityTypeValue) {
			case "ANUAL":
				paymenteDescriptionPosValue = "01";
				break;
			case "MENSUAL":
				paymenteDescriptionPosValue = "02";
				break;
			case "DIARIO":
				paymenteDescriptionPosValue = "03";
				break;
			case "HORAS":
				paymenteDescriptionPosValue = "04";
				break;
			default:
				paymenteDescriptionPosValue = "05";
				break;
			}
		} else if (AonStringUtils.equalsIgnoreCase(paymentTypeValue, "PLUS_SALARIAL")) {
			switch (periodicityTypeValue) {
			case "MENSUAL":
				paymenteDescriptionPosValue = "10";
				break;
			case "DIAS TRABAJADOS":
				paymenteDescriptionPosValue = "11";
				break;
			case "DIAS EFECTIVOS":
				paymenteDescriptionPosValue = "12";
				break;
			case "FIJO":
				paymenteDescriptionPosValue = "13";
				break;
			default:
				paymenteDescriptionPosValue = "14";
				break;
			}
		} else if (AonStringUtils.equalsIgnoreCase(paymentTypeValue, "PLUS_EXTRA_SALARIAL")) {
			switch (periodicityTypeValue) {
			case "MENSUAL":
				paymenteDescriptionPosValue = "20";
				break;
			case "DIAS TRABAJADOS":
				paymenteDescriptionPosValue = "21";
				break;
			case "DIAS EFECTIVOS":
				paymenteDescriptionPosValue = "22";
				break;
			case "FIJO":
				paymenteDescriptionPosValue = "23";
				break;
			default:
				paymenteDescriptionPosValue = "24";
				break;
			}
		} else if (AonStringUtils.equalsIgnoreCase(paymentTypeValue, "PAGA_EXTRA")) {
			paymenteDescriptionPosValue = "93";
		} else if (AonStringUtils.equalsIgnoreCase(paymentTypeValue, "MANUAL")) {
			paymenteDescriptionPosValue = "99";
		}

		paymentDescriptionPos.setValue(Integer.parseInt(paymenteDescriptionPosValue));
	}

	private void createPaymentDescription() {
		String paymentDescriptionText = "";

		String paymentTypeValue = paymentType.getSelectedValue();
		paymentTypeValue = paymentTypeValue.replaceAll("_", " ");

		if (AonStringUtils.containsIgnoreCase(paymentTypeValue, "PLUS EXTRA")) {
			String extraNameValue = extraName.getValue();
			if (AonStringUtils.isNotBlank(extraNameValue))
				paymentTypeValue = "PLUS EXTRA SALARIAL " + extraNameValue.toUpperCase();
		} else if (AonStringUtils.containsIgnoreCase(paymentTypeValue, "MANUAL")) {
			String extraNameValue = extraName.getValue();
			if (AonStringUtils.isNotBlank(extraNameValue))
				paymentTypeValue = extraNameValue.toUpperCase();
		} else if (AonStringUtils.containsIgnoreCase(paymentTypeValue, "PLUS")) {
			String extraNameValue = extraName.getValue();
			if (AonStringUtils.isNotBlank(extraNameValue))
				paymentTypeValue = "PLUS " + extraNameValue.toUpperCase();
		} else if (AonStringUtils.equalsIgnoreCase(paymentTypeValue, "PAGA EXTRA")) {
			String extraNameValue = extraName.getValue();
			if (AonStringUtils.isNotBlank(extraNameValue))
				paymentTypeValue = "PAGA EXTRA " + extraNameValue.toUpperCase();
		}

		String periodicityTypeValue = periodicityType.getSelectedItemText();
		periodicityTypeValue = periodicityTypeValue.replaceAll("_", " ");

		paymentDescriptionText = paymentTypeValue + " (" + periodicityTypeValue + ")";
		this.paymentDescription.setValue(paymentDescriptionText);
	}

	private void createPaymentExpression() {
		String paymentExpressionText = "";

		String paymentTypeValue = paymentType.getSelectedValue();
		String periodicity = periodicityType.getSelectedValue();

		if (AonStringUtils.equalsIgnoreCase(paymentTypeValue, "SALARIO_BASE"))
			paymentExpressionText = createBaseSalaryExpression();

		if (AonStringUtils.containsIgnoreCase(paymentTypeValue, "MANUAL")
				&& AonStringUtils.containsIgnoreCase(periodicity, "PRORRATEAR"))
			paymentExpressionText = createProrratExpression();
		else if (AonStringUtils.containsIgnoreCase(paymentTypeValue, "PLUS")
				|| AonStringUtils.containsIgnoreCase(paymentTypeValue, "MANUAL"))
			paymentExpressionText = createPlusExpression();

		this.paymentExpression.setValue(paymentExpressionText, true);
	}

	private void createQuoteExpression() {
		String paymentTypeValue = paymentType.getSelectedValue();
		String periodicity = periodicityType.getSelectedValue();

		if (AonStringUtils.containsIgnoreCase(paymentTypeValue, "MANUAL")
				&& !AonStringUtils.containsIgnoreCase(periodicity, "PRORRATEAR")
				&& getType() == com.esferalia.aon.gwt.payroll.shared.Payment.Type.CRA_0005) {
			quoteTypeLB.setEnabled(false);
			quoteTypeLB.getElement().getStyle().setVisibility(Visibility.VISIBLE);
			quoteTypeLB.getElement().getStyle().setDisplay(Display.BLOCK);
			quoteTypeLB.setSelectedIndex(3);
			quoteExpression.setValue(
					"PRORRATEAR(" + monthStartLB.getSelectedValue() + "," + monthEndLB.getSelectedValue() + ")");
		} else {
			quoteTypeLB.setEnabled(true);
			quoteExpression.setValue("");
		}
	}

	private String createBaseSalaryExpression() {
		String expression = "";
		String periodicity = periodicityType.getSelectedItemText();

		if (AonStringUtils.equals(periodicity, "HORAS"))
			periodicity = "HORA";

		expression = "SALARIO_" + periodicity;
		expression += periodicityType.getSelectedValue();

		return expression;
	}

	private String createProrratExpression() {
		String expression = "";
		String variable = "";

		variable = (AonStringUtils.isBlank(paymentConcept.getValue()) ? "SIN_DEFINIR"
				: paymentConcept.getValue().toUpperCase());

		expression = variable;
		if (AonStringUtils.isNotBlank(expression))
			expression = expression.replaceAll("\\s", "_");

		String newExpression = "PRORRATEAR(" + expression + "," + monthStartLB.getSelectedValue() + ","
				+ monthEndLB.getSelectedValue() + ")";
		expression = newExpression;

		return expression;
	}

	private String createPlusExpression() {
		String expression = "";
		String variable = "";

		variable = (AonStringUtils.isBlank(paymentConcept.getValue()) ? "SIN_DEFINIR"
				: paymentConcept.getValue().toUpperCase());

		expression = variable;
		if (AonStringUtils.isNotBlank(expression))
			expression = expression.replaceAll("\\s", "_");

		String periodicityTypeValue = periodicityType.getSelectedValue();
		if (AonStringUtils.equalsIgnoreCase(periodicityTypeValue, "FIJO")) {
			String newExpression = "FRACCIONAR(" + expression + ")";
			expression = newExpression;
		} else if (!AonStringUtils.containsIgnoreCase(periodicityTypeValue, "MANUAL")) {
			expression += AonStringUtils.containsIgnoreCase(periodicityTypeValue, "DIAS_SEMANA")
					? checkWeekDaysExpression()
					: periodicityTypeValue;
		} else
			paymentExpression.setEnabled(true);

		return expression;
	}

	private String checkWeekDaysExpression() {
		List<String> selectedWeekDays = new ArrayList<>();
		if (Boolean.TRUE.equals(mondayCB.getValue()))
			selectedWeekDays.add("DIAS_LUNES");
		if (Boolean.TRUE.equals(tuesdayCB.getValue()))
			selectedWeekDays.add("DIAS_MARTES");
		if (Boolean.TRUE.equals(wednesdayCB.getValue()))
			selectedWeekDays.add("DIAS_MIERCOLES");
		if (Boolean.TRUE.equals(thursdayCB.getValue()))
			selectedWeekDays.add("DIAS_JUEVES");
		if (Boolean.TRUE.equals(fridayCB.getValue()))
			selectedWeekDays.add("DIAS_VIERNES");
		if (Boolean.TRUE.equals(saturdayCB.getValue()))
			selectedWeekDays.add("DIAS_SABADO");
		if (Boolean.TRUE.equals(sundayCB.getValue()))
			selectedWeekDays.add("DIAS_DOMINGO");

		String weekDaysExpression = "";
		if (!selectedWeekDays.isEmpty()) {
			weekDaysExpression = " * (";
			for (int i = 0; i < selectedWeekDays.size() - 1; i++)
				weekDaysExpression += selectedWeekDays.get(i) + " + ";
			weekDaysExpression += selectedWeekDays.get(selectedWeekDays.size() - 1) + ")";
		}
		return weekDaysExpression;
	}

	private void checkPartialityButton() {
		String periodicityTypeValue = periodicityType.getSelectedValue();
		if (AonStringUtils.equalsIgnoreCase(paymentType.getSelectedValue(), "PAGA_EXTRA")
				&& !AonStringUtils.equalsIgnoreCase(periodicityType.getSelectedValue(), "PRORRATEO")) {
			partialityPanel.getElement().getStyle().setDisplay(Display.NONE);
		} else {
			partialityPanel.getElement().getStyle().clearDisplay();
			String expression = paymentExpression.getValue();
			if (AonStringUtils.containsIgnoreCase(expression, "DIAS_EFECTIVOS")
					|| AonStringUtils.containsIgnoreCase(expression, "FRACCIONAR")
					|| AonStringUtils.containsIgnoreCase(periodicityTypeValue, "DIAS_SEMANA")) {
				hasPartiality = false;
				partialityButton.setEnabled(true);
				getEnableDisableButton(partialityButton, hasPartiality);
				partialityButton.removeStyleName(style.visibilityDisabled());
			} else if (AonStringUtils.containsIgnoreCase(expression, "HORAS_TRABAJADAS")
					|| AonStringUtils.containsIgnoreCase(expression, "JORNADAS_REALES")) {
				hasPartiality = false;
				partialityButton.setEnabled(false);
				getEnableDisableButton(partialityButton, hasPartiality);
				partialityButton.addStyleName(style.visibilityDisabled());
			} else {
				hasPartiality = true;
				partialityButton.setEnabled(false);
				getEnableDisableButton(partialityButton, hasPartiality);
				partialityButton.addStyleName(style.visibilityDisabled());
			}
		}
	}

	private void checkPartiality() {
		String extraPayTypeValue = extraPayType.getSelectedValue();
		String expression = paymentExpression.getValue();
		String paymentTypeValue = paymentType.getSelectedValue();

		if (hasPartiality && (AonStringUtils.containsIgnoreCase(expression, "DIAS_EFECTIVOS")
				|| AonStringUtils.containsIgnoreCase(expression, "FRACCIONAR")
				|| AonStringUtils.containsIgnoreCase(expression, "HORAS_TRABAJADAS")
				|| AonStringUtils.containsIgnoreCase(expression, "JORNADAS_REALES")
				|| AonStringUtils.containsIgnoreCase(extraPayTypeValue, "VARIABLE"))) {
			if (AonStringUtils.containsIgnoreCase(extraPayTypeValue, "VARIABLE")
					&& AonStringUtils.equalsIgnoreCase(paymentTypeValue, "PAGA_EXTRA"))
				paymentExpression.setValue(paymentExpression.getValue() + " * DIAS_TRABAJADOS / DIAS_MES", true);
			else
				paymentExpression.setValue(paymentExpression.getValue() + " * COEFICIENTE_PARCIALIDAD", true);
		} else {
			String paymentExpressionValue = paymentExpression.getValue();
			if (AonStringUtils.containsIgnoreCase(paymentExpressionValue, " * COEFICIENTE_PARCIALIDAD")) {
				String newExpression = paymentExpressionValue.split(" \\* COEFICIENTE_PARCIALIDAD")[0];
				paymentExpression.setValue(newExpression.trim(), true);
			}
			if (AonStringUtils.containsIgnoreCase(paymentExpressionValue, " * DIAS_TRABAJADOS / DIAS_MES")) {
				String newExpression = paymentExpressionValue.split(" \\* DIAS_TRABAJADOS / DIAS_MES")[0];
				paymentExpression.setValue(newExpression.trim(), true);
			}
		}
	}

	// -------------------------------------------- Issue Date Extra Pay

	private boolean matchIssueValue(String issueValue) {
		RegExp issuePattern = RegExp.compile("\\d{2}-*/*\\d{2}");
		return issuePattern.test(issueValue);
	}

	private String parseIssueValue(String issueValue) {
		issueValue = issueValue.replaceAll("[-]*", "");
		issueValue = issueValue.replaceAll("[/]*", "");

		if (isRealDate(issueValue))
			return issueValue.substring(0, 2) + "/" + issueValue.substring(2, 4);
		else
			return null;
	}

	private boolean isRealDate(String issueValue) {
		Integer dayOfMonth = Integer.parseInt(issueValue.substring(0, 2));
		Integer month = Integer.parseInt(issueValue.substring(2, 4)) - 1;

		if (dayOfMonth > 31 || month > 11)
			return false;

		Date lastDayOfMonth = DateUtils.getLastDayOfMonth(new Date(new Date().getYear(), month, 1));

		try {
			Date date = new Date(new Date().getYear(), month, dayOfMonth);
			if (lastDayOfMonth.getDate() == date.getDate() && lastDayOfMonth.getMonth() == date.getMonth())
				return true;
			else if (lastDayOfMonth.getMonth() == date.getMonth())
				return true;
			else
				return false;
		} catch (Exception e) {
			return false;
		}
	}

	// -------------------------------------------- FooterButtons

	private void initFooterButtons() {
		Button closeBtnDialog = new Button();
		closeBtnDialog.setStyleName(AON.CSS.aonCancelButtonSmall());
		closeBtnDialog.setText(AON.MSG.cancelAction());
		closeBtnDialog.addClickHandler(e -> hide());

		closeBtnDialog.getElement().getStyle().setMarginRight(10, Unit.PX);

		buttonsPanel.add(closeBtnDialog);

		acceptBtnDialog = new Button();
		acceptBtnDialog.setStyleName(AON.CSS.aonOkButtonSmall());
		acceptBtnDialog.setText(AON.MSG.accept());
		acceptBtnDialog.addClickHandler(e -> {
			if (AonStringUtils.equalsIgnoreCase(paymentType.getSelectedItemText(), "PAGA_EXTRA")) {
				createExtraPayment();
				onExtraAccept(paymentExtra, extra);
			} else if (AonStringUtils.equalsIgnoreCase(paymentType.getSelectedItemText(), "MEJORA_IT")) {
				List<Payment> gtzdoPayments = gtzdoWizard.createPayments();
				onGtzdoAccept(gtzdoPayments);
			} else {
				createPayment();
				onAccept(payment);
			}
			hide();
		});

		buttonsPanel.add(acceptBtnDialog);
	}

	private void createPayment() {
		String description = null == paymentDescriptionPos.getValue() ? paymentDescription.getValue()
				: "[" + paymentDescriptionPos.getValue() + "] " + paymentDescription.getValue();

		payment.setId(-1);
		payment.setDescription(description);
		payment.setExpression(createExpression());
		payment.setIrpfExpression(getTaxedExpression());
		payment.setQuoteExpression(getQuoteExpression());
		payment.setType(paymentTypeListBox.getSelected());
		payment.setSalaryType(Salary.Type.SALARY);
		payment.setName(paymentConcept.getValue());
		payment.setMonth(getMonth());
	}

	public String getTaxedExpression() {
		String selected = taxedTypeLB.getSelectedValue();
		return getExpression(selected, taxedExpression.getValue());
	}

	public String getQuoteExpression() {
		String selected = quoteTypeLB.getSelectedValue();
		return getExpression(selected, quoteExpression.getValue());
	}

	public String getExpression(String lbValue, String src) {
		if (AonStringUtils.equalsIgnoreCase(lbValue, "NONE")) {
			return "0.00"; // Avoid null'
		}
		if (AonStringUtils.equalsIgnoreCase(lbValue, "FULL")) {
			return "_P";
		}
		if (AonStringUtils.equalsIgnoreCase(lbValue, "PRORRAT")) {
			return "PRORRATEAR()";
		}
		if (AonStringUtils.equalsIgnoreCase(lbValue, "IRPF_CTA_ESP")) {
			return "BASE_CTA_ESP=_P";
		}
		// It must be CUSTOM
		return src;
	}

	private String createExpression() {
		String expression = "";
		String paymentExpressionValue = paymentExpression.getValue();
		String periodicityTypeValue = periodicityType.getSelectedValue();
		String extraNameValue = paymentConcept.getValue().toUpperCase();
		expression = paymentExpressionValue.replace(periodicityTypeValue, "");
		expression = expression.trim();

		if (paymentType.getSelectedIndex() == 0)
			expression = "/*read-only*/" + expression + "/**/";
		else if ((paymentType.getSelectedIndex() == 1 || paymentType.getSelectedIndex() == 2)
				&& AonStringUtils.equalsIgnoreCase(periodicityTypeValue, "FIJO")) {
			expression = "/*user*/" + extraNameValue + "/**/";
		} else
			expression = "/*user*/" + expression + "/**/";

		if (AonStringUtils.equalsIgnoreCase(periodicityTypeValue, "FIJO")) {
			String newExpression = "FRACCIONAR(/*read-only*/" + extraNameValue + "/**/)";
			expression = newExpression;
		} else if (!AonStringUtils.containsIgnoreCase(periodicityTypeValue, "DIAS_SEMANA")) {
			expression += periodicityTypeValue;
		}

		if (AonStringUtils.containsIgnoreCase(paymentType.getSelectedValue(), "MANUAL")
				&& AonStringUtils.containsIgnoreCase(periodicityTypeValue, "PRORRATEAR"))
			expression = createProrratExpression();

		return "/*wizard*/" + expression + "/**/";
	}

	private void createExtraPayment() {
		String description = null == paymentDescriptionPos.getValue() ? paymentDescription.getValue()
				: "[" + paymentDescriptionPos.getValue() + "] " + paymentDescription.getValue();

		paymentExtra.setId(-1);
		paymentExtra.setDescription(description);
		paymentExtra.setExpression(paymentExpression.getValue());
		paymentExtra.setIrpfExpression("_P");
		paymentExtra.setQuoteExpression("_P");
		paymentExtra.setType(paymentTypeListBox.getSelected());
		// Si tiene agreement extra es SalaryType EXTRA si no, es SALARY
		paymentExtra.setSalaryType(Salary.Type.EXTRA);
		paymentExtra.setName(paymentConcept.getValue());

//		try {
//			String monthIssueDate = extraIssueDate.getValue().split("/")[1];
//			Integer monthIssue = Integer.parseInt(monthIssueDate);
//			paymentExtra.setMonth(Short.parseShort((monthIssue - 1) + ""));
//		} catch (Exception e) {
//			paymentExtra.setMonth(null);
//		}

		createExtra(paymentExtra);
	}
	
	private void createExtra(Payment paymentExtra) {
		String extraTypeValue = extraType.getSelectedValue();
		if(AonStringUtils.equalsIgnoreCase(extraTypeValue, "Prorrateada"))
			extra = null;
		else {
			extra.setId(-1);
			extra.setIssueDate(extraIssueDate.getValue());
			extra.setPaymentId(paymentExtra.getId());
			extra.setPaymentDescription(paymentExtra.getDescription());
			extra.setAgreementDescription(paymentExtra.getDescription());
			
			String startMonth = extraStartDateMonth.getSelectedValue();
			if(AonStringUtils.equalsIgnoreCase(startMonth, "01")) startMonth = "01/01";
			else if(AonStringUtils.equalsIgnoreCase(startMonth, "07")) startMonth = "01/07";
			extra.setStartDate(startMonth + extraStartDateYear.getSelectedValue());
			
			String endMonth = extraEndDateMonth.getSelectedValue();
			if(AonStringUtils.equalsIgnoreCase(endMonth, "06")) endMonth = "30/06";
			else if(AonStringUtils.equalsIgnoreCase(endMonth, "12")) endMonth = "31/12";
			extra.setEndDate(endMonth + extraEndDateYear.getSelectedValue());
			extra.setIssueDate(extraIssueDate.getValue());
			
			if(AonStringUtils.isNotBlank(extraIssueDate.getValue())) {
				String issueDate = extraIssueDate.getValue();
				Short month = Short.parseShort(issueDate.split("/")[1]);
				month--;
				paymentExtra.setMonth(month);
			} else
				paymentExtra.setMonth(null);
			}
	}

//	private void createExtra(Payment paymentExtra) {
//		String periodicityValue = periodicityType.getSelectedValue();
//
//		if (AonStringUtils.equalsIgnoreCase(periodicityValue, "PRORRATEO")
//				|| AonStringUtils.isBlank(extraPayDate.getValue())) {
//			extra = null;
//			paymentExtra.setSalaryType(Salary.Type.SALARY);
//		} else {
//			extra.setId(-1);
//			extra.setIssueDate(extraPayDate.getValue());
//			createExtraPeriod();
//			extra.setPaymentId(paymentExtra.getId());
//			extra.setPaymentDescription(paymentExtra.getDescription());
//			extra.setAgreementDescription(paymentExtra.getDescription());
//		}
//	}
//
//	private void createExtraPeriod() {
//		Integer extraPayCalcIdx = extraPayCalc.getSelectedIndex();
//
//		switch (extraPayCalcIdx) {
//		case 0: // AÑO ACTUAL
//			extra.setStartDate("01/01");
//			extra.setEndDate("31/12");
//			break;
//		case 1: // AÑO ANTERIOR
//			extra.setStartDate("01/01 -1");
//			extra.setEndDate("31/12 -1");
//			break;
//		default: // ULTIMOS 12 MESES
//			createSpecialStartAndEnd();
//			break;
//		}
//	}
//
//	private void createSpecialStartAndEnd() {
//		String extraPayDateValue = extraPayDate.getValue();
//		String monthStr = extraPayDateValue.split("/")[1];
//		Integer month = Integer.parseInt(monthStr) - 1;
//
//		Date issueDate = DateUtils.getDate(month, DateUtils.getYear());
//		Date lastDayOfPreviusMonthIssueDate = DateUtils.getLastDayOfMonth(DateUtils.addMonths2Date(issueDate, -1));
//
//		extra.setStartDate("01/" + monthStr + " -1");
//		extra.setEndDate(lastDayOfPreviusMonthIssueDate.getDate() + "/"
//				+ (DateUtils.getMonth(lastDayOfPreviusMonthIssueDate) + 1));
//	}

	// -------------------------------------------- Auxiliar Methods

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

	// -------------------------------------------- Abstract Methods

	protected abstract void onAccept(Payment payment);

	protected abstract void onExtraAccept(Payment payment, Extra extra);

	protected abstract void onGtzdoAccept(List<Payment> payments);

	// -------------------------------------------- ExtraPayment.Methods

	private void enableOrDisablePayments() {
		Type type = getType();
		boolean visible = type == Type.CRA_0004 || type == Type.CRA_0055 || type == Type.CRA_0056;

		paymentsDataGridPanel.setVisible(visible);
		if (!visible)
			return;

		filterAvailablePayments();
	}

	public Payment.Type getType() {
		int index = paymentTypeListBox.getSelectedIndex();
		int code = Integer.parseInt(paymentTypeListBox.getValue(index));
		return Payment.Type.getByCode(code);
	}

	private void filterAvailablePayments() {
		List<Payment> availablePayments = payments.stream().filter(p -> p.getType() != getType())
				.collect(Collectors.toList());

		// Filter availablePayments only one entry for each type
		availablePayments = filterAvailablePayments(availablePayments);

		new ListDataProvider<Payment>(availablePayments).addDataDisplay(paymentsDataGrid);
	}

	private List<Payment> filterAvailablePayments(List<Payment> availablePayments) {
		List<Payment> availablePaymentsResult = new ArrayList<>();

		for (Payment paymentIt : availablePayments) {
			if (availablePaymentsResult.isEmpty()
					|| !availablePaymentsContainsType(paymentIt.getName(), availablePaymentsResult))
				availablePaymentsResult.add(paymentIt);
		}

		return availablePaymentsResult;
	}

	private boolean availablePaymentsContainsType(String searchName, List<Payment> availablePaymentsResult) {

		for (Payment paymentIt : availablePaymentsResult) {
			if (AonStringUtils.equalsIgnoreCase(paymentIt.getName(), searchName))
				return true;
		}

		return false;
	}

	// -------------------------------------------- ShowDialog

	public void showDialog() {
		// Show center
		Scheduler.get().scheduleDeferred(() -> {
			center();
			show();
		});
	}

	public void centerDialog() {
		// Show center
		Scheduler.get().scheduleDeferred(() -> center());
	}

}
