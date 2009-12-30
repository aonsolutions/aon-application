package com.code.aon.supplier;


import java.util.HashMap;
import java.util.Map;

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

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.code.aon.common.ILookupObject;
import com.code.aon.common.ITransferObject;
import com.code.aon.config.IScopable;
import com.code.aon.config.Scope;
import com.code.aon.registry.Registry;
import com.code.aon.supplier.dao.ISupplierAlias;
import com.code.aon.supplier.enumeration.SupplierStatus;

// TODO: Auto-generated Javadoc
/**
 * Transfer Object that represents a supplier.
 */
@Entity
@Table(name="supplier")
@PrimaryKeyJoinColumn(name="registry")
public class Supplier implements ITransferObject, ILookupObject, IScopable{
	
	/** The Constant SUPPLIER_FULL_NAME used to retrieve the complete name of the supplier using the lookup. */
	private static final String SUPPLIER_FULL_NAME = "Supplier_full_name";

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
	public Scope getScope() {
		return scope;
	}

	public void setScope(Scope scope) {
		this.scope = scope;
	}

	/**
	 * Gets the a map that will be used by the lookup.
	 * 
	 * @return the map
	 */
	@Transient
    public Map<String,Object> getLookups() {
        Map<String,Object> map = new HashMap<String,Object>();
        map.put(ISupplierAlias.SUPPLIER_ID, getId());
        map.put(ISupplierAlias.SUPPLIER_REGISTRY_NAME, getRegistry().getName() );
        map.put(ISupplierAlias.SUPPLIER_REGISTRY_SURNAME, getRegistry().getSurname());
        map.put(ISupplierAlias.SUPPLIER_REGISTRY_DOCUMENT, getRegistry().getDocument());
        map.put(SUPPLIER_FULL_NAME, getRegistry().getName() + " " + ((getRegistry().getSurname() == null) ? "" : getRegistry().getSurname()) );
        return map;
    }
}