package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

public enum NoticeType implements Serializable {
	CALL,
	VISIT,
	MESSAGE,
    COMMUNICATION,
    ISSUE,
    TICKET,
    WARNING, //aviso
    NOTE,
    COMMENT; //comentario de notice

	public byte value() {
		return (byte) this.ordinal();
	}
	
	public String getValue(){
		return this.toString();
	}
}
