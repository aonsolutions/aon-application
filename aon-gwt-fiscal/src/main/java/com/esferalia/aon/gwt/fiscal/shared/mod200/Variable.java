package com.esferalia.aon.gwt.fiscal.shared.mod200;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

public abstract class Variable<T> implements Serializable, IsSerializable {

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
