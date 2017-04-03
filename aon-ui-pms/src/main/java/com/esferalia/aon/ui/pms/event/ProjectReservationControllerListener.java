package com.esferalia.aon.ui.pms.event;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.product.pricing.ItemPricesManager;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.product.strategy.PriceStrategyFactory;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.ProjectReservation;
import com.esferalia.aon.pms.ProjectReservationGuest;
import com.esferalia.aon.pms.ProjectReservationRoom;
import com.esferalia.aon.pms.ProjectReservationService;
import com.esferalia.aon.pms.enumeration.BookingHolder;
import com.esferalia.aon.pms.enumeration.ReservationCheckStatus;
import com.esferalia.aon.pms.enumeration.ReservationSource;
import com.esferalia.aon.pms.enumeration.ReservationStatus;
import com.esferalia.aon.pms.reservation.InventoryManager;
import com.esferalia.aon.pms.reservation.ReservationUtils;
import com.esferalia.aon.ui.pms.controller.IPmsConstants;
import com.esferalia.aon.ui.pms.controller.ProjectReservationController;

public class ProjectReservationControllerListener extends ControllerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		ProjectReservationController controller = (ProjectReservationController)event.getController();
		ProjectReservation reservation = (ProjectReservation)controller.getTo();
		reservation.setStartDate(DateUtils.truncate(new Date(), Calendar.DATE));
		reservation.setEndDate(DateUtils.addDays(reservation.getStartDate(), 1));
		reservation.setSource(ReservationSource.MANUAL);
		reservation.setCheckStatus(ReservationCheckStatus.NO_CHECK);
		reservation.setStatus(ReservationStatus.ACTIVE);
		reservation.setLastConexFlowOperation(null);

		try {
			controller.getReservationPermission().setReservation(reservation);
			controller.setReservationConexFlow(null);
			controller.resetHotel();
			controller.resetStartTime();
			controller.resetEndTime();
			controller.resetNights();
			controller.resetGuestName();
			controller.resetNewRoom();
			controller.resetNewTariff();
			controller.resetNewService();
			controller.resetNewServiceDetail();
			controller.resetNewExtraPax();
			controller.resetNewExtraPaxDetail();
			controller.setInvoiceModel(null);
		} catch(ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		ProjectReservationController controller = (ProjectReservationController)event.getController();
		ProjectReservation reservation = (ProjectReservation)controller.getTo();
		reservation.setLastConexFlowOperation(null);

		try {
			controller.getReservationPermission().setReservation(reservation);
			controller.setReservationConexFlow(null);
			controller.resetStartTime();
			controller.resetEndTime();
			controller.resetNights();
			controller.checkMultipleReservation();
			controller.setInvoiceModel(null);
		} catch(ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		ProjectReservationController controller = (ProjectReservationController)event.getController();
		ProjectReservation reservation = (ProjectReservation)controller.getTo();
		validateReservation(reservation);
		reservation.setStartTime(controller.obtainStartTime());
		reservation.setEndTime(controller.obtainEndTime());
		reservation.setHotelReservation(reservation.getHotel());
		try {
			IPriceStrategy priceStrategy = PriceStrategyFactory.getPriceStrategy();
			ItemPricesManager pricesManager = new ItemPricesManager();
			if (controller.getNewService().getItem() != null && controller.getNewServiceDetail().getEditableSalesPrice() != 0) {
				reservation.setVatPercent(controller.getNewService().getItem().getProduct().getVat().getDatedPercentage(reservation.getDate()));
				double price = pricesManager.getPrice(reservation.getVatPercent(), 0, controller.getNewServiceDetail().getEditableSalesPrice(), 4);
				controller.getNewServiceDetail().setProjectReservationService(controller.getNewService());
				controller.getNewServiceDetail().setPrice(price);
				controller.getNewServiceDetail().setTaxableBase(priceStrategy.getBasePrice(controller.getNewServiceDetail()));

				reservation.setTaxableBase(CommonUtil.round(controller.getNewServiceDetail().getTaxableBase() * reservation.getNights(), 4));
				reservation.setVatQuota(CommonUtil.round(reservation.getTaxableBase() * reservation.getVatPercent() / 100));
			}
			if (controller.getNewExtraPax().getItem() != null && controller.getNewExtraPaxDetail().getEditableSalesPrice() != 0) {
				double vatPercent = controller.getNewExtraPax().getItem().getProduct().getVat().getDatedPercentage(reservation.getDate());
				double price = pricesManager.getPrice(vatPercent, 0, controller.getNewExtraPaxDetail().getEditableSalesPrice(), 4);
				controller.getNewExtraPaxDetail().setProjectReservationService(controller.getNewExtraPax());
				controller.getNewExtraPaxDetail().setPrice(price);
				controller.getNewExtraPaxDetail().setTaxableBase(priceStrategy.getBasePrice(controller.getNewExtraPaxDetail()));

				double base = CommonUtil.round(controller.getNewExtraPaxDetail().getTaxableBase() * reservation.getNights(), 4);
				reservation.setTaxableBase(CommonUtil.round(reservation.getTaxableBase() + base, 4));
				if (vatPercent == reservation.getVatPercent()) {
					reservation.setVatQuota(CommonUtil.round(reservation.getTaxableBase() * reservation.getVatPercent() / 100));
				} else {
					double vatQuota = CommonUtil.round(base * vatPercent / 100);
					reservation.setVatQuota(CommonUtil.round(reservation.getVatQuota() + vatQuota));
				}
			}
			reservation.setTaxableBase(CommonUtil.round(reservation.getTaxableBase()));
			reservation.setTotal(CommonUtil.round(reservation.getTaxableBase() + reservation.getVatQuota()));

			String penaltyValue = null;
			if (controller.getNewRoom().getTariff() != null) {
				Integer tariffId = controller.getNewRoom().getTariff().getId();
				penaltyValue = controller.getReservationUtils().obtainCancellationPenaltyValue(reservation, tariffId, reservation.getDate());
			} else {
				penaltyValue = controller.getReservationUtils().obtainCancellationPenaltyValue(reservation, reservation.getDate());
			}
			if (penaltyValue != null) {
				reservation.setPenaltyAmount(reservation.getAutoCancellationPenaltyPrice(penaltyValue, true));
				reservation.setPenaltyDate(controller.getReservationUtils().obtainCancellationPenaltyDate(reservation));
			}
		} catch (ManagerBeanException ex) {
			throw new ControllerListenerException(ex.getMessage(), ex);
		}
	}

	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		ProjectReservationController controller = (ProjectReservationController)event.getController();
		ProjectReservation reservation = (ProjectReservation)controller.getTo();
		try {
			insertProjectReservationGuest(reservation, controller.getGuestName(), controller.getGuestSurname());
			if (controller.getNewRoom() != null && controller.getNewRoom().getItem() != null) {
				controller.getNewRoom().setRoomIndex(1);
				insertProjectReservationRoom(reservation, controller.getNewRoom());
			}
			if (controller.getNewService() != null && controller.getNewService().getItem() != null) {
				controller.getNewService().setServiceIndex(1);
				controller.getNewService().setMealPlan(controller.getReservationUtils().obtainMealPlan(controller.getNewService().getItem().getDetail()));
				controller.getNewService().setProjectReservationRoom(controller.getNewRoom().getId());
				insertProjectReservationService(reservation, controller.getNewService(), controller.getNewServiceDetail().getQuantity(), 
						controller.getNewServiceDetail().getPrice(), controller.getReservationUtils());
			}
			if (controller.getNewExtraPax() != null && controller.getNewExtraPax().getItem() != null) {
				controller.getNewExtraPax().setServiceIndex(2);
				controller.getNewExtraPax().setMealPlan(controller.getReservationUtils().obtainMealPlan(controller.getNewExtraPax().getItem().getDetail()));
				controller.getNewExtraPax().setProjectReservationRoom(controller.getNewRoom().getId());
				insertProjectReservationService(reservation, controller.getNewExtraPax(), controller.getNewExtraPaxDetail().getQuantity(), 
						controller.getNewExtraPaxDetail().getPrice(), controller.getReservationUtils());
			}
		} catch(ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		ProjectReservationController controller = (ProjectReservationController)event.getController();
		ProjectReservation reservation = (ProjectReservation)controller.getTo();
		validateReservation(reservation);
		reservation.setStartTime(controller.obtainStartTime());
		reservation.setEndTime(controller.obtainEndTime());
		reservation.setForceInventoryControl(true);
	}

	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		ProjectReservation reservation = (ProjectReservation)event.getController().getTo();
		if (reservation.isRefreshRooms()) {
			IController reservationRoomController = FormUtil.getController(IPmsConstants.RESERVATION_ROOM_CONTROLLER_NAME);
			reservationRoomController.onSearch(null);

			reservation.setRefreshRooms(false);
		}
	}

	private void validateReservation(ProjectReservation reservation) throws ControllerListenerException {
		int currentYear = CommonUtil.getYear(new Date());
		if (CommonUtil.getYear(reservation.getStartDate()) > (currentYear + 1) || CommonUtil.getYear(reservation.getStartDate()) < (currentYear - 1)) {
			throw new ControllerListenerException("Fecha de Entrada de la Reserva incorrecta.");
		}
		if (!reservation.getStartDate().before(reservation.getEndDate())) {
			throw new ControllerListenerException("Fecha de Salida de la Reserva incorrecta.");
		}
		if (CommonUtil.getDaysBetweenDates(reservation.getStartDate(), reservation.getEndDate()) > 90) {
			throw new ControllerListenerException("La Estancia no puede ser superior a 90 días.");
		}
		try {
			if (reservation.isDirty()) {
				String message = "La Reserva ha sido modificada por otro usuario. Refrescar para obtener los datos actualizados.";
				throw new ControllerListenerException(message);
			}
			if (StringUtils.isEmpty(reservation.getCrsCode())) {
				IManagerBean reservationBean = BeanManager.getManagerBean(ProjectReservation.class);
				Criteria criteria = new Criteria();
				if (reservation.getId() != null) {
					criteria.addNotEqualExpression(reservationBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ID), reservation.getId());
				}
				criteria.addEqualExpression(reservationBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_CODE), reservation.getCode());
				criteria.addEqualExpression(reservationBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_START_DATE), reservation.getStartDate());
				criteria.addNotEqualExpression(reservationBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_STATUS), ReservationStatus.CANCELLED);
				if (reservation.getAgency() != null && reservation.getAgency().getId() != null) {
					criteria.addEqualExpression(reservationBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_AGENCY_ID), reservation.getAgency().getId());
				} else {
					criteria.addEqualExpression(reservationBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_BOOKING_HOLDER), BookingHolder.GUEST);
				}
				if (reservationBean.getCount(criteria) > 0) {
					StringBuffer message = new StringBuffer();
					message.append("Ya existe una Reserva con ese Localizador y Fecha de Entrada para ");
					if (reservation.getAgency() != null && reservation.getAgency().getId() != null) {
						message.append("la Agencia " + reservation.getAgency().getRegistry().getFullName() + ".");
					} else {
						message.append("un Cliente Directo.");
					}
					throw new ControllerListenerException(message.toString());
				}
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	private void insertProjectReservationGuest(ProjectReservation reservation, String name, String surname) throws ManagerBeanException {
		ProjectReservationGuest reservationGuest = new ProjectReservationGuest();
		reservationGuest.setProjectReservation(reservation);
		reservationGuest.setGuestIndex(1);
		reservationGuest.setName(name);
		reservationGuest.setSurname(surname);
		
		IManagerBean reservationGuestBean = BeanManager.getManagerBean(ProjectReservationGuest.class);
		reservationGuestBean.insert(reservationGuest);
	}

	private void insertProjectReservationRoom(ProjectReservation reservation, ProjectReservationRoom reservationRoom) throws ManagerBeanException {
		reservationRoom.setProjectReservation(reservation);
		reservationRoom = (ProjectReservationRoom)BeanManager.getManagerBean(ProjectReservationRoom.class).insert(reservationRoom);

		sendInventoryData(reservationRoom);
	}

    private void sendInventoryData(ProjectReservationRoom reservationRoom) {
    	InventoryManager manager = new InventoryManager();
    	manager.processInventoryQuery(reservationRoom);
    }

	private void insertProjectReservationService(ProjectReservation reservation, ProjectReservationService reservationService, double quantity, double price,
			ReservationUtils reservationUtils) throws ManagerBeanException {
		reservationService.setProjectReservation(reservation);
		reservationService.setExtra(false);
		reservationService = (ProjectReservationService)BeanManager.getManagerBean(ProjectReservationService.class).insert(reservationService);

		Date fromDate = reservation.getStartDate();
		Date toDate = DateUtils.addDays(reservation.getEndDate(), -1);
		reservationUtils.insertProjectReservationServiceDetails(reservationService, fromDate, toDate, quantity, price, null, null);
	}

}