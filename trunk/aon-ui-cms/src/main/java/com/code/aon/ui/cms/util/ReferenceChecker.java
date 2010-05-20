package com.code.aon.ui.cms.util;

import java.io.Serializable;

import com.code.aon.cms.DirectAccess;
import com.code.aon.cms.ModularPageOption;
import com.code.aon.cms.SidebarOption;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.cms.enumeration.ContentLevel;
import com.code.aon.cms.enumeration.ModularPageOptionType;
import com.code.aon.cms.enumeration.PageType;
import com.code.aon.cms.enumeration.SidebarType;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;

public class ReferenceChecker {
	
	private static void addEnumToCriteria( Criteria criteria, String alias, Object[] values ) throws ManagerBeanException {
		Expression expToAdd = null;
		for( Object value : values ) {
			if ( value != null ) {
				if ( expToAdd == null ) {
					expToAdd = ExpressionUtilities.getEqualExpression(alias, value);				
				} else {
					Expression exp  = ExpressionUtilities.getEqualExpression(alias, value);
					expToAdd = ExpressionUtilities.getOrExpression(expToAdd, exp);
				}
			}
		}
		if ( expToAdd != null ) {
			criteria.addExpression(expToAdd);
		}
	}		

	public static boolean isInSideBar( Serializable id, SidebarType ... type ) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(SidebarOption.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(ICMSAlias.SIDEBAR_OPTION_IDENT), id);
		addEnumToCriteria(criteria, bean.getFieldName(ICMSAlias.SIDEBAR_OPTION_TYPE), type);
		int count = bean.getCount(criteria);
		return count > 0;
	}
	
	public static boolean isInModulaPage( Serializable id, ModularPageOptionType ... type ) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(ModularPageOption.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(ICMSAlias.MODULAR_PAGE_OPTION_IDENT), id);
		addEnumToCriteria(criteria, bean.getFieldName(ICMSAlias.MODULAR_PAGE_OPTION_TYPE), type);
		int count = bean.getCount(criteria);
		return count > 0;
	}
	
	public static boolean isInDirectAccess( Serializable id, PageType ... type ) throws ManagerBeanException {
		return isInDirectAccess(id, null, type);
	}
	
	public static boolean isInDirectAccess( Serializable id, ContentLevel level, PageType ... type ) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(DirectAccess.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(ICMSAlias.DIRECT_ACCESS_IDENT), id);
		addEnumToCriteria(criteria, bean.getFieldName(ICMSAlias.DIRECT_ACCESS_TYPE), type);
		if ( level != null ) {
			criteria.addEqualExpression(bean.getFieldName(ICMSAlias.DIRECT_ACCESS_LEVEL), level);
		}
		int count = bean.getCount(criteria);
		return count > 0;
	}

}