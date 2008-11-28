package com.code.aon.warehouse;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.code.aon.common.ITransferObject;
import com.code.aon.company.resources.Employee;

@Entity
@Table(name="delivery_detail_labour")
public class DeliveryDetailLabour implements ITransferObject{

	private static final long serialVersionUID = 1830895777162249227L;

	private Integer id;
	
	private DeliveryDetail deliveryDetail;
	
	private Employee employee;
	
	private double quantity;

	@Id
	@GeneratedValue
	@Column(nullable=false)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@ManyToOne
    @JoinColumn(name="delivery_detail", nullable = false)
	public DeliveryDetail getDeliveryDetail() {
		return deliveryDetail;
	}

	public void setDeliveryDetail(DeliveryDetail deliveryDetail) {
		this.deliveryDetail = deliveryDetail;
	}

	@ManyToOne
    @JoinColumn(name="employee")
	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public double getQuantity() {
		return quantity;
	}

	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}

    @Override
    public boolean equals(Object obj) {
    	if(id == null){
    		return super.equals(obj);
    	}
        if (obj instanceof DeliveryDetailLabour) {
            return (this.id.equals(((DeliveryDetailLabour)obj).getId()));
        }
        return false;
    }

}