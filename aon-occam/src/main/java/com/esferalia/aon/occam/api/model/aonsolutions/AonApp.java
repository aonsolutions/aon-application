package com.esferalia.aon.occam.api.model.aonsolutions;

import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.occam.api.model.Module;
import com.esferalia.aon.watson.util.AonStringUtils;

public enum AonApp implements Serializable{
	
	INVOICE(getEmptyModules(), "Facturas"),
	DOCUMENTAL(getDocumentalModules(), "Documental"),
	MESSENGER(getMessengerModules(), "Mensajería"),
	ACCOUNTING(getAccountingModules(), "Contabilidad"),
	FISCAL(getFiscalModules(), "Fiscal"),
	PAYROLL(getPayrollModules(), "Laboral"),
	OCR(getEmptyModules(), "OCR"),
	AIO(getEmptyModules(), "AIO"),
	ALMA(getEmptyModules(), "Alma"),
	COMUNICA(getEmptyModules(), "Comunica"),
	BIDOQ(getEmptyModules(), "Bidoq"),
	CONVENIOS(getEmptyModules(), "Convenios"),
	BANK(getEmptyModules(), "Bancos"),
	TIMECONTROL(getEmptyModules(), "Control Horario"),
	MANAGEMENT(getManagementModules(), "Gestión"),
	PACK_SUITE(getPackSuiteModules(), "Suite Completa"),
	PACK_PORTAL(getPackPortalModules(), "Pack Portal"),
	PACK_PAYROLL(getPayrollModules(), "Pack Cotización"),
	PACK_FISCAL_ACCOUNTING(getPackFiscalAccountingModules(), "Pack Tributación"),
	SELFCONTA(getEmptyModules(), "Selfconta"),
	CUSTOM_VIEW(getEmptyModules(), "Vista Personalizada"),
	AULA(getEmptyModules(), "Aula"),
	NOTES(getEmptyModules(), "Notas"),
	SALTRA(getEmptyModules(), "Saltra"),
	BASIC_MANAGEMENT(getBasicManagementModules(), "Gestión Básica"),
	STANDAR_MANAGEMENT(getStandarManagementModules(), "Gestión Estándar"),
	PROFESSIONAL_MANAGEMENT(getProfessionalManagementModules(), "Gestión Profesional"),
	KIT_DIGITAL_FACE(getBasicManagementModules(), "Kit Digital FACe"),
	KIT_DIGITAL_CRM(getKitDigitalCrmModules(), "Kit Digital CRM"),
	KIT_DIGITAL_ERP(getKitDigitalErpModules(), "Kit Digital ERP"),
	API_SERVICE(getEmptyModules(), "Servicio API"),
	WAREHOUSE(getWarehouseModules(), "Almacén"),
	COMMERCIAL(getCommercialModules(), "Comercial"),
	MARKETING(getMarketingModules(), "Marketing"),
	TREASURY(getTreasuryModules(), "Tesorería"),
	GROUPWARE(getGroupwareModules(), "Expedientes")
	;
	
	LinkedList<Module> modules;
	String description;
	
	private AonApp(LinkedList<Module> modules, String description) {
	    
		this.modules = modules;
		this.description = description;
	}
	
	public List<Module> getModules() {
		return modules;
	}
	
	public String getDescription() {
		return description;
	}
	
	public Byte value(){
		return (byte) ordinal();
	}
	
