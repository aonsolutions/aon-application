/**
 * 
 */
package com.code.aon.jaas.auth.session;

import java.util.Date;

import com.code.aon.jaas.auth.AuthPrincipal;

/**
 * @author Consulting & Development. Iñaki Ayerbe - 16/05/2007
 *
 */
public class SessionInfo {

	private String sessionId;
	private long creationTime;
	private long lastAccessedTime;
	private int maxInactiveInterval;	
	private boolean expired = false;
	private AuthPrincipal principal;

	/**
	 * @param sessionId
	 * @param creationTime
	 * @param lastAccessedTime
	 * @param maxInactiveInterval
	 * @param principal
	 */
	public SessionInfo(String sessionId, long creationTime, long lastAccessedTime, 
			int maxInactiveInterval, AuthPrincipal principal) {
		super();
		this.sessionId = sessionId;
		this.creationTime = creationTime;
		this.lastAccessedTime = lastAccessedTime;
		this.maxInactiveInterval = maxInactiveInterval;
		this.principal = principal;
	}

	/**
	 * @return the sessionId
	 */
	public String getSessionId() {
		return sessionId;
	}

	/**
	 * @param sessionId the sessionId to set
	 */
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	/**
	 * @return the lastAccessedTime
	 */
	public long getLastAccessedTime() {
		return lastAccessedTime;
	}

	/**
	 * @param lastAccessedTime the lastAccessedTime to set
	 */
	public void setLastAccessedTime(long lastAccessedTime) {
		this.lastAccessedTime = lastAccessedTime;
	}

	/**
	 * @return the creationTime
	 */
	public long getCreationTime() {
		return creationTime;
	}

	/**
	 * @param creationTime the creationTime to set
	 */
	public void setCreationTime(long creationTime) {
		this.creationTime = creationTime;
	}

	/**
	 * @return the maxInactiveInterval
	 */
	public int getMaxInactiveInterval() {
		return maxInactiveInterval;
	}

	/**
	 * @param maxInactiveInterval the maxInactiveInterval to set
	 */
	public void setMaxInactiveInterval(int maxInactiveInterval) {
		this.maxInactiveInterval = maxInactiveInterval;
	}

	/**
	 * @return the expired
	 */
	public boolean isExpired() {
		return expired;
	}

	/**
	 * @param expired the expired to set
	 */
	public void setExpired(boolean expired) {
		this.expired = expired;
	}

	/**
	 * @return the principal
	 */
	public AuthPrincipal getPrincipal() {
		return principal;
	}

	/**
	 * @param principal the principal to set
	 */
	public void setPrincipal(AuthPrincipal principal) {
		this.principal = principal;
	}

	/**
	 * Refreshes the internal lastAccessedTime to the current date and time.
	 */
	public void refreshLastRequest() {
		this.lastAccessedTime = new Date().getTime();
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append( "SessionInfo[creationTime:" );sb.append( creationTime );sb.append( ";" );
		sb.append( "lastAccessedTime:" );sb.append( lastAccessedTime );sb.append( ";" );
		sb.append( "maxInactiveInterval:" );sb.append( maxInactiveInterval );sb.append( ";" );
		sb.append( "expired:" );sb.append( expired );sb.append( ";" );
		sb.append( "shortName:" + principal.getShortName() + ";" );
		sb.append( "domain:" + principal.getDomain() + "]" );
		return sb.toString();
	}

}
