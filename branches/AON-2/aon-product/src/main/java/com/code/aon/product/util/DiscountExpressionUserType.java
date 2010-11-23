package com.code.aon.product.util;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;


/**
 * Hibernate custom mapping type for Rating.
 * <p>
 * This mapping type persists comment ratings to a <tt>TINYINT</tt> database
 * column.
 * 
 * @see CustomerStatus
 * @author Christian Bauer <christian@hibernate.org>
 */
public class DiscountExpressionUserType implements UserType {

	/**
	 * Field SQL_TYPES
	 */
	private static final int[] SQL_TYPES = { Types.TINYINT };

	/**
	 * Method sqlTypes
	 * @return int[]
	 * @see org.hibernate.usertype.UserType#sqlTypes()
	 */
	public int[] sqlTypes() {
		return SQL_TYPES;
	}

	/**
	 * Method returnedClass
	 * @return Class
	 * @see org.hibernate.usertype.UserType#returnedClass()
	 */
	public Class returnedClass() {
		return DiscountExpression.class;
	}

	/**
	 * Method assemble
	 * @param cached Serializable
	 * @param owner Object
	 * @return Object
	 * @throws HibernateException
	 * @see org.hibernate.usertype.UserType#assemble(Serializable, Object)
	 */
	public Object assemble(Serializable cached, Object owner)
			throws HibernateException {
		return cached;
	}

	/**
	 * Method disassemble
	 * @param value Object
	 * @return Serializable
	 * @throws HibernateException
	 * @see org.hibernate.usertype.UserType#disassemble(Object)
	 */
	public Serializable disassemble(Object value) throws HibernateException {
		return (DiscountExpression) value;
	}

	/**
	 * Method hashCode
	 * @param x Object
	 * @return int
	 * @throws HibernateException
	 * @see org.hibernate.usertype.UserType#hashCode(Object)
	 */
	public int hashCode(Object x) throws HibernateException {
		return ((DiscountExpression) x).hashCode();
	}

	/**
	 * Method replace
	 * @param original Object
	 * @param target Object
	 * @param owner Object
	 * @return Object
	 * @throws HibernateException
	 * @see org.hibernate.usertype.UserType#replace(Object, Object, Object)
	 */
	public Object replace(Object original, Object target, Object owner)
			throws HibernateException {
		return null;
	}

	/**
	 * Method equals
	 * @param x Object
	 * @param y Object
	 * @return boolean
	 * @see org.hibernate.usertype.UserType#equals(Object, Object)
	 */
	public boolean equals(Object x, Object y) {
		return x == y;
	}

	/**
	 * Method deepCopy
	 * @param value Object
	 * @return Object
	 * @see org.hibernate.usertype.UserType#deepCopy(Object)
	 */
	public Object deepCopy(Object value) {
		return value;
	}

	/**
	 * Method isMutable
	 * @return boolean
	 * @see org.hibernate.usertype.UserType#isMutable()
	 */
	public boolean isMutable() {
		return false;
	}

	/**
	 * Method nullSafeGet
	 * @param resultSet ResultSet
	 * @param names String[]
	 * @param owner Object
	 * @return Object
	 * @throws HibernateException
	 * @throws SQLException
	 * @see org.hibernate.usertype.UserType#nullSafeGet(ResultSet, String[], Object)
	 */
	public Object nullSafeGet(ResultSet resultSet, String[] names, Object owner)
			throws HibernateException, SQLException {

		String value = resultSet.getString(names[0]);
		return resultSet.wasNull() ? null : new DiscountExpression(value);
	}

	/**
	 * Method nullSafeSet
	 * @param statement PreparedStatement
	 * @param value Object
	 * @param index int
	 * @throws HibernateException
	 * @throws SQLException
	 * @see org.hibernate.usertype.UserType#nullSafeSet(PreparedStatement, Object, int)
	 */
	public void nullSafeSet(PreparedStatement statement, Object value, int index)
			throws HibernateException, SQLException {

		if (value == null) {
			statement.setNull(index, Types.CHAR);
		} else {
			DiscountExpression discountExpression = (DiscountExpression) value;
			statement.setString(index, discountExpression.getDiscountExpr() );
		}
	}
}