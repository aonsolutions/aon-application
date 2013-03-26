/*
 * Created on 11-oct-2006
 *
 */
package com.code.aon.bridge.jndi;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;

import com.code.aon.bridge.jmx.mbean.ConsoleAdminFactoryManager;
import com.code.aon.bridge.jmx.mbean.IConsoleAdmin;

/**
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 11-oct-2006
 * @since 1.0
 *
 */

public class SecurityLocator {

    /**
     * Contexto Inicial de JNDI.
     */
    private InitialContext context = null;

    /**
     * Cache de objetos del árbol JNDI.
     */
    private Map<String,Object> cache;

    /**
     * Instancia "singleton".
     */
    private static SecurityLocator ME;

    /**
     * Constructor privado, el cual inicializa todas las tablas y el Contexto
     * inicial por defecto.
     * 
     * @throws ServiceLocatorException
     *             Si se produjo algún en la inicialización del contexto JNDI.
     */
    private SecurityLocator() throws SecurityLocatorException {
        try {
            context = new InitialContext();
            cache = Collections.synchronizedMap(new HashMap<String,Object>());
        } catch (NamingException ne) {
            throw new SecurityLocatorException(ne.getMessage(),ne);
        }
    }

    /**
     * Método para acceder a la instancia "singleton" de esta clase.
     * 
     * @return La instancia de esta clase.
     * @throws ServiceLocatorException
     *             Si se produjo algún en la creación de la instancia.
     */
    public static SecurityLocator getInstance() throws SecurityLocatorException{
        return (ME == null)?ME = new SecurityLocator():ME;
    }

    /**
     * Recupera la consola de administracion de usuarios segun el recurso especificado
     * en el servidor de aplicaciones o contenedor de servlets.
     * 
     * @param name
     * @return
     * @throws SecurityLocatorException
     */
    public IConsoleAdmin getConsole(String name) throws SecurityLocatorException {
        try {
            Object home = null;
            if (cache.containsKey(name)) {
                home = cache.get(name);
            } else {
                Context envCtx = (Context) context.lookup( IJNDIConstants.CONTEXT );
                Object ejbRef = envCtx.lookup(name);
                home = PortableRemoteObject.narrow(ejbRef, ejbRef.getClass());
                cache.put(name, home);
            }
            return ConsoleAdminFactoryManager.createConsoleAdmin();
        } catch (Exception e) {
            throw new SecurityLocatorException(e.getMessage(),e);
        }
    }

}
