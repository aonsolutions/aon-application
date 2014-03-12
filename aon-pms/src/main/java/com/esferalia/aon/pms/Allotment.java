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
import com.esferalia.aon.entity.master.AllotmentDB;

@Entity
@Table(name="allotment")
public class Allotment extends AllotmentDB implements IAuditable {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	public Allotment() {
		setActive(true);
	}

	@Transient
	public boolean isGroup() {
		return getAgencyGroup() != null && getAgencyGroup().getId() != null;
	}

	@Transient
	public List<ITransferObject> getAllotmentItems() throws ManagerBeanException {
		IManagerBean allotmentItemBean = BeanManager.getManagerBean(AllotmentItem.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(allotmentItemBean.getFieldName(IEntityAlias.ALLOTMENT_ITEM_ALLOTMENT_ID), getId());
		criteria.addOrder(allotmentItemBean.getFieldName(IEntityAlias.ALLOTMENT_ITEM_ITEM_PRODUCT_CODE));
		return allotmentItemBean.getList(criteria);
	}

	@Transient
	public String getRoomTypeCodes() throws ManagerBeanException {
		StringBuffer roomTypeCodes = new StringBuffer();
		for (ITransferObject ito : getAllotmentItems()) {
			AllotmentItem allotmentItem = (AllotmentItem)ito;
			roomTypeCodes.append(allotmentItem.getItem().getProduct().getCode());
			roomTypeCodes.append(" ");
		}
		return roomTypeCodes.length() > 0 ? roomTypeCodes.toString() : "TODAS"; 
	}

	@Transient
	public List<ITransferObject> getAllotmentTariffs() throws ManagerBeanException {
		IManagerBean allotmentTariffBean = BeanManager.getManagerBean(AllotmentTariff.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(allotmentTariffBean.getFieldName(IEntityAlias.ALLOTMENT_TARIFF_ALLOTMENT_ID), getId());
		criteria.addOrder(allotmentTariffBean.getFieldName(IEntityAlias.ALLOTMENT_TARIFF_TARIFF_CODE));
		return allotmentTariffBean.getList(criteria);
	}

	@Transient
	public String getTariffCodes() throws ManagerBeanException {
		StringBuffer tariffCodes = new StringBuffer();
		for (ITransferObject ito : getAllotmentTariffs()) {
			AllotmentTariff allotmentTariff = (AllotmentTariff)ito;
			tariffCodes.append(allotmentTariff.getTariff().getCode());
			tariffCodes.append(" ");
		}
		return tariffCodes.length() > 0 ? tariffCodes.toString() : "TODAS";
	}

}
