package com.code.aon.hyperview;

import java.sql.Types;

import org.apache.commons.lang.StringEscapeUtils;
import org.hibernate.Hibernate;
import org.hibernate.type.Type;

import com.code.aon.hyperview.enumeration.DataType;

public class HyperViewUtil {

	public static String escapeCharacters(String text) {
		text = text.replace("á", "a");
		text = text.replace("é", "e");
		text = text.replace("í", "i");
		text = text.replace('ó', 'o');
		text = text.replace("ú", "u");
		text = text.replace("Á", "A");
		text = text.replace("É", "E");
		text = text.replace("Í", "I");
		text = text.replace("Ó", "O");
		text = text.replace("Ú", "U");
		text = StringEscapeUtils.escapeJava(text);
		text = StringEscapeUtils.escapeXml(text);
		return text;
	}

	public static DataType convertType(int sqlType) {
		if (sqlType == Types.TINYINT || sqlType == Types.SMALLINT
				|| sqlType == Types.INTEGER || sqlType == Types.BIGINT) {
			return DataType.INTEGER;
		} else if (sqlType == Types.REAL || sqlType == Types.FLOAT
				|| sqlType == Types.DOUBLE || sqlType == Types.NUMERIC
				|| sqlType == Types.DECIMAL) {
			return DataType.DOUBLE;
		} else if (sqlType == Types.CHAR || sqlType == Types.VARCHAR
				|| sqlType == Types.LONGVARCHAR || sqlType == Types.CLOB) {
			return DataType.STRING;
		} else if (sqlType == Types.DATE) {
			return DataType.DATE;
		} else if (sqlType == Types.TIME) {
			return DataType.TIME;
		} else if (sqlType == Types.TIMESTAMP) {
			return DataType.TIMESTAMP;
		} else if (sqlType == Types.BOOLEAN) {
			return DataType.BOOLEAN;
		} else {
			/*
			 * Types.BINARY Types.VARBINARY Types.LONGVARBINARY Types.NULL
			 * Types.OTHER Types.JAVA_OBJECT Types.DISTINCT Types.STRUCT
			 * Types.ARRAY Types.BLOB Types.REF Types.DATALINK Types.BIT
			 */
			throw new UnsupportedOperationException("Conversion of type "
					+ sqlType + " is not supported.");
		}
	}

	public static Type hibernateType(int sqlType, int scale) {
		if (sqlType == Types.TINYINT || sqlType == Types.SMALLINT
				|| sqlType == Types.INTEGER || sqlType == Types.BIGINT) {
			return Hibernate.INTEGER;
		} else if (sqlType == Types.REAL || sqlType == Types.FLOAT
				|| sqlType == Types.DOUBLE) {
			return Hibernate.DOUBLE;
		} else if (sqlType == Types.CHAR || sqlType == Types.VARCHAR
				|| sqlType == Types.LONGVARCHAR || sqlType == Types.CLOB) {
			return Hibernate.STRING;
		} else if (sqlType == Types.DATE) {
			return Hibernate.DATE;
		} else if (sqlType == Types.TIME) {
			return Hibernate.TIME;
		} else if (sqlType == Types.TIMESTAMP) {
			return Hibernate.TIMESTAMP;
		} else if (sqlType == Types.BOOLEAN) {
			return Hibernate.BOOLEAN;
		} else if (sqlType == Types.DECIMAL || sqlType == Types.NUMERIC) {
			if (scale == 0) {
				return Hibernate.INTEGER;
			} 
			return Hibernate.DOUBLE;
		} else
			/*
			 * Types.BINARY Types.VARBINARY Types.LONGVARBINARY Types.NULL
			 * Types.OTHER Types.JAVA_OBJECT Types.DISTINCT Types.STRUCT
			 * Types.ARRAY Types.BLOB Types.REF Types.DATALINK Types.BIT
			 */
			throw new UnsupportedOperationException("Conversion of type "
					+ sqlType + " is not supported.");
	}
}
