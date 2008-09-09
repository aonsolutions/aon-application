package com.code.aon.desktop.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.code.aon.bridge.jmx.mbean.IConsoleAdmin;
import com.code.aon.bridge.plugin.DomainManager;
import com.code.aon.bridge.plugin.UserManager;
import com.code.aon.bridge.plugin.Utils;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.jaas.client.ast.IDomainApplication;
import com.code.aon.jaas.client.ast.INode;
import com.code.aon.jaas.client.ast.IRelation;
import com.code.aon.jaas.client.ast.IUser;
import com.code.aon.jaas.client.ast.core.Relation;
import com.code.aon.jaas.deployment.DeploymentException;
import com.code.aon.ldap.AonDN;
import com.code.aon.ldap.BasicLdap;
import com.code.aon.ldap.DistinguishedName;
import com.code.aon.ldap.Entry;
import com.code.aon.ldap.IAonObjectClasses;
import com.code.aon.ldap.ILdapConstants;
import com.code.aon.ldap.LdapException;
import com.code.aon.ldap.LdapSession;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;

public class AonDomainController extends BasicController implements IAonObjectClasses, ILdapConstants {

    /** Obtiene un logger apropiado. */
	private static final Log LOGGER = LogFactory.getLog( AonDomainController.class.getName() );
	
	/** Domain manager. */
    private DomainManager domainManager;
    private ListDataModel applications;
    private ListDataModel profiles;
    private ListDataModel users;
    private IDomainApplication da;
    private Relation profile;
    private Relation user;
    private String currentTab;
    
    private boolean newProfile;
    private boolean newUser;
    
    private List<SelectItem> availableUsers;
    
	public boolean isNewProfile() {
		return newProfile;
	}

	public void setNewProfile(boolean newProfile) {
		this.newProfile = newProfile;
	}
	
	public boolean isNewUser() {
		return newUser;
	}

	public void setNewUser(boolean newUser) {
		this.newUser = newUser;
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
		this.newUser = false;
	}

    /* (non-Javadoc)
     * @see com.code.aon.ui.form.IController#onReset(javax.faces.event.ActionEvent)
     */
    public void onResetProfile(ActionEvent event) {
    	this.profile = new Relation();
    	getDomainManager().setRelation(this.profile);
    	newProfile = true;
    }
    
    @SuppressWarnings("unchecked")
	public List<SelectItem> calculaAvailableUsers() throws DeploymentException {
    	List<SelectItem> list = new LinkedList<SelectItem>();
    	List<IRelation> relations = (List<IRelation>) users.getWrappedData();
    	UserManager userManager = new UserManager();
		for( IUser securityUser : userManager.getUsers() ) {
    		boolean add = true;
        	for( IRelation relation : relations ) {
    			if ( relation.getId().equals(securityUser.getId()) ) {
    				add = false;
    				break;
    			}
    		}
    		if ( add ) {
        		SelectItem item = new SelectItem(securityUser.getId(), securityUser.getId());
        		list.add(item);    			
    		}
    	}
    	return list;
    }    

    public void onResetUser(ActionEvent event) throws DeploymentException {
    	this.user = new Relation();
    	getDomainManager().setRelation(this.user);
    	this.availableUsers = calculaAvailableUsers();
    	newUser = true;
    }
    
	public List<SelectItem> getAvailableUsers() {
		return availableUsers;
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
			Collections.sort(l, getNodeComparator());
			applications = new ListDataModel(l); 
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		}
	}

	private Comparator<INode> getNodeComparator() {
		Comparator<INode> comparator = new Comparator<INode>() {
			public int compare(INode n1, INode n2) {
				return n1.getId().compareTo(n2.getId());
			}			
		};
		return comparator;
	}
	
	public void loadProfiles()  {
		Iterator<IRelation> iter = da.profiles().iterator();
		List<IRelation> l = new ArrayList<IRelation>();
		while (iter.hasNext()) {
			IRelation r = iter.next();
			l.add(r);
		}
		Collections.sort(l, getNodeComparator());
		profiles = new ListDataModel(l); 
	}

	public void loadUsers()  {
		Iterator<IRelation> iter = da.users().iterator();
		List<IRelation> l = new ArrayList<IRelation>();
		while (iter.hasNext()) {
			IRelation r = iter.next();
			l.add(r);
		}
		Collections.sort(l, getNodeComparator());
		users = new ListDataModel(l); 
	}
	
	public String getCurrentUserProfiles() {
		IRelation user = (IRelation) this.users.getRowData();
		List<String> relations = user.relations();
		if ( (relations != null) && (!relations.isEmpty()) ) {
			String profiles = StringUtils.join(relations.toArray(), ", ");
			return StringUtils.abbreviate(profiles, 140);
		}
		return "";
	}	

	public String getCurrentUserName() {
		IRelation user = (IRelation) this.users.getRowData();
		AonUserController userController = (AonUserController) AonUtil.getRegisteredBean("currentUser");		
		return userController.getUserName(user.getId());
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
			newProfile = false;
			getDomainManager().saveProfile();
			loadProfiles();
			flushAuthenticationCache(null);			
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

	public void flushAuthenticationCache( String userName ) throws DeploymentException {
		IConsoleAdmin console = Utils.getSecurityConsole();
		AuthPrincipal principal = null;
		if ( userName != null ) {
			String name = UserUtils.getInstance().getPrincipal().getName();
			principal = new AuthPrincipal( userName + name.substring(name.indexOf('@')));			
		}
		console.flushAuthenticationCache(UserManager.LDAP_SECURITY_DOMAIN, principal);
	}
	
	private void addUser( String domainId, String applicationId, IRelation user ) {
		DistinguishedName dn = AonDN.getDomainApplicationUserDN(domainId, applicationId, user.getId());
		BasicLdap ldap = new BasicLdap();
		Entry entry = new Entry(dn.toString());
		try {
			LdapSession session = ldap.getLdapSession();
			entry.addObjectClasses(new String[] {TOP, DOMAIN_APPLICATION_USER} );
			for( String role : user.relations() ) {
				DistinguishedName member = session.getFullDN( AonDN.getApplicationProfileDN(applicationId, role) );
				entry.put( MEMBER_ATTRIBUTE, member.toString() );
			}
			entry.put( STATUS_ATTRIBUTE, 0 );
			session.add(entry);
		} catch ( LdapException e ) {
			LOGGER.error( e.getMessage(), e );
		} finally {
			ldap.closeSession();
		}
	}

	public void acceptUser(ActionEvent event) {
		try {
			if (isNewUser()) {
				addUser(getDomainManager().getDomain().getId(),
						getDomainManager().getApplication().getId(), this.user);
				loadUsers();
			} else {
				getDomainManager().updateUserProfiles();
			}
			flushAuthenticationCache(this.user.getId());
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
	
	private boolean isSystemProfile( String name ) {
		DistinguishedName profileDN = AonDN.getApplicationProfileDN(da.getId(), name);
		BasicLdap ldap = new BasicLdap();
		return ldap.exists( profileDN, PROFILE );
	}
	
	public boolean isSelectedSystemProfile() {
		return isSystemProfile(this.profile.getId());
	}

	public boolean isCurrentSystemProfile() {
		Relation profile = (Relation)this.profiles.getRowData();
		return isSystemProfile(profile.getId());
	}
	
}
