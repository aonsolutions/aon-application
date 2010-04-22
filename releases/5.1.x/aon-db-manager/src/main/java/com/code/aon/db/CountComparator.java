package com.code.aon.db;

import java.io.Serializable;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.hibernate.criterion.Projections;

public class CountComparator implements Comparator<Class<? extends Serializable>> {
	
	private SessionFactory sessionFactory;
	
	private Map<Class<? extends Serializable>,Integer> counts;

	public CountComparator(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
		this.counts = new HashMap<Class<? extends Serializable>, Integer>();
	}
	
	public static Integer getRowCount( SessionFactory sessionFactory, Class<?> entity ) {
		Integer result = null;
		StatelessSession session = sessionFactory.openStatelessSession();
		Criteria criteria = session.createCriteria(entity);
		criteria.setProjection(Projections.rowCount());
		Object value = criteria.uniqueResult();
		if ( value != null ) {
			result = (Integer) value;
		}
		session.close();
		return result;
	}
	
	private Integer getCount( Class<? extends Serializable> entity ) {
		Integer result = this.counts.get(entity);
		if ( result == null ) {
			result = getRowCount(sessionFactory, entity);
			this.counts.put(entity, result);
		}
		return result;
	}
	
	@Override
	public int compare(Class<? extends Serializable> o1,
			Class<? extends Serializable> o2) {
		return getCount(o1).compareTo(getCount(o2));
	}

}
