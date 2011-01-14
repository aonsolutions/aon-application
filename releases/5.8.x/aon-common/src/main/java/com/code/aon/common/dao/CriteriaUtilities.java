package com.code.aon.common.dao;

import java.io.Serializable;
import java.io.StringWriter;

import org.hibernate.Session;

import com.code.aon.common.dao.hibernate.HibernateRenderer;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ProjectionList;
import com.code.aon.ql.ast.sql.SqlRenderer;
import com.code.aon.ql.util.ExpressionException;

/**
 * Utilities for doing SQL statements.
 * 
 * @author Consulting & Development. Aimar Tellitu - 21-jul-2005
 * @since 1.0
 * 
 */
public class CriteriaUtilities {

	/**
	 * WHERE literal.
	 */
	private static final String WHERE = " WHERE "; //$NON-NLS-1$

	/**
	 * Literal para la palabra reservada SQL AND literal.
	 */
	private static final String AND = " AND "; //$NON-NLS-1$

	/**
	 * Return a String SQL statement from another root statement and its
	 * <code>Criteria</code> that will transform to a WHERE clause.
	 * 
	 * @param criteria
	 * @param stmt
	 * @return The statement String.
	 */
	@Deprecated
	public static String toSQLString(Criteria criteria, String stmt) {
		if (criteria != null) {
			StringBuffer buff = new StringBuffer(stmt);

			if (criteria.getExpression() != null) {
				if (buff.indexOf(WHERE) == -1) {
					buff.append(WHERE);
				} else {
					buff.append(AND);
				}
			}
			StringWriter out = new StringWriter();
			SqlRenderer renderer = new SqlRenderer(out);
			criteria.accept(renderer);
			buff.append(out.toString());

			return buff.toString();
		}
		return stmt;
	}

	/**
	 * Return a String SQL statement from another root statement, the primary
	 * key field name and its value that will conform the WHERE clause.
	 * 
	 * @param field
	 * @param id
	 * @param stmt
	 * @return The WHERE clause.
	 */
	@Deprecated
	public static String toSQLString(String field, Serializable id, String stmt) {
		Criteria criteria = new Criteria();
		try {
			criteria.addExpression(field, id.toString());
		} catch (ExpressionException e) {
			e.printStackTrace();
		}
		return toSQLString(criteria, stmt);
	}

	/**
	 * Return a <code>org.hibernate.Criteria</code> from a
	 * <code>Criteria</code> that will transform to a WHERE clause, and the
	 * <code>DAOConstantsEntry</code>.
	 * 
	 * @param criteria
	 * @param session
	 * @param daoEntry
	 * @return The <code>org.hibernate.Criteria</code>.
	 * @throws DAOException
	 */
	public static org.hibernate.Criteria toHibernateCriteria(Criteria criteria,
			Session session, DAOConstantsEntry daoEntry) throws DAOException {
		return toHibernateCriteria(criteria, null, session, daoEntry);
	}

	/**
	 * Return a <code>org.hibernate.Criteria</code> from a
	 * <code>Criteria</code> that will transform to a WHERE clause, and the
	 * <code>DAOConstantsEntry</code>.
	 * 
	 * @param criteria
	 * @param projectionList
	 * @param session
	 * @param daoEntry
	 * @return The <code>org.hibernate.Criteria</code>.
	 * @throws DAOException
	 */
	public static org.hibernate.Criteria toHibernateCriteria(Criteria criteria,
			ProjectionList projectionList, Session session,
			DAOConstantsEntry daoEntry) throws DAOException {

		org.hibernate.Criteria hibernateCriteria = session
				.createCriteria(daoEntry.getPojo());

		boolean hasCriteria = (criteria != null) && (!criteria.isEmpty());
		boolean hasProjections = (projectionList != null) && (!projectionList.isEmpty()); 
		if ( hasCriteria || hasProjections ) {
			HibernateRenderer hibernateRenderer = new HibernateRenderer(
					hibernateCriteria, session.getSessionFactory(), daoEntry.getPojo());
			if ( hasCriteria ) {
				criteria.accept(hibernateRenderer);
			}
			if ( hasProjections ) {
				projectionList.accept(hibernateRenderer);
			}
			Exception exception = hibernateRenderer.getException();
			if (exception != null) {
				throw new DAOException(exception.getMessage(), exception);
			}
		}
		return hibernateCriteria;
	}

}
