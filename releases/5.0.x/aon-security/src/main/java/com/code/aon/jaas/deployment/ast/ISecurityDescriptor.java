package com.code.aon.jaas.deployment.ast;

import java.util.List;

/**
 * //TODO [iayerbe] Documéntame!
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 12-may-2004
 * @since 1.0
 *  
 */
public interface ISecurityDescriptor extends IDescriptor {

    /**
     * //TODO [iayerbe] Documéntame!
     * 
     * @return List
     */
    List<ISecurityRole> roles();

    /**
     * //TODO [iayerbe] Documéntame!
     * 
     * @return List
     */
    List<IPermission> permissions();
}