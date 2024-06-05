package com.code.aon.ui.finance.event;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.Month;
import com.code.aon.company.WorkPlace;
import com.code.aon.config.Scope;
import com.code.aon.customer.Customer;
import com.code.aon.customer.enumeration.CustomerStatus;
import com.code.aon.finance.enumeration.BillingPeriod;
import com.code.aon.product.Item;
import com.code.aon.product.ProductCategory;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.seller.Seller;
import com.code.aon.ui.finance.controller.FeeExportGwtController;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.registry.controller.event.RegistrySearchListener;
import com.esferalia.aon.entity.IEntityAlias;

public class FeePrinterSearchListener extends RegistrySearchListener  { //ControllerSearchListener {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private Item item;
	private ProductCategory category;
	private Customer customer;
	private Seller seller;
	private Month billingDateMonth;
	private Integer billingDateYear;
	private Boolean anual;
	private WorkPlace workPlace;
	private BillingPeriod period;
	private CustomerStatus status;
	private Scope scope;
	
	FeeExportGwtController feeGwtExport;
	
	public Boolean getAnual() {
		return anual;
	}

	public void setAnual(Boolean anual) {
		this.anual = anual;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	
	public Seller getSeller() {
		return seller;
	}
	
	public void setSeller(Seller seller) {
		this.seller = seller;
	}
	
	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}
	
	public ProductCategory getCategory() {
		return category;
	}
	
	public void setCategory(ProductCategory category) {
		this.category = category;
	}

	public Month getBillingDateMonth() {
		return billingDateMonth;
	}

	public void setBillingDateMonth(Month billingDateMonth) {
		this.billingDateMonth = billingDateMonth;
	}

	public Integer getBillingDateYear() {
		return billingDateYear;
	}

	public void setBillingDateYear(Integer billingDateYear) {
		this.billingDateYear = billingDateYear;
	}
	
	public WorkPlace getWorkPlace() {
		return workPlace;
	}
	
	public void setWorkPlace(WorkPlace workPlace) {
		this.workPlace = workPlace;
	}
	
	public BillingPeriod getPeriod() {
		return period;
	}
	
	public void setPeriod(BillingPeriod period) {
		this.period = period;
	}
	
	public CustomerStatus getStatus() {
		return status;
	}
	
	public void setStatus(CustomerStatus status) {
		this.status = status;
	}
	
	public Scope getScope() {
		return scope;
	}
	
	public void setScope(Scope scope) {
		this.scope = scope;
	}
	
	public FeeExportGwtController getFeeGwtExport() {
		return feeGwtExport;
	}
	
	public void setFeeGwtExport(FeeExportGwtController feeGwtExport) {
		this.feeGwtExport = feeGwtExport;
	}
	
	@Override
	protected void init() throws ManagerBeanException {
		setItem((Item)BeanManager.getManagerBean(Item.class).createNewTo());
		setCustomer((Customer)BeanManager.getManagerBean(Customer.class).createNewTo());
		setSeller((Seller)BeanManager.getManagerBean(Seller.class).createNewTo());
		setWorkPlace((WorkPlace)BeanManager.getManagerBean(WorkPlace.class).createNewTo());
		setCategory((ProductCategory)BeanManager.getManagerBean(ProductCategory.class).createNewTo());
		setPeriod(null);
		setStatus(null);
		setScope((Scope)BeanManager.getManagerBean(Scope.class).createNewTo());
		setFeeGwtExport(new FeeExportGwtController());
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(new Date());
		setBillingDateMonth(Month.getMonthByValue(calendar.get(Calendar.MONTH)));
		setBillingDateYear(calendar.get(Calendar.YEAR));
		super.init();
	}
	
