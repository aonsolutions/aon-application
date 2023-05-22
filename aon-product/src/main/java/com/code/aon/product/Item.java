package com.code.aon.product;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Hibernate;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.annotations.Where;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.annotations.Heritable;
import com.code.aon.common.audit.IAuditable;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.Tax;
import com.code.aon.product.enumeration.ProductStatus;
import com.code.aon.product.pricing.IPriceable;
import com.code.aon.product.pricing.ItemPricesManager;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ql.ProjectionList;
import com.code.aon.registry.RegistryItem;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.entity.master.ItemDB;

@Entity
@Table(name="item")
@Heritable
public class Item extends ItemDB implements IPriceable, IAuditable {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	private static Logger LOGGER = Logger.getLogger(Item.class.getName()); 

	private double packFormatQuantity;
	private double packUnitsQuantity;
	private double packMeasurementQuantity;

    private Set<RegistryItem> customers = new HashSet<RegistryItem>();
    private Set<RegistryItem> suppliers = new HashSet<RegistryItem>();
    private Set<ItemAddInfo> addInfos = new HashSet<ItemAddInfo>();
    private Set<ItemComposition> compositions = new HashSet<ItemComposition>();

    @Transient
    public double getPackFormatQuantity() {
    	return packFormatQuantity;
    }
	public void setPackFormatQuantity(double packFormatQuantity) {
		this.packFormatQuantity = CommonUtil.round(packFormatQuantity, 3);
		this.packUnitsQuantity = CommonUtil.round(packFormatQuantity * getPackUnits());
		this.packMeasurementQuantity = CommonUtil.round(packFormatQuantity * getPackUnits() * getPackMeasurement());
	}

    @Transient
    public double getPackUnitsQuantity() {
    	return packUnitsQuantity;
    }
	public void setPackUnitsQuantity(double packUnitsQuantity) {
		this.packUnitsQuantity = CommonUtil.round(packUnitsQuantity, 3);
		this.packFormatQuantity = CommonUtil.round(packUnitsQuantity / getPackUnits());
		this.packMeasurementQuantity = CommonUtil.round(packUnitsQuantity * getPackMeasurement());
	}

    @Transient
    public double getPackMeasurementQuantity() {
    	return packMeasurementQuantity;
    }
	public void setPackMeasurementQuantity(double packMeasurementQuantity) {
		this.packMeasurementQuantity = CommonUtil.round(packMeasurementQuantity, 3);
		this.packUnitsQuantity = CommonUtil.round(packMeasurementQuantity / getPackMeasurement());
		this.packFormatQuantity = CommonUtil.round(packMeasurementQuantity / getPackMeasurement() / getPackUnits());
	}

	@OneToMany(mappedBy = "item", cascade={CascadeType.REMOVE})
	@Where(clause = "type=1")
	public Set<RegistryItem> getCustomers() {
		return customers;
	}
	public void setCustomers(Set<RegistryItem> customers) {
		this.customers = customers;
	}
	
	@OneToMany(mappedBy = "item", cascade={CascadeType.REMOVE})
	@Where(clause = "type=2")
	public Set<RegistryItem> getSuppliers() {
		return suppliers;
	}
	public void setSuppliers(Set<RegistryItem> suppliers) {
		this.suppliers = suppliers;
	}
	
	@OneToMany(mappedBy = "item", cascade={CascadeType.REMOVE})
	public Set<ItemAddInfo> getAddInfos() {
		return addInfos;
	}
	public void setAddInfos(Set<ItemAddInfo> addInfos) {
		this.addInfos = addInfos;
	}
	
	@OneToMany(mappedBy = "item", cascade={CascadeType.REMOVE})
	public Set<ItemComposition> getCompositions() {
		return compositions;
	}
	public void setCompositions(Set<ItemComposition> compositions) {
		this.compositions = compositions;
	}
	
