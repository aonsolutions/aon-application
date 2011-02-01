package com.code.aon.jaas.deployment.ast;

import java.util.List;

/**
 * //TODO [iayerbe] Documéntame!
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 14-may-2004
 * @since 1.0
 *  
 */
public interface IApplicationDescriptor extends IDescriptor {

    /**
     * //TODO [iayerbe] Documéntame!
     * 
     * @return List
     */
    List<String> jars();

    /**
     * //TODO [iayerbe] Documéntame!
     * 
     * @return List
     */
    List<String> wars();
}