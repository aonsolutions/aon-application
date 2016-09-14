package com.esferalia.aon.pms.event;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.time.DateUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.product.Item;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ql.ProjectionList;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.Hotel;
import com.esferalia.aon.pms.ProjectReservation;
import com.esferalia.aon.pms.ProjectReservationRoom;
import com.esferalia.aon.pms.reservation.InventoryManager;
import com.esferalia.aon.pms.reservation.ReservationUtils;

public class ProjectReservationBeanVetoListener extends ManagerBeanVetoListenerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

    @Override
    public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
    	ProjectReservation to = (ProjectReservation)evt.getTo();
    	ReservationUtils reservationUtils = new ReservationUtils();
    	try {
    		reservationUtils.fillProject(to);
    		calculateReservationTotals(reservationUtils, to);
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
    		calculateReservationTotals(reservationUtils, to);
    		checkInventoryChanges(to);
    		if (!to.isForceRefreshBooking()) {
        		to.setForceRefreshBooking(to.isEarlyCheckOut() || to.isCancelled() || to.isNoShow());
        	}
    	} catch (ManagerBeanException ex) {
    		throw new ManagerBeanVetoListenerException(ex.getMessage(), ex);
    	}
    }

	private void calculateReservationTotals(ReservationUtils reservationUtils, ProjectReservation reservation) throws ManagerBeanException {
		if (reservation.isForceCalculateTotals()) {
			double taxableBase = reservationUtils.getReservationCalculatedTaxableBase(reservation);
			double vatQuota = reservationUtils.getReservationCalculatedVatQuota(reservation);

			reservation.setTaxableBase(taxableBase);
			reservation.setVatQuota(vatQuota);
			reservation.setTotal(CommonUtil.round(taxableBase + vatQuota));
		}
	}

	private void checkInventoryChanges(ProjectReservation reservation) throws ManagerBeanException {
		IManagerBean reservationBean = BeanManager.getManagerBean(ProjectReservation.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(reservationBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ID), reservation.getId());
		Projection prjHotel = Projection.property(reservationBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_HOTEL_ID));
		Projection prjStart = Projection.property(reservationBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_START_DATE));
		Projection prjEnd = Projection.property(reservationBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_END_DATE));
		Projection prjAgency = Projection.property("ProjectReservation.agency<id");
		Object[] objs = (Object[])reservationBean.getUniqueResult(new ProjectionList(prjHotel, prjStart, prjEnd, prjAgency), criteria);

		boolean hotelChanged = !reservation.getHotel().getId().equals((Integer)objs[0]);
		boolean startChanged = !DateUtils.isSameDay(reservation.getStartDate(), (Date)objs[1]);
		boolean endChanged = !DateUtils.isSameDay(reservation.getEndDate(), (Date)objs[2]);
		Integer agency = (reservation.getAgency() != null && reservation.getAgency().getId() != null) ? reservation.getAgency().getId() : null;
		boolean agencyChanged = !ObjectUtils.equals(agency, objs[3]);

		if (hotelChanged || startChanged || endChanged || agencyChanged) {
			reservation.setForceRefreshBooking(true);

			ReservationUtils reservationUtils = new ReservationUtils(reservation.getDomain());
			String oldRateCode = reservation.getAllotmentRateCode();
			String newRateCode = (agencyChanged) ? reservationUtils.obtainAllotmentRateCode(reservation) : oldRateCode;
			boolean rateCodeChanged = (agencyChanged) ? !ObjectUtils.equals(newRateCode, oldRateCode) : false;
			if (hotelChanged || startChanged || endChanged || rateCodeChanged) {
				Map<Item, Integer> inventoryItemMap = new HashMap<Item, Integer>();
				for (ProjectReservationRoom reservationRoom : reservation.getReservationRoomList()) {
					int count = (inventoryItemMap.containsKey(reservationRoom.getItem())) ? inventoryItemMap.get(reservationRoom.getItem()) : 0;
					inventoryItemMap.put(reservationRoom.getItem(), ++count);
				}

				/** Si ha cambiado el Hotel o el Cupo hay que enviar todo a +1 con los datos viejos y a -1 con los nuevos.
				Si unicamente han cambiado las Fechas, hay que enviar las fechas viejas que ya no estan en la Reserva a +1 y las nuevas a -1 */
				if (!hotelChanged && !rateCodeChanged) {
					for(Date date=(Date)objs[1]; date.before((Date)objs[2]); date=DateUtils.addDays(date, 1)) {
						if (date.before(reservation.getStartDate()) || !date.before(reservation.getEndDate())) {
							sendInventoryData(reservation.getHotel(), oldRateCode, date, date, inventoryItemMap, 1);
						}
					}

					for(Date date=reservation.getStartDate(); date.before(reservation.getEndDate()); date=DateUtils.addDays(date, 1)) {
						if (date.before((Date)objs[1]) || !date.before((Date)objs[2])) {
							sendInventoryData(reservation.getHotel(), oldRateCode, date, date, inventoryItemMap, -1);
						}
					}
				} else {
					Hotel oldHotel = (hotelChanged) ? (Hotel)BeanManager.getManagerBean(Hotel.class).get((Integer)objs[0]) : reservation.getHotel();
					sendInventoryData(oldHotel, oldRateCode, (Date)objs[1], DateUtils.addDays((Date)objs[2], -1), inventoryItemMap, 1);

					Date endDate = DateUtils.addDays(reservation.getEndDate(), -1);
					sendInventoryData(reservation.getHotel(), newRateCode, reservation.getStartDate(), endDate, inventoryItemMap, -1);
				}

				/** Si ha cambiado el Cupo hay que grabar el nuevo Cupo en las Habitaciones. Esto se hace lo ultimo, ya que hay que hacerlo asi
				para usar los datos antiguos del Booking, que todavia no se han modificado, se modificaran en el ProjectReservationBeanListener.*/
				if (rateCodeChanged) {
					for (ProjectReservationRoom reservationRoom : reservation.getReservationRoomList()) {
						reservationRoom.setAllotmentRateCode(newRateCode);
						BeanManager.getManagerBean(ProjectReservationRoom.class).update(reservationRoom);
					}
					reservation.setRefreshRooms(true);
				}
			}
		}
	}

    private void sendInventoryData(Hotel hotel, String rateCode, Date startDate, Date endDate, Map<Item, Integer> inventoryItemMap, int factor) {
    	InventoryManager manager = new InventoryManager();
		for (Item item : inventoryItemMap.keySet()) {
	    	manager.processInventoryQuery(hotel, item, rateCode, startDate, endDate, inventoryItemMap.get(item) * factor);
		}
    }

}
