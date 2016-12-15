package com.code.aon.ui.common.role;

import static com.code.aon.ui.common.ICommonMessages.CONFIDENTIAL;
import static com.code.aon.ui.common.ICommonMessages.MODULE_ACCOUNTING;
import static com.code.aon.ui.common.ICommonMessages.MODULE_COMMERCIAL;
import static com.code.aon.ui.common.ICommonMessages.MODULE_DOCUMENT;
import static com.code.aon.ui.common.ICommonMessages.MODULE_FINANCE;
import static com.code.aon.ui.common.ICommonMessages.MODULE_WAREHOUSE;
import static com.code.aon.ui.common.ICommonMessages.PURCHASES;
import static com.code.aon.ui.common.ICommonMessages.ROLE_ACCOUNTING_MANAGER;
import static com.code.aon.ui.common.ICommonMessages.ROLE_ADMIN;
import static com.code.aon.ui.common.ICommonMessages.ROLE_AUDITOR;
import static com.code.aon.ui.common.ICommonMessages.ROLE_CONFIGURATION;
import static com.code.aon.ui.common.ICommonMessages.ROLE_DOCUMENT_MANAGER;
import static com.code.aon.ui.common.ICommonMessages.ROLE_ESIGNATURE;
import static com.code.aon.ui.common.ICommonMessages.ROLE_FISCAL;
import static com.code.aon.ui.common.ICommonMessages.ROLE_GUEST;
import static com.code.aon.ui.common.ICommonMessages.ROLE_PAYROLL;
import static com.code.aon.ui.common.ICommonMessages.ROLE_PRODUCT;
import static com.code.aon.ui.common.ICommonMessages.ROLE_STATISTICS;
import static com.code.aon.ui.common.ICommonMessages.ROLE_SYS_ADMIN;
import static com.code.aon.ui.common.ICommonMessages.ROLE_TASK_MONITORING;
import static com.code.aon.ui.common.ICommonMessages.ROLE_TGC;
import static com.code.aon.ui.common.ICommonMessages.SALES;

import java.util.HashMap;
import java.util.Map;

import com.code.aon.ui.common.ICommonMessages;
import com.code.aon.ui.util.AonUtil;

/**
 * Lista de roles habituales en las aplicaciones AON.
 * 
 * @author ecastellano
 * 
 */
public enum IAonRole {

	/**
	 * Role de Invitado
	 */
	GUEST( "Guest", ROLE_GUEST ),

	/**
	 * Role de Administrador
	 */
	ADMIN( "Admin", ROLE_ADMIN ),

	/**
	 * Role de Configuración
	 */
	CONFIG( "Config", ROLE_CONFIGURATION ),
	
	/**
	 * Role de Auditor
	 */
	AUDITOR( "Auditor", ROLE_AUDITOR ),

	/**
	 * Role de Acceso a la función de confidencialidad.
	 */
	CONFIDENTIALITY( "Confidentiality", CONFIDENTIAL ),

	/**
	 * Role de Acceso a Productos
	 */
	PRODUCT( "Product", ROLE_PRODUCT ),
	
	/**
	 * Role de Acceso a Comercial
	 */
	COMMERCIAL( "Commercial", MODULE_COMMERCIAL ),

	/**
	 * Role de Acceso a Ventas
	 */
	SALE( "Sale", SALES ),

	/**
	 * Role de Acceso a Compras
	 */
	PURCHASE( "Purchase", PURCHASES ),
	
	/**
	 * Role de Acceso a Almacén
	 */
	WAREHOUSE( "Warehouse", MODULE_WAREHOUSE ),
	
	/**
	 * Role de Acceso a Contabilidad
	 */
	ACCOUNTING( "Accounting", MODULE_ACCOUNTING ),

	/**
	 * Role de Acceso a Facturación y Tesoreria
	 */
	FINANCE( "Finance", MODULE_FINANCE ),

	/**
	 * Role de Acceso a Estadísticas
	 */
	STATISTICS( "Statistics", ROLE_STATISTICS ),
	
	/**
	 * Role de Monitor de Tareas.
	 */
	TASK_MONITORING( "TaskMonitoring", ROLE_TASK_MONITORING ),

	/**
	 * Role para dar capacidad de firmar documentos electrónicos.
	 */
	E_SIGNATURE( "eSignature", ROLE_ESIGNATURE ),
	
	/**
	 * Role para dar capacidad de modificar las expresiones de las percepciones y deducciones.
	 */
	SYS_ADMIN( "SisAdmin", ROLE_SYS_ADMIN ),
	
	/**
	 * Role de acceso a los informes de nominas.
	 */
	TGC( "Tgc", ROLE_TGC ),

	/**
	 * Role de acceso a los documentos.
	 */
	DOCUMENT( "Document", MODULE_DOCUMENT ),
	
	/**
	 * Role de Adminitrador Documental.
	 */
	DOCUMENT_MANAGER( "DocumentManager", ROLE_DOCUMENT_MANAGER ),
	
	/**
	 * Role para dar capacidad de modificar las expresiones de las percepciones y deducciones.
	 */
	PAYROLL( "Payroll", ROLE_PAYROLL ),
	
	/**
	 * Role de acceso a los informes de nominas.
	 */
	FISCAL( "Fiscal", ROLE_FISCAL ),

	/**
	 * Role de Gestor de Contabilidad.
	 */
	ACCOUNTING_MANAGER( "AccountingManager", ROLE_ACCOUNTING_MANAGER  ),
	
	/**
	 * Role de acceso al call center.
	 */
	CALL_CENTER( "CallCenter",  ICommonMessages.MODULE_CALL_CENTER),
	
	/**
	 * Role de Adminitrador Documental.
	 */
	CALL_CENTER_MANAGER( "CallCenterManager", ICommonMessages.ROLE_CALL_CENTER_MANAGER);

	private String name;
	
	private String messageKey;
	
	private static Map<String,IAonRole> map = createMap();

	private IAonRole(String name, String messageKey) {
		this.name = name;
		this.messageKey = messageKey;
	}
	
	public String getName() {
		return name;
	}
	
    public String getDisplayName() {
    	return AonUtil.getMessage(messageKey);
    }

	private static Map<String,IAonRole> createMap() {
		Map<String,IAonRole> map = new HashMap<String, IAonRole>();
		for( IAonRole role : IAonRole.values() ) {
			map.put(role.getName(), role);
		}
		return map;
	}

	public static IAonRole get( String name ) {
		return map.get(name);
	}
	
}