package com.code.aon.ui.admin.controller;

import static com.code.aon.ui.common.ICommonConstants.AON_AIO_APPLICATION;
import static com.code.aon.ui.company.controller.ICompanyConstants.COMPANY_CONTROLLER_NAME;
import static com.code.aon.ui.config.controller.ConfigConstants.DOMAIN_SWITCHER;
import static javax.faces.application.FacesMessage.SEVERITY_ERROR;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;
import javax.mail.Address;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.validator.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.audit.DomainApplicationModule;
import com.code.aon.audit.enumeration.Module;
import com.code.aon.common.AonException;
import com.code.aon.AonVersion;
import com.code.aon.common.BasicAttachment;
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
import com.code.aon.config.User;
import com.code.aon.config.enumeration.DomainType;
import com.code.aon.config.util.AppParamUtil;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ql.ProjectionList;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.registry.RegistryMedia;
import com.code.aon.registry.enumeration.MediaType;
import com.code.aon.registry.enumeration.RegistryAttachmentType;
import com.code.aon.ui.admin.DomainApplicationInfo;
import com.code.aon.ui.admin.DomainInfo;
import com.code.aon.ui.admin.DomainModuleInfo;
import com.code.aon.ui.admin.DomainModuleInfoManagement;
import com.code.aon.ui.audit.AuditManager;
import com.code.aon.ui.audit.controller.ActionDeniedController;
import com.code.aon.ui.audit.controller.IAuditConstants;
import com.code.aon.ui.common.ICommonConstants;
import com.code.aon.ui.common.ICommonMessages;
import com.code.aon.ui.common.controller.LoggedUser;
import com.code.aon.ui.company.controller.CompanyController;
import com.code.aon.ui.config.controller.DomainSwitcher;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.DataScrollerState;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.form.event.IControllerListener;
import com.code.aon.ui.registry.controller.DocumentManager;
import com.code.aon.ui.registry.controller.IRegistryConstants;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.util.DownloadUtil;
import com.code.aon.webmail.EmailSender;
import com.code.aon.webmail.WebmailException;
import com.code.aon.webmail.bean.AonMessage;
import com.code.aon.webmail.db.MailAccount;
import com.code.aon.webmail.enumeration.ConnectionSecurity;
import com.esferalia.aon.entity.IEntityAlias;

public class DomainController extends BasicController {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final String LEGAL_WARNING_NAME = "avisoLegal";
	
	private static final String LEGAL_WARNING_FILE = LEGAL_WARNING_NAME + "." + MimeType.MIME_PDF.getExtension();
	
	private static final String LEGAL_WARNING_PATH = AdminMainController.PROPERTIES_PATH + LEGAL_WARNING_FILE;
	
	private final static Logger LOGGER = LoggerFactory.getLogger(DomainController.class);
	
	public final static int DEFAULT_MAX_TOTAL_DOCUMENT_SIZE = 100;	
	
	private final static Pattern URL_LABEL_PATTERN = Pattern.compile("[a-z\\d][a-z\\d-]{0,62}");
	
	private final static Pattern URL_TLD_PATTERN = Pattern.compile("[a-z]{2,6}");
	
	public final static int MAX_DOMAIN_LABEL_LENGTH = 63;
	
	public final static int MAX_DOMAIN_NAME_LENGTH = 253;
	
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
	
	private DataScrollerState historyState;
	
	private AdminMainController getAdmin() {
		return (AdminMainController) AonUtil.getRegisteredBean(IAdminConstants.ADMIN_CONTROLLER_NAME);
	}
	
	public Domain getDomain() {
		return (Domain) getTo();
	}	
	
	public Domain getParentDomain() {
		Domain parent = getDomain().getParent();
		if ( parent != null && parent.getId() != null ) {
			return parent;
		}		
		return null;
	}

	public void onInit( ActionEvent event ) {
		getAdmin().resetTermsOfServiceAccepted();
		try {
			select(event, DomainManager.getCurrentDomain());
			initDomainApplication();
			initApplicationInfos();
			initOEM();
			initHistory();
			this.currentDomainInfo = getDomainInfo();
			if ( this.historyState.getDirectModel().getRowCount() == 0 ) {
				saveHistory(this.currentDomainInfo);
			}
		} catch (ManagerBeanException e) {
			LOGGER.error( e.getMessage(), e );
		}				
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
		return parent != null && parent.getType()==DomainType.CONSULTANCY;
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
		if ( management != null && treasury != null ) {
			DomainModuleInfoManagement dmim = new DomainModuleInfoManagement(management, treasury);
			this.aioInfo.getApplicationModules().remove(management);
			this.aioInfo.getApplicationModules().remove(treasury);
			this.aioInfo.getApplicationModules().add(dmim);
			dmim.setDescription(AonUtil.getMessage(ICommonMessages.MODULE_MANAGEMENT_FINANCE));				
		}
	}
	
