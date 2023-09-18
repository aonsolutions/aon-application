package com.code.aon.aio;

import static com.code.aon.ui.admin.controller.DomainUserController.FAVORITES;
import static com.code.aon.ui.audit.controller.IAuditConstants.ACTION_DENIED_CONTROLLER_NAME;
import static com.code.aon.ui.company.controller.ICompanyConstants.COMPANY_CONTROLLER_NAME;
import static com.code.aon.ui.config.controller.ConfigConstants.DOMAIN_SWITCHER;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;

import java.io.Serializable;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.DataModel;
import jakarta.servlet.ServletContext;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.audit.enumeration.Module;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.enumeration.AppParam;
import com.code.aon.common.util.AdminUtil;
import com.code.aon.config.User;
import com.code.aon.config.enumeration.DomainType;
import com.code.aon.config.util.AppParamUtil;
import com.code.aon.faces.controller.IRichConstants;
import com.code.aon.faces.controller.SelectedMenuController;
import com.code.aon.groupware.Note;
import com.code.aon.groupware.enumeration.AlarmSource;
import com.code.aon.groupware.enumeration.AlarmStatus;
import com.code.aon.groupware.enumeration.NoticeType;
import com.code.aon.groupware.enumeration.TaskStatus;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.admin.PortalInfo;
import com.code.aon.ui.admin.controller.MarketplaceController;
import com.code.aon.ui.admin.controller.OCRConfigurationController;
import com.code.aon.ui.audit.ApplicationCategory;
import com.code.aon.ui.audit.ApplicationOption;
import com.code.aon.ui.audit.BasicOption;
import com.code.aon.ui.audit.IOption;
import com.code.aon.ui.audit.controller.ActionDeniedController;
import com.code.aon.ui.audit.controller.ApplicationOptionController;
import com.code.aon.ui.audit.controller.IAuditConstants;
import com.code.aon.ui.audit.controller.MenuParser;
import com.code.aon.ui.commercial.controller.ICommercialConstants;
import com.code.aon.ui.common.ICommonConstants;
import com.code.aon.ui.common.LocaleElement;
import com.code.aon.ui.common.controller.ConfigurationController;
import com.code.aon.ui.common.role.IAonRole;
import com.code.aon.ui.common.serialize.SerializableListDataModel;
import com.code.aon.ui.company.controller.CompanyController;
import com.code.aon.ui.config.controller.ConfigConstants;
import com.code.aon.ui.config.controller.DomainSwitcher;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.customer.controller.ICustomerConstants;
import com.code.aon.ui.finance.controller.IFinanceConstants;
import com.code.aon.ui.groupware.controller.IGroupWareConstants;
import com.code.aon.ui.product.controller.IItemConstants;
import com.code.aon.ui.purchase.controller.IPurchaseConstants;
import com.code.aon.ui.sales.controller.ISalesConstants;
import com.code.aon.ui.tas.controller.ITasConstants;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.warehouse.controller.IWarehouseConstants;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;

import net.aonsolutions.core.dbutils.DatabaseUtil;
import net.aonsolutions.core.pool.AonConnectionException;

public class DesktopState implements Serializable {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private final static Logger LOGGER = LoggerFactory.getLogger(DesktopState.class);
	
	private static final String PATCH_INIT_ACTION = "aon.patch.initAction";
	
	private static final String CHECK_SERIALIZATION = "com.code.aon.checkSerialization";
	
    private DataModel recentNoteModel;
    private NoticeInfo noticeInfo;
    private TaskInfo taskInfo;
    private IOption initOption;
    private boolean adminDomain;
    private boolean supportEnabled;
    private boolean patchInitAction;
    private PortalInfo portalInfo;
    private boolean userWithPortalView;
    private boolean showFavorites;
    private boolean portalUser;
	private boolean adminRole;
    
