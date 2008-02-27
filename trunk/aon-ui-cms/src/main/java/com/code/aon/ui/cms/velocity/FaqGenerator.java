package com.code.aon.ui.cms.velocity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.code.aon.cms.FaqCategoryDetail;
import com.code.aon.cms.FaqConfig;
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

public class FaqGenerator extends Generator {

	public static void generate() {
		VelocityUtil vu = new VelocityUtil();
		CommonGenerator.getCommonGenerator().init(vu);
		vu.addMessage("Iniciando proceso de generación", VelocityUtil.INFO);
		vu.addMessage("", VelocityUtil.INFO);
		vu.addMessage("Buscando plantilla seleccionada '" + ControllerUtil.getCurrentConfig().getTemplate() + "' ...", VelocityUtil.INFO);
		vu.setTemplate_path(ControllerUtil.getCurrentVmTemplatePath());
		vu.initialize();
		vu.addMessage("", VelocityUtil.INFO);
		vu.addMessage("Creando faq... ", VelocityUtil.INFO);
		
		ArrayList<FaqCategoryHandler> fchList;
		List<ITransferObject> faqCategoryDetailList;
		HashMap categoryMap = new HashMap<Section, List>();
		try {
			fchList = new ArrayList<FaqCategoryHandler>(); 
			
			IManagerBean bean = BeanManager.getManagerBean(FaqCategoryDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(ICMSAlias.FAQ_CATEGORY_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
			faqCategoryDetailList = (List<ITransferObject>)bean.getList(criteria);
			FaqCategoryDetail fcd;
			List l;
			Section currentSection;
			Section configSection = GeneratorConfigController.currentSection(FaqConfig.class);;
			for (int i=0; i < faqCategoryDetailList.size(); i++) {
				fcd = (FaqCategoryDetail)faqCategoryDetailList.get(i);
				if (fcd.getFaqCategory().isActive()) {
					FaqCategoryHandler fch = new FaqCategoryHandler(fcd);
					fchList.add(fch);
					vu.put("faq_category", fch);
					vu.addMessage(" Generando categoria Faq '" + fcd.getFaqCategory().getAlias() + "'.", VelocityUtil.INFO);
					
					if (fcd.getFaqCategory().getSection()!=null){
						currentSection = fcd.getFaqCategory().getSection();
					}else{
						if (configSection!=null)
							currentSection = configSection;
						else
							currentSection = GeneratorConfigController.defaultSection();
					}
					CommonGenerator.getCommonGenerator().chargeContext(vu, currentSection);
					generate(vu, Templates.FAQ, fcd.getFaqCategory().getAlias());
					vu.remove("faq_category");
					
					l = (List) categoryMap.get(currentSection);
					if (l == null)
						l = new ArrayList<FaqCategoryHandler>();
					l.add(fch);
					categoryMap.put(currentSection,l);
				}
			}
			Iterator<Section> iter = categoryMap.keySet().iterator();
			Section key;
			ArrayList<FaqCategoryHandler> faqCategoryHandlerSet;
			while (iter.hasNext()){
				key = iter.next();
				faqCategoryHandlerSet = (ArrayList<FaqCategoryHandler>)categoryMap.get(key);
				vu.put("faq_categories", faqCategoryHandlerSet);
				vu.addMessage(" Generando listado categoria seccion Faq.", VelocityUtil.INFO);
				CommonGenerator.getCommonGenerator().chargeContext(vu, key);
				generate(vu, Templates.FAQ, FAQ_CATEGORY_BY_SECTION_PAGE + key.getId());
				vu.remove("faq_categories");
			}
			iter = null;
			
			vu.put("faq_categories", fchList);
			vu.addMessage(" Generando listado categoria Faq.", VelocityUtil.INFO);
			CommonGenerator.getCommonGenerator().chargeContext(vu, configSection);
			generate(vu, Templates.FAQ, FAQ_CATEGORY_LIST_PAGE);
			vu.remove("faq_categories");
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		} finally {
			fchList = null;
			faqCategoryDetailList = null;
			categoryMap = null;
		}
		vu.finalize();
		vu = null;
	}
	
	public static String FAQ_CATEGORY_LIST_PAGE = "category";

	public static String FAQ_CATEGORY_BY_SECTION_PAGE = "category_section_";

}
