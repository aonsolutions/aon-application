package com.code.aon.ui.cms.velocity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.code.aon.cms.Link;
import com.code.aon.cms.LinkCategory;
import com.code.aon.cms.LinkCategoryDetail;
import com.code.aon.cms.LinkConfig;
import com.code.aon.cms.LinkDetail;
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
import com.code.aon.ui.cms.velocity.attribute.LinkHandler;

public class LinkGenerator extends Generator {

	public static void generate() {
		LinkGenerator.generate(null);
	}

	public static void generate(LinkCategory selectedCategory) {
		VelocityUtil vu = new VelocityUtil();
		CommonGenerator.getCommonGenerator().init(vu);
		vu.setTemplate_path(ControllerUtil.getCurrentVmTemplatePath());
		vu.initialize();

		ArrayList<LinkCategoryHandler> lchList;
		List<ITransferObject> linkCategoryList;
		List<ITransferObject> linkCategoryDetailList;
		HashMap categoryMap = new HashMap<Integer, List>();
		try {
			lchList = new ArrayList<LinkCategoryHandler>();
			
			IManagerBean bean = BeanManager.getManagerBean(LinkCategory.class);
			Criteria criteria = new Criteria();
			if (selectedCategory!=null)
				criteria.addEqualExpression(bean.getFieldName(ICMSAlias.LINK_CATEGORY_ID), selectedCategory.getId());
			criteria.addEqualExpression(bean.getFieldName(ICMSAlias.LINK_CATEGORY_ACTIVE), true);
			criteria.addOrder(bean.getFieldName(ICMSAlias.LINK_CATEGORY_POSITION));
			linkCategoryList = (List<ITransferObject>)bean.getList(criteria);
			
			LinkCategory lc;
			LinkCategoryDetail lcd;
			List l;
			Section currentSection;
			Section configSection = GeneratorConfigController.currentSection(LinkConfig.class);;
			for (int i=0; i < linkCategoryList.size(); i++) {
				lc = (LinkCategory)linkCategoryList.get(i);
				
				bean = BeanManager.getManagerBean(LinkCategoryDetail.class);
				criteria = new Criteria();
				criteria.addEqualExpression(bean.getFieldName(ICMSAlias.LINK_CATEGORY_DETAIL_LINK_CATEGORY_ID), lc.getId());
				criteria.addEqualExpression(bean.getFieldName(ICMSAlias.LINK_CATEGORY_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
				linkCategoryDetailList = (List<ITransferObject>)bean.getList(criteria);
				if (linkCategoryDetailList.isEmpty()){
					VelocityUtil.addMessage("La categoria de links " + lc.getAlias() + " no esta internacionalizada.", VelocityUtil.WARN);
				}else{
					lcd = (LinkCategoryDetail)linkCategoryDetailList.get(0);
				
					LinkCategoryHandler lch = new LinkCategoryHandler(lcd,getlinkList(lc));
					lchList.add(lch);
					vu.put("link_category", lch);
					VelocityUtil.addMessage(" Generando categoria Link '" + lcd.getLinkCategory().getAlias() + "'.", VelocityUtil.INFO);

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
					
					l = (List) categoryMap.get(currentSection.getId());
					if (l == null)
						l = new ArrayList<LinkCategoryHandler>();
					l.add(lch);
					categoryMap.put(currentSection.getId(),l);
				}
			}
			Iterator<Integer> iter = categoryMap.keySet().iterator();
			Integer key;
			ArrayList<LinkCategoryHandler> linkCategoryHandlerSet;
			while (iter.hasNext()){
				key = iter.next();
				IManagerBean beanSection = BeanManager.getManagerBean(Section.class);
				Criteria criteriaSection = new Criteria();
				criteriaSection.addEqualExpression(beanSection.getFieldName(ICMSAlias.SECTION_ID),key);
				Section section = (Section)((List<ITransferObject>)beanSection.getList(criteriaSection)).get(0);
				linkCategoryHandlerSet = (ArrayList<LinkCategoryHandler>)categoryMap.get(key);
				vu.put("link_categories", linkCategoryHandlerSet);
				VelocityUtil.addMessage(" Generando listado categoria seccion Link.", VelocityUtil.INFO);
				CommonGenerator.getCommonGenerator().chargeContext(vu, section);
				generate(vu, Templates.LINK, LINK_CATEGORY_BY_SECTION_PAGE + section.getId());
				vu.remove("link_categories");
			}
			iter = null;
			vu.put("link_categories", lchList);
			VelocityUtil.addMessage(" Generando listado categoria Link.", VelocityUtil.INFO);
			CommonGenerator.getCommonGenerator().chargeContext(vu, configSection);
			generate(vu, Templates.LINK, LINK_CATEGORY_LIST_PAGE);
			vu.remove("link_categories");
		} catch (ManagerBeanException e) {
			VelocityUtil.addMessage(e.getMessage(), VelocityUtil.ERROR);
		} finally {
			linkCategoryList = null;
			linkCategoryDetailList = null;
			lchList = null;
			categoryMap = null;
		}
		vu.finalize();
		vu = null;
	}
	
	private static ArrayList<LinkHandler> getlinkList(LinkCategory lc) {
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
			if (linkList.isEmpty())
				VelocityUtil.addMessage("La categoria de links " + lc.getAlias() + " no tiene links asociados.", VelocityUtil.WARN);
			Link l;
			Criteria detailCriteria;
			for (int i = 0; i < linkList.size(); i++) {
				l = (Link)linkList.get(i);
				detailCriteria = new Criteria();
				detailCriteria.addEqualExpression(detailBean.getFieldName(ICMSAlias.LINK_DETAIL_LINK_ID), l.getId());
				detailCriteria.addEqualExpression(detailBean.getFieldName(ICMSAlias.LINK_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
				linkDetailList = (List<ITransferObject>)detailBean.getList(detailCriteria);
				if (linkDetailList.isEmpty()) {
					VelocityUtil.addMessage("El link " + l.getAlias() + " no esta internacionalizado.", VelocityUtil.WARN);
				}else{
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


	public static Object getLinkCategoryHandler(Integer ident) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(LinkCategory.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(ICMSAlias.LINK_CATEGORY_ID), ident);
			List<ITransferObject> l = (List<ITransferObject>)bean.getList(criteria);
			if (l.isEmpty()){
				VelocityUtil.addMessage("CATEGORIA DE LINKS "+ident+" REFERENCIADA NO EXISTE !!!", VelocityUtil.WARN);
				return null;
			}
			LinkCategory link = (LinkCategory)l.get(0);
			if (link.isActive()) {
				IManagerBean beanDetail = BeanManager.getManagerBean(LinkCategoryDetail.class);
				criteria = new Criteria();
				criteria.addEqualExpression(beanDetail.getFieldName(ICMSAlias.LINK_CATEGORY_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
				criteria.addEqualExpression(beanDetail.getFieldName(ICMSAlias.LINK_CATEGORY_DETAIL_LINK_CATEGORY_ID), ident);
				List<ITransferObject> ld = (List<ITransferObject>)beanDetail.getList(criteria);
				if (ld.isEmpty()){
					VelocityUtil.addMessage("El categoria de links " + link.getAlias() + " no esta internacionalizado.", VelocityUtil.WARN);
				}else{
					LinkCategoryDetail lcd = (LinkCategoryDetail)ld.get(0);
					LinkCategoryHandler lch = new LinkCategoryHandler(lcd,getlinkList(link));
					return lch;
				}
			}
		} catch (ManagerBeanException e) {
			VelocityUtil.addMessage(e.getMessage(), VelocityUtil.ERROR);
		}
		return null;
	}

	public static String LINK_CATEGORY_LIST_PAGE = "category";

	public static String LINK_CATEGORY_BY_SECTION_PAGE = "category_section_";

}
