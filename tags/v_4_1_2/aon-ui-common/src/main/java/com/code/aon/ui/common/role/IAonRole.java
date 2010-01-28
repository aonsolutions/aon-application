package com.code.aon.ui.common.role;

/**
 * Lista de roles habituales en las aplicaciones AON.
 * 
 * @author ecastellano
 * 
 */
public interface IAonRole {

	/**
	 * Role de Monitor de Tareas.
	 */
	String TASK_MONITORING = "TaskMonitoring";

	/**
	 * Role de Administrador
	 */
	String ADMIN = "Admin";

	/**
	 * Role de Operador
	 */
	String USER = "User";

	/**
	 * Role de Acceso a Contabilidad
	 */
	String ACCOUNTING = "Accounting";

	/**
	 * Role de Acceso a Facturación
	 */
	String INVOICING = "Invoicing";

	/**
	 * Role de Acceso a la función de confidencialidad.
	 */
	String CONFIDENTIALITY = "Confidentiality";

}
