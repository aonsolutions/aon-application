package net.aonsolutions.occam.api.model;


import java.util.Objects;

import com.esferalia.aon.watson.util.AonObjectUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.occam.api.model.metadata.AccountMetadata;


public class Account extends AonEntity<AccountMetadata> {

	private static final long serialVersionUID = 3940903705871158256L;

	private Integer id;
	private Integer domain;
	private String code;
	private String description;
	private String alias;
	private boolean entryEnabled;
	private byte level;
	private boolean active;
	private String costCenter;
	
	@Override
	protected Object getUuid() {
		return getId();
	}
	@Override
	public Account markAsClean() {
		super.markAsClean();
		return this; 
	}
	
	@Override
	public Account setSelected( boolean selected) {
		super.setSelected(selected);
		return this; 
	}
	
	public Integer getId() {
		return id;
	}
	public Account setId(Integer id) {
		checkIfDirty( this.id,id, AccountMetadata.ID);
		this.id = id;
		return this;
	}

	public Integer getDomain() {
		return domain;
	}
	public Account setDomain(Integer domain) {
		checkIfDirty( this.domain,domain, AccountMetadata.DOMAIN);
		this.domain = domain;
		return this;
	}

	public String getCode() {
		return code;
	}
	public Account setCode(String code) {
		checkIfDirty( this.code,code, AccountMetadata.CODE);
		this.code = code;
		return this;
	}

	public String getDescription() {
		return description;
	}
	public Account setDescription(String description) {
		checkIfDirty( this.description,description, AccountMetadata.DESCRIPTION);
		this.description = description;
		return this;
	}

	public String getAlias() {
		return alias;
	}
	public Account setAlias(String alias) {
		checkIfDirty( this.alias, alias, AccountMetadata.ALIAS);
		this.alias = alias;
		return this;
	}

	public boolean isEntryEnabled() {
		return entryEnabled;
	}
	public Account setEntryEnabled(boolean entryEnabled) {
		checkIfDirty( this.entryEnabled, entryEnabled, AccountMetadata.ENTRY_ENABLED);
		this.entryEnabled = entryEnabled;
		return this;
	}

	public byte getLevel() {
		return level;
	}
	public Account setLevel(byte level) {
		checkIfDirty( this.level, level, AccountMetadata.LEVEL);
		this.level = level;
		return this;
	}

	public boolean isActive() {
		return active;
	}
	public Account setActive(boolean active) {
		checkIfDirty( this.active, active, AccountMetadata.ACTIVE);
		this.active = active;
		return this;
	}

	public String getCostCenter() {
		return costCenter;
	}
	public Account setCostCenter(String costCenter) {
		checkIfDirty( this.costCenter, costCenter, AccountMetadata.COST_CENTER);
		this.costCenter = costCenter;
		return this;
	}
	
	public String getFullName() {
		return (AonStringUtils.join(
			 AonStringUtils.defaultString(getCode())
			,AonStringUtils.isNotBlank(getDescription())
				?(AonStringUtils.SPACE + '-' + AonStringUtils.SPACE + getDescription())  
				:(AonStringUtils.EMPTY)
	   ));
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj instanceof Account other) {
			return AonObjectUtils.equals( this.getUuid(),other.getUuid() );
		}
	    return false;
	}
	
	@Override
	public int hashCode() {
	    return 31 * 7 + Objects.requireNonNullElse(getUuid(), 0).hashCode();
	}
}
