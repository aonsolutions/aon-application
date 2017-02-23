package com.code.aon.ui.purchase.controller;

import static com.code.aon.ui.common.ICommonMessages.DATE_PATTERN;
import static com.code.aon.ui.common.ICommonMessages.INVOICE_DELIVERY;
import static com.code.aon.ui.common.ICommonMessages.LINE;
import static com.code.aon.ui.common.ICommonMessages.PURCHASE_MODULE;
import static com.code.aon.ui.common.ICommonMessages.QUANTITY_PATTERN;
import static com.code.aon.ui.common.ICommonMessages.SALES_ORDER;
import static com.code.aon.ui.common.ICommonMessages.TRANSFERED_TO;
import static com.code.aon.ui.common.ICommonMessages.UNITS;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.product.Item;
import com.code.aon.product.Product;
import com.code.aon.product.enumeration.ProductStatus;
import com.code.aon.product.strategy.ICalculable;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.product.strategy.PriceStrategyFactory;
import com.code.aon.purchase.Purchase;
import com.code.aon.purchase.PurchaseDetail;
import com.code.aon.purchase.enumeration.PurchaseDetailStatus;
import com.code.aon.purchase.enumeration.PurchaseSource;
import com.code.aon.ql.Criteria;
import com.code.aon.sales.SalesDetail;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.warehouse.IncomeDetail;
import com.esferalia.aon.entity.IEntityAlias;

public class PurchaseDetailController extends LinesController implements IPurchaseConstants {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private IPriceStrategy priceStrategy;
	private boolean longDescription;
	private PurchaseDetail purchaseDetail;
	private boolean showSerialNumberWindow;
	private Item serializableItem;
	private double serializableQuantity;
	private String serialNumber;
	private Date serialDate;
	private List<SelectItem> serialNumbers;
	private String[] selectedBreakdown;
	private boolean showItemPackageWindow;
	private boolean showDeliveryDateWindow;
	private boolean showCarrierWindow;

	public IPriceStrategy getPriceStrategy() {
		if(priceStrategy == null){
			priceStrategy = PriceStrategyFactory.getPriceStrategy();
		}
		return priceStrategy;
	}

	public boolean isLongDescription() {
		return longDescription;
	}

	public void setLongDescription(boolean longDescription) {
		this.longDescription = longDescription;
	}

	public void onLongDescription(ActionEvent event) {
		setLongDescription(true);

		PurchaseDetail purchaseDetail = (PurchaseDetail)getTo();
		if (StringUtils.equals(purchaseDetail.getItem().getFullName().trim(), purchaseDetail.getDescription().trim())) {
			String longDescription = purchaseDetail.getItem().getDescription();
			if (!StringUtils.isEmpty(longDescription)) {
				purchaseDetail.setDescription(purchaseDetail.getDescription() + "\r\n" + longDescription);
			}
		}
	}

	public void onShortDescription(ActionEvent event) {
		setLongDescription(false);
	}

	public PurchaseDetail getPurchaseDetail() {
		return purchaseDetail;
	}

	public void setPurchaseDetail(PurchaseDetail purchaseDetail) {
		this.purchaseDetail = purchaseDetail;
	}

	public boolean isShowSerialNumberWindow() {
		return showSerialNumberWindow;
	}

	public void setShowSerialNumberWindow(boolean value) {
		this.showSerialNumberWindow = value;
	}

	public Item getSerializableItem() {
		return serializableItem;
	}

	public void setSerializableItem(Item serializableItem) {
		this.serializableItem = serializableItem;
	}

	public double getSerializableQuantity() {
		return serializableQuantity;
	}

