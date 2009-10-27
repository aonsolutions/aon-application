package com.code.aon.desktop.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.ListDataModel;

import com.code.aon.bridge.plugin.DomainManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.jaas.client.ast.IDomainApplication;
import com.code.aon.jaas.client.ast.IRelation;
import com.code.aon.jaas.client.ast.core.Relation;
import com.code.aon.jaas.deployment.DeploymentException;
import com.code.aon.ui.form.BasicController;

public class AonDomainController extends BasicController {

	/** Domain manager. */
    private DomainManager domainManager = null;
    private ListDataModel applications;
    private ListDataModel profiles;
    private ListDataModel users;
    private IDomainApplication da;
    private Relation profile;
    private Relation user;
    private String currentTab;
    
    private boolean newProfile = false;
    
	public boolean isNewProfile() {
		return newProfile;
	}

	public void setNewProfile(boolean newProfile) {
		this.newProfile = newProfile;
	}

	public void onSelectApplication(ActionEvent event) throws ManagerBeanException {
        try {
            da = (IDomainApplication)this.applications.getRowData();
			getDomainManager().findApplicationById(da.getId());
			loadProfiles();
			loadUsers();
		} catch (DeploymentException e) {
			e.printStackTrace();
		}
	}

	public void onSelectProfile(ActionEvent event) throws ManagerBeanException {
        this.profile = (Relation)this.profiles.getRowData();
		getDomainManager().setRelation(this.profile);
		newProfile = false;
	}

	public void onSelectUser(ActionEvent event) throws ManagerBeanException {
        this.user = (Relation)this.users.getRowData();
		getDomainManager().setRelation(this.user);
	}

    /* (non-Javadoc)
     * @see com.code.aon.ui.form.IController#onReset(javax.faces.event.ActionEvent)
     */
    public void onResetProfile(ActionEvent event) {
    	this.profile = new Relation();
    	getDomainManager().setRelation(this.profile);
    	newProfile = true;
    }

	/**
	 * @return Returns the userManager.
	 */
	public DomainManager getDomainManager() {
		return domainManager;
	}

	/**
	 * @param userManager The userManager to set.
	 */
	public void setDomainManager(DomainManager domainManager) {
		this.domainManager = domainManager;
	}	
	
	public void onLoadCurrentDomain(ActionEvent event)  {
		try {
			loadDomain();
			Iterator<IDomainApplication> iter = getDomainManager().getDomainApplications().iterator();
			List<IDomainApplication> l = new ArrayList<IDomainApplication>();
			while (iter.hasNext()) {
				IDomainApplication da = iter.next();
				l.add(da);
			}
			applications = new ListDataModel(l); 
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		}
	}

	public void loadProfiles()  {
		Iterator<IRelation> iter = da.profiles().iterator();
		List<IRelation> l = new ArrayList<IRelation>();
		while (iter.hasNext()) {
			IRelation r = iter.next();
			l.add(r);
		}
		profiles = new ListDataModel(l); 
	}

	public void loadUsers()  {
		Iterator<IRelation> iter = da.users().iterator();
		List<IRelation> l = new ArrayList<IRelation>();
		while (iter.hasNext()) {
			IRelation r = iter.next();
			l.add(r);
		}
		users = new ListDataModel(l); 
	}

	@SuppressWarnings("unchecked")
	private void loadDomain() throws ManagerBeanException {
		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
		AuthPrincipal user = null;
		Principal principal = ec.getUserPrincipal();
		if ( principal instanceof AuthPrincipal ) {
			user = (AuthPrincipal) principal;
		} 
		else {
			user = new AuthPrincipal( principal.getName() );
		}
		try {
			setDomainManager(new DomainManager(user));
		} catch (DeploymentException e) {
			e.printStackTrace();
		}
	}

	public void acceptProfile(ActionEvent event) {
		try {
			getDomainManager().saveProfile();
			loadProfiles();
		} catch (DeploymentException e) {
			e.printStackTrace();
		}
	}

	public void removeProfile(ActionEvent event) {
		try {
			getDomainManager().removeProfile();
			loadProfiles();
		} catch (DeploymentException e) {
			e.printStackTrace();
		}
	}

	public void acceptUser(ActionEvent event) {
		try {
			getDomainManager().updateUserProfiles();
		} catch (DeploymentException e) {
			e.printStackTrace();
		}
	}

	public ListDataModel getApplications() {
		return applications;
	}

	public void setApplications(ListDataModel applications) {
		this.applications = applications;
	}

	public IDomainApplication getDa() {
		return da;
	}

	public void setDa(IDomainApplication da) {
		this.da = da;
	}

	public ListDataModel getProfiles() {
		return profiles;
	}

	public void setProfiles(ListDataModel profiles) {
		this.profiles = profiles;
	}

	public Relation getProfile() {
		return profile;
	}

	public void setProfile(Relation profile) {
		this.profile = profile;
	}

	public ListDataModel getUsers() {
		return users;
	}

	public void setUsers(ListDataModel users) {
		this.users = users;
	}

	public Relation getUser() {
		return user;
	}

	public void setUser(Relation user) {
		this.user = user;
	}

	public String getCurrentTab() {
		return currentTab;
	}

	public void setCurrentTab(String currentTab) {
		this.currentTab = currentTab;
	}

}
