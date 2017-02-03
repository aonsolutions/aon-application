package com.code.aon.ui.sales.controller;

import static com.code.aon.ui.common.ICommonMessages.DATE_PATTERN;
import static com.code.aon.ui.common.ICommonMessages.QUANTITY_PATTERN;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
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
import com.code.aon.purchase.PurchaseDetail;
import com.code.aon.ql.Criteria;
import com.code.aon.sales.Sales;
import com.code.aon.sales.SalesDetail;
import com.code.aon.sales.enumeration.SalesDetailStatus;
import com.code.aon.ui.common.ICommonMessages;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.sales.util.SalesUtils;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.warehouse.DeliveryDetail;
import com.esferalia.aon.entity.IEntityAlias;

public class SalesDetailController extends LinesController implements ISalesConstants {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private IPriceStrategy priceStrategy;
	private boolean longDescription;
	private SalesDetail salesDetail;
	private boolean showSerialNumberWindow;
	private boolean createSerialNumbers;
	private Item serializableItem;
	private double serializableQuantity;
	private String serialNumber;
	private Date serialDate;
	private List<SelectItem> serialNumbers;
	private String[] selectedBreakdown;
	private boolean showItemPackageWindow;
	
	private Map<Integer, PurchaseDetail> purchaseDetailMap = new HashMap<>();
	private Map<Integer, PurchaseDetail> manufactureDetailMap = new HashMap<>();

	public IPriceStrategy getPriceStrategy(){
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

		SalesDetail salesDetail = (SalesDetail)getTo();
		if (StringUtils.equals(salesDetail.getItem().getFullName().trim(), salesDetail.getDescription().trim())) {
			String longDescription = salesDetail.getItem().getDescription();
			if (!StringUtils.isEmpty(longDescription)) {
				salesDetail.setDescription(salesDetail.getDescription() + "\r\n" + longDescription);
			}
		}
	}

	public void onShortDescription(ActionEvent event) {
		setLongDescription(false);
	}

	public SalesDetail getSalesDetail() {
		return salesDetail;
	}

	public void setSalesDetail(SalesDetail salesDetail) {
		this.salesDetail = salesDetail;
	}

	public boolean isShowSerialNumberWindow() {
		return showSerialNumberWindow;
	}

	public void setShowSerialNumberWindow(boolean value) {
		this.showSerialNumberWindow = value;
	}

	public boolean isCreateSerialNumbers() {
		return createSerialNumbers;
	}

	public void setCreateSerialNumbers(boolean value) {
		this.createSerialNumbers = value;
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

	public boolean isPending() throws ManagerBeanException {
		if (getModel().isRowAvailable()) {
			SalesDetail salesDetail = (SalesDetail)this.getModel().getRowData();
			if (salesDetail.getStatus() != null) {
				return salesDetail.getStatus().equals(SalesDetailStatus.PENDING);
			}
		}
		return false;
	}

	public boolean isSettled() throws ManagerBeanException {
		if (getModel().isRowAvailable()) {
			SalesDetail salesDetail = (SalesDetail)this.getModel().getRowData();
			if (salesDetail.getStatus() != null) {
				return salesDetail.getStatus().equals(SalesDetailStatus.SETTLED);
			}
		}
		return false;
	}

	public boolean isEditable() throws ManagerBeanException {
		SalesDetail salesDetail = (SalesDetail)this.getTo();
		if (salesDetail != null) {
			return isEditable(salesDetail);
		}
		return false;
	}

	public boolean isModelEditable() throws ManagerBeanException {
		if (getModel().isRowAvailable()) {
			return isEditable((SalesDetail)this.getModel().getRowData());
		}
		return false;
	}

	private boolean isEditable(SalesDetail salesDetail) throws ManagerBeanException {
		return salesDetail.getOfferDetail() == null || salesDetail.getOfferDetail().getId() == null;
	}
	
	public boolean isManufactured() throws ManagerBeanException {
		if(this.getModel().isRowAvailable()) {
			SalesDetail salesDetail = (SalesDetail)this.getModel().getRowData();
			return manufactureDetailMap.containsKey(salesDetail.getId());
		}
		return false;
	}
	
	public boolean isPurchased() throws ManagerBeanException {
		if(this.getModel().isRowAvailable()) {
			SalesDetail salesDetail = (SalesDetail)this.getModel().getRowData();
			return purchaseDetailMap.containsKey(salesDetail.getId());
		}
		return false;
	}
	
	public void loadPurchaseDetailMap() {
		purchaseDetailMap = new HashMap<>();
		if(this.getMasterController().getTo()!=null){
			Sales sales = (Sales) this.getMasterController().getTo();
			SalesUtils utils = new SalesUtils();
			purchaseDetailMap = utils.getTargetPurchaseDetailMap(sales);
		}
	}
	
	public void onItemChanged(LookupChangeEvent event) {
		if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
			Item item = (Item)event.getNewValue();
			Sales sales = (Sales)getMasterController().getTo();

			SalesDetail salesDetail = (SalesDetail)getTo();
			salesDetail.setItem(item);
			salesDetail.setDescription(item.getFullName());
			if (salesDetail.getQuantity() == 0) {
				salesDetail.setQuantity(1);
			}
			salesDetail.setPrice(getPriceStrategy().getUnitPrice(salesDetail, sales.getIssueDate(), sales.getCustomer()));
		}
	}	

