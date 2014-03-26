package com.code.aon.ui.audit.controller;

import static com.code.aon.ui.audit.controller.IAuditConstants.APPLICATION_OPTION_CONTROLLER_NAME;
import static com.code.aon.ui.common.ICommonMessages.ADMIN_ADVANCED_MODE;
import static com.code.aon.ui.common.ICommonMessages.DOMAIN_CHANGE;
import static com.code.aon.ui.common.ICommonMessages.FAVORITES_MANAGEMENT;
import static com.code.aon.ui.common.ICommonMessages.HOME;
import static com.code.aon.ui.common.ICommonMessages.MENU;
import static com.code.aon.ui.common.ICommonMessages.TOOLBAR_FORM;
import static com.code.aon.ui.common.ICommonMessages.TOOLBAR_LIST;
import static com.code.aon.ui.common.ICommonMessages.TOOLBAR_SEARCH;
import static com.code.aon.ui.common.ICommonMessages.WEB_MAP;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.audit.ActionEntry;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.audit.ApplicationCategory;
import com.code.aon.ui.audit.ApplicationOption;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;

public class ActionEntryController extends LinesController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final String[] MANAGED_BEAN_SUFFIXES = new String[]{
		IController.FORM_SUFFIX, IController.LIST_SUFFIX, IController.SEARCH_SUFFIX
	};
	
	private static String getMode( String suffix ) {
		String mode = null;
		if ( IController.FORM_SUFFIX.equals(suffix) ) {
			mode = AonUtil.getMessage(TOOLBAR_FORM);
		} else if ( IController.LIST_SUFFIX.equals(suffix) ) {
			mode = AonUtil.getMessage(TOOLBAR_LIST);
		} else if ( IController.SEARCH_SUFFIX.equals(suffix) ) {
			mode = AonUtil.getMessage(TOOLBAR_SEARCH);
		}
		return mode;
	}
	
	private static String getManagedBeanSuffix( String action ) {
		String suffix = StringUtils.substringBeforeLast(action, "-");
		suffix = StringUtils.substringAfterLast(suffix, "_");
		if (! StringUtils.isEmpty(suffix) ) {
			suffix = "_" + suffix;
			if ( ArrayUtils.contains(MANAGED_BEAN_SUFFIXES, suffix) ) {
				return suffix;
			}
		}
		return null;
	}
	
	private static String getOption( ApplicationOptionController aoc, String action ) {
		String suffix = getManagedBeanSuffix(action);
		ApplicationOption option = aoc.getOptionMap().get(action);
		if (option == null) {
			for( String mbSuffix : MANAGED_BEAN_SUFFIXES ) {
				String findAction = StringUtils.substringBeforeLast(action, "_") + mbSuffix;
				option = aoc.getOptionMap().get(findAction);
				if ( option != null) {
					break;
				}
			}
		}
		if ( option != null ) {
			StringBuffer sb = new StringBuffer();
			sb.append( option.getDescription() );
			if ( suffix != null ) {
				sb.append( " [" ).append(getMode(suffix)).append( "]");
			}
			sb.append( " (" ).append(option.getGroup().getCategory().getDescription()).append( ")");
			return sb.toString();	
		}
		return null;
	}
	
	private static String getActionLabel( String action ) {
		if ( "start".equals(action) ) {
			return AonUtil.getMessage(DOMAIN_CHANGE);
		} else if ( "home".equals(action) ) {
			return AonUtil.getMessage(HOME);
		} else if ( "advancedMode".equals(action) ) {			
			return AonUtil.getMessage(ADMIN_ADVANCED_MODE);
		} else if ( "actionFavorite".equals(action) ) {			
			return AonUtil.getMessage(FAVORITES_MANAGEMENT);
		} else if ( "webMap".equals(action) ) {			
			return AonUtil.getMessage(WEB_MAP);
		}
		return null;
	}

	public static String getOptionDescription( String action ) {
		String description = action;
		ApplicationOptionController aoc = (ApplicationOptionController) AonUtil.getRegisteredBean(APPLICATION_OPTION_CONTROLLER_NAME);
		ApplicationCategory category = aoc.getCategory(action);
		if ( category != null ) {
			description = category.getDescription() + " (" + AonUtil.getMessage(MENU) + ")";
		} else {
			String option = getOption(aoc, action);
			if ( option == null ) {
				option = getActionLabel(action);
			}
			if ( option != null ) {			
				description = option;
			}
		}		
		return description;
	}
	
	public String getCurrentName() throws ManagerBeanException {
		String action = null;
		if ( getModel().isRowAvailable() ) {
			ActionEntry entry = (ActionEntry) getModel().getRowData();
			action = getOptionDescription(entry.getAction().getName());
		}
		return action;
	}	
	
}
