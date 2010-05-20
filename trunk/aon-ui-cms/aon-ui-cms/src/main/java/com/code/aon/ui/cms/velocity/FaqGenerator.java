package com.code.aon.ui.cms.velocity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.code.aon.cms.Faq;
import com.code.aon.cms.FaqCategory;
import com.code.aon.cms.FaqCategoryDetail;
import com.code.aon.cms.FaqConfig;
import com.code.aon.cms.FaqDetail;
import com.code.aon.cms.Section;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.cms.enumeration.Templates;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.cms.controller.GeneratorConfigController;
import com.code.aon.ui.cms.util.ControllerUtil;
import com.code.aon.ui.cms.util.VelocityUtil;
import com.code.aon.ui.cms.velocity.attribute.FaqCategoryHandler;
import com.code.aon.ui.cms.velocity.attribute.FaqHandler;

public class FaqGenerator extends Generator {

	public static final String FAQ_CATEGORY_LIST_PAGE = "category";

	public static final String FAQ_CATEGORY_BY_SECTION_PAGE = "category_section_";
	
	public void generate() {
		generate(null);
	}

	public void generate(FaqCategory selectedCategory) {
		VelocityUtil vu = context.initVelocityUtil();	
		ArrayList<FaqCategoryHandler> fchList;
		List<ITransferObject> faqCategoryList;
		List<ITransferObject> faqCategoryDetailList;
		Map<Section, List<FaqCategoryHandler>> categoryMap = new HashMap<Section, List<FaqCategoryHandler>>();
		try {
			fchList = new ArrayList<FaqCategoryHandler>(); 
			
			IManagerBean bean = BeanManager.getManagerBean(FaqCategory.class);
			Criteria criteria = new Criteria();
			if (selectedCategory!=null)
				criteria.addEqualExpression(bean.getFieldName(ICMSAlias.FAQ_CATEGORY_ID), selectedCategory.getId());
			criteria.addEqualExpression(bean.getFieldName(ICMSAlias.FAQ_CATEGORY_ACTIVE), true);
			criteria.addOrder(bean.getFieldName(ICMSAlias.FAQ_CATEGORY_POSITION));
			faqCategoryList = (List<ITransferObject>)bean.getList(criteria);

			FaqCategory fc;
			FaqCategoryDetail fcd;
			List<FaqCategoryHandler> l;
			Section currentSection;
			Section configSection = GeneratorConfigController.currentSection(FaqConfig.class);;
			for (int i=0; i < faqCategoryList.size(); i++) {
				fc = (FaqCategory)faqCategoryList.get(i);
				
				bean = BeanManager.getManagerBean(FaqCategoryDetail.class);
				criteria = new Criteria();
				criteria.addEqualExpression(bean.getFieldName(ICMSAlias.FAQ_CATEGORY_DETAIL_FAQ_CATEGORY_ID), fc.getId());
				criteria.addEqualExpression(bean.getFieldName(ICMSAlias.FAQ_CATEGORY_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
				faqCategoryDetailList = (List<ITransferObject>)bean.getList(criteria);
				if(faqCategoryDetailList.isEmpty()){
					logger.warning("La categoria de FAQ " + fc.getAlias() + " no esta internacionalizada.");
				}else{
					fcd = (FaqCategoryDetail)faqCategoryDetailList.get(0);
					if (fc.isActive()) {
						FaqCategoryHandler fch = new FaqCategoryHandler(fcd,getFaqList(fc));
						fchList.add(fch);
						vu.put("faq_category", fch);
						logger.info(" Generando categoria Faq '" + fcd.getFaqCategory().getAlias() + "'.");
						
						if (fcd.getFaqCategory().getSection()!=null){
							currentSection = fcd.getFaqCategory().getSection();
						}else{
							if (configSection!=null)
								currentSection = configSection;
							else
								currentSection = GeneratorConfigController.defaultSection();
						}
						context.changeSection(vu, currentSection);
						generate(vu, Templates.FAQ, fcd.getFaqCategory().getAlias());
						vu.remove("faq_category");
						
						l = categoryMap.get(currentSection);
						if (l == null)
							l = new ArrayList<FaqCategoryHandler>();
						l.add(fch);
						categoryMap.put(currentSection,l);
					}
				}
			}
			Iterator<Section> iter = categoryMap.keySet().iterator();
			Section key;
			ArrayList<FaqCategoryHandler> faqCategoryHandlerSet;
			while (iter.hasNext()){
				key = iter.next();
				faqCategoryHandlerSet = (ArrayList<FaqCategoryHandler>)categoryMap.get(key);
				vu.put("faq_categories", faqCategoryHandlerSet);
				logger.info(" Generando listado categoria seccion Faq.");
				context.changeSection(vu, key);
				generate(vu, Templates.FAQ, FAQ_CATEGORY_BY_SECTION_PAGE + key.getId());
				vu.remove("faq_categories");
			}
			iter = null;
			
			vu.put("faq_categories", fchList);
			logger.info(" Generando listado categoria Faq.");
			context.changeSection(vu, configSection);
			generate(vu, Templates.FAQ, FAQ_CATEGORY_LIST_PAGE);
			vu.remove("faq_categories");
		} catch (ManagerBeanException e) {
			logger.error(e.getMessage());
		} finally {
			fchList = null;
			faqCategoryList = null;
			faqCategoryDetailList = null;
			categoryMap = null;
		}
	}
	
	private ArrayList<FaqHandler> getFaqList(FaqCategory fc) {
		ArrayList<FaqHandler> list = new ArrayList<FaqHandler>();
		List<ITransferObject> ld;
		try {
			IManagerBean bean = BeanManager.getManagerBean(Faq.class);
			IManagerBean detailBean = BeanManager.getManagerBean(FaqDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(ICMSAlias.FAQ_FAQ_CATEGORY_ID), fc.getId());
			criteria.addEqualExpression(bean.getFieldName(ICMSAlias.FAQ_ACTIVE), true);
			criteria.addOrder(bean.getFieldName(ICMSAlias.FAQ_POSITION));
			List<ITransferObject> l = (List<ITransferObject>)bean.getList(criteria);
			if (l.isEmpty())
				logger.warning("La categoria de FAQ " + fc.getAlias() + " no tiene FAQs.");
			Criteria detailCriteria;
			Faq f;
			FaqDetail fd;
			for (int i = 0; i < l.size(); i++) {
				f = (Faq)l.get(i);
				detailCriteria = new Criteria();
				detailCriteria.addEqualExpression(detailBean.getFieldName(ICMSAlias.FAQ_DETAIL_FAQ_ID), f.getId());
				detailCriteria.addEqualExpression(detailBean.getFieldName(ICMSAlias.FAQ_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
				ld = (List<ITransferObject>)detailBean.getList(detailCriteria);
				if (ld.isEmpty()) {
					logger.warning("La FAQ " + f.getAlias() + " no esta internacionalizada.");
				}else{
					fd = (FaqDetail)ld.get(0);
					FaqHandler fh = new FaqHandler(fd);
					list.add(fh);
				}
			}
		} catch (ManagerBeanException e) {
			logger.error(e.getMessage());
		} finally {
			ld = null;
		}
		return list;
	}

}