    public DesktopState() {
    	initCompany();
		DomainSwitcher ds = (DomainSwitcher) AonUtil.getRegisteredBean(DOMAIN_SWITCHER);
		this.adminDomain = ds.getType() == DomainType.ADMIN;
		if ( this.adminDomain ) {
			initAdminDomain();
		} else {
	        updateRecentNoteModel();
			initGarage();
			initAcademy();
			initHotel();				
		}
		User user = UserUtils.getInstance().getLoggedUser();
		initPortal(user, ds);
		initOCR(user, ds);
		initMarketplace();
		initUser(user);
		initSupport();
		checkSerialization();
		updateLastAccess(user, ds);
	}

    private void initCompany() {
    	CompanyController controller = (CompanyController) AonUtil.getRegisteredBean(COMPANY_CONTROLLER_NAME);
    	controller.obtainCompany();
    }
    
	private void initAdminDomain() {
		ActionDeniedController adc = (ActionDeniedController) AonUtil.getRegisteredBean(ACTION_DENIED_CONTROLLER_NAME);
		String[] categories = new String[]{IAuditConstants.CONFIGURATION_CATEGORY};
		String[] groups = new String[]{IAuditConstants.GROUP_CONFIG_SECURITY, IAuditConstants.GROUP_CONFIG_COMPANY};
		if (! adc.isDeniedModule(Module.DOCUMENT.getName()) ) {
			categories = (String[]) ArrayUtils.add(categories, IAuditConstants.DOCUMENT_CATEGORY);
			groups = (String[]) ArrayUtils.addAll(groups, new String[]{IAuditConstants.GROUP_DOCUMENT, IAuditConstants.GROUP_DOCUMENT_UTILITIES});
		}
		if (! adc.isDeniedModule(Module.PAYROLL.getName()) ) {
			categories = (String[]) ArrayUtils.add(categories, IAuditConstants.PAYROLL_CATEGORY);
			groups = (String[]) ArrayUtils.addAll(groups, new String[]{IAuditConstants.GROUP_PAYROLL_CONTRATA_MAIN, IAuditConstants.GROUP_CONFIGURATION_UTILITIES});
		}
		adc.getManager().enableOnly(categories, groups, IAuditConstants.MAIL_ACCOUNT_ACTION, IAuditConstants.SIGNATURE_ACTION);
	}
	
	private void updateRecentNoteModel()  {
		try {
			IManagerBean noteBean = BeanManager.getManagerBean(Note.class);
			AuthPrincipal principal = AonUtil.getAuthPrincipal();
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(noteBean.getFieldName(IEntityAlias.NOTE_OWNER_ID), principal.getUserId());
			criteria.addOrder(noteBean.getFieldName(IEntityAlias.NOTE_DATE), false);
			this.recentNoteModel = new SerializableListDataModel(noteBean.getList(criteria));
		} catch (ManagerBeanException e) {
			this.recentNoteModel = new SerializableListDataModel();
			LOGGER.error( e.getMessage(), e);
		}
    }	

