package com.code.aon.ui.common.role;

import java.util.HashMap;
import java.util.Map;

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
	GUEST( "Guest", "aon_role_guest" ),

	/**
	 * Role de Administrador
	 */
	ADMIN( "Admin", "aon_role_admin" ),

	/**
	 * Role de Configuración
	 */
	CONFIG( "Config", "aon_configuration" ),
	
	/**
	 * Role de Auditor
	 */
	AUDITOR( "Auditor", "aon_role_auditor" ),

	/**
	 * Role de Acceso a la función de confidencialidad.
	 */
	CONFIDENTIALITY( "Confidentiality", "aon_confidential" ),

	/**
	 * Role de Acceso a Productos
	 */
	PRODUCT( "Product", "aon_role_product" ),
	
	/**
	 * Role de Acceso a Comercial
	 */
	COMMERCIAL( "Commercial", "aon_module_commercial" ),

	/**
	 * Role de Acceso a Ventas
	 */
	SALE( "Sale", "aon_sales" ),

	/**
	 * Role de Acceso a Compras
	 */
	PURCHASE( "Purchase", "aon_purchases" ),
	
	/**
	 * Role de Acceso a Almacén
	 */
	WAREHOUSE( "Warehouse", "aon_module_warehouse" ),
	
	/**
	 * Role de Acceso a Contabilidad
	 */
	ACCOUNTING( "Accounting", "aon_module_accounting" ),

	/**
	 * Role de Acceso a Facturación y Tesoreria
	 */
	FINANCE( "Finance", "aon_module_finance" ),

	/**
	 * Role de Acceso a Estadísticas
	 */
	STATISTICS( "Statistics", "aon_role_statistics" ),
	
	/**
	 * Role de Monitor de Tareas.
	 */
	TASK_MONITORING( "TaskMonitoring", "aon_role_task_monitoring" ),

	/**
	 * Role para dar capacidad de firmar documentos electrónicos.
	 */
	E_SIGNATURE( "eSignature", "aon_role_eSignature" ),
	
	/**
	 * Role para dar capacidad de modificar las expresiones de las percepciones y deducciones.
	 */
	SYS_ADMIN( "SisAdmin", "aon_role_sys_admin" ),
	
	/**
	 * Role de acceso a los informes de nominas.
	 */
	TGC( "Tgc", "aon_role_tgc" ),

	/**
	 * Role de acceso a los documentos.
	 */
	DOCUMENT( "Document", "aon_module_document" ),
	
	/**
	 * Role de Adminitrador Documental.
	 */
	DOCUMENT_MANAGER( "DocumentManager", "aon_role_document_manager" ),
	
	/**
	 * Role para dar capacidad de modificar las expresiones de las percepciones y deducciones.
	 */
	PAYROLL( "Payroll", "aon_role_payroll" ),
	
	/**
	 * Role de acceso a los informes de nominas.
	 */
	FISCAL( "Fiscal", "aon_role_fiscal" ),

	/**
	 * Role de Gestor de Contabilidad.
	 */
	ACCOUNTING_MANAGER( "AccountingManager", "aon_role_accounting_manager" );

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