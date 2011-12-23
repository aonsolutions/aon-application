package com.code.aon.audit;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.esferalia.aon.entity.master.ApplicationDB;

@Entity
@Table(name="application", uniqueConstraints = @UniqueConstraint(columnNames="name"))
public class Application extends ApplicationDB {

	private static final long serialVersionUID = 1L;

	private List<Action> actions = new LinkedList<Action>();

	@OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE }, mappedBy = "application")
	@org.hibernate.annotations.Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE,
			org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
	public List<Action> getActions() {
		return this.actions;
	}

	public void setActions(List<Action> actions) {
		this.actions = actions;
	}

}