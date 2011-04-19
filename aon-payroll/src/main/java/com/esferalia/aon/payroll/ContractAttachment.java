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
import javax.persistence.Transient;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.Index;

import com.code.aon.common.IAttachment;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.common.enumeration.IConfidentialable;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.config.IScopable;
import com.code.aon.config.Scope;
import com.esferalia.aon.payroll.enumeration.ContractAttachmentType;

/**
 * Transfer Object that represents the ContractAttachment.
 * 
 * @author Esferalia
 * @since 1.0
 */
@Entity
@Table(name="contract_attach")
public class ContractAttachment implements IAttachment, IScopable, IConfidentialable {

	private static final long serialVersionUID = 6477057459960336594L;

	/** The id. */
    private Integer id;

    /** The contract. */
    private Contract contract;
    
    /** The mime type. */
    private MimeType mimeType;

    /** The data (binary). */
    private byte[] data;

    /** The description. */
    private String description;
    
    /** The size in bytes. */
    private Integer size;
    
    /** The registry attachment type. */
    private ContractAttachmentType attachmentType;
    
    /** The scope. */
	private Scope scope;
	
	private SecurityLevel securityLevel;
	
	private Date attachDate;
    
    /**
     * The empty constructor.
     */
    public ContractAttachment() {
    	this.securityLevel = SecurityLevel.OFFICIAL;
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
     * Gets the contract.
     * 
     * @return the contract
     */
    @ManyToOne
    @JoinColumn(name="contract", nullable = false, updatable = false)    
    @ForeignKey(name = "FK_CONTRACT_ATTACH_CONTRACT")
    @Index(name = "IDX_CONTRACT_ATTACH_CONTRACT")
	public Contract getContract() {
        return this.contract;
    }

    /**
     * Sets the contract.
     * 
     * @param contract the contract
     */
    public void setContract(Contract contract) {
        this.contract = contract;
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
	public ContractAttachmentType getAttachmentType() {
		return attachmentType;
	}

	/**
	 * Sets the registry attachment type.
	 * 
	 * @param registryAttachmentType the registry attachment type
	 */
	public void setAttachmentType(ContractAttachmentType attachmentType) {
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

	@Column(name = "security_level")
    public SecurityLevel getSecurityLevel() {
        return securityLevel;
    }
    
    public void setSecurityLevel(SecurityLevel securityLevel) {
        this.securityLevel = securityLevel;
    }

	@Override
	@Transient
	public boolean isConfidential() {
		return SecurityLevel.CONFIDENTIAL == getSecurityLevel();
	}

	@Override
	@Transient
	public void setConfidential(boolean confidential) {
		setSecurityLevel(confidential ? SecurityLevel.CONFIDENTIAL : SecurityLevel.OFFICIAL);
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
		final ContractAttachment o = (ContractAttachment) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.attachDate, o.attachDate)
				.append(this.data, o.data)				
				.append(this.description, o.description)
				.append(this.mimeType, o.mimeType)				
				.append(this.contract, o.contract)
				.append(this.attachmentType, o.attachmentType)
				.append(this.scope, o.scope)
				.append(this.securityLevel, o.securityLevel)
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
			.append(contract)
			.append(attachmentType)				
			.append(scope)
			.append(securityLevel)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}

}