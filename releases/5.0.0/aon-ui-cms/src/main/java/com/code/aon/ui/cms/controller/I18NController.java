package com.code.aon.ui.cms.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.ListDataModel;
import javax.servlet.http.HttpSession;

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

	private Language currentLanguage;

	private List<I18NControllerListener> listeners = new ArrayList<I18NControllerListener>();

	private ListDataModel language_list;
	
	public I18NController() {
		try {
			init();
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		}
	}

	private void init() throws ManagerBeanException {
		System.out.println(">>>>>> ----------------------------------------");
		System.out.println(">>>>>> CARGANDO LISTA DE IDIOMAS DISPONIBLES...");
		System.out.println(">>>>>> ----------------------------------------");
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
		System.out.println(">>>>>>>>>> SESSION CURRENT LANGUAGE " + currentLanguage.getDescription());
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
		System.out.println(">>>>>>>>>>>>>>> CHANGE LANGUAGE TO: " + language.getDescription());
		setCurrentLanguage(language);
		fireEvent();
	}

}
