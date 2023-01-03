package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarSmallButton;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.EmployeeIrpf;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public abstract class EmployeeContractIrpf extends Composite {

	// ----------------------------------------------- UiBinder 
	
	private static EmployeeContractIrpfUiBinder uiBinder = GWT.create(EmployeeContractIrpfUiBinder.class);

	interface EmployeeContractIrpfUiBinder extends UiBinder<Widget, EmployeeContractIrpf> {}
	
	// ----------------------------------------------- UiField 
	
	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String flexColumn();
		String flex();
		String width120();
		String width90();
		String width70();
		String width50();
		String width40();
		String title();
		String header();
		String l00();
		String m190();
	}
	
	@UiField
	DockLayoutPanel dockLayoutPanel;
	
	@UiField
	ScrollPanel scrollPanel;
	
	@UiField
	HTMLPanel mainPanel;
	
	// ----------------------------------------------- Variables 
	
//	private static NumberFormat df2 = NumberFormat.getFormat("###,##0.00");
	
	private EmployeeContractIrpfObject employeeContractIrpfObject;
	
	private AonToolbar toolbar;
	private AonToolbarButton saveButton;
	private ListBox yearLB;
	
	// ----------------------------------------------- Constructor 
	
	protected EmployeeContractIrpf() {
		initializeToolbarPanel();
		initWidget(uiBinder.createAndBindUi(this));
		
		this.getElement().getStyle().setHeight(100, Unit.PCT);
		dockLayoutPanel.addNorth(toolbar, AonToolbar.HEIGTH);

		saveButton.setEnabled(false);
		
		scrollPanel.setHeight((Window.getClientHeight() - 200) + "px");
	}
		
	// ----------------------------------------------- setEmployeeContractIrpfObject 
	
	public void setEmployeeContractIrpfObject(EmployeeContractIrpfObject employeeContractIrpfObject) {
		this.employeeContractIrpfObject = employeeContractIrpfObject;
		
		initializeYearLB(yearLB);
		Date auxDate = DateUtils.getDate(0, Integer.parseInt(yearLB.getSelectedValue()));
		Date date = DateUtils.getFirstDayOfMonth(auxDate);
		
		this.employeeContractIrpfObject.getEmployeeIrpf(
				date,
				r -> {
					initEmployeeIrpfTable();
					String ssNumber = this.employeeContractIrpfObject.getSSNumber();
					if(AonStringUtils.isBlank(ssNumber)) {
						this.yearLB.setEnabled(false);
						this.saveButton.setEnabled(false);
						showErrorMessage("Error n\u00FAmero Seguridad Social", "El contrato " + employeeContractIrpfObject.getFullName() + " no tiene definido el n\u00FAmero de la Seguridad Social. Def\u00EDnalo antes de rellas los IRPFs");
					}
				},
				t -> {});
	}

	protected abstract void showErrorMessage(String title, String message);

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
			getMonthRow(table, monthRow, month);
		}
		
		HTMLPanel accumulateRow = new HTMLPanel("");
		getAccumulateRow(accumulateRow);
		table.add(accumulateRow);
			
		// Add table to mainPanel
		mainPanel.clear();
		mainPanel.add(table);
	}

	public void getHeaderRow(HTMLPanel headerRow) {
		headerRow.addStyleName(style.flex());
		headerRow.addStyleName(style.header());
		
		Label emptyLabel = new Label("");
		emptyLabel.addStyleName(style.width90());
		
//		Label typeLabel = new Label("Tipo");
//		typeLabel.addStyleName(style.width90());
//		typeLabel.addStyleName(style.title());
		
		Label irpfPercentLabel = new Label("% IRPF");
		irpfPercentLabel.addStyleName(style.width70());
		irpfPercentLabel.addStyleName(style.title());
		
		Label moneyBaseLabel = new Label("Base Dineraria");
		moneyBaseLabel.addStyleName(style.width90());
		moneyBaseLabel.addStyleName(style.title());
		
		Label moneyQuoteLabel = new Label("IRPF Dineraria");
		moneyQuoteLabel.addStyleName(style.width90());
		moneyQuoteLabel.addStyleName(style.title());
		
		Label inkindBaseLabel = new Label("Base Especie");
		inkindBaseLabel.addStyleName(style.width90());
		inkindBaseLabel.addStyleName(style.title());
		
		Label inkindQuoteLabel = new Label("IRPF Especie");
		inkindQuoteLabel.addStyleName(style.width90());
		inkindQuoteLabel.addStyleName(style.title());
		
		Label totalIrpfLabel = new Label("Total IRPF");
		totalIrpfLabel.addStyleName(style.width90());
		totalIrpfLabel.addStyleName(style.title()); 
		
		Label employeeSSQuoteLabel = new Label("Cuota SS Trabajador");
		employeeSSQuoteLabel.addStyleName(style.width120());
		employeeSSQuoteLabel.addStyleName(style.title()); 
		
		Label actionLabel = new Label("");
		actionLabel.addStyleName(style.width50());
		
		headerRow.add(emptyLabel);
//		headerRow.add(typeLabel);
		headerRow.add(irpfPercentLabel);
		headerRow.add(moneyBaseLabel);
		headerRow.add(moneyQuoteLabel);
		headerRow.add(inkindBaseLabel);
		headerRow.add(inkindQuoteLabel);
		headerRow.add(totalIrpfLabel);
		headerRow.add(employeeSSQuoteLabel);
		headerRow.add(actionLabel);
	}
	
	private void getMonthRow(HTMLPanel table, HTMLPanel monthRow, int month) {
		// Get date
		final Date date = DateUtils.getFirstDayOfMonth(DateUtils.getDate(month, Integer.parseInt(yearLB.getSelectedValue())));
		
		// Get employeeIrpf by date
		List<EmployeeIrpf> employeeIrpfList = this.employeeContractIrpfObject.getEmployeeIrpf(date);
		if(employeeIrpfList.isEmpty())
			createFirstMonthRow(table, null, monthRow, month, date, true);
		else
			for(int i=0; i<employeeIrpfList.size(); i++)
				createFirstMonthRow(table, employeeIrpfList.get(i), i==0 ? monthRow : new HTMLPanel(""), month, date, i==0);
	}
	
	private void createFirstMonthRow(HTMLPanel table, EmployeeIrpf employeeIrpf, HTMLPanel monthRow, int month, Date date, boolean firstLine) {
		monthRow.addStyleName(style.flex());
		
		List<TextBox> valuesLabels = new ArrayList<>();
		
		Label monthLabel = new Label("");
		if(firstLine)
			monthLabel.setText(getStringMonth(month));
		monthLabel.addStyleName(style.width90());
		monthLabel.addStyleName(style.title());
		
//		Label typeLabel = new Label("");
//		typeLabel.addStyleName(style.width90());
		
		HTMLPanel irpfPercentPanel = new HTMLPanel("");
		irpfPercentPanel.addStyleName(style.width70());
		TextBox irpfPercentBox = new ExpressionBox();
		irpfPercentBox.addStyleName(style.width40());
		irpfPercentBox.addStyleName(AON.AON_TEXT_RIGHT);
		valuesLabels.add(irpfPercentBox);
		irpfPercentPanel.add(irpfPercentBox);
		
		HTMLPanel moneyBasePanel = new HTMLPanel("");
		moneyBasePanel.addStyleName(style.width90());
		TextBox moneyBaseBox = new ExpressionBox();
		moneyBaseBox.addStyleName(style.width50());
		moneyBaseBox.addStyleName(AON.AON_TEXT_RIGHT);
		valuesLabels.add(moneyBaseBox);
		moneyBasePanel.add(moneyBaseBox);
		
		HTMLPanel moneyQuotePanel = new HTMLPanel("");
		moneyQuotePanel.addStyleName(style.width90());
		TextBox moneyQuoteBox = new ExpressionBox();
		moneyQuoteBox.addStyleName(style.width50());
		moneyQuoteBox.addStyleName(AON.AON_TEXT_RIGHT);
		valuesLabels.add(moneyQuoteBox);
		moneyQuotePanel.add(moneyQuoteBox);
		
		HTMLPanel inkindBasePanel = new HTMLPanel("");
		inkindBasePanel.addStyleName(style.width90());
		TextBox inkindBaseBox = new ExpressionBox();
		inkindBaseBox.addStyleName(style.width50());
		inkindBaseBox.addStyleName(AON.AON_TEXT_RIGHT);
		valuesLabels.add(inkindBaseBox);
		inkindBasePanel.add(inkindBaseBox);
		
		HTMLPanel inkindQuotePanel = new HTMLPanel("");
		inkindQuotePanel.addStyleName(style.width90());
		TextBox inkindQuoteBox = new ExpressionBox();
		inkindQuoteBox.addStyleName(style.width50());
		inkindQuoteBox.addStyleName(AON.AON_TEXT_RIGHT);
		valuesLabels.add(inkindQuoteBox);
		inkindQuotePanel.add(inkindQuoteBox);
		
		HTMLPanel totalIrpfBasePanel = new HTMLPanel("");
		totalIrpfBasePanel.addStyleName(style.width90());
		TextBox totalIrpfBaseBox = new ExpressionBox();
		totalIrpfBaseBox.addStyleName(style.width50());
		totalIrpfBaseBox.addStyleName(AON.AON_TEXT_RIGHT);
		valuesLabels.add(totalIrpfBaseBox);
		totalIrpfBasePanel.add(totalIrpfBaseBox);
		
		HTMLPanel employeeSSQuoteBasePanel = new HTMLPanel("");
		employeeSSQuoteBasePanel.addStyleName(style.width120());
		TextBox employeeSSQuoteBaseBox = new ExpressionBox();
		employeeSSQuoteBaseBox.addStyleName(style.width50());
		employeeSSQuoteBaseBox.addStyleName(AON.AON_TEXT_RIGHT);
		valuesLabels.add(employeeSSQuoteBaseBox);
		employeeSSQuoteBasePanel.add(employeeSSQuoteBaseBox);
		
		HTMLPanel actionPanel = new HTMLPanel("");
		actionPanel.addStyleName(style.width50());
		
		if(null != employeeIrpf) {
//			typeLabel.setText(employeeIrpf.getSalaryType());
			irpfPercentBox.setValue(format(employeeIrpf.getIrpfPercent()));
			moneyBaseBox.setValue(format(employeeIrpf.getMoneyBase()));
			moneyQuoteBox.setValue(format(employeeIrpf.getMoneyQuote()));
			inkindBaseBox.setValue(format(employeeIrpf.getInkindBase()));
			inkindQuoteBox.setValue(format(employeeIrpf.getInkindQuote()));
			employeeSSQuoteBaseBox.setValue(format(employeeIrpf.getEmployeeSSQuote()));
			totalIrpfBaseBox.setValue(format(employeeIrpf.getTotalIrpf()));
			if(!employeeIrpf.isNew())
				valuesLabels.forEach(box -> box.setEnabled(false));
		
			// Check styles
			if(AonStringUtils.equalsIgnoreCase(employeeIrpf.getSalaryType(), "L00")) {
				employeeSSQuoteBaseBox.addStyleName(style.l00());
				employeeSSQuoteBaseBox.setTitle("Valor obtenido de un L00");
			} else if(AonStringUtils.equalsIgnoreCase(employeeIrpf.getSalaryType(), "Manual")) {
				employeeSSQuoteBaseBox.addStyleName(style.m190());
				employeeSSQuoteBaseBox.setTitle("Valor obtenido de un M190 (Manual)");
			}
		}
		
		// Add ValueChangeHandlers
		irpfPercentBox.addValueChangeHandler(e -> {
			Double irpfPercent = parseDouble(irpfPercentBox.getValue());
			Double moneyBase = parseDouble(moneyBaseBox.getValue());
			Double moneyQuote = parseDouble(moneyQuoteBox.getValue());
			
			moneyBaseBox.setValue(format((moneyQuote * 100.00 / irpfPercent)));
			moneyQuoteBox.setValue(format((moneyBase * irpfPercent / 100.00)));  
			
			Double inkindBase = parseDouble(inkindBaseBox.getValue());
			Double inkindQuote = parseDouble(inkindQuoteBox.getValue());
			
			inkindBaseBox.setValue(format((inkindQuote * 100.00 / irpfPercent)));
			inkindQuoteBox.setValue(format((inkindBase * irpfPercent / 100.00))); 
			
			checkTotalIrpfAmount(moneyQuoteBox, inkindQuoteBox, totalIrpfBaseBox);
			createUpdateEmployeeIrpf(date, irpfPercentBox, moneyBaseBox, moneyQuoteBox, inkindBaseBox, inkindQuoteBox, employeeSSQuoteBaseBox, totalIrpfBaseBox, null == employeeIrpf ? null : employeeIrpf.getSalaryId());
		});
		
		moneyBaseBox.addValueChangeHandler(e -> {
			Double irpfPercent = parseDouble(irpfPercentBox.getValue());
			Double moneyBase = parseDouble(moneyBaseBox.getValue());
			
			moneyQuoteBox.setValue(format((moneyBase * irpfPercent / 100.00)));  
			
			checkTotalIrpfAmount(moneyQuoteBox, inkindQuoteBox, totalIrpfBaseBox);
			createUpdateEmployeeIrpf(date, irpfPercentBox, moneyBaseBox, moneyQuoteBox, inkindBaseBox, inkindQuoteBox, employeeSSQuoteBaseBox, totalIrpfBaseBox, null == employeeIrpf ? null : employeeIrpf.getSalaryId());
		});
		
		moneyQuoteBox.addValueChangeHandler(e -> {
			Double irpfPercent = parseDouble(irpfPercentBox.getValue());
			Double moneyBase = parseDouble(moneyBaseBox.getValue());
			Double moneyQuote = parseDouble(moneyQuoteBox.getValue());
			
			if(moneyBase * irpfPercent / 100.00 != moneyQuote) {
				AonDialog dialog = new AonDialog("C\u00e1lculo IRPF Dineraria", new HTMLPanel("El valor introducido como <b>IRPF Dineraria</b> (" + format(moneyQuote) + ") no corresponde con el calculado en funci\u00f3n al <b>\u0025 IRPF</b> (" + irpfPercent + ") y a la <b>Base Dineraria</b> (" + moneyBase + ") --> <b>IRPF Dineraria</b> (" + format(moneyBase * irpfPercent / 100.00) + ").<br>\u00bfDesea mantener el valor introducido manualmente o corregirlo\u003f"));
				dialog.confirm(new AonAcceptDialogCallback() {
					
					@Override
					public void onCancel() {
						checkTotalIrpfAmount(moneyQuoteBox, inkindQuoteBox, totalIrpfBaseBox);
						createUpdateEmployeeIrpf(date, irpfPercentBox, moneyBaseBox, moneyQuoteBox, inkindBaseBox, inkindQuoteBox, employeeSSQuoteBaseBox, totalIrpfBaseBox, null == employeeIrpf ? null : employeeIrpf.getSalaryId());
					}
					
					@Override
					public void onAccept() {
						checkTotalIrpfAmount(moneyBase * irpfPercent / 100.00, inkindQuoteBox, totalIrpfBaseBox);
						createUpdateEmployeeIrpf(date, irpfPercentBox, moneyBaseBox, moneyBase * irpfPercent / 100.00, inkindBaseBox, inkindQuoteBox, employeeSSQuoteBaseBox, totalIrpfBaseBox, null == employeeIrpf ? null : employeeIrpf.getSalaryId());
					}
				});
				dialog.setAcceptText("Corregir");
				dialog.setCancelText("Mantener");
			}
			
		});
		
		inkindBaseBox.addValueChangeHandler(e -> {
			Double irpfPercent = parseDouble(irpfPercentBox.getValue());
			Double inkindBase = parseDouble(inkindBaseBox.getValue());
			
			inkindQuoteBox.setValue(format((inkindBase * irpfPercent / 100.00))); 
			
			checkTotalIrpfAmount(moneyQuoteBox, inkindQuoteBox, totalIrpfBaseBox);
			createUpdateEmployeeIrpf(date, irpfPercentBox, moneyBaseBox, moneyQuoteBox, inkindBaseBox, inkindQuoteBox, employeeSSQuoteBaseBox, totalIrpfBaseBox, null == employeeIrpf ? null : employeeIrpf.getSalaryId());
		});
		
		inkindQuoteBox.addValueChangeHandler(e -> {
			Double irpfPercent = parseDouble(irpfPercentBox.getValue());
			Double inkindBase = parseDouble(inkindBaseBox.getValue());
			Double inkindQuote = parseDouble(inkindQuoteBox.getValue());
			
			if(inkindBase * irpfPercent / 100.00 != inkindQuote) {
				AonDialog dialog = new AonDialog("C\u00e1lculo IRPF Especie", new HTMLPanel("El valor introducido como <b>IRPF Especie</b> (" + format(inkindQuote) + ") no corresponde con el calculado en funci\u00f3n al <b>\u0025 IRPF</b> (" + irpfPercent + ") y a la <b>Base Especie</b> (" + inkindBase + ") --> <b>IRPF Especie</b> (" + format(inkindBase * irpfPercent / 100.00) + ").<br>\u00bfDesea mantener el valor introducido manualmente o corregirlo\u003f"));
				dialog.confirm(new AonAcceptDialogCallback() {
					
					@Override
					public void onCancel() {
						checkTotalIrpfAmount(moneyQuoteBox, inkindQuoteBox, totalIrpfBaseBox);
						createUpdateEmployeeIrpf(date, irpfPercentBox, moneyBaseBox, moneyQuoteBox, inkindBaseBox, inkindQuoteBox, employeeSSQuoteBaseBox, totalIrpfBaseBox, null == employeeIrpf ? null : employeeIrpf.getSalaryId());
					}
					
					@Override
					public void onAccept() {
						checkTotalIrpfAmount(moneyQuoteBox, inkindBase * irpfPercent / 100.00, totalIrpfBaseBox);
						createUpdateEmployeeIrpf(date, irpfPercentBox, moneyBaseBox, moneyQuoteBox, inkindBaseBox, inkindBase * irpfPercent / 100.00, employeeSSQuoteBaseBox, totalIrpfBaseBox, null == employeeIrpf ? null : employeeIrpf.getSalaryId());
					}
				});
				dialog.setAcceptText("Corregir");
				dialog.setCancelText("Mantener");
			}
		});
		
		totalIrpfBaseBox.addValueChangeHandler(e -> createUpdateEmployeeIrpf(date, irpfPercentBox, moneyBaseBox, moneyQuoteBox, inkindBaseBox, inkindQuoteBox, employeeSSQuoteBaseBox, totalIrpfBaseBox, null == employeeIrpf ? null : employeeIrpf.getSalaryId()));
		employeeSSQuoteBaseBox.addValueChangeHandler(e -> createUpdateEmployeeIrpf(date, irpfPercentBox, moneyBaseBox, moneyQuoteBox, inkindBaseBox, inkindQuoteBox, employeeSSQuoteBaseBox, totalIrpfBaseBox, null == employeeIrpf ? null : employeeIrpf.getSalaryId()));
		
		// Add elements to monthRow
		monthRow.add(monthLabel);
//		monthRow.add(typeLabel);
		monthRow.add(irpfPercentPanel);
		monthRow.add(moneyBasePanel);
		monthRow.add(moneyQuotePanel);
		monthRow.add(inkindBasePanel);
		monthRow.add(inkindQuotePanel);
		monthRow.add(totalIrpfBasePanel);
		monthRow.add(employeeSSQuoteBasePanel);
		if(null != employeeIrpf && AonStringUtils.equalsIgnoreCase("Manual", employeeIrpf.getSalaryType())) {
			AonToolbarSmallButton deleteBtn = new AonToolbarSmallButton("Eliminar", AON.CSS.aonIconDelete());
			actionPanel.add(deleteBtn);
			deleteBtn.addClickHandler(e -> {
				employeeContractIrpfObject.deleteEmployeeIrpf(employeeIrpf);
				initEmployeeIrpfTable();
			});
		}
		monthRow.add(actionPanel);
		table.add(monthRow);
	}

	public void getAccumulateRow(HTMLPanel accumulateRow) {
		accumulateRow.addStyleName(style.flex());
		accumulateRow.addStyleName(style.header());
		
		Label emptyLabel = new Label("");
		emptyLabel.addStyleName(style.width90());
		
//		Label typeLabel = new Label("");
//		typeLabel.addStyleName(style.width90());
		
		Label irpfPercentLabel = new Label("");
		irpfPercentLabel.addStyleName(style.width70());
		
		Label moneyBaseLabel = new Label(format(employeeContractIrpfObject.getAccumulateMoneyBase()));
		moneyBaseLabel.addStyleName(style.width90());
		moneyBaseLabel.addStyleName(style.title());
		
		Label moneyQuoteLabel = new Label(format(employeeContractIrpfObject.getAccumulateMoneyQuote()));
		moneyQuoteLabel.addStyleName(style.width90());
		moneyQuoteLabel.addStyleName(style.title());
		
		Label inkindBaseLabel = new Label(format(employeeContractIrpfObject.getAccumulateInkindBase()));
		inkindBaseLabel.addStyleName(style.width90());
		inkindBaseLabel.addStyleName(style.title());
		
		Label inkindQuoteLabel = new Label(format(employeeContractIrpfObject.getAccumulateInkindQuote()));
		inkindQuoteLabel.addStyleName(style.width90());
		inkindQuoteLabel.addStyleName(style.title());
		
		Label totalIrpfLabel = new Label(format(employeeContractIrpfObject.getAccumulateTotalIrpf()));
		totalIrpfLabel.addStyleName(style.width90());
		totalIrpfLabel.addStyleName(style.title()); 
		
		Label employeeSSQuoteLabel = new Label(format(employeeContractIrpfObject.getAccumulateEmployeeSSQuote()));
		employeeSSQuoteLabel.addStyleName(style.width120());
		employeeSSQuoteLabel.addStyleName(style.title()); 
		
		Label actionLabel = new Label("");
		actionLabel.addStyleName(style.width50());
		
		accumulateRow.add(emptyLabel);
//		accumulateRow.add(typeLabel);
		accumulateRow.add(irpfPercentLabel);
		accumulateRow.add(moneyBaseLabel);
		accumulateRow.add(moneyQuoteLabel);
		accumulateRow.add(inkindBaseLabel);
		accumulateRow.add(inkindQuoteLabel);
		accumulateRow.add(totalIrpfLabel);
		accumulateRow.add(employeeSSQuoteLabel);
		accumulateRow.add(actionLabel);
	}
	
	// ----------------------------------------------- Auxiliar methods
	
	private Double parseDouble(String value) {
		Double result = 0.0001;
		try {
			if(AonStringUtils.isNotBlank(value)) {
				if(value.contains(","))
					value = value.replace(".", "");
				value = value.replace(',', '.');
			}
			result = Double.parseDouble(value);
		} catch (NumberFormatException e) {
			// Not use
		}
		return result;
	}
	
	private void checkTotalIrpfAmount(TextBox moneyQuoteBox, TextBox inkindQuoteBox, TextBox totalIrpfBaseBox) {
		try{
			Double moneyQuote = parseDouble(moneyQuoteBox.getValue());
			Double inkindQuote = parseDouble(inkindQuoteBox.getValue());
			totalIrpfBaseBox.setValue(format(moneyQuote + inkindQuote)); 
		} catch (Exception e) {
			// Skip exception
		}
	}
	
	private void checkTotalIrpfAmount(Double moneyQuote, TextBox inkindQuoteBox, TextBox totalIrpfBaseBox) {
		try{
			Double inkindQuote = parseDouble(inkindQuoteBox.getValue());
			totalIrpfBaseBox.setValue(format(moneyQuote + inkindQuote)); 
		} catch (Exception e) {
			// Skip exception
		}
	}
	
	private void checkTotalIrpfAmount(TextBox moneyQuoteBox, Double inkindQuote, TextBox totalIrpfBaseBox) {
		try{
			Double moneyQuote = parseDouble(moneyQuoteBox.getValue());
			totalIrpfBaseBox.setValue(format(moneyQuote + inkindQuote)); 
		} catch (Exception e) {
			// Skip exception
		}
	}

	private void createUpdateEmployeeIrpf(Date date, TextBox irpfPercentBox, TextBox moneyBaseBox, TextBox moneyQuoteBox, TextBox inkindBaseBox, TextBox inkindQuoteBox, TextBox employeeSSQuoteBaseBox, TextBox totalIrpfBaseBox, Integer salaryId) {
		try{
			this.employeeContractIrpfObject.createUpdateEmployeeIrpf(
					date, 
					parseDouble(moneyBaseBox.getValue()),
					parseDouble(moneyQuoteBox.getValue()),
					parseDouble(inkindBaseBox.getValue()),
					parseDouble(inkindQuoteBox.getValue()),
					parseDouble(irpfPercentBox.getValue()),
					parseDouble(employeeSSQuoteBaseBox.getValue()),
					parseDouble(totalIrpfBaseBox.getValue()),
					salaryId);	
			initEmployeeIrpfTable();
		} catch (Exception e) {
			// Skip exception
		}
	}
	
	private void createUpdateEmployeeIrpf(Date date, TextBox irpfPercentBox, TextBox moneyBaseBox, Double moneyQuote, TextBox inkindBaseBox, TextBox inkindQuoteBox, TextBox employeeSSQuoteBaseBox, TextBox totalIrpfBaseBox, Integer salaryId) {
		try{
			this.employeeContractIrpfObject.createUpdateEmployeeIrpf(
					date, 
					parseDouble(moneyBaseBox.getValue()),
					moneyQuote,
					parseDouble(inkindBaseBox.getValue()),
					parseDouble(inkindQuoteBox.getValue()),
					parseDouble(irpfPercentBox.getValue()),
					parseDouble(employeeSSQuoteBaseBox.getValue()),
					parseDouble(totalIrpfBaseBox.getValue()),
					salaryId);	
			initEmployeeIrpfTable();
		} catch (Exception e) {
			// Skip exception
		}
	}
	
	private void createUpdateEmployeeIrpf(Date date, TextBox irpfPercentBox, TextBox moneyBaseBox, TextBox moneyQuoteBox, TextBox inkindBaseBox, Double inkindQuote, TextBox employeeSSQuoteBaseBox, TextBox totalIrpfBaseBox, Integer salaryId) {
		try{
			this.employeeContractIrpfObject.createUpdateEmployeeIrpf(
					date, 
					parseDouble(moneyBaseBox.getValue()),
					parseDouble(moneyQuoteBox.getValue()),
					parseDouble(inkindBaseBox.getValue()),
					inkindQuote,
					parseDouble(irpfPercentBox.getValue()),
					parseDouble(employeeSSQuoteBaseBox.getValue()),
					parseDouble(totalIrpfBaseBox.getValue()),
					salaryId);	
			initEmployeeIrpfTable();
		} catch (Exception e) {
			// Skip exception
		}
	}
	
	public static String format(Double amount) {
		return AonNumberUtils.isNotValid(amount) ? AON.CURRENCY_FORMAT.format(AON.round(0.00)) : AON.CURRENCY_FORMAT.format(AON.round(amount));
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
		Integer contractStartYear = employeeContractIrpfObject.getContractStartYear();
		
		Integer yearAux = year;
		yearAux++;
		
		yearLB.clear();
		
		while(contractStartYear <= yearAux) {
			yearLB.addItem(contractStartYear.toString(), contractStartYear.toString());
			contractStartYear++;
		}
		
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
