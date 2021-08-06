package com.esferalia.aon.gwt.payroll.client;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.payroll.shared.EmployeeContractInfo;
import com.esferalia.aon.gwt.payroll.shared.Messages;
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
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public abstract class ContractBonusUI extends ResizeComposite {

	private static final Map<String, String> T50_QUOTA = new HashMap<String, String>(){
		{
			put("01","Cuota empresarial por AT y EP, Cuotas de recaudación");
			put("02","Cuota empresarial por Desempleo");
			put("03","Cuota empresarial por Contingencias Comunes");
			put("04","Cuota total Desempleo");
			put("05","Cuota trabajador por Desempleo");
			put("06","Cuota total Desempleo, Formación Profesional y FOGASA");
			put("07","Cuota empresarial Contingencias Comunes, Desempleo,");
			put("08","Cuota trabajador totalidad");
			put("09","Cuota empresarial Contingencias Comunes, excepto IT");
			put("10","Cuota total Contingencias Comunes excepto IT,  Desempleo,");
			put("11","Protección familiar y FOGASA cuota total");
			put("12","Desempleo y FOGASA cuota total");
			put("13","FOGASA cuota total");
			put("14","Cuota total Contingencias Comunes y Otras Cotizaciones");
			put("15","Cuota Total IT Contingencias Comunes, Asistencia Sanitaria,");
			put("16","Cuota Total IT Contingencias Comunes, Asistencia Sanitaria,");
			put("17","Cuota obrera Contingencias Comunes. Cuota Total AT y EP y");
			put("18","Cuota obrera Contingencias Comunes. Cuota Total AT y EP y");
			put("19","Cuota Total AT y EP y Otras Cotizaciones");
			put("20","Cuota Total Asistencia Sanitaria y Prestación Farmaceútica");
			put("21","Cuota Total Contingencias Comunes, Asistencia Sanitaria y");
			put("22","Cuota Total Jubilación");
			put("23","Cuota Total IT Contingencias Comunes, Maternidad, Protección");
			put("24","Cuota Total IT Contingencias Comunes");
			put("25","Cuota Total IT, Protección a la Familia, Asistencia Sanitaria,");
			put("26","Cuota Total Prestación Farmaceútica");
			put("27","Cuota Total IT de AT, Desempleo, FOGASA");
			put("28","Cuota Total IT de AT, FOGASA");
			put("29","Cuota Total IT, Maternidad, AT y EP, Prestación a la familia,");
			put("30","Cuota Total IT, Maternidad, AT y EP, Otras Cotizaciones");
			put("31","Cuota Total IT, Incapacidad Permanente, Muerte y");
			put("32","Cuota Total Jubilación, IT, Incapacidad Permanente, Muerte y");
			put("33","Cuota Total Jubilación, IT, Incapacidad Permanente, Muerte y");
			put("34","Cuota Total Jubilación, IT, Incapacidad Permanente, Muerte y");
			put("35","Cuota Total IT Contingencias Comunes, Prestación a la Familia,");
			put("36","Cuota Total Prestación a la Familia, Desempleo");
			put("37","Cuota Total Jubilación, Incapacidad Permanente Contingencias");
			put("38","Cuota Total IT, AT y EP");
			put("39","Cuota Total IT");
			put("40","Cuota Total Contingencias Comunes, Desempleo");
			put("41","Cuota Total AT y EP");
			put("42","Sistema Especial del Tomate Fresco");
			put("43","Cuota Total Contingencias Comunes");
			put("44","Cuota Total IT Contingencias Comunes, Desempleo y FOGASA");
			put("45","Cuota Total IT Contingencias Comunes y FOGASA");
			put("46","Contingencias comunes IT");
			put("47","Contingencias Comunes/Base mínima RETA");
			put("48","Contingencias Comunes obligatoria-IT/REA");
			put("49","Aportación empresarial CC/Hogar");
			put("50","Contingencias Comunes -IT/Cuenta Propia");
			put("51","Cuota Empresarial - Horas extras");
			put("52","Contingencias obligatorias/Base mínima");
			put("53","P.F. Desempleo, FOGASA, Formación Profesional - Cuota total");
			put("54","Contingencias comunes -Cuota trabajador");
			put("55","Contingencias comunes - Cuota obrera, AT y EP, OC - Cuota");
			put("56","Contingencias comunes - Cuota empresarial diferencias base");
			put("57","Cuota total");
			put("58","Contingencia común - Cuota empresarial ERE");
			put("59","Contrato Formación - Sin exclusiones");
			put("60","AS, PF, IT, MA, DE, FGS y FP  - Cuota total");
			put("61","Cuota empresarial - Jubilación, Incapacidad permanente,");
			put("62","FOGASA, Formación Profesional - Cuota Total");
			put("63","S.E.A. - Prestaciones de corta duración");
			put("64","Contingencias comunes, Incapacidad Temporal, Otras");
			put("65","FOGASA - Cese Actividad - Obligación");
			put("68","Contingencias Comunes y Profesionales - Cuota Total");
			put("69","FOGASA/Cese actividad");
			put("70","C.C. Cuota Empresarial S./ Horas complementarias");
			put("71","Contingencias Comunes -IT/Base mínima RET");
			put("72","Cuota Empresarial maternidad/paternidad tiempo parcial");
			put("73","C.EMP.C.C. T.Plana 3A-MAT/PAT T.PARC.");
			put("74","JUB, IPCC, MSCC, D.-Cuota total");
			put("75","Cuota total sin horas extras");
			put("76","Tipo cotización IT AT");
			put("77","Tipo cotización ISM AT");
			put("78","Formación profesional. Cuota total");
			put("79","Desempleo y Formación profesional. Cuota total");
			put("80","Decremento BBCC sobre tiempo completo");
			put("81","Contingencias comunes y profesionales - BBCC media 12");
		}
	};

	private static String getQuota( SSBonusData bonus )  {
		MatchResult matchResult = 
		RegExp.compile("quota:([0-9]+)")
		.exec(bonus.getFormula());
		return T50_QUOTA.getOrDefault(matchResult.getGroup(1), "");
	}

	private static String getFullDescription(SSBonusData bonus) {
		String description = Arrays.stream(new String [] {bonus.getDescription(), getQuota(bonus).toUpperCase() }).collect(Collectors.joining(". "));
		return description;
	}


	// -------------------------------------------------- UiBinder --------------------------------------------------

	private static ContractAttachUIBinder uiBinder = GWT.create(ContractAttachUIBinder.class);

	interface ContractAttachUIBinder extends UiBinder<Widget, ContractBonusUI> {}

	// -------------------------------------------------- UiFields --------------------------------------------------

	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String headerLabelStyle();
		String maxWidthTB();
		String maxWidthLB();
		String clauseTD();
	}

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
	
	// ------------------------------------------------------ Constructor ---------------------------------------------------------

	private DateTimeFormat formatFullDate = DateTimeFormat.getFormat("dd/MM/yyyy");
	private EmployeeContractInfo employeeContractInfo;
	private Messages messages;
	
	public ContractBonusUI() {
		initWidget(uiBinder.createAndBindUi(this));
		messages = new Messages();
		initializeView();
		deckPanel.showWidget(0);
	}
	
	public void setEmployeeContractInfo(EmployeeContractInfo employeeContractInfoIn) {
		this.employeeContractInfo = employeeContractInfoIn;
		resetAttachDataTableStructure();
		
		if(employeeContractInfo.getContractBonus().size() > 0)
			deckPanel.showWidget(1);
		
		for(SSBonusData contractBonus : employeeContractInfo.getContractBonus())
			paintContractBonus(contractBonus);
		
		// Footer
		messagePanel.clear();
		AonTableButton infoBtn = new AonTableButton("Actualizado", AON.CSS.aonIconInfo());
		Label messageLabel = new Label("Bonificaciones actualizadas a " + formatFullDate.format(new Date()));
		messagePanel.add(infoBtn);
		messagePanel.add(messageLabel);
	}
	
	// --------------------------------------------------- UiHandlers (Aux Methods) -------------------------------------------------
	
	private void resetAttachDataTableStructure() {
		contractBonusDataTable.clear();
		contractBonusDataTable.resize(0, 0);
		contractBonusDataTable.resizeColumns(3);
		
		setColumnsWidth();
	}
	
	// -------------------------------------------------- Paint Table Header Methods --------------------------------------------------
	
	private void paintHeaderContractBonusTable() {
		int row = contractBonusDataTableHeader.insertRow(contractBonusDataTableHeader.getRowCount());
		
		Label startDate = new Label("FECHA INICIO");
		Label endDate = new Label("FECHA FIN");
		Label description = new Label("DESCRIPCI" + String.valueOf("\u00D3") + "N");
		
		description.addStyleName(style.headerLabelStyle());
		startDate.addStyleName(style.headerLabelStyle());
		endDate.addStyleName(style.headerLabelStyle());
		startDate.getElement().getStyle().setPaddingLeft(10, Unit.PX);
		
		contractBonusDataTableHeader.setWidget(row, 0, startDate);
		contractBonusDataTableHeader.setWidget(row, 1, endDate);
		contractBonusDataTableHeader.setWidget(row, 2, description);
	}
	
	// ------------------------------------------------------ Paint Table Methods -----------------------------------------------------

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
	
	// -------------------------------------------------- Paint Table Auxiliar Methods ---------------------------------------------------

	protected abstract void fireMessagesResults(Messages messages);
	
	// ------------------------------------------------------ Auxiliar Methods ----------------------------------------------------
	
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

}
