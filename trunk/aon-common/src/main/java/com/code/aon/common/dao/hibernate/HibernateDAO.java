package com.code.aon.common.dao.hibernate;

import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.hibernate.EntityMode;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Projections;
import org.hibernate.impl.CriteriaImpl;
import org.hibernate.impl.CriteriaImpl.OrderEntry;
import org.hibernate.metadata.ClassMetadata;

import com.code.aon.common.AbstractFieldMapper;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.CriteriaUtilities;
import com.code.aon.common.dao.DAOConstants;
import com.code.aon.common.dao.DAOConstantsEntry;
import com.code.aon.common.dao.IDAO;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ql.ProjectionList;

/**
 * This class access to the <code>DataSource</code> through Hibernate.
 * It has a basic implementation for all DAO. 
 * 
 * @author Consulting & Development. Aimar Tellitu - 16-may-2005
 * @since 1.0
 *  
 */
public class HibernateDAO extends AbstractFieldMapper implements IDAO {

	private DAOConstantsEntry entry;
	private ClassMetadata classMetaData;
	private Class POJOClass;
	private String sessionFactoryName;

	/**
	 * Construct an Hibernate DAO.
	 * 
	 * @param POJOClass
	 * @param sessionFactoryName 
	 */
	public HibernateDAO(Class POJOClass, String sessionFactoryName) {
		this.POJOClass = POJOClass;
		this.sessionFactoryName = sessionFactoryName;
		this.entry = DAOConstants.getDAOConstant(this.POJOClass);
		SessionFactory factory = HibernateUtil.getSessionFactory(sessionFactoryName);
		this.classMetaData = factory.getClassMetadata( this.POJOClass );
	}

