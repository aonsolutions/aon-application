package com.code.aon.ui.sales.util;

import static com.code.aon.ui.common.ICommonMessages.SALES_TO_PURCHASE;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.config.util.SeriesNumberUtil;
import com.code.aon.finance.Finance;
import com.code.aon.product.Item;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.purchase.Purchase;
import com.code.aon.purchase.PurchaseDetail;
import com.code.aon.purchase.enumeration.PurchaseDetailStatus;
import com.code.aon.purchase.enumeration.PurchaseDocumentType;
import com.code.aon.purchase.enumeration.PurchaseSource;
import com.code.aon.purchase.enumeration.PurchaseStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryItem;
import com.code.aon.registry.enumeration.RegistryMode;
import com.code.aon.sales.Sales;
import com.code.aon.sales.SalesDetail;
import com.code.aon.sales.enumeration.DocumentType;
import com.code.aon.seller.Seller;
import com.code.aon.supplier.Supplier;
import com.code.aon.ui.common.serialize.SerializableListDataModel;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.DataScrollerState;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

/**
 * This creates the necessary purchase orders resulting from sales order
 * 
 * @author Esferalia
 *
 */
public class PurchaseGeneratorManager extends DataScrollerState {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PurchaseGeneratorManager.class.getName());
	
	private boolean nevv;
	private boolean customerShippingAddress;
	private List<TempPurchaseDetail> tempPurchaseDetail;
	private Sales sales;
	private TempPurchaseDetail to;
	private String massiveDiscountExpr;
	
	public PurchaseGeneratorManager() {
		setBeanName("salesDetails");
		setPageLimit(-1);
	}

	public boolean isNevv() {
		return nevv;
	}
	
	public String getMassiveDiscountExpr() {
		return massiveDiscountExpr;
	}

	public void setMassiveDiscountExpr(String massiveDiscountExpr) {
		this.massiveDiscountExpr = massiveDiscountExpr;
	}

	public boolean isGenerated() {
		return !isNevv();
	}
	
	public TempPurchaseDetail getTo() {
		return to;
	}
	public void setTo(TempPurchaseDetail to) {
		this.to = to;
	}

	public void onSelect(ActionEvent event) {
		if ( getDirectModel().isRowAvailable() ) {
			setTo((TempPurchaseDetail) getDirectModel().getRowData());
			if(getTo().isReadOnly()){
				onCancel(event);
			}
		}
	}
	
	public void onAccept(ActionEvent event) {
		this.to = null;
	}

	public void onCancel(ActionEvent event) {
		this.to.setSupplier(obtainPreferedSupplier(this.to.getDetail()));
		this.to = null;
	}

	public PurchaseGeneratorManager(Sales sales) {
		this.sales = sales;
		this.nevv = true;
		try {
			buildTempList(obtainSalesDetail(sales));
			setModel(new SerializableListDataModel(getTempPurchaseDetail()));
		} catch (ManagerBeanException e) {
			String msg = "No se ha podido obtener el detalle del pedido.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		}
	}

	public boolean isCustomerShippingAddress() {
		return customerShippingAddress;
	}

	public void setCustomerShippingAddress(boolean customerShippingAddress) {
		this.customerShippingAddress = customerShippingAddress;
	}
	
	private List<ITransferObject> obtainSalesDetail(Sales sales) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(SalesDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.SALES_DETAIL_SALES_ID), sales.getId());
		criteria.addOrder(bean.getFieldName(IEntityAlias.SALES_DETAIL_LINE));
		return bean.getList(criteria);
	}

	public List<TempPurchaseDetail> getTempPurchaseDetail() {
		return tempPurchaseDetail;
	}

	public void setTempPurchaseDetail(List<TempPurchaseDetail> tempPurchaseDetail) {
		this.tempPurchaseDetail = tempPurchaseDetail;
	}

	/**
	 * Build the list, searching the corresponding supplier defined in item_supplier for each sales detail line
	 * 
	 * @param salesDetailList
	 */
	private void buildTempList(List<ITransferObject> salesDetailList) {
		setTempPurchaseDetail(new LinkedList<PurchaseGeneratorManager.TempPurchaseDetail>());
		for(ITransferObject to: salesDetailList){
			SalesDetail detail = (SalesDetail) to;
			Supplier supplier = obtainPreferedSupplier(detail);
			TempPurchaseDetail temp = new TempPurchaseDetail();
			detail.setDiscountExpression(new DiscountExpression("0"));
			temp.setDetail(detail);
			temp.setSupplier(supplier);
			temp.setReadOnly(detail.getQuantity()==0);
			getTempPurchaseDetail().add(temp);
		}
	}

	private Supplier obtainPreferedSupplier(SalesDetail detail) {
		Supplier supplier = null;
		try {
			supplier = (Supplier) BeanManager.getManagerBean(Supplier.class).createNewTo();
			IManagerBean bean = BeanManager.getManagerBean(RegistryItem.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_ITEM_ITEM_ID), detail.getItem().getId());
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_ITEM_TYPE), RegistryMode.SUPPLIER);
			criteria.addOrder(bean.getFieldName(IEntityAlias.REGISTRY_ITEM_PRIORITY));
			
			for (ITransferObject ito : bean.getList(criteria)) {
				RegistryItem rItem = (RegistryItem)ito;
				if((rItem.getWorkPlace()==null || rItem.getWorkPlace().getId().equals(detail.getSales().getWorkPlace().getId())) && supplier.getId()==null){
					supplier = (Supplier)BeanManager.getManagerBean(Supplier.class).get(rItem.getRegistry().getId());
				}
			}
			return supplier!=null?supplier:(Supplier) BeanManager.getManagerBean(Supplier.class).createNewTo();
		} catch (ManagerBeanException e) {
			String msg = "No se ha podido obtener el proveedor definido para el producto " + detail.getDescription() + " (linea " + detail.getLine() +")";
			AonUtil.addErrorMessage(msg);
		}
		return null;
	}
	
	public void onLoadPurchase(ActionEvent event) throws ManagerBeanException {
		Purchase purchase = (Purchase)this.getModel().getRowData();
		BasicController controller = (BasicController)AonUtil.getRegisteredBean("purchase");
		controller.onLoad(event, purchase.getId(), "sales_form", null);
	}
	
	@SuppressWarnings("unchecked")
	public void onApplyMassiveDiscount (ActionEvent event) {
		try {
			for(TempPurchaseDetail purchaseDetail: (List<TempPurchaseDetail>)getModel().getWrappedData()){
				purchaseDetail.getDetail().getDiscountExpression().setDiscountExpr(getMassiveDiscountExpr());
			}
		} catch (Exception e) {
			AonUtil.addErrorMessage("No se ha podido aplicar el descuento.");
			AonUtil.addErrorMessage(e.getMessage());
		}
	}
	
	public void onExecute(ActionEvent event){
		List<Purchase> purchaseList = null;
		
		beforePurchasesCreate();

		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName(Finance.class.getName());
		try {
			HibernateUtil.setBeginTransaction(false);
			HibernateUtil.setCloseSession(false);
			HibernateUtil.beginTransaction(sessionName);
			// begin process 
			purchaseList = new LinkedList<Purchase>();
			Purchase purchase = null;
			List<TempPurchaseDetail> readOnlyDetails = new LinkedList<TempPurchaseDetail>();
			for(TempPurchaseDetail temp: getTempPurchaseDetail()){
				if(!temp.isReadOnly()){
					if(purchase==null || !purchase.getSupplier().equals(temp.getSupplier())){
						purchase = createPurchase(temp.getDetail().getSales(), temp.getSupplier());
						purchaseList.add(purchase);
					}
					createPurchaseDetails(purchase, temp.getDetail(), temp.isReadOnly());
				} else {
					readOnlyDetails.add(temp);
				}
			}
			for(TempPurchaseDetail temp: readOnlyDetails){
				for(Purchase pur: purchaseList){
					createPurchaseDetails(pur, temp.getDetail(), temp.isReadOnly());
				}
			}
			// end process 
			HibernateUtil.commitTransaction(sessionName);
			setModel(new SerializableListDataModel(purchaseList));
		} catch (Exception e) {
			String msg = "Error al crear los pedidos de compra. ";
			AonUtil.addErrorMessage(msg  + e.getMessage());
			try {
				HibernateUtil.rollbackTransaction(sessionName);
			} catch (DAOException daoe) {
				msg = "Unable to rollback transaction!";
				LOGGER.error(msg, e);
			}
			throw new AbortProcessingException("No se han podido generar las compras", e);
		} finally {
			HibernateUtil.closeSession(sessionName);
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);
		}

		afterPurchasesCreate(purchaseList);
	}
	
	private void beforePurchasesCreate() {
		for(TempPurchaseDetail temp: getTempPurchaseDetail()){
			if(!temp.isReadOnly() && (temp.getSupplier()==null || temp.getSupplier().getId()==null)){
				AonUtil.addErrorMessage("El proveedor es obligatorio.");
				throw new AbortProcessingException("El proveedor es obligatorio.");
			}
		}
		Collections.sort(getTempPurchaseDetail(), new TempPurchaseDetailComparator());
	}
	
	private void afterPurchasesCreate(List<Purchase> purchaseList) {
		nevv = false;
		try {
			IManagerBean bean = BeanManager.getManagerBean(Sales.class);
			sales.setPurchaseGenerated(true);
			StringBuffer buf = new StringBuffer();
			if(StringUtils.isNotBlank(sales.getRemarks())){
				buf.append(sales.getRemarks());
				buf.append('\n');
			}
			for(Purchase purchase: purchaseList){
	    		buf.append(AonUtil.getMessage(SALES_TO_PURCHASE) + " ");
	    		buf.append(purchase.getReferenceCode());
	    		buf.append('\n');
	    	}
	    	sales.setRemarks(buf.toString());
			bean.restoreNullSubPOJOs(sales);
			sales = (Sales) bean.update(sales);
			// initialize lookups
			if(sales.getSeller()==null){
				sales.setSeller((Seller) BeanManager.getManagerBean(Seller.class).createNewTo());
			}
		} catch (ManagerBeanException e) {
			String msg = "No se ha podido actualizar el estado de la compra.";
			LOGGER.error(msg);
		}
	}

	private Purchase createPurchase(Sales sales, Supplier supplier) throws ManagerBeanException {
			
		Purchase purchase = new Purchase();
		
		purchase.setRegistryAddress(supplier.getRegistry().getDefaultAddress());
		purchase.setScope(sales.getScope());
		purchase.setProject(sales.getProject());
		purchase.setSupplier(supplier);
		purchase.setWorkPlace(sales.getWorkPlace());
		purchase.setSeries(sales.getSeries());
		purchase.setNumber(obtainSeriesMaxNumber(sales.getSeries()));
		purchase.setPurchaseReference(sales.getPurchaseReference());
		purchase.setDiscountExpression(sales.getDiscountExpression());
		purchase.setIssueDate(new Date());
		purchase.setSecurityLevel(sales.getSecurityLevel());
		purchase.setStatus(PurchaseStatus.PENDING);
		purchase.setComments(sales.getComments());
		if(StringUtils.isNotBlank(sales.getPurchaseReference())){
			String message = AonUtil.getMessage(SALES_TO_PURCHASE);
			purchase.setRemarks(message + ": " +  sales.getPurchaseReference());
		}
		if(sales.getDocumentType()==DocumentType.NORMAL){
			purchase.setDocumentType(PurchaseDocumentType.NORMAL);
		} else if(sales.getDocumentType()==DocumentType.ITEM_RETURN){
			purchase.setDocumentType(PurchaseDocumentType.ITEM_RETURN);
		}
		purchase.setEmailCommunication(false);
		purchase.setConfidential(sales.isConfidential());
		
		purchase.setPayMethod(null);
		purchase.setBankAccount(null);
		purchase.setBankAlias(null);
		purchase.setBic(null);
		purchase.setNumberOfPayments(1);
		purchase.setDaysToFirstPayment(0);
		purchase.setDaysBetweenPayments(0);
		purchase.setPaymentDays("");

		if(isCustomerShippingAddress()){
			if(isShippingDataDefined(sales)){
				purchase.setCarrier(sales.getCarrier());
				purchase.setShippingAlternativeAddress(sales.getShippingAlternativeAddress());
				purchase.setShippingAlternativeAddress2(sales.getShippingAlternativeAddress2());
				purchase.setShippingAlternativeZip(sales.getShippingAlternativeZip());
				purchase.setShippingAlternativeCity(sales.getShippingAlternativeCity());
				purchase.setShippingAlternativePhone(sales.getShippingAlternativePhone());
				purchase.setShippingAlternativeRecipient(sales.getShippingAlternativeRecipient());
				purchase.setShippingContact(sales.getShippingContact());
				purchase.setShippingPeriod(sales.getShippingPeriod());
			} else {
				RegistryAddress ra = sales.getShippingAddress();
				purchase.setCarrier(null);
				
				StringBuffer buf = new StringBuffer();
		    	buf.append((ra.getStreetType()!=null) ? ra.getStreetType().getName(AonUtil.getCurrentLocale()) : "");
		    	buf.append((ra.getStreetType()!=null) ? " " : "");
		    	buf.append(StringUtils.isEmpty(ra.getAddress())? "":ra.getAddress());
		    	buf.append(StringUtils.isEmpty(ra.getNumber())?"":" ");
		    	buf.append(StringUtils.isEmpty(ra.getNumber())?"":ra.getNumber());
		    	purchase.setShippingAlternativeAddress(buf.toString());

		    	buf = new StringBuffer();
		    	buf.append(StringUtils.isEmpty(ra.getAddress2())?"":ra.getAddress2());
		    	buf.append(StringUtils.isEmpty(ra.getAddress3())?"":" (");
		    	buf.append(StringUtils.isEmpty(ra.getAddress3())?"":ra.getAddress3());
		    	buf.append(StringUtils.isEmpty(ra.getAddress3())?"":")");
		    	purchase.setShippingAlternativeAddress2(buf.toString());
				
				purchase.setShippingAlternativeZip(ra.getZip());
				purchase.setShippingAlternativeCity(ra.getLocation());
				String phone = sales.getCustomer().getRegistry().getPhone()==null?"":sales.getCustomer().getRegistry().getPhone().getValue();
				String cellular = sales.getCustomer().getRegistry().getCellular()==null?"":sales.getCustomer().getRegistry().getCellular().getValue();
				purchase.setShippingAlternativePhone((StringUtils.isEmpty(phone)?"":phone+" ") + (StringUtils.isEmpty(cellular)?"":cellular));
				purchase.setShippingAlternativeRecipient(sales.getCustomer().getRegistry().getFullName());
				purchase.setShippingContact(null);
				purchase.setShippingPeriod(null);
			}
		}
		
		IManagerBean purchaseBean = BeanManager.getManagerBean(Purchase.class);
		return (Purchase) purchaseBean.insert(purchase);
	}
	
	private void createPurchaseDetails(Purchase purchase, SalesDetail salesDetail, boolean readOnly) throws ManagerBeanException {
		IManagerBean purchaseDetailBean = BeanManager.getManagerBean(PurchaseDetail.class);
		PurchaseDetail detail = new PurchaseDetail();
		detail.setItem(salesDetail.getItem());
		detail.setPurchase(purchase);
		detail.setLine(salesDetail.getLine());
		detail.setDescription(salesDetail.getDescription());
		detail.setStatus(PurchaseDetailStatus.PENDING);
		detail.setSource(PurchaseSource.SALES);
		detail.setSourceId(salesDetail.getId());
		if(!readOnly){
			detail.setProject(null); 
			detail.setProposalDetail(null);
			detail.setQuantity(salesDetail.getQuantity());
			detail.setPrice(obtainItemPrice(purchase.getSupplier(), salesDetail.getItem()));
			detail.setDiscountExpression(salesDetail.getDiscountExpression());
			detail.setTaxes(salesDetail.getTaxes());
			detail.setDelivered(0);
		}
		purchaseDetailBean.insert(detail);
	}
	
	private double obtainItemPrice(Supplier supplier, Item item) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(RegistryItem.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_ITEM_REGISTRY_ID), supplier.getId());
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_ITEM_ITEM_ID), item.getId());
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_ITEM_TYPE), RegistryMode.SUPPLIER);
		criteria.addOrder(bean.getFieldName(IEntityAlias.REGISTRY_ITEM_PRIORITY), true);
		List<ITransferObject> list = bean.getList(criteria);
		if (!list.isEmpty()) {
			return ((RegistryItem)list.get(0)).getPrice();
		}
		return item.getPurchasePrice();
	}

	public boolean isShippingDataDefined(Sales sales) {
		if(sales!=null){
			if( StringUtils.isNotBlank(sales.getShippingContact())
					|| sales.getShippingPeriod()!=null
					|| isShippingAlternativeAddressDefined(sales) ){
				return true;
			}
		}
		return false;
	}
	
	public boolean isShippingAlternativeAddressDefined(Sales sales) {
		if(sales!=null){
			if( StringUtils.isNotBlank(sales.getShippingAlternativeAddress())
				|| StringUtils.isNotBlank(sales.getShippingAlternativeAddress2())
				|| StringUtils.isNotBlank(sales.getShippingAlternativeZip())
				|| StringUtils.isNotBlank(sales.getShippingAlternativeCity())
				|| StringUtils.isNotBlank(sales.getShippingAlternativePhone())
				|| StringUtils.isNotBlank(sales.getShippingAlternativeRecipient()) ){
				return true;
			}
		}
		return false;
	}
	
	private int obtainSeriesMaxNumber(String seriesId) throws ManagerBeanException {
		return SeriesNumberUtil.obtainNumber(seriesId, "Purchase", null);
	}
	
	public Integer calculateNextLine(Purchase purchase) throws ManagerBeanException {
		IManagerBean purchaseDetailBean = BeanManager.getManagerBean(PurchaseDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(purchaseDetailBean.getFieldName(IEntityAlias.PURCHASE_DETAIL_PURCHASE_ID), purchase.getId());
		Projection projection = Projection.max(purchaseDetailBean.getFieldName(IEntityAlias.PURCHASE_DETAIL_LINE));
		Object value = purchaseDetailBean.getUniqueResult(projection, criteria);
		return (value != null) ? ((Integer)value) + 1 : 1;
	}

	public static class TempPurchaseDetail implements Serializable  {
		
		private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
		
		private SalesDetail detail;
		private Supplier supplier;
		private boolean readOnly;
		public SalesDetail getDetail() {
			return detail;
		}
		public void setDetail(SalesDetail detail) {
			this.detail = detail;
		}
		public Supplier getSupplier() {
			return supplier;
		}
		public void setSupplier(Supplier supplier) {
			this.supplier = supplier;
		}
		public boolean isReadOnly() {
			return readOnly;
		}
		public void setReadOnly(boolean readOnly) {
			this.readOnly = readOnly;
		}
	}
	
	class TempPurchaseDetailComparator implements Comparator<TempPurchaseDetail> {

		@Override
		public int compare(TempPurchaseDetail o1, TempPurchaseDetail o2) {
			if( o1.getSupplier()==null || o1.getSupplier().getId()==null 
				|| o2.getSupplier()==null || o2.getSupplier().getId()==null){
				return -1;
			}
			return o1.getSupplier().getId().compareTo(o2.getSupplier().getId());
		}
		
	}
	
}
