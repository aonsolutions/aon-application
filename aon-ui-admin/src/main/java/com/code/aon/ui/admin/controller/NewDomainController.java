package com.code.aon.ui.admin.controller;

import static com.code.aon.ui.config.controller.ConfigConstants.DOMAIN_SWITCHER;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.sql.Connection;
import java.util.Calendar;
import java.util.Date;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.mail.Address;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.CharEncoding;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.audit.DomainApplicationModule;
import com.code.aon.audit.enumeration.Module;
import com.code.aon.common.AonException;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.enumeration.AppParam;
import com.code.aon.common.util.AdminUtil;
import com.code.aon.company.Company;
import com.code.aon.company.Enterprise;
import com.code.aon.config.Application;
import com.code.aon.config.ApplicationParameter;
import com.code.aon.config.Domain;
import com.code.aon.config.DomainApplication;
import com.code.aon.config.Scope;
import com.code.aon.config.User;
import com.code.aon.config.enumeration.DomainType;
import com.code.aon.config.util.AppParamUtil;
import net.aonsolutions.core.dbutils.AonDomainDuplicate;
import net.aonsolutions.core.dbutils.AonSQLException;
import net.aonsolutions.core.dbutils.AonSQLFile;
import net.aonsolutions.core.dbutils.AonSQLScript;
import net.aonsolutions.core.dbutils.DatabaseUtil;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.master.IConstants;
import com.code.aon.master.VersionManager;
import net.aonsolutions.core.pool.AonConnectionException;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.enumeration.DocumentType;
import com.code.aon.registry.enumeration.RegistryAttachmentType;
import com.code.aon.ui.admin.BookingInfo;
import com.code.aon.ui.admin.DomainInfo;
import com.code.aon.ui.admin.DomainInfoType;
import com.code.aon.ui.common.ICommonMessages;
import com.code.aon.ui.company.controller.CompanyController;
import com.code.aon.ui.config.controller.DomainSwitcher;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.form.event.IControllerListener;
import com.code.aon.ui.registry.controller.DocumentManager;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.webmail.WebmailException;
import com.esferalia.aon.entity.IEntityAlias;

public class NewDomainController implements Serializable {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private final static Logger LOGGER = LoggerFactory.getLogger(NewDomainController.class);
	
	private String domainName;
	private String domainDescription;
	private String domainSuffix;
	private String password;
	private Domain parentDomain;
	private Domain templateDomain;
	private boolean loadDefaultValuesEnabled;
	private boolean enableHeredity;
	private boolean domainManagement;
	private DomainType type;
	private boolean allowDuplicateDomain;
	private String owner;
	private boolean activeExpirationDate;
	private Date expirationDate;
	private Scope scope;
	
	private IControllerListener templateDomainFilter;
	
