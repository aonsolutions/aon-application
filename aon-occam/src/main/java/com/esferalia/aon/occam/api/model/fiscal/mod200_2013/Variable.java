package com.esferalia.aon.occam.api.model.fiscal.mod200_2013;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.fiscal.mod200.IMod200Key;

public abstract class Variable<K extends IMod200Key,T> implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private K key;
	private boolean changedByUser;
	
	public Variable( ) {
		
	}
	public Variable( K key ) {
		this.key = key;
	}
	
	public K getKey() {
		return key;
	}
	public void setKey(K key) {
		this.key = key;
	}
//	public String getDescription() {
//		return getKey().getDescription();
//	}
	public boolean isChangedByUser() {
		return changedByUser;
	}
	public void setChangedByUser(boolean changedByUser) {
		this.changedByUser = changedByUser;
	}
	public abstract Variable<K,T> clone();
	public abstract T getValue();
	public abstract void setValue(T value);
	
	
}
