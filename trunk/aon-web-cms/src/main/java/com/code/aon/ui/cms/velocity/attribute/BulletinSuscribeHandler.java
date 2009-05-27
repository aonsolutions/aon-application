package com.code.aon.ui.cms.velocity.attribute;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.code.aon.cms.Language;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.cms.util.ControllerUtil;

public class BulletinSuscribeHandler {
	
	private static final Logger LOGGER = Logger.getLogger(BulletinSuscribeHandler.class.getName());

	private String from;
	
	private ArrayList<LanguageHandler> languages;
	
	public BulletinSuscribeHandler () {
		from = ControllerUtil.getCurrentConfig().getFrom_email();
		languages = getActiveLanguages();
	}

	public String getFrom() {
		return from;
	}

	public ArrayList<LanguageHandler> getLanguages() {
		return languages;
	}

	private ArrayList<LanguageHandler> getActiveLanguages() {
		ArrayList<LanguageHandler> list = new ArrayList<LanguageHandler>();
		try {
			IManagerBean bean = BeanManager.getManagerBean(Language.class);
			Criteria criteria = new Criteria();
			criteria.addOrder(bean.getFieldName(ICMSAlias.LANGUAGE_POSITION));
			List<ITransferObject> l = (List<ITransferObject>)bean.getList(criteria);
			for (int i = 0; i < l.size(); i++) {
				Language lang = (Language)l.get(i);
				LanguageHandler lh = new LanguageHandler(lang);
				list.add(lh);
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
		return list;
	}
	
}
