package com.esferalia.aon.occam.api.model.fiscal.mod200_2015;

import java.io.Serializable;

public abstract class Variable2015<T> implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Mod2002015Key key;
	private boolean changedByUser;
	
	public Variable2015( ) {
		
	}
	public Variable2015( Mod2002015Key key ) {
		this.key = key;
	}
	
	public Mod2002015Key getKey() {
		return key;
	}
	public void setKey(Mod2002015Key key) {
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
	public abstract Variable2015<T> clone();
	public abstract T getValue();
	public abstract void setValue(T value);
	
	
}
