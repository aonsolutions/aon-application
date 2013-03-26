package com.code.aon.common.dao.hibernate.type;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.usertype.EnhancedUserType;
import org.hibernate.usertype.ParameterizedType;
import org.hibernate.util.ReflectHelper;

import com.code.aon.common.enumeration.IStringEnum;

/**
 * A generic UserType that handles String-based JDK 5.0 Enums.
 *
 * @author Gavin King
 */
public class StringEnumUserType implements EnhancedUserType, ParameterizedType {

    private Class<Enum> enumClass;

    public void setParameterValues(Properties parameters) {
        String enumClassName = parameters.getProperty("enumClassname");
        try {
            enumClass = ReflectHelper.classForName(enumClassName);
        }
        catch (ClassNotFoundException cnfe) {
            throw new HibernateException("Enum class not found", cnfe);
        }
    }

    public Class returnedClass() {
        return enumClass;
    }

    public int[] sqlTypes() {
        return new int[] { Hibernate.STRING.sqlType() };
    }

    public boolean isMutable() {
        return false;
    }

    public Object deepCopy(Object value) {
        return value;
    }

    public Serializable disassemble(Object value) {
        return (Enum) value;
    }

    public Object replace(Object original, Object target, Object owner) {
        return original;
    }

    public Object assemble(Serializable cached, Object owner) {
        return cached;
    }

    public boolean equals(Object x, Object y) {
        return x==y;
    }

    public int hashCode(Object x) {
        return x.hashCode();
    }

    public Object fromXMLString(String xmlValue) {
        return Enum.valueOf(enumClass, xmlValue);
    }

    public String objectToSQLString(Object value) {
        return '\'' + ( (Enum) value ).name() + '\'';
    }

    public String toXMLString(Object value) {
        return ( (Enum) value ).name();
    }

    private Object getValue( String name ) {
    	if ( IStringEnum.class.isAssignableFrom(enumClass) ) {
    		for (Enum element : enumClass.getEnumConstants()) {
    			IStringEnum stringEnum = (IStringEnum) element;
    			if ( StringUtils.equals(stringEnum.getValue(), name)) {
    				return element;
    			}
    		}
    		return null;
    	}
    	return Enum.valueOf(enumClass, name);
    }
    
    private String getString( Enum value ) {
    	if ( IStringEnum.class.isAssignableFrom(enumClass) ) {
    		return ((IStringEnum) value).getValue();
    	}
    	return value.name();
    }    
    
    public Object nullSafeGet(ResultSet rs, String[] names, Object owner)
            throws SQLException {
        String name = rs.getString( names[0] );
        return rs.wasNull() ? null : getValue(name);
    }

    public void nullSafeSet(PreparedStatement st, Object value, int index)
            throws SQLException {
        if (value==null) {
            st.setNull(index, Hibernate.STRING.sqlType());
        }
        else {
            st.setString( index, getString((Enum) value ) );
        }
    }

}