	public static AonApp safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	
	public static AonApp safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= AonApp.values().length) return null;
		return AonApp.values()[i];
	}
	
	public static AonApp safeValueOf( String i ) {
		if(AonStringUtils.isBlank(i)) return null;
		for (AonApp rs : values()) {
			if(i.equalsIgnoreCase(rs.name()))
				return rs;
		}
		return null;
	}
	
	public static List<AonApp> getValues() {
		return Arrays.asList(values());
	}
	
	private static LinkedList<Module> getEmptyModules() {
		return new LinkedList<>();
	}
	
	private static LinkedList<Module> getBasicManagementModules() {
		LinkedList<Module> list = new LinkedList<>();
		list.add(Module.AON_FINANCE);
		list.add(Module.MANAGEMENT);
		list.add(Module.CRM);
		return list;
	}
	
	private static LinkedList<Module> getStandarManagementModules() {
		LinkedList<Module> list = getBasicManagementModules();
		list.add(Module.TREASURY);
		list.add(Module.MARKETING);
		return list;
	}
	
	private static LinkedList<Module> getProfessionalManagementModules() {
		LinkedList<Module> list = getStandarManagementModules();
		list.add(Module.WAREHOUSE);
		list.add(Module.GROUPWARE);
		return list;
	}
	
	private static LinkedList<Module> getKitDigitalCrmModules() {
		LinkedList<Module> list = new LinkedList<>();
		list.add(Module.CRM);
		list.add(Module.MARKETING);
		return list;
	}
	
	private static LinkedList<Module> getKitDigitalErpModules() {
		LinkedList<Module> list = new LinkedList<>();
		list.add(Module.MANAGEMENT);
		list.add(Module.TREASURY);
		return list;
	}
	
	private static LinkedList<Module> getDocumentalModules() {
		LinkedList<Module> list = new LinkedList<>();
		list.add(Module.DOCUMENT);
		return list;
	}
	
	private static LinkedList<Module> getMessengerModules() {
		LinkedList<Module> list = new LinkedList<>();
		list.add(Module.CALL_CENTER);
		return list;
	}
	
	private static LinkedList<Module> getWarehouseModules() {
		LinkedList<Module> list = new LinkedList<>();
		list.add(Module.WAREHOUSE);
		return list;
	}
	
	private static LinkedList<Module> getAccountingModules() {
		LinkedList<Module> list = new LinkedList<>();
		list.add(Module.ACCOUNTING);
		return list;
	}
	
	private static LinkedList<Module> getFiscalModules() {
		LinkedList<Module> list = new LinkedList<>();
		list.add(Module.FISCAL);
		return list;
	}
	
	private static LinkedList<Module> getPayrollModules() {
		LinkedList<Module> list = new LinkedList<>();
		list.add(Module.PAYROLL);
		return list;
	}
	
	private static LinkedList<Module> getManagementModules() {
		LinkedList<Module> list = new LinkedList<>();
		list.add(Module.MANAGEMENT);
		return list;
	}
	
	private static LinkedList<Module> getPackSuiteModules() {
		LinkedList<Module> list =new LinkedList<>();
		list.add(Module.DOCUMENT);
		list.add(Module.ACCOUNTING);
		list.add(Module.FISCAL);
		list.add(Module.PAYROLL);
		list.add(Module.CALL_CENTER);
		return list;
	}
	
	private static LinkedList<Module> getPackPortalModules() {
		LinkedList<Module> list =new LinkedList<>();
		list.add(Module.DOCUMENT);
		list.add(Module.CALL_CENTER);
		return list;
	}
	
	private static LinkedList<Module> getPackFiscalAccountingModules() {
		LinkedList<Module> list = new LinkedList<>();
		list.add(Module.FISCAL);
		list.add(Module.ACCOUNTING);
		return list;
	}
	
	private static LinkedList<Module> getCommercialModules() {
		LinkedList<Module> list = new LinkedList<>();
		list.add(Module.CRM);
		return list;
	}
	
	private static LinkedList<Module> getMarketingModules() {
		LinkedList<Module> list = new LinkedList<>();
		list.add(Module.MARKETING);
		return list;
	}
	
	private static LinkedList<Module> getTreasuryModules() {
		LinkedList<Module> list = new LinkedList<>();
		list.add(Module.TREASURY);
		return list;
	}
	
	private static LinkedList<Module> getGroupwareModules() {
		LinkedList<Module> list = new LinkedList<>();
		list.add(Module.GROUPWARE);
		return list;
	}

}
