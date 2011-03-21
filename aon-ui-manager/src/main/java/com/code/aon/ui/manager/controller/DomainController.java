package com.code.aon.ui.manager.controller;

import static com.code.aon.ui.company.controller.ICompanyConstants.COMPANY_CONTROLLER_NAME;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import javax.faces.application.FacesMessage;
import javax.faces.convert.Converter;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;
import javax.naming.Context;
import javax.naming.Name;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BasicManagerBean;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Company;
import com.code.aon.dao.ldap.LdapDAO;
import com.code.aon.ldap.BasicLdap;
import com.code.aon.ldap.IAonObjectClasses;
import com.code.aon.ldap.LdapException;
import com.code.aon.ldap.NameResolver;
import com.code.aon.manager.AccessPolicy;
import com.code.aon.manager.DBConnnection;
import com.code.aon.manager.Domain;
import com.code.aon.manager.DomainApplication;
import com.code.aon.manager.dao.IManagerAlias;
import com.code.aon.manager.enumeration.AccessPolicyType;
import com.code.aon.manager.enumeration.DomainType;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryBank;
import com.code.aon.ui.company.controller.CompanyController;
import com.code.aon.ui.config.event.BankAccountValidationListener;
import com.code.aon.ui.manager.converter.TransferObjectConverter;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.controller.LdapBasicController;

public class DomainController extends LdapBasicController implements IAonObjectClasses, IManagerConstants {

	private final static Logger LOGGER = LoggerFactory.getLogger(DomainController.class);
	
	private final static int DEFAULT_DOMAIN_NAME_MAX_LENGTH = 128;
	
	private AccessPolicy accessPolicy;
	
	private List<SelectItem> accessPolicies;
	
	private String selectedTab;
	
	private List<SelectItem> parentDomains;
	
	private List<SelectItem> domainTypes;
	
	private Converter converter;
	
	private boolean userManagementChanged;
	
	private boolean domainManagementChanged;
	
	private boolean documentManagementChanged;
	
	private boolean showCompanyWindow;
	
	private RegistryBank registryBank;
	
	private boolean enterpriseRecipient;
	
	public String getSelectedTab() {
		return selectedTab;
	}

	public void setSelectedTab(String selectedTab) {
		this.selectedTab = selectedTab;
	}	
	
	public int getDomainNameMaxLength() {
		return DEFAULT_DOMAIN_NAME_MAX_LENGTH;
	}
	
	public AccessPolicy getAccessPolicy() {
		return accessPolicy;
	}

