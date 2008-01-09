package com.code.aon.bridge.plugin;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Logger;

import com.code.aon.bridge.jmx.mbean.IConsoleAdmin;
import com.code.aon.bridge.jmx.mbean.IOperation;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.jaas.client.ast.IApplication;
import com.code.aon.jaas.client.ast.IDomain;
import com.code.aon.jaas.client.ast.IRelation;
import com.code.aon.jaas.client.ast.IRole;
import com.code.aon.jaas.deployment.DeploymentException;

/**
 * @author Consulting & Development. Iñaki Ayerbe - 20-dec-2007
 *
 */

public class DomainManager implements Serializable {

	/** DomainManager Logger instance. */
	static final Logger LOGGER = Logger.getLogger( DomainManager.class.getName() );
	/** Default context name. */
	public static final String SECURITY_CONTEXT_NAME = "/aon-security"; 

    transient IConsoleAdmin console;
    /** Requested application identifier. */
    IApplication application;
    /** Domain identifier. */
    String domainId;

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
		this.domainId = name;
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
		return this.application.getDomain( this.domainId ).getDomainApplication( id ).profiles();
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
		if ( this.domainId != null ) {
			String id = ( appId == null )? this.application.getId(): appId;
			IDomain domain = this.application.getDomain( this.domainId );
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
	 * Adds a profile or updates existing one, inside current domain.
	 *  
	 * @param relation
	 * @throws DeploymentException 
	 */
	public void saveProfile(IRelation relation) throws DeploymentException {
		Object[] params = { this.application.getId(), domainId, relation };
		String[] sig = { String.class.getName(), String.class.getName(), IRelation.class.getName() };
		String oname = this.console.getAonSecurityName();
		this.console.invoke( oname, IOperation.UPDATE_PROFILE, params, sig );
	}

	/**
	 * Find the application for requested application context path. 
	 *
	 * @param ctx
	 * @throws DeploymentException
	 */
	private void findApplication(String ctx) throws DeploymentException {
		Object[] params = { ctx };
		String[] sig = { String.class.getName() };
		String oname = this.console.getAonSecurityName();
		this.application = 
			(IApplication) this.console.invoke( oname, IOperation.GET_APPLICATION4CTX, params, sig );
	}

}
