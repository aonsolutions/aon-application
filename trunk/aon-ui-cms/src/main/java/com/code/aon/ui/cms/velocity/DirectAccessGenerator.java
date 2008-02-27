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
		VelocityUtil vu = new VelocityUtil();
		CommonGenerator.getCommonGenerator().init(vu);
		vu.addMessage("Iniciando proceso de generación", VelocityUtil.INFO);
		vu.addMessage("", VelocityUtil.INFO);
		vu.addMessage("Buscando plantilla seleccionada '" + ControllerUtil.getCurrentConfig().getTemplate() + "' ...", VelocityUtil.INFO);
		vu.setTemplate_path(ControllerUtil.getCurrentVmTemplatePath());
		vu.initialize();
		vu.addMessage("", VelocityUtil.INFO);
		vu.addMessage("Creando acceso directo... ", VelocityUtil.INFO);
		
		List<ITransferObject> directAccessGroupList;
		List<ITransferObject> directAccessGroupDetailList;
		ArrayList<DirectAccessHandler> directAccessHandlerList;
		try {
			IManagerBean bean = BeanManager.getManagerBean(DirectAccessGroup.class);
			IManagerBean detailBean = BeanManager.getManagerBean(DirectAccessGroupDetail.class);
			directAccessGroupList = (List<ITransferObject>)bean.getList(null);
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
				if (directAccessGroupDetailList.size() > 0) {
					detail = (DirectAccessGroupDetail)directAccessGroupDetailList.get(0);
					dagh = new DirectAccessGroupHandler(detail);
					vu.put("direct_access_group", dagh);
					vu.addMessage(" Generando accesos directos " + group.getAlias() + ".", VelocityUtil.INFO);
					CommonGenerator.getCommonGenerator().chargeContext(vu, group.getSection());
					generate(vu, Templates.DIRECT_ACCESS, group.getAlias());
					vu.remove("direct_access_group");
				}
			}
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		} finally {
			directAccessGroupList = null;
			directAccessGroupDetailList = null;
			directAccessHandlerList = null;
		}
		vu.finalize();
		vu = null;
	}
	
	public static Object getDirectAccessHandler(Integer ident) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(DirectAccess.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(ICMSAlias.DIRECT_ACCESS_ID), ident);
			List<ITransferObject> l = (List<ITransferObject>)bean.getList(criteria);
			
			DirectAccess a = (DirectAccess)l.get(0);
			if (a.isActive()) {
				IManagerBean beanDetail = BeanManager.getManagerBean(DirectAccessDetail.class);
				criteria = new Criteria();
				criteria.addEqualExpression(beanDetail.getFieldName(ICMSAlias.DIRECT_ACCESS_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
				criteria.addEqualExpression(beanDetail.getFieldName(ICMSAlias.DIRECT_ACCESS_DETAIL_DIRECT_ACCESS_ID), ident);
				List<ITransferObject> ld = (List<ITransferObject>)beanDetail.getList(criteria);
				DirectAccessDetail ad = (DirectAccessDetail)ld.get(0);
				DirectAccessHandler ah = new DirectAccessHandler(ad);
				return ah;
			}
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Object getDirectAccessGroupHandler(Integer ident) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(DirectAccessGroup.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(ICMSAlias.DIRECT_ACCESS_GROUP_ID), ident);
			List<ITransferObject> l = (List<ITransferObject>)bean.getList(criteria);
			
			DirectAccessGroup a = (DirectAccessGroup)l.get(0);
			if (a.isActive()) {
				IManagerBean beanDetail = BeanManager.getManagerBean(DirectAccessGroupDetail.class);
				criteria = new Criteria();
				criteria.addEqualExpression(beanDetail.getFieldName(ICMSAlias.DIRECT_ACCESS_GROUP_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
				criteria.addEqualExpression(beanDetail.getFieldName(ICMSAlias.DIRECT_ACCESS_GROUP_DETAIL_DIRECT_ACCESS_GROUP_ID), ident);
				List<ITransferObject> ld = (List<ITransferObject>)beanDetail.getList(criteria);
				DirectAccessGroupDetail ad = (DirectAccessGroupDetail)ld.get(0);
				DirectAccessGroupHandler ah = new DirectAccessGroupHandler(ad);
				return ah;
			}
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		}
		return null;
	}

}
