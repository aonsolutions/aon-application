package com.code.aon.supplier;


import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.ObjectUtils;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Parameter;

import com.code.aon.common.ITransferObject;
import com.code.aon.config.IScopable;
import com.code.aon.config.Scope;
import com.code.aon.registry.IRegistry;
import com.code.aon.registry.ITaxInfo;
import com.code.aon.registry.Registry;
import com.code.aon.supplier.enumeration.SupplierStatus;

/**
 * Transfer Object that represents a supplier.
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

	/** The status of the supplier. */
	private SupplierStatus status;
	
	/** The withholding. */
	private boolean withholding;
	
	/** The supplier segment. */
	private SupplierSegment supplierSegment;
	
	private Scope scope;
	
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

	/**
	 * Gets the status.
	 * 
	 * @return the status
	 */
	@Column(name="status")
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
	 * Gets the supplier segment.
	 * 
	 * @return the supplier segment
	 */
	@ManyToOne
	@JoinColumn(name="segment")
	@ForeignKey(name = "FK_SUPPLIER_SEGMENT")
	@Index(name = "IDX_SUPPLIER_SEGMENT")
	public SupplierSegment getSupplierSegment() {
		return supplierSegment;
	}

	/**
	 * Sets the supplier segment.
	 * 
	 * @param supplierSegment the supplier segment
	 */
	public void setSupplierSegment(SupplierSegment supplierSegment) {
		this.supplierSegment = supplierSegment;
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
		return false;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
    		return super.equals(obj);
		}
		if (obj instanceof Supplier) {
			Supplier o = (Supplier) obj;
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