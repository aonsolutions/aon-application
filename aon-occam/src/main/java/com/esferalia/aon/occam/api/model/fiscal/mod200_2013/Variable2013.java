package com.esferalia.aon.occam.api.model.fiscal.mod200_2013;

import java.io.Serializable;

public abstract class Variable2013<T> implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Mod2002013Key key;
	private boolean changedByUser;
	
	public Variable2013( ) {
		
	}
	public Variable2013( Mod2002013Key key ) {
		this.key = key;
	}
	
	public Mod2002013Key getKey() {
		return key;
	}
	public void setKey(Mod2002013Key key) {
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
	public abstract Variable2013<T> clone();
	public abstract T getValue();
	public abstract void setValue(T value);
	
	
}
