package com.code.aon.warehouse;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.entity.master.ItemWarehouseDB;

@Entity
@Table(name="item_warehouse")
public class ItemWarehouse extends ItemWarehouseDB {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Transient
	public Double getStock() {
		try {
			IManagerBean stockBean = BeanManager.getManagerBean(Stock.class);
			Criteria criteria = new Criteria();
			String iAlias = stockBean.getFieldName(IEntityAlias.STOCK_ITEM_ID);
			String wAlias = stockBean.getFieldName(IEntityAlias.STOCK_WAREHOUSE_ID);
			criteria.addEqualExpression(iAlias, getItem().getId());
			criteria.addEqualExpression(wAlias, getWarehouse().getId());
			List<ITransferObject> list = stockBean.getList(criteria);
			if (list != null && list.size() > 0 ) {
				Stock stock = (Stock) list.get(0);
				return stock.getQuantity();
			}
		} catch (ManagerBeanException e) {
			
		}
		return 0.0;
	}

}