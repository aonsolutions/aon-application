package com.code.aon.ui.manager.controller;

import static com.code.aon.ldap.IAonObjectClasses.ORGANIZATIONAL_UNIT;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.faces.model.SelectItem;
import javax.naming.Name;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.Scope;
import com.code.aon.config.WorkGroup;
import com.code.aon.config.enumeration.WorkGroupStatus;
import com.code.aon.ldap.BasicLdap;
import com.code.aon.ldap.NameResolver;
import com.code.aon.manager.Application;
import com.code.aon.manager.BasicProfile;
import com.code.aon.manager.DomainApplication;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.controller.LdapBasicController;

public class DomainApplicationController extends LdapBasicController implements IManagerConstants {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(DomainApplicationController.class);
	
	private String[] APPLICATIONS_WITHOUT_DB = new String[] {AON_CMS, AON_PUBLISHER, AON_WEBMAIL};
	
	private String selectedTab;
	
	private boolean aonDB;
	
	public String getSelectedTab() {
		return selectedTab;
	}

	public void setSelectedTab(String selectedTab) {
		this.selectedTab = selectedTab;
	}
	
	public boolean isAonDB() {
		return aonDB;
	}

	public void setAonDB(boolean aonDB) {
		this.aonDB = aonDB;
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
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<DomainApplication> getDomainApplications() throws ManagerBeanException {
		return (List) getModel().getWrappedData();
	}
	
	private boolean isRegistableApplication( List<DomainApplication> das, Application application ) {
		if ( application.getContratable() ) {
			for( DomainApplication da : das ) {
				if ( StringUtils.equals(da.getCommonName(), application.getCommonName()) ) {
					return false;
				}
			}			
			return true;
		}
		return false;
	}
	
	public List<SelectItem> getAvailableApplications() throws ManagerBeanException {
		List<SelectItem> list = new LinkedList<SelectItem>();
		List<DomainApplication> das = getDomainApplications();
		ApplicationController controller = (ApplicationController) AonUtil.getRegisteredBean(APPLICATION_CONTROLLER_NAME);
		for (Application application : controller.getApplications()) {
			if ( isRegistableApplication(das, application) ) {
				SelectItem item = new SelectItem(application.getCommonName(), application.getCommonName() );
				list.add(item);				
			}
		}
		return list;
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
        	Name applicationDN = NameResolver.getApplicationDN(da.getCommonName());
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
    
	public List<SelectItem> getScopes() throws ManagerBeanException {
		List<SelectItem> scopes = new LinkedList<SelectItem>();
		IController controller = FormUtil.getController(SCOPE_CONTROLLER_NAME);
		IManagerBean scopeBean = controller.getManagerBean();
		Criteria criteria = new Criteria();
		criteria.addOrder("Scope.description");
		for( ITransferObject to : scopeBean.getList(criteria) ) {
			Scope scope = (Scope) to;
			SelectItem item = new SelectItem(scope, scope.getDescription());
			scopes.add(item);
		}
		return scopes;
	}
	
	public List<SelectItem> getWorkgroups() throws ManagerBeanException {
		List<SelectItem> workgroups = new LinkedList<SelectItem>(); 
		IController controller = FormUtil.getController(WORK_GROUP_CONTROLLER_NAME);
		IManagerBean workGroupBean = controller.getManagerBean(); 
		Criteria criteria = new Criteria();
		criteria.addEqualExpression("WorkGroup.status", WorkGroupStatus.ACTIVE);
		criteria.addOrder("WorkGroup.description");
		for( ITransferObject to : workGroupBean.getList(criteria) ) {
			WorkGroup workGroup = (WorkGroup) to;
			SelectItem item = new SelectItem(workGroup, workGroup.getDescription());
			workgroups.add(item);
		}
		return workgroups;
	}    
    
	public boolean isWithoutDB() {
		return ArrayUtils.contains(APPLICATIONS_WITHOUT_DB, getDomainApplication().getCommonName());	
	}
	
}
