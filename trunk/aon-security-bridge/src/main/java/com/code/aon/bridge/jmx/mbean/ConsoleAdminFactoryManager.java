package com.code.aon.bridge.jmx.mbean;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.code.aon.jaas.deployment.DeploymentException;

/**
 * Manejador de las diferentes factorias de consolas de administración
 * registradas.
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 16-nov-2004
 * @since 1.0
 *  
 */
public class ConsoleAdminFactoryManager {

    /**
     * Lista donde se almacenan las diferentes factories de la consola de
     * administración.
     */
    private static final List<IConsoleAdminFactory> FACTORIES = new LinkedList<IConsoleAdminFactory>();

    /**
     * Registra una factoría.
     * 
     * @param factory
     *            Factoría que se desea registrar.
     *  
     */
    public static final void register(IConsoleAdminFactory factory) {
        if (!FACTORIES.contains(factory)) {
            FACTORIES.add(factory);
        }
    }

    /**
     * Devuelve la factoría de la consola de administración válida en este
     * contexto.
     * 
     * @return La factoría de la consola de administración válida en este
     *         contexto.
     * @throws DeploymentException
     *             Si se produce algún error durante el proceso.
     */
    public static IConsoleAdminFactory getConsoleAdminFactory()
            throws DeploymentException {
        Iterator<IConsoleAdminFactory> iter = FACTORIES.iterator();
        while (iter.hasNext()) {
            IConsoleAdminFactory factory = iter.next();
            if (factory.accept()) {
                return factory;
            }
        }
        return null;
    }

    /**
     * Crea una nueva consola de administración mediante la factoría apropiada.
     * 
     * @return La consola de administración.
     * @throws DeploymentException
     *             Si se produce algún error durante el proceso.
     */
    public static IConsoleAdmin createConsoleAdmin() throws DeploymentException {
        IConsoleAdminFactory factory = getConsoleAdminFactory();
        return (factory != null) ? factory.createConsoleAdmin() : null;
    }
}