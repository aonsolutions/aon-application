package com.code.aon.ui.admin.controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.cfg.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.util.AdminUtil;
import com.code.aon.config.Application;
import com.code.aon.config.Domain;
import com.code.aon.config.DomainApplication;
import com.code.aon.config.User;
import com.code.aon.config.enumeration.DomainType;
import com.code.aon.dbutils.AonDomainDuplicate;
import com.code.aon.dbutils.AonSQLException;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.config.controller.ConfigConstants;
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
	private Domain templateDomain;
	private boolean loadDefaultValuesEnabled;
	
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
	
	private Connection getConnection( Properties dbProperties ) throws SQLException {
		DbUtils.loadDriver(dbProperties.getProperty(Environment.DRIVER));
		String url = dbProperties.getProperty(Environment.URL);
		String user = dbProperties.getProperty(Environment.USER);
		String password = dbProperties.getProperty(Environment.PASS);
		Connection connection = DriverManager.getConnection(url, user, password);
		return connection;	
	}	

	public void onInit( ActionEvent event) {
		setDomainName(null);
		setDomainDescription(null);
		setPassword(null);
		setLoadDefaultValuesEnabled(true);
		setTemplateDomain(new Domain());
		
		try {
			IManagerBean bean = BeanManager.getManagerBean(Domain.class);
			Domain domain = (Domain) bean.get(DomainManager.getCurrentDomain());
			if (StringUtils.isNotBlank( domain.getSubDomainSuffix() )) {
				setDomainSuffix( domain.getSubDomainSuffix() );	
			} else {
				setDomainSuffix( StringUtils.substringAfter(domain.getName(), "." ));
			}
			if (!StringUtils.startsWith(getDomainSuffix(), ".")) {
				setDomainSuffix( "." + getDomainSuffix());
			}
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage("No se pudo recuperar el sufijo del dominio padre. Escriba el nombre completo.");
		}	
	}
	
	private boolean existsDomainName( String name ) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(Domain.class);
		Criteria criteria = new Criteria();
		criteria.setSkipDomainFilter(true);
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.DOMAIN_NAME), name);
		return bean.getCount(criteria) > 0;
	}
	
	public void onSave( ActionEvent event) {
		String domainFinalName = getDomainName() + getDomainSuffix(); 
		Pattern p = Pattern.compile("[A-Z\\d][A-Z\\d.-]{1,61}[A-Z\\d]$",Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(domainFinalName);
		if (!m.matches() ) {
			String message = "El formato del nombre del dominio no es válido, debe comenzar y terminar por una letra o número. Solo puede contener letras, números y los caracteres '-' (guión) o '.' (punto).";
			AonUtil.addErrorMessage(message);
			throw new AbortProcessingException(message);
		}
		
		try {
			if ( existsDomainName(domainFinalName) ) {
				String message = "Ya existe un dominio '" + domainFinalName + "'";
				AonUtil.addErrorMessage(message);
				throw new AbortProcessingException(message);			
			}			
		} catch ( ManagerBeanException e ) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage());			
		}
		
		User user = UserUtils.getInstance().getLoggedUser();
		String sent_passwd  = AdminUtil.encodeSHA(getPassword());
		if (!StringUtils.equals(user.getPassword(), sent_passwd)) {
			String msg = "La contraseña no es correcta.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}	
		
		Integer newDomainId = null;
		try {			
			if ( isLoadDefaultValuesEnabled() ) {
				newDomainId = createDomain(DomainManager.getCurrentDomain(), domainFinalName, getDomainDescription());
			} else {
				newDomainId = duplicateDomain(getTemplateDomain().getId(), domainFinalName, getDomainDescription());
			}
			if ( newDomainId != null ) {
				DomainSwitcher switcher = (DomainSwitcher) AonUtil.getRegisteredBean(ConfigConstants.DOMAIN_SWITCHER);
				switcher.select(newDomainId, getDomainDescription() );				
			}
		} catch (Throwable e) {
			LOGGER.error(">>>> onSave: ", e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}

	private Integer createDomain(Integer parent, String name, String description) throws ManagerBeanException {
		AuthPrincipal principal = AonUtil.getAuthPrincipal();		
		IManagerBean bean = BeanManager.getManagerBean(Domain.class);
		Domain domain = new Domain();
		Domain parentDomain = (Domain) bean.get(parent); 
		domain.setParent( parentDomain );
		domain.setOwner( principal.getShortName() + "@" + parentDomain.getName() );
		domain.setName(name);
		domain.setDescription(description);
		domain.setType(DomainType.ENTERPRISE);
		domain.setEnableHeredity(true);
		bean.insert(domain);
		DomainApplication da = new DomainApplication(); 
		Application application = (Application) BeanManager.getManagerBean(Application.class).get(principal.getApplicationId());
		da.setApplication(application);
		da.setDomain(domain.getId());
		BeanManager.getManagerBean(DomainApplication.class).insert(da);
		return domain.getId();
	}
	
	private Integer duplicateDomain(Integer parent, String name, String description) throws AonSQLException, SQLException {
		Integer newDomainId = null;
		Connection connection = null;
		try {			
			Properties properties = DataSourceUtil.getDBProperties();
			connection = getConnection(properties);				
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
						String parent = controller.getFieldName(IEntityAlias.DOMAIN_PARENT_ID);
						controller.getCriteria().addEqualExpression(parent, DomainManager.getCurrentDomain());
						String active = controller.getFieldName(IEntityAlias.DOMAIN_ACTIVE);
						controller.getCriteria().addEqualExpression(active, Boolean.TRUE);
					} catch (ManagerBeanException e) {
						LOGGER.error("Error filtering offer", e);
					}
				}
			};
		}
		return this.templateDomainFilter;
	}
	
}