package com.code.aon.ui.commercial.event;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang.ArrayUtils;

import com.code.aon.commercial.CommercialActivity;
import com.code.aon.commercial.enumeration.CommercialTrackingStatus;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.registry.enumeration.MediaType;
import com.code.aon.sales.Seller;
import com.code.aon.ui.commercial.controller.CommercialCollectionsController;
import com.code.aon.ui.commercial.controller.ICommercialConstants;
import com.code.aon.ui.form.event.ControllerSearchListener;
import com.code.aon.ui.util.AonUtil;

public class TargetSearchListener extends ControllerSearchListener implements ICommercialConstants {

	private Date trackingDateFrom;
	
	private Date trackingDateTo;

	private Seller seller;
	
	private CommercialActivity activity;
	
	private CommercialTrackingStatus[] trackingStatuses;
	
	private List<MediaType> mediaTypes;
	
	public Date getTrackingDateFrom() {
		return trackingDateFrom;
	}

	public void setTrackingDateFrom(Date trackingDateFrom) {
		this.trackingDateFrom = trackingDateFrom;
	}

	public Date getTrackingDateTo() {
		return trackingDateTo;
	}

	public void setTrackingDateTo(Date trackingDateTo) {
		this.trackingDateTo = trackingDateTo;
	}

	public Seller getSeller() {
		return seller;
	}

	public void setSeller(Seller seller) {
		this.seller = seller;
	}
	
	public CommercialActivity getActivity() {
		return activity;
	}

	public void setActivity(CommercialActivity activity) {
		this.activity = activity;
	}	
	
	public CommercialTrackingStatus[] getTrackingStatuses() {
		return trackingStatuses;
	}

	public void setTrackingStatuses(CommercialTrackingStatus[] trackingStatuses) {
		this.trackingStatuses = trackingStatuses;
	}
	
	public List<MediaType> getMediaTypes() {
		return mediaTypes;
	}

	public void setMediaTypes(List<MediaType> mediaTypes) {
		this.mediaTypes = mediaTypes;
	}

	public int getMediaTypesSize() {
		return mediaTypes.size();
	}
	
	@Override
	protected void init() throws ManagerBeanException {
		setTrackingDateFrom(null);
		setTrackingDateTo(null);
		setActivity(null);
		setTrackingStatuses( new CommercialTrackingStatus[0] );
		setSeller( new Seller() );
    	CommercialCollectionsController collections = (CommercialCollectionsController) AonUtil.getRegisteredBean(ICommercialConstants.COLLECTIONS_CONTROLLER_NAME);
		collections.refreshActivities();		
		setMediaTypes( new LinkedList<MediaType>() );
		getMediaTypes().add( null );
	}
	
	@Override
	protected void completeCriteria() throws ManagerBeanException, ExpressionException {
		Criteria criteria = getController().getCriteria();
		if (getTrackingDateFrom() != null) {
			criteria.addGreaterThanOrEqualExpression("Target.trackings.date", getTrackingDateFrom());
		}
		if (getTrackingDateTo() != null) {
			criteria.addLessThanOrEqualExpression("Target.trackings.date", getTrackingDateTo());
		}
		if ( (getSeller() != null) && (getSeller().getId() != null) ) {
			criteria.addEqualExpression("Target.trackings.seller.id", getSeller().getId());			
		}
		if (getActivity() != null) {
			criteria.addEqualExpression("Target.trackings.activity.id", getActivity().getId());			
		}		
		if (! ArrayUtils.isEmpty(getTrackingStatuses()) ) {
			addEnumToCriteria( criteria, "Target.trackings.status", getTrackingStatuses() );
		}
		addEnumToCriteria( criteria, "Target.registry.medias.mediaType", getMediaTypes().toArray() );
	}
	
	public void onAddMediaType( ActionEvent event ) {
		this.mediaTypes.add( null );
	}
	
	public void onRemoveMediaType( ActionEvent event ) {
        FacesContext context = FacesContext.getCurrentInstance();
		int index = Integer.valueOf( context.getExternalContext().getRequestParameterMap().get("index") );		
		this.mediaTypes.remove( index );
		if ( this.mediaTypes.isEmpty() ) {
			this.mediaTypes.add( null );
		}
	}
}