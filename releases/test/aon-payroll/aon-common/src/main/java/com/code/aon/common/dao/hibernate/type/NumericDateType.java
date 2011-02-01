package com.code.aon.common.dao.hibernate.type;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;

/**
 * A "NumericDate" is a class that knows how to serialize instances of another
 * class to and from JDBC.<br>
 * 
 * @author Consulting & Development. 
 *
 */

public class NumericDateType implements UserType {

	private static final int[] TYPES = { Types.INTEGER };
	private DateFormat sixFormatter = new SimpleDateFormat("yyMMdd");
	private DateFormat eightFormatter = new SimpleDateFormat("yyyyMMdd");

	/*
	 * (non-Javadoc)
	 * @see org.hibernate.usertype.UserType#assemble(java.io.Serializable, java.lang.Object)
	 */
	public Object assemble(Serializable cached, Object owner)
			throws HibernateException {
		return cached;
	}

	/*
	 * (non-Javadoc)
	 * @see org.hibernate.usertype.UserType#deepCopy(java.lang.Object)
	 */
	public Object deepCopy(Object value) throws HibernateException {
		return value;
	}

	/*
	 * (non-Javadoc)
	 * @see org.hibernate.usertype.UserType#disassemble(java.lang.Object)
	 */
	public Serializable disassemble(Object value) throws HibernateException {
		return (Serializable) value;
	}

	/*
	 * (non-Javadoc)
	 * @see org.hibernate.usertype.UserType#equals(java.lang.Object, java.lang.Object)
	 */
	public boolean equals(Object x, Object y) throws HibernateException {
		return (x == y);
	}

	/*
	 * (non-Javadoc)
	 * @see org.hibernate.usertype.UserType#hashCode(java.lang.Object)
	 */
	public int hashCode(Object x) throws HibernateException {
		return x.hashCode();
	}

	/*
	 * (non-Javadoc)
	 * @see org.hibernate.usertype.UserType#isMutable()
	 */
	public boolean isMutable() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see org.hibernate.usertype.UserType#nullSafeGet(java.sql.ResultSet, java.lang.String[], java.lang.Object)
	 */
	public Object nullSafeGet(ResultSet rs, String[] names, Object owner)
			throws HibernateException, SQLException {
		int v = rs.getInt(names[0]);
		Integer value = new Integer( v);
		return (rs.wasNull() || v == 0)? null : getDate(value);
	}

	/*
	 * (non-Javadoc)
	 * @see org.hibernate.usertype.UserType#nullSafeSet(java.sql.PreparedStatement, java.lang.Object, int)
	 */
	public void nullSafeSet(PreparedStatement st, Object value, int index) throws HibernateException, SQLException {
	    if (value == null) {
	        st.setNull(index, Types.INTEGER);
	    } else {
	        st.setInt(index, getNumber((Date)value));
	    }
	}

	/*
	 * (non-Javadoc)
	 * @see org.hibernate.usertype.UserType#replace(java.lang.Object, java.lang.Object, java.lang.Object)
	 */
	public Object replace(Object original, Object target, Object owner)
			throws HibernateException {
		return original;
	}

	/*
	 * (non-Javadoc)
	 * @see org.hibernate.usertype.UserType#returnedClass()
	 */
	public Class returnedClass() {
		return Date.class;
	}

	/*
	 * (non-Javadoc)
	 * @see org.hibernate.usertype.UserType#sqlTypes()
	 */
	public int[] sqlTypes() {
		return TYPES;
	}

	/**
	 * Return the number.
	 * 
	 * @param date
	 * @return The number.
	 * @throws HibernateException
	 */
	private int getNumber(Date date) throws HibernateException {
		String s = eightFormatter.format(date);
		return Integer.parseInt(s);
	}

	/**
	 * Return the Date.
	 * 
	 * @param number
	 * @return The Date.
	 * @throws HibernateException
	 */
	private Date getDate(Number number) throws HibernateException {
		String n = Integer.toString( number.intValue() );
		DateFormat formatter = null;
		if (n.length() < 6) { // YYMMDD
			StringBuffer buf = new StringBuffer( n );
			while (buf.length() < 6) {
				buf.insert(0, '0');	
			}
			n = buf.toString();
			formatter = sixFormatter;
		} else if (n.length() == 6) { // YYMMDD
			formatter = sixFormatter;
		} else if (n.length() < 8) { // YYYYMMDD
			n = '0' + n;
			formatter = eightFormatter;
		} else if (n.length() == 8) { // YYYYMMDD
			formatter = eightFormatter;
		} else if (n.length() > 8) {
			throw new HibernateException ("Unknown Date.. '" + number + "'");
		}
		try {
			Date date = formatter.parse( n );
			return date; 
		} catch (Exception e) {
			throw new HibernateException ("Unknown Date.. '" + number + "'. " + e.getMessage());
		}
	}

}

