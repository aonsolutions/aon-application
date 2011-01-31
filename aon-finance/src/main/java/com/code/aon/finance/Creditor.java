package com.code.aon.finance;

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
import com.code.aon.finance.enumeration.CreditorStatus;
import com.code.aon.registry.IRegistry;
import com.code.aon.registry.ITaxInfo;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryAttachment;

@Entity
@Table(name="creditor")
@PrimaryKeyJoinColumn(name="registry")
public class Creditor implements ITransferObject, ITaxInfo, IScopable, IRegistry {
	
	private static final long serialVersionUID = 6173766150887358588L;

	private Integer id;
	private Registry registry;
	private boolean withholding;
    private InvoiceTransactionType transaction;
	private CreditorStatus status;
	private Scope scope;
	private Set<RegistryAttachment> documents = new HashSet<RegistryAttachment>();

	@Id
	@Column(name="registry")
	@GeneratedValue(generator="registry_id")
	@GenericGenerator(name="registry_id", strategy="foreign", parameters = {
			@Parameter(name="property", value="registry")})
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}

	@OneToOne(cascade={CascadeType.PERSIST, CascadeType.MERGE})
	@org.hibernate.annotations.Cascade(value = org.hibernate.annotations.CascadeType.SAVE_UPDATE)
	@PrimaryKeyJoinColumn 
	public Registry getRegistry() {
		return registry;
	}
	public void setRegistry(Registry registry) {
		this.registry = registry;
	}

	@Column(nullable=true)
	public boolean isWithholding() {
		return withholding;
	}
	public void setWithholding(boolean withholding) {
		this.withholding = withholding;
	}

	public InvoiceTransactionType getTransaction() {
		return transaction;
	}
	public void setTransaction(InvoiceTransactionType transaction) {
		this.transaction = transaction;
	}

	public CreditorStatus getStatus() {
		return status;
	}
	public void setStatus(CreditorStatus status) {
		this.status = status;
	}
	
	@ManyToOne
    @JoinColumn(name="scope", nullable=false)
	@ForeignKey(name="FK_CREDITOR_SCOPE")
	@Index(name="IDX_CREDITOR_SCOPE")    
	public Scope getScope() {
		return scope;
	}
	public void setScope(Scope scope) {
		this.scope = scope;
	}
	
	@OneToMany(mappedBy = "registry", cascade={CascadeType.REMOVE})
	public Set<RegistryAttachment> getDocuments() {
		return documents;
	}
	public void setDocuments(Set<RegistryAttachment> documents) {
		this.documents = documents;
	}

	@Transient
	public boolean isTaxFree() {
		return (getTransaction() != InvoiceTransactionType.NATIONAL);
	}

	@Transient
	public boolean isSurcharge() {
		return false;
	}

	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final Creditor o = (Creditor) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
			.append(this.registry,o.registry)
			.append(this.scope,o.scope)
			.append(this.status,o.status)
			.append(this.transaction,o.transaction)
			.append(this.withholding,o.withholding)
			.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)		
			.append(this.registry)
			.append(this.scope)
			.append(this.status)
			.append(this.transaction)
			.append(this.withholding)
			.toHashCode();
	}	

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}

}