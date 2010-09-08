package com.code.aon.ui.manager.controller;

import java.util.LinkedList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;
import javax.naming.Name;

import com.code.aon.common.BasicManagerBean;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.dao.ldap.LdapDAO;
import com.code.aon.ldap.NameResolver;
import com.code.aon.manager.DBConnnection;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;

public class DomainDBConnectionController extends BasicController {
	
	private LdapDAO ldapDAO;
	
	private BasicManagerBean ldapManagerBean;
	
	public DomainDBConnectionController() {
		this.ldapDAO = new LdapDAO(DBConnnection.class);		
		this.ldapManagerBean = new BasicManagerBean(this.ldapDAO);			
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
	
	public void dbConnectionNameCheck(FacesContext context, UIComponent component, Object value) {
		String domainName = value.toString();
		if (! domainName.matches("[a-zA-Z][a-zA-Z0-9]*") ) {
			String summary = AonUtil.getMessage("appBundle", "desktop_domain_invalid_name");
			throw new ValidatorException( new FacesMessage(summary) );
		}
	}
	
}
