package com.esferalia.aon.occam.api.model.console;

import java.io.Serializable;

public class ConsoleDomainMessage implements Serializable {
	
	private static final long serialVersionUID = -3303922083871181409L;
	
	private ConsoleDomainMessageType type;
	private ConsoleDomainMessageFixType fixType;
	private String schema;
	private Integer domainId;
	private String table;
	private Integer pkId;
	private String pkCode;
	private String fkTable;
	private String fkColumn;
	private Integer fkId;
	private Integer wrongDomainId;
	private String message;
	
	public ConsoleDomainMessageType getType() {
		return type;
	}
	public ConsoleDomainMessage setType(ConsoleDomainMessageType type) {
		this.type = type;
		return this;
	}

	public ConsoleDomainMessageFixType getFixType() {
		return fixType;
	}
	public ConsoleDomainMessage setFixType(ConsoleDomainMessageFixType fixType) {
		this.fixType = fixType;
		return this;
	}
	
	public String getSchema() {
		return schema;
	}
	public ConsoleDomainMessage setSchema(String schema) {
		this.schema = schema;
		return this;
	}
	
	public Integer getDomainId() {
		return domainId;
	}
	public ConsoleDomainMessage setDomainId(Integer domainId) {
		this.domainId = domainId;
		return this;
	}
	
	public String getTable() {
		return table;
	}
	public ConsoleDomainMessage setTable(String table) {
		this.table = table;
		return this;
	}
	
	public Integer getPkId() {
		return pkId;
	}
	public ConsoleDomainMessage setPkId(Integer pkId) {
		this.pkId = pkId;
		return this;
	}
	
	public String getPkCode() {
		return pkCode;
	}
	public ConsoleDomainMessage setPkCode(String pkCode) {
		this.pkCode = pkCode;
		return this;
	}
	
	public String getFkTable() {
		return fkTable;
	}
	public ConsoleDomainMessage setFkTable(String fkTable) {
		this.fkTable = fkTable;
		return this;
	}
	
	public String getFkColumn() {
		return fkColumn;
	}
	public ConsoleDomainMessage setFkColumn(String fkColumn) {
		this.fkColumn = fkColumn;
		return this;
	}
	
	public Integer getFkId() {
		return fkId;
	}
	public ConsoleDomainMessage setFkId(Integer fkId) {
		this.fkId = fkId;
		return this;
	}
	
	public Integer getWrongDomainId() {
		return wrongDomainId;
	}
	public ConsoleDomainMessage setWrongDomainId(Integer wrongDomainId) {
		this.wrongDomainId = wrongDomainId;
		return this;
	}
	
	public String getMessage() {
		return message;
	}
	public ConsoleDomainMessage setMessage(String message) {
		this.message = message;
		return this;
	}
	
	
}
