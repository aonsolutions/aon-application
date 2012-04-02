package com.esferalia.aon.ui.pms.controller;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.time.DateUtils;
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
import com.esferalia.aon.pms.ProjectReservationService;
import com.esferalia.aon.pms.ProjectReservationServiceDetail;

public class BoardListController implements ICollectionProvider {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(BoardListController.class.getName());
	
	private ProductCategory category;
	private List<RoomBoard> boardList;
	private DataModel model;
	private BoardParams params;
	
	public BoardParams getParams() {
		return params;
	}
	public void setParams(BoardParams params) {
		this.params = params;
	}
	public Hotel getHotel() {
		return getParams().getHotel();
	}
	public Date getDate() {
		return getParams().getDate();
	}
	public boolean isBoardPageBreak() {
		return getParams().isBoardPageBreak();
	}
	public List<RoomBoard> getBoardList() {
		return boardList;
	}
	public void setBoardList(List<RoomBoard> boardList) {
		this.boardList = boardList;
	}
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
	
	public void onInit(ActionEvent event) throws ManagerBeanException{
		setParams(new BoardParams());
		getParams().setDate(new Date());
		getParams().setBoardPageBreak(true);
		getParams().setBoardItemFilter(null);
		onSearch(event);
	}
	
	private List<ITransferObject> boardItems() {
		try {
			IManagerBean bean = BeanManager.getManagerBean(Item.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.ITEM_PRODUCT_CATEGORY_ID), getBoardCategoryId());
			criteria.addOrder(bean.getFieldName(IEntityAlias.ITEM_PRODUCT_CODE));
			List<ITransferObject> list = bean.getList(criteria);
			return list.isEmpty()?null:list;
		} catch (ManagerBeanException e) {
			String msg =  "******** Error getting board items. ";
			LOGGER.error(msg, e);
			AonUtil.addErrorMessage(msg + e.getMessage());
		}
		return null;
	}
	
	private Integer getBoardCategoryId() {
		// TODO: Id de categoria a pinon. Se asume que la categoria de las pensiones es la de id=4
		return 4;
	}
	private boolean isBreakfastBoard(Item item){
		// TODO: se asume como desayuno la primera pension de la categoria
		if(boardItems()!=null){
			if(boardItems().get(0)!=null && ((Item)boardItems().get(0)).getId().equals(item.getId())){
				return true;
			}
		}
		return false;
	}
	
	public List<ITransferObject> getBoardItems() {
		if(getParams().getBoardItemFilter()!=null && getParams().getBoardItemFilter().getId()!=null){
			List<ITransferObject> list = new LinkedList<ITransferObject>(); 
			list.add(getParams().getBoardItemFilter());
			return list;
		}
		return boardItems();
	}
	
	public List<SelectItem> getBoardItemList() {
		List<SelectItem> list = new LinkedList<SelectItem>();
		for (ITransferObject ito : boardItems()) {
			Item i = (Item)ito;
			SelectItem item = new SelectItem(i, i.getProduct().getName());
			list.add(item);
		}
		return list;
	}
	
	public void onSearch(ActionEvent event) {
		if (getParams().getHotel() != null && getParams().getHotel().getId() != null && getParams().getDate() != null) {
			buildBoardList();
		} else {
			setBoardList(null);
		}
		setModel(new ListDataModel(getBoardList()));
	}
	
	private void buildBoardList() {
		List<RoomBoard> roomBoardList = new LinkedList<RoomBoard>();
		List<RoomBoard> compositeList = new LinkedList<RoomBoard>();
		RoomBoard roomBoard = null;
		for(ITransferObject to: getServiceDetailList()){
			ProjectReservationServiceDetail serviceDetail = (ProjectReservationServiceDetail) to;
			for(ITransferObject to2: getBoardItems()){
				Item item = (Item) to2;
				if(serviceDetail.getItem().getProduct().isComposition()){
					try {	
						for( ItemComposition ic: serviceDetail.getItem().getItemCompositionList() ){
							if(ic.getCompositionItem().getProduct().getCode().equals(item.getProduct().getCode())) {
								if( (isBreakfastBoard(ic.getCompositionItem()) && DateUtils.isSameDay(serviceDetail.getEffectiveDate(), DateUtils.addDays(getParams().getDate(), -1)))
										|| (!isBreakfastBoard(ic.getCompositionItem()) && DateUtils.isSameDay(serviceDetail.getEffectiveDate(), getParams().getDate())) ){
									roomBoard = new RoomBoard();
									roomBoard.setItem(ic.getCompositionItem());
									int roomTotalGuests = serviceDetail.getProjectReservationService().getProjectReservationRoom().getAdults()+serviceDetail.getProjectReservationService().getProjectReservationRoom().getChildren();
									roomBoard.setQuantity(ic.getQuantity()*roomTotalGuests);
									roomBoard.setProjectReservationService(serviceDetail.getProjectReservationService());
									compositeList.add(roomBoard);
								}
							}
						}
					} catch (ManagerBeanException e) {
						String msg =  "******** Error obtaining item composition list. ";
						LOGGER.error(msg, e);
						AonUtil.addErrorMessage(msg + e.getMessage());
					}
				} else {
					if(serviceDetail.getItem().getProduct().getCode().equals(item.getProduct().getCode())){
						try {
							if( DateUtils.isSameDay(serviceDetail.getEffectiveDate(), getParams().getDate()) ){
								roomBoard = new RoomBoard();
								roomBoard.setItem(serviceDetail.getItem());
								int roomTotalGuests = serviceDetail.getProjectReservationService().getProjectReservationRoom().getAdults()+serviceDetail.getProjectReservationService().getProjectReservationRoom().getChildren();
								roomBoard.setQuantity(new Double(roomTotalGuests));
								roomBoard.setProjectReservationService(serviceDetail.getProjectReservationService());
								roomBoardList.add(roomBoard);
							}
						} catch (ManagerBeanException e) {
							String msg =  "******** Error addding board to list. ";
							LOGGER.error(msg, e);
							AonUtil.addErrorMessage(msg + e.getMessage());
						}
					}
				}
			}
		}
		
		for(RoomBoard board: compositeList){
			for(ITransferObject to2: getBoardItems()){
				Item item = (Item) to2;
				if(board.getItem().getProduct().getCode().equals(item.getProduct().getCode())) {
					addCompositeItem(roomBoardList, board);
				}
			}
		}
		
		setBoardList(roomBoardList);
	}
	
	private List<ITransferObject> getServiceDetailList() {
		try {
			IManagerBean bean = BeanManager.getManagerBean(ProjectReservationServiceDetail.class);
			Criteria criteria = new Criteria();
			if (getParams().getHotel() != null && getParams().getHotel().getId() != null) {
				criteria.addEqualExpression(
						bean.getFieldName(IEntityAlias.PROJECT_RESERVATION_SERVICE_DETAIL_PROJECT_RESERVATION_SERVICE_PROJECT_RESERVATION_HOTEL_ID),
						getParams().getHotel().getId());
			}
			if (getParams().getDate() != null) {
				criteria.addBetweenExpression(
						bean.getFieldName(IEntityAlias.PROJECT_RESERVATION_SERVICE_DETAIL_EFFECTIVE_DATE),
						DateUtils.addDays(getParams().getDate(), -1), getParams().getDate());
			}
			criteria.addNotNullExpression(bean.getFieldName(IEntityAlias.PROJECT_RESERVATION_SERVICE_DETAIL_PROJECT_RESERVATION_ROOM_DETAIL_ID));
			criteria.addOrder(bean.getFieldName(IEntityAlias.PROJECT_RESERVATION_SERVICE_DETAIL_EFFECTIVE_DATE));
			criteria.addOrder(bean.getFieldName(IEntityAlias.PROJECT_RESERVATION_SERVICE_DETAIL_PROJECT_RESERVATION_SERVICE_ITEM_PRODUCT_CODE));
			criteria.addOrder(bean.getFieldName(IEntityAlias.PROJECT_RESERVATION_SERVICE_DETAIL_PROJECT_RESERVATION_ROOM_DETAIL_ASSET_ACTIVITY_ASSET_NAME));
			return bean.getList(criteria);
		} catch (ManagerBeanException e) {
			String msg =  "******** Error searching service detail. ";
			LOGGER.error(msg, e);
			AonUtil.addErrorMessage(msg + e.getMessage());
			throw new AbortProcessingException(msg, e);
		}
	}

	private void addCompositeItem(List<RoomBoard> roomBoardList, RoomBoard board) {
		if(roomBoardList.isEmpty()){
			roomBoardList.add(board);
		} else {
			int idx = 0;
			for(RoomBoard rb: roomBoardList){
				try {
					if( rb.isSameBoard(board) ){
						if( rb.isSameRoom(board) ){
							rb.setQuantity( rb.getQuantity() + board.getQuantity() );
							break;
						} else {
							if (board.getProjectReservationService().getRoomNumber().compareToIgnoreCase(rb.getProjectReservationService().getRoomNumber()) < 0 ){
								roomBoardList.add(idx, board);
								break;
							} else if( roomBoardList.size()-1 == idx ){
								roomBoardList.add(board);
								break;
							} else if (board.getProjectReservationService().getRoomNumber().compareToIgnoreCase(rb.getProjectReservationService().getRoomNumber()) > 0 
											&& !roomBoardList.get(idx+1).isSameBoard(board) ){
								roomBoardList.add(idx+1, board);
								break;
							}
						}
					} else {
						if( board.getItem().getProduct().getCode().compareToIgnoreCase(rb.getItem().getProduct().getCode()) < 0 ) {
							roomBoardList.add(idx, board);
							break;
						} else if( roomBoardList.size()-1 == idx ) {
							roomBoardList.add(board);
							break;
						}
					}
				} catch (ManagerBeanException e) {
					String msg =  "******** Error adding board to list. ";
					LOGGER.error(msg, e);
					AonUtil.addErrorMessage(msg + e.getMessage());
				}
				idx++;
			}
		}
	}

	/**************************************************/
	/**************************************************/
	
	public class BoardParams {
		private Hotel hotel;
		private Date date;
		private boolean boardPageBreak;
		private Item boardItemFilter;
		
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
		public boolean isBoardPageBreak() {
			return boardPageBreak;
		}
		public void setBoardPageBreak(boolean boardPageBreak) {
			this.boardPageBreak = boardPageBreak;
		}
		public Item getBoardItemFilter() {
			return boardItemFilter;
		}
		public void setBoardItemFilter(Item boardItemFilter) {
			this.boardItemFilter = boardItemFilter;
		}
		
	}
	public class RoomBoard {
		private Item item;
		private ProjectReservationService projectReservationService;
		private Double quantity;
		
		public Item getItem() {
			return item;
		}
		public void setItem(Item item) {
			this.item = item;
		}
		public ProjectReservationService getProjectReservationService() {
			return projectReservationService;
		}
		public void setProjectReservationService(
				ProjectReservationService projectReservationService) {
			this.projectReservationService = projectReservationService;
		}
		public Double getQuantity() {
			return quantity;
		}
		public void setQuantity(Double quantity) {
			this.quantity = quantity;
		}
		
		public boolean isSameBoard(RoomBoard board) throws ManagerBeanException {
			return item.getProduct().getId().equals(board.getItem().getProduct().getId())
				&& item.getProduct().getCode().toLowerCase().equals(board.getItem().getProduct().getCode().toLowerCase());
	    }
		
		public boolean isSameRoom(RoomBoard board) throws ManagerBeanException {
			return projectReservationService.getRoomNumber().equals(board.getProjectReservationService().getRoomNumber());
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
