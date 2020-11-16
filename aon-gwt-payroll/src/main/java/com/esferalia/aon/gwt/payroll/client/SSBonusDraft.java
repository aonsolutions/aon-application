package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.esferalia.aon.gwt.common.client.widget.CustomDialog;
import com.esferalia.aon.gwt.payroll.shared.SSBonusData;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class SSBonusDraft extends CustomDialog {
	
	
	
	// -------------------------------------------------- UiBinder --------------------------------------------------

	//Starting Service
	final DomainEnterprisesServiceAsync impl = DomainEnterprisesServiceAsync.newInstance();
	
	interface Binder extends UiBinder<Widget, SSBonusDraft> {}

	private static final Binder binder = GWT.create(Binder.class);
	
	// -------------------------------------------------- UiFields --------------------------------------------------
			
	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String hide();
		String buttonTable();
		String bold();
		String formulaStyle();
		String descriptionStyle();
		String dateStyle();
		String selected();
		String selectedBG();
		String unSelected();
	}
	
	//ELEMENTOS HTML
	
	
	//@UiField
	//HTMLPanel listBonusesPanel;
	
	@UiField
	Grid listBonusesTable;

	//BUTTONS ACCEPT AND CANCEL
	
	@UiField
	Button acceptButton;
	
	@UiField
	Button cancelButton;
	
	@UiField
	FormPanel idcFormPanel;
	
	@UiField
	FileUpload idcFileUpload;
	
	@UiField
	Hidden idcUserNameHidden;
	
	@UiField
	Hidden idcDomainNameHidden;
// ------------------------------------------------------------ VARIABLES DE LA CLASE ----------------------------------------------------
		
//	private SSBonusDraftObject ssBonusDraftObject;
	private Integer contractId;
	private List<SSBonusData> ssBonuses;
	
	private static final DateTimeFormat DATE_FORMAT = DateTimeFormat.getFormat("dd/MM/yyyy");

	
// ---------------------------------------------------------------- CONSTRUCTOR ----------------------------------------------------------
	
	public SSBonusDraft(Integer contractId) {
		
		setCaption("Bonificaciones...");
		
		setWidget(binder.createAndBindUi(this));
				
		
		this.ssBonuses = new ArrayList<>();
		this.contractId = contractId;

		impl.getEmployeeSSBonuses(this.contractId, new AsyncCallback<List<SSBonusData>>() {
			
			@Override
			public void onSuccess(List<SSBonusData> result) {
				ssBonuses = result;
				showTable();
			}

			@Override
			public void onFailure(Throwable caught) {
				
			}
		});
		
		this.idcUserNameHidden.setValue(Wnd.getCurrentUser());
		this.idcDomainNameHidden.setValue(Wnd.getCurrentDomainNameURL());

	}
	
	private void showTable() {
		cleanSelected();
		showListBonuses();
	}
	
	
