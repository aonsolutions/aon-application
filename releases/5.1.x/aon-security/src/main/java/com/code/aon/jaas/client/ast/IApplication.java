package com.code.aon.jaas.client.ast;

import java.util.Collection;

import com.code.aon.jaas.deployment.event.ISubDeployerListener;

/**
 * WAR, EAR application to deploy.
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 17-may-2004
 * @since 1.0
 *  
 */
public interface IApplication extends INode, ISubDeployerListener {

    /**
     * Return application description.
     * 
     * @return String
     */
	String getDescription();

	/**
     * Return application context.
     * 
     * @return String
     */
	String getContext();

	/**
     * Return application security-domain.
     * 
     * @return String
	 */
	String getSecurityDomain();

	/**
     * Return the hash algorithm used to encrypt.
     * 
     * @return String
	 */
	String getHashAlgorithm();

	/**
     * Return the hash encoding used to encrypt.
     * 
     * @return String
	 */
	String getHashEncoding();

	/**
     * Return an unmodifiable collection of application defined roles.
     * 
     * @return Collection
     */
	Collection<IRole> roles();

	/**
     * Return application defined roles.
     * 
     * @return Collection
     */
	String getRoles();

	/**
     * Devuelve el rol asociado al nombre pasado por parámetro.
     * 
     * @param name String
     * @return IRelation
     */
	IRole getRole(String name);

    /**
     * Return an unmodifiable collection of application defined domains.
     * 
     * @return Collection
     */
	Collection<IDomain> domains();

    /**
     * Return the application domain.
     * 
     * @param name String
     * @return IDomain
     */
	IDomain getDomain(String name);

	/**
	 * Remove a domain from the application, except default domain: <b>localhost</b>.
	 *   
	 * @param domain
	 * @return
	 */
	IDomain remove(IDomain domain);
}