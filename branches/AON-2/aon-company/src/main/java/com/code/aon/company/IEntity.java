package com.code.aon.company;

import java.io.Serializable;

/**
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 30-jan-2007
 * @since 1.0
 *  
 */
public interface IEntity extends Serializable {

    /**
     * Returns Entity identifier.
     *  
     * @return Integer
     */
    Integer getId();

    /**
     * Accept visitor node and execute its visitant method.
     * 
     * @param visitor INodeVisitor
     */
    void accept(IEntityVisitor visitor);
}