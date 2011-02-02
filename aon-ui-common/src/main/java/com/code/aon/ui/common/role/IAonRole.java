package com.code.aon.ui.common.role;

/**
 * Lista de roles habituales en las aplicaciones AON.
 * 
 * @author ecastellano
 * 
 */
public interface IAonRole {


	/**
	 * Role de Operador
	 */
	String USER = "User";

	/**
	 * Role de Invitado
	 */
	String GUEST = "Guest";

	/**
	 * Role de Administrador
	 */
	String ADMIN = "Admin";

	/**
	 * Role de Configuración
	 */
	String CONFIG = "Config";
	
	/**
	 * Role de Auditor
	 */
	String AUDITOR = "Auditor";

	/**
	 * Role de Acceso a la función de confidencialidad.
	 */
	String CONFIDENTIALITY = "Confidentiality";

	/**
	 * Role de Acceso a Productos
	 */
	String PRODUCT = "Product";
	
	/**
	 * Role de Acceso a Comercial
	 */
	String COMMERCIAL = "Commercial";

	/**
	 * Role de Acceso a Ventas
	 */
	String SALE = "Sale";

	/**
	 * Role de Acceso a Compras
	 */
	String PURCHASE = "Purchase";
	
	/**
	 * Role de Acceso a Almacén
	 */
	String WAREHOUSE = "Warehouse";
	
	/**
	 * Role de Acceso a Contabilidad
	 */
	String ACCOUNTING = "Accounting";

	/**
	 * Role de Acceso a Facturación y Tesoreria
	 */
	String FINANCE = "Finance";

	/**
	 * Role de Acceso a Estadísticas
	 */
	String STATISTICS = "Statistics";
	
	/**
	 * Role de Monitor de Tareas.
	 */
	String TASK_MONITORING = "TaskMonitoring";

	/**
	 * Role para dar capacidad de firmar documentos electrónicos.
	 */
	String E_SIGNATURE = "eSignature";

}