	public boolean isShowDocumentSelection() {
		if ( documental != null && documentPortal != null ) {
			DomainSwitcher ds = (DomainSwitcher) AonUtil.getRegisteredBean(DOMAIN_SWITCHER);
			return ds.isChildDomain();
		}
		return false;
	}

	public boolean isShowPayrollSelection() {
		if ( payroll != null && payrollPortal != null ) {
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
		} else {
			DomainModuleInfo info =  appInfo.getModuleInfo(Module.CONFIGURATION);
			appInfo.removeModuleInfo(info);
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
		if ( domain != null && domain.getId() != null ) {
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
			try {
				DomainController.checkDomainName(name, 3);
			} catch (AonException e) {
				String message = AonUtil.getMessage(ICommonMessages.SUBDOMAIN_SUFFIX);
				FacesMessage fm = new FacesMessage(message + ": " + e.getMessage());
				fm.setSeverity(SEVERITY_ERROR);
				throw new ValidatorException(fm);		
			}		
			Criteria criteria = new Criteria();
			criteria.setSkipDomainFilter(true);
			criteria.addEqualExpression(getFieldName(IEntityAlias.DOMAIN_NAME), name);
			if ( getManagerBean().getCount(criteria) > 0 ) {
				String message = AonUtil.getMessage(ICommonMessages.DOMAIN_NAME_DUPLICATED, name);
				throw new ValidatorException(new FacesMessage(message));
			}			
		}
	}	
	
	public void subDomainSuffixCheck(FacesContext context, UIComponent component, Object value) {
		String name = (String) value;
		if (! StringUtils.equals(name, getDomain().getName()) ) {
			try {
				DomainController.checkDomainName(name, 3);
			} catch (AonException e) {
				String message = AonUtil.getMessage(ICommonMessages.SUBDOMAIN_SUFFIX);
				FacesMessage fm = new FacesMessage(message + ": " + e.getMessage());
				fm.setSeverity(SEVERITY_ERROR);
				throw new ValidatorException(fm);		
			}		
		}		
	}
	
	public static void checkDomainName( String name, int maxLevel ) throws AonException {
		if (! StringUtils.isEmpty(name) ) {
			if ( name.length() <= MAX_DOMAIN_NAME_LENGTH ) {
				String[] labels = StringUtils.split(name, ".");
				if ( ArrayUtils.getLength(labels) <= maxLevel ) {
					for( int i = 0; i < labels.length; i++ ) {
						String label = labels[i];
						if ( StringUtils.length(label) > MAX_DOMAIN_LABEL_LENGTH ) {
							throw new AonException( AonUtil.getMessage(ICommonMessages.DOMAIN_INVALID_LABEL_LENGTH, label) );
						}
						if ( StringUtils.startsWith(label, "-") || StringUtils.endsWith(label, "-") ) {
							throw new AonException( AonUtil.getMessage(ICommonMessages.DOMAIN_INVALID_LABEL_DASH, label) );
						}
						Pattern p = (i+1==labels.length) ? URL_TLD_PATTERN : URL_LABEL_PATTERN;
						Matcher m = p.matcher(label);
						if (! m.matches() ) {
							throw new AonException( AonUtil.getMessage(ICommonMessages.DOMAIN_INVALID_LABEL_FORMAT, label) );
						}
					}	
				} else {
					throw new AonException( AonUtil.getMessage(ICommonMessages.DOMAIN_INVALID_NAME_LEVEL, maxLevel) );
				}
			} else {
				throw new AonException( AonUtil.getMessage(ICommonMessages.DOMAIN_INVALID_NAME_LARGE) );
			}
		} else {
			throw new AonException( AonUtil.getMessage(ICommonMessages.DOMAIN_INVALID_NAME, name) );	
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
		Domain domain = getDomain();
		if ( dm.updateLimits(domain) ) {
			getManagerBean().update(domain);
		}
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
		di.setUser(AonUtil.getAuthPrincipal().getShortName());
		di.setType(domain.getType());
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
		mailAccount.setDisplayName("aonSolutions");
		Address from = new InternetAddress( mailAccount.getEmail(), mailAccount.getDisplayName() );
		return new EmailSender( from, mailAccount );							
	}
	
	private String getEmailContent( Domain domain, DomainInfo di ) {
		StringBuffer body = new StringBuffer();
		body.append( "<html><head>" );
		body.append( "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />" );
		body.append( "</head><body>" );
		
		body.append( AonUtil.getMessage(ICommonMessages.COMPANY_EMAIL_BODY_HEADER) );
		LoggedUser loggedUser = (LoggedUser) AonUtil.getRegisteredBean(ICommonConstants.LOGGED_USER_CONTROLLER_NAME);
		body.append( AonUtil.getMessage(ICommonMessages.DOMAIN_EMAIL_BODY_1, loggedUser.getLoggedUserName(), domain.getName()) );
		body.append( AonUtil.getMessage(ICommonMessages.DOMAIN_EMAIL_BODY_2, StringEscapeUtils.escapeHtml(domain.getDescription())) );
		if ( domain.getParent() != null && domain.getParent().getId() != null ) {
			String parent = StringEscapeUtils.escapeHtml(domain.getParent().getDescription());
			body.append( AonUtil.getMessage(ICommonMessages.DOMAIN_EMAIL_BODY_3, parent) );
		}
		Locale locale = AonUtil.getCurrentLocale();
		String type = StringEscapeUtils.escapeHtml(di.getType().getName(locale));
		String size = FileUtils.byteCountToDisplaySize(di.getMaxTotalDocumentSize()*FileUtils.ONE_MB);
		body.append( AonUtil.getMessage(ICommonMessages.DOMAIN_EMAIL_BODY_4, type, di.getNumberOfUsers(), size ) );
		String multiDomain = di.isDomainManagement() ? AonUtil.getMessage(ICommonMessages.YES) : AonUtil.getMessage(ICommonMessages.NO);
		body.append( AonUtil.getMessage(ICommonMessages.DOMAIN_EMAIL_BODY_5, multiDomain, di.getModules().size()) );
		if (! di.getModules().isEmpty() ) {
			body.append( "<ul>" );
			for( Module module : di.getModules() ) {
				String name = StringEscapeUtils.escapeHtml(module.getName(locale));
				body.append( "<li>" ).append(name).append( "</li>" );
			}
			body.append( "</ul>" );
		}
		
		body.append( AonUtil.getMessage(ICommonMessages.DOMAIN_EMAIL_BODY_FOOTER) );
		body.append( "</body>" );
		return body.toString();
	}	

	private AonFile getDiffFile( DomainInfo di1, DomainInfo di2 ) throws IOException {
		String diff = di1.getDifferences(di2);
		if (! StringUtils.isEmpty(diff) ) {
			AonFile aonFile = new AonFile();
			File file = File.createTempFile( "diff", "." + MimeType.MIME_TXT.getExtension() );
			FileUtils.writeStringToFile(file, diff);
			aonFile.setFile( file );
			aonFile.setFileName( "diff." + MimeType.MIME_TXT.getExtension() );
			aonFile.setMimeType(MimeType.MIME_TXT);
			return aonFile;			
		}
		return null;
	}		
	
	private void updateDomain( Domain domain, AuthPrincipal principal ) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(Domain.class);
			domain.setModificationUser(principal.getShortName());
			domain.setModificationDate(new Date());
			bean.update(domain);
		} catch ( ManagerBeanException e ) {
			LOGGER.error(e.getMessage(), e);
		}
	}
	
