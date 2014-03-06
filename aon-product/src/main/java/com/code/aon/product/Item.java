package com.code.aon.product;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.annotations.Heritable;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.Tax;
import com.code.aon.product.enumeration.ProductStatus;
import com.code.aon.product.pricing.IPriceable;
import com.code.aon.product.pricing.ItemPricesManager;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.entity.master.ItemDB;

@Entity
@Table(name="item")
@Heritable
public class Item extends ItemDB implements IPriceable {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
    private Set<ItemSupplier> suppliers = new HashSet<ItemSupplier>();

	@Transient
	public String getFullName() {
		StringBuffer sb = new StringBuffer();
		
		if (StringUtils.isNotEmpty(getDetail())) {
			sb.append(getDetail());
		}
		if (StringUtils.isNotEmpty(getDetail2())) {
			if ( sb.length() > 0 ) {
				sb.append("/");
			}
			sb.append(getDetail3());
		}
		if (StringUtils.isNotEmpty(getDetail3())) {
			if ( sb.length() > 0 ) {
				sb.append("/");
			}
			sb.append(getDetail3());
		}
		if ( sb.length() > 0 ) {
			return getProduct().getName() + " [" + sb.toString() + "]";
		}
		return getProduct().getName();
	}

	public void setFullName( String value ) {
	}
	
    public void setPrice(double price) {
        super.setPrice(CommonUtil.round(price, 4));
    }
    
	public void setProfitPercent(double profitPercent) {
		super.setProfitPercent(CommonUtil.round(profitPercent, 3));
	}
	
    public void setPurchasePrice(double purchasePrice) {
		super.setPurchasePrice(CommonUtil.round(purchasePrice, 4));
	}

	@Transient
	public double getProfitablePrice() {
		return getPurchasePrice();
	}

	@Transient
	public double getSalesPrice() {
		return getSalesPrice(this.getPrice());
	}
	public double getSalesPrice(double price) {
		ItemPricesManager pricesManager = new ItemPricesManager();
		return pricesManager.getSalesPrice(this, getPrice());
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

	@OneToMany(mappedBy = "item", cascade={CascadeType.REMOVE})
	public Set<ItemSupplier> getSuppliers() {
		return this.suppliers;
	}
	public void setSuppliers(Set<ItemSupplier> suppliers) {
		this.suppliers = suppliers;
	}
	
}