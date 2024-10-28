package net.aonsolutions.occam.api.model;

import java.io.Serializable;
import java.util.Optional;

public class RegistryPayMethod implements Serializable {
	
	private static final long serialVersionUID = 2913140560827128658L;
	
	private boolean deleted;
	
	private Integer id;
	private Integer domain;
	private Integer registry;
	private PayMethod payMethod;
	private RegistryBank registryBank;
	private short numberOfPymnts;
	private short daysToFirstPymnt;
	private short daysBetweenPymnts;
	private String pymntDays;
	
	
	public RegistryPayMethod() {
		this.numberOfPymnts = 1;
		this.daysToFirstPymnt = 0;
		this.daysBetweenPymnts = 0;
		this.pymntDays = "";
	}
	
	public boolean isDeleted() {
		return deleted;
	}
	public RegistryPayMethod setDeleted(boolean deleted) {
		this.deleted = deleted;
		return this;
	}
	public void delete() {
		setDeleted(true);
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
		this.domain = domain;
		return this;
	}
	
	public Integer getRegistry() {
		return registry;
	}
	public RegistryPayMethod setRegistry(Integer registry) {
		this.registry = registry;
		return this;
	}
	
	public Optional<PayMethod> getPayMethod() {
		return Optional.ofNullable(payMethod);
	}
	public RegistryPayMethod setPayMethod(PayMethod payMethod) {
		this.payMethod = payMethod;
		return this;
	}
	
	public Optional<RegistryBank> getRegistryBank() {
		return Optional.ofNullable(registryBank);
	}
	public RegistryPayMethod setRegistryBank(RegistryBank registryBank) {
		this.registryBank = registryBank;
		return this;
	}
	
	public short getNumberOfPymnts() {
		return numberOfPymnts;
	}
	public RegistryPayMethod setNumberOfPymnts(short numberOfPymnts) {
		this.numberOfPymnts = numberOfPymnts;
		return this;
	}
	
	public short getDaysToFirstPymnt() {
		return daysToFirstPymnt;
	}
	public RegistryPayMethod setDaysToFirstPymnt(short daysToFirstPymnt) {
		this.daysToFirstPymnt = daysToFirstPymnt;
		return this;
	}
	
	public short getDaysBetwenPymnts() {
		return daysBetweenPymnts;
	}
	public RegistryPayMethod setDaysBetwenPymnts(short daysBetweenPymnts) {
		this.daysBetweenPymnts = daysBetweenPymnts;
		return this;
	}
	
	public String getPymntDays() {
		return pymntDays;
	}
	public RegistryPayMethod setPymntDays(String pymntDays) {
		this.pymntDays = pymntDays;
		return this;
	}
	
}
