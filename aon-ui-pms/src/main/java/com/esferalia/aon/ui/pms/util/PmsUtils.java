package com.esferalia.aon.ui.pms.util;

import java.util.LinkedList;
import java.util.List;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAddInfo;
import com.code.aon.ui.config.util.UserUtils;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.ui.pms.controller.IPmsConstants;

public class PmsUtils implements IPmsConstants {

	public static boolean isAgencyUser() throws ManagerBeanException {
		IManagerBean rAddInfoBean = BeanManager.getManagerBean(RegistryAddInfo.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(rAddInfoBean.getFieldName(IEntityAlias.REGISTRY_ADD_INFO_ATTRIBUTE), REQUEST_USER);
		criteria.addEqualExpression(rAddInfoBean.getFieldName(IEntityAlias.REGISTRY_ADD_INFO_VALUE), UserUtils.getInstance().getLoggedUser().getLogin());
		return rAddInfoBean.getCount(criteria) > 0;
	}

	public static List<Integer> getUserAgencies() throws ManagerBeanException {
		List<Integer> userAgencies = new LinkedList<Integer>();
		IManagerBean rAddInfoBean = BeanManager.getManagerBean(RegistryAddInfo.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(rAddInfoBean.getFieldName(IEntityAlias.REGISTRY_ADD_INFO_ATTRIBUTE), REQUEST_USER);
		criteria.addEqualExpression(rAddInfoBean.getFieldName(IEntityAlias.REGISTRY_ADD_INFO_VALUE), UserUtils.getInstance().getLoggedUser().getLogin());
		for (ITransferObject ito : rAddInfoBean.getList(criteria)) {
			RegistryAddInfo rAddInfo = (RegistryAddInfo)ito;
			userAgencies.add(rAddInfo.getRegistry().getId());
		}
		return userAgencies;
	}

}