	@Override
	protected void completeCriteria( Criteria criteria ) throws ManagerBeanException, ExpressionException {
		if ( (getItem() != null) && (getItem().getId() != null) ) {
			String field = getController().getFieldName(IEntityAlias.CUSTOMER_FEE_ITEM_ID);
			criteria.addEqualExpression(field, getItem().getId());
		}
		
		if ( (getCategory() != null) && (getCategory().getId() != null) ) {
			String field = "CustomerFee.item.product.category.id";
			criteria.addEqualExpression(field, getCategory().getId());
		}
		
		if ( (getCustomer() != null) && (getCustomer().getId() != null) ) {
			String field = getController().getFieldName(IEntityAlias.CUSTOMER_FEE_CUSTOMER_ID);
			criteria.addEqualExpression(field, getCustomer().getId());
		}
		
		if ( (getSeller() != null) && (getSeller().getId() != null) ) {
			String field = getController().getFieldName(IEntityAlias.CUSTOMER_FEE_SELLER_ID);
			criteria.addEqualExpression(field, getSeller().getId());
		}
		
		if ( (getWorkPlace() != null) && (getWorkPlace().getId() != null) ) {
			String field = getController().getFieldName(IEntityAlias.CUSTOMER_FEE_WORK_PLACE_ID);
			criteria.addEqualExpression(field, getWorkPlace().getId());
		}
		
		if (getBillingDateMonth() != null) {
			criteria.addBetweenExpression(getFieldName(IEntityAlias.CUSTOMER_FEE_BILLING_DATE), obtainFromDate(), obtainToDate());
			criteria.addLessThanOrEqualExpression(getFieldName(IEntityAlias.CUSTOMER_FEE_INITIAL_DATE), obtainToDate());
			Expression finalExp1 = ExpressionUtilities.getGreaterThanOrEqualExpression(getFieldName(IEntityAlias.CUSTOMER_FEE_FINAL_DATE), obtainFromDate());
			Expression finalExp2 = ExpressionUtilities.getNullExpression(getFieldName(IEntityAlias.CUSTOMER_FEE_FINAL_DATE));
			criteria.addExpression(ExpressionUtilities.getOrExpression(finalExp1, finalExp2));
		}		
		
		if(getPeriod() != null) {
			String field = getController().getFieldName(IEntityAlias.CUSTOMER_FEE_PERIOD);
			criteria.addEqualExpression(field, getPeriod());
		}
		
		if(getStatus() != null) {
			String field = getController().getFieldName(IEntityAlias.CUSTOMER_FEE_CUSTOMER_STATUS);
			criteria.addEqualExpression(field, getStatus());
		}
		
		if(getScope() != null && getScope().getId() != null) {
			String field = getController().getFieldName(IEntityAlias.CUSTOMER_FEE_CUSTOMER_SCOPE_ID);
			criteria.addEqualExpression(field, getScope().getId());
		}

		String segment = "CustomerFee.customer.registry.segments.segment.id";
		addEnumToCriteria(criteria, segment, getSegmentsIds().toArray());

		getFeeGwtExport().setItem((getItem() != null) && (getItem().getId() != null) 
				? getItem().getId() : null);
		getFeeGwtExport().setCategory((getCategory() != null) && (getCategory().getId() != null)
				? getCategory().getId() : null);
		getFeeGwtExport().setCustomer((getCustomer() != null) && (getCustomer().getId() != null)
				? getCustomer().getId() : null);
		getFeeGwtExport().setSeller((getSeller() != null) && (getSeller().getId() != null)
				? getSeller().getId() : null);
		getFeeGwtExport().setWorkplace((getWorkPlace() != null) && (getWorkPlace().getId() != null)
				? getWorkPlace().getId() : null);
		getFeeGwtExport().setFrom(getBillingDateMonth() != null ? obtainFromDate() : null);
		getFeeGwtExport().setTo(getBillingDateMonth() != null ? obtainToDate() : null);
		getFeeGwtExport().setPeriod(getPeriod() != null ? getPeriod().ordinal() : null);
		getFeeGwtExport().setScope(getScope() != null ? getScope().getId() : null);
		getFeeGwtExport().setStatus(getStatus() != null ? getStatus().ordinal() : null);

		getFeeGwtExport().setSegment(getSegmentsIds());

	}	
	
	@Override
	public void beforeModelSearched(ControllerEvent event) throws ControllerListenerException {
		checkGwtExport();
		super.beforeModelSearched(event);
	}
	
	public String getFilter() {
		return getFeeGwtExport().getFilter();
	}

	private Date obtainFromDate() {
		Calendar calendar = new GregorianCalendar();
		calendar.set(getBillingDateYear(), getBillingDateMonth().getValue(), 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		return calendar.getTime();
	}

	private Date obtainToDate() {
		if(!anual){
			Calendar calendar = new GregorianCalendar();
			calendar.setTime(obtainFromDate());
			calendar.add(Calendar.MONTH, 1);
			calendar.add(Calendar.DATE, -1);
			calendar.set(Calendar.HOUR_OF_DAY, 23);
			calendar.set(Calendar.MINUTE, 59);
			calendar.set(Calendar.SECOND, 59);
			return calendar.getTime();
		}else {
			Calendar calendar = new GregorianCalendar();
			calendar.setTime(obtainFromDate());
			calendar.add(Calendar.YEAR, 1);
			calendar.add(Calendar.DATE, -1);
			calendar.set(Calendar.HOUR_OF_DAY, 23);
			calendar.set(Calendar.MINUTE, 59);
			calendar.set(Calendar.SECOND, 59);
			return calendar.getTime();
			
		}
	}

	private void checkGwtExport() {
		if ( getFeeGwtExport() == null ) {
			setFeeGwtExport(new FeeExportGwtController());
		}
	}
}