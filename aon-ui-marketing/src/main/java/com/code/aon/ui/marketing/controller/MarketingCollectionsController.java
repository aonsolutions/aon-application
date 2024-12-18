package com.code.aon.ui.marketing.controller;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.model.SelectItem;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.marketing.enumeration.ActionMediaType;
import com.code.aon.marketing.enumeration.ActionTargetStatus;
import com.code.aon.marketing.enumeration.NewsType;
import com.code.aon.marketing.enumeration.NewsletterLayout;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.Category;
import com.code.aon.registry.enumeration.CategoryType;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.registry.controller.RegistryCollectionsController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class MarketingCollectionsController implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private List<SelectItem> actionMediaTypes;
	
	private List<SelectItem> actionTargetStatuses;
	
	private List<SelectItem> newsletterLayouts;
	
	private List<SelectItem> newsTypes;
	
	/**
	 * Gets the action media types.
	 * 
	 * @return the action media types
	 */
	public List<SelectItem> getActionMediaTypes() {
		if ( actionMediaTypes == null ) {
			Locale locale = AonUtil.getCurrentLocale();
			actionMediaTypes = new LinkedList<SelectItem>();
			for (ActionMediaType mediaType : ActionMediaType.values()) {
				switch ( mediaType ) {
					case PHONE:
					case EMAIL:
					case MAIL:
					case INCOMING:
					case NEWSLETTER:
						String name = mediaType.getName(locale);
						SelectItem item = new SelectItem(mediaType, name);
						actionMediaTypes.add(item);
						break;
					case SMS:
					case FAX:
						break;
				}
			}
		}
		return actionMediaTypes;
	}	

	/**
	 * Gets the action target statuses.
	 * 
	 * @return the action target statuses
	 */
	public List<SelectItem> getActionTargetStatuses() {
		if ( actionTargetStatuses == null ) {
			Locale locale = AonUtil.getCurrentLocale();
			actionTargetStatuses = new LinkedList<SelectItem>();
			for (ActionTargetStatus status : ActionTargetStatus.values()) {
				String name = status.getName(locale);
				SelectItem item = new SelectItem(status, name);
				actionTargetStatuses.add(item);
			}
		}
		return actionTargetStatuses;
	}	

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<SelectItem> getChannels() throws ManagerBeanException {
		IManagerBean categoryBean = BeanManager.getManagerBean(Category.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(categoryBean.getFieldName(IEntityAlias.CATEGORY_TYPE), CategoryType.ARTICLE);
		UserUtils.getInstance().addNullableScopeExpression(criteria, categoryBean.getFieldName(IEntityAlias.CATEGORY_SCOPE_ID));
		criteria.addOrder(categoryBean.getFieldName(IEntityAlias.CATEGORY_NAME));
		return RegistryCollectionsController.getCategoryList( (List) categoryBean.getList(criteria));
	}
	
	public Category getCategory() {
		return null;
	}

	public void setCategory( Category category ) {
	}		

	/**
	 * Gets the newsletter layouts.
	 * 
	 * @return the newsletter layouts
	 */
	public List<SelectItem> getNewsletterLayouts() {
		if ( newsletterLayouts == null ) {
			Locale locale = AonUtil.getCurrentLocale();
			newsletterLayouts = new LinkedList<SelectItem>();
			for (NewsletterLayout layout : NewsletterLayout.values()) {
				String name = layout.getName(locale);
				SelectItem item = new SelectItem(layout, name);
				newsletterLayouts.add(item);
			}
		}
		return newsletterLayouts;
	}	

	/**
	 * Gets the news types.
	 * 
	 * @return the news types
	 */
	public List<SelectItem> getNewsTypes() {
		if ( newsTypes == null ) {
			Locale locale = AonUtil.getCurrentLocale();
			newsTypes = new LinkedList<SelectItem>();
			for (NewsType type : NewsType.values()) {
				String name = type.getName(locale);
				SelectItem item = new SelectItem(type, name);
				newsTypes.add(item);
			}
		}
		return newsTypes;
	}	

}
