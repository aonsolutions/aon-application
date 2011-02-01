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
@Table(name="invoicing_group")
public class InvoicingGroup implements ITransferObject {

	private static final long serialVersionUID = -7948345500064583985L;

	private Integer id;
	private Registry parent;

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
    @JoinColumn(name="parent", nullable = false)
    @ForeignKey(name="FK_INVOICING_GROUP_PARENT")
    @Index(name="IDX_INVOICING_GROUP_PARENT")                                            
	public Registry getParent() {
		return parent;
	}
	public void setParent(Registry parent) {
		this.parent = parent;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final InvoicingGroup o = (InvoicingGroup) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
			.append(this.parent,o.parent)
			.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)		
			.append(this.parent)
			.toHashCode();
	}	

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}

}