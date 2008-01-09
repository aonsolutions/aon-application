package com.code.aon.jaas.client.ast;

import java.io.Serializable;

/**
 * All components of Aon-security must implement this interface. 
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 17-may-2004
 * @since 1.0
 *  
 */
public interface INode extends Serializable {

    /**
     * Returns node identifier.
     * 
     * @return String
     */
    String getId();

    /**
     * Visits node structure.
     * 
     * @param visitor INodeVisitor
     */
    void accept(INodeVisitor visitor);
}