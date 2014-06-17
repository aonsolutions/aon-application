package com.code.aon.ui.admin.controller;

import static com.code.aon.ui.config.controller.ConfigConstants.DOMAIN_SWITCHER;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.sql.Connection;
import java.util.Date;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.lang.CharEncoding;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.AonException;
import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.enumeration.AppParam;
import com.code.aon.common.util.AdminUtil;
import com.code.aon.config.Application;
import com.code.aon.config.ApplicationParameter;
import com.code.aon.config.Domain;
import com.code.aon.config.DomainApplication;
import com.code.aon.config.Scope;
import com.code.aon.config.User;
import com.code.aon.config.enumeration.DomainType;
import com.code.aon.config.util.AppParamUtil;
import com.code.aon.dbutils.AonDomainDuplicate;
import com.code.aon.dbutils.AonSQLException;
import com.code.aon.dbutils.AonSQLFile;
import com.code.aon.dbutils.AonSQLScript;
import com.code.aon.dbutils.DatabaseUtil;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.master.IConstants;
import com.code.aon.master.VersionManager;
import com.code.aon.pool.AonConnectionException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.common.ICommonMessages;
import com.code.aon.ui.config.controller.DomainSwitcher;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.form.event.IControllerListener;
import com.code.aon.ui.registry.controller.DocumentManager;
import com.code.aon.ui.util.AonUtil;
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
	private String owner;
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
		setExpirationDate(null);
		setType(DomainType.ENTERPRISE);
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
	
	private boolean existsDomainName( String name ) throws ManagerBeanException {
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
		
		try {			
			if ( isLoadDefaultValuesEnabled() ) {
				Integer newDomain = createDomain(domainFinalName);
				if (! isEnableHeredity() ) {
					insertDefaults(newDomain,domainFinalName);	
				}
				copyCustomizeId(newDomain);
			} else {
				duplicateDomain(domainFinalName);
			}
			DomainSwitcher ds = (DomainSwitcher) AonUtil.getRegisteredBean(DOMAIN_SWITCHER);
			ds.setModel(null);			
		} catch (Throwable e) {
			LOGGER.error(">>>> onSave: ", e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}
	
	private void copyCustomizeId( Integer newDomain ) {
		String idValue = AppParamUtil.getValue(AppParam.AON_CUSTOMIZE_HERITABLE_ID);
		if (! StringUtils.isEmpty(idValue) ) {
			ApplicationParameter ap1 = new ApplicationParameter(AppParam.AON_CUSTOMIZE_ID.getValue(), idValue);
			ap1.setDomain(newDomain);
			AppParamUtil.insertParameter(ap1);
			ApplicationParameter ap2 = new ApplicationParameter(AppParam.AON_CUSTOMIZE_HERITABLE_ID.getValue(), idValue);
			ap2.setDomain(newDomain);
			AppParamUtil.insertParameter(ap2);
		}		
	}
	
	private void insertDefaults( Integer domain, String domainName ) throws AonSQLException, AonException, IOException {
		Connection connection = null;
		try {			
			URL script = VersionManager.getScript(IConstants.INSERT_DOMAIN_DEFAULTS_SCRIPT);
			AonSQLFile file = new AonSQLFile(script.openStream(), CharEncoding.ISO_8859_1);
			file.setFileName(IConstants.INSERT_DOMAIN_DEFAULTS_SCRIPT);
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

	private Integer createDomain(String name) throws ManagerBeanException {
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
		domain.setExpirationDate(getExpirationDate());
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
		return domain.getId();		
	}
	
	private void updateDomain( Integer domainId  ) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(Domain.class);
		Domain domain = (Domain) bean.get(domainId);
		if ( domain != null ) {
			domain.setParent(getParentDomain());
			domain.setCreationUser(AonUtil.getAuthPrincipal().getShortName());
			domain.setCreationDate(new Date());
			domain.setModificationDate(null);
			domain.setModificationUser(null);
			bean.update(domain);
		}
	}
	
	private void duplicateDomain(String name) throws AonConnectionException, AonSQLException, ManagerBeanException {
		Connection connection = null;
		try {			
			connection = DatabaseUtil.getConnection(AonUtil.getDomainName());
			AonDomainDuplicate add = new AonDomainDuplicate(connection);
			add.setDescription(getDomainDescription());
			add.setOwner(getOwner());
			Integer newDomainId = add.execute(getTemplateDomain().getId(), name);
			if ( newDomainId != null ) {
				updateDomain( newDomainId );
			}
		} finally {
			DbUtils.closeQuietly(connection);
		}		
	}
	
	public IControllerListener getTemplateDomainFilter() {
		if ( this.templateDomainFilter == null ) {
			this.templateDomainFilter = new TemplateDomainFilter(parentDomain);
		}
		return this.templateDomainFilter;
	}
	
	public int getMaxDomainNameLength() {
		return DomainController.DEFAULT_MAX_TOTAL_DOCUMENT_SIZE - StringUtils.length(getDomainSuffix());
	}

	public void onChangedEnableHeredity( ActionEvent event ) {
		setLoadDefaultValuesEnabled(true);
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
					controller.getCriteria().addNullExpression("Domain.parent");
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