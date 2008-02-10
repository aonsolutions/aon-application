/*
 * Created on 13-sep-2005
 *
 */
package com.code.aon.jaas.storage;

import java.net.URL;
import java.security.Principal;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import com.code.aon.jaas.client.ast.IAccessPolicy;
import com.code.aon.jaas.client.ast.IApplication;
import com.code.aon.jaas.client.ast.IDataSourceMetaData;
import com.code.aon.jaas.client.ast.IDomain;
import com.code.aon.jaas.client.ast.IRelation;
import com.code.aon.jaas.client.ast.IUser;

/**
 * Application server basic operations.
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 13-sep-2005
 * @since 1.0
 *
 */

public interface IOperation {

    /**
     * Return a unmodifiable collection of registered applications.
     * 
     * @param securityDomain
     * @return Collection
     */
	Collection<IApplication> applications();

    /**
     * Return a unmodifiable collection of securityDomain applications.
     * 
     * @param securityDomain
     * @return Collection
     */
	Collection<IApplication> getSDApplications(String securityDomain);

    /**
     * Return a unmodifiable collection of user applications.
     * 
     * @param userId
     * @param domainId
     * @return Collection
     */
	List<IApplication> getUserApplications(String domainId, String userId);

	/**
     * Return the <code>IApplication</code>.
     * 
     * @param appId
     * @return IApplication
     */
	IApplication getApplication(String appId);

	/**
     * Return the <code>IApplication</code>.
	 * 
	 * @param ctx
	 * @return
	 */
	IApplication getApplication4Ctx(String ctx);

    /**
     * Return the data source properties.
     * 
     * @param Principal
     * @return Properties
     */
	Properties getDSMDProperties(Principal principal);

    /**
     * Return a collection of domain names bound to the application passed by parameter for
     * import.
     * 
     * @param appId
     * @return Collection
     */
	List<String> getDomainNames2Import(String appId);

	/**
     * Return the <code>IDomain</code>.
     * 
     * @param appContext
     * @param domainId
     * @return IDomain
     */
	IDomain getDomain(String appContext, String domainId);

	/**
	 * Return the </code>IUser</code>.
	 *  
	 * @param principal
	 * @return
	 */
	IUser getUser(Principal principal);

	/**
	 * Return </code>IUser</code>.
	 *  
	 * @param appContext
	 * @param domainId
	 * @param userId
	 * @return
	 */
	IUser getUser(String appContext, String domainId, String userId);

	/**
	 * Initialize domain AccessPolicy and application domain DataSourceMetaData 
	 * the first time an application has deployed and define application context file as well with 
	 * privileged and contextExtraInfo parameters.
	 * 
	 * @param appId
	 * @param domainId
	 * @param privileged
	 * @param contextExtraInfo
	 * @param access
	 * @param dsmd
	 * @throws DeploymentException
	 */
	void initApplicationDeployed(String appId, String domainId, Boolean privileged, String contextExtraInfo, IAccessPolicy accessPolicy, IDataSourceMetaData metadata) throws StorageException;

	/**
	 * Update domain AccessPolicy.
	 * 
	 * @param appId
	 * @param domainId
	 * @param access
	 */
	void updateAccessPolicy(String appId, String domainId, IAccessPolicy accessPolicy) throws StorageException;
	
	/**
	 * Update application domain DataSourceMetaData. 
	 * 
	 * @param appId
	 * @param domainId
	 * @param dsmd
	 */
	void updateDSMD(String appId, String domainId, IDataSourceMetaData metadata) throws StorageException;

	/**
	 * Add the the <code>IDomain</code>, and serialize it.
	 * 
	 * @param appId
	 * @param domain
	 * @param flag Indicates if the domain is being imported.
	 */
	void addDomain(String appId, IDomain domain, Boolean flag) throws StorageException ;

	/**
	 * Loads domain in each deployed applications.
	 * 
	 * @param domain
	 * @throws StorageException
	 */
	void loadDomain(IDomain domain) throws StorageException;

