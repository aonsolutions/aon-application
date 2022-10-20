package com.esferalia.aon.occam.api.model.console;

import java.io.Serializable;
import java.util.LinkedHashMap;

public class ConsoleTableRow implements Serializable {
	
	private static final long serialVersionUID = 6999766472212215893L;
	
	private String schema;
	private String table;
	private Integer id;
	private Integer domain;
	private LinkedHashMap<String,ConsoleTableField> fields;
	
	public String getSchema() {
		return schema;
	}
	public ConsoleTableRow setSchema(String schema) {
		this.schema = schema;
		return this;
	}
	
	public String getTable() {
		return table;
	}
	public ConsoleTableRow setTable(String table) {
		this.table = table;
		return this;
	}
	
	public Integer getId() {
		return id;
	}
	public ConsoleTableRow setId(Integer id) {
		this.id = id;
		return this;
	}
	
	public Integer getDomain() {
		return domain;
	}
	public ConsoleTableRow setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	
	public LinkedHashMap<String,ConsoleTableField> getFields() {
		if (this.fields == null) setFields(new LinkedHashMap<>());
		return fields;
	}
	public ConsoleTableRow setFields(LinkedHashMap<String,ConsoleTableField> fields) {
		this.fields = fields;
		return this;
	}
	
	public void add(ConsoleTableField f) {
		getFields().put(f.getColumn(), f);
	}
	public ConsoleTableField getField( String key) {
		return getFields().get(key);
	}
}
