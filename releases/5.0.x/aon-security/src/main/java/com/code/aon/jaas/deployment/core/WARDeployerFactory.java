package com.code.aon.jaas.deployment.core;

import com.code.aon.jaas.deployment.DeployerFactoryManager;
import com.code.aon.jaas.deployment.DeploymentInfo;
import com.code.aon.jaas.deployment.IDeployerFactory;
import com.code.aon.jaas.deployment.ISubDeployer;

/**
 * // TODO [iayerbe] Documéntame!
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 14-may-2004
 * @since 1.0
 *  
 */
public class WARDeployerFactory implements IDeployerFactory {

    /*
     * (non-Javadoc)
     * 
     * @see com.code.aon.jaas.deployment.IDeployerFactory#accept(com.code.aon.jaas.deployment.DeploymentInfo)
     */
    public boolean accept(DeploymentInfo di) {
        String urlStr = di.url.getFile();
        return urlStr.endsWith("war") || urlStr.endsWith("/");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.code.aon.jaas.deployment.IDeployerFactory#createDeployer()
     */
    public ISubDeployer createDeployer() {
        return new WARDeployer();
    }

    /**
     * // TODO [iayerbe] Documéntame!
     */
    static final IDeployerFactory FACTORY = new WARDeployerFactory();

    static {
        DeployerFactoryManager.register(FACTORY);
    }
}