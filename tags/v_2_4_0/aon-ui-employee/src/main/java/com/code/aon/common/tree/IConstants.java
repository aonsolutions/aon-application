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

public interface IConstants {

    /**
     * Identificador del tipo application para el facet del tree2.
     */
    static final String ICON_PATH_BASE = "/images/obj16/";

    /**
     * Identificador del tipo para el facet del tree2.
     */
    static final String ROOT_TYPE = "root",
                        BRANCH_TYPE = "branch",
                        LEAF_TYPE = "leaf";

    /**
     * Identificador del icono para el facet del tree2.
     */
    static final String ROOT_ICON           = ICON_PATH_BASE + "applications.gif",
                        APPLICATION_ICON    = ICON_PATH_BASE + "application.gif",  
                        DOMAIN_ICON         = ICON_PATH_BASE + "domain.gif",
                        ACCESS_POLICY_ICON  = ICON_PATH_BASE + "access_policy.gif",
                        DATASOURCE_ICON     = ICON_PATH_BASE + "datasource.gif",
                        USERS_ICON          = ICON_PATH_BASE + "users.gif",
                        USER_ICON           = ICON_PATH_BASE + "user.gif",
                        PROFILES_ICON       = ICON_PATH_BASE + "profiles.gif",
                        PROFILE_ICON        = ICON_PATH_BASE + "profile.gif",
                        RELATION_ICON       = ICON_PATH_BASE + "relation.gif";

    /**
     * Indica la navegacion de cada uno de los nodos para el facet del tree2.
     */
    static final String ROOT_NAV         		= "applications_grid",
    					APPLICATION_NAV         = "application_content",
                        DOMAIN_NAV              = "domain_content",
                        ACCESS_POLICY_NAV       = "access_policy_content",
                        DATASOURCE_NAV          = "datasource_content",
                        USERS_NAV               = "standalone_users_content",
                        USER_NAV                = "standalone_user_content",
                        USERS_RELATION_NAV      = "users_content",
                        USER_RELATION_NAV       = "user_relation_content",
                        PROFILES_RELATION_NAV   = "profiles_content",
                        PROFILE_RELATION_NAV    = "profile_relation_content";
}


