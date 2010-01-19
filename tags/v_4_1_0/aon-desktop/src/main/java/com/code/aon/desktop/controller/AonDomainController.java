package com.code.aon.desktop.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import javax.naming.Name;

import org.apache.commons.lang.StringUtils;

import com.code.aon.bridge.jmx.mbean.IConsoleAdmin;
import com.code.aon.bridge.plugin.DomainManager;
import com.code.aon.bridge.plugin.UserManager;
import com.code.aon.bridge.plugin.Utils;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.desktop.Domain;
import com.code.aon.desktop.IDesktopConstants;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.jaas.client.ast.IDomainApplication;
import com.code.aon.jaas.client.ast.INode;
import com.code.aon.jaas.client.ast.IRelation;
import com.code.aon.jaas.client.ast.IUser;
import com.code.aon.jaas.client.ast.core.Relation;
import com.code.aon.jaas.deployment.DeploymentException;
import com.code.aon.ldap.BasicLdap;
import com.code.aon.ldap.Entry;
import com.code.aon.ldap.IAonObjectClasses;
import com.code.aon.ldap.ILdapConstants;
import com.code.aon.ldap.LdapException;
import com.code.aon.ldap.LdapSession;
import com.code.aon.ldap.NameResolver;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;

public class AonDomainController extends BasicController implements IAonObjectClasses, ILdapConstants, IDesktopConstants {

	/** Obtiene un logger apropiado. */
	private static final Logger LOGGER = Logger.getLogger(AonDomainController.class.getName());
	
	/** Domain manager. */
    private DomainManager domainManager;
    private ListDataModel applications;
    private ListDataModel profiles;
    private ListDataModel users;
    private Domain domain;
    private IDomainApplication da;
    private Relation profile;
    private Relation user;
    private String currentTab;
    
    private boolean userManagement;
    private boolean domainManagement;
    
    private boolean newProfile;
    private boolean newUser;
    
    private List<SelectItem> availableUsers;
    
	public AonDomainController() {
		domain = getCurrentDomain();
		if ( domain != null ) {
			userManagement = domain.getUserManagement();
			domainManagement = domain.getDomainManagement();
		}
	}

	public Domain getDomain() {
		return domain;
	}

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
			LOGGER.log(Level.SEVERE, e.getMessage(), e );
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
			LOGGER.log(Level.SEVERE, e.getMessage(), e );
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
		if ( this.users.isRowAvailable() ) {
			IRelation user = (IRelation) this.users.getRowData();
			List<String> relations = user.relations();
			if ( (relations != null) && (!relations.isEmpty()) ) {
				String profiles = StringUtils.join(relations.toArray(), ", ");
				return StringUtils.abbreviate(profiles, 140);
			}
		}
		return "";
	}	

	public String getCurrentUserName() {
		if ( this.users.isRowAvailable() ) {
			IRelation user = (IRelation) this.users.getRowData();
			AonUserController userController = (AonUserController) AonUtil.getRegisteredBean(CURRENT_USER_CONTROLLER_NAME);		
			return userController.getUserName(user.getId());
		}
		return "";
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
			LOGGER.log(Level.SEVERE, e.getMessage(), e );
		}
	}

	public void acceptProfile(ActionEvent event) {
		try {
			newProfile = false;
			getDomainManager().saveProfile();
			loadProfiles();
			flushAuthenticationCache(null);			
		} catch (DeploymentException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e );
		}
	}

	public void removeProfile(ActionEvent event) {
		try {
			getDomainManager().removeProfile();
			loadProfiles();
		} catch (DeploymentException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e );
		}
	}

	public void removeUser(ActionEvent event) {
		try {
			removeUser(getDomainManager().getDomain().getId(),
					getDomainManager().getApplication().getId(), this.user);			
			loadUsers();
		} catch (LdapException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e );
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
		Name dn = NameResolver.getDomainApplicationUserDN(domainId, applicationId, user.getId());
		BasicLdap ldap = new BasicLdap();
		Entry entry = new Entry(dn);
		try {
			LdapSession session = ldap.getLdapSession();
			entry.addObjectClasses(new String[] {TOP, DOMAIN_APPLICATION_USER} );
			for( String role : user.relations() ) {
				Name member = session.getFullDN( NameResolver.getApplicationProfileDN(applicationId, role) );
				entry.put( MEMBER_ATTRIBUTE, member.toString() );
			}
			entry.put( STATUS_ATTRIBUTE, 0 );
			session.add(entry);
		} catch ( LdapException e ) {
			LOGGER.log(Level.SEVERE, "Error añadiendo usuario " + dn, e );
		} finally {
			ldap.closeSession();
		}
	}

	private void removeUser( String domainId, String applicationId, IRelation user ) throws LdapException {
		Name dn = NameResolver.getDomainApplicationUserDN(domainId, applicationId, user.getId());
		BasicLdap ldap = new BasicLdap();
		ldap.delete(dn);
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
			setNewUser(false);
		} catch (DeploymentException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e );
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
		Name profileDN = NameResolver.getApplicationProfileDN(da.getId(), name);
		BasicLdap ldap = new BasicLdap();
		return ldap.exists( profileDN, PROFILE );
	}
	
	public boolean isSelectedSystemProfile() {
		return isSystemProfile(this.profile.getId());
	}

	public boolean isCurrentSystemProfile() {
		if ( this.profiles.isRowAvailable() ) {
			Relation profile = (Relation)this.profiles.getRowData();
			return isSystemProfile(profile.getId());
		}
		return false;
	}

	private Domain getCurrentDomain() {
		Domain domain = null;
		AonUserController userController = (AonUserController) FormUtil.getController(CURRENT_USER_CONTROLLER_NAME);
		try {
			IManagerBean bean = FormUtil.getController(DOMAIN_CONTROLLER_NAME).getManagerBean();
			Name id = NameResolver.getDomainDN(userController.getDomain());
			domain = (Domain) bean.get( id );
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obteniendo de LDAP el aonDomain " + userController.getDomain(), e );
		}
		return domain;
	}

	public boolean isUserManagement() {
		return userManagement;
	}

	public boolean isDomainManagement() {
		return domainManagement;
	}
	
}
