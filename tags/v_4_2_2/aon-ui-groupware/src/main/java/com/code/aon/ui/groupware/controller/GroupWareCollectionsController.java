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
	
	private List<SelectItem> alarmStatuses;
	
	private List<SelectItem> alarmSources;
	
	private List<SelectItem> noticeStatuses;
	
	private List<SelectItem> noticeTypes;
	
	private List<SelectItem> priorities;
	
	private List<SelectItem> delayTimes;

	public List<SelectItem> getAlarmStatus() {
		if ( alarmStatuses == null ) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			alarmStatuses = new LinkedList<SelectItem>();
			for( AlarmStatus alarmStatus : AlarmStatus.values() ) {
				String name = alarmStatus.getName(locale);
				SelectItem item = new SelectItem(alarmStatus, name);
				alarmStatuses.add(item);				
			}
		}
		return alarmStatuses;
	}

	public List<SelectItem> getAlarmSources() {
		if ( alarmSources == null ) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			alarmSources = new LinkedList<SelectItem>();
			for( AlarmSource alarmSource : AlarmSource.values() ) {
				String name = alarmSource.getName(locale);
				SelectItem item = new SelectItem(alarmSource, name);
				alarmSources.add(item);				
			}
		}
		return alarmSources;
	}

	public List<SelectItem> getNoticeStatuses() {
		if ( noticeStatuses == null ) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			noticeStatuses = new LinkedList<SelectItem>();
			for( NoticeStatus noticeStatus : NoticeStatus.values() ) {
				String name = noticeStatus.getName(locale);
				SelectItem item = new SelectItem(noticeStatus, name);
				noticeStatuses.add(item);
			}
		}
		return noticeStatuses;
	}

	public List<SelectItem> getNoticeTypes() {
		if ( noticeTypes == null ) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			noticeTypes = new LinkedList<SelectItem>();
			for( NoticeType noticeType : NoticeType.values() ) {
				String name = noticeType.getName(locale);
				SelectItem item = new SelectItem(noticeType, name);
				noticeTypes.add(item);
			}
		}
		return noticeTypes;
	}
	
	public List<SelectItem> getPriorities() {
		if ( priorities == null ) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			priorities = new LinkedList<SelectItem>();
			for( Priority priority : Priority.values() ) {
				String name = priority.getName(locale);
				SelectItem item = new SelectItem(priority, name);
				priorities.add(item);
			}
		}
		return priorities;
	}
	
	public List<SelectItem> getDelayTimes() {
		if ( delayTimes == null ) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			delayTimes = new LinkedList<SelectItem>();
			for( DelayTime delay : DelayTime.values() ) {
				String name = delay.getName(locale);
				SelectItem item = new SelectItem(delay, name);
				delayTimes.add(item);
			}
		}
		return delayTimes;
	}
	
	@SuppressWarnings("unchecked")
	public List<SelectItem> getFavoriteCategories() throws ManagerBeanException{
		List<SelectItem> favoriteCategoriesList = new LinkedList<SelectItem>();
		IManagerBean favoriteCategoriesBean = BeanManager.getManagerBean(FavoriteCategory.class);
		User user = UserUtils.getInstance().getLoggedUser();
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(favoriteCategoriesBean.getFieldName(IGroupWareAlias.FAVORITE_CATEGORY_USER_ID), user.getId());
		criteria.addOrder(favoriteCategoriesBean.getFieldName(IGroupWareAlias.FAVORITE_CATEGORY_DESCRIPTION));
		Iterator iter = favoriteCategoriesBean.getList(criteria).iterator();
		while(iter.hasNext()){
			FavoriteCategory category = (FavoriteCategory)iter.next();
			SelectItem item = new SelectItem(category, category.getDescription());
			favoriteCategoriesList.add(item);
		}
		return favoriteCategoriesList;
	}
	
	public FavoriteCategory getFavoriteCategory() {
		return null;
	}

	public void setFavoriteCategory( FavoriteCategory favoriteCategory ) {
	}
	

}