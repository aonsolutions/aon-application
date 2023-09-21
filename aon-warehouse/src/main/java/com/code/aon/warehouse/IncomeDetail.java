package com.code.aon.warehouse;

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
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryItem;
import com.code.aon.registry.enumeration.RegistryMode;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.entity.master.IncomeDetailDB;

@Entity
@Table(name="income_detail")
public class IncomeDetail extends IncomeDetailDB implements ICalculable, IStockable, IAuditable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	private final static Logger LOGGER = LoggerFactory.getLogger(IncomeDetail.class);

	public void setPrice(double price) {
        super.setPrice( CommonUtil.round(price, 4) );
	}
	
	@Transient
	public double getTaxes() {
		return 0;
	}

	@Transient
	public WorkPlace getWorkPlace() {
		return getIncome() != null ? getIncome().getWorkPlace() : null;
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
			IManagerBean rItemBean = BeanManager.getManagerBean(RegistryItem.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(rItemBean.getFieldName(IEntityAlias.REGISTRY_ITEM_REGISTRY_ID), getIncome().getSupplier().getId());
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