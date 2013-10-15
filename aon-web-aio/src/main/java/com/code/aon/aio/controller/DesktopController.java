package com.code.aon.aio.controller;


import static com.code.aon.common.enumeration.AppParam.AON_SUPPORT_ENABLED;
import static com.code.aon.faces.controller.IRichConstants.SELECTED_MENU_CONTROLLER_NAME;
import static com.code.aon.ui.audit.controller.IAuditConstants.ACTION_DENIED_CONTROLLER_NAME;
import static com.code.aon.ui.audit.controller.IAuditConstants.APPLICATION_OPTION_CONTROLLER_NAME;
import static com.code.aon.ui.audit.controller.IAuditConstants.CONFIGURATION_CATEGORY;
import static com.code.aon.ui.audit.controller.IAuditConstants.GROUP_CONFIG_COMPANY;
import static com.code.aon.ui.audit.controller.IAuditConstants.GROUP_CONFIG_SECURITY;
import static com.code.aon.ui.audit.controller.IAuditConstants.MAIL_ACCOUNT_ACTION;
import static com.code.aon.ui.audit.controller.IAuditConstants.SIGNATURE_ACTION;
import static com.code.aon.ui.company.controller.ICompanyConstants.COMPANY_CONTROLLER_NAME;
import static com.code.aon.ui.config.controller.ConfigConstants.DOMAIN_SWITCHER;
import static com.code.aon.ui.customer.controller.ICustomerConstants.SHOW_ABSENCE;
import static com.code.aon.ui.customer.controller.ICustomerConstants.SHOW_COURSE;
import static com.code.aon.ui.customer.controller.ICustomerConstants.SHOW_LOAN;
import static com.code.aon.ui.customer.controller.ICustomerConstants.SHOW_PERSON;
import static com.code.aon.ui.groupware.controller.IGroupWareConstants.NOTE_CONTROLLER_NAME;
import static com.code.aon.ui.product.controller.IItemConstants.SHOW_SALES_PRICE;
import static com.code.aon.ui.tas.controller.ITasConstants.SHOW_TAS_DATA;

import java.io.InputStream;
import java.io.StringWriter;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

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
import com.code.aon.config.User;
import com.code.aon.config.enumeration.DomainType;
import com.code.aon.config.util.AppParamUtil;
import com.code.aon.dbutils.DatabaseUtil;
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
import com.code.aon.ui.commercial.controller.ICommercialConstants;
import com.code.aon.ui.common.LocaleElement;
import com.code.aon.ui.common.controller.ConfigurationController;
import com.code.aon.ui.company.controller.CompanyController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.config.controller.ConfigConstants;
import com.code.aon.ui.config.controller.DomainSwitcher;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.customer.controller.ICustomerConstants;
import com.code.aon.ui.finance.controller.IFinanceConstants;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.groupware.controller.NoteController;
import com.code.aon.ui.product.controller.IItemConstants;
import com.code.aon.ui.purchase.controller.IPurchaseConstants;
import com.code.aon.ui.sales.controller.ISalesConstants;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.warehouse.controller.IWarehouseConstants;
import com.esferalia.aon.entity.IEntityAlias;


public class DesktopController {

	private static final String HOMEPAGE_DESKTOP = "/homepage.xhtml";
	private static final String DESKTOP_TEMPLATE = "/facelet/homepage/desktop.xhtml";
	private static final String ADMIN_TEMPLATE = "/com/code/aon/ui/admin/facelet/domains/list.xhtml";
	private static final String NEW_COMPANY_TEMPLATE = "/com/code/aon/ui/company/facelet/company/form.xhtml";
	private static final String PASSWORD_EXPIRED_TEMPLATE = "/com/code/aon/ui/config/facelet/changePassword/expiredPasswordContent.xhtml";
	private final static Logger LOGGER = LoggerFactory.getLogger(DesktopController.class);
	private static final int UPDATE_CONNECTION_TIMEOUT = 5000;
	
    private ListDataModel recentNoteModel;
    
