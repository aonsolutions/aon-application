package net.aonsolutions.occam.api.config;

import java.io.Serializable;
import java.util.Date;
import java.util.Optional;

import net.aonsolutions.occam.api.HasDirtyFlag;
import net.aonsolutions.occam.api.constants.AonStatus;
import net.aonsolutions.watson.server.AonObjectUtils;

public class Booking implements Serializable,HasDirtyFlag<Booking> {

	private static final long serialVersionUID = 1103864813445453030L;
	
	private Date expirationDate;
	private String owner;
	
	private boolean domainManagement;
	private boolean disableDomainManagement;
	private Integer maxDefinedUsers;
	private Integer aonCustomer;
	private AonStatus aonStatus;
	private boolean dirty;
	
	public Optional<Date> getExpirationDate() {
		return Optional.ofNullable(expirationDate);  
	}
	public Booking setExpirationDate(Date expirationDate) {
		this.dirtyMark( AonObjectUtils.notEquals(this.expirationDate,expirationDate) );
		this.expirationDate = expirationDate;
		return this;
	}

	public String getOwner() {
		return owner;
	}
	public Booking setOwner(String owner) {
		this.dirtyMark( AonObjectUtils.notEquals(this.owner,owner) );
		this.owner = owner;
		return this;
	}

	public boolean isDomainManagement() {
		return domainManagement;
	}
	public Booking setDomainManagement(boolean domainManagement) {
		this.dirtyMark( AonObjectUtils.notEquals(this.domainManagement,domainManagement) );
		this.domainManagement = domainManagement;
		return this;
	}
	
	public boolean isDisableDomainManagement() {
		return disableDomainManagement;
	}
	public Booking setDisableDomainManagement(boolean disableDomainManagement) {
		this.dirtyMark( AonObjectUtils.notEquals(this.disableDomainManagement,disableDomainManagement) );
		this.disableDomainManagement = disableDomainManagement;
		return this;
	}
	
	public Optional<Integer> getMaxDefinedUsers() {
		return Optional.ofNullable(maxDefinedUsers);
	}
	public Booking setMaxDefinedUsers(Integer maxDefinedUsers) {
		this.dirtyMark( AonObjectUtils.notEquals(this.maxDefinedUsers,maxDefinedUsers) );
		this.maxDefinedUsers = maxDefinedUsers;
		return this;
	}
	
	public Optional<Integer> getAonCustomer() {
		return Optional.ofNullable(aonCustomer);
	}
	public Booking setAonCustomer(Integer aonCustomer) {
		this.dirtyMark( AonObjectUtils.notEquals(this.aonCustomer,aonCustomer) );
		this.aonCustomer = aonCustomer;
		return this;
	}
	
	public AonStatus getAonStatus() {
		return aonStatus;
	}
	public Booking setAonStatus(AonStatus aonStatus) {
		this.dirtyMark( AonObjectUtils.notEquals(this.aonStatus,aonStatus) );
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