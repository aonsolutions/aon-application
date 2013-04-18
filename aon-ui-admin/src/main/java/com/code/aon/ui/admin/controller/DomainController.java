package com.code.aon.ui.admin.controller;

import static com.code.aon.ui.admin.controller.IAdminConstants.ADMIN_CONTROLLER_NAME;
import static com.code.aon.ui.admin.controller.IAdminConstants.BUNDLE_NAME;
import static com.code.aon.ui.admin.controller.IAdminConstants.DOMAIN_DISPLAY_NAME;
import static com.code.aon.ui.admin.controller.IAdminConstants.DOMAIN_DOMAIN_MANAGEMENT;
import static com.code.aon.ui.admin.controller.IAdminConstants.DOMAIN_EMAIL_BODY_1;
import static com.code.aon.ui.admin.controller.IAdminConstants.DOMAIN_EMAIL_BODY_2;
import static com.code.aon.ui.admin.controller.IAdminConstants.DOMAIN_EMAIL_BODY_3;
import static com.code.aon.ui.admin.controller.IAdminConstants.DOMAIN_EMAIL_BODY_4;
import static com.code.aon.ui.admin.controller.IAdminConstants.DOMAIN_EMAIL_BODY_5;
import static com.code.aon.ui.admin.controller.IAdminConstants.DOMAIN_EMAIL_BODY_FOOTER;
import static com.code.aon.ui.admin.controller.IAdminConstants.DOMAIN_MANAGEMENT;
import static com.code.aon.ui.admin.controller.IAdminConstants.DOMAIN_MAX_DEFINED_USERS;
import static com.code.aon.ui.admin.controller.IAdminConstants.DOMAIN_MAX_TOTAL_DOCUMENT_SIZE;
import static com.code.aon.ui.admin.controller.IAdminConstants.DOMAIN_MODULES;
import static com.code.aon.ui.admin.controller.IAdminConstants.DOMAIN_NAME_DUPLICATED;
import static com.code.aon.ui.admin.controller.IAdminConstants.DOMAIN_PARENT;
import static com.code.aon.ui.admin.controller.IAdminConstants.DOMAIN_TYPE;
import static com.code.aon.ui.admin.controller.IAdminConstants.DOMAIN_URL;
import static com.code.aon.ui.audit.controller.IAuditConstants.AUDIT_LEVEL;
import static com.code.aon.ui.common.ICommonConstants.ACTIVE;
import static com.code.aon.ui.common.ICommonConstants.AON_AIO_APPLICATION;
import static com.code.aon.ui.common.ICommonConstants.LOGGED_USER_CONTROLLER_NAME;
import static com.code.aon.ui.common.ICommonConstants.MODULE_MANAGEMENT_FINANCE;
import static com.code.aon.ui.common.ICommonConstants.NO;
import static com.code.aon.ui.common.ICommonConstants.YES;
import static com.code.aon.ui.company.controller.ICompanyConstants.COMPANY_EMAIL_BODY_HEADER;
import static com.code.aon.ui.config.controller.ConfigConstants.DOMAIN_SWITCHER;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;
import javax.mail.Address;
import javax.mail.internet.InternetAddress;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.audit.DomainApplicationModule;
import com.code.aon.audit.enumeration.Module;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.enumeration.AppParam;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.util.AdminUtil;
import com.code.aon.common.util.AonFile;
import com.code.aon.company.Company;
import com.code.aon.config.Application;
import com.code.aon.config.ApplicationParameter;
import com.code.aon.config.Domain;
import com.code.aon.config.DomainApplication;
import com.code.aon.config.enumeration.DomainType;
import com.code.aon.config.util.AppParamUtil;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ql.ProjectionList;
import com.code.aon.ui.admin.DomainApplicationInfo;
import com.code.aon.ui.admin.DomainInfo;
import com.code.aon.ui.admin.DomainModuleInfo;
import com.code.aon.ui.admin.DomainModuleInfoManagement;
import com.code.aon.ui.audit.AuditManager;
import com.code.aon.ui.audit.controller.IAuditConstants;
import com.code.aon.ui.common.controller.LoggedUser;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.config.controller.DomainSwitcher;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.form.event.IControllerListener;
import com.code.aon.ui.registry.controller.DocumentManager;
import com.code.aon.ui.registry.controller.IRegistryConstants;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.webmail.EmailSender;
import com.code.aon.webmail.WebmailException;
import com.code.aon.webmail.db.MailAccount;
import com.code.aon.webmail.enumeration.ConnectionSecurity;
import com.esferalia.aon.entity.IEntityAlias;

