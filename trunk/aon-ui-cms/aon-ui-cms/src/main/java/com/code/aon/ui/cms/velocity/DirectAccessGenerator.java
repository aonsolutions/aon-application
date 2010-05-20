package com.code.aon.ui.cms.velocity;

import java.util.ArrayList;
import java.util.List;

import com.code.aon.cms.DirectAccess;
import com.code.aon.cms.DirectAccessDetail;
import com.code.aon.cms.DirectAccessGroup;
import com.code.aon.cms.DirectAccessGroupDetail;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.cms.enumeration.Templates;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.cms.util.ControllerUtil;
import com.code.aon.ui.cms.util.VelocityUtil;
import com.code.aon.ui.cms.velocity.attribute.DirectAccessGroupHandler;
import com.code.aon.ui.cms.velocity.attribute.DirectAccessHandler;

public class DirectAccessGenerator extends Generator {
	
	public void generate() {
		generate(null);
	}
	
	public void generate(DirectAccessGroup selectedCategory) {
		VelocityUtil vu = context.initVelocityUtil();		
		
		List<ITransferObject> directAccessGroupList;
		List<ITransferObject> directAccessGroupDetailList;
		try {
			IManagerBean bean = BeanManager.getManagerBean(DirectAccessGroup.class);
			IManagerBean detailBean = BeanManager.getManagerBean(DirectAccessGroupDetail.class);
			Criteria criteria = null;
			if (selectedCategory!=null){
				criteria = new Criteria();
				criteria.addEqualExpression(bean.getFieldName(ICMSAlias.DIRECT_ACCESS_GROUP_ID), selectedCategory.getId());
			}
			directAccessGroupList = (List<ITransferObject>)bean.getList(criteria);
			DirectAccessGroup group;
			DirectAccessGroupDetail detail;
			Criteria detailCriteria;
			DirectAccessGroupHandler dagh;
			for (int i=0; i < directAccessGroupList.size(); i++) {
				group = (DirectAccessGroup)directAccessGroupList.get(i);
				detailCriteria = new Criteria();
				detailCriteria.addEqualExpression(detailBean.getFieldName(ICMSAlias.DIRECT_ACCESS_GROUP_DETAIL_DIRECT_ACCESS_GROUP_ID), group.getId());
				detailCriteria.addEqualExpression(detailBean.getFieldName(ICMSAlias.DIRECT_ACCESS_GROUP_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
				directAccessGroupDetailList = (List<ITransferObject>)detailBean.getList(detailCriteria);
				if (directAccessGroupDetailList.isEmpty()){
					logger.warning("La categoria de accesos directos " + group.getAlias() + " no esta internacionalizada.");
				}else{
					detail = (DirectAccessGroupDetail)directAccessGroupDetailList.get(0);
					dagh = new DirectAccessGroupHandler(detail,getDirectAccessList(group));
					vu.put(DIRECT_ACCESS_GROUP_KEY, dagh);
					logger.info(" Generando accesos directos " + group.getAlias() + ".");
					context.changeSection(vu, group.getSection());
					generate(vu, Templates.DIRECT_ACCESS, group.getAlias());
					vu.remove(DIRECT_ACCESS_GROUP_KEY);
				}
			}
		} catch (ManagerBeanException e) {
			logger.error(e.getMessage());
		} finally {
			directAccessGroupList = null;
			directAccessGroupDetailList = null;
		}
	}
	
	private static ArrayList<DirectAccessHandler> getDirectAccessList(DirectAccessGroup group) {
		ArrayList<DirectAccessHandler> list = new ArrayList<DirectAccessHandler>();
		List<ITransferObject> directAccessList;
		List<ITransferObject> directAccessDetailList;
		try {
			IManagerBean bean = BeanManager.getManagerBean(DirectAccess.class);
			IManagerBean detailBean = BeanManager.getManagerBean(DirectAccessDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(ICMSAlias.DIRECT_ACCESS_DIRECT_ACCESS_GROUP_ID), group.getId());
			criteria.addEqualExpression(bean.getFieldName(ICMSAlias.DIRECT_ACCESS_ACTIVE), true);
			criteria.addOrder(bean.getFieldName(ICMSAlias.DIRECT_ACCESS_POSITION));
			directAccessList = (List<ITransferObject>)bean.getList(criteria);
			if (directAccessList.isEmpty())
				getLogger().warning("La categoria de accesos directos " + group.getAlias() + " no tiene accesos directos.");
			DirectAccess da;
			DirectAccessDetail detail;
			Criteria criteria_detail;
			for (int i = 0; i < directAccessList.size(); i++) {
				da = (DirectAccess)directAccessList.get(i);
				criteria_detail = new Criteria();
				criteria_detail.addEqualExpression(detailBean.getFieldName(ICMSAlias.DIRECT_ACCESS_DETAIL_DIRECT_ACCESS_ID), da.getId());
				criteria_detail.addEqualExpression(detailBean.getFieldName(ICMSAlias.DIRECT_ACCESS_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
				directAccessDetailList = (List<ITransferObject>)detailBean.getList(criteria_detail);
				if (directAccessDetailList.isEmpty()) {
					getLogger().warning("El acceso directo " + da.getAlias() + " no esta internacionalizado.");
				}else{
					detail = (DirectAccessDetail)directAccessDetailList.get(0);
					DirectAccessHandler handler = new DirectAccessHandler(detail);
					if (handler.getUrl() != null) list.add(handler);
				}
			}
		} catch (ManagerBeanException e) {
			getLogger().error(e.getMessage());
		} finally {
			directAccessList = null;
			directAccessDetailList = null;
		}
		
		return list;
	}
	
	public static Object getDirectAccessHandler(Integer ident, String message) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(DirectAccess.class);
			DirectAccess a = (DirectAccess) bean.get(ident);
			if (a == null){
				getLogger().error( message + " REFERENCIA A UN ACCESO DIRECTO ("+ident+") INEXISTENTE");
				return null;
			}
			if (a.isActive()) {
				IManagerBean beanDetail = BeanManager.getManagerBean(DirectAccessDetail.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(beanDetail.getFieldName(ICMSAlias.DIRECT_ACCESS_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
				criteria.addEqualExpression(beanDetail.getFieldName(ICMSAlias.DIRECT_ACCESS_DETAIL_DIRECT_ACCESS_ID), ident);
				List<ITransferObject> ld = (List<ITransferObject>)beanDetail.getList(criteria);
				if (ld.isEmpty()){
					getLogger().warning("El acceso directo " + a.getAlias() + " no esta internacionalizado.");
				}else{
					DirectAccessDetail ad = (DirectAccessDetail)ld.get(0);
					DirectAccessHandler ah = new DirectAccessHandler(ad);
					return ah;
				}
			}
		} catch (ManagerBeanException e) {
			getLogger().error(e.getMessage());
		}
		return null;
	}

	public static Object getDirectAccessGroupHandler(Integer ident, String message) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(DirectAccessGroup.class);
			DirectAccessGroup a = (DirectAccessGroup) bean.get(ident);
			if (a == null){
				getLogger().error( message + " REFERENCIA A UN GRUPO DE ACCESO DIRECTO ("+ident+") INEXISTENTE");
				return null;
			}
			if (a.isActive()) {
				IManagerBean beanDetail = BeanManager.getManagerBean(DirectAccessGroupDetail.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(beanDetail.getFieldName(ICMSAlias.DIRECT_ACCESS_GROUP_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
				criteria.addEqualExpression(beanDetail.getFieldName(ICMSAlias.DIRECT_ACCESS_GROUP_DETAIL_DIRECT_ACCESS_GROUP_ID), ident);
				List<ITransferObject> ld = (List<ITransferObject>)beanDetail.getList(criteria);
				if (ld.isEmpty()){
					getLogger().warning("El grupo de accesos directos " + a.getAlias() + " no esta internacionalizado.");
				}else{
					DirectAccessGroupDetail ad = (DirectAccessGroupDetail)ld.get(0);
					DirectAccessGroupHandler ah = new DirectAccessGroupHandler(ad,getDirectAccessList(a));
					return ah;
				}
			}
		} catch (ManagerBeanException e) {
			getLogger().error(e.getMessage());
		}
		return null;
	}

}
