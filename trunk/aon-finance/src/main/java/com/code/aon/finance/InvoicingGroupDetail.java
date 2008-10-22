package com.code.aon.finance;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;


import com.code.aon.common.ITransferObject;
import com.code.aon.registry.Registry;

/**
 * The Class InvoicingGroupDetail.
 */
@Entity
@Table(name="invoicing_group_detail")
public class InvoicingGroupDetail implements ITransferObject {
	
	private static final long serialVersionUID = 1579515712994525181L;

	/** The id. */
	private Integer id;
	
	/** The group. */
	private InvoicingGroup invoicingGroup;
	
	/** The child. */
	private Registry child;
	
	/** The grouped. */
	private boolean grouped;


	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
	@Id
	@Column(nullable=false)
	@GeneratedValue
	public Integer getId() {
		return id;
	}

	/**
	 * Sets the id.
	 * 
	 * @param id the id
	 */
	public void setId(Integer id) {
		this.id = id;
	}
	
    /**
     * Gets the group.
     * 
     * @return the group
     */
	@ManyToOne
    @JoinColumn(name="invoicing_group", nullable = false)
    public InvoicingGroup getInvoicingGroup() {
		return invoicingGroup;
	}

	/**
	 * Sets the group.
	 * 
	 * @param group the group
	 */
	public void setInvoicingGroup(InvoicingGroup invoicingGroup) {
		this.invoicingGroup = invoicingGroup;
	}

	/**
	 * Gets the child.
	 * 
	 * @return the child
	 */
	@ManyToOne
    @JoinColumn(name="child", nullable = false)
	public Registry getChild() {
		return child;
	}

	/**
	 * Sets the child.
	 * 
	 * @param child the child
	 */
	public void setChild(Registry child) {
		this.child = child;
	}

	/**
	 * Checks if is grouped.
	 * 
	 * @return true, if is grouped
	 */
	public boolean isGrouped() {
		return grouped;
	}

	/**
	 * Sets the grouped.
	 * 
	 * @param grouped the grouped
	 */
	public void setGrouped(boolean grouped) {
		this.grouped = grouped;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return super.equals(obj);
		}
		if (obj instanceof InvoicingGroupDetail) {
			InvoicingGroupDetail o = (InvoicingGroupDetail) obj;
			if (ObjectUtils.equals(getId(), o.getId())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		return 0;
	}
}
