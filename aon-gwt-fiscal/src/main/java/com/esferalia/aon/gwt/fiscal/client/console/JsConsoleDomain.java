package com.esferalia.aon.gwt.fiscal.client.console;

import java.util.Date;

import com.esferalia.aon.gwt.common.client.AonDateUtils;
import com.esferalia.aon.occam.api.model.type.DomainType;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.JavaScriptObject;

public class JsConsoleDomain extends JavaScriptObject {
	
	protected JsConsoleDomain() {
	}
	
	private final native Integer id() /*-{
		return this.id;
	}-*/;
	
	public final Integer getId(){
		return AonNumberUtils.toInteger("" +  id());
	}
	
	public final native String getName() /*-{
		return this.name;
	}-*/;
	
	public final native String getDescription() /*-{
		return this.description;
	}-*/;

	public final native String getOwner() /*-{
		return this.owner;
	}-*/;
	
	public final native Integer getParentId() /*-{
		return this.parentId;
	}-*/;
	
	public final native String getDomainTypeString() /*-{
		return this.domainType;
	}-*/;
	public final DomainType getDomainType() {
		return DomainType.safeValueOf(getDomainTypeString());
	}
	
	public final native boolean isEnableHeredity() /*-{
		return this.enableHeredity;
	}-*/;
	
	public final native boolean isDomainManagement() /*-{
		return this.domainManagement;
	}-*/;
	
	public final native boolean isDisableDomainManagement() /*-{
		return this.disableDomainManagement;
	}-*/;
	
	public final native boolean isActive() /*-{
		return this.active;
	}-*/;
	public final native void setActive(boolean active) /*-{
		this.active = active;
	}-*/;

	public final native Integer getScope() /*-{
		return this.scope;
	}-*/;
	
	public final native Integer getMaxDefinedUsers() /*-{
		return this.maxDefinedUsers;
	}-*/;
	
	public final native Integer getDefinedUsers() /*-{
		return this.definedUsers;
	}-*/;
	
	public final native Integer getMaxDocumentSize() /*-{
		return this.maxDocumentSize;
	}-*/;

	public final native Integer getMaxTotalDocumentSize() /*-{
		return this.maxTotalDocumentSize;
	}-*/;
	
	public final native String getLastAccessUser() /*-{
		return this.lastAccessUser;
	}-*/;

	public final native String getLastAccessDateString() /*-{
		return this.lastAccessDate;
	}-*/;
	public final Date getLastAccessDate() {
		return AonStringUtils.mapIfNotBlank(getLastAccessDateString(), AonDateUtils::parseDateTime );
	}
	
	public final native String getExpirationDateString() /*-{
		return this.expirationDate;
	}-*/;
	public final Date getExpirationDate() {
		return AonStringUtils.mapIfNotBlank(getExpirationDateString(), AonDateUtils::parseDateTime );
	}
	
	public final native String getCreationUser() /*-{
		return this.creationUser;
	}-*/;
	
	public final native String getCreationDateString() /*-{
		return this.creationDate;
	}-*/;
	public final Date getCreationDate() {
		return AonStringUtils.mapIfNotBlank(getCreationDateString(), AonDateUtils::parseDateTime );
	}
	
	public final native String getModificationUser() /*-{
		return this.modificationUser;
	}-*/;
	
	public final native String getModificationDateString() /*-{
		return this.modificationDate;
	}-*/;
	public final Date getModificationDate() {
		return AonStringUtils.mapIfNotBlank(getModificationDateString(), AonDateUtils::parseDateTime );
	}
	
	public final native Integer getChildCount() /*-{
		return this.childCount;
	}-*/;
	public final native Integer getActiveChildCount() /*-{
		return this.activeChildCount;
	}-*/;

	public final boolean isStandalone() {
		return getParentId() == null && !isDomainManagement();
	}
	public final boolean isParent() {
		return getParentId() == null && isDomainManagement();
	}

	public final boolean hasChild() {
		if (getChildCount() == null) return false;
		Integer cc = AonNumberUtils.toInteger("" + getChildCount());
		return cc.intValue() > 0;
	}

	public final native boolean isRemoteAccessEnabled() /*-{
		return this.remoteAccessEnabled;
	}-*/;
	
}
