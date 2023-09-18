package com.code.aon.audit;

import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.SessionDB;

@Entity
@Table(name = "session")
public class Session extends SessionDB {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private Set<ActionEntry> actionEntries;

	@OneToMany(mappedBy = "session", cascade={CascadeType.REMOVE})
	public Set<ActionEntry> getActionEntries() {
		return this.actionEntries;
	}
	
	public void setActionEntries( Set<ActionEntry> actionEntries ) {
		this.actionEntries = actionEntries;
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.append("application", getApplication().getId())
			.append("domain", getDomain())
			.append("endDate", getEndDate())
			.append("remoteAddress", getRemoteAddress())
			.append("remoteHost", getRemoteHost())
			.append("sessionId", getSessionId())
			.append("startDate", getStartDate())
			.toString();
	}
	
}