package com.esferalia.aon.ui.pms.controller;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import org.apache.commons.lang.time.DateUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.product.Item;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.Hotel;

public class BoardBookingController implements ICollectionProvider {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(BoardBookingController.class.getName());
	
	private Hotel hotel;
	private Date fromDate;
	private Date toDate;
	
	private List<Booking> bookingList;
	private DataModel model;
	
	private List<ITransferObject> boardItems;
	
	public Hotel getHotel() {
		return hotel;
	}
	public void setHotel(Hotel hotel) {
		this.hotel = hotel;
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
	public List<Booking> getBookingList() {
		return bookingList;
	}
	public void setBookingList(List<Booking> bookingList) {
		this.bookingList = bookingList;
	}
	public DataModel getModel() {
		return model;
	}
	public void setModel(DataModel model) {
		this.model = model;
	}
	private Integer getBoardCategoryId() {
		// TODO: Id de categoria a pinon. Se asume que la categoria de las pensiones es la de id=4
		return 4;
	}
	
	public void onInit(ActionEvent event) {
		setHotel(null);
		setFromDate(new Date());
		setToDate(new Date());
	}
	
	public void onSearch(ActionEvent event) {
		try {
			buildBookingList();
		} catch (ManagerBeanException e) {
			String msg = "Error al construir el listado de booking";
			LOGGER.error(msg);
			throw new AbortProcessingException(msg, e);
		}
	}
	
	private void buildBookingList() throws ManagerBeanException {
		String select = ""
		+ " ( SELECT W.description, " 
		+ " IF (P.code='001',date(PRSD.effective_date)+ INTERVAL 1 DAY,PRSD.effective_date) AS Fecha,"
		+ " SUM(PRSD.quantity), P.name AS"
		+ " Servicio, P.code"
		+ " FROM project_reservation_service_detail AS PRSD"
		+ " LEFT JOIN project_reservation_service AS PRS ON"
		+ " PRSD.project_reservation_service=PRS.id"
		+ " LEFT JOIN project_reservation AS PR ON PRS.project_reservation=PR.project"
		+ " LEFT JOIN item AS I ON PRS.item=I.id"
		+ " LEFT JOIN hotel AS H ON PR.hotel=H.id"
		+ " LEFT JOIN workplace AS W ON H.workplace=W.id"
		+ " LEFT JOIN item_composition AS IC ON I.id=IC.item"
		+ " LEFT JOIN product AS P ON IC.composition_item=P.id"
		+ " WHERE PRSD.effective_date BETWEEN :start AND :end"
		+ " AND PRS.item<>34 AND P.category=4 AND PR.status<>2"
		+ ( getHotel() != null ? " AND PR.hotel = " + getHotel().getId():"" )
		+ " GROUP BY PR.hotel,PRSD.effective_date,P.code"
		+ " ORDER BY PR.hotel,PRSD.effective_date,P.code )"
		+ " UNION"
		+ " ( SELECT W.description, " 
		+ " IF (P.code='001' OR P.code='001F',date(PRSD.effective_date)+ INTERVAL 1 DAY,PRSD.effective_date) AS Fecha, " 
		+ " SUM(PRSD.quantity), P.name AS"
		+ " Servicio, P.code"
		+ " FROM project_reservation_service_detail AS PRSD"
		+ " LEFT JOIN project_reservation_service AS PRS ON"
		+ " PRSD.project_reservation_service=PRS.id"
		+ " LEFT JOIN project_reservation AS PR ON PRS.project_reservation=PR.project"
		+ " LEFT JOIN item AS I ON PRS.item=I.id"
		+ " LEFT JOIN hotel AS H ON PR.hotel=H.id"
		+ " LEFT JOIN workplace AS W ON H.workplace=W.id"
		+ " LEFT JOIN product AS P ON I.product=P.id"
		+ " WHERE PRSD.effective_date BETWEEN :start AND :end"
		+ " AND PRS.item<>34 AND P.category=4 AND PR.status<>2"
		+ " AND P.composition=0" 
		+ ( getHotel() != null ? " AND PR.hotel = " + getHotel().getId():"" )
		+ " GROUP BY PR.hotel,PRSD.effective_date,P.code"
		+ " ORDER BY PR.hotel,PRSD.effective_date,P.code )"
		+ " ORDER BY 1,2,5";
		Session session = HibernateUtil.getSession(HibernateUtil.getSessionFactoryName());
		Query query = session.createSQLQuery(select);
		query.setDate("start", new java.sql.Date(DateUtils.addDays(getFromDate(),-1).getTime()));
		query.setDate("end", new java.sql.Date(getToDate().getTime()));
		
		
		List<Booking> list = new LinkedList<BoardBookingController.Booking>();
		Booking booking = null;
		
		Calendar fromCal = Calendar.getInstance();
		Calendar toCal = Calendar.getInstance();
		
		for(Object o: query.list()){
			String hotel = (String) (((Object[])o)[0]);
			Date date = (Date) (((Object[])o)[1]);
			Double quantity = (Double) (((Object[])o)[2]);
			String name = (String) (((Object[])o)[3]);
			if(booking != null && booking.getHotel().equals(hotel) && booking.getDate().equals(date)){
				booking.getQuantityList().add(getBoardPosition(name), quantity.intValue());
			} else {
				
				if( booking==null || !booking.getHotel().equals(hotel)){
					fromCal.setTime(getFromDate());
				} else {
					fromCal.setTime(DateUtils.addDays(booking.getDate(),1));
				}
				
				toCal.setTime(date);
				while( fromCal.before(toCal) ){
					booking = new Booking();
					booking.setHotel(hotel);
					booking.setDate(fromCal.getTime());
					booking.setQuantityList(getEmptyList());
					list.add(booking);
					fromCal.add(Calendar.DAY_OF_MONTH, 1);
				}
				
				booking = new Booking();
				booking.setHotel(hotel);
				booking.setDate(date);
				booking.setQuantityList(getEmptyList());
				booking.getQuantityList().add(getBoardPosition(name), quantity.intValue());
				booking.setBoardList(null);
				list.add(booking);
				fromCal.setTime(date);
			}
		}
		list.remove(0);
		setBookingList(list);
		setModel(new ListDataModel(getBookingList()));
		
	}
	
	private List<Integer> getEmptyList() {
		List<Integer> list = new LinkedList<Integer>();
		Iterator<ITransferObject> it = getBoardItems().iterator();
		while(it.hasNext()){
			it.next();
			list.add(0);
		}
		return list;
	}
	private int getBoardPosition(String name) {
		Iterator<ITransferObject> it = getBoardItems().iterator();
		int i = 0;
		while(it.hasNext()){
			Item item = (Item) it.next();
			if(item.getProduct().getName().equals(name)){
				return i;
			}
			++i;
		}
		return -1;
	}
	
	public List<ITransferObject> getBoardItems() {
		if(boardItems==null){
			try {
				IManagerBean bean = BeanManager.getManagerBean(Item.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.ITEM_PRODUCT_CATEGORY_ID), getBoardCategoryId());
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.ITEM_PRODUCT_COMPOSITION), false);
				criteria.addOrder(bean.getFieldName(IEntityAlias.ITEM_PRODUCT_CODE));
				List<ITransferObject> list = bean.getList(criteria);
				return list.isEmpty()?null:list;
			} catch (ManagerBeanException e) {
				String msg =  "******** Error getting board items. ";
				LOGGER.error(msg, e);
				AonUtil.addErrorMessage(msg + e.getMessage());
			}
		}
		return boardItems;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public Collection getCollection() {
		return getBookingList();
	}
	@SuppressWarnings("rawtypes")
	@Override
	public Collection getCollection(boolean forceRefresh)
			throws ManagerBeanException {
		return getCollection();
	}
	
	/**************************************************/
	/**************************************************/
	
	public class Booking {
		private String hotel;
		private Date date;
		private List<Item> boardList;
		private List<Integer> quantityList;
		
		public Booking (){
			
		}
		public String getHotel() {
			return hotel;
		}
		public void setHotel(String hotel) {
			this.hotel = hotel;
		}
		public Date getDate() {
			return date;
		}
		public void setDate(Date date) {
			this.date = date;
		}
		public List<Item> getBoardList() {
			return boardList;
		}
		public void setBoardList(List<Item> boardList) {
			this.boardList = boardList;
		}
		public List<Integer> getQuantityList() {
			return quantityList;
		}
		public void setQuantityList(List<Integer> quantityList) {
			this.quantityList = quantityList;
		}
	
	}
	
}
