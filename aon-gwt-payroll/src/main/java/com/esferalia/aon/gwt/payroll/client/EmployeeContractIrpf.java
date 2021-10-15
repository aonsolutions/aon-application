package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarSmallButton;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.EmployeeIrpf;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public class EmployeeContractIrpf extends Composite {

	// ----------------------------------------------- UiBinder 
	
	private static EmployeeContractIrpfUiBinder uiBinder = GWT.create(EmployeeContractIrpfUiBinder.class);

	interface EmployeeContractIrpfUiBinder extends UiBinder<Widget, EmployeeContractIrpf> {}
	
	// ----------------------------------------------- UiField 
	
	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String flexColumn();
		String flex();
		String width90();
		String width50();
		String title();
		String header();
	}
	
	@UiField
	DockLayoutPanel dockLayoutPanel;
	
	@UiField
	HTMLPanel mainPanel;
	
	// ----------------------------------------------- Variables 
	
	private EmployeeContractIrpfObject employeeContractIrpfObject;
	
	private AonToolbar toolbar;
	private AonToolbarButton saveButton;
	private ListBox yearLB;
	
	// ----------------------------------------------- Constructor 
	
	public EmployeeContractIrpf() {
		initializeToolbarPanel();
		initWidget(uiBinder.createAndBindUi(this));
		
		this.getElement().getStyle().setHeight(100, Unit.PCT);
		dockLayoutPanel.addNorth(toolbar, AonToolbar.HEIGTH);
		
		saveButton.setEnabled(false);
	}
		
	// ----------------------------------------------- setEmployeeContractIrpfObject 
	
	public void setEmployeeContractIrpfObject(EmployeeContractIrpfObject employeeContractIrpfObject) {
		this.employeeContractIrpfObject = employeeContractIrpfObject;
		
		Date auxDate = DateUtils.getDate(0, Integer.parseInt(yearLB.getSelectedValue()));
		Date date = DateUtils.getFirstDayOfMonth(auxDate);
		
		this.employeeContractIrpfObject.getEmployeeIrpf(
				date,
				r -> initEmployeeIrpfTable(),
				t -> {});
	}

	// ----------------------------------------------- setEmployeeContractPaymentsObject.Methods
	
	private void initEmployeeIrpfTable() {
		HTMLPanel table = new HTMLPanel("");
		table.addStyleName(style.flexColumn());
		
		// Header Row
		HTMLPanel headerRow = new HTMLPanel("");
		getHeaderRow(headerRow);
		table.add(headerRow);
		
		// Fill lines
		for(int month = 0; month < 12; month++) {
			HTMLPanel monthRow = new HTMLPanel("");
			getMonthRow(monthRow, month);
			table.add(monthRow);
		}
			
		// Add table to mainPanel
		mainPanel.clear();
		mainPanel.add(table);
	}

	public void getHeaderRow(HTMLPanel headerRow) {
		headerRow.addStyleName(style.flex());
		headerRow.addStyleName(style.header());
		
		Label emptyLabel = new Label("");
		emptyLabel.addStyleName(style.width90());
		
		Label typeLabel = new Label("Tipo");
		typeLabel.addStyleName(style.width90());
		typeLabel.addStyleName(style.title());
		
		Label irpfPercentLabel = new Label("% IRPF");
		irpfPercentLabel.addStyleName(style.width90());
		irpfPercentLabel.addStyleName(style.title());
		
		Label moneyBaseLabel = new Label("Base Dineraria");
		moneyBaseLabel.addStyleName(style.width90());
		moneyBaseLabel.addStyleName(style.title());
		
		Label moneyQuoteLabel = new Label("Cuota Dineraria");
		moneyQuoteLabel.addStyleName(style.width90());
		moneyQuoteLabel.addStyleName(style.title());
		
		Label inkindBaseLabel = new Label("Base Especie");
		inkindBaseLabel.addStyleName(style.width90());
		inkindBaseLabel.addStyleName(style.title());
		
		Label inkindQuoteLabel = new Label("Cuota Especie");
		inkindQuoteLabel.addStyleName(style.width90());
		inkindQuoteLabel.addStyleName(style.title());
		
		Label cgcBaseLabel = new Label("Base CGC");
		cgcBaseLabel.addStyleName(style.width90());
		cgcBaseLabel.addStyleName(style.title()); 
		
		Label cgpBaseLabel = new Label("Base CGP");
		cgpBaseLabel.addStyleName(style.width90());
		cgpBaseLabel.addStyleName(style.title()); 
		
		Label employeeSSQuoteLabel = new Label("Cuota SS Trabajador");
		employeeSSQuoteLabel.addStyleName(style.width90());
		employeeSSQuoteLabel.addStyleName(style.title()); 
		
		Label totalIrpfLabel = new Label("Total IRPF");
		totalIrpfLabel.addStyleName(style.width90());
		totalIrpfLabel.addStyleName(style.title()); 
		
		Label actionLabel = new Label("");
		actionLabel.addStyleName(style.width50());
		
		headerRow.add(emptyLabel);
		headerRow.add(typeLabel);
		headerRow.add(irpfPercentLabel);
		headerRow.add(moneyBaseLabel);
		headerRow.add(moneyQuoteLabel);
		headerRow.add(inkindBaseLabel);
		headerRow.add(inkindQuoteLabel);
		headerRow.add(cgcBaseLabel);
		headerRow.add(cgpBaseLabel);
		headerRow.add(employeeSSQuoteLabel);
		headerRow.add(totalIrpfLabel);
		headerRow.add(actionLabel);
	}
	
	private void getMonthRow(HTMLPanel monthRow, int month) {
		monthRow.addStyleName(style.flex());
		List<DoubleBox> valuesDBx = new ArrayList<>();
		
		// Get date
		final Date date = DateUtils.getFirstDayOfMonth(DateUtils.getDate(month, Integer.parseInt(yearLB.getSelectedValue())));
		
		Label monthLabel = new Label(getStringMonth(month));
		monthLabel.addStyleName(style.width90());
		monthLabel.addStyleName(style.title());
		
		Label typeLabel = new Label("");
		typeLabel.addStyleName(style.width90());
		
		HTMLPanel irpfPercentPanel = new HTMLPanel("");
		irpfPercentPanel.addStyleName(style.width90());
		DoubleBox irpfPercentDBx = new DoubleBox();
		irpfPercentDBx.addStyleName(style.width50());
		valuesDBx.add(irpfPercentDBx);
		irpfPercentPanel.add(irpfPercentDBx);
		
		HTMLPanel moneyBasePanel = new HTMLPanel("");
		moneyBasePanel.addStyleName(style.width90());
		DoubleBox moneyBaseDBx = new DoubleBox();
		moneyBaseDBx.addStyleName(style.width50());
		valuesDBx.add(moneyBaseDBx);
		moneyBasePanel.add(moneyBaseDBx);
		
		HTMLPanel moneyQuotePanel = new HTMLPanel("");
		moneyQuotePanel.addStyleName(style.width90());
		DoubleBox moneyQuoteDBx = new DoubleBox();
		moneyQuoteDBx.addStyleName(style.width50());
		valuesDBx.add(moneyQuoteDBx);
		moneyQuotePanel.add(moneyQuoteDBx);
		
		HTMLPanel inkindBasePanel = new HTMLPanel("");
		inkindBasePanel.addStyleName(style.width90());
		DoubleBox inkindBaseDBx = new DoubleBox();
		inkindBaseDBx.addStyleName(style.width50());
		valuesDBx.add(inkindBaseDBx);
		inkindBasePanel.add(inkindBaseDBx);
		
		HTMLPanel inkindQuotePanel = new HTMLPanel("");
		inkindQuotePanel.addStyleName(style.width90());
		DoubleBox inkindQuoteDBx = new DoubleBox();
		inkindQuoteDBx.addStyleName(style.width50());
		valuesDBx.add(inkindQuoteDBx);
		inkindQuotePanel.add(inkindQuoteDBx);
		
		HTMLPanel cgcBasePanel = new HTMLPanel("");
		cgcBasePanel.addStyleName(style.width90());
		DoubleBox cgcBaseDBx = new DoubleBox();
		cgcBaseDBx.addStyleName(style.width50());
		valuesDBx.add(cgcBaseDBx);
		cgcBasePanel.add(cgcBaseDBx);
		
		HTMLPanel cgpBasePanel = new HTMLPanel("");
		cgpBasePanel.addStyleName(style.width90());
		DoubleBox cgpBaseDBx = new DoubleBox();
		cgpBaseDBx.addStyleName(style.width50());
		valuesDBx.add(cgpBaseDBx);
		cgpBasePanel.add(cgpBaseDBx);
		
		HTMLPanel employeeSSQuoteBasePanel = new HTMLPanel("");
		employeeSSQuoteBasePanel.addStyleName(style.width90());
		DoubleBox employeeSSQuoteBaseDBx = new DoubleBox();
		employeeSSQuoteBaseDBx.addStyleName(style.width50());
		valuesDBx.add(employeeSSQuoteBaseDBx);
		employeeSSQuoteBasePanel.add(employeeSSQuoteBaseDBx);
		
		HTMLPanel totalIrpfBasePanel = new HTMLPanel("");
		totalIrpfBasePanel.addStyleName(style.width90());
		DoubleBox totalIrpfBaseDBx = new DoubleBox();
		totalIrpfBaseDBx.addStyleName(style.width50());
		valuesDBx.add(totalIrpfBaseDBx);
		totalIrpfBasePanel.add(totalIrpfBaseDBx);
		
		HTMLPanel actionPanel = new HTMLPanel("");
		actionPanel.addStyleName(style.width50());
		
		// Get employeeIrpf by date
		EmployeeIrpf employeeIrpf = this.employeeContractIrpfObject.getEmployeeIrpf(date);
		
		if(null != employeeIrpf) {
			typeLabel.setText(employeeIrpf.getSalaryType());
			irpfPercentDBx.setValue(employeeIrpf.getIrpfPercent());
			moneyBaseDBx.setValue(employeeIrpf.getMoneyBase());
			moneyQuoteDBx.setValue(employeeIrpf.getMoneyQuote());
			inkindBaseDBx.setValue(employeeIrpf.getInkindBase());
			inkindQuoteDBx.setValue(employeeIrpf.getInkindQuote());
			cgcBaseDBx.setValue(employeeIrpf.getBaseCgc());
			cgpBaseDBx.setValue(employeeIrpf.getBaseCgp());
			employeeSSQuoteBaseDBx.setValue(employeeIrpf.getEmployeeSSQuote());
			totalIrpfBaseDBx.setValue(employeeIrpf.getTotalIrpf());
			valuesDBx.forEach(dBx -> dBx.setEnabled(false));
		}
		
		// Add ValueChangeHandlers
		irpfPercentDBx.addValueChangeHandler(e -> {
			checkMoneyAmounts(irpfPercentDBx, moneyBaseDBx, moneyQuoteDBx);
			checkInkindAmounts(irpfPercentDBx, inkindBaseDBx, inkindQuoteDBx);
			checkTotalIrpfAmount(moneyQuoteDBx, inkindQuoteDBx, totalIrpfBaseDBx);
			createUpdateEmployeeIrpf(date, irpfPercentDBx, moneyBaseDBx, moneyQuoteDBx, inkindBaseDBx, inkindQuoteDBx, cgcBaseDBx, cgpBaseDBx, employeeSSQuoteBaseDBx, totalIrpfBaseDBx);
		});
		
		moneyBaseDBx.addValueChangeHandler(e -> {
			checkMoneyAmounts(irpfPercentDBx, moneyBaseDBx, moneyQuoteDBx);
			checkTotalIrpfAmount(moneyQuoteDBx, inkindQuoteDBx, totalIrpfBaseDBx);
			createUpdateEmployeeIrpf(date, irpfPercentDBx, moneyBaseDBx, moneyQuoteDBx, inkindBaseDBx, inkindQuoteDBx, cgcBaseDBx, cgpBaseDBx, employeeSSQuoteBaseDBx, totalIrpfBaseDBx);
		});
		
		moneyQuoteDBx.addValueChangeHandler(e -> {
			checkMoneyAmounts(irpfPercentDBx, moneyBaseDBx, moneyQuoteDBx);
			checkTotalIrpfAmount(moneyQuoteDBx, inkindQuoteDBx, totalIrpfBaseDBx);
			createUpdateEmployeeIrpf(date, irpfPercentDBx, moneyBaseDBx, moneyQuoteDBx, inkindBaseDBx, inkindQuoteDBx, cgcBaseDBx, cgpBaseDBx, employeeSSQuoteBaseDBx, totalIrpfBaseDBx);
		});
		
		inkindBaseDBx.addValueChangeHandler(e -> {
			checkInkindAmounts(irpfPercentDBx, inkindBaseDBx, inkindQuoteDBx);
			checkTotalIrpfAmount(moneyQuoteDBx, inkindQuoteDBx, totalIrpfBaseDBx);
			createUpdateEmployeeIrpf(date, irpfPercentDBx, moneyBaseDBx, moneyQuoteDBx, inkindBaseDBx, inkindQuoteDBx, cgcBaseDBx, cgpBaseDBx, employeeSSQuoteBaseDBx, totalIrpfBaseDBx);
		});
		
		inkindQuoteDBx.addValueChangeHandler(e -> {
			checkInkindAmounts(irpfPercentDBx, inkindBaseDBx, inkindQuoteDBx);
			checkTotalIrpfAmount(moneyQuoteDBx, inkindQuoteDBx, totalIrpfBaseDBx);
			createUpdateEmployeeIrpf(date, irpfPercentDBx, moneyBaseDBx, moneyQuoteDBx, inkindBaseDBx, inkindQuoteDBx, cgcBaseDBx, cgpBaseDBx, employeeSSQuoteBaseDBx, totalIrpfBaseDBx);
		});
		
		cgcBaseDBx.addValueChangeHandler(e -> createUpdateEmployeeIrpf(date, irpfPercentDBx, moneyBaseDBx, moneyQuoteDBx, inkindBaseDBx, inkindQuoteDBx, cgcBaseDBx, cgpBaseDBx, employeeSSQuoteBaseDBx, totalIrpfBaseDBx));
		cgpBaseDBx.addValueChangeHandler(e -> createUpdateEmployeeIrpf(date, irpfPercentDBx, moneyBaseDBx, moneyQuoteDBx, inkindBaseDBx, inkindQuoteDBx, cgcBaseDBx, cgpBaseDBx, employeeSSQuoteBaseDBx, totalIrpfBaseDBx));
		employeeSSQuoteBaseDBx.addValueChangeHandler(e -> createUpdateEmployeeIrpf(date, irpfPercentDBx, moneyBaseDBx, moneyQuoteDBx, inkindBaseDBx, inkindQuoteDBx, cgcBaseDBx, cgpBaseDBx, employeeSSQuoteBaseDBx, totalIrpfBaseDBx));
		totalIrpfBaseDBx.addValueChangeHandler(e -> createUpdateEmployeeIrpf(date, irpfPercentDBx, moneyBaseDBx, moneyQuoteDBx, inkindBaseDBx, inkindQuoteDBx, cgcBaseDBx, cgpBaseDBx, employeeSSQuoteBaseDBx, totalIrpfBaseDBx));
		
		// Add elements to monthRow
		monthRow.add(monthLabel);
		monthRow.add(typeLabel);
		monthRow.add(irpfPercentPanel);
		monthRow.add(moneyBasePanel);
		monthRow.add(moneyQuotePanel);
		monthRow.add(inkindBasePanel);
		monthRow.add(inkindQuotePanel);
		monthRow.add(cgcBasePanel);
		monthRow.add(cgpBasePanel);
		monthRow.add(employeeSSQuoteBasePanel);
		monthRow.add(totalIrpfBasePanel);
		if(null != employeeIrpf && AonStringUtils.equalsIgnoreCase("Manual", employeeIrpf.getSalaryType())) {
			AonToolbarSmallButton deleteBtn = new AonToolbarSmallButton("Eliminar", AON.CSS.aonIconDelete());
			actionPanel.add(deleteBtn);
			deleteBtn.addClickHandler(e -> {
				employeeContractIrpfObject.deleteEmployeeIrpf(employeeIrpf);
				initEmployeeIrpfTable();
			});
		}
		monthRow.add(actionPanel);
	}

	private void checkMoneyAmounts(DoubleBox irpfPercentDBx, DoubleBox moneyBaseDBx, DoubleBox moneyQuoteDBx) {
		Double irpfPercent = irpfPercentDBx.getValue();
		Double moneyBase = moneyBaseDBx.getValue();
		Double moneyQuote = moneyQuoteDBx.getValue();
		
		if(null != irpfPercent && null != moneyQuote)
			moneyBaseDBx.setValue(moneyQuote * 100.00 / irpfPercent);
		
		if(null != moneyBase && null != moneyQuote)
			irpfPercentDBx.setValue(moneyQuote / moneyBase * 100.00); 
		
		if(null != moneyBase && null != irpfPercent)
			moneyQuoteDBx.setValue(moneyBase * irpfPercent / 100.00); 
	}
		
	private void checkInkindAmounts(DoubleBox irpfPercentDBx, DoubleBox inkindBaseDBx, DoubleBox inkindQuoteDBx) {
		Double irpfPercent = irpfPercentDBx.getValue();
		Double inkindBase = inkindBaseDBx.getValue();
		Double inkindQuote = inkindQuoteDBx.getValue();
		
		if(null != irpfPercent && null != inkindQuote)
			inkindBaseDBx.setValue(inkindQuote * 100.00 / irpfPercent);
		
		if(null != inkindBase && null != inkindQuote)
			irpfPercentDBx.setValue(inkindBase / inkindBase * 100.00); 
		
		if(null != inkindBase && null != irpfPercent)
			inkindQuoteDBx.setValue(inkindBase * irpfPercent / 100.00); 	
	}
	
	private void checkTotalIrpfAmount(DoubleBox moneyQuoteDBx, DoubleBox inkindQuoteDBx, DoubleBox totalIrpfBaseDBx) {
		Double moneyQuote = null == moneyQuoteDBx.getValue() ? 0.00 : moneyQuoteDBx.getValue();
		Double inkindQuote = null == inkindQuoteDBx.getValue() ? 0.00 : inkindQuoteDBx.getValue();
		totalIrpfBaseDBx.setValue(moneyQuote + inkindQuote); 
	}

	private void createUpdateEmployeeIrpf(Date date, DoubleBox irpfPercentDBx, DoubleBox moneyBaseDBx, DoubleBox moneyQuoteDBx, DoubleBox inkindBaseDBx, DoubleBox inkindQuoteDBx, DoubleBox cgcBaseDBx, DoubleBox cgpBaseDBx, DoubleBox employeeSSQuoteBaseDBx, DoubleBox totalIrpfBaseDBx) {
		this.employeeContractIrpfObject.createUpdateEmployeeIrpf(
				date, 
				irpfPercentDBx.getValue(),
				moneyBaseDBx.getValue(),
				moneyQuoteDBx.getValue(),
				inkindBaseDBx.getValue(),
				inkindQuoteDBx.getValue(),
				cgcBaseDBx.getValue(),
				cgpBaseDBx.getValue(),
				employeeSSQuoteBaseDBx.getValue(),
				totalIrpfBaseDBx.getValue());				
	}
	
	private String getStringMonth(int month) {
		switch (month) {
			case 0:
				return "Enero";
			case 1:
				return "Ferbrero";
			case 2:
				return "Marzo";
			case 3:
				return "Abril";
			case 4:
				return "Mayo";
			case 5:
				return "Junio";
			case 6:
				return "Julio";
			case 7:
				return "Agosto";
			case 8:
				return "Septiembre";
			case 9:
				return "Octubre";
			case 10:
				return "Noviembre";
			default:
				return "Diciembre";
		}
	}

	public void initializeYearLB(ListBox yearLB) {
		Integer year = DateUtils.getYear();
		Integer yearAux = DateUtils.getYear();
		Integer previusYear = year - 1;
		Integer nextYear = year + 1;
		
		yearLB.clear();
		yearLB.addItem(nextYear.toString(), nextYear.toString());
		yearLB.addItem(yearAux.toString(), yearAux.toString());
		yearLB.addItem(previusYear.toString(), previusYear.toString());
		
		yearLB.addChangeHandler(e -> changeYear());
		
		setSelectedValueLB(yearLB, year.toString());
	}
	
	private void changeYear() {
		
		Date auxDate = DateUtils.getDate(0, Integer.parseInt(yearLB.getSelectedValue()));
		Date date = DateUtils.getFirstDayOfMonth(auxDate);
		
		this.employeeContractIrpfObject.setEmployeeIrpf(
				s -> 
					this.employeeContractIrpfObject.getEmployeeIrpf(
							date, 
							suc -> initEmployeeIrpfTable(), 
							f -> {})
				, 
				f -> {});
	}

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

	// ----------------------------------------------- Toolbar
	
	private void initializeToolbarPanel() {
		
		this.toolbar = new AonToolbar("Irpf");
		
		saveButton = new AonToolbarButton( AON.MSG.saveAction(), AON.CSS.aonIconSave() );
		saveButton.addClickHandler(e -> onSave());
		toolbar.add(saveButton);
		
		this.yearLB = new ListBox();
		initializeYearLB(this.yearLB);
		this.toolbar.add(this.yearLB);
	}

	// ----------------------------------------------- Toolbar.Methods

	public void onSave() {
		changeYear();
	}
	
	// -------------------------------------------------- ContrataEmployee.Methods
	
	public void hideToolbar(){
		dockLayoutPanel.remove(toolbar);
		mainPanel.getElement().getStyle().setMarginTop(0, Unit.PX);
	}
	
	public void setYearLB(ListBox yearLB) {
		this.yearLB = yearLB;
	}
	
}
