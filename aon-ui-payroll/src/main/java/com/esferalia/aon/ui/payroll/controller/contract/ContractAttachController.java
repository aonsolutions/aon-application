package com.esferalia.aon.ui.payroll.controller.contract;

import static com.code.aon.ldap.IAonObjectClasses.CONFIG;
import static com.code.aon.ldap.IAonObjectClasses.DOMAIN;
import static com.code.aon.ldap.ILdapConstants.DOCUMENT_MANAGEMENT_ATTRIBUTE;
import static com.code.aon.ldap.ILdapConstants.MAX_DOCUMENT_SIZE_ATTRIBUTE;
import static com.code.aon.ldap.ILdapConstants.MAX_TOTAL_DOCUMENT_SIZE_ATTRIBUTE;
import static com.code.aon.ui.common.ICommonConstants.DEFAULT_BUNDLE;
import static com.code.aon.ui.common.ICommonConstants.DOCUMENT_SIZE_MESSAGE;
import static com.code.aon.ui.common.ICommonConstants.DOMAIN_RESOLVER_CONTROLLER_NAME;

import javax.naming.Name;

import org.apache.commons.io.FileUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.faces.controller.AttachmentController;
import com.code.aon.ldap.BasicLdap;
import com.code.aon.ldap.Entry;
import com.code.aon.ldap.NameResolver;
import com.code.aon.ui.common.controller.DomainResolver;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.enumeration.ContractAttachmentType;

public class ContractAttachController extends AttachmentController {
	
	private ContractAttachmentType type;
	
	private final static Logger LOGGER = LoggerFactory.getLogger(ContractAttachController.class);
	
	private static final Long MB_SIZE = 1048576L;
	
	private static final Long DEFAULT_MAX_DOCUMENT_SIZE = 1 * MB_SIZE;
	
	private static final Long DEFAULT_MAX_TOTAL_DOCUMENT_SIZE = 10 * MB_SIZE;
	
	/** Maximum number of users defined for the domain. */
	private Long maxDocumentSize;

	/** Maximun number of users allowed to access application in each domain. */
	private Long maxTotalDocumentSize;	

	private boolean show;
	
	public ContractAttachController() {
		this.show = true;
		this.maxDocumentSize = DEFAULT_MAX_DOCUMENT_SIZE;
		this.maxTotalDocumentSize = DEFAULT_MAX_TOTAL_DOCUMENT_SIZE;
		init();
	}

	public ContractAttachmentType getType() {
		return type;
	}
	
	public void setType(ContractAttachmentType type) {
		this.type = type;
	}

	private void init() {
		DomainResolver resolver = (DomainResolver) AonUtil.getRegisteredBean(DOMAIN_RESOLVER_CONTROLLER_NAME);
    	String domainName = resolver.getDomain();
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

	public boolean isShow() {
		return show;
	}

	public void setShow(boolean show) {
		this.show = show;
	}
	
	private Long getUsedSpace() {
		Long usedSpace = 0L;
		try {
	    	String name = HibernateUtil.getSessionFactoryName();
	        Session session = HibernateUtil.getSession(name);
	        Query query = session.createQuery("select sum(length(data)) from ContractAttachment");
	        usedSpace = (Long) query.uniqueResult();
		} catch ( Throwable th ) {
			LOGGER.error( "Error calculating free space", th);
		}
		if(usedSpace==null){
			return new Long(0);
		}
        return usedSpace;
	}
	
	public long getFreeSpace() {
		return Math.max( this.maxTotalDocumentSize - getUsedSpace(), 0);
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
	
	public long getMaximumSize() {
		return getMaximumDocumentSize();
	}
	
}
