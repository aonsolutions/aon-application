package com.esferalia.aon.occam.api.model.stat;

import java.io.Serializable;

public class SelectableEnum<E extends Enum<E>> implements Serializable {

	private static final long serialVersionUID = 407306684560037880L;
	
	private E type;
	private boolean selected;

	public SelectableEnum() {
		
	}
	public SelectableEnum(E type) {
		setType(type);
	}
	
	public E getType() {
		return type;
	}
	public SelectableEnum<E> setType(E type) {
		this.type = type;
		return this;
	}
	
	public boolean isSelected() {
		return selected;
	}

	public SelectableEnum<E> setSelected(boolean selected) {
		this.selected = selected;
		return this;
	}
	
	

}
