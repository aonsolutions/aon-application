package com.code.aon.ui.cms.velocity.attribute;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.cms.Language;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.cms.util.ControllerUtil;

public class BulletinSuscribeHandler {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(BulletinSuscribeHandler.class);

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
			LOGGER.error(e.getMessage(), e);
		}
		return list;
	}
	
}
