package com.esferalia.aon.ui.pms.controller;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.util.AdminUtil;
import net.aonsolutions.core.dbutils.DatabaseUtil;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.ReservationRequest;
import com.esferalia.aon.pms.ReservationRequestRoom;
import com.esferalia.aon.pms.reservation.AvailableRoomStay;
import com.esferalia.aon.pms.reservation.ReservationRequestManager;
import com.esferalia.aon.pms.reservation.ReservationUtils;
import com.esferalia.aon.pms.sql.SQLStopSales;
import com.esferalia.aon.pms.sql.SQLUtils;
import com.esferalia.aon.ui.pms.util.PmsUtils;

public class ReservationRequestRoomController extends LinesController implements IPmsConstants {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private ReservationUtils reservationUtils;
	private Map<Integer,List<AvailableRoomStay>> availableRoomStayMap;

	public ReservationUtils getReservationUtils() {
		if (reservationUtils == null) {
			reservationUtils = new ReservationUtils(DomainManager.getCurrentDomain());
		}
		return reservationUtils;
	}

	public Map<Integer,List<AvailableRoomStay>> getAvailableRoomStayMap() {
		if (availableRoomStayMap == null) {
			availableRoomStayMap = new HashMap<Integer,List<AvailableRoomStay>>();
		}
		return availableRoomStayMap;
	}
	public void setAvailableRoomStayMap(Map<Integer,List<AvailableRoomStay>> availableRoomStayMap) {
		this.availableRoomStayMap = availableRoomStayMap;
	}

	public boolean isAvailabilityRequested() {
		return (getAvailableRoomStayMap().size() > 0);
	}

	public List<SelectItem> getHotelRoomItems() throws ManagerBeanException {
		return PmsUtils.getHotelRoomItems(((ReservationRequest)getMasterController().getTo()).getHotel());
	}