	public String getDomainName() {
		return domainName;
	}
	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}
	
	public String getDomainDescription() {
		return domainDescription;
	}
	public void setDomainDescription(String domainDescription) {
		this.domainDescription = domainDescription;
	}

	public String getDomainSuffix() {
		return domainSuffix;
	}
	public void setDomainSuffix(String domainSuffix) {
		this.domainSuffix = domainSuffix;
	}
	
	public boolean isLoadDefaultValuesEnabled() {
		return loadDefaultValuesEnabled;
	}
	public void setLoadDefaultValuesEnabled(boolean loadDefaultValuesEnabled) {
		this.loadDefaultValuesEnabled = loadDefaultValuesEnabled;
	}
	
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}

	public Domain getTemplateDomain() {
		return templateDomain;
	}

	public void setTemplateDomain(Domain templateDomain) {
		this.templateDomain = templateDomain;
	}
	
	public boolean isDomainManagement() {
		return domainManagement;
	}

	public void setDomainManagement(boolean domainManagement) {
		this.domainManagement = domainManagement;
	}

	public DomainType getType() {
		return type;
	}

	public void setType(DomainType type) {
		this.type = type;
	}
	
	public boolean isAllowDuplicateDomain() {
		return allowDuplicateDomain;
	}
	
	public void setAllowDuplicateDomain(boolean allowDuplicateDomain) {
		this.allowDuplicateDomain = allowDuplicateDomain;
	}
	
	public boolean isEnableHeredity() {
		return enableHeredity;
	}

	public void setEnableHeredity(boolean enableHeredity) {
		this.enableHeredity = enableHeredity;
	}

	public Domain getParentDomain() {
		return parentDomain;
	}
	
	public void setParentDomain(Domain parentDomain) {
		this.parentDomain = parentDomain;
	}
	
	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}
	
	public Date getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}
	
	public Scope getScope() {
		return scope;
	}

	public void setScope(Scope scope) {
		this.scope = scope;
	}

	public void onInit( ActionEvent event) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(Domain.class);
			Domain domain = (Domain) bean.get(DomainManager.getCurrentDomain());
			reset( domain, domain );
		} catch (ManagerBeanException e) {
			LOGGER.error( e.getMessage(), e );
		}	
	}
	
	public void reset( Domain suffixDomain, Domain parentDomain ) throws ManagerBeanException {
		setDomainName(null);
		setDomainDescription(null);
		setPassword(null);
		setOwner(null);
		setLoadDefaultValuesEnabled(true);
		setDomainManagement(false);
		setEnableHeredity(parentDomain != null);
		setScope(null);
		setActiveExpirationDate(false);
		setExpirationDate(null);
		setType(DomainType.ENTERPRISE);
		setAllowDuplicateDomain(new Boolean(AppParamUtil.getValue(AppParam.AON_ALLOW_DUPLICATE_DOMAIN)));
		setParentDomain(parentDomain);
		setTemplateDomain((Domain)BeanManager.getManagerBean(Domain.class).createNewTo());
		if ( suffixDomain != null ) {
			calculateDomainSuffix(suffixDomain);
		}
	}	
	
	private void calculateDomainSuffix( Domain domain ) {
		String value = null;
		if (StringUtils.isNotBlank( domain.getSubDomainSuffix() )) {
			value = domain.getSubDomainSuffix();
			if ( StringUtils.startsWith(value, ".") ) {
				value = StringUtils.substringAfter(value, ".");
			}
		} else {
			value = StringUtils.substringAfter(domain.getName(), "." );
		}
		int level = StringUtils.countMatches(value, ".");
		if ( level >= 2 ) {
			value = "-" + value;
		} else {
			value = "." + value;
		}
		setDomainSuffix(value);
	}
	
	protected boolean existsDomainName( String name ) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(Domain.class);
		Criteria criteria = new Criteria();
		criteria.setSkipDomainFilter(true);
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.DOMAIN_NAME), name);
		return bean.getCount(criteria) > 0;
	}
	
	public static void validateUserPassword( String password ) {
		User user = UserUtils.getInstance().getLoggedUser();
		String sent_passwd  = AdminUtil.encodeSHA(password);
		if (!StringUtils.equals(user.getPassword(), sent_passwd)) {
			String message = AonUtil.addErrorMessageFromBundle(ICommonMessages.INVALID_PASSWORD);
			throw new AbortProcessingException(message);
		}			
	}
	
	public void onSave( ActionEvent event) {
		String domainFinalName = getDomainName() + StringUtils.defaultString(getDomainSuffix()); 
		try {
			DomainController.checkDomainName(domainFinalName, 3);
		} catch (AonException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage());			
		}		
		try {
			if ( existsDomainName(domainFinalName) ) {
				String message = AonUtil.addErrorMessageFromBundle(ICommonMessages.DOMAIN_NAME_DUPLICATED, domainFinalName);
				throw new AbortProcessingException(message);			
			}			
		} catch ( ManagerBeanException e ) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage());			
		}
		
		validateUserPassword(getPassword());
		validateExpirationDate();
		
		try {			
			Domain newDomain = null;
			if ( isLoadDefaultValuesEnabled() ) {
				newDomain = createDomain(domainFinalName);
				if (! isEnableHeredity() ) {
					insertScript(newDomain.getId(), domainFinalName, IConstants.INSERT_DOMAIN_DEFAULTS_SCRIPT);	
				}
				if ( getType() == DomainType.GARAGE ) {
					insertScript(newDomain.getId(), domainFinalName, IConstants.INSERT_DOMAIN_GARAGE_DEFAULTS_SCRIPT);
				}
				copyCustomizeId(newDomain);
				addCompany(newDomain);
			} else {
				newDomain = duplicateDomain(domainFinalName);
			}
			DomainSwitcher ds = (DomainSwitcher) AonUtil.getRegisteredBean(DOMAIN_SWITCHER);
			ds.setModel(null);
			saveHistory(newDomain);			
		} catch (Throwable e) {
			LOGGER.error(">>>> onSave: ", e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}
	
	protected void copyCustomizeId( Domain newDomain ) {
		String idValue = AppParamUtil.getValue(AppParam.AON_CUSTOMIZE_HERITABLE_ID);
		if (! StringUtils.isEmpty(idValue) ) {
			ApplicationParameter ap1 = new ApplicationParameter(AppParam.AON_CUSTOMIZE_ID.getValue(), idValue);
			ap1.setDomain(newDomain.getId());
			AppParamUtil.insertParameter(ap1);
			ApplicationParameter ap2 = new ApplicationParameter(AppParam.AON_CUSTOMIZE_HERITABLE_ID.getValue(), idValue);
			ap2.setDomain(newDomain.getId());
			AppParamUtil.insertParameter(ap2);
		}		
	}
	
	protected void insertScript( Integer domain, String domainName, String scriptPath ) throws AonSQLException, AonException, IOException {
		Connection connection = null;
		try {			
			URL script = VersionManager.getScript(scriptPath);
			AonSQLFile file = new AonSQLFile(script.openStream(), CharEncoding.ISO_8859_1);
			file.setFileName(scriptPath);
			connection = DatabaseUtil.getConnection(domainName);
			AonSQLScript sqlScript = new AonSQLScript(file, connection);
			sqlScript.setDomain(domain);
			sqlScript.execute();
		} catch (AonConnectionException e) {
			throw new AonSQLException(e.getMessage(),e);
		} finally {
			DbUtils.closeQuietly(connection);
		}
	}		

	protected Domain createDomain(String name) throws ManagerBeanException {
		AuthPrincipal principal = AonUtil.getAuthPrincipal();		
		IManagerBean bean = BeanManager.getManagerBean(Domain.class);
		Domain domain = new Domain();
		domain.setCreationUser(AonUtil.getAuthPrincipal().getShortName());
		domain.setCreationDate(new Date());		
		domain.setDomainManagement( isDomainManagement() );
		domain.setType( getType() );
		domain.setParent( getParentDomain() );
		domain.setOwner( getOwner() );
		domain.setName(name);
		domain.setDescription( getDomainDescription() );
		domain.setEnableHeredity( isEnableHeredity() );
		domain.setMaxDefinedUsers(0);
		if ( isActiveExpirationDate() ) {
			domain.setExpirationDate(getExpirationDate());	
		}
		domain.setScope( getScope() );
		domain.setMaxDocumentSize(DocumentManager.MINIMUM_MAX_DOCUMENT_SIZE);
		domain.setMaxTotalDocumentSize(DocumentManager.MINIMUM_MAX_TOTAL_DOCUMENT_SIZE);
		if ( isDomainManagement() ) {
			domain.setSubDomainSuffix(name);
		}
		bean.insert(domain);
		DomainApplication da = new DomainApplication(); 
		Application application = (Application) BeanManager.getManagerBean(Application.class).get(principal.getApplicationId());
		da.setApplication(application);
		da.setDomain(domain.getId());
		BeanManager.getManagerBean(DomainApplication.class).insert(da);
		if ( getType() == DomainType.ENTERPRISE ) {
			DomainApplicationModule dam = new DomainApplicationModule();
			dam.setDomain(domain.getId());
			dam.setDomainApplication(da);
			dam.setModule(Module.AON_FINANCE);
			BeanManager.getManagerBean(DomainApplicationModule.class).insert(dam);
		}		
		return domain;		
	}
	
	private Domain updateDomain( Integer domainId  ) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(Domain.class);
		Domain domain = (Domain) bean.get(domainId);
		if ( domain != null ) {
			domain.setCreationUser(AonUtil.getAuthPrincipal().getShortName());
			domain.setCreationDate(new Date());
			domain.setModificationDate(null);
			domain.setModificationUser(null);
			bean.update(domain);
		}
		return domain;
	}
	
	private Domain duplicateDomain(String name) throws AonConnectionException, AonSQLException, ManagerBeanException {
		Connection connection = null;
		Domain newDomain = null;
		try {			
			connection = DatabaseUtil.getConnection(AonUtil.getDomainName());
			AonDomainDuplicate add = new AonDomainDuplicate(connection);
			add.setDescription(getDomainDescription());
			add.setOwner(getOwner());
			Integer parent = (getParentDomain() != null) ? getParentDomain().getId() : null;
			Integer newDomainId = add.execute(getTemplateDomain().getId(), parent, name);
			if ( newDomainId != null ) {
				newDomain = updateDomain( newDomainId );
			}
		} finally {
			DbUtils.closeQuietly(connection);
		}	
		return newDomain;
	}
	
	public IControllerListener getTemplateDomainFilter() {
		if ( this.templateDomainFilter == null ) {
			this.templateDomainFilter = new TemplateDomainFilter(parentDomain);
		}
		return this.templateDomainFilter;
	}
	
	public int getMaxDomainNameLength() {
		return DomainController.MAX_DOMAIN_NAME_LENGTH - StringUtils.length(getDomainSuffix());
	}

	public void onChangedEnableHeredity( ActionEvent event ) {
		setLoadDefaultValuesEnabled(true);
	}
	
	public void onChangedLoadDefaultValues( ActionEvent event ) {
		if (! isLoadDefaultValuesEnabled() ) {
			setActiveExpirationDate(false);	
		}
	}
	
	protected void saveHistory( Domain domain ) throws ManagerBeanException, IOException, WebmailException {
		if ( domain != null ) {
			BookingInfo bookingInfo = DomainController.getBookingInfo(domain);
			DomainInfo di = DomainInfo.getDomainInfo(domain, bookingInfo);
			di.setInfoType(DomainInfoType.INSERT);
			Company company = DomainController.getAdminCompany();
			if ( company != null ) {
				DomainController.saveHistory(di, company, RegistryAttachmentType.DOMAIN_INSERT_HISTORY);
			}
			Address[] emails = DomainController.getNotificationEmails(domain.getId());
			if (! ArrayUtils.isEmpty(emails) ) {
				String subject = AonUtil.getMessage(ICommonMessages.DOMAIN_INSERT_EMAIL_SUBJECT, domain.getName());
				String content = DomainController.getEmailContent(domain, di, ICommonMessages.DOMAIN_INSERT_EMAIL_BODY);
				DomainController.sendNotificationEmail(emails, subject, content, null);
			}
		}
	}
	
	protected void validateExpirationDate() {
		if ( isActiveExpirationDate() && (getExpirationDate() != null) ) {
			Date today = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
			if ( getExpirationDate().compareTo(today) <= 0 ) {
				String message = AonUtil.addErrorMessageFromBundle(ICommonMessages.DOMAIN_EXPIRATION_ERROR);
				throw new AbortProcessingException(message);
			}
		}
	}		
	
	private Scope getEnterpriseScope( Domain domain ) throws ManagerBeanException {
		Scope enterpriseScope = getScope();
		if ( enterpriseScope == null ) {
			if ( getParentDomain() != null ) {
				enterpriseScope = CompanyController.obtainScope(getParentDomain().getId());
			}
			if ( enterpriseScope == null ) {
				enterpriseScope = CompanyController.obtainScope(domain.getId());
			}
		}
		return enterpriseScope;
	}
	
	protected void addCompany( Domain domain ) {
		try {
			Company company = new Company();
			company.setActive(true);
			company.setDomain(domain.getId());
			company.setDocumentType(DocumentType.CIF);
			company.setName( StringUtils.left(domain.getDescription(), 64) );
			String alias = StringUtils.upperCase( StringUtils.substringBefore(domain.getName(), "."));
			company.setAlias( StringUtils.left(alias, 32) );
			BeanManager.getManagerBean(Company.class).insert(company);
			Enterprise enterprise = CompanyController.addEnterprise(company, getEnterpriseScope(domain));
			RegistryAddress address = CompanyController.getEmpyMainAddress(company);
			address.setGeozone(null);
			BeanManager.getManagerBean(RegistryAddress.class).insert(address);
			CompanyController.insertWorkPlace(address, enterprise);
		} catch ( ManagerBeanException e ) {
			LOGGER.error( "Error creating company for " + domain, e );
		}
	}
	
	public boolean isActiveExpirationDate() {
		return activeExpirationDate;
	}

	public void setActiveExpirationDate(boolean activeExpirationDate) {
		this.activeExpirationDate = activeExpirationDate;
	}

	private static class TemplateDomainFilter extends ControllerAdapter {
		
		private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
		
		private Domain parentDomain;
		
		public TemplateDomainFilter(Domain parentDomain) {
			this.parentDomain = parentDomain;
		}

		@Override
		public void beforeModelInitialized(ControllerEvent event)
				throws ControllerListenerException {
			IController controller = event.getController();
			try {					
				controller.getCriteria().setSkipDomainFilter(true);						
				if ( parentDomain != null ) {
					if ( AonUtil.getRoleManager().isSysAdmin() ) {
						String management = controller.getFieldName(IEntityAlias.DOMAIN_DOMAIN_MANAGEMENT);
						controller.getCriteria().addEqualExpression(management, Boolean.FALSE);
					} else {
						String parent = controller.getFieldName(IEntityAlias.DOMAIN_PARENT_ID);
						controller.getCriteria().addEqualExpression(parent, parentDomain.getId());	
					}							
				} else {
					DomainSwitcher ds = (DomainSwitcher) AonUtil.getRegisteredBean(DOMAIN_SWITCHER);
					if (ds.getType() != DomainType.ADMIN ) {
						controller.getCriteria().addNullExpression("Domain.parent");
					}
				}
				String type = controller.getFieldName(IEntityAlias.DOMAIN_TYPE);
				controller.getCriteria().addNotEqualExpression(type, DomainType.ADMIN);
				String active = controller.getFieldName(IEntityAlias.DOMAIN_ACTIVE);
				controller.getCriteria().addEqualExpression(active, Boolean.TRUE);
			} catch (ManagerBeanException e) {
				LOGGER.error("Error filtering domain", e);
			}
		}
		
	}
	
}