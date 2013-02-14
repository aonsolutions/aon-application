package com.code.aon.jaas.deployment.ast.core;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.code.aon.jaas.deployment.ast.INodeVisitor;
import com.code.aon.jaas.deployment.ast.IResource;

/**
 * // TODO [iayerbe] Documéntame!
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 19-may-2004
 * @since 1.0
 *  
 */
public class ResourceCollection implements IResource {

    /**
     * // TODO [iayerbe] Documéntame!
     */
    String name;

    /**
     * // TODO [iayerbe] Documéntame!
     */
    String description;

    /**
     * // TODO [iayerbe] Documéntame!
     */
    List<String> patterns = new LinkedList<String>();

    /**
     * // TODO [iayerbe] Documéntame!
     */
    List<String> methods = new LinkedList<String>();

    /**
     * // TODO [iayerbe] Documéntame!
     * 
     * @param pattern
     */
    public void addPattern(String pattern) {
        if (!patterns.contains(pattern)) {
            patterns.add(pattern);
        }
    }

    /**
     * // TODO [iayerbe] Documéntame!
     * 
     * @param method
     */
    public void addMethod(String method) {
        if (!methods.contains(method)) {
            methods.add(method);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.code.aon.jaas.deployment.ast.IResource#getName()
     */
    public String getName() {
        return name;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.code.aon.jaas.deployment.ast.IResource#getDescription()
     */
    public String getDescription() {
        return description;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.code.aon.jaas.deployment.ast.IResource#patterns()
     */
    public List<String> patterns() {
        return Collections.unmodifiableList(patterns);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.code.aon.jaas.deployment.ast.IResource#methods()
     */
    public List<String> methods() {
        return Collections.unmodifiableList(methods);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.code.aon.jaas.deployment.ast.INode#accept(com.code.aon.jaas.deployment.ast.INodeVisitor)
     */
    public void accept(INodeVisitor visitor) {
        visitor.visitResource(this);
    }

    /**
     * // TODO [iayerbe] Documéntame!
     * 
     * @param string
     */
    public void setDescription(String string) {
        description = string;
    }

    /**
     * // TODO [iayerbe] Documéntame!
     * 
     * @param string
     */
    public void setName(String string) {
        name = string;
    }

}