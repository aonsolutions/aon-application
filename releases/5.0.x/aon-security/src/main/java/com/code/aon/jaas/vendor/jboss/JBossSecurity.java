package com.code.aon.jaas.vendor.jboss;

import java.net.URL;
import java.security.Principal;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import org.jboss.system.ServiceMBeanSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.jaas.client.ast.IAccessPolicy;
import com.code.aon.jaas.client.ast.IApplication;
import com.code.aon.jaas.client.ast.IDataSourceMetaData;
import com.code.aon.jaas.client.ast.IDomain;
import com.code.aon.jaas.client.ast.IRelation;
import com.code.aon.jaas.client.ast.IUser;
import com.code.aon.jaas.client.ast.core.AstLoader;
import com.code.aon.jaas.client.xml.ApplicationsRenderer;
import com.code.aon.jaas.deployment.event.IDeployerListener;
import com.code.aon.jaas.deployment.util.FileUtils;
import com.code.aon.jaas.storage.ApplicationsStorage;
import com.code.aon.jaas.storage.IOperation;
import com.code.aon.jaas.storage.StorageException;
import com.code.aon.jaas.storage.StorageManager;
import com.code.aon.jaas.storage.StorageSupport;

/**
 * Clase encargada de manegar la seguridad definida en el servidor de aplicaciones.
 * Delega en <code>SecurityFile</code> a la hora de realizar 
 * su cometido.
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 17-may-2004
 * @since 1.0
 *  
 * @jmx:mbean name="jboss.admin:service=AonSecurity" extends="org.jboss.system.ServiceMBean"
 */
