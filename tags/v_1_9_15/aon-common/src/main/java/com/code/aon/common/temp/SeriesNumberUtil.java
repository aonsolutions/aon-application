package com.code.aon.common.temp;

import org.hibernate.Query;
import org.hibernate.Session;

import com.code.aon.common.dao.CriteriaUtilities;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.ql.Criteria;

/**
 * TODO
 * 
 * @author Consulting & Development. ecastellano - 22/11/2006
 * 
 */
public class SeriesNumberUtil {

	/**
	 * TODO
	 * 
	 * @param series
	 * @param table
	 * @return int
	 */
	public static int obtainNumber(String series, String table) {
		Session session = HibernateUtil.getSession();
		String hqlQuery = "SELECT MAX(" + table.toLowerCase() + ".number) " + "FROM " + table + " "
				+ table.toLowerCase();
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(table.toLowerCase() + ".series", series);
		hqlQuery = CriteriaUtilities.toSQLString(criteria, hqlQuery);
		Query q = session.createQuery(hqlQuery);
		Integer results = (Integer) q.list().iterator().next();
		if (results != null) {
			return results.intValue() + 1;
		}
		return 1;
	}

	/**
	 * TODO
	 * 
	 * @param series
	 * @param level
	 * @return String
	 */
	public static String obtainSeries(String series, SecurityLevel level) {
		if (series == null || series.trim().equals("")) {
			series = (level.equals(SecurityLevel.OFFICIAL)) ? "07A" : "07B";
		}
		return series;
	}

}
