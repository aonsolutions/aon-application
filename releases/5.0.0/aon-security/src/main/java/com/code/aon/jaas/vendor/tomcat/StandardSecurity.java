/*
 * Created on 12-sep-2006
 *
 */
package com.code.aon.jaas.vendor.tomcat;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.net.URL;
import java.security.Principal;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.management.ObjectName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.jaas.client.ast.IAccessPolicy;
import com.code.aon.jaas.client.ast.IApplication;
import com.code.aon.jaas.client.ast.IDataSourceMetaData;
import com.code.aon.jaas.client.ast.IDomain;
import com.code.aon.jaas.client.ast.IRelation;
import com.code.aon.jaas.client.ast.IUser;
import com.code.aon.jaas.client.ast.core.Application;
import com.code.aon.jaas.client.ast.core.AstLoader;
import com.code.aon.jaas.client.ast.core.Option;
import com.code.aon.jaas.client.xml.ApplicationsRenderer;
import com.code.aon.jaas.deployment.event.IDeployerListener;
import com.code.aon.jaas.storage.ApplicationsStorage;
import com.code.aon.jaas.storage.ConfigurationStorage;
import com.code.aon.jaas.storage.IOperation;
import com.code.aon.jaas.storage.StorageException;
import com.code.aon.jaas.storage.StorageManager;
import com.code.aon.jaas.storage.StorageSupport;

/**
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 12-sep-2005
 * @since 1.0
 *
 * @jmx:mbean name="Catalina:type=Security,name=AonSecurity" extends="com.code.aon.jaas.vendor.tomcat.SecurityMBean"
 */

