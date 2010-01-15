package com.code.aon.bridge.plugin;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.faces.model.SelectItem;

import com.code.aon.bridge.jmx.mbean.IConsoleAdmin;
import com.code.aon.bridge.jmx.mbean.IOperation;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.jaas.client.ast.IApplication;
import com.code.aon.jaas.client.ast.IDomain;
import com.code.aon.jaas.client.ast.IDomainApplication;
import com.code.aon.jaas.client.ast.IRelation;
import com.code.aon.jaas.client.ast.IRole;
import com.code.aon.jaas.client.ast.IUser;
import com.code.aon.jaas.client.ast.core.Relation;
import com.code.aon.jaas.deployment.DeploymentException;

/**
 * @author Consulting & Development. Iñaki Ayerbe - 20-dec-2007
 *
 */

public class DomainManager implements Serializable {

	private static final long serialVersionUID = -4290619091644477668L;

	/** Default context name. */
	public static final String SECURITY_CONTEXT_NAME = "/aon-security"; 

    transient IConsoleAdmin console;
    /** Requested application identifier. */
    IApplication application;
    /** Domain. */
    IDomain domain;
    /** Relation (Profile / User). */
    Relation relation;
    
	/**
	 * Constructor for a given <code>AuthPrincipal</code>.
	 * 
	 * @throws DeploymentException 
	 */
	public DomainManager(AuthPrincipal principal) throws DeploymentException {
		this( principal.getContext(), principal.getDomain() );
	}

	/**
	 * Constructor for a given context and domain name.
	 * 
	 * @param context
	 * @param name
	 * @throws DeploymentException 
	 */
	public DomainManager(String context, String name) throws DeploymentException {
		this.console = Utils.getSecurityConsole();
		findApplication( context ); 
		this.domain = this.application.getDomain(name);
	}

	/**
	 * Return the roles for given application domain.
	 * 
	 * @return
	 */
	public Collection<IRole> getRoles() {
		return this.application.roles();
	}

	/**
	 * Return the profiles for given application domain.
	 * 
	 * @param appId
	 * @return
	 */
	public Collection<IRelation> getProfiles(String appId) {
		String id = ( appId == null )? this.application.getId(): appId;
		return this.application.getDomain( this.domain.getId() ).getDomainApplication( id ).profiles();
	}

	/**
	 * Return the enabled user names for given application domain and profile.
	 * 
	 * @param appId
	 * @param profileId
	 * @return
	 */
	public String getUsers(String appId, String profileId) {
		StringBuffer sb = new StringBuffer();
		if ( this.domain != null ) {
			String id = ( appId == null )? this.application.getId(): appId;
			IDomain domain = this.application.getDomain( this.domain.getId() );
			Iterator<IRelation> it = domain.getDomainApplication( id ).users().iterator();
			while (it.hasNext()) {
				IRelation user = it.next();
				if ( user.relations().contains( profileId ) ) {
					String name = domain.standaloneUsers().get( user.getId() ).getName();
					sb.append( name + "," );
				}
			}
			sb.deleteCharAt( sb.length() - 1 );
		}
		return sb.toString();
	}

	/**
	 * Updates a profile, inside current domain.
	 *  
	 * @throws DeploymentException 
	 */
	public void saveProfile() throws DeploymentException {
		saveProfile(this.relation);
	}

	/**
	 * Updates a profile, inside current domain.
	 *  
	 * @param relation
	 * @throws DeploymentException 
	 */
	private void saveProfile(IRelation relation) throws DeploymentException {
		Object[] params = { this.application.getId(), domain.getId(), relation };
		String[] sig = { String.class.getName(), String.class.getName(), IRelation.class.getName() };
		String oname = this.console.getAonSecurityName();
		this.console.invoke( oname, IOperation.UPDATE_PROFILE, params, sig );
	}
	
	/**
	 * Romoves a profile, inside current domain.
	 *  
	 * @throws DeploymentException 
	 */
	public void removeProfile() throws DeploymentException {
		removeProfile(this.relation);
	}

	/**
	 * Updates a profile, inside current domain.
	 *  
	 * @param relation
	 * @throws DeploymentException 
	 */
	private void removeProfile(IRelation relation) throws DeploymentException {
		Object[] params = { this.application.getId(), domain.getId(), relation };
		String[] sig = { String.class.getName(), String.class.getName(), IRelation.class.getName() };
		String oname = this.console.getAonSecurityName();
		this.console.invoke( oname, IOperation.REMOVE_PROFILE, params, sig );
	}

	/**
	 * Update user, in the application domain.
	 * 
	 * @param relation
	 * @throws DeploymentException 
	 */
	public void saveUser() throws DeploymentException {
		saveUser(this.relation);
	}
	
