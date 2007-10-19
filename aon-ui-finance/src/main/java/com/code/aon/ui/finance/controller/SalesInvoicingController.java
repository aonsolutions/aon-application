package com.code.aon.ui.finance.controller;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import com.code.aon.account.Account;
import com.code.aon.account.AccountEntry;
import com.code.aon.account.DefaultAccounts;
import com.code.aon.account.bridge.util.AccountUtil;
import com.code.aon.account.bridge.writer.AccountEntryInvoiceWriter;
import com.code.aon.account.dao.IAccountAlias;
import com.code.aon.account.enumeration.AccountEntryType;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.WorkPlace;
import com.code.aon.config.ApplicationParameter;
import com.code.aon.config.Scope;
import com.code.aon.config.dao.IConfigAlias;
import com.code.aon.customer.Customer;
import com.code.aon.customer.dao.ICustomerAlias;
import com.code.aon.finance.Finance;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.InvoiceSource;
import com.code.aon.finance.enumeration.InvoiceStatus;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.finance.invoicing.FinanceGenerator;
import com.code.aon.finance.invoicing.InvoicePriceStrategy;
import com.code.aon.product.enumeration.TaxType;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.product.strategy.TaxBreakDown;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.tasDelivery.TasDelivery;
import com.code.aon.tasDelivery.dao.ITasDeliveryAlias;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.menu.jsf.MenuEvent;
import com.code.aon.ui.menu.jsf.MenuManager;
import com.code.aon.ui.report.OutputFormat;
import com.code.aon.ui.report.controller.ReportManager;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.warehouse.controller.DeliveryController;
import com.code.aon.warehouse.Delivery;
import com.code.aon.warehouse.DeliveryDetail;
import com.code.aon.warehouse.dao.IWarehouseAlias;
import com.code.aon.warehouse.enumeration.DeliveryStatus;

/**
 * Invoicing sales Controller.
 * 
 * @author Consulting & Development.
 * @since 1.0
 */
public class SalesInvoicingController extends BasicController {
	
	/** The class logger. */
	private static final Logger LOGGER = Logger.getLogger(SalesInvoicingController.class.getName());
	
	/** Delivery controllers name. */
	private static final String DELIVERY_CONTROLLER_NAME = "delivery";

	/** SalesInvoicingDetail controllers name. */
	private static final String SALES_INVOICING_DETAIL_CONTROLLER_NAME = "salesInvoicingDetail";
	
	/** SalesFinance controllers name. */
	private static final String SALES_FINANCE_CONTROLLER_NAME = "salesFinance";
	
	/** The price strategy. */
	private IPriceStrategy priceStrategy;
	
	/** The finance generator. */
	private FinanceGenerator financeGenerator;
	
	/** List of Addresses. */
	private List<SelectItem> addresses;
	
	/** Warehouse ident. */
	private Integer warehouseId;
	
	/** The ident of the point of sale. */
	private Integer posId;
	
	private AccountEntryInvoiceWriter accountEntryInvoiceWriter;

	/**
	 * Returns ident of the point of sale.
	 * 
	 * @return pos ident
	 */
	public Integer getPosId() {
		return posId;
	}

	/**
	 * Assigns the ident of the point of sale.
	 * 
	 * @param posId ident of the point of sale
	 */
	public void setPosId(Integer posId) {
		this.posId = posId;
	}

	/**
	 * Returns warehouse ident.
	 * 
	 * @return warehouse ident
	 */
	public Integer getWarehouseId() {
		return warehouseId;
	}

	/**
	 * Assigns warehouse ident.
	 * 
	 * @param warehouseId warehouse ident
	 */
	public void setWarehouseId(Integer warehouseId) {
		this.warehouseId = warehouseId;
	}

	/**
	 * Resets the controller.
	 * 
	 * @param event menu event
	 */
	@SuppressWarnings("unused")
	public void onReset(MenuEvent event){
		super.onReset(null);
	}
	
