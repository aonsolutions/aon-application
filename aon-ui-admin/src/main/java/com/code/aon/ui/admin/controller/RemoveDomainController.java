package com.code.aon.ui.admin.controller;

import static com.code.aon.ui.config.controller.ConfigConstants.DOMAIN_SWITCHER;

import java.sql.Connection;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.config.Domain;
import com.code.aon.dbutils.AonDomainRemove;
import com.code.aon.dbutils.AonSQLException;
import com.code.aon.dbutils.DatabaseUtil;
import com.code.aon.pool.AonConnectionException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.config.controller.DomainSwitcher;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.form.event.IControllerListener;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class RemoveDomainController {

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
	
	private void removeDomain( String domainName, Integer domain) throws AonConnectionException, AonSQLException {
		Connection connection = null;
		try {			
			connection = DatabaseUtil.getConnection(domainName);				
			AonDomainRemove adr = new AonDomainRemove(connection);
			adr.execute(domain);
		} finally {
			DatabaseUtil.closeQuietly(connection);
		}
	}
	
	public void onRemove( ActionEvent event) {
		NewDomainController.validateUserPassword(getPassword());
		
		try {			
			removeDomain(domain.getName(),domain.getId());
			onInit(event);
			DomainSwitcher ds = (DomainSwitcher) AonUtil.getRegisteredBean(DOMAIN_SWITCHER);
			ds.setModel(null);			
		} catch (Throwable e) {
			LOGGER.error(">>>> onRemove: ", e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}

	public IControllerListener getDomainFilter() {
		if ( this.domainFilter == null ) {
			this.domainFilter = new ControllerAdapter() {
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
			};
		}
		return this.domainFilter;
	}
	
}
