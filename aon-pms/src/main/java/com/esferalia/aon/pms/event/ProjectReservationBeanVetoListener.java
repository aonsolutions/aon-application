package com.esferalia.aon.pms.event;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.customer.Customer;
import com.code.aon.product.strategy.BasicPriceStrategy;
import com.esferalia.aon.pms.ProjectReservation;
import com.esferalia.aon.pms.reservation.ReservationUtils;

public class ProjectReservationBeanVetoListener extends ManagerBeanVetoListenerAdapter {

    @Override
    public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
    	ProjectReservation to = (ProjectReservation)evt.getTo();
    	ReservationUtils reservationUtils = new ReservationUtils();
    	try {
    		reservationUtils.fillProject(to);
    		calculateReservationTotals(to);
    	} catch (ManagerBeanException ex) {
    		throw new ManagerBeanVetoListenerException(ex.getMessage(), ex);
    	}
    }

    @Override
    public void vetoableBeanUpdated(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
    	ProjectReservation to = (ProjectReservation)evt.getTo();
    	ReservationUtils reservationUtils = new ReservationUtils();
    	try {
    		reservationUtils.fillProject(to);
    		calculateReservationTotals(to);
    	} catch (ManagerBeanException ex) {
    		throw new ManagerBeanVetoListenerException(ex.getMessage(), ex);
    	}
    }

	private void calculateReservationTotals(ProjectReservation reservation) throws ManagerBeanException {
		Customer customer = (Customer)BeanManager.getManagerBean(Customer.class).get(reservation.getProject().getRegistry().getId());
		BasicPriceStrategy strategy = new BasicPriceStrategy();
		double taxableBase = strategy.getTaxableBase(reservation);
		double vatQuota = strategy.getTotalVatQuota(reservation, customer);

		reservation.setTaxableBase(taxableBase);
		reservation.setVatQuota(vatQuota);
		reservation.setTotal(CommonUtil.round(taxableBase + vatQuota));
	}

}