	public void setSerializableQuantity(double serializableQuantity) {
		this.serializableQuantity = serializableQuantity;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public Date getSerialDate() {
		return serialDate;
	}

	public void setSerialDate(Date serialDate) {
		this.serialDate = serialDate;
	}

	public List<SelectItem> getSerialNumbers() {
		return serialNumbers;
	}

	public void setSerialNumbers(List<SelectItem> serialNumbers) {
		this.serialNumbers = serialNumbers;
	}

	public String[] getSelectedBreakdown() {
		return selectedBreakdown;
	}

	public void setSelectedBreakdown(String[] selectedBreakdown) {
		this.selectedBreakdown = selectedBreakdown;
	}

	public boolean isShowItemPackageWindow() {
		return showItemPackageWindow;
	}

	public void setShowItemPackageWindow(boolean value) {
		this.showItemPackageWindow = value;
	}
	
	public boolean isShowDeliveryDateWindow() {
		return showDeliveryDateWindow;
	}

	public void setShowDeliveryDateWindow(boolean value) {
		this.showDeliveryDateWindow = value;
	}

	public boolean isShowCarrierWindow() {
		return showCarrierWindow;
	}

	public void setShowCarrierWindow(boolean value) {
		this.showCarrierWindow = value;
	}

	protected String getLinkBackAction(){
		return PURCHASE_FORM_NAME;
	}
	
	public boolean isPurchaseSource() throws ManagerBeanException {
		if(this.getModel().isRowAvailable()){
			PurchaseDetail detail = (PurchaseDetail) this.getModel().getRowData();
			if(detail!=null){
				return detail.getSourceId()!=null && detail.getSource()==PurchaseSource.PURCHASE;
			}
		}
		return false;
	}

	public boolean isSalesSource() throws ManagerBeanException {
		if(this.getModel().isRowAvailable()){
			PurchaseDetail detail = (PurchaseDetail) this.getModel().getRowData();
			if(detail!=null){
				return detail.getSourceId()!=null && detail.getSource()==PurchaseSource.SALES;
			}
		}
		return false;
	}

	public boolean isProposalSource() throws ManagerBeanException {
		if(this.getModel().isRowAvailable()){
			PurchaseDetail detail = (PurchaseDetail) this.getModel().getRowData();
			if(detail!=null){
				return detail.getSourceId()!=null && detail.getSource()==PurchaseSource.PROPOSAL;
			}
		}
		return false;
	}
	
	public void onLoadPurchase(ActionEvent event) throws ManagerBeanException {
		if(this.getModel().isRowAvailable()){
			PurchaseDetail detail = (PurchaseDetail) this.getModel().getRowData();
			IManagerBean detailBean = BeanManager.getManagerBean(PurchaseDetail.class);
			Purchase purchase = ((PurchaseDetail) detailBean.get(detail.getSourceId())).getPurchase();
			BasicController controller = (BasicController)AonUtil.getRegisteredBean(PURCHASE_CONTROLLER_NAME);
			controller.onLoad(event, purchase.getId(), getLinkBackAction(), null);
		}
	}

	public void onLoadSales(ActionEvent event) throws ManagerBeanException {
		if(this.getModel().isRowAvailable()){
			PurchaseDetail purchaseDetail = (PurchaseDetail) this.getModel().getRowData();
			if(purchaseDetail.getSource()==PurchaseSource.SALES){
				IManagerBean salesDetailBean = BeanManager.getManagerBean(SalesDetail.class);
				SalesDetail salesDetail = (SalesDetail) salesDetailBean.get(purchaseDetail.getSourceId());
				Integer id = salesDetail.getSales().getId();
				BasicController controller = (BasicController)AonUtil.getRegisteredBean(SALES_CONTROLLER_NAME);
				controller.onLoad(event, id, getLinkBackAction(), null);
			}
		}
	}
	
//	TODO
//	public void onLoadProposal(ActionEvent event) throws ManagerBeanException {
//		if(this.getModel().isRowAvailable()){
//			PurchaseDetail detail = (PurchaseDetail) this.getModel().getRowData();
//			IManagerBean detailBean = BeanManager.getManagerBean(ProposalDetail.class);
//			Proposal proposal = ((ProposalDetail) detailBean.get(detail.getSourceId())).getProposal();
//			BasicController controller = (BasicController)AonUtil.getRegisteredBean(PROPOSAL_CONTROLLER_NAME);
//			controller.onLoad(event, proposal.getId(), getLinkBackAction(), null);
//		}
//	}

	public void onPurchaseDetailProjectShow(ActionEvent event) throws ManagerBeanException {
		if (getModel().isRowAvailable()) {
			setPurchaseDetail((PurchaseDetail)this.getModel().getRowData());
		}
	}

	public void addPurchaseDetailProject(ActionEvent event) throws ManagerBeanException {
		if(purchaseDetail.getProject()!=null && purchaseDetail.getProject().getId()==null){
			purchaseDetail.setProject(null);
		}
		getManagerBean().update(purchaseDetail);
	}

	public boolean isPending() throws ManagerBeanException {
		if (getModel().isRowAvailable()) {
			PurchaseDetail purchaseDetail = (PurchaseDetail)this.getModel().getRowData();
			if (purchaseDetail.getStatus() != null) {
				return purchaseDetail.getStatus().equals(PurchaseDetailStatus.PENDING);
			}
		}
		return false;
	}

	public boolean isSettled() throws ManagerBeanException {
		if (getModel().isRowAvailable()) {
			PurchaseDetail purchaseDetail = (PurchaseDetail)this.getModel().getRowData();
			if (purchaseDetail.getStatus() != null) {
				return purchaseDetail.getStatus().equals(PurchaseDetailStatus.SETTLED);
			}
		}
		return false;
	}

	public void onItemChanged(LookupChangeEvent event) {
		if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
			Item item = (Item)event.getNewValue();
			Purchase purchase = (Purchase)getMasterController().getTo();

			PurchaseDetail purchaseDetail = (PurchaseDetail)getTo();
			purchaseDetail.setItem(item);
			purchaseDetail.setDescription(item.getFullName());
			if (purchaseDetail.getQuantity() == 0) {
				purchaseDetail.setQuantity(1);
			}
			purchaseDetail.setPrice(getPriceStrategy().getUnitPurchasePrice(purchaseDetail, purchase.getIssueDate(), purchase.getSupplier()));
		}
	}

