package com.code.aon.ui.cms.velocity;

import java.util.List;

import org.apache.commons.lang.StringUtils;

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

	public void generate() {
		generate(null);
	}

	public void generate(GenericPage selectedPage) {
		VelocityUtil vu = context.initVelocityUtil();	
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
					vu.put(GENERIC_KEY, gph);
					if (gph.getMenu() != null) {
						vu.put(MENU_KEY, gph.getMenu());
					} else {
						vu.remove(MENU_KEY);
					}
					if (! StringUtils.isEmpty(gph.getDescription())) {
						vu.put(DESCRIPTION_KEY, gph.getDescription());
					} else {
						vu.remove(DESCRIPTION_KEY);
					}
					if (! StringUtils.isEmpty(gph.getKeywords())) {
						vu.put(KEYWORDS_KEY, gph.getKeywords());
					} else {
						vu.remove(KEYWORDS_KEY);
					}
					logger.info(" Generando Página Genérica '" + gpd.getGeneric_page().getAlias() + "'.");
					context.changeSection(vu, gpd.getGeneric_page().getSection());
					generate(vu, Templates.GENERIC, gpd.getGeneric_page().getAlias());
					vu.remove(GENERIC_KEY);
					vu.remove(DESCRIPTION_KEY);
					vu.remove(KEYWORDS_KEY);
				}
			}
		} catch (ManagerBeanException e) {
			logger.error(e.getMessage());
		} finally {
			genericPageList = null;
			genericPageDetailList = null;
		}
	}

	public static GenericPageHandler getGenericHandler(Integer ident, String message) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(GenericPage.class);
			GenericPage gp = (GenericPage) bean.get(ident);
			if ( gp == null ){
				getLogger().error( message + " REFERENCIA A UNA PAGINA GENERICA ("+ident+") INEXISTENTE");
				return null;
			}
			if (gp.isActive()) {
				IManagerBean beanDetail = BeanManager.getManagerBean(GenericPageDetail.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(beanDetail.getFieldName(ICMSAlias.GENERIC_PAGE_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
				criteria.addEqualExpression(beanDetail.getFieldName(ICMSAlias.GENERIC_PAGE_DETAIL_GENERIC_PAGE_ID), ident);
				List<ITransferObject> genericPageDetailList = (List<ITransferObject>)beanDetail.getList(criteria);
				if (genericPageDetailList.isEmpty()){
					getLogger().warning("La pagina generica " + gp.getAlias() + " no esta internacionalizada.");
				}else{
					GenericPageDetail gpd = (GenericPageDetail)genericPageDetailList.get(0);
					GenericPageHandler gph = new GenericPageHandler(gpd);
					return gph;
				}
			}
		} catch (ManagerBeanException e) {
			getLogger().error(e.getMessage());
		}
		return null;
	}

}
