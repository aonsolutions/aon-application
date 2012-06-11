package com.esferalia.aon.ui.pms.controller;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

import org.hibernate.Query;
import org.hibernate.Session;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.product.Item;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.pms.Hotel;
import com.esferalia.aon.pms.ProjectReservation;
import com.esferalia.aon.pms.ProjectReservationRoom;
import com.esferalia.aon.pms.enumeration.ReservationCheckStatus;
import com.esferalia.aon.pms.enumeration.ReservationStatus;
import com.esferalia.aon.ui.pms.util.PmsReportManager;

public class ReservationInOutController implements ICollectionProvider {
	
	private Hotel hotel;
	private boolean checkin;
	private Date fromDate;
	private Date toDate;
	private Integer shortOption;
	private ReservationCheckStatus[] checkStatuses;

	private DataModel model;
	private List<ListItem> list;
	
	
	public ReservationCheckStatus[] getCheckStatuses() {
		return checkStatuses;
	}
	public void setCheckStatuses(ReservationCheckStatus[] checkStatuses) {
		this.checkStatuses = checkStatuses;
	}
	
	public List<ListItem> getList() {
		return list;
	}
	public void setList(List<ListItem> list) {
		this.list = list;
	}
	public DataModel getModel() {
		return model;
	}
	public void setModel(DataModel model) {
		this.model = model;
	}
	public Integer getShortOption() {
		return shortOption;
	}
	public void setShortOption(Integer shortOption) {
		this.shortOption = shortOption;
	}
	public Hotel getHotel() {
		return hotel;
	}
	public void setHotel(Hotel hotel) {
		this.hotel = hotel;
	}
	public boolean isCheckin() {
		return checkin;
	}
	public void setCheckin(boolean checkin) {
		this.checkin = checkin;
	}
	public Date getFromDate() {
		return fromDate;
	}
	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}
	public Date getToDate() {
		return toDate;
	}
	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}
	public void onEditSearch(ActionEvent event) {
		onInit(event);
	}

	public void onSelect(ActionEvent event) {
		ProjectReservationController controller = (ProjectReservationController) AonUtil.getRegisteredBean(IPmsConstants.RESERVATION_CONTROLLER_NAME);
		controller.setBackAction(IPmsConstants.RESERVATION_IO_LIST_NAME);
		ListItem row = (ListItem) getModel().getRowData();
		try {
			controller.select(event, row.getProjectReservation());
		} catch (ManagerBeanException e) {
			String msg = "No se ha podido seleccionar la reserva.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}
	
	public void onInit(ActionEvent event) {
		setShortOption(SortType.RESERVATION.ordinal());
		setCheckin(true);
		setFromDate(new Date());
		setToDate(new Date());
	}
	
	public void onSearch(ActionEvent event) throws ManagerBeanException{
		String select = PmsReportManager.getInstance().getReservationInOutSQL(ReservationStatus.CANCELLED, getHotel(), isCheckin(), getCheckStatuses(), shortOption);
		Session session = HibernateUtil.getSession(HibernateUtil.getSessionFactoryName());
		Query query = session.createSQLQuery(select);
		query.setDate("start", new java.sql.Date(getFromDate().getTime()));
		query.setDate("end", new java.sql.Date(getToDate().getTime()));
		List<ListItem> list = new LinkedList<ReservationInOutController.ListItem>();
		for(Object o: query.list()){
			ListItem r = new ListItem();
			r.setReservationId((Integer) (((Object[])o)[0]));
			r.setReservationRoomId((Integer) (((Object[])o)[1]));
			r.setItemId((Integer) (((Object[])o)[3]));
			list.add(r);
		}
		setList(list);
		setModel(new ListDataModel(getList()));
	}
	
	public List<SelectItem> getAbbreviatedReservationCheckStatuses() {
		LinkedList<SelectItem> list = new LinkedList<SelectItem>();
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		for (ReservationCheckStatus status : ReservationCheckStatus.values()) {
			String name = status.getName(locale);
			if (status == ReservationCheckStatus.NO_CHECK) {
				name = "No";
				SelectItem item = new SelectItem(status, name);
				list.add(item);
			} else if (status == ReservationCheckStatus.CHECK_IN) {
				name = "In";
				SelectItem item = new SelectItem(status, name);
				list.add(item);
			} else if (status == ReservationCheckStatus.CHECK_OUT && !isCheckin()) {
				name = "Out";
				SelectItem item = new SelectItem(status, name);
				list.add(item);
			} 
		}
		if (isCheckin()) {
			ReservationCheckStatus[] defaultCheckStatuses = {ReservationCheckStatus.NO_CHECK};
			setCheckStatuses(defaultCheckStatuses);
		} else {
			ReservationCheckStatus[] defaultCheckStatuses = {ReservationCheckStatus.NO_CHECK, ReservationCheckStatus.CHECK_IN};
			setCheckStatuses(defaultCheckStatuses);
		}
		return list;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Collection getCollection() {
		return (Collection) getModel().getWrappedData();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Collection getCollection(boolean forceRefresh)
			throws ManagerBeanException {
		return getCollection();
	}

	/////////////////////////////////////
	/////////////////////////////////////
	
	public enum SortType {
		RESERVATION,
		GUEST,
		AGENCY_AND_GUEST,
		AGENCY,
		ROOM_NUMBER
		;
	}
	
	public class ListItem {
		private Integer itemId;
		private Integer reservationId;
		private Integer reservationRoomId;
		
		public Integer getItemId() {
			return itemId;
		}
		public void setItemId(Integer itemId) {
			this.itemId = itemId;
		}
		public Integer getReservationId() {
			return reservationId;
		}
		public void setReservationId(Integer reservationId) {
			this.reservationId = reservationId;
		}
		public Integer getReservationRoomId() {
			return reservationRoomId;
		}
		public void setReservationRoomId(Integer reservationRoomId) {
			this.reservationRoomId = reservationRoomId;
		}
		public Item getItem() throws ManagerBeanException{
			if(getItemId()!=null){
				IManagerBean bean = BeanManager.getManagerBean(Item.class);
				return (Item) bean.get(getItemId());
			}
			return null;
		}
		public ProjectReservation getProjectReservation() throws ManagerBeanException{
			if(getReservationId()!=null){
				IManagerBean bean = BeanManager.getManagerBean(ProjectReservation.class);
				return (ProjectReservation) bean.get(getReservationId());
			}
			return null;
		}
		public ProjectReservationRoom getProjectReservationRoom() throws ManagerBeanException{
			if(getReservationRoomId()!=null){
				IManagerBean bean = BeanManager.getManagerBean(ProjectReservationRoom.class);
				return (ProjectReservationRoom) bean.get(getReservationRoomId());
			}
			return null;
		}
		
	}
	
}