	/**
	 * Add or update user, in the application domain.
	 * 
	 * @param relation
	 * @throws DeploymentException 
	 */
	private void saveUser(IRelation relation) throws DeploymentException {
		IUser user = findUser(relation.getId());
		Object[] params = { this.application.getId(), domain.getId(), user, null };
		String[] sig = {String.class.getName(), String.class.getName(), IUser.class.getName(), String.class.getName()};
		String oname = console.getAonSecurityName();
		this.console.invoke( oname, IOperation.UPDATE_USER, params, sig );
	}

	/**
	 * Update user profiles, in the application domain.
	 * 
	 * @param relation
	 * @throws DeploymentException 
	 */
	public void updateUserProfiles() throws DeploymentException {
		updateUserProfiles(this.relation);
	}

	/**
	 * Update user profiles, in the application domain.
	 * 
	 * @param relation
	 * @throws DeploymentException 
	 */
	private void updateUserProfiles(IRelation relation) throws DeploymentException {
		Object[] params = { this.application.getId(), domain.getId(), relation };
		String[] sig = {String.class.getName(), String.class.getName(), IRelation.class.getName()};
		boolean hasRelations = this.relation.relations().size() > 0;
		String operation = (hasRelations)? IOperation.UPDATE_RELATION: IOperation.REMOVE_RELATION;
		String oname = console.getAonSecurityName();
		this.console.invoke( oname, operation, params, sig );
		
	}
	
	/**
	 * Find <code>IUser</code> and its relations and assigns them, new user will be created 
	 * if no one is found.
	 * 
	 * @param username
	 * @return IUser
	 */
	public IUser findUser(String username) {
		return domain.getStandaloneUser( username );
	}

	/**
	 * Find the application for requested application context path. 
	 *
	 * @param ctx
	 * @throws DeploymentException
	 */
	public void findApplication(String ctx) throws DeploymentException {
		findApplication(ctx, IOperation.GET_APPLICATION4CTX);
	}

	/**
	 * Find the application for requested application id. 
	 *
	 * @param id
	 * @throws DeploymentException
	 */
	public void findApplicationById(String id) throws DeploymentException {
		findApplication(id, IOperation.GET_APPLICATION);
	}

	/**
	 * Find the application for requested application context path. 
	 *
	 * @param s
	 * @param operation
	 * @throws DeploymentException
	 */
	public void findApplication(String s, String operation) throws DeploymentException {
		Object[] params = { s };
		String[] sig = { String.class.getName() };
		String oname = this.console.getAonSecurityName();
		this.application = 
			(IApplication) this.console.invoke( oname, operation, params, sig );
	}

	public Collection<IDomainApplication> getDomainApplications() {
		return domain.applications();
	}

    /**
     * Available roles list defined in application.
     * 
     * @return List
     * @throws ManagerBeanException
     */
    public List<SelectItem> getAvailableRoles() {
        List<SelectItem> list = new ArrayList<SelectItem>();
    	Collection<IRole> c = this.application.roles();
        Iterator<IRole> iter = c.iterator();
        while (iter.hasNext()) {
            IRole r = iter.next();
            SelectItem item = new SelectItem( r.getId(), r.getId() );
            list.add(item);
        }
        return list;
    }

    /**
     * Available profiles list defined in domain application.
     * 
     * @return List
     * @throws ManagerBeanException
     */
    public List<SelectItem> getAvailableProfiles() {
        List<SelectItem> list = new ArrayList<SelectItem>();
    	Collection<IRelation> c = domain.getDomainApplication( this.application.getId() ).profiles();
        Iterator<IRelation> iter = c.iterator();
        while (iter.hasNext()) {
            IRelation r = iter.next();
            SelectItem item = new SelectItem( r.getId(), r.getId() );
            list.add(item);
        }
        return list;
    }

	/**
     * Selected roles / profiles.
	 * 
	 * @return
	 */
    public String[] getRelations() { 
		String[] list = new String[0];
    	if ( this.relation != null ) {
    		List<String> relations = this.relation.relations();
    		list = new String[ relations.size() ];
	        Iterator<String> iter = relations.iterator();
	        int i = 0;
	        while (iter.hasNext()){
	            String role = (String) iter.next();
	            list[i++] = role;
	        }
    	}
        return list;
    }

    public void setRelations(String[] relations) {
        ( (Relation) this.relation ).setRelations( Arrays.asList(relations) );
    }

	/**
	 * @return Returns the domain.
	 */
	public IDomain getDomain() {
		return domain;
	}
    
	/**
	 * @return Returns the application.
	 */
	public IApplication getApplication() {
		return application;
	}

	/**
	 * @param application The application to set.
	 */
	public void setApplication(IApplication application) {
		this.application = application;
	}

	/**
	 * @return Returns the relation (profile / user).
	 */
	public Relation getRelation() {
		return relation;
	}

	/**
	 * @param relation The relation to set.
	 */
	public void setRelation(Relation relation) {
		this.relation = relation;
	}
}
