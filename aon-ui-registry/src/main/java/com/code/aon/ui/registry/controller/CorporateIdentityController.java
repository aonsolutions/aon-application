package com.code.aon.ui.registry.controller;

import java.io.Serializable;

import javax.faces.event.ValueChangeEvent;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.registry.controller.RegistryAttachController;

public class CorporateIdentityController extends RegistryAttachController {
	
	public void addCategoryEqualExpression(ValueChangeEvent event) throws ManagerBeanException {
		if (event.getNewValue() != null) {
			ITransferObject to = (ITransferObject) event.getNewValue();
			String fieldName = resolveAlias("RegistryAttachment_category<id");
			IManagerBean bean = BeanManager.getManagerBean(to.getClass());
			Serializable id = bean.getId(to);
			getCriteria().addEqualExpression(fieldName, id);
		}
	}		

}
