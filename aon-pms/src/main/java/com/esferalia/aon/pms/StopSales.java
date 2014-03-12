package com.esferalia.aon.pms;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.aon.common.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.audit.IAuditable;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.entity.master.StopSalesDB;

@Entity
@Table(name="stop_sales")
public class StopSales extends StopSalesDB implements IAuditable {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	public StopSales() {
		setActive(true);
	}

	@Transient
	public List<ITransferObject> getStopSalesItems() throws ManagerBeanException {
		IManagerBean stopSalesItemBean = BeanManager.getManagerBean(StopSalesItem.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(stopSalesItemBean.getFieldName(IEntityAlias.STOP_SALES_ITEM_STOP_SALES_ID), getId());
		criteria.addOrder(stopSalesItemBean.getFieldName(IEntityAlias.STOP_SALES_ITEM_ITEM_PRODUCT_CODE));
		return stopSalesItemBean.getList(criteria);
	}

	@Transient
	public String getRoomTypeCodes() throws ManagerBeanException {
		StringBuffer roomTypeCodes = new StringBuffer();
		for (ITransferObject ito : getStopSalesItems()) {
			StopSalesItem stopSalesItem = (StopSalesItem)ito;
			roomTypeCodes.append(stopSalesItem.getItem().getProduct().getCode());
			roomTypeCodes.append(" ");
		}
		return roomTypeCodes.length() > 0 ? roomTypeCodes.toString() : "TODAS"; 
	}

	@Transient
	public List<ITransferObject> getStopSalesTariffs() throws ManagerBeanException {
		IManagerBean stopSalesTariffBean = BeanManager.getManagerBean(StopSalesTariff.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(stopSalesTariffBean.getFieldName(IEntityAlias.STOP_SALES_TARIFF_STOP_SALES_ID), getId());
		criteria.addOrder(stopSalesTariffBean.getFieldName(IEntityAlias.STOP_SALES_TARIFF_TARIFF_CODE));
		return stopSalesTariffBean.getList(criteria);
	}

	@Transient
	public String getTariffCodes() throws ManagerBeanException {
		StringBuffer tariffCodes = new StringBuffer();
		for (ITransferObject ito : getStopSalesTariffs()) {
			StopSalesTariff stopSalesTariff = (StopSalesTariff)ito;
			tariffCodes.append(stopSalesTariff.getTariff().getCode());
			tariffCodes.append(" ");
		}
		return tariffCodes.length() > 0 ? tariffCodes.toString() : "TODAS";
	}

}
