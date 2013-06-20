package com.code.aon.ui.commercial.controller;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.ObjectUtils;

import com.code.aon.commercial.CommercialActivity;
import com.code.aon.commercial.Commission;
import com.code.aon.commercial.Question;
import com.code.aon.commercial.QuestionValue;
import com.code.aon.commercial.enumeration.Advertising;
import com.code.aon.commercial.enumeration.CommercialTrackingStatus;
import com.code.aon.commercial.enumeration.OfferDetailCommissionStatus;
import com.code.aon.commercial.enumeration.OfferDetailStatus;
import com.code.aon.commercial.enumeration.OfferStatus;
import com.code.aon.commercial.enumeration.OfferType;
import com.code.aon.commercial.enumeration.ProjectSource;
import com.code.aon.commercial.enumeration.ProjectStatus;
import com.code.aon.commercial.enumeration.QuestionType;
import com.code.aon.commercial.enumeration.TargetItemStatus;
import com.code.aon.commercial.enumeration.TargetSellerStatus;
import com.code.aon.commercial.enumeration.TargetStatus;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

/**
 * Controller used to get Collections related with clasess in <code>com.code.aon.commercial</code>
 * 
 * @author Consulting & Development. Joseba Urkiri - 6-sept-2006
 */
public class CommercialCollectionsController {

	private List<SelectItem> offerStatuses;
	
	private List<SelectItem> offerTypes;
	
	private List<SelectItem> offerDetailStatuses;
	
	private List<SelectItem> commercialTrackingStatuses;
	
	private List<SelectItem> projectStatuses;
	
	private List<SelectItem> projectSources;
	
	private List<SelectItem> targetItemStatuses;
	
	private List<SelectItem> targetSellerStatuses;
	
	private List<SelectItem> advertisings;

	private List<SelectItem> targetStatuses;
	
	private List<SelectItem> activities;
	
	private List<SelectItem> offerDetailCommissionStatuses;
	
