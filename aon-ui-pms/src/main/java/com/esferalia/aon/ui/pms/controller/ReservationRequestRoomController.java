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

import com.code.aon.common.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.dbutils.DatabaseUtil;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.ProjectReservation;
import com.esferalia.aon.pms.ReservationRequest;
import com.esferalia.aon.pms.ReservationRequestRoom;
import com.esferalia.aon.pms.reservation.AvailableRoomStay;
import com.esferalia.aon.pms.reservation.ReservationRequestManager;
import com.esferalia.aon.pms.sql.SQLStopSales;
import com.esferalia.aon.pms.sql.SQLUtils;
import com.esferalia.aon.ui.pms.util.PmsUtils;

public class ReservationRequestRoomController extends LinesController implements IPmsConstants {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private Map<Integer,List<AvailableRoomStay>> availableRoomStayMap;

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

	private boolean mustStopSale(ReservationRequestRoom requestRoom) {
   		Connection connection = null;
		try {
			connection = DatabaseUtil.getConnection(CommonUtil.getDomainName(requestRoom.getReservationRequest().getDomain()));
			if (SQLStopSales.mustStopSale(connection, requestRoom)) {
				return true;
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
			} else {
				AonUtil.addErrorMessage("Hay un Paro de Ventas definido para el Hotel en ese periodo y condiciones.");
			}
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

					//manager.processNewReservation(requestRoom);
				}
			} else {
				AonUtil.addErrorMessage("Hay un Paro de Ventas definido para el Hotel en ese periodo y condiciones.");
			}
		}
	}

	public void onLoadRoomReservation(ActionEvent event) throws ManagerBeanException {
		if (getModel().isRowAvailable()) {
			ReservationRequestRoom room = (ReservationRequestRoom)this.getModel().getRowData();
			Integer reservationId = null;
			IManagerBean reservationBean = BeanManager.getManagerBean(ProjectReservation.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(reservationBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_CRS_CODE), room.getCrsCode());
			for (ITransferObject ito : reservationBean.getList(criteria)) {
				ProjectReservation reservation = (ProjectReservation)ito;
				reservationId = reservation.getId();
				break;
			}

			if (reservationId != null) {
				BasicController reservationController = (BasicController)AonUtil.getRegisteredBean(IPmsConstants.RESERVATION_CONTROLLER_NAME);
				reservationController.onLoad(event, reservationId, RESERVATION_REQUEST_FORM_NAME, null);
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
			IManagerBean reservationBean = BeanManager.getManagerBean(ProjectReservation.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(reservationBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_CRS_CODE), room.getCrsCode());
			page = (reservationBean.getCount(criteria) > 0) ? RESERVATION_FORM_NAME : "";
		}
		return page;
	}

}