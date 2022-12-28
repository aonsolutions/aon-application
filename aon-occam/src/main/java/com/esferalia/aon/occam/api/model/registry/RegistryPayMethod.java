package com.esferalia.aon.occam.api.model.registry;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.finance.PayMethod;

public class RegistryPayMethod implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private Integer domain;
	private Integer registry;
	private PayMethod payMethod;
	private RegistryBank rbank;
	private Short numberOfPymnts;
	private Short daysToFirstPymnt;
	private Short daysBetweenPymnts;
	private String pymntDays;
	
	private boolean dirty;
	private boolean removed;
	
	public RegistryPayMethod() {
		this.numberOfPymnts = 1;
		this.daysToFirstPymnt = 0;
		this.daysBetweenPymnts = 0;
		this.pymntDays = "";
	}
	
	public Integer getId() {
		return id;
	}
	
	public RegistryPayMethod setId(Integer id) {
		this.id = id;
		return this;
	}
	
	public Integer getDomain() {
		return domain;
	}
	
	public RegistryPayMethod setDomain(Integer domain) {
		setDirty(true);
		this.domain = domain;
		return this;
	}
	
	public Integer getRegistry() {
		return registry;
	}
	
	public RegistryPayMethod setRegistry(Integer registry) {
		setDirty(true);
		this.registry = registry;
		return this;
	}
	
	public PayMethod getPayMethod() {
	    if(payMethod == null) {
	        payMethod = new PayMethod();
	    }
		return payMethod;
	}
	
	public RegistryPayMethod setPayMethod(PayMethod payMethod) {
		setDirty(true);
		this.payMethod = payMethod;
		return this;
	}
	
	public RegistryBank getRbank() {
	    if(rbank == null) {
	        rbank = new RegistryBank();
	    }
		return rbank;
	}
	
	public RegistryPayMethod setRbank(RegistryBank rbank) {
		setDirty(true);
		this.rbank = rbank;
		return this;
	}
	
	public Short getNumberOfPymnts() {
		if(numberOfPymnts == null) {
			numberOfPymnts = 1;
		}
		return numberOfPymnts;
	}
	
	public RegistryPayMethod setNumberOfPymnts(Short numberOfPymnts) {
		setDirty(true);
		this.numberOfPymnts = numberOfPymnts;
		return this;
	}
	
	public Short getDaysToFirstPymnt() {
		if(daysToFirstPymnt == null) {
			daysToFirstPymnt = 0;
		}
		return daysToFirstPymnt;
	}
	
	public RegistryPayMethod setDaysToFirstPymnt(Short daysToFirstPymnt) {
		setDirty(true);
		this.daysToFirstPymnt = daysToFirstPymnt;
		return this;
	}
	
	public Short getDaysBetwenPymnts() {
		if(daysBetweenPymnts == null) {
			daysBetweenPymnts = 0;
		}
		return daysBetweenPymnts;
	}
	
	public RegistryPayMethod setDaysBetwenPymnts(Short daysBetweenPymnts) {
		setDirty(true);
		this.daysBetweenPymnts = daysBetweenPymnts;
		return this;
	}
	
	public String getPymntDays() {
		if(pymntDays == null) {
			pymntDays = "";
		}
		return pymntDays;
	}
	
	public RegistryPayMethod setPymntDays(String pymntDays) {
		setDirty(true);
		this.pymntDays = pymntDays;
		return this;
	}
	
	public boolean isDirty() {
		return dirty;
	}
	
	public RegistryPayMethod setDirty(boolean dirty) {
		this.dirty = dirty;
		return this;
	}
	
	public boolean isRemoved() {
		return removed;
	}
	
	public RegistryPayMethod setRemoved(boolean removed) {
		this.removed = removed;
		return this;
	}
	
	public void remove() {
		setRemoved(true);
	}
	
}
