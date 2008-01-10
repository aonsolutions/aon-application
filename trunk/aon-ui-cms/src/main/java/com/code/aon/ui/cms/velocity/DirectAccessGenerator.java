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
	
	public static ArrayList<DirectAccessHandler> getDirectAccessList(DirectAccessGroupDetail groupDetail) {
		ArrayList<DirectAccessHandler> list = new ArrayList<DirectAccessHandler>();

		try {
			IManagerBean bean = BeanManager.getManagerBean(DirectAccess.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(ICMSAlias.DIRECT_ACCESS_DIRECT_ACCESS_GROUP_ID), groupDetail.getDirectAccessGroup().getId());
			criteria.addEqualExpression(bean.getFieldName(ICMSAlias.DIRECT_ACCESS_ACTIVE), true);
			criteria.addOrder(bean.getFieldName(ICMSAlias.DIRECT_ACCESS_POSITION));
			List<ITransferObject> l = (List<ITransferObject>)bean.getList(criteria);
			for (int i = 0; i < l.size(); i++) {
				DirectAccess da = (DirectAccess)l.get(i);
				IManagerBean detailBean = BeanManager.getManagerBean(DirectAccessDetail.class);
				Criteria criteria_detail = new Criteria();
				criteria_detail.addEqualExpression(detailBean.getFieldName(ICMSAlias.DIRECT_ACCESS_DETAIL_DIRECT_ACCESS_ID), da.getId());
				criteria_detail.addEqualExpression(detailBean.getFieldName(ICMSAlias.DIRECT_ACCESS_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
				List<ITransferObject> ld = (List<ITransferObject>)detailBean.getList(criteria_detail);
				if (ld.size() > 0) {
					DirectAccessDetail detail = (DirectAccessDetail)ld.get(0);
					DirectAccessHandler handler = new DirectAccessHandler(detail);
					if (handler.getUrl() != null) list.add(handler);
				}
			}
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		}
		
		return list;
	}
	
	public static void generate(VelocityUtil vu) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(DirectAccessGroup.class);
			List<ITransferObject> l = (List<ITransferObject>)bean.getList(null);
			for (int i=0; i < l.size(); i++) {
				DirectAccessGroup group = (DirectAccessGroup)l.get(i);
				IManagerBean detailBean = BeanManager.getManagerBean(DirectAccessGroupDetail.class);
				Criteria detailCriteria = new Criteria();
				detailCriteria.addEqualExpression(detailBean.getFieldName(ICMSAlias.DIRECT_ACCESS_GROUP_DETAIL_DIRECT_ACCESS_GROUP_ID), group.getId());
				detailCriteria.addEqualExpression(detailBean.getFieldName(ICMSAlias.DIRECT_ACCESS_GROUP_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
				List<ITransferObject> detailList = (List<ITransferObject>)detailBean.getList(detailCriteria);
				if (detailList.size() > 0) {
					DirectAccessGroupDetail detail = (DirectAccessGroupDetail)detailList.get(0);
					ArrayList<DirectAccessHandler> accessList = getDirectAccessList(detail);
					if (accessList != null && accessList.size() > 0) {
						vu.put("direct_access_group", detail);
						vu.put("direct_access_list", accessList);
						vu.addMessage(" Generando accesos directos " + group.getAlias() + ".", VelocityUtil.INFO);
						CommonGenerator.chargeContext(vu, group.getSection());
						generate(vu, Templates.DIRECT_ACCESS, group.getAlias());
						vu.remove("direct_access_list");
					}
				}
			}
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		}
	}
	
	public static Object getDirectAccessHandler(Integer ident) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(DirectAccessGroupDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(ICMSAlias.DIRECT_ACCESS_GROUP_DETAIL_ID), ident);
			List<ITransferObject> l = (List<ITransferObject>)bean.getList(criteria);
			if  (l.size()>0) {
				DirectAccessGroupDetail groupDetail = (DirectAccessGroupDetail)l.get(0);
				DirectAccessGroupHandler h = new DirectAccessGroupHandler(groupDetail);
				return h;
			}
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		}
		return null;
	}

}
