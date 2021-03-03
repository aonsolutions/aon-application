package com.esferalia.aon.occam.api.model.aonsolutions;

import java.io.Serializable;
import java.util.LinkedList;

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
	PACK_FISCAL_ACCOUNTING(getPackFiscalAccountingModules())
	;
	
	LinkedList<Module> modules;
	
	private AonApp(LinkedList<Module> modules) {
		this.modules = modules;
	}
	
	public LinkedList<Module> getModules() {
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
	
	private static LinkedList<Module> getEmptyModules() {
		return new LinkedList<>();
	}
	private static LinkedList<Module> getDocumentalModules() {
		LinkedList<Module> list = new LinkedList<Module>();
		list.add(Module.DOCUMENT);
		list.add(Module.DOCUMENT_PORTAL);
		return list;
	}
	
	private static LinkedList<Module> getMessengerModules() {
		LinkedList<Module> list = new LinkedList<Module>();
		list.add(Module.CALL_CENTER);
		return list;
	}
	
	private static LinkedList<Module> getAccountingModules() {
		LinkedList<Module> list = new LinkedList<Module>();
		list.add(Module.ACCOUNTING);
		return list;
	}
	
	private static LinkedList<Module> getFiscalModules() {
		LinkedList<Module> list = new LinkedList<Module>();
		list.add(Module.FISCAL);
		return list;
	}
	
	private static LinkedList<Module> getPayrollModules() {
		LinkedList<Module> list = new LinkedList<Module>();
		list.add(Module.PAYROLL);
		list.add(Module.PAYROLL_PORTAL);
		return list;
	}
	
	private static LinkedList<Module> getManagementModules() {
		LinkedList<Module> list = new LinkedList<Module>();
		list.add(Module.MANAGEMENT);
		return list;
	}
	
	private static LinkedList<Module> getPackSuiteModules() {
		LinkedList<Module> list =new LinkedList<Module>();
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
		LinkedList<Module> list =new LinkedList<Module>();
		list.add(Module.DOCUMENT);
		list.add(Module.DOCUMENT_PORTAL);
		list.add(Module.CALL_CENTER);
		return list;
	}
	
	private static LinkedList<Module> getPackFiscalAccountingModules() {
		LinkedList<Module> list = new LinkedList<Module>();
		list.add(Module.FISCAL);
		list.add(Module.ACCOUNTING);
		return list;
	}
}