	public void onPurchaseDetailSelect(ActionEvent event) throws ManagerBeanException {
		if (getModel().isRowAvailable()) {
			setPurchaseDetail((PurchaseDetail)this.getModel().getRowData());
		}
	}

	public void onPurchaseDetailSave(ActionEvent event) throws ManagerBeanException {
		getManagerBean().restoreNullSubPOJOs(getPurchaseDetail());
		getManagerBean().update(getPurchaseDetail());
	}

	public void onAssignSerialNumberShow(ActionEvent event) throws ManagerBeanException {
		if (getModel().isRowAvailable()) {
			loadAssignSerialNumber((PurchaseDetail)this.getModel().getRowData());
		} else {
			setShowSerialNumberWindow(false);
		}
	}
	
	protected void loadAssignSerialNumber(PurchaseDetail purchaseDetail) throws ManagerBeanException {
		setPurchaseDetail(purchaseDetail);
		setSerializableItem(purchaseDetail.getItem());
		setSerializableQuantity(purchaseDetail.getItem().getProduct().isLotable() ? purchaseDetail.getQuantity() : 1);
		setSerialNumber(null);
		setSerialDate(null);
		setSerialNumbers(obtainItemSerialNumbers(purchaseDetail.getItem().getProduct()));
		setSelectedBreakdown(null);
	}

