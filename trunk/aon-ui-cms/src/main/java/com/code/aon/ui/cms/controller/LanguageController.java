package com.code.aon.ui.cms.controller;

import java.util.ArrayList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.ListDataModel;

import com.code.aon.cms.Language;
import com.code.aon.cms.LanguageObject;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.cms.enumeration.Languages;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.menu.jsf.MenuEvent;

public class LanguageController extends BasicController {

	private ListDataModel languages;
	
	private int rowCount = 0;

	public ListDataModel getLanguages() throws ManagerBeanException {
		if (languages == null) {
			reorderLanguages();
			languages = obtainLanguageList();
		}
		return languages;
	}

	private ListDataModel obtainLanguageList() throws ManagerBeanException {
		List<LanguageObject> languageList = new ArrayList<LanguageObject>();
		// Primero cargamos los idiomas que esten en base de datos.
		IManagerBean languageBean = BeanManager.getManagerBean(Language.class);
		Criteria criteria = new Criteria();
		criteria.addOrder(languageBean.getFieldName(ICMSAlias.LANGUAGE_POSITION));
		List<ITransferObject> list = languageBean.getList(criteria);
		String locales = "";
		rowCount = 0;
		for (int i = 0; i < list.size(); i++) {
			++rowCount;
			Language language = (Language) list.get(i);
			LanguageObject lo = new LanguageObject();
			lo.setDefaultLanguage(language.isDefaultLanguage());
			lo.setDescription(language.getDescription());
			lo.setLanguage(language.getLanguage());
			lo.setPosition(language.getPosition());
			lo.setSelected(true);
			languageList.add(lo);
			locales += language.getLanguage().getLocale().getLanguage() + "|";
		}

		// Segundo cargamos los idiomas del enumeration que no existan ya en la lista.
		for (Languages languages : Languages.values()) {
			if (locales.indexOf(languages.getLocale().getLanguage()) < 0) {
				LanguageObject lo = new LanguageObject();
				lo.setLanguage(languages);
				lo.setDescription(languages.getName());
				lo.setDefaultLanguage(false);
				lo.setSelected(false);
				lo.setPosition(rowCount++);
				languageList.add(lo);
			}
		}
		return new ListDataModel(languageList);
	}

	public void selectedLanguageChanged(ValueChangeEvent event) throws ManagerBeanException, ExpressionException {
		Boolean selectedLanguage = (Boolean) event.getNewValue();
		LanguageObject languageObject = (LanguageObject) languages.getRowData();
		languageObject.setSelected(selectedLanguage.booleanValue());
		updateSelectedLanguage(languageObject, selectedLanguage.booleanValue());
	}

	public void defaultLanguageChanged(ValueChangeEvent event) throws ManagerBeanException, ExpressionException {
		boolean selected = ((Boolean)event.getNewValue()).booleanValue();
		LanguageObject languageObject = (LanguageObject) languages.getRowData();
		if (selected) {
			languageObject.setSelected(true);
			updateDefaultLanguage(languageObject);
			languageObject.setDefaultLanguage(true);
		}
	}

	public void textChanged(ValueChangeEvent event) throws ManagerBeanException, ExpressionException {
		String description = (String) event.getNewValue();
		LanguageObject languageObject = (LanguageObject) languages.getRowData();
		if (description != null && !description.trim().equals("") && languageObject.isSelected()) {
			languageObject.setDescription(description);
			updateDescriptionLanguage(languageObject);
		}
	}

	private void updateSelectedLanguage(LanguageObject selectedLanguage, boolean create) throws ManagerBeanException, ExpressionException {
		IManagerBean languageBean = BeanManager.getManagerBean(Language.class);
		Criteria criteria = new Criteria();
		criteria.addExpression(languageBean.getFieldName(ICMSAlias.LANGUAGE_LANGUAGE), ""+selectedLanguage.getLanguage().ordinal());
		List<ITransferObject> list = languageBean.getList(criteria);
		Language language;
		if (list.size() > 0) {
			language = (Language) list.get(0);
			languageBean.remove(language);
		} else {
			language = new Language();
			language.setId(selectedLanguage.getLanguage().ordinal());
			language.setDescription(selectedLanguage.getDescription());
			language.setLanguage(selectedLanguage.getLanguage());
			language.setPosition(selectedLanguage.getPosition());
			languageBean.insert(language);
		}
	}

