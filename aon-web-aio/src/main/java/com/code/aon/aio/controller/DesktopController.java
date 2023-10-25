package com.code.aon.aio.controller;


import static com.code.aon.ui.audit.controller.IAuditConstants.ACTION_DENIED_CONTROLLER_NAME;
import static com.code.aon.ui.company.controller.ICompanyConstants.COMPANY_CONTROLLER_NAME;
import static com.code.aon.ui.config.controller.ConfigConstants.DOMAIN_SWITCHER;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.aio.DesktopState;
import com.code.aon.aio.NoticeInfo;
import com.code.aon.aio.TaskInfo;
import com.code.aon.audit.enumeration.Module;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.AppParam;
import com.code.aon.config.util.AppParamUtil;
import com.code.aon.faces.controller.IRichConstants;
import com.code.aon.faces.controller.SelectedMenuController;
import com.code.aon.groupware.Note;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.audit.ActionSource;
import com.code.aon.ui.audit.ApplicationCategory;
import com.code.aon.ui.audit.ApplicationOption;
import com.code.aon.ui.audit.IOption;
import com.code.aon.ui.audit.controller.ActionDeniedController;
import com.code.aon.ui.audit.controller.ApplicationOptionController;
import com.code.aon.ui.audit.controller.IAuditConstants;
import com.code.aon.ui.common.ICommonConstants;
import com.code.aon.ui.company.controller.CompanyController;
import com.code.aon.ui.config.controller.DomainSwitcher;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.groupware.controller.IGroupWareConstants;
import com.code.aon.ui.groupware.controller.NoteController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;


