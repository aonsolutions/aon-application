package com.code.aon.ui.registry.controller;

import static com.code.aon.ldap.IAonObjectClasses.CONFIG;
import static com.code.aon.ldap.IAonObjectClasses.DOMAIN;
import static com.code.aon.ldap.ILdapConstants.DOCUMENT_MANAGEMENT_ATTRIBUTE;
import static com.code.aon.ldap.ILdapConstants.MAX_DOCUMENT_SIZE_ATTRIBUTE;
import static com.code.aon.ldap.ILdapConstants.MAX_TOTAL_DOCUMENT_SIZE_ATTRIBUTE;
import static com.code.aon.ui.common.ICommonConstants.DEFAULT_BUNDLE;
import static com.code.aon.ui.common.ICommonConstants.DOCUMENT_SIZE_MESSAGE;
import static com.code.aon.ui.common.ICommonConstants.DOMAIN_RESOLVER_CONTROLLER_NAME;
import static com.code.aon.ui.common.ICommonConstants.USED_SPACE_MESSAGE;

import javax.naming.Name;

import org.apache.commons.io.FileUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.config.Domain;
import com.code.aon.ldap.BasicLdap;
import com.code.aon.ldap.Entry;
import com.code.aon.ldap.NameResolver;
import com.code.aon.ui.common.controller.DomainResolver;
import com.code.aon.ui.util.AonUtil;

public class DocumentManager {

	private final static Logger LOGGER = LoggerFactory.getLogger(DocumentManager.class);
	
	private static final Long MB_SIZE = 1048576L;
	
	private static final int MINIMUM_MAX_DOCUMENT_SIZE = 1;
	
	private static final int MAXIMUM_MAX_DOCUMENT_SIZE = 16;
	
	private static final int MINIMUM_MAX_TOTAL_DOCUMENT_SIZE = 100;
	
	private static final int MAXIMUM_MAX_TOTAL_DOCUMENT_SIZE = 1500;
	
	/** Maximum number of users defined for the domain. */
	private Long maxDocumentSize;

	/** Maximun number of users allowed to access application in each domain. */
	private Long maxTotalDocumentSize;	
	
	public DocumentManager() {
		this.maxDocumentSize = MINIMUM_MAX_DOCUMENT_SIZE * MB_SIZE;
		this.maxTotalDocumentSize = MINIMUM_MAX_TOTAL_DOCUMENT_SIZE * MB_SIZE;
		if (! AonUtil.isSkipLdap() ) {
			initLdap();	
		} else {
			initDB();
		}
	}

	private void initLdap() {
		DomainResolver domainResolver = (DomainResolver) AonUtil.getRegisteredBean(DOMAIN_RESOLVER_CONTROLLER_NAME);
		String domainName = domainResolver.getDomain();		
		Name domainDN = NameResolver.getDomainDN(domainName);
		boolean documentManagement = false;
		BasicLdap ldap = new BasicLdap();
		Entry domain = ldap.get(domainDN, DOMAIN);
		if ( (domain != null) && domain.containsKey(DOCUMENT_MANAGEMENT_ATTRIBUTE) ) {
			documentManagement = domain.toBoolean(DOCUMENT_MANAGEMENT_ATTRIBUTE);
		}
		if ( documentManagement ) {
			init(domain);
		} else {
			Entry config = ldap.get(domainDN, CONFIG);
			if (config != null) {
				init(config);
			}
		}
	}
	
	private void init( Entry entry ) {
		if ( entry.containsKey(MAX_DOCUMENT_SIZE_ATTRIBUTE) ) {
			maxDocumentSize = entry.toInteger(MAX_DOCUMENT_SIZE_ATTRIBUTE) * MB_SIZE;
		}		
		if ( entry.containsKey(MAX_TOTAL_DOCUMENT_SIZE_ATTRIBUTE) ) {
			maxTotalDocumentSize = entry.toInteger(MAX_TOTAL_DOCUMENT_SIZE_ATTRIBUTE) * MB_SIZE;
		}
	}
	
