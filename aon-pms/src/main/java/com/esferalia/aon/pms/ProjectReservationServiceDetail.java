package com.esferalia.aon.pms;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.product.Item;
import com.code.aon.product.pricing.ItemPricesManager;
import com.code.aon.product.strategy.ICalculable;
import com.code.aon.product.util.DiscountExpression;
import com.esferalia.aon.entity.master.ProjectReservationServiceDetailDB;
import com.esferalia.aon.pms.reservation.ReservationUtils;

@Entity
@Table(name="project_reservation_service_detail")
public class ProjectReservationServiceDetail extends ProjectReservationServiceDetailDB implements ICalculable {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

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

    @Transient
    public double getSalesPrice() throws ManagerBeanException {
        ReservationUtils reservationUtils = new ReservationUtils();
		double vatPercent = reservationUtils.getTaxPercentage(getItem().getProduct().getVat(), getProjectReservationService().getProjectReservation().getStartDate());

		ItemPricesManager pricesManager = new ItemPricesManager();
		return pricesManager.getSalesPrice(vatPercent, 0, getPrice());
	}

    @Transient
    public double getTotalPrice() throws ManagerBeanException {
        ReservationUtils reservationUtils = new ReservationUtils();
		double vatPercent = reservationUtils.getTaxPercentage(getItem().getProduct().getVat(), getProjectReservationService().getProjectReservation().getStartDate());

		ItemPricesManager pricesManager = new ItemPricesManager();
		return pricesManager.getSalesPrice(vatPercent, 0, getTaxableBase());
	}

}