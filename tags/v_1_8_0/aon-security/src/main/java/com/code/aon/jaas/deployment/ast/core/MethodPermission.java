package com.code.aon.jaas.deployment.ast.core;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.code.aon.jaas.deployment.ast.IMethod;
import com.code.aon.jaas.deployment.ast.INodeVisitor;
import com.code.aon.jaas.deployment.ast.IPermission;

/**
 * // TODO [iayerbe] Documéntame!
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 12-may-2004
 * @since 1.0
 *  
 */
public class MethodPermission implements IPermission {

    /**
     * // TODO [iayerbe] Documéntame!
     */
    private String description;

    /**
     * // TODO [iayerbe] Documéntame!
     */
    private List<String> roles = new LinkedList<String>();

    /**
     * // TODO [iayerbe] Documéntame!
     */
    private List<IMethod> methods = new LinkedList<IMethod>();

    /**
     * // TODO [iayerbe] Documéntame!
     */
    private boolean uncheked = false;

    /**
     * // TODO [iayerbe] Documéntame!
     * 
     * @param role
     */
    public void addRole(String role) {
        if (!roles.contains(role) || !uncheked) {
            roles.add(role);
        }
        if (role.equals("")) {
            roles.clear();
            roles.add(IPermission.ANONIMOUS);
            uncheked = true;
        }
    }

    /**
     * // TODO [iayerbe] Documéntame!
     * 
     * @param method
     */
    public void addMethod(IMethod method) {
        if (!methods.contains(method)) {
            methods.add(method);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.code.aon.jaas.deployment.ast.IPermission#getDescription()
     */
    public String getDescription() {
        return description;
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
    public List resources() {
        return Collections.unmodifiableList(methods);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.code.aon.jaas.deployment.ast.INode#accept(com.code.aon.jaas.deployment.ast.INodeVisitor)
     */
    public void accept(INodeVisitor visitor) {
        visitor.visitMethodPermission(this);
    }

    /**
     * // TODO [iayerbe] Documéntame!
     * 
     * @param string
     */
    public void setDescription(String string) {
        description = string;
    }

}