	private void initDB() {
		try {
			IManagerBean bean = BeanManager.getManagerBean(Domain.class);
			Domain domain = (Domain) bean.get(DomainManager.getCurrentDomain());
			if ( domain != null ) {
				updateLimits(bean, domain);
			}
		} catch ( Throwable th ) {
			LOGGER.error( "Error init max document szie", th);
		}		
	}
	
	public void updateLimits( IManagerBean bean, Domain domain ) throws ManagerBeanException {
		boolean updateDomain = false;
		Integer value = domain.getMaxDocumentSize();
		if ( (value == null) || (value < MINIMUM_MAX_DOCUMENT_SIZE)  ) {
			updateDomain = true;
			domain.setMaxDocumentSize(MINIMUM_MAX_DOCUMENT_SIZE);
		} else if (value > MAXIMUM_MAX_DOCUMENT_SIZE  ) {
			updateDomain = true;
			domain.setMaxDocumentSize(MAXIMUM_MAX_DOCUMENT_SIZE);
		}
		maxDocumentSize = domain.getMaxDocumentSize() * MB_SIZE;
		value = domain.getMaxTotalDocumentSize();
		if ( (value == null) || (value < MINIMUM_MAX_TOTAL_DOCUMENT_SIZE)  ) {
			updateDomain = true;
			domain.setMaxTotalDocumentSize(MINIMUM_MAX_TOTAL_DOCUMENT_SIZE);
		} else if (value > MAXIMUM_MAX_TOTAL_DOCUMENT_SIZE  ) {
			updateDomain = true;
			domain.setMaxTotalDocumentSize(MAXIMUM_MAX_TOTAL_DOCUMENT_SIZE);
		}
		maxTotalDocumentSize = domain.getMaxTotalDocumentSize() * MB_SIZE;
		if ( updateDomain ) {
			bean.update(domain);
		}		
	}
	
	private Long getUsedSpace() {
		Long usedSpace = 0L;
		try {
	    	String name = HibernateUtil.getSessionFactoryName();
	        Session session = HibernateUtil.getSession(name);
	        Query query = session.createQuery("select sum(length(data)) from RegistryAttachment ra WHERE ra.domain = ?");
	        query.setInteger(0, DomainManager.getCurrentDomain());
	        usedSpace = (Long) query.uniqueResult();
		} catch ( Throwable th ) {
			LOGGER.error( "Error calculating free space", th);
		}
        return (usedSpace != null) ? usedSpace : 0L;
	}
	
	public long getFreeSpace() {
		return this.maxTotalDocumentSize - getUsedSpace();
	}
	
	public long getMaximumDocumentSize() {
		return Math.min( getFreeSpace(), this.maxDocumentSize );
	}

	public String getFreeSpaceMessage() {
		String totalSpace = FileUtils.byteCountToDisplaySize(this.maxTotalDocumentSize);
		String freeSpace = FileUtils.byteCountToDisplaySize(getFreeSpace()); 
		String maxSize = FileUtils.byteCountToDisplaySize(maxDocumentSize);
		return AonUtil.getMessage(DEFAULT_BUNDLE, DOCUMENT_SIZE_MESSAGE, totalSpace, freeSpace, maxSize);
	}
	
	public String getFreeSpaceStyle() {
		return ( getFreeSpace() <= 0 ) ? "color:red;font-style:bold;font-style:italic;" : "";
	}

	public String getUsedSpaceMessage() {
		String usedSpace = FileUtils.byteCountToDisplaySize(getUsedSpace()); 
		return AonUtil.getMessage(DEFAULT_BUNDLE, USED_SPACE_MESSAGE, usedSpace);
	}
	
	public int getUsedSpaceInMB() {
		int value = (int) (getUsedSpace() / MB_SIZE);
		return value;
	}
	
}