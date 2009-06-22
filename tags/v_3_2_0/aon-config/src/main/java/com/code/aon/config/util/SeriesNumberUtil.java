package com.code.aon.config.util;

import java.util.Iterator;

import org.hibernate.Query;
import org.hibernate.Session;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.CriteriaUtilities;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.config.Series;
import com.code.aon.config.dao.IConfigAlias;
import com.code.aon.ql.Criteria;

/**
 * Clase de utilidad para la manipulación de la serie y el número de los
 * documentos mercantiles.
 * 
 * @author Consulting & Development. ecastellano - 22/11/2006
 * 
 */
public class SeriesNumberUtil {

	/**
	 * Devuelve la serie correspondiente al id pasado por parámetro.
	 * 
	 * @param seriesId Id de la serie hay que devolver.
	 * @return la Serie.
	 */
	public static Series obtainSeries(String seriesId) throws ManagerBeanException {
		IManagerBean seriesBean = BeanManager.getManagerBean(Series.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(seriesBean.getFieldName(IConfigAlias.SERIES_ID), seriesId);
		Iterator iter = seriesBean.getList(criteria).iterator();
		if (iter.hasNext()) {
			return (Series)iter.next();
		}
		return null;
	}

	/**
	 * Devuelve el siguiente número de la tabla pasada por parámetro.
	 * 
	 * @param series Serie de la que hay que devolver el siguiente número.
	 * @param table Tabla de la que hay que devolver el siguiente número.
	 * @return El siguiente número.
	 */
	public static int obtainNumber(String series, String table) {
		return obtainNumber(series, table, null);
	}

	/**
	 * Devuelve el siguiente número de la tabla pasada por parámetro.
	 * 
	 * @param series Serie de la que hay que devolver el siguiente número.
	 * @param table Tabla de la que hay que devolver el siguiente número.
	 * @param criteria Restricciones sobre la consulta.
	 * @return El siguiente número.
	 */
	public static int obtainNumber(String series, String table,	Criteria criteria) {
		Session session = HibernateUtil.getSession();
		String hqlQuery = 
			"SELECT MAX(" + table.toLowerCase() + ".number) "
				+ "FROM " + table + " " + table.toLowerCase() + " WHERE "
				+ table.toLowerCase() + ".series = :series";
		if (criteria != null) {
			hqlQuery = CriteriaUtilities.toSQLString(criteria, hqlQuery);
		}
		Query query = session.createQuery(hqlQuery);
		query.setString("series", series);
		Integer results = (Integer)query.list().iterator().next();
		if (results != null) {
			return (results.intValue() + 1);
		}
		return 1;

	}
}
