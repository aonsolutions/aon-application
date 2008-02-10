package com.code.aon.jaas.client.ast;

import java.util.List;

/**
 * Interfaz que indica el permiso de acceso definido para un Rol en el archivo web.xml.
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 17-may-2004
 * @since 1.0
 *  
 */
public interface IAccess extends IPermission {

    /**
     * Lista de patrones a los que tiene acceso el Rol.
     * 
     * @return List
     */
    List<String> getPatterns();

    /**
     * Lista de permisos asociados a los que tiene acceso el Rol.
     */
    List<String> boundPermissions();
}