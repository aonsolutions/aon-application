package com.esferalia.aon.pms;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.aon.product.strategy.ICalculable;
import com.code.aon.product.util.DiscountExpression;
import com.esferalia.aon.entity.master.ProjectReservationServiceDB;

@Entity
@Table(name="project_reservation_service")
public class ProjectReservationService extends ProjectReservationServiceDB implements ICalculable {

	private static final long serialVersionUID = 1L;

    @Transient
    public DiscountExpression getDiscountExpression() {
    	return new DiscountExpression("0.0");
    }

    @Transient
    public double getTaxes() {
    	return 0;
    }

}