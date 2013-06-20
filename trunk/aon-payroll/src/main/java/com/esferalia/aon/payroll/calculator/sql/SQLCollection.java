package com.esferalia.aon.payroll.calculator.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import com.esferalia.aon.payroll.calculator.AbstractIterator;

public abstract class SQLCollection<E> extends AbstractIterator<E> {

	public SQLCollection() {
	}

	protected SQLCollection(ResultSet resultSet) {
		this.resultSet = resultSet;
	}

	// -------------------------------------------
	// Iterator<IContractPayment>
	// -------------------------------------------

	protected ResultSet resultSet;

	@Override
	public boolean hasNext() {
		try {
			return this.resultSet.next();
		} catch (SQLException e) {
			return false; // TODO Relanzar como RuntimeException ???
		}
	}

	public ResultSet getResultSet() {
		return resultSet;
	}

	public void setResultSet(ResultSet resultSet) {
		this.resultSet = resultSet;
	}

	protected String getString(String columnLabel) {
		try {
			return this.resultSet.getString(columnLabel);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	protected String getString(String... columnLabels) {
		for (String columnLabel : columnLabels) {
			String value = getString(columnLabel);
			if (value != null && !value.isEmpty()) {
				return value;
			}
		}
		return null;
	}

	protected Integer getInt(String columnLabel) {
		try {
			Object value = this.resultSet.getObject(columnLabel);
			return value == null ? null : this.resultSet.getInt(columnLabel);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	protected Integer getInt(String... columnLabels) {
		for (String columnLabel : columnLabels) {
			Integer value = getInt(columnLabel);
			if (value != null) {
				return value;
			}
		}
		return null;
	}

	protected Date getDate(String columnLabel) {
		try {
			return this.resultSet.getDate(columnLabel);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	protected <T extends Enum<T>> T getEnum(String columnLabel,
			Class<T> enumType) {
		Integer ordinal = getInt(columnLabel);
		if (ordinal == null || ordinal < 0)
			return null;
		T constants[] = enumType.getEnumConstants();
		if (ordinal >= constants.length)
			return null;
		return constants[ordinal];
	}

	protected void close() throws SQLException {
		if (this.resultSet != null) {
			this.resultSet.close();
		}
	}
}
