package com.esferalia.aon.occam.api.model.aonsolutions;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import com.esferalia.aon.watson.util.AonStringUtils;

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
	MESSENGER,			// ACCESO A MENSAJERIA -  MODO EMPLEADO
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
	AIO,
	BIDOQ,
	EMPLOYEE,			// USUARIO TIPO EMPLEADO
	ENTERPRISE,			// USUARIO TIPO EMPRESA
	DOCUMENTAL_PORTAL,	// ACCESO A DOCUMENTAL -  MODO PORTAL/EMPRESA
	CONFIDENTIALITY,	// USUARIO CON ACCESO A DATOS CONFIDENCIALES
	ALPHA,				// USUARIO CON ACCESO A FUNCIONALIDADES ALPHA
	BETA,				// USUARIO CON ACCESO A FUNCIONALIDADES BETA
	AON_AIO,			// ACCESO A AON AIO
	AON_SMB,			// ACCESO A AON SMB
	DEV,				// USUARIO TIPO DESARROLLADOR.
	SELFCONTA,
	MESSENGER_PORTAL,	// ACCESO A MENSAJERIA -  MODO MODO PORTAL/EMPRESA
	NOTES,
	SALTRA,
	SALTRA_MANAGER,		// ACCESO A COMUNIC@ -  MODO ASESOR
	SALTRA_PORTAL,		// ACCESO A COMUNIC@ -  MODO PORTAL/EMPRESA
	COMMERCIAL, 		// ACCESO A COMERCIAL
	WAREHOUSE,
	TREASURY,
	MARKETING,
	GROUPWARE,
	CONSOLE				// ACCESO A CONSOLE (sig.aonsolutions.org)
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
		if(AonStringUtils.isBlank(i)) return null;
		for (AonRole rs : values()) {
			if(i.equalsIgnoreCase(rs.name()))
				return rs;
		}
		return null;
	}
	
	public static List<AonRole> getEmployeeRoles() {
		LinkedList<AonRole> list = new LinkedList<>();
		list.add(EMPLOYEE);
		list.add(TIMECONTROL);
		list.add(DOCUMENTAL);
		list.add(PAYROLL);
		list.add(MESSENGER);		
		return list;
	}
	
	public static List<AonRole> getEnterpriseRoles() {
		LinkedList<AonRole> list = new LinkedList<>();
		list.add(ENTERPRISE);
		list.add(TIMECONTROL);
		list.add(TIMECONTROL_PORTAL);
		list.add(DOCUMENTAL);
		list.add(DOCUMENTAL_PORTAL);
		list.add(PAYROLL);
		list.add(PAYROLL_PORTAL);
		list.add(MESSENGER);		
		list.add(COMUNICA);
		list.add(COMUNICA_PORTAL);
		list.add(FISCAL);
		list.add(ACCOUNTING);
		list.add(INVOICE);
		list.add(INVOICE_PORTAL);
		list.add(OCR);
		list.add(MANAGEMENT);
		return list;
	}
	
	public static List<AonRole> list() {
		LinkedList<AonRole> list = new LinkedList<>();
		Collections.addAll(list, values());
		return list;
	}
	
	public static Stream<AonRole> stream() {
		return list().stream();
	}
}

