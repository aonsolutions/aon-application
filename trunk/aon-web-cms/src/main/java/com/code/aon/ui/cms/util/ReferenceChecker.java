package com.code.aon.ui.cms.util;

import java.io.Serializable;

import com.code.aon.cms.DirectAccess;
import com.code.aon.cms.ModularPageOption;
import com.code.aon.cms.SidebarOption;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.cms.enumeration.ModularPageOptionType;
import com.code.aon.cms.enumeration.PageType;
import com.code.aon.cms.enumeration.SidebarType;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;

public class ReferenceChecker {

	private static boolean isUsed( IManagerBean bean, String identAlias, String typeAlias, Serializable id, Object type ) throws ManagerBeanException {
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(identAlias, id);
		criteria.addEqualExpression(typeAlias, type);
		int count = bean.getCount(criteria);
		return (count > 0);
	}

	public static boolean isInSideBar( Serializable id, SidebarType type ) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(SidebarOption.class);
		String identAlias = bean.getFieldName(ICMSAlias.SIDEBAR_OPTION_IDENT);
		String typeAlias = bean.getFieldName(ICMSAlias.SIDEBAR_OPTION_TYPE);
		return isUsed(bean, identAlias, typeAlias, id, type );
	}
	
	public static boolean isInModulaPage( Serializable id, ModularPageOptionType type ) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(ModularPageOption.class);
		String identAlias = bean.getFieldName(ICMSAlias.MODULAR_PAGE_OPTION_IDENT);
		String typeAlias = bean.getFieldName(ICMSAlias.MODULAR_PAGE_OPTION_TYPE);
		return isUsed(bean, identAlias, typeAlias, id, type );
	}

	public static boolean isInDirectAccess( Serializable id, PageType type ) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(DirectAccess.class);
		String identAlias = bean.getFieldName(ICMSAlias.DIRECT_ACCESS_IDENT);
		String typeAlias = bean.getFieldName(ICMSAlias.DIRECT_ACCESS_TYPE);
		return isUsed(bean, identAlias, typeAlias, id, type );
	}

}