	private List<SelectItem> obtainItemSerialNumbers(Product product) throws ManagerBeanException {
		List<SelectItem> items = new LinkedList<SelectItem>();
		IManagerBean itemBean = BeanManager.getManagerBean(Item.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(itemBean.getFieldName(IEntityAlias.ITEM_PRODUCT_ID), product.getId());
		criteria.addEqualExpression(itemBean.getFieldName(IEntityAlias.ITEM_STATUS), ProductStatus.ACTIVE);
		criteria.addNotNullExpression(itemBean.getFieldName(IEntityAlias.ITEM_SERIAL_NUMBER));
		criteria.addNotEqualExpression(itemBean.getFieldName(IEntityAlias.ITEM_SERIAL_NUMBER), StringUtils.EMPTY);
		criteria.addOrder(itemBean.getFieldName(IEntityAlias.ITEM_SERIAL_NUMBER));
		for (ITransferObject ito : itemBean.getList(criteria)) {
			SerializableBreakdown breakdown = new SerializableBreakdown((Item)ito);
			breakdown.setQuantity(getSerializableQuantity());
			items.add(new SelectItem(breakdown, breakdown.getLabel()));
		}
		return items;
	}

	public void onChangeSerialQuantity(ActionEvent event) {
		for (SelectItem selectItem : getSerialNumbers()) {
			if (ArrayUtils.contains(getSelectedBreakdown(), selectItem.getLabel())) {
				SerializableBreakdown breakdown = (SerializableBreakdown)selectItem.getValue();
				int index = ArrayUtils.indexOf(getSelectedBreakdown(), breakdown.getLabel());
				breakdown.setQuantity(getSerializableQuantity());
				selectItem.setLabel(breakdown.getLabel());
				setSelectedBreakdown((String[])ArrayUtils.add(getSelectedBreakdown(), index, breakdown.getLabel()));
			}
		}
	}

	public void onAddSerialNumber(ActionEvent event) {
		if (StringUtils.isNotBlank(getSerialNumber())) {
			SerializableBreakdown breakdown = new SerializableBreakdown();
			breakdown.setLotable(getSerializableItem().getProduct().isLotable());
			breakdown.setSerialNumber(getSerialNumber());
			breakdown.setSerialDate(getSerialDate());
			breakdown.setQuantity(getSerializableQuantity());
			getSerialNumbers().add(new SelectItem(breakdown, breakdown.getLabel()));
			setSerialNumber(null);
		}
	}

	public void onRemoveSerialNumber(ActionEvent event) {
		for (String breakdownLabel : getSelectedBreakdown()) {
			for (SelectItem selectItem : getSerialNumbers()) {
				SerializableBreakdown breakdown = (SerializableBreakdown)selectItem.getValue();
				if (breakdown.getLabel().equals(breakdownLabel)) {
					getSerialNumbers().remove(selectItem);
					break;
				}
			}
		}
		setSelectedBreakdown(null);
	}

	public void onAssignSerialNumber(ActionEvent event) throws ManagerBeanException {
		if (getPurchaseDetail() != null) {
			boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
			boolean mustCloseSession = HibernateUtil.mustCloseSession();
			String sessionName = HibernateUtil.getSessionFactoryName();
			try {
				HibernateUtil.setBeginTransaction(false);
				HibernateUtil.setCloseSession(false);
				HibernateUtil.beginTransaction(sessionName);
				
				assignSerialNumber(getPurchaseDetail());
				onSearch(event);
				
				HibernateUtil.commitTransaction(sessionName);
			} catch (Exception e) {
				try {
					HibernateUtil.rollbackTransaction(sessionName);
				} catch (DAOException daoe) {}
				AonUtil.addErrorMessage(e.getMessage());
				throw new AbortProcessingException(e.getMessage());
			} finally {
				HibernateUtil.closeSession(sessionName);
				HibernateUtil.setCloseSession(mustCloseSession);
				HibernateUtil.setBeginTransaction(mustBeginTransaction);
			}
		}
		setPurchaseDetail(null);
	}

	private void assignSerialNumber(PurchaseDetail purchaseDetail) throws ManagerBeanException {
		int line = purchaseDetail.getLine();
		double quantity = getSelectedBreakdownQuantity();
		if (purchaseDetail.getQuantity() > quantity) {
			purchaseDetail.setQuantity(CommonUtil.round(purchaseDetail.getQuantity() - quantity, 3));
			getManagerBean().restoreNullSubPOJOs(purchaseDetail);
			getManagerBean().update(purchaseDetail);
		} else {
			getManagerBean().remove(purchaseDetail);
			--line;
		}

		for (SelectItem selectItem : getSerialNumbers()) {
			if (ArrayUtils.contains(getSelectedBreakdown(), selectItem.getLabel())) {
				SerializableBreakdown breakdown = (SerializableBreakdown)selectItem.getValue();
				Item item = breakdown.getItem();
				if (item == null) {
					IManagerBean itemBean = BeanManager.getManagerBean(Item.class);
					Criteria criteria = new Criteria();
					criteria.addEqualExpression(itemBean.getFieldName(IEntityAlias.ITEM_PRODUCT_ID), getSerializableItem().getProduct().getId());
					criteria.addEqualExpression(itemBean.getFieldName(IEntityAlias.ITEM_SERIAL_NUMBER), breakdown.getSerialNumber());
					List<ITransferObject> itemList = itemBean.getList(criteria);
					if (!itemList.isEmpty()) {
						item = (Item)itemList.get(0);
					} else {
						item = new Item();
						Product product = (Product)HibernateUtil.getSession(HibernateUtil.getSessionFactoryName()).merge(getSerializableItem().getProduct());
						item.setProduct(product);
						item.setDescription(getSerializableItem().getDescription());
						item.setSerialNumber(breakdown.getSerialNumber());
						item.setSerialDate(breakdown.getSerialDate());
						item.setPrice(getSerializableItem().getPrice());
						item.setProfitPercent(getSerializableItem().getProfitPercent());
						item.setPurchasePrice(getSerializableItem().getPurchasePrice());
						item.setStatus(ProductStatus.ACTIVE);
						itemBean.restoreNullSubPOJOs(item);
						item = (Item)itemBean.insert(item);
					}
				}

				if (item != null) {
					PurchaseDetail newPurchaseDetail = new PurchaseDetail();
					newPurchaseDetail.setPurchase(purchaseDetail.getPurchase());
					newPurchaseDetail.setProject(purchaseDetail.getProject());
					newPurchaseDetail.setLine(++line);
					newPurchaseDetail.setItem(item);
					newPurchaseDetail.setDescription(purchaseDetail.getDescription() + " #" + item.getSerialNumber());
					newPurchaseDetail.setQuantity(breakdown.getQuantity());
					newPurchaseDetail.setPrice(purchaseDetail.getPrice());
					newPurchaseDetail.setDiscountExpression(purchaseDetail.getDiscountExpression());
					newPurchaseDetail.setTaxes(purchaseDetail.getTaxes());
					newPurchaseDetail.setStatus(PurchaseDetailStatus.PENDING);
					newPurchaseDetail.setProposalDetail(purchaseDetail.getProposalDetail());
					newPurchaseDetail.setDelivered(0);
					newPurchaseDetail.setSource(purchaseDetail.getSource());
					newPurchaseDetail.setSourceId(purchaseDetail.getSourceId());
					newPurchaseDetail.setDeliveryDate(purchaseDetail.getDeliveryDate());
					newPurchaseDetail.setCarrier(purchaseDetail.getCarrier());
					newPurchaseDetail.setCarrierPacking(purchaseDetail.getCarrierPacking());
					getManagerBean().restoreNullSubPOJOs(newPurchaseDetail);
					getManagerBean().insert(newPurchaseDetail);
				}
			}
		}
	}

	private double getSelectedBreakdownQuantity() {
		double quantity = 0;
		for (SelectItem selectItem : getSerialNumbers()) {
			if (ArrayUtils.contains(getSelectedBreakdown(), selectItem.getLabel())) {
				SerializableBreakdown breakdown = (SerializableBreakdown)selectItem.getValue();
				quantity = CommonUtil.round(quantity + breakdown.getQuantity(), 3);
			}
		}
		return quantity;
	}

	public void onItemPackageShow(ActionEvent event) {
		PurchaseDetail purchaseDetail = (PurchaseDetail)getTo();
		purchaseDetail.getItem().initializePackQuantities(purchaseDetail.getQuantity());
	}

	public void onAssignItemPackage(ActionEvent event) {
		PurchaseDetail purchaseDetail = (PurchaseDetail)getTo();
		purchaseDetail.setQuantity(purchaseDetail.getItem().getPackStockQuantity());
	}

	public double getAmount() {
		return getPriceStrategy().getBasePrice((ICalculable)this.getTo());
	}

	public double getModelAmount() throws ManagerBeanException {
		return getPriceStrategy().getBasePrice((ICalculable)this.getModel().getRowData());
	}

	public String getLineStatusInfo() throws ManagerBeanException {
		StringBuffer info = new StringBuffer(64);
		DecimalFormat formatter = new DecimalFormat(AonUtil.getMessage(QUANTITY_PATTERN));

		PurchaseDetail purchaseDetail = (PurchaseDetail)this.getModel().getRowData();
		IManagerBean incomeDetailBean = BeanManager.getManagerBean(IncomeDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(incomeDetailBean.getFieldName(IEntityAlias.INCOME_DETAIL_PURCHASE_DETAIL_ID), purchaseDetail.getId());
		Iterator<?> iterator = incomeDetailBean.getList(criteria).iterator();
		while (iterator.hasNext()) {
			IncomeDetail incomeDetail = (IncomeDetail)iterator.next();
			info.append("<aon:div>");
			info.append(AonUtil.getMessage(TRANSFERED_TO));
			info.append(" ");
			info.append(AonUtil.getMessage(INVOICE_DELIVERY));
			info.append(" ");
			info.append(incomeDetail.getIncome().getReferenceCode());
			info.append(" - ");
			info.append(AonUtil.getMessage(LINE));
			info.append(" ");
			info.append(incomeDetail.getLine());
			if (purchaseDetail.getQuantity() > incomeDetail.getQuantity()) {
				info.append(" (");
				info.append(formatter.format(incomeDetail.getQuantity()));
				info.append(" ");
				info.append(AonUtil.getMessage(UNITS));
				info.append(")");
			}
			info.append("</aon:div>");
		}
		return info.toString();
	}
	
	public String getLineSalesInfo() throws ManagerBeanException {
		StringBuffer info = new StringBuffer(64);
		DecimalFormat formatter = new DecimalFormat(AonUtil.getMessage(QUANTITY_PATTERN));
		
		PurchaseDetail purchaseDetail = (PurchaseDetail)this.getModel().getRowData();
		if(purchaseDetail.getSourceId()!=null && purchaseDetail.getSource()==PurchaseSource.SALES){
			IManagerBean salesDetailBean = BeanManager.getManagerBean(SalesDetail.class);
			SalesDetail salesDetail = (SalesDetail) salesDetailBean.get(purchaseDetail.getSourceId());
			info.append("<aon:div>");
			info.append(AonUtil.getMessage(SALES_ORDER));
			info.append(" ");
			info.append(salesDetail.getSales().getReferenceCode());
			info.append(" - ");
			info.append(AonUtil.getMessage(LINE));
			info.append(" ");
			info.append(salesDetail.getLine());
			if (purchaseDetail.getQuantity() > salesDetail.getQuantity()) {
				info.append(" (");
				info.append(formatter.format(salesDetail.getQuantity()));
				info.append(" ");
				info.append(AonUtil.getMessage(UNITS));
				info.append(")");
			}
			info.append("</aon:div>");
		}
		return info.toString();
	}
	
	public String getLinePurchaseInfo() throws ManagerBeanException {
		StringBuffer info = new StringBuffer(64);
		DecimalFormat formatter = new DecimalFormat(AonUtil.getMessage(QUANTITY_PATTERN));
		
		PurchaseDetail purchaseDetail = (PurchaseDetail)this.getModel().getRowData();
		if(purchaseDetail.getSourceId()!=null && purchaseDetail.getSource()==PurchaseSource.PURCHASE){
			IManagerBean purchaseDetailBean = BeanManager.getManagerBean(PurchaseDetail.class);
			PurchaseDetail sourcePurchaseDetail = (PurchaseDetail) purchaseDetailBean.get(purchaseDetail.getSourceId());
			info.append("<aon:div>");
			info.append(AonUtil.getMessage(PURCHASE_MODULE));
			info.append(" ");
			info.append(sourcePurchaseDetail.getPurchase().getReferenceCode());
			info.append(" - ");
			info.append(AonUtil.getMessage(LINE));
			info.append(" ");
			info.append(sourcePurchaseDetail.getLine());
			if (sourcePurchaseDetail.getQuantity() > sourcePurchaseDetail.getQuantity()) {
				info.append(" (");
				info.append(formatter.format(sourcePurchaseDetail.getQuantity()));
				info.append(" ");
				info.append(AonUtil.getMessage(UNITS));
				info.append(")");
			}
			info.append("</aon:div>");
		}
		return info.toString();
	}

	public void onLoadIncome(ActionEvent event) throws ManagerBeanException {
		PurchaseDetail purchaseDetail = (PurchaseDetail)this.getModel().getRowData();
		IManagerBean incomeDetailBean = BeanManager.getManagerBean(IncomeDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(incomeDetailBean.getFieldName(IEntityAlias.INCOME_DETAIL_PURCHASE_DETAIL_ID), purchaseDetail.getId());
		criteria.addOrder(incomeDetailBean.getFieldName(IEntityAlias.INCOME_DETAIL_ID), false);
		Iterator<?> iterator = incomeDetailBean.getList(criteria).iterator();
		if (iterator.hasNext()) {
			IncomeDetail incomeDetail = (IncomeDetail)iterator.next();
			BasicController incomeController = (BasicController)AonUtil.getRegisteredBean(INCOME_CONTROLLER_NAME);
			incomeController.onLoad(event, incomeDetail.getIncome().getId(), PURCHASE_FORM_NAME, PURCHASE_DETAIL_CONTROLLER_NAME + ".onBackPurchase");
		}
	}

	public void onBackPurchase(ActionEvent event) throws ManagerBeanException {
		PurchaseController purchaseController = (PurchaseController) AonUtil.getRegisteredBean(PURCHASE_CONTROLLER_NAME);
		purchaseController.refresh(event);
		onSearch(event);
	}
	
	public void onLoadProposal(ActionEvent event) throws ManagerBeanException {
		PurchaseDetail purchaseDetail = (PurchaseDetail)this.getModel().getRowData();
		BasicController proposalController = (BasicController)AonUtil.getRegisteredBean(IPurchaseConstants.PROPOSAL_CONTROLLER_NAME);
		proposalController.onLoad(event, purchaseDetail.getProposalDetail().getProposal().getId(), "purchase_form", null);
	}


	public static class SerializableBreakdown implements Serializable {
		
		private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
		
		private Item item;
		private boolean lotable;
		private String serialNumber;
		private Date serialDate;
		private double quantity;

		public SerializableBreakdown() {
			setItem(null);
			setLotable(false);
			setSerialNumber("");
			setQuantity(0);
		}

		public SerializableBreakdown(Item item) {
			setItem(item);
			setLotable(item.getProduct().isLotable());
			setSerialNumber(item.getSerialNumber());
			setSerialDate(item.getSerialDate());
			setQuantity(0);
		}

		public Item getItem() {
			return item;
		}
		public void setItem(Item item) {
			this.item = item;
		}

		public boolean isLotable() {
			return lotable;
		}
		public void setLotable(boolean lotable) {
			this.lotable = lotable;
		}

		public String getSerialNumber() {
			return serialNumber;
		}
		public void setSerialNumber(String serialNumber) {
			this.serialNumber = serialNumber;
		}

		public Date getSerialDate() {
			return serialDate;
		}
		public void setSerialDate(Date serialDate) {
			this.serialDate = serialDate;
		}

		public double getQuantity() {
			return quantity;
		}
		public void setQuantity(double quantity) {
			this.quantity = quantity;
		}

		public String getLabel() {
			String label = "";
			if (isLotable()) {
				NumberFormat numberFormat = new DecimalFormat(AonUtil.getMessage(QUANTITY_PATTERN));
				label += "(" + numberFormat.format(getQuantity()) + ") ";
			}
			label += "#" + getSerialNumber();
			if (getSerialDate() != null) {
				DateFormat dateFormat = new SimpleDateFormat(AonUtil.getMessage(DATE_PATTERN));
				label += " [" + dateFormat.format(getSerialDate()) + "]";
			}
			return label;
		}

		@Override
		public String toString() {
			return getLabel();
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null) return false;
			final SerializableBreakdown o = (SerializableBreakdown)obj;
			return o.getSerialNumber().equals(getSerialNumber()) && o.getQuantity() == getQuantity();
		}

	}

}