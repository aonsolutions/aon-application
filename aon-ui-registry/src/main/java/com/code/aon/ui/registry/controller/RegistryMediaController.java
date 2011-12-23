package com.code.aon.ui.registry.controller;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import javax.faces.model.SelectItem;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.ui.form.LinesController;
import com.esferalia.aon.entity.IEntityAlias;

public class RegistryMediaController extends LinesController {
	
	public List<SelectItem> getAddresses() {
		List<SelectItem> addresses = new LinkedList<SelectItem>();
		try {
			ITransferObject master = getMasterController().getTo();
			if (master != null) {
				Serializable masterId = getMasterController().getManagerBean().getId(master);	
				IManagerBean bean = BeanManager.getManagerBean(RegistryAddress.class);
				Criteria c = new Criteria();
				c.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_ADDRESS_REGISTRY_ID), masterId);
				c.addOrder(bean.getFieldName(IEntityAlias.REGISTRY_ADDRESS_ADDRESS_TYPE));
				List<ITransferObject> list = bean.getList(c);
				for (ITransferObject to: list) {
					RegistryAddress address = (RegistryAddress) to;
					addresses.add(new SelectItem(address,address.getShortAddress()));
				}
			}
		} catch (ManagerBeanException e) {
			// Nada. Devuelve la colleción vacia.
		}
		return addresses;
	}

}
