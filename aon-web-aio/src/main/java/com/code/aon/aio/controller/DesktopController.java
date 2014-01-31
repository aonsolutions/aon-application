package com.code.aon.aio.controller;


import static com.code.aon.ui.audit.controller.IAuditConstants.ACTION_DENIED_CONTROLLER_NAME;
import static com.code.aon.ui.audit.controller.IAuditConstants.APPLICATION_OPTION_CONTROLLER_NAME;
import static com.code.aon.ui.company.controller.ICompanyConstants.COMPANY_CONTROLLER_NAME;
import static com.code.aon.ui.config.controller.ConfigConstants.DOMAIN_SWITCHER;

import java.io.IOException;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.ListDataModel;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.aio.NoticeInfo;
import com.code.aon.aio.TaskInfo;
import com.code.aon.audit.enumeration.Module;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.enumeration.AppParam;
import com.code.aon.config.User;
import com.code.aon.config.enumeration.DomainType;
import com.code.aon.config.util.AppParamUtil;
import com.code.aon.dbutils.DatabaseUtil;
import com.code.aon.faces.controller.IRichConstants;
import com.code.aon.faces.controller.SelectedMenuController;
import com.code.aon.groupware.Note;
import com.code.aon.groupware.enumeration.AlarmSource;
import com.code.aon.groupware.enumeration.AlarmStatus;
import com.code.aon.groupware.enumeration.NoticeType;
import com.code.aon.groupware.enumeration.TaskStatus;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.pool.AonConnectionException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.audit.ActionSource;
import com.code.aon.ui.audit.ApplicationCategory;
import com.code.aon.ui.audit.ApplicationOption;
import com.code.aon.ui.audit.IOption;
import com.code.aon.ui.audit.controller.ActionDeniedController;
import com.code.aon.ui.audit.controller.ApplicationOptionController;
import com.code.aon.ui.audit.controller.IAuditConstants;
import com.code.aon.ui.commercial.controller.ICommercialConstants;
import com.code.aon.ui.common.ICommonConstants;
import com.code.aon.ui.common.LocaleElement;
import com.code.aon.ui.common.controller.ConfigurationController;
import com.code.aon.ui.company.controller.CompanyController;
import com.code.aon.ui.config.controller.ConfigConstants;
import com.code.aon.ui.config.controller.DomainSwitcher;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.customer.controller.ICustomerConstants;
import com.code.aon.ui.finance.controller.IFinanceConstants;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.groupware.controller.IGroupWareConstants;
import com.code.aon.ui.groupware.controller.NoteController;
import com.code.aon.ui.product.controller.IItemConstants;
import com.code.aon.ui.purchase.controller.IPurchaseConstants;
import com.code.aon.ui.sales.controller.ISalesConstants;
import com.code.aon.ui.tas.controller.ITasConstants;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.warehouse.controller.IWarehouseConstants;
import com.esferalia.aon.entity.IEntityAlias;


public class DesktopController {

	private static final String PATCH_INIT_ACTION = "aon.patch.initAction";
	
	private static final String HOMEPAGE_DESKTOP = "/homepage.xhtml";
	private static final String DESKTOP_TEMPLATE = "/facelet/homepage/desktop.xhtml";
	private static final String ADMIN_TEMPLATE = "/com/code/aon/ui/admin/facelet/domains/list.xhtml";
	private static final String INIT_ACTION_TEMPLATE = "/facelet/homepage/initAction.xhtml";
	private static final String NEW_COMPANY_TEMPLATE = "/com/code/aon/ui/company/facelet/company/form.xhtml";
	private static final String PASSWORD_EXPIRED_TEMPLATE = "/com/code/aon/ui/config/facelet/changePassword/expiredPasswordContent.xhtml";
	private final static Logger LOGGER = LoggerFactory.getLogger(DesktopController.class);
	
    private ListDataModel recentNoteModel;
    
    private NoticeInfo noticeInfo;
    private TaskInfo taskInfo;
    private IOption homepagOption;
    private boolean adminDomain;
    private boolean supportEnabled;
    private boolean patchInitAction;

