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
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DeckLayoutPanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionModel.AbstractSelectionModel;

public abstract class AgreementPaymentWizard extends AonCustomDialog {
	
	// -------------------------------------------- UiBinder
	
	interface AgreementPaymentDialogBinder extends UiBinder<Widget, AgreementPaymentWizard> {}

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
			paymentExpression.addKeyUpHandler( e -> synchronizer.schedule(2000));
		}

		@Override
		public boolean isSelected(Payment payment) {
			String expression =  paymentExpression.getValue();
			
			if ( AonStringUtils.isBlank(expression))
				return false;

			String var = getVariableName(payment);
			
			if ( AonStringUtils.isBlank(var))
				return false;
			
			return expression.contains(var);
		}

		@Override
		public void setSelected(Payment payment, boolean selected) {
			String expression = paymentExpression.getValue();
			
			String var = getVariableName(payment);
			if ( AonStringUtils.isBlank(var))
				return;
			
			if ( selected ) {
				if(AonStringUtils.isBlank(expression))
					expression += var;
				else
					expression += " + " + var;
			} else {
				// first of all remove variable.
				if(expression.contains(var + " + "))
					expression = expression.replace(var + " + ", ""); 
				else if(expression.contains(" + " + var))
					expression = expression.replace(" + " + var, ""); 
				else
					expression = expression.replace(var, ""); 
				
				if(AonStringUtils.isNotBlank(expression))
					expression.trim();
			}
			
			paymentExpression.setValue(expression);
			fireSelectionChangeEvent();
		}
		
	}
	
	// -------------------------------------------- PaymentsSelectionModel.Methods
	
	private static String getVariableName(Payment payment) {
		String name = payment.getName();
		if ( AonStringUtils.isNotBlank(name))
			return name;
		
		String description = payment.getDescription();
		if ( AonStringUtils.isNotBlank(description) )
			return description.toUpperCase()
					.replaceAll("\\s", "_")
					.replaceAll("\u00c1", "A")
					.replaceAll("\u00c9", "E")
					.replaceAll("\u00cd", "I")
					.replaceAll("\u00d3", "O")
					.replaceAll("\u00da", "U")
					.replaceAll("\u00dc", "U")

					.replaceAll("\u00d1", "N")
					.replaceAll("\\W", "")
					;
		return null;
	}
	
	// -------------------------------------------- UiField
	
	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String buttonFillPage();
		String lineFill();
		String visibilityDisabled();
	}
	
	@UiField
	DockLayoutPanel dockLayoutPanel;
	
	@UiField
	DeckLayoutPanel deckPanel;
	
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
	HTMLPanel periodicityPanel;
	
	@UiField
	ListBox periodicityType;
	
//	@UiField
//	HTMLPanel secondPage;
	
	@UiField
	HTMLPanel extraPayDatePanel;
	
	@UiField
	TextBox extraPayDate;
	
	@UiField
	HTMLPanel extraPayCalcPanel;
	
	@UiField
	ListBox extraPayCalc;
	
	@UiField
	HTMLPanel extraPayTypePanel;
	
	@UiField
	ListBox extraPayType;
	
	@UiField
	HTMLPanel extraPayValuePanel;
	
	@UiField
	TextBox extraPayValue;
	
	@UiField
	HTMLPanel partialityPanel;
	
	@UiField
	Button partialityButton;
	
	@UiField
	HTMLPanel paymentsDataGridPanel;
	
	@UiField
	ScrollPanel paymentDataScrollPanel;
	
	@UiField(provided = true)
	DataGrid<Payment> paymentsDataGrid;
	
//	@UiField
//	HTMLPanel prevNextButtons;
//	
//	@UiField
//	Button firstButton;
//	
//	@UiField
//	Button secondButton;
//	
//	@UiField
//	HTMLPanel firstLine;
	
	@UiField
	TextBox paymentConcept;
	
	@UiField
	HTMLPanel paymentCRAPanel;
	
	@UiField
	TextBox paymentDescriptionPos;
	
	@UiField
	TextBox paymentDescription;
	
	@UiField(provided = true)
	TextBox paymentExpression;
	
	@UiField
	HTMLPanel buttonsAcceptCancelPanel;
	
	// -------------------------------------------- Variables
	
//	private AonToolbarButton nextButton;
//	private AonToolbarButton prevButton;
	
	private Button closeBtnDialog;
	private Button acceptBtnDialog;
	
