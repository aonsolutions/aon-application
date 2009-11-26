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
import org.hibernate.annotations.Type;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.WorkPlace;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.InvoiceSource;
import com.code.aon.product.Item;
import com.code.aon.product.strategy.ICalculable;
import com.code.aon.product.strategy.TaxBreakDown;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.ql.Criteria;

/**
 * Transfer Object that represents an InvoiceDetail.
 * 
 * @author Consulting & Development. Iñigo Gayarre - 13-sep-2005
 * @since 1.0
 */
@Entity
@Table(name = "invoice_detail")
public class InvoiceDetail implements ITransferObject, ICalculable {

	private static final long serialVersionUID = -4734071580890529329L;

	/** The id. */
    private Integer id;

    /** The invoice. */
    private Invoice invoice;

    /** The number of the line. */
    private int line;

    /** The item. */
    private Item item;

    /** The description. */
    private String description;

    /** The quantity. */
    private double quantity;

    /** The price. */
    private double price;

    /** The discount expression. */
    private DiscountExpression discountExpression;

    /** The source. */
    private InvoiceSource source;
    
    /** A reference to a DeliveryDetail or an IncomeDetail. */
    private Integer deliveryDetail;
    
    /** The taxable base. */
    private double taxableBase;
    
    /** The taxes. */
    private double taxes;

	private WorkPlace workPlace;

    /**
     * Gets the id.
     * 
     * @return the id
     */
    @Id
    @GeneratedValue
    public Integer getId() {
        return id;
    }

    /**
     * Sets the id.
     * 
     * @param id the id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Gets the invoice.
     * 
     * @return the invoice
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="invoice", nullable = false)
    public Invoice getInvoice() {
        return invoice;
    }

    /**
     * Sets the invoice.
     * 
     * @param invoice the invoice
     */
    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    /**
     * Gets the line.
     * 
     * @return the line
     */
    public int getLine() {
        return line;
    }

    /**
     * Sets the line.
     * 
     * @param line the line
     */
    public void setLine(int line) {
        this.line = line;
    }

    /**
     * Gets the item.
     * 
     * @return the item
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="item")
    public Item getItem() {
        return item;
    }

    /**
     * Sets the item.
     * 
     * @param item the item
     */
    public void setItem(Item item) {
        this.item = item;
    }

    /**
     * Gets the description.
     * 
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description.
     * 
     * @param description the description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the quantity.
     * 
     * @return the quantity
     */
    public double getQuantity() {
        return quantity;
    }

    /**
     * Sets the quantity.
     * 
     * @param quantity the quantity
     */
    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    /**
     * Gets the price.
     * 
     * @return the price
     */
    public double getPrice() {
        return price;
    }

    /**
     * Sets the price.
     * 
     * @param price the price
     */
    public void setPrice(double price) {
        this.price = price;
    }

    /**
     * Gets the discount expression.
     * 
     * @return the discount expression
     */
    @Column(name ="discount_expr")
    @Type(type = "com.code.aon.product.util.DiscountExpressionUserType")
    public DiscountExpression getDiscountExpression() {
        return discountExpression;
    }

    /**
     * Sets the discount expression.
     * 
     * @param discountExpression the discount expression
     */
    public void setDiscountExpression(DiscountExpression discountExpression) {
        this.discountExpression = discountExpression;
    }

    /**
     * Gets the source.
     * 
     * @return the source
     */
    @Column(name = "source")
    public InvoiceSource getSource() {
        return source;
    }

    /**
     * Sets the source.
     * 
     * @param source the source
     */
    public void setSource(InvoiceSource source) {
        this.source = source;
    }

    /**
     * Gets the delivery detail.
     * 
     * @return the delivery detail
     */
    @Column(name="delivery_detail")
    public Integer getDeliveryDetail() {
        return deliveryDetail;
    }

    /**
     * Sets the delivery detail.
     * 
     * @param deliveryDetail the delivery detail
     */
    public void setDeliveryDetail(Integer deliveryDetail) {
        this.deliveryDetail = deliveryDetail;
    }
    
    /**
     * Gets the taxable base.
     * 
     * @return the taxable base
     */
    @Column(name="taxable_base")
	public double getTaxableBase() {
		return taxableBase;
	}

	/**
	 * Sets the taxable base.
	 * 
	 * @param taxableBase the taxable base
	 */
	public void setTaxableBase(double taxableBase) {
		this.taxableBase = taxableBase;
	}
	
	/**
	 * Gets the taxes.
	 * 
	 * @return the taxes
	 */
	public double getTaxes() {
		return taxes;
	}

	/**
	 * Sets the taxes.
	 * 
	 * @param taxes the taxes
	 */
	public void setTaxes(double taxes) {
		this.taxes = taxes;
	}
	
    @ManyToOne
    @JoinColumn(name="workplace", nullable = false)
	public WorkPlace getWorkPlace() {
		return workPlace;
	}

	public void setWorkPlace(WorkPlace workPlace) {
		this.workPlace = workPlace;
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
				taxBreakDowns.add(taxBreakDown);
			}
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		}
		return taxBreakDowns;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
    		return super.equals(obj);
		}
		if (obj instanceof InvoiceDetail) {
			InvoiceDetail o = (InvoiceDetail) obj;
			if (o.getId() == null && id == null) {
				return super.equals(obj);	
			}
			if (ObjectUtils.equals(getId(), o.getId())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		return 0;
	}
	
}