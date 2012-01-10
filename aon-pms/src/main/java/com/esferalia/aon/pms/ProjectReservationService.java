package com.esferalia.aon.pms;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.product.Item;
import com.code.aon.product.strategy.ICalculable;
import com.code.aon.product.util.DiscountExpression;

@Entity
@Table(name="project_reservation_service")
public class ProjectReservationService implements ITransferObject, ICalculable {

	private static final long serialVersionUID = -2595051575335189544L;

	private Integer id;
	private ProjectReservation projectReservation;
	private int serviceIndex;
	private Date effectiveDate;
    private Item item;
	private String description;
	private double quantity;
	private double price;
	private double taxableBase;

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
	@JoinColumn(name="project_reservation", nullable=false)
	public ProjectReservation getProjectReservation() {
		return projectReservation;
	}

	public void setProjectReservation(ProjectReservation projectReservation) {
		this.projectReservation = projectReservation;
	}

    @Column(name="service_index", nullable=false)
    public int getServiceIndex() {
        return serviceIndex;
    }
    public void setServiceIndex(int serviceIndex) {
        this.serviceIndex = serviceIndex;
    }

    @Column(name="effective_date", nullable=false)
    public Date getEffectiveDate() {
        return effectiveDate;
    }
    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

	@ManyToOne (fetch=FetchType.EAGER)
	@JoinColumn(name="item", nullable=false)
	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

    @Column(name="description", length=64)
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name="quantity", precision=15, scale=2)
    public double getQuantity() {
        return quantity;
    }
    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    @Column(name="price", precision=15, scale=2)
    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }

    @Column(name="taxable_base", precision=15, scale=2)
    public double getTaxableBase() {
        return taxableBase;
    }
    public void setTaxableBase(double taxableBase) {
        this.taxableBase = taxableBase;
    }

    @Transient
    public DiscountExpression getDiscountExpression() {
    	return new DiscountExpression("0.0");
    }

    @Transient
    public double getTaxes() {
    	return 0;
    }

    @Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final ProjectReservationService o = (ProjectReservationService) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.description, o.description)
				.append(this.effectiveDate, o.effectiveDate)
				.append(this.item, o.item)
				.append(this.price, o.price)
				.append(this.projectReservation, o.projectReservation)
				.append(this.quantity, o.quantity)
				.append(this.serviceIndex, o.serviceIndex)
				.append(this.taxableBase, o.taxableBase)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(description)
			.append(effectiveDate)
			.append(id)
			.append(item)
			.append(price)
			.append(projectReservation)
			.append(quantity)
			.append(serviceIndex)
			.append(taxableBase)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}
	
}