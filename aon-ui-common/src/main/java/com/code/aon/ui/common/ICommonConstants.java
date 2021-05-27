package com.code.aon.ui.common;


/**
 * @author ecastellano
 *
 */
public interface ICommonConstants {
	
	String MUNICIPALITIES_BUNDLE_NAME = "com.code.aon.common.i18n.municipalities";

	// ************************************************************
	// BEAN
	// ************************************************************

	/** The aonConfiguration controller. */
	String CONFIGURATION_CONTROLLER_NAME = "aonConfiguration";
	
	/** The loggedUser controller. */
	String LOGGED_USER_CONTROLLER_NAME = "loggedUser";
	
	/** The domainResolver controller. */
	String DOMAIN_RESOLVER_CONTROLLER_NAME = "domainResolver";

	/** The aonRole controller. */
	String AON_ROLE_CONTROLLER_NAME = "aonRole";

	/** The customize controller. */
	String CUSTOMIZE_CONTROLLER_NAME = "customize";

	/** The bean config controller. */
	String BEAN_CONFIG_CONTROLLER_NAME = "beanConfig";
		
	// ************************************************************
	// CONFIGURATION
	// ************************************************************
	
	/** The Skip LDAP property. */
	String SKIP_LDAP = "com.code.aon.skipLdap";	
	
	/** The hide header links. */
	String HIDE_HEADER_LINKS = "hideHeaderLinks";

	/** The hide menu bar. */
	String HIDE_MENU_BAR = "hideMenuBar";
	
	String HIDE_MENU_EMAIL = "hideMenuEmail";
	
	String HIDE_MENU_HOME = "hideMenuHome";
	
	String HIDE_MENU_FAVORITE = "hideMenuFavorite";
	
	String HIDE_MENU_ADVANCED_MODE = "hideMenuAdvancedMode";
	
	String HIDE_MENU_CHOOSE_LANGUAGE = "hideMenuChooseLanguage";
	
	String HIDE_MENU_WEB_MAP = "hideMenuWebMap";
	
	String HIDE_MENU_HELP = "hideMenuHelp";
	
	String HIDE_MENU_ABOUT = "hideMenuAbout";

	/** Logout action listener. */
	String ON_LOGOUT = "onLogout";
	
	String HOME_ACTION = "home";
	
	String FAVICON_NAME = "favicon.svg";

	String LOGIN_LOGO_NAME = "aon-login-logo";

	String HEADER_LOGO_NAME = "aon-header-logo";
	
	String TOOLBAR_LOGO_NAME = "aon-toolbar-logo";
	
	String STATUS_START_NAME = "aon-status-start";

	String STATUS_STOP_NAME = "aon-status-stop";

	String STATUS_FAILED_NAME = "aon-status-failed";

	String AON_AIO_APPLICATION = "aon-aio";
	
	String PRINCIPAL_SESSION_PROPERTY = "com.code.aon.jaas.session";
	
}