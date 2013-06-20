package com.code.aon.common;

import java.io.Serializable;
import java.util.List;

import com.code.aon.common.dao.IDAO;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.event.FinderBeanEvent;
import com.code.aon.common.event.IManagerBeanListener;
import com.code.aon.common.event.IManagerBeanVetoListener;
import com.code.aon.common.event.ManagerBeanListenerSupport;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.common.event.ManagerBeanVetoListenerSupport;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.OrderByList;
import com.code.aon.ql.Projection;
import com.code.aon.ql.ProjectionList;

/**
 * Basic implementation of the <code>IFinderBean</code> class.
 * 
 * @author 	Consulting & Development. Aimar Tellitu - 27-jun-2005
 * @since 	1.0
 * @see 	com.code.aon.common.IFinderBean
 * 
 */

public class BasicFinderBean implements IFinderBean {

	/**
	 * Data Access Object.
	 */
	private IDAO dao;

	/**
	 * Bean Listeners.
	 */
	private ManagerBeanListenerSupport listeners;

	/**
	 * Bean Veto Listeners.
	 */
	private ManagerBeanVetoListenerSupport vetoListeners;
	
	/**
	 * Construct a finder bean.
	 * 
	 * @param dao
	 */
	public BasicFinderBean(IDAO dao) {
		this.dao = dao;
	}

	/**
	 * @return Returns the dao.
	 */
	protected IDAO getDao() {
		return dao;
	}

	/**
	 * @return Returns the listeners.
	 */
	protected ManagerBeanListenerSupport getListeners() {
		return listeners;
	}

	/**
	 * @return Returns the vetoListeners.
	 */
	protected ManagerBeanVetoListenerSupport getVetoListeners() {
		return vetoListeners;
	}

	/**
	 * Listener registration method. Tell the ManagerBeanListenerSupport to add a new
	 * <code>IManagerBeanListener</code>.
	 * 
	 * @param listener
	 */
	public void addManagerBeanListener( IManagerBeanListener listener ) {
		if (listeners == null) {
			listeners = new ManagerBeanListenerSupport();
		}
		listeners.addListener( listener );
	}

	/**
	 * Listener registration method. Tell the ManagerBeanVetoListenerSupport to add a new
	 * <code>IManagerBeanVetoListener</code>.
	 * 
	 * @param vetoListener
	 */
	public void addManagerBeanVetoListener( IManagerBeanVetoListener vetoListener ) {
		if (vetoListeners == null) {
			vetoListeners = new ManagerBeanVetoListenerSupport(); 
		}
		vetoListeners.addListener( vetoListener );
	}

	/* 
	 * (non-Javadoc)
	 * @see com.code.aon.common.IFinderBean#getList(com.code.aon.ql.Criteria)
	 */
	public List<ITransferObject> getList(Criteria criteria) throws ManagerBeanException {
		try {
			criteria = criteria==null?new Criteria():criteria;
			FinderBeanEvent evt = getNewFinderBeanEvent( criteria );
			fireVetoableBeanSearched(evt);
			List<ITransferObject> ret = dao.getList(criteria);
			return ret;
		} catch (DAOException e) {
			throw new ManagerBeanException(e.getMessage(), e);
		} catch (ManagerBeanVetoListenerException e) {
			throw new ManagerBeanException(e.getMessage(), e);
		}
	}

	/* 
	 * (non-Javadoc)
	 * @see com.code.aon.common.IFinderBean#getList(com.code.aon.ql.Criteria, int, int)
	 */
	public List<ITransferObject> getList(Criteria criteria, int offset, int count) throws ManagerBeanException {
		try {
			criteria = criteria==null?new Criteria():criteria;
			FinderBeanEvent evt = getNewFinderBeanEvent( criteria );
			fireVetoableBeanSearched(evt);
			List<ITransferObject> ret = dao.getList(criteria, offset, count);
			return ret;
		} catch (DAOException e) {
			throw new ManagerBeanException(e.getMessage(), e);
		} catch (ManagerBeanVetoListenerException e) {
			throw new ManagerBeanException(e.getMessage(), e);
		}
	}

	public List<?> getList(ProjectionList projectionList, Criteria criteria) throws ManagerBeanException {
		try {
			criteria = criteria==null?new Criteria():criteria;
			FinderBeanEvent evt = getNewFinderBeanEvent( criteria );
			fireVetoableBeanSearched(evt);
			List<?> ret = dao.getList(projectionList, criteria);
			return ret;
		} catch (DAOException e) {
			throw new ManagerBeanException(e.getMessage(), e);
		} catch (ManagerBeanVetoListenerException e) {
			throw new ManagerBeanException(e.getMessage(), e);
		}
	}

