package com.esferalia.aon.gwt.fiscal.client.customer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;

/** Fila diagnosticada que devuelve /ms/api/domain/sig-integrity. */
public class SigIntegrityRow {

    private Integer customerId;
    private Integer domainId;
    private String  domainName;
    private String  schema;

    private boolean found;
    private boolean domainActive;
    private Date    domainExpirationDate;
    private Integer domainAonCustomer;
    private boolean domainAccessible;

    private boolean aligned;
    private boolean targetDomainActive;
    private Date    targetDate;
    private boolean domainNeedsUpdate;
    private boolean customerNeedsUpdate;
    private boolean aonCustomerMismatch;
    private String  diagnosis;

    public static List<SigIntegrityRow> parseArray(String responseBody) {
        List<SigIntegrityRow> result = new ArrayList<SigIntegrityRow>();

        JSONArray array = JSONParser.parseStrict(responseBody).isArray();
        if (null == array) return result;

        for (int i = 0; i < array.size(); i++) {
            JSONObject json = array.get(i).isObject();
            if (null != json) result.add(parse(json));
        }
        return result;
    }

    private static SigIntegrityRow parse(JSONObject json) {
        SigIntegrityRow row = new SigIntegrityRow();
        row.customerId           = integer(json, "customer");
        row.domainId             = integer(json, "domainId");
        row.domainName           = string(json, "domainName");
        row.schema               = string(json, "schema");
        row.found                = bool(json, "found");
        row.domainActive         = bool(json, "domainActive");
        row.domainExpirationDate = date(json, "domainExpirationDate");
        row.domainAonCustomer    = integer(json, "domainAonCustomer");
        row.domainAccessible     = bool(json, "domainAccessible");
        row.aligned              = bool(json, "aligned");
        row.targetDomainActive   = bool(json, "targetDomainActive");
        row.targetDate           = date(json, "targetDate");
        row.domainNeedsUpdate    = bool(json, "domainNeedsUpdate");
        row.customerNeedsUpdate  = bool(json, "customerNeedsUpdate");
        row.aonCustomerMismatch  = bool(json, "aonCustomerMismatch");
        row.diagnosis            = string(json, "diagnosis");
        return row;
    }

    private static boolean bool(JSONObject json, String key) {
        JSONValue v = json.get(key);
        return null != v && null != v.isBoolean() && v.isBoolean().booleanValue();
    }

    private static String string(JSONObject json, String key) {
        JSONValue v = json.get(key);
        return (null == v || null == v.isString()) ? null : v.isString().stringValue();
    }

    private static Integer integer(JSONObject json, String key) {
        JSONValue v = json.get(key);
        return (null == v || null == v.isNumber()) ? null : Integer.valueOf((int) v.isNumber().doubleValue());
    }

    private static Date date(JSONObject json, String key) {
        JSONValue v = json.get(key);
        return (null == v || null == v.isNumber()) ? null : new Date((long) v.isNumber().doubleValue());
    }

    public Integer getCustomerId()           { return customerId; }
    public Integer getDomainId()             { return domainId; }
    public String  getDomainName()           { return domainName; }
    public String  getSchema()               { return schema; }
    public boolean isFound()                 { return found; }
    public boolean isDomainActive()          { return domainActive; }
    public Date    getDomainExpirationDate() { return domainExpirationDate; }
    public Integer getDomainAonCustomer()    { return domainAonCustomer; }
    public boolean isDomainAccessible()      { return domainAccessible; }
    public boolean isAligned()               { return aligned; }
    public boolean isTargetDomainActive()    { return targetDomainActive; }
    public Date    getTargetDate()           { return targetDate; }
    public boolean isDomainNeedsUpdate()     { return domainNeedsUpdate; }
    public boolean isCustomerNeedsUpdate()   { return customerNeedsUpdate; }
    public boolean isAonCustomerMismatch()   { return aonCustomerMismatch; }
    public String  getDiagnosis()            { return diagnosis; }
}