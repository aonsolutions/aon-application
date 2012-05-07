package com.code.aon.common.domain;

import java.util.EventObject;

public class DomainEvent extends EventObject {
	
	private static final long serialVersionUID = 8548597086493958180L;
	
	private IDomainSwitcher source;
	private Integer oldDomain;
	private Integer newDomain;
	
	public DomainEvent(IDomainSwitcher source, Integer oldDomain, Integer newDomain) {
		super(source);
		this.oldDomain = oldDomain;
		this.newDomain = newDomain;
	}

	public IDomainSwitcher getDomainSwitcher() {
		return source;
	}

	public Integer getOldDomain() {
		return oldDomain;
	}

	public Integer getNewDomain() {
		return newDomain;
	}


}
