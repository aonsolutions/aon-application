package com.code.aon.ui.cms.controller;

import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpSession;

import com.code.aon.cms.Config;
import com.code.aon.cms.ConfigDetail;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.cms.Constants;
import com.code.aon.ui.cms.util.ControllerUtil;

public class ConfigController extends BasicI18nController{

	private Config currentConfig;
	
	public ConfigController() {
		try {
			init();
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		}
	}

	public void onInit(ActionEvent arg0) {
		initializeModel();
	}
	
	private void init() throws ManagerBeanException {
		System.out.println(">>>>>> ----------------------------------------");
		System.out.println(">>>>>> CARGANDO CONFIGURACION...");
		System.out.println(">>>>>> ----------------------------------------");
		IManagerBean configBean = BeanManager.getManagerBean(Config.class);
		List<ITransferObject> list = configBean.getList(null);
		System.out.println(">>>>>>>>>>>>>>>>> CONFIGS: " + list.size());
		if (list.size() > 0) {
			setCurrentConfig((Config)list.get(0));
		}
		System.out.println(">>>>>> DOMINIO: " + currentConfig.getDomain());
		System.out.println(">>>>>> TEMPLATE: " + currentConfig.getTemplate());
		System.out.println(">>>>>> PREVIEW: " + currentConfig.getPreview_host());
		System.out.println(">>>>>> HOST: " + currentConfig.getHost());
	}

	public Config getCurrentConfig() {
		return currentConfig;
	}

	public ConfigDetail getCurrentConfigDetail() {
		try {
			IManagerBean cdBean = BeanManager.getManagerBean(ConfigDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(cdBean.getFieldName(ICMSAlias.CONFIG_DETAIL_CONFIG_ID), currentConfig.getId());
			criteria.addEqualExpression(cdBean.getFieldName(ICMSAlias.CONFIG_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
			List<ITransferObject> list = cdBean.getList(criteria);
			if (list.size() > 0) {
				ConfigDetail cd = (ConfigDetail)list.get(0);
				return cd;
			}
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void setCurrentConfig(Config currentConfig) {
		this.currentConfig = currentConfig;
		HttpSession session = (HttpSession)FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		session.setAttribute(Constants.SESSION_CONFIG, currentConfig);
	}

}
