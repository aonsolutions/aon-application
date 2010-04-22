package com.code.aon.bridge.jmx.mbean;

import com.code.aon.jaas.deployment.DeploymentException;

/**
 * Generador de consolas de administración.
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 16-nov-2004
 * @since 1.0
 *  
 */
public interface IConsoleAdminFactory {

    /**
     * Evalúa si esta factoria es compatible con el contexto en curso.
     * 
     * @return boolean True si es compatible, false si no.
     * @throws DeploymentException
     *             Si se produce algún error en el proceso.
     */
    boolean accept() throws DeploymentException;

    /**
     * Crea una nueva consola de administración utilizando esta factoría.
     * 
     * @return IConsoleAdmin La nueva consola de administración.
     * @throws DeploymentException
     *             Si se produce algún error en el proceso.
     */
    IConsoleAdmin createConsoleAdmin() throws DeploymentException;
}