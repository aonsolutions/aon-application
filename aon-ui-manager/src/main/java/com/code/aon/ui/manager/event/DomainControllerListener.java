package com.code.aon.ui.manager.event;

import static com.code.aon.ldap.IAonObjectClasses.ORGANIZATIONAL_UNIT;
import static com.code.aon.ui.company.controller.ICompanyConstants.COMPANY_CONTROLLER_NAME;

import javax.naming.Name;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.config.Bank;
import com.code.aon.config.BankAccount;
import com.code.aon.ldap.IAonObjectClasses;
import com.code.aon.ldap.ILdapConstants;
import com.code.aon.ldap.LdapException;
import com.code.aon.ldap.NameResolver;
import com.code.aon.manager.DBConnnection;
import com.code.aon.manager.Domain;
import com.code.aon.manager.DomainUser;
import com.code.aon.registry.RegistryBank;
import com.code.aon.ui.company.controller.CompanyController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.manager.UserType;
import com.code.aon.ui.manager.controller.AliasController;
import com.code.aon.ui.manager.controller.DomainApplicationController;
import com.code.aon.ui.manager.controller.DomainController;
import com.code.aon.ui.manager.controller.DomainDBConnectionController;
import com.code.aon.ui.manager.controller.DomainUserController;
import com.code.aon.ui.manager.controller.IManagerConstants;
import com.code.aon.ui.manager.controller.ManagerController;
import com.code.aon.ui.util.AonUtil;

public class DomainControllerListener extends ControllerAdapter implements IManagerConstants {

	private final static Logger LOGGER = LoggerFactory.getLogger(DomainControllerListener.class);

