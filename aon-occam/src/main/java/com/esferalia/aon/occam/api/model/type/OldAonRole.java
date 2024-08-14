package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

import com.esferalia.aon.watson.util.AonStringUtils;

public enum OldAonRole implements Serializable {
	
	 GUEST( "Guest" )				// Invitado
	,ADMIN( "Admin" )				// Administrador
	,CONFIG( "Config" )				// Configuración
	,AUDITOR( "Auditor" )			// Auditor
	,CONFIDENTIALITY( "Confidentiality" )	// Acceso a la función de confidencialidad.
	,PRODUCT( "Product" )			// Acceso a Productos
	,COMMERCIAL( "Commercial" ) 		// Acceso a Comercial
	,SALE( "Sale" ) 				// Acceso a Ventas
	,PURCHASE( "Purchase" ) 			// Acceso a Compras
	,WAREHOUSE( "Warehouse" ) 			// Acceso a Almacén
	,ACCOUNTING( "Accounting" ) 		// Acceso a Contabilidad
	,FINANCE( "Finance" ) 			// Acceso a Facturación y Tesoreria
	,STATISTICS( "Statistics" ) 		// Acceso a Estadísticas
	,TASK_MONITORING( "TaskMonitoring" ) 	// Monitor de Tareas.
	,E_SIGNATURE( "eSignature" ) 		// Capacidad de firmar documentos electrónicos.
	,SYS_ADMIN( "SisAdmin") 			// Capacidad de modificar las expresiones de las percepciones y deducciones.
	,TGC( "Tgc" ) 				// Acceso a los informes de nominas.
	,DOCUMENT( "Document" ) 			// Acceso a los documentos.
	,DOCUMENT_MANAGER( "DocumentManager" ) 	// Administrador documental.
	,PAYROLL( "Payroll" ) 			// Capacidad de modificar las expresiones de las percepciones y deducciones.
	,FISCAL( "Fiscal" ) 			// Acceso a los informes de nominas.
	,ACCOUNTING_MANAGER( "AccountingManager" ) // Gestor de Contabilidad.
	,CALL_CENTER( "CallCenter" ) 			// Acceso al Call Center.
	,CALL_CENTER_MANAGER( "CallCenterManager" ) 	// Administrador Call Center.
	;
	 
	private String value;

	private OldAonRole(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public static OldAonRole valueOfBDValue(String value) {
		for (OldAonRole role : OldAonRole.values()) {
			if (AonStringUtils.equals( role.getValue(), value)) {
				return role;
			}
		}
		return null;
	}

	public static OldAonRole safeValueOf( String i ) {
		if(AonStringUtils.isBlank(i)) return null;
		for (OldAonRole rs : values()) {
			if(i.equalsIgnoreCase(rs.name()) || i.equalsIgnoreCase(rs.getValue()))
				return rs;
		}
		return null;
	}
}
