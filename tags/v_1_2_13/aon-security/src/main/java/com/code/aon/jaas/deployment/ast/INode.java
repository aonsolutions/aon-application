package com.code.aon.jaas.deployment.ast;

/**
 * Application deployment node.
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 15-ene-2004
 * @since 1.0
 *  
 */
public interface INode {

    /**
     * Visitor pattern implementation.
     * 
     * @param visitor
     */
    void accept(INodeVisitor visitor);
}