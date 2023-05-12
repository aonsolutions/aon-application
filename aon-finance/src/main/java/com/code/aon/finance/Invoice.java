package com.code.aon.finance;

import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.UniqueConstraint;

import org.hibernate.annotations.Formula;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IHeaderObject;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.audit.IAuditable;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.IScopable;
import com.code.aon.config.enumeration.InvoiceTransactionType;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.finance.enumeration.InvoiceStatus;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.finance.enumeration.RectificationType;
import com.code.aon.finance.util.FinanceUtil;
import com.code.aon.product.enumeration.ProductType;
import com.code.aon.product.strategy.ICalculableContainer;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.registry.IAddress;
import com.code.aon.registry.ITaxInfo;
import com.code.aon.registry.RegistryDocument;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.entity.master.InvoiceDB;
import com.esferalia.aon.watson.util.AonStringUtils;

@Entity
@Table(name="invoice", uniqueConstraints = @UniqueConstraint(columnNames={"series", "number", "type"}))
public class Invoice extends InvoiceDB implements IHeaderObject, ICalculableContainer, ITaxInfo, IScopable, IAuditable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Invoice.class.getName());

	private int issueYear;
	private int issueMonth;
	private int issueDay;
	private boolean defaultTaxInfo;
	private boolean skipCalculateMainActivity;
	private boolean updateEnabled;
	private boolean updateDetails;
	private boolean attachmentAvailable;

	private Set<InvoiceDetail> lines = new HashSet<InvoiceDetail>();
	private Set<Finance> finances = new HashSet<Finance>();
	private Set<InvoiceAddress> addresses = new HashSet<InvoiceAddress>();
	private Set<InvoiceAttachment> attachments = new HashSet<InvoiceAttachment>();

	public Invoice() {
		setIssueDate(new Date());
		setSecurityLevel(SecurityLevel.OFFICIAL);
		setDefaultTaxInfo(true);
		setSkipCalculateMainActivity(false);
		setUpdateEnabled(true);
		setUpdateDetails(false);
	}

    @Formula("year(issue_date)")
	public int getIssueYear() {
	 return issueYear;	
	}
	public void setIssueYear(int year) {
		issueYear = year;
	}

	@Formula("month(issue_date)")
	public int getIssueMonth() {
	 return issueMonth;	
	}
	public void setIssueMonth(int month) {
		issueMonth = month;
	}
	
	@Formula("day(issue_date)")
	public int getIssueDay() {
	 return issueDay;	
	}
	public void setIssueDay(int day) {
		issueDay = day;
	}
	
	@Transient
	public boolean isDefaultTaxInfo() {
		return defaultTaxInfo;
	}
	public void setDefaultTaxInfo(boolean defaultTaxInfo) {
		this.defaultTaxInfo = defaultTaxInfo;
	}

	@Transient
	public boolean isSkipCalculateMainActivity() {
		return skipCalculateMainActivity;
	}
	public void setSkipCalculateMainActivity(boolean skipCalculateMainActivity) {
		this.skipCalculateMainActivity = skipCalculateMainActivity;
	}

	@Transient
	public boolean isUpdateEnabled() {
		return updateEnabled;
	}
	public void setUpdateEnabled(boolean updateEnabled) {
		this.updateEnabled = updateEnabled;
	}

	@Transient
	public boolean isUpdateDetails() {
		return updateDetails;
	}
	public void setUpdateDetails(boolean updateDetails) {
		this.updateDetails = updateDetails;
	}

	@Formula("(select COUNT(*) from invoice_attach ia where id = ia.invoice)")
	public boolean isAttachmentAvailable() {
		return attachmentAvailable;
	}
	
	public void setAttachmentAvailable(boolean customer) {
		this.attachmentAvailable = customer;
	}

	@OneToMany(mappedBy = "invoice", cascade={CascadeType.REMOVE})
	@OrderBy("line")
	public Set<InvoiceDetail> getLines() {
		return this.lines;
	}
	public void setLines(Set<InvoiceDetail> lines) {
		this.lines = lines;
	}

	@OneToMany(mappedBy = "invoice", cascade={CascadeType.REMOVE})
	@OrderBy()
	public Set<Finance> getFinances() {
		return this.finances;
	}
	public void setFinances(Set<Finance> finances) {
		this.finances = finances;
	}

	@OneToMany(mappedBy = "invoice", cascade={CascadeType.REMOVE})
	public Set<InvoiceAddress> getAddresses() {
		return addresses;
	}
	public void setAddresses(Set<InvoiceAddress> addresses) {
		this.addresses = addresses;
	}

	@OneToMany(mappedBy = "invoice", cascade={CascadeType.REMOVE})
	@LazyCollection(LazyCollectionOption.EXTRA)
	public Set<InvoiceAttachment> getAttachments() {
		return attachments;
	}
	public void setAttachments(Set<InvoiceAttachment> attachments) {
		this.attachments = attachments;
	}

	@Transient
	public List<ITransferObject> getDetailList() {
		try {
			IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_INVOICE_ID), getId());
			return invoiceDetailBean.getList(criteria);
		} catch (ManagerBeanException e) {
			LOGGER.error("Error obtaining invoiceDetail list", e);
		}
		return null;
	}

	@Transient
	public List<ITransferObject> getOrderedDetailList() {
		try {
			IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_INVOICE_ID), getId());
			if (getType().equals(InvoiceType.SALES)) {
				criteria.addOrder(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_ITEM_PRODUCT_TYPE));
			}
			criteria.addOrder(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_ID));
			return invoiceDetailBean.getList(criteria);
		} catch (ManagerBeanException e) {
			LOGGER.error("Error obtaining invoiceDetail orderedList", e);
		}
		return null;
	}

	@Transient
	public boolean isVatFree() {
		return (getTransaction() != InvoiceTransactionType.NATIONAL);
	}

	@Transient
	public boolean isRetentionFree() {
		return (getTransaction() == InvoiceTransactionType.INTRACOMMUNITY || getTransaction() == InvoiceTransactionType.EXTRACOMMUNITY);
	}
	
	@Transient
	public Date getDate() {
		return getIssueDate();
	}
	
	@Transient
	public String getReference() {
		if(getNumber() <= 0) {
			String reference = "";
			if(!AonStringUtils.isBlank(getSeries())) {
				reference = reference + getSeries() + "/";
			}
			reference += "PROFORMA";
			return reference;
		} else return getReferenceCode();
	}
	
	@Transient
	public String getDocumentNumber() {
		return FinanceUtil.getDocumentNumber(getType(), getSeries(), getNumber());
	}

	@Transient
	public boolean isValidRegistryDocument() {
		RegistryDocument registryDocument = new RegistryDocument();
		registryDocument.setDocument(getRegistryDocument());
		registryDocument.setType(getRegistryDocumentType());
		registryDocument.setCountry(getRegistryDocumentCountry());
		return registryDocument.isValid();
	}

	@Transient
	public boolean isRegistryDocumentValidable() {
		RegistryDocument registryDocument = new RegistryDocument();
		registryDocument.setDocument(getRegistryDocument());
		registryDocument.setType(getRegistryDocumentType());
		registryDocument.setCountry(getRegistryDocumentCountry());
		return registryDocument.isValidable();
	}

	@Transient
	public boolean isProforma() {
		return getNumber() <= 0;
	}
	
	@Transient
	public boolean isConfidential() {
		return SecurityLevel.CONFIDENTIAL == getSecurityLevel();
	}
	@Transient
	public void setConfidential(boolean confidential) {
		setSecurityLevel(confidential ? SecurityLevel.CONFIDENTIAL : SecurityLevel.OFFICIAL);
	}

	@Transient
	public IAddress getAddress() {
		for (IAddress iAddress : getAddresses()) {
			return iAddress;
		}
		return getRegistryAddress();
	}
	
	@Transient
	public DiscountExpression getDiscountExpression() {
		return new DiscountExpression("0.0");
	}

	@Transient
	public boolean isRecordable() {
		return getStatus() == InvoiceStatus.PENDING;
	}
	@Transient
	public boolean isRecorded() {
		return getStatus() == InvoiceStatus.SCORED;
	}
	@Transient
	public boolean isSales() {
		return getType() == InvoiceType.SALES;
	}
	@Transient
	public boolean isNotSales() {
		return getType() != InvoiceType.SALES;
	}
	@Transient
	public boolean isPurchase() {
		return getType() == InvoiceType.PURCHASE;
	}
	@Transient
	public boolean isExpense() {
		return getType() == InvoiceType.EXPENSES;
	}
	@Transient
	public boolean isUndeductible() {
		return getType() == InvoiceType.UNDEDUCTIBLE;
	}
	@Transient
	public boolean isNational() {
		return getTransaction() == InvoiceTransactionType.NATIONAL;
	}
	@Transient
	public boolean isIntracommunity() {
		return getTransaction() == InvoiceTransactionType.INTRACOMMUNITY;
	}
	@Transient
	public boolean isExtracommunity() {
		return getTransaction() == InvoiceTransactionType.EXTRACOMMUNITY;
	}
	@Transient
	public boolean isCanCeuMel() {
		return getTransaction() == InvoiceTransactionType.CAN_CEU_MEL;
	}
	@Transient
	public boolean isOtherISP() {
		return getTransaction() == InvoiceTransactionType.OTHER_ISP;
	}
	@Transient
	public boolean isNoRectification() {
		return (getRectificationType() == RectificationType.NONE);
	}
	@Transient
	public boolean isRectifier() {
		return (isNormalRectifier() || isSpecialRectifier());
	}
	@Transient
	public boolean isNormalRectifier() {
		return getRectificationType() == RectificationType.NORMAL_RECTIFIER;
	}
	@Transient
	public boolean isSpecialRectifier() {
		return getRectificationType() == RectificationType.SPECIAL_RECTIFIER;
	}
	@Transient
	public boolean isRectified() {
		return (getRectificationType() == RectificationType.RECTIFIED);
	}

	@Transient
	public List<Invoice> getRectificationInvoices() throws ManagerBeanException {
		if (isRectified()) {
			List<Invoice> rectificationInvoices = new LinkedList<Invoice>();
			if (getRectificationInvoice() != null && getRectificationInvoice().getId() != null) {
				rectificationInvoices.add(getRectificationInvoice());
			} else {
				IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(invoiceBean.getFieldName(IEntityAlias.INVOICE_RECTIFICATION_INVOICE_ID), getId());
				for (ITransferObject ito : invoiceBean.getList(criteria)) {
					Invoice rectifier = (Invoice)ito;
					rectificationInvoices.add(rectifier);
				}
			}
			return rectificationInvoices;
		} else {
			return null;
		}
	}

	@Transient
	public String getRectificationInvoicesString() throws ManagerBeanException {
		String rectificationInvoiceStr = "";
		if (isRectified()) {
			for (Invoice rectifier : getRectificationInvoices()) {
				rectificationInvoiceStr += rectificationInvoiceStr.equals("") ? "" : " - ";
				rectificationInvoiceStr += rectifier.getReferenceCode();
			}
		}
		return rectificationInvoiceStr;
	}

	@Transient
	public boolean isFinanceContainer() throws ManagerBeanException {
		IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(financeBean.getFieldName(IEntityAlias.FINANCE_INVOICE_ID), getId());
		return (financeBean.getCount(criteria) > 0);
	}

	@Transient
	public boolean isAllFinancePaid() throws ManagerBeanException {
		IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(financeBean.getFieldName(IEntityAlias.FINANCE_INVOICE_ID), getId());
		criteria.addNotEqualExpression(financeBean.getFieldName(IEntityAlias.FINANCE_FINANCE_STATUS), FinanceStatus.PAID);
		criteria.addNotEqualExpression(financeBean.getFieldName(IEntityAlias.FINANCE_FINANCE_STATUS), FinanceStatus.SETTLED);
		return (financeBean.getCount(criteria) == 0);
	}

	@Transient
	public boolean isAllFinancePending() throws ManagerBeanException {
		IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(financeBean.getFieldName(IEntityAlias.FINANCE_INVOICE_ID), getId());
		criteria.addNotEqualExpression(financeBean.getFieldName(IEntityAlias.FINANCE_FINANCE_STATUS), FinanceStatus.PENDING);
		return (financeBean.getCount(criteria) == 0);
	}

	@Transient
	public boolean isPayMethodNull() throws ManagerBeanException {
		IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(financeBean.getFieldName(IEntityAlias.FINANCE_INVOICE_ID), getId());
		criteria.addNullExpression(financeBean.getFieldName(IEntityAlias.FINANCE_PAY_METHOD));
		return financeBean.getCount(criteria) > 0;
	}

	@Transient
	public String getPayMethod() throws ManagerBeanException {
		String payMethodName = null;
		IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(financeBean.getFieldName(IEntityAlias.FINANCE_INVOICE_ID), getId());
		for (ITransferObject ito : financeBean.getList(criteria)) {
			Finance finance = (Finance)ito;
			if (payMethodName == null) {
				payMethodName = (finance.getPayMethod() != null) ? finance.getPayMethod().getName() : "SIN ESPECIFICAR";
			}
			if (finance.getPayMethod() != null && !finance.getPayMethod().getName().equals(payMethodName)) {
				return "MULTIPLE";
			}
		}
		return payMethodName;
	}

	@Transient
	public double getFinanceTotal() throws ManagerBeanException {
		double financeTotal = 0;
		try {
			IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(financeBean.getFieldName(IEntityAlias.FINANCE_INVOICE_ID), getId());
			Projection projection = Projection.sum(financeBean.getFieldName(IEntityAlias.FINANCE_AMOUNT));
    		Object result = financeBean.getUniqueResult(projection, criteria);
    		financeTotal = (result != null) ? CommonUtil.round(((Double)result).doubleValue()) : 0;
		} catch (ManagerBeanException e) {
			LOGGER.error("Error obtaining invoiceDetail list", e);
		}
		return financeTotal;
	}

	@Transient
	public double getTotalQuantity() {
		double totalQuantity = 0;
		try {
			IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_INVOICE_ID), getId());
			Projection projection = Projection.sum(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_QUANTITY));
    		Object result = invoiceDetailBean.getUniqueResult(projection, criteria);
    		totalQuantity = (result != null) ? CommonUtil.round(((Double)result).doubleValue(), 3) : 0;
		} catch (ManagerBeanException e) {
			LOGGER.error("Error obtaining invoiceDetail list", e);
		}
		return totalQuantity;
	}

	@Transient
	public boolean isAllCommercialProducts() {
		try {
			IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_INVOICE_ID), getId());
			criteria.addNotEqualExpression(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_ITEM_PRODUCT_TYPE), ProductType.COMMERCIAL_PRODUCT);
			return (invoiceDetailBean.getCount(criteria) == 0);
		} catch (ManagerBeanException e) {
			LOGGER.error("Error obtaining invoiceDetail list", e);
		}
		return false;
	}

	@Transient
	public List<ITransferObject> getIncreaseDetails() {
		try {
			IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_INVOICE_ID), getId());
			criteria.addEqualExpression(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_ITEM_PRODUCT_TYPE), ProductType.INCREASE);
			return invoiceDetailBean.getList(criteria);
		} catch (ManagerBeanException e) {
			LOGGER.error("Error obtaining invoiceDetail list", e);
		}
		return null;
	}
	
	@Transient
	public double getPrepaymentTotal() {
		double totalQuantity = 0;
		try {
			IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_INVOICE_ID), getId());
			criteria.addEqualExpression(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_PREPAYMENT), Boolean.TRUE);
			Projection projection = Projection.sum(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_TAXABLE_BASE));
    		Object result = invoiceDetailBean.getUniqueResult(projection, criteria);
    		totalQuantity = (result != null) ? CommonUtil.round(((Double)result).doubleValue(), 3) : 0;
		} catch (ManagerBeanException e) {
			LOGGER.error("Error obtaining invoiceDetail list", e);
		}
		return totalQuantity;
	}

}