package com.code.aon.jaas.client.ast;

import java.util.Collection;

/**
 * Interfaz que identifica un Rol de la aplicacion definido en el archivo web.xml.
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 17-may-2004
 * @since 1.0
 *  
 */
public interface IRole extends INode {

    /**
     * Indica un permiso definido para el Rol. Puede ser de acceso o ejecucion.
     * 
     * @param key
     * @return IPermission
     */
    IPermission getPermission(String key);

    /**
     * Coleccion de permisos definidos para el Rol.
     * 
     * @return Collection
     */
    Collection getPermissions();
}