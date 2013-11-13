package com.code.aon.ui.registry.controller;

import java.util.LinkedList;
import java.util.List;

import javax.faces.model.SelectItem;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ql.ProjectionList;
import com.code.aon.registry.RegistryAddInfo;
import com.code.aon.ui.form.BasicController;
import com.esferalia.aon.entity.IEntityAlias;

public class RegistryAddInfoController extends BasicController {

	public List<SelectItem> getAddInfoAttributes() throws ManagerBeanException {
		List<SelectItem> attributes = new LinkedList<SelectItem>();
		IManagerBean rAddInfoBean = BeanManager.getManagerBean(RegistryAddInfo.class);
		Criteria criteria = new Criteria();
		criteria.addOrder(rAddInfoBean.getFieldName(IEntityAlias.REGISTRY_ADD_INFO_ATTRIBUTE));
		Projection projection = Projection.group(rAddInfoBean.getFieldName(IEntityAlias.REGISTRY_ADD_INFO_ATTRIBUTE));
		for (Object obj : rAddInfoBean.getList(new ProjectionList(projection), null)) {
			String attribute = (String)obj;
			attributes.add(new SelectItem(attribute, attribute));
		}
		return attributes;
	}

}