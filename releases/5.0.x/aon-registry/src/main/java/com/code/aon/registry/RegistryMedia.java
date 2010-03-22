package com.code.aon.registry;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.registry.enumeration.MediaType;

/**
 * Transfer Object that represents the RegistryMedia.
 * 
 * @author Consulting & Development. Eugenio Castellano - 28-ene-2005
 * @since 1.0
 */
@Entity
@Table(name="rmedia")
public class RegistryMedia implements ITransferObject {

	private static final long serialVersionUID = -8159019613531419719L;

	/** The id. */
    private Integer id;

    /** The registry. */
    private Registry registry;

    /** The media. */
    private MediaType media;

    /** The value. */
    private String value;

    /** The comment. */
    private String comment;

	private boolean administrative;

	private boolean commercial;

	private boolean technical;

	/**
     * Gets the id.
     * 
     * @return the id
     */
    @Id
    @GeneratedValue
	@Column(nullable=false)
	public Integer getId() {
        return id;
    }

    /**
     * Sets the id.
     * 
     * @param id the id
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
    @JoinColumn(name="registry", nullable = false, updatable = false)    
	@ForeignKey(name = "FK_RMEDIA_REGISTRY")
	@Index(name = "IDX_RMEDIA_REGISTRY")    
    public Registry getRegistry() {
        return this.registry;
    }

    /**
     * Sets the registry.
     * 
     * @param registry the registry
     */
    public void setRegistry(Registry registry) {
        this.registry = registry;
    }

    /**
     * Gets the media type.
     * 
     * @return the media type
     */
    @Column(name="media", nullable = false)
    public MediaType getMediaType() {
        return media;
    }

    /**
     * Sets the media type.
     * 
     * @param media the media
     */
    public void setMediaType(MediaType media) {
        this.media = media;
    }

    /**
     * Gets the value.
     * 
     * @return the value
     */
    @Column(length=64)
	public String getValue() {
        return value;
    }

    /**
     * Sets the value.
     * 
     * @param value the value
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Gets the comment.
     * 
     * @return the comment
     */
    @Column(length=64)
	public String getComment() {
        return comment;
    }

    /**
     * Sets the comment.
     * 
     * @param comment the comment
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

	@Column(name="administrative")
	public boolean isAdministrative() {
		return administrative;
	}

	public void setAdministrative(boolean administrative) {
		this.administrative = administrative;
	}

	@Column(name="commercial")
	public boolean isCommercial() {
		return commercial;
	}

	public void setCommercial(boolean commercial) {
		this.commercial = commercial;
	}

	@Column(name="technical")
	public boolean isTechnical() {
		return technical;
	}

	public void setTechnical(boolean technical) {
		this.technical = technical;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final RegistryMedia o = (RegistryMedia) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.comment, o.comment)
				.append(this.media, o.media)				
				.append(this.registry, o.registry)
				.append(this.value, o.value)
				.append(this.administrative, o.administrative)
				.append(this.commercial, o.commercial)
				.append(this.technical, o.technical)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(comment)
			.append(id)
			.append(media)
			.append(registry)	
			.append(value)
			.append(administrative)
			.append(commercial)
			.append(technical)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	} 

}