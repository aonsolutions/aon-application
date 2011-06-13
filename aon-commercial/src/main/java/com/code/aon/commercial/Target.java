package com.code.aon.commercial;

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
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Parameter;

import com.code.aon.commercial.enumeration.Advertising;
import com.code.aon.commercial.enumeration.TargetStatus;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.config.Tariff;
import com.code.aon.config.enumeration.InvoiceTransactionType;
import com.code.aon.registry.IRegistry;
import com.code.aon.registry.ITaxInfo;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryAttachment;

/**
 * Transfer Object that represents a Target.
 */
@Entity
@Table(name="target")
@PrimaryKeyJoinColumn(name="registry")
public class Target implements ITransferObject, ITaxInfo, IRegistry {
	
	private static final long serialVersionUID = -7492435795404962788L;

	private Integer id;
    private Tariff tariff;
	private Registry registry;
	private Advertising advertising;
    private boolean surcharge;
    private boolean withholding;
    private InvoiceTransactionType transaction;
    private TargetStatus status;
	private boolean customer;
	private Set<TargetItem> items = new HashSet<TargetItem>();
	private Set<TargetSeller> sellers = new HashSet<TargetSeller>();
	private Set<Project> projects = new HashSet<Project>();
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

    @ManyToOne
    @JoinColumn(name="tariff")
    @ForeignKey(name = "FK_TARGET_TARIFF")
    @Index(name = "IDX_TARGET_TARIFF")        
	public Tariff getTariff() {
		return tariff;
	}
	
	public void setTariff(Tariff tariff) {
		this.tariff = tariff;
	}
	
	@Column(nullable=false)
	public Advertising getAdvertising() {
		return advertising;
	}

	public void setAdvertising(Advertising advertising) {
		this.advertising = advertising;
	}

    public boolean isSurcharge() {
        return surcharge;
    }

    public void setSurcharge(boolean surcharge) {
        this.surcharge = surcharge;
    }

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

    public TargetStatus getStatus() {
        return status;
    }

    public void setStatus(TargetStatus status) {
        this.status = status;
    }

	@OneToMany(mappedBy = "target", cascade={CascadeType.REMOVE})	
	public Set<TargetItem> getItems() {
		return items;
	}

	public void setItems(Set<TargetItem> items) {
		this.items = items;
	}

	@OneToMany(mappedBy = "target", cascade={CascadeType.REMOVE})	
	public Set<TargetSeller> getSellers() {
		return sellers;
	}

	public void setSellers(Set<TargetSeller> sellers) {
		this.sellers = sellers;
	}

	@OneToMany(mappedBy = "target", cascade={CascadeType.REMOVE})
	public Set<Project> getProjects() {
		return projects;
	}

	public void setProjects(Set<Project> projects) {
		this.projects = projects;
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

	@Formula("(select COUNT(*) from customer c where registry = c.registry)")
	public boolean isCustomer() {
		return customer;
	}

	public void setCustomer(boolean customer) {
		this.customer = customer;
	}
		
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final Target o = (Target) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.tariff, o.tariff)
				.append(this.advertising, o.advertising)
				.append(this.registry, o.registry)
				.append(this.surcharge, o.surcharge)
				.append(this.transaction, o.transaction)
				.append(this.withholding, o.withholding)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(tariff)
			.append(advertising)
			.append(id)
			.append(registry)			
			.append(surcharge)
			.append(transaction)
			.append(withholding)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}

}