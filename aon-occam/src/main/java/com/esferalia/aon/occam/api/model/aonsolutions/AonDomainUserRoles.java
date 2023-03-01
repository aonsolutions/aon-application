package com.esferalia.aon.occam.api.model.aonsolutions;

import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.json.DomainJSON;
import com.esferalia.aon.occam.api.json.UserJSON;
import com.esferalia.aon.occam.api.model.IJsonNames;

public class AonDomainUserRoles extends DomainUserRoles{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AonDomainUserRoles(DomainUserRoles dur) {
		super();
		setDomain(dur.getDomain());
		setParentDomain(dur.getParentDomain());
		setUser(dur.getUser());
		setDomainApps(dur.getDomainApps());
		setDomainUserRoles(dur.getDomainUserRoles());
		setParentDomainApps(dur.getParentDomainApps());
		setParentDomainUserRoles(dur.getParentDomainUserRoles());
		
		setOldDomainModules(dur.getOldDomainModules());
		setOldParentDomainModules(dur.getOldParentDomainModules());

	}
	
	public JSONObject toJSON() {
		JSONObject json = new JSONObject();

		JSONArray domainApps = new JSONArray();
		JSONArray parentDomainApps = new JSONArray();
		JSONArray domainUserRoles = new JSONArray();
		JSONArray parentDomainUserRoles = new JSONArray();
		JSONArray oldDomainModules = new JSONArray();
		JSONArray oldParentDomainModules = new JSONArray();
		JSONArray oldUserRoles = new JSONArray();

		getDomainApps().forEach(r -> domainApps.put(r.name()));
		getParentDomainApps().forEach(r -> parentDomainApps.put(r.name()));
		getDomainUserRoles().forEach(r -> domainUserRoles.put(r.name()));
		getParentDomainUserRoles().forEach(r -> parentDomainUserRoles.put(r.name()));
		getOldDomainModules().forEach(r -> oldDomainModules.put(r.name()));
		getOldParentDomainModules().forEach(r -> oldParentDomainModules.put(r.name()));
	
		if(getUser() != null && getUser().getUserRoles() != null) {
			Arrays.asList(getUser().getUserRoles()).stream().forEach(r -> oldUserRoles.put(r.name()));
		}
		json.put(IJsonNames.USER, UserJSON.toJSON(getUser()));
		json.put(IJsonNames.DOMAIN, DomainJSON.toJSON(getDomain()));
		json.put(IJsonNames.PARENT_DOMAIN, DomainJSON.toJSON(getParentDomain()));
		json.put(IJsonNames.MAX_DEFINED_USERS, getDomain().getMaxDefinedUsers());
		json.put(IJsonNames.DEFINED_USERS, getDomain().getDefinedUsers());
		
		json.put("parentUser", isParentUser());
		json.put("domainApps", domainApps);
		json.put("oldDomainModules", oldDomainModules);
		json.put("oldParentDomainModules", oldParentDomainModules);

		json.put("parentDomainApps", parentDomainApps);
		json.put("domainUserRoles", domainUserRoles);
		json.put("parentDomainUserRoles", parentDomainUserRoles);

		json.put("oldUserRoles", oldUserRoles);
		return json;
	}
}
