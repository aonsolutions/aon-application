package com.code.aon.purchase;

import java.util.Iterator;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.product.ItemSupplier;
import com.code.aon.product.strategy.ICalculable;
import com.code.aon.purchase.enumeration.PurchaseDetailStatus;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.entity.master.PurchaseDetailDB;

@Entity
@Table(name="purchase_detail")
public class PurchaseDetail extends PurchaseDetailDB implements ICalculable {

	private static final long serialVersionUID = 1L;
	private final static Logger LOGGER = LoggerFactory.getLogger(PurchaseDetail.class);
	private double transfered;
	private boolean forcePendingQuantityCancel;

    public void setPrice(double price) {
        super.setPrice(CommonUtil.round(price, 4));
    }

    @Transient
	public double getPendingQuantity() {
		return CommonUtil.round(getQuantity() - getDelivered(), 3);
	}
	@Transient
	public double getTransfered() {
		return transfered;
	}
	public void setTransfered(double transfered) {
		this.transfered = transfered;
	}
	@Transient
	public boolean isForcePendingQuantityCancel() {
		return forcePendingQuantityCancel;
	}
	public void setForcePendingQuantityCancel(boolean forcePendingQuantityCancel) {
		this.forcePendingQuantityCancel = forcePendingQuantityCancel;
	}

	@Transient
	public boolean isPending() {
		return getStatus() == PurchaseDetailStatus.PENDING;
	}
	@Transient
	public boolean isPartialSettled() {
		return getStatus() == PurchaseDetailStatus.PARTIAL_SETTLED;
	}
	@Transient
	public boolean isSettled() {
		return getStatus() == PurchaseDetailStatus.SETTLED;
	}

	@Transient
    public String getItemSupplierCode() {
    	try {
			IManagerBean itemSupplierBean = BeanManager.getManagerBean(ItemSupplier.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(itemSupplierBean.getFieldName(IEntityAlias.ITEM_SUPPLIER_ITEM_ID),getItem().getId());
			criteria.addEqualExpression(itemSupplierBean.getFieldName(IEntityAlias.ITEM_SUPPLIER_SUPPLIER_ID),getPurchase().getSupplier().getId());
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
	
}