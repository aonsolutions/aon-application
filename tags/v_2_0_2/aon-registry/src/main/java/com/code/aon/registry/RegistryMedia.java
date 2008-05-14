package com.code.aon.registry;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.code.aon.common.ITransferObject;
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

    /**
     * The empty constructor.
     */
    public RegistryMedia() {
    }

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
    @Column(name="media")
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
}