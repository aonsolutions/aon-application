package com.esferalia.aon.pms;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.aon.product.Item;
import com.code.aon.product.strategy.ICalculable;
import com.code.aon.product.util.DiscountExpression;
import com.esferalia.aon.entity.master.ProjectReservationServiceDetailDB;

@Entity
@Table(name="project_reservation_service_detail")
public class ProjectReservationServiceDetail extends ProjectReservationServiceDetailDB implements ICalculable {

	private static final long serialVersionUID = 1L;

    @Transient
    public Item getItem() {
    	return getProjectReservationService().getItem();
    }

    @Transient
    public DiscountExpression getDiscountExpression() {
    	return new DiscountExpression("0.0");
    }

    @Transient
    public double getTaxes() {
    	return 0;
    }

}