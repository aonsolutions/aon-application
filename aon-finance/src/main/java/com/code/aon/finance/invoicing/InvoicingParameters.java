package com.code.aon.finance.invoicing;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.ArrayUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.Month;
import com.code.aon.company.WorkPlace;
import com.code.aon.config.Scope;
import com.code.aon.config.Series;
import com.code.aon.customer.Customer;
import com.code.aon.customer.InvoicingGroup;
import com.code.aon.product.Item;
import com.code.aon.product.ProductCategory;
import com.code.aon.registry.Segment;

public class InvoicingParameters implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private InvoicingGroup invoicingGroup;
	private Customer customer;
	private Item item;
	private ProductCategory category;
	private Month month;
	private int year;
	private Series series;
	private Integer fromNumber;
	private Integer toNumber;
	private Date fromDate;
	private Date toDate;
	private boolean confidential;
	private WorkPlace workPlace;
	private Scope scope;
	private List<Scope> scopes;
	private Segment segment;
	private Segment[] segments;


	private Series invoiceSeries;
	private int invoiceNumber;
	private Date invoiceDate;
	private boolean invoiceRecordable;
	private String invoiceComments;
	
	public InvoicingGroup getInvoicingGroup() {
		return invoicingGroup;
	}

	public void setInvoicingGroup(InvoicingGroup invoicingGroup) {
		this.invoicingGroup = invoicingGroup;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
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

	public Month getMonth() {
		return month;
	}

	public void setMonth(Month month) {
		this.month = month;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public Series getSeries() {
		return series;
	}

	public void setSeries(Series series) {
		this.series = series;
	}

	public Integer getFromNumber() {
		return fromNumber;
	}

	public void setFromNumber(Integer fromNumber) {
		this.fromNumber = fromNumber;
	}
	
	public Integer getToNumber() {
		return toNumber;
	}

	public void setToNumber(Integer toNumber) {
		this.toNumber = toNumber;
	}
	
	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public boolean isConfidential() {
		return confidential;
	}

	public void setConfidential(boolean confidential) {
		this.confidential = confidential;
	}

	public WorkPlace getWorkPlace() {
		return workPlace;
	}

	public void setWorkPlace(WorkPlace workPlace) {
		this.workPlace = workPlace;
	}

	public Scope getScope() {
		return scope;
	}

	public void setScope(Scope scope) {
		this.scope = scope;
	}

	public List<Scope> getScopes() {
		return scopes;
	}

	public void setScopes(List<Scope> scopes) {
		this.scopes = scopes;
	}

	public Series getInvoiceSeries() {
		return invoiceSeries;
	}

	public void setInvoiceSeries(Series invoiceSeries) {
		this.invoiceSeries = invoiceSeries;
	}

	public int getInvoiceNumber() {
		return invoiceNumber;
	}

	public void setInvoiceNumber(int invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}
	
	public Date getInvoiceDate() {
		return invoiceDate;
	}

	public void setInvoiceDate(Date invoiceDate) {
		this.invoiceDate = invoiceDate;
	}

	public boolean isInvoiceRecordable() {
		return invoiceRecordable;
	}

	public void setInvoiceRecordable(boolean invoiceRecordable) {
		this.invoiceRecordable = invoiceRecordable;
	}

	public String getInvoiceComments() {
		return invoiceComments;
	}

	public void setInvoiceComments(String invoiceComments) {
		this.invoiceComments = invoiceComments;
	}

	public void initializeParams() throws ManagerBeanException {
		Calendar calendar = Calendar.getInstance();

		setInvoicingGroup((InvoicingGroup)BeanManager.getManagerBean(InvoicingGroup.class).createNewTo());
		setCustomer((Customer)BeanManager.getManagerBean(Customer.class).createNewTo());
		setItem((Item)BeanManager.getManagerBean(Item.class).createNewTo());
		setCategory(new ProductCategory());
		setMonth(Month.getMonthByValue(calendar.get(Calendar.MONTH)));
		setYear(calendar.get(Calendar.YEAR));
		setConfidential(false);
		setWorkPlace(new WorkPlace());
		setScope(new Scope());
		setSegment(new Segment());
	}

	public List<Integer> getScopeIds() {
		List<Integer> scopeIds = new LinkedList<>();
		for (Scope sc : getScopes()) {
			scopeIds.add(sc.getId());
		}
		return scopeIds;
	}
	
	
	public Segment getSegment() {
		return segment;
	}
	public void setSegment(Segment segment) {
		this.segment = segment;
	}
	public int getSegmentsSize() {
		return ArrayUtils.getLength(segments);
	}	
	public Segment getEmptySegment() {
		return new Segment();
	}
	
	public Segment[] getSegments() {
		if (segments == null) {
			segments = new Segment[]{new Segment()};
		}
		return segments;
	}
	public void setSegments(Segment[] segments) {
		this.segments = segments;
	}
	
	public List<Integer> getSegmentsIds() {
		if (getSegments() != null) {
			return Arrays.stream(getSegments())
				.filter( s -> s != null)
				.filter( s -> s.getId() != null)
				.map( s -> s.getId() )
				.collect(Collectors.toCollection(LinkedList::new));
		}
		return new LinkedList<>();
	}
}