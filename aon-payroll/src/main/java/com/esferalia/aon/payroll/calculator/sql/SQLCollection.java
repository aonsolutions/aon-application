package com.esferalia.aon.payroll.calculator.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;


public abstract class SQLCollection <E>  implements Collection<E>, Iterator<E> {
	
	protected SQLCollection() {
	}

	protected SQLCollection(ResultSet resultSet) {
		this.resultSet = resultSet;
	}
	
	@Override
	public boolean add(E e) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean contains(Object o) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isEmpty() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Iterator<E> iterator() {
		return this;
	}

	@Override
	public boolean remove(Object o) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int size() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object[] toArray() {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		throw new UnsupportedOperationException();
	}

	//-------------------------------------------
	// Iterator<IContractPayment>
	//-------------------------------------------
	
	protected ResultSet resultSet;
	
	@Override
	public boolean hasNext() {
		try {
			return this.resultSet.next();
		} catch (SQLException e) {
			return false; // TODO Relanzar como RuntimeException ???
		}
	}


	@Override
	public void remove() {
		throw new UnsupportedOperationException();
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
	
	protected Integer getInt(String columnLabel) {
		try {
			Object value = this.resultSet.getObject(columnLabel);
			return value == null? null : this.resultSet.getInt(columnLabel);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	protected void close() throws SQLException{
		if (this.resultSet != null ){
			this.resultSet.close();
		}
	}
}
