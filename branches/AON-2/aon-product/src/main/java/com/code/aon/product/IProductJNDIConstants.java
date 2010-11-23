package com.code.aon.product;

/**
 * Constants related with JNDI (Java Naming and Directory Interface).
 * 
 * @author Consulting & Development. Iñigo GAyarre - 14-mar-2005
 * @since 1.0
 * @version 1.0
 *  
 */
public interface IProductJNDIConstants {

    /**
     * This will store the <code>javax.sql.DataSource</code> name 
     * used by DAO (Data Access Object).
     */
    //String AON_PRODUCT_DATASOURCE = "java:comp/env/jdbc/AonProduct"; //$NON-NLS-1$

    /**
     * Constant that stores the value of an implementation of 
     * <code>com.code.aon.product.IProductManagerBean</code> needed to
     * feed back with product entities.
     *  
     */
    String AON_PRODUCT_MANAGER_BEAN_CLASS = "java:comp/env/AonProductManagerBeanClass"; //$NON-NLS-1$

    /**
     * Constant that stores the value of an implementation of 
     * <code>com.code.aon.product.core.dao.ProductDAOFactory</code>
     *  needed to get corresponding DAO.
     *  
     */
    String AON_PRODUCT_DAO_FACTORY_CLASS = "java:comp/env/AonProductDAOFactoryClass"; //$NON-NLS-1$
}