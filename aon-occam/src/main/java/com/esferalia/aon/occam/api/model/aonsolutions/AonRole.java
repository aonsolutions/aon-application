package com.esferalia.aon.occam.api.model.aonsolutions;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import com.esferalia.aon.watson.util.AonStringUtils;

public enum AonRole  implements Serializable {
	
	ADMIN, 				// 0 
	ACCOUNTING, 		// 1 - ACCESO A CONTABILIDAD -  MODO PORTAL/EMPRESA 
	ACCOUNTING_MANAGER,	// 2 - ACCESO A CONTABILIDAD -  MODO ASESOR
	FISCAL,				// 3 - ACCESO A FISCAL -  MODO PORTAL/EMPRESA 
	FISCAL_MANAGER,		// 4 - ACCESO A FISCAL -  MODO ASESOR
	PAYROLL,			// 5 - ACCESO A LABORAL -  MODO EMPLEADO
	PAYROLL_MANAGER,	// 6 - ACCESO A LABORAL - MODO ASESOR
	PAYROLL_PORTAL,		// 7 - ACCESO A LABORAL -  MODO PORTAL/EMPRESA
	DOCUMENTAL,			// 8 - ACCESO A DOCUMENTAL -  MODO PORTAL/EMPRESA
	DOCUMENTAL_MANAGER,	// 9 - ACCESO A DOCUMENTAL -  MODO ASESOR
	COMUNICA,			// 10 - ACCESO A COMUNIC@ -  MODO EMPLEADO
	COMUNICA_MANAGER,	// 11 - ACCESO A COMUNIC@ -  MODO ASESOR
	COMUNICA_PORTAL,	// 12 - ACCESO A COMUNIC@ -  MODO PORTAL/EMPRESA
	TIMECONTROL,		// 13 - ACCESO A CONTROL DE HORARIO -  MODO EMPLEADO
	TIMECONTROL_MANAGER,// 14 - ACCESO A CONTROL DE HORARIO -  MODO ASESOR
	TIMECONTROL_PORTAL, // 15 - ACCESO A CONTROL DE HORARIO -  MODO PORTAL/EMPRESA
	MESSENGER,			// 16 - ACCESO A MENSAJERIA -  MODO EMPLEADO
	MESSENGER_MANAGER,	// 17 - ACCESO A MENSAJERIA -  MODO ASESOR
	INVOICE,			// 18 - ACCESO A FACTURAS - MODO EMPLEADO
	INVOICE_MANAGER,	// 19 - ACCESO A FACTURAS - MODO ASESOR
	INVOICE_PORTAL,		// 20 - ACCESO A FACTURAS - MODO PORTAL/EMPRESA
	MANAGEMENT,			// 21 - ACCESO A GESTION - MODO PORTAL/EMPRESA
	MANAGEMENT_MANAGER,	// 22 - ACCESO A GESTION - MODO ASESOR
	ALMA,				// 23 - ACCESO AL SERVICIO ALMA
	OCR,				// 24 - ACCESO AL SERVICIO OCR
	BANK,				// 25 - ACCESO AL SERVICIO BANK
	CONVENIOS,			// 26 - ACCESO AL SERVICIO CONVENIOS
	AIO,				// 27 -
	BIDOQ,				// 28 -
	EMPLOYEE,			// 29 - USUARIO TIPO EMPLEADO
	ENTERPRISE,			// 30 - USUARIO TIPO EMPRESA
	DOCUMENTAL_PORTAL,	// 31 - ACCESO A DOCUMENTAL -  MODO PORTAL/EMPRESA
	CONFIDENTIALITY,	// 32 - USUARIO CON ACCESO A DATOS CONFIDENCIALES
	ALPHA,				// 33 - USUARIO CON ACCESO A FUNCIONALIDADES ALPHA
	BETA,				// 34 - USUARIO CON ACCESO A FUNCIONALIDADES BETA
	AON_AIO,			// 35 - ACCESO A AON AIO
	AON_SMB,			// 36 - ACCESO A AON SMB
	DEV,				// 37 - USUARIO TIPO DESARROLLADOR.
	SELFCONTA,			// 38 - 
	MESSENGER_PORTAL,	// 39 - ACCESO A MENSAJERIA -  MODO MODO PORTAL/EMPRESA
	NOTES,				// 40 -
	@Deprecated
	SALTRA,				// 41 -
	@Deprecated
	SALTRA_MANAGER,		// 42 - ACCESO A COMUNIC@ -  MODO ASESOR
	@Deprecated
	SALTRA_PORTAL,		// 43 - ACCESO A COMUNIC@ -  MODO PORTAL/EMPRESA
	COMMERCIAL, 		// 44 - ACCESO A COMERCIAL
	WAREHOUSE,			// 45 -
	TREASURY,			// 46 -
	MARKETING,			// 47 -
	GROUPWARE,			// 48 -
	CONSOLE,			// 49 - ACCESO A CONSOLE (sig.aonsolutions.org)
	INVOFOX,			// 50 -
	SERES,				// 51 - 
	FACTURAE,			// 52 -
	OFFICE,				// 53 - ACCESO A DESPACHO - MODO EMPLEADO
	OFFICE_MANAGER,		// 54 - ACCESO A DESPACHO - MODO ASESOR
	OFFICE_PORTAL,		// 55 - ACCESO A DESPACHO - MODO PORTAL/EMPRESA
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

