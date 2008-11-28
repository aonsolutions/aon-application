/**
 * 
 * Copyright 1993-2006 obinary Ltd. (http://www.obinary.com) All rights reserved.
 */
package es.code.ecm.repository;

import java.io.IOException;
import java.io.InputStream;

import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.Workspace;

import es.code.ecm.security.acl.AclManager;

/**
 * @author Consulting & Development. Iñaki Ayerbe - 19/06/2007
 *
 */
public interface IProvider {

    /** JAAS config file key. */
    static final String JAAS_CONFIG_FILE_KEY = "java.security.auth.login.config";
    /** Role mappings file key. */
    static final String PROFILE_MAPPINGS_FILE_KEY = "java.security.auth.PROFILE.mapping";
	/** Repository connection user. */
    static final String REPOSITORY_CONNECTION_USER = "connection.jcr.user";
    /** Repository connection password.*/
    static final String REPOSITORY_CONNECTION_PSWD = "connection.jcr.password";

    static final String REPOSITORY_CONFIG_FILENAME_KEY = "repository.config";

	static final String REPOSITORY_HOME_KEY = "repository.home";

	static final String REPOSITORY_NAME_KEY = "repository.name";

	static final String NAMING_FACTORY_CLASS_KEY = "java.naming.factory.initial";

	static final String PROVIDER_URL_KEY = "java.naming.provider.url";

    static final String CUSTOM_PROVIDER_KEY = "provider";

    static final String CUSTOM_NODETYPES_KEY = "custom.node.types";

	static final String JAAS = "resources/jaas.config";

	static final String PROFILE_MAPPINGS = "resources/profilemappings.properties";

	static final String REPOSITORY = "resources/repository.xml";

	static final String NODETYPES = "resources/custom-nodetypes.xml";

	/**
	 * Initializes repository, this depends on the underlying repository implementation. Use any available method to get
	 * the instance of Repository.
	 * 
	 * @param ri key value pars as define in bootstrap.properties
	 * @throws IOException
	 * @throws RepositoryNotInitializedException
	 */
	void init(RepositoryInfo ri) throws IOException, RepositoryNotInitializedException;

	/**
	 * Gets repository information.
	 * 
	 * @return
	 */
	RepositoryInfo getRi();

    /**
     * Gets the repository instance initialized on init() call.
     * @throws RepositoryNotInitializedException if init failed to get repository
     */
    Repository getUnderlineRepository() throws RepositoryNotInitializedException;

    /**
     * Gets the JCR session.
     * 
     * @param sc
     * @param workspaceId
     * @return
     * @throws RepositoryException
     */
	Session getSessionInstance(SimpleCredentials sc, String workspaceId) throws RepositoryException;

    /**
     * Gets the ACL manager.
     * 
     * @return
     */
	AclManager getAclManager();

	/**
     * Register namespace with the repository. Refer JCR-170 specifications.
     * @param prefix namespace prefix
     * @param uri namespace URI
     * @param workspace session workspace instance
     * @throws RepositoryException
     */
    void registerNamespace(String prefix, String uri, Workspace workspace) throws RepositoryException;

    /**
     * Unregister namespace with the repository.
     * @param prefix as registered previously
     * @param workspace session workspace instance
     * @throws RepositoryException
     */
    void unregisterNamespace(String prefix, Workspace workspace) throws RepositoryException;

    /**
     * Node type registration is entirely dependent on the implementation. Refer JSR-170 specifications.
     * @throws RepositoryException
     */
    void registerNodeTypes() throws RepositoryException;

    /**
     * Node type registration is entirely dependent on the implementation. Refer JSR-170 specifications.
     * @throws RepositoryException
     */
    void registerNodeTypes(String configuration) throws RepositoryException;

    /**
     * Node type registration is entirely dependent on the implementation. Refer JSR-170 specifications.
     * @param stream , stream type depends on the implementation of this method
     * @throws RepositoryException
     */
    void registerNodeTypes(InputStream stream) throws RepositoryException;

    /**
     * Register a new workspace in the current repository
     * @param workspaceName workspace name
     * @return <code>true</code> true if the workspace is registered now of <code>false</code> if it was already
     * registered
     * @throws RepositoryException if any exception occours during registration
     */
    boolean registerWorkspace(String workspaceName) throws RepositoryException;

    /**
     * Register a new workspace in the current repository using passed by paramenter credentials
     * @param workspaceName workspace name
     * @param sc credentials
     * @return <code>true</code> true if the workspace is registered now of <code>false</code> if it was already
     * registered
     * @throws RepositoryException if any exception occours during registration
     */
    boolean registerWorkspace(SimpleCredentials sc, String workspaceName) throws RepositoryException;
}