	private IOption getOption( String actionName ) {
		SelectedMenuController smc = (SelectedMenuController) AonUtil.getRegisteredBean(IRichConstants.SELECTED_MENU_CONTROLLER_NAME);
		ApplicationOptionController aoc = ApplicationOptionController.getInstance();
		ActionDeniedController adc = (ActionDeniedController) AonUtil.getRegisteredBean(ACTION_DENIED_CONTROLLER_NAME);
		ApplicationOption option = aoc.getOptionMap().get(actionName);
		if ( option!=null && option.getViewId()!=null && !adc.getManager().isDenied(option) ) {
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

	private boolean hasCompany(CompanyController controller) {
		try {
			return controller.getModel().getRowCount() > 0;
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return false;
	}	
	
	private void setupInitAction( User user ) {
		CompanyController controller = (CompanyController) AonUtil.getRegisteredBean(COMPANY_CONTROLLER_NAME);
		if ( !hasCompany(controller) ) {
			controller.onLoad(false);
			controller.setHideHeaderContent(true);
			this.initOption = new BasicOption(COMPANY_CONTROLLER_NAME, COMPANY_CONTROLLER_NAME);
		} else if (! StringUtils.isEmpty(user.getInitAction()) ) {
			if ( FAVORITES.equals(user.getInitAction()) ) {
				setShowFavorites(true);
			} else {
				this.initOption = getOption(user.getInitAction());	
			}	
		}
		FacesContext ctx = FacesContext.getCurrentInstance();
		String value = ctx.getExternalContext().getInitParameter(PATCH_INIT_ACTION);
		this.patchInitAction = StringUtils.equals(value, Boolean.TRUE.toString());
	}
	
	public boolean isAdminRole() {
		return this.adminRole;
	}
	
	public boolean isOCR() {
		OCRConfigurationController ocr = (OCRConfigurationController) AonUtil.getRegisteredBean("ocrConfiguration");
		return ocr.isActive();
	}
	
	public boolean isComunica() {
		MarketplaceController marketplace = (MarketplaceController) AonUtil.getRegisteredBean("marketplace");
		return marketplace.isComunicaActive();
	}
	
	public boolean isConvenios() {
		MarketplaceController marketplace = (MarketplaceController) AonUtil.getRegisteredBean("marketplace");
		return marketplace.isConveniosActive();
	}
	
	public boolean isBank() {
		MarketplaceController marketplace = (MarketplaceController) AonUtil.getRegisteredBean("marketplace");
		return marketplace.isBankActive();
	}
	
	public boolean isTimecontrol() {
		MarketplaceController marketplace = (MarketplaceController) AonUtil.getRegisteredBean("marketplace");
		return marketplace.isTimecontrolActive();
	}

	private boolean calculateAdminRole() {
		Integer applicationUser = AdminUtil.getApplicationUser(AonUtil.getAuthPrincipal());
		if ( applicationUser != null ) {
			List<Integer> profiles = AdminUtil.getProfiles(applicationUser);
			if ( (profiles != null) && (!profiles.isEmpty()) ) {
				for( Integer profile : profiles ) {
					List<String> list = AdminUtil.getProfileRoles(profile);
					if ( list != null ) {
						for( String role : list ) {
							if ( IAonRole.ADMIN.getName().equals(role) ) {
								return true;
							}
						}
					}
				}
			}						
		}
		return false;
	}
	
	private User initUser( User user ) {
		setupInitAction(user);
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
		this.adminRole = calculateAdminRole();
		return user;
	}
	
	public boolean isSupportEnabled() {
		return supportEnabled;
	}

	public void setSupportEnabled(boolean supportEnabled) {
		this.supportEnabled = supportEnabled;
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

	private boolean isPayrollPortal( User user ) {
		if (user.getInitAction()!=null) {
			if ( this.portalInfo.isPayrollPortal() ) {
				return true;
			}
			ActionDeniedController adc = (ActionDeniedController) AonUtil.getRegisteredBean(ACTION_DENIED_CONTROLLER_NAME);
			List<Module> modules = adc.getManager().getVisibleModules();
			if ( modules.size()==1 && modules.get(0)==Module.PAYROLL_PORTAL ) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isPortalActive() {
		return this.portalInfo != null && this.portalInfo.isActive() ;
	}
	
	private boolean calculatePortalActive( User user, DomainSwitcher ds ) {
		this.portalInfo = new PortalInfo();
		if ( ds.isChildDomain() && (ds.getDomainId()==user.getDomain()) ) {
			this.portalInfo.init();
			if ( user.getEnterprise() != null ) {
				if (user.getInitAction()!=null) {
					return true;	
				} else if ( this.portalInfo.isActive() ) {
					return true;
				}
			}
			if ( this.portalInfo.isActive() ) {
				return true;
			}
		}
		this.portalInfo.reset();
		return false;
	}
	
	private String[] getPortalEnabledCategories( User user) {
		String[] categories = new String[0];
		if ( isPayrollPortal(user) || isPayrollInfoVisibleForPortal() ) {
			categories = (String[]) ArrayUtils.add(categories, Module.PAYROLL_PORTAL.getName());
		}
		if ( isDocumentalInfoVisibleForPortal() || isDocumentalManagementVisibleForPortal() ) {
			categories = (String[]) ArrayUtils.add(categories, Module.DOCUMENT.getName());
		}
		if ( this.portalInfo.isFinanceManagement() ) {
			categories = (String[]) ArrayUtils.add(categories, Module.FINANCE_PORTAL.getName());
			categories = (String[]) ArrayUtils.add(categories, Module.MANAGEMENT.getName());
		}
		return categories;
	}
	
	private void initPortal( User user, DomainSwitcher ds ) {
		if ( calculatePortalActive(user, ds) ) {
			this.userWithPortalView = user.getEnterprise() == null;
			String[] enableCategories = getPortalEnabledCategories(user);
			ActionDeniedController adc = (ActionDeniedController) AonUtil.getRegisteredBean(ACTION_DENIED_CONTROLLER_NAME);
			if (! this.userWithPortalView ) {
				this.portalUser = true;
				AonUtil.setBeanValue(IGroupWareConstants.ALARM_CONTROLLER_NAME, IGroupWareConstants.SHOW_PENDING, Boolean.FALSE);
				AonUtil.setBeanValue(IGroupWareConstants.ALARM_CONTROLLER_NAME, IGroupWareConstants.SHOW_LIST, Boolean.FALSE);
				Map<String, Object> properties = AonUtil.getConfigurationController().getProperties();
				properties.put( ICommonConstants.HIDE_MENU_EMAIL, Boolean.TRUE );
				properties.put( ICommonConstants.HIDE_MENU_FAVORITE, Boolean.TRUE );
				properties.put( ICommonConstants.HIDE_MENU_CHOOSE_LANGUAGE, Boolean.TRUE );
				properties.put( ICommonConstants.HIDE_MENU_ADVANCED_MODE, Boolean.TRUE );
				properties.put( ICommonConstants.HIDE_MENU_WEB_MAP, Boolean.TRUE );
				if ( isPayrollPortal(user) ) {
					properties.put( ICommonConstants.HIDE_MENU_HOME, Boolean.TRUE );
					properties.put( ICommonConstants.HIDE_MENU_ABOUT, Boolean.TRUE );
				}
				adc.getManager().enableOnly(enableCategories, new String[0], IAuditConstants.USER_PROFILE_ACTION, IAuditConstants.BATCH_DOCUMENT_ACTION);				
			} else {
				if (! adc.isDeniedModule(Module.PAYROLL.getName()) ) {
					enableCategories = (String[]) ArrayUtils.removeElement(enableCategories, Module.PAYROLL_PORTAL.getName());
				}
				adc.getManager().enableCategories(enableCategories);
			}
		}
	}
	
	private void initOCR(User user, DomainSwitcher ds) {
		OCRConfigurationController ocr = (OCRConfigurationController) AonUtil.getRegisteredBean("ocrConfiguration");
		ocr.init();
	}
	
	private void initMarketplace() {
		MarketplaceController marketplace = (MarketplaceController) AonUtil.getRegisteredBean("marketplace");
		marketplace.init();
	}

	private void initHotel() {
		ActionDeniedController adc = (ActionDeniedController) AonUtil.getRegisteredBean(ACTION_DENIED_CONTROLLER_NAME);
		if (! adc.isDeniedModule(Module.HOTEL.getName()) ) {
			AonUtil.setBeanValue(IItemConstants.PRODUCT, IItemConstants.SHOW_SALES_PRICE, Boolean.TRUE);
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

    public void setNoticeInfo(NoticeInfo noticeInfo) {
		this.noticeInfo = noticeInfo;
	}

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
	
    public void setTaskInfo(TaskInfo taskInfo) {
		this.taskInfo = taskInfo;
	}

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
 	
	public DataModel getRecentNoteModel() {
    	if (recentNoteModel == null) {
    		updateRecentNoteModel();
    	}
    	return recentNoteModel;
	}
	
	public void setRecentNoteModel(DataModel recentNoteModel) {
		this.recentNoteModel = recentNoteModel;
	}

	public IOption getInitOption() {
		return initOption;
	}

	public void setInitOption(IOption initOption) {
		this.initOption = initOption;
	}

	public boolean isAdminDomain() {
		return adminDomain;
	}

	public boolean isPatchInitAction() {
		return patchInitAction;
	}
	
	private void checkSerialization() {
		ExternalContext ectx = FacesContext.getCurrentInstance().getExternalContext();
		String value = ectx.getInitParameter(CHECK_SERIALIZATION);
		if ( StringUtils.equals(Boolean.TRUE.toString(), value) ) {
			ServletContext servletContext = (ServletContext) ectx.getContext();
			MenuParser parser = new MenuParser(servletContext);
			parser.checkSerialization();
		}	
	}

	public boolean isPayrollEnabled() {
		if ( AonUtil.getRoleManager().isPayroll() ) {
			ActionDeniedController adc = (ActionDeniedController) AonUtil.getRegisteredBean(ACTION_DENIED_CONTROLLER_NAME);
			return ! adc.isDeniedModule(Module.PAYROLL.getName());
		}
		return false;
	}

	public boolean isAccoutingEnabled() {
		if ( AonUtil.getRoleManager().isAccountingManager() ) {
			ActionDeniedController adc = (ActionDeniedController) AonUtil.getRegisteredBean(ACTION_DENIED_CONTROLLER_NAME);
			return ! adc.isDeniedModule(Module.ACCOUNTING.getName());
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
	
	public boolean isDocumentalEnabled() {
		if ( AonUtil.getRoleManager().isDocument() ) {
			ActionDeniedController adc = (ActionDeniedController) AonUtil.getRegisteredBean(ACTION_DENIED_CONTROLLER_NAME);
			return ! adc.isDeniedModule(Module.DOCUMENT.getName());
		}
		return false;
	}

	public boolean isAccountingInfoVisibleForPortal() {
		if ( this.portalInfo.isAccountingInfo() ) {
			if ( this.userWithPortalView ) {
				return isAccoutingEnabled();
			}
			return true;
		}
		return false;
	}
	
	public boolean isFiscalInfoVisibleForPortal() {
		if ( this.portalInfo.isFiscalInfo() ) {
			if ( this.userWithPortalView ) {
				return isFiscalEnabled();
			}
			return true;
		}
		return false;
	}
	
	public boolean isPayrollInfoVisibleForPortal() {
		if ( this.portalInfo.isPayrollInfo() ) {
			if ( this.userWithPortalView ) {
				return AonUtil.getRoleManager().isPayroll();
			}
			return true;
		}
		return false;
	}
	
	public boolean isDocumentalInfoVisibleForPortal() {
		if ( this.portalInfo.isDocumentalInfo() ) {
			if ( this.userWithPortalView ) {
				return isDocumentalEnabled();
			}
			return true;
		}
		return false;
	}

	public boolean isDocumentalManagementVisibleForPortal() {
		if ( this.portalInfo.isDocumentalManagement() ) {
			if ( this.userWithPortalView ) {
				return isDocumentalEnabled();
			}
			return true;
		}
		return false;
	}	

	public boolean isShowExternalApplications() {
		return !this.portalUser;
	}
	
	public boolean isShowFavorites() {
		return showFavorites;
	}

	public void setShowFavorites(boolean showFavorites) {
		this.showFavorites = showFavorites;
	}
	
	private void updateLastAccess( User user, DomainSwitcher ds ) {
		boolean adminDomainUser = DomainSwitcher.getDomainType(user.getDomain()) == DomainType.ADMIN;
		if ( this.adminDomain || !adminDomainUser ) {
			CloseableAONContext ctx = AONContext.getAONContext(AonUtil.getDomainName(), ds.getDomainId(), user.getLogin());
			Timestamp now = new java.sql.Timestamp(new Date().getTime());
			try {
				ctx.getDslContext().update(DOMAIN)
				.set(DOMAIN.LASTACCESS_DATE, now )
				.set(DOMAIN.LASTACCESS_USER, user.getLogin() )
				.where(DOMAIN.ID.eq(ds.getDomainId()))
				.execute();	
			} catch ( Throwable th ) {
				LOGGER.error(th.getMessage(), th);
			} finally {
				ctx.close();	
			}							
		}
	}
	
}