	@Transient
	public String getFullName() {
		String details = getDetails();
		StringBuffer sb = new StringBuffer();
		if (getProduct() != null) {
			if (StringUtils.isNotEmpty(getProduct().getName())) {
				sb.append(getProduct().getName());
			}
			try {
				if (getPackFormatTag() != null && getPackFormatTag().getId() != null) {
					sb.append(" " + getPackFormatTag().getName());
				}
				if (getProduct().isPackaged()) {
					sb.append(" " + getPackUnits());
					sb.append(" " + getPackUnitsTag().getName());
					sb.append(" (" + getPackMeasurement());
					sb.append(" " + getPackMeasurementTag().getName() + ")");
				}
			} catch (Exception e) {
				sb.append(" ??? ");
				LOGGER.severe(sb.toString() + " #### " + e.toString());
			}
		}
			
		if (StringUtils.isNotEmpty(details)) {
			sb.append(" [" + details + "]");
		}
			
		if (getProduct() != null) {
			if (getProduct().isSerializable() && StringUtils.isNotEmpty(getSerialNumber())) {
				sb.append(" #" + getSerialNumber());
			}
		}
		if (getProduct() == null) {
			LOGGER.warning("Atributo 'product' es nulo. POSIBLE ERROR DE LOOKUP?");
		}
		return (sb.length() > 0) ? sb.toString() : "";
	}

	public void setFullName( String value ) {
	}

	@Transient
	public String getDetails() {
		StringBuffer sb = new StringBuffer();
		if (StringUtils.isNotEmpty(getDetail())) {
			sb.append(getDetail());
		}
		if (StringUtils.isNotEmpty(getDetail2())) {
			if (sb.length() > 0) {
				sb.append(" / ");
			}
			sb.append(getDetail2());
		}
		if (StringUtils.isNotEmpty(getDetail3())) {
			if (sb.length() > 0) {
				sb.append(" / ");
			}
			sb.append(getDetail3());
		}
		return (sb.length() > 0) ? sb.toString() : "";
	}

	@Transient
	public String getFullDetails() {
		StringBuffer sb = new StringBuffer();
		sb.append(getDetails());
		if (getProduct().isSerializable() && StringUtils.isNotEmpty(getSerialNumber())) {
			sb.append(" #" + getSerialNumber());
		}
		return (sb.length() > 0) ? sb.toString() : "";
	}

	public void setPurchasePrice(double purchasePrice) {
		super.setPurchasePrice(CommonUtil.round(purchasePrice, 4));
	}

	public void setProfitPercent(double profitPercent) {
		super.setProfitPercent(CommonUtil.round(profitPercent, 3));
	}
	
    public void setPrice(double price) {
        super.setPrice(CommonUtil.round(price, 4));
    }
    
	@Transient
	public double getProfitablePrice() {
		return getPurchasePrice();
	}

	@Transient
	public double getSalesProfitPercent() {
		return getSalesProfitPercent(getPurchasePrice(), getPrice());
	}
	public double getSalesProfitPercent(double purchasePrice, double price) {
		ItemPricesManager pricesManager = new ItemPricesManager();
		return pricesManager.getSalesProfit(purchasePrice, price);
	}
	public void setSalesProfitPercent(double salesProfitPercent) {
	}

	@Transient
	public double getSalesPrice() {
		return getSalesPrice(getPrice());
	}
	public double getSalesPrice(double price) {
		ItemPricesManager pricesManager = new ItemPricesManager();
		return pricesManager.getSalesPrice(this, price);
	}
	public void setSalesPrice(double salesPrice) {
	}

	@Transient
	public Tax getVat() {
		return getProduct().getVat();
	}

	@Transient
	public Tax getRetention() {
		return (getProduct().isWithholding()) ? getProduct().getRetention() : null;
	}

	@Transient
	public boolean isActive() {
		return getStatus() == ProductStatus.ACTIVE;
	}

