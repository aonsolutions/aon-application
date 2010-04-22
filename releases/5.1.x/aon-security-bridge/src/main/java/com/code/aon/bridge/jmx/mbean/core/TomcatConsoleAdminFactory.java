package com.code.aon.bridge.jmx.mbean.core;

import com.code.aon.bridge.jmx.mbean.ConsoleAdminFactoryManager;
import com.code.aon.bridge.jmx.mbean.IConsoleAdmin;
import com.code.aon.bridge.jmx.mbean.IConsoleAdminFactory;
import com.code.aon.jaas.deployment.DeploymentException;

/**
 * Factoría de creación de la consola de mantenimiento del módulo de seguridad para Tomcat.
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 16-nov-2004
 * @since 1.0
 *  
 */
public class TomcatConsoleAdminFactory implements IConsoleAdminFactory {

    /** Consola de mantenimiento del módulo de seguridad. */
    IConsoleAdmin console;

    /*(non-Javadoc)
     * @see com.aon.jmx.adaptor.mbean.IConsoleAdminFactory#accept(java.lang.String)
     */
    public boolean accept() throws DeploymentException {
        if (console == null) {
            console = new TomcatConsoleAdmin();
        }
        return console.getServerInfo().indexOf("Apache Tomcat") > -1; //$NON-NLS-1$
    }

    /*(non-Javadoc)
     * @see com.aon.jmx.adaptor.mbean.IConsoleAdminFactory#createConsoleAdmin()
     */
    public IConsoleAdmin createConsoleAdmin() throws DeploymentException {
        return console;
    }

    static final IConsoleAdminFactory FACTORY = new TomcatConsoleAdminFactory();

    static {
        ConsoleAdminFactoryManager.register(FACTORY);
    }

}