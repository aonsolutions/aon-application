package com.code.aon.common.dao.hibernate;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.hibernate.EntityMode;
import org.hibernate.HibernateException;
import org.hibernate.Session;
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

	/**
	 * Construct an Hibernate DAO.
	 * 
	 * @param POJOClass
	 */
	public HibernateDAO(Class POJOClass) {
		this.POJOClass = POJOClass;
		this.entry = DAOConstants.getDAOConstant(this.POJOClass);
		this.classMetaData = HibernateUtil.getSessionFactory().getClassMetadata( this.POJOClass );
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
        Session session = HibernateUtil.getSession();
		try {
			return (ITransferObject) session.get(entityName, pk);
		} catch (HibernateException he) {
			if (HibernateUtil.mustCloseSession()) {
                HibernateUtil.closeSession();
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

    /* 
     * (non-Javadoc)
	 * @see com.code.aon.common.dao.IDAO#getList(com.code.aon.ql.Criteria, int, int)
	 */
	@SuppressWarnings("unchecked")
	public List<ITransferObject> getList(Criteria criteria, int offset, int count)
			throws DAOException {
		List<ITransferObject> list = null;
        Session session = HibernateUtil.getSession();
		try {
			if (HibernateUtil.mustBeginTransaction()) {
				HibernateUtil.getSession().beginTransaction();	
			}

			org.hibernate.Criteria hibernateCriteria = CriteriaUtilities
					.toHibernateCriteria(criteria, session, this.entry);
			if (count != -1) {
				hibernateCriteria.setFirstResult(offset);
				hibernateCriteria.setMaxResults(count);
			}
			list = hibernateCriteria.list();
			if (HibernateUtil.mustBeginTransaction()) {
				HibernateUtil.getSession().getTransaction().commit();
			}
		} catch (HibernateException he) {
			if (HibernateUtil.mustBeginTransaction()) {
				HibernateUtil.getSession().getTransaction().rollback();
			}
			if (he.getCause() != null) {
				throw new DAOException(he.getCause().getMessage(), he.getCause());	
			}
			throw new DAOException(he.getMessage(), he);
		} finally {
			if (HibernateUtil.mustCloseSession()) {
                HibernateUtil.closeSession();
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
        Session session = HibernateUtil.getSession();
		boolean removed = false;
		try {
			if (HibernateUtil.mustBeginTransaction()) {
				HibernateUtil.getSession().beginTransaction();	
			}
			Serializable pk = getId( t );
			Object to = session.get(this.entry.getPojo(), pk);
			if (to != null) {
				session.delete(to);
				removed = true;
			}

			if (HibernateUtil.mustBeginTransaction()) {
				HibernateUtil.getSession().getTransaction().commit();
			}
		} catch (HibernateException he) {
			if (HibernateUtil.mustBeginTransaction()) {
				HibernateUtil.getSession().getTransaction().rollback();
			}
			if (he.getCause() != null) {
				throw new DAOException(he.getCause().getMessage(), he.getCause());	
			}
			throw new DAOException(he.getMessage(), he);
		} finally {
			if (HibernateUtil.mustCloseSession()) {
                HibernateUtil.closeSession();
			}
		}
		return removed;
	}

	/* 
	 * (non-Javadoc)
	 * @see com.code.aon.common.dao.IDAO#update(com.code.aon.common.ITransferObject)
	 */
	public ITransferObject update(ITransferObject to) throws DAOException {
        Session session = HibernateUtil.getSession();
		try {
			if (HibernateUtil.mustBeginTransaction()) {
                HibernateUtil.getSession().beginTransaction();  
			}

			session.update(to);
			
			if (HibernateUtil.mustBeginTransaction()) {
                HibernateUtil.getSession().getTransaction().commit();
			}
		} catch (HibernateException he) {
			if (HibernateUtil.mustBeginTransaction()) {
                HibernateUtil.getSession().getTransaction().rollback();
			}
			if (he.getCause() != null) {
				throw new DAOException(he.getCause().getMessage(), he.getCause());	
			}
			throw new DAOException(he.getMessage(), he);
		} finally {
			if (HibernateUtil.mustCloseSession()) {
                HibernateUtil.closeSession();
			}
		}
		return to;
	}

	/* 
	 * (non-Javadoc)
	 * @see com.code.aon.common.dao.IDAO#insert(com.code.aon.common.ITransferObject)
	 */
	public ITransferObject insert(ITransferObject to) throws DAOException {
        Session session = HibernateUtil.getSession();
		try {
			if (HibernateUtil.mustBeginTransaction()) {
				HibernateUtil.getSession().beginTransaction();	
			}

			session.save(to);
			
			if (HibernateUtil.mustBeginTransaction()) {
				HibernateUtil.getSession().getTransaction().commit();
			}
		} catch (HibernateException he) {
			if (HibernateUtil.mustBeginTransaction()) {
				HibernateUtil.getSession().getTransaction().rollback();
			}
			if (he.getCause() != null) {
				throw new DAOException(he.getCause().getMessage(), he.getCause());	
			}
			throw new DAOException(he.getMessage(), he);	
			
		} finally {
			if (HibernateUtil.mustCloseSession()) {
                HibernateUtil.closeSession();
			}
		}
		return to;
	}

	/* 
	 * (non-Javadoc)
	 * @see com.code.aon.common.dao.IDAO#insert(com.code.aon.common.ITransferObject)
	 */
	public ITransferObject insertOrUpdate(ITransferObject to) throws DAOException {
        Session session = HibernateUtil.getSession();
		try {
			if (HibernateUtil.mustBeginTransaction()) {
				HibernateUtil.getSession().beginTransaction();	
			}

			session.saveOrUpdate(to);
			
			if (HibernateUtil.mustBeginTransaction()) {
				HibernateUtil.getSession().getTransaction().commit();
			}
		} catch (HibernateException he) {
			if (HibernateUtil.mustBeginTransaction()) {
				HibernateUtil.getSession().getTransaction().rollback();
			}
			if (he.getCause() != null) {
				throw new DAOException(he.getCause().getMessage(), he.getCause());	
			}
			throw new DAOException(he.getMessage(), he);
		} finally {
			if (HibernateUtil.mustCloseSession()) {
                HibernateUtil.closeSession();
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
        Session session = HibernateUtil.getSession();
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
                HibernateUtil.closeSession();
			}
		}
	}

	public List getList(ProjectionList projectionList, Criteria criteria) throws DAOException {
        Session session = HibernateUtil.getSession();
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
                HibernateUtil.closeSession();
			}
		}
	}
	
	public Class getPOJOClass() {
		return POJOClass;
	}

}