    @Transient
	public List<ItemComposition> getItemCompositionList() throws ManagerBeanException {
		List<ItemComposition> compositionList = new LinkedList<ItemComposition>();
		IManagerBean itemCompositionBean = BeanManager.getManagerBean(ItemComposition.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(itemCompositionBean.getFieldName(IEntityAlias.ITEM_COMPOSITION_ITEM_ID), getId());
		criteria.addOrder(itemCompositionBean.getFieldName(IEntityAlias.ITEM_COMPOSITION_SEQUENCE));
		for (ITransferObject ito : itemCompositionBean.getList(criteria)) {
			ItemComposition composition = (ItemComposition)ito;
			compositionList.add(composition);
			if (composition.getCompositionItem().getProduct().isComposition()) {
				compositionList.addAll(composition.getCompositionItem().getItemCompositionList());
			}
		}
		return compositionList;
	}

    @Transient
    public double getStock() throws ManagerBeanException {
    	double stock = 0;
    	if (getId() != null) {
        	String select = "SELECT SUM(quantity) quantity FROM stock as stock WHERE stock.item = " + getId();
        	Session session = HibernateUtil.getSession(HibernateUtil.getSessionFactoryName());
        	SQLQuery query = session.createSQLQuery(select);
        	List<?> list = query.addScalar("quantity", Hibernate.DOUBLE).list();
        	if (!list.isEmpty() && list.get(0) != null) {
        		stock = (Double)list.get(0);
        	}
    	}
    	return stock;
    }

    @Transient
    public boolean isWildCard() throws ManagerBeanException {
    	boolean wildCard = false;
    	if (getProduct().getId() != null && getProduct().isSerializable()) {
    		if (getId() != null) {
        		IManagerBean itemBean = BeanManager.getManagerBean(Item.class);
        		Criteria criteria = new Criteria();
        		criteria.addEqualExpression(itemBean.getFieldName(IEntityAlias.ITEM_PRODUCT_ID), getProduct().getId());
        		criteria.addNullExpression(itemBean.getFieldName(IEntityAlias.ITEM_SERIAL_NUMBER));
        		criteria.addOrder(itemBean.getFieldName(IEntityAlias.ITEM_ID));
        		Projection prjId = Projection.property(itemBean.getFieldName(IEntityAlias.ITEM_ID));
            	List<?> list = itemBean.getList(new ProjectionList(prjId), criteria);
				if (list.size() > 0 && list.get(0) != null) {
            		wildCard = (getId().intValue() == ((Integer)list.get(0)).intValue());
        		}
    		} else {
    			return getProduct().getItemCount() == 0;
    		}
    	}
    	return wildCard;
    }

    @Transient
	public void initializePackQuantities(double stockQuantity) {
    	if (getProduct().isPackaged() && getStockUnitTag() != null) {
    		if (getPackFormatTag() != null && getStockUnitTag().equals(getPackFormatTag())) {
    			packFormatQuantity = stockQuantity;
    			packUnitsQuantity = CommonUtil.round(stockQuantity * getPackUnits());
    			packMeasurementQuantity = CommonUtil.round(stockQuantity * getPackUnits() * getPackMeasurement());
    		} else if (getPackUnitsTag() != null && getStockUnitTag().equals(getPackUnitsTag())) {
    			packFormatQuantity = CommonUtil.round(stockQuantity / getPackUnits());
    			packUnitsQuantity = stockQuantity;
    			packMeasurementQuantity = CommonUtil.round(stockQuantity * getPackMeasurement());
    		} else if (getPackMeasurementTag() != null && getStockUnitTag().equals(getPackMeasurementTag())) {
    			packFormatQuantity = CommonUtil.round(stockQuantity / (getPackUnits() * getPackMeasurement()));
    			packUnitsQuantity = CommonUtil.round(stockQuantity / getPackMeasurement());
    			packMeasurementQuantity = stockQuantity;
    		}
    	}
    }

    @Transient
	public double getPackStockQuantity() {
    	if (getProduct().isPackaged() && getStockUnitTag() != null) {
    		if (getPackFormatTag() != null && getStockUnitTag().equals(getPackFormatTag())) {
    			return getPackFormatQuantity(); 
    		} else if (getPackUnitsTag() != null && getStockUnitTag().equals(getPackUnitsTag())) {
    			return getPackUnitsQuantity();
    		} else if (getPackMeasurementTag() != null && getStockUnitTag().equals(getPackMeasurementTag())) {
    			return getPackMeasurementQuantity();
    		}
    	}
    	return 0;
    }

}