	private AonFile getTermsOfServiceFile() throws IOException {
		File file = File.createTempFile( LEGAL_WARNING_NAME, "." + MimeType.MIME_PDF.getExtension() );
		FileUtils.writeByteArrayToFile(file, getTermsOfServiceData());
		AonFile aonFile = new AonFile();
		aonFile.setFile(file);
		aonFile.setFileName(LEGAL_WARNING_FILE);
		aonFile.setMimeType(MimeType.MIME_PDF);
		return aonFile;
	}	
	
	public void updateDomainInfo() throws IOException, WebmailException, ManagerBeanException {
		DomainInfo di = getDomainInfo(); 
		AonFile diffFile = getDiffFile(this.currentDomainInfo, di);
		if ( diffFile != null ) {
			Domain domain = getDomain();
			AuthPrincipal principal = AonUtil.getAuthPrincipal();
			updateDomain(domain, principal);
			saveHistory(di);
			Address[] emails = getNotificationEmails();
			if (! ArrayUtils.isEmpty(emails) ) {
				LOGGER.info( "Notication emails: {}", ArrayUtils.toString(emails) );
				EmailSender sender = getEmailSender();
				String subject = AonUtil.getMessage(ICommonMessages.DOMAIN_EMAIL_SUBJECT, domain.getName());
				AonMessage message = sender.createMessage(subject);
				message.setRecipientsBcc(emails);
				String content = getEmailContent(domain, di);
				AonFile termsOfServiceFile = getTermsOfServiceFile();
				sender.addMessageContent(message, content, MimeType.MIME_HTML, diffFile, termsOfServiceFile);
				sender.sendMessage(message);
				diffFile.clean();
				termsOfServiceFile.clean();
			}
			if ( DomainSwitcher.getDomainType(principal.getDomainId()) != DomainType.ADMIN ) {
				ActionDeniedController adc = (ActionDeniedController) AonUtil.getRegisteredBean(IAuditConstants.ACTION_DENIED_CONTROLLER_NAME);
				adc.init();
			}
			initHistory();
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
	
	public void ownerCheck(FacesContext context, UIComponent component, Object value) {
		String emails = (String) value;
		try {
			InternetAddress[] addresses = InternetAddress.parse(emails, true);
			if (! ArrayUtils.isEmpty(addresses) ) {
				for( InternetAddress address : addresses ) {
					if (! EmailValidator.getInstance().isValid(address.toString()) ) {
						String message = AonUtil.getMessage(ICommonMessages.WRONG_EMAIL, address.toString());
						throw new ValidatorException(new FacesMessage(SEVERITY_ERROR, message, null));
					}
				}
			}
		} catch (AddressException e) {
			String message = AonUtil.getMessage(ICommonMessages.WRONG_EMAILS);
			throw new ValidatorException(new FacesMessage(SEVERITY_ERROR, message, null));
		}
	}			
	
	private InternetAddress getEmail( String email, String displayName ) {
		InternetAddress address = null;
		if ( EmailValidator.getInstance().isValid(email) ) {
			try {
				if (! StringUtils.isEmpty(displayName) ) {
					address = new InternetAddress(email, displayName);	
				} else {
					address = new InternetAddress(email);
				}
			} catch (Throwable e) {
				LOGGER.error(e.getMessage(), e);
			}
		} else {
			LOGGER.error( "Invalid email: {}", email );
		}
		return address;
	}
	
	private  List<InternetAddress> getOwnerEmails( Integer domainId ) throws ManagerBeanException {
		List<InternetAddress> list = new LinkedList<InternetAddress>();
		IManagerBean bean = BeanManager.getManagerBean(Domain.class);
		Domain domain = (Domain) bean.get(domainId);
		if (! StringUtils.isEmpty(domain.getOwner()) ) {
			try {
				InternetAddress[] addresses = InternetAddress.parse(domain.getOwner(), true);
				if (! ArrayUtils.isEmpty(addresses) ) {
					for( InternetAddress address : addresses ) {
						if ( EmailValidator.getInstance().isValid(address.toString()) ) {
							list.add(address);
						} else {
							LOGGER.error( "Invalid email: {}", address );
						}
					}
				}
			} catch (AddressException e) {
				LOGGER.error( e.getMessage(), e );
			}					
		}
		return list;
	}

	private List<InternetAddress> getUserEmails() throws ManagerBeanException {
		List<InternetAddress> list = new LinkedList<InternetAddress>();
		User user = UserUtils.getInstance().getLoggedUser();
		IManagerBean bean = BeanManager.getManagerBean(MailAccount.class);
		Criteria criteria = new Criteria();
		criteria.setSkipDomainFilter(true);
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.MAIL_ACCOUNT_USER_ID), user.getId());
		for( ITransferObject to : bean.getList(criteria) ) {
			MailAccount ma = (MailAccount) to;
			InternetAddress email = getEmail(ma.getEmail(), ma.getDisplayName());
			if ( email != null ) {
				list.add( email );
			}
		}
		return list;
	}

