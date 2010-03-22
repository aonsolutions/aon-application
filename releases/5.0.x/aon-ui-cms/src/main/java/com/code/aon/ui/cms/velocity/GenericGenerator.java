package com.code.aon.ui.cms.velocity;

import java.util.List;

import com.code.aon.cms.GenericPage;
import com.code.aon.cms.GenericPageDetail;
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

public class GenericGenerator extends Generator {

	public static void generate() {
		VelocityUtil vu = new VelocityUtil();
		CommonGenerator.getCommonGenerator().init(vu);
		vu.addMessage("Iniciando proceso de generación", VelocityUtil.INFO);
		vu.addMessage("", VelocityUtil.INFO);
		vu.addMessage("Buscando plantilla seleccionada '" + ControllerUtil.getCurrentConfig().getTemplate() + "' ...", VelocityUtil.INFO);
		vu.setTemplate_path(ControllerUtil.getCurrentVmTemplatePath());
		vu.initialize();
		vu.addMessage("", VelocityUtil.INFO);
		vu.addMessage("Creando páginas genéricas... ", VelocityUtil.INFO);
		
		List<ITransferObject> genericPageDetailList;
		try {
			IManagerBean bean = BeanManager.getManagerBean(GenericPageDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(ICMSAlias.GENERIC_PAGE_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
			genericPageDetailList = (List<ITransferObject>)bean.getList(criteria);
			GenericPageDetail gpd;
			for (int i=0; i < genericPageDetailList.size(); i++) {
				gpd = (GenericPageDetail)genericPageDetailList.get(i);
				if (gpd.getGeneric_page().isActive()) {
					GenericPageHandler gph = new GenericPageHandler(gpd);
					vu.put("generic", gph);
					if (gph.getMenu() != null) vu.put("menu", gph.getMenu());
					else vu.remove("menu");
					if (gph.getDescription() != null && !gph.getDescription().equals("")) vu.put("description", gph.getDescription());
					else vu.remove("description");
					if (gph.getKeywords() != null && !gph.getKeywords().equals("")) vu.put("keywords", gph.getKeywords());
					else vu.remove("keywords");
					vu.addMessage(" Generando Página Genérica '" + gpd.getGeneric_page().getAlias() + "'.", VelocityUtil.INFO);
					CommonGenerator.getCommonGenerator().chargeContext(vu, gpd.getGeneric_page().getSection());
					generate(vu, Templates.GENERIC, gpd.getGeneric_page().getAlias());
					vu.remove("generic");
					vu.remove("description");
					vu.remove("keywords");
				}
			}
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		} finally {
			genericPageDetailList = null;
		}
		vu.finalize();
		vu = null;
	}

	public static Object getGenericHandler(Integer ident) {
		List<ITransferObject> genericPageList;
		List<ITransferObject> genericPageDetailList;
		try {
			IManagerBean bean = BeanManager.getManagerBean(GenericPage.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(ICMSAlias.GENERIC_PAGE_ID), ident);
			genericPageList = (List<ITransferObject>)bean.getList(criteria);
			
			GenericPage gp = (GenericPage)genericPageList.get(0);
			if (gp.isActive()) {
				IManagerBean beanDetail = BeanManager.getManagerBean(GenericPageDetail.class);
				criteria = new Criteria();
				criteria.addEqualExpression(beanDetail.getFieldName(ICMSAlias.GENERIC_PAGE_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
				criteria.addEqualExpression(beanDetail.getFieldName(ICMSAlias.GENERIC_PAGE_DETAIL_GENERIC_PAGE_ID), ident);
				genericPageDetailList = (List<ITransferObject>)beanDetail.getList(criteria);
				GenericPageDetail gpd = (GenericPageDetail)genericPageDetailList.get(0);
				GenericPageHandler gph = new GenericPageHandler(gpd);
				return gph;
			}
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		} finally {
			genericPageList = null;
			genericPageDetailList = null;
		}
		return null;
	}

}
