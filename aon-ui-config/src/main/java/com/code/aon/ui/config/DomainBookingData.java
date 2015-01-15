package com.code.aon.ui.config;

import org.apache.commons.io.FileUtils;

import com.code.aon.AonVersion;
import com.code.aon.config.enumeration.DomainType;

public class DomainBookingData extends DomainData {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private Integer activeUsers;
	
	private int maxDefinedUsers;
	
	private boolean aonOne;
	
	private boolean portal;
	
	private boolean dehOnline;
	
	private boolean tirant;
	
	private boolean dehOnlineConfigured;
	
	private DomainType type;
	
	private long maxTotalDocumentSize;
	
	public DomainBookingData(Integer id, String name, String description, boolean active, boolean enableHeredity,
			int maxDefinedUsers, Byte type, int maxTotalDocumentSize ) {
		super( id, name, description, active, enableHeredity );
		this.maxDefinedUsers = maxDefinedUsers;
		this.type = getType(type);
		long value = (maxTotalDocumentSize > 0) ? maxTotalDocumentSize : 100;
		this.maxTotalDocumentSize = value * FileUtils.ONE_MB;
		this.activeUsers = 0;
	}
	
	private DomainType getType( Byte value ) {
		DomainType type = DomainType.ENTERPRISE;
		DomainType[] values = DomainType.values();
		if ( (value != null) && (value >=0) && (value < values.length) ) {
			type = values[value];
		}
		return type;
	}

	public Integer getActiveUsers() {
		return activeUsers;
	}

	public void setActiveUsers(Integer activeUsers) {
		this.activeUsers = activeUsers;
	}

	public boolean isAonOne() {
		return aonOne;
	}

	public void setAonOne(boolean aonOne) {
		this.aonOne = aonOne;
	}

	public boolean isPortal() {
		return portal;
	}

	public void setPortal(boolean portal) {
		this.portal = portal;
	}

	public boolean isTirant() {
		return tirant;
	}

	public void setTirant(boolean tirant) {
		this.tirant = tirant;
	}

	public boolean isDehOnline() {
		return dehOnline;
	}

	public void setDehOnline(boolean dehOnline) {
		this.dehOnline = dehOnline;
	}
	
	public boolean isDehOnlineConfigured() {
		return dehOnlineConfigured;
	}

	public void setDehOnlineConfigured(boolean dehOnlineConfigured) {
		this.dehOnlineConfigured = dehOnlineConfigured;
	}

	public int getMaxDefinedUsers() {
		return maxDefinedUsers;
	}

	public DomainType getType() {
		return type;
	}

	public String getMaxTotalDocumentSize() {
		return FileUtils.byteCountToDisplaySize(maxTotalDocumentSize);
	}

}
