package com.code.aon.jaas.deployment.ast;

import java.util.List;

/**
 * //TODO [iayerbe] Documéntame!
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 12-may-2004
 * @since 1.0
 *  
 */
public interface IPermission extends INode {

    /**
     * //TODO [iayerbe] Documéntame!
     */
    static final String ANONIMOUS = "anonimous";

    /**
     * //TODO [iayerbe] Documéntame!
     * 
     * @return String
     */
    String getDescription();

    /**
     * //TODO [iayerbe] Documéntame!
     * 
     * @return List
     */
    List<String> roles();

    /**
     * //TODO [iayerbe] Documéntame!
     * 
     * @return List
     */
    List<IResource> resources();
}