    public DesktopController() {
		DomainSwitcher ds = (DomainSwitcher) AonUtil.getRegisteredBean(DOMAIN_SWITCHER);
		this.adminDomain = ds.getType() == DomainType.ADMIN;
		if ( this.adminDomain ) {
			initAdminDomain();
		} else {
	        AuthPrincipal principal = AonUtil.getAuthPrincipal();
	        updateRecentNoteModel(principal);
			initGarage();
			initAcademy();
			initHotel();				
		}
		initUser();
		initSupport();
		initPortal(ds);
    }
    
    public ListDataModel getRecentNoteModel() {
    	return this.recentNoteModel;
    }    
    
	public void onRefresh( ActionEvent event ) {
		LOGGER.info( "Desktop Refresh" );
        AuthPrincipal principal = AonUtil.getAuthPrincipal();
		this.taskInfo = getTaskInfo(principal);
        this.noticeInfo = getNoticeInfo(principal);
		updateRecentNoteModel(principal);
	}
 
    public void onSelectNote(ActionEvent event) throws ManagerBeanException{
        NoteController noteController = (NoteController)FormUtil.getController(IGroupWareConstants.NOTE_CONTROLLER_NAME);
        Note note = (Note)recentNoteModel.getRowData();
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
 	
	private void initGarage() {
		ActionDeniedController adc = (ActionDeniedController) AonUtil.getRegisteredBean(ACTION_DENIED_CONTROLLER_NAME);
		if (! adc.isDeniedModule(Module.GARAGE.getName()) ) {
			AonUtil.setBeanValue(ConfigConstants.SERIES, ITasConstants.SHOW_TAS_DATA, Boolean.TRUE);
			AonUtil.setBeanValue(IFinanceConstants.INCOME_CONTROLLER_NAME, ITasConstants.SHOW_TAS_DATA, Boolean.TRUE);
			AonUtil.setBeanValue(ICommercialConstants.OFFER_CONTROLLER_NAME, ITasConstants.SHOW_TAS_DATA, Boolean.TRUE);
			AonUtil.setBeanValue(IWarehouseConstants.DELIVERY_CONTROLLER_NAME, ITasConstants.SHOW_TAS_DATA, Boolean.TRUE);
			AonUtil.setBeanValue(IWarehouseConstants.INCOME_CONTROLLER_NAME, ITasConstants.SHOW_TAS_DATA, Boolean.TRUE);
			AonUtil.setBeanValue(ISalesConstants.SALES_CONTROLLER_NAME, ITasConstants.SHOW_TAS_DATA, Boolean.TRUE);
			AonUtil.setBeanValue(IPurchaseConstants.PURCHASE_CONTROLLER_NAME, ITasConstants.SHOW_TAS_DATA, Boolean.TRUE);
		}
	}

	private void initAcademy() {
		ActionDeniedController adc = (ActionDeniedController) AonUtil.getRegisteredBean(ACTION_DENIED_CONTROLLER_NAME);
		if (! adc.isDeniedModule(Module.ACADEMY.getName()) ) {
			AonUtil.setBeanValue(ICustomerConstants.CUSTOMER_CONTROLLER_NAME, ICustomerConstants.SHOW_ABSENCE, Boolean.TRUE);
			AonUtil.setBeanValue(ICustomerConstants.CUSTOMER_CONTROLLER_NAME, ICustomerConstants.SHOW_LOAN, Boolean.TRUE);
			AonUtil.setBeanValue(ICustomerConstants.CUSTOMER_CONTROLLER_NAME, ICustomerConstants.SHOW_COURSE, Boolean.TRUE);
			AonUtil.setBeanValue(ICustomerConstants.CUSTOMER_CONTROLLER_NAME, ICustomerConstants.SHOW_PERSON, Boolean.TRUE);
		}
	}

	private void initHotel() {
		ActionDeniedController adc = (ActionDeniedController) AonUtil.getRegisteredBean(ACTION_DENIED_CONTROLLER_NAME);
		if (! adc.isDeniedModule(Module.HOTEL.getName()) ) {
			AonUtil.setBeanValue(IItemConstants.PRODUCT, IItemConstants.SHOW_SALES_PRICE, Boolean.TRUE);
		}
	}
	
	private IOption getOption( String actionName ) {
		SelectedMenuController smc = (SelectedMenuController) AonUtil.getRegisteredBean(IRichConstants.SELECTED_MENU_CONTROLLER_NAME);
		ApplicationOptionController aoc = (ApplicationOptionController) AonUtil.getRegisteredBean(APPLICATION_OPTION_CONTROLLER_NAME);
		ActionDeniedController adc = (ActionDeniedController) AonUtil.getRegisteredBean(ACTION_DENIED_CONTROLLER_NAME);
		ApplicationOption option = aoc.getOptionMap().get(actionName);
		if ( option!=null && option.getViewId()!=null && !adc.isDenied(option) ) {
			smc.setLastMenuAction(option.getGroup().getCategory().getAction());
			return option;	
		}
		ApplicationCategory category = aoc.getCategory(actionName);
		if ( category!=null && category.isRendered() && !adc.isDeniedModule(category.getAlias()) ) {
			smc.setLastMenuAction(category.getAction());
			return category;	
		}
		return null;
	}

	private void initUser() {
		User user = UserUtils.getInstance().getLoggedUser();
		if ( user.getInitAction() != null ) {
			setupInitAction(user.getInitAction());
		}
		ConfigurationController cc = AonUtil.getConfigurationController();
		if (! StringUtils.isEmpty(user.getLocale()) ) {
			for( LocaleElement element : cc.getLocales() ) {
				if ( StringUtils.equals(element.getId(), user.getLocale()) ) {
					element.changeLanguage();
					break;
				}
			}
		}
		if ( user.getPageLimit() != null ) {
			cc.setPageLimit(user.getPageLimit());
		}
		if ( user.getLinesPageLimit() != null ) {
			cc.setPageLimit(user.getLinesPageLimit());
		}
	}
	
	private void initAdminDomain() {
		ActionDeniedController adc = (ActionDeniedController) AonUtil.getRegisteredBean(ACTION_DENIED_CONTROLLER_NAME);
		String[] categories = new String[]{IAuditConstants.CONFIGURATION_CATEGORY};
		String[] groups = new String[]{IAuditConstants.GROUP_CONFIG_SECURITY, IAuditConstants.GROUP_CONFIG_COMPANY};
		adc.enableOnly(categories, groups, IAuditConstants.MAIL_ACCOUNT_ACTION, IAuditConstants.SIGNATURE_ACTION);
	}
	
	private String getHomepage() {
		String value = HOMEPAGE_DESKTOP;
		if ( this.homepagOption!=null && !patchInitAction ) {
			for( ActionSource as : this.homepagOption.getActionSources() ) {
				as.execute();
			}
			value = this.homepagOption.getViewId();
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
		if ( adminDomain ) {
			return ADMIN_TEMPLATE;
		} else if ( homepagOption!=null && patchInitAction ) {
			return INIT_ACTION_TEMPLATE;
		}
		return DESKTOP_TEMPLATE;
	}

	private void initSupport() {
		String value = AppParamUtil.getValue(AppParam.AON_SUPPORT_ENABLED);
		if ( value != null ) {
			Date date = new Date( NumberUtils.toLong(value) );
			if ( DateUtils.isSameDay(date, new Date()) ) {
				setSupportEnabled(true);
			}
		}
		if (! isSupportEnabled() ) {
			AppParamUtil.removeParameter(AppParam.AON_SUPPORT_ENABLED);
		}
	}
	
	public boolean isSupportEnabled() {
		return supportEnabled;
	}

	public void setSupportEnabled(boolean supportEnabled) {
		this.supportEnabled = supportEnabled;
	}

	public void onEnableSupport(ActionEvent event) {
		setSupportEnabled(true);
		String value = String.valueOf(new Date().getTime());
		AppParamUtil.insertParameter(AppParam.AON_SUPPORT_ENABLED, value);
	}

	public void onDisableSupport(ActionEvent event) {
		setSupportEnabled(false);
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

	// ********** TASK RELATED METHODS.
    public TaskInfo getTaskInfo() {
    	if (this.taskInfo == null) {
    		AuthPrincipal principal = AonUtil.getAuthPrincipal();
    		this.taskInfo = getTaskInfo(principal);		
    	}
    	return this.taskInfo;
    }

    private TaskInfo getTaskInfo (AuthPrincipal principal) {
		DomainSwitcher ds = (DomainSwitcher) AonUtil.getRegisteredBean(DOMAIN_SWITCHER);
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		try {
			Integer userId = principal.getUserId();
			conn = DatabaseUtil.getConnection(AonUtil.getDomainName());
			StringWriter stmt = new StringWriter();
			stmt.append(" SELECT 0,t.status,count(*),IF(t.due_date < DATE(NOW()),1,0)"); 
			stmt.append( "  FROM task_holder th,task t");
			stmt.append( "  WHERE th.user_id = ?");
			if ( ds.isChildDomain() ) {
				stmt.append( " AND ");
				stmt.append( DomainManager.getSQLWhereClause("th.domain"));
			}
			stmt.append( "  AND th.registry = t.task_holder");
			stmt.append( "  GROUP BY t.status,IF(t.due_date < DATE(NOW()),1,0)");
			stmt.append( " UNION");
			stmt.append(" SELECT 1,t.status,count(*),IF(t.due_date < DATE(NOW()),1,0)"); 
			stmt.append( "  FROM task_holder th,task t, task_holder_workgroup thg");
			stmt.append( "  WHERE th.user_id = ?");
			if ( ds.isChildDomain() ) {
				stmt.append( " AND ");
				stmt.append( DomainManager.getSQLWhereClause("th.domain"));
			}
			stmt.append( "  AND thg.task_holder = th.registry");
			stmt.append( "  AND t.workgroup = thg.workgroup ");
			stmt.append( "  AND t.task_holder is null");
			stmt.append( "  GROUP BY t.status,IF(t.due_date < DATE(NOW()),1,0)");
			ps = conn.prepareStatement(stmt.toString(),ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			ps.setInt(1,userId);
			ps.setInt(2,userId);
			rs = ps.executeQuery();
			TaskInfo taskInfo = new TaskInfo();
			while (rs.next()) {
				boolean userTask = rs.getInt(1) == 0;
				TaskStatus status = TaskStatus.values()[rs.getInt(2)];
				int count = rs.getInt(3);
				boolean expired = rs.getInt(4) == 1;
				taskInfo.add(userTask, status, count, expired );
			}
			return taskInfo;
		} catch (SQLException e) {
			LOGGER.error(e.getMessage(), e);
			return new TaskInfo();
		} catch (AonConnectionException e) {
			LOGGER.error(e.getMessage(), e);
			return new TaskInfo();
		} finally {
			DatabaseUtil.closeQuietly(rs);
			DatabaseUtil.closeQuietly(ps);
			DatabaseUtil.closeQuietly(conn);
		}
    }
 
	// ********** NOTICE RELATED METHODS.
    public NoticeInfo getNoticeInfo() {
    	if (this.noticeInfo == null) {
    		AuthPrincipal principal = AonUtil.getAuthPrincipal();
    		this.noticeInfo = getNoticeInfo(principal);		
    	}
    	return this.noticeInfo;
    }

	private NoticeInfo getNoticeInfo( AuthPrincipal principal ) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		try {
	        String select = "SELECT nt .type, count(*) " 
                    +" FROM notice nt, alarm al" 
                    +" where nt.id = al.source_id " 
                    +" and " + DomainManager.getSQLWhereClause("al.domain")  
                    +" and al.source = ? "  
                    +" and al.status = ? "  
                    +" and al.user_id = ? "   
                    +" and al.alarm_date < NOW() "
                    +" group by nt.type ";
			conn = DatabaseUtil.getConnection(AonUtil.getDomainName());
			ps = conn.prepareStatement(select,ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
	        ps.setInt(1, AlarmSource.NOTICE.ordinal());
	        ps.setInt(2, AlarmStatus.PENDING.ordinal());
	        ps.setInt(3, principal.getUserId());
			rs = ps.executeQuery();
			NoticeInfo noticeInfo = new NoticeInfo();
			while (rs.next()) {
				NoticeType noticeType = NoticeType.values()[rs.getInt(1)];
				int count = rs.getInt(2);
				noticeInfo.add(noticeType, count);
			}
			return noticeInfo;
		} catch (SQLException e) {
			LOGGER.error(e.getMessage(), e);
			return new NoticeInfo();
		} catch (AonConnectionException e) {
			LOGGER.error(e.getMessage(), e);
			return new NoticeInfo();
		} finally {
			DatabaseUtil.closeQuietly(rs);
			DatabaseUtil.closeQuietly(ps);
			DatabaseUtil.closeQuietly(conn);
		}
    }
	
	// Note related methods
	private void updateRecentNoteModel( AuthPrincipal principal )  {
		try {
			IManagerBean noteBean = BeanManager.getManagerBean(Note.class);
			Criteria criteria = new Criteria();    	criteria.addEqualExpression(noteBean.getFieldName(IEntityAlias.NOTE_OWNER_ID), principal.getUserId());
			criteria.addOrder(noteBean.getFieldName(IEntityAlias.NOTE_DATE), false);
			this.recentNoteModel = new ListDataModel(noteBean.getList(criteria));
		} catch (ManagerBeanException e) {
			this.recentNoteModel = new ListDataModel();
			LOGGER.error( e.getMessage(), e);
		}
    }
	
	private void setupInitAction( String action ) {
		if (! StringUtils.isEmpty(action) ) {
			this.homepagOption = getOption(action);	
			FacesContext ctx = FacesContext.getCurrentInstance();
			String value = ctx.getExternalContext().getInitParameter(PATCH_INIT_ACTION);
			this.patchInitAction = StringUtils.equals(value, Boolean.TRUE.toString());
		}
	}
	
	public String getInitActionTemplate() throws IOException {
		ApplicationOptionController aoc = (ApplicationOptionController) AonUtil.getRegisteredBean(APPLICATION_OPTION_CONTROLLER_NAME);
		String template = aoc.getTemplate(IAuditConstants.INIT_ACTION_TEMPLATE, 
				IAuditConstants.OPTION_VM, this.homepagOption);
		resetHomepage();
		return template;
	}		

	private void initPortal( DomainSwitcher ds ) {
		if ( ds.isChildDomain() && this.homepagOption!=null ) {
			ActionDeniedController adc = (ActionDeniedController) AonUtil.getRegisteredBean(ACTION_DENIED_CONTROLLER_NAME);
			List<Module> modules = adc.getVisibleModules();
			if ( modules.size()==1 && modules.get(0)==Module.PAYROLL_PORTAL ) {
				AonUtil.setBeanValue(IGroupWareConstants.ALARM_CONTROLLER_NAME, IGroupWareConstants.SHOW_PENDING, Boolean.FALSE);
				AonUtil.setBeanValue(IGroupWareConstants.ALARM_CONTROLLER_NAME, IGroupWareConstants.SHOW_LIST, Boolean.FALSE);
				Map<String, Object> properties = AonUtil.getConfigurationController().getProperties();
				properties.put( ICommonConstants.HIDE_MENU_HOME, Boolean.TRUE );
				properties.put( ICommonConstants.HIDE_MENU_FAVORITE, Boolean.TRUE );
				properties.put( ICommonConstants.HIDE_MENU_CHOOSE_LANGUAGE, Boolean.TRUE );
				properties.put( ICommonConstants.HIDE_MENU_ADVANCED_MODE, Boolean.TRUE );
				properties.put( ICommonConstants.HIDE_MENU_WEB_MAP, Boolean.TRUE );
				properties.put( ICommonConstants.HIDE_MENU_HELP, Boolean.TRUE );
				properties.put( ICommonConstants.HIDE_MENU_ABOUT, Boolean.TRUE );
			}
		}
	}

	private void resetHomepage() {
		Map<String, Object> properties = AonUtil.getConfigurationController().getProperties();
		Boolean value = (Boolean) properties.get( ICommonConstants.HIDE_MENU_HOME );
		if ( value != Boolean.TRUE ) {
			this.homepagOption = null;	
		}
	}
	
}