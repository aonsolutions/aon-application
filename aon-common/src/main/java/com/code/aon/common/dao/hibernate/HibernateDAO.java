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
import org.hibernate.ReplicationMode;
import org.hibernate.Session;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Projections;
import org.hibernate.impl.CriteriaImpl;
import org.hibernate.impl.CriteriaImpl.OrderEntry;
import org.hibernate.metadata.ClassMetadata;

import com.code.aon.AonVersion;
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
public class HibernateDAO extends AbstractFieldMapper implements IDAO, Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private transient DAOConstantsEntry entry;
	private transient ClassMetadata classMetaData;
	private Class<? extends ITransferObject> POJOClass;
	private ISessionManager sessionManager;

	/**
	 * Construct an Hibernate DAO.
	 * 
	 * @param POJOClass
	 * @param sessionManager 
	 */
	public HibernateDAO(Class<? extends ITransferObject> POJOClass, ISessionManager sessionManager) {
		this.POJOClass = POJOClass;
		this.sessionManager = sessionManager;
	}
	
	private ClassMetadata getClassMetadata() {
		if ( this.classMetaData == null ) {
			this.classMetaData = sessionManager.getSessionFactory().getClassMetadata(POJOClass);
		}
		return this.classMetaData;
	}

	private DAOConstantsEntry getEntry() {
		if ( this.entry == null ) {
			this.entry = DAOConstants.getDAOConstant(this.POJOClass);
		}
		return this.entry;
	}
	
	@Override
	public ITransferObject newTo() throws DAOException {
		ITransferObject to = null;
		try {
			to = getPOJOClass().newInstance();
		} catch (InstantiationException e) {
			throw new DAOException(e.getMessage(), e);
		} catch (IllegalAccessException e) {
			throw new DAOException(e.getMessage(), e);
		}
		return to;
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
        Session session = sessionManager.getSession();
		try {
			return (ITransferObject) session.get(entityName, pk);
		} catch (HibernateException he) {
			if (he.getCause() != null) {
				throw new DAOException(he.getCause().getMessage(), he.getCause());	
			}
			throw new DAOException(he.getMessage(), he);
		} finally {
			if (sessionManager.mustCloseSession()) {
				sessionManager.closeSession();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.code.aon.common.AbstractFieldMapper#getFieldMap()
	 */
	protected Map<String,String> getFieldMap() {
		return getEntry().getHibernateMap();
	}

	@Override
	public Serializable getId(ITransferObject to) {
		return getClassMetadata().getIdentifier(to,EntityMode.POJO);
		
	}

	@Override
	public void setId(ITransferObject to, Serializable id) {
		getClassMetadata().setIdentifier( to, id, EntityMode.POJO );
	}

	@Override
	public void setProperty(ITransferObject to, String propertyName, Object value) {
		getClassMetadata().setPropertyValue(to, propertyName, value, EntityMode.POJO);
	}

	@Override
    public ITransferObject get(Serializable pk) throws DAOException {
    	return get( this.POJOClass.getName(), pk );
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
    		org.hibernate.Criteria entityCriteria = session.createCriteria(this.POJOClass.getName());
    		ClassMetadata cm = session.getSessionFactory().getClassMetadata(this.POJOClass.getName());
    		String id = cm.getIdentifierPropertyName();
    		entityCriteria.add(Expression.in(id, idlist));
    		return entityCriteria.list();
    	} else {
    		return Collections.emptyList();
    	}    	
    }
    
    @Override
    @SuppressWarnings("unchecked")
	public List<ITransferObject> getList(Criteria criteria, int offset, int count)
			throws DAOException {
		List<ITransferObject> list = null;
        Session session = sessionManager.getSession();
		try {
			if (sessionManager.mustBeginTransaction()) {
				session.beginTransaction();	
			}

			org.hibernate.Criteria hibernateCriteria = CriteriaUtilities
					.toHibernateCriteria(criteria, session, this.POJOClass.getName());
			if (offset != -1) {
				hibernateCriteria.setFirstResult(offset);
			}
			if (count != -1) {
				hibernateCriteria.setMaxResults(count);
			}
			if (! isDistinctProblem(hibernateCriteria, count) ) {
				list = hibernateCriteria.list();				
			} else {
				list = getDistinctList(session, (CriteriaImpl) hibernateCriteria);
			}	
			if (sessionManager.mustBeginTransaction()) {
				session.getTransaction().commit();
			}
		} catch (HibernateException he) {
			if (sessionManager.mustBeginTransaction()) {
				session.getTransaction().rollback();
			}
			if (he.getCause() != null) {
				throw new DAOException(he.getCause().getMessage(), he.getCause());	
			}
			throw new DAOException(he.getMessage(), he);
		} finally {
			if (sessionManager.mustCloseSession()) {
				sessionManager.closeSession();
			}
		}
		return list;
	}

	@Override
	public List<ITransferObject> getList(Criteria criteria) throws DAOException {
		return getList(criteria, -1, -1);
	}

	@Override
	public boolean remove(ITransferObject t) throws DAOException {
		return remove( getId(t) ); 
	}

	@Override
	public boolean remove(Serializable pk) throws DAOException {
        Session session = sessionManager.getSession();
		boolean removed = false;
		try {
			if (sessionManager.mustBeginTransaction()) {
				session.beginTransaction();	
			}
			Object to = session.get(this.POJOClass.getName(), pk);
			if (to != null) {
				session.delete(to);
				removed = true;
			}

			if (sessionManager.mustBeginTransaction()) {
				session.getTransaction().commit();
			}
		} catch (HibernateException he) {
			if (sessionManager.mustBeginTransaction()) {
				session.getTransaction().rollback();
			}
			if (he.getCause() != null) {
				throw new DAOException(he.getCause().getMessage(), he.getCause());	
			}
			throw new DAOException(he.getMessage(), he);
		} finally {
			if (sessionManager.mustCloseSession()) {
				sessionManager.closeSession();
			}
		}
		return removed;
	}
	
	@Override
	public ITransferObject update(ITransferObject to) throws DAOException {
        Session session = sessionManager.getSession();
		try {
			if (sessionManager.mustBeginTransaction()) {
				session.beginTransaction();  
			}

			session.update(to);
			
			if (sessionManager.mustBeginTransaction()) {
				session.getTransaction().commit();
			}
		} catch (HibernateException he) {
			if (sessionManager.mustBeginTransaction()) {
				session.getTransaction().rollback();
			}
			if (he.getCause() != null) {
				throw new DAOException(he.getCause().getMessage(), he.getCause());	
			}
			throw new DAOException(he.getMessage(), he);
		} finally {
			if (sessionManager.mustCloseSession()) {
				sessionManager.closeSession();
			}
		}
		return to;
	}

	@Override
	public ITransferObject insert(ITransferObject to) throws DAOException {
        Session session = sessionManager.getSession();
		try {
			if (sessionManager.mustBeginTransaction()) {
				session.beginTransaction();	
			}

			session.save(to);
			
			if (sessionManager.mustBeginTransaction()) {
				session.getTransaction().commit();
			}
		} catch (HibernateException he) {
			if (sessionManager.mustBeginTransaction()) {
				session.getTransaction().rollback();
			}
			if (he.getCause() != null) {
				throw new DAOException(he.getCause().getMessage(), he.getCause());	
			}
			throw new DAOException(he.getMessage(), he);	
			
		} finally {
			if (sessionManager.mustCloseSession()) {
				sessionManager.closeSession();
			}
		}
		return to;
	}

	@Override
	public ITransferObject insertOrUpdate(ITransferObject to) throws DAOException {
        Session session = sessionManager.getSession();
		try {
			if (sessionManager.mustBeginTransaction()) {
				session.beginTransaction();	
			}

			session.saveOrUpdate(to);
			
			if (sessionManager.mustBeginTransaction()) {
				session.getTransaction().commit();
			}
		} catch (HibernateException he) {
			if (sessionManager.mustBeginTransaction()) {
				session.getTransaction().rollback();
			}
			if (he.getCause() != null) {
				throw new DAOException(he.getCause().getMessage(), he.getCause());	
			}
			throw new DAOException(he.getMessage(), he);
		} finally {
			if (sessionManager.mustCloseSession()) {
				sessionManager.closeSession();
			}
		}
		return to;
	}
	
	private ReplicationMode getReplicationMode( com.code.aon.common.dao.hibernate.ReplicationMode mode ) {
		switch (mode) {
			case EXCEPTION:
				return ReplicationMode.EXCEPTION;
			case IGNORE:
				return ReplicationMode.IGNORE;
			case LATEST_VERSION:
				return ReplicationMode.LATEST_VERSION;
			case OVERWRITE:
				return ReplicationMode.OVERWRITE;
		}
		return null;
	}
	
	@Override
	public ITransferObject replicate(ITransferObject to, com.code.aon.common.dao.hibernate.ReplicationMode mode) throws DAOException {
        Session session = sessionManager.getSession();
		try {
			if (sessionManager.mustBeginTransaction()) {
				session.beginTransaction();	
			}

			session.replicate(to, getReplicationMode(mode));
			
			if (sessionManager.mustBeginTransaction()) {
				session.getTransaction().commit();
			}
		} catch (HibernateException he) {
			if (sessionManager.mustBeginTransaction()) {
				session.getTransaction().rollback();
			}
			if (he.getCause() != null) {
				throw new DAOException(he.getCause().getMessage(), he.getCause());	
			}
			throw new DAOException(he.getMessage(), he);	
			
		} finally {
			if (sessionManager.mustCloseSession()) {
				sessionManager.closeSession();
			}
		}
		return to;
	}	
	
	@Override
	public int getCount(Criteria criteria) throws DAOException {
		Object value = getUniqueResult( Projection.rowCount(), criteria);
		if ( value != null ) {
			return ((Integer) value).intValue();
		}
		return 0;
	}

	@Override
	public Object getUniqueResult(Projection projection, Criteria criteria) throws DAOException {
		return getUniqueResult(new ProjectionList(projection), criteria);
	}
	
	@Override
	public Object getUniqueResult(ProjectionList projectionList, Criteria criteria) throws DAOException {
        Session session = sessionManager.getSession();
		try {
			org.hibernate.Criteria hibernateCriteria = CriteriaUtilities
					.toHibernateCriteria(criteria, projectionList, session, this.POJOClass.getName());
			return hibernateCriteria.uniqueResult();
		} catch (HibernateException he) {
			if (he.getCause() != null) {
				throw new DAOException(he.getCause().getMessage(), he.getCause());	
			}
			throw new DAOException(he.getMessage(), he);
		} finally {
			if (sessionManager.mustCloseSession()) {
				sessionManager.closeSession();
			}
		}
	}

	@Override
	public List<?> getList(ProjectionList projectionList, Criteria criteria) throws DAOException {
        Session session = sessionManager.getSession();
		try {
			org.hibernate.Criteria hibernateCriteria = CriteriaUtilities
					.toHibernateCriteria(criteria, projectionList, session, this.POJOClass.getName());
			return hibernateCriteria.list();
		} catch (HibernateException he) {
			if (he.getCause() != null) {
				throw new DAOException(he.getCause().getMessage(), he.getCause());	
			}
			throw new DAOException(he.getMessage(), he);
		} finally {
			if (sessionManager.mustCloseSession()) {
				sessionManager.closeSession();
			}
		}
	}
	
	@Override
	public Class<? extends ITransferObject> getPOJOClass() {
		return POJOClass;
	}

}