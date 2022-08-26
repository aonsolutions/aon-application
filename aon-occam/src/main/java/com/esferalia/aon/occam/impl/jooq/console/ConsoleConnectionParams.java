package com.esferalia.aon.occam.impl.jooq.console;

import java.util.Properties;

import org.jooq.DSLContext;
import org.jooq.Schema;


public class ConsoleConnectionParams {
	
	private Schema schema;
	private DSLContext dslContext;
	private Properties connectionProperties;
		
	public Schema getSchema() {
		return schema;
	}
	public ConsoleConnectionParams setSchema(Schema schema) {
		this.schema = schema;
		return this;
	}

	public DSLContext getDslContext() {
		return dslContext;
	}
	public ConsoleConnectionParams setDslContext(DSLContext dslContext) {
		this.dslContext = dslContext;
		return this;
	}
	
	public Properties getConnectionProperties() {
		return connectionProperties;
	}
	public ConsoleConnectionParams setConnectionProperties(Properties connectionProperties) {
		this.connectionProperties = connectionProperties;
		return this;
	}
	
}
