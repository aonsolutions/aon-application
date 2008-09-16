package com.code.aon.config.util;

import org.hibernate.Query;
import org.hibernate.Session;

import com.code.aon.common.dao.CriteriaUtilities;
import com.code.aon.common.dao.hibernate.HibernateUtil;
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
	public static int obtainNumber(String series, String table,
			Criteria criteria) {
		Session session = HibernateUtil.getSession();
		String hqlQuery = "SELECT MAX(" + table.toLowerCase() + ".number) "
				+ "FROM " + table + " " + table.toLowerCase() + " WHERE "
				+ table.toLowerCase() + ".series = :series";
		if (criteria != null) {
			hqlQuery = CriteriaUtilities.toSQLString(criteria, hqlQuery);
		}
		Query q = session.createQuery(hqlQuery);
		q.setString("series", series);
		Integer results = (Integer) q.list().iterator().next();
		if (results != null) {
			return (results.intValue() + 1);
		}
		return 1;

	}
}
