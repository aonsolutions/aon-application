package com.code.aon.ui.groupware.controller;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.User;
import com.code.aon.groupware.FavoriteCategory;
import com.code.aon.groupware.dao.IGroupWareAlias;
import com.code.aon.groupware.enumeration.AlarmSource;
import com.code.aon.groupware.enumeration.AlarmStatus;
import com.code.aon.groupware.enumeration.DelayTime;
import com.code.aon.groupware.enumeration.NoticeStatus;
import com.code.aon.groupware.enumeration.NoticeType;
import com.code.aon.groupware.enumeration.Priority;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.config.util.UserUtils;

public class GroupWareCollectionsController {

	public List<SelectItem> getAlarmStatus() {
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		LinkedList<SelectItem> alarmStatusList = new LinkedList<SelectItem>();
		AlarmStatus[] alarmStatuses = AlarmStatus.values();
		for (int i = 0; i < alarmStatuses.length; i++) {
			AlarmStatus alarmStatus = alarmStatuses[i];
			String name = alarmStatus.getName(locale);
			SelectItem item = new SelectItem(alarmStatus, name);
			alarmStatusList.add(item);
		}
		return alarmStatusList;
	}

	public List<SelectItem> getAlarmSources() {
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		LinkedList<SelectItem> alarmSourcesList = new LinkedList<SelectItem>();
		AlarmSource[] alarmSources = AlarmSource.values();
		for (int i = 0; i < alarmSources.length; i++) {
			AlarmSource alarmSource = alarmSources[i];
			String name = alarmSource.getName(locale);
			SelectItem item = new SelectItem(alarmSource, name);
			alarmSourcesList.add(item);
		}
		return alarmSourcesList;
	}

	public List<SelectItem> getNoticeStatuses() {
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		LinkedList<SelectItem> noticeStatusList = new LinkedList<SelectItem>();
		NoticeStatus[] noticeStatuses = NoticeStatus.values();
		for (int i = 0; i < noticeStatuses.length; i++) {
			NoticeStatus noticeStatus = noticeStatuses[i];
			String name = noticeStatus.getName(locale);
			SelectItem item = new SelectItem(noticeStatus, name);
			noticeStatusList.add(item);
		}
		return noticeStatusList;
	}

	public List<SelectItem> getNoticeTypes() {
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		LinkedList<SelectItem> noticeTypesList = new LinkedList<SelectItem>();
		NoticeType[] noticeTypes = NoticeType.values();
		for (int i = 0; i < noticeTypes.length; i++) {
			NoticeType noticeType = noticeTypes[i];
			String name = noticeType.getName(locale);
			SelectItem item = new SelectItem(noticeType, name);
			noticeTypesList.add(item);
		}
		return noticeTypesList;
	}
	
	public List<SelectItem> getPriorities() {
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		LinkedList<SelectItem> prioritiesList = new LinkedList<SelectItem>();
		Priority[] priorities = Priority.values();
		for (int i = 0; i < priorities.length; i++) {
			Priority priority = priorities[i];
			String name = priority.getName(locale);
			SelectItem item = new SelectItem(priority, name);
			prioritiesList.add(item);
		}
		return prioritiesList;
	}
	
	public List<SelectItem> getDelayTimes() {
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		LinkedList<SelectItem> delayTimesList = new LinkedList<SelectItem>();
		DelayTime[] delayTimes = DelayTime.values();
		for (int i = 0; i < delayTimes.length; i++) {
			DelayTime delay = delayTimes[i];
			String name = delay.getName(locale);
			SelectItem item = new SelectItem(delay, name);
			delayTimesList.add(item);
		}
		return delayTimesList;
	}
	
	@SuppressWarnings("unchecked")
	public List<SelectItem> getFavoriteCategories() throws ManagerBeanException{
		List<SelectItem> favoriteCategoriesList = new LinkedList<SelectItem>();
		IManagerBean favoriteCategoriesBean = BeanManager.getManagerBean(FavoriteCategory.class);
		User user = UserUtils.getLoggedUser();
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(favoriteCategoriesBean.getFieldName(IGroupWareAlias.FAVORITE_CATEGORY_USER_ID), user.getId());
		criteria.addOrder(favoriteCategoriesBean.getFieldName(IGroupWareAlias.FAVORITE_CATEGORY_DESCRIPTION));
		Iterator iter = favoriteCategoriesBean.getList(criteria).iterator();
		while(iter.hasNext()){
			FavoriteCategory category = (FavoriteCategory)iter.next();
			SelectItem item = new SelectItem(category.getId(), category.getDescription());
			favoriteCategoriesList.add(item);
		}
		return favoriteCategoriesList;
	}
}