	/**
	 * Return the <code>ITransferObject</code> bound to the entity name.
	 * 
	 * @param entityName
	 * @param pk
	 * @return The <code>ITransferObject</code> bound to the entity name.
	 * @throws DAOException
	 */
	protected ITransferObject get(String entityName, Serializable pk) throws DAOException {
        Session session = HibernateUtil.getSession(sessionFactoryName);
		try {
			return (ITransferObject) session.get(entityName, pk);
		} catch (HibernateException he) {
			if (HibernateUtil.mustCloseSession()) {
                HibernateUtil.closeSession(sessionFactoryName);
			}
			if (he.getCause() != null) {
				throw new DAOException(he.getCause().getMessage(), he.getCause());	
			}
			throw new DAOException(he.getMessage(), he);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.code.aon.common.AbstractFieldMapper#getFieldMap()
	 */
	protected Map getFieldMap() {
		return this.entry.getHibernateMap();
	}

	/*
	 * (non-Javadoc)
	 * @see com.code.aon.common.dao.IDAO#getId(com.code.aon.common.ITransferObject)
	 */
	public Serializable getId(ITransferObject to) {
		return this.classMetaData.getIdentifier(to,EntityMode.POJO);
		
	}

	/*
	 * (non-Javadoc)
	 * @see com.code.aon.common.dao.IDAO#setId(com.code.aon.common.ITransferObject, java.io.Serializable)
	 */
	public void setId(ITransferObject to, Serializable id) {
		this.classMetaData.setIdentifier( to, id, EntityMode.POJO );
	}

	/*
	 * (non-Javadoc) //JORGE ******
	 * @see com.code.aon.common.dao.IDAO#set(com.code.aon.common.ITransferObject, java.io.String, java.lang.Object)
	 */
	public void setProperty(ITransferObject to, String propertyName, Object value) {
		this.classMetaData.setPropertyValue(to, propertyName, value, EntityMode.POJO);
	}

    /* 
     * (non-Javadoc)
     * @see com.code.aon.common.dao.IDAO#get(java.io.Serializable)
     */
    public ITransferObject get(Serializable pk) throws DAOException {
    	return get( this.entry.getPojo(), pk );
    }
    
    private boolean isDistinctProblem( org.hibernate.Criteria criteria, int count ) {
    	if ( count != -1 ) {
    		CriteriaImpl criteriaImpl = (CriteriaImpl) criteria;
    		return criteriaImpl.getResultTransformer() == CriteriaSpecification.DISTINCT_ROOT_ENTITY;
    	}
    	return false;
    }
    
    private List<ITransferObject> getDistinctList( Session session, CriteriaImpl criteria ) {
    	org.hibernate.criterion.ProjectionList projectionList = Projections.projectionList().add(Projections.id());
    	for (Iterator i = criteria.iterateOrderings(); i.hasNext();) {
    		OrderEntry entry = (OrderEntry) i.next();
    		String property = StringUtils.split(entry.getOrder().toString())[0];
    		projectionList.add(Projections.property(property));
    	}    	
    	criteria.setProjection(Projections.distinct(projectionList));
    	
    	List idlist = new LinkedList<Serializable>();
    	for( Object o : criteria.list() ) {
    		idlist.add( (projectionList.getLength() > 1 ? ((Object[])o)[0] : o) );
    	}

    	if (! idlist.isEmpty()) {
    		org.hibernate.Criteria entityCriteria = session.createCriteria(this.entry.getPojo());
    		ClassMetadata cm = session.getSessionFactory().getClassMetadata(this.entry.getPojo());
    		String id = cm.getIdentifierPropertyName();
    		entityCriteria.add(Expression.in(id, idlist));
    		return entityCriteria.list();
    	} else {
    		return Collections.EMPTY_LIST;
    	}    	
    }
    
    /* 
     * (non-Javadoc)
	 * @see com.code.aon.common.dao.IDAO#getList(com.code.aon.ql.Criteria, int, int)
	 */
	@SuppressWarnings("unchecked")
	public List<ITransferObject> getList(Criteria criteria, int offset, int count)
			throws DAOException {
		List<ITransferObject> list = null;
        Session session = HibernateUtil.getSession(sessionFactoryName);
		try {
			if (HibernateUtil.mustBeginTransaction()) {
				session.beginTransaction();	
			}

			org.hibernate.Criteria hibernateCriteria = CriteriaUtilities
					.toHibernateCriteria(criteria, session, this.entry);
			if (count != -1) {
				hibernateCriteria.setFirstResult(offset);
				hibernateCriteria.setMaxResults(count);
			}
			if (! isDistinctProblem(hibernateCriteria, count) ) {
				list = hibernateCriteria.list();				
			} else {
				list = getDistinctList(session, (CriteriaImpl) hibernateCriteria);
			}	
			if (HibernateUtil.mustBeginTransaction()) {
				session.getTransaction().commit();
			}
		} catch (HibernateException he) {
			if (HibernateUtil.mustBeginTransaction()) {
				session.getTransaction().rollback();
			}
			if (he.getCause() != null) {
				throw new DAOException(he.getCause().getMessage(), he.getCause());	
			}
			throw new DAOException(he.getMessage(), he);
		} finally {
			if (HibernateUtil.mustCloseSession()) {
                HibernateUtil.closeSession(sessionFactoryName);
			}
		}
		return list;
	}

	/* 
	 * (non-Javadoc)
	 * @see com.code.aon.common.dao.IDAO#getList(com.code.aon.ql.Criteria)
	 */
	public List<ITransferObject> getList(Criteria criteria) throws DAOException {
		return getList(criteria, -1, -1);
	}

	/* 
	 * (non-Javadoc)
	 * @see com.code.aon.common.dao.IDAO#remove(com.code.aon.common.ITransferObject)
	 */
	public boolean remove(ITransferObject t) throws DAOException {
        Session session = HibernateUtil.getSession(sessionFactoryName);
		boolean removed = false;
		try {
			if (HibernateUtil.mustBeginTransaction()) {
				session.beginTransaction();	
			}
			Serializable pk = getId( t );
			Object to = session.get(this.entry.getPojo(), pk);
			if (to != null) {
				session.delete(to);
				removed = true;
			}

			if (HibernateUtil.mustBeginTransaction()) {
				session.getTransaction().commit();
			}
		} catch (HibernateException he) {
			if (HibernateUtil.mustBeginTransaction()) {
				session.getTransaction().rollback();
			}
			if (he.getCause() != null) {
				throw new DAOException(he.getCause().getMessage(), he.getCause());	
			}
			throw new DAOException(he.getMessage(), he);
		} finally {
			if (HibernateUtil.mustCloseSession()) {
                HibernateUtil.closeSession(sessionFactoryName);
			}
		}
		return removed;
	}

	/* 
	 * (non-Javadoc)
	 * @see com.code.aon.common.dao.IDAO#update(com.code.aon.common.ITransferObject)
	 */
	public ITransferObject update(ITransferObject to) throws DAOException {
        Session session = HibernateUtil.getSession(sessionFactoryName);
		try {
			if (HibernateUtil.mustBeginTransaction()) {
				session.beginTransaction();  
			}

			session.update(to);
			
			if (HibernateUtil.mustBeginTransaction()) {
				session.getTransaction().commit();
			}
		} catch (HibernateException he) {
			if (HibernateUtil.mustBeginTransaction()) {
				session.getTransaction().rollback();
			}
			if (he.getCause() != null) {
				throw new DAOException(he.getCause().getMessage(), he.getCause());	
			}
			throw new DAOException(he.getMessage(), he);
		} finally {
			if (HibernateUtil.mustCloseSession()) {
                HibernateUtil.closeSession(sessionFactoryName);
			}
		}
		return to;
	}

	/* 
	 * (non-Javadoc)
	 * @see com.code.aon.common.dao.IDAO#insert(com.code.aon.common.ITransferObject)
	 */
	public ITransferObject insert(ITransferObject to) throws DAOException {
        Session session = HibernateUtil.getSession(sessionFactoryName);
		try {
			if (HibernateUtil.mustBeginTransaction()) {
				session.beginTransaction();	
			}

			session.save(to);
			
			if (HibernateUtil.mustBeginTransaction()) {
				session.getTransaction().commit();
			}
		} catch (HibernateException he) {
			if (HibernateUtil.mustBeginTransaction()) {
				session.getTransaction().rollback();
			}
			if (he.getCause() != null) {
				throw new DAOException(he.getCause().getMessage(), he.getCause());	
			}
			throw new DAOException(he.getMessage(), he);	
			
		} finally {
			if (HibernateUtil.mustCloseSession()) {
                HibernateUtil.closeSession(sessionFactoryName);
			}
		}
		return to;
	}

	/* 
	 * (non-Javadoc)
	 * @see com.code.aon.common.dao.IDAO#insert(com.code.aon.common.ITransferObject)
	 */
	public ITransferObject insertOrUpdate(ITransferObject to) throws DAOException {
        Session session = HibernateUtil.getSession(sessionFactoryName);
		try {
			if (HibernateUtil.mustBeginTransaction()) {
				session.beginTransaction();	
			}

			session.saveOrUpdate(to);
			
			if (HibernateUtil.mustBeginTransaction()) {
				session.getTransaction().commit();
			}
		} catch (HibernateException he) {
			if (HibernateUtil.mustBeginTransaction()) {
				session.getTransaction().rollback();
			}
			if (he.getCause() != null) {
				throw new DAOException(he.getCause().getMessage(), he.getCause());	
			}
			throw new DAOException(he.getMessage(), he);
		} finally {
			if (HibernateUtil.mustCloseSession()) {
                HibernateUtil.closeSession(sessionFactoryName);
			}
		}
		return to;
	}
	
	public int getCount(Criteria criteria) throws DAOException {
		Object value = getUniqueResult( Projection.rowCount(), criteria);
		if ( value != null ) {
			return ((Integer) value).intValue();
		}
		return 0;
	}

	public Object getUniqueResult(Projection projection, Criteria criteria) throws DAOException {
        Session session = HibernateUtil.getSession(sessionFactoryName);
		try {
			org.hibernate.Criteria hibernateCriteria = CriteriaUtilities
					.toHibernateCriteria(criteria, new ProjectionList(projection), session, this.entry);
			return hibernateCriteria.uniqueResult();
		} catch (HibernateException he) {
			if (he.getCause() != null) {
				throw new DAOException(he.getCause().getMessage(), he.getCause());	
			}
			throw new DAOException(he.getMessage(), he);
		} finally {
			if (HibernateUtil.mustCloseSession()) {
                HibernateUtil.closeSession(sessionFactoryName);
			}
		}
	}

	public List getList(ProjectionList projectionList, Criteria criteria) throws DAOException {
        Session session = HibernateUtil.getSession(sessionFactoryName);
		try {
			org.hibernate.Criteria hibernateCriteria = CriteriaUtilities
					.toHibernateCriteria(criteria, projectionList, session, this.entry);
			return hibernateCriteria.list();
		} catch (HibernateException he) {
			if (he.getCause() != null) {
				throw new DAOException(he.getCause().getMessage(), he.getCause());	
			}
			throw new DAOException(he.getMessage(), he);
		} finally {
			if (HibernateUtil.mustCloseSession()) {
                HibernateUtil.closeSession(sessionFactoryName);
			}
		}
	}
	
	public Class getPOJOClass() {
		return POJOClass;
	}

}