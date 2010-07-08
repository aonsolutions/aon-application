/**
 * 
 */
package com.code.aon.company.resources;

import java.util.Date;
import java.util.List;

import org.hibernate.Query;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
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
	@SuppressWarnings("unchecked")
	public List getResources() throws ManagerBeanException {
		String select = "SELECT r FROM Resource r, Employee e " +
			"WHERE r.employee = e " +
			"GROUP BY r.employee " +
			"ORDER BY r.employee";
		Query query = HibernateUtil.getSession().createQuery( select );
		return query.list();
	}

	/**
	 * Return the company active resources list.
	 * 
	 * @return
	 * @throws ManagerBeanException
	 */
	@SuppressWarnings("unchecked")
	public List getActiveResources() throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean( Resource.class );
		Criteria criteria = new Criteria();
		criteria.addNullExpression( bean.getFieldName( ICompanyAlias.RESOURCE_ENDING_DATE ) );
		return bean.getList( criteria );
	}

	/**
	 * Return the company inactive resources list.
	 * 
	 * @return
	 * @throws ManagerBeanException
	 */
	@SuppressWarnings("unchecked")
	public List getInActiveResources() throws ManagerBeanException {
		String select = "SELECT r FROM Resource r, Employee e " +
			"WHERE r.employee = e " +
			"AND r.active = 0 " +
			"GROUP BY r.employee " +
			"ORDER BY r.employee";
		Query query = HibernateUtil.getSession().createQuery( select );
		return query.list();
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
	@SuppressWarnings("unchecked")
	public Resource getResource(Employee employee) throws ManagerBeanException {
		String select = "SELECT r FROM Resource r, Employee e " +
			"WHERE r.employee = e " +
			"AND r.employee = :employee " +
			"AND r.endingDate is null " +
			"GROUP BY r.employee";
		Query query = HibernateUtil.getSession().createQuery( select );
		query.setEntity( "employee", employee );
		List l = query.list();
		return ( l.size() > 0 )? (Resource) l.get( l.size() - 1 ): null; 
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
