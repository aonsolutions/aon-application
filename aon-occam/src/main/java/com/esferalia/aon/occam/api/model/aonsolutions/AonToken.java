package com.esferalia.aon.occam.api.model.aonsolutions;

import org.json.JSONObject;

public class AonToken {

	private static final String SCHEMA = "schema";
	private static final String SCHEMA_FIRST_DOMAIN = "schema_first_domain";
	private static final String UUID = "uuid";

	private String schema;
	private String schemaFirstDomain;
	private String uuid;
	private byte[] auth;

	public String getSchema() {
		return schema;
	}

	public AonToken setSchema(String schema) {
		this.schema = schema;
		return this;
	}
	
	public String getSchemaFirstDomain() {
		return schemaFirstDomain;
	}

	public AonToken setSchemaFirstDomain(String schemaFirstDomain) {
		this.schemaFirstDomain = schemaFirstDomain;
		return this;
	}

	public String getUuid() {
		return uuid;
	}

	public AonToken setUuid(String uuid) {
		this.uuid = uuid;
		return this;
	}

	public byte[] getAuth() {
		return auth;
	}

	public AonToken setAuth(byte[] auth) {
		this.auth = auth;
		return this;
	}

	public JSONObject toJson() {
		return new JSONObject()
				.put(SCHEMA, getSchema())
				.put(SCHEMA_FIRST_DOMAIN, getSchemaFirstDomain())
				.put(UUID, getUuid());
	}

	public static AonToken parse(String json) {
		return parse(new JSONObject(json));
	}

	public static AonToken parse(JSONObject json) {
		return new AonToken()
				.setSchema(json.getString(SCHEMA))
				.setUuid(json.getString(UUID))
				.setSchemaFirstDomain(json.getString(SCHEMA_FIRST_DOMAIN));
	}

}