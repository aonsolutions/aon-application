package com.code.aon.bridge.jmx.mbean;

/**
     * //TODO [iayerbe] Documéntame!
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 22-jun-2004
 * @since 1.0
 *  
 */
public interface IMBeanInfo {

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
    String getURL();

    /**
     * //TODO [iayerbe] Documéntame!
     * 
     * @return String
     */
    String getDeployer();

    /**
     * //TODO [iayerbe] Documéntame!
     * 
     * @return String
     */
    String getStatus();

    /**
     * //TODO [iayerbe] Documéntame!
     * 
     * @return String
     */
    String getState();

    /**
     * //TODO [iayerbe] Documéntame!
     * 
     * @return String
     */
    String getWatch();

    /**
     * //TODO [iayerbe] Documéntame!
     * 
     * @return long
     */
    long getLastDeployed();

    /**
     * //TODO [iayerbe] Documéntame!
     * 
     * @return long
     */
    long getLastModified();
}