	public boolean isAgreedPriceVisible() throws ManagerBeanException {
		if (isAgreedPriceEditable()) {
			return true;
		}
		ReservationRequest request = (ReservationRequest)getMasterController().getTo();
		IManagerBean requestRoomBean = BeanManager.getManagerBean(ReservationRequestRoom.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(requestRoomBean.getFieldName(IEntityAlias.RESERVATION_REQUEST_ROOM_RESERVATION_REQUEST_ID), request.getId());
		criteria.addGreaterThanExpression(requestRoomBean.getFieldName(IEntityAlias.RESERVATION_REQUEST_ROOM_AGREED_PRICE), Double.valueOf(0));
		return requestRoomBean.getCount(criteria) > 0;
	}

	public boolean isAgreedPriceEditable() throws ManagerBeanException {
		return AonUtil.getRoleManager().isAuditor();
	}

	private boolean isStopSalesDefined(ReservationRequest request) {
		Connection connection = null;
		try {
			connection = DatabaseUtil.getConnection(AdminUtil.getDomainName(request.getDomain()));
			return SQLStopSales.isStopSalesDefined(connection, null, request.getHotel(), request.getStartDate(), request.getEndDate(), null, null);
		} catch (Throwable e) {
			try {
				connection.rollback();
			} catch (SQLException ex) {
			}
		} finally {
			SQLUtils.closeQuietly(connection);
		}
		return false;
	}

	private boolean mustStopSale(ReservationRequestRoom requestRoom, String itemCode, String tariffCode) {
   		Connection connection = null;
		try {
			connection = DatabaseUtil.getConnection(AdminUtil.getDomainName(requestRoom.getReservationRequest().getDomain()));
			if (StringUtils.isNotBlank(itemCode) || StringUtils.isNotBlank(tariffCode)) {
				return SQLStopSales.mustStopSale(connection, requestRoom, itemCode, tariffCode);
			} else {
				return SQLStopSales.mustStopSale(connection, requestRoom);
			}
		} catch (Throwable e) {
			try {
				connection.rollback();
			} catch (SQLException ex) {
			}
		} finally {
			SQLUtils.closeQuietly(connection);
		}
		return false;
	}

	private boolean mustStopSale(ReservationRequestRoom requestRoom) {
		return mustStopSale(requestRoom, null, null);
	}

	public void sendAvailabilityQuery(ActionEvent event) throws ManagerBeanException {
		if (getModel().isRowAvailable()) {
			ReservationRequestController requestController = (ReservationRequestController)getMasterController();
			ReservationRequest request = (ReservationRequest)requestController.getTo();
			ReservationRequestRoom requestRoom = (ReservationRequestRoom)getModel().getRowData();
			requestRoom.setReservationRequest(request);
			if (!mustStopSale(requestRoom)) {
				request.setRequestCounter(request.getRequestCounter() + 1);
				requestController.setSkipResetAvailabilityMap(true);
				requestController.accept(event);
				requestController.setSkipResetAvailabilityMap(false);

				ReservationRequestManager manager = new ReservationRequestManager();
				getAvailableRoomStayMap().put(requestRoom.getId(), manager.processAvailabilityQuery(requestRoom));
				if (isStopSalesDefined(request)) {
					fillTariffStopSales(requestRoom, getAvailableRoomStayMap().get(requestRoom.getId()));
				}
			} else {
				AonUtil.addErrorMessage("Hay un Paro de Ventas definido para el Hotel en ese periodo y condiciones.");
			}
		}
	}

	private void fillTariffStopSales(ReservationRequestRoom requestRoom, List<AvailableRoomStay> availableRoomStays) {
		Map<String, Boolean> tariffStopSalesMap = new HashMap<String, Boolean>();
		for (AvailableRoomStay availableRoomStay : availableRoomStays) {
			String tariff = availableRoomStay.getTariffCode();
			String item = (!requestRoom.getItem().getProduct().getCode().equals(availableRoomStay.getRoomCode())) ? availableRoomStay.getRoomCode() : null;
			String key = tariff + ((item != null) ? "|" + item : "");

			boolean mustStopSale = false;
			if (tariffStopSalesMap.containsKey(key)) {
				mustStopSale = tariffStopSalesMap.get(key);
			} else {
				mustStopSale = mustStopSale(requestRoom, item, tariff);
				tariffStopSalesMap.put(key, mustStopSale);
			}
			availableRoomStay.setTariffStopSales(mustStopSale);
		}
	}

	public List<AvailableRoomStay> getAvailableRoomStayList() throws ManagerBeanException {
		if (getModel().isRowAvailable()) {
			ReservationRequestRoom requestRoom = (ReservationRequestRoom)getModel().getRowData();
			return getAvailableRoomStayMap().get(requestRoom.getId());
		}
		return null;
	}

	public boolean isIdInAvailableRoomStayMap() throws ManagerBeanException {
		if (getModel().isRowAvailable()) {
			ReservationRequestRoom requestRoom = (ReservationRequestRoom)getModel().getRowData();
			return getAvailableRoomStayMap().containsKey(requestRoom.getId());
		}
		return false;
	}

	public void sendBookingQuery(ActionEvent event) throws ManagerBeanException {
		if (getModel().isRowAvailable()) {
			ReservationRequestController requestController = (ReservationRequestController)getMasterController();
			ReservationRequest request = (ReservationRequest)requestController.getTo();
			ReservationRequestRoom requestRoom = (ReservationRequestRoom)getModel().getRowData();
			ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
			AvailableRoomStay availableRoomStay = getAvailableRoomStayList().get(Integer.parseInt(ec.getRequestParameterMap().get("availableRoomStayIndex")));
			requestRoom.setReservationRequest(request);
			requestRoom.setTariffCode(availableRoomStay.getTariffCode());
			if (getReservationUtils().obtainTariff(requestRoom.getTariffCode()) != null) {
				if (!mustStopSale(requestRoom)) {
					request.setRequestCounter(request.getRequestCounter() + 1);
					requestController.setSkipResetAvailabilityMap(true);
					requestController.accept(event);
					requestController.setSkipResetAvailabilityMap(false);

					ReservationRequestManager manager = new ReservationRequestManager();
					availableRoomStay = manager.processBookingRequest(requestRoom, requestController.getRequestGuest(), availableRoomStay);
					if (!availableRoomStay.isError()) {
						requestRoom.setCrsCode(availableRoomStay.getReservationId());
						requestRoom.setTariffCode(availableRoomStay.getTariffCode());
						requestRoom.setTariffDescription(availableRoomStay.getTariffDescription());
						requestRoom.setInventoryCode(availableRoomStay.getInventoryCode());
						requestRoom.setRoomCode(availableRoomStay.getRoomCode());
						requestRoom.setRoomDescription(availableRoomStay.getRoomDescription());
						requestRoom.setMealPlan(availableRoomStay.getMealPlan());
						requestRoom.setDailyPrice(availableRoomStay.getDailyPrice());
						requestRoom.setTotalPrice(availableRoomStay.getTotalPrice());
						requestRoom.setCancelPenalty(availableRoomStay.getCancelPenalty());
						getManagerBean().update(requestRoom);

						request.setActive(false);
						requestController.setSkipResetAvailabilityMap(true);
						requestController.accept(event);
						requestController.setSkipResetAvailabilityMap(false);

						getAvailableRoomStayMap().remove(requestRoom.getId());

						manager.processNewReservation(requestRoom);
					}
				} else {
					AonUtil.addErrorMessage("Hay un Paro de Ventas definido para el Hotel en ese periodo y condiciones.");
				}
			} else {
				AonUtil.addErrorMessage("La Tarifa no existe en el PMS. Contactar con el Administrador del Sistema.");
			}
		}
	}

	public void onLoadRoomReservation(ActionEvent event) throws ManagerBeanException {
		if (getModel().isRowAvailable()) {
			ReservationRequestRoom room = (ReservationRequestRoom)this.getModel().getRowData();
			if (room.getReservation() != null && room.getReservation().getId() != null) {
				BasicController reservationController = (BasicController)AonUtil.getRegisteredBean(RESERVATION_CONTROLLER_NAME);
				reservationController.onLoad(event, room.getReservation().getId(), RESERVATION_REQUEST_FORM_NAME, null);
			} else {
				String msg = "La Reserva no se encuentra disponible en este momento.";
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			}
		}
	}

	public String getReservationPage() throws ManagerBeanException {
		String page = "";
		if (getModel().isRowAvailable()) {
			ReservationRequestRoom room = (ReservationRequestRoom)this.getModel().getRowData();
			page = (room.getReservation() != null && room.getReservation().getId() != null) ? RESERVATION_FORM_NAME : "";
			room.setReservation(null);
		}
		return page;
	}

	public void onCancelRoomReservation(ActionEvent event) throws ManagerBeanException {
		if (getModel().isRowAvailable()) {
			ReservationRequestRoom room = (ReservationRequestRoom)this.getModel().getRowData();
			if (room.getReservation() != null && room.getReservation().getId() != null) {
				ProjectReservationController reservationController = (ProjectReservationController)AonUtil.getRegisteredBean(RESERVATION_CONTROLLER_NAME);
				reservationController.load(event, room.getReservation().getId());
				reservationController.setConfirmNoShow(false);
				reservationController.onCancelReservation(event);
			} else {
				String msg = "La Reserva no se encuentra disponible en este momento.";
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			}
			room.setReservation(null);
		}
	}

}