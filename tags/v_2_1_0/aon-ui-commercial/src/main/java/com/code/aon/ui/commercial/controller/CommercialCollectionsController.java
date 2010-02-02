package com.code.aon.ui.commercial.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import com.code.aon.commercial.enumeration.CommercialTrackingStatus;
import com.code.aon.commercial.enumeration.OfferDetailStatus;
import com.code.aon.commercial.enumeration.OfferStatus;
import com.code.aon.commercial.enumeration.OfferType;
import com.code.aon.commercial.enumeration.TargetItemStatus;
import com.code.aon.commercial.enumeration.TargetSellerStatus;

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
	
	private List<SelectItem> targetItemStatuses;
	
	private List<SelectItem> targetSellerStatuses;
	
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
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			offerTypes = new LinkedList<SelectItem>();
			for (OfferType type : OfferType.values()) {
				String name = type.getName(locale);
				SelectItem item = new SelectItem(type, name);
				offerTypes.add(item);
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
	
}