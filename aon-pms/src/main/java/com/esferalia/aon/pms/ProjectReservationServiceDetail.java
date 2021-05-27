package com.esferalia.aon.pms;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.WorkPlace;
import com.code.aon.product.Item;
import com.code.aon.product.pricing.ItemPricesManager;
import com.code.aon.product.strategy.ICalculable;
import com.code.aon.product.util.DiscountExpression;
import com.esferalia.aon.entity.master.ProjectReservationServiceDetailDB;

@Entity
@Table(name="project_reservation_service_detail")
public class ProjectReservationServiceDetail extends ProjectReservationServiceDetailDB implements ICalculable {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private double editableSalesPrice;

	@Transient
	public double getEditableSalesPrice() {
		return editableSalesPrice;
	}
	public void setEditableSalesPrice(double editableSalesPrice) {
		this.editableSalesPrice = editableSalesPrice;
	}

	@Transient
    public ProjectReservation getProjectReservation() {
    	return getProjectReservationService().getProjectReservation();
    }

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
    public WorkPlace getWorkPlace() {
    	return getProjectReservationService().getProjectReservation().getHotel().getWorkPlace();
    }

    @Transient
    public double getSalesPrice() throws ManagerBeanException {
		ItemPricesManager pricesManager = new ItemPricesManager();
		double vatPercent = getItem().getProduct().getVat().getDatedPercentage(getProjectReservationService().getProjectReservation().getStartDate());
		return pricesManager.getSalesPrice(vatPercent, 0, getPrice());
	}

    @Transient
    public double getTotalPrice() throws ManagerBeanException {
		ItemPricesManager pricesManager = new ItemPricesManager();
		double vatPercent = getItem().getProduct().getVat().getDatedPercentage(getProjectReservationService().getProjectReservation().getStartDate());
		return pricesManager.getSalesPrice(vatPercent, 0, getTaxableBase());
	}

    @Transient
    public boolean hasProduction() {
    	return getTaxableBaseProduction() != 0;
    }

}