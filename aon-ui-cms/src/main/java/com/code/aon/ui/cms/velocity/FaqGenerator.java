package com.code.aon.ui.cms.velocity;

import java.util.ArrayList;
import java.util.List;

import com.code.aon.cms.FaqCategoryDetail;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.cms.enumeration.Templates;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.cms.util.ControllerUtil;
import com.code.aon.ui.cms.util.VelocityUtil;
import com.code.aon.ui.cms.velocity.attribute.FaqCategoryHandler;

public class FaqGenerator extends Generator {

	public static void generate(VelocityUtil vu) {
		try {
			ArrayList<FaqCategoryHandler> fchList = new ArrayList<FaqCategoryHandler>(); 
			
			IManagerBean bean = BeanManager.getManagerBean(FaqCategoryDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(ICMSAlias.FAQ_CATEGORY_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
			List<ITransferObject> l = (List<ITransferObject>)bean.getList(criteria);
			for (int i=0; i < l.size(); i++) {
				FaqCategoryDetail fcd = (FaqCategoryDetail)l.get(i);
				if (fcd.getFaqCategory().isActive()) {
					FaqCategoryHandler fch = new FaqCategoryHandler(fcd);
					fchList.add(fch);
					vu.put("faq_category", fch);
					vu.addMessage(" Generando categoria Faq '" + fcd.getFaqCategory().getAlias() + "'.", VelocityUtil.INFO);
					generate(vu, Templates.FAQ, fcd.getFaqCategory().getAlias());
					vu.remove("faq_category");
				}
			}
			vu.put("faq_categories", fchList);
			vu.addMessage(" Generando listado categoria Faq.", VelocityUtil.INFO);
			generate(vu, Templates.FAQ, FAQ_CATEGORY_LIST_PAGE);
			vu.remove("faq_categories");
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		}
	}
	
	public static String FAQ_CATEGORY_LIST_PAGE = "category";

}