	/**
	 * Loads a set of users with their relations inside the selected domain..
	 * 
	 * @param domain
	 * @return array of loding errors.
	 * @throws StorageException
	 */
	List<StorageException> loadUsers(IDomain domain) throws StorageException;

	/**
	 * Update the the <code>IDomain</code>, and serialize it.
	 * 
	 * @param appId
	 * @param domain
	 * @return
	 * @throws DeploymentException
	 */
	void updateDomain(String appId, IDomain domain) throws StorageException;

	/**
	 * Remove the <code>IDomain</code>, and serialize it.
	 * 
	 * @param appId
	 * @param domain
	 * @return
	 * @throws DeploymentException
	 */
	IDomain removeDomain(String appId, IDomain domain) throws StorageException;

	/**
	 * Modifica el usuario y sus relaciones del Dominio, Entidad y Aplicación indicadas, y serializa 
	 * el fichero asociado a la entidad. En caso de no existir, lo añade.
	 * 
	 * @param appId
	 * @param domainId
	 * @param relation
	 * @return
	 * @throws DeploymentException
	 */
	IRelation updateRelation(String appId, String domainId, IRelation relation) throws StorageException;

	/**
	 * Elimina el usuario y sus relaciones del Dominio, Entidad y Aplicación indicadas, y serializa 
	 * el fichero asociado a la entidad.
	 * 
	 * @param appId
	 * @param domainId
	 * @param relation
	 * @return
	 * @throws DeploymentException
	 */
	IRelation removeRelation(String appId, String domainId, IRelation relation) throws StorageException;

	/**
	 * Modifica el perfil y sus relaciones de la Entidad y Aplicación indicadas, y serializa 
	 * el fichero asociado a la entidad. En caso de no existir, lo añade.
	 * 
	 * @param appId
	 * @param domainId
	 * @param relation
	 * @return
	 * @throws DeploymentException
	 */
	IRelation updateProfile(String appId, String domainId, IRelation relation) throws StorageException;

	/**
	 * Elimina el perfil y sus relaciones de la Entidad y Aplicación indicadas, y serializa 
	 * el fichero asociado a la entidad.
	 * 
	 * @param appId
	 * @param domainId
	 * @param relation
	 * @return
	 * @throws DeploymentException
	 */
	IRelation removeProfile(String appId, String domainId, IRelation relation) throws StorageException;

	/**
	 * Añade el usuario a la Aplicación y Dominio indicados, y serializa el fichero asociado 
	 * a la entidad.
	 * 
	 * @param appId
	 * @param domainId
	 * @param user
	 * @param oldUserId
	 * @throws DeploymentException
	 */
	void addUser(String appId, String domainId, IUser user, String oldUserId) throws StorageException;

	/**
	 * Modifica el usuario en la Aplicación y Dominio indicados, y serializa el fichero asociado 
	 * a la entidad.
	 * 
	 * @param appId
	 * @param domainId
	 * @param user
	 * @param oldUserId
	 * 
	 * @return
	 * @throws DeploymentException
	 */
	IUser updateUser(String appId, String domainId, IUser user, String oldUserId) throws StorageException;

	/**
	 * Elimina el usuario en la Entidad y Aplicación indicadas, y serializa el fichero asociado 
	 * a la entidad.
	 * 
	 * @param appId
	 * @param domainId
	 * @param user
	 * @return
	 * @throws DeploymentException
	 */
	IUser removeUser(String appId, String domainId, IUser user) throws StorageException;

	/**
	 * Devuelve la URL, del fichero de almacenamiento de aplicaciones desplegadas en caso de ser nulo, o
	 * la entidad en el resto de casos.
	 * 
	 * @param domain
	 * @return
	 * @throws DeploymentException
	 */
	URL read(IDomain domain) throws StorageException;

	/**
	 * Escribe en el fichero de almacenamiento de aplicaciones desplegadas en caso de ser nulo, o
	 * en el de la entidad en el resto de casos.
	 * 
	 * @param domain
	 * @throws DeploymentException
	 */
	void write(IDomain domain) throws StorageException;
}