	@SuppressWarnings("unchecked")
	private void reorderLanguages() throws ManagerBeanException {
		IManagerBean languageBean = BeanManager.getManagerBean(Language.class);
		Criteria criteria = new Criteria();
		criteria.addOrder(languageBean.getFieldName(ICMSAlias.LANGUAGE_POSITION));
		List<ITransferObject> list = languageBean.getList(criteria);
		int count = 0;
		for (int i = 0; i < list.size(); i++) {
			Language language = (Language)list.get(i);
			int oldPosition = language.getPosition();
			int newPosition = count++;
			if (oldPosition != newPosition) {
				language.setPosition(newPosition);
				languageBean.update(language);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void updateDefaultLanguage(LanguageObject defaultLanguage) throws ManagerBeanException, ExpressionException {
		// Quitamos el defaultLanguage de todos los idiomas de la base de datos y el modelo.
		List<ITransferObject> list = (List<ITransferObject>)languages.getWrappedData();
		for (int i = 0; i < list.size(); i++) {
			LanguageObject languageObject = (LanguageObject)list.get(i);
			languageObject.setDefaultLanguage(false);
		}
		IManagerBean languageBean = BeanManager.getManagerBean(Language.class);
		list = (List<ITransferObject>)languageBean.getList(null);
		for (int i = 0; i < list.size(); i++) {
			Language language = (Language)list.get(i);
			language.setDefaultLanguage(false);
			languageBean.update(language);
		}

		// Ponemos default al seleccionado
		Criteria criteria = new Criteria();
		criteria.addExpression(languageBean.getFieldName(ICMSAlias.LANGUAGE_LANGUAGE), ""+defaultLanguage.getLanguage().ordinal());
		list = languageBean.getList(criteria);
		Language language;
		if (list.size() > 0) {
			language = (Language) list.get(0);
			language.setDefaultLanguage(true);
			languageBean.update(language);
		} else {
			language = new Language();
			language.setId(defaultLanguage.getLanguage().ordinal());
			language.setDefaultLanguage(true);
			language.setDescription(defaultLanguage.getDescription());
			language.setLanguage(defaultLanguage.getLanguage());
			language.setPosition(defaultLanguage.getPosition());
			languageBean.insert(language);
		}
	}

	private void updateDescriptionLanguage(LanguageObject currentLanguage) throws ManagerBeanException, ExpressionException {
		IManagerBean languageBean = BeanManager.getManagerBean(Language.class);
		Criteria criteria = new Criteria();
		criteria.addExpression(languageBean.getFieldName(ICMSAlias.LANGUAGE_LANGUAGE), ""+currentLanguage.getLanguage().ordinal());
		List<ITransferObject> list = languageBean.getList(criteria);
		Language language;
		if (list.size() > 0) {
			language = (Language) list.get(0);
			language.setDescription(currentLanguage.getDescription());
			languageBean.update(language);
		}
	}

	public int getRowCount() {
		return rowCount;
	}

	@SuppressWarnings("unchecked")
	private void move( LanguageObject languageObject, int movement ) throws ManagerBeanException, ExpressionException {
		int oldPosition = languageObject.getPosition();
		int newPosition = oldPosition + movement;
		languageObject.setPosition(newPosition);
		IManagerBean languageBean = BeanManager.getManagerBean(Language.class);
		Criteria criteria = new Criteria();
		criteria.addExpression(languageBean.getFieldName(ICMSAlias.LANGUAGE_LANGUAGE), ""+languageObject.getLanguage().ordinal());
		List<ITransferObject> list = languageBean.getList(criteria);
		if (list.size() > 0) {
			Language language = (Language)list.get(0);
			language.setPosition(newPosition);
			languageBean.update(language);
		}
    	List<LanguageObject> listObjects = (List<LanguageObject>) this.languages.getWrappedData();
		LanguageObject languageObjectMoved = listObjects.get( newPosition );
		languageObjectMoved.setPosition( oldPosition );
		criteria = new Criteria();
		criteria.addExpression(languageBean.getFieldName(ICMSAlias.LANGUAGE_LANGUAGE), ""+languageObjectMoved.getLanguage().ordinal());
		list = languageBean.getList(criteria);
		if (list.size() > 0) {
			Language language = (Language)list.get(0);
			language.setPosition(oldPosition);
			languageBean.update(language);
		}
		listObjects.set( newPosition, languageObject );
		listObjects.set( oldPosition, languageObjectMoved );
	}
	
    public void onMoveUp(ActionEvent event) throws ManagerBeanException, ExpressionException {
    	move((LanguageObject) this.languages.getRowData(), -1);
    }

    public void onMoveDown(ActionEvent event) throws ManagerBeanException, ExpressionException {
    	move((LanguageObject) this.languages.getRowData(), 1);    	
    }

}
