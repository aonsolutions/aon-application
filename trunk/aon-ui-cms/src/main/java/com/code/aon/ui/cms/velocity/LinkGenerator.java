package com.code.aon.ui.cms.velocity;

import java.util.ArrayList;
import java.util.List;

import com.code.aon.cms.GenericPage;
import com.code.aon.cms.GenericPageDetail;
import com.code.aon.cms.LinkCategory;
import com.code.aon.cms.LinkCategoryDetail;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.cms.enumeration.Templates;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.cms.util.ControllerUtil;
import com.code.aon.ui.cms.util.VelocityUtil;
import com.code.aon.ui.cms.velocity.attribute.GenericPageHandler;
import com.code.aon.ui.cms.velocity.attribute.LinkCategoryHandler;

public class LinkGenerator extends Generator {

	public static void generate(VelocityUtil vu) {
		ArrayList<LinkCategoryHandler> lchList;
		List<ITransferObject> linkCategoryDetailList;
		try {
			lchList = new ArrayList<LinkCategoryHandler>(); 
			IManagerBean bean = BeanManager.getManagerBean(LinkCategoryDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(ICMSAlias.LINK_CATEGORY_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
			linkCategoryDetailList = (List<ITransferObject>)bean.getList(criteria);
			LinkCategoryDetail lcd;
			for (int i=0; i < linkCategoryDetailList.size(); i++) {
				lcd = (LinkCategoryDetail)linkCategoryDetailList.get(i);
				if (lcd.getLinkCategory().isActive()) {
					LinkCategoryHandler lch = new LinkCategoryHandler(lcd);
					lchList.add(lch);
					vu.put("link_category", lch);
					vu.addMessage(" Generando categoria Link '" + lcd.getLinkCategory().getAlias() + "'.", VelocityUtil.INFO);
					generate(vu, Templates.LINK, lcd.getLinkCategory().getAlias());
					vu.remove("link_category");
				}
			}
			vu.put("link_categories", lchList);
			vu.addMessage(" Generando listado categoria Link.", VelocityUtil.INFO);
			generate(vu, Templates.LINK, LINK_CATEGORY_LIST_PAGE);
			vu.remove("link_categories");
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		} finally {
			linkCategoryDetailList = null;
			lchList = null;
		}
	}
	
	public static Object getLinkCategoryHandler(Integer ident) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(LinkCategory.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(ICMSAlias.LINK_CATEGORY_ID), ident);
			List<ITransferObject> l = (List<ITransferObject>)bean.getList(criteria);
			
			LinkCategory link = (LinkCategory)l.get(0);
			if (link.isActive()) {
				IManagerBean beanDetail = BeanManager.getManagerBean(LinkCategoryDetail.class);
				criteria = new Criteria();
				criteria.addEqualExpression(beanDetail.getFieldName(ICMSAlias.LINK_CATEGORY_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
				criteria.addEqualExpression(beanDetail.getFieldName(ICMSAlias.LINK_CATEGORY_DETAIL_LINK_CATEGORY_ID), ident);
				List<ITransferObject> ld = (List<ITransferObject>)beanDetail.getList(criteria);
				LinkCategoryDetail lcd = (LinkCategoryDetail)ld.get(0);
				LinkCategoryHandler lch = new LinkCategoryHandler(lcd);
				return lch;
			}
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String LINK_CATEGORY_LIST_PAGE = "category";

}