public class DomainController extends BasicController {

	private final static Logger LOGGER = LoggerFactory.getLogger(DomainController.class);
	
	public final static int DEFAULT_MAX_TOTAL_DOCUMENT_SIZE = 100;	
	
	private DomainApplicationInfo aioInfo;
	
	private DomainModuleInfo documental;
	
	private DomainModuleInfo documentPortal;

	private DomainModuleInfo payroll;
	
	private DomainModuleInfo payrollPortal;
	
	private DomainApplication domainApplication;
		
	private boolean OEM;
	
	private Domain OEMDomain;
	
	private Domain heritableOEMDomain;
	
	private IControllerListener parentDomainFilter;
	
	private IControllerListener OEMDomainFilter;
	
	private DomainInfo currentDomainInfo;
	
	private List<SelectItem> payrollModules;
	
	private List<SelectItem> documentModules;
	
	private Module payrollModule;
	
	private Module documentModule;
	
	private boolean showAuditInfoWindow;
	
	private AdminMainController getAdmin() {
		return (AdminMainController) AonUtil.getRegisteredBean(ADMIN_CONTROLLER_NAME);
	}
	
	public Domain getDomain() {
		return (Domain) getTo();
	}	
	
	public Domain getParentDomain() {
		Domain parent = getDomain().getParent();
		if ( (parent != null) && (parent.getId() != null) ) {
			return parent;
		}		
		return null;
	}

	public void onInit( ActionEvent event ) {
		initDomainApplication();
		getAdmin().resetTermsOfServiceAccepted();
		try {
			select(event, getDomain().getId());			
		} catch (ManagerBeanException e) {
			LOGGER.error( e.getMessage(), e );
		}				
		this.currentDomainInfo = getDomainInfo();
	}
	
	public DomainModuleInfo getDocumental() {
		return documental;
	}

	public void onDocumentalChanged( ActionEvent event ) {
		if (! getDocumental().isChecked() ) {
			getDomain().setMaxTotalDocumentSize(DEFAULT_MAX_TOTAL_DOCUMENT_SIZE);
		}
	}

	public void onPortalDocumentalChanged( ActionEvent event ) {
		getDocumental().setChecked(getDocumentModule() == Module.DOCUMENT);
		onDocumentalChanged(event);
	}
	
	public boolean isConsultancyParent() throws ManagerBeanException {
		Domain parent = getParentDomain();
		return (parent != null) && (parent.getType()  == DomainType.CONSULTANCY);
	}
	
