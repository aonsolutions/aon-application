package com.esferalia.aon.occam.impl.jooq.console;

import org.jooq.Schema;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Domain;


public class ConsoleConnectionParams {
	
	private String schemaName;
	private Schema schema;
	private AONContext ctx;
	private String domainName;
	private Domain fullDomain;
	
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
	
	public String getDomainName() {
		return domainName;
	}
	public ConsoleConnectionParams setDomainName(String domainName) {
		this.domainName = domainName;
		return this;
	}
	
	public Domain getFullDomain() {
		return fullDomain;
	}
	public ConsoleConnectionParams setFullDomain(Domain fullDomain) {
		this.fullDomain = fullDomain;
		return this;
	}
}
