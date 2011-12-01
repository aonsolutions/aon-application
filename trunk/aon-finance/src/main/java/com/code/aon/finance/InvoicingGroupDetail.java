package com.code.aon.finance;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.registry.Registry;

@Entity
@Table(name="invoicing_group_detail")
public class InvoicingGroupDetail implements ITransferObject {
	
	private static final long serialVersionUID = 1579515712994525181L;

	private Integer id;
	private InvoicingGroup invoicingGroup;
	private Registry child;
	private boolean grouped;

	@Id
	@Column(nullable=false)
	@GeneratedValue
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@ManyToOne
    @JoinColumn(name="invoicing_group", nullable = false)
    @ForeignKey(name="FK_INVOICING_GROUP_DETAIL_INVOICING_GROUP")
    @Index(name="IDX_INVOICING_GROUP_DETAIL_INVOICING_GROUP")                                                    
    public InvoicingGroup getInvoicingGroup() {
		return invoicingGroup;
	}
	public void setInvoicingGroup(InvoicingGroup invoicingGroup) {
		this.invoicingGroup = invoicingGroup;
	}

	@ManyToOne
    @JoinColumn(name="child", nullable = false)
    @ForeignKey(name="FK_INVOICING_GROUP_DETAIL_CHILD")
    @Index(name="IDX_INVOICING_GROUP_DETAIL_CHILD")                                                
	public Registry getChild() {
		return child;
	}
	public void setChild(Registry child) {
		this.child = child;
	}

	@Column(nullable=true)
	public boolean isGrouped() {
		return grouped;
	}
	public void setGrouped(boolean grouped) {
		this.grouped = grouped;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final InvoicingGroupDetail o = (InvoicingGroupDetail) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
			.append(this.child,o.child)
			.append(this.grouped,o.grouped)
			.append(this.invoicingGroup,o.invoicingGroup)
			.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)		
			.append(this.child)
			.append(this.grouped)
			.append(this.invoicingGroup)
			.toHashCode();
	}	

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}

}