	private List<InternetAddress> getCompanyEmail( Integer domainId ) throws ManagerBeanException {
		List<InternetAddress> list = new LinkedList<InternetAddress>();
		Integer companyId = AdminUtil.getCompanyId(domainId);
		if ( companyId != null ) {
			IManagerBean bean = BeanManager.getManagerBean(RegistryMedia.class);
			Criteria criteria = new Criteria();
			criteria.setSkipDomainFilter(true);
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_MEDIA_REGISTRY_ID), companyId);
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_MEDIA_MEDIA_TYPE), MediaType.EMAIL);
			for( ITransferObject to : bean.getList(criteria) ) {
				String value = ((RegistryMedia) to).getValue();
				InternetAddress email = getEmail(value, null);
				if ( email != null ) {
					list.add( email );
				}
			}			
		}
		return list;
	}
	
	private Address[] getNotificationEmails() {
		Set<Address> emails = new HashSet<Address>();
		try {
			Integer adminId = AdminUtil.getAdminDomain();
			if ( adminId != null ) {
				emails.addAll(getCompanyEmail(adminId));
				emails.addAll(getOwnerEmails(adminId));
			}
			emails.addAll(getOwnerEmails(DomainManager.getCurrentDomain()));
			emails.addAll(getUserEmails());
		} catch (Throwable e) {
			LOGGER.error( e.getMessage(), e );
		}
		return emails.toArray(new Address[emails.size()]);
	}

	public int getMaxDomainNameLength() {
		return MAX_DOMAIN_NAME_LENGTH;
	}

	private Company getCompany() {
		CompanyController companyController = (CompanyController) AonUtil.getRegisteredBean(COMPANY_CONTROLLER_NAME);
		return companyController.obtainCompany();		
	}
	
	private void initHistory() throws ManagerBeanException {
		List<DomainInfo> list = new LinkedList<DomainInfo>();
		IManagerBean bean = BeanManager.getManagerBean(RegistryAttachment.class);
		Criteria criteria = new Criteria();
		Integer companyId = getCompany().getId();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_ATTACHMENT_REGISTRY_ID), companyId);
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_ATTACHMENT_REGISTRY_ATTACHMENT_TYPE), RegistryAttachmentType.DOMAIN_BOOK_HISTORY);
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_ATTACHMENT_MIME_TYPE), MimeType.MIME_TXT);
		criteria.addOrder(bean.getFieldName(IEntityAlias.REGISTRY_ATTACHMENT_DESCRIPTION), false);
		for( ITransferObject to : bean.getList(criteria) ) {
			DomainInfo di = DomainInfo.getDomainInfo((RegistryAttachment) to);
			list.add(di);
		}
		this.historyState = new DataScrollerState(new ListDataModel(list), "history");
	}
	
	private void saveHistory( DomainInfo di ) throws ManagerBeanException {
		RegistryAttachment ra = new RegistryAttachment();
		ra.setRegistry(getCompany());
		ra.setAttachDate(new Date());
		String description = DomainInfo.DATE_FORMAT.format(ra.getAttachDate());
		ra.setDescription(description);
		ra.setRegistryAttachmentType(RegistryAttachmentType.DOMAIN_BOOK_HISTORY);
		ra.setData(di.getData());
		ra.setMimeType(MimeType.MIME_TXT);
		IManagerBean bean = BeanManager.getManagerBean(RegistryAttachment.class);
		bean.insert(ra);
	}
	
	public DataScrollerState getHistoryState() {
		return historyState;
	}

	public void setHistoryState(DataScrollerState historyState) {
		this.historyState = historyState;
	}

	@SuppressWarnings("unchecked")
	public String getCurrentDiff() {
		String diff = null;
		if ( historyState.getDirectModel().isRowAvailable() ) {
			List<DomainInfo> list = (List<DomainInfo>) historyState.getDirectModel().getWrappedData();
			int index = historyState.getDirectModel().getRowIndex();
			diff = list.get(index+1).getDifferences(list.get(index));
		}
		if ( StringUtils.isEmpty(diff) ) {
			diff = AonUtil.getMessage(ICommonMessages.FINANCE_NONE);
		}
		return diff;
	}
	
	private byte[] getTermsOfServiceData() {
		InputStream in = null;
		byte[] data = null;
		try {
			in = DomainController.class.getResourceAsStream(LEGAL_WARNING_PATH);
			data = IOUtils.toByteArray(in);
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e );
		} finally {
			IOUtils.closeQuietly(in);
		}	
		return data;
	}

	public void onDownloadTermsOfService( ActionEvent event ) {
		byte[] data = getTermsOfServiceData();
		BasicAttachment attach = new BasicAttachment();
		attach.setData(data);
		attach.setDescription(LEGAL_WARNING_FILE);
		attach.setMimeType(MimeType.MIME_PDF);
		DownloadUtil.downloadAttachment(attach);
	}
	
}