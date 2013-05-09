package com.code.aon.ui.manager.event;

import static com.code.aon.ui.company.controller.ICompanyConstants.COMPANY_CONTROLLER_NAME;
import static com.code.aon.ui.manager.controller.DomainController.DEFAULT_MAX_DOCUMENT_SIZE;
import static com.code.aon.ui.manager.controller.DomainController.DEFAULT_MAX_TOTAL_DOCUMENT_SIZE;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.config.Bank;
import com.code.aon.config.BankAccount;
import com.code.aon.ldap.LdapException;
import com.code.aon.manager.DBConnnection;
import com.code.aon.manager.Domain;
import com.code.aon.manager.DomainUser;
import com.code.aon.registry.RegistryBank;
import com.code.aon.ui.company.controller.CompanyController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.manager.UserType;
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
			ensureDomainValues(dc);
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
		DomainController dc = (DomainController) event.getController();
		Domain domain = dc.getDomain();
		try {
			dc.insertOrUpdateAccessPolicy();
			updateCurrentDomain(domain);
			if ( dc.getPreviousMaxTotalDocumentSize() != null ) {
				getManager().getLogger().maxTotalDocumentSizeChanged( domain,
					dc.getPreviousMaxTotalDocumentSize(), domain.getMaxTotalDocumentSize() );
			}
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
			throw new ControllerListenerException( e.getMessage(), e );
		}		
	}
	
	@Override
	public void afterEditSearch(ControllerEvent event)
			throws ControllerListenerException {
		DomainController dc = (DomainController) event.getController();
		dc.updateParentDomains();
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
		ManagerController.updateController(ALIAS_CONTROLLER_NAME, domain.getId());
		ManagerController.updateController(IManagerConstants.BEAN_SIGNATURE, domain.getId());
		ManagerController.updateController(IManagerConstants.BEAN_MAIL_ACCOUNT, domain.getId());		
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
	
	private void ensureDomainValues( DomainController dc ) throws DAOException, ManagerBeanException {
		Domain domain = dc.getDomain();
		domain.construct( dc.getLdapDAO() );
		boolean updated = false;		
		if ( domain.getDomainManagement() && StringUtils.isEmpty(domain.getSubDomainSuffix()) ) {
			domain.setSubDomainSuffix(DEFAULT_SUBDOMAIN_SUFFIX);
			updated = true;
		}
		if ( domain.isDocumentManagement() ) {
			if ( domain.getMaxDocumentSize() == null ) {
				domain.setMaxDocumentSize(DEFAULT_MAX_DOCUMENT_SIZE);
				updated = true;
			}
			if ( domain.getMaxTotalDocumentSize() == null ) {
				domain.setMaxTotalDocumentSize(DEFAULT_MAX_TOTAL_DOCUMENT_SIZE);
				updated = true;
			}
		}
		if ( updated ) {
			dc.getManagerBean().update(domain);			
		}
	}
	
}