package com.code.aon.warehouse;

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
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.entity.master.IncomeDetailDB;

@Entity
@Table(name="income_detail")
public class IncomeDetail extends IncomeDetailDB implements ICalculable, IStockable {
	
	private static final long serialVersionUID = 3100497435533821492L;
	private final static Logger LOGGER = LoggerFactory.getLogger(IncomeDetail.class);

	public void setPrice(double price) {
        setPrice( CommonUtil.round(price, 4) );
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
			criteria.addEqualExpression(itemSupplierBean.getFieldName(IEntityAlias.ITEM_SUPPLIER_ITEM_ID),getItem().getId());
			criteria.addEqualExpression(itemSupplierBean.getFieldName(IEntityAlias.ITEM_SUPPLIER_SUPPLIER_ID),getIncome().getSupplier().getId());
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