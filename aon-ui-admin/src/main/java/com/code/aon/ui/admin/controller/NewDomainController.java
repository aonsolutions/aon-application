package com.code.aon.ui.admin.controller;

import static com.code.aon.ui.admin.controller.IAdminConstants.BUNDLE_NAME;
import static com.code.aon.ui.admin.controller.IAdminConstants.DOMAIN_INVALID_NAME;
import static com.code.aon.ui.admin.controller.IAdminConstants.DOMAIN_NAME_DUPLICATED;
import static com.code.aon.ui.admin.controller.IAdminConstants.INVALID_PASSWORD;
import static com.code.aon.ui.common.ICommonConstants.AON_CUSTOMIZE_HERITABLE_ID;
import static com.code.aon.ui.common.ICommonConstants.AON_CUSTOMIZE_ID;
import static com.code.aon.ui.config.controller.ConfigConstants.DOMAIN_SWITCHER;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.AonException;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.util.AdminUtil;
import com.code.aon.common.util.ConnectionProvider;
import com.code.aon.config.Application;
import com.code.aon.config.ApplicationParameter;
import com.code.aon.config.Domain;
import com.code.aon.config.DomainApplication;
import com.code.aon.config.User;
import com.code.aon.config.enumeration.DomainType;
import com.code.aon.config.util.AppParamUtil;
import com.code.aon.dbutils.AonDomainDuplicate;
import com.code.aon.dbutils.AonSQLException;
import com.code.aon.dbutils.AonSQLFile;
import com.code.aon.dbutils.AonSQLScript;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.master.IConstants;
import com.code.aon.master.VersionManager;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.config.controller.DomainSwitcher;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.form.event.IControllerListener;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.util.DataSourceUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class NewDomainController {

	private final static Logger LOGGER = LoggerFactory.getLogger(NewDomainController.class);
	
	private String domainName;
	private String domainDescription;
	private String domainSuffix;
	private String password;
	private Domain parentDomain;
	private Domain templateDomain;
	private boolean loadDefaultValuesEnabled;
	private boolean domainManagement;
	private DomainType type;
	
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

	public Domain getParentDomain() {
		return parentDomain;
	}
	
	public void setParentDomain(Domain parentDomain) {
		this.parentDomain = parentDomain;
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
	
	public void reset( Domain parentDomain, Domain suffixDomain ) throws ManagerBeanException {
		setDomainName(null);
		setDomainDescription(null);
		setPassword(null);
		setLoadDefaultValuesEnabled(true);
		setDomainManagement(false);
		setType(DomainType.ENTERPRISE);
		setParentDomain(parentDomain);
		setTemplateDomain((Domain)BeanManager.getManagerBean(Domain.class).createNewTo());
		if ( suffixDomain != null ) {
			if (StringUtils.isNotBlank( suffixDomain.getSubDomainSuffix() )) {
				setDomainSuffix( suffixDomain.getSubDomainSuffix() );	
			} else {
				setDomainSuffix( StringUtils.substringAfter(suffixDomain.getName(), "." ));
			}
			if (!StringUtils.startsWith(getDomainSuffix(), ".")) {
				setDomainSuffix( "." + getDomainSuffix());
			}				
		}		
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
			String message = AonUtil.addErrorMessageFromBundle(BUNDLE_NAME, INVALID_PASSWORD);
			throw new AbortProcessingException(message);
		}			
	}
	
	public void onSave( ActionEvent event) {
		String domainFinalName = getDomainName() + StringUtils.defaultString(getDomainSuffix()); 
		Pattern p = Pattern.compile("[A-Z\\d][A-Z\\d.-]{1,61}[A-Z\\d]$",Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(domainFinalName);
		if ( (!m.matches()) || (domainFinalName.length() > 64) ) {
			String message = AonUtil.addErrorMessageFromBundle(BUNDLE_NAME, DOMAIN_INVALID_NAME);
			throw new AbortProcessingException(message);
		}
		
		try {
			if ( existsDomainName(domainFinalName) ) {
				String message = AonUtil.addErrorMessageFromBundle(BUNDLE_NAME, DOMAIN_NAME_DUPLICATED, domainFinalName);
				throw new AbortProcessingException(message);			
			}			
		} catch ( ManagerBeanException e ) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage());			
		}
		
		validateUserPassword(getPassword());
		
		
		try {			
			if ( isLoadDefaultValuesEnabled() ) {
				Integer newDomain = createDomain(domainFinalName, getDomainDescription());
				insertDefaults(newDomain);
				copyCustomizeId(newDomain);
			} else {
				duplicateDomain(getTemplateDomain().getId(), domainFinalName, getDomainDescription());
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
		String idValue = AppParamUtil.getValue(AON_CUSTOMIZE_HERITABLE_ID);
		if (! StringUtils.isEmpty(idValue) ) {
			ApplicationParameter ap1 = new ApplicationParameter(AON_CUSTOMIZE_ID, idValue);
			ap1.setDomain(newDomain);
			AppParamUtil.insertParameter(ap1);
			ApplicationParameter ap2 = new ApplicationParameter(AON_CUSTOMIZE_HERITABLE_ID, idValue);
			ap2.setDomain(newDomain);
			AppParamUtil.insertParameter(ap2);
		}		
	}
	
	private void insertDefaults( Integer domain ) throws AonSQLException, AonException, IOException {
		Connection connection = null;
		try {			
			String scriptName = IConstants.INSERT_DOMAIN_DEFAULTS_SCRIPT;
			if ( getParentDomain() != null ) {
				scriptName = IConstants.INSERT_DOMAIN_FROM_PARENT_DEFAULTS_SCRIPT;
			}
			URL script = VersionManager.getScript(scriptName);
			AonSQLFile file = new AonSQLFile(script.openStream());
			file.setFileName(scriptName);
			Properties properties = DataSourceUtil.getDBProperties();
			connection = ConnectionProvider.getConnection(properties);
			AonSQLScript sqlScript = new AonSQLScript(file, connection);
			sqlScript.setDomain(domain);
			sqlScript.execute();
		} finally {
			DbUtils.closeQuietly(connection);
		}
	}		

	private Integer createDomain(String name, String description) throws ManagerBeanException {
		AuthPrincipal principal = AonUtil.getAuthPrincipal();		
		IManagerBean bean = BeanManager.getManagerBean(Domain.class);
		Domain domain = new Domain();
		domain.setDomainManagement( isDomainManagement() );
		domain.setType( getType() );
		domain.setParent( getParentDomain() );
		domain.setOwner( principal.getShortName() );
		domain.setName(name);
		domain.setDescription(description);
		domain.setEnableHeredity(true);
		bean.insert(domain);
		DomainApplication da = new DomainApplication(); 
		Application application = (Application) BeanManager.getManagerBean(Application.class).get(principal.getApplicationId());
		da.setApplication(application);
		da.setDomain(domain.getId());
		BeanManager.getManagerBean(DomainApplication.class).insert(da);
		return domain.getId();		
	}
	
	private Integer duplicateDomain(Integer parent, String name, String description) throws AonSQLException, SQLException, AonException {
		Integer newDomainId = null;
		Connection connection = null;
		try {			
			Properties properties = DataSourceUtil.getDBProperties();
			connection = ConnectionProvider.getConnection(properties);				
			AonDomainDuplicate add = new AonDomainDuplicate(connection);
			newDomainId = add.execute(getTemplateDomain().getId(), name, description);
		} finally {
			DbUtils.closeQuietly(connection);
		}
		return newDomainId;		
	}
	
	public IControllerListener getTemplateDomainFilter() {
		if ( this.templateDomainFilter == null ) {
			this.templateDomainFilter = new ControllerAdapter() {
				@Override
				public void beforeModelInitialized(ControllerEvent event)
						throws ControllerListenerException {
					IController controller = event.getController();
					try {					
						controller.getCriteria().setSkipDomainFilter(true);						
						if ( parentDomain != null ) {
							String parent = controller.getFieldName(IEntityAlias.DOMAIN_PARENT_ID);
							controller.getCriteria().addEqualExpression(parent, parentDomain.getId());							
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
			};
		}
		return this.templateDomainFilter;
	}
	
}