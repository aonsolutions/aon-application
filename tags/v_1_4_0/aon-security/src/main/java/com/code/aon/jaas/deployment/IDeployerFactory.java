package com.code.aon.jaas.deployment;

/**
 * //TODO [iayerbe] Dcouméntame!
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 13-may-2004
 * @since 1.0
 *  
 */
public interface IDeployerFactory {

    /**
     * //TODO [iayerbe] Dcouméntame!
     * 
     * @param di
     * @return boolean
     */
    boolean accept(DeploymentInfo di);

    /**
     * //TODO [iayerbe] Dcouméntame!
     * 
     * @return ISubDeployer
     */
    ISubDeployer createDeployer();
}