package com.code.aon.jaas.client.ast;

import java.util.List;

/**
 * Interfaz que el permiso de acceso y/o ejecucion definido para un Rol en el archivo web.xml.
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 17-may-2004
 * @since 1.0
 *  
 */
public interface IPermission extends INode {

    /**
     * Descripcion del permiso.
     * 
     * @return String
     */
    String getDescription();

    /**
     * Lista de metodos del permiso.
     * 
     * @return List
     */
    List<String> getMethods();

}