//	private Integer currentPage = 0;
	
	private Payment payment;
	private Payment paymentExtra;
	private Extra extra;
	
	TypeListBox<Payment.Type> paymentTypeListBox;
	
	private boolean hasPartiality = true;
	
	private List<Payment> payments = Collections.emptyList();
	private PaymentsSelectionModel paymentsSelectionModel;
	
	// -------------------------------------------- Constructor
	
	public AgreementPaymentWizard(Set<Payment> aviablePayments) {
		setCaption("Asistente Conceptos");
		
		paymentExpression = new TextBox();
		paymentExpression.setEnabled(false);
		paymentsDataGrid = providePaymentsDataGrid();
		
		setWidget(binder.createAndBindUi(this));
		
		setAvailablePayments(aviablePayments);
		
//		initPrevNextButtons();
		initFooterButtons();
		
		// DeckPanel
		deckPanel.setAnimationVertical(false);
		
		// Hide extra default
		extraPanel.getElement().getStyle().setDisplay(Display.NONE);
		
		// Init Payment
		payment = new Payment();
		paymentExtra = new Payment();
		extra = new Extra();
		
		// Set height
		paymentDataScrollPanel.setHeight("90px");
		
		extraPayDate.getElement().setPropertyString("placeholder", "dd/mm");
		
		initPaymentListBox();
		initExtraPayCalc();
		initExtraPayType();
		initPartialityButton();
		initPaymentCRAType();
		showFirstPage();
		
		// Fire SALARIO_BASE
		DomEvent.fireNativeEvent(Document.get().createChangeEvent(), paymentType);
		
	}

	// -------------------------------------------- ProvidePaymentsDataGrid
	
	protected CustomDataGrid<Payment> providePaymentsDataGrid() {
		payments = Collections.emptyList();
		
		ProvidesKey<Payment> keyProvider = HasIdKeyProvider.getKeyProvider();
		CustomDataGrid<Payment> paymentsDataGrid = new CustomDataGrid<Payment>(10, keyProvider);

		paymentsDataGrid.setAutoHeaderRefreshDisabled(true);
		
		paymentsSelectionModel = new PaymentsSelectionModel(keyProvider);
		paymentsDataGrid.setSelectionModel(paymentsSelectionModel, DefaultSelectionEventManager.<Payment> createCheckboxManager(0));
		
		// Checkbox column. This table will uses a checkbox column for  selection.
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
				return null == payment.getDescription() ? " (" + payment.getName() + ")" : payment.getDescription() + " (" + payment.getName() + ")";
			}
		};
		paymentsDataGrid.addColumn(nameColumn);
		
		paymentsDataGrid.addStyleName(AON.AON_WIDTH_ALL);
		paymentsDataGrid.getElement().getStyle().setPropertyPx("minHeight", 95);
		paymentsDataGrid.setWidth("100%");
			
		new ListDataProvider<Payment>(Collections.emptyList()).addDataDisplay(paymentsDataGrid);
		
		return paymentsDataGrid;
	}
	
	// -------------------------------------------- Initialize View
	
	private void setAvailablePayments(Set<Payment> paymentsIn) {
		payments = paymentsIn.stream().collect(Collectors.toList());
	}

	private void initPaymentListBox() {
		paymentType.clear();
		paymentType.addItem("SALARIO_BASE", "SALARIO_BASE");
		paymentType.addItem("PLUS_SALARIAL", "PLUS_SALARIAL");
		paymentType.addItem("PLUS_EXTRA_SALARIAL", "PLUS_EXTRA_SALARIAL");
		paymentType.addItem("PAGA_EXTRA", "PAGA_EXTRA");
		
		periodicityType.clear();
		periodicityType.addItem("-", "-1");
	}
	
	private void initExtraPayCalc() {
		extraPayCalc.clear();
		extraPayCalc.addItem("A\u00D1O ACTUAL", "A\u00D1O ACTUAL");
		extraPayCalc.addItem("A\u00D1O ANTERIOR", "A\u00D1O ANTERIOR");
		extraPayCalc.addItem("\u00DALTIMOS 12 MESES", "\u00DALTIMOS 12 MESES");
	}
	
	private void initExtraPayType() {
		extraPayType.clear();
		extraPayType.addItem("VARIABLE", "VARIABLE");
		extraPayType.addItem("FIJO", "FIJO");
	}

	private void initPeriodicityType(String paymentType) {
		if(AonStringUtils.equalsIgnoreCase(paymentType, "SALARIO_BASE")) {
			periodicityType.clear();
			periodicityType.addItem("ANUAL", " / PAGAS * DIAS_TRABAJADOS / DIAS_MES");
			periodicityType.addItem("MENSUAL", " * DIAS_TRABAJADOS / DIAS_MES");
			periodicityType.addItem("DIARIO", " * DIAS_TRABAJADOS");
			periodicityType.addItem("HORAS", " * HORAS_TRABAJADAS");
			periodicityType.addItem("PEONADAS", " * JORNADAS_REALES");
		}
		
		if(AonStringUtils.equalsIgnoreCase(paymentType, "PLUS_SALARIAL") || AonStringUtils.equalsIgnoreCase(paymentType, "PLUS_EXTRA_SALARIAL")) {
			periodicityType.clear();
			periodicityType.addItem("MENSUAL", " * DIAS_TRABAJADOS / DIAS_MES");
			periodicityType.addItem("DIAS TRABAJADOS", " * DIAS_TRABAJADOS");
			periodicityType.addItem("DIAS EFECTIVOS", " * DIAS_EFECTIVOS");
			periodicityType.addItem("HORAS TRABAJADAS", " * HORAS_TRABAJADAS");
			periodicityType.addItem("FIJO", "FIJO");
		}
		
		if(AonStringUtils.equalsIgnoreCase(paymentType, "PAGA_EXTRA")) {
			periodicityType.clear();
			periodicityType.addItem("ANUAL", "ANUAL");
			periodicityType.addItem("PRORRATEO", "PRORRATEO");
		}
	}
	
	private void initPaymentCRAType() {
		paymentTypeListBox = new TypeListBox<Payment.Type>(Payment.Type.class, 10);
		paymentTypeListBox.setSelected(Payment.Type.DEFAULT);
		paymentTypeListBox.addStyleName("aon-selectOneMenu");
		paymentTypeListBox.getElement().getStyle().setWidth(100, Unit.PCT);
		paymentTypeListBox.setEnabled(false);
		paymentTypeListBox.addStyleName(style.visibilityDisabled());
		paymentCRAPanel.add(paymentTypeListBox);
	}
	
	private void initPartialityButton() {
		getEnableDisableButton(partialityButton, hasPartiality);
	}
	
	private void setExtraPayFieldsVisible(boolean visible) {
		extraPayDatePanel.setVisible(visible);
		extraPayCalcPanel.setVisible(visible);
		extraPayTypePanel.setVisible(visible);
		extraPayValuePanel.setVisible(visible);
	}

	
	// -------------------------------------------- ToggleButton.Methods
	
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

	// -------------------------------------------- UiHandler
	