public class JBossSecurity extends ServiceMBeanSupport 
							implements IOperation, JBossSecurityMBean {

	/** JBossSecurity Logger instance. */
	private final static Logger LOGGER = LoggerFactory.getLogger(JBossSecurity.class);

	/** Deployed application storage support. */
	StorageSupport storage = new StorageSupport();

	/**
     * Problems with Tomcat forces me to implement this wrapper around applications base method. 
     * Tomcat does not allow method calls without parameters.
     * 
	 * @jmx:managed-operation
     */
	public Collection applications(String apps) {
		return applications();
	}

	/**(non-Javadoc)
     * @see com.code.aon.jaas.storage.IOperation#applications()
     * 
	 * @jmx:managed-operation
     */
	public Collection applications() {
		sanityCheck();
		if ( LOGGER.isDebugEnabled() )
			LOGGER.debug("Retrieving deployed applications" );

		return this.storage.applications();
	}

    /**(non-Javadoc)
     * @see com.code.aon.jaas.storage.IOperation#getApplication(java.lang.String)
     * 
	 * @jmx:managed-operation
     */
    public IApplication getApplication(String name) {
		sanityCheck();
		if ( LOGGER.isDebugEnabled() )
			LOGGER.debug("Retrieving application for: NAME[{}]", name );

		return this.storage.getApplication(name);   
	}

	/** (non-Javadoc)
	 * @see com.code.aon.jaas.storage.IOperation#getApplication4Ctx(java.lang.String)
     * 
	 * @jmx:managed-operation
	 */
	public IApplication getApplication4Ctx(String ctx) {
		sanityCheck();
		if ( LOGGER.isDebugEnabled() )
			LOGGER.debug("Retrieving application for: CONTEXT[{}]", ctx);

		return this.storage.getApplication4Ctx(ctx);   
	}

	/**(non-Javadoc)
     * @see com.code.aon.jaas.storage.IOperation#getSDApplications(java.lang.String)
     * 
	 * @jmx:managed-operation
     */
	public Collection getSDApplications(String securityDomain) {
		sanityCheck();
		if ( LOGGER.isDebugEnabled() )
			LOGGER.debug("Retrieving deployed applications for: SECURITY-DOMAIN[{}]", securityDomain );

		return this.storage.getSDApplications(securityDomain);
	}

	/**(non-Javadoc)
	 * @see com.code.aon.jaas.storage.IOperation#getUserApplications(java.lang.String, java.lang.String)
     * 
	 * @jmx:managed-operation
     */
	public List getUserApplications(String domainId, String userId) {
		sanityCheck();
		if ( LOGGER.isDebugEnabled() )
			LOGGER.debug("Retrieving user applications for: DOMAIN[{}], USER [{}]", domainId, userId );

		return this.storage.getUserApplications( domainId, userId );
	}

	/**(non-Javadoc)
	 * @see com.code.aon.jaas.storage.IOperation#getDomainNames2Import(java.lang.String)
	 * 
	 * @jmx:managed-operation
	 */
	public List getDomainNames2Import(String appId) {
		sanityCheck();
		if ( LOGGER.isDebugEnabled() )
			LOGGER.debug("Retrieving domain names to import for: APPLICATION[{}]", appId );

		return this.storage.getDomainNames2Import( appId );
	}

	/**(non-Javadoc)
	 * @see com.code.aon.jaas.storage.IOperation#getDSMDProperties(java.security.Principal)
     * 
	 * @jmx:managed-operation
	 */
	public Properties getDSMDProperties(Principal principal) {
		sanityCheck();
		if ( LOGGER.isDebugEnabled() )
			LOGGER.debug("Retrieving DataSource properties for: PRINCIPAL[{}]", principal );

		return this.storage.getDSMDProperties(principal);
	}

	/**(non-Javadoc)
	 * @see com.code.aon.jaas.storage.IOperation#addDomain(java.lang.String, com.code.aon.jaas.client.ast.IDomain, Boolean)
     * 
	 * @jmx:managed-operation
	 */
	public void addDomain(String appId, IDomain domain, Boolean flag) throws StorageException {
		if ( LOGGER.isDebugEnabled() )
			LOGGER.debug("Adding a domain {} for: APPLICATION[{}]", domain.getId(), appId );

		this.storage.addDomain( appId, domain, flag );
	}

	/**(non-Javadoc)
	 * @see com.code.aon.jaas.storage.IOperation#loadDomain(com.code.aon.jaas.client.ast.IDomain)
     * 
	 * @jmx:managed-operation
	 */
	public void loadDomain(IDomain domain) throws StorageException {
		if ( LOGGER.isDebugEnabled() )
			LOGGER.debug("Loading domain {}", domain.getId() );

		this.storage.loadDomain( domain );
	}

	/** (non-Javadoc)
	 * @see com.code.aon.jaas.storage.IOperation#getDomain(java.lang.String, java.lang.String)
     * 
	 * @jmx:managed-operation
	 */
	public IDomain getDomain(String appContext, String domainId) {
		sanityCheck();
		if ( LOGGER.isDebugEnabled() )
			LOGGER.debug("Retrieving IDomain for: CONTEXT[{}]", appContext );

		return this.storage.getDomain(appContext, domainId);
	}

	/**(non-Javadoc)
	 * @see com.code.aon.jaas.storage.IOperation#loadUsers(com.code.aon.jaas.client.ast.IDomain)
     * 
	 * @jmx:managed-operation
	 */
	public List loadUsers(IDomain domain) throws StorageException {
		if ( LOGGER.isDebugEnabled() )
			LOGGER.debug("Loading users and relations {}", domain.getId() );

		return this.storage.loadUsers( domain );
	}

	/** (non-Javadoc)
	 * @see com.code.aon.jaas.storage.IOperation#addUser(java.lang.String, java.lang.String, com.code.aon.jaas.client.ast.IUser, java.lang.String)
     * 
	 * @jmx:managed-operation
	 */
	public void addUser(String appId, String domainId, IUser user, String oldUserId) throws StorageException {
		if ( LOGGER.isDebugEnabled() )
			LOGGER.debug("Adding IUser {} for: DOMAIN[{}], APPLICATION[{}]", new Object[] {user.getId(), domainId, appId} );

		this.storage.addUser( appId, domainId, user, oldUserId );
	}

	/**(non-Javadoc)
	 * @see com.code.aon.jaas.storage.IOperation#getUser(java.security.Principal)
	 * 
	 * @jmx:managed-operation
	 */
	public IUser getUser(Principal principal) {
		sanityCheck();
		if ( LOGGER.isDebugEnabled() )
			LOGGER.debug("Retrieving IUser for: PRINCIPAL[{}]", principal );

		return this.storage.getUser(principal);
	}

	/** (non-Javadoc)
	 * @see com.code.aon.jaas.storage.IOperation#getUser(java.lang.String, java.lang.String, java.lang.String)
	 * 
	 * @jmx:managed-operation
	 */
	public IUser getUser(String appContext, String domainId, String userId) {
		sanityCheck();
		if ( LOGGER.isDebugEnabled() )
			LOGGER.debug("Retrieving IUser for: USERNAME[{}]", userId );

		return this.storage.getUser( appContext, domainId, userId );
	}

	/**(non-Javadoc)
	 * @see com.code.aon.jaas.storage.IOperation#initApplicationDeployed(java.lang.String, java.lang.String, java.lang.Boolean, java.lang.String, com.code.aon.jaas.client.ast.IAccessPolicy, com.code.aon.jaas.client.ast.IDataSourceMetaData)
	 * 
	 * @jmx:managed-operation
	 */
	public void initApplicationDeployed(String appId, String domainId, Boolean privileged, String contextExtraInfo, IAccessPolicy accessPolicy, IDataSourceMetaData metadata) 
				throws StorageException {
		if ( LOGGER.isDebugEnabled() )
			LOGGER.debug("Initializing Deployed Application for: NAME[{}]", appId );

		this.storage.initApplicationDeployed(appId, domainId, privileged, contextExtraInfo, accessPolicy, metadata);
	}

	/**(non-Javadoc)
	 * @see com.code.aon.jaas.storage.IOperation#removeDomain(java.lang.String, com.code.aon.jaas.client.ast.IDomain)
     * 
	 * @jmx:managed-operation
	 */
	public IDomain removeDomain(String appId, IDomain domain) throws StorageException {
		if ( LOGGER.isDebugEnabled() )
			LOGGER.debug("Removing domain from: APPLICATION[{}]", appId );

		return this.storage.removeDomain( appId, domain );
	}

	/**(non-Javadoc)
	 * @see com.code.aon.jaas.storage.IOperation#removeProfile(java.lang.String, java.lang.String, com.code.aon.jaas.client.ast.IRelation)
     * 
	 * @jmx:managed-operation
	 */
	public IRelation removeProfile(String appId, String domainId, IRelation relation) throws StorageException {
		if ( LOGGER.isDebugEnabled() )
			LOGGER.debug("Removing profile relation from: DOMAIN[{}], APPLICATION[{}]", domainId, appId );

		return this.storage.removeProfile( appId, domainId, relation );
	}

	/**(non-Javadoc)
	 * @see com.code.aon.jaas.storage.IOperation#removeRelation(java.lang.String, java.lang.String, com.code.aon.jaas.client.ast.IRelation)
     * 
	 * @jmx:managed-operation
	 */
	public IRelation removeRelation(String appId, String domainId, IRelation relation) throws StorageException {
		if ( LOGGER.isDebugEnabled() )
			LOGGER.debug("Removing user relation from: DOMAIN[{}], APPLICATION[{}]", domainId, appId );

		return this.storage.removeRelation( appId, domainId, relation );
	}

	/**(non-Javadoc)
	 * @see com.code.aon.jaas.storage.IOperation#removeUser(java.lang.String, java.lang.String, com.code.aon.jaas.client.ast.IUser)
     * 
	 * @jmx:managed-operation
	 */
	public IUser removeUser(String appId, String domainId, IUser user) throws StorageException {
		if ( LOGGER.isDebugEnabled() )
			LOGGER.debug("Removing user for: DOMAIN[{}], APPLICATION[{}]", domainId, appId );

		return this.storage.removeUser( appId, domainId, user );
	}

	/** (non-Javadoc)
	 * @see com.code.aon.jaas.storage.IOperation#updateAccessPolicy(java.lang.String, java.lang.String, com.code.aon.jaas.client.ast.IAccessPolicy)
	 * 
	 * @jmx:managed-operation
	 */
	public void updateAccessPolicy(String appId, String domainId, IAccessPolicy accessPolicy) throws StorageException {
		if ( LOGGER.isDebugEnabled() )
			LOGGER.debug("Updating Access Policy for: DOMAIN[{}], APPLICATION[{}]", domainId, appId );

		this.storage.updateAccessPolicy(appId, domainId, accessPolicy);
	}

	/** (non-Javadoc)
	 * @see com.code.aon.jaas.storage.IOperation#updateDSMD(java.lang.String, java.lang.String, com.code.aon.jaas.client.ast.IDataSourceMetaData)
	 * 
	 * @jmx:managed-operation
	 */
	public void updateDSMD(String appId, String domainId, IDataSourceMetaData metadata) throws StorageException {
		if ( LOGGER.isDebugEnabled() )
			LOGGER.debug("Updating Datasource Properties for: DOMAIN[{}], APPLICATION[{}]", domainId, appId );

		this.storage.updateDSMD(appId, domainId, metadata);
	}

	/**(non-Javadoc)
	 * @see com.code.aon.jaas.storage.IOperation#updateDomain(java.lang.String, com.code.aon.jaas.client.ast.IDomain)
     * 
	 * @jmx:managed-operation
	 */
	public void updateDomain(String appId, IDomain domain) throws StorageException {
		if ( LOGGER.isDebugEnabled() )
			LOGGER.debug("Adding domain to: APPLICATION[{}]", appId );

		this.storage.updateDomain( appId, domain );
	}

	/**(non-Javadoc)
	 * @see com.code.aon.jaas.storage.IOperation#updateProfile(java.lang.String, java.lang.String, com.code.aon.jaas.client.ast.IRelation)
     * 
	 * @jmx:managed-operation
	 */
	public IRelation updateProfile(String appId, String domainId, IRelation relation) throws StorageException {
		if ( LOGGER.isDebugEnabled() )
			LOGGER.debug("Updating profile relation for: DOMAIN[{}], APPLICATION[{}]", domainId, appId );

		return this.storage.updateProfile( appId, domainId, relation );
	}

	/**(non-Javadoc)
	 * @see com.code.aon.jaas.storage.IOperation#updateRelation(java.lang.String, java.lang.String, java.lang.String, com.code.aon.jaas.client.ast.IRelation)
     * 
	 * @jmx:managed-operation
	 */
	public IRelation updateRelation(String appId, String domainId, IRelation relation) throws StorageException {
		if ( LOGGER.isDebugEnabled() )
			LOGGER.debug("Updating user relation for: DOMAIN[{}], APPLICATION[{}]", domainId, appId );

		return this.storage.updateRelation( appId, domainId, relation );
	}

	/**(non-Javadoc)
	 * @see com.code.aon.jaas.storage.IOperation#updateUser(java.lang.String, java.lang.String, com.code.aon.jaas.client.ast.IUser, java.lang.String)
	 * 
	 * @jmx:managed-operation
	 */
	public IUser updateUser(String appId, String domainId, IUser user, String oldUserId) throws StorageException {
		if ( LOGGER.isDebugEnabled() )
			LOGGER.debug("Updating IUser {} for: DOMAIN[{}], APPLICATION[{}]", new Object[]{user.getId(), domainId, appId} );

		return this.storage.updateUser( appId, domainId, user, oldUserId );
	}

	/** (non-Javadoc)
     * @see com.code.aon.jaas.storage.IOperation#read(com.code.aon.jaas.client.ast.IDomain)
     * 
	 * @jmx:managed-operation
     */
	public URL read(IDomain domain) throws StorageException {
		return this.storage.read(domain);
	}

	/** (non-Javadoc)
     * @see com.code.aon.jaas.storage.IOperation#write(com.code.aon.jaas.client.ast.IDomain)
     * 
	 * @jmx:managed-operation
     */
	public void write(IDomain domain) throws StorageException {
		this.storage.write(domain);
	}

    /* (non-Javadoc)
     * @see org.jboss.system.ServiceMBeanSupport#startService()
     */
	protected void startService() throws Exception {
		ObjectName oname = new ObjectName(JBossMainDeployerMBean.OBJECT_NAME);
		URL config = 
			(URL) server.invoke(oname, "getConfigResource", new Object[] { null }, new String[] { String.class.getName() });
		this.storage.as = 
			(ApplicationsStorage) AstLoader.getInstance().parse( 0, config.openStream() );
		this.storage.as.initialize( StorageManager.getInstance( config, ApplicationsRenderer.getInstance() ) );
		Object[] params = { this.storage.as };
		String[] sig = { IDeployerListener.class.getName() };
		server.invoke( oname, "addDeployerListener", params, sig );
		Iterator iter = this.storage.as.applications().values().iterator();
		while (iter.hasNext()) {
			IApplication app = (IApplication) iter.next();
			ObjectName name = new ObjectName("jboss.system:type=ServerConfig");
			String home = ( (URL) getServer().getAttribute(name, "ServerHomeURL") ).getPath();
			URL appURL = new URL( "file:" + home + "deploy/" + app.getId() );
			server.invoke(oname, "deploy", new Object[] { appURL }, new String[] { URL.class.getName() });
		}
		String dirtyFile = this.storage.as.getStorageDir().getCanonicalPath();
		FileUtils.getUptodateFile( dirtyFile ).createNewFile();
	}

    /* (non-Javadoc)
     * @see org.jboss.system.ServiceMBeanSupport#stopService()
     */
	protected void stopService() throws Exception {
		ObjectName oname = new ObjectName(JBossMainDeployerMBean.OBJECT_NAME);
		Object[] params = { this.storage.as };
		String[] sig = { IDeployerListener.class.getName() };
		server.invoke( oname, "removeDeployerListener", params, sig );
		Iterator iter = this.storage.as.applications().values().iterator();
		while (iter.hasNext()) {
			IApplication app = (IApplication) iter.next();
			ObjectName name = new ObjectName("jboss.system:type=ServerConfig");
			String home = ( (URL) getServer().getAttribute(name, "ServerHomeURL") ).getPath();
			URL appURL = new URL( "file:" + home + "deploy/" + app.getId() );
			server.invoke(oname, "undeploy", new Object[] { appURL }, new String[] { URL.class.getName() });
		}
		this.storage.as = null;
		this.storage = new StorageSupport();
	}

	/**
	 * Flush Authentication cache domain.
	 * 
	 * @param domain
	 * @throws MalformedObjectNameException
	 * @throws InstanceNotFoundException
	 * @throws MBeanException
	 * @throws ReflectionException
	 */
	protected void flushAuthenticationCache(String domain)
			throws MalformedObjectNameException, InstanceNotFoundException, MBeanException, ReflectionException {
		ObjectName jaasMgr = new ObjectName("jboss.security:service=JaasSecurityManager");
		Object[] params = { domain };
		String[] signature = { String.class.getName() };
		getServer().invoke(jaasMgr, "flushAuthenticationCache", params, signature);
	}

	/**
	 * Verifies if applications must be re-deployed because another 
	 * entity( Application Server, ... ) has updated application or domains resource files.
	 */
	private void sanityCheck() {
		if ( this.storage.as.isDirty() ) {
			try {
				if ( LOGGER.isDebugEnabled() )
					LOGGER.debug( "Re-Loading applications and domains because of an external update has been made." );

				stopService();
				startService();
			} catch (Exception e) {
				LOGGER.error( e.getMessage(), e );
			}
		}
	}

}