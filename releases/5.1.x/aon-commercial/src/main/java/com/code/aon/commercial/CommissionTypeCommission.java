package com.code.aon.commercial;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
import com.code.aon.config.CommissionType;

@Entity
@Table(name="commission_type_commission")
public class CommissionTypeCommission implements ITransferObject {

	private static final long serialVersionUID = -8681356323315121617L;

	private Integer id;
	private CommissionType commissionType;
	private Commission commission;

	@Id
	@GeneratedValue
	@Column(nullable=false)
    public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@ManyToOne (fetch=FetchType.EAGER)
	@JoinColumn(name="commission_type", nullable=false)
    @ForeignKey(name = "FK_COMMISSION_TYPE_COMMISSION_COMMISSION_TYPE")
    @Index(name = "IDX_COMMISSION_TYPE_COMMISSION_COMMISSION_TYPE")
	public CommissionType getCommissionType() {
		return commissionType;
	}

	public void setCommissionType(CommissionType commissionType) {
		this.commissionType = commissionType;
	}

	@ManyToOne (fetch=FetchType.EAGER)
	@JoinColumn(name="commission", nullable=false)
    @ForeignKey(name = "FK_COMMISSION_TYPE_COMMISSION_COMMISSION")
    @Index(name = "IDX_COMMISSION_TYPE_COMMISSION_COMMISSION")
	public Commission getCommission() {
		return commission;
	}

	public void setCommission(Commission commission) {
		this.commission = commission;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final CommissionTypeCommission o = (CommissionTypeCommission) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.commissionType, o.commissionType)
				.append(this.commission, o.commission)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)
			.append(commissionType)
			.append(commission)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}

}