	private List<Module> getDisabledModules() throws ManagerBeanException {
		List<Module> list = new LinkedList<Module>();
		Domain parent = getParentDomain();
		if ( parent != null ) {
			IManagerBean bean = BeanManager.getManagerBean(DomainApplicationModule.class);
			Criteria criteria = new Criteria();
			criteria.setSkipDomainFilter(true);
			Application application = DomainApplicationInfo.getApplication(AON_AIO_APPLICATION);
			Integer da = AdminUtil.getDomainApplication(parent.getId(), application.getId());
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.DOMAIN_APPLICATION_MODULE_DOMAIN), parent.getId());
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.DOMAIN_APPLICATION_MODULE_DOMAIN_APPLICATION_ID), da);
			for( ITransferObject to : bean.getList(criteria) ) {
				DomainApplicationModule dam = (DomainApplicationModule) to;
				list.add(dam.getModule());
			}
			if ( isConsultancyParent() ) {
				list.remove(Module.DOCUMENT);
				list.remove(Module.PAYROLL);
				list.remove(Module.DOCUMENT_PORTAL);
				list.remove(Module.PAYROLL_PORTAL);
				list.remove(Module.CONTRATA);
			}
		}
		return list;
	}
	
	private void joinManagementTreasury() throws ManagerBeanException {
		DomainModuleInfo management = this.aioInfo.getModuleInfo(Module.MANAGEMENT);
		DomainModuleInfo treasury = this.aioInfo.getModuleInfo(Module.TREASURY);
		if ( (management != null) && (treasury != null) ) {
			DomainModuleInfoManagement dmim = new DomainModuleInfoManagement(management, treasury);
			this.aioInfo.getApplicationModules().remove(management);
			this.aioInfo.getApplicationModules().remove(treasury);
			this.aioInfo.getApplicationModules().add(dmim);
			dmim.setDescription(AonUtil.getMessage(MODULE_MANAGEMENT_FINANCE));				
		}
	}
	
	public boolean isShowDocumentSelection() {
		if ( (documental != null) && (documentPortal != null) ) {
			DomainSwitcher ds = (DomainSwitcher) AonUtil.getRegisteredBean(DOMAIN_SWITCHER);
			return ds.isChildDomain();
		}
		return false;
	}

	public boolean isShowPayrollSelection() {
		if ( (payroll != null) && (payrollPortal != null) ) {
			DomainSwitcher ds = (DomainSwitcher) AonUtil.getRegisteredBean(DOMAIN_SWITCHER);
			return ds.isChildDomain();
		}
		return false;
	}
	
	private void initPortalModules() throws ManagerBeanException {
		setDocumentModule(null);
		setPayrollModule(null);
		this.documental = this.aioInfo.getModuleInfo(Module.DOCUMENT);
		this.documentPortal = this.aioInfo.getModuleInfo(Module.DOCUMENT_PORTAL);
		this.payroll = this.aioInfo.getModuleInfo(Module.PAYROLL);
		this.payrollPortal = this.aioInfo.getModuleInfo(Module.PAYROLL_PORTAL);		
		if ( isShowDocumentSelection() ) {
			getDocumental().setRendered(false);
			documentPortal.setRendered(false);
			if ( getDocumental().isChecked() ) {				
				setDocumentModule(Module.DOCUMENT);
				documentPortal.setChecked(false);
			}
			if ( documentPortal.isChecked() ) {
				setDocumentModule(Module.DOCUMENT_PORTAL);
			}
		}
		if ( isShowPayrollSelection() ) {		
			payroll.setRendered(false);
			payrollPortal.setRendered(false);
			if ( payroll.isChecked() ) {				
				setPayrollModule(Module.PAYROLL);
				payrollPortal.setChecked(false);
			}
			if ( payrollPortal.isChecked() ) {
				setPayrollModule(Module.PAYROLL_PORTAL);
			}
		}
	}
	
	private void updateModules( DomainApplicationInfo appInfo, boolean sysAdmin ) throws ManagerBeanException {
		if (! sysAdmin ) {
			List<Module> disabledModules = getDisabledModules();
			for( Module module : disabledModules ) {
				DomainModuleInfo info = appInfo.getModuleInfo(module);
				if ( info == null ) {
					info = new DomainModuleInfo(module);
					appInfo.getApplicationModules().add(info);
					LOGGER.debug( "Added: {}", module );
				}			
				info.setChecked(true);
				info.setDisabled(true);
				LOGGER.debug( "Checked and disabled: {}", info );
			}
			List<Module> visibleModules = AuditManager.getVisibleModules(getDomain().getId(), appInfo.getApplication().getId());
			for( int i = appInfo.getApplicationModules().size()-1; i >= 0; i-- ) {
				DomainModuleInfo info =  appInfo.getApplicationModules().get(i);
				if (! visibleModules.contains(info.getModule()) ) {
					appInfo.removeModuleInfo(info);
					LOGGER.debug( "Removed from list: {}", info );
				}
			}
		}
		joinManagementTreasury();
		initPortalModules();
		appInfo.updateApplicationModules();
		appInfo.sortApplicationModules();
		if (! sysAdmin ) {
			DomainModuleInfo infoweb = this.aioInfo.getModuleInfo(Module.INFOWEB);
			if ( infoweb != null ) {
				this.aioInfo.getApplicationModules().remove(infoweb);
			}
			DomainModuleInfo pos = this.aioInfo.getModuleInfo(Module.POS);
			if ( pos != null ) {
				this.aioInfo.getApplicationModules().remove(pos);
			}
		}
	}

	public DomainApplicationInfo getAioInfo() {
		return aioInfo;
	}

	public void initApplicationInfos() throws ManagerBeanException {
		this.aioInfo = DomainApplicationInfo.getApplicationInfos(getDomain(), AON_AIO_APPLICATION);
		updateModules(this.aioInfo, AonUtil.getRoleManager().isSysAdmin());
	}
	
	private void updatePortalModules() throws ManagerBeanException {
		if ( isShowDocumentSelection()) {
			getDocumental().setChecked(false);
			documentPortal.setChecked(false);
			if ( getDocumentModule() == Module.DOCUMENT ) {
				getDocumental().setChecked(true);
			} else if ( getDocumentModule() == Module.DOCUMENT_PORTAL ) {
				documentPortal.setChecked(true);
			}
		}
		if ( isShowPayrollSelection() ) {
			payroll.setChecked(false);
			payrollPortal.setChecked(false);
			if ( getPayrollModule() == Module.PAYROLL ) {
				payroll.setChecked(true);
			} else if ( getPayrollModule() == Module.PAYROLL_PORTAL ) {
				payrollPortal.setChecked(true);
			}
		}
	}	

	public void saveApplications() throws ManagerBeanException {
		updatePortalModules();
		if ( this.aioInfo.isChecked() ) {
			this.aioInfo.register();
		} else {
			this.aioInfo.unregister();
		}
	}	
	
	public boolean isShowApplications() {
		return this.aioInfo.isChecked();
	}

	private void initDomainApplication() {
		this.domainApplication = null;
		try {
			IManagerBean bean = BeanManager.getManagerBean(DomainApplication.class);
			Criteria criteria = new Criteria();
			Integer appId = AonUtil.getAuthPrincipal().getApplicationId();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.DOMAIN_APPLICATION_DOMAIN), getDomain().getId());
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.DOMAIN_APPLICATION_APPLICATION_ID), appId);
			List<ITransferObject> list = bean.getList(criteria);
			if (! list.isEmpty() ) {
				this.domainApplication = (DomainApplication) list.get(0);
			}
		} catch (ManagerBeanException e) {
			LOGGER.error( e.getMessage(), e );
		}				
	}	

	public void updateDomainApplication() {
		if ( this.domainApplication != null ) {
			try {
				IManagerBean bean = BeanManager.getManagerBean(DomainApplication.class);
				bean.update(domainApplication);
			} catch (ManagerBeanException e) {
				LOGGER.error( e.getMessage(), e );
			}							
		}
	}

	public DomainApplication getDomainApplication() {
		return domainApplication;
	}

	public void setDomainApplication(DomainApplication domainApplication) {
		this.domainApplication = domainApplication;
	}	
	
	public boolean isOEM() {
		return OEM;
	}

	public void setOEM(boolean oEM) {
		OEM = oEM;
	}
	
	private Domain getOEMDomain( AppParam appParam ) throws ManagerBeanException {
		Domain domain = null;
		String idValue = AppParamUtil.getValue(appParam);
		if (! StringUtils.isEmpty(idValue) ) {
			Integer id = NumberUtils.toInt(idValue);
			Company company = (Company) BeanManager.getManagerBean(Company.class).get(id);
			if ( company != null ) {
				domain = (Domain) BeanManager.getManagerBean(Domain.class).get(company.getDomain());
			}
		}
		if ( domain == null ) {
			domain = (Domain) BeanManager.getManagerBean(Domain.class).createNewTo();
		}
		return domain;
	}

	public void initOEM() throws ManagerBeanException {
		String oemValue = AppParamUtil.getValue(AppParam.AON_CUSTOMIZE_OEM);
		this.OEM = StringUtils.equals(oemValue, Boolean.TRUE.toString());
		this.OEMDomain = getOEMDomain(AppParam.AON_CUSTOMIZE_ID);
		this.heritableOEMDomain = getOEMDomain(AppParam.AON_CUSTOMIZE_HERITABLE_ID);
	}

	private void saveOEMDomain( AppParam appParam, Domain domain) throws ManagerBeanException {
		String id = null;
		if ( (domain != null) && (domain.getId() != null) ) {
			IManagerBean bean = BeanManager.getManagerBean(Company.class);
			Criteria criteria = new Criteria();
			criteria.setSkipDomainFilter(true);
			criteria.addEqualExpression("Company.domain", domain.getId());
			List<ITransferObject> list = bean.getList(criteria);
			if (! list.isEmpty()) {
				Company company = (Company) list.get(0);
				id = String.valueOf(company.getId());
			}
		}
		AppParamUtil.insertParameter(appParam, id);
	}		
	
	public void saveOEM() throws ManagerBeanException {
		AppParamUtil.insertParameter(AppParam.AON_CUSTOMIZE_OEM, String.valueOf(isOEM()) );
		saveOEMDomain(AppParam.AON_CUSTOMIZE_ID, this.OEMDomain);
		saveOEMDomain(AppParam.AON_CUSTOMIZE_HERITABLE_ID, this.heritableOEMDomain);
	}	

	public void domainNameCheck(FacesContext context, UIComponent component, Object value) throws ManagerBeanException {
		String name = (String) value;
		if (! StringUtils.equals(name, getDomain().getName()) ) {
			Criteria criteria = new Criteria();
			criteria.setSkipDomainFilter(true);
			criteria.addEqualExpression(getFieldName(IEntityAlias.DOMAIN_NAME), name);
			if ( getManagerBean().getCount(criteria) > 0 ) {
				String message = AonUtil.getMessage(BUNDLE_NAME, DOMAIN_NAME_DUPLICATED, name);
				throw new ValidatorException(new FacesMessage(message));
			}			
		}
	}	
	
	public Domain getOEMDomain() {
		return OEMDomain;
	}

	public void setOEMDomain(Domain oEMDomain) {
		OEMDomain = oEMDomain;
	}
	
	public Domain getHeritableOEMDomain() {
		return heritableOEMDomain;
	}

	public void setHeritableOEMDomain(Domain heritableOEMDomain) {
		this.heritableOEMDomain = heritableOEMDomain;
	}

	public void updateDocumental() throws ManagerBeanException {
		DocumentManager dm = (DocumentManager) AonUtil.getRegisteredBean(IRegistryConstants.DOCUMENT_MANAGER_CONTROLLER_NAME);
		dm.updateLimits(getManagerBean(), getDomain());
	}

	public IControllerListener getParentDomainFilter() {
		if ( this.parentDomainFilter == null ) {
			this.parentDomainFilter = new ControllerAdapter() {
				@Override
				public void beforeModelInitialized(ControllerEvent event)
						throws ControllerListenerException {
					IController controller = event.getController();
					try {					
						controller.getCriteria().setSkipDomainFilter(true);
						String parent = controller.getFieldName(IEntityAlias.DOMAIN_DOMAIN_MANAGEMENT);
						controller.getCriteria().addEqualExpression(parent, Boolean.TRUE);
						String active = controller.getFieldName(IEntityAlias.DOMAIN_ACTIVE);
						controller.getCriteria().addEqualExpression(active, Boolean.TRUE);
						String idAlias = controller.getFieldName(IEntityAlias.DOMAIN_ID);
						controller.getCriteria().addNotEqualExpression(idAlias, DomainManager.getCurrentDomain());
					} catch (ManagerBeanException e) {
						LOGGER.error("Error filtering offer", e);
					}
				}
			};
		}
		return this.parentDomainFilter;
	}
	
	@SuppressWarnings("unchecked")
	private List<Integer> getOEMDomains() throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(ApplicationParameter.class);	
		Criteria criteria = new Criteria();
		criteria.setSkipDomainFilter(true);
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.APPLICATION_PARAMETER_NAME), AppParam.AON_CUSTOMIZE_OEM.toString());
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.APPLICATION_PARAMETER_VALUE), Boolean.TRUE.toString());
		String domainAlias = bean.getFieldName(IEntityAlias.APPLICATION_PARAMETER_DOMAIN);
		ProjectionList pl = new ProjectionList(Projection.property(domainAlias));
		return bean.getList(pl, criteria);
	}

	public IControllerListener getOEMDomainFilter() {
		if ( this.OEMDomainFilter == null ) {
			this.OEMDomainFilter = new ControllerAdapter() {
				@Override
				public void beforeModelInitialized(ControllerEvent event)
						throws ControllerListenerException {
					IController controller = event.getController();
					try {					
						controller.getCriteria().setSkipDomainFilter(true);
						String active = controller.getFieldName(IEntityAlias.DOMAIN_ACTIVE);
						controller.getCriteria().addEqualExpression(active, Boolean.TRUE);
						
						String idAlias = controller.getFieldName(IEntityAlias.DOMAIN_ID);
						List<Integer> oemDomains = getOEMDomains();
						if (! oemDomains.isEmpty() ) {
							controller.getCriteria().addInExpression(idAlias, oemDomains);	
						} else {
							controller.getCriteria().addNullExpression(idAlias);
						}					
					} catch (ManagerBeanException e) {
						LOGGER.error("Error filtering offer", e);
					}
				}
			};
		}
		return this.OEMDomainFilter;
	}
	
	private DomainInfo getDomainInfo() {
		Domain domain = getDomain();
		DomainInfo di = new DomainInfo();
		di.setName( domain.getDescription() );
		di.setUrl( domain.getName() );
		di.setType(domain.getType());
		di.setParent(domain.getParent());
		di.setNumberOfUsers(domain.getMaxDefinedUsers());
		di.setMaxTotalDocumentSize(domain.getMaxTotalDocumentSize());
		di.setDomainManagement(domain.isDomainManagement());
		List<Module> modules = new LinkedList<Module>();
		for( DomainModuleInfo dim : this.aioInfo.getApplicationModules() ) {
			if ( dim.isChecked() ) {
				modules.add(dim.getModule());	
			}
		}
		di.setModules(modules);
		di.setActive(domain.isActive());
		if ( this.domainApplication != null ) {
			di.setAuditLevel(this.domainApplication.getAuditLevel());
		}
		return di;
	}
	
	private EmailSender getEmailSender() throws UnsupportedEncodingException {
		MailAccount mailAccount = new MailAccount();
		mailAccount.setEmail("admin@aonSolutions.es");
		mailAccount.setMailUsername("admin@aonSolutions.es");
		mailAccount.setPasswordString("admineM41L");
		mailAccount.setIncomingSecurity(ConnectionSecurity.TLS);
		mailAccount.setIncomingHost("imap.aonsolutions.es");
		mailAccount.setOutgoingSecurity(ConnectionSecurity.TLS);
		mailAccount.setOutgoingHost("smtp.aonsolutions.es");
		mailAccount.setDisplayName("ESFERALIA Networks S.A.");
		Address from = new InternetAddress( mailAccount.getEmail(), mailAccount.getDisplayName() );
		return new EmailSender( from, mailAccount );							
	}
	
	private String getEmailContent( DomainInfo di ) {
		StringBuffer body = new StringBuffer();
		body.append( "<html><head>" );
		body.append( "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />" );
		body.append( "</head><body>" );
		
		body.append( AonUtil.getMessage(ICompanyConstants.BUNDLE_NAME, COMPANY_EMAIL_BODY_HEADER) );
		LoggedUser loggedUser = (LoggedUser) AonUtil.getRegisteredBean(LOGGED_USER_CONTROLLER_NAME);
		body.append( AonUtil.getMessage(BUNDLE_NAME, DOMAIN_EMAIL_BODY_1, loggedUser.getLoggedUserName(), di.getUrl()) );
		body.append( AonUtil.getMessage(BUNDLE_NAME, DOMAIN_EMAIL_BODY_2, di.getName()) );
		if ( (di.getParent() != null) && (di.getParent().getId() != null) ) {
			body.append( AonUtil.getMessage(BUNDLE_NAME, DOMAIN_EMAIL_BODY_3, di.getParent().getDescription()) );
		}
		Locale locale = AonUtil.getCurrentLocale();
		String type = di.getType().getName(locale);
		String size = FileUtils.byteCountToDisplaySize(di.getMaxTotalDocumentSize()*FileUtils.ONE_MB);
		body.append( AonUtil.getMessage(BUNDLE_NAME, DOMAIN_EMAIL_BODY_4, type, di.getNumberOfUsers(), size ) );
		String multiDomain = di.isDomainManagement() ? AonUtil.getMessage(YES) : AonUtil.getMessage(NO) ;
		body.append( AonUtil.getMessage(BUNDLE_NAME, DOMAIN_EMAIL_BODY_5, multiDomain, di.getModules().size()) );
		if (! di.getModules().isEmpty() ) {
			body.append( "<ul>" );
			for( Module module : di.getModules() ) {
				body.append( "<li>" ).append( module.getName(locale) ).append( "</li>" );
			}
			body.append( "</ul>" );
		}
		
		body.append( AonUtil.getMessage(BUNDLE_NAME, DOMAIN_EMAIL_BODY_FOOTER) );
		body.append( "</body>" );
		return body.toString();
	}	

	private void diff( StringBuffer sb, String message, Object oldValue, Object newValue ) {
		diff( AonUtil.getMessage(BUNDLE_NAME, message), sb, oldValue, newValue );
	}

	private void diff( String message, StringBuffer sb, Object oldValue, Object newValue ) {
		sb.append( message ).append(": ");
		sb.append( oldValue ).append( " -> ").append( newValue );
		sb.append(IOUtils.LINE_SEPARATOR);
	}
	
	private AonFile getDiffFile( DomainInfo di1, DomainInfo di2 ) throws IOException {
		StringBuffer sb = new StringBuffer();
		Locale locale = AonUtil.getCurrentLocale();	
		if (! StringUtils.equals(di1.getName(), di2.getName()) ) {
			diff( sb, DOMAIN_DISPLAY_NAME, di1.getName(), di2.getName() );
		}
		if ( di1.getType() != di2.getType() ) {
			diff( sb, DOMAIN_TYPE, di1.getType().getName(locale), di2.getType().getName(locale) );
		}
		if (! StringUtils.equals(di1.getUrl(), di2.getUrl()) ) {
			diff( sb, DOMAIN_URL, di1.getUrl(), di2.getUrl() );
		}
		if (! ObjectUtils.equals(di1.getParentId(), di2.getParentId()) ) {
			String p1 = (di1.getParentId() != null) ? di1.getParent().getId() + "-" + di1.getParent().getDescription() : "null";
			String p2 = (di2.getParentId() != null) ? di2.getParent().getId() + "-" + di2.getParent().getDescription() : "null";
			diff( sb, DOMAIN_PARENT, p1, p2 );
		}
		if (! ObjectUtils.equals(di1.getNumberOfUsers(), di2.getNumberOfUsers()) ) {
			diff( sb, DOMAIN_MAX_DEFINED_USERS, di1.getNumberOfUsers(), di2.getNumberOfUsers() );
		}
		if (! ObjectUtils.equals(di1.getMaxTotalDocumentSize(), di2.getMaxTotalDocumentSize()) ) {
			diff( sb, DOMAIN_MAX_TOTAL_DOCUMENT_SIZE, di1.getMaxTotalDocumentSize(), di2.getMaxTotalDocumentSize() );
		}
		if ( di1.isDomainManagement() != di2.isDomainManagement() ) {
			diff( sb, DOMAIN_DOMAIN_MANAGEMENT, di1.isDomainManagement(), di2.isDomainManagement() );
		}
		if (! Arrays.equals(di1.getModuleArray(), di2.getModuleArray()) ) {
			diff( sb, DOMAIN_MODULES, di1.getModuleList(), di2.getModuleList() );
		}
		if ( di1.isActive() != di2.isActive() ) {
			String active1 = di1.isActive() ? AonUtil.getMessage(YES) : AonUtil.getMessage(NO) ;
			String active2 = di2.isActive() ? AonUtil.getMessage(YES) : AonUtil.getMessage(NO) ;
			diff( AonUtil.getMessage(ACTIVE), sb, active1, active2 );
		}
		if (! ObjectUtils.equals(di1.getAuditLevel(), di2.getAuditLevel()) ) {
			diff( AonUtil.getMessage(IAuditConstants.BUNDLE_NAME, AUDIT_LEVEL), sb, di1.getAuditLevel().getName(locale), di2.getAuditLevel().getName(locale) );
		}
		
		if ( sb.length() > 0 ) {
			AonFile aonFile = new AonFile();
			File file = File.createTempFile( "diff", "." + MimeType.MIME_TXT.getExtension() );
			FileUtils.writeStringToFile(file, sb.toString());
			aonFile.setFile( file );
			aonFile.setFileName( "diff." + MimeType.MIME_TXT.getExtension() );
			aonFile.setMimeType(MimeType.MIME_TXT);
			return aonFile;			
		}
		return null;
	}		
	
	public void updateDomainInfo() throws IOException, WebmailException {
		DomainInfo di = getDomainInfo(); 
		AonFile diffFile = getDiffFile(this.currentDomainInfo, di);
		if ( diffFile != null ) {
			Address to = new InternetAddress("administracion@aonSolutions.es", "Administración");
			Address[] recipients = new Address[] {to};
			String subject = AonUtil.getMessage(BUNDLE_NAME, DOMAIN_MANAGEMENT);
			getEmailSender().sendMessage(recipients, subject, getEmailContent(di), MimeType.MIME_HTML, diffFile );	
		}
		this.currentDomainInfo = di;
	}

	public Module getPayrollModule() {
		return payrollModule;
	}

	public void setPayrollModule(Module payrollModule) {
		this.payrollModule = payrollModule;
	}
	
	public List<SelectItem> getPayrollModules() {
		if ( payrollModules == null ) {
			Locale locale = AonUtil.getCurrentLocale();
			payrollModules = new LinkedList<SelectItem>();
			payrollModules.add(new SelectItem(Module.PAYROLL_PORTAL, Module.PAYROLL_PORTAL.getName(locale)));		
			payrollModules.add(new SelectItem(Module.PAYROLL, Module.PAYROLL.getName(locale)));
		}
		return payrollModules;
	}

	public Module getDocumentModule() {
		return documentModule;
	}

	public void setDocumentModule(Module documentModule) {
		this.documentModule = documentModule;
	}	
	
	public List<SelectItem> getDocumentModules() {
		if ( documentModules == null ) {
			Locale locale = AonUtil.getCurrentLocale();
			documentModules = new LinkedList<SelectItem>();
			documentModules.add(new SelectItem(Module.DOCUMENT_PORTAL, Module.DOCUMENT_PORTAL.getName(locale)));		
			documentModules.add(new SelectItem(Module.DOCUMENT, Module.DOCUMENT.getName(locale)));			
		}
		return documentModules;
	}	

	public boolean isShowAuditInfoWindow() {
		return showAuditInfoWindow;
	}

	public void setShowAuditInfoWindow(boolean showAuditInfoWindow) {
		this.showAuditInfoWindow = showAuditInfoWindow;
	}
	
}