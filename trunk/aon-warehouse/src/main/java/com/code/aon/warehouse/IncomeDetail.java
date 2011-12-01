package com.code.aon.warehouse;

import java.util.Iterator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.product.Item;
import com.code.aon.product.ItemSupplier;
import com.code.aon.product.dao.IProductAlias;
import com.code.aon.product.strategy.ICalculable;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.project.Project;
import com.code.aon.purchase.PurchaseDetail;
import com.code.aon.ql.Criteria;

@Entity
@Table(name="income_detail")
public class IncomeDetail implements ITransferObject, ICalculable, IStockable {
	
	private static final long serialVersionUID = 3100497435533821492L;
	private final static Logger LOGGER = LoggerFactory.getLogger(IncomeDetail.class);

	private Integer id;
	private Income income;
	private Project project;
    private Integer line;
	private Item item;
	private String description;
	private Warehouse warehouse;
	private double quantity;
	private double price;
	private DiscountExpression discountExpression;
	private PurchaseDetail purchaseDetail;
	
	@Id
	@GeneratedValue
	@Column(nullable=false)	
	public Integer getId() {
		return id;
	}
	public void setId(Integer primaryKey) {
		this.id = primaryKey;
	}
	
	@ManyToOne
    @JoinColumn(name="income", nullable = false, updatable = false)
	public Income getIncome() {
		return income;
	}
	public void setIncome(Income income) {
		this.income = income;
	}

	@ManyToOne
    @JoinColumn(name="project")
	public Project getProject() {
		return project;
	}
	public void setProject(Project project) {
		this.project = project;
	}

	public Integer getLine() {
		return line;
	}
	public void setLine(Integer line) {
		this.line = line;
	}

	@ManyToOne
	@JoinColumn(name="item", nullable=false)
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
	
	@ManyToOne
	@JoinColumn(name="warehouse", nullable = false)
	public Warehouse getWarehouse() {
		return warehouse;
	}
	public void setWarehouse(Warehouse warehouse) {
		this.warehouse = warehouse;
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
	
	@Column(name ="discount_expr")
    @Type(type = "com.code.aon.product.util.DiscountExpressionUserType")
	public DiscountExpression getDiscountExpression() {
		return discountExpression;
	}
	public void setDiscountExpression(DiscountExpression discountExpression) {
		this.discountExpression = discountExpression;
	}

	@ManyToOne
    @JoinColumn(name="purchase_detail")
	public PurchaseDetail getPurchaseDetail() {
		return purchaseDetail;
	}

	public void setPurchaseDetail(PurchaseDetail purchaseDetail) {
		this.purchaseDetail = purchaseDetail;
	}

	@Transient
	public double getTaxes() throws ManagerBeanException {
		return 0;
	}
	@Transient
	public boolean isEntry() {
		return true;
	}
	@Transient
	public String getTableName() {
		return "income_detail";
	}
	
	@Transient
    public String getItemSupplierCode() {
    	try {
			IManagerBean itemSupplierBean = BeanManager.getManagerBean(ItemSupplier.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(itemSupplierBean.getFieldName(IProductAlias.ITEM_SUPPLIER_ITEM_ID),getItem().getId());
			criteria.addEqualExpression(itemSupplierBean.getFieldName(IProductAlias.ITEM_SUPPLIER_SUPPLIER_ID),getIncome().getSupplier().getId());
			Iterator<?> iterator = itemSupplierBean.getList(criteria).iterator();
			if (iterator.hasNext()) {
				ItemSupplier itemSupplier = (ItemSupplier)iterator.next();
				return !StringUtils.isEmpty(itemSupplier.getCode()) ? itemSupplier.getCode() : getItem().getProduct().getCode();
			}
		} catch (ManagerBeanException e) {
			LOGGER.error("Can't get ItemSupplier.code", e);
		}
		return getItem().getProduct().getCode();
	}  

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final IncomeDetail o = (IncomeDetail) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.description, o.description)
				.append(this.discountExpression, o.discountExpression)
				.append(this.income, o.income)
				.append(this.item, o.item)
				.append(this.line, o.line)
				.append(this.price, o.price)
				.append(this.project, o.project)
				.append(this.purchaseDetail, o.purchaseDetail)
				.append(this.quantity, o.quantity)
				.append(this.warehouse, o.warehouse)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(description)
			.append(discountExpression)
			.append(id)	
			.append(income)
			.append(item)
			.append(line)
			.append(price)
			.append(project)
			.append(purchaseDetail)
			.append(quantity)
			.append(warehouse)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}
}