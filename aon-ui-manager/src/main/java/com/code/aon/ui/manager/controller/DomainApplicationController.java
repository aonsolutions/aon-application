package com.code.aon.ui.manager.controller;

import static com.code.aon.ldap.IAonObjectClasses.ORGANIZATIONAL_UNIT;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.faces.model.SelectItem;
import javax.naming.Name;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ldap.BasicLdap;
import com.code.aon.ldap.NameResolver;
import com.code.aon.manager.Application;
import com.code.aon.manager.BasicProfile;
import com.code.aon.manager.DomainApplication;
import com.code.aon.ui.util.AonUtil;

public class DomainApplicationController extends LdapBasicController {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(DomainApplicationController.class);
	
	private String selectedTab;
	
	public String getSelectedTab() {
		return selectedTab;
	}

	public void setSelectedTab(String selectedTab) {
		this.selectedTab = selectedTab;
	}
	
	@Override
	public void updateBaseDN(Name parent) {
		String domain = NameResolver.getValue(parent, 0);
		Name baseDN = NameResolver.getDomainApplicationsDN(domain);
		getLdapDAO().setBaseDN(baseDN);
	}
	
	public DomainApplication getDomainApplication() {
		return (DomainApplication) getTo();
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
	
	public void createOrganizationalUnits( DomainApplication da ) {
		BasicLdap ldap = new BasicLdap();
		String domainName = NameResolver.getValue(da.getId(), 2);
		String application = NameResolver.getValue(da.getId(), 0);
		Name profilesDN = NameResolver.getDomainApplicationProfilesDN(domainName, application);
		if (! ldap.exists(profilesDN, ORGANIZATIONAL_UNIT) ) {
			ldap.addOrganizationUnit(profilesDN);
		}
		Name usersDN = NameResolver.getDomainApplicationUsersDN(domainName, application);
		if (! ldap.exists(usersDN, ORGANIZATIONAL_UNIT) ) {
			ldap.addOrganizationUnit(usersDN);
		}
	}		

    /**
     * Available roles list defined in application.
     * 
     * @return List
     * @throws ManagerBeanException
     */
    public List<SelectItem> getAvailableProfiles() {
        List<SelectItem> list = new ArrayList<SelectItem>();
        try {
        	DomainApplication da = getDomainApplication();
        	Name applicationDN = NameResolver.getApplicationDN(NameResolver.getValue(da.getId(), 0));
        	ProfileController pController = (ProfileController) AonUtil.getRegisteredBean(PROFILE_CONTROLLER_NAME);      	
        	for( BasicProfile profile : pController.getProfiles(applicationDN)) {
			    SelectItem item = new SelectItem( profile.getId(), profile.getCommonName() );
			    list.add(item);        	        		
        	}
        	ProfileController dpController = (ProfileController) AonUtil.getRegisteredBean(DOMAIN_PROFILE_CONTROLLER_NAME);
        	for( BasicProfile profile : dpController.getProfiles()) {
        		SelectItem item = new SelectItem( profile.getId(), profile.getCommonName() );
			    list.add(item);        	
			}
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
		}
        return list;
    }	
    
}
