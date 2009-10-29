/*
 * Created on 04-sep-2006
 *
 */
package com.code.aon.common.tree;

/**
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 04-sep-2006
 * @since 1.0
 *
 */

public interface IEmployeeConstants {

    /**
     * Identificador del tipo application para el facet del tree2.
     */
    static final String ICON_PATH_BASE = "/images/tree/";

    /**
     * Identificador del tipo para el facet del tree2.
     */
    static final String ROOT_TYPE = "root",
                        BRANCH_TYPE = "branch",
                        LEAF_TYPE = "leaf";

    /**
     * Identificador del icono para el facet del tree2.
     */
    static final String ROOT_ICON			= ICON_PATH_BASE + "home_nav.gif",
    					DEPARTMENT_ICON_OPEN	= ICON_PATH_BASE + "toc_open.gif",
    					WORKPLACE_ICON		= ICON_PATH_BASE + "workplace.gif",
    					WORKACTIVITY_ICON	= ICON_PATH_BASE + "workactivity.gif",
    					EMPLOYEE_ICON		= ICON_PATH_BASE + "udt.gif";

    /**
     * Identificador del icono para el facet del tree2.
     */
    static final String DEPARTMENT_ICON_CLOSED	= ICON_PATH_BASE + "toc_closed.gif";
}
