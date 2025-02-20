package com.esferalia.aon.occam.api.model;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.occam.api.model.console.ConsoleSchema;

public class DomainParams implements Serializable {

	private static final long serialVersionUID = -8674079205943551908L;
	
	private String dbSchema;
	private Integer id;
	private String query;
	private String name;
	private String description;
	private Integer type;
	private Integer parent;
	private Boolean orphan;	
	private Boolean active;
	private Boolean enableHeredity;
	private Boolean domainManagement;
	private Date  fromLastAccess;
	private Date toLastAccess;
	private Date fromExpirationDate;
	private Date toExpirationDate;
	private boolean advancedMode;
	
	private boolean validate;
	private boolean mustFlatten;
	
	private int limit = 50;
	private int[] schemasOffsets = new int[ConsoleSchema.values().length];
//	private int offset;
	
	public DomainParams() {
		initializeOffsets();
	}

	public String getDbSchema() {
		return dbSchema;
	}
	public DomainParams setDbSchema(String dbSchema) {
		this.dbSchema = dbSchema;
		return this;
	}
	
	public Integer getId() {
		return id;
	}
	public DomainParams setId(Integer id) {
		this.id = id;
		return this;
	}
	
	public String getName() {
		return name;
	}
	public DomainParams setName(String name) {
		this.name = name;
		return this;
	}
	
	public String getDescription() {
		return description;
	}
	public DomainParams setDescription(String description) {
		this.description = description;
		return this;
	}
	public String getQuery() {
		return query;
	}
	public DomainParams setQuery(String query) {
		this.query = query;
		return this;
	}

	public Integer getType() {
		return type;
	}
	public DomainParams setType(Integer type) {
		this.type = type;
		return this;
	}
	
	public Integer getParent() {
		return parent;
	}
	public DomainParams setParent(Integer parent) {
		this.parent = parent;
		return this;
	}
	
	public Boolean getOrphan() {
		return orphan;
	}
	public DomainParams setOrphan(Boolean orphan) {
		this.orphan = orphan;
		return this;
	}
	
	public Boolean getActive() {
		return active;
	}
	public DomainParams setActive(Boolean active) {
		this.active = active;
		return this;
	}

	public Boolean getEnableHeredity() {
		return enableHeredity;
	}
	public DomainParams setEnableHeredity(Boolean enableHeredity) {
		this.enableHeredity = enableHeredity;
		return this;
	}

	public Boolean getDomainManagement() {
		return domainManagement;
	}
	public DomainParams setDomainManagement(Boolean domainManagement) {
		this.domainManagement = domainManagement;
		return this;
	}

	public Date getFromLastAccess() {
		return fromLastAccess;
	}
	public DomainParams setFromLastAccess(Date fromLastAccess) {
		this.fromLastAccess = fromLastAccess;
		return this;
	}

	public Date getToLastAccess() {
		return toLastAccess;
	}
	public DomainParams setToLastAccess(Date toLastAccess) {
		this.toLastAccess = toLastAccess;
		return this;
	}

	public Date getFromExpirationDate() {
		return fromExpirationDate;
	}
	public DomainParams setFromExpirationDate(Date fromExpirationDate) {
		this.fromExpirationDate = fromExpirationDate;
		return this;
	}

	public Date getToExpirationDate() {
		return toExpirationDate;
	}
	public DomainParams setToExpirationDate(Date toExpirationDate) {
		this.toExpirationDate = toExpirationDate;
		return this;
	}
	
	public boolean isValidate() {
		return validate;
	}
	public DomainParams setValidate(boolean validate) {
		this.validate = validate;
		return this;
	}
	public boolean mustFlatten() {
		return mustFlatten;
	}
	public DomainParams setMustFlatten(boolean mustFlatten) {
		this.mustFlatten = mustFlatten;
		return this;
	}
	
	public int getLimit() {
		return limit;
	}
	public DomainParams setLimit(int limit) {
		this.limit = limit;
		return this;
	}
	public int[] getSchemasOffsets() {
		return schemasOffsets;
	}
	public DomainParams setSchemasOffsets(int[] schemasOffsets) {
		this.schemasOffsets = schemasOffsets;
		return this;
	}
	public int getOffset( ConsoleSchema cs ) {
		if (cs == null) throw new IllegalArgumentException("ConsoleSchema is mandatory");
		return schemasOffsets[ cs.ordinal() ]; 
	}
	public int setOffset( ConsoleSchema cs, int offset ) {
		if (cs == null) throw new IllegalArgumentException("ConsoleSchema is mandatory");
		schemasOffsets[ cs.ordinal() ] = offset;
		return schemasOffsets[ cs.ordinal() ]; 
	}
	public void initializeOffsets( ) {
		this.schemasOffsets = new int[ConsoleSchema.values().length];
		for ( int i = 0; i < schemasOffsets.length; i++ ) schemasOffsets[i] = 0;
	}
	public int addOffset( ConsoleSchema cs ) {
		return addOffset(cs, 1);
	}
	public int addOffset( ConsoleSchema cs, int increment ) {
		if (cs == null) throw new IllegalArgumentException("ConsoleSchema is mandatory");
		schemasOffsets[ cs.ordinal() ] = schemasOffsets[ cs.ordinal() ] + increment;
		return schemasOffsets[ cs.ordinal() ]; 
	}
	
	public boolean isAdvancedMode() {
		return advancedMode;
	}
	public DomainParams setAdvancedMode(boolean advancedMode) {
		this.advancedMode = advancedMode;
		return this;
	}
	
	
	
}
