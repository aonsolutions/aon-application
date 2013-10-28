package com.code.aon.audit;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.esferalia.aon.entity.master.SessionDB;

@Entity
@Table(name = "session")
public class Session extends SessionDB {

	private static final long serialVersionUID = 1L;

	private Set<ActionEntry> actionEntries;

	@OneToMany(mappedBy = "session", cascade={CascadeType.REMOVE})
	public Set<ActionEntry> getActionEntries() {
		return this.actionEntries;
	}
	
	public void setActionEntries( Set<ActionEntry> actionEntries ) {
		this.actionEntries = actionEntries;
	}
	
}