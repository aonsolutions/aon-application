package com.code.aon.ui.cms.velocity;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	
	private final static Logger LOGGER = LoggerFactory.getLogger(ModularPageGenerator.class);

	public void generate(ModularPage selected_modular) {
		VelocityUtil vu = context.initVelocityUtil();			
		try {
			IManagerBean bean = BeanManager.getManagerBean(ModularPage.class);
			IManagerBean moBean = BeanManager.getManagerBean(ModularPageOption.class);
			IManagerBean modBean = BeanManager.getManagerBean(ModularPageOptionDetail.class);
			IManagerBean mdBean = BeanManager.getManagerBean(ModularPageDetail.class);

			Criteria criteria_mBean = null;
			if (selected_modular!=null){
				criteria_mBean = new Criteria();
				criteria_mBean.addEqualExpression(bean.getFieldName(ICMSAlias.MODULAR_PAGE_ID),selected_modular.getId());
			}
			List<ITransferObject> modularPageList = (List<ITransferObject>) bean.getList(criteria_mBean);

			for (int i = 0; i < modularPageList.size(); i++) {
				ModularPage mp = (ModularPage) modularPageList.get(i);
				
				Criteria criteria_mdBean = new Criteria();
				criteria_mdBean.addEqualExpression(mdBean.getFieldName(ICMSAlias.MODULAR_PAGE_DETAIL_MODULAR_PAGE_ID),mp.getId());
				criteria_mdBean.addEqualExpression(mdBean.getFieldName(ICMSAlias.MODULAR_PAGE_DETAIL_LANGUAGE_ID),ControllerUtil.getCurrentLanguage().getId());
				List<ITransferObject> modularPageDetailList = (List<ITransferObject>) mdBean.getList(criteria_mdBean);
				if (modularPageDetailList.isEmpty()) {
					logger.warning("La pagina modular " + mp.getAlias() + " no esta internacionalizada.");
				} else {
					ModularPageDetail mpd = (ModularPageDetail)modularPageDetailList.get(0);
	
					Criteria criteria_moBean = new Criteria();
					criteria_moBean.addEqualExpression(moBean.getFieldName(ICMSAlias.MODULAR_PAGE_OPTION_MODULAR_PAGE_ID),mp.getId());
					criteria_moBean.addEqualExpression(moBean.getFieldName(ICMSAlias.MODULAR_PAGE_OPTION_ACTIVE),true);
					criteria_moBean.addOrder(moBean.getFieldName(ICMSAlias.MODULAR_PAGE_OPTION_POSITION));
					List<ITransferObject> modularPageOptionList = (List<ITransferObject>) moBean.getList(criteria_moBean);
	
					if (modularPageOptionList.isEmpty()) {
						logger.warning("La pagina modular " + mp.getAlias() + " esta vacia.");
					}
					
					List<ModularPageOptionHandler> modularPageOptionHandlerList = new ArrayList<ModularPageOptionHandler>();
					for (int j = 0; j < modularPageOptionList.size(); j++) {
						ModularPageOption mpo = (ModularPageOption) modularPageOptionList.get(j);
						if ( mpo.getIdent() == null ) {
							getLogger().error("LA OPCION " + mpo.getAlias() + " DE LA PAGINA MODULAR " + mp.getAlias() + " NO REFERENCIA A NINGUN ELEMENTO");
							continue;
						}						
						Criteria criteria_modBean = new Criteria();
						criteria_modBean.addEqualExpression(modBean.getFieldName(ICMSAlias.MODULAR_PAGE_OPTION_DETAIL_MODULAR_PAGE_OPTION_ID),mpo.getId());
						criteria_modBean.addEqualExpression(modBean.getFieldName(ICMSAlias.MODULAR_PAGE_OPTION_DETAIL_LANGUAGE_ID),ControllerUtil.getCurrentLanguage().getId());
						List<ITransferObject> modularPageOptionDetailList = (List<ITransferObject>) modBean.getList(criteria_modBean);
						if (modularPageOptionDetailList.isEmpty()) {
							logger.warning("La opcion de la pagina modular " + mpo.getAlias() + " no esta internacionalizada.");
						}else{
							ModularPageOptionDetail mpod = (ModularPageOptionDetail) modularPageOptionDetailList.get(0);
							if (mpod.getModular_page_option().isActive()) {
								ModularPageOptionHandler mpoh = new ModularPageOptionHandler(mpod);
								if (mpoh.getContent()!=null) {
									modularPageOptionHandlerList.add(mpoh);
								}
							}
						}
						modularPageOptionDetailList = null;
					}
					modularPageOptionList = null;
					modularPageDetailList = null;
					
					vu.put("module", new ModularPageHandler(mpd));
					vu.put("modules", modularPageOptionHandlerList);
	
					// Cargar datos comunes a todas las paginas
					context.changeSection(vu, mp.getSection());
	
					logger.info(" Generando Página Modular '" + mp.getAlias()+ "'.");
					if (mp.isHomepage()){
						if (mp.getModularType()==null){
							generate(vu, Templates.HOME, mp.getAlias());
						}else{
							String template_name = Templates.MODULAR_TYPES.getTemplateName();
							template_name = template_name.replaceAll("%TYPE%", ""+mp.getModularType().ordinal()); 
							generate(vu, Templates.HOME, template_name, mp.getAlias());
						}
					}else{
						if (mp.getModularType()==null){
							generate(vu, Templates.MODULAR, mp.getAlias());
						}else{
							String template_name = Templates.MODULAR_TYPES.getTemplateName();
							template_name = template_name.replaceAll("%TYPE%", ""+mp.getModularType().ordinal()); 
							generate(vu, Templates.MODULAR, template_name, mp.getAlias());
						}
					}
					vu.remove("module");
					vu.remove("modules");
					modularPageOptionHandlerList = null;
				}
			}
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	public void generate() {
		generate(null);
	}
	
}
