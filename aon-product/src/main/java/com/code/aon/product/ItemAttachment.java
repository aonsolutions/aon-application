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
import com.code.aon.product.enumeration.AttachmentType;

@Entity
@Table(name="iattach")
public class ItemAttachment implements IAttachment, Cloneable {

	private static final long serialVersionUID = 5541725371222814863L;

    private Integer id;
    private Item item;
    private MimeType mimeType;
    private byte[] data;
    private String description;
    private Integer size;
    private AttachmentType type;

    
    @Id
    @GeneratedValue
	@Column(nullable=false)
	public Integer getId() {
        return id;
    }

    public void setId(Integer primaryKey) {
        this.id = primaryKey;
    }

    @ManyToOne
    @JoinColumn(name="item", nullable = false, updatable = false)    
    @ForeignKey(name = "FK_IATTACH_ITEM")
    @Index(name = "IDX_IATTACH_ITEM")    
	public Item getItem() {
        return this.item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

	public MimeType getMimeType() {
        return mimeType;
    }

    public void setMimeType(MimeType mimeType) {
        this.mimeType = mimeType;
    }

    @Lob
    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
	
	@Column(length=64)
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Formula("LENGTH(data)")
	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}
	
	@Column(name="type")
	public AttachmentType getType() {
		return type;
	}

	public void setType(AttachmentType type) {
		this.type = type;
	}

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
			.append(id)			
			.append(this.data)
			.append(this.description)
			.append(this.item)						
			.append(this.mimeType)						
			.append(this.size)						
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}

}