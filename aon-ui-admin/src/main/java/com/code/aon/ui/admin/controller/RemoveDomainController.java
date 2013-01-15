package com.code.aon.ui.admin.controller;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.apache.commons.dbutils.DbUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.AonException;
import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.domain.IDomainProvider;
import com.code.aon.common.util.ConnectionProvider;
import com.code.aon.config.Domain;
import com.code.aon.dbutils.AonDomainRemove;
import com.code.aon.dbutils.AonSQLException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.form.event.IControllerListener;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.util.DataSourceUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class RemoveDomainController {

	private final static Logger LOGGER = LoggerFactory.getLogger(RemoveDomainController.class);
	
	private Domain domain;
	private String password;
	private IControllerListener domainFilter;
	
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
	
	public void onInit( ActionEvent event) throws ManagerBeanException {
		reset();
	}
	
	private void reset() {
		setPassword(null);
		try {
			setDomain((Domain)BeanManager.getManagerBean(Domain.class).createNewTo());
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
		}					
	}
	
	private void removeDomain(Integer domain) throws AonSQLException, SQLException, AonException {
		Connection connection = null;
		try {			
			Properties properties = DataSourceUtil.getDBProperties();
			connection = ConnectionProvider.getConnection(properties);				
			AonDomainRemove adr = new AonDomainRemove(connection);
			adr.execute(domain);
		} finally {
			DbUtils.closeQuietly(connection);
		}
	}
	
	public void onRemove( ActionEvent event) {
		NewDomainController.validateUserPassword(getPassword());
		
		try {			
			removeDomain(domain.getId());
			reset();
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
						IDomainProvider provider = DomainManager.getDomainProvider();
						criteria.addNotEqualExpression(controller.getFieldName(IEntityAlias.DOMAIN_ID), provider.getCurrentDomain());
						Integer parentId = provider.getParentDomain();
						if ( parentId != null ) {
							criteria.addEqualExpression(controller.getFieldName(IEntityAlias.DOMAIN_PARENT_ID), parentId);
						}
					} catch (ManagerBeanException e) {
						LOGGER.error("Error filtering domain", e);
					}
				}
			};
		}
		return this.domainFilter;
	}
	
}
