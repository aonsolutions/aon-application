package com.code.aon.common.jndi;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;
import javax.sql.DataSource;

/**
 * This class looks for JNDI objects. The Singleton pattern is used to implement it.
 * 
 * @author Consulting & Development. Eugenio Castellano - 01-feb-2005
 * @since 1.0
 *  
 */
public class ServiceLocator {

    /**
     * JNDI Initial Context.
     */
    private InitialContext context = null;

    /**
     * JNDI cache objects.
     */
    private Map<String,Object> cache;

    /**
     * Singleton instance.
     */
    private static ServiceLocator ME;

    /**
     * Private constructor, initialize default Initial Context and cached tables.
     * 
     * @throws ServiceLocatorException
     */
    private ServiceLocator() throws ServiceLocatorException {
        try {
            context = new InitialContext();
            cache = Collections.synchronizedMap(new HashMap<String,Object>());
        } catch (NamingException ne) {
            throw new ServiceLocatorException(ne.getMessage(),ne);
        }
    }

    /**
     * Return the singleton instance of <code>ServiceLocator</code> 
     * 
     * @return The singleton instance of <code>ServiceLocator</code>
     * @throws ServiceLocatorException
     */
    public static ServiceLocator getInstance() throws ServiceLocatorException{
        return (ME == null)?ME = new ServiceLocator():ME;
    }

    /**
     * Return an object allocated in the JNDI context.
     * 
     * @param name
     * @param clazz
     * @return An object allocated in the JNDI context.
     * @throws ServiceLocatorException
     */
    public Object getObject(String name, Class clazz)
            throws ServiceLocatorException {
        try {
            Object home = null;
            if (cache.containsKey(name)) {
                home = cache.get(name);
            } else {
                Object ejbRef = context.lookup(name);
                home = PortableRemoteObject.narrow(ejbRef, clazz);
                cache.put(name, home);
            }
            return home;
        } catch (NamingException e) {
            throw new ServiceLocatorException(e.getMessage(),e);
        }
    }

    /**
     * Return a <code>String</code> object allocated in the JNDI context.
     * 
     * @param name
     * @return A <code>String</code> object allocated in the JNDI context.
     * @throws ServiceLocatorException
     */
    public String getProperty(String name) throws ServiceLocatorException {
        Object strRef = getObject(name, String.class);
        String str = (String) PortableRemoteObject.narrow(strRef, String.class);
        return str;
    }

    /**
     * Return a <code>DataSource</code> allocated in the JNDI context.
     * 
     * @param dsName
     * @return A <code>DataSource</code> allocated in the JNDI context.
     * @throws ServiceLocatorException
     */
    public DataSource getDataSource(String dsName)
            throws ServiceLocatorException {
        Object dsRef = getObject(dsName, DataSource.class);
        DataSource ds = (DataSource) PortableRemoteObject.narrow(dsRef,
                DataSource.class);
        return ds;
    }

}
