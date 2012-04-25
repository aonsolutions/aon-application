package com.code.aon.ui.common.role;

/**
 * Lista de roles habituales en las aplicaciones AON.
 * 
 * @author ecastellano
 * 
 */
public enum IAonRole {

	/**
	 * Role de Operador
	 */
	USER( "User" ),

	/**
	 * Role de Invitado
	 */
	GUEST( "Guest" ),

	/**
	 * Role de Administrador
	 */
	ADMIN( "Admin" ),

	/**
	 * Role de Configuración
	 */
	CONFIG( "Config" ),
	
	/**
	 * Role de Auditor
	 */
	AUDITOR( "Auditor" ),

	/**
	 * Role de Acceso a la función de confidencialidad.
	 */
	CONFIDENTIALITY( "Confidentiality" ),

	/**
	 * Role de Acceso a Productos
	 */
	PRODUCT( "Product" ),
	
	/**
	 * Role de Acceso a Comercial
	 */
	COMMERCIAL( "Commercial" ),

	/**
	 * Role de Acceso a Ventas
	 */
	SALE( "Sale" ),

	/**
	 * Role de Acceso a Compras
	 */
	PURCHASE( "Purchase" ),
	
	/**
	 * Role de Acceso a Almacén
	 */
	WAREHOUSE( "Warehouse" ),
	
	/**
	 * Role de Acceso a Contabilidad
	 */
	ACCOUNTING( "Accounting" ),

	/**
	 * Role de Acceso a Facturación y Tesoreria
	 */
	FINANCE( "Finance" ),

	/**
	 * Role de Acceso a Estadísticas
	 */
	STATISTICS( "Statistics" ),
	
	/**
	 * Role de Monitor de Tareas.
	 */
	TASK_MONITORING( "TaskMonitoring" ),

	/**
	 * Role para dar capacidad de firmar documentos electrónicos.
	 */
	E_SIGNATURE( "eSignature" ),
	
	/**
	 * Role para dar capacidad de modificar las expresiones de las percepciones y deducciones.
	 */
	SIS_ADMIN( "SisAdmin" ),
	
	/**
	 * Role de acceso a los informes de nominas.
	 */
	TGC( "Tgc" );
	
	private String name;

	private IAonRole(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
}
