package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.List;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.payroll.shared.Payment;
import com.esferalia.aon.gwt.payroll.shared.Salary;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ValueBoxBase.TextAlignment;
import com.google.gwt.user.client.ui.Widget;

public abstract class GtzdoWizard extends ResizeComposite {

	// -------------------------------------------------- UiBinder --------------------------------------------------

	private static GtzdoWizardBinder uiBinder = GWT.create(GtzdoWizardBinder.class);

	interface GtzdoWizardBinder extends UiBinder<Widget, GtzdoWizard> {}

	// -------------------------------------------------- UiFields --------------------------------------------------

	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String title();
	}
	
	@UiField
	ListBox paymentType;
	
	@UiField
	ListBox gtzdoType;
	
	@UiField
	ListBox gtzdoPeriodicityType;
	
	@UiField
	ListBox gtzdoAboutType;
	
	@UiField
	HTMLPanel gtzdoTablePanel;

	@UiField
	Grid gtzdoDataTableHeader;
	
	@UiField
	ScrollPanel gtzdoScrollPanel;
	
	@UiField
	Grid gtzdoDataTable;

	@UiField
	HTMLPanel footerOptionsToolbarPanel;
	
	@UiField
	HTMLPanel footerOptionsToolbar;
	
	// ------------------------------------------------------ Constructor ---------------------------------------------------------

	public GtzdoWizard() {
		initWidget(uiBinder.createAndBindUi(this));
		
		// Init ListBox
		initPaymentType();
		initGtzdoType();
		initGtzdoPeriodicityType();
		initGtzdoAboutType();
		
		// Days Table
		paintHeaderClausesTable();
		initFooterOptionsToolbar();
		resetGtzdoDataTableStructure();
		
		DomEvent.fireNativeEvent(Document.get().createChangeEvent(), gtzdoPeriodicityType);
		
	}
	
	private void initPaymentType() {
		paymentType.clear();
		paymentType.addItem("SALARIO_BASE", "SALARIO_BASE");
		paymentType.addItem("PLUS_SALARIAL", "PLUS_SALARIAL");
		paymentType.addItem("PLUS_EXTRA_SALARIAL", "PLUS_EXTRA_SALARIAL");
		paymentType.addItem("PAGA_EXTRA", "PAGA_EXTRA");
		paymentType.addItem("MEJORA_IT", "MEJORA_IT");
		paymentType.setSelectedIndex(4);
		
		paymentType.addChangeHandler(e -> {
			onPaymentTypeChange(paymentType.getSelectedValue());
		});
	}
	
	private void initGtzdoType() {
		gtzdoType.clear();
		gtzdoType.addItem("EC", "EC");
		gtzdoType.addItem("AT/EP", "AT/EP");
		gtzdoType.addItem("AMBAS", "AMBAS");
		
		gtzdoType.addChangeHandler(e -> {
			String gtzdoTypeValue = gtzdoType.getSelectedValue();
			if(AonStringUtils.equalsIgnoreCase(gtzdoTypeValue, "EC"))
				onDescriptionChange("MEJORA PREST. SS. ENFERMEDAD COMUN");
			else if(AonStringUtils.equalsIgnoreCase(gtzdoTypeValue, "AT/EP"))
				onDescriptionChange("MEJORA PREST. SS. ACCIDENTE DE TRABAJO");
			else
				onDescriptionChange("MEJORA PREST. SS.");
		});
	}
	
	private void initGtzdoPeriodicityType() {
		gtzdoPeriodicityType.clear();
		gtzdoPeriodicityType.addItem("TRAMOS", "TRAMOS");
		gtzdoPeriodicityType.addItem("DIAS", "DIAS");
		gtzdoPeriodicityType.addItem("IT COMPLETA", "IT COMPLETA");
		
		gtzdoPeriodicityType.addChangeHandler(e -> {
			createPeriodicityTypeView(gtzdoPeriodicityType.getSelectedValue());
		});
	}
	
	private void createPeriodicityTypeView(String gtzdoPeriodicityTypeValue) {
		switch (gtzdoPeriodicityTypeValue) {
			case "TRAMOS":
				gtzdoTablePanel.setVisible(true);
				footerOptionsToolbarPanel.setVisible(false);
				resetGtzdoDataTableStructure();
				createFrozenGtzdoTable("TRAMOS");
				break;
			case "DIAS":
				gtzdoTablePanel.setVisible(true);
				footerOptionsToolbarPanel.setVisible(true);
				resetGtzdoDataTableStructure();
				addGtzdoDataTableRow();
				break;
			default:
				gtzdoTablePanel.setVisible(true);
				footerOptionsToolbarPanel.setVisible(false);
				resetGtzdoDataTableStructure();
				createFrozenGtzdoTable("IT COMPLETA");
				break;
		}
	}

	private void initGtzdoAboutType() {
		gtzdoAboutType.clear();
		gtzdoAboutType.addItem("CALCULADO", "CALCULADO");
		gtzdoAboutType.addItem("BASE REGULADORA", "BASE REGULADORA");
		gtzdoAboutType.addItem("TODO", "TODO");
		
		gtzdoAboutType.addChangeHandler(e -> {
			onShowPaymentAviables(gtzdoAboutType.getSelectedValue());
			if(AonStringUtils.equalsIgnoreCase(gtzdoAboutType.getSelectedValue(), "BASE REGULADORA")) 
				setExpression("BASE_REGULADORA");
			else if(AonStringUtils.equalsIgnoreCase(gtzdoAboutType.getSelectedValue(), "TODO")) 
				setExpression("TODO");
			else
				setExpression("");
			
			checkTableFormula();
		});
	}

	private void initFooterOptionsToolbar() {
		footerOptionsToolbar.clear();
		
		AonTableButton newAttachmentBtn = new AonTableButton(AON.MSG.newAction(),  AON.CSS.aonIconAdd());
		newAttachmentBtn.addClickHandler(e -> {
			addGtzdoDataTableRow();
		});
		
		Label newAttachmentL = new Label("A" + String.valueOf("\u00F1") + "adir Tramo");
		newAttachmentL.getElement().getStyle().setMarginRight(5, Unit.PX);
		
		footerOptionsToolbar.add(newAttachmentBtn);
		footerOptionsToolbar.add(newAttachmentL);
		
	}
	
	private void addGtzdoDataTableRow() {
		// Insert new row
		int row = gtzdoDataTable.insertRow(gtzdoDataTable.getRowCount());
		
		// StartDay TextBox
		TextBox startDayTB = new TextBox();
		startDayTB.setWidth("50px");
		startDayTB.setAlignment(TextAlignment.CENTER);
		
		// EndDay TextBox
		TextBox endDayTB = new TextBox();
		endDayTB.setWidth("50px");
		endDayTB.setAlignment(TextAlignment.CENTER);
		
		// GtzdoPercent TextBox
		TextBox gtzdoPercentTB = new TextBox();
		gtzdoPercentTB.setWidth("50px");
		gtzdoPercentTB.setAlignment(TextAlignment.CENTER);
		
		// Expression TextBox
		Label gtzdoExpressionL = new Label();
		
		// Delete Row
		AonTableButton deleteBtn = new AonTableButton("Eliminar tramo", AON.CSS.aonIconDelete());
		deleteBtn.addClickHandler(e -> {
			String startDay = startDayTB.getValue();
			for(int rowIt = 0; rowIt < gtzdoDataTable.getRowCount(); rowIt++) {
				TextBox startTB = (TextBox) gtzdoDataTable.getWidget(rowIt, 0);
				if(AonStringUtils.equalsIgnoreCase(startTB.getValue(), startDay)) {
					gtzdoDataTable.removeRow(rowIt);
					break;
				}
			}
		});
		
		// Add Handlers
		startDayTB.addValueChangeHandler(e -> {
			gtzdoExpressionL.setText(createGtzdoExpression(startDayTB.getValue(), endDayTB.getValue(), gtzdoPercentTB.getValue()));
		});
		
		endDayTB.addValueChangeHandler(e -> {
			gtzdoExpressionL.setText(createGtzdoExpression(startDayTB.getValue(), endDayTB.getValue(), gtzdoPercentTB.getValue()));
		});
		
		gtzdoPercentTB.addValueChangeHandler(e -> {
			gtzdoExpressionL.setText(createGtzdoExpression(startDayTB.getValue(), endDayTB.getValue(), gtzdoPercentTB.getValue()));
		});
		
		gtzdoDataTable.setWidget(row, 0, startDayTB);
		gtzdoDataTable.setWidget(row, 1, endDayTB);
		gtzdoDataTable.setWidget(row, 2, gtzdoPercentTB);
		gtzdoDataTable.setWidget(row, 3, gtzdoExpressionL);
		gtzdoDataTable.setWidget(row, 4, deleteBtn);
	}
	
	private void createFrozenGtzdoTable(String periodicityTypeValue) {
		if(AonStringUtils.equalsIgnoreCase(periodicityTypeValue, "TRAMOS")) {
			insertFrozenGtzdoTableRow("01", "03", "60");
			insertFrozenGtzdoTableRow("04", "15", "75");
			insertFrozenGtzdoTableRow("16", "20", "90");
			insertFrozenGtzdoTableRow("21", "365", "100");
		} else if (AonStringUtils.equalsIgnoreCase(periodicityTypeValue, "IT COMPLETA"))
			insertFrozenGtzdoTableRow("01", "365", null);
	}

	private void insertFrozenGtzdoTableRow(String startDay, String endDay, String gtzdoPercent) {
		// Insert new row
		int row = gtzdoDataTable.insertRow(gtzdoDataTable.getRowCount());
		
		// StartDay TextBox
		TextBox startDayTB = new TextBox();
		startDayTB.setWidth("50px");
		startDayTB.setAlignment(TextAlignment.CENTER);
		startDayTB.setValue(startDay);
		startDayTB.setEnabled(false);
		
		// EndDay TextBox
		TextBox endDayTB = new TextBox();
		endDayTB.setWidth("50px");
		endDayTB.setAlignment(TextAlignment.CENTER);
		endDayTB.setValue(endDay);
		endDayTB.setEnabled(false);
		
		// GtzdoPercent TextBox
		TextBox gtzdoPercentTB = new TextBox();
		gtzdoPercentTB.setWidth("50px");
		gtzdoPercentTB.setAlignment(TextAlignment.CENTER);
		gtzdoPercentTB.setValue(gtzdoPercent);
		
		// Expression TextBox
		Label gtzdoExpressionL = new Label();
		gtzdoExpressionL.setText(createGtzdoExpression(startDay, endDay, gtzdoPercent));
		
		// Add handlers
		gtzdoPercentTB.addValueChangeHandler(e -> {
			gtzdoExpressionL.setText(createGtzdoExpression(startDay, endDay, gtzdoPercentTB.getValue()));
		});
		
		gtzdoDataTable.setWidget(row, 0, startDayTB);
		gtzdoDataTable.setWidget(row, 1, endDayTB);
		gtzdoDataTable.setWidget(row, 2, gtzdoPercentTB);
		gtzdoDataTable.setWidget(row, 3, gtzdoExpressionL);
		gtzdoDataTable.setWidget(row, 4, null);
	}

	private String createGtzdoExpression(String startDay, String endDay, String gtzdoPercent) {
		String gtzdoAboutTypeValue = gtzdoAboutType.getSelectedValue();
		String expression = "";
		
		if(AonStringUtils.equalsIgnoreCase(gtzdoAboutTypeValue, "BASE REGULADORA")) {
			try {
				Integer start = Integer.parseInt(startDay);
				Integer end = Integer.parseInt(endDay);
				gtzdoPercent = gtzdoPercent.replace("%", "");
				Double percent = Double.parseDouble(gtzdoPercent);
				percent = percent / 100;
				
				if(percent < 1)
					expression = "GTZDO(BASE_REGULADORA*" + percent + "," + start + ", " + end + ")";
				else
					expression = "GTZDO(BASE_REGULADORA," + start + ", " + end + ")";
			} catch (Exception e) {
				expression = "";
			}
		} else {
			try {
				Integer start = Integer.parseInt(startDay);
				Integer end = Integer.parseInt(endDay);
				gtzdoPercent = gtzdoPercent.replace("%", "");
				Double percent = Double.parseDouble(gtzdoPercent);
				percent = percent / 100;
				
				if(percent < 1)
					expression = "GTZDO((" + getExpression() + ")*" + percent + "," + start + ", " + end + ")";
				else
					expression = "GTZDO((" + getExpression() + ")," + start + ", " + end + ")";
			} catch (Exception e) {
				expression = "";
			}
			
		}
		
		return expression;
	}

	// --------------------------------------------------- UiHandlers (Aux Methods) -------------------------------------------------
	
	private void resetGtzdoDataTableStructure() {
		gtzdoDataTable.clear();
		gtzdoDataTable.resize(0, 0);
		gtzdoDataTable.resizeColumns(5);
		
		setColumnsWidth();
	}
	
	// -------------------------------------------------- Paint Table Header Methods --------------------------------------------------

	private void paintHeaderClausesTable() {
		gtzdoDataTableHeader.clear();
		gtzdoDataTableHeader.resize(0, 0);
		gtzdoDataTableHeader.resizeColumns(5);
		
		int row = gtzdoDataTableHeader.insertRow(gtzdoDataTableHeader.getRowCount());
		
		Label startDay = new Label("D" + String.valueOf("\u00CD") + "A INICIAL");
		Label endDay = new Label("D" + String.valueOf("\u00CD") + "A FINAL");
		Label gtzdoPercent = new Label("% Gtzdo.");
		Label formula = new Label("F" + String.valueOf("\u00D3") + "RMULA");
		Label action = new Label("");
		
		startDay.addStyleName(style.title());
		endDay.addStyleName(style.title());
		gtzdoPercent.addStyleName(style.title());
		formula.addStyleName(style.title());
		
		gtzdoDataTableHeader.setWidget(row, 0, startDay);
		gtzdoDataTableHeader.setWidget(row, 1, endDay);
		gtzdoDataTableHeader.setWidget(row, 2, gtzdoPercent);
		gtzdoDataTableHeader.setWidget(row, 3, formula);
		gtzdoDataTableHeader.setWidget(row, 4, action);
	}	
	
	// ------------------------------------------------------ Auxiliar Methods ----------------------------------------------------
	
	private void setColumnsWidth() {
		gtzdoDataTableHeader.getCellFormatter().getElement(0, 0).getStyle().setWidth(70, Unit.PX);
		gtzdoDataTableHeader.getCellFormatter().getElement(0, 1).getStyle().setWidth(70, Unit.PX);
		gtzdoDataTableHeader.getCellFormatter().getElement(0, 2).getStyle().setWidth(70, Unit.PX);
		gtzdoDataTableHeader.getCellFormatter().getElement(0, 3).getStyle().setWidth(300, Unit.PX);
		gtzdoDataTableHeader.getCellFormatter().getElement(0, 4).getStyle().setWidth(10, Unit.PX);
		
		gtzdoDataTable.getColumnFormatter().getElement(0).getStyle().setWidth(70, Unit.PX);
		gtzdoDataTable.getColumnFormatter().getElement(1).getStyle().setWidth(70, Unit.PX);
		gtzdoDataTable.getColumnFormatter().getElement(2).getStyle().setWidth(70, Unit.PX);
		gtzdoDataTable.getColumnFormatter().getElement(3).getStyle().setWidth(300, Unit.PX);
		gtzdoDataTable.getColumnFormatter().getElement(4).getStyle().setWidth(10, Unit.PX);
	}
	
	// ------------------------------------------------------ Create Payments ----------------------------------------------------
	
	public List<Payment> createPayments() {
		List<Payment> payments = new ArrayList<Payment>();
		
		for(int row=0; row < gtzdoDataTable.getRowCount(); row++) {
			Payment payment = new Payment();
			
			payment.setId((row+1)*(-1));
			payment.setDescription(createDescription(row));
			payment.setExpression(createExpression(row));
			payment.setIrpfExpression("_P");
			payment.setQuoteExpression("_P");
			payment.setType(Payment.Type.CRA_0055);
			payment.setSalaryType(Salary.Type.SALARY);
			payment.setName("GARANTIZADO");
			
			payments.add(payment);
		}
		
		return payments;
	}
	
	private String createDescription(int row) {
		TextBox startTB = (TextBox) gtzdoDataTable.getWidget(row, 0);
		TextBox endTB = (TextBox) gtzdoDataTable.getWidget(row, 1);
		
		String description = getDescription() + " (" + startTB.getValue() + " - " + endTB.getValue() + ")";
		
		return description;
	}
	

	private String createExpression(int row) {
		String expression = "";
		
		switch (gtzdoType.getSelectedValue()) {
		case "EC":
			expression = "isdef DIAS_ENFERMEDAD_COMUN ? /*user*/";
			break;
		case "AT/EP":
			expression = "isdef DIAS_ENFERMEDAD_PROFESIONAL ? /*user*/";
			break;
		default:
			expression = "isdef DIAS_IT ? /*user*/";
			break;
		}
		
		if(AonStringUtils.equalsIgnoreCase(gtzdoAboutType.getSelectedValue(), "TODO") && AonStringUtils.equalsIgnoreCase(gtzdoPeriodicityType.getSelectedValue(), "IT COMPLETA")) {
			expression += "GTZDO(TODO)/**/ : HIDE()";
		} else {
			TextBox startTB = (TextBox) gtzdoDataTable.getWidget(row, 0);
			TextBox endTB = (TextBox) gtzdoDataTable.getWidget(row, 1);
			TextBox percentTB = (TextBox) gtzdoDataTable.getWidget(row, 2);
			String generatedExpression = createGtzdoExpression(startTB.getValue(), endTB.getValue(), percentTB.getValue());
			
			if(AonStringUtils.isBlank(generatedExpression))
				return "";
			
			expression += generatedExpression + "/**/ : HIDE()";
		}
		
		return expression;
	}

	// ------------------------------------------------------ Abstract Methods ----------------------------------------------------
	
	protected abstract void onPaymentTypeChange(String paymentTypeValue);
	protected abstract void onShowPaymentAviables(String gtzdoAboutTypeValue);
	protected abstract void getGtzdoPayments(List<Payment> payments);
	protected abstract String getExpression();
	protected abstract void setExpression(String string);
	protected abstract String getDescription();
	protected abstract void onDescriptionChange(String description);

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
	
	public void setPaymentType(String paymentTypeValue) {
		setSelectedValueLB(paymentType, paymentTypeValue);
	}
	
	public void fireGtzdoAboutType() {
		DomEvent.fireNativeEvent(Document.get().createChangeEvent(), gtzdoAboutType);
	}

	public void fireGtzdoType() {
		DomEvent.fireNativeEvent(Document.get().createChangeEvent(), gtzdoType);
	}

	public void checkTableFormula() {
		for(int row = 0; row < gtzdoDataTable.getRowCount(); row++) {
			TextBox startTB = (TextBox) gtzdoDataTable.getWidget(row, 0);
			TextBox endTB = (TextBox) gtzdoDataTable.getWidget(row, 1);
			TextBox percentTB = (TextBox) gtzdoDataTable.getWidget(row, 2);
			Label formulaLabel = (Label) gtzdoDataTable.getWidget(row, 3);
			formulaLabel.setText(createGtzdoExpression(startTB.getValue(), endTB.getValue(), percentTB.getValue()));
		}
	}

}
