package com.code.aon.jaas.client.ast;

import java.util.Collection;

import com.code.aon.jaas.deployment.event.ISubDeployerListener;

/**
 * Interfaz que define una aplicación, WAR, EAR a desplegar en el módulo de seguridad.
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 17-may-2004
 * @since 1.0
 *  
 */
public interface IApplication extends INode, ISubDeployerListener {

    /**
     * Devuelve el contexto de la aplicación.
     * 
     * @return String
     */
	String getContext();

	/**
     * Devuelve el dominio de seguridad de la aplicación.
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
     * Devuelve una colección con los roles definidos para la entidad.
     * 
     * @return Collection
     */
	Collection<IRole> roles();

	/**
     * Devuelve los roles definidos.
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
     * Devuelve una colección con las dominios definidos para la aplicación.
     * 
     * @return Collection
     */
	Collection<IDomain> domains();

    /**
     * Devuelve la dominio asociado al nombre pasado por parámetro.
     * 
     * @param name String
     * @return IDomain
     */
	IDomain getDomain(String name);

	/**
	 * Elimina el dominio, salvo el caso de tratarse del dominio por defecto.
	 *   
	 * @param domain
	 * @return
	 */
	IDomain remove(IDomain domain);
}