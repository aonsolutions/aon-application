package com.esferalia.aon.occam.api.model.fiscal.mod200_2014;

import java.io.Serializable;

public abstract class Variable2014<T> implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Mod2002014Key key;
	private boolean changedByUser;
	
	public Variable2014( ) {
		
	}
	public Variable2014( Mod2002014Key key ) {
		this.key = key;
	}
	
	public Mod2002014Key getKey() {
		return key;
	}
	public void setKey(Mod2002014Key key) {
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
	public abstract Variable2014<T> clone();
	public abstract T getValue();
	public abstract void setValue(T value);
	
	
}
