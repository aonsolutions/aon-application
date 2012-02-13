package com.esferalia.aon.ui.pms.controller;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

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
	
	private Hotel hotel;
	private Date date;
	private ProductCategory category;
	private List<RoomBoard> boardList;
	private DataModel model;
	private boolean boardPageBreak;
	private Item boardItem;
	
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
	public Item getBoardItem() {
		return boardItem;
	}
	public void setBoardItem(Item boardItem) {
		this.boardItem = boardItem;
	}
	
	public void onInit(ActionEvent event) throws ManagerBeanException{
		setDate(new Date());
		setBoardPageBreak(true);
		setBoardItem(null);
		onSearch(event);
	}
	
	public List<ITransferObject> getBoardItems() throws ManagerBeanException{
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
	
	public List<SelectItem> getBoardItemList() throws ManagerBeanException{
		List<SelectItem> list = new LinkedList<SelectItem>();
		for (ITransferObject ito : getBoardItems()) {
			Item i = (Item)ito;
			SelectItem item = new SelectItem(i, i.getProduct().getName());
			list.add(item);
		}
		return list;
	}
	
	public void onSearch(ActionEvent event) {
		buildBoardList();
		setModel(new ListDataModel(getBoardList()));
	}
	
	private void buildBoardList() {
		List<RoomBoard> reportList = null;
		List<RoomBoard> compositeList = null;
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
			
			reportList = new LinkedList<RoomBoard>();
			compositeList = new LinkedList<RoomBoard>();
			for(ITransferObject to: bean.getList(criteria)){
				ProjectReservationServiceDetail d = (ProjectReservationServiceDetail) to;
				if(getBoardItem()!=null && getBoardItem().getId()!=null){
					Item item = getBoardItem();
					RoomBoard board = new RoomBoard();
					board.setItem(d.getItem());
					board.setQuantity(d.getQuantity());
					board.setProjectReservationService(d.getProjectReservationService());
					if(d.getItem().getProduct().isComposition()){
						for( ItemComposition ic: d.getItem().getItemCompositionList() ){
							board = new RoomBoard();
							board.setItem(ic.getCompositionItem());
							board.setQuantity(ic.getQuantity());
							board.setProjectReservationService(d.getProjectReservationService());
							if(ic.getCompositionItem().getProduct().getCode().equals(item.getProduct().getCode())) {
								compositeList.add(board);
							}
						}
					}
					if(d.getItem().getProduct().getCode().equals(item.getProduct().getCode())){
						reportList.add(board);
					}
				} else {
					for(ITransferObject to2: getBoardItems()){
						Item item = (Item) to2;
						RoomBoard board = new RoomBoard();
						board.setItem(d.getItem());
						board.setQuantity(d.getQuantity());
						board.setProjectReservationService(d.getProjectReservationService());
						if(d.getItem().getProduct().isComposition()){
							for( ItemComposition ic: d.getItem().getItemCompositionList() ){
								board = new RoomBoard();
								board.setItem(ic.getCompositionItem());
								board.setQuantity(ic.getQuantity());
								board.setProjectReservationService(d.getProjectReservationService());
								if(ic.getCompositionItem().getProduct().getCode().equals(item.getProduct().getCode())) {
									compositeList.add(board);
								}
							}
						}
						if(d.getItem().getProduct().getCode().equals(item.getProduct().getCode())){
							reportList.add(board);
						}
					}
				}
				
			}
			for(RoomBoard board: compositeList){
				if(getBoardItem()!=null && getBoardItem().getId()!=null){
					Item item = getBoardItem();
					if(board.getItem().getProduct().getCode().equals(item.getProduct().getCode())) {
						addCompositeItem(reportList, board);
					}
				} else {
					for(ITransferObject to2: getBoardItems()){
						Item item = (Item) to2;
						if(board.getItem().getProduct().getCode().equals(item.getProduct().getCode())) {
							addCompositeItem(reportList, board);
						}
					}
				}
			}
		} catch (ManagerBeanException e) {
			String msg =  "Error searching service detail. ";
			LOGGER.error(msg, e);
			AonUtil.addErrorMessage(msg + e.getMessage());
		}
		setBoardList(reportList);
	}
	
	private void addCompositeItem(List<RoomBoard> reportList, RoomBoard board) throws ManagerBeanException {
		ListIterator<RoomBoard> it = reportList.listIterator();
		while(it.hasNext()){
			RoomBoard b = it.next();
			if( b.isSameBoard(board) ){
				if( b.isSameRoom(board) ){
					reportList.get(it.nextIndex()-1).setQuantity(reportList.get(it.nextIndex()-1).getQuantity()+board.getQuantity());
					break;
				} else if( it.hasNext() 
					&& reportList.get(it.nextIndex()).getItem().getProduct().getCode().compareToIgnoreCase(board.getItem().getProduct().getCode()) != 0 ) {
					reportList.add(it.nextIndex(), board);
					break;
				}
			} else if( it.hasNext() 
					&& board.getItem().getProduct().getCode().compareToIgnoreCase(reportList.get(it.nextIndex()).getItem().getProduct().getCode()) < 0 ) {
				reportList.add(it.nextIndex(), board);
				break;
			} else if( !it.hasNext() ) {
				reportList.add(board);
				break;
			}
		}
	}

	/**************************************************/
	/**************************************************/
	
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
