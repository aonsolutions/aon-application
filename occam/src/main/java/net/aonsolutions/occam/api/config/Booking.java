package net.aonsolutions.occam.api.config;

import java.io.Serializable;
import java.util.Date;
import java.util.Optional;

import com.esferalia.aon.watson.util.AonUtils;

import net.aonsolutions.occam.api.HasDirtyFlag;
import net.aonsolutions.occam.api.constants.AonStatus;

public class Booking implements Serializable,HasDirtyFlag<Booking> {

	private static final long serialVersionUID = 1103864813445453030L;
	
	private Date expirationDate;
	private String owner;
	
	private Boolean domainManagement;
	private Boolean disableDomainManagement;
	private Integer maxDefinedUsers;
	private Integer aonCustomer;
	private AonStatus aonStatus;
	private boolean dirty;
	
	public Optional<Date> getExpirationDate() {
		return Optional.ofNullable(expirationDate);  
	}
	public Booking setExpirationDate(Date expirationDate) {
		this.dirtyMark( AonUtils.notEquals(this.expirationDate,expirationDate) );
		this.expirationDate = expirationDate;
		return this;
	}

	public Optional<String> getOwner() {
		return Optional.ofNullable(owner);
	}
	public Booking setOwner(String owner) {
		this.dirtyMark( AonUtils.notEquals(this.owner,owner) );
		this.owner = owner;
		return this;
	}

	public Optional<Boolean> isDomainManagement() {
		return Optional.ofNullable(domainManagement);
	}
	public Booking setDomainManagement(Boolean domainManagement) {
		this.dirtyMark( AonUtils.notEquals(this.domainManagement,domainManagement) );
		this.domainManagement = domainManagement;
		return this;
	}
	
	public Optional<Boolean> isDisableDomainManagement() {
		return Optional.ofNullable(disableDomainManagement);
	}
	public Booking setDisableDomainManagement(Boolean disableDomainManagement) {
		this.dirtyMark( AonUtils.notEquals(this.disableDomainManagement,disableDomainManagement) );
		this.disableDomainManagement = disableDomainManagement;
		return this;
	}
	
	public Optional<Integer> getMaxDefinedUsers() {
		return Optional.ofNullable(maxDefinedUsers);
	}
	public Booking setMaxDefinedUsers(Integer maxDefinedUsers) {
		this.dirtyMark( AonUtils.notEquals(this.maxDefinedUsers,maxDefinedUsers) );
		this.maxDefinedUsers = maxDefinedUsers;
		return this;
	}
	
	public Optional<Integer> getAonCustomer() {
		return Optional.ofNullable(aonCustomer);
	}
	public Booking setAonCustomer(Integer aonCustomer) {
		this.dirtyMark( AonUtils.notEquals(this.aonCustomer,aonCustomer) );
		this.aonCustomer = aonCustomer;
		return this;
	}
	
	public Optional<AonStatus> getAonStatus() {
		return Optional.ofNullable(aonStatus);
	}
	public Booking setAonStatus(AonStatus aonStatus) {
		this.dirtyMark( AonUtils.notEquals(this.aonStatus,aonStatus) );
		this.aonStatus = aonStatus;
		return this;
	}

	@Override
	public boolean isDirty() {
		return dirty;
	}
	@Override
	public Booking setDirty(boolean dirty) {
		this.dirty = dirty;
		return this;
	}

}