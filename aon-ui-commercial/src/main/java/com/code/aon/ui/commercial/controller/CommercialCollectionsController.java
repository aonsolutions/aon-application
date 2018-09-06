package com.code.aon.ui.commercial.controller;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.model.SelectItem;

import com.code.aon.commercial.CommercialActivity;
import com.code.aon.commercial.Commission;
import com.code.aon.commercial.enumeration.Advertising;
import com.code.aon.commercial.enumeration.CommercialTrackingStatus;
import com.code.aon.commercial.enumeration.OfferDetailCommissionStatus;
import com.code.aon.commercial.enumeration.OfferDetailStatus;
import com.code.aon.commercial.enumeration.OfferStatus;
import com.code.aon.commercial.enumeration.OfferType;
import com.code.aon.commercial.enumeration.ProjectSource;
import com.code.aon.commercial.enumeration.ProjectStatus;
import com.code.aon.commercial.enumeration.TargetStatus;
import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.commercial.enumeration.DeduplicationType;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

/**
 * Controller used to get Collections related with clasess in <code>com.code.aon.commercial</code>
 * 
 * @author Consulting & Development. Joseba Urkiri - 6-sept-2006
 */
public class CommercialCollectionsController implements Serializable {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private List<SelectItem> offerStatuses;
	
	private List<SelectItem> offerTypes;
	
	private List<SelectItem> offerDetailStatuses;
	
	private List<SelectItem> commercialTrackingStatuses;
	
	private List<SelectItem> projectStatuses;
	
	private List<SelectItem> projectSources;
	
	private List<SelectItem> advertisings;

	private List<SelectItem> targetStatuses;
	
	private List<SelectItem> activities;
	
	private List<SelectItem> offerDetailCommissionStatuses;
	
	private List<SelectItem> deduplicationTypes;

	
	/**
	 * Gets the offer statuses.
	 * 
	 * @return the offer statuses
	 */
	public List<SelectItem> getOfferStatuses() {
		if ( offerStatuses == null ) {
			Locale locale = AonUtil.getCurrentLocale();
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
			boolean otherEnabled = (Boolean)AonUtil.getConfigurationController().getBean()
				.get(ICommercialConstants.CONFIG_OFFER_BEAN)
				.get(ICommercialConstants.SHOW_TAS_DATA);
			Locale locale = AonUtil.getCurrentLocale();
			offerTypes = new LinkedList<SelectItem>();
			for (OfferType type : OfferType.values()) {
				String name = type.getName(locale);
				SelectItem item = new SelectItem(type, name);
				if ( type!=OfferType.OTHER || type==OfferType.OTHER && otherEnabled) {  
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
			Locale locale = AonUtil.getCurrentLocale();
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
			Locale locale = AonUtil.getCurrentLocale();
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
			Locale locale = AonUtil.getCurrentLocale();
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
			Locale locale = AonUtil.getCurrentLocale();
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
	 * Gets the advertisings.
	 * 
	 * @return the advertisings
	 */
	public List<SelectItem> getAdvertisings() {
		if ( advertisings == null ) {
			Locale locale = AonUtil.getCurrentLocale();
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
			Locale locale = AonUtil.getCurrentLocale();
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
			Locale locale = AonUtil.getCurrentLocale();
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

	public List<SelectItem> getDeduplicationTypes() {
		if ( deduplicationTypes == null ) {
			Locale locale = AonUtil.getCurrentLocale();
			deduplicationTypes = new LinkedList<SelectItem>();
			for( DeduplicationType type : DeduplicationType.values() ) {
				String name = type.getName(locale);
				SelectItem item = new SelectItem(type, name);
				deduplicationTypes.add(item);
			}			
		}
		return deduplicationTypes;
	}
	
}