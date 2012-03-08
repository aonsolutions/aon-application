package com.esferalia.aon.ui.pms.controller;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import org.hibernate.Query;
import org.hibernate.Session;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.config.User;
import com.code.aon.config.UserScope;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.config.util.UserUtils;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.Hotel;
import com.esferalia.aon.pms.enumeration.ReservationStatus;

public class ReservationInOutController implements ICollectionProvider {
	
	private Hotel hotel;
	private boolean checkin;
	private Date fromDate;
	private Date toDate;
	
	private Integer shortOption;
	
	private DataModel model;
	private List<ITransferObject> list;
	
	public List<ITransferObject> getList() {
		return list;
	}

	public void setList(List<ITransferObject> list) {
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
	
	public void onInit(ActionEvent event) throws ManagerBeanException{
		setShortOption(0);
		setFromDate(new Date());
		setToDate(new Date());
	}
	
	@SuppressWarnings("unchecked")
	public void onSearch(ActionEvent event) throws ManagerBeanException{
		
		String order = " ORDER BY";
		if(shortOption.equals(SortTypes.RESERVATION.ordinal())){
			order += " pReservationRoomDetail.projectReservationRoom.projectReservation.id";
		} else if(shortOption.equals(SortTypes.AGENCY.ordinal())){
			order += " pReservationRoomDetail.projectReservationRoom.projectReservation.agency.id";
		} else if(shortOption.equals(SortTypes.ROOM_NUMBER.ordinal())){
			order += " pReservationRoomDetail.assetActivity.asset.name";
		}

		String select = "SELECT pReservationRoom" 
				+ " FROM ProjectReservationRoomDetail pReservationRoomDetail" 
				+ " LEFT JOIN pReservationRoomDetail.projectReservationRoom as pReservationRoom"
				+ " WHERE pReservationRoomDetail.projectReservationRoom.projectReservation.status <> " + ReservationStatus.CANCELLED.ordinal()
				+ ( getHotel() != null ? " AND pReservationRoomDetail.projectReservationRoom.projectReservation.hotel = " + getHotel().getId():"" )
				+ ( isCheckin() ? " AND pReservationRoomDetail.projectReservationRoom.projectReservation.startDate BETWEEN :start AND :end":" AND pReservationRoomDetail.projectReservationRoom.projectReservation.endDate BETWEEN :start AND :end")
				+ getScopeClause()
				+ order
				;

		Session session = HibernateUtil.getSession(HibernateUtil.getSessionFactoryName());
		Query query = session.createQuery(select);
		query.setDate("start", new java.sql.Date(getFromDate().getTime()));
		query.setDate("end", new java.sql.Date(getToDate().getTime()));
		setList(query.list());
		setModel(new ListDataModel(getList()));
	}

	private static List<ITransferObject> obtainUserScopeList(User user) throws ManagerBeanException {
		IManagerBean userScopeBean = BeanManager.getManagerBean(UserScope.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(userScopeBean.getFieldName(IEntityAlias.USER_SCOPE_USER_ID), user.getId());
		return userScopeBean.getList(criteria);
	}
	
	public String getScopeClause( ) throws ManagerBeanException {
		String alias = "pReservationRoom.projectReservation.hotel.scope.id";
		String exp = null;
		User user = UserUtils.getInstance().getLoggedUser();
		if (user != null) {
			List<ITransferObject> list = obtainUserScopeList(user);
			if (! list.isEmpty() ) {
				for( ITransferObject to : list ) {
					UserScope userScope = (UserScope) to;
					if(exp==null){
						exp = " AND ( "+alias+" = "+userScope.getScope().getId();
					} else {
						exp += " OR "+alias+" = "+userScope.getScope().getId();
					}
					exp += " ) ";
				}
			}
		}
		return exp;
		
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
	
	enum SortTypes {
		RESERVATION,
		GUEST,
		AGENCY_AND_GUEST,
		AGENCY,
		ROOM_NUMBER
		;
	}
	
}
