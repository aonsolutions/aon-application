package com.code.aon.common;

import com.code.aon.common.dao.hibernate.ReplicationMode;

/**
 * Interface that allows operations such as create, read, update and delete, over
 * the entities of the model.
 * 
 * @author Consulting & Development. Aimar Tellitu - 27-jun-2005
 * @since 1.0
 *  
 */
public interface IManagerBean extends IFinderBean {

	/**
	 * Initialize POJO.
	 * 
	 * @param to
	 * @throws ManagerBeanException
	 */
	void initializePOJO(ITransferObject to) throws ManagerBeanException;
	
	/**
	 * Restores null value in framework created subpojos.
	 * 
	 * @param to
	 * @throws ManagerBeanException
	 */
	void restoreNullSubPOJOs(ITransferObject to) throws ManagerBeanException;
	
	/**
	 * Create a new <code>ITransferObject</code> attending to the POJO.
	 * associated to controller.
	 * 
	 * @return ITransferObject.
	 * @throws ManagerBeanException
	 */
	ITransferObject createNewTo() throws ManagerBeanException;

    /**
     * Remove from the data source, the <code>ITransferObject</code>.
     * 
     * @param to Transfer Object to remove.
     * @return <code>true</code> if it has removed.
     * @throws ManagerBeanException if an unexpected error occurs.
     */
    boolean remove(ITransferObject to) throws ManagerBeanException;

    /**
     * Update the <code>ITransferObject</code>.
     * 
     * @param to Transfer Object to update.
     * @return The updated object.
     * @throws ManagerBeanException if an unexpected error occurs.
     */
    ITransferObject update(ITransferObject to) throws ManagerBeanException;

    /**
     * Insert in the data source, the <code>ITransferObject</code>.
     * 
     * @param to Transfer Object to insert.
     * @return Transfer Object con los datos insertados.
     * @throws ManagerBeanException if an unexpected error occurs.
     */
    ITransferObject insert(ITransferObject to) throws ManagerBeanException;
    
    /**
     * Insert or update in the data source, the <code>ITransferObject</code>.
     * 
     * @param to Transfer Object to insert.
     * @return Transfer Object con los datos insertados.
     * @throws ManagerBeanException if an unexpected error occurs.
     */
    ITransferObject insertOrUpdate(ITransferObject to) throws ManagerBeanException;
 
    /**
     * Replicate in the data source, the <code>ITransferObject</code>.
     * 
     * @param to Transfer Object to insert.
	 * @param mode defines the replication mode
	 *  
     * @return Transfer Object con los datos insertados.
     * @throws ManagerBeanException if an unexpected error occurs.
     */
    ITransferObject replicate(ITransferObject to, ReplicationMode mode) throws ManagerBeanException;
    
}