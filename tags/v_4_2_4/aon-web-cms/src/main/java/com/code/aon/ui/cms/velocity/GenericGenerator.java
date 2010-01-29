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
import com.code.aon.ui.cms.IGeneratorLogger;
import com.code.aon.ui.cms.util.ControllerUtil;
import com.code.aon.ui.cms.util.VelocityUtil;
import com.code.aon.ui.cms.velocity.attribute.GenericPageHandler;

public class GenericGenerator extends Generator {

	public static void generate() {
		GenericGenerator.generate(null);
	}

	public static void generate(GenericPage selectedPage) {
		VelocityUtil vu = CommonGenerator.getCommonGenerator().initVelocityUtil();
		IGeneratorLogger logger = CommonGenerator.getLogger();
		
		List<ITransferObject> genericPageList;
		List<ITransferObject> genericPageDetailList;
		try {
			IManagerBean bean = BeanManager.getManagerBean(GenericPage.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(ICMSAlias.GENERIC_PAGE_ACTIVE), true);
			if (selectedPage!=null)
				criteria.addEqualExpression(bean.getFieldName(ICMSAlias.GENERIC_PAGE_ID), selectedPage.getId());
			genericPageList = (List<ITransferObject>)bean.getList(criteria);
			
			GenericPage gp;
			GenericPageDetail gpd;
			for (int i=0; i < genericPageList.size(); i++) {
				gp = (GenericPage)genericPageList.get(i);

				bean = BeanManager.getManagerBean(GenericPageDetail.class);
				criteria = new Criteria();
				criteria.addEqualExpression(bean.getFieldName(ICMSAlias.GENERIC_PAGE_DETAIL_GENERIC_PAGE_ID), gp.getId());
				criteria.addEqualExpression(bean.getFieldName(ICMSAlias.GENERIC_PAGE_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
				genericPageDetailList = (List<ITransferObject>)bean.getList(criteria);
				if (genericPageDetailList.isEmpty()){
					logger.warning("La pagina generica " + gp.getAlias() + " no esta internacionalizada.");
				}else{
					gpd = (GenericPageDetail)genericPageDetailList.get(0);

					GenericPageHandler gph = new GenericPageHandler(gpd);
					vu.put("generic", gph);
					if (gph.getMenu() != null) vu.put("menu", gph.getMenu());
					else vu.remove("menu");
					if (gph.getDescription() != null && !gph.getDescription().equals("")) vu.put("description", gph.getDescription());
					else vu.remove("description");
					if (gph.getKeywords() != null && !gph.getKeywords().equals("")) vu.put("keywords", gph.getKeywords());
					else vu.remove("keywords");
					logger.info(" Generando Página Genérica '" + gpd.getGeneric_page().getAlias() + "'.");
					CommonGenerator.getCommonGenerator().chargeContext(vu, gpd.getGeneric_page().getSection());
					generate(vu, Templates.GENERIC, gpd.getGeneric_page().getAlias());
					vu.remove("generic");
					vu.remove("description");
					vu.remove("keywords");
				}
			}
		} catch (ManagerBeanException e) {
			logger.error(e.getMessage());
		} finally {
			genericPageList = null;
			genericPageDetailList = null;
		}
		vu = null;
	}

	public static Object getGenericHandler(Integer ident) {
		List<ITransferObject> genericPageList;
		List<ITransferObject> genericPageDetailList;
		IGeneratorLogger logger = CommonGenerator.getLogger();
		try {
			IManagerBean bean = BeanManager.getManagerBean(GenericPage.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(ICMSAlias.GENERIC_PAGE_ID), ident);
			genericPageList = (List<ITransferObject>)bean.getList(criteria);
			if (genericPageList.isEmpty()){
				logger.warning("PAGINA GENERICA "+ident+" REFERENCIADA NO EXISTE !!!");
				return null;
			}
			GenericPage gp = (GenericPage)genericPageList.get(0);
			if (gp.isActive()) {
				IManagerBean beanDetail = BeanManager.getManagerBean(GenericPageDetail.class);
				criteria = new Criteria();
				criteria.addEqualExpression(beanDetail.getFieldName(ICMSAlias.GENERIC_PAGE_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
				criteria.addEqualExpression(beanDetail.getFieldName(ICMSAlias.GENERIC_PAGE_DETAIL_GENERIC_PAGE_ID), ident);
				genericPageDetailList = (List<ITransferObject>)beanDetail.getList(criteria);
				if (genericPageDetailList.isEmpty()){
					logger.warning("La pagina generica " + gp.getAlias() + " no esta internacionalizada.");
				}else{
					GenericPageDetail gpd = (GenericPageDetail)genericPageDetailList.get(0);
					GenericPageHandler gph = new GenericPageHandler(gpd);
					return gph;
				}
			}
		} catch (ManagerBeanException e) {
			logger.error(e.getMessage());
		} finally {
			genericPageList = null;
			genericPageDetailList = null;
		}
		return null;
	}

}