	private List<SelectItem> questionTypes;

	
	/**
	 * Gets the offer statuses.
	 * 
	 * @return the offer statuses
	 */
	public List<SelectItem> getOfferStatuses() {
		if ( offerStatuses == null ) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			offerStatuses = new LinkedList<SelectItem>();
			for (OfferStatus status : OfferStatus.values()) {
				String name = status.getName(locale);
				SelectItem item = new SelectItem(status, name);
				offerStatuses.add(item);
			}
		}
		return offerStatuses;
	}
	
	/**
	 * Gets the offer types.
	 * 
	 * @return the offer types
	 */
	public List<SelectItem> getOfferTypes() {
		if ( offerTypes == null ) {
			boolean audatexEnabled = (Boolean)AonUtil.getConfigurationController().getBean()
				.get(ICommercialConstants.CONFIG_OFFER_BEAN)
				.get(ICommercialConstants.SHOW_TAS_DATA);
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			offerTypes = new LinkedList<SelectItem>();
			for (OfferType type : OfferType.values()) {
				String name = type.getName(locale);
				SelectItem item = new SelectItem(type, name);
				if ((type != OfferType.AUDATEX) || (type == OfferType.AUDATEX && audatexEnabled)) {  
					offerTypes.add(item);
				}
			}
		}
		return offerTypes;
	}

	/**
	 * Gets the offer detail statuses.
	 * 
	 * @return the offer detail statuses
	 */
	public List<SelectItem> getOfferDetailStatuses() {
		if ( offerDetailStatuses == null ) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			offerDetailStatuses = new LinkedList<SelectItem>();
			for (OfferDetailStatus status : OfferDetailStatus.values()) {
				String name = status.getName(locale);
				SelectItem item = new SelectItem(status, name);
				offerDetailStatuses.add(item);
			}
		}
		return offerDetailStatuses;
	}

	/**
	 * Gets the commercial tracking statuses.
	 * 
	 * @return the commercial tracking statuses
	 */
	public List<SelectItem> getCommercialTrackingStatuses() {
		if ( commercialTrackingStatuses == null ) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			commercialTrackingStatuses = new LinkedList<SelectItem>();
			for (CommercialTrackingStatus status : CommercialTrackingStatus.values()) {
				String name = status.getName(locale);
				SelectItem item = new SelectItem(status, name);
				commercialTrackingStatuses.add(item);
			}
		}
		return commercialTrackingStatuses;
	}

	/**
	 * Gets the project statuses.
	 * 
	 * @return the project statuses
	 */
	public List<SelectItem> getProjectStatuses() {
		if ( projectStatuses == null ) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			projectStatuses = new LinkedList<SelectItem>();
			for (ProjectStatus status : ProjectStatus.values()) {
				String name = status.getName(locale);
				SelectItem item = new SelectItem(status, name);
				projectStatuses.add(item);
			}
		}
		return projectStatuses;
	}

	/**
	 * Gets the project sources.
	 * 
	 * @return the project sources
	 */
	public List<SelectItem> getProjectSources() {
		if ( projectSources == null ) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			projectSources = new LinkedList<SelectItem>();
			for (ProjectSource source : ProjectSource.values()) {
				String name = source.getName(locale);
				SelectItem item = new SelectItem(source, name);
				projectSources.add(item);
			}
		}
		return projectSources;
	}
	
	/**
	 * Gets the target item statuses.
	 * 
	 * @return the target item statuses
	 */
	public List<SelectItem> getTargetItemStatuses() {
		if ( targetItemStatuses == null ) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			targetItemStatuses = new LinkedList<SelectItem>();
			for (TargetItemStatus status : TargetItemStatus.values()) {
				String name = status.getName(locale);
				SelectItem item = new SelectItem(status, name);
				targetItemStatuses.add(item);
			}
		}
		return targetItemStatuses;
	}

	/**
	 * Gets the target item statuses.
	 * 
	 * @return the target item statuses
	 */
	public List<SelectItem> getTargetSellerStatuses() {
		if ( targetSellerStatuses == null ) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			targetSellerStatuses = new LinkedList<SelectItem>();
			for (TargetSellerStatus status : TargetSellerStatus.values()) {
				String name = status.getName(locale);
				SelectItem item = new SelectItem(status, name);
				targetSellerStatuses.add(item);
			}
		}
		return targetSellerStatuses;
	}
	
	/**
	 * Gets the advertisings.
	 * 
	 * @return the advertisings
	 */
	public List<SelectItem> getAdvertisings() {
		if ( advertisings == null ) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			advertisings = new LinkedList<SelectItem>();
			for (Advertising advertising : Advertising.values()) {
				String name = advertising.getName(locale);
				SelectItem item = new SelectItem(advertising, name);
				advertisings.add(item);
			}
		}
		return advertisings;
	}

	/**
	 * Gets the target statuses.
	 * 
	 * @return the target statuses
	 */
	public List<SelectItem> getTargetStatuses() {
		if ( targetStatuses == null ) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			targetStatuses = new LinkedList<SelectItem>();
			for( TargetStatus status : TargetStatus.values() ) {
				String name = status.getName(locale);
				SelectItem item = new SelectItem(status, name);
				targetStatuses.add(item);
			}			
		}
		return targetStatuses;
	}
	
	
	public List<SelectItem> getOfferDetailCommissionStatuses() {
		if ( offerDetailCommissionStatuses == null ) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			offerDetailCommissionStatuses = new LinkedList<SelectItem>();
			for( OfferDetailCommissionStatus status : OfferDetailCommissionStatus.values() ) {
				String name = status.getName(locale);
				SelectItem item = new SelectItem(status, name);
				offerDetailCommissionStatuses.add(item);
			}			
		}
		return offerDetailCommissionStatuses;
	}

	public CommercialActivity getActivity() {
		return null;
	}

	public void setActivity( CommercialActivity activity ) {
	}
	
	public List<SelectItem> getActivities() throws ManagerBeanException {
		if ( activities == null ) {
			refreshActivities();
		}
		return activities;
	}	

	public void refreshActivities() throws ManagerBeanException {
		activities = new LinkedList<SelectItem>();
		IManagerBean activityBean = BeanManager.getManagerBean(CommercialActivity.class);
		Criteria criteria = new Criteria();
		criteria.addOrder(activityBean.getFieldName(IEntityAlias.COMMERCIAL_ACTIVITY_NAME));
		Iterator<ITransferObject> iter = activityBean.getList(criteria).iterator();
		while(iter.hasNext()){
			CommercialActivity activity = (CommercialActivity)iter.next();
			SelectItem item = new SelectItem(activity, activity.getName());
			activities.add(item);
		}
	}
	
	public List<SelectItem> getCommissions() throws ManagerBeanException {
		List<SelectItem> commissions = new LinkedList<SelectItem>();
		IManagerBean commissionBean = BeanManager.getManagerBean(Commission.class);
		Criteria criteria = new Criteria();
		criteria.addOrder(commissionBean.getFieldName(IEntityAlias.COMMISSION_NAME));
		Iterator<ITransferObject> iter = commissionBean.getList(criteria).iterator();
		while (iter.hasNext()) {
			Commission commission = (Commission) iter.next();
			SelectItem item = new SelectItem(commission,commission.getName());
			commissions.add(item);
		}
		return commissions;
	}

	/**
	 * Gets the question types.
	 * 
	 * @return the question types.
	 */
	public List<SelectItem> getQuestionTypes() {
		if ( questionTypes == null ) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			questionTypes = new LinkedList<SelectItem>();
			for (QuestionType auditLevel : QuestionType.values()) {
				String name = auditLevel.getName(locale);
				SelectItem item = new SelectItem(auditLevel, name);
				questionTypes.add(item);
			}
		}
		return questionTypes;
	}	

	public static List<SelectItem> getQuestionValues( Question question ) throws ManagerBeanException {
		List<SelectItem> list = new LinkedList<SelectItem>();
		IManagerBean bean = BeanManager.getManagerBean(QuestionValue.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.QUESTION_VALUE_QUESTION_ID), question.getId());
		for( ITransferObject to : bean.getList(criteria) ) {
			QuestionValue questionValue = (QuestionValue) to;
			Object value = questionValue.getValue( question.getType() );
			SelectItem item = new SelectItem(questionValue.getId(), ObjectUtils.toString(value));
			list.add(item);
		}
		return list;
	}	
	
}