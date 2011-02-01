package com.code.aon.jaas.deployment.ast;

import java.util.List;

/**
 * //TODO [iayerbe] Documéntame!
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 12-may-2004
 * @since 1.0
 *  
 */
public interface IResource extends INode {

    /**
     * //TODO [iayerbe] Documéntame!
     * 
     * @return String
     */
    String getName();

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
    List<String> patterns();

    /**
     * //TODO [iayerbe] Documéntame!
     * 
     * @return List
     */
    List<String> methods();
}