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
import com.code.aon.common.util.CommonUtil;
import com.code.aon.product.Item;
import com.code.aon.product.enumeration.ProductStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.Hotel;
import com.esferalia.aon.ui.pms.util.PmsReportManager;

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
		if(CommonUtil.getDaysBetweenDates(fromDate, toDate, false)>15){
			toDate = DateUtils.addDays(fromDate, 15);
		}
		this.fromDate = fromDate;
	}
	public Date getToDate() {
		return toDate;
	}
	public void setToDate(Date toDate) {
		if(toDate.before(fromDate)){
			this.toDate = fromDate;
		} else if(CommonUtil.getDaysBetweenDates(fromDate, toDate, false)>15){
			this.toDate = DateUtils.addDays(fromDate, 15);
		} else {
			this.toDate = toDate;
		}
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
		fromDate = new Date();
		toDate = DateUtils.addDays(new Date(), 10);
	}
	
	public void onSearch(ActionEvent event) {
		try {
			buildBookingList();
		} catch (ManagerBeanException e) {
			String msg = "Error al construir el booking de pensiones";
			LOGGER.error(msg);
			throw new AbortProcessingException(msg, e);
		}
		setModel(new ListDataModel(getBookingList()));
	}
	
	@SuppressWarnings("rawtypes")
	private void buildBookingList() throws ManagerBeanException {
		buildEmptyList(getHotel());
		
		String select = PmsReportManager.getInstance().getBoardBookingSQL(getHotel());
		Session session = HibernateUtil.getSession(HibernateUtil.getSessionFactoryName());
		Query query = session.createSQLQuery(select);
		query.setDate("start", new java.sql.Date(DateUtils.addDays(getFromDate(), -1).getTime()));
		query.setDate("end", new java.sql.Date(getToDate().getTime()));

		Iterator it = query.list().iterator();
				
		Object o = it.hasNext()?it.next():null;
		Integer hotelId = o!=null?(Integer)(((Object[])o)[PmsReportManager.BOARD_HOTEL_NAME]):null;
		Date date = o!=null?(Date) (((Object[])o)[PmsReportManager.BOARD_DATE]):null;
		String code = o!=null?(String) (((Object[])o)[PmsReportManager.BOARD_CODE]):null;
		Double quantity = o!=null?Double.parseDouble((((Object[])o)[PmsReportManager.BOARD_QUANTITY]).toString()):null;
		
		for(Booking booking: getBookingList() ){
			while( booking.getHotelId().equals(hotelId) && booking.getDate().after(date) && it.hasNext() ){
				o = it.next();
				hotelId = o!=null?(Integer)(((Object[])o)[PmsReportManager.BOARD_HOTEL_NAME]):null;
				date = o!=null?(Date) (((Object[])o)[PmsReportManager.BOARD_DATE]):null;
			}

			while( booking.getHotelId().equals(hotelId) && booking.getDate().equals(date) && it.hasNext() ){
				code = o!=null?(String) (((Object[])o)[PmsReportManager.BOARD_CODE]):null;
				quantity = o!=null?Double.parseDouble((((Object[])o)[PmsReportManager.BOARD_QUANTITY]).toString()):null;
				booking.getQuantityList().set(getBoardPosition(code), booking.getQuantityList().get(getBoardPosition(code))+quantity.intValue());
				
				o = it.next();
				hotelId = o!=null?(Integer)(((Object[])o)[PmsReportManager.BOARD_HOTEL_NAME]):null;
				date = o!=null?(Date) (((Object[])o)[PmsReportManager.BOARD_DATE]):null;
			}
			while( booking.getHotelId().equals(hotelId) && getToDate().before(date) && it.hasNext() ){
				o = it.next();
				hotelId = o!=null?(Integer)(((Object[])o)[PmsReportManager.BOARD_HOTEL_NAME]):null;
				date = o!=null?(Date) (((Object[])o)[PmsReportManager.BOARD_DATE]):null;
			}
		}
		
	}
	
	private void buildEmptyList(Hotel hotel2) throws ManagerBeanException{
		setBookingList(new LinkedList<Booking>());
		Calendar fromCal = Calendar.getInstance();
		Calendar toCal = Calendar.getInstance();
		if( getHotel() != null && getHotel().getId()!=null ){
			fromCal.setTime(getFromDate());
			toCal.setTime(getToDate());
			while(fromCal.before(toCal) || fromCal.equals(toCal)){
				Booking b = new Booking();
				b.setHotelId(getHotel().getId());
				b.setDate(fromCal.getTime());
				b.setQuantityList(obtainEmptyQuantityList());
				getBookingList().add(b);
				fromCal.add(Calendar.DAY_OF_MONTH, 1);
			}
		} else {
			PmsCollectionsController collections = (PmsCollectionsController) AonUtil.getRegisteredBean(IPmsConstants.COLLECTIONS_CONTROLLER_NAME);
			Hotel hotel = null;
			for(ITransferObject to: collections.getCurrentUserHotelList() ){
				hotel = (Hotel) to;
				fromCal.setTime(getFromDate());
				toCal.setTime(getToDate());
				Booking b = null;
				while(fromCal.before(toCal) || fromCal.equals(toCal)){
					b = new Booking();
					b.setHotelId(hotel.getId());
					b.setDate(fromCal.getTime());
					b.setQuantityList(obtainEmptyQuantityList());
					getBookingList().add(b);
					fromCal.add(Calendar.DAY_OF_MONTH, 1);
				}
			}
		}
	}
	
	private List<Integer> obtainEmptyQuantityList() {
		List<Integer> list = new LinkedList<Integer>();
		for(int i=0; i<getBoardItems().size();i++){
			list.add(0);
		}
		return list;
	}
	
	private int getBoardPosition(String code) {
		Iterator<ITransferObject> it = getBoardItems().iterator();
		int i = 0;
		while(it.hasNext()){
			Item item = (Item) it.next();
			if(item.getProduct().getCode().equals(code)){
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
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.ITEM_STATUS), ProductStatus.ACTIVE);
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
	
	public class DayBooking {
		private String hotel;
		private Date date;
		private Integer quantity;
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
		public Integer getQuantity() {
			return quantity;
		}
		public void setQuantity(Integer quantity) {
			this.quantity = quantity;
		}
	}
	
	public class Booking {
		private Integer hotelId;
		private String hotelName;
		private Date date;
		private List<Integer> quantityList;
		
		public Booking (){
			
		}
		public Integer getHotelId() {
			return hotelId;
		}
		public void setHotelId(Integer hotelId) throws ManagerBeanException {
			this.hotelId = hotelId;
			hotelName = ((Hotel)BeanManager.getManagerBean(Hotel.class).get(hotelId)).getWorkPlace().getDescription();
		}
		public Date getDate() {
			return date;
		}
		public void setDate(Date date) {
			this.date = date;
		}
		public List<Integer> getQuantityList() {
			return quantityList;
		}
		public void setQuantityList(List<Integer> quantityList) {
			this.quantityList = quantityList;
		}
		public String getHotelName() {
			return hotelName;
		}
	
	}
	
}