//	@UiHandler("firstButton")
//	void onFirstButtonClick(ClickEvent event) {
//		showFirstPage();
//	}
	
	@UiHandler("paymentType")
	void onPaymentTypeChange(ChangeEvent event) {
		if(AonStringUtils.equalsIgnoreCase(paymentType.getSelectedValue(), "PAGA_EXTRA")) {
			paymentTypeListBox.setSelected(Payment.Type.CRA_0004);
			setExtraPayFieldsVisible(true);
			periodicityType.setEnabled(false);
		} else {
			paymentTypeListBox.setSelected(Payment.Type.CRA_0001);
			setExtraPayFieldsVisible(false);
			periodicityType.setEnabled(true);
		}
		
		// ExtraPayType
		DomEvent.fireNativeEvent(Document.get().createChangeEvent(), extraPayType);
		enableOrDisablePayments();
		
		// Init Periodicity
		initPeriodicityType(paymentType.getSelectedValue());
		
		// Extra panel
		if(AonStringUtils.equalsIgnoreCase(paymentType.getSelectedValue(), "SALARIO_BASE"))
			extraPanel.getElement().getStyle().setDisplay(Display.NONE);
		else {
			if(AonStringUtils.equalsIgnoreCase(paymentType.getSelectedValue(), "PLUS_SALARIAL"))
				extraLabel.setText("Descripci\u00F3n plus salarial");
			if(AonStringUtils.equalsIgnoreCase(paymentType.getSelectedValue(), "PLUS_EXTRA_SALARIAL"))
				extraLabel.setText("Descripci\u00F3n plus extra salarial");
			if(AonStringUtils.equalsIgnoreCase(paymentType.getSelectedValue(), "PAGA_EXTRA"))
				extraLabel.setText("Descripci\u00F3n paga extra");
			
			extraPanel.getElement().getStyle().clearDisplay();
		}
		
		createUpdatePayment();
		checkPartialityButton();
	}
	
	@UiHandler("periodicityType")
	void onPeriodicityTypeChange(ChangeEvent event) {
		createUpdatePayment();
		checkPartialityButton();
	}
	
	@UiHandler("extraName")
	void onExtraNameChange(ChangeEvent event) {
		createUpdatePayment();
		checkPartialityButton();
	}
	
	@UiHandler("extraPayDate")
	void onExtraPayDateChange(ChangeEvent event) {
		String issueValue = extraPayDate.getValue();
		if(matchIssueValue(issueValue)) {
			issueValue = parseIssueValue(issueValue);
			extraPayDate.setValue(issueValue);
			if(null == issueValue) {
				extraPayDate.getElement().getStyle().setBorderColor("red");
				extraPayDate.setTitle("La fecha introducida no es correcta");
				extraPayDate.setValue("");
			} else {
				extraPayDate.getElement().getStyle().setBorderColor("black");
				extraPayDate.setTitle("");
			}
		} else {
			extraPayDate.getElement().getStyle().setBorderColor("red");
			extraPayDate.setTitle("La fecha introducida no es correcta");
			extraPayDate.setValue("");
		}
	}
	
	@UiHandler("extraPayType")
	void onExtraPayTypeChange(ChangeEvent event) {
		if(AonStringUtils.equalsIgnoreCase(extraPayType.getSelectedValue(), "FIJO")) {
			paymentsDataGridPanel.getElement().getStyle().setDisplay(Display.NONE);
			
			partialityPanel.getElement().getStyle().clearDisplay();
			extraPayValuePanel.getElement().getStyle().clearDisplay();
			
			hasPartiality = false;
			partialityButton.setEnabled(true);
			getEnableDisableButton(partialityButton, hasPartiality);
			partialityButton.removeStyleName(style.visibilityDisabled());
		} else {
			paymentsDataGridPanel.getElement().getStyle().clearDisplay();
			
			partialityPanel.getElement().getStyle().setDisplay(Display.NONE);
			extraPayValuePanel.getElement().getStyle().setDisplay(Display.NONE);
		}
		
		paymentExpression.setValue("");
	}
	
	@UiHandler("extraPayValue")
	void onExtraPayValueChange(ChangeEvent event) {	
		paymentExpression.setValue(extraPayValue.getValue());
		checkPartiality();
	}
	
