package net.aonsolutions.occam.api.accounting;

import java.util.Date;
import java.util.Objects;
import java.util.Optional;

import net.aonsolutions.occam.api.AonNames;
import net.aonsolutions.occam.api.OccamEntity;
import net.aonsolutions.occam.api.config.Audit;
import net.aonsolutions.occam.api.constants.AccountingPeriodStatus;
import net.aonsolutions.watson.server.AonObjectUtils;

public class AccountingPeriod extends OccamEntity {

	private static final long serialVersionUID = -1686197262238560337L;
	
	private Integer id;
	private Integer domain;
	private String name;
	private Date startDate;
	private Date endDate;
	private AccountingPeriodStatus status;
	private boolean defaultPeriod;
	private Audit audit;
	

	@Override
	protected Object getUuid() {
		return getId();
	}
	@Override
	public AccountingPeriod markAsClean() {
		super.markAsClean();
		return this;
	}

	public Integer getId() {
		return this.id;
	}
	public AccountingPeriod setId(Integer id) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.id,id), () -> markAsDirty(AonNames.ID));
		this.id = id;
		return this; 
	}

	public Integer getDomain() {
		return this.domain;
	}
	public AccountingPeriod setDomain(Integer domain) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.domain,domain), () -> markAsDirty(AonNames.DOMAIN));
		this.domain = domain;
		return this; 
	}

	public String getName() {
		return this.name;
	}
	public AccountingPeriod setName(String name) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.name,name), () -> markAsDirty(AonNames.NAME));
		this.name = name;
		return this; 
	}

	public Date getStartDate() {
		return this.startDate;
	}
	public AccountingPeriod setStartDate(Date startDate) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.startDate,startDate), () -> markAsDirty(AonNames.START_DATE));
		this.startDate = startDate;
		return this; 
	}

	public Date getEndDate() {
		return this.endDate;
	}
	public AccountingPeriod setEndDate(Date endDate) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.endDate,endDate), () -> markAsDirty(AonNames.END_DATE));
		this.endDate = endDate;
		return this; 
	}

	public AccountingPeriodStatus getStatus() {
		return this.status;
	}
	public AccountingPeriod setStatus(AccountingPeriodStatus status) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.status,status), () -> markAsDirty(AonNames.STATUS));
		this.status = status;
		return this; 
	}
	
	public boolean isDefaultPeriod() {
		return defaultPeriod;
	}
	public AccountingPeriod setDefaultPeriod(boolean defaultPeriod) {
		this.defaultPeriod = defaultPeriod;
		return this;
	}
	
	public Optional<Audit> getAudit() {
		return Optional.ofNullable(audit);
	}
	public AccountingPeriod setAudit(Audit audit) {
		this.audit = audit;
		return this;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj instanceof AccountingPeriod other) {
			return AonObjectUtils.equals( this.getUuid(),other.getUuid() );
		}
	    return false;
	}
	
	@Override
	public int hashCode() {
	    return 31 * 7 + Objects.requireNonNullElse(getUuid(), 0).hashCode();
	}
	
}