    private NoticeInfo noticeInfo;
    private TaskInfo taskInfo;
    private boolean checkUpdateURL = true;
    private IOption homepagOption;
    private boolean adminDomain;
    private boolean supportEnabled;

    public DesktopController() throws MalformedURLException {
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
 
    public boolean isUpdatesAvailable() {
    	boolean available = false;
    	if ( checkUpdateURL ) {
    		try {
				URL url = new URL( getUpdateURL() + "/hasupdate.rpy" );
				URLConnection connection = url.openConnection();
				connection.setConnectTimeout(UPDATE_CONNECTION_TIMEOUT);
				InputStream in = connection.getInputStream();
				char result = (char) in.read();
				in.close();
				available = (result == '1');
    		} catch (ConnectException e) {
    			checkUpdateURL = false;
    			LOGGER.debug( "Timeout getting updates available", e);
			} catch (Throwable e) {
				checkUpdateURL = false;
				LOGGER.error( "Error getting updates available", e);
			}
    	}
    	return available;
    }	
 
	public String getUpdateApplicationURL() {
		return getUpdateURL() + "/update.rpy";
	}    
    
	private String getUpdateURL() {
    	String server = null;
		try {
			server = InetAddress.getLocalHost().getCanonicalHostName();
		} catch (UnknownHostException e) {
			try {
				server = InetAddress.getLocalHost().getHostAddress();
			} catch (UnknownHostException e1) {
				LOGGER.error( "Error getting server address", e1);
			}
		}
    	return "http://" + server + ":7654";		
	}    

    public void onSelectNote(ActionEvent event) throws ManagerBeanException{
        NoteController noteController = (NoteController)FormUtil.getController(NOTE_CONTROLLER_NAME);
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
    
	public boolean isHideHeaderContent() {
		CompanyController companyController = (CompanyController) AonUtil.getRegisteredBean(ICompanyConstants.COMPANY_CONTROLLER_NAME);
		boolean hide = companyController.isHideHeaderContent();
		if (! hide) {
			if ( UserUtils.getInstance().isPasswordExpired() ) {
				companyController.setHideHeaderContent(true);
				return true;
			}			
		}
		return hide;			
	}
 
	private void initGarage() {
		ActionDeniedController adc = (ActionDeniedController) AonUtil.getRegisteredBean(ACTION_DENIED_CONTROLLER_NAME);
		if (! adc.isDeniedModule(Module.GARAGE.getName()) ) {
			AonUtil.setBeanValue(ConfigConstants.SERIES, SHOW_TAS_DATA, Boolean.TRUE);
			AonUtil.setBeanValue(IFinanceConstants.INCOME_CONTROLLER_NAME, SHOW_TAS_DATA, Boolean.TRUE);
			AonUtil.setBeanValue(ICommercialConstants.OFFER_CONTROLLER_NAME, SHOW_TAS_DATA, Boolean.TRUE);
			AonUtil.setBeanValue(IWarehouseConstants.DELIVERY_CONTROLLER_NAME, SHOW_TAS_DATA, Boolean.TRUE);
			AonUtil.setBeanValue(IWarehouseConstants.INCOME_CONTROLLER_NAME, SHOW_TAS_DATA, Boolean.TRUE);
			AonUtil.setBeanValue(ISalesConstants.SALES_CONTROLLER_NAME, SHOW_TAS_DATA, Boolean.TRUE);
			AonUtil.setBeanValue(IPurchaseConstants.PURCHASE_CONTROLLER_NAME, SHOW_TAS_DATA, Boolean.TRUE);
		}
	}

	private void initAcademy() {
		ActionDeniedController adc = (ActionDeniedController) AonUtil.getRegisteredBean(ACTION_DENIED_CONTROLLER_NAME);
		if (! adc.isDeniedModule(Module.ACADEMY.getName()) ) {
			AonUtil.setBeanValue(ICustomerConstants.CUSTOMER_CONTROLLER_NAME, SHOW_ABSENCE, Boolean.TRUE);
			AonUtil.setBeanValue(ICustomerConstants.CUSTOMER_CONTROLLER_NAME, SHOW_LOAN, Boolean.TRUE);
			AonUtil.setBeanValue(ICustomerConstants.CUSTOMER_CONTROLLER_NAME, SHOW_COURSE, Boolean.TRUE);
			AonUtil.setBeanValue(ICustomerConstants.CUSTOMER_CONTROLLER_NAME, SHOW_PERSON, Boolean.TRUE);
		}
	}

	private void initHotel() {
		ActionDeniedController adc = (ActionDeniedController) AonUtil.getRegisteredBean(ACTION_DENIED_CONTROLLER_NAME);
		if (! adc.isDeniedModule(Module.HOTEL.getName()) ) {
			AonUtil.setBeanValue(IItemConstants.PRODUCT, SHOW_SALES_PRICE, Boolean.TRUE);
		}
	}
	
	private IOption getOption( String actionName ) {
		SelectedMenuController smc = (SelectedMenuController) AonUtil.getRegisteredBean(SELECTED_MENU_CONTROLLER_NAME);
		ApplicationOptionController aoc = (ApplicationOptionController) AonUtil.getRegisteredBean(APPLICATION_OPTION_CONTROLLER_NAME);
		ActionDeniedController adc = (ActionDeniedController) AonUtil.getRegisteredBean(ACTION_DENIED_CONTROLLER_NAME);
		ApplicationOption option = aoc.getOptionMap().get(actionName);
		if ( (option != null) && (option.getViewId() != null) ) {
			if (! adc.isDenied(option) ) {
				smc.setLastMenuAction(option.getGroup().getCategory().getAction());
				return option;	
			}
		}
		ApplicationCategory category = aoc.getCategory(actionName);
		if ( category != null ) {
			if (category.isRendered() && !adc.isDeniedModule(category.getAlias()) ) {
				smc.setLastMenuAction(category.getAction());
				return category;	
			}
		}
		return null;
	}

	private void initUser() {
		User user = UserUtils.getInstance().getLoggedUser();
		if ( user.getInitAction() != null ) {
			this.homepagOption = getOption(user.getInitAction());
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
		String[] categories = new String[]{CONFIGURATION_CATEGORY};
		String[] groups = new String[]{GROUP_CONFIG_SECURITY, GROUP_CONFIG_COMPANY};
		adc.enableOnly(categories, groups, MAIL_ACCOUNT_ACTION, SIGNATURE_ACTION);
	}
	
	private String getHomepage() {
		String value = HOMEPAGE_DESKTOP;
		if ( this.homepagOption != null ) {
			for( ActionSource as : this.homepagOption.getActionSources() ) {
				as.execute();
			}
			value = this.homepagOption.getViewId();
			this.homepagOption = null;
		}
		return value;
	}
	
	private boolean hasCompany() {
		CompanyController company = (CompanyController) AonUtil.getRegisteredBean(COMPANY_CONTROLLER_NAME);
		try {
			return company.getModel().getRowCount() > 0;
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return false;
	}
	
	public String getViewId() {
		if ( !hasCompany() ) {
			return NEW_COMPANY_TEMPLATE;
		}
		if ( UserUtils.getInstance().isPasswordExpired() ) {
			return PASSWORD_EXPIRED_TEMPLATE;
		}
		return getHomepage();
	}
	
	public String getTemplate() {
		return adminDomain ?  ADMIN_TEMPLATE : DESKTOP_TEMPLATE;
	}

	private void initSupport() {
		String value = AppParamUtil.getValue(AON_SUPPORT_ENABLED);
		if ( value != null ) {
			Date date = new Date( NumberUtils.toLong(value) );
			if ( DateUtils.isSameDay(date, new Date()) ) {
				setSupportEnabled(true);
			}
		}
		if (! isSupportEnabled() ) {
			AppParamUtil.removeParameter(AON_SUPPORT_ENABLED);
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
		AppParamUtil.insertParameter(AON_SUPPORT_ENABLED, value);
	}

	public void onDisableSupport(ActionEvent event) {
		setSupportEnabled(false);
		AppParamUtil.removeParameter(AON_SUPPORT_ENABLED);
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
			ps = conn.prepareStatement(select.toString(),ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
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
}