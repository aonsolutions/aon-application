package com.code.aon.ui.admin.controller;

import static com.code.aon.ui.config.controller.ConfigConstants.DOMAIN_SWITCHER;

import java.io.Serializable;
import java.sql.Connection;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import jakarta.mail.Address;

import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.company.Company;
import com.code.aon.config.Domain;
import net.aonsolutions.core.dbutils.AonDomainRemove;
import net.aonsolutions.core.dbutils.AonSQLException;
import net.aonsolutions.core.dbutils.DatabaseUtil;
import net.aonsolutions.core.pool.AonConnectionException;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.enumeration.RegistryAttachmentType;
import com.code.aon.ui.admin.BookingInfo;
import com.code.aon.ui.admin.DomainInfo;
import com.code.aon.ui.admin.DomainInfoType;
import com.code.aon.ui.common.ICommonMessages;
import com.code.aon.ui.config.controller.DomainSwitcher;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.form.event.IControllerListener;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class RemoveDomainController implements Serializable {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private final static Logger LOGGER = LoggerFactory.getLogger(RemoveDomainController.class);
	
	private Domain domain;
	private String password;
	private IControllerListener domainFilter;
	private boolean domainDisabled;
	
	public Domain getDomain() {
		return domain;
	}
	public void setDomain(Domain domain) {
		this.domain = domain;
	}
	
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	public boolean isDomainDisabled() {
		return domainDisabled;
	}
	
	public void setDomainDisabled(boolean domainDisabled) {
		this.domainDisabled = domainDisabled;
	}

	public void onInit( ActionEvent event) {
		try {
			Domain domain = (Domain)BeanManager.getManagerBean(Domain.class).createNewTo();
			reset(domain);
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
		}					
	}
	
	public void reset( Domain domain ) {
		setPassword(null);
		setDomain(domain);
		setDomainDisabled(false);
	}
	
	private boolean removeDomain( String domainName, Integer domain) throws AonConnectionException, AonSQLException {
		Connection connection = null;
		try {			
			connection = DatabaseUtil.getConnection(domainName);				
			AonDomainRemove adr = new AonDomainRemove(connection);
			return adr.execute(domain);
		} finally {
			DatabaseUtil.closeQuietly(connection);
		}
	}
	
	public void onRemove( ActionEvent event) {
		NewDomainController.validateUserPassword(getPassword());
		
		try {			
			Address[] emails = DomainController.getNotificationEmails(domain.getId());
			BookingInfo bookingInfo = DomainController.getBookingInfo(domain);
			DomainInfo di = DomainInfo.getDomainInfo(domain, bookingInfo);
			di.setInfoType(DomainInfoType.REMOVE);
			boolean domainDeleted = removeDomain(domain.getName(),domain.getId());
			if ( domainDeleted ) {
				Company company = DomainController.getAdminCompany();
				if ( company != null ) {
					DomainController.saveHistory(di, company, RegistryAttachmentType.DOMAIN_REMOVE_HISTORY);
				}
				if (! ArrayUtils.isEmpty(emails) ) {
					String subject = AonUtil.getMessage(ICommonMessages.DOMAIN_REMOVE_EMAIL_SUBJECT, domain.getName());
					String content = DomainController.getEmailContent(domain, di, ICommonMessages.DOMAIN_REMOVE_EMAIL_BODY);
					DomainController.sendNotificationEmail(emails, subject, content, null);
				}				
			} else {
				AonUtil.addErrorMessageFromBundle(ICommonMessages.REMOVE_DOMAIN_ERROR, domain.getName(), "");
			}
			onInit(event);
			DomainSwitcher ds = (DomainSwitcher) AonUtil.getRegisteredBean(DOMAIN_SWITCHER);
			ds.setModel(null);			
		} catch (Throwable e) {
			LOGGER.error(">>>> onRemove: ", e);
			AonUtil.addErrorMessageFromBundle(ICommonMessages.REMOVE_DOMAIN_ERROR, domain.getName(), e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}

	public IControllerListener getDomainFilter() {
		if ( this.domainFilter == null ) {
			this.domainFilter = new DomainFilter();
		}
		return this.domainFilter;
	}
	
	private static class DomainFilter extends ControllerAdapter {
		
		private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

		@Override
		public void beforeModelInitialized(ControllerEvent event)
				throws ControllerListenerException {
			IController controller = event.getController();
			try {					
				Criteria criteria = controller.getCriteria();
				criteria.setSkipDomainFilter(true);
				criteria.addEqualExpression(controller.getFieldName(IEntityAlias.DOMAIN_ACTIVE), Boolean.TRUE);
				Integer domainId = DomainManager.getCurrentDomain();
				criteria.addNotEqualExpression(controller.getFieldName(IEntityAlias.DOMAIN_ID), domainId);
				criteria.addEqualExpression(controller.getFieldName(IEntityAlias.DOMAIN_PARENT_ID), domainId);
			} catch (ManagerBeanException e) {
				LOGGER.error("Error filtering domain", e);
			}
		}
		
	}
	
}
