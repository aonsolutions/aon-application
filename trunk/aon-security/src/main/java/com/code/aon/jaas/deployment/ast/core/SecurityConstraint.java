package com.code.aon.jaas.deployment.ast.core;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.code.aon.jaas.deployment.ast.INodeVisitor;
import com.code.aon.jaas.deployment.ast.IPermission;
import com.code.aon.jaas.deployment.ast.IResource;

/**
 * // TODO [iayerbe] Documéntame!
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 19-may-2005
 * @since 1.0
 *  
 */
public class SecurityConstraint implements IPermission {

    /**
     * // TODO [iayerbe] Documéntame!
     */
    private List<String> roles = new LinkedList<String>();

    /**
     * // TODO [iayerbe] Documéntame!
     */
    private List<IResource> resources = new LinkedList<IResource>();

    /**
     * // TODO [iayerbe] Documéntame!
     * 
     * @param role
     */
    public void addRole(String role) {
        if (!roles.contains(role)) {
            roles.add(role);
        }
    }

    /**
     * // TODO [iayerbe] Documéntame!
     * 
     * @param resource
     */
    public void addMethod(IResource resource) {
        if (!resources.contains(resource)) {
            resources.add(resource);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.code.aon.jaas.deployment.ast.IPermission#getDescription()
     */
    public String getDescription() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.code.aon.jaas.deployment.ast.IPermission#roles()
     */
    public List<String> roles() {
        return Collections.unmodifiableList(roles);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.code.aon.jaas.deployment.ast.IPermission#resources()
     */
    public List<IResource> resources() {
        return Collections.unmodifiableList(resources);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.code.aon.jaas.deployment.ast.INode#accept(com.code.aon.jaas.deployment.ast.INodeVisitor)
     */
    public void accept(INodeVisitor visitor) {
        visitor.visitSecurityConstraint(this);
    }

}