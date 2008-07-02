package com.code.aon.department.ast;

import java.io.Serializable;

/**
 * Interfaz base a implementar por cada uno de los nodos que forman parte de la política de 
 * seguridad Aon. 
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 17-may-2004
 * @since 1.0
 *  
 */
public interface INode extends Serializable {

    /**
     * Devuelve el identificador del nodo.
     * 
     * @return Integer
     */
    Integer getId();

    /**
     * Visita la estructura del nodo.
     * 
     * @param visitor INodeVisitor
     */
    void accept(INodeVisitor visitor);
}