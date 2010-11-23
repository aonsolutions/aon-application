package com.code.aon.product;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Formula;

import com.code.aon.common.IAttachment;
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
     * Default contructor.
     * 
     */
    public ItemAttachment() {
    }

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
	
}