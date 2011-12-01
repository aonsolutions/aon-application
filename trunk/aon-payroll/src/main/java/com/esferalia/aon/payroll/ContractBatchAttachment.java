package com.esferalia.aon.payroll;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.Index;

import com.code.aon.common.IAttachment;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.config.IScopable;
import com.code.aon.config.Scope;
import com.esferalia.aon.payroll.enumeration.ContractBatchAttachmentType;

/**
 * Transfer Object that represents the ContractBatchAttachment.
 * 
 * @author Esferalia
 * @since 1.0
 */
@Entity
@Table(name="contract_batch_attach")
public class ContractBatchAttachment implements IAttachment, IScopable {

	private static final long serialVersionUID = 6018833861951379143L;

	/** The id. */
    private Integer id;

    /** The leave batch. */
    private ContractBatch contractBatch;
    
    /** The mime type. */
    private MimeType mimeType;

    /** The data (binary). */
    private byte[] data;

    /** The description. */
    private String description;
    
    /** The size in bytes. */
    private Integer size;
    
    /** The attachment type. */
    private ContractBatchAttachmentType attachmentType;
    
    /** The scope. */
	private Scope scope;
	
	private Date attachDate;
    
    
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
     * Gets the ContractBatch.
     * 
     * @return the ContractBatch
     */
    @ManyToOne
    @JoinColumn(name="contract_batch", nullable = false, updatable = false)    
    @ForeignKey(name = "FK_CONTRACT_BATCH_ATTACH_CONTRACT_BATCH")
    @Index(name = "IDX_CONTRACT_BATCH_ATTACH_CONTRACT_BATCH")
	public ContractBatch getContractBatch() {
        return this.contractBatch;
    }

    /**
     * Sets the ContractBatch.
     * 
     * @param contract the ContractBatch
     */
    public void setContractBatch(ContractBatch contractBatch) {
        this.contractBatch = contractBatch;
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
    @Lob
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
	public ContractBatchAttachmentType getAttachmentType() {
		return attachmentType;
	}

	/**
	 * Sets the attachment type.
	 * 
	 * @param attachmentType the attachment type
	 */
	public void setAttachmentType(ContractBatchAttachmentType attachmentType) {
		this.attachmentType = attachmentType;
	}

	/**
	 * Gets the scope.
	 * 
	 * @return the scope
	 */
    @ManyToOne
    @JoinColumn(name="scope")
	public Scope getScope() {
		return scope;
	}

	/**
	 * Sets the scope.
	 * 
	 * @param scope the scope
	 */
	public void setScope(Scope scope) {
		this.scope = scope;
	}

	@Column(name="attach_date")
	@Temporal(TemporalType.DATE)
    public Date getAttachDate() {
		return attachDate;
	}

	public void setAttachDate(Date attachDate) {
		this.attachDate = attachDate;
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
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final ContractBatchAttachment o = (ContractBatchAttachment) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.attachDate, o.attachDate)
				.append(this.data, o.data)				
				.append(this.description, o.description)
				.append(this.mimeType, o.mimeType)				
				.append(this.contractBatch, o.contractBatch)
				.append(this.attachmentType, o.attachmentType)
				.append(this.scope, o.scope)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(attachDate)
			.append(data)
			.append(description)	
			.append(id)			
			.append(mimeType)
			.append(contractBatch)
			.append(attachmentType)				
			.append(scope)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}

}