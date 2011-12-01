package com.code.aon.supplier;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Parameter;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.config.IScopable;
import com.code.aon.config.Scope;
import com.code.aon.config.enumeration.InvoiceTransactionType;
import com.code.aon.registry.IRegistry;
import com.code.aon.registry.ITaxInfo;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.supplier.enumeration.SupplierStatus;

/**
 * Transfer Object that represents a Supplier.
 */
@Entity
@Table(name="supplier")
@PrimaryKeyJoinColumn(name="registry")
public class Supplier implements ITransferObject, ITaxInfo, IScopable, IRegistry {
	
	private static final long serialVersionUID = -5482729597797009950L;

	/** The id. */
	private Integer id;

	/** The Registry. */
	private Registry registry;

	/** The withholding. */
	private boolean withholding;
	
    /** The transaction type. */
    private InvoiceTransactionType transaction;

	/** The status. */
	private SupplierStatus status;
	
	/** The scope. */
	private Scope scope;
	
	/** The documents. */
	private Set<RegistryAttachment> documents = new HashSet<RegistryAttachment>();
	
	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
    @Id
	@Column(name="registry")
	@GeneratedValue(generator="registry_id")
	@GenericGenerator(name="registry_id", strategy="foreign", parameters = {
			@Parameter(name="property", value="registry")})
	public Integer getId() {
		return id;
	}

    /**
     * Sets the id.
     * 
     * @param id The id
     */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * Gets the registry.
	 * 
	 * @return the registry.
	 */
	@OneToOne(cascade={CascadeType.PERSIST, CascadeType.MERGE})
	@org.hibernate.annotations.Cascade(value = org.hibernate.annotations.CascadeType.SAVE_UPDATE)
	@PrimaryKeyJoinColumn 
	public Registry getRegistry() {
		return registry;
	}

	/**
	 * Sets the registry.
	 * 
	 * @param registry the Registry
	 */
	public void setRegistry(Registry registry) {
		this.registry = registry;
	}

	/**
	 * Checks if a withholding is applied.
	 * 
	 * @return true, if a withholding is applied
	 */
	@Column(nullable=true)
	public boolean isWithholding() {
		return withholding;
	}

	/**
	 * Sets the withholding.
	 * 
	 * @param withholding the withholding
	 */
	public void setWithholding(boolean withholding) {
		this.withholding = withholding;
	}

	/**
	 * Gets the transaction type.
	 * 
	 * @return the transaction type
	 */
	public InvoiceTransactionType getTransaction() {
		return transaction;
	}

	/**
	 * Sets the transaction type.
	 * 
	 * @param transaction the transaction type
	 */
	public void setTransaction(InvoiceTransactionType transaction) {
		this.transaction = transaction;
	}

	/**
	 * Gets the status.
	 * 
	 * @return the status
	 */
	public SupplierStatus getStatus() {
		return status;
	}

	/**
	 * Sets the status.
	 * 
	 * @param status the status
	 */
	public void setStatus(SupplierStatus status) {
		this.status = status;
	}
	
	@ManyToOne
	@JoinColumn(name="scope", nullable=false)
	@ForeignKey(name = "FK_SUPPLIER_SCOPE")
	@Index(name = "IDX_SUPPLIER_SCOPE")	
	public Scope getScope() {
		return scope;
	}

	public void setScope(Scope scope) {
		this.scope = scope;
	}

	@Transient
	public boolean isSurcharge() {
		return false;
	}

	@Transient
	public boolean isTaxFree() {
		return (getTransaction() != InvoiceTransactionType.NATIONAL);
	}
	
	@OneToMany(mappedBy = "registry", cascade={CascadeType.REMOVE})
	public Set<RegistryAttachment> getDocuments() {
		return documents;
	}
	
	public void setDocuments(Set<RegistryAttachment> documents) {
		this.documents = documents;
	}	

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final Supplier o = (Supplier) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.registry, o.registry)
				.append(this.scope, o.scope)
				.append(this.status, o.status)
				.append(this.transaction, o.transaction)
				.append(this.withholding, o.withholding)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()		
			.append(id)
			.append(registry)
			.append(scope)
			.append(status)
			.append(transaction)
			.append(withholding)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}

}