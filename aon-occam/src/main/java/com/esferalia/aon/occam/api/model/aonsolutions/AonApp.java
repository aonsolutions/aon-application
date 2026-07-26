package com.esferalia.aon.occam.api.model.aonsolutions;

import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.occam.api.model.Module;
import com.esferalia.aon.watson.util.AonStringUtils;

public enum AonApp implements Serializable {
	/**
	 * FACTURA Y GESTI�N - INVOICE & MANAGEMENT
	 */
	INVOICE(getEmptyModules(), "Facturas"), 											// 0
	DOCUMENTAL(getDocumentalModules(), "Documental"),									// 1
	MESSENGER(getMessengerModules(), "Mensajer\u00eda"),								// 2
	ACCOUNTING(getAccountingModules(), "Contabilidad"),									// 3
	FISCAL(getFiscalModules(), "Fiscal"),												// 4
	PAYROLL(getPayrollModules(), "Laboral"),											// 5
	OCR(getEmptyModules(), "OCR"),														// 6
	AIO(getEmptyModules(), "AIO"),														// 7
	ALMA(getEmptyModules(), "Alma"),													// 8
	COMUNICA(getEmptyModules(), "Comunica"),											// 9
	@Deprecated
	BIDOQ(getEmptyModules(), "Bidoq"),													// 10
	CONVENIOS(getEmptyModules(), "Convenios"),											// 11
	BANK(getEmptyModules(), "Bancos"),													// 12
	TIMECONTROL(getEmptyModules(), "Control Horario"),									// 13
	MANAGEMENT(getManagementModules(), "Gesti\u00f3n"),									// 14
	PACK_SUITE(getEmptyModules(), "Suite Completa"),									// 15
	PACK_PORTAL(getEmptyModules(), "Pack Portal"),										// 16
	PACK_PAYROLL(getEmptyModules(), "Pack Cotizaci\u00f3n"),							// 17
	PACK_FISCAL_ACCOUNTING(getEmptyModules(), "Pack Tributaci\u00f3n"),					// 18
	@Deprecated 
	SELFCONTA(getEmptyModules(), "Selfconta"),											// 19
	CUSTOM_VIEW(getEmptyModules(), "Vista Personalizada"),								// 20
	AULA(getEmptyModules(), "Aula"),													// 21
	NOTES(getEmptyModules(), "Notas"),													// 22
	@Deprecated
	SALTRA(getEmptyModules(), "Saltra"),												// 23
	BASIC_MANAGEMENT(getEmptyModules(), "Gesti\u00f3n B\u00e1sica"),					// 24
	STANDAR_MANAGEMENT(getStandarManagementModules(), "Gesti\u00f3n Est\u00e1ndar"),	// 25
	PROFESSIONAL_MANAGEMENT(getEmptyModules(), "Gesti\u00f3n Profesional"),				// 26
	@Deprecated
	KIT_DIGITAL_FACE(getBasicManagementModules(), "Kit Digital FACe"),					// 27
	@Deprecated
	KIT_DIGITAL_CRM(getKitDigitalCrmModules(), "Kit Digital CRM"),						// 28
	@Deprecated
	KIT_DIGITAL_ERP(getKitDigitalErpModules(), "Kit Digital ERP"),						// 29
	API_SERVICE(getEmptyModules(), "Servicio API"),										// 30
	WAREHOUSE(getWarehouseModules(), "Almac\u00e9n"),									// 31
	COMMERCIAL(getCommercialModules(), "Comercial"),									// 32
	MARKETING(getMarketingModules(), "Marketing"),										// 33
	TREASURY(getTreasuryModules(), "Tesorer\u00eda"),									// 34
	GROUPWARE(getGroupwareModules(), "Expedientes"),									// 35
	INVOFOX(getEmptyModules(), "OCR Invofox"),											// 36
	SERES(getEmptyModules(), "Seres"),													// 37
	AUTOBOOKING(getEmptyModules(), "Auto-Contratacion"),								// 38
	CAU(getEmptyModules(), "Soporte"),													// 39
	CAU_ADVANCE(getEmptyModules(), "Soporte Avanzado")									// 40
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
	

	public static AonApp getByDescription(String selectedAonApp) {
		if(AonStringUtils.isBlank(selectedAonApp)) return null;
		for (AonApp rs : values()) {
			if(selectedAonApp.equalsIgnoreCase(rs.getDescription()))
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
		list.add(Module.MANAGEMENT);
		list.add(Module.CRM);
		return list;
	}
	
	private static LinkedList<Module> getStandarManagementModules() {
		LinkedList<Module> list = new LinkedList<>();
		list.add(Module.AON_ONE);
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

	public static List<AonRole> getPortalAonRole(AonApp aonApp) {
		switch (aonApp) {
		case INVOICE:
			return List.of(AonRole.INVOICE_PORTAL);
		case DOCUMENTAL:
			return List.of(AonRole.DOCUMENTAL);
		case MESSENGER:
			return List.of(AonRole.MESSENGER_PORTAL);
		case ACCOUNTING:
			return List.of(AonRole.ACCOUNTING_MANAGER);
		case FISCAL:
			return List.of(AonRole.FISCAL_MANAGER);
		case PAYROLL:
			return List.of(AonRole.PAYROLL_PORTAL);
		case OCR:
			return List.of(AonRole.OCR);
		case AIO:
			return List.of(AonRole.AON_AIO);
		case ALMA:
			return List.of(AonRole.ALMA);
		case COMUNICA:
			return List.of(AonRole.COMUNICA_PORTAL);
		case BIDOQ:
			return List.of(AonRole.BIDOQ);
		case CONVENIOS:
			return List.of(AonRole.CONVENIOS);
		case BANK:
			return List.of(AonRole.BANK);
		case TIMECONTROL:
			return List.of(AonRole.TIMECONTROL_PORTAL);
		case MANAGEMENT:
			return List.of(AonRole.MANAGEMENT);
		case PACK_SUITE:
			return List.of();
		case PACK_PORTAL:
			return List.of();
		case PACK_PAYROLL:
			return List.of();
		case PACK_FISCAL_ACCOUNTING:
			return List.of();
		case SELFCONTA:
			return List.of(AonRole.SELFCONTA);
		case CUSTOM_VIEW:
			return List.of();
		case AULA:
			return List.of();
		case NOTES:
			return List.of(AonRole.NOTES);
		case SALTRA:
			return List.of(AonRole.SALTRA_PORTAL);
		case BASIC_MANAGEMENT:
			return List.of();
		case STANDAR_MANAGEMENT:
			return List.of();
		case PROFESSIONAL_MANAGEMENT:
			return List.of();
		case API_SERVICE:
			return List.of();
		case WAREHOUSE:
			return List.of(AonRole.WAREHOUSE);
		case COMMERCIAL:
			return List.of(AonRole.COMMERCIAL);
		case MARKETING:
			return List.of(AonRole.MARKETING);
		case TREASURY:
			return List.of(AonRole.TREASURY);
		case GROUPWARE:
			return List.of(AonRole.GROUPWARE);
		case INVOFOX:
			return List.of(AonRole.INVOFOX);
		case SERES:
			return List.of(AonRole.SERES);
		case AUTOBOOKING:
			return List.of();
		default:
			return List.of(AonRole.ENTERPRISE);
		}
	}

}
