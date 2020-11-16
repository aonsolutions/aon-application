package com.esferalia.aon.occam.api.model.aonsolutions;

import java.io.Serializable;

public enum AonRole  implements Serializable {
	ADMIN, 
	ACCOUNTING, 		// ACCESO A CONTABILIDAD -  MODO PORTAL/EMPRESA 
	ACCOUNTING_MANAGER,	// ACCESO A CONTABILIDAD -  MODO ASESOR
	FISCAL,				// ACCESO A FISCAL -  MODO PORTAL/EMPRESA 
	FISCAL_MANAGER,		// ACCESO A FISCAL -  MODO ASESOR
	PAYROLL,			// ACCESO A LABORAL -  MODO EMPLEADO
	PAYROLL_MANAGER,	// ACCESO A LABORAL - MODO ASESOR
	PAYROLL_PORTAL,		// ACCESO A LABORAL -  MODO PORTAL/EMPRESA
	DOCUMENTAL,			// ACCESO A DOCUMENTAL -  MODO PORTAL/EMPRESA
	DOCUMENTAL_MANAGER,	// ACCESO A DOCUMENTAL -  MODO ASESOR
	COMUNICA,			// ACCESO A COMUNIC@ -  MODO EMPLEADO
	COMUNICA_MANAGER,	// ACCESO A COMUNIC@ -  MODO ASESOR
	COMUNICA_PORTAL,	// ACCESO A COMUNIC@ -  MODO PORTAL/EMPRESA
	TIMECONTROL,		// ACCESO A CONTROL DE HORARIO -  MODO EMPLEADO
	TIMECONTROL_MANAGER,// ACCESO A CONTROL DE HORARIO -  MODO ASESOR
	TIMECONTROL_PORTAL, // ACCESO A CONTROL DE HORARIO -  MODO PORTAL/EMPRESA
	MESSENGER,			// ACCESO A MENSAJERIA -  MODO PORTAL/EMPRESA
	MESSENGER_MANAGER,	// ACCESO A MENSAJERIA -  MODO ASESOR
	INVOICE,			// ACCESO A FACTURAS - MODO EMPLEADO
	INVOICE_MANAGER,	// ACCESO A FACTURAS - MODO ASESOR
	INVOICE_PORTAL,		// ACCESO A FACTURAS - MODO PORTAL/EMPRESA
	MANAGEMENT,			// ACCESO A GESTION - MODO PORTAL/EMPRESA
	MANAGEMENT_MANAGER,	// ACCESO A GESTION - MODO ASESOR
	ALMA,				// ACCESO AL SERVICIO ALMA
	OCR,				// ACCESO AL SERVICIO OCR
	BANK,				// ACCESO AL SERVICIO BANK
	CONVENIOS,			// ACCESO AL SERVICIO CONVENIOS
	AON,
	BIDOQ
	;
	public Byte value(){
		return (byte) ordinal();
	}
	
	public static AonRole safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}

	public static AonRole safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= AonRole.values().length) return null;
		return AonRole.values()[i];
	}
	
	public static AonRole safeValueOf( String i ) {
		if (i == null || "".equals(i)) return null;
		if(i.equalsIgnoreCase(ADMIN.name())) {
			return ADMIN;
		} else if(i.equalsIgnoreCase(ACCOUNTING.name())) {
			return ACCOUNTING;
		} else if(i.equalsIgnoreCase(ACCOUNTING_MANAGER.name())) {
			return ACCOUNTING_MANAGER;
		} else if(i.equalsIgnoreCase(FISCAL.name())) {
			return FISCAL;
		} else if(i.equalsIgnoreCase(FISCAL_MANAGER.name())) {
			return FISCAL_MANAGER;
		} else if(i.equalsIgnoreCase(PAYROLL.name())) {
			return PAYROLL;
		} else if(i.equalsIgnoreCase(PAYROLL_MANAGER.name())) {
			return PAYROLL_MANAGER;
		} else if(i.equalsIgnoreCase(PAYROLL_PORTAL.name())) {
			return PAYROLL_PORTAL;
		} else if(i.equalsIgnoreCase(DOCUMENTAL.name())) {
			return DOCUMENTAL;
		} else if(i.equalsIgnoreCase(DOCUMENTAL_MANAGER.name())) {
			return DOCUMENTAL_MANAGER;
		} else if(i.equalsIgnoreCase(COMUNICA.name())) {
			return COMUNICA;
		} else if(i.equalsIgnoreCase(COMUNICA_MANAGER.name())) {
			return COMUNICA_MANAGER;
		} else if(i.equalsIgnoreCase(COMUNICA_PORTAL.name())) {
			return COMUNICA_PORTAL;
		} else if(i.equalsIgnoreCase(TIMECONTROL.name())) {
			return TIMECONTROL;
		} else if(i.equalsIgnoreCase(TIMECONTROL_MANAGER.name())) {
			return TIMECONTROL_MANAGER;
		} else if(i.equalsIgnoreCase(TIMECONTROL_PORTAL.name())) {
			return TIMECONTROL_PORTAL;
		} else if(i.equalsIgnoreCase(MESSENGER.name())) {
			return MESSENGER;
		} else if(i.equalsIgnoreCase(MESSENGER_MANAGER.name())) {
			return MESSENGER_MANAGER;
		} else if(i.equalsIgnoreCase(INVOICE.name())) {
			return INVOICE;
		} else if(i.equalsIgnoreCase(INVOICE_MANAGER.name())) {
			return INVOICE_MANAGER;
		} else if(i.equalsIgnoreCase(INVOICE_PORTAL.name())) {
			return INVOICE_PORTAL;
		} 
		return null;
	}
}

