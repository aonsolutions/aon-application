package com.code.aon.ui.form;

import javax.faces.event.ValueChangeEvent;

import com.code.aon.common.ICriteriaProvider;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;

/**
 * Interface to define search methods for the controllers.
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 07-jun-2005
 */
public interface ISearchable extends ICriteriaProvider, IDataModelDataProvider {

    /**
     * Add a new expression to the criteria to condition the following searches.
     * The id component is managed as an alias to resolve the real property path.
     * 
     * @param event
     * @throws ManagerBeanException
     */
    void addExpression(ValueChangeEvent event) throws ManagerBeanException;

    /**
     * Add a new expression to the criteria to condition the following searches.
     * The id component is managed as the property path, replacing <code>_</code>
     * with <code>.</code>. 
     * 
     * @param event
     * @throws ManagerBeanException
     */
    void addIdEqualExpression(ValueChangeEvent event) throws ManagerBeanException;
    
	/**
	 * Add a new == expression to the criteria to condition the following searches.
	 * The id component is managed as an alias to resolve the real property
	 * path.
	 * 
	 * @param event
	 * @throws ManagerBeanException
	 */
	void addEqualExpression(ValueChangeEvent event) throws ManagerBeanException;

	/**
	 * Add a new >= expression to the criteria to condition the following searches.
	 * The id component is managed as an alias to resolve the real property
	 * path.
	 * 
	 * @param event
	 * @throws ManagerBeanException
	 */
	void addGreaterThanOrEqualExpression(ValueChangeEvent event) throws ManagerBeanException;

	/**
	 * Add a new <= expression to the criteria to condition the following searches.
	 * The id component is managed as an alias to resolve the real property
	 * path.
	 * 
	 * @param event
	 * @throws ManagerBeanException
	 */
	void addLessThanOrEqualExpression(ValueChangeEvent event) throws ManagerBeanException;
	
    /**
     * Return the name of the field that corresponds to the parameter alias.
     * 
     * @param alias
     * @return String
     * @throws ManagerBeanException
     */
    String getFieldName(String alias) throws ManagerBeanException;

    /**
     * Return the name of the field that corresponds to the parameter alias.
     * If it doesn't exist, it will return the same string repacling "_" by ".".
     * 
     * @param alias
     * @return String
     * @throws ManagerBeanException
     */
    String resolveAlias(String alias) throws ManagerBeanException;
    
    /**
     * Clear the criteria.
     * 
     * @throws ManagerBeanException
     */
    void clearCriteria() throws ManagerBeanException;

    /**
     * Return the criteria.
     * 
     * @return Criteria
     * @throws ManagerBeanException
     */
    Criteria getCriteria() throws ManagerBeanException;

    /**
     * Set the criteria.
     * 
     * @param criteria
     * @throws ManagerBeanException
     */
    void setCriteria(Criteria criteria) throws ManagerBeanException;
}
