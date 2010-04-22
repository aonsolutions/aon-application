package com.code.aon.jaas.deployment.ast.core;

import com.code.aon.jaas.deployment.ast.INodeVisitor;
import com.code.aon.jaas.deployment.ast.ISecurityRole;

/**
 * // TODO [iayerbe] Documéntame!
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 12-may-2005
 * @since 1.0
 *  
 */
public class SecurityRole implements ISecurityRole {

    /**
     * // TODO [iayerbe] Documéntame!
     */
    private String name;

    /**
     * // TODO [iayerbe] Documéntame!
     */
    private String description;

    /*
     * (non-Javadoc)
     * 
     * @see com.code.aon.jaas.deployment.ast.ISecurityRole#getName()
     */
    public String getName() {
        return name;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.code.aon.jaas.deployment.ast.ISecurityRole#getDescription()
     */
    public String getDescription() {
        return description;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.code.aon.jaas.deployment.ast.INode#accept(com.code.aon.jaas.deployment.ast.INodeVisitor)
     */
    public void accept(INodeVisitor visitor) {
        visitor.visitSecurityRole(this);
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