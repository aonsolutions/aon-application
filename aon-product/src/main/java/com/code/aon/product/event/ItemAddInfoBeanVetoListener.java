package com.code.aon.product.event;

import com.code.aon.AonVersion;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.product.ItemAddInfo;

public class ItemAddInfoBeanVetoListener extends ManagerBeanVetoListenerAdapter {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Override
	public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		ItemAddInfo itemAddInfo = (ItemAddInfo)evt.getTo();
		itemAddInfo.setProduct(itemAddInfo.getItem().getProduct());
	}

	@Override
	public void vetoableBeanUpdated(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		ItemAddInfo itemAddInfo = (ItemAddInfo)evt.getTo();
		itemAddInfo.setProduct(itemAddInfo.getItem().getProduct());
	}

}