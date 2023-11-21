package com.esferalia.aon.occam.api.model.finance.nordigen;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.watson.util.AonStringUtils;

public class NordigenAccessToken implements Serializable {

	private static final long serialVersionUID = 2227685219198528965L;
	
	private String access;
	private Long accessExpires;
	private String refresh;
	private Long refreshExpires;
	
	private Date creationDate;
	private Date refreshDate;

	public String getAccess() {
		return access;
	}

	public NordigenAccessToken setAccess(String access) {
		this.access = access;
		return this;
	}

	public Long getAccessExpires() {
		return accessExpires;
	}

	public NordigenAccessToken setAccessExpires(Long accessExpires) {
		this.accessExpires = accessExpires;
		return this;
	}

	public String getRefresh() {
		return refresh;
	}

	public NordigenAccessToken setRefresh(String refresh) {
		this.refresh = refresh;
		return this;
	}

	public Long getRefreshExpires() {
		return refreshExpires;
	}

	public NordigenAccessToken setRefreshExpires(Long refreshExpires) {
		this.refreshExpires = refreshExpires;
		return this;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public NordigenAccessToken setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
		return this;
	}

	public Date getRefreshDate() {
		return refreshDate;
	}

	public NordigenAccessToken setRefreshDate(Date refreshDate) {
		this.refreshDate = refreshDate;
		return this;
	}
	
	public NordigenAccessToken refreshAccessToken(NordigenAccessToken refreshedToken) {
		if (refreshedToken != null ) {
			if (AonStringUtils.isNotBlank(refreshedToken.getAccess())) {
				this.setAccess(refreshedToken.getAccess());				
			}
			if (refreshedToken.getAccessExpires() != null && refreshedToken.getAccessExpires() != 0) {
				this.setAccessExpires(refreshedToken.getAccessExpires());
			}
			if (AonStringUtils.isNotBlank(refreshedToken.getRefresh())) {
				this.setRefresh(refreshedToken.getRefresh());				
			}
			if (refreshedToken.getRefreshExpires() != null && refreshedToken.getRefreshExpires() != 0) {
				this.setRefreshExpires(refreshedToken.getRefreshExpires());
			}
		}
		return this;
	}
}
