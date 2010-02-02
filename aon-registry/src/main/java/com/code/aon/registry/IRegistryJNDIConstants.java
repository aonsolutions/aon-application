package com.code.aon.registry;

/**
 * JNDI Constants (Java Naming and Directory Interface).
 * 
 * @author Consulting & Development. Eugenio Castellano - 01-feb-2005
 * @since 1.0
 *  
 */
public interface IRegistryJNDIConstants {

	/**
     * Constant that constains the <code>javax.sql.DataSource</code> that will be used by DAOs 
     * (Data Access Object).
     */
    String AON_REGISTRY_DATASOURCE = "java:comp/env/jdbc/AonRegistry"; 

    /**
     * Constant that contains an implementation of <code>com.code.aon.common.IManagerBean</code> 
     * necessary to use entities of <code>com.code.aon.registry</code>
     *  
     */
    String AON_REGISTRY_MANAGER_BEAN_CLASS = "java:comp/env/AonRegistryManagerBeanClass"; 

    String AON_REGISTRY_DAO_FACTORY_CLASS = "java:comp/env/AonRegistryDAOFactoryClass";

}