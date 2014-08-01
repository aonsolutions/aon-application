package com.code.aon.ui.seller.event;

import org.apache.commons.lang.ArrayUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.seller.enumeration.SellerStatus;
import com.code.aon.ui.registry.controller.event.RegistrySearchListener;
import com.esferalia.aon.entity.IEntityAlias;

public class SellerSearchListener extends RegistrySearchListener {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private SellerStatus[] sellerStatuses;
	
	public SellerStatus[] getSellerStatuses() {
		return sellerStatuses;
	}

	public void setSellerStatuses(SellerStatus[] sellerStatuses) {
		this.sellerStatuses = sellerStatuses;
	}
	
	@Override
	protected void init() throws ManagerBeanException {
		SellerStatus[] defaultSellerStatus = {SellerStatus.ACTIVE};
		setSellerStatuses(defaultSellerStatus);
		super.init();
	}
	
	@Override
	protected void completeCriteria(Criteria criteria) throws ManagerBeanException, ExpressionException {
		if (!ArrayUtils.isEmpty(getSellerStatuses())) {
			String status = getController().resolveAlias(IEntityAlias.SELLER_STATUS);
			addEnumToCriteria(criteria, status, getSellerStatuses());
		}
		super.completeCriteria(criteria);
	}

}