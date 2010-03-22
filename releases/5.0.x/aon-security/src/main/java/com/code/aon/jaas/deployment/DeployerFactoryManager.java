package com.code.aon.jaas.deployment;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * This class manages a deployer factory list.
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 13-may-2004
 * @since 1.0
 *  
 */
public class DeployerFactoryManager {

    /**
     * Manages a deployer factory list.
     */
    private static final List<IDeployerFactory> FACTORIES = new LinkedList<IDeployerFactory>();

    /**
     * Register a <code>IDeployerFactory</code> 
     * 
     * @param factory
     */
    public static final void register(IDeployerFactory factory) {
        if (!FACTORIES.contains(factory)) {
            FACTORIES.add(factory);
        }
    }

    /**
     * Returns the factory attribute of the DeployerFactoryManager class.
     * 
     * @param di
     *            The complete type to create the deployer.
     * @return The factory value
     */
    public static IDeployerFactory getDeployerFactory(DeploymentInfo di) {
        Iterator<IDeployerFactory> iter = FACTORIES.iterator();
        while (iter.hasNext()) {
            IDeployerFactory factory = iter.next();
            if (factory.accept(di)) {
                return factory;
            }
        }
        return null;
    }

    /**
     * Creates a new Deployer for the given type.
     * 
     * @param di
     *            the DeploymentInfo.
     * @return The deployer value
     */
    public static ISubDeployer createDeployer(DeploymentInfo di) {
        IDeployerFactory factory = getDeployerFactory(di);
        return (factory != null) ? factory.createDeployer() : null;
    }
}