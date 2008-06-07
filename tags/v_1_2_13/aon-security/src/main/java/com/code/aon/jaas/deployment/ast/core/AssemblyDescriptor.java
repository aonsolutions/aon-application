package com.code.aon.jaas.deployment.ast.core;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.code.aon.jaas.deployment.ast.INodeVisitor;
import com.code.aon.jaas.deployment.ast.IPermission;
import com.code.aon.jaas.deployment.ast.ISecurityDescriptor;
import com.code.aon.jaas.deployment.ast.ISecurityRole;

/**
 * // TODO [iayerbe] Documéntame!
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 12-may-2005
 * @since 1.0
 *  
 */
public class AssemblyDescriptor implements ISecurityDescriptor {

    /**
     * // TODO [iayerbe] Documéntame!
     */
    List<ISecurityRole> roles;

    /**
     * // TODO [iayerbe] Documéntame!
     */
    List<IPermission> permissions;

    /**
     * // TODO [iayerbe] Documéntame!
     * 
     * @param role
     */
    public void add(ISecurityRole role) {
        if (roles == null) {
            roles = new LinkedList<ISecurityRole>();
        }
        roles.add(role);
    }

    /**
     * // TODO [iayerbe] Documéntame!
     * 
     * @param permission
     */
    public void add(IPermission permission) {
        if (permissions == null) {
            permissions = new LinkedList<IPermission>();
        }
        permissions.add(permission);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.code.aon.jaas.deployment.ast.ISecurityDescriptor#roles()
     */
    public List<ISecurityRole> roles() {
        return Collections.unmodifiableList(roles);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.code.aon.jaas.deployment.ast.ISecurityDescriptor#permissions()
     */
    public List<IPermission> permissions() {
        return Collections.unmodifiableList(permissions);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.code.aon.jaas.deployment.ast.INode#accept(com.code.aon.jaas.deployment.ast.INodeVisitor)
     */
    public void accept(INodeVisitor visitor) {
        visitor.visitAssemblyDescriptor(this);
    }

}