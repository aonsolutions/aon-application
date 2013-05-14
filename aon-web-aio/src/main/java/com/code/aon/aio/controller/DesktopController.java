package com.code.aon.aio.controller;


import static com.code.aon.common.enumeration.AppParam.AON_SUPPORT_ENABLED;
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
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.ListDataModel;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang.time.DateUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.aio.DesktopNoticeSummary;
import com.code.aon.aio.TaskInfo;
import com.code.aon.audit.enumeration.Module;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.config.User;
import com.code.aon.config.enumeration.DomainType;
import com.code.aon.config.util.AppParamUtil;
import com.code.aon.groupware.Note;
import com.code.aon.groupware.Task;
import com.code.aon.groupware.TaskHolder;
import com.code.aon.groupware.TaskHolderWorkgroup;
import com.code.aon.groupware.enumeration.AlarmSource;
import com.code.aon.groupware.enumeration.AlarmStatus;
import com.code.aon.groupware.enumeration.NoticeType;
import com.code.aon.groupware.enumeration.TaskStatus;

import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.audit.ActionSource;
import com.code.aon.ui.audit.ApplicationOption;
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
import com.code.aon.ui.groupware.GroupwareUtils;
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
    
    private List<DesktopNoticeSummary> noticeSummaryList;
    
    private List<TaskInfo> taskSummaryModel;
    
    private TaskHolder taskHolder;
    
    private boolean checkUpdateURL = true;
    
    private ApplicationOption homepagOption;
    
    private boolean adminDomain;
    
    private boolean supportEnabled;

    public DesktopController() {
		try {
			DomainSwitcher ds = (DomainSwitcher) AonUtil.getRegisteredBean(DOMAIN_SWITCHER);
			this.adminDomain = ds.getType() == DomainType.ADMIN;
			if ( this.adminDomain ) {
				initAdminDomain();
			} else {
		        AuthPrincipal principal = AonUtil.getAuthPrincipal();
				updateRecentNoteModel(principal);
				updateNoticeSummaryModel(principal);
				initTask();
				initGarage();
				initAcademy();
				initHotel();				
			}
			initUser();
			initSupport();
	    } catch (ManagerBeanException e) {
	    	LOGGER.error( e.getMessage(), e );
	        throw new AbortProcessingException("Error initing desktop models", e);
		}
    }

	public void updateNoticeSummaryModel( AuthPrincipal principal ) {
        this.noticeSummaryList = new LinkedList<DesktopNoticeSummary>();
        NoticeType[] noticeTypes = NoticeType.values();
        for (int i=0; i<noticeTypes.length; i++) {
            DesktopNoticeSummary summary = new DesktopNoticeSummary(noticeTypes[i]);
            noticeSummaryList.add(summary);
        }
        Calendar to = new GregorianCalendar();
        to.set(Calendar.HOUR_OF_DAY, 23);
        to.set(Calendar.MINUTE, 59);
        to.set(Calendar.SECOND, 59);
        String select = "select notice.type, count(*) " 
                        +" from Notice as notice, Alarm as alarm " 
                        +" where notice.id = alarm.sourceId " 
                        +" and " + DomainManager.getSQLWhereClause("alarm.domain")  
                        +" and alarm.source = :source "  
                        +" and alarm.status = :status "  
                        +" and alarm.user = :user "   
                        +" and alarm.alarmDate < :alarmDate "
                        +" group by notice.type " 
                        +" order by notice.type";
        
    	String name = HibernateUtil.getSessionFactoryName();
        Session session = HibernateUtil.getSession(name);
        Query query = session.createQuery(select);
        query.setInteger("source", AlarmSource.NOTICE.ordinal());
        query.setInteger("status", AlarmStatus.PENDING.ordinal());
        query.setInteger("user", principal.getUserId());
        query.setDate("alarmDate", to.getTime());
        Iterator<?> iterator = query.list().iterator();
        while (iterator.hasNext()) {
            Object[] obj = (Object[])iterator.next();
            NoticeType noticeType = (NoticeType)obj[0];
            Long count = (Long)obj[1];

            DesktopNoticeSummary summary = noticeSummaryList.get(noticeType.ordinal());
            summary.setCount(count);
        }

    }

	public List<DesktopNoticeSummary> getNoticeSummaryModel() {
        return noticeSummaryList;
    }

	private void updateRecentNoteModel( AuthPrincipal principal ) throws ManagerBeanException {
    	IManagerBean noteBean = BeanManager.getManagerBean(Note.class);
    	Criteria criteria = new Criteria();    	criteria.addEqualExpression(noteBean.getFieldName(IEntityAlias.NOTE_OWNER_ID), principal.getUserId());
    	criteria.addOrder(noteBean.getFieldName(IEntityAlias.NOTE_DATE), false);
    	this.recentNoteModel = new ListDataModel(noteBean.getList(criteria));
    }
    
    public ListDataModel getRecentNoteModel() {
    	return this.recentNoteModel;
    }    
    
	public void onRefresh( ActionEvent event ) {
		LOGGER.info( "Desktop Refresh" );
		try {
	        AuthPrincipal principal = AonUtil.getAuthPrincipal();
			updateRecentNoteModel(principal);
			updateNoticeSummaryModel(principal);
			updateTaskSummaryModel();
	    } catch (ManagerBeanException e) {
	    	LOGGER.error( e.getMessage(), e );
	        throw new AbortProcessingException("Error updating desktop models", e);
		}
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

	private void initTask() {
		try {
			taskHolder = new GroupwareUtils().getCurrentTaskHolder();
			updateTaskSummaryModel();
		} catch (Throwable th) {
			LOGGER.error("Error on task init", th);
		}
	}

	public boolean isTaskHolderAvailable() {
		return (taskHolder != null);
	}	
	
	private int getExpiredCount() throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(Task.class);
		Criteria criteria = new Criteria();
		String user = bean.getFieldName(IEntityAlias.TASK_TASK_HOLDER_ID);
		criteria.addEqualExpression(user, this.taskHolder.getId());
		String statusAlias = bean.getFieldName(IEntityAlias.TASK_STATUS);
		Expression exp1  = ExpressionUtilities.getEqualExpression(statusAlias, TaskStatus.IN_PROGRESS);
		Expression exp2  = ExpressionUtilities.getEqualExpression(statusAlias, TaskStatus.PENDING);
		criteria.addExpression( ExpressionUtilities.getOrExpression(exp1, exp2) );
		String dueDate = bean.getFieldName(IEntityAlias.TASK_DUE_DATE);
		criteria.addLessThanExpression(dueDate, new Date());
		return bean.getCount(criteria);
	}	
	
	private int getTaskCount( TaskStatus status ) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(Task.class);
		Criteria criteria = new Criteria();
		String user = bean.getFieldName(IEntityAlias.TASK_TASK_HOLDER_ID);
		criteria.addEqualExpression(user, this.taskHolder.getId());
		String statusAlias = bean.getFieldName(IEntityAlias.TASK_STATUS);
		criteria.addEqualExpression(statusAlias, status);
		return bean.getCount(criteria);
	}	
	
	private List<Integer> getUserWorkgroups() throws ManagerBeanException {
		List<Integer> result = new LinkedList<Integer>();
		IManagerBean bean = BeanManager.getManagerBean(TaskHolderWorkgroup.class);
		Criteria criteria = new Criteria();
		String user = bean.getFieldName(IEntityAlias.TASK_HOLDER_WORKGROUP_TASK_HOLDER_ID);
		criteria.addEqualExpression(user, this.taskHolder.getId());
		for( ITransferObject to : bean.getList(criteria) ) {
			TaskHolderWorkgroup uwg = (TaskHolderWorkgroup) to;
			result.add( uwg.getTaskHolder().getId() );
		}
		return result;
	}
	
	private int getUserWorkgroupCount() throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(Task.class);
		Criteria criteria = new Criteria();
		String user = bean.getFieldName(IEntityAlias.TASK_TASK_HOLDER_ID);
		criteria.addNullExpression(user);
		String workGroup = bean.getFieldName(IEntityAlias.TASK_WORK_GROUP_ID);
		Expression expression = null;
		for( Integer id : getUserWorkgroups() ) {
			if ( expression == null ) {
				expression = ExpressionUtilities.getEqualExpression(workGroup, id);				
			} else {
				Expression exp  = ExpressionUtilities.getEqualExpression(workGroup, id);
				expression = ExpressionUtilities.getOrExpression(expression, exp);
			}
		}
		if ( expression != null ) {
			criteria.addExpression(expression);
		}
		return bean.getCount(criteria);
	}	

    private void updateTaskSummaryModel() throws ManagerBeanException {
    	if ( isTaskHolderAvailable() ) {
        	this.taskSummaryModel = new ArrayList<TaskInfo>();
    		int inprogress = getTaskCount( TaskStatus.IN_PROGRESS );
    		int pending = getTaskCount( TaskStatus.PENDING );
    		int group = getUserWorkgroupCount();
    		String desc = TaskStatus.IN_PROGRESS.getName( AonUtil.getCurrentLocale() );
    		taskSummaryModel.add( new TaskInfo( desc, inprogress + "/" + (inprogress + pending + group) ) );

    		String dues = AonUtil.getMessage( "aon_dues" );
    		taskSummaryModel.add( new TaskInfo( dues, "" + getExpiredCount() ) );    		
    	}
    }
    
    public List<TaskInfo> getTaskSummaryModel() {
    	return this.taskSummaryModel;
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
	
	private ApplicationOption getOption( String actionName ) {
		ApplicationOptionController aoc = (ApplicationOptionController) AonUtil.getRegisteredBean(APPLICATION_OPTION_CONTROLLER_NAME);
		ApplicationOption option = aoc.getOptionMap().get(actionName);
		if ( (option != null) && (option.getViewId() != null) ) {
			ActionDeniedController adc = (ActionDeniedController) AonUtil.getRegisteredBean(ACTION_DENIED_CONTROLLER_NAME);
			if (! adc.isDenied(option) ) {
				return option;	
			}
		}
		return null;
	}

	private void initUser() throws ManagerBeanException {
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

	public boolean isPosEnabled() {
		ActionDeniedController adc = (ActionDeniedController) AonUtil.getRegisteredBean(ACTION_DENIED_CONTROLLER_NAME);
		return adc.isDeniedModule(Module.POS.getName());
	}
	
}