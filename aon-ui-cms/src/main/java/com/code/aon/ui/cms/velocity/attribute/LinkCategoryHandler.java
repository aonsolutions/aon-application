package com.code.aon.ui.cms.velocity.attribute;

import java.util.ArrayList;
import java.util.List;

import com.code.aon.cms.Link;
import com.code.aon.cms.LinkCategory;
import com.code.aon.cms.LinkCategoryDetail;
import com.code.aon.cms.LinkDetail;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.cms.util.ControllerUtil;

public class LinkCategoryHandler {

	private String label;
	
	private ArrayList<LinkHandler> list;
	
	public LinkCategoryHandler (LinkCategoryDetail lcd) {
		label = lcd.getLabel();
		list = getlinkList(lcd.getLinkCategory());
	}

	public String getLabel() {
		return label;
	}

	public ArrayList<LinkHandler> getList() {
		return list;
	}

	private ArrayList<LinkHandler> getlinkList(LinkCategory lc) {
		ArrayList<LinkHandler> list = new ArrayList<LinkHandler>();
		try {
			IManagerBean bean = BeanManager.getManagerBean(Link.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(ICMSAlias.LINK_LINK_CATEGORY_ID), lc.getId());
			criteria.addEqualExpression(bean.getFieldName(ICMSAlias.LINK_ACTIVE), true);
			criteria.addOrder(bean.getFieldName(ICMSAlias.LINK_POSITION));
			List<ITransferObject> linkList = (List<ITransferObject>)bean.getList(criteria);
			for (int i = 0; i < linkList.size(); i++) {
				Link l = (Link)linkList.get(i);
				IManagerBean detailBean = BeanManager.getManagerBean(LinkDetail.class);
				Criteria detailCriteria = new Criteria();
				detailCriteria.addEqualExpression(detailBean.getFieldName(ICMSAlias.LINK_DETAIL_LINK_ID), l.getId());
				detailCriteria.addEqualExpression(detailBean.getFieldName(ICMSAlias.LINK_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
				List<ITransferObject> ld = (List<ITransferObject>)detailBean.getList(detailCriteria);
				if (ld.size() > 0) {
					LinkDetail fd = (LinkDetail)ld.get(0);
					LinkHandler fh = new LinkHandler(fd);
					list.add(fh);
				}
			}
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		}
		return list;
	}
	
}