	private ManagerController getManager() {
		return (ManagerController) AonUtil.getRegisteredBean(MANAGER_CONTROLLER_NAME);
	}
	
	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		DomainController domainController = (DomainController) event.getController();
		Domain domain = domainController.getDomain();
		try {
			domainController.init();
			if ( getManager().getUserType() == UserType.PARENT ) {
				domain.setParentDomain( getManager().getCurrentDomain() );
			}
			domainController.updateParentDomains();
			DomainUser administrator = new DomainUser();
			administrator.setUid(ADMIN_USER);
			administrator.setName(USUARIO_PROFILE);
			domain.setAdministrator(administrator);
			domainController.setEnterpriseRecipient(false);
			RegistryBank registryBank = new RegistryBank();
			registryBank.setBank( new Bank() );
			registryBank.setBankAccount( new BankAccount() );
			domainController.setRegistryBank( registryBank );
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
			throw new ControllerListenerException( e.getMessage(), e );
		}		
	}

	@Override
	public void beforeBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		DomainController domainController = (DomainController) event.getController();
		Domain domain = domainController.getDomain();
		String newName = domainController.getDomainName(domain.getCommonName());
		domain.setCommonName(newName);
	}

	@Override
	public void afterBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		DomainController dc = (DomainController) event.getController();
		Domain domain = dc.getDomain();
		try {
			updateDomain(domain);
			dc.insertOrUpdateAccessPolicy();
			DBConnnection dbc = dc.createAndRegister(domain);
			dc.registerApplication(AON_DESKTOP, dbc);
			dc.registerApplication(AON_MANAGER, dbc);
			dc.registerApplication(AON_WEBMAIL, null);
			updateDomainManagement(dc, false);
			initCompanyData(dc);
			addAdminUser(dc, dbc);
			updateAonDBConnection(dc, dbc);
		} catch (Throwable e) {
			LOGGER.error(e.getMessage(), e);
			dc.removeDomain( domain );
			throw new ControllerListenerException( e.getMessage(), e );
		}
		getManager().getLogger().domainAddded(domain);
	}

	@Override
	public void beforeBeanRemoved(ControllerEvent event)
			throws ControllerListenerException {
		DomainController domainController = (DomainController) event.getController();
		try {		
			domainController.removeDBs( domainController.getDomain() );
			domainController.removeMailAccounts();
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
			throw new ControllerListenerException( e.getMessage(), e );
		}	
	}

	@Override
	public void afterBeanRemoved(ControllerEvent event)
			throws ControllerListenerException {
		Domain domain = (Domain) event.getController().getTo();
		getManager().getLogger().domainRemoved(domain);
	}

	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		DomainController dc = (DomainController) event.getController();
		try {
			ensureAliases(dc);
			updateDomain(dc.getDomain());
			dc.init();
			dc.updateParentDomains();
			updateAonDBConnection(dc, null);
		} catch (Throwable e) {
			LOGGER.error(e.getMessage(), e);
			throw new ControllerListenerException( e.getMessage(), e );
		}		
	}

	@Override
	public void afterBeanUpdated(ControllerEvent event)
			throws ControllerListenerException {
		DomainController domainController = (DomainController) event.getController();
		try {
			domainController.insertOrUpdateAccessPolicy();
			updateCurrentDomain(domainController.getDomain());
			updateDomainManagement(domainController, true);
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
			throw new ControllerListenerException( e.getMessage(), e );
		}		
	}

	private void updateCurrentDomain( Domain selectedDomain ) {
		Domain currentDomain = getManager().getCurrentDomain();
		if ( StringUtils.equals(selectedDomain.getCommonName(), currentDomain.getCommonName()) ) {
			getManager().setCurrentDomain(selectedDomain);
		}
	}
	
	private void updateDomain( Domain domain ) {
		DomainApplicationController dac = (DomainApplicationController) AonUtil.getRegisteredBean(DOMAIN_APPLICATION_CONTROLLER_NAME);
		dac.updateBaseDN(domain.getId());
		DomainUserController duc = (DomainUserController) AonUtil.getRegisteredBean(DOMAIN_USER_CONTROLLER_NAME);
		duc.updateBaseDN(domain.getId());		
		DomainDBConnectionController ddbc = (DomainDBConnectionController) AonUtil.getRegisteredBean(DOMAIN_DB_CONNECTION_CONTROLLER_NAME);
		ddbc.updateBaseDN(domain.getId());		
		AliasController ac = (AliasController) AonUtil.getRegisteredBean(ALIAS_CONTROLLER_NAME);
		ac.updateBaseDN(domain.getId());
		ac.onCancel(null);
	}

	private void updateDomainManagement( DomainController domainController, boolean updated ) throws ManagerBeanException {
		Domain domain = domainController.getDomain();
		if ( domainController.isDocumentManagementChanged() ) {
			getManager().getLogger().documental(domain);
		}
		if ( domainController.isUserManagementChanged() ) {
			if ( updated && (!domain.getUserManagement()) ) {
				DomainUserController duc = (DomainUserController) AonUtil.getRegisteredBean(DOMAIN_USER_CONTROLLER_NAME);
				duc.deactiveUsers();
			}
			getManager().getLogger().multiUser(domain);
		}
		if ( domainController.isDomainManagementChanged() ) {
			getManager().getLogger().multiDomain(domain);
		}
	}
	
	private void initCompanyData( DomainController dc) {
		if (! getManager().isAdministrator() ) {
			CompanyController companyController = (CompanyController) AonUtil.getRegisteredBean(COMPANY_CONTROLLER_NAME);
			companyController.onLoad(null);
			dc.setShowCompanyWindow(true);
		}		
	}
	
	private void addAdminUser( DomainController dc, DBConnnection dbc ) throws ManagerBeanException, LdapException {		
		DomainUserController duc = (DomainUserController) AonUtil.getRegisteredBean(DOMAIN_USER_CONTROLLER_NAME);		
		duc.createUser(dbc, dc.getDomain().getAdministrator());
		dc.getManagerBean().update(dc.getDomain());
	}

	private void updateAonDBConnection( DomainController dc, DBConnnection dbc ) throws ManagerBeanException {
		DomainDBConnectionController ddbcc = (DomainDBConnectionController) AonUtil.getRegisteredBean(DOMAIN_DB_CONNECTION_CONTROLLER_NAME);
		if ( dbc == null ) {
			dbc = ddbcc.getMasterConnection();
		}
		dc.setAonDB( ddbcc.updateAonDBConnection(dbc) );
	}		
	
	private void ensureAliases( DomainController dc ) throws DAOException {
		String domain = dc.getDomain().getCommonName();
		Name aliasesDN = NameResolver.getAliasesDN(domain);
		if (! dc.getLdapDAO().exists(aliasesDN, ORGANIZATIONAL_UNIT) ) {
			dc.getLdapDAO().addOrganizationUnit(aliasesDN);
		}
	}

}