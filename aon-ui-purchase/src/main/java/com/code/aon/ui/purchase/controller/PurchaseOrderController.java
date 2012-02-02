package com.code.aon.ui.purchase.controller;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import org.hibernate.Query;
import org.hibernate.Session;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.company.WorkPlace;
import com.code.aon.company.WorkplaceDepartment;
import com.code.aon.product.Item;
import com.code.aon.purchase.ProposalDetail;
import com.code.aon.purchase.enumeration.ProposalStatus;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;


public class PurchaseOrderController {
	
	private OrderParams params;
	private DataModel model;
	private DataModel detailModel;
	private List<ItemGroup> productList;
	private int productIndex;
	private int detailIndex;
	
	public int getProductIndex() {
		return productIndex;
	}
	public void setProductIndex(int productIndex) {
		this.productIndex = productIndex;
	}
	public int getDetailIndex() {
		return detailIndex;
	}
	public void setDetailIndex(int detailIndex) {
		this.detailIndex = detailIndex;
	}
	public List<ItemGroup> getProductList() {
		return productList;
	}
	public void setProductList(List<ItemGroup> productList) {
		this.productList = productList;
	}
	public OrderParams getParams() {
		return params;
	}
	public void setParams(OrderParams params) {
		this.params = params;
	}
	public DataModel getModel() {
		return model;
	}
	public void setModel(DataModel model) {
		this.model = model;
	}
	public DataModel getDetailModel() {
		return detailModel;
	}
	public void setDetailModel(DataModel detailModel) {
		this.detailModel = detailModel;
	}
	
	public void onInit(ActionEvent event) throws ManagerBeanException{
		setParams(new OrderParams());
		onSearch(event);
	}
	
	public void onSelectGroup(ActionEvent event) throws ManagerBeanException{
		setProductIndex(getModel().getRowIndex());
		searchDetail(event);
	}
	
	public void onSearch(ActionEvent event) throws ManagerBeanException{
		Session session = HibernateUtil.getSession(HibernateUtil.getSessionFactoryName());
		String select = "SELECT ProposalDetail, sum(ProposalDetail.quantity), count(ProposalDetail.id)" 
				+ " FROM ProposalDetail as ProposalDetail" 
				+ " WHERE ProposalDetail.proposal.status = " + ProposalStatus.PENDING.ordinal()
				+ (getParams().getStartDate() != null  ? " AND ProposalDetail.proposal.issueDate >= " + new java.sql.Date(getParams().getStartDate().getTime()) : "")
				+ (getParams().getEndDate() != null  ? " AND ProposalDetail.proposal.issueDate <= " + new java.sql.Date(getParams().getEndDate().getTime()) : "")
				+ ((getParams().getWorkPlace() != null && getParams().getWorkPlace().getId() != null ) ? " AND ProposalDetail.proposal.workplaceDepartment.workPlace = " + getParams().getWorkPlace().getId() : "")
				+ " GROUP BY ProposalDetail.item"
				+ " ORDER BY ProposalDetail.item.product.name";
		Query query = session.createQuery(select);
		productList = new LinkedList<ItemGroup>(); 
		for( Object o: query.list() ){
			Object[] ob = (Object[]) o;
			ItemGroup ig = new ItemGroup();
			ig.setItem(((ProposalDetail) ob[0]).getItem());
			ig.setTotalQuantity(((Double) ob[1]));
			ig.setTotalItem(((Long) ob[2]));
			productList.add(ig);
		}
		setModel(new ListDataModel(productList));
		searchDetail(null);
		setProductIndex(-1);
	}
	
	public void searchDetail(ActionEvent event) throws ManagerBeanException{
		List<ITransferObject> list = null;
		if(getModel().isRowAvailable()){
			IManagerBean bean = BeanManager.getManagerBean(ProposalDetail.class);
			Criteria criteria = new Criteria();
			if( getParams().getStartDate() != null ){
				criteria.addGreaterThanOrEqualExpression(bean.getFieldName(IEntityAlias.PROPOSAL_DETAIL_PROPOSAL_ISSUE_DATE), getParams().getStartDate());
			}
			if( getParams().getEndDate() != null ){
				criteria.addLessThanOrEqualExpression(bean.getFieldName(IEntityAlias.PROPOSAL_DETAIL_PROPOSAL_ISSUE_DATE), getParams().getEndDate());
			}
			if( getParams().getWorkPlace() != null ){
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.PROPOSAL_DETAIL_PROPOSAL_WORK_PLACE_ID), getParams().getWorkPlace().getId());
			}
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.PROPOSAL_DETAIL_ITEM_ID), ((ItemGroup)getModel().getRowData()).getItem().getId());
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.PROPOSAL_DETAIL_PROPOSAL_STATUS), ProposalStatus.PENDING);
			criteria.addOrder(bean.getFieldName(IEntityAlias.PROPOSAL_DETAIL_PROPOSAL_WORK_PLACE_ID));
			list = bean.getList(criteria);
		}
		detailModel = new ListDataModel(list);
		setDetailIndex(-1);
	}

	public void onPurchaseConfirm(ActionEvent event) throws ManagerBeanException{
		
	}
	
	public void onSelectDetail(ActionEvent event) throws ManagerBeanException{
		setDetailIndex(getDetailModel().getRowIndex());
	}
	
	public void onAcceptDetail(ActionEvent event) throws ManagerBeanException{
		IManagerBean bean = BeanManager.getManagerBean(ProposalDetail.class);
		ProposalDetail pd = (ProposalDetail) getDetailModel().getRowData();
		bean.update(pd);
		setDetailIndex(-1);
	}
	public void onCancelDetail(ActionEvent event) throws ManagerBeanException{
		setDetailIndex(-1);
	}
	public void onRemoveDetail(ActionEvent event) throws ManagerBeanException{
		IManagerBean bean = BeanManager.getManagerBean(ProposalDetail.class);
		ProposalDetail pd = (ProposalDetail) getDetailModel().getRowData();
		bean.remove(pd);
		searchDetail(event);
		setDetailIndex(-1);
	}

	/**************************************************/
	/**************************************************/
	
	public class ItemGroup{
		private Item item;
		private Double totalQuantity;
		private Long totalItem;
		public Item getItem() {
			return item;
		}
		public void setItem(Item item) {
			this.item = item;
		}
		public Double getTotalQuantity() {
			return totalQuantity;
		}
		public void setTotalQuantity(Double totalQuantity) {
			this.totalQuantity = totalQuantity;
		}
		public Long getTotalItem() {
			return totalItem;
		}
		public void setTotalItem(Long totalItem) {
			this.totalItem = totalItem;
		}
	}
	
	public class OrderParams{
		private Date startDate;
		private Date endDate;
		private WorkPlace workPlace;
		private WorkplaceDepartment workplaceDepartment;
		private ProposalStatus status;
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
		public WorkPlace getWorkPlace() {
			return workPlace;
		}
		public void setWorkPlace(WorkPlace workPlace) {
			this.workPlace = workPlace;
		}
		public WorkplaceDepartment getWorkplaceDepartment() {
			return workplaceDepartment;
		}
		public void setWorkplaceDepartment(WorkplaceDepartment workplaceDepartment) {
			this.workplaceDepartment = workplaceDepartment;
		}
		public ProposalStatus getStatus() {
			return status;
		}
		public void setStatus(ProposalStatus status) {
			this.status = status;
		}
	}
	
}
