package com.code.aon.finance;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Type;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.company.WorkPlace;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.InvoiceSource;
import com.code.aon.product.Item;
import com.code.aon.product.strategy.ICalculable;
import com.code.aon.product.strategy.TaxBreakDown;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.project.Project;
import com.code.aon.ql.Criteria;
import com.code.aon.warehouse.DeliveryDetail;

@Entity
@Table(name = "invoice_detail")
public class InvoiceDetail implements ITransferObject, ICalculable {

	private static final long serialVersionUID = -4734071580890529329L;

    private Integer id;
    private Invoice invoice;
    private Project project;
    private int line;
    private Item item;
    private String description;
    private double quantity;
    private double price;
    private DiscountExpression discountExpression;
    private InvoiceSource source;
    private Integer sourceId;
    private double taxableBase;
    private double taxes;
	private WorkPlace workPlace;

	private boolean updateEnabled;
	private boolean taxDataInDetail;
	private double vatPercent;
	private double vatQuota;
	private double retentionPercent;
	private double retentionQuota;

	public InvoiceDetail() {
		this.updateEnabled = true;
		this.taxDataInDetail = false;
	}

    @Id
    @GeneratedValue
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="invoice", nullable = false)
    @ForeignKey(name="FK_INVOICE_DETAIL_INVOICE")
    @Index(name="IDX_INVOICE_DETAIL_INVOICE")                                    
    public Invoice getInvoice() {
        return invoice;
    }
    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="project")
    @ForeignKey(name="FK_INVOICE_DETAIL_PROJECT")
    @Index(name="IDX_INVOICE_DETAIL_PROJECT")                                    
    public Project getProject() {
        return project;
    }
    public void setProject(Project project) {
        this.project = project;
    }

    public int getLine() {
        return line;
    }
    public void setLine(int line) {
        this.line = line;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="item")
    @ForeignKey(name="FK_INVOICE_DETAIL_ITEM")
    @Index(name="IDX_INVOICE_DETAIL_ITEM")                                        
    public Item getItem() {
        return item;
    }
    public void setItem(Item item) {
        this.item = item;
    }

    @Column(length=1024)
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

	@Column(precision=15, scale=3)
    public double getQuantity() {
        return quantity;
    }
    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

	@Column(precision=15, scale=4)
    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = CommonUtil.round(price, 4);
    }

    @Column(name ="discount_expr",length=32)
    @Type(type = "com.code.aon.product.util.DiscountExpressionUserType")
    public DiscountExpression getDiscountExpression() {
        return discountExpression;
    }
    public void setDiscountExpression(DiscountExpression discountExpression) {
        this.discountExpression = discountExpression;
    }

    @Column(name = "source")
    public InvoiceSource getSource() {
        return source;
    }
    public void setSource(InvoiceSource source) {
        this.source = source;
    }

    @Column(name="source_id")
    @Index(name="IDX_INVOICE_DETAIL_SOURCE_ID")
    public Integer getSourceId() {
        return sourceId;
    }
    public void setSourceId(Integer sourceId) {
        this.sourceId = sourceId;
    }
    
    @Column(name="taxable_base", precision=15, scale=4)
	public double getTaxableBase() {
		return taxableBase;
	}
	public void setTaxableBase(double taxableBase) {
		this.taxableBase = CommonUtil.round(taxableBase, 4);
	}
	
	@Column(precision=15, scale=3)
	public double getTaxes() {
		return taxes;
	}
	public void setTaxes(double taxes) {
		this.taxes = CommonUtil.round(taxes, 3);
	}
	
    @ManyToOne
    @JoinColumn(name="workplace", nullable = false)
    @ForeignKey(name="FK_INVOICE_DETAIL_WORKPLACE")
    @Index(name="IDX_INVOICE_DETAIL_WORKPLACE")                                            
	public WorkPlace getWorkPlace() {
		return workPlace;
	}

	public void setWorkPlace(WorkPlace workPlace) {
		this.workPlace = workPlace;
	}
	
	@Transient
	public boolean isUpdateEnabled() {
		return updateEnabled;
	}
	public void setUpdateEnabled(boolean updateEnabled) {
		this.updateEnabled = updateEnabled;
	}

	@Transient
	public boolean isTaxDataInDetail() {
		return taxDataInDetail;
	}
	public void setTaxDataInDetail(boolean taxDataInDetail) {
		this.taxDataInDetail = taxDataInDetail;
	}

	@Transient
	public double getVatPercent() {
		return vatPercent;
	}
	public void setVatPercent(double vatPercent) {
		this.vatPercent = CommonUtil.round(vatPercent);
	}

	@Transient
	public double getVatQuota() {
		return vatQuota;
	}
	public void setVatQuota(double vatQuota) {
		this.vatQuota = CommonUtil.round(vatQuota);
	}

	@Transient
	public double getRetentionPercent() {
		return retentionPercent;
	}
	public void setRetentionPercent(double retentionPercent) {
		this.retentionPercent = CommonUtil.round(retentionPercent);
	}

	@Transient
	public double getRetentionQuota() {
		return retentionQuota;
	}
	public void setRetentionQuota(double retentionQuota) {
		this.retentionQuota = CommonUtil.round(retentionQuota);
	}

	@Transient
	@SuppressWarnings("unchecked")
	public List getTaxBreakDowns() {
		List<TaxBreakDown> taxBreakDowns = new LinkedList<TaxBreakDown>();
		try {
			IManagerBean invoiceTaxBean = BeanManager.getManagerBean(InvoiceTax.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(invoiceTaxBean.getFieldName(IFinanceAlias.INVOICE_TAX_INVOICE_DETAIL_ID), getId());
			criteria.addOrder(invoiceTaxBean.getFieldName(IFinanceAlias.INVOICE_TAX_TAX_TYPE));
			Iterator iter = invoiceTaxBean.getList(criteria).iterator();
			while(iter.hasNext()){
				InvoiceTax invoiceTax = (InvoiceTax)iter.next();
				TaxBreakDown taxBreakDown = new TaxBreakDown();
				taxBreakDown.setBase(getTaxableBase());
				taxBreakDown.setTaxType(invoiceTax.getTaxType());
				taxBreakDown.setTaxPercent(invoiceTax.getPercentage());
				taxBreakDown.setSurchargePercent(invoiceTax.getSurcharge());
				taxBreakDown.setTaxQuota(invoiceTax.getQuota());
				taxBreakDown.setSurchargeQuota(invoiceTax.getSurchargeQuota());
				taxBreakDowns.add(taxBreakDown);
			}
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		}
		return taxBreakDowns;
	}

	@Transient
	public ITransferObject getSourceTo() throws ManagerBeanException {
		if (getSourceId() != null) {
			if (InvoiceSource.DIRECT_SALES == getSource() || InvoiceSource.DELIVERY == getSource()) {
				IManagerBean deliveryDetailBean = BeanManager.getManagerBean(DeliveryDetail.class);
				return (DeliveryDetail)deliveryDetailBean.get(getSourceId());
			}
			/*if (InvoiceSource.DIRECT_PURCHASE == getSource() || InvoiceSource.INCOME == getSource()) {
				IManagerBean incomeDetailBean = BeanManager.getManagerBean(IncomeDetail.class);
				return (IncomeDetail)incomeDetailBean.get(getSourceId());
			}*/
		}
		return null;
	}

	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final InvoiceDetail o = (InvoiceDetail) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
			.append(this.description,o.description)
			.append(this.discountExpression,o.discountExpression)
			.append(this.invoice,o.invoice)
			.append(this.item,o.item)
			.append(this.line,o.line)
			.append(this.quantity,o.quantity)
			.append(this.price,o.price)
			.append(this.project,o.project)
			.append(this.source,o.source)
			.append(this.sourceId,o.sourceId)
			.append(this.taxableBase,o.taxableBase)
			.append(this.taxes,o.taxes)
			.append(this.workPlace,o.workPlace)
			.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(description)
			.append(discountExpression)
			.append(invoice)
			.append(id)		
			.append(item)
			.append(line)
			.append(quantity)
			.append(price)
			.append(project)
			.append(source)
			.append(sourceId)
			.append(taxableBase)
			.append(taxes)
			.append(workPlace)
			.toHashCode();
	}	

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}

}