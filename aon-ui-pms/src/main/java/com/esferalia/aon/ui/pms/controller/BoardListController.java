package com.esferalia.aon.ui.pms.controller;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.product.Item;
import com.code.aon.product.ItemComposition;
import com.code.aon.product.ProductCategory;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.Hotel;
import com.esferalia.aon.pms.ProjectReservationServiceDetail;


public class BoardListController implements ICollectionProvider {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(BoardListController.class.getName());
	
	private Hotel hotel;
	private Date date;
	private ProductCategory category;
	private List<RoomBoard> roomBoardList;	
	private DataModel model;
	
	public ProductCategory getCategory() {
		return category;
	}
	public void setCategory(ProductCategory category) {
		this.category = category;
	}
	
	public DataModel getModel() {
		return model;
	}
	public void setModel(DataModel model) {
		this.model = model;
	}
	public Hotel getHotel() {
		return hotel;
	}
	public void setHotel(Hotel hotel) {
		this.hotel = hotel;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public List<RoomBoard> getRoomBoardList() {
		return roomBoardList;
	}
	public void setRoomBoardList(List<RoomBoard> roomBoardList) {
		this.roomBoardList = roomBoardList;
	}
	public void onInit(ActionEvent event) throws ManagerBeanException{
		setDate(new Date());
	}
	
	public List<ITransferObject> getItemList() throws ManagerBeanException{
		IManagerBean bean = BeanManager.getManagerBean(Item.class);
		Criteria criteria = new Criteria();
		// TODO: Id de categoria a pinon. Se asume que la categoria de las pensiones es la de id=4
//		if(getCategory()!=null){
//			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.ITEM_PRODUCT_CATEGORY_ID), getCategory().getId());
//		}
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.ITEM_PRODUCT_CATEGORY_ID), 4);
		criteria.addOrder(bean.getFieldName(IEntityAlias.ITEM_PRODUCT_CODE));
		List<ITransferObject> list = bean.getList(criteria);
		return list.isEmpty()?null:list;
	}
	
	
	public void onSearch(ActionEvent event) {
		try {
			roomBoardList = new LinkedList<BoardListController.RoomBoard>();
			IManagerBean bean = BeanManager.getManagerBean(ProjectReservationServiceDetail.class);
			Criteria criteria = new Criteria();
			if(getHotel()!=null && getHotel().getId()!=null){
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.PROJECT_RESERVATION_SERVICE_DETAIL_PROJECT_RESERVATION_SERVICE_PROJECT_RESERVATION_HOTEL_ID), getHotel().getId());
			}
			if(getDate()!=null){
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.PROJECT_RESERVATION_SERVICE_DETAIL_EFFECTIVE_DATE), getDate());
			}
			criteria.addNotNullExpression(bean.getFieldName(IEntityAlias.PROJECT_RESERVATION_SERVICE_DETAIL_PROJECT_RESERVATION_ROOM_DETAIL_ID));
			criteria.addOrder(bean.getFieldName(IEntityAlias.PROJECT_RESERVATION_SERVICE_DETAIL_PROJECT_RESERVATION_ROOM_DETAIL_ASSET_ACTIVITY_ASSET_NAME));
			List<ITransferObject> list = bean.getList(criteria);
			if( !list.isEmpty() ){
				RoomBoard rb = null;
				String roomNumber = null;
				for( Object o: list ){
					ProjectReservationServiceDetail d = (ProjectReservationServiceDetail) o;
					if(roomNumber==null || !d.getProjectReservationService().getRoomNumber().equals(roomNumber)){
						rb = new RoomBoard();
						roomBoardList.add(rb);
						rb.setName(d.getProjectReservationService().getRoomNumber());
						rb.setServicesCount(new Integer[6]);
					}
					roomNumber = d.getProjectReservationService().getRoomNumber();
					for(int i=0; i<getItemList().size(); i++){
						if(d.getProjectReservationService().getItem().getItemCompositionList().isEmpty()){
							if(d.getProjectReservationService().getItem().getProduct().getId().equals(((Item)getItemList().get(i)).getProduct().getId())) {
								if(rb.getServicesCount()[i] == null){
									rb.getServicesCount()[i] = 0;
								}
								rb.getServicesCount()[i] = rb.getServicesCount()[i] + (int)d.getQuantity();
							}
						} else {
							for( ItemComposition ic: d.getProjectReservationService().getItem().getItemCompositionList() ){
								if(ic.getCompositionItem().getProduct().getId().equals(((Item)getItemList().get(i)).getProduct().getId())) {
									if(rb.getServicesCount()[i] == null){
										rb.getServicesCount()[i] = 0;
									}
									rb.getServicesCount()[i] = rb.getServicesCount()[i] + (int)ic.getQuantity();
								}
							}
						}
					}
				}
			}
		} catch (ManagerBeanException e) {
			String msg =  "Error searching service detail. ";
			LOGGER.error(msg, e);
			AonUtil.addErrorMessage(msg + e.getMessage());
		}
		setModel(new ListDataModel(roomBoardList));
	}
	
	private List<ITransferObject> getBoardList() {
		List<ITransferObject> reportList = null;
		try {
			IManagerBean bean = BeanManager.getManagerBean(ProjectReservationServiceDetail.class);
			Criteria criteria = new Criteria();
			if(getHotel()!=null && getHotel().getId()!=null){
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.PROJECT_RESERVATION_SERVICE_DETAIL_PROJECT_RESERVATION_SERVICE_PROJECT_RESERVATION_HOTEL_ID), getHotel().getId());
			}
			if(getDate()!=null){
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.PROJECT_RESERVATION_SERVICE_DETAIL_EFFECTIVE_DATE), getDate());
			}
			criteria.addNotNullExpression(bean.getFieldName(IEntityAlias.PROJECT_RESERVATION_SERVICE_DETAIL_PROJECT_RESERVATION_ROOM_DETAIL_ID));
			criteria.addOrder(bean.getFieldName(IEntityAlias.PROJECT_RESERVATION_SERVICE_DETAIL_PROJECT_RESERVATION_SERVICE_ITEM_PRODUCT_CODE));
			criteria.addOrder(bean.getFieldName(IEntityAlias.PROJECT_RESERVATION_SERVICE_DETAIL_PROJECT_RESERVATION_ROOM_DETAIL_ASSET_ACTIVITY_ASSET_NAME));
			
			reportList = new LinkedList<ITransferObject>();
			for(ITransferObject to: bean.getList(criteria)){
				ProjectReservationServiceDetail d = (ProjectReservationServiceDetail) to;
				for(ITransferObject to2: getItemList()){
					Item item = (Item) to2;
					if(d.getItem().getProduct().isComposition()){
						
					}
					if(d.getItem().getProduct().getCode().equals(item.getProduct().getCode())){
						reportList.add(d);
					}
				}
			}
			
		} catch (ManagerBeanException e) {
			String msg =  "Error searching service detail. ";
			LOGGER.error(msg, e);
			AonUtil.addErrorMessage(msg + e.getMessage());
		}
		
		return reportList;
	}
	
	/**************************************************/
	/**************************************************/
	
	public class RoomBoard {
		private String name;
		private Integer[] servicesCount;
		
		public Integer[] getServicesCount() {
			return servicesCount;
		}
		public void setServicesCount(Integer[] servicesCount) {
			this.servicesCount = servicesCount;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public Date getDate() {
			return date;
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Collection getCollection() {
		return getBoardList();
	}
	@SuppressWarnings("rawtypes")
	@Override
	public Collection getCollection(boolean forceRefresh)
			throws ManagerBeanException {
		return getCollection();
	}
	
}