//	@UiHandler("secondButton")
//	void onSecondButtonClick(ClickEvent event) {
//		showSecondPage();
//	}
	
	@UiHandler("partialityButton")
	void onPartialityButtonClick(ClickEvent event) {
		Boolean oldValue = isActiveToggleButton(partialityButton);
		Boolean value = !oldValue;
		hasPartiality = value;
		getEnableDisableButton(partialityButton, value);
		checkPartiality();
	}
	
	// -------------------------------------------- DeckPanel.Methods

	private void showFirstPage() {
		deckPanel.showWidget(deckPanel.getWidgetIndex(firstPage));
		deckPanel.animate(500);
//		addFirstButtonFill();
	}

//	private void showSecondPage() {
//		deckPanel.showWidget(deckPanel.getWidgetIndex(secondPage));
//		deckPanel.animate(500);
//		addSecondButtonFill();
//	}

	// -------------------------------------------- ButtonsPanel.Methods
	
//	private void addFirstButtonFill() {
//		secondButton.removeStyleName(style.buttonFillPage());
//		firstLine.removeStyleName(style.lineFill());
//		
//		firstButton.addStyleName(style.buttonFillPage());
//	}
//
//	private void addSecondButtonFill() {
//		firstButton.addStyleName(style.buttonFillPage());
//		secondButton.addStyleName(style.buttonFillPage());
//		firstLine.addStyleName(style.lineFill());
//	}
	
	// -------------------------------------------- Payment
	
	private void createUpdatePayment() {
		createPaymentConcept();
		createPaymentDescriptionPos();
		createPaymentDescription();
		createPaymentExpression();
	}

	private void createPaymentConcept() {
		String conceptName = getConceptName();
		paymentConcept.setValue(conceptName);
		
		paymentConcept.setEnabled(!AonStringUtils.equalsIgnoreCase(conceptName, "SALARIO_BASE"));
	}

	private String getConceptName() {
		String paymentTypeValue = paymentType.getSelectedValue();
		if(AonStringUtils.equalsIgnoreCase(paymentTypeValue, "SALARIO_BASE"))
			return "SALARIO_BASE";
		else if(AonStringUtils.containsIgnoreCase(paymentTypeValue, "PLUS_EXTRA")) {
			String extraNameValue = extraName.getValue();
			return AonStringUtils.isNotBlank(extraNameValue) ? "PLUS_XS_" + extraNameValue.toUpperCase() : "PLUS_XS_SIN_DEFINIR";
		} if(AonStringUtils.containsIgnoreCase(paymentTypeValue, "PLUS")) {
			String extraNameValue = extraName.getValue();
			return AonStringUtils.isNotBlank(extraNameValue) ? "PLUS_" + extraNameValue.toUpperCase() : "PLUS_SIN_DEFINIR";
		} if(AonStringUtils.containsIgnoreCase(paymentTypeValue, "PAGA_EXTRA")) {
			String extraNameValue = extraName.getValue();
			return AonStringUtils.isNotBlank(extraNameValue) ? "PAGA_EXTRA_" + extraNameValue.toUpperCase() : "PAGA_EXTRA_SIN_DEFINIR";
		} else
			return "SIN_DEFINIR";
	}
	
	private void createPaymentDescriptionPos() {
		String paymenteDescriptionPosValue = "";
		
		String paymentTypeValue = paymentType.getSelectedValue();
		String periodicityTypeValue = periodicityType.getSelectedItemText();
		
		
		/**
		 * 
		if(AonStringUtils.equalsIgnoreCase(paymentType, "PLUS_SALARIAL") || AonStringUtils.equalsIgnoreCase(paymentType, "PLUS_EXTRA_SALARIAL")) {
			periodicityType.clear();
			periodicityType.addItem("MENSUAL", " * DIAS_TRABAJADOS / DIAS_MES");
			periodicityType.addItem("DIAS TRABAJADOS", " * DIAS_TRABAJADOS");
			periodicityType.addItem("DIAS EFECTIVOS", " * DIAS_EFECTIVOS");
			periodicityType.addItem("HORAS TRABAJADAS", " * HORAS_TRABAJADAS");
			periodicityType.addItem("FIJO", "FIJO");
		}
		
		if(AonStringUtils.equalsIgnoreCase(paymentType, "PAGA_EXTRA")) {
			periodicityType.clear();
			periodicityType.addItem("ANUAL", "ANUAL");
			periodicityType.addItem("PRORRATEO", "PRORRATEO");
		}*/
		
		if(AonStringUtils.equalsIgnoreCase(paymentTypeValue, "SALARIO_BASE")) {
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
		} else if(AonStringUtils.equalsIgnoreCase(paymentTypeValue, "PLUS_SALARIAL")) {
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
		} else if(AonStringUtils.equalsIgnoreCase(paymentTypeValue, "PLUS_EXTRA_SALARIAL")) {
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
		} else if(AonStringUtils.equalsIgnoreCase(paymentTypeValue, "PAGA_EXTRA")) {
			paymenteDescriptionPosValue = "93";
		}
			
		
		paymentDescriptionPos.setValue(paymenteDescriptionPosValue);
	}

	private void createPaymentDescription() {
		String paymentDescription = "";
		
		String paymentTypeValue = paymentType.getSelectedValue();
		paymentTypeValue = paymentTypeValue.replaceAll("_", " ");
		
		if(AonStringUtils.containsIgnoreCase(paymentTypeValue, "PLUS EXTRA")) {
			String extraNameValue = extraName.getValue();
			if(AonStringUtils.isNotBlank(extraNameValue))
				paymentTypeValue = "PLUS EXTRA SALARIAL " + extraNameValue.toUpperCase();
		} else if(AonStringUtils.containsIgnoreCase(paymentTypeValue, "PLUS")) {
			String extraNameValue = extraName.getValue();
			if(AonStringUtils.isNotBlank(extraNameValue))
				paymentTypeValue = "PLUS " + extraNameValue.toUpperCase();
		} else if(AonStringUtils.equalsIgnoreCase(paymentTypeValue, "PAGA EXTRA")) {
			String extraNameValue = extraName.getValue();
			if(AonStringUtils.isNotBlank(extraNameValue))
				paymentTypeValue = "PAGA EXTRA " + extraNameValue.toUpperCase();
		}
		
		String periodicityTypeValue = periodicityType.getSelectedItemText();
		periodicityTypeValue = periodicityTypeValue.replaceAll("_", " ");
		
		paymentDescription = paymentTypeValue + " (" + periodicityTypeValue + ")";
		this.paymentDescription.setValue(paymentDescription);
	}

	private void createPaymentExpression() {
		String paymentExpression = "";
		
		String paymentTypeValue = paymentType.getSelectedValue();
		
		if(AonStringUtils.equalsIgnoreCase(paymentTypeValue, "SALARIO_BASE"))
			paymentExpression = createBaseSalaryExpression();
		
		if(AonStringUtils.containsIgnoreCase(paymentTypeValue, "PLUS"))
			paymentExpression = createPlusExpression();
		
		this.paymentExpression.setValue(paymentExpression);
	}

	private String createBaseSalaryExpression() {
		String expression = "";
		String periodicity = periodicityType.getSelectedItemText();
		
		if(AonStringUtils.equals(periodicity, "HORAS")) periodicity = "HORA";
		
		expression = "SALARIO_" + periodicity;
		expression += periodicityType.getSelectedValue();
		
		return expression;
	}
	
	private String createPlusExpression() {
		String expression = "";
		String variable = "";
		
		variable = (AonStringUtils.isBlank(extraName.getValue()) ? "SIN_DEFINIR" : extraName.getValue().toUpperCase());
		
		expression = variable;
		
		String periodicityTypeValue = periodicityType.getSelectedValue();
		if(AonStringUtils.equalsIgnoreCase(periodicityTypeValue, "FIJO")) {
			String newExpression = "FRACCIONAR(" + expression + ")";
			expression = newExpression;
		} else {
			expression += periodicityTypeValue;
		}
		
		return expression;
	}
	
	private void checkPartialityButton() {
		if(AonStringUtils.equalsIgnoreCase(paymentType.getSelectedValue(), "PAGA_EXTRA") && !AonStringUtils.equalsIgnoreCase(periodicityType.getSelectedValue(), "PRORRATEO")) {
			partialityPanel.getElement().getStyle().setDisplay(Display.NONE);
		} else {
			partialityPanel.getElement().getStyle().clearDisplay();
			String expression = paymentExpression.getValue();
			if(AonStringUtils.containsIgnoreCase(expression, "DIAS_EFECTIVOS") || AonStringUtils.containsIgnoreCase(expression, "FRACCIONAR")) {
				hasPartiality = false;
				partialityButton.setEnabled(true);
				getEnableDisableButton(partialityButton, hasPartiality);
				partialityButton.removeStyleName(style.visibilityDisabled());
			} else if(AonStringUtils.containsIgnoreCase(expression, "HORAS_TRABAJADAS") || AonStringUtils.containsIgnoreCase(expression, "JORNADAS_REALES")) {
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
		
		if(hasPartiality && (AonStringUtils.containsIgnoreCase(expression, "DIAS_EFECTIVOS") || AonStringUtils.containsIgnoreCase(expression, "FRACCIONAR") ||
				AonStringUtils.containsIgnoreCase(expression, "HORAS_TRABAJADAS") || AonStringUtils.containsIgnoreCase(expression, "JORNADAS_REALES") ||
				AonStringUtils.containsIgnoreCase(extraPayTypeValue, "FIJO"))) {
			paymentExpression.setValue(paymentExpression.getValue() + " * COEFICIENTE_PARCIALIDAD");
		} else {
			String paymentExpressionValue = paymentExpression.getValue();
			if(AonStringUtils.containsIgnoreCase(paymentExpressionValue, " * COEFICIENTE_PARCIALIDAD")) {
				String newExpression = paymentExpressionValue.split(" \\* COEFICIENTE_PARCIALIDAD")[0];
				paymentExpression.setValue(newExpression.trim());
			}
		}
	}
	
	// -------------------------------------------- Issue Date Extra Pay
	
	private boolean matchIssueValue(String issueValue) {
		RegExp issuePattern = RegExp.compile("\\d{2}-*/*\\d{2}");
		return issuePattern.test(issueValue);
	}
	
	private String parseIssueValue(String issueValue) {
		issueValue = issueValue.replaceAll("-", "");
		issueValue = issueValue.replaceAll("/", "");
		
		if(isRealDate(issueValue))
			return issueValue.substring(0, 2) + "/" + issueValue.substring(2, 4);
		else
			return null;
	}

	private boolean isRealDate(String issueValue) {
		Integer dayOfMonth = Integer.parseInt(issueValue.substring(0, 2));
		Integer month = Integer.parseInt(issueValue.substring(2, 4)) - 1;
		
		if(dayOfMonth > 31 || month > 11)
			return false;
		
		Date lastDayOfMonth = DateUtils.getLastDayOfMonth(new Date(new Date().getYear(), month, 1));
		
		try {
			Date date = new Date(new Date().getYear(), month, dayOfMonth);
			if(lastDayOfMonth.getDate() == date.getDate() && lastDayOfMonth.getMonth() == date.getMonth())
				return true;
			else if(lastDayOfMonth.getMonth() == date.getMonth())
				return true;
			else
				return false;
		} catch (Exception e) {
			return false;
		}
	}

	// -------------------------------------------- FooterButtons
	
//	private void initPrevNextButtons() {
//		prevButton = new AonToolbarButton("Anterior", AON.CSS.aonIconPrev());
//		prevButton.addClickHandler(new ClickHandler() {
//			@Override
//			public void onClick(ClickEvent event) {
//				prevPage();
//			}
//		});
//		prevNextButtons.add(prevButton);
//		
//		nextButton = new AonToolbarButton("Siguiente", AON.CSS.aonIconNext());
//		nextButton.addClickHandler(new ClickHandler() {
//			@Override
//			public void onClick(ClickEvent event) {
//				nextPage();
//			}
//
//		});
//		prevNextButtons.add(nextButton);
//	}
//	
//	private void nextPage() {
//		if(currentPage < 1) {
//			currentPage++;
//			showSecondPage();
//		}
//	}
//
//	private void prevPage() {
//		if(currentPage > 0) {
//			currentPage--;
//			showFirstPage();
//		}
//	}
	
	// -------------------------------------------- FooterButtons
	
	private void initFooterButtons() {
		closeBtnDialog = new Button();
		closeBtnDialog.setStyleName(AON.CSS.aonCancelButtonSmall());
		closeBtnDialog.setText( AON.MSG.cancelAction());
		closeBtnDialog.setAccessKey('C');
		closeBtnDialog.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide();
			}
		});
		
		closeBtnDialog.getElement().getStyle().setMarginRight(10, Unit.PX);
		
		buttonsAcceptCancelPanel.add(closeBtnDialog);
		
		acceptBtnDialog = new Button();
		acceptBtnDialog.setStyleName(AON.CSS.aonOkButtonSmall());
		acceptBtnDialog.setText( AON.MSG.accept());
		acceptBtnDialog.setAccessKey('A');
		acceptBtnDialog.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if(AonStringUtils.equalsIgnoreCase(paymentType.getSelectedItemText(), "PAGA_EXTRA")) {
					createExtraPayment();
					onExtraAccept(paymentExtra, extra);
				} else {
					createPayment();
					onAccept(payment);
				}
				hide();
			}
		});
		
		buttonsAcceptCancelPanel.add(acceptBtnDialog);
	}
	
	private void createPayment() {
		String description = AonStringUtils.isBlank(paymentDescriptionPos.getValue()) ? paymentDescription.getValue() : "[" + paymentDescriptionPos.getValue()  + "] " +  paymentDescription.getValue();
		
		payment.setId(-1);
		payment.setDescription(description);
		payment.setExpression(createExpression());
		payment.setIrpfExpression("_P");
		payment.setQuoteExpression("_P");
		payment.setType(paymentTypeListBox.getSelected());
		payment.setSalaryType(Salary.Type.SALARY);
		payment.setName(paymentConcept.getValue());
	}
	
	private String createExpression() {
		String expression = "";
		String paymentExpressionValue = paymentExpression.getValue();
		String periodicityTypeValue = periodicityType.getSelectedValue();
		expression = paymentExpressionValue.replace(periodicityTypeValue, "");
		expression = expression.trim();
		
		if(paymentType.getSelectedIndex() == 0)
			expression =  "/*read-only*/" + expression + "/**/";
		else if((paymentType.getSelectedIndex() == 1 || paymentType.getSelectedIndex() == 2) && periodicityTypeValue == "FIJO") {
			String extraNameValue = extraName.getValue();
			expression =  "/*user*/" + extraNameValue + "/**/";
		} else
			expression =  "/*user*/" + expression + "/**/";
		
		if(AonStringUtils.equalsIgnoreCase(periodicityTypeValue, "FIJO")) {
			String newExpression = "FRACCIONAR(" + expression + ")";
			expression = newExpression;
		} else {
			expression += periodicityTypeValue;
		}
		
		return "/*wizard*/" + expression;
	}

	private void createExtraPayment() {
		String description = AonStringUtils.isBlank(paymentDescriptionPos.getValue()) ? paymentDescription.getValue() : "[" + paymentDescriptionPos.getValue()  + "] " +  paymentDescription.getValue();
		
		paymentExtra.setId(-1);
		paymentExtra.setDescription(description);
		paymentExtra.setExpression(paymentExpression.getValue());
		paymentExtra.setIrpfExpression("_P");
		paymentExtra.setQuoteExpression("_P");
		paymentExtra.setType(paymentTypeListBox.getSelected());
		paymentExtra.setSalaryType(Salary.Type.SALARY);
		paymentExtra.setName(paymentConcept.getValue());
		
		try {
			String monthIssueDate = extraPayDate.getValue().split("/")[1];
			Integer monthIssue = Integer.parseInt(monthIssueDate);
			paymentExtra.setMonth(Short.parseShort((monthIssue -1) + ""));
		} catch (Exception e) {
			paymentExtra.setMonth(null);
		}
		
		createExtra();
	}
	
	private void createExtra() {
		String periodicityValue = periodicityType.getSelectedValue();
		
		if(AonStringUtils.equalsIgnoreCase(periodicityValue, "PRORRATEO"))
			extra = null;
		else {
			extra.setId(-1);
			extra.setIssueDate(extraPayDate.getValue());
			createExtraPeriod();
			extra.setPaymentId(paymentExtra.getId());
			extra.setPaymentDescription(paymentExtra.getDescription());
			extra.setAgreementDescription(paymentExtra.getDescription());
		}
	}

	private void createExtraPeriod() {
		Integer extraPayCalcIdx = extraPayCalc.getSelectedIndex();
		
		switch (extraPayCalcIdx) {
		case 0: // AÑO ACTUAL
			extra.setStartDate("01/01");
			extra.setEndDate("31/12");
			break;
		case 1: // AÑO ANTERIOR
			extra.setStartDate("01/01 -1");
			extra.setEndDate("31/12 -1");
			break;
		default: // ULTIMOS 12 MESES
			createSpecialStartAndEnd();
			break;
		}
	}

	private void createSpecialStartAndEnd() {
		String extraPayDateValue = extraPayDate.getValue();
		String monthStr = extraPayDateValue.split("/")[1];
		Integer month = Integer.parseInt(monthStr) - 1;
		
		Date issueDate = DateUtils.getDate(month, DateUtils.getYear());
		Date lastDayOfPreviusMonthIssueDate = DateUtils.getLastDayOfMonth(DateUtils.addMonths2Date(issueDate, -1));
		
		extra.setStartDate("01/" + monthStr + " -1");
		extra.setEndDate(lastDayOfPreviusMonthIssueDate.getDate() + "/" + (DateUtils.getMonth(lastDayOfPreviusMonthIssueDate) + 1));
	}

	// -------------------------------------------- Abstract Methods
	
	protected abstract void onAccept(Payment payment);
	protected abstract void onExtraAccept(Payment payment, Extra extra);
	
	// -------------------------------------------- ExtraPayment.Methods
	
	private void enableOrDisablePayments() {
		Type type = getType();
		boolean visible = type == Type.CRA_0004 
				|| type == Type.CRA_0055
				|| type == Type.CRA_0056 ;
		
		paymentsDataGridPanel.setVisible(visible);
		if ( !visible )
			return;

		filterAvailablePayments();
	}
	
	public Payment.Type getType() {
		int index = paymentTypeListBox.getSelectedIndex();
		int code = Integer.valueOf(paymentTypeListBox.getValue(index));
		return Payment.Type.getByCode(code);
	}
	
	private void filterAvailablePayments() {
		List<Payment> availablePayments = payments.stream().filter(p -> p.getType() != getType()).collect(Collectors.toList());
		
		//Filter availablePayments only one entry for each type
		availablePayments = filterAvailablePayments(availablePayments);
		
		new ListDataProvider<Payment>(availablePayments).addDataDisplay(paymentsDataGrid);
	}

	private List<Payment> filterAvailablePayments(List<Payment> availablePayments) {
		List<Payment> availablePaymentsResult = new ArrayList<>();
		
		for(Payment payment : availablePayments){
			if(availablePaymentsResult.isEmpty() || !availablePaymentsContainsType(payment.getName(), availablePaymentsResult))
				availablePaymentsResult.add(payment);
		}
		
		return availablePaymentsResult;
	}

	private boolean availablePaymentsContainsType(String searchName,
			List<Payment> availablePaymentsResult) {
		
		for(Payment payment : availablePaymentsResult){
			if(payment.getName() == searchName)
				return true;
		}
		
		return false;
	}
	
	// -------------------------------------------- ShowDialog

	public void showDialog() {
		// Show center
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				center();
				show();
			}
		});
	}

}
