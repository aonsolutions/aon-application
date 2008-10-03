/**
 * 
 */
package com.code.aon.company.resources;

import java.util.Date;
import java.util.List;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.WorkPlace;
import com.code.aon.company.dao.ICompanyAlias;
import com.code.aon.ql.Criteria;

/**
 * @author Consulting & Development. Iñaki Ayerbe - 29/08/2007
 *
 */
public final class ResourceManager {

	static private ResourceManager RESOURCEMANAGER = new ResourceManager();

	private ResourceManager() { }

	static public ResourceManager getResourceManager() {
		return RESOURCEMANAGER;
	}

	/**
	 * Return the company resources list.
	 * 
	 * @return
	 * @throws ManagerBeanException
	 */
	public List getResources() throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean( Resource.class );
		Criteria criteria = new Criteria();
		criteria.addNullExpression( bean.getFieldName( ICompanyAlias.RESOURCE_ENDING_DATE ) );
		return bean.getList( criteria );
	}

	/**
	 * Return the company resource.
	 * 
	 * @param id
	 * @return
	 * @throws ManagerBeanException
	 */
	public Resource getResource(Integer id) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean( Resource.class );
		Criteria criteria = new Criteria();
		criteria.addEqualExpression( bean.getFieldName( ICompanyAlias.RESOURCE_ID), id);
		return (Resource) bean.getList( criteria ).get( 0 );
	}

	/**
	 * Return the current employee resource.
	 * 
	 * @param employee
	 * @return
	 * @throws ManagerBeanException
	 */
	public Resource getResource(Employee employee) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean( Resource.class );
		Criteria criteria = new Criteria();
		String field = bean.getFieldName( ICompanyAlias.RESOURCE_EMPLOYEE_ID );
		criteria.addEqualExpression( field, employee.getId() );
		criteria.addNullExpression( bean.getFieldName( ICompanyAlias.RESOURCE_ENDING_DATE ) );
		return (Resource) bean.getList( criteria ).get( 0 );
	}

	/**
	 * Return a new employee resource.
	 * 
	 * @param employee
	 * @return
	 * @throws ManagerBeanException
	 */
	public Resource createResource(Employee employee) throws ManagerBeanException {
		Resource resource = new Resource();
		resource.setEmployee( employee );
		IManagerBean bean = BeanManager.getManagerBean( WorkPlace.class );
		resource.setWorkPlace( (WorkPlace) bean.getList( null ).get( 0 ) );
		resource.setStartingDate( new Date() );
		return resource;
	}

	/**
	 * Add the resource.
	 * 
	 * @param resource
	 * @throws ManagerBeanException
	 */
	public void addResource(Resource resource) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean( Resource.class );
		bean.insert( resource );
	}

	/**
	 * Update the resource.
	 * 
	 * @param resource
	 * @throws ManagerBeanException
	 */
	public void updateResource(Resource resource) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean( Resource.class );
		bean.update( resource );
	}

}
