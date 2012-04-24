package com.code.aon.ui.purchase.controller;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.company.Department;
import com.code.aon.company.WorkPlace;
import com.code.aon.company.WorkplaceDepartment;
import com.code.aon.config.Series;
import com.code.aon.config.util.SeriesNumberUtil;
import com.code.aon.product.Item;
import com.code.aon.product.ItemSupplier;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.purchase.ProposalDetail;
import com.code.aon.purchase.Purchase;
import com.code.aon.purchase.PurchaseDetail;
import com.code.aon.purchase.enumeration.ProposalDetailStatus;
import com.code.aon.purchase.enumeration.ProposalStatus;
import com.code.aon.purchase.enumeration.PurchaseDetailStatus;
import com.code.aon.purchase.enumeration.PurchaseStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.supplier.Supplier;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;


public class PurchaseOrderController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PurchaseOrderController.class.getName());
	
	private OrderParams params;
	private DataModel model;
	private DataModel detailModel;
	private List<ItemGroup> itemGroupList;
	private List<PurchaseGroup> purchaseGroupList;
	private List<GroupDetail> proposalDetailList;
	private int productIndex;
	private int detailIndex;
	private Criteria purchasePrintcriteria = new Criteria();
	
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
	public List<ItemGroup> getItemGroupList() {
		return itemGroupList;
	}
	public void setItemGroupList(List<ItemGroup> itemGroupList) {
		this.itemGroupList = itemGroupList;
	}
	public List<PurchaseGroup> getPurchaseGroupList() {
		return purchaseGroupList;
	}
	public void setPurchaseGroupList(List<PurchaseGroup> purchaseGroupList) {
		this.purchaseGroupList = purchaseGroupList;
	}
	public List<GroupDetail> getProposalDetailList() {
		return proposalDetailList;
	}
	public void setProposalDetailList(List<GroupDetail> proposalDetailList) {
		this.proposalDetailList = proposalDetailList;
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
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.set(Calendar.DAY_OF_MONTH, 1);
		getParams().setStartDate(cal.getTime());
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
		getParams().setEndDate(cal.getTime());
		onSearch(event);
	}
	
	public void onSelectGroup(ActionEvent event) throws ManagerBeanException{
		if(getProductIndex()==getModel().getRowIndex()){
			setProductIndex(-1);
		} else {
			setProductIndex(getModel().getRowIndex());
		}
		buildGroupDetail();
	}
	
	public void onSearch(ActionEvent event) throws ManagerBeanException{
		String workPlaceClause = getWorkPlaceClause();
		String select = "SELECT ProposalDetail, sum(ProposalDetail.quantity), count(ProposalDetail.id)" 
				+ " FROM ProposalDetail as ProposalDetail" 
				+ " WHERE " + DomainManager.getSQLWhereClause("ProposalDetail.domain")
				+ " AND ProposalDetail.status = " + ProposalDetailStatus.PENDING.ordinal()
				+ (getParams().getStartDate() != null ? " AND ProposalDetail.proposal.issueDate >= :startDate" : "")
				+ (getParams().getEndDate() != null ? " AND ProposalDetail.proposal.issueDate <= :endDate" : "")
				+ workPlaceClause
				+ ((getParams().getDepartment() != null && getParams().getDepartment().getId() != null ) ? " AND ProposalDetail.proposal.department = :departmentId"  : "")
				+ " GROUP BY ProposalDetail.item"
				+ " ORDER BY ProposalDetail.item.product.name";
		Session session = HibernateUtil.getSession(HibernateUtil.getSessionFactoryName());
		Query query = session.createQuery(select);
		if(getParams().getStartDate() != null ){
			query.setDate("startDate", new java.sql.Date(getParams().getStartDate().getTime()));
		}
		if(getParams().getEndDate() != null ){
			query.setDate("endDate", new java.sql.Date(getParams().getEndDate().getTime()));
		}
		if((getParams().getWorkPlace() != null && getParams().getWorkPlace().getId() != null ) ){
			query.setInteger("workplaceId", getParams().getWorkPlace().getId());
		}
		if((getParams().getDepartment() != null && getParams().getDepartment().getId() != null ) ){
			query.setInteger("departmentId", getParams().getDepartment().getId());
		}
		itemGroupList = new LinkedList<ItemGroup>(); 
		for( Object o: query.list() ){
			Object[] ob = (Object[]) o;
			ItemGroup ig = new ItemGroup();
			ig.setItem(((ProposalDetail) ob[0]).getItem());
			ig.setTotalQuantity(((Double) ob[1]));
			ig.setTotalItem(((Long) ob[2]));
			itemGroupList.add(ig);
		}
		setModel(new ListDataModel(itemGroupList));
		setProductIndex(-1);
	}
	
	private String getWorkPlaceClause() throws ManagerBeanException {
		if(getParams().getWorkPlace() != null && getParams().getWorkPlace().getId() != null ){
			return " AND ProposalDetail.proposal.workPlace = :workplaceId";
		} else {
			String clause = ""; 
			for(ITransferObject to: getCurrentUserWorkPlaces()){
				WorkPlace wp = (WorkPlace) to;
				clause += (clause.isEmpty()?" AND (":" OR") + " ProposalDetail.proposal.workPlace = " + wp.getId();
			}
			clause += " ) ";
			return clause;
		}
	}
	
	private List<ITransferObject> getCurrentUserWorkPlaces() throws ManagerBeanException {
   		IManagerBean workPlaceBean = BeanManager.getManagerBean(WorkPlace.class);
   		Criteria criteria = new Criteria();
   		criteria.addEqualExpression(workPlaceBean.getFieldName(IEntityAlias.WORK_PLACE_ACTIVE), new Boolean(true));
   		UserUtils.getInstance().addScopeFilterToCriteria(criteria, workPlaceBean.getFieldName(IEntityAlias.WORK_PLACE_SCOPE_ID));
    	criteria.addOrder(workPlaceBean.getFieldName(IEntityAlias.WORK_PLACE_DESCRIPTION));
    	return workPlaceBean.getList(criteria);
	}	
	
	private void buildGroupDetail() throws ManagerBeanException{
		detailModel = new ListDataModel(obtainGroupDetail());
		setDetailIndex(-1);
	}

	private List<ITransferObject> obtainGroupDetail() throws ManagerBeanException{
		List<ITransferObject> list = null;
		if(getModel().isRowAvailable()){
			IManagerBean bean = BeanManager.getManagerBean(ProposalDetail.class);
			Criteria criteria = getParams().getProposalStatusCriteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.PROPOSAL_DETAIL_ITEM_ID), ((ItemGroup)getModel().getRowData()).getItem().getId());
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.PROPOSAL_DETAIL_STATUS), ProposalDetailStatus.PENDING);
			if((getParams().getWorkPlace() != null && getParams().getWorkPlace().getId() != null ) ){
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.PROPOSAL_DETAIL_PROPOSAL_WORK_PLACE_ID), getParams().getWorkPlace().getId());
			}
			if((getParams().getDepartment() != null && getParams().getDepartment().getId() != null ) ){
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.PROPOSAL_DETAIL_PROPOSAL_DEPARTMENT_ID), getParams().getDepartment().getId());
			}
			criteria.addOrder(bean.getFieldName(IEntityAlias.PROPOSAL_DETAIL_PROPOSAL_WORK_PLACE_ID));
			list = bean.getList(criteria);
		}
		return list;
	}
	
	public void onPurchaseConfirm(ActionEvent event) throws ManagerBeanException{
		IManagerBean bean = BeanManager.getManagerBean(ProposalDetail.class);
		Criteria criteria = getParams().getProposalStatusCriteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.PROPOSAL_DETAIL_STATUS), ProposalDetailStatus.PENDING);
		criteria.addOrder(bean.getFieldName(IEntityAlias.PROPOSAL_DETAIL_SUPPLIER_ID));
		if((getParams().getWorkPlace() != null && getParams().getWorkPlace().getId() != null ) ){
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.PROPOSAL_DETAIL_PROPOSAL_WORK_PLACE_ID), getParams().getWorkPlace().getId());
		}
		if((getParams().getDepartment() != null && getParams().getDepartment().getId() != null ) ){
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.PROPOSAL_DETAIL_PROPOSAL_DEPARTMENT_ID), getParams().getDepartment().getId());
		}
		criteria.addOrder(bean.getFieldName(IEntityAlias.PROPOSAL_DETAIL_PROPOSAL_DEPARTMENT_ID));
		int groupIndex = -1;
		Supplier supplier = null;
		Department dep = null;
		PurchaseGroup purchaseGroup = null;
		purchaseGroupList = new LinkedList<PurchaseOrderController.PurchaseGroup>();
		for(ITransferObject to: bean.getList(criteria)){
			ProposalDetail pd = (ProposalDetail) to;
			if(groupIndex==-1 || !dep.equals(pd.getProposal().getDepartment()) || !supplier.equals(pd.getSupplier()) ){
				supplier = pd.getSupplier();
				dep = pd.getProposal().getDepartment();
				groupIndex++;
				purchaseGroup = new PurchaseGroup();
				purchaseGroup.setSupplier(supplier);
				purchaseGroup.setWorkPlace(pd.getProposal().getWorkPlace());
				purchaseGroup.setDepartment(dep);
				purchaseGroup.setGroupIndex(groupIndex);
				purchaseGroup.setDetailList(new LinkedList<PurchaseOrderController.GroupDetail>());
				purchaseGroup.setTotalAmount(0.0);
				purchaseGroupList.add(purchaseGroup);
			}
			GroupDetail gd = new GroupDetail();
			gd.setProposalDetail(pd);
			gd.setChecked(true);
			gd.setGroupIndex(groupIndex);
			purchaseGroup.getDetailList().add(gd);
			purchaseGroup.setTotalAmount(purchaseGroup.getTotalAmount()+(gd.getProposalDetail().getItem().getPrice()*gd.getProposalDetail().getQuantity()));
		}
	}
	
	public void onPurchaseAccept(ActionEvent event) throws ManagerBeanException{
		//inicio transaccion
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName();
		try {
			try {
				HibernateUtil.setBeginTransaction(false);
				HibernateUtil.setCloseSession(false);
				HibernateUtil.beginTransaction(sessionName);
				// BEGIN operaciones de la transaccion
				purchasePrintcriteria = null;
				for(PurchaseGroup pg: purchaseGroupList){
					if(pg.hasCheckedDetail()){
						Purchase purchase = createPurchase(pg.getSupplier(), pg.getWorkPlace(), pg.getDepartment());
						addToPurchaseCriteria(purchase);
						for(GroupDetail gd: pg.getDetailList()){
							if(gd.isChecked()){
								insertPurchaseDetail(purchase, gd.getProposalDetail());
								updateProposalDetailStatus(gd.getProposalDetail().getId());
							}
						}
					}
				}
				FormUtil.getController("purchasePrint").setCriteria(purchasePrintcriteria);
				// FIN operaciones de la transaccion
				HibernateUtil.getSession(sessionName).flush();
				HibernateUtil.commitTransaction(sessionName);
			} catch (Exception e) {
				try {
					HibernateUtil.rollbackTransaction(sessionName);
				} catch (DAOException daoe) {
					String msg = "Unable to rollback transaction!";
					LOGGER.error(msg, e);
				}
				throw new ManagerBeanException(e.getMessage(),e);
			} finally {
				HibernateUtil.closeSession(sessionName);
			}
		} finally {
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);
		}
	}
	
	private void addToPurchaseCriteria(Purchase purchase) throws ExpressionException {
		if(purchasePrintcriteria==null){
			purchasePrintcriteria = new Criteria();
			purchasePrintcriteria.addEqualExpression("purchase.id", purchase.getId());
		} else {
			purchasePrintcriteria.addOrExpression("purchase.id", purchase.getId().toString());
		}
	}
	private Purchase createPurchase(Supplier supplier, WorkPlace workPlace, Department department) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(Purchase.class);
		Purchase pur = new Purchase();
		pur.setNumberOfPayments(1);
		pur.setDaysToFirstPayment(0);
		pur.setDaysBetweenPayments(0);
		pur.setPaymentDays("");
		pur.setSupplier(supplier);
		pur.setWorkPlace(workPlace);
		pur.setIssueDate(new Date());
		pur.setStatus(PurchaseStatus.PENDING);
		pur.setRegistryAddress(workPlace.getAddress());
		String serie = obtainWorkPlaceSerie(workPlace);
		pur.setSeries(serie);
		pur.setNumber(obtainSeriesMaxNumber(serie));
		pur.setScope(supplier.getScope());
	    Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
	    ResourceBundle bundle = ResourceBundle.getBundle("com.code.aon.ui.company.i18n.messages", locale); 
		pur.setComments(bundle.getString("company_department") +": "+ department.getName());
		return (Purchase) bean.insert(pur);
	}
	private void insertPurchaseDetail(Purchase pur, ProposalDetail proposalDetail) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(PurchaseDetail.class);
		PurchaseDetail purDet = new PurchaseDetail();
		purDet.setProject((pur.getProject() != null && pur.getProject().getId() != null) ? pur.getProject() : null);
		purDet.setProposalDetail(proposalDetail);
		purDet.setLine(calculateNextLine(pur));
		purDet.setStatus(PurchaseDetailStatus.PENDING);
		purDet.setPurchase(pur);
		purDet.setItem(proposalDetail.getItem());
		purDet.setDescription(proposalDetail.getItem().getProduct().getName());
		purDet.setPrice(proposalDetail.getPrice());
		purDet.setQuantity(proposalDetail.getQuantity());
		purDet.setPrice(proposalDetail.getItem().getPrice());
		purDet.setStatus(PurchaseDetailStatus.PENDING);
		if(proposalDetail.getDiscountExpr()!=null){
			purDet.setDiscountExpression(new DiscountExpression(proposalDetail.getDiscountExpr()));
		}
		bean.insert(purDet);
	}
	private void updateProposalDetailStatus(Integer proposalDetailId) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(ProposalDetail.class);
		ProposalDetail pd = (ProposalDetail) bean.get(proposalDetailId);
		pd.setStatus(ProposalDetailStatus.PROCESSED);
		bean.update(pd);
	}
	
	private String obtainWorkPlaceSerie(WorkPlace workPlace) throws ManagerBeanException {
		List<ITransferObject> seriesList = getWorkPlaceSeries(workPlace);
		return (seriesList.size() > 0) ? ((Series)seriesList.get(0)).getCode() : "";
	}

	public List<ITransferObject> getWorkPlaceSeries(WorkPlace workPlace) throws ManagerBeanException {
		IManagerBean seriesBean = BeanManager.getManagerBean(Series.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(seriesBean.getFieldName(IEntityAlias.SERIES_SCOPE_ID), workPlace.getScope().getId());
		criteria.addEqualExpression(seriesBean.getFieldName(IEntityAlias.SERIES_ACTIVE), new Boolean(true));
		criteria.addEqualExpression(seriesBean.getFieldName(IEntityAlias.SERIES_SECURITY_LEVEL), SecurityLevel.OFFICIAL);
		criteria.addEqualExpression(seriesBean.getFieldName(IEntityAlias.SERIES_INVOICE), new Boolean(true));
		return seriesBean.getList(criteria);
	}
	private int obtainSeriesMaxNumber(String seriesId) throws ManagerBeanException {
		return SeriesNumberUtil.obtainNumber(seriesId, "Purchase", null);
	}
	private	Integer calculateNextLine(Purchase purchase) throws ManagerBeanException {
		IManagerBean purchaseDetailBean = BeanManager.getManagerBean(PurchaseDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(purchaseDetailBean.getFieldName(IEntityAlias.PURCHASE_DETAIL_PURCHASE_ID), purchase.getId());
		Projection projection = Projection.max(purchaseDetailBean.getFieldName(IEntityAlias.PURCHASE_DETAIL_LINE));
		Object value = purchaseDetailBean.getUniqueResult(projection, criteria);
		return (value != null) ? ((Integer)value) + 1 : 1;
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
		buildGroupDetail();
		setDetailIndex(-1);
	}
	
	public List<SelectItem> getItemSuppliers(){
		List<SelectItem> list = new LinkedList<SelectItem>();
		Item item = ((ProposalDetail)getDetailModel().getRowData()).getItem();
		try {
			IManagerBean bean = BeanManager.getManagerBean(ItemSupplier.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.ITEM_SUPPLIER_ITEM_ID), item.getId());
			for (ITransferObject ito : bean.getList(criteria)) {
				ItemSupplier is = (ItemSupplier) ito;
				SelectItem i = new SelectItem(is.getSupplier(), is.getSupplier().getRegistry().getFullName());
				list.add(i);
			}
		} catch (ManagerBeanException e) {
			String msg =  "******** Error getting item suppliers. ";
			LOGGER.error(msg, e);
			AonUtil.addErrorMessage(msg + e.getMessage());
		}
		return list;
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
	
	public class PurchaseGroup{
		private Supplier supplier;
		private WorkPlace workPlace;
		private Department department;
		private int groupIndex;
		private List<GroupDetail> detailList;
		private Double totalAmount;
		
		public Supplier getSupplier() {
			return supplier;
		}
		public void setSupplier(Supplier supplier) {
			this.supplier = supplier;
		}
		public WorkPlace getWorkPlace() {
			return workPlace;
		}
		public void setWorkPlace(WorkPlace workPlace) {
			this.workPlace = workPlace;
		}
		public Department getDepartment() {
			return department;
		}
		public void setDepartment(Department department) {
			this.department = department;
		}
		public int getGroupIndex() {
			return groupIndex;
		}
		public void setGroupIndex(int groupIndex) {
			this.groupIndex = groupIndex;
		}
		public List<GroupDetail> getDetailList() {
			return detailList;
		}
		public void setDetailList(List<GroupDetail> detailList) {
			this.detailList = detailList;
		}
		public Double getTotalAmount() {
			return totalAmount;
		}
		public void setTotalAmount(Double totalAmount) {
			this.totalAmount = totalAmount;
		}
		public Integer getDetailCount(){
			return getDetailList().size();
		}
		
		public void onUncheckAll(ActionEvent event){
			checkAll(false);
		}

		public void onCheckAll(ActionEvent event){
			checkAll(true);
		}
		
		public void checkAll(boolean value){
			for(GroupDetail gd: getDetailList()){
				gd.setChecked(value);
			}
		}
		
		public boolean hasCheckedDetail(){
			for(GroupDetail gd: getDetailList()){
				if(gd.isChecked()){
					return true;
				}
			}
			return false;
		}
		
	}
	
	public class GroupDetail {
		private boolean checked;
		private ProposalDetail proposalDetail;
		private int groupIndex;
	
		public boolean isChecked() {
			return checked;
		}
		public void setChecked(boolean checked) {
			this.checked = checked;
		}
		public ProposalDetail getProposalDetail() {
			return proposalDetail;
		}
		public void setProposalDetail(ProposalDetail proposalDetail) {
			this.proposalDetail = proposalDetail;
		}
		public int getGroupIndex() {
			return groupIndex;
		}
		public void setGroupIndex(int groupIndex) {
			this.groupIndex = groupIndex;
		}
	}
	
	public class OrderParams{
		private Date startDate;
		private Date endDate;
		private WorkPlace workPlace;
		private WorkplaceDepartment workplaceDepartment;
		private Department department;
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
		public Department getDepartment() {
			return department;
		}
		public void setDepartment(Department department) {
			this.department = department;
		}
		public ProposalStatus getStatus() {
			return status;
		}
		public void setStatus(ProposalStatus status) {
			this.status = status;
		}

		public Criteria getProposalStatusCriteria() throws ManagerBeanException {
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
			return criteria;
		}
	}
	
}
