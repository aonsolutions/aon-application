package com.esferalia.aon.occam.api.model.aonsolutions;

import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

//import org.json.JSONArray;

import com.esferalia.aon.occam.api.model.Module;

public enum AonApp implements Serializable{
	
	INVOICE(getEmptyModules()),
	DOCUMENTAL(getDocumentalModules()),
	MESSENGER(getMessengerModules()),
	ACCOUNTING(getAccountingModules()),
	FISCAL(getFiscalModules()),
	PAYROLL(getPayrollModules()),
	OCR(getEmptyModules()),
	AIO(getEmptyModules()),
	ALMA(getEmptyModules()),
	COMUNICA(getEmptyModules()),
	BIDOQ(getEmptyModules()),
	CONVENIOS(getEmptyModules()),
	BANK(getEmptyModules()),
	TIMECONTROL(getEmptyModules()),
	MANAGEMENT(getManagementModules()),
	PACK_SUITE(getPackSuiteModules()),
	PACK_PORTAL(getPackPortalModules()),
	PACK_PAYROLL(getPayrollModules()),
	PACK_FISCAL_ACCOUNTING(getPackFiscalAccountingModules()),
	SELFCONTA(getEmptyModules()),
	CUSTOM_VIEW(getEmptyModules()),
	AULA(getEmptyModules()),
	NOTES(getEmptyModules()),
	SALTRA(getEmptyModules()),
	BASIC_MANAGEMENT(getBasicManagementModules()),
	STANDAR_MANAGEMENT(getStandarManagementModules()),
	PROFESSIONAL_MANAGEMENT(getProfessionalManagementModules())
	;
	
	LinkedList<Module> modules;
	
	private AonApp(LinkedList<Module> modules) {
		this.modules = modules;
	}
	
	public List<Module> getModules() {
		return modules;
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
		for (AonApp rs : values()) {
			if(i.equalsIgnoreCase(rs.name()))
				return rs;
		}
		return null;
	}
	
//	public static List<AonApp> safeValueOf(JSONArray array){
//		LinkedList<AonApp> apps = new LinkedList<>();
//		for(int i = 0; i < array.length(); i++) {
//			AonApp app = AonApp.safeValueOf(array.optString(i));
//			if(app != null) {
//				apps.add(AonApp.safeValueOf(array.optString(i)));
//			}
//		}
//		return apps;
//	}
	
	public static List<AonApp> getValues() {
		return Arrays.asList(values());
	}
	
	private static LinkedList<Module> getEmptyModules() {
		return new LinkedList<>();
	}
	
	private static LinkedList<Module> getBasicManagementModules() {
		LinkedList<Module> list = new LinkedList<>();
		list.add(Module.AON_FINANCE);
		return list;
	}
	
	private static LinkedList<Module> getStandarManagementModules() {
		LinkedList<Module> list = new LinkedList<>();
//		list.add(Module.TREASURY);
//		list.add(Module.MARKETING);
		list.add(Module.CRM);
		list.add(Module.MANAGEMENT);
		return list;
	}
	
	private static LinkedList<Module> getProfessionalManagementModules() {
		LinkedList<Module> list = getStandarManagementModules();
		list.add(Module.WAREHOUSE);
		list.add(Module.GROUPWARE);
		return list;
	}
	
	private static LinkedList<Module> getDocumentalModules() {
		LinkedList<Module> list = new LinkedList<>();
		list.add(Module.DOCUMENT);
		list.add(Module.DOCUMENT_PORTAL);
		return list;
	}
	
	private static LinkedList<Module> getMessengerModules() {
		LinkedList<Module> list = new LinkedList<>();
		list.add(Module.CALL_CENTER);
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
		list.add(Module.PAYROLL_PORTAL);
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
		list.add(Module.DOCUMENT_PORTAL);
		list.add(Module.ACCOUNTING);
		list.add(Module.FISCAL);
		list.add(Module.PAYROLL);
		list.add(Module.PAYROLL_PORTAL);
		list.add(Module.CALL_CENTER);
		return list;
	}
	
	private static LinkedList<Module> getPackPortalModules() {
		LinkedList<Module> list =new LinkedList<>();
		list.add(Module.DOCUMENT);
		list.add(Module.DOCUMENT_PORTAL);
		list.add(Module.CALL_CENTER);
		return list;
	}
	
	private static LinkedList<Module> getPackFiscalAccountingModules() {
		LinkedList<Module> list = new LinkedList<>();
		list.add(Module.FISCAL);
		list.add(Module.ACCOUNTING);
		return list;
	}
}
