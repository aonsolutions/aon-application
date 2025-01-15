package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.MultiFileUpload;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDockLayout;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonExpandButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarSmallButton;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.EmployeeIrpf;
import com.esferalia.aon.gwt.payroll.shared.FIEService;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class EmployeeContractIrpf extends AonCustomDockLayout {

	// ----------------------------------------------- ContextMenu 
	
	class DownloandExcelCommand implements ScheduledCommand {

		@Override
		public void execute() {
			onDownloadExcel();
		}
	}
	
	class UploadExcelCommand implements ScheduledCommand {

		@Override
		public void execute() {
			onUploadExcel();
		}
	}
	
	class ExcelContextMenu extends ContextMenu {

		public ExcelContextMenu() {
			addMenuItem("Descargar datos excel", new DownloandExcelCommand(), AON.CSS.aonIconExcel(), "downloadExcel");
			addMenuItem("Carga datos excel", new UploadExcelCommand(), AON.CSS.aonIconExcel(), "uploadExcel");			
		}
		
		private MenuItem addMenuItem(String title, ScheduledCommand command, String iconStyle, String debugId) {
			MenuItem item = addItem(title, command, iconStyle, AON.AON_ICON_CMD_BUTTON, AON.CSS.aonCmdItem());
			item.ensureDebugId(debugId);
			return item;
		}
		
	}
	
	// ----------------------------------------------- Variables 
	
	private HTMLPanel container;
	private HTMLPanel messagePanel = new HTMLPanel("");
	
	private ExcelContextMenu excelContextMenu;
	
	private AonToolbarButton saveButton;
	private ListBox yearLB;
	private AonExpandButton excelButton;
	
	private ScrollPanel scrollPanel;
	private SimplePanel containerTable;
	private AonCustomTable tab;
	
	private MultiFileUpload msjFIEFileUpload;
	
	private static enum COLUMNS {
		  MON(AonStringUtils.EMPTY					,"6rem"				,"font-weight: bold; text-align: center;")
		, IRF("% IRFP"								,"7rem"  			,"text-align: right;")
		, BDN("Base Dineraria"						,"7rem"  			,"text-align: right;")
		, IDN("IRPF Dineraria"						,"7rem"  			,"text-align: right;")
		, BES("Base Especie"						,"7rem"  			,"text-align: right;")
		, IES("IRPF Especie"						,"7rem"  			,"text-align: right;")
		, TIR("Total IRPF"							,"7rem"  			,"text-align: right;")
		, QUO("Cuota SS Trabajador"					,"9rem"  			,"text-align: right;")
		, BUT(AonStringUtils.EMPTY					,"3rem" 			,"")
		;

		String headerLabel;
		String colWidth;
		String cellStyleClass;

		private COLUMNS(String headerLabel,String colWidth) {
			this(headerLabel, colWidth, null);
		}

		private COLUMNS(String headerLabel,String colWidth,String cellStyleClass) {
			this.headerLabel = headerLabel;
			this.colWidth = colWidth;
			this.cellStyleClass = cellStyleClass;
		}
		public String getColWidth() {
			return colWidth;
		}
		public String getHeaderLabel() {
			return headerLabel;
		}
		public String getCellStyleClass() {
			return cellStyleClass;
		}
	}
	
	private DomainEmployeesServiceAsync employeesService = DomainEmployeesServiceAsync.newInstance();
	
	private List<EmployeeIrpf> employeeIrpfList;
	private Integer contractId;
	private String ssNumber;
	private String fullName;
	private String document;
	private Date contractStartDate;
	
	// ----------------------------------------------- Constructor 
	
	protected EmployeeContractIrpf() {
		super("Impuesto sobre la Renta de las Personas Fisicas (IRPF)");
		
		excelContextMenu = new ExcelContextMenu();
		
		initializeToolbarPanel();
		
		container = new HTMLPanel("");
		container.addStyleName(AON.CSS.aonFlexColumn());

		container.add(messagePanel);
		
		scrollPanel = new ScrollPanel();
		scrollPanel.setHeight("100%");
		scrollPanel.getElement().getStyle().setProperty("display", "flex");
		scrollPanel.getElement().getStyle().setProperty("justify-content", "center");
		containerTable = new SimplePanel();
		containerTable.getElement().getStyle().setProperty("padding", "0 1rem");
		scrollPanel.setWidget(containerTable);
		
		container.add(scrollPanel);
		
		add(container);
	}
	
	@Override
	protected void onClearFilter() {}
	
	private void initializeToolbarPanel() {
		saveButton = new AonToolbarButton( AON.MSG.saveAction(), AON.CSS.aonIconSave() );
		saveButton.setEnabled(false);
		saveButton.addClickHandler(e -> onSave());
		addToolbarButton(saveButton);
		
		yearLB = new ListBox();
		addToolbarButton(yearLB);
		
		excelButton = new AonExpandButton("Excel", AON.CSS.aonIconExcel()) {
			
			@Override
			public void onExpandClick(ClickEvent event) {
				NativeEvent nativeEvent = event.getNativeEvent();
				excelContextMenu.setPopupPosition(nativeEvent.getClientX(), nativeEvent.getClientY());
				excelContextMenu.show();
			}
			
			@Override
			public void onDefaultClick(ClickEvent evet) {
				onDownloadExcel();
			}
		};
		excelButton.setVisible(false);
		addToolbarButton(excelButton);
		
		hideSearchWidget();
	}
		
	// ----------------------------------------------- setEmployeeContractIrpfObject 
	
	public void setEmployeeContractIrpfObject(Integer contractId, String fullName, String document, String ssNumber, Date contractStartDate) {
		this.contractId = contractId;
		this.ssNumber = ssNumber;
		this.fullName = fullName;
		this.document = document;
		this.contractStartDate = contractStartDate;
		this.employeeIrpfList = new ArrayList<>();
		
		initializeYearLB(yearLB);
		
		onSearch();
	}
	
	private void onSearch() {
		AonMessagePanel.showLoading(messagePanel, "Obteniendo Impuesto sobre la Renta de las Personas Fisicas (IRPF) ...");
		
		Date auxDate = DateUtils.getDate(0, Integer.parseInt(yearLB.getSelectedValue()));
		Date date = DateUtils.getFirstDayOfMonth(auxDate);
		
		getEmployeeIrpfList(date,
				r -> {
					AonMessagePanel.hideMessage(messagePanel);
					
					initEmployeeIrpfTable();
					
					if(AonStringUtils.isBlank(ssNumber)) {
						this.yearLB.setEnabled(false);
						this.saveButton.setEnabled(false);
						AonMessagePanel.showError(messagePanel, "El contrato " + fullName + " no tiene definido el n\u00FAmero de la Seguridad Social. Def\u00EDnalo antes de rellas los IRPFs");
					}
				},
				t -> {});
	}
	
	// ----------------------------------------------- setEmployeeContractPaymentsObject.Methods
	
	private void initEmployeeIrpfTable() {
		containerTable.clear();
		tab = new AonCustomTable();
		
		paintHeader();
		containerTable.setWidget(tab);
		
		// Fill lines
		for(int month = 0; month < 12; month++) {
			getMonthRow(month);
		}
		
		getAccumulateRow();
	}

	private void paintHeader() {
		HTMLPanel header = tab.createHeader();
		header.getElement().getStyle().setProperty("top", "0px");
		
		for ( COLUMNS col : COLUMNS.values()) 
			tab.addHeader(new Label(col.getHeaderLabel()), col.getColWidth(), col.getCellStyleClass());
	}
	
	private void getMonthRow(int month) {
		// Get date
		final Date date = DateUtils.getFirstDayOfMonth(DateUtils.getDate(month, Integer.parseInt(yearLB.getSelectedValue())));
		
		// Get employeeIrpf by date
		List<EmployeeIrpf> employeeIrpfList = getEmployeeIrpf(date);
		if(employeeIrpfList.isEmpty())
			createFirstMonthRow(null, month, date, true);
		else
			for(int i=0; i<employeeIrpfList.size(); i++)
				createFirstMonthRow(employeeIrpfList.get(i), month, date, i==0);
	}
	
	private void createFirstMonthRow(EmployeeIrpf employeeIrpf, int month, Date date, boolean firstLine) {
		List<TextBox> valuesLabels = new ArrayList<>();
		
		FlowPanel buttonContainer = new FlowPanel();
		buttonContainer.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		
		if(null != employeeIrpf && AonStringUtils.equalsIgnoreCase("Manual", employeeIrpf.getSalaryType())) {
			AonToolbarSmallButton deleteBtn = new AonToolbarSmallButton("Eliminar", AON.CSS.aonIconDelete());
			deleteBtn.addStyleName(AON.CSS.aonCustomRowButtom());
			deleteBtn.addClickHandler(e -> {
				deleteEmployeeIrpf(employeeIrpf);
				initEmployeeIrpfTable();
				saveButton.setEnabled(true);
			});
			buttonContainer.add(deleteBtn);
		}
		
		HTMLPanel row = tab.createRow();
		
		Label monthLabel = new Label(getStringMonth(month));
		monthLabel.setTitle(getStringMonth(month));
		tab.addInlineStyle(monthLabel, COLUMNS.MON.getCellStyleClass());
		tab.addRow(row, monthLabel, COLUMNS.MON.getColWidth());
		
		HTMLPanel irpfPercentPanel = new HTMLPanel(AonStringUtils.EMPTY);
		irpfPercentPanel.addStyleName(AON.CSS.aonDisplayFlexEnd());
		TextBox irpfPercentBox = new ExpressionBox();
		irpfPercentBox.setWidth("5rem");
		irpfPercentBox.addStyleName(AON.CSS.aonTextRight());
		irpfPercentPanel.add(irpfPercentBox);
		valuesLabels.add(irpfPercentBox);
		tab.addInlineStyle(irpfPercentPanel, COLUMNS.IRF.getCellStyleClass());
		tab.addRow(row, irpfPercentPanel, COLUMNS.IRF.getColWidth());
		
		HTMLPanel moneyBasePanel = new HTMLPanel(AonStringUtils.EMPTY);
		moneyBasePanel.addStyleName(AON.CSS.aonDisplayFlexEnd());
		TextBox moneyBaseBox = new ExpressionBox();
		moneyBaseBox.setWidth("5rem");
		moneyBaseBox.addStyleName(AON.CSS.aonTextRight());
		valuesLabels.add(moneyBaseBox);
		moneyBasePanel.add(moneyBaseBox);
		tab.addInlineStyle(moneyBasePanel, COLUMNS.BDN.getCellStyleClass());
		tab.addRow(row, moneyBasePanel, COLUMNS.BDN.getColWidth());
		
		HTMLPanel moneyQuotePanel = new HTMLPanel(AonStringUtils.EMPTY);
		moneyQuotePanel.addStyleName(AON.CSS.aonDisplayFlexEnd());
		TextBox moneyQuoteBox = new ExpressionBox();
		moneyQuoteBox.setWidth("5rem");
		moneyQuoteBox.addStyleName(AON.CSS.aonTextRight());
		valuesLabels.add(moneyQuoteBox);
		moneyQuotePanel.add(moneyQuoteBox);
		tab.addInlineStyle(moneyQuotePanel, COLUMNS.IDN.getCellStyleClass());
		tab.addRow(row, moneyQuotePanel, COLUMNS.IDN.getColWidth());
		
		HTMLPanel inkindBasePanel = new HTMLPanel(AonStringUtils.EMPTY);
		inkindBasePanel.addStyleName(AON.CSS.aonDisplayFlexEnd());
		TextBox inkindBaseBox = new ExpressionBox();
		inkindBaseBox.setWidth("5rem");
		inkindBaseBox.addStyleName(AON.CSS.aonTextRight());
		valuesLabels.add(inkindBaseBox);
		inkindBasePanel.add(inkindBaseBox);
		tab.addInlineStyle(inkindBasePanel, COLUMNS.BES.getCellStyleClass());
		tab.addRow(row, inkindBasePanel, COLUMNS.BES.getColWidth());
		
		HTMLPanel inkindQuotePanel = new HTMLPanel(AonStringUtils.EMPTY);
		inkindQuotePanel.addStyleName(AON.CSS.aonDisplayFlexEnd());
		TextBox inkindQuoteBox = new ExpressionBox();
		inkindQuoteBox.setWidth("5rem");
		inkindQuoteBox.addStyleName(AON.CSS.aonTextRight());
		valuesLabels.add(inkindQuoteBox);
		inkindQuotePanel.add(inkindQuoteBox);
		tab.addInlineStyle(inkindQuotePanel, COLUMNS.IES.getCellStyleClass());
		tab.addRow(row, inkindQuotePanel, COLUMNS.IES.getColWidth());
		
		HTMLPanel totalIrpfBasePanel = new HTMLPanel(AonStringUtils.EMPTY);
		totalIrpfBasePanel.addStyleName(AON.CSS.aonDisplayFlexEnd());
		TextBox totalIrpfBaseBox = new ExpressionBox();
		totalIrpfBaseBox.setWidth("5rem");
		totalIrpfBaseBox.addStyleName(AON.CSS.aonTextRight());
		valuesLabels.add(totalIrpfBaseBox);
		totalIrpfBasePanel.add(totalIrpfBaseBox);
		tab.addInlineStyle(totalIrpfBasePanel, COLUMNS.TIR.getCellStyleClass());
		tab.addRow(row, totalIrpfBasePanel, COLUMNS.TIR.getColWidth());
		
		HTMLPanel employeeSSQuoteBasePanel = new HTMLPanel(AonStringUtils.EMPTY);
		employeeSSQuoteBasePanel.addStyleName(AON.CSS.aonDisplayFlexEnd());
		TextBox employeeSSQuoteBaseBox = new ExpressionBox();
		employeeSSQuoteBaseBox.setWidth("5rem");
		employeeSSQuoteBaseBox.addStyleName(AON.CSS.aonTextRight());
		valuesLabels.add(employeeSSQuoteBaseBox);
		employeeSSQuoteBasePanel.add(employeeSSQuoteBaseBox);
		tab.addInlineStyle(employeeSSQuoteBasePanel, COLUMNS.QUO.getCellStyleClass());
		tab.addRow(row, employeeSSQuoteBasePanel, COLUMNS.QUO.getColWidth());
		
		tab.addRow(row, buttonContainer, COLUMNS.BUT.getColWidth());
		
		if(null != employeeIrpf) {
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
				employeeSSQuoteBaseBox.setTitle("Valor obtenido de un L00");
				employeeSSQuoteBaseBox.getElement().getStyle().setColor("green");
			} else if(AonStringUtils.equalsIgnoreCase(employeeIrpf.getSalaryType(), "Manual")) {
				employeeSSQuoteBaseBox.setTitle("Valor obtenido de un M190 (Manual)");
				employeeSSQuoteBaseBox.getElement().getStyle().setColor("blue");
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
	}
	
	private void getAccumulateRow() {
		tab.createFooter();

		tab.addFooter(new Label(AonStringUtils.EMPTY), COLUMNS.MON.getColWidth());
		tab.addFooter(new Label(AonStringUtils.EMPTY), COLUMNS.IRF.getColWidth());
		
		tab.addFooter(new Label(format(getAccumulateMoneyBase())), COLUMNS.BDN.getColWidth(), "text-align: right;");
		tab.addFooter(new Label(format(getAccumulateMoneyQuote())), COLUMNS.IDN.getColWidth(), "text-align: right;");
		tab.addFooter(new Label(format(getAccumulateInkindBase())), COLUMNS.BES.getColWidth(), "text-align: right;");
		tab.addFooter(new Label(format(getAccumulateInkindQuote())), COLUMNS.IES.getColWidth(), "text-align: right;");
		tab.addFooter(new Label(format(getAccumulateTotalIrpf())), COLUMNS.TIR.getColWidth(), "text-align: right;");
		tab.addFooter(new Label(format(getAccumulateEmployeeSSQuote())), COLUMNS.QUO.getColWidth(), "text-align: right;");
		
		tab.addFooter(new Label(AonStringUtils.EMPTY), COLUMNS.BUT.getColWidth());
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
			createUpdateEmployeeIrpf(
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
			saveButton.setEnabled(true);
		} catch (Exception e) {
			// Skip exception
		}
	}
	
	private void createUpdateEmployeeIrpf(Date date, TextBox irpfPercentBox, TextBox moneyBaseBox, Double moneyQuote, TextBox inkindBaseBox, TextBox inkindQuoteBox, TextBox employeeSSQuoteBaseBox, TextBox totalIrpfBaseBox, Integer salaryId) {
		try{
			createUpdateEmployeeIrpf(
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
			saveButton.setEnabled(true);
		} catch (Exception e) {
			// Skip exception
		}
	}
	
	private void createUpdateEmployeeIrpf(Date date, TextBox irpfPercentBox, TextBox moneyBaseBox, TextBox moneyQuoteBox, TextBox inkindBaseBox, Double inkindQuote, TextBox employeeSSQuoteBaseBox, TextBox totalIrpfBaseBox, Integer salaryId) {
		try{
			createUpdateEmployeeIrpf(
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
			saveButton.setEnabled(true);
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
		Integer contractStartYear = DateUtils.getYear(contractStartDate);
		
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
		AonMessagePanel.showLoading(messagePanel, "Guardando Impuesto sobre la Renta de las Personas Fisicas (IRPF) ...");
		setEmployeeIrpf(
				s -> {
					saveButton.setEnabled(false);
					onSearch();
				}, 
				f -> AonMessagePanel.showLoading(messagePanel, "Error guardando " + f.getMessage()));
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

	// ----------------------------------------------- Toolbar.Methods

	public void onSave() {
		changeYear();
	}
	
	// -------------------------------------------------- ContrataEmployee.Methods
	
	public void setYearLB(ListBox yearLB) {
		this.yearLB = yearLB;
	}

	public void onDownloadExcel() {
		String printURL = URL.encode(GWT.getModuleBaseURL() + "ContractIrpfExcel/");
		
		FormPanel formPanel = new FormPanel("_blank");
		formPanel.setAction(printURL);
		formPanel.setMethod(FormPanel.METHOD_GET);
		
		FlowPanel flowPanel = new FlowPanel();
		flowPanel.add(new Hidden("domain", Wnd.getCurrentDomainNameURL()));
		flowPanel.add(new Hidden("login", Wnd.getCurrentUser()));
		flowPanel.add(new Hidden("year", yearLB.getSelectedValue()));
		flowPanel.add(new Hidden("ssNumber", ssNumber));
		flowPanel.add(new Hidden("document", document));
		flowPanel.add(new Hidden("fullName", fullName));
		formPanel.add(flowPanel);
		
		formPanel.addSubmitCompleteHandler(e1 -> removeFromToolbar(formPanel));
		
		addToolbarButton(formPanel);
		
		formPanel.submit();
	}

	public void onUploadExcel() {
		// FORM
		FormPanel msjFIEFormPanel = new FormPanel();
		msjFIEFormPanel.setMethod(FormPanel.METHOD_POST);
		msjFIEFormPanel.setEncoding(FormPanel.ENCODING_MULTIPART);
		msjFIEFormPanel.setAction(URL.encode(GWT.getModuleBaseURL() + "ContractIrpfExcel"));
		
		Hidden userNameHidden = new Hidden(FIEService.Parameter.USER.name(), Wnd.getCurrentUser());
		Hidden domainNameHidden = new Hidden(FIEService.Parameter.DOMAIN.name(), Wnd.getCurrentDomainNameURL());
		
		msjFIEFileUpload = new MultiFileUpload();
		msjFIEFileUpload.setName(FIEService.Parameter.FILE.name());
		msjFIEFileUpload.setVisible(false);
		msjFIEFileUpload.setAccept(".xls");
		msjFIEFileUpload.addChangeHandler(e -> {
			msjFIEFormPanel.submit();
			
		});
		msjFIEFormPanel.addSubmitCompleteHandler(e -> removeFromToolbar(msjFIEFormPanel));
		
		FlowPanel formFlowPanel = new FlowPanel();
		formFlowPanel.add(userNameHidden);
		formFlowPanel.add(domainNameHidden);
		formFlowPanel.add(msjFIEFileUpload);
		
		msjFIEFormPanel.add(formFlowPanel);
		addToolbarButton(msjFIEFormPanel);
		
		msjFIEFileUpload.click();
	}
	
	private void removeFromToolbar(Widget widget) {
		removeToolbarButton(widget);
	}

	public void onExpandClick(ClickEvent event) {
		NativeEvent nativeEvent = event.getNativeEvent();
		excelContextMenu.setPopupPosition(nativeEvent.getClientX(), nativeEvent.getClientY());
		excelContextMenu.show();
	}

	public String getYear() {
		return yearLB.getSelectedValue();
	}
	
	// ----------------------------------------------- DataBase
	
	private void getEmployeeIrpfList(Date date, Consumer<List<EmployeeIrpf>> success, Consumer<Throwable> failure) {
		employeesService.getEmployeeIrpf(ssNumber, document, date, new AsyncCallback<List<EmployeeIrpf>>() {
			
			@Override
			public void onSuccess(List<EmployeeIrpf> employeeIrpfListDB) {
				initEmployeeIrpfList(employeeIrpfListDB);
				success.accept(employeeIrpfListDB);
			}
			
			private void initEmployeeIrpfList(List<EmployeeIrpf> employeeIrpfListDB) {
				employeeIrpfList.clear();
				employeeIrpfList.addAll(employeeIrpfListDB);
				employeeIrpfList.sort((o1, o2) -> o1.getDate().compareTo(o2.getDate()));
			}

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
		});
	}
	
	private List<EmployeeIrpf> getEmployeeIrpf(Date date) {
		DateUtils.resetTime(date);
		List<EmployeeIrpf> result = new ArrayList<>();
		
		for(EmployeeIrpf employeeIrpf : employeeIrpfList)
			if(DateUtils.equals(date, employeeIrpf.getDate()) && !employeeIrpf.isDelete())
				result.add(employeeIrpf);
		
		return result;
	}
	
	private void setEmployeeIrpf(Consumer<Void> success, Consumer<Throwable> failure) {
		employeesService.setEmployeeIrpf(contractId, fullName, document, ssNumber, employeeIrpfList, new AsyncCallback<Void>() {
			
			@Override
			public void onSuccess(Void result) {
				success.accept(result);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
		});
	}
	
	private void createUpdateEmployeeIrpf(Date date, Double moneyBase, Double moneyQuote, Double inkindBase,
			Double inkindQuote, Double irpfPercent, Double employeeSSQuote, Double totalIrpf, Integer salaryId) {
		
		EmployeeIrpf employeeIrpf = getEmployeeIrpf(date, salaryId);
		
		if(null == employeeIrpf) {
			employeeIrpf = new EmployeeIrpf();
			employeeIrpf.setNew(true);
			employeeIrpf.setSalaryType("Manual");
			employeeIrpfList.add(employeeIrpf);
		}
		
		employeeIrpf.setDate(date)
					.setMoneyBase(moneyBase)
					.setMoneyQuote(moneyQuote)
					.setInkindBase(inkindBase)
					.setInkindQuote(inkindQuote)
					.setIrpfPercent(irpfPercent)
					.setEmployeeSSQuote(employeeSSQuote)
					.setTotalIrpf(totalIrpf);
	}

	private EmployeeIrpf getEmployeeIrpf(Date date, Integer salaryId) {
		if(salaryId == null) {
			List<EmployeeIrpf> employeeIrpfListAux = getEmployeeIrpf(date);
			return employeeIrpfListAux.isEmpty() ? null : employeeIrpfListAux.get(0);
		}
		
		for(EmployeeIrpf employeeIrpf : employeeIrpfList)
			if(salaryId == employeeIrpf.getSalaryId() && !employeeIrpf.isDelete())
				return employeeIrpf;
		
		return null;
	}

	private void deleteEmployeeIrpf(EmployeeIrpf employeeIrpf) {
		employeeIrpf.setDelete(true);
	}
	
	private Double getAccumulateMoneyBase() {
		Double accumulate = 0.00;
		
		for(EmployeeIrpf employeeIrpf : employeeIrpfList)
			if(employeeIrpf.getMoneyBase() != null)
				accumulate += employeeIrpf.getMoneyBase();
		
		return accumulate;
	}

	private Double getAccumulateMoneyQuote() {
		Double accumulate = 0.00;
		
		for(EmployeeIrpf employeeIrpf : employeeIrpfList)
			if(employeeIrpf.getMoneyQuote() != null)
				accumulate += employeeIrpf.getMoneyQuote();
		
		return accumulate;
	}

	private Double getAccumulateInkindBase() {
		Double accumulate = 0.00;
		
		for(EmployeeIrpf employeeIrpf : employeeIrpfList)
			if(employeeIrpf.getInkindBase() != null)
				accumulate += employeeIrpf.getInkindBase();
		
		return accumulate;
	}

	private Double getAccumulateInkindQuote() {
		Double accumulate = 0.00;
		
		for(EmployeeIrpf employeeIrpf : employeeIrpfList)
			if(employeeIrpf.getInkindQuote() != null)
				accumulate += employeeIrpf.getInkindQuote();
		
		return accumulate;
	}

	private Double getAccumulateTotalIrpf() {
		Double accumulate = 0.00;
		
		for(EmployeeIrpf employeeIrpf : employeeIrpfList)
			if(employeeIrpf.getTotalIrpf() != null)
				accumulate += employeeIrpf.getTotalIrpf();
		
		return accumulate;
	}

	private Double getAccumulateEmployeeSSQuote() {
		Double accumulate = 0.00;
		
		for(EmployeeIrpf employeeIrpf : employeeIrpfList)
			if(employeeIrpf.getEmployeeSSQuote() != null)
				accumulate += employeeIrpf.getEmployeeSSQuote();
		
		return accumulate;
	}
	
}
