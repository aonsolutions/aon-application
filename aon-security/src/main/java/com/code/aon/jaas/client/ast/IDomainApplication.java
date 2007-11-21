package com.code.aon.jaas.client.ast;

import java.util.Collection;

/**
 * Interfaz que define una aplicación dentro del dominio en el módulo de seguridad.
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 21-ago-2006
 * @since 1.0
 *
 */
public interface IDomainApplication extends INode {

    /**
     * Devuelve las propiedades de la fuente de datos.
     * 
     * @return Properties
     */
    IDataSourceMetaData getDataSourceMetaData();

	/**
     * Devuelve una colección con los perfiles definidos para la entidad.
     * 
     * @return Collection
     */
	Collection<IRelation> profiles();

    /**
     * Devuelve el perfil asociado al nombre pasado por parámetro.
     * 
     * @param name String
     * @return IRelation
     */
	IRelation getProfile(String name);

    /**
     * Modifica el Perfil y lo añade en caso de no existir. Devuelve el Perfil modificado y/o añadido.
     * 
     * @param relation
     * @return
     */
	IRelation updateProfile(IRelation relation);

    /**
     * Elimina el Perfil.
     * 
     * @param relation
     */
	void removeProfile(IRelation relation);

    /**
     * Indica si el perfil esta definido para alguno de los usuarios de la entidad.
     * 
     * @param profile String
     * @return boolean
     */
    boolean isProfileInUsers(String profile);

    /**
     * Devuelve los usuarios de la entidad que tienen algun perfíl asociado.
     * 
     * @return Collection
     */
    Collection<IRelation> users();

    /**
     * Devuelve el usuario de la entidad que tienen algun perfíl asociado.
     * 
     * @param name String
     * @return IRelation
     */
    IRelation getUser(String name);

    /**
     * Modifica el Usuario y lo añade en caso de no existir. Devuelve el Usuario modificado y/o añadido.
     * 
     * @param relation
     * @return
     */
    IRelation updateUser(IRelation relation);

    /**
     * Elimina el Usuario junto con sus relaciones.
     * 
     * @param relation
     * @return
     */
    void removeUser(IRelation relation);

}


