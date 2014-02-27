package com.code.aon.ui.purchase.controller;

import static com.code.aon.ui.common.ICommonMessages.PURCHASE_DEPARTMENT;
import static com.code.aon.ui.common.ICommonMessages.SOURCE;
import static com.code.aon.ui.purchase.controller.IPurchaseConstants.PURCHASE_PRINT_CONTROLLER_NAME;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;
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
import com.code.aon.company.Department;
import com.code.aon.company.WorkPlace;
import com.code.aon.company.WorkplaceDepartment;
import com.code.aon.product.Item;
import com.code.aon.product.ItemSupplier;
import com.code.aon.purchase.ProposalDetail;
import com.code.aon.purchase.Purchase;
import com.code.aon.purchase.enumeration.ProposalDetailStatus;
import com.code.aon.purchase.enumeration.ProposalStatus;
import com.code.aon.purchase.enumeration.ProposalTransferStatus;
import com.code.aon.purchase.enumeration.PurchaseDocumentType;
import com.code.aon.ql.Criteria;
import com.code.aon.supplier.Supplier;
import com.code.aon.ui.company.controller.CompanyCollectionsController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.form.DataScrollerState;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.purchase.event.PurchaseSearchListener;
import com.code.aon.ui.purchase.util.PurchaseUtils;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;


