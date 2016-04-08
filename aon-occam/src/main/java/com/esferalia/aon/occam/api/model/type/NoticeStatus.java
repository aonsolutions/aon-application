package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

public enum NoticeStatus implements Serializable {
	CLOSED,
	REOPEN,
	OPEN,
	DUPLICATED,
	FAQ;
	
	public byte value() {
		return (byte) this.ordinal();
	}
	
	public String getValue(){
		return this.toString();
	}
	
	public static boolean isOpen( String name ) {
		return name != null && name.equals(OPEN.getValue());
	}

	public static boolean isReopen( String name ) {
		return name != null && name.equals(REOPEN.getValue());
	}
	
	/**
	 * 
	 * @param name Nombre de la etiqueta que se desea evaluar
	 * @return boolan isOpen || isReopen
	 */

	public static boolean isOpened( String name ) {
		return name != null && ( isOpen(name) || isReopen(name) );
	}
	
	public static boolean isClosed( String name ) {
		return name != null && name.equals(CLOSED.getValue());
	}
}
