package com.code.aon.common.domain;

import java.util.LinkedList;
import java.util.List;

public abstract class AbstractDomainSwitcher implements IDomainSwitcher {

	private List<IDomainChangeListener> listeners;
	protected Integer domainId;
	
	@Override
	public Integer getDomainId() {
		return domainId;
	}

	@Override
	public void setDomainId(Integer domainId) {
		Integer oldDomainId = this.domainId;
		fireBeforeDomainChanged(oldDomainId, domainId);
		this.domainId = domainId;
		fireAfterDomainChanged(oldDomainId, domainId);
	}

	@Override
	public void addDomainChangeListener( IDomainChangeListener listener) {
		if (listeners == null) {
			listeners = new LinkedList<IDomainChangeListener>();
		}
		listeners.add(listener);
	}
	
	@Override
	public void fireBeforeDomainChanged(Integer oldDomain, Integer newDomain) {
		if (listeners != null) {
			for (IDomainChangeListener listener : listeners) {
				DomainEvent event = new DomainEvent(this, oldDomain, newDomain);
				listener.beforeDomainChanged(event);
			}
		}
	}
	
	@Override
	public void fireAfterDomainChanged(Integer oldDomain, Integer newDomain) {
		if (listeners != null) {
			for (IDomainChangeListener listener : listeners) {
				DomainEvent event = new DomainEvent(this, oldDomain, newDomain);
				listener.afterDomainChanged(event);
			}
		}
	}

	
}
