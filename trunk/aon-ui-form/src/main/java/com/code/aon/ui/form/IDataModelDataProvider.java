package com.code.aon.ui.form;

import java.util.List;

import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;

/**
 * Interface to define search methods for the DataModel classes.
 * 
 * @author Consulting & Development. Aimar Tellitu - 15-dic-2006
 */
public interface IDataModelDataProvider {

    /**
     * Return the manager of bean associated to controller.
     * 
     * @return IManagerBean
     * @throws ManagerBeanException
     */
    public IManagerBean getManagerBean() throws ManagerBeanException;
	
    /**
     * Search the list of ITransferObject conditioned by the criteria and the row interval delimited by parameters start and count.
     * 
     * @param start
     * @param count
     * @return List<ITransferObject>
     * @throws ManagerBeanException
     */
    List<ITransferObject> search(int start, int count) throws ManagerBeanException;

    /**
     * Return the criteria.
     * 
     * @return Criteria
     * @throws ManagerBeanException
     */
    Criteria getCriteria() throws ManagerBeanException;
    
	/**
	 * Gets the page limit.
	 * 
	 * @return the page limit
	 */
	int getPageLimit();
    
}
