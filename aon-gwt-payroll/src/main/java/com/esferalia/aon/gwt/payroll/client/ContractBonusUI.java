package com.esferalia.aon.gwt.payroll.client;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.payroll.shared.SSBonusData;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ContractBonusUI extends ResizeComposite {

	// -------------------------------------------------- UiBinder

	private static ContractAttachUIBinder uiBinder = GWT.create(ContractAttachUIBinder.class);

	interface ContractAttachUIBinder extends UiBinder<Widget, ContractBonusUI> {}

	// -------------------------------------------------- UiFields

	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String headerLabelStyle();
		String maxWidthTB();
		String maxWidthLB();
	}
	
	@UiField
	DockLayoutPanel dockLayoutPanel;
	
	@UiField (provided = true)
	AonToolbar toolbar;

	@UiField
	VerticalPanel contractBonusTable;

	@UiField
	Grid contractBonusDataTableHeader;
	
	@UiField
	ScrollPanel contractBonusScrollPanel;
	
	@UiField
	DeckPanel deckPanel;
	
	@UiField
	Grid contractBonusDataTable;

	@UiField
	HTMLPanel messagePanel;
	
	// ------------------------------------------------------ Variables

	private DateTimeFormat formatFullDate = DateTimeFormat.getFormat("dd/MM/yyyy");
	private ContractBonusObject contractBonusObject;
	private Map<String, String> t50QUOTA;
	
	// ------------------------------------------------------ Constructor
	
	public ContractBonusUI() {
		createToolbar();
		initWidget(uiBinder.createAndBindUi(this));
		initt50QUOTA();
		initializeView();
		deckPanel.showWidget(0);
	}

	// ------------------------------------------------------ t50QUOTA.Methods
	
	private void initt50QUOTA() {
		t50QUOTA = new HashMap<>();
		t50QUOTA.put("01","Cuota empresarial por AT y EP, Cuotas de recaudación");
		t50QUOTA.put("02","Cuota empresarial por Desempleo");
		t50QUOTA.put("03","Cuota empresarial por Contingencias Comunes");
		t50QUOTA.put("04","Cuota total Desempleo");
		t50QUOTA.put("05","Cuota trabajador por Desempleo");
		t50QUOTA.put("06","Cuota total Desempleo, Formación Profesional y FOGASA");
		t50QUOTA.put("07","Cuota empresarial Contingencias Comunes, Desempleo,");
		t50QUOTA.put("08","Cuota trabajador totalidad");
		t50QUOTA.put("09","Cuota empresarial Contingencias Comunes, excepto IT");
		t50QUOTA.put("10","Cuota total Contingencias Comunes excepto IT,  Desempleo,");
		t50QUOTA.put("11","Protección familiar y FOGASA cuota total");
		t50QUOTA.put("12","Desempleo y FOGASA cuota total");
		t50QUOTA.put("13","FOGASA cuota total");
		t50QUOTA.put("14","Cuota total Contingencias Comunes y Otras Cotizaciones");
		t50QUOTA.put("15","Cuota Total IT Contingencias Comunes, Asistencia Sanitaria,");
		t50QUOTA.put("16","Cuota Total IT Contingencias Comunes, Asistencia Sanitaria,");
		t50QUOTA.put("17","Cuota obrera Contingencias Comunes. Cuota Total AT y EP y");
		t50QUOTA.put("18","Cuota obrera Contingencias Comunes. Cuota Total AT y EP y");
		t50QUOTA.put("19","Cuota Total AT y EP y Otras Cotizaciones");
		t50QUOTA.put("20","Cuota Total Asistencia Sanitaria y Prestación Farmaceútica");
		t50QUOTA.put("21","Cuota Total Contingencias Comunes, Asistencia Sanitaria y");
		t50QUOTA.put("22","Cuota Total Jubilación");
		t50QUOTA.put("23","Cuota Total IT Contingencias Comunes, Maternidad, Protección");
		t50QUOTA.put("24","Cuota Total IT Contingencias Comunes");
		t50QUOTA.put("25","Cuota Total IT, Protección a la Familia, Asistencia Sanitaria,");
		t50QUOTA.put("26","Cuota Total Prestación Farmaceútica");
		t50QUOTA.put("27","Cuota Total IT de AT, Desempleo, FOGASA");
		t50QUOTA.put("28","Cuota Total IT de AT, FOGASA");
		t50QUOTA.put("29","Cuota Total IT, Maternidad, AT y EP, Prestación a la familia,");
		t50QUOTA.put("30","Cuota Total IT, Maternidad, AT y EP, Otras Cotizaciones");
		t50QUOTA.put("31","Cuota Total IT, Incapacidad Permanente, Muerte y");
		t50QUOTA.put("32","Cuota Total Jubilación, IT, Incapacidad Permanente, Muerte");
		t50QUOTA.put("33","Cuota Total Jubilación, IT, Incapacidad Permanente, Muerte y");
		t50QUOTA.put("34","Cuota Total Jubilación, IT, Incapacidad Permanente, Muerte y");
		t50QUOTA.put("35","Cuota Total IT Contingencias Comunes, Prestación a la Familia,");
		t50QUOTA.put("36","Cuota Total Prestación a la Familia, Desempleo");
		t50QUOTA.put("37","Cuota Total Jubilación, Incapacidad Permanente Contingencias");
		t50QUOTA.put("38","Cuota Total IT, AT y EP");
		t50QUOTA.put("39","Cuota Total IT");
		t50QUOTA.put("40","Cuota Total Contingencias Comunes, Desempleo");
		t50QUOTA.put("41","Cuota Total AT y EP");
		t50QUOTA.put("42","Sistema Especial del Tomate Fresco");
		t50QUOTA.put("43","Cuota Total Contingencias Comunes");
		t50QUOTA.put("44","Cuota Total IT Contingencias Comunes, Desempleo y FOGASA");
		t50QUOTA.put("45","Cuota Total IT Contingencias Comunes y FOGASA");
		t50QUOTA.put("46","Contingencias comunes IT");
		t50QUOTA.put("47","Contingencias Comunes/Base mínima RETA");
		t50QUOTA.put("48","Contingencias Comunes obligatoria-IT/REA");
		t50QUOTA.put("49","Aportación empresarial CC/Hogar");
		t50QUOTA.put("50","Contingencias Comunes -IT/Cuenta Propia");
		t50QUOTA.put("51","Cuota Empresarial - Horas extras");
		t50QUOTA.put("52","Contingencias obligatorias/Base mínima");
		t50QUOTA.put("53","P.F. Desempleo, FOGASA, Formación Profesional - Cuota total");
		t50QUOTA.put("54","Contingencias comunes -Cuota trabajador");
		t50QUOTA.put("55","Contingencias comunes - Cuota obrera, AT y EP, OC - Cuota");
		t50QUOTA.put("56","Contingencias comunes - Cuota empresarial diferencias base");
		t50QUOTA.put("57","Cuota total");
		t50QUOTA.put("58","Contingencia común - Cuota empresarial ERE");
		t50QUOTA.put("59","Contrato Formación - Sin exclusiones");
		t50QUOTA.put("60","AS, PF, IT, MA, DE, FGS y FP  - Cuota total");
		t50QUOTA.put("61","Cuota empresarial - Jubilación, Incapacidad permanente,");
		t50QUOTA.put("62","FOGASA, Formación Profesional - Cuota Total");
		t50QUOTA.put("63","S.E.A. - Prestaciones de corta duración");
		t50QUOTA.put("64","Contingencias comunes, Incapacidad Temporal, Otras");
		t50QUOTA.put("65","FOGASA - Cese Actividad - Obligación");
		t50QUOTA.put("68","Contingencias Comunes y Profesionales - Cuota Total");
		t50QUOTA.put("69","FOGASA/Cese actividad");
		t50QUOTA.put("70","C.C. Cuota Empresarial S./ Horas complementarias");
		t50QUOTA.put("71","Contingencias Comunes -IT/Base mínima RET");
		t50QUOTA.put("72","Cuota Empresarial maternidad/paternidad tiempo parcial");
		t50QUOTA.put("73","C.EMP.C.C. T.Plana 3A-MAT/PAT T.PARC.");
		t50QUOTA.put("74","JUB, IPCC, MSCC, D.-Cuota total");
		t50QUOTA.put("75","Cuota total sin horas extras");
		t50QUOTA.put("76","Tipo cotización IT AT");
		t50QUOTA.put("77","Tipo cotización ISM AT");
		t50QUOTA.put("78","Formación profesional. Cuota total");
		t50QUOTA.put("79","Desempleo y Formación profesional. Cuota total");
		t50QUOTA.put("80","Decremento BBCC sobre tiempo completo");
		t50QUOTA.put("81","Contingencias comunes y profesionales - BBCC media 12");
	}
	
	// ------------------------------------------------------ initializeView
	
	private void initializeView() {
		initAttachmentsTable();
		paintHeaderContractBonusTable();
		
		setScrollPanelsHeight();
		setColumnsWidth();
	}
	
	private void initAttachmentsTable() {
		contractBonusDataTableHeader.clear();
		contractBonusDataTableHeader.resize(0, 0);
		contractBonusDataTableHeader.resizeColumns(3);
		contractBonusDataTable.clear();
		contractBonusDataTable.resize(0, 0);
		contractBonusDataTable.resizeColumns(3);
	}
	
	private void paintHeaderContractBonusTable() {
		int row = contractBonusDataTableHeader.insertRow(contractBonusDataTableHeader.getRowCount());
		
		Label startDate = new Label("FECHA INICIO");
		Label endDate = new Label("FECHA FIN");
		Label description = new Label("DESCRIPCI\u00D3N");
		
		description.addStyleName(style.headerLabelStyle());
		startDate.addStyleName(style.headerLabelStyle());
		endDate.addStyleName(style.headerLabelStyle());
		startDate.getElement().getStyle().setPaddingLeft(10, Unit.PX);
		
		contractBonusDataTableHeader.setWidget(row, 0, startDate);
		contractBonusDataTableHeader.setWidget(row, 1, endDate);
		contractBonusDataTableHeader.setWidget(row, 2, description);
	}
	
	private void setScrollPanelsHeight() {
		contractBonusScrollPanel.setHeight((Window.getClientHeight() - 390) + "px");
	}
	
	private void setColumnsWidth() {
		contractBonusDataTableHeader.getCellFormatter().getElement(0, 0).getStyle().setWidth(150, Unit.PX);
		contractBonusDataTableHeader.getCellFormatter().getElement(0, 1).getStyle().setWidth(150, Unit.PX);
		contractBonusDataTableHeader.getCellFormatter().getElement(0, 2).getStyle().setWidth(650, Unit.PX);
		
		contractBonusDataTable.getColumnFormatter().getElement(0).getStyle().setWidth(150, Unit.PX);
		contractBonusDataTable.getColumnFormatter().getElement(1).getStyle().setWidth(150, Unit.PX);
		contractBonusDataTable.getColumnFormatter().getElement(2).getStyle().setWidth(650, Unit.PX);
	}
	
	// ------------------------------------------------------ setEmployeeContractInfo
	
	public void setContractBonusObject(ContractBonusObject contractBonusObject) {
		this.contractBonusObject = contractBonusObject;
		this.contractBonusObject.getSSBonus(
			s -> loadView(), 
			f -> showError("Bonificaciones", f.getMessage())
		);
	}
	
	private void loadView() {
		resetAttachDataTableStructure();
		
		if(!this.contractBonusObject.getSSBonusList().isEmpty())
			deckPanel.showWidget(1);
		
		for(SSBonusData contractBonus : this.contractBonusObject.getSSBonusList())
			paintContractBonus(contractBonus);
	}
	
	// ------------------------------------------------------ setEmployeeContractInfo.Methods
	
	private void resetAttachDataTableStructure() {
		contractBonusDataTable.clear();
		contractBonusDataTable.resize(0, 0);
		contractBonusDataTable.resizeColumns(3);
		
		setColumnsWidth();
	}
	
	private void paintContractBonus(SSBonusData ssBonusData) {
		// Insert new row
		int row = this.contractBonusDataTable.insertRow(contractBonusDataTable.getRowCount());
		
		// Description TextBox
		Label startDateLabel = new Label(formatFullDate.format(ssBonusData.getStartDate()));
		startDateLabel.getElement().getStyle().setPaddingLeft(10, Unit.PX);
		
		// Description TextBox
		Label endDateLabel = new Label(null == ssBonusData.getEndDate() ? "" : formatFullDate.format(ssBonusData.getEndDate()));
		
		// Description TextBox
		Label descriptionLabel = new Label(getFullDescription(ssBonusData));
		
		//Add to table
		contractBonusDataTable.setWidget(row, 0, startDateLabel);
		contractBonusDataTable.setWidget(row, 1, endDateLabel);
		contractBonusDataTable.setWidget(row, 2, descriptionLabel);
	}	
	
	private String getFullDescription(SSBonusData bonus) {
		return Arrays.stream(new String [] {bonus.getDescription(), getQuota(bonus).toUpperCase() }).collect(Collectors.joining(". "));
	}
	
	private String getQuota( SSBonusData bonus )  {
		MatchResult matchResult = 
		RegExp.compile("quota:([0-9]+)")
		.exec(bonus.getFormula());
		return t50QUOTA.getOrDefault(matchResult.getGroup(1), "");
	}
	
	// -------------------------------------------------- Toolbar
	
	private void createToolbar() {
		toolbar = new AonToolbar("Bonificaciones");
		
		AonToolbarButton checkBonus = new AonToolbarButton("Actualizar Bonificaciones", AON.CSS.aonIconTgss());
		checkBonus.addClickHandler(e -> checkBonus());
		toolbar.add(checkBonus);
	}

	private void checkBonus() {
		showLoading("Obteniendo bonificaciones del contrato");
		this.contractBonusObject.syncSSBonus(
			s -> {
				showSuccess("Bonificaciones", "Bonificaciones actualizadas a " + formatFullDate.format(new Date()));
				loadView();
			},
			f -> showError("Obtenci\u00F3n Bonificaciones", f.getMessage())
		);
	}
	
	// -------------------------------------------------- MessagesPanel
	
	private void showSuccess(String title, String message) {
		Map<String, String> successMap = new HashMap<>();
		successMap.put(title, message);
		AonMessagePanel.showSuccess(messagePanel, successMap);
	}
	
	private void showError(String title, String message) {
		Map<String, String> errorMap = new HashMap<>();
		errorMap.put(title, message);
		AonMessagePanel.showError(messagePanel, errorMap);
	}
	
	private void showLoading(String message) {
		AonMessagePanel.showLoading(messagePanel, message);
	}
	
}
