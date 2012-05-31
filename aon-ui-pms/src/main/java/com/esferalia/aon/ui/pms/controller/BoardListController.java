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
import com.code.aon.product.ProductCategory;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.Hotel;
import com.esferalia.aon.ui.pms.util.PmsReportManager;

public class BoardListController implements ICollectionProvider {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(BoardListController.class.getName());
	
	private ProductCategory category;
	private List<DayBoard> boardList;
	private DataModel model;
	private BoardParams params;
	private List<BoardTotal> boardsTotalList;
	
	
	public List<BoardTotal> getBoardsTotalList() {
		return boardsTotalList;
	}
	public void setBoardsTotalList(List<BoardTotal> boardsTotalList) {
		this.boardsTotalList = boardsTotalList;
	}
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
	public List<DayBoard> getBoardList() {
		return boardList;
	}
	public void setBoardList(List<DayBoard> boardList) {
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
		setBoardsTotalList(null);
		onSearch(event);
	}
	
	private List<ITransferObject> boardItems() {
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
		return null;
	}
	
	private Integer getBoardCategoryId() {
		// TODO: Id de categoria a pinon. Se asume que la categoria de las pensiones es la de id=4
		return 4;
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
		try {
			if (getParams().getHotel() != null && getParams().getHotel().getId() != null && getParams().getDate() != null) {
				buildBoardList();
			} else {
				setBoardList(null);
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al construir el listado de pensiones";
			LOGGER.error(msg);
			throw new AbortProcessingException(msg, e);
		}
		setModel(new ListDataModel(getBoardList()));
	}
	
	@SuppressWarnings("rawtypes")
	private void buildBoardList() throws ManagerBeanException {
		
		String select = PmsReportManager.getInstance().getBoardBookingSQL(getParams().getHotel(), getParams().getBoardItemFilter()!=null?getParams().getBoardItemFilter().getProduct():null);
		Session session = HibernateUtil.getSession(HibernateUtil.getSessionFactoryName());
		Query query = session.createSQLQuery(select);
		query.setDate("start", new java.sql.Date(DateUtils.addDays(getParams().getDate(),-1).getTime()));
		query.setDate("end", new java.sql.Date(getParams().getDate().getTime()));

		List list = query.list();
		
		setBoardList(new LinkedList<DayBoard>());
		
		BoardTotal total = new BoardTotal();
		setBoardsTotalList(new LinkedList<BoardListController.BoardTotal>());
		for(Object o: list ){
			DayBoard db = new DayBoard();
			Date date = (Date) (((Object[])o)[PmsReportManager.BOARD_DATE]);
			if(getParams().getDate().equals(date)){
				db.setBoardName((String) (((Object[])o)[PmsReportManager.BOARD_NAME]));
				db.setRoom((String) (((Object[])o)[PmsReportManager.BOARD_ROOM_NAME]));
				db.setQuantity( Integer.parseInt((((Object[])o)[PmsReportManager.BOARD_QUANTITY]).toString()) );
				db.setGuest(((String) (((Object[])o)[PmsReportManager.BOARD_GUEST_NAME])));
				db.setStartDate(((Date) (((Object[])o)[PmsReportManager.BOARD_GUEST_START_DATE])));
				db.setEndDate(((Date) (((Object[])o)[PmsReportManager.BOARD_GUEST_END_DATE])));
				getBoardList().add(db);
				
				if(db.getBoardName().equals(total.getBoardName())){
					BoardTotal t = getBoardsTotalList().get(getBoardsTotalList().size()-1);
					t.setCount(t.getCount()+db.getQuantity());
				} else {
					total = new BoardTotal();
					total.setBoardName(db.getBoardName());
					total.setCount(total.getCount()!=null?total.getCount()+db.getQuantity():db.getQuantity());
					getBoardsTotalList().add(total);
				}
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
	
	public class DayBoard {
		private String boardName;
		private String room;
		private Integer quantity;
		private String guest;
		private Date startDate;
		private Date endDate;
		
		public String getBoardName() {
			return boardName;
		}
		public void setBoardName(String boardName) {
			this.boardName = boardName;
		}
		public String getRoom() {
			return room;
		}
		public void setRoom(String room) {
			this.room = room;
		}
		public Integer getQuantity() {
			return quantity;
		}
		public void setQuantity(Integer quantity) {
			this.quantity = quantity;
		}
		public String getGuest() {
			return guest;
		}
		public void setGuest(String guest) {
			this.guest = guest;
		}
		public Date getStartDate() {
			return startDate;
		}
		public void setStartDate(Date startDate) {
			this.startDate = startDate;
		}
		public Date getEndDate() {
			return endDate;
		}
		public void setEndDate(Date endDate) {
			this.endDate = endDate;
		}
	}
	
	public class BoardTotal {
		private String boardName;
		private Integer count;
		public Integer getCount() {
			return count;
		}
		public String getBoardName() {
			return boardName;
		}
		public void setBoardName(String boardName) {
			this.boardName = boardName;
		}
		public void setCount(Integer count) {
			this.count = count;
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
