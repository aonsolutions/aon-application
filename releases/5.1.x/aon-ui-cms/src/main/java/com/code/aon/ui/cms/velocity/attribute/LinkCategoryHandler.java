package com.code.aon.ui.cms.velocity.attribute;

import java.util.ArrayList;
import java.util.List;

import com.code.aon.cms.Link;
import com.code.aon.cms.LinkCategory;
import com.code.aon.cms.LinkCategoryDetail;
import com.code.aon.cms.LinkDetail;
import com.code.aon.cms.Section;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.cms.enumeration.Templates;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.cms.util.ControllerUtil;

public class LinkCategoryHandler {

	private String label;

	private String url;

	private ArrayList<LinkHandler> list;
	
	public LinkCategoryHandler (LinkCategoryDetail lcd) {
		label = lcd.getLabel();
		String link = Templates.LINK.getHtmlName();
		link = link.replaceAll("%NAME%", lcd.getLinkCategory().getAlias());
		url = link;
		list = getlinkList(lcd.getLinkCategory());
	}

	public String getLabel() {
		return label;
	}

	public String getUrl() {
		return url;
	}

	public ArrayList<LinkHandler> getList() {
		return list;
	}

	private ArrayList<LinkHandler> getlinkList(LinkCategory lc) {
		ArrayList<LinkHandler> list = new ArrayList<LinkHandler>();
		List<ITransferObject> linkList;
		List<ITransferObject> linkDetailList;
		try {
			IManagerBean bean = BeanManager.getManagerBean(Link.class);
			IManagerBean detailBean = BeanManager.getManagerBean(LinkDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(ICMSAlias.LINK_LINK_CATEGORY_ID), lc.getId());
			criteria.addEqualExpression(bean.getFieldName(ICMSAlias.LINK_ACTIVE), true);
			criteria.addOrder(bean.getFieldName(ICMSAlias.LINK_POSITION));
			linkList = (List<ITransferObject>)bean.getList(criteria);
			Link l;
			Criteria detailCriteria;
			for (int i = 0; i < linkList.size(); i++) {
				l = (Link)linkList.get(i);
				detailCriteria = new Criteria();
				detailCriteria.addEqualExpression(detailBean.getFieldName(ICMSAlias.LINK_DETAIL_LINK_ID), l.getId());
				detailCriteria.addEqualExpression(detailBean.getFieldName(ICMSAlias.LINK_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
				linkDetailList = (List<ITransferObject>)detailBean.getList(detailCriteria);
				if (linkDetailList.size() > 0) {
					LinkDetail fd = (LinkDetail)linkDetailList.get(0);
					LinkHandler fh = new LinkHandler(fd);
					list.add(fh);
				}
			}
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		} finally {
			linkList = null;
			linkDetailList = null;
		}
		return list;
	}
	
}
