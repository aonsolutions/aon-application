package com.code.aon.aio.controller;


import static com.code.aon.ui.audit.controller.IAuditConstants.ACTION_DENIED_CONTROLLER_NAME;
import static com.code.aon.ui.audit.controller.IAuditConstants.APPLICATION_OPTION_CONTROLLER_NAME;
import static com.code.aon.ui.company.controller.ICompanyConstants.COMPANY_CONTROLLER_NAME;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;

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
import com.code.aon.groupware.Note;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.audit.ActionSource;
import com.code.aon.ui.audit.controller.ActionDeniedController;
import com.code.aon.ui.audit.controller.ApplicationOptionController;
import com.code.aon.ui.audit.controller.IAuditConstants;
import com.code.aon.ui.common.ICommonConstants;
import com.code.aon.ui.company.controller.CompanyController;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.groupware.controller.IGroupWareConstants;
import com.code.aon.ui.groupware.controller.NoteController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;


public class DesktopController implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final String HOMEPAGE_DESKTOP = "/homepage.xhtml";
	private static final String DESKTOP_TEMPLATE = "/facelet/homepage/desktop.xhtml";
	private static final String ADMIN_TEMPLATE = "/com/code/aon/ui/admin/facelet/domains/list.xhtml";
	private static final String INIT_ACTION_TEMPLATE = "/facelet/homepage/initAction.xhtml";
	private static final String NEW_COMPANY_TEMPLATE = "/com/code/aon/ui/company/facelet/company/form.xhtml";
	private static final String PASSWORD_EXPIRED_TEMPLATE = "/com/code/aon/ui/config/facelet/changePassword/expiredPasswordContent.xhtml";

	private final static Logger LOGGER = LoggerFactory.getLogger(DesktopController.class);
	
	private DesktopState state;
	
    public DesktopState getState() {
    	if ( state == null ) {
    		state = new DesktopState();
    	}
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
		if ( getState().getHomepagOption()!=null && !getState().isPatchInitAction() ) {
			for( ActionSource as : getState().getHomepagOption().getActionSources() ) {
				as.execute();
			}
			value = getState().getHomepagOption().getViewId();
			resetHomepage();		
		}
		return value;
	}
	
	private boolean hasCompany(CompanyController controller) {
		try {
			return controller.getModel().getRowCount() > 0;
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return false;
	}
	
	public String getViewId() {
		CompanyController controller = (CompanyController) AonUtil.getRegisteredBean(COMPANY_CONTROLLER_NAME);
		if ( !hasCompany(controller) ) {
			controller.onLoad(false);
			controller.setHideHeaderContent(true);
			return NEW_COMPANY_TEMPLATE;
		}
		if ( UserUtils.getInstance().isPasswordExpired() ) {
			controller.setHideHeaderContent(true);
			return PASSWORD_EXPIRED_TEMPLATE;
		}
		return getHomepage();
	}
	
	public String getTemplate() {
		if ( getState().isAdminDomain() ) {
			return ADMIN_TEMPLATE;
		} else if ( getState().getHomepagOption()!=null && getState().isPatchInitAction() ) {
			return INIT_ACTION_TEMPLATE;
		}
		return DESKTOP_TEMPLATE;
	}

	public boolean isSupportEnabled() {
		return getState().isSupportEnabled();
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

	public boolean isPayrollEnabled() {
		if ( AonUtil.getRoleManager().isPayroll() ) {
			ActionDeniedController adc = (ActionDeniedController) AonUtil.getRegisteredBean(ACTION_DENIED_CONTROLLER_NAME);
			return ! adc.isDeniedModule(Module.PAYROLL.getName());
		}
		return false;
	}

	public boolean isFiscalEnabled() {
		if ( AonUtil.getRoleManager().isFiscal() ) {
			ActionDeniedController adc = (ActionDeniedController) AonUtil.getRegisteredBean(ACTION_DENIED_CONTROLLER_NAME);
			return ! adc.isDeniedModule(Module.FISCAL.getName());
		}
		return false;
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
		ApplicationOptionController aoc = (ApplicationOptionController) AonUtil.getRegisteredBean(APPLICATION_OPTION_CONTROLLER_NAME);
		String template = aoc.getTemplate(IAuditConstants.INIT_ACTION_TEMPLATE, 
				IAuditConstants.OPTION_VM, getState().getHomepagOption());
		resetHomepage();
		return template;
	}		

	private void resetHomepage() {
		Map<String, Object> properties = AonUtil.getConfigurationController().getProperties();
		Boolean value = (Boolean) properties.get( ICommonConstants.HIDE_MENU_HOME );
		if ( value != Boolean.TRUE ) {
			getState().setHomepagOption(null);	
		}
	}
	
}