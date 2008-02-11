package com.code.aon.ui.cms.velocity.attribute;

import java.util.ArrayList;
import java.util.List;

import com.code.aon.cms.DirectAccess;
import com.code.aon.cms.DirectAccessDetail;
import com.code.aon.cms.DirectAccessGroupDetail;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.cms.util.ControllerUtil;
import com.code.aon.ui.cms.velocity.DirectAccessGenerator;

public class DirectAccessGroupHandler {

	private int groupId;
	
	private String label;
	
	private ArrayList<DirectAccessHandler> list;
	
	public DirectAccessGroupHandler (DirectAccessGroupDetail group) {
		groupId = group.getDirectAccessGroup().getId();
		label = group.getLabel();
	}


	public String getLabel() {
		return label;
	}


	public ArrayList<DirectAccessHandler> getList() {
		if (list == null)
			list = this.getDirectAccessList(groupId);
		return list;
	}

	private ArrayList<DirectAccessHandler> getDirectAccessList(int directAccessGroupId) {
		ArrayList<DirectAccessHandler> list = new ArrayList<DirectAccessHandler>();
		List<ITransferObject> directAccessList;
		List<ITransferObject> directAccessDetailList;
		try {
			IManagerBean bean = BeanManager.getManagerBean(DirectAccess.class);
			IManagerBean detailBean = BeanManager.getManagerBean(DirectAccessDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(ICMSAlias.DIRECT_ACCESS_DIRECT_ACCESS_GROUP_ID), directAccessGroupId);
			criteria.addEqualExpression(bean.getFieldName(ICMSAlias.DIRECT_ACCESS_ACTIVE), true);
			criteria.addOrder(bean.getFieldName(ICMSAlias.DIRECT_ACCESS_POSITION));
			directAccessList = (List<ITransferObject>)bean.getList(criteria);
			DirectAccess da;
			DirectAccessDetail detail;
			Criteria criteria_detail;
			for (int i = 0; i < directAccessList.size(); i++) {
				da = (DirectAccess)directAccessList.get(i);
				criteria_detail = new Criteria();
				criteria_detail.addEqualExpression(detailBean.getFieldName(ICMSAlias.DIRECT_ACCESS_DETAIL_DIRECT_ACCESS_ID), da.getId());
				criteria_detail.addEqualExpression(detailBean.getFieldName(ICMSAlias.DIRECT_ACCESS_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
				directAccessDetailList = (List<ITransferObject>)detailBean.getList(criteria_detail);
				if (directAccessDetailList.size() > 0) {
					detail = (DirectAccessDetail)directAccessDetailList.get(0);
					DirectAccessHandler handler = new DirectAccessHandler(detail);
					if (handler.getUrl() != null) list.add(handler);
				}
			}
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		} finally {
			directAccessList = null;
			directAccessDetailList = null;
		}
		
		return list;
	}
	

}
