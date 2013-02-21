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

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.annotations.Heritable;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.Tax;
import com.code.aon.product.enumeration.ProductStatus;
import com.code.aon.product.pricing.IPriceable;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.entity.master.ItemDB;

@Entity
@Table(name="item")
@Heritable
public class Item extends ItemDB implements IPriceable {

	private static final long serialVersionUID = -2720748805321005422L;
	
    private Set<ItemSupplier> suppliers = new HashSet<ItemSupplier>();

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

	@Transient
	public double getProfitablePrice() {
		return getPurchasePrice();
	}

	@Transient
	public double getSalesPrice() {
		return getSalesPrice(this.getPrice());
	}
	public double getSalesPrice(double price) {
		double vatQuota = (getVat() != null) ? CommonUtil.round(price * getVat().getPercentage() / 100) : 0;
		double retentionQuota = (getProduct().isWithholding()) ? CommonUtil.round(price * getRetention().getPercentage() / 100) : 0;
		return CommonUtil.round(CommonUtil.round(price) + vatQuota - retentionQuota);
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
	public String getFullName() {
		StringBuffer sb = new StringBuffer();
		if ( (getProduct() != null) && (getProduct().getId() != null) ) {
			sb.append(getProduct().getName());
			if (! StringUtils.isEmpty(getDetail())) {
				sb.append( " (").append(getDetail());
				if (! StringUtils.isEmpty(getDetail2())) {
					sb.append( ",").append(getDetail2());
				}
				sb.append( ")");
			}
		}
		return sb.toString();
	}

}