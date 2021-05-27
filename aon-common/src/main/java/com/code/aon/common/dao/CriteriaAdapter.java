package com.code.aon.common.dao;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.transform.ResultTransformer;

public class CriteriaAdapter {

	private Criteria criteria;
	
	private DetachedCriteria detachedCriteria;

	private CriteriaAdapter(Criteria criteria) {
		this.criteria = criteria;
	}
	
	private CriteriaAdapter(DetachedCriteria detachedCriteria) {
		this.detachedCriteria = detachedCriteria;
	}
	
	public static CriteriaAdapter asCriteria( Session session, String pojo ) {
		return new CriteriaAdapter(session.createCriteria(pojo));
	}

	public static CriteriaAdapter asDetachedCriteria( String pojo ) {
		return new CriteriaAdapter(DetachedCriteria.forEntityName(pojo));
	}

	public Criteria getCriteria() {
		return criteria;
	}

	public DetachedCriteria getDetachedCriteria() {
		return detachedCriteria;
	}

	public void add(Criterion criterion) {
		if ( criteria != null ) {
			criteria.add(criterion);	
		} else {
			detachedCriteria.add(criterion);
		}
	}
	
	public void addOrder(Order order) {
		if ( criteria != null ) {
			criteria.addOrder(order);	
		} else {
			detachedCriteria.addOrder(order);
		}
	}

    public void createAlias(String associationPath, String alias, int joinType) throws HibernateException {
		if ( criteria != null ) {
	        criteria.createAlias(associationPath, alias, joinType);	
		} else {
			detachedCriteria.createAlias(associationPath, alias, joinType);
		}
    }
    		
	public void setProjection(Projection projection) {
		if ( criteria != null ) {
			criteria.setProjection(projection);	
		} else {
			detachedCriteria.setProjection(projection);
		}
	}	
	
	public void setResultTransformer(ResultTransformer resultTransformer) {
		if ( criteria != null ) {
			criteria.setResultTransformer(resultTransformer);	
		} else {
			detachedCriteria.setResultTransformer(resultTransformer);
		}
	}	
	
}
