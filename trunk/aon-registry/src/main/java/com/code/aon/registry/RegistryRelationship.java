package com.code.aon.registry;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;

import com.code.aon.common.ITransferObject;

/**
 * Transfer Object that represents the relationship between two registries.
 * 
 * @author Consulting & Development. Eugenio Castellano - 21-feb-2007
 * @since 1.0
 */
@Entity
@Table(name = "rrelationship")
public class RegistryRelationship implements ITransferObject {

	private static final long serialVersionUID = -363214031769789728L;

	/** The id. */
	private Integer id;

	/** The registry. */
	private Registry registry;

	/** The related registry. */
	private Registry relatedRegistry;

	/** The type of the relationship. */
	private Relationship relationship;

	/** The comments. */
	private String comments;

	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
	@Id
	@GeneratedValue
	public Integer getId() {
		return id;
	}

	/**
	 * Sets the id.
	 * 
	 * @param id
	 *            the id
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * Gets the registry.
	 * 
	 * @return the registry
	 */
	@ManyToOne
	@JoinColumn(name = "registry", nullable = false)
	public Registry getRegistry() {
		return registry;
	}

	/**
	 * Sets the registry.
	 * 
	 * @param registry
	 *            the registry
	 */
	public void setRegistry(Registry registry) {
		this.registry = registry;
	}

	/**
	 * Gets the related registry.
	 * 
	 * @return the related registry
	 */
	@ManyToOne
	@JoinColumn(name = "related_registry", nullable = false)
	public Registry getRelatedRegistry() {
		return relatedRegistry;
	}

	/**
	 * Sets the registry.
	 * 
	 * @param registry
	 *            the registry
	 */
	public void setRelatedRegistry(Registry relatedRegistry) {
		this.relatedRegistry = relatedRegistry;
	}

	/**
	 * Gets the type of the relationship.
	 * 
	 * @return the type of the relationship
	 */
	@ManyToOne
	@JoinColumn(name = "relationship")
	public Relationship getRelationship() {
		return relationship;
	}

	/**
	 * Sets the type of the relationship.
	 * 
	 * @param relationship
	 *            the type of the relationship.
	 */
	public void setRelationship(Relationship relationship) {
		this.relationship = relationship;
	}

	/**
	 * Gets the comments.
	 * 
	 * @return the comments
	 */
	@Column(length = 64)
	public String getComments() {
		return comments;
	}

	/**
	 * Sets the comments.
	 * 
	 * @param comments
	 *            the comments
	 */
	public void setComments(String comments) {
		this.comments = comments;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
    		return super.equals(obj);
		}
		if (obj instanceof RegistryRelationship) {
			RegistryRelationship o = (RegistryRelationship) obj;
			if (o.getId() == null && id == null) {
				return super.equals(obj);	
			}
			if (ObjectUtils.equals(getId(), o.getId())) {
				return true;
			}
		}
		return false;
	}

	@Override
    public int hashCode() {
        return id != null ? this.getClass().hashCode() + id.hashCode() : super.hashCode();
    }
    
}