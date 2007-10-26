package com.code.aon.common.dao;

import java.io.Serializable;
import java.util.List;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ql.ProjectionList;

/**
 * This interface allows CRUD operations about <code>ITransferObject</code>
 * entities.
 * 
 * @author Consulting & Development. Aimar Tellitu - 20-jun-2005
 * @since 1.0
 */
public interface IDAO {

	/**
	 * Return an <code>com.code.aon.common.ITransferObject</code> for the
	 * given identifier.
	 * 
	 * @param pk the pk
	 * 
	 * @return An <code>com.code.aon.common.ITransferObject</code>.
	 * 
	 * @throws DAOException the DAO exception
	 */
	ITransferObject get(Serializable pk) throws DAOException;

	/**
	 * Returns the identifier for the given
	 * <code>com.code.aon.common.ITransferObject</code>.
	 * 
	 * @param to the to
	 * 
	 * @return The identifier.
	 * 
	 * @throws DAOException the DAO exception
	 */
	Serializable getId(ITransferObject to) throws DAOException;

	/**
	 * Assign the Primary key for the Transfer Object.
	 * 
	 * @param to the to
	 * @param id the id
	 * 
	 * @throws DAOException the DAO exception
	 */
	void setId(ITransferObject to, Serializable id) throws DAOException;

	/**
	 * Assign the Value for the Transfer Object. //JORGE ****
	 * 
	 * @param to the to
	 * @param propertyName the name of property
	 * @param value the value
	 * 
	 * @throws DAOException the DAO exception
	 */
	void setProperty(ITransferObject to, String propertyName, Object value) throws DAOException;

	/**
	 * Return a <code>Collection</code> of
	 * <code>com.code.aon.common.ITransferObject</code> that carries out the
	 * criteria.
	 * 
	 * @param criteria the criteria
	 * 
	 * @return The Collection.
	 * 
	 * @throws DAOException the DAO exception
	 */
	List<ITransferObject> getList(Criteria criteria) throws DAOException;

	/**
	 * Return a <code>Collection</code> of
	 * <code>com.code.aon.common.ITransferObject</code> that carries out the
	 * criteria
	 * 
	 * @param criteria the criteria
	 * @param count the count
	 * @param offset the offset
	 * 
	 * @return The Collection.
	 * 
	 * @throws DAOException the DAO exception
	 */
	List<ITransferObject> getList(Criteria criteria, int offset, int count) throws DAOException;

	/**
	 * Return a {@link Object} that carries out the projection list and the criteria.
	 * 
	 * @param projection the projection.
	 * @param criteria the criteria
	 * 
	 * @return The object.
	 * 
	 * @throws DAOException the DAO exception
	 */
	Object getUniqueResult(Projection projection, Criteria criteria) throws DAOException;

	/**
	 * Return a {@link List} that carries out the projection list and the criteria.
	 * 
	 * @param projectionList the Projection list.
	 * @param criteria the criteria
	 * 
	 * @return The list.
	 * 
	 * @throws DAOException the DAO exception
	 */
	List getList(ProjectionList projectionList, Criteria criteria) throws DAOException;
	
	/**
	 * Remove the Transfer Object.
	 * 
	 * @param to the to
	 * 
	 * @return <code>true</code> if something was removed.
	 * 
	 * @throws DAOException the DAO exception
	 */
	boolean remove(ITransferObject to) throws DAOException;

	/**
	 * Return the field name for the alias passed by parameter.
	 * 
	 * @param alias the alias
	 * 
	 * @return The field name.
	 * 
	 * @throws DAOException the DAO exception
	 */
	String getFieldName(String alias) throws DAOException;

	/**
	 * Return the number of rows.
	 * 
	 * @param criteria the criteria
	 * 
	 * @return The number of rows.
	 * 
	 * @throws DAOException the DAO exception
	 */
	int getCount(Criteria criteria) throws DAOException;

	/**
	 * Update the Transfer Object.
	 * 
	 * @param to the to
	 * 
	 * @return The update object.
	 * 
	 * @throws DAOException the DAO exception
	 */
	ITransferObject update(ITransferObject to) throws DAOException;

	/**
	 * Insert the Transfer Object.
	 * 
	 * @param to the to
	 * 
	 * @return The inserted object.
	 * 
	 * @throws DAOException the DAO exception
	 */
	ITransferObject insert(ITransferObject to) throws DAOException;

	/**
	 * Insert or update the Transfer Object depending of the value of the identifier.
	 * 
	 * @param to the to
	 * 
	 * @return the I transfer object
	 * 
	 * @throws DAOException the DAO exception
	 */
	public ITransferObject insertOrUpdate(ITransferObject to) throws DAOException;
	
	/**
	 * Return the POJO(Plain Old Java Object) Class.
	 * 
	 * @return The class of the POJO.
	 */
	Class getPOJOClass();

}