public class DesktopController implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final String HOMEPAGE_DESKTOP = "/homepage.xhtml";
//	private static final String PORTAL_TEMPLATE = "/facelet/portal/portal.xhtml";
//	private static final String PORTAL_NEW_SUITE_TEMPLATE = "/facelet/portal/aonDesktop.xhtml";
	private static final String DESKTOP_TEMPLATE = "/facelet/homepage/desktop.xhtml";
	private static final String ADMIN_TEMPLATE = "/com/code/aon/ui/admin/facelet/domains/list.xhtml";
	private static final String INIT_ACTION_TEMPLATE = "/facelet/homepage/initAction.xhtml";
	private static final String PASSWORD_EXPIRED_TEMPLATE = "/com/code/aon/ui/config/facelet/changePassword/expiredPasswordContent.xhtml";
	
	private final static Logger LOGGER = LoggerFactory.getLogger(DesktopController.class);
	
	public final static String CONTROLLER_NAME = "desktop";
	
	private DesktopState state;
	private Boolean logEnabled;
	
	
    public DesktopState getState() {
    	if ( state == null ) {
    		state = new DesktopState();
    	}
    	checkInitAction();
    	return state;
	}
    
	public DataModel getRecentNoteModel() {
    	return getState().getRecentNoteModel();
    }    
    
	public void onRefresh( ActionEvent event ) {
		LOGGER.info( "Desktop Refresh" );
		getState().setTaskInfo(null);
		getState().setNoticeInfo(null);
		getState().setRecentNoteModel(null);
	}
	
 
    public void onSelectNote(ActionEvent event) throws ManagerBeanException{
        NoteController noteController = (NoteController)FormUtil.getController(IGroupWareConstants.NOTE_CONTROLLER_NAME);
        Note note = (Note) getRecentNoteModel().getRowData();
        Criteria criteria = new Criteria();
        try {
            criteria.addEqualExpression(noteController.getFieldName(IEntityAlias.NOTE_ID), note.getId());
            noteController.setCriteria(criteria);
            noteController.onSearch(null);
            noteController.getModel().setRowIndex(0);
            noteController.onSelect(null);
        } catch (ManagerBeanException e) {
            throw new ManagerBeanException("Error obtaining note with id=" + note.getId(), e);
        }
    }

	private String getHomepage() {
		String value = HOMEPAGE_DESKTOP;
		if ( getState().getInitOption()!=null && !getState().isPatchInitAction() ) {
			for( ActionSource as : getState().getInitOption().getActionSources() ) {
				as.execute();
			}
			value = getState().getInitOption().getViewId();
			resetHomepage();		
		}
		return value;
	}
	
	public String getViewId() {
		if ( UserUtils.getInstance().isPasswordExpired() ) {
			CompanyController controller = (CompanyController) AonUtil.getRegisteredBean(COMPANY_CONTROLLER_NAME);
			controller.setHideHeaderContent(true);
			return PASSWORD_EXPIRED_TEMPLATE;
		}
		return getHomepage();
	}
	
	public boolean isPortalActive() {
		return  getState() != null && getState().isPortalActive() && UserUtils.getInstance().isNewAONTheme();
	}
	
	public boolean isPortalNewSuiteActive() {
		return  getState() != null && getState().isPortalActive() && UserUtils.getInstance().isAonNewSuite();
	}
	
	public String getTemplate() {
		String template = getTemplateParameter();
		if ( template != null ) {
			return template;
		} else if ( getState().getInitOption()!=null && getState().isPatchInitAction() ) {
			return INIT_ACTION_TEMPLATE;
		} else if ( getState().isAdminDomain() ) {
			return ADMIN_TEMPLATE;
		} else if ( isPortalActive() || isPortalNewSuiteActive()) {
			return DESKTOP_TEMPLATE;
		} 
		return DESKTOP_TEMPLATE;
	}

	private String getTemplateParameter() {
		FacesContext ctx = FacesContext.getCurrentInstance();
		Map<String,String> params = 
	    ctx.getExternalContext().getRequestParameterMap();
		
	    return params.get("template");
	}

	public void onEnableSupport(ActionEvent event) {
		getState().setSupportEnabled(true);
		String value = String.valueOf(new Date().getTime());
		AppParamUtil.insertParameter(AppParam.AON_SUPPORT_ENABLED, value);
	}

	public void onDisableSupport(ActionEvent event) {
		getState().setSupportEnabled(false);
		AppParamUtil.removeParameter(AppParam.AON_SUPPORT_ENABLED);
	}

	public boolean isGroupwareEnabled() {
		if ( AonUtil.getRoleManager().isFiscal() ) {
			ActionDeniedController adc = (ActionDeniedController) AonUtil.getRegisteredBean(ACTION_DENIED_CONTROLLER_NAME);
			return ! adc.isDeniedModule(Module.GROUPWARE.getName());
		}
		return false;
	}

    public TaskInfo getTaskInfo() {
    	return getState().getTaskInfo();
    }

    public NoticeInfo getNoticeInfo() {
    	return getState().getNoticeInfo();
    }
	
	public String getInitActionTemplate() throws IOException {
		ApplicationOptionController aoc = ApplicationOptionController.getInstance();
		String template = aoc.getTemplate(IAuditConstants.INIT_ACTION_TEMPLATE, 
				IAuditConstants.OPTION_VM, getState().getInitOption());
		resetHomepage();
		return template;
	}		

	private void resetHomepage() {
		Map<String, Object> properties = AonUtil.getConfigurationController().getProperties();
		Boolean value = (Boolean) properties.get( ICommonConstants.HIDE_MENU_HOME );
		if ( value != Boolean.TRUE ) {
			getState().setInitOption(null);	
		}
	}

	public boolean isLogEnabled() {
		if ( logEnabled == null ) {
			this.logEnabled = AppParamUtil.getValueAsBoolean(AppParam.AON_LOG_ENABLED);
		}
		return logEnabled;
	}

	public boolean isShowGraphicsPortlet() {
		if (! getState().isShowFavorites() ) {
			DomainSwitcher ds = (DomainSwitcher) AonUtil.getRegisteredBean(DOMAIN_SWITCHER);
			if ( ds.isChildDomain() ) {
				if ( getState().isFiscalEnabled() ||
					getState().isPayrollEnabled() ||
					getState().isDocumentalEnabled()) {
					return true;
				}
				if ( getState().isFiscalInfoVisibleForPortal() ||
					getState().isPayrollInfoVisibleForPortal() ||
					getState().isDocumentalInfoVisibleForPortal() ) {
					return true;
				}
			}			
		}
		return false;
	}
	
	// --------------------------------------------------------------------
	
	private enum MenuOption {
	    HELP,
	    FISCAL,
	    PAYROLL,
	    TREASURY,
	    MANAGEMENT,
	    ENTERPRISE,
	    ACCOUNTING,
	    CONFIGURATION,
	    TIME_COONTROL
	    ;
	}
	
	private MenuOption menuOptionSelected = MenuOption.ENTERPRISE;
	
	
	public boolean isHelpSelected() {
	    return menuOptionSelected == MenuOption.HELP;
	}

	public boolean isFiscalSelected() {
	    return menuOptionSelected == MenuOption.FISCAL;
	}

	public boolean isTreasurySelected() {
	    return menuOptionSelected == MenuOption.TREASURY;
	}

	public boolean isPayrollSelected() {
	    return menuOptionSelected == MenuOption.PAYROLL;
	}

	public boolean isManagementSelected() {
	    return menuOptionSelected == MenuOption.MANAGEMENT;
	}

	public boolean isEnterpriseSelected() {
	    return menuOptionSelected == MenuOption.ENTERPRISE;
	}

	public boolean isAccountingSelected() {
	    return menuOptionSelected == MenuOption.ACCOUNTING;
	}

	public boolean isConfigurationSelected() {
	    return menuOptionSelected == MenuOption.CONFIGURATION;
	}

	public boolean isTimeControlSelected() {
	    return menuOptionSelected == MenuOption.TIME_COONTROL;
	}

	public void onSelectHelp(ActionEvent event) {
	    menuOptionSelected = MenuOption.HELP;
	}

	public void onSelectFiscal(ActionEvent event) {
	    menuOptionSelected = MenuOption.FISCAL;
	}

	public void onSelectTreasury(ActionEvent event) {
	    menuOptionSelected = MenuOption.TREASURY;
	}

	public void onSelectPayroll(ActionEvent event) {
	    menuOptionSelected = MenuOption.PAYROLL;
	}

	public void onSelectManagement(ActionEvent event) {
	    menuOptionSelected = MenuOption.MANAGEMENT;
	}

	public void onSelectEnterprise(ActionEvent event) {
	    menuOptionSelected = MenuOption.ENTERPRISE;
	}
	
	public void onSelectAccounting(ActionEvent event) {
	    menuOptionSelected = MenuOption.ACCOUNTING;
	}
	
	public void onSelectConfiguration(ActionEvent event) {
	    menuOptionSelected = MenuOption.CONFIGURATION;
	}
	
	public void onSelectTimeControl(ActionEvent event) {
	    menuOptionSelected = MenuOption.TIME_COONTROL;
	}

	private void checkInitAction() {
		AuthPrincipal principal = AonUtil.getAuthPrincipal();
		if(principal.getInitAction() != null) {
			state.setInitOption(getOptionWithoutDenied(principal.getInitAction()));	
		}
	}
	    
	private IOption getOptionWithoutDenied( String actionName ) {
		SelectedMenuController smc = (SelectedMenuController) AonUtil.getRegisteredBean(IRichConstants.SELECTED_MENU_CONTROLLER_NAME);
		ApplicationOptionController aoc = ApplicationOptionController.getInstance();
		ApplicationOption option = aoc.getOptionMap().get(actionName);
		if ( option!=null && option.getViewId()!=null) {
			smc.setLastMenuAction(option.getGroup().getCategory().getAction());
			return option;	
		}
		ApplicationCategory category = aoc.getCategory(actionName);
		if ( category!=null && category.isRendered()) {
			smc.setLastMenuAction(category.getAction());
			return category;	
		}
		return null;
	}
}