package com.code.aon.ui.manager.controller;

import java.util.LinkedList;
import java.util.List;

import javax.faces.model.SelectItem;
import javax.naming.Name;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.BasicManagerBean;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.dao.ldap.LdapDAO;
import com.code.aon.ldap.NameResolver;
import com.code.aon.manager.Application;
import com.code.aon.manager.DomainApplication;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;

public class DomainApplicationController extends BasicController implements IManagerConstants {
	
	private LdapDAO ldapDAO;
	
	private BasicManagerBean ldapManagerBean;
	
	public DomainApplicationController() {
		this.ldapDAO = new LdapDAO(DomainApplication.class);		
		this.ldapManagerBean = new BasicManagerBean(this.ldapDAO);			
	}

	public void setDomain( String domain ) {
		Name dasDN = NameResolver.getDomainApplicationsDN(domain);
		this.ldapDAO.setBaseDN(dasDN);
	}
	
	@Override
	public IManagerBean getManagerBean() throws ManagerBeanException {
		return this.ldapManagerBean;
	}

	@SuppressWarnings("unchecked")
	public List<DomainApplication> getDomainApplications() throws ManagerBeanException {
		return (List) getModel().getWrappedData();
	}
	
	private boolean isRegisteredApplication( List<DomainApplication> das, String name ) {
		for( DomainApplication da : das ) {
			if ( StringUtils.equals(da.getCommonName(), name) ) {
				return true;
			}
		}
		return false;
	}
	
	public List<SelectItem> getAvailableApplications() throws ManagerBeanException {
		List<SelectItem> list = new LinkedList<SelectItem>();
		List<DomainApplication> das = getDomainApplications();
		ApplicationController controller = (ApplicationController) AonUtil.getRegisteredBean(APPLICATION_CONTROLLER_NAME);
		for (Application application : controller.getApplications()) {
			if (! isRegisteredApplication(das, application.getCommonName()) ) {
				SelectItem item = new SelectItem(application.getCommonName(), application.getCommonName() );
				list.add(item);				
			}
		}
		return list;
	}	
	
}
