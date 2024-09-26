package com.esferalia.aon.occam.api.json;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.aonsolutions.DomainUserRoles;

public class DomainUserRolesJSON {
	
	private DomainUserRolesJSON() {
		
	}
	
	public static DomainUserRoles fromJSON(JSONObject json) {
		if(json == null) return new DomainUserRoles();
		return new DomainUserRoles()
			.setDomain(DomainJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.DOMAIN)))
			.setParentDomain(DomainJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.PARENT_DOMAIN)))
			.setUser(UserJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.USER)))
		
			.setDomainApps(AonAppJSON.fromJSON(JsonUtils.getJSONArray(json, IJsonNames.DOMAIN_APPS)))
			.setParentDomainApps(AonAppJSON.fromJSON(JsonUtils.getJSONArray(json, IJsonNames.PARENT_DOMAIN_APPS)))
			.setOldDomainModules(ModuleJSON.fromJSON(JsonUtils.getJSONArray(json, IJsonNames.OLD_DOMAIN_MODULES)))
			.setOldParentDomainModules(ModuleJSON.fromJSON(JsonUtils.getJSONArray(json, IJsonNames.OLD_PARENT_DOMAIN_MODULES)))
			.setDomainUserRoles(AonRoleJSON.fromJSON(JsonUtils.getJSONArray(json, IJsonNames.DOMAIN_USER_ROLES)))
			.setParentDomainUserRoles(AonRoleJSON.fromJSON(JsonUtils.getJSONArray(json, IJsonNames.PARENT_DOMAIN_USER_ROLES)))
			.setDomainPayer(JsonUtils.getboolean(json, IJsonNames.DOMAIN_PAYER));
		
	}
	
	
	public static JSONObject toJSON(DomainUserRoles object) {
		if(object == null) return new JSONObject();
		return new JSONObject()
			.put(IJsonNames.DOMAIN, DomainJSON.toJSON(object.getDomain()))
			.put(IJsonNames.PARENT_DOMAIN, DomainJSON.toJSON(object.getParentDomain()))
			.put(IJsonNames.USER, UserJSON.toJSON(object.getUser()))
			.put(IJsonNames.DOMAIN_APPS, AonAppJSON.toJSON(object.getDomainApps()))
			.put(IJsonNames.PARENT_DOMAIN_APPS, AonAppJSON.toJSON(object.getParentDomainApps()))
			.put(IJsonNames.OLD_DOMAIN_MODULES, ModuleJSON.toJSON(object.getOldDomainModules()))
			.put(IJsonNames.OLD_PARENT_DOMAIN_MODULES, ModuleJSON.toJSON(object.getOldParentDomainModules()))
			.put(IJsonNames.DOMAIN_USER_ROLES, AonRoleJSON.toJSON(object.getDomainUserRoles()))
			.put(IJsonNames.PARENT_DOMAIN_USER_ROLES, AonRoleJSON.toJSON(object.getParentDomainUserRoles()))
			.put(IJsonNames.DOMAIN_PAYER, object.isDomainPayer());
	}
}
