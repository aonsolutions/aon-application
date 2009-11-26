package com.code.aon.jaas.client.ast;

import java.util.List;

/**
 * Conjunto de roles de un Perfil y/o perfiles de un Usuario. 
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 17-jun-2004
 * @since 1.0
 *  
 */
public interface IRelation extends INode {

    /**
     * Devuelve la lista de relaciones.
     * 
     * @return List
     */
    List<String> relations();

    /**
     * Devuelve un <code>String</code> con la lista de relaciones.
     * 
     * @return List
     */
    String getRelations();
}