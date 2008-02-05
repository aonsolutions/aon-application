package com.code.aon.ui.cms.velocity;

import java.util.ArrayList;
import java.util.List;

import com.code.aon.cms.ModularPage;
import com.code.aon.cms.ModularPageDetail;
import com.code.aon.cms.ModularPageOption;
import com.code.aon.cms.ModularPageOptionDetail;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.cms.enumeration.Templates;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.cms.util.ControllerUtil;
import com.code.aon.ui.cms.util.VelocityUtil;
import com.code.aon.ui.cms.velocity.attribute.ModularPageHandler;
import com.code.aon.ui.cms.velocity.attribute.ModularPageOptionHandler;

public class ModularPageGenerator extends Generator {

	public static void generate(VelocityUtil vu) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(ModularPage.class);
			List<ITransferObject> l = (List<ITransferObject>) bean.getList(null);
			for (int i = 0; i < l.size(); i++) {
				ModularPage mp = (ModularPage) l.get(i);
				ArrayList<ModularPageOptionHandler> list = new ArrayList<ModularPageOptionHandler>();
				IManagerBean moBean = BeanManager.getManagerBean(ModularPageOption.class);
				Criteria criteria_moBean = new Criteria();
				criteria_moBean.addEqualExpression(moBean.getFieldName(ICMSAlias.MODULAR_PAGE_OPTION_MODULAR_PAGE_ID),mp.getId());
				criteria_moBean.addEqualExpression(moBean.getFieldName(ICMSAlias.MODULAR_PAGE_OPTION_ACTIVE),true);
				criteria_moBean.addOrder(moBean.getFieldName(ICMSAlias.MODULAR_PAGE_OPTION_POSITION));
				List<ITransferObject> l2 = (List<ITransferObject>) moBean.getList(criteria_moBean);
				ArrayList<ModularPageOptionHandler> moduleList = new ArrayList<ModularPageOptionHandler>();
				for (int j = 0; j < l2.size(); j++) {
					ModularPageOption mo = (ModularPageOption) l2.get(j);
					IManagerBean modBean = BeanManager.getManagerBean(ModularPageOptionDetail.class);
					Criteria criteria_detail = new Criteria();
					criteria_detail.addEqualExpression(modBean.getFieldName(ICMSAlias.MODULAR_PAGE_OPTION_DETAIL_MODULAR_PAGE_OPTION_ID),mo.getId());
					criteria_detail.addEqualExpression(modBean.getFieldName(ICMSAlias.MODULAR_PAGE_OPTION_DETAIL_LANGUAGE_ID),ControllerUtil.getCurrentLanguage().getId());
					List<ITransferObject> ld = (List<ITransferObject>) modBean.getList(criteria_detail);
					if (ld.size()>0) {
						ModularPageOptionDetail mpod = (ModularPageOptionDetail) ld.get(0);
						if (mpod.getModular_page_option().isActive()) {
							ModularPageOptionHandler mph = new ModularPageOptionHandler(mpod);
							moduleList.add(mph);
						}
					}
				}
				IManagerBean mdBean = BeanManager.getManagerBean(ModularPageDetail.class);
				Criteria criteria_mp_detail = new Criteria();
				criteria_mp_detail.addEqualExpression(mdBean.getFieldName(ICMSAlias.MODULAR_PAGE_DETAIL_MODULAR_PAGE_ID),mp.getId());
				criteria_mp_detail.addEqualExpression(mdBean.getFieldName(ICMSAlias.MODULAR_PAGE_DETAIL_LANGUAGE_ID),ControllerUtil.getCurrentLanguage().getId());
				List<ITransferObject> lmpd = (List<ITransferObject>) mdBean.getList(criteria_mp_detail);
				if (lmpd.size()>0) {
					ModularPageHandler mph = new ModularPageHandler((ModularPageDetail)lmpd.get(0));
					vu.put("module", mph);
				}
				vu.put("modules", moduleList);

				// Cargar datos comunes a todas las paginas
				vu.addMessage("", VelocityUtil.INFO);
				vu.addMessage("Cargando configuraciones comunes en el contexto  '"+ ControllerUtil.getCurrentConfig().getTemplate() + "' ...",VelocityUtil.INFO);
				CommonGenerator.getCommonGenerator().chargeContext(vu, mp.getSection());

				vu.addMessage(" Generando Página Modular '" + mp.getAlias()+ "'.", VelocityUtil.INFO);
				if (mp.isHomepage()){
					generate(vu, Templates.HOME, mp.getAlias());
				}else{
					generate(vu, Templates.MODULAR, mp.getAlias());
				}
				vu.remove("module");
				vu.remove("modules");
			}
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		}
	}

}
