package com.code.aon.ui.cms.velocity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.code.aon.cms.LinkCategory;
import com.code.aon.cms.LinkCategoryDetail;
import com.code.aon.cms.LinkConfig;
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
import com.code.aon.ui.cms.velocity.attribute.LinkCategoryHandler;

public class LinkGenerator extends Generator {

	public static void generate() {
		VelocityUtil vu = new VelocityUtil();
		CommonGenerator.getCommonGenerator().init(vu);
		vu.addMessage("Iniciando proceso de generación", VelocityUtil.INFO);
		vu.addMessage("", VelocityUtil.INFO);
		vu.addMessage("Buscando plantilla seleccionada '" + ControllerUtil.getCurrentConfig().getTemplate() + "' ...", VelocityUtil.INFO);
		vu.setTemplate_path(ControllerUtil.getCurrentVmTemplatePath());
		vu.initialize();
		vu.addMessage("", VelocityUtil.INFO);
		vu.addMessage("Creando link... ", VelocityUtil.INFO);

		ArrayList<LinkCategoryHandler> lchList;
		List<ITransferObject> linkCategoryDetailList;
		HashMap categoryMap = new HashMap<Section, List>();
		try {
			lchList = new ArrayList<LinkCategoryHandler>(); 
			IManagerBean bean = BeanManager.getManagerBean(LinkCategoryDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(ICMSAlias.LINK_CATEGORY_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
			linkCategoryDetailList = (List<ITransferObject>)bean.getList(criteria);
			LinkCategoryDetail lcd;
			List l;
			Section currentSection;
			Section configSection = GeneratorConfigController.currentSection(LinkConfig.class);;
			for (int i=0; i < linkCategoryDetailList.size(); i++) {
				lcd = (LinkCategoryDetail)linkCategoryDetailList.get(i);
				if (lcd.getLinkCategory().isActive()) {
					LinkCategoryHandler lch = new LinkCategoryHandler(lcd);
					lchList.add(lch);
					vu.put("link_category", lch);
					vu.addMessage(" Generando categoria Link '" + lcd.getLinkCategory().getAlias() + "'.", VelocityUtil.INFO);

					if (lcd.getLinkCategory().getSection()!=null){
						currentSection = lcd.getLinkCategory().getSection();
					}else{
						if (configSection!=null)
							currentSection = configSection;
						else
							currentSection = GeneratorConfigController.defaultSection();
					}
					CommonGenerator.getCommonGenerator().chargeContext(vu, currentSection);

					generate(vu, Templates.LINK, lcd.getLinkCategory().getAlias());
					vu.remove("link_category");
					
					l = (List) categoryMap.get(currentSection);
					if (l == null)
						l = new ArrayList<LinkCategoryHandler>();
					l.add(lch);
					categoryMap.put(currentSection,l);
				}
			}
			Iterator<Section> iter = categoryMap.keySet().iterator();
			Section key;
			ArrayList<LinkCategoryHandler> linkCategoryHandlerSet;
			while (iter.hasNext()){
				key = iter.next();
				linkCategoryHandlerSet = (ArrayList<LinkCategoryHandler>)categoryMap.get(key);
				vu.put("link_categories", linkCategoryHandlerSet);
				vu.addMessage(" Generando listado categoria seccion Link.", VelocityUtil.INFO);
				CommonGenerator.getCommonGenerator().chargeContext(vu, key);
				generate(vu, Templates.LINK, LINK_CATEGORY_BY_SECTION_PAGE + key.getId());
				vu.remove("link_categories");
			}
			iter = null;
			vu.put("link_categories", lchList);
			vu.addMessage(" Generando listado categoria Link.", VelocityUtil.INFO);
			CommonGenerator.getCommonGenerator().chargeContext(vu, configSection);
			generate(vu, Templates.LINK, LINK_CATEGORY_LIST_PAGE);
			vu.remove("link_categories");
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		} finally {
			linkCategoryDetailList = null;
			lchList = null;
			categoryMap = null;
		}
		vu.finalize();
		vu = null;
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

	public static String LINK_CATEGORY_BY_SECTION_PAGE = "category_section_";

}