public final class StandardSecurity extends SecurityMBeanSupport 
		implements IOperation, StandardSecurityMBean {

	/** StandardSecurity Logger instance. */
	private final static Logger LOGGER = LoggerFactory.getLogger(StandardSecurity.class);
	/** login.config relative path. */
	static final String LOGIN_CONFIG_FILE_RELATIVE_PATH = 
		File.separator + "conf" + File.separator + "login.config";

	/** Deployed applications store. */
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
		LOGGER.debug("Retrieving deployed applications");
		return this.storage.applications();
	}

	/**(non-Javadoc)
     * @see com.code.aon.jaas.storage.IOperation#getApplication(java.lang.String)
     * 
	 * @jmx:managed-operation
     */
    public IApplication getApplication(String name) {
		LOGGER.debug("Retrieving application for: NAME[{}]", name);
		return this.storage.getApplication(name);   
    }

	/** (non-Javadoc)
	 * @see com.code.aon.jaas.storage.IOperation#getApplication4Ctx(java.lang.String)
     * 
	 * @jmx:managed-operation
	 */
	public IApplication getApplication4Ctx(String ctx) {
		LOGGER.debug("Retrieving application for: CONTEXT[{}]", ctx);
		return this.storage.getApplication4Ctx(ctx);   
	}

	/**(non-Javadoc)
     * @see com.code.aon.jaas.storage.IOperation#getSDApplications(java.lang.String)
     * 
	 * @jmx:managed-operation
     */
	public Collection getSDApplications(String securityDomain) {
		LOGGER.debug("Retrieving deployed applications for: SECURITY-DOMAIN[{}]", securityDomain );
		return this.storage.getSDApplications(securityDomain);
	}

	/**(non-Javadoc)
	 * @see com.code.aon.jaas.storage.IOperation#getUserApplications(java.lang.String, java.lang.String)
     * 
	 * @jmx:managed-operation
     */
	public List getUserApplications(String domainId, String userId) {
		LOGGER.debug("Retrieving user applications for: DOMAIN[{}], USER [{}]", domainId, userId );
		return this.storage.getUserApplications( domainId, userId );
	}

	/**
     * Problems with Tomcat forces me to implement this wrapper around getDSMDProperties base method. 
	 * 
	 * @jmx:managed-operation
	 */
	public Properties getDSMDProperties(String principal) {
		LOGGER.debug("STRING: Retrieving Properties  for: PRINCIPAL[{}]", principal );
		return null;
	}

	/**(non-Javadoc)
	 * @see com.code.aon.jaas.storage.IOperation#getDSMDProperties(java.security.Principal)
     * 
	 * @jmx:managed-operation
	 */
	public Properties getDSMDProperties(Principal principal) {
		LOGGER.debug("Retrieving DataSource properties for: PRINCIPAL[{}]", principal );
		return this.storage.getDSMDProperties(principal);
	}

	/**(non-Javadoc)
	 * @see com.code.aon.jaas.storage.IOperation#getDomains2Import(java.lang.String)
     * 
	 * @jmx:managed-operation
	 */
	public List getDomainNames2Import(String appId) {
		LOGGER.debug("Retrieving domain names to import for: APPLICATION[{}]", appId );
		return this.storage.getDomainNames2Import( appId );
	}

	/**
     * Problems with Tomcat forces me to implement this wrapper around addDomain base method.
     * 
	 * @jmx:managed-operation
	 */
	public void addDomain(String appId, String domain, String flag) throws StorageException {
		LOGGER.debug("STRING: Adding a domain {} for: APPLICATION[{}]", domain, appId );
	}

	/**(non-Javadoc)
	 * @see com.code.aon.jaas.storage.IOperation#addDomain(java.lang.String, com.code.aon.jaas.client.ast.IDomain, Boolean)
     * 
	 * @jmx:managed-operation
	 */
	public void addDomain(String appId, IDomain domain, Boolean flag) throws StorageException {
		LOGGER.debug("Adding a domain {} for: APPLICATION[{}]", domain, appId );
		this.storage.addDomain( appId, domain, flag );
	}

	/**
     * Problems with Tomcat forces me to implement this wrapper around loadDomain base method.
	 * 
	 * @jmx:managed-operation
	 */
	public void loadDomain(String domain) throws StorageException {
		LOGGER.debug("Loading domain {}", domain );
	}

	/**(non-Javadoc)
	 * @see com.code.aon.jaas.storage.IOperation#loadDomain(com.code.aon.jaas.client.ast.IDomain)
	 * 
	 * @jmx:managed-operation
	 */
	public void loadDomain(IDomain domain) throws StorageException {
		LOGGER.debug("Loading domain {}", domain.getId() );
		this.storage.loadDomain( domain );
	}

	/** (non-Javadoc)
	 * @see com.code.aon.jaas.storage.IOperation#getDomain(java.lang.String, java.lang.String)
     * 
	 * @jmx:managed-operation
	 */
	public IDomain getDomain(String appContext, String domainId) {
		LOGGER.debug("Retrieving IDomain for: CONTEXT[{}]", appContext );
		return this.storage.getDomain(appContext, domainId);
	}

	/**
     * Problems with Tomcat forces me to implement this wrapper around loadUsers base method.
	 * 
	 * @jmx:managed-operation
	 */
	public List loadUsers(String domain) throws StorageException {
		LOGGER.debug("Loading users {}", domain );
		return null;
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

	/**
     * Problems with Tomcat forces me to implement this wrapper around addUser base method.
     * 
	 * @jmx:managed-operation
	 */
	public void addUser(String appName, String domainId, String user, String oldUserId) throws StorageException {
		LOGGER.debug("STRING: Adding IUser for: DOMAIN[{}], APPLICATION[{}]", domainId, appName );
	}

	/** (non-Javadoc)
	 * @see com.code.aon.jaas.storage.IOperation#addUser(java.lang.String, java.lang.String, com.code.aon.jaas.client.ast.IUser, java.lang.String)
     * 
	 * @jmx:managed-operation
	 */
	public void addUser(String appId, String domainId, IUser user, String oldUserId) throws StorageException {
		LOGGER.info("Adding IUser {} for: DOMAIN[{}], APPLICATION[{}]", new Object[]{user.getId(), domainId, appId} );
		this.storage.addUser( appId, domainId, user, oldUserId );
	}

	/**
     * Problems with Tomcat forces me to implement this wrapper around getUser base method.
     *  
	 * @jmx:managed-operation
	 */
	public IUser getUser(String principal) {
		LOGGER.debug("STRING: Retrieving IUser for PRINCIPAL[{}]", principal );
		return null;
	}

	/**(non-Javadoc)
	 * @see com.code.aon.jaas.storage.IOperation#getUser(java.security.Principal)
	 * 
	 * @jmx:managed-operation
	 */
	public IUser getUser(Principal principal) {
		LOGGER.debug("Retrieving IUser for: PRINCIPAL[{}]", principal );
		return this.storage.getUser(principal);
	}

	/** (non-Javadoc)
	 * @see com.code.aon.jaas.storage.IOperation#getUser(java.lang.String, java.lang.String, java.lang.String)
	 * 
	 * @jmx:managed-operation
	 */
	public IUser getUser(String appContext, String domainId, String userId) {
		LOGGER.debug("Retrieving IUser for: USERNAME[{}]", userId );
		return this.storage.getUser( appContext, domainId, userId );
	}

	/**
	 * Problems with Tomcat forces me to implement this wrapper around initApplicationDeployed base method. 
	 * 
	 * @jmx:managed-operation
	 */
	public void initApplicationDeployed(String appName, String domainName, String privileged, String contextExtraInfo, String accessPolicy, String metadata) 
			throws StorageException {
		LOGGER.debug("STRING: Initializing Deployed Application for: NAME[{}]", appName );
	}

	/**(non-Javadoc)
	 * @see com.code.aon.jaas.storage.IOperation#initApplicationDeployed(java.lang.String, java.lang.String, java.lang.Boolean, java.lang.String, com.code.aon.jaas.client.ast.IAccessPolicy, com.code.aon.jaas.client.ast.IDataSourceMetaData)
	 * 
	 * @jmx:managed-operation
	 */
	public void initApplicationDeployed(String appId, String domainId, 
										Boolean privileged, String contextExtraInfo,
										IAccessPolicy accessPolicy, IDataSourceMetaData metadata) 
			throws StorageException {
		LOGGER.info("Initializing Deployed Application for: DOMAIN[{}], NAME[{}]", domainId, appId );
		this.storage.initApplicationDeployed( appId, domainId, privileged, contextExtraInfo, accessPolicy, metadata );
		Application app = (Application) this.storage.getApplication(appId);
		LOGGER.info("PRIVILEGED " + privileged );
		if (privileged) {
			ConfigurationStorage conf = new ConfigurationStorage();
			conf.setApplication(app);
			conf.setContextExtraInfo(contextExtraInfo);
			conf.initialize(null);
			conf.write();
		}
		LOGGER.info("CREATED " + contextExtraInfo );
	}

	/**
	 * Problems with Tomcat forces me to implement this wrapper around removeDomain base method.
	 *  
	 * @jmx:managed-operation
	 */
	public IDomain removeDomain(String appName, String domain) throws StorageException {
		LOGGER.debug("STRING: Removing IDomain from: APPLICATION[{}]", appName );
		return null;
	}

	/**(non-Javadoc)
	 * @see com.code.aon.jaas.storage.IOperation#removeDomain(java.lang.String, com.code.aon.jaas.client.ast.IDomain)
     * 
	 * @jmx:managed-operation
	 */
	public IDomain removeDomain(String appId, IDomain domain) throws StorageException {
		LOGGER.debug("Removing domain from: APPLICATION[{}]", appId );
		return this.storage.removeDomain( appId, domain );
	}

	/**
	 * Problems with Tomcat forces me to implement this wrapper around removeProfile base method.
	 *  
	 * @jmx:managed-operation
	 */
	public IRelation removeProfile(String appName, String domainId, String relation) throws StorageException {
		LOGGER.debug("STRING: Removing IRelation from: DOMAIN[{}], APPLICATION[{}]", domainId, appName );
		return null;
	}

	/**(non-Javadoc)
	 * @see com.code.aon.jaas.storage.IOperation#removeProfile(java.lang.String, java.lang.String, com.code.aon.jaas.client.ast.IRelation)
     * 
	 * @jmx:managed-operation
	 */
	public IRelation removeProfile(String appId, String domainId, IRelation relation) throws StorageException {
		LOGGER.debug("Removing profile relation from: DOMAIN[{}], APPLICATION[{}]", domainId, appId );
		return this.storage.removeProfile( appId, domainId, relation );
	}

	/**
	 * Problems with Tomcat forces me to implement this wrapper around removeRelation base method.
	 *  
	 * @jmx:managed-operation
	 */
	public IRelation removeRelation(String appName, String domainId, String relation) throws StorageException {
		LOGGER.debug("STRING: Removing IRelation from: DOMAIN[{}], APPLICATION[{}]", domainId, appName );
		return null;
	}

	/**(non-Javadoc)
	 * @see com.code.aon.jaas.storage.IOperation#removeRelation(java.lang.String, java.lang.String, com.code.aon.jaas.client.ast.IRelation)
     * 
	 * @jmx:managed-operation
	 */
	public IRelation removeRelation(String appId, String domainId, IRelation relation) 
			throws StorageException {
		LOGGER.debug("Removing user relation from: DOMAIN[{}], APPLICATION[{}]", domainId, appId );
		return this.storage.removeRelation( appId, domainId, relation );
	}

	/**
	 * Problems with Tomcat forces me to implement this wrapper around removeUser base method.
	 *  
	 * @jmx:managed-operation
	 */
	public IUser removeUser(String appName, String domainId, String user) throws StorageException {
		LOGGER.debug("STRING: Removing IUser for: DOMAIN[{}], APPLICATION[{}]", domainId, appName );
		return null;
	}

	/**(non-Javadoc)
	 * @see com.code.aon.jaas.storage.IOperation#removeUser(java.lang.String, java.lang.String, com.code.aon.jaas.client.ast.IUser)
     * 
	 * @jmx:managed-operation
	 */
	public IUser removeUser(String appId, String domainId, IUser user) throws StorageException {
		LOGGER.debug("Removing user for: DOMAIN[{}], APPLICATION[{}]", domainId, appId );
		return this.storage.removeUser( appId, domainId, user );
	}

	/**
	 * Problems with Tomcat forces me to implement this wrapper around updateAccessPolicy base method.
	 *  
	 * @jmx:managed-operation
	 */
	public void updateAccessPolicy(String appName, String domainName, String accessPolicy) throws StorageException {
		LOGGER.debug("STRING: Updating Access Policy for: NAME[{}] and DOMAIN[{}]", appName, domainName );
	}

	/** (non-Javadoc)
	 * @see com.code.aon.jaas.storage.IOperation#updateAccessPolicy(java.lang.String, java.lang.String, com.code.aon.jaas.client.ast.IAccessPolicy)
	 * 
	 * @jmx:managed-operation
	 */
	public void updateAccessPolicy(String appId, String domainId, IAccessPolicy accessPolicy) 
			throws StorageException {
		LOGGER.debug("Updating Access Policy for: NAME[{}] and DOMAIN[{}]", appId, domainId );
		this.storage.updateAccessPolicy(appId, domainId, accessPolicy);
	}

	/**
	 * Problems with Tomcat forces me to implement this wrapper around updateDSMD base method.
	 *  
	 * @jmx:managed-operation
	 */
	public void updateDSMD(String appName, String domainName, String metadata) throws StorageException {
		LOGGER.debug("STRING: Updating Datasource Properties for: NAME[{}] and DOMAIN[{}]", appName, domainName );
	}

	/** (non-Javadoc)
	 * @see com.code.aon.jaas.storage.IOperation#updateDSMD(java.lang.String, java.lang.String, com.code.aon.jaas.client.ast.IDataSourceMetaData)
	 * 
	 * @jmx:managed-operation
	 */
	public void updateDSMD(String appId, String domainId, IDataSourceMetaData metadata) 
			throws StorageException {
		LOGGER.debug("Updating Datasource Properties for: NAME[{}] and DOMAIN[{}]", appId, domainId );
		this.storage.updateDSMD(appId, domainId, metadata);
	}

	/**
	 * Problems with Tomcat forces me to implement this wrapper around updateDomain base method.
	 *  
	 * @jmx:managed-operation
	 */
	public void updateDomain(String appName, String domain) throws StorageException {
		LOGGER.debug("STRING: Updating IDomain for: APPLICATION[{}]", appName );
	}

	/**(non-Javadoc)
	 * @see com.code.aon.jaas.storage.IOperation#updateDomain(java.lang.String, com.code.aon.jaas.client.ast.IDomain)
     * 
	 * @jmx:managed-operation
	 */
	public void updateDomain(String appId, IDomain domain) throws StorageException {
		LOGGER.debug("Updating domain to: APPLICATION[{}]", appId );
		this.storage.updateDomain( appId, domain );
	}

	/**
	 * Problems with Tomcat forces me to implement this wrapper around updateProfile base method.
	 *  
     * @jmx:managed-operation
	 */
	public IRelation updateProfile(String appName, String domainId, String relation) throws StorageException {
		LOGGER.debug("STRING: Updating IRelation for: DOMAIN[{}], APPLICATION[{}]", domainId, appName );
		return null;
	}

	/**(non-Javadoc)
	 * @see com.code.aon.jaas.storage.IOperation#updateProfile(java.lang.String, java.lang.String, com.code.aon.jaas.client.ast.IRelation)
     * 
	 * @jmx:managed-operation
	 */
	public IRelation updateProfile(String appId, String domainId, IRelation relation) 
			throws StorageException {
		LOGGER.debug("Updating profile relation for: DOMAIN[{}], APPLICATION[{}]", domainId, appId );
		return this.storage.updateProfile( appId, domainId, relation );
	}

	/**
	 * Problems with Tomcat forces me to implement this wrapper around updateRelation base method.
	 *  
	 * @jmx:managed-operation
	 */
	public IRelation updateRelation(String appName, String domainId, String relation) throws StorageException {
		LOGGER.debug("STRING: Updating IRelation for: DOMAIN[{}], APPLICATION[{}]", domainId, appName );
		return null;
	}

	/**(non-Javadoc)
	 * @see com.code.aon.jaas.storage.IOperation#updateRelation(java.lang.String, java.lang.String, java.lang.String, com.code.aon.jaas.client.ast.IRelation)
     * 
	 * @jmx:managed-operation
	 */
	public IRelation updateRelation(String appId, String domainId, IRelation relation) 
			throws StorageException {
		LOGGER.debug("Updating user relation for: DOMAIN[{}], APPLICATION[{}]", domainId, appId );
		return this.storage.updateRelation( appId, domainId, relation );
	}

	/**
	 * Problems with Tomcat forces me to implement this wrapper around updateUser base method.
	 *  
	 * @jmx:managed-operation
	 */
	public IUser updateUser(String appName, String domainId, String user, String oldUserId) throws StorageException {
		LOGGER.debug("STRING: Updating IUser for: DOMAIN[{}], APPLICATION[{}]", domainId, appName );
		return null;
	}

	/**(non-Javadoc)
	 * @see com.code.aon.jaas.storage.IOperation#updateUser(java.lang.String, java.lang.String, com.code.aon.jaas.client.ast.IUser, java.lang.String)
	 * 
	 * @jmx:managed-operation
	 */
	public IUser updateUser(String appId, String domainId, IUser user, String oldUserId) 
			throws StorageException {
		LOGGER.info("Updating IUser {} for: DOMAIN[{}], APPLICATION[{}]", new Object[]{user.getId(), domainId, appId} );
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

	/*(non-Javadoc)
	 * @see com.code.aon.jaas.vendor.tomcat.SecurityMBeanSupport#getName()
	 */
	public String getName() {
		return StandardSecurity.class.getName();
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.vendor.tomcat.SecurityMBeanSupport#startService()
	 */
	protected void startService() throws Exception {
		ObjectName oname = new ObjectName(TomcatMainDeployer.OBJECT_NAME);
		URL config = (URL) server.invoke(oname, "getConfigResource",
				new Object[] { null },
				new String[] { String.class.getName() });
		this.storage.as = 
			(ApplicationsStorage) AstLoader.getInstance().parse( 0, config.openStream() );
		this.storage.as.initialize( StorageManager.getInstance( config, ApplicationsRenderer.getInstance() ) );
//		this.storage.as.initialize( new StorageManager( config, ApplicationsRenderer.getInstance() ) );
		Object[] params = { this.storage.as };
		String[] sig = { IDeployerListener.class.getName() };
		server.invoke( oname, "addDeployerListener", params, sig );
		String loginConfigFileName = 
			(String) server.invoke( oname, "getCatalinaHome", new Object[] { "" }, new String[] { String.class.getName() } );
		loadLoginOptions( loginConfigFileName + LOGIN_CONFIG_FILE_RELATIVE_PATH );
		Iterator iter = this.storage.as.applications().values().iterator();
		while (iter.hasNext()) {
			IApplication app = (IApplication) iter.next();
			server.invoke(oname, "deploy", new Object[] { app.getId(), "" }, new String[] { String.class.getName(), String.class.getName() });
		}
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.vendor.tomcat.SecurityMBeanSupport#stopService()
	 */
	protected void stopService() throws Exception {
		ObjectName oname = new ObjectName(TomcatMainDeployer.OBJECT_NAME);
		URL config = (URL) server.invoke(oname, "getConfigResource",
										new Object[] { null },
										new String[] { String.class.getName() });
		this.storage.as = 
			(ApplicationsStorage) AstLoader.getInstance().parse( 0, config.openStream() );
		this.storage.as.initialize( StorageManager.getInstance( config, ApplicationsRenderer.getInstance() ) );
//		this.storage.as.initialize( new StorageManager( config, ApplicationsRenderer.getInstance() ) );
		Object[] params = { this.storage.as };
		String[] sig = { IDeployerListener.class.getName() };
		server.invoke( oname, "removeDeployerListener", params, sig );
		Iterator iter = this.storage.as.applications().values().iterator();
		while (iter.hasNext()) {
			IApplication app = (IApplication) iter.next();
			server.invoke(oname, "undeploy", 
						new Object[] { app.getId() }, 
						new String[] { String.class.getName() });
		}
	}

	/**
	 * Look for Login Config file and loads configuration options.
	 * 
	 * @param login
	 */
	private void loadLoginOptions(String login) {
		try {
			FileReader fr = new FileReader(login); 
			LineNumberReader reader = new LineNumberReader(fr);
			String line = reader.readLine();
			while ( line != null ) {
				if ( line != null ) {
					StringTokenizer st = new StringTokenizer( line, " " );
					while (st.hasMoreTokens()) {
						String token = st.nextToken();
						if ( token.indexOf( "=\"" ) > -1 ) {
							token = token.replace( "=\"", "&");
							token = token.replace( ';', ' ');
							StringTokenizer _st = new StringTokenizer(token, "&");
							Option opt = new Option();
							opt.setName( _st.nextToken().replace('\"', ' ').trim() );
							opt.setValue( _st.nextToken().replace('\"', ' ').trim() );
							this.storage.as.addOption( opt );
						}
					}
				}
				line = reader.readLine();
			}
		} catch (IOException e) {
			LOGGER.error( e.getMessage(), e );
		}		
	}

}