	public void onQuantityChanged(ValueChangeEvent event) {
		if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
			Sales sales = (Sales)getMasterController().getTo();
			SalesDetail salesDetail = (SalesDetail)getTo();
			if (salesDetail.getItem() != null && salesDetail.getItem().getId() != null) {
				salesDetail.setQuantity((Double)event.getNewValue());
				salesDetail.setPrice(getPriceStrategy().getUnitPrice(salesDetail, sales.getIssueDate(), sales.getCustomer()));
			}
		}
	}

	public void onAssignSerialNumberShow(ActionEvent event) throws ManagerBeanException {
		if (getModel().isRowAvailable()) {
			SalesDetail salesDetail = (SalesDetail)this.getModel().getRowData();
			setSalesDetail(salesDetail);
			setSerializableItem(salesDetail.getItem());
			setSerializableQuantity(salesDetail.getItem().getProduct().isLotable() ? salesDetail.getQuantity() : 1);
			setSerialNumber(null);
			setSerialDate(null);
			setSerialNumbers(obtainItemSerialNumbers(salesDetail.getItem().getProduct()));
			setSelectedBreakdown(null);
		} else {
			setShowSerialNumberWindow(false);
		}
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
		if (getSalesDetail() != null) {
			boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
			boolean mustCloseSession = HibernateUtil.mustCloseSession();
			String sessionName = HibernateUtil.getSessionFactoryName();
			try {
				HibernateUtil.setBeginTransaction(false);
				HibernateUtil.setCloseSession(false);
				HibernateUtil.beginTransaction(sessionName);
				
				assignSerialNumber(getSalesDetail());
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
		setSalesDetail(null);
	}

	private void assignSerialNumber(SalesDetail salesDetail) throws ManagerBeanException {
		int line = salesDetail.getLine();
		double quantity = getSelectedBreakdownQuantity();
		if (salesDetail.getQuantity() > quantity) {
			salesDetail.setQuantity(CommonUtil.round(salesDetail.getQuantity() - quantity, 3));
			getManagerBean().restoreNullSubPOJOs(salesDetail);
			getManagerBean().update(salesDetail);
		} else {
			salesDetail.setSkipOfferUpdate(true);
			getManagerBean().remove(salesDetail);
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
					SalesDetail newSalesDetail = new SalesDetail();
					newSalesDetail.setSales(salesDetail.getSales());
					newSalesDetail.setLine(++line);
					newSalesDetail.setItem(item);
					newSalesDetail.setDescription(salesDetail.getDescription() + " #" + item.getSerialNumber());
					newSalesDetail.setQuantity(breakdown.getQuantity());
					newSalesDetail.setPrice(salesDetail.getPrice());
					newSalesDetail.setDiscountExpression(salesDetail.getDiscountExpression());
					newSalesDetail.setTaxes(salesDetail.getTaxes());
					newSalesDetail.setStatus(SalesDetailStatus.PENDING);
					newSalesDetail.setOfferDetail(salesDetail.getOfferDetail());
					newSalesDetail.setDelivered(0);
					getManagerBean().restoreNullSubPOJOs(newSalesDetail);
					getManagerBean().insert(newSalesDetail);
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
		SalesDetail salesDetail = (SalesDetail)getTo();
		salesDetail.getItem().initializePackQuantities(salesDetail.getQuantity());
	}

	public void onAssignItemPackage(ActionEvent event) {
		SalesDetail salesDetail = (SalesDetail)getTo();
		salesDetail.setQuantity(salesDetail.getItem().getPackStockQuantity());
	}

	public double getAmount() {
		return getPriceStrategy().getBasePrice((ICalculable)this.getTo());
	}

	public double getModelAmount() throws ManagerBeanException {
		return getPriceStrategy().getBasePrice((ICalculable)this.getModel().getRowData());
	}

	public String getLineSourceInfo() throws ManagerBeanException {
		StringBuffer info = new StringBuffer(64);

		SalesDetail salesDetail = (SalesDetail)this.getModel().getRowData();
		if (salesDetail.getOfferDetail() != null && salesDetail.getOfferDetail().getId() != null) {
			info.append(AonUtil.getMessage(ICommonMessages.SOURCE));
			info.append(' ');
			info.append(AonUtil.getMessage(ICommonMessages.INVOICE_OFFER));
			info.append(' ');
			info.append(salesDetail.getOfferDetail().getOffer().getReferenceCode());
			info.append(" - ");
			info.append(AonUtil.getMessage(ICommonMessages.LINE));
			info.append(' ');
			info.append(salesDetail.getOfferDetail().getLine());
		}
		return info.toString();
	}

	public String getLineStatusInfo() throws ManagerBeanException {
		StringBuffer info = new StringBuffer(64);
		DecimalFormat formatter = new DecimalFormat(AonUtil.getMessage(ICommonMessages.QUANTITY_PATTERN));

		SalesDetail salesDetail = (SalesDetail)this.getModel().getRowData();
		IManagerBean deliveryDetailBean = BeanManager.getManagerBean(DeliveryDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(deliveryDetailBean.getFieldName(IEntityAlias.DELIVERY_DETAIL_SALES_DETAIL_ID), salesDetail.getId());
		Iterator<?> iterator = deliveryDetailBean.getList(criteria).iterator();
		while (iterator.hasNext()) {
			DeliveryDetail deliveryDetail = (DeliveryDetail)iterator.next();
			//info.append("<p>");
			info.append(AonUtil.getMessage(ICommonMessages.TRANSFERED_TO));
			info.append(' ');
			info.append(AonUtil.getMessage(ICommonMessages.INVOICE_DELIVERY));
			info.append(' ');
			info.append(deliveryDetail.getDelivery().getReferenceCode());
			info.append(" - ");
			info.append(AonUtil.getMessage(ICommonMessages.LINE));
			info.append(' ');
			info.append(deliveryDetail.getLine());
			if (salesDetail.getQuantity() > deliveryDetail.getQuantity()) {
				info.append(" (");
				info.append(formatter.format(deliveryDetail.getQuantity()));
				info.append(' ');
				info.append(AonUtil.getMessage(ICommonMessages.UNITS));
				info.append(')');
			}
			//info.append("</p>");
		}
		return info.toString();
	}
	
	public String getLineManufactureInfo() throws ManagerBeanException {
		StringBuffer info = new StringBuffer(64);
		DecimalFormat formatter = new DecimalFormat(AonUtil.getMessage(ICommonMessages.QUANTITY_PATTERN));
		
		SalesDetail salesDetail = (SalesDetail)this.getModel().getRowData();
		PurchaseDetail purchaseDetail = manufactureDetailMap.get(salesDetail.getId());
		if(purchaseDetail!=null && purchaseDetail.getId()!=null){
			info.append(AonUtil.getMessage(ICommonMessages.WAREHOUSE_MANUFACTURING_ORDER));
			info.append(' ');
			info.append(purchaseDetail.getPurchase().getReferenceCode());
			info.append(" - ");
			info.append(AonUtil.getMessage(ICommonMessages.LINE));
			info.append(' ');
			info.append(purchaseDetail.getLine());
			if (salesDetail.getQuantity() > purchaseDetail.getQuantity()) {
				info.append(" (");
				info.append(formatter.format(purchaseDetail.getQuantity()));
				info.append(' ');
				info.append(AonUtil.getMessage(ICommonMessages.UNITS));
				info.append(')');
			}
		}
		return info.toString();
	}
	
	public String getLinePurchaseInfo() throws ManagerBeanException {
		StringBuffer info = new StringBuffer(64);
		DecimalFormat formatter = new DecimalFormat(AonUtil.getMessage(ICommonMessages.QUANTITY_PATTERN));
		
		SalesDetail salesDetail = (SalesDetail)this.getModel().getRowData();
		PurchaseDetail purchaseDetail = purchaseDetailMap.get(salesDetail.getId());
		if(purchaseDetail!=null && purchaseDetail.getId()!=null){
			info.append(AonUtil.getMessage(ICommonMessages.PURCHASE_MODULE));
			info.append(' ');
			info.append(purchaseDetail.getPurchase().getReferenceCode());
			info.append(" - ");
			info.append(AonUtil.getMessage(ICommonMessages.LINE));
			info.append(' ');
			info.append(purchaseDetail.getLine());
			if (salesDetail.getQuantity() > purchaseDetail.getQuantity()) {
				info.append(" (");
				info.append(formatter.format(purchaseDetail.getQuantity()));
				info.append(' ');
				info.append(AonUtil.getMessage(ICommonMessages.UNITS));
				info.append(')');
			}
		}
		return info.toString();
	}

	public void onLoadOffer(ActionEvent event) throws ManagerBeanException {
		SalesDetail salesDetail = (SalesDetail)this.getModel().getRowData();
		BasicController offerController = (BasicController)AonUtil.getRegisteredBean(OFFER_CONTROLLER_NAME);
		offerController.onLoad(event, salesDetail.getOfferDetail().getOffer().getId(), SALES_FORM_NAME, null);
	}

	public void onLoadDelivery(ActionEvent event) throws ManagerBeanException {
		SalesDetail salesDetail = (SalesDetail)this.getModel().getRowData();
		IManagerBean deliveryDetailBean = BeanManager.getManagerBean(DeliveryDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(deliveryDetailBean.getFieldName(IEntityAlias.DELIVERY_DETAIL_SALES_DETAIL_ID), salesDetail.getId());
		criteria.addOrder(deliveryDetailBean.getFieldName(IEntityAlias.DELIVERY_DETAIL_ID), false);
		Iterator<?> iterator = deliveryDetailBean.getList(criteria).iterator();
		if (iterator.hasNext()) {
			DeliveryDetail deliveryDetail = (DeliveryDetail)iterator.next();
			BasicController deliveryController = (BasicController)AonUtil.getRegisteredBean(DELIVERY_CONTROLLER_NAME);
			deliveryController.onLoad(event, deliveryDetail.getDelivery().getId(), SALES_FORM_NAME, SALES_DETAIL_CONTROLLER_NAME + ".onBackSales");
		}
	}

	public void onBackSales(ActionEvent event) throws ManagerBeanException {
		SalesController salesController = (SalesController) AonUtil.getRegisteredBean(SALES_CONTROLLER_NAME);
		salesController.refresh(event);

		onSearch(event);
	}

	public void onLoadManufacturingOrder(ActionEvent event) throws ManagerBeanException {
		if(this.getModel().isRowAvailable()){
			SalesDetail salesDetail = (SalesDetail)this.getModel().getRowData();
			PurchaseDetail purchaseDetail = manufactureDetailMap.get(salesDetail.getId());
			if(purchaseDetail!=null && purchaseDetail.getId()!=null){
				BasicController sourceController = (BasicController)AonUtil.getRegisteredBean("manufacturingOrder");
				loadSourceLink(event, sourceController, purchaseDetail.getPurchase().getId());
			}
		}
	}
	public void onLoadPurchase(ActionEvent event) throws ManagerBeanException {
		if(this.getModel().isRowAvailable()){
			SalesDetail salesDetail = (SalesDetail)this.getModel().getRowData();
			PurchaseDetail purchaseDetail = purchaseDetailMap.get(salesDetail.getId());
			if(purchaseDetail!=null && purchaseDetail.getId()!=null){
				BasicController sourceController = (BasicController)AonUtil.getRegisteredBean("purchase");
				loadSourceLink(event, sourceController, purchaseDetail.getPurchase().getId());
			}
		}
	}
	private void loadSourceLink(ActionEvent event, BasicController sourceController, Integer targetId) throws ManagerBeanException {
		sourceController.onLoad(event, targetId, SALES_FORM_NAME, SALES_DETAIL_CONTROLLER_NAME + ".onBackSales");
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
			String label = (getItem()==null) ? "+" : "";
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