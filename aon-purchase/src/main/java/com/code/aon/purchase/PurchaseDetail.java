package com.code.aon.purchase;

import java.util.Iterator;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.audit.IAuditable;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.company.WorkPlace;
import com.code.aon.product.strategy.ICalculable;
import com.code.aon.purchase.enumeration.PurchaseDetailStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryItem;
import com.code.aon.registry.enumeration.RegistryMode;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.entity.master.PurchaseDetailDB;

@Entity
@Table(name="purchase_detail")
public class PurchaseDetail extends PurchaseDetailDB implements ICalculable, IAuditable {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
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
	public WorkPlace getWorkPlace() {
		return getPurchase().getWorkPlace();
	}

	@Transient
    public String getItemSupplierCode() {
    	try {
			IManagerBean rItemBean = BeanManager.getManagerBean(RegistryItem.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(rItemBean.getFieldName(IEntityAlias.REGISTRY_ITEM_REGISTRY_ID), getPurchase().getSupplier().getId());
			criteria.addEqualExpression(rItemBean.getFieldName(IEntityAlias.REGISTRY_ITEM_ITEM_ID), getItem().getId());
			criteria.addEqualExpression(rItemBean.getFieldName(IEntityAlias.REGISTRY_ITEM_TYPE), RegistryMode.SUPPLIER);
			Iterator<?> iterator = rItemBean.getList(criteria).iterator();
			if (iterator.hasNext()) {
				RegistryItem registryItem = (RegistryItem)iterator.next();
				return registryItem.getCode();
			}
		} catch (ManagerBeanException e) {
			LOGGER.error("Can't get ItemSupplier.code", e);
		}
		return null;
	}

}