	public Object getUniqueResult(Projection projection, Criteria criteria) throws ManagerBeanException {
		try {
			criteria = criteria==null?new Criteria():criteria;
			FinderBeanEvent evt = getNewFinderBeanEvent( criteria );
			fireVetoableBeanSearched(evt);
			Object ret = dao.getUniqueResult(projection, criteria);
			return ret;
		} catch (DAOException e) {
			throw new ManagerBeanException(e.getMessage(), e);
		} catch (ManagerBeanVetoListenerException e) {
			throw new ManagerBeanException(e.getMessage(), e);
		}
	}
	
	@Override
	public Object getUniqueResult(ProjectionList projectionList, Criteria criteria) throws ManagerBeanException {
		try {
			criteria = criteria==null?new Criteria():criteria;
			FinderBeanEvent evt = getNewFinderBeanEvent( criteria );
			fireVetoableBeanSearched(evt);
			Object ret = dao.getUniqueResult(projectionList, criteria);
			return ret;
		} catch (DAOException e) {
			throw new ManagerBeanException(e.getMessage(), e);
		} catch (ManagerBeanVetoListenerException e) {
			throw new ManagerBeanException(e.getMessage(), e);
		}
	}

	/* 
	 * (non-Javadoc)
	 * @see com.code.aon.common.IFinderBean#getFieldName(java.lang.String)
	 */
	public String getFieldName(String alias) throws ManagerBeanException {
		try {
			return dao.getFieldName(alias);
		} catch (DAOException e) {
			throw new ManagerBeanException(e.getMessage(), e);
		}
	}

	/* 
	 * (non-Javadoc)
	 * @see com.code.aon.common.IFinderBean#getCount(com.code.aon.ql.Criteria)
	 */
	public int getCount(Criteria criteria) throws ManagerBeanException {
		try {
			OrderByList obl = null;
			if ( criteria != null ) {
				obl = criteria.getOrderByList();
				criteria.setOrderByList( null );
			}
			criteria = criteria==null?new Criteria():criteria;
			FinderBeanEvent evt = getNewFinderBeanEvent( criteria );
			fireVetoableBeanSearched(evt);
			int count = dao.getCount(criteria);
			if ( criteria != null ) {
				criteria.setOrderByList( obl );	
			}
			return count;
		} catch (DAOException e) {
			throw new ManagerBeanException(e.getMessage(), e);
		} catch (ManagerBeanVetoListenerException e) {
			throw new ManagerBeanException(e.getMessage(), e);
		}
	}

	private FinderBeanEvent getNewFinderBeanEvent(Criteria criteria) {
		return new FinderBeanEvent(criteria,getPOJOClass());
	}

	/* 
	 * (non-Javadoc)
	 * @see com.code.aon.common.IFinderBean#getPOJOClass()
	 */
	@SuppressWarnings("unchecked")
	public Class<ITransferObject> getPOJOClass() {
		return (Class<ITransferObject>) dao.getPOJOClass();
	}

	/*
	 * (non-Javadoc)
	 * @see com.code.aon.common.IFinderBean#get(java.io.Serializable)
	 */
	public ITransferObject get(Serializable pk) throws ManagerBeanException {
		try {
			return dao.get(pk);
		} catch (DAOException e) {
			throw new ManagerBeanException(e.getMessage(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.code.aon.common.IFinderBean#getId(com.code.aon.common.ITransferObject)
	 */
	public Serializable getId(ITransferObject to) throws ManagerBeanException {
		try {
			return dao.getId(to);
		} catch (DAOException e) {
			throw new ManagerBeanException(e.getMessage(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.code.aon.common.IFinderBean#setId(com.code.aon.common.ITransferObject, java.io.Serializable)
	 */
	public void setId(ITransferObject to, Serializable id) throws ManagerBeanException {
		try {
			dao.setId(to, id);
		} catch (DAOException e) {
			throw new ManagerBeanException(e.getMessage(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.code.aon.common.IFinderBean#setId(com.code.aon.common.ITransferObject, java.lang.String, java.lang.Object)
	 */
	public void setProperty(ITransferObject to, String propertyName, Object value) throws ManagerBeanException {
		try {
			dao.setProperty(to, propertyName, value);
		} catch (DAOException e) {
			throw new ManagerBeanException(e.getMessage(), e);
		}
	}

	/**
     * Fire an existing FinderBeanEvent to any registered vetoListeners.
	 * 
	 * @param evt the FinderBeanEvent object
	 * @throws ManagerBeanVetoListenerException
	 */
	private void fireVetoableBeanSearched( FinderBeanEvent evt ) throws ManagerBeanVetoListenerException{
		if (getVetoListeners() != null) {
			getVetoListeners().vetoableBeanSearched( evt );
		}
	}
	
}