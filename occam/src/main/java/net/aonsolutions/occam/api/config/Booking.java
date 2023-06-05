package net.aonsolutions.occam.api.config;

import java.util.Date;
import java.util.Objects;
import java.util.Optional;

import net.aonsolutions.occam.api.OccamEntity;
import net.aonsolutions.occam.api.AonNames;
import net.aonsolutions.occam.api.constants.AonStatus;
import net.aonsolutions.watson.server.AonObjectUtils;

public class Booking extends OccamEntity {

	private static final long serialVersionUID = 1103864813445453030L;
	
	private Integer id;
	private Date expirationDate;
	private String owner;
	
	private boolean domainManagement;
	private boolean disableDomainManagement;
	private Integer maxDefinedUsers;
	private Integer aonCustomer;
	private AonStatus aonStatus;
	
	@Override
	protected Object getUuid() {
		return getId();
	}
	@Override
	public Booking markAsClean() {
		super.markAsClean();
		return this;
	}
	
	public Integer getId() {
		return id;
	}
	public Booking setId(Integer id) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.id,id), () -> markAsDirty(AonNames.ID));
		this.id = id;
		return this;
	}

	public Optional<Date> getExpirationDate() {
		return Optional.ofNullable(expirationDate);  
	}
	public Booking setExpirationDate(Date expirationDate) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.expirationDate,expirationDate), () -> markAsDirty(AonNames.EXPIRATION_DATE));
		this.expirationDate = expirationDate;
		return this;
	}

	public String getOwner() {
		return owner;
	}
	public Booking setOwner(String owner) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.owner,owner), () -> markAsDirty(AonNames.OWNER));
		this.owner = owner;
		return this;
	}

	public boolean isDomainManagement() {
		return domainManagement;
	}
	public Booking setDomainManagement(boolean domainManagement) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.domainManagement,domainManagement), () -> markAsDirty(AonNames.DOMAIN_MANAGEMENT));
		this.domainManagement = domainManagement;
		return this;
	}
	
	public boolean isDisableDomainManagement() {
		return disableDomainManagement;
	}
	public Booking setDisableDomainManagement(boolean disableDomainManagement) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.disableDomainManagement,disableDomainManagement), () -> markAsDirty(AonNames.DISABLE_DOMAIN_MANAGEMENT));
		this.disableDomainManagement = disableDomainManagement;
		return this;
	}
	
	public Optional<Integer> getMaxDefinedUsers() {
		return Optional.ofNullable(maxDefinedUsers);
	}
	public Booking setMaxDefinedUsers(Integer maxDefinedUsers) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.maxDefinedUsers,maxDefinedUsers), () -> markAsDirty(AonNames.MAX_DEFINED_USERS));
		this.maxDefinedUsers = maxDefinedUsers;
		return this;
	}
	
	public Optional<Integer> getAonCustomer() {
		return Optional.ofNullable(aonCustomer);
	}
	public Booking setAonCustomer(Integer aonCustomer) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.aonCustomer,aonCustomer), () -> markAsDirty(AonNames.AON_CUSTOMER));
		this.aonCustomer = aonCustomer;
		return this;
	}
	
	public AonStatus getAonStatus() {
		return aonStatus;
	}
	public Booking setAonStatus(AonStatus aonStatus) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.aonStatus,aonStatus), () -> markAsDirty(AonNames.AON_STATUS));
		this.aonStatus = aonStatus;
		return this;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj instanceof Booking other) {
			return AonObjectUtils.equals( this.getUuid(),other.getUuid() );
		}
	    return false;
	}
	
	@Override
	public int hashCode() {
	    return 31 * 7 + Objects.requireNonNullElse(getUuid(), 0).hashCode();
	}
	
}