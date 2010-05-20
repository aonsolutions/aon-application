package com.code.aon.ui.cms.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.ListDataModel;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.cms.Language;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.cms.Constants;
import com.code.aon.ui.cms.event.I18NControllerListener;


public class I18NController {

	private final static Logger LOGGER = LoggerFactory.getLogger(I18NController.class);
	
	private Language currentLanguage;

	private List<I18NControllerListener> listeners = new ArrayList<I18NControllerListener>();

	private ListDataModel language_list;
	
	public I18NController() {
		try {
			init();
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	public void onReloadLanguages(ActionEvent event) {
		try {
			init();
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	private void init() throws ManagerBeanException {
		LOGGER.info("CARGANDO LISTA DE IDIOMAS DISPONIBLES...");
		IManagerBean languageBean = BeanManager.getManagerBean(Language.class);
		Criteria criteria = new Criteria();
		criteria.addOrder(languageBean.getFieldName(ICMSAlias.LANGUAGE_POSITION));
		List<ITransferObject> list = languageBean.getList(criteria);
		language_list = new ListDataModel(list);
	
		criteria = new Criteria();
		criteria.addEqualExpression(languageBean.getFieldName(ICMSAlias.LANGUAGE_DEFAULT_LANGUAGE), true);
		list = languageBean.getList(criteria);
		if (list.size() > 0) {
			setCurrentLanguage((Language)list.get(0));
		}
	}

	public Language getCurrentLanguage() {
		return currentLanguage;
	}

	public void setCurrentLanguage(Language currentLanguage) {
		this.currentLanguage = currentLanguage;
		HttpSession session = (HttpSession)FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		session.setAttribute(Constants.SESSION_CURRENT_LANGUAGE, currentLanguage);
		LOGGER.info("SESSION CURRENT LANGUAGE: {}", currentLanguage.getDescription());
	}
	
	public void addListener(I18NControllerListener listener){
		listeners.add(listener);
	}
	
	public void removeListener(I18NControllerListener listener){
		listeners.remove(listener);
	}

	public void fireEvent(){
		if (listeners!= null && !listeners.isEmpty()){
			Iterator<I18NControllerListener> iter = listeners.iterator();
			while (iter.hasNext()) {
				iter.next().languajeChanged();
			}
		}
	}

	public ListDataModel getLanguageList() {
		return language_list;
	}

	public void setLanguageList(ListDataModel languageList) {
		this.language_list = languageList;
	}

	public void onChangeLanguage(ActionEvent event) throws ManagerBeanException {
		Language language = (Language)language_list.getRowData();
		LOGGER.info("CHANGE LANGUAGE TO: ", language.getDescription());
		setCurrentLanguage(language);
		fireEvent();
	}

}
