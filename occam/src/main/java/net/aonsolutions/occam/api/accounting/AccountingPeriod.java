package net.aonsolutions.occam.api.accounting;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

import net.aonsolutions.occam.api.HasDirtyFlag;
import net.aonsolutions.occam.api.HasSelector;
import net.aonsolutions.occam.api.config.Audit;
import net.aonsolutions.occam.api.constants.AccountingPeriodStatus;
import net.aonsolutions.watson.client.util.AonNumberUtils;
import net.aonsolutions.watson.server.AonObjectUtils;

public class AccountingPeriod implements Serializable, HasSelector<AccountingPeriod>,HasDirtyFlag<AccountingPeriod>{

	private static final long serialVersionUID = -1686197262238560337L;
	
	private Integer id;
	private Integer domain;
	private String name;
	private Date startDate;
	private Date endingDate;
	private AccountingPeriodStatus status;
	private boolean defaultPeriod;
	private Audit audit;
	
	private boolean dirty;
	private boolean selected;


	public Integer getId() {
		return this.id;
	}
	public AccountingPeriod setId(Integer id) {
		this.dirtyMark( AonObjectUtils.notEquals(this.id,id) );
		this.id = id;
		return this; 
	}

	public Integer getDomain() {
		return this.domain;
	}
	public AccountingPeriod setDomain(Integer domain) {
		this.dirtyMark( AonObjectUtils.notEquals(this.domain,domain) );
		this.domain = domain;
		return this; 
	}

	public String getName() {
		return this.name;
	}
	public AccountingPeriod setName(String name) {
		this.dirtyMark( AonObjectUtils.notEquals(this.name,name) );
		this.name = name;
		return this; 
	}

	public Date getStartDate() {
		return this.startDate;
	}
	public AccountingPeriod setStartDate(Date startDate) {
		this.dirtyMark( AonObjectUtils.notEquals(this.startDate,startDate) );
		this.startDate = startDate;
		return this; 
	}

	public Date getEndingDate() {
		return this.endingDate;
	}
	public AccountingPeriod setEndingDate(Date endingDate) {
		this.dirtyMark( AonObjectUtils.notEquals(this.endingDate,endingDate) );
		this.endingDate = endingDate;
		return this; 
	}

	public AccountingPeriodStatus getStatus() {
		return this.status;
	}
	public AccountingPeriod setStatus(AccountingPeriodStatus status) {
		this.dirtyMark( AonObjectUtils.notEquals(this.status,status) );
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
	public boolean isDirty() {
		return dirty;
	}
	@Override
	public AccountingPeriod setDirty(boolean dirty) {
		this.dirty = dirty;
		return this;
	}

	@Override
	public boolean isSelected() {
		return selected;
	}
	@Override
	public AccountingPeriod setSelected(boolean selected) {
		this.selected = selected;
		return this;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj instanceof AccountingPeriod other) {
			return AonNumberUtils.equals(this.id,other.id);
		}
	    return false;
	}
	
	@Override
	public int hashCode() {
	    return 31 * 7 + Objects.requireNonNullElse(id, 0).hashCode();
	}
}
