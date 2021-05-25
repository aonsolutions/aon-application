package com.esferalia.aon.gwt.payroll.client;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.payroll.client.AgreementDraft.TypeListBox;
import com.esferalia.aon.gwt.payroll.shared.Payment;
import com.esferalia.aon.gwt.payroll.shared.Salary;
import com.esferalia.aon.watson.util.AonStringUtils;
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
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DeckLayoutPanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public abstract class AgreementPaymentWizard extends AonCustomDialog {
	
	// -------------------------------------------- UiBinder
	
	interface AgreementPaymentDialogBinder extends UiBinder<Widget, AgreementPaymentWizard> {}

	private static final AgreementPaymentDialogBinder binder = GWT.create(AgreementPaymentDialogBinder.class);
	
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
	HTMLPanel partialityPanel;
	
	@UiField
	Button partialityButton;
	
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
	TextBox paymentDescription;
	
	@UiField
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
	
	TypeListBox<Payment.Type> paymentTypeListBox;
	
	private boolean hasPartiality = true;
	
	// -------------------------------------------- Constructor
	
	public AgreementPaymentWizard() {
		setCaption("Asistente Conceptos");
		setWidget(binder.createAndBindUi(this));
		
//		initPrevNextButtons();
		initFooterButtons();
		
		// DeckPanel
		deckPanel.setAnimationVertical(false);
		
		// Hide extra default
		extraPanel.getElement().getStyle().setDisplay(Display.NONE);
		
		// Init Payment
		payment = new Payment();
		
		initPaymentListBox();
		initPartialityButton();
		initPaymentCRAType();
		showFirstPage();
		
		// Fire SALARIO_BASE
		DomEvent.fireNativeEvent(Document.get().createChangeEvent(), paymentType);
		
		// Show center
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				center();
				show();
			}
		});
	}

	private void initPaymentListBox() {
		paymentType.clear();
		paymentType.addItem("SALARIO_BASE", "SALARIO_BASE");
		paymentType.addItem("PLUS_SALARIAL", "PLUS_SALARIAL");
		paymentType.addItem("PLUS_EXTRA_SALARIAL", "PLUS_EXTRA_SALARIAL");
		
		periodicityType.clear();
		periodicityType.addItem("-", "-1");
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
		initPeriodicityType(paymentType.getSelectedValue());
		
		if(AonStringUtils.equalsIgnoreCase(paymentType.getSelectedValue(), "SALARIO_BASE"))
			extraPanel.getElement().getStyle().setDisplay(Display.NONE);
		else {
			if(AonStringUtils.equalsIgnoreCase(paymentType.getSelectedValue(), "PLUS_SALARIAL"))
				extraLabel.setText("Descripci\u00F3n plus salarial");
			if(AonStringUtils.equalsIgnoreCase(paymentType.getSelectedValue(), "PLUS_EXTRA_SALARIAL"))
				extraLabel.setText("Descripci\u00F3n plus extra salarial");
			
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
		} else
			return "SIN_DEFINIR";
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
		
		expression = "/*read-only*/SALARIO_" + periodicity + "/**/";
		expression += periodicityType.getSelectedValue();
		
		return expression;
	}
	
	private String createPlusExpression() {
		String expression = "";
		String variable = "";
		
		variable = (AonStringUtils.isBlank(extraName.getValue()) ? "SIN_DEFINIR" : extraName.getValue().toUpperCase());
		
		expression = "/*user*/" + variable + "/**/";
		
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
	
	private void checkPartiality() {
		String expression = paymentExpression.getValue();
		
		if(hasPartiality && (AonStringUtils.containsIgnoreCase(expression, "DIAS_EFECTIVOS") || AonStringUtils.containsIgnoreCase(expression, "FRACCIONAR") ||
				AonStringUtils.containsIgnoreCase(expression, "HORAS_TRABAJADAS") || AonStringUtils.containsIgnoreCase(expression, "JORNADAS_REALES"))) {
			paymentExpression.setValue(paymentExpression.getValue() + " * COEFICIENTE_PARCIALIDAD");
		} else {
			String paymentExpressionValue = paymentExpression.getValue();
			if(AonStringUtils.containsIgnoreCase(paymentExpressionValue, " * COEFICIENTE_PARCIALIDAD")) {
				String newExpression = paymentExpressionValue.split(" \\* COEFICIENTE_PARCIALIDAD")[0];
				paymentExpression.setValue(newExpression.trim());
			}
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
				createPayment();
				onAccept(payment);
				hide();
			}
		});
		
		buttonsAcceptCancelPanel.add(acceptBtnDialog);
	}
	
	private void createPayment() {
		payment.setId(-1);
		payment.setDescription(paymentDescription.getValue());
		payment.setExpression("/*wizard*/" + paymentExpression.getValue());
		payment.setIrpfExpression("_P");
		payment.setQuoteExpression("_P");
		payment.setType(paymentTypeListBox.getSelected());
		payment.setSalaryType(Salary.Type.SALARY);
		payment.setName(paymentConcept.getValue());
	}
	
	// -------------------------------------------- Abstract Methods
	
	protected abstract void onAccept(Payment payment);

}
