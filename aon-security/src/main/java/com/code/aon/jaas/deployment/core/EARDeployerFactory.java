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
public class EARDeployerFactory implements IDeployerFactory {

    /*
     * (non-Javadoc)
     * 
     * @see com.code.aon.jaas.deployment.IDeployerFactory#accept(com.code.aon.jaas.deployment.DeploymentInfo)
     */
    public boolean accept(DeploymentInfo di) {
        String urlStr = di.url.getFile();
        return urlStr.endsWith("ear") || urlStr.endsWith("ear/"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.code.aon.jaas.deployment.IDeployerFactory#createDeployer()
     */
    public ISubDeployer createDeployer() {
        return new EARDeployer();
    }

    /**
     * // TODO [iayerbe] Documéntame!
     */
    static final IDeployerFactory FACTORY = new EARDeployerFactory();

    static {
        DeployerFactoryManager.register(FACTORY);
    }
}