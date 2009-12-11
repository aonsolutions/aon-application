package com.code.aon.ui.cms.velocity;

import java.util.ArrayList;
import java.util.List;

import com.code.aon.cms.DirectAccessGroup;
import com.code.aon.cms.DirectAccess;
import com.code.aon.cms.DirectAccessDetail;
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
	
	public static void generate() {
		generate(null);
	}
	
	public static void generate(DirectAccessGroup selectedCategory) {
		VelocityUtil vu = new VelocityUtil();
		CommonGenerator.getCommonGenerator().init(vu);
		vu.setTemplate_path(ControllerUtil.getCurrentVmTemplatePath());
		vu.initialize();
		
		List<ITransferObject> directAccessGroupList;
		List<ITransferObject> directAccessGroupDetailList;
		ArrayList<DirectAccessHandler> directAccessHandlerList;
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
					VelocityUtil.addMessage("La categoria de accesos directos " + group.getAlias() + " no esta internacionalizada.", VelocityUtil.WARN);
				}else{
					detail = (DirectAccessGroupDetail)directAccessGroupDetailList.get(0);
					dagh = new DirectAccessGroupHandler(detail,getDirectAccessList(group));
					vu.put("direct_access_group", dagh);
					VelocityUtil.addMessage(" Generando accesos directos " + group.getAlias() + ".", VelocityUtil.INFO);
					CommonGenerator.getCommonGenerator().chargeContext(vu, group.getSection());
					generate(vu, Templates.DIRECT_ACCESS, group.getAlias());
					vu.remove("direct_access_group");
				}
			}
		} catch (ManagerBeanException e) {
			VelocityUtil.addMessage(e.getMessage(), VelocityUtil.ERROR);;
		} finally {
			directAccessGroupList = null;
			directAccessGroupDetailList = null;
			directAccessHandlerList = null;
		}
		vu.finalize();
		vu = null;
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
				VelocityUtil.addMessage("La categoria de accesos directos " + group.getAlias() + " no tiene accesos directos.", VelocityUtil.WARN);
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
					VelocityUtil.addMessage("El acceso directo " + da.getAlias() + " no esta internacionalizado.", VelocityUtil.WARN);
				}else{
					detail = (DirectAccessDetail)directAccessDetailList.get(0);
					DirectAccessHandler handler = new DirectAccessHandler(detail);
					if (handler.getUrl() != null) list.add(handler);
				}
			}
		} catch (ManagerBeanException e) {
			VelocityUtil.addMessage(e.getMessage(), VelocityUtil.ERROR);;
		} finally {
			directAccessList = null;
			directAccessDetailList = null;
		}
		
		return list;
	}
	
	public static Object getDirectAccessHandler(Integer ident) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(DirectAccess.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(ICMSAlias.DIRECT_ACCESS_ID), ident);
			List<ITransferObject> l = (List<ITransferObject>)bean.getList(criteria);
			if (l.isEmpty()){
				VelocityUtil.addMessage("ACCESO DIRECTO "+ident+" REFERENCIADO NO EXISTE !!!", VelocityUtil.WARN);
				return null;
			}
			DirectAccess a = (DirectAccess)l.get(0);
			if (a.isActive()) {
				IManagerBean beanDetail = BeanManager.getManagerBean(DirectAccessDetail.class);
				criteria = new Criteria();
				criteria.addEqualExpression(beanDetail.getFieldName(ICMSAlias.DIRECT_ACCESS_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
				criteria.addEqualExpression(beanDetail.getFieldName(ICMSAlias.DIRECT_ACCESS_DETAIL_DIRECT_ACCESS_ID), ident);
				List<ITransferObject> ld = (List<ITransferObject>)beanDetail.getList(criteria);
				if (ld.isEmpty()){
					VelocityUtil.addMessage("El acceso directo " + a.getAlias() + " no esta internacionalizado.", VelocityUtil.WARN);
				}else{
					DirectAccessDetail ad = (DirectAccessDetail)ld.get(0);
					DirectAccessHandler ah = new DirectAccessHandler(ad);
					return ah;
				}
			}
		} catch (ManagerBeanException e) {
			VelocityUtil.addMessage(e.getMessage(), VelocityUtil.ERROR);;
		}
		return null;
	}

	public static Object getDirectAccessGroupHandler(Integer ident) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(DirectAccessGroup.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(ICMSAlias.DIRECT_ACCESS_GROUP_ID), ident);
			List<ITransferObject> l = (List<ITransferObject>)bean.getList(criteria);
			if (l.isEmpty()){
				VelocityUtil.addMessage("GRUPO DE ACCESO DIRECTO "+ident+" REFERENCIADO NO EXISTE !!!", VelocityUtil.WARN);
				return null;
			}
			DirectAccessGroup a = (DirectAccessGroup)l.get(0);
			if (a.isActive()) {
				IManagerBean beanDetail = BeanManager.getManagerBean(DirectAccessGroupDetail.class);
				criteria = new Criteria();
				criteria.addEqualExpression(beanDetail.getFieldName(ICMSAlias.DIRECT_ACCESS_GROUP_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
				criteria.addEqualExpression(beanDetail.getFieldName(ICMSAlias.DIRECT_ACCESS_GROUP_DETAIL_DIRECT_ACCESS_GROUP_ID), ident);
				List<ITransferObject> ld = (List<ITransferObject>)beanDetail.getList(criteria);
				if (ld.isEmpty()){
					VelocityUtil.addMessage("El grupo de accesos directos " + a.getAlias() + " no esta internacionalizado.", VelocityUtil.WARN);
				}else{
					DirectAccessGroupDetail ad = (DirectAccessGroupDetail)ld.get(0);
					DirectAccessGroupHandler ah = new DirectAccessGroupHandler(ad,getDirectAccessList(a));
					return ah;
				}
			}
		} catch (ManagerBeanException e) {
			VelocityUtil.addMessage(e.getMessage(), VelocityUtil.ERROR);;
		}
		return null;
	}

}
