package com.code.aon.jaas.deployment.ast;

/**
 * //TODO [iayerbe] Documéntame!
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 13-may-2004
 * @since 1.0
 *  
 */
public interface ISecurityHandler {

    /**
     * //TODO [iayerbe] Documéntame!
     * 
     * @return Object
     */
    Object getWebSecurity();

    /**
     * //TODO [iayerbe] Documéntame!
     * 
     * @return Object
     */
    Object getEjbSecurity();
}