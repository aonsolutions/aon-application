package com.code.aon.common;

import java.io.Serializable;
import java.util.List;

import com.code.aon.common.dao.IDAO;
import com.code.aon.common.dao.sql.DAOException;
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

	/* 
	 * (non-Javadoc)
	 * @see com.code.aon.common.IFinderBean#getList(com.code.aon.ql.Criteria)
	 */
	public List<ITransferObject> getList(Criteria criteria) throws ManagerBeanException {
		try {
			return dao.getList(criteria);
		} catch (DAOException e) {
			throw new ManagerBeanException(e.getMessage(), e);
		}
	}

	/* 
	 * (non-Javadoc)
	 * @see com.code.aon.common.IFinderBean#getList(com.code.aon.ql.Criteria, int, int)
	 */
	public List<ITransferObject> getList(Criteria criteria, int offset, int count)
			throws ManagerBeanException {
		try {
			return dao.getList(criteria, offset, count);
		} catch (DAOException e) {
			throw new ManagerBeanException(e.getMessage(), e);
		}
	}

	public List getList(ProjectionList projectionList, Criteria criteria) throws ManagerBeanException {
		try {
			return dao.getList(projectionList, criteria);
		} catch (DAOException e) {
			throw new ManagerBeanException(e.getMessage(), e);
		}
	}

	public Object getUniqueResult(Projection projection, Criteria criteria) throws ManagerBeanException {
		try {
			return dao.getUniqueResult(projection, criteria);
		} catch (DAOException e) {
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
			int count = dao.getCount(criteria);
			if ( criteria != null ) {
				criteria.setOrderByList( obl );	
			}
			return count;
		} catch (DAOException e) {
			throw new ManagerBeanException(e.getMessage(), e);
		}
	}

	/* 
	 * (non-Javadoc)
	 * @see com.code.aon.common.IFinderBean#getPOJOClass()
	 */
	public Class getPOJOClass() {
		return dao.getPOJOClass();
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

}