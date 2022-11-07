package com.esferalia.aon.occam.api.model.aonsolutions;

import java.io.Serializable;
import java.util.Date;

import org.json.JSONObject;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.security.Auth;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class AonToken implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Deprecated
	private String schema;
	@Deprecated
	private String schemaFirstDomain;
	private String uuid;
	private byte[] auth;
	private boolean expired;
	
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

	public boolean isExpired() {
		return expired;
	}
	
	public AonToken setExpired(boolean expired) {
		this.expired = expired;
		return this;
	}
	
	public JSONObject toJson() {
		return new JSONObject()
				.put(IJsonNames.SCHEMA, getSchema())
				.put(IJsonNames.SCHEMA_FIRST_DOMAIN, getSchemaFirstDomain())
				.put(IJsonNames.UUID, getUuid());
	}

	public static AonToken parse(String json) {
		return parse(new JSONObject(json));
	}

	public static AonToken parse(JSONObject json) {
		return new AonToken()
				.setSchema(JsonUtils.getString(json, IJsonNames.SCHEMA))
				.setUuid(JsonUtils.getString(json, IJsonNames.UUID))
				.setSchemaFirstDomain(JsonUtils.getString(json, IJsonNames.SCHEMA_FIRST_DOMAIN))
				.setExpired(JsonUtils.getboolean(json, "expired"));
	}
	
	@Deprecated
	public static String build(Auth auth, Date expireDate) {
		return build(auth, expireDate,	AonStringUtils.isBlank(auth.getSchema())
				? "" :	AONContext.getSchemaFirstDomain(auth.getSchema()));
	}
	
	@Deprecated
	public static String build(Auth auth, Date expireDate, String domain) {
		JSONObject tokenObject = new JSONObject();
		tokenObject
			.put(IJsonNames.SCHEMA, auth.getSchema())
			.put(IJsonNames.SCHEMA_FIRST_DOMAIN, domain)
			.put(IJsonNames.UUID, auth.getUuid());
		return build(tokenObject, expireDate);
	}
	
	@Deprecated
	public static String build(User user, Date expireDate, String domain) {
		JSONObject tokenObject = new JSONObject();
		tokenObject
			.put(IJsonNames.SCHEMA_FIRST_DOMAIN, domain)
			.put(IJsonNames.USER, user.getId())
			.put(IJsonNames.LOGIN, user.getLogin())
			.put(IJsonNames.DOMAIN, user.getDomain());
		return build(tokenObject, expireDate);
	}
	
	public static String build(String uuid, Date expireDate) {
        JSONObject tokenObject = new JSONObject();
        tokenObject.put(IJsonNames.UUID, uuid);
        return build(tokenObject, expireDate);
    }
	
	public static String build(String uuid, Date expireDate, JSONObject data) {
	    data.put(IJsonNames.UUID, uuid);
	    return build(data, expireDate);
	}
	
	public static String build(JSONObject object, Date expireDate) {
        String token = "";
        try {
            Algorithm algorithm = Algorithm.HMAC256("aonsecret");
            expireDate = expireDate != null ? expireDate : AonDateUtils.addMonths(new Date(), 3);
            token = JWT.create()
                    .withIssuer("auth0")
                    .withSubject(object.toString())
                    .withIssuedAt(new Date())
                    .withExpiresAt(expireDate)
                    .sign(algorithm);
        } catch (JWTCreationException exception) {
            exception.printStackTrace();
            throw exception;
        }
        return token;
    }   
	
}