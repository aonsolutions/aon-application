package com.esferalia.aon.occam.impl.jooq.console;

import org.jooq.Schema;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Domain;


public class ConsoleConnectionParams {
	
	private String schemaName;
	private Schema schema;
	private AONContext ctx;
	private Domain domain;
	
	public String getSchemaName() {
		return schemaName;
	}
	public ConsoleConnectionParams setSchemaName(String schemaName) {
		this.schemaName = schemaName;
		return this;
	}
		
	public Schema getSchema() {
		return schema;
	}
	public ConsoleConnectionParams setSchema(Schema schema) {
		this.schema = schema;
		return this;
	}

	public AONContext getAONContext() {
		return ctx;
	}
	public ConsoleConnectionParams setAONContext(AONContext ctx) {
		this.ctx = ctx;
		return this;
	}
	
	public Domain getDomain() {
		return domain;
	}
	public ConsoleConnectionParams setDomain(Domain domain) {
		this.domain = domain;
		return this;
	}
	
	public Integer getDomainId() {
		return getDomain()==null?null:getDomain().getId();
	}
	public String getDomainName() {
		return getDomain()==null?null:getDomain().getName();
	}
	
}
