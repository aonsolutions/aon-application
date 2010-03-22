package com.code.aon.finance;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

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
    @ForeignKey(name="FK_INVOICING_GROUP_DETAIL_INVOICING_GROUP")
    @Index(name="IDX_INVOICING_GROUP_DETAIL_INVOICING_GROUP")                                                    
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
    @ForeignKey(name="FK_INVOICING_GROUP_DETAIL_CHILD")
    @Index(name="IDX_INVOICING_GROUP_DETAIL_CHILD")                                                
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
	@Column(nullable=true)
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
		if (obj == null) {
    		return super.equals(obj);
		}
		if (obj instanceof InvoicingGroupDetail) {
			InvoicingGroupDetail o = (InvoicingGroupDetail) obj;
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
