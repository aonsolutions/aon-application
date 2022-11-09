package com.esferalia.aon.occam.api.model.registry;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Relationship;

public class RegistryRelationship implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private Integer id;
	private Domain domain;
	private Integer registry; 
	private Integer relatedRegistry; // Identificador de la Persona o Empresa relacionada
	private Relationship relationship; // Identificador del Tipo de Relación
	private String comments;
	
	private boolean removed;
	
	public RegistryRelationship() {
   // TODO document why this constructor is empty
    }

	public Integer getId() {
		return id;
	}

	public RegistryRelationship setId(Integer id) {
		this.id = id;
		return this;
	}

	public Domain getDomain() {
		return domain;
	}

	public RegistryRelationship setDomain(Domain domain) {
		this.domain = domain;
		return this;
	}

	public Integer getRegistry() {
		return registry;
	}

	public RegistryRelationship setRegistry(Integer registry) {
		this.registry = registry;
		return this;
	}

	public Integer getRelatedRegistry() {
		return relatedRegistry;
	}

	public RegistryRelationship setRelatedRegistry(Integer relatedRegistry) {
		this.relatedRegistry = relatedRegistry;
		return this;
	}

	public Relationship getRelationship() {
		if(relationship==null)
			relationship = new Relationship();
		return relationship;
	}

	public RegistryRelationship setRelationship(Relationship relationship) {
		this.relationship = relationship;
		return this;
	}

	public String getComments() {
		return comments;
	}

	public RegistryRelationship setComments(String comments) {
		this.comments = comments;
		return this;
	}	
	
	public boolean isRemoved() {
		return removed;
	}

	public RegistryRelationship setRemoved(boolean removed) {
		this.removed = removed;
		return this;
	}	
}
