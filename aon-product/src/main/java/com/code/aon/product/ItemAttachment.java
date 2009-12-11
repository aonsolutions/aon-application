package com.code.aon.product;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.Index;

import com.code.aon.common.IAttachment;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.common.enumeration.MimeType;

/**
 * Transfer Object that represents the attachment 
 * linked to item.
 * 
 * @author Consulting & Development. Aimar Tellitu - 24-ene-2006
 * @since 1.0
 */
@Entity
@Table(name="iattach")
public class ItemAttachment implements IAttachment, Cloneable {

	private static final long serialVersionUID = 5541725371222814863L;

	/**
     * Unique key.
     */
    private Integer id;

    /**
     * References the item linked.
     */
    private Item item;

    /**
     * MIME type of the attachment.
     */
    private MimeType mimeType;

    /**
     * Attachment data byte array.
     */
    private byte[] data;

    /**
     * Description of the attachment.
     */
    private String description;
    
    /**
     * Attachment byte size.
     */
    private Integer size;
    
    /**
     * Returns the unique key of the item attachment.
     *
     * @return unique key. 
     */
    @Id
    @GeneratedValue
	@Column(nullable=false)
	public Integer getId() {
        return id;
    }

    /**
     * Assigns the unique key to the item attachment.
     * 
     * @param primaryKey
     *            unique key
     */
    public void setId(Integer primaryKey) {
        this.id = primaryKey;
    }

    /**
     * Returns the referenced item.
     *
     * @return the item. 
     */
    @ManyToOne
    @JoinColumn(name="item", nullable = false, updatable = false)    
    @ForeignKey(name = "FK_IATTACH_ITEM")
    @Index(name = "IDX_IATTACH_ITEM")    
	public Item getItem() {
        return this.item;
    }

    /**
     * Assigns the item.
     * 
     * @param item
     * 		referenced item.
     */
    public void setItem(Item item) {
        this.item = item;
    }

    /**
     * Returns the mime type of the attachment.
     * 
     * @return mime type. 
     */
	public MimeType getMimeType() {
        return mimeType;
    }

    /**
     * Asigns the mime type.
     * 
     * @param mimeType
     *            mime type of the attachment.
     */
    public void setMimeType(MimeType mimeType) {
        this.mimeType = mimeType;
    }

    /**
     * Returns the attachment data byte array.
     *
     * @return attachement data. 
     *         
     */
    @Lob
    public byte[] getData() {
        return data;
    }

    /**
     * Assign the attachment data byte array.
     * 
     * @param data
     *            attachment data byte array.
     */
    public void setData(byte[] data) {
        this.data = data;
    }
	
    /**
     * Returns the attachment's description.
     * 
     * @return attachment's description. 
     */
	@Column(length=64)
	public String getDescription() {
		return this.description;
	}

    /**
     * Assigns attachment's description.
     * 
     * @param description
     * 		attachment's description.
     */
	public void setDescription(String description) {
		this.description = description;
	}

    /**
     * Returns attachment's size in bytes.
     * 
     * @return attachment's size in bytes. 
     */
	@Formula("LENGTH(data)")
	public Integer getSize() {
		return size;
	}

    /**
     * Assigns attachment's size in bytes.
     * 
     * @param size
     * 		attachment's size in bytes.
     */
	public void setSize(Integer size) {
		this.size = size;
	}

    /**
     * Returns a clone of this item attachment.
     * 
     * @return object. 
     */
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final ItemAttachment o = (ItemAttachment) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.data, o.data)			
				.append(this.description, o.description)								
				.append(this.item, o.item)				
				.append(this.mimeType, o.mimeType)								
				.append(this.size, o.size)				
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(data)
			.append(description)
			.append(id)			
			.append(item)						
			.append(mimeType)						
			.append(size)						
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}

}