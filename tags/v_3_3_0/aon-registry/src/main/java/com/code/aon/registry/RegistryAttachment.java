package com.code.aon.registry;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;
import org.hibernate.annotations.Formula;

import com.code.aon.common.IAttachment;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.registry.enumeration.RegistryAttachmentType;

/**
 * Transfer Object that represents the RegistryAttachment.
 * 
 * @author Consulting & Development. Aimar Tellitu - 29-jul-2005
 * @since 1.0
 */
@Entity
@Table(name="rattach")
public class RegistryAttachment implements IAttachment {

	private static final long serialVersionUID = -6774043069274297973L;

	/** The id. */
    private Integer id;

    /** The registry. */
    private Registry registry;

    /** The category. */
    private Category category;
    
    /** The mime type. */
    private MimeType mimeType;

    /** The data (binary). */
    private byte[] data;

    /** The description. */
    private String description;
    
    /** The size in bytes. */
    private Integer size;
    
    /** The registry attachment type. */
    private RegistryAttachmentType registryAttachmentType;
    
    /**
     * The empty constructor.
     */
    public RegistryAttachment() {
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
     * @param primaryKey the primary key
     * @param id the id
     */
    public void setId(Integer primaryKey) {
        this.id = primaryKey;
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
     * Gets the category.
     * 
     * @return the category
     */
    @ManyToOne
    @JoinColumn(name="category")    
    public Category getCategory() {
        return this.category;
    }

    /**
     * Sets the category.
     * 
     * @param category the category
     */
    public void setCategory(Category category) {
        this.category = category;
    }
    
	/**
	 * Gets the mime type.
	 * 
	 * @return the mime type
	 */
	public MimeType getMimeType() {
        return mimeType;
    }

    /**
     * Sets the mime type.
     * 
     * @param mimeType the mime type
     */
    public void setMimeType(MimeType mimeType) {
        this.mimeType = mimeType;
    }

    /**
     * Gets the data.
     * 
     * @return the data
     */
    public byte[] getData() {
        return data;
    }

    /**
     * Sets the data.
     * 
     * @param data the data
     */
    public void setData(byte[] data) {
        this.data = data;
    }
	
	/**
	 * Gets the description.
	 * 
	 * @return the description
	 */
	@Column(length=64)
	public String getDescription() {
		return this.description;
	}

	/**
	 * Sets the description.
	 * 
	 * @param description the description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Gets the size.
	 * 
	 * @return the size
	 */
	@Formula("LENGTH(data)")
	public Integer getSize() {
		return size;
	}

	/**
	 * Sets the size.
	 * 
	 * @param size the size
	 */
	public void setSize(Integer size) {
		this.size = size;
	}
	
	/**
	 * Gets the registry attachment type.
	 * 
	 * @return the registry attachment type
	 */
	@Column(name="type")
	public RegistryAttachmentType getRegistryAttachmentType() {
		return registryAttachmentType;
	}

	/**
	 * Sets the registry attachment type.
	 * 
	 * @param registryAttachmentType the registry attachment type
	 */
	public void setRegistryAttachmentType(RegistryAttachmentType registryAttachmentType) {
		this.registryAttachmentType = registryAttachmentType;
	}

	/**
	 * Clones the RegistryAttachment.
	 * 
	 * @return the object
	 * 
	 * @throws CloneNotSupportedException the clone not supported exception
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
    		return super.equals(obj);
		}
		if (obj instanceof RegistryAttachment) {
			RegistryAttachment o = (RegistryAttachment) obj;
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