public class PurchaseOrderController extends DataScrollerState {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PurchaseOrderController.class.getName());
	
	private PurchaseUtils utils;
	private OrderParams params;
	private DataModel detailModel;
	private List<ItemGroup> itemGroupList;
	private List<PurchaseGroup> purchaseGroupList;
	private List<GroupDetail> proposalDetailList;
	private int productIndex;
	private int detailIndex;
	
	private CompanyCollectionsController companyCollections;
	
	public CompanyCollectionsController getCompanyCollections() {
		if(companyCollections == null){
			companyCollections = (CompanyCollectionsController) AonUtil.getRegisteredBean(ICompanyConstants.COLLECTIONS_CONTROLLER_NAME);
		}
		return companyCollections;
	}

	public PurchaseUtils getUtils() {
		if(utils == null) {
			utils = new PurchaseUtils();
		}
		return utils;
	}
	public void setUtils(PurchaseUtils utils) {
		this.utils = utils;
	}
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
				+ " AND ProposalDetail.proposal.itemReturn = :itemReturn"
				+ " AND ProposalDetail.proposal.transferStatus <> " + ProposalTransferStatus.TRANSFER_PENDING.ordinal()
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
		query.setBoolean("itemReturn", getParams().isItemReturn());
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
			return " AND ProposalDetail.proposal.workPlace IN ( " + StringUtils.join(getCompanyCollections().getCurrentUserWorkPlacesIds(), ",") +" )"; 
		}
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
			} else {
				criteria.addInExpression(bean.getFieldName(IEntityAlias.PROPOSAL_DETAIL_PROPOSAL_WORK_PLACE_ID), getCompanyCollections().getCurrentUserWorkPlacesIds());
			}
			if((getParams().getDepartment() != null && getParams().getDepartment().getId() != null ) ){
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.PROPOSAL_DETAIL_PROPOSAL_DEPARTMENT_ID), getParams().getDepartment().getId());
			}
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.PROPOSAL_DETAIL_PROPOSAL_ITEM_RETURN), getParams().isItemReturn());
			criteria.addNotEqualExpression(bean.getFieldName(IEntityAlias.PROPOSAL_DETAIL_PROPOSAL_TRANSFER_STATUS), ProposalTransferStatus.TRANSFER_PENDING);
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
		if(getParams().getWorkPlace() != null && getParams().getWorkPlace().getId() != null ){
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.PROPOSAL_DETAIL_PROPOSAL_WORK_PLACE_ID), getParams().getWorkPlace().getId());
		} else {
			criteria.addInExpression(bean.getFieldName(IEntityAlias.PROPOSAL_DETAIL_PROPOSAL_WORK_PLACE_ID), getCompanyCollections().getCurrentUserWorkPlacesIds());
		}
		if((getParams().getDepartment() != null && getParams().getDepartment().getId() != null ) ){
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.PROPOSAL_DETAIL_PROPOSAL_DEPARTMENT_ID), getParams().getDepartment().getId());
		}
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.PROPOSAL_DETAIL_PROPOSAL_ITEM_RETURN), getParams().isItemReturn());
		criteria.addNotEqualExpression(bean.getFieldName(IEntityAlias.PROPOSAL_DETAIL_PROPOSAL_TRANSFER_STATUS), ProposalTransferStatus.TRANSFER_PENDING);
		criteria.addOrder(bean.getFieldName(IEntityAlias.PROPOSAL_DETAIL_PROPOSAL_DEPARTMENT_ID));
		int groupIndex = -1;
		Supplier supplier = null;
		Department dep = null;
		WorkPlace wp = null;
		PurchaseGroup purchaseGroup = null;
		purchaseGroupList = new LinkedList<PurchaseOrderController.PurchaseGroup>();
		for(ITransferObject to: bean.getList(criteria)){
			ProposalDetail pd = (ProposalDetail) to;
			if(groupIndex==-1 
					|| !wp.equals(pd.getProposal().getWorkPlace()) 
					|| !dep.equals(pd.getProposal().getDepartment()) 
					|| !supplier.equals(pd.getSupplier()) ){
				supplier = pd.getSupplier();
				dep = pd.getProposal().getDepartment();
				wp = pd.getProposal().getWorkPlace();
				groupIndex++;
				purchaseGroup = new PurchaseGroup();
				purchaseGroup.setSupplier(supplier);
				purchaseGroup.setWorkPlace(wp);
				purchaseGroup.setDepartment(dep);
				purchaseGroup.setComments(pd.getProposal().getRemarks());
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
			purchaseGroup.setItemReturn(getParams().isItemReturn());
			purchaseGroup.setTotalAmount(purchaseGroup.getTotalAmount()+(gd.getProposalDetail().getPrice()*gd.getProposalDetail().getQuantity()));
		}
	}
	
	public void onPurchaseAccept(ActionEvent event) throws ManagerBeanException{
		//inicio transaccion
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName();
		List<Integer> purchaseIds = new LinkedList<Integer>();
		String comments = null;
		String remarks = null;
		try {
			try {
				HibernateUtil.setBeginTransaction(false);
				HibernateUtil.setCloseSession(false);
				HibernateUtil.beginTransaction(sessionName);
				// BEGIN operaciones de la transaccion
				PurchaseUtils utils = new PurchaseUtils();
				for(PurchaseGroup pg: purchaseGroupList){
					if(pg.hasCheckedDetail()){
						comments = AonUtil.getMessage(PURCHASE_DEPARTMENT) +": "+ pg.getDepartment().getName()+". ";
						Purchase purchase = utils.createPurchase(pg.getSupplier(), pg.getWorkPlace(),  
								pg.isItemReturn()?PurchaseDocumentType.ITEM_RETURN:null, comments + pg.getComments(), remarks);
						purchaseIds.add(purchase.getId());
						for(GroupDetail gd: pg.getDetailList()){
							if(gd.isChecked()){
								utils.insertPurchaseDetail(purchase, gd.getProposalDetail());
								utils.updateProposalDetailStatus(gd.getProposalDetail().getId());
								if(gd.getProposalDetail().getProposal().getTransferProposal()!=null){
									remarks = remarks==null?(AonUtil.getMessage(SOURCE) +": "):(remarks);
									remarks += gd.getProposalDetail().getProposal().getTransferProposal().getWorkPlace().getDescription();
									if( !(pg.getDepartment().getId().equals(gd.getProposalDetail().getProposal().getTransferProposal().getDepartment())) ){
										remarks += "("+gd.getProposalDetail().getProposal().getTransferProposal().getDepartment().getName()+"). ";
									}
								}
							}
						}
						if(remarks!=null){
							purchase.setRemarks(remarks);
							IManagerBean bean = BeanManager.getManagerBean(Purchase.class);
							bean.update(purchase);
						}
					}
				}
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
		IController purchsePrint = FormUtil.getController(PURCHASE_PRINT_CONTROLLER_NAME);
		purchsePrint.clearCriteria();
		if (! purchaseIds.isEmpty() ) {
			String alias = purchsePrint.getFieldName(IEntityAlias.PURCHASE_ID);
			purchsePrint.getCriteria().addInExpression(alias, purchaseIds);
			if(getParams().isItemReturn()){
				PurchaseSearchListener purchaseSearch = (PurchaseSearchListener) AonUtil.getRegisteredBean("purchasePrintSearch");
				purchaseSearch.setPurchaseDocumentTypes(null);
			}
		}
	}
	
	public void onSelectDetail(ActionEvent event) throws ManagerBeanException{
		setDetailIndex(getDetailModel().getRowIndex());
	}
	
	public void onAcceptDetail(ActionEvent event) throws ManagerBeanException{
		IManagerBean bean = BeanManager.getManagerBean(ProposalDetail.class);
		ProposalDetail pd = (ProposalDetail) getDetailModel().getRowData();
		pd.setSkipProposalUpdating(true);
		pd.setPrice( ((ItemSupplier)getItemSuppliers(pd.getItem(), pd.getSupplier()).get(0)).getPrice() );
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
		if( isReturnedProduct() ) {
			SelectItem i = new SelectItem(getUtils().getCompanySupplier(), getUtils().getCompanySupplier().getRegistry().getFullName());
			list.add(i);
		} else {
			ProposalDetail proposalDetail = (ProposalDetail)getDetailModel().getRowData();
			try {
				for (ITransferObject ito : getItemSuppliers(proposalDetail.getItem(), null)) {
					ItemSupplier is = (ItemSupplier) ito;
					if(is.getWorkPlace()==null){
						SelectItem i = new SelectItem(is.getSupplier(), is.getSupplier().getRegistry().getFullName());
						list.add(i);
					}
					if(is.getWorkPlace()!=null && is.getWorkPlace().getId().equals(proposalDetail.getProposal().getWorkPlace().getId())){
						SelectItem i = new SelectItem(is.getSupplier(), is.getSupplier().getRegistry().getFullName());
						list.add(0, i);
					}
				}
			} catch (ManagerBeanException e) {
				String msg =  "******** Error getting item suppliers. ";
				LOGGER.error(msg, e);
				AonUtil.addErrorMessage(msg + e.getMessage());
			}
		}
		return list;
	}
	
	private List<ITransferObject> getItemSuppliers(Item item, Supplier supplier) throws ManagerBeanException{
		IManagerBean bean = BeanManager.getManagerBean(ItemSupplier.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.ITEM_SUPPLIER_ITEM_ID), item.getId());
		if(supplier!=null && supplier.getId()!=null){
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.ITEM_SUPPLIER_SUPPLIER_ID), supplier.getId());
		}
		criteria.addOrder(bean.getFieldName(IEntityAlias.ITEM_SUPPLIER_PRIORITY));
		return bean.getList(criteria);
	}
	
	public boolean isReturnedProduct(){
		return isReturnedProduct((ProposalDetail)getDetailModel().getRowData());
	}
	
	public boolean isReturnedProduct(ProposalDetail proposalDetail){
		return proposalDetail.getProposal().isItemReturn();
	}

	public List<SelectItem> getAvailableDepartments() throws ManagerBeanException{
		if(getParams()==null || getParams().getWorkPlace()==null){
			return ((CompanyCollectionsController)AonUtil.getRegisteredBean(ICompanyConstants.COLLECTIONS_CONTROLLER_NAME)).getDepartments();
		} else {
			List<SelectItem> list = new LinkedList<SelectItem>();
			IManagerBean wdBean = BeanManager.getManagerBean(WorkplaceDepartment.class);
			Criteria wdCriteria = new Criteria();
			wdCriteria.addEqualExpression(wdBean.getFieldName(IEntityAlias.WORKPLACE_DEPARTMENT_WORK_PLACE_ID), getParams().getWorkPlace().getId());
			wdCriteria.addEqualExpression(wdBean.getFieldName(IEntityAlias.WORKPLACE_DEPARTMENT_ACTIVE), Boolean.TRUE);
			wdCriteria.addOrder(wdBean.getFieldName(IEntityAlias.WORKPLACE_DEPARTMENT_DEPARTMENT_ID));
			IManagerBean dBean = BeanManager.getManagerBean(Department.class);
			Criteria dCriteria = new Criteria();
			List<Integer> idList = new LinkedList<Integer>();
			for (ITransferObject ito : wdBean.getList(wdCriteria)) {
				WorkplaceDepartment wd = (WorkplaceDepartment)ito;
				idList.add(wd.getDepartment().getId());
			}
			dCriteria.addInExpression(dBean.getFieldName(IEntityAlias.DEPARTMENT_ID), idList);
			for (ITransferObject ito : dBean.getList(dCriteria)) {
				Department d = (Department)ito;
				SelectItem item = new SelectItem(d, d.getName());
				list.add(item);
			}
			return list;
		}
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
		private String comments;
		private boolean itemReturn;
		
		public boolean isItemReturn() {
			return itemReturn;
		}
		public void setItemReturn(boolean itemReturn) {
			this.itemReturn = itemReturn;
		}
		public String getComments() {
			return comments;
		}
		public void setComments(String comments) {
			this.comments = comments;
		}
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
		private boolean itemReturn;
		
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
		
		public boolean isItemReturn() {
			return itemReturn;
		}
		public void setItemReturn(boolean itemReturn) {
			this.itemReturn = itemReturn;
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
