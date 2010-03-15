package com.code.aon.jaas.deployment.ast;

import com.code.aon.jaas.vendor.deployment.ast.IVendorDescriptor;

/**
 * //TODO [iayerbe] Documéntame!
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 06-feb-2004
 * @since 1.0
 *  
 */
public interface INodeVisitor {

    /**
     * //TODO [iayerbe] Documéntame!
     * 
     * @param descriptor
     */
    void visitApplicationDescriptor(IApplicationDescriptor descriptor);

    /**
     * //TODO [iayerbe] Documéntame!
     * 
     * @param descriptor
     */
    void visitAssemblyDescriptor(ISecurityDescriptor descriptor);

    /**
     * //TODO [iayerbe] Documéntame!
     * 
     * @param descriptor
     */
    void visitWebDescriptor(ISecurityDescriptor descriptor);

    /**
     * //TODO [iayerbe] Documéntame!
     * 
     * @param permission
     */
    void visitMethodPermission(IPermission permission);

    /**
     * //TODO [iayerbe] Documéntame!
     * 
     * @param method
     */
    void visitMethod(IMethod method);

    /**
     * //TODO [iayerbe] Documéntame!
     * 
     * @param permission
     */
    void visitSecurityConstraint(IPermission permission);

    /**
     * //TODO [iayerbe] Documéntame!
     * 
     * @param resource
     */
    void visitResource(IResource resource);

    /**
     * //TODO [iayerbe] Documéntame!
     * 
     * @param role
     */
    void visitSecurityRole(ISecurityRole role);

    /**
     * //TODO [iayerbe] Documéntame!
     * 
     * @param vendor
     */
    void visitVendorDescriptor(IVendorDescriptor vendor);
}