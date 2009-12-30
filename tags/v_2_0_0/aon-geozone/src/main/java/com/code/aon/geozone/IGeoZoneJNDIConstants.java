package com.code.aon.geozone;
/**
 * Constants related with JNDI (Java Naming and Directory Interface).
 * 
 * @author Consulting & Development. Eugenio Castellano - 01-feb-2005
 * @since 1.0
 *  
 */
public interface IGeoZoneJNDIConstants {

    /**
     * Constant that stores the
     * <code>javax.sql.DataSource</code> used by DAO (Data Access
     * Object).
     */
    String AON_GEOZONE_DATASOURCE = "java:comp/env/jdbc/AonGeoZone"; //$NON-NLS-1$

    /**
     * Constant that stores the name of the factory class
     * related to make objects with the  
     * <code>com.code.aon.common.IManagerBean</code> implementation 
     * needed to interact with the entities.
     *  
     */
    String AON_GEOZONE_MANAGER_BEAN_FACTORY_CLASS = "java:comp/env/AonGeoZoneManagerBeanFactoryClass"; //$NON-NLS-1$
    
    /**
     * Constant that stores the name of the factory
     * that makes object with the <code>com.code.aon.geozone.core.dao.IGeoZoneDAO</code>
     * implementation.
     *  
     */
    String AON_GEOZONE_DAO_FACTORY_CLASS = "java:comp/env/AonGeoZoneDAOFactoryClass"; //$NON-NLS-1$
}