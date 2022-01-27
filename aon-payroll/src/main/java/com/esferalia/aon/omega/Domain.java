package com.esferalia.aon.omega;

import java.util.List;

public class Domain {
	
	Integer domain;				// Domain Id (se rellena en el proceso)
	Integer parentDomain;		// Parent domain Id (ver como se gestiona esto por que hace falta el SCOPE)
	Integer registry;			// Registry Id (se rellena en el proceso)
	Integer scope;				// Scope Id (ver como gestionar esto...)
	
	String document;			// CIF
	String name;				// URL de acceso
	String description;			// Descripcion dominio
	
	List<Workplace> workplaces;	// Centros de trabajo
	List<Activity> activities;	// Actividades
	List<Contract> contracts;	// Contratos
	
	protected Domain() {
		super();
	}

	public Integer getDomain() {
		return domain;
	}

	public Domain setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}

	public Integer getParentDomain() {
		return parentDomain;
	}

	public Domain setParentDomain(Integer parentDomain) {
		this.parentDomain = parentDomain;
		return this;
	}

	public Integer getRegistry() {
		return registry;
	}

	public Domain setRegistry(Integer registry) {
		this.registry = registry;
		return this;
	}

	public Integer getScope() {
		return scope;
	}

	public Domain setScope(Integer scope) {
		this.scope = scope;
		return this;
	}

	public String getDocument() {
		return document;
	}

	public Domain setDocument(String document) {
		this.document = document;
		return this;
	}

	public String getName() {
		return name;
	}

	public Domain setName(String name) {
		this.name = name;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public Domain setDescription(String description) {
		this.description = description;
		return this;
	}

	public List<Workplace> getWorkplaces() {
		return workplaces;
	}

	public Domain setWorkplaces(List<Workplace> workplaces) {
		this.workplaces = workplaces;
		return this;
	}

	public List<Activity> getActivities() {
		return activities;
	}

	public Domain setActivities(List<Activity> activities) {
		this.activities = activities;
		return this;
	}

	public List<Contract> getContracts() {
		return contracts;
	}

	public Domain setContracts(List<Contract> contracts) {
		this.contracts = contracts;
		return this;
	}
	
	
	
}
