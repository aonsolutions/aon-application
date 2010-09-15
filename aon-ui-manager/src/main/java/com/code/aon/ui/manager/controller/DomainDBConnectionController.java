package com.code.aon.ui.manager.controller;

import java.util.LinkedList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;
import javax.naming.Name;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BasicManagerBean;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.dao.ldap.LdapDAO;
import com.code.aon.ldap.NameResolver;
import com.code.aon.manager.DBConnnection;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;

public class DomainDBConnectionController extends BasicController implements IManagerConstants {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(DomainDBConnectionController.class);
	
	private LdapDAO ldapDAO;
	
	private BasicManagerBean ldapManagerBean;
	
	private boolean createDB;
	
	public DomainDBConnectionController() {
		this.ldapDAO = new LdapDAO(DBConnnection.class);		
		this.ldapManagerBean = new BasicManagerBean(this.ldapDAO);
		this.createDB = true;
	}

	public void setDomain( String domain ) {
		Name bdsDN = NameResolver.getDomainBDsDN(domain);
		this.ldapDAO.setBaseDN(bdsDN);
	}
	
	@Override
	public IManagerBean getManagerBean() throws ManagerBeanException {
		return this.ldapManagerBean;
	}

	@SuppressWarnings("unchecked")
	public List<DBConnnection> getDBConnections() throws ManagerBeanException {
		return (List) getModel().getWrappedData();
	}

	public List<SelectItem> getDataSources() throws ManagerBeanException {
		List<SelectItem> dataSources = new LinkedList<SelectItem>();
		for (DBConnnection dbc : getDBConnections()) {
			SelectItem item = new SelectItem(dbc.getId(), dbc.getCommonName() );
			dataSources.add(item);
		}
		return dataSources;
	}	
	
	private boolean exists( String name ) {
		try {
			for( DBConnnection dbc : getDBConnections() ) {
				if ( StringUtils.equals(name, dbc.getCommonName()) ) {
					return true;
				}
			}
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return false;
	}
	
	public void dbConnectionNameCheck(FacesContext context, UIComponent component, Object value) {
		String name = value.toString();
		if (! name.matches("[a-zA-Z][a-zA-Z0-9_-]*") ) {
			String summary = AonUtil.getMessage(BUNDLE_NAME, DB_CONNECTION_INVALID_NAME);
			throw new ValidatorException( new FacesMessage(summary) );
		}
		if ( exists(name) ) {
			String summary = AonUtil.getMessage(BUNDLE_NAME, DB_CONNECTION_DUPLICATED_NAME, name);
			throw new ValidatorException( new FacesMessage(summary) );			
		}
	}
	
	public boolean isCreateDB() {
		return createDB;
	}

	public void setCreateDB(boolean createDB) {
		this.createDB = createDB;
	}
	
}
