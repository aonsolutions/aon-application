package com.code.aon.desktop.controller;

import static com.code.aon.desktop.Domain.DOMAIN_PARENT_DOMAIN;

import java.util.ArrayList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.naming.Name;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.bridge.jmx.mbean.IConsoleAdmin;
import com.code.aon.bridge.plugin.UserManager;
import com.code.aon.bridge.plugin.Utils;
import com.code.aon.common.BasicManagerBean;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.dao.ldap.LdapDAO;
import com.code.aon.desktop.Domain;
import com.code.aon.desktop.IDesktopConstants;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.jaas.deployment.DeploymentException;
import com.code.aon.ldap.BasicLdap;
import com.code.aon.ldap.IAonObjectClasses;
import com.code.aon.ldap.NameResolver;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.desktop.applications.ApplicationsManager;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;

public class DomainController extends BasicController implements IDesktopConstants, IAonObjectClasses {

	private final static Logger LOGGER = LoggerFactory.getLogger(DomainController.class);
	
	private BasicManagerBean ldapManagerBean;
	
	private ApplicationsManager.App selectedApplication;
	
	private DataModel registeredDomains;

	public ApplicationsManager.App getSelectedApplication() {
		return selectedApplication;
	}

	public void setSelectedApplication(ApplicationsManager.App selectedApplication) {
		this.selectedApplication = selectedApplication;
	}

	public String getCurrentDomainApplicationURL() throws ManagerBeanException {
		if ( getRegisteredDomains().isRowAvailable() ) {
			Domain domain = (Domain) getRegisteredDomains().getRowData();
			StringBuffer url = new StringBuffer( "http://" );
			url.append( domain.getCommonName() );
			url.append( selectedApplication.getContext() );
			return url.toString();				
		}
		return null;
	}
	
	@Override
	public IManagerBean getManagerBean() throws ManagerBeanException {
		if (this.ldapManagerBean == null) {
			LdapDAO dao = new LdapDAO(Domain.class);
			this.ldapManagerBean = new BasicManagerBean(dao);
		}
		return this.ldapManagerBean;
	}
	
	public Domain getCurrentDomain() {
		Domain domain = null;
		AonUserController userController = (AonUserController) AonUtil.getRegisteredBean(CURRENT_USER_CONTROLLER_NAME);
		try {
			Name id = NameResolver.getDomainDN(userController.getDomain());
			domain = (Domain) getManagerBean().get( id );
		} catch (ManagerBeanException e) {
			LOGGER.error( "Error obteniendo de LDAP el aonDomain " + userController.getDomain(), e );
		}
		return domain;
	}	

	public void onInitRegisteredDomains( ActionEvent event ) throws ManagerBeanException {
		List<Domain> domains = new ArrayList<Domain>();
		Domain domain = getCurrentDomain(); 
		domains.add( domain );
		clearCriteria();
		Criteria criteria = getCriteria();
		criteria.addEqualExpression(getFieldName(DOMAIN_PARENT_DOMAIN), domain.getId());
		List<ITransferObject> list = getManagerBean().getList(criteria);
		if (! list.isEmpty() ) {
			BasicLdap ldap = new BasicLdap();
			for( ITransferObject to : list ) {
				Domain childDomain = (Domain) to;
				Name dn = NameResolver.getDomainApplicationUsersDN(childDomain.getCommonName(), selectedApplication.getId());
				if ( ldap.exists(dn, IAonObjectClasses.ORGANIZATIONAL_UNIT) ) {
					domains.add( childDomain );
				}
			}					
		}
		this.registeredDomains = new ListDataModel(domains);
	}
	
	public DataModel getRegisteredDomains() {
		return registeredDomains;
	}
	
	public void flushAuthenticationCache( String userName ) throws DeploymentException {
		IConsoleAdmin console = Utils.getSecurityConsole();
		AuthPrincipal principal = null;
		if ( userName != null ) {
			String name = UserUtils.getInstance().getPrincipal().getName();
			principal = new AuthPrincipal( userName + name.substring(name.indexOf('@')));			
		}
		console.flushAuthenticationCache(UserManager.LDAP_SECURITY_DOMAIN, principal);
	}
	
}