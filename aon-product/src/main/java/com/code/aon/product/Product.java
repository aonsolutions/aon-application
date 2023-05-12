package com.code.aon.product;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.UniqueConstraint;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Hibernate;
import org.hibernate.SQLQuery;
import org.hibernate.Session;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.annotations.Heritable;
import com.code.aon.common.audit.IAuditable;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.product.enumeration.ProductKind;
import com.code.aon.product.enumeration.ProductType;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ql.ProjectionList;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.entity.master.ProductDB;

@Entity
@Table(name="product", uniqueConstraints = @UniqueConstraint(columnNames="code"))
@Heritable
public class Product extends ProductDB implements IAuditable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private Set<Item> items = new HashSet<Item>();
	private Set<ProductTag> tags = new HashSet<ProductTag>();
	private Set<ItemAddInfo> addInfos = new HashSet<ItemAddInfo>();    

	public Product() {
		setKind(ProductKind.SALE_PURCHASE);
	}

	@OneToMany(mappedBy="product")
	public Set<Item> getItems() {
		return this.items;
	}
	public void setItems(Set<Item> items) {
		this.items = items;
	}
	@Transient
	public void addItems(Item item) {
		item.setProduct(this);
		this.items.add(item);
	}

	@OneToMany(mappedBy = "product", cascade={CascadeType.REMOVE})
	public Set<ProductTag> getTags() {
		return this.tags;
	}
	public void setTags(Set<ProductTag> tags) {
		this.tags = tags;
	}

	@OneToMany(mappedBy="product")
	public Set<ItemAddInfo> getAddInfos() {
		return this.addInfos;
	}
	public void setAddInfos(Set<ItemAddInfo> addInfos) {
		this.addInfos = addInfos;
	}

	@Transient
	public boolean isSalePurchase() throws ManagerBeanException {
		return getKind() == ProductKind.SALE_PURCHASE;
	}

	@Transient
	public boolean isPurchase() throws ManagerBeanException {
		return getKind() == ProductKind.PURCHASE;
	}

	@Transient
	public boolean isSale() throws ManagerBeanException {
		return getKind() == ProductKind.SALE;
	}

	@Transient
	public String getTagList() throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(ProductTag.class);
		Criteria criteria = new Criteria();
		String alias = bean.getFieldName(IEntityAlias.PRODUCT_TAG_PRODUCT_ID);
		criteria.addEqualExpression(alias, getId());
		List<ITransferObject> list = bean.getList(criteria);
		if (!list.isEmpty()) {
			Set<String> tags = new TreeSet<String>();
			for(ITransferObject to : list) {
				ProductTag pt = (ProductTag) to;
				tags.add(pt.getTag().getName());
			}
			return StringUtils.join(tags, "| ");
		}
		return null;
	}
	
	@Transient
	public Item getBaseItem() throws ManagerBeanException{
		IManagerBean itemBean = BeanManager.getManagerBean(Item.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(itemBean.getFieldName(IEntityAlias.ITEM_PRODUCT_ID), getId());
		criteria.addNullExpression(itemBean.getFieldName(IEntityAlias.ITEM_SERIAL_NUMBER));
		criteria.addOrder(itemBean.getFieldName(IEntityAlias.ITEM_ID));
		for (ITransferObject ito : itemBean.getList(criteria, 0, 1)) {
			return (Item)ito;
		}
    	return null;
	}

	@Transient
	public String getFullName() throws ManagerBeanException {
		String tags = getTagList();
		if (!StringUtils.isEmpty(tags)) {
			return getName() + " (" + tags + ")";
		}
		return getName();
	}	

	@Transient
	public String getShortName() {
		return (getName().length() > 16) ? StringUtils.substring(getName(), 0, 16) : getName();
	}

	@Transient
	public boolean isWithholding() {
		return (getRetention() != null && getRetention().getId() != null);
	}

	@Transient
	public boolean isPrepayment() {
		return (getType() == ProductType.PREPAYMENT);
	}

    @Transient
    public int getItemCount() throws ManagerBeanException {
    	if (getId() != null) {
        	IManagerBean itemBean = BeanManager.getManagerBean(Item.class);
        	Criteria criteria = new Criteria();
        	criteria.addEqualExpression(itemBean.getFieldName(IEntityAlias.ITEM_PRODUCT_ID), getId());
    		return itemBean.getCount(criteria);
    	}
    	return 0;
    }

    @Transient
    public List<ITransferObject> getItemList() throws ManagerBeanException {
    	List<ITransferObject> itemList = new LinkedList<ITransferObject>();
    	if (getId() != null) {
        	IManagerBean itemBean = BeanManager.getManagerBean(Item.class);
        	Criteria criteria = new Criteria();
        	criteria.addEqualExpression(itemBean.getFieldName(IEntityAlias.ITEM_PRODUCT_ID), getId());
        	criteria.addOrder(itemBean.getFieldName(IEntityAlias.ITEM_DETAIL));
        	criteria.addOrder(itemBean.getFieldName(IEntityAlias.ITEM_DETAIL2));
        	criteria.addOrder(itemBean.getFieldName(IEntityAlias.ITEM_DETAIL3));
        	criteria.addOrder(itemBean.getFieldName(IEntityAlias.ITEM_SERIAL_NUMBER));
        	itemList = itemBean.getList(criteria);
    	}
    	return itemList;
    }

    @Transient
    public Item getUniqueItem() throws ManagerBeanException {
    	if (getItemCount() == 1) {
    		return (Item)getItemList().get(0);
    	}
    	return null;
    }

    @Transient
    public double getLastItemPrice() throws ManagerBeanException {
    	if (getId() != null) {
        	IManagerBean itemBean = BeanManager.getManagerBean(Item.class);
        	Criteria criteria = new Criteria();
        	criteria.addEqualExpression(itemBean.getFieldName(IEntityAlias.ITEM_PRODUCT_ID), getId());
        	criteria.addOrder(itemBean.getFieldName(IEntityAlias.ITEM_ID), false);
        	Projection prjPrice = Projection.property(itemBean.getFieldName(IEntityAlias.ITEM_PRICE));
        	for (Object obj : itemBean.getList(new ProjectionList(prjPrice), criteria)) {
        		return (Double)obj;
        	}
    	}
    	return 0;
    }

    @Transient
    public double getCompositionCount() throws ManagerBeanException {
    	if (getId() != null) {
			IManagerBean itemCompositionBean = BeanManager.getManagerBean(ItemComposition.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(itemCompositionBean.getFieldName(IEntityAlias.ITEM_COMPOSITION_ITEM_PRODUCT_ID), getId());
			return itemCompositionBean.getCount(criteria);
    	}
    	return 0;
    }

    @Transient
    public double getStock() throws ManagerBeanException {
    	double stock = 0;
    	if (getId() != null) {
    		String select = "SELECT SUM(quantity) quantity FROM stock as stock WHERE stock.item IN (" +
        						"SELECT id FROM item as item WHERE item.product = " + getId() + ")";
        	Session session = HibernateUtil.getSession(HibernateUtil.getSessionFactoryName());
        	SQLQuery query = session.createSQLQuery(select);
        	List<?> list = query.addScalar("quantity", Hibernate.DOUBLE).list();
        	if (!list.isEmpty() && list.get(0) != null) {
        		stock = (Double)list.get(0);
        	}
    	}
    	return stock;
    }

}