package com.esferalia.aon.occam.api.model.fiscal.mod200;

import java.io.Serializable;

public abstract class Variable<T> implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Mod200Key key;
	private boolean changedByUser;
	
	public Variable( ) {
		
	}
	public Variable( Mod200Key key ) {
		this.key = key;
	}
	
	public Mod200Key getKey() {
		return key;
	}
	public void setKey(Mod200Key key) {
		this.key = key;
	}
	public String getDescription() {
		return getKey().getDescription();
	}
	public boolean isChangedByUser() {
		return changedByUser;
	}
	public void setChangedByUser(boolean changedByUser) {
		this.changedByUser = changedByUser;
	}
	public abstract Variable<T> clone();
	public abstract T getValue();
	public abstract void setValue(T value);
	
	
}