// ----------------------------------------------------------------- UiHandlers ----------------------------------------------------------
	
	@UiHandler("idcButton")
	void onClickIdcButton(ClickEvent e ) {
		idcFileUpload.click();
	}

	@UiHandler("idcFormPanel") 
	void onIdcSubmitComplete(SubmitCompleteEvent e) {
	}

	private void cleanSelected() {
		//Set Unselected
		int rows = this.listBonusesTable.getRowCount();
		for(int itRow = 0; itRow < rows; itRow++){
			this.listBonusesTable.getRowFormatter().removeStyleName(itRow, style.selected());
		}
	}

	@UiHandler("cancelButton")
	void onCancelButonClick(ClickEvent e ) {
		hide();
	}

	@UiHandler("acceptButton")
	void onAcceptButonClick(ClickEvent e ) {
		
	}

	private void clearListBonuses() {
		this.listBonusesTable.clear();
	}
	

	private void showListBonuses() {
		clearListBonuses();
		
		this.listBonusesTable.resize(getBonuses().size() + 1, this.listBonusesTable.getColumnCount());
		
		//Cabecera
		this.listBonusesTable.setWidget(0, 0, new Label("Id"));
		this.listBonusesTable.getWidget(0, 0).addStyleName(style.hide());
		this.listBonusesTable.setWidget(0, 1, new Label(""));
		this.listBonusesTable.setWidget(0, 2, new Label("Fecha Incio"));
		this.listBonusesTable.getWidget(0, 2).addStyleName(style.dateStyle());
		this.listBonusesTable.setWidget(0, 3, new Label("Fecha Fin"));
		this.listBonusesTable.getWidget(0, 3).addStyleName(style.dateStyle());
		this.listBonusesTable.setWidget(0, 4, new Label("Descripci"+String.valueOf("\u00F3")+"n"));
		this.listBonusesTable.getWidget(0, 4).addStyleName(style.descriptionStyle());
		this.listBonusesTable.setWidget(0, 5, new Label(""));
		
		for(int i = 0; i < this.listBonusesTable.getColumnCount(); i++)
			this.listBonusesTable.getWidget(0, i).addStyleName(style.bold());
		
		int row = 1;
		
		for(SSBonusData bonus : getBonuses()){
			listBonusesTable.setWidget(row, 0, new Label(bonus.getId().toString()));
			listBonusesTable.getWidget(row, 0).addStyleName(style.hide());
			Label system = new Label();
			if(bonus.isSystem())
				system.setStyleName("aon-icon-rowSelector-S aon-editDataTable-button");
			else
				system.setStyleName("aon-icon-rowSelector aon-editDataTable-button");
			listBonusesTable.setWidget(row, 1, system);
			listBonusesTable.setWidget(row, 2, new Label((null == bonus.getStartDate()) ? "" : DATE_FORMAT.format(bonus.getStartDate())));
			listBonusesTable.setWidget(row, 3, new Label((null == bonus.getEndDate()) ? "" : DATE_FORMAT.format(bonus.getEndDate())));
			String description = getFullDescription(bonus);
			listBonusesTable.setWidget(row, 4, new Label( description ));
			
						
			row++;
		}
		cleanSelected();
	}

	

	
	// ---------------------------------------------------------------------------------------------------------------------------------------------
	// 														PRIVATE METHODS
	// ---------------------------------------------------------------------------------------------------------------------------------------------
	
	public List<SSBonusData> getBonuses(){
		return this.ssBonuses;
	}
	
	public SSBonusData getBonus(Integer id){
		for(SSBonusData bonus : this.ssBonuses){
			if(bonus.getId() == id)
				return bonus;
		}
		return null;
	}
	
	public void newBonus(Integer id, Date startDate, Date endDate, String description, Byte type, String expression){
//		Window.alert("ID : " + id + ", startDate : " + startDate + ", endDate : " + endDate + 
//					", description : " + description + ", type : " + type + ", expression : " + expression);
		SSBonusData newBonus = new SSBonusData(id, false, startDate, endDate, description, type, expression);
		this.ssBonuses.add(newBonus);
	}
	
	public void modifyBonus(Integer id, Date startDate, Date endDate, String description, Byte type, String expression) {
//		Window.alert("ID : " + id + ", startDate : " + startDate + ", endDate : " + endDate + 
//				", description : " + description + ", type : " + type + ", expression : " + expression);
		for(SSBonusData bonus : ssBonuses){
			if(id == bonus.getId()){
				bonus.setStartDate(startDate);
				bonus.setEndDate(endDate);
				bonus.setDescription(description);
				bonus.setType(type);
				bonus.setFormula(expression);
			}
		}
	}
	
	public void deleteBonus(Integer id_bonus) {
		SSBonusData bonus = getBonus(id_bonus);
		if(null != bonus)
			ssBonuses.remove(bonus);
	}

	public int getLastBonusesId() {
		int index = -1;
		for(SSBonusData bonus : ssBonuses){
			if(index < bonus.getId())
				index = bonus.getId();
		}
		return index;
	}
	
	public static String getQuota( SSBonusData bonus )  {
		MatchResult matchResult = 
		RegExp.compile("quota:([0-9]+)")
		.exec(bonus.getFormula());
		return T50_QUOTA.getOrDefault(matchResult.getGroup(1), "");
	}

	protected static String getFullDescription(SSBonusData bonus) {
		String description = Arrays.stream(new String [] {bonus.getDescription(), getQuota(bonus).toUpperCase() }).collect(Collectors.joining(". "));
		return description;
	}

	public static final Map<String, String> T50_QUOTA = new HashMap<String, String>(){
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

	
}
