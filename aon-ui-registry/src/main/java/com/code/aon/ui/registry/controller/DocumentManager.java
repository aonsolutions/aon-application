package com.code.aon.ui.registry.controller;

import static com.code.aon.ui.common.ICommonMessages.DOCUMENT_SIZE_MESSAGE;
import static com.code.aon.ui.common.ICommonMessages.USED_SPACE_MESSAGE;

import java.io.Serializable;

import org.apache.commons.io.FileUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.config.Domain;
import com.code.aon.ui.util.AonUtil;

public class DocumentManager implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private final static Logger LOGGER = LoggerFactory.getLogger(DocumentManager.class);
	
	public static final int MINIMUM_MAX_DOCUMENT_SIZE = 1;
	
	private static final int MAXIMUM_MAX_DOCUMENT_SIZE = 16;
	
	public static final int MINIMUM_MAX_TOTAL_DOCUMENT_SIZE = 100;
	
	public static final int MAX_TOTAL_DOCUMENT_SIZE_VALUES[] = {
		MINIMUM_MAX_TOTAL_DOCUMENT_SIZE, (int) (10*FileUtils.ONE_KB),
		(int) (100*FileUtils.ONE_KB), (int) FileUtils.ONE_MB 
	};
	
	/** Maximum number of users defined for the domain. */
	private Long maxDocumentSize;

	/** Maximun number of users allowed to access application in each domain. */
	private Long maxTotalDocumentSize;	
	
	public DocumentManager() {
		init();
	}
	
	private void init() {
		try {
			init(MINIMUM_MAX_DOCUMENT_SIZE, MINIMUM_MAX_TOTAL_DOCUMENT_SIZE);
			IManagerBean bean = BeanManager.getManagerBean(Domain.class);
			Domain domain = (Domain) bean.get(DomainManager.getCurrentDomain());
			if ( domain != null ) {
				updateLimits(domain);
				init(domain.getMaxDocumentSize(), domain.getMaxTotalDocumentSize());
			}
		} catch ( Throwable th ) {
			LOGGER.error( "Error init max document szie", th);
		}		
	}
	
	private void init( int maxDocumentSize, int maxTotalDocumentSize) {
		this.maxDocumentSize = maxDocumentSize * FileUtils.ONE_MB;
		this.maxTotalDocumentSize = maxTotalDocumentSize * FileUtils.ONE_MB;		
	}
	
	private static int getMaximumTotalDocumentSize( int value ) {
		for( int i = 0; i < MAX_TOTAL_DOCUMENT_SIZE_VALUES.length; i++ ) {
			if ( value <= MAX_TOTAL_DOCUMENT_SIZE_VALUES[i] ) {
				return MAX_TOTAL_DOCUMENT_SIZE_VALUES[i];
			}
		}
		return MAX_TOTAL_DOCUMENT_SIZE_VALUES[MAX_TOTAL_DOCUMENT_SIZE_VALUES.length-1];
	}
	
	public static boolean updateLimits( Domain domain ) throws ManagerBeanException {
		boolean updateDomain = false;
		Integer value = domain.getMaxDocumentSize();
		if ( (value == null) || (value < MINIMUM_MAX_DOCUMENT_SIZE)  ) {
			updateDomain = true;
			domain.setMaxDocumentSize(MINIMUM_MAX_DOCUMENT_SIZE);
		} else if (value > MAXIMUM_MAX_DOCUMENT_SIZE  ) {
			updateDomain = true;
			domain.setMaxDocumentSize(MAXIMUM_MAX_DOCUMENT_SIZE);
		}
		value = ( domain.getMaxTotalDocumentSize() != null) ? domain.getMaxTotalDocumentSize() : 0;
		int newValue = getMaximumTotalDocumentSize(value);
		if ( value != newValue ) {
			updateDomain = true;
			domain.setMaxTotalDocumentSize(newValue);			
		}
		return updateDomain;
	}
	
	private Long getUsedSpace() {
		return getUsedSpace( DomainManager.getCurrentDomain() );
	}

	private static Long getUsedSpace( Integer domain ) {
		Long usedSpace = 0L;
//    	String sessionFactoryName = HibernateUtil.getSessionFactoryName();
//		try {
//	        Session session = HibernateUtil.getSession(sessionFactoryName);
//	        Query query = session.createQuery("select sum(length(data)) from RegistryAttachment ra WHERE ra.domain = ?");
//	        query.setInteger(0, domain );
//	        usedSpace = (Long) query.uniqueResult();
//		} catch ( Throwable th ) {
//			LOGGER.error( "Error calculating free space", th);
//		} finally {
//			HibernateUtil.closeSession(sessionFactoryName, false);
//		}
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
		return AonUtil.getMessage(DOCUMENT_SIZE_MESSAGE, totalSpace, freeSpace, maxSize);
	}
	
	public String getFreeSpaceStyle() {
		return ( getFreeSpace() <= 0 ) ? "color:red;font-style:bold;font-style:italic;" : "";
	}

	public String getUsedSpaceMessage() {
		String usedSpace = FileUtils.byteCountToDisplaySize(getUsedSpace()); 
		return AonUtil.getMessage(USED_SPACE_MESSAGE, usedSpace);
	}
	
	public int getUsedSpaceInMB() {
		return getUsedSpaceInMB(DomainManager.getCurrentDomain());
	}

	public static int getUsedSpaceInMB( Integer domain) {
		int value = (int) (getUsedSpace(domain) / FileUtils.ONE_MB);
		return value;
	}
	
}