	public void setAccessPolicy(AccessPolicy accessPolicy) {
		this.accessPolicy = accessPolicy;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<Domain> getDomains() throws ManagerBeanException {
		return (List) getModel().getWrappedData();
	}
	
	public Domain getDomain() {
		return (Domain) getTo();
	}

	protected String getInvalidMessage( String name ) {
		return AonUtil.getMessage(BUNDLE_NAME, DOMAIN_INVALID_NAME, name);
	}
	
	protected String getDuplicatedMessage( String name ) {
		return AonUtil.getMessage(BUNDLE_NAME, DOMAIN_DUPLICATED_NAME, name);
	}	

	private BasicManagerBean getAccessPolicyManagerBean( String domain ) {
		LdapDAO dao = new LdapDAO(AccessPolicy.class);
		Name baseDN = NameResolver.getDomainDN(domain);
		dao.setBaseDN( baseDN );			
		return new BasicManagerBean(dao);
	}		
	
	public List<SelectItem> getAccessPolicies() throws ManagerBeanException {
		if ( accessPolicies == null) {
			Locale locale = AonUtil.getCurrentLocale();
			this.accessPolicies = new LinkedList<SelectItem>();
			for (AccessPolicyType type : AccessPolicyType.values()) {
				String name = type.getName(locale);
				SelectItem item = new SelectItem(type.getName(), name );
				accessPolicies.add(item);
			}			
		}
		return accessPolicies;
	}	
	
	public void insertOrUpdateAccessPolicy() throws ManagerBeanException {
		BasicManagerBean bean = getAccessPolicyManagerBean( getDomain().getCommonName() );
		bean.insertOrUpdate(accessPolicy);
	}

	public void init() throws ManagerBeanException {
		this.documentManagementChanged = false;
		this.userManagementChanged = false;
		this.domainManagementChanged = false;	
		initAccessPolicy();
	}
	
	private void initAccessPolicy() throws ManagerBeanException {
		this.accessPolicy = new AccessPolicy();
		if (! isNew() ) {
			BasicManagerBean bean = getAccessPolicyManagerBean( getDomain().getCommonName() );
			List<ITransferObject> list = bean.getList(null);
			if (! list.isEmpty() ) {
				this.accessPolicy = (AccessPolicy) list.get(0);
			}
		}
	}
	
	private ManagerController getManager() {
		return (ManagerController) AonUtil.getRegisteredBean(MANAGER_CONTROLLER_NAME);
	}
	
	public void removeDomain( Domain domain ) {
		BasicLdap ldap = null;
		try {
			Properties properties = (Properties) BasicLdap.getLdapProperties().clone();
			properties.put(Context.REFERRAL, "ignore");
			ldap = new BasicLdap( properties );
			Name dn = domain.getId();
			if ( ldap.exists(dn, DOMAIN) ) {
				ldap.getLdapSession().deleteDepth(dn, true);	
			}
		} catch ( LdapException e ) {
			LOGGER.error( e.getMessage(), e);
		} finally {
			ldap.closeSession();
		}
	}	

	public void removeDBs( Domain domain ) throws ManagerBeanException {
		BasicLdap ldap = new BasicLdap();
		Name bdsDN = NameResolver.getDomainBDsDN(domain.getCommonName());
		if ( ldap.exists(bdsDN, ORGANIZATIONAL_UNIT) ) {
			DomainDBConnectionController ddbc = (DomainDBConnectionController) AonUtil.getRegisteredBean(DOMAIN_DB_CONNECTION_CONTROLLER_NAME);
			ddbc.updateBaseDN(domain.getId());
			for (ITransferObject to : ddbc.getManagerBean().getList(null) ) {
				try {
					getManager().removeDB( (DBConnnection) to );
				} catch ( SQLException sqle ) {
					LOGGER.error(sqle.getMessage(), sqle);
				}
			}
		}
	}	
	
	public DBConnnection createAndRegister( Domain domain ) throws ManagerBeanException {
		DBConnnection dbc = new DBConnnection();
		DomainDBConnectionController ddbcc = (DomainDBConnectionController) AonUtil.getRegisteredBean(DOMAIN_DB_CONNECTION_CONTROLLER_NAME);
		ddbcc.init( dbc, domain.getCommonName() );
		ddbcc.getManagerBean().insert(dbc);
		getManager().createDB(dbc);
		getManager().changeDbConnection(dbc);
		return dbc;
	}
	
	public void registerApplication( String name, DBConnnection dataSource ) throws ManagerBeanException {
		DomainApplicationController dac = (DomainApplicationController) AonUtil.getRegisteredBean(DOMAIN_APPLICATION_CONTROLLER_NAME);
		DomainApplication application = new DomainApplication();
		application.setCommonName(name);
		application.setDataSource(dataSource);
		dac.getManagerBean().insert(application);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void updateParentDomains() {
		this.parentDomains = new LinkedList<SelectItem>();
		try {
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(getFieldName(IManagerAlias.DOMAIN_DOMAIN_MANAGEMENT), Boolean.TRUE);
			List<Domain> list = (List) getManagerBean().getList(criteria);
			for (Domain domain : list) {
				SelectItem item = new SelectItem(domain, domain.getCommonName() );
				this.parentDomains.add(item);
			}
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}	
		
	public List<SelectItem> getParentDomains() {
		return parentDomains;
	}

	public Converter getConverter() {
		if ( converter == null ) {
			this.converter = new TransferObjectConverter(this);			
		}
		return converter;
	}

	public List<SelectItem> getDomainTypes() {
		if ( domainTypes == null ) {
			Locale locale = AonUtil.getCurrentLocale();
			domainTypes = new LinkedList<SelectItem>();
			for (DomainType type : DomainType.values()) {
				String name = type.getName(locale);
				SelectItem item = new SelectItem(type, name);
				domainTypes.add(item);
			}		
		}
		return domainTypes;
	}	
	
	public boolean isUserManagementChanged() {
		return userManagementChanged;
	}

	public boolean isDomainManagementChanged() {
		return domainManagementChanged;
	}

	public boolean isDocumentManagementChanged() {
		return documentManagementChanged;
	}

	public void documentManagementChanged( ValueChangeEvent event ) {
		this.documentManagementChanged = true;
	}

	public void userManagementChanged( ValueChangeEvent event ) {
		this.userManagementChanged = true;
	}

	public void domainManagementChanged( ValueChangeEvent event ) {
		this.domainManagementChanged = true;
	}

	public boolean isAddDomainSuffix() {
		return (!getManager().isAdministrator()) &&
			(!StringUtils.isEmpty(getManager().getCurrentDomain().getSubDomainSuffix()));  
	}
	
	public String getDomainName( String domainName ) {
		if ( isAddDomainSuffix() ) {
			return domainName + "." + getManager().getCurrentDomain().getSubDomainSuffix();	
		}
		return domainName;
	}

	@Override
	protected void idCheck(String id) {
		if ( isAddDomainSuffix() ) {
			if (! id.matches("\\p{Alpha}[\\w\\-]*") ) {
				throw new ValidatorException(new FacesMessage(getInvalidMessage(id)));
			}
		}
		super.idCheck( getDomainName(id) );
	}
	
	public boolean isShowCompanyWindow() {
		return showCompanyWindow;
	}

	public void setShowCompanyWindow(boolean showCompanyWindow) {
		this.showCompanyWindow = showCompanyWindow;
	}

	public boolean isEnterpriseRecipient() {
		return enterpriseRecipient;
	}

	public void setEnterpriseRecipient(boolean enterpriseRecipient) {
		this.enterpriseRecipient = enterpriseRecipient;
	}

	public RegistryBank getRegistryBank() {
		return registryBank;
	}

	public void setRegistryBank(RegistryBank registryBank) {
		this.registryBank = registryBank;
	}
	
	public void onCompanySave( ActionEvent event ) {
		boolean saveRegistryBank = isEnterpriseRecipient() &&
			(getManager().getCurrentDomain().getType() == DomainType.CONSULTANCY);
		try {
			if ( saveRegistryBank ) {
				BankAccountValidationListener.checkBankAccount( getRegistryBank(), false );
			}
			CompanyController companyController = (CompanyController) AonUtil.getRegisteredBean(COMPANY_CONTROLLER_NAME);
			companyController.accept(event);
			if ( saveRegistryBank ) {
				IManagerBean bean = BeanManager.getManagerBean(RegistryBank.class);
				getRegistryBank().setRegistry(companyController.obtainCompany());
				bean.insert( getRegistryBank() );
			}
		} catch (Throwable e) {
			LOGGER.error(">>>> onBeforeCompanyAccept ",e);
			addMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}		
	
	public Company getCompany() throws ManagerBeanException {
		ManagerController manager = getManager();
		DomainDBConnectionController ddbc = (DomainDBConnectionController) AonUtil.getRegisteredBean(DOMAIN_DB_CONNECTION_CONTROLLER_NAME);
		for (DBConnnection dbc : ddbc.getDBConnnections()) {
			manager.changeDbConnection(dbc);
			if ( manager.getDBManager().existsTable(dbc, "company") ) {
				try {
					IManagerBean bean = BeanManager.getManagerBean(Company.class);
					List<ITransferObject> list = bean.getList(null);
					if (! list.isEmpty() ) {
						return (Company) list.get(0);
					}
				} catch ( Throwable th ) {
					LOGGER.error( "Error getting company in " + dbc, th );
				}
			}					
		}
		return null;
	}
	
	public boolean isChildDomain() {
		Domain parent = getDomain().getParentDomain();
		return (parent != null) && (parent.getId() != null);
	}
	
}