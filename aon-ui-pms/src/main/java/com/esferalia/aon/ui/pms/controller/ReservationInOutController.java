package com.esferalia.aon.ui.pms.controller;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

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
import com.esferalia.aon.pms.enumeration.ReservationStatus;

public class ReservationInOutController implements ICollectionProvider {
	
	private Hotel hotel;
	private boolean checkin;
	private Date fromDate;
	private Date toDate;
	private Integer shortOption;
	
	private DataModel model;
	private List<ListItem> list;
	
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
		String select = "SELECT pr.project, prr.id, iF(isnull(a.name),'---',a.name), prr.item, prg.name"
			+ " FROM project_reservation as pr"
			+ " LEFT JOIN project_reservation_room AS prr ON prr.project_reservation=pr.project"
			+ " LEFT JOIN project_reservation_guest AS prg ON prg.project_reservation=pr.project"
			+ " LEFT JOIN project_reservation_room_detail AS prrd ON prrd.project_reservation_room=prr.id"
			+ " LEFT JOIN asset_activity AS aa ON aa.id=prrd.asset_activity" 
			+ " LEFT JOIN asset AS a ON a.id=aa.asset"
			+ " LEFT JOIN registry as ar on ar.id = pr.agency"
			+ " WHERE pr.status <> " + ReservationStatus.CANCELLED.ordinal()
			+ ( getHotel() != null ? " AND pr.hotel = " + getHotel().getId():"" )
			+ " AND pr."+(isCheckin() ?"start_date":"end_date")+" BETWEEN :start AND :end"
			+ " GROUP BY pr.project, prr.id"
			+ getOrder()
			;
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
	
	public String getOrder( ) {
		String order = " ORDER BY";
		if(shortOption.equals(SortType.RESERVATION.ordinal())){
			order += " pr.project";
		} else if(shortOption.equals(SortType.GUEST.ordinal())){
			order += " prg.name";
		} else if(shortOption.equals(SortType.AGENCY_AND_GUEST.ordinal())){
			order += " ar.name, prg.name";
		} else if(shortOption.equals(SortType.AGENCY.ordinal())){
			order += " ar.name";
		} else if(shortOption.equals(SortType.ROOM_NUMBER.ordinal())){
			order += " iF(isnull(a.name),'ZZZZZZZZ',a.name)";
		}
		return order;
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
	
	enum SortType {
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