	/**
	 * Checks if is editable.
	 * 
	 * @return true, if is editable
	 */
	public boolean isEditable() {
		if(this.getTo() != null){
			if(((Invoice)this.getTo()).getStatus() == null){
				return true;
			}
			return ((Invoice)this.getTo()).getStatus().equals(InvoiceStatus.PENDING);
		}
		return false;
	}
	
	public boolean isRecorded(){
		Invoice invoice = (Invoice)this.getTo();
		if(invoice.getStatus() != null){
			return invoice.getStatus().equals(InvoiceStatus.SCORED);
		}
		return false;
	}
	
	/**
	 * Search a customer with this ident, and assigns to invoice.
	 * 
	 * @param event the event thet contains the new value
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 */
	public void customerData(ValueChangeEvent event) throws ManagerBeanException{
		if(event.getNewValue() != null){
			IManagerBean customerBean = BeanManager.getManagerBean(Customer.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(customerBean.getFieldName(ICustomerAlias.CUSTOMER_ID),event.getNewValue());
			Iterator iter = customerBean.getList(criteria).iterator();
			if(iter.hasNext()){
				Customer customer = (Customer)iter.next();
				Registry registry = new Registry();
				registry.setAlias(customer.getRegistry().getAlias());
				registry.setDocument(customer.getRegistry().getDocument());
				registry.setName(customer.getRegistry().getName());
				registry.setSurname(customer.getRegistry().getSurname());
				((Invoice)this.getTo()).setRegistry(registry);
			}
			loadAddresses((Integer)event.getNewValue());
		}
	}
	
	/**
	 * Recover the addresses for this Registry ident.
	 * 
	 * @param customerId the ident of a customer
	 */
	public void loadAddresses(Integer customerId) {
		addresses  = new LinkedList<SelectItem>();
		try {
			IManagerBean rAddressBean = BeanManager.getManagerBean(RegistryAddress.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(rAddressBean.getFieldName(IRegistryAlias.REGISTRY_ADDRESS_REGISTRY_ID), customerId);
			Iterator iter = rAddressBean.getList(criteria).iterator();
			while(iter.hasNext()){
				RegistryAddress address = (RegistryAddress)iter.next();
				SelectItem item = new SelectItem(address.getId(), address.getAddress()+ " " + address.getAddress2() + " " + address.getCity());
				addresses.add(item);
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "error loading addresses for customer with id= " + customerId, e);
		}
	}

	/**
	 * Invoices selected Deliveries, inserting invoicedetails and closing deliveries.
	 * 
	 * @param event action event
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 */
	@SuppressWarnings("unused")
	public void onInvoice(ActionEvent event) throws ManagerBeanException{
		DeliveryController deliveryController = (DeliveryController)AonUtil.getController(DELIVERY_CONTROLLER_NAME);
		Iterator iter = ((List)deliveryController.getModel().getWrappedData()).iterator();
		while(iter.hasNext()){
			Delivery delivery = (Delivery)iter.next();
			if(deliveryController.isChecked(delivery)){
				IManagerBean deliveryBean = BeanManager.getManagerBean(Delivery.class);
				insertInvoiceDetails((Invoice)this.getTo(), delivery);
				delivery.setStatus(DeliveryStatus.CLOSED);
				deliveryBean.update(delivery);
			}
		}
		generateFinances(null);
		deliveryController.clearCheckList();
		SalesInvoicingDetailController salesInvoicingDetailController = (SalesInvoicingDetailController) AonUtil.getController(SALES_INVOICING_DETAIL_CONTROLLER_NAME);
		salesInvoicingDetailController.onSearch(null);
	}
	
	@SuppressWarnings("unused")
	public void onRecordInvoice(ActionEvent event) throws ManagerBeanException, ExpressionException{
		Invoice invoice = (Invoice)this.getTo();
		recordInvoice(invoice);
	}
	
	@SuppressWarnings("unused")
	public void onUnrecordInvoice(ActionEvent event) throws ManagerBeanException, ExpressionException{
		Invoice invoice = (Invoice)this.getTo();
		getAccountEntryInvoiceWriter().unrecordInvoice(invoice);
		IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
		invoice.setStatus(InvoiceStatus.PENDING);
		if(invoice.getRegistryAddress() != null && invoice.getRegistryAddress().getId() == null){
			invoice.setRegistryAddress(null);
		}
		invoiceBean.update(invoice);
	}
	
	private void recordInvoice(Invoice invoice) throws ManagerBeanException, ExpressionException {
		AccountEntry entry = new AccountEntry();
		entry.setAccountPeriod(AccountUtil.obtainPeriod(invoice.getIssueDate()).getId());
		entry.setEntryDate(invoice.getIssueDate());
		entry.setJournal(null);
		entry.setType((invoice.getType().equals(InvoiceType.SALES)?AccountEntryType.SALES_INVOICE:AccountEntryType.PURCHASE_INVOICE));
		entry = getAccountEntryInvoiceWriter().insertorUpdateAccountEntry(entry, true);
		List taxBreakDown = getPriceStrategy().getTaxBreakDowns(invoice, invoice);
		getAccountEntryInvoiceWriter().insertEntryDetails(entry, AccountUtil.obtainCustomerAccount(invoice.getRegistry()), obtainBalancingAccount(invoice), invoice.getSeries(), invoice.getNumber(), getPriceStrategy().getTotalPrice(invoice, invoice), getRetentionTotal(taxBreakDown), getTaxQuota(taxBreakDown), getPriceStrategy().getTaxableBase(invoice));
		getAccountEntryInvoiceWriter().insertAccountEntryInvoice(entry, invoice);
		IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
		invoice.setStatus(InvoiceStatus.SCORED);
		if(invoice.getRegistryAddress() != null && invoice.getRegistryAddress().getId() == null){
			invoice.setRegistryAddress(null);
		}
		invoiceBean.update(invoice);
	}
	
	public AccountEntryInvoiceWriter getAccountEntryInvoiceWriter() {
		if(accountEntryInvoiceWriter == null){
			accountEntryInvoiceWriter = new AccountEntryInvoiceWriter();
		}
		return accountEntryInvoiceWriter;
	}
	
	private Account obtainBalancingAccount(Invoice invoice) throws ManagerBeanException {
		String paramName = (invoice.getType().equals(InvoiceType.SALES)?DefaultAccounts.SALES_ACCOUNT:DefaultAccounts.PURCHASE_ACCOUNT);
		IManagerBean appParamsBean = BeanManager.getManagerBean(ApplicationParameter.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(appParamsBean.getFieldName(IConfigAlias.APPLICATION_PARAMETER_NAME), paramName);
		Iterator iter = appParamsBean.getList(criteria).iterator();
		if(iter.hasNext()){
			ApplicationParameter param = (ApplicationParameter)iter.next();
			IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
			Criteria accountCriteria = new Criteria();
			accountCriteria.addEqualExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ID), param.getValue());
			Iterator accountIter = accountBean.getList(accountCriteria).iterator();
			if(accountIter.hasNext()){
				return (Account)accountIter.next();
			}
		}
		return null;
	}
	
	private double getRetentionTotal(List taxBreakDownList) {
		Iterator iter = taxBreakDownList.iterator();
		double retentionQuota = 0;
		while(iter.hasNext()){
			TaxBreakDown taxBreakDown = (TaxBreakDown)iter.next();
			if(taxBreakDown.getTaxType().equals(TaxType.RETENTION)){
				retentionQuota = retentionQuota + taxBreakDown.getTaxQuota();
			}
		}
		return retentionQuota;
	}
	
	private double getTaxQuota(List taxBreakDownList) {
		Iterator iter = taxBreakDownList.iterator();
		double taxQuota = 0;
		while(iter.hasNext()){
			TaxBreakDown taxBreakDown = (TaxBreakDown)iter.next();
			if(taxBreakDown.getTaxType().equals(TaxType.VAT)){
				taxQuota = taxQuota + taxBreakDown.getTaxQuota() + taxBreakDown.getSurchargeQuota();
			}
		}
		return taxQuota;
	}
	
	/**
	 * Recovers DeliveryControllers Delivery.
	 * Creates a new invoice, fills invoice data.
	 * And calls onInvoice to invoices it.
	 * 
	 * @param event action event
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 */
	@SuppressWarnings("unused")
	public void onImportDelivery(ActionEvent event) throws ManagerBeanException{
		this.onReset(null);
		DeliveryController deliveryController = (DeliveryController)AonUtil.getController(DELIVERY_CONTROLLER_NAME);
		Delivery delivery = (Delivery)deliveryController.getTo();
		this.setPosId(deliveryController.getPosId());
		this.setWarehouseId(deliveryController.getWarehouseId());
		((Invoice)this.getTo()).setIssueDate(delivery.getIssueTime());
		((Invoice)this.getTo()).setSeries(delivery.getSeries());
		((Invoice)this.getTo()).setNumber(0);
		((Invoice)this.getTo()).setRegistry(delivery.getCustomer().getRegistry());
		((Invoice)this.getTo()).setRegistryName(delivery.getCustomer().getRegistry().getName() + " " + delivery.getCustomer().getRegistry().getSurname());
		((Invoice)this.getTo()).setRegistryDocument(delivery.getCustomer().getRegistry().getDocument());
		((Invoice)this.getTo()).setSecurityLevel(delivery.getSecurityLevel());
		((Invoice)this.getTo()).setStatus(InvoiceStatus.PENDING);
		((Invoice)this.getTo()).setType(InvoiceType.SALES);
		((Invoice)this.getTo()).setRegistryAddress(obtainRegistryAddress(delivery.getCustomer().getRegistry()));
		this.accept(null);
		if(((Invoice)this.getTo()).getRegistryAddress() == null){
			((Invoice)this.getTo()).setRegistryAddress(new RegistryAddress());
		}
		Iterator iter = ((List)deliveryController.getModel().getWrappedData()).iterator();
		while(iter.hasNext()){
			Delivery del = (Delivery)iter.next();
			if(delivery.getId().equals(del.getId())){
				deliveryController.addToCheckList(del);
				break;
			}
		}
		this.onInvoice(null);
		IController salesFinanceController = AonUtil.getController(SALES_FINANCE_CONTROLLER_NAME);
		salesFinanceController.onSearch(null);
		updateBreadCrumb();
	}
	
	/**
	 * Obtain Registry's first recovered address.
	 * 
	 * @param registry the registry
	 * 
	 * @return the first recovered address
	 */
	private RegistryAddress obtainRegistryAddress(Registry registry) {
		try {
			IManagerBean registryAddressBean = BeanManager.getManagerBean(RegistryAddress.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(registryAddressBean.getFieldName(IRegistryAlias.REGISTRY_ADDRESS_REGISTRY_ID), registry.getId());
			Iterator iter = registryAddressBean.getList(criteria).iterator();
			if(iter.hasNext()){
				return (RegistryAddress)iter.next();
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining addres for customer with id= " + registry.getId(), e);
		}
		return null;
	}

	/**
	 * Updates bread crumb.
	 */
	private void updateBreadCrumb() {
		FacesContext ctx = FacesContext.getCurrentInstance();
		ValueBinding vb = ctx.getApplication().createValueBinding("#{menuManager}");
		MenuManager menuManager = (MenuManager)vb.getValue(ctx);
		menuManager.setCurrentMenu("AON_APP");
		menuManager.getCurrentMenuModel().setSelectedNode("aon_app.sales.salesInvoicing");
	}
	
	/**
	 * Recovers the Delivery, removes invoicedetails related to this.
	 * Sets delivey staus to pending.
	 * Refresh data.
	 * 
	 * @param event action event
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 */
	@SuppressWarnings("unused")
	public void onRemoveDelivery(ActionEvent event) throws ManagerBeanException{
		DeliveryController deliveryController = (DeliveryController)AonUtil.getController(DELIVERY_CONTROLLER_NAME);
		Delivery delivery = (Delivery)deliveryController.getModel().getRowData();
		removeInvoiceDetails(delivery.getId());
		IManagerBean deliveryBean = BeanManager.getManagerBean(Delivery.class);
		delivery.setStatus(DeliveryStatus.PENDING);
		deliveryBean.update(delivery);
		generateFinances(null);
		SalesInvoicingDetailController salesInvoicingDetailController = (SalesInvoicingDetailController) AonUtil.getController(SALES_INVOICING_DETAIL_CONTROLLER_NAME);
		salesInvoicingDetailController.onSearch(null);
		deliveryController.clearCheckList();
	}

	/**
	 * Creates the InvoiceDetais of this Invoice instead of the DeliveryDetails of this Delivery.
	 * 
	 * @param delivery related delivery
	 * @param invoice related invoice
	 */
	private void insertInvoiceDetails(Invoice invoice, Delivery delivery) {
		try {
			IManagerBean deliveryDetailBean = BeanManager.getManagerBean(DeliveryDetail.class);
			IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(deliveryDetailBean.getFieldName(IWarehouseAlias.DELIVERY_DETAIL_DELIVERY_ID), delivery.getId());
			Iterator iter = deliveryDetailBean.getList(criteria).iterator();
			while(iter.hasNext()){
				DeliveryDetail deliveryDetail = (DeliveryDetail)iter.next();
				InvoiceDetail invoiceDetail = new InvoiceDetail();
				invoiceDetail.setDeliveryDetail(deliveryDetail.getId());
				invoiceDetail.setDescription(deliveryDetail.getDescription());
				invoiceDetail.setDiscountExpression(deliveryDetail.getDiscountExpression());
				invoiceDetail.setInvoice(invoice);
				invoiceDetail.setItem(deliveryDetail.getItem());
				invoiceDetail.setPrice(deliveryDetail.getPrice());
				invoiceDetail.setQuantity(deliveryDetail.getQuantity());
				invoiceDetail.setSource(InvoiceSource.DELIVERY);
				invoiceDetail.setTaxableBase(obtainTaxableBase(invoiceDetail));
				invoiceDetail.setWorkPlace(obtainWorkPlace());
				invoiceDetailBean.insert(invoiceDetail);
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "error inserting invoiceDetails for invoice with id= " + invoice.getId(), e);
		}
	}

	/**
	 * Returns workplace.
	 * 
	 * @return workplace
	 */
	private WorkPlace obtainWorkPlace() {
		try {
			IManagerBean wpBean = BeanManager.getManagerBean(WorkPlace.class);
			List<ITransferObject> wpLst = wpBean.getList(null);
			if (wpLst.size() > 0) {
				WorkPlace wp = (WorkPlace)wpLst.get(0);
				return wp;
			}
		}
		catch (ManagerBeanException mbe) {
			mbe.printStackTrace();
		}
		return null;
	}

	/**
	 * Returns taxable base of this invoice detail.
	 * 
	 * @param invoiceDetail related invoice detail
	 * 
	 * @return taxable base
	 */
	private double obtainTaxableBase(InvoiceDetail invoiceDetail) {
		return getPriceStrategy().getBasePrice(invoiceDetail);
	}
	
	/**
	 * Returns total price of the controllers transfer object.
	 * 
	 * @return transfer object total price
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 */
	public double getToInvoiceTotalPrice() throws ManagerBeanException{
		Invoice invoice = (Invoice)this.getTo();
		return getPriceStrategy().getTotalPrice(invoice,invoice);
	}
	
	/**
	 * Returns total price of the controllers rows data.
	 * 
	 * @return rows data total price
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 */
	public double getInvoiceTotalPrice() throws ManagerBeanException{
		Invoice invoice = (Invoice)this.getModel().getRowData();
		return getPriceStrategy().getTotalPrice(invoice,invoice);
	}
	
	/**
	 * Returns price strategy.
	 * 
	 * @return price calculator
	 */
	public IPriceStrategy getPriceStrategy(){
		if(priceStrategy == null){
			priceStrategy = new InvoicePriceStrategy();
		}
		return priceStrategy;
	}
	
	/**
	 * Gets the finance generator.
	 * 
	 * @return the finance generator
	 */
	public FinanceGenerator getFinanceGenerator() {
		if(financeGenerator == null){
			financeGenerator = new FinanceGenerator();
		}
		return financeGenerator;
	}
	
	@SuppressWarnings("unused")
	public void generateFinances(ActionEvent event) throws ManagerBeanException{
		Invoice invoice = (Invoice)this.getTo();
		try {
			IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
			IController salesFinanceController = AonUtil.getController(SALES_FINANCE_CONTROLLER_NAME);
			Iterator iter = ((List)salesFinanceController.getModel().getWrappedData()).iterator();
			while(iter.hasNext()){
				Finance finance = (Finance)iter.next();
				financeBean.remove(finance);
			}
			getFinanceGenerator().generateFinances(invoice, getPriceStrategy().getTotalPrice(invoice, invoice));
			salesFinanceController.onSearch(null);
		} catch (ManagerBeanException e) {
			throw new ManagerBeanException("Error generating finances for invoice with id= " + invoice.getId(),e);
		}
	}
	
	/**
	 * Deletes all invoiceDetails related to this delivery.
	 * 
	 * @param deliveryId the ident of delivery
	 */
	public void removeInvoiceDetails(Integer deliveryId) {
		try {
			IManagerBean deliveryDetailBean = BeanManager.getManagerBean(DeliveryDetail.class);
			IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(deliveryDetailBean.getFieldName(IWarehouseAlias.DELIVERY_DETAIL_DELIVERY_ID), deliveryId);
			Iterator iter = deliveryDetailBean.getList(criteria).iterator();
			while(iter.hasNext()){
				DeliveryDetail deliveryDetail = (DeliveryDetail)iter.next();
				criteria = new Criteria();
				criteria.addEqualExpression(invoiceDetailBean.getFieldName(IFinanceAlias.INVOICE_DETAIL_DELIVERY_DETAIL), deliveryDetail.getId());
				Iterator iterator = invoiceDetailBean.getList(criteria).iterator();
				if(iterator.hasNext()){
					invoiceDetailBean.remove((ITransferObject)iterator.next());
				}
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error removing invoiceDetails linked with delivery with id= " + deliveryId, e);
		}
	}
	
	/**
	 * Returns invoice collection.
	 * 
	 * @return the collection
	 * 
	 * @see com.code.aon.ui.form.BasicController#getCollection()
	 */
	public Collection getCollection(){
		List<ITransferObject> l = new LinkedList<ITransferObject>();
		if(this.getTo() == null){
			try {
				l = getManagerBean().getList(getCriteria());
			} catch (ManagerBeanException e) {
				LOGGER.log(Level.SEVERE, "Error obtaining collection to print", e);
			}
		}else{
			l.add(obtainInvoice(((Invoice)this.getTo()).getId()));
		}
		return l;
	}
	
	/**
	 * Recovers the invoice instead of this invoice ident.
	 * 
	 * @param invoiceId invoice ident
	 * 
	 * @return the invoice
	 */
	private ITransferObject obtainInvoice(Integer invoiceId) {
		try {
			IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(invoiceBean.getFieldName(IFinanceAlias.INVOICE_ID), invoiceId);
			Iterator iter = invoiceBean.getList(criteria).iterator();
			if(iter.hasNext()){
				return (Invoice)iter.next();
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining invoice with id= " + invoiceId, e);
		}
		return null;
	}
	
	/**
	 * Adds greater than or equal date expression to the criteria.
	 * 
	 * @param event contains the date value
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 */
	public void addIssuedate1Expression(ValueChangeEvent event)
		throws ManagerBeanException {
	    if (event.getNewValue() != null) {
	    	Criteria c = getCriteria();
			Object value = event.getNewValue();
			c.addGreaterThanOrEqualExpression(getFieldName(IFinanceAlias.INVOICE_ISSUE_DATE), value);
			setCriteria(c);
		}
	}
	
	/**
	 * Adds less than or equal date expression to the criteria.
	 * 
	 * @param event contains the date value
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 */
	public void addIssuedate2Expression(ValueChangeEvent event)
		throws ManagerBeanException {
	    if (event.getNewValue() != null) {
	    	Criteria c = getCriteria();
			Object value = event.getNewValue();
			c.addLessThanOrEqualExpression(getFieldName(IFinanceAlias.INVOICE_ISSUE_DATE), value);
			setCriteria(c);
		}
	}

	/**
	 * Returns the tasdelivery instead of delivery ident.
	 * 
	 * @return tasdelivery
	 */
	public TasDelivery obtainTasDelivery(){
		try {
			IManagerBean tasDeliveryBean = BeanManager.getManagerBean(TasDelivery.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(tasDeliveryBean.getFieldName(ITasDeliveryAlias.TAS_DELIVERY_DELIVERY_ID), obtainDeliveryId());
			Iterator iter = tasDeliveryBean.getList(criteria).iterator();
			if(iter.hasNext()){
				return (TasDelivery)iter.next();
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining associated tasDelivery", e);
		}
		return null;
	}
	
	/**
	 * Returns the Delivery ident instead of this controllers invoice.
	 * 
	 * @return the delivery ident
	 */
	private Integer obtainDeliveryId() {
		Invoice invoice = (Invoice)this.getTo();
		try {
			IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(invoiceDetailBean.getFieldName(IFinanceAlias.INVOICE_DETAIL_INVOICE_ID), invoice.getId());
			Iterator iter = invoiceDetailBean.getList(criteria).iterator();
			if(iter.hasNext()){
				IManagerBean deliveryDetailBean = BeanManager.getManagerBean(DeliveryDetail.class);
				InvoiceDetail invoiceDetail = (InvoiceDetail)iter.next();
				criteria = new Criteria();
				criteria.addEqualExpression(deliveryDetailBean.getFieldName(IWarehouseAlias.DELIVERY_DETAIL_ID), invoiceDetail.getDeliveryDetail());
				iter = deliveryDetailBean.getList(criteria).iterator();
				if(iter.hasNext()){
					DeliveryDetail deliveryDetail = (DeliveryDetail)iter.next();
					return deliveryDetail.getDelivery().getId();
				}
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining associated deliveryId", e);
		}
		return null;
	}

	/**
	 * Returns addresses list.
	 * 
	 * @return addresses list
	 */
	public List<SelectItem> getAddresses() {
		return addresses;
	}
	
	/**
	 * Assigns addresses list.
	 * 
	 * @param addresses addresses list
	 */
	public void setAddresses(List<SelectItem> addresses) {
		this.addresses = addresses;
	}

	/**
	 * Returns addresses list number of objects.
	 * 
	 * @return total addresses
	 */
	public int getAddressCount() {
		if(addresses != null){
			return addresses.size();
		}
		return 0;
	}
	
	/**
	 * Adds registry to the criteria.
	 * 
	 * @param event contains criteria ident
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 */
	public void addCustomerExpression(ValueChangeEvent event) throws ManagerBeanException{
		if(event.getNewValue() != null && !((String)event.getNewValue()).trim().equals("")){
			try{
				Integer id = new Integer((String)event.getNewValue());
				Criteria criteria = getCriteria();
				criteria.addEqualExpression(getManagerBean().getFieldName(IFinanceAlias.INVOICE_REGISTRY_ID), id);
				setCriteria(criteria);
			} catch (Exception e) {
			}
		}
	}

    /**
     * Sets default parameters to report.
     * 
     * @param event that launched report
     */
	@SuppressWarnings("unused")
    public void onReport(ActionEvent event) {
        ReportManager manager = (ReportManager)AonUtil.getRegisteredBean("report");
        manager.setReportKey("salesInvoice");
        manager.setOutputFormat(OutputFormat.PDF);
    }
	
	/* Used by the report */
	public Customer getCustomer() throws ManagerBeanException{
		Invoice invoice = (Invoice)this.getTo();
		IManagerBean customerBean = BeanManager.getManagerBean(Customer.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(customerBean.getFieldName(ICustomerAlias.CUSTOMER_ID), invoice.getRegistry().getId());
		Iterator iter = customerBean.getList(criteria).iterator();
		if(iter.hasNext()){
			return (Customer)iter.next();
		}
		return null;
	}
}