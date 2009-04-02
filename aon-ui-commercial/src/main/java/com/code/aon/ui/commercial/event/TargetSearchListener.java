package com.code.aon.ui.commercial.event;

import java.util.LinkedList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import com.code.aon.commercial.CommercialActivity;
import com.code.aon.commercial.enumeration.CommercialTrackingStatus;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.registry.enumeration.MediaType;
import com.code.aon.seller.Seller;
import com.code.aon.ui.commercial.controller.CommercialCollectionsController;
import com.code.aon.ui.commercial.controller.ICommercialConstants;
import com.code.aon.ui.form.event.ControllerSearchListener;
import com.code.aon.ui.util.AonUtil;

public class TargetSearchListener extends ControllerSearchListener implements ICommercialConstants {

	private Seller seller;
	
	private CommercialActivity activity;
	
	private CommercialTrackingStatus[] trackingStatuses;
	
	private List<MediaType> mediaTypes;
	
	private List<String> segments;

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
		
	public List<String> getSegments() {
		return segments;
	}

	public void setSegments(List<String> segments) {
		this.segments = segments;
	}
	
	public int getSegmentsSize() {
		return this.segments.size();
	}	

	public List<Integer> getSegmentsIds() {
		List<Integer> ids = new LinkedList<Integer>();
		for( String segment : getSegments() ) {
			if (! StringUtils.isBlank(segment) ) {
				ids.add( Integer.valueOf(segment) );
			}
		}
		return ids;
	}
	
	@Override
	protected void init() throws ManagerBeanException {
		setActivity(null);
		setTrackingStatuses( new CommercialTrackingStatus[0] );
		setSeller( new Seller() );
    	CommercialCollectionsController collections = (CommercialCollectionsController) AonUtil.getRegisteredBean(ICommercialConstants.COLLECTIONS_CONTROLLER_NAME);
		collections.refreshActivities();		
		setMediaTypes( new LinkedList<MediaType>() );
		getMediaTypes().add( null );
		setSegments( new LinkedList<String>() );
		getSegments().add( null );
	}
	
	@Override
	protected void completeCriteria() throws ManagerBeanException, ExpressionException {
		Criteria criteria = getController().getCriteria();
		if ( (getSeller() != null) && (getSeller().getId() != null) ) {
			String activity = getController().resolveAlias("Target_trackings_seller_id");
			criteria.addEqualExpression(activity, getSeller().getId());			
		}
		if (getActivity() != null) {
			String activity = getController().resolveAlias("Target_trackings_activity_id");
			criteria.addEqualExpression(activity, getActivity().getId());			
		}		
		if (! ArrayUtils.isEmpty(getTrackingStatuses()) ) {
			String status = getController().resolveAlias("Target_trackings_status");
			addEnumToCriteria( criteria, status, getTrackingStatuses() );
		}
		String mediaType = getController().resolveAlias("Target_registry_medias_mediaType");
		addEnumToCriteria( criteria, mediaType, getMediaTypes().toArray() );
		String segment = getController().resolveAlias("Target_segments_segment_id");
		addEnumToCriteria( criteria, segment, getSegmentsIds().toArray() );
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

	public void onAddSegment( ActionEvent event ) {
		getSegments().add( null );
	}
	
	public void onRemoveSegment( ActionEvent event ) {
        FacesContext context = FacesContext.getCurrentInstance();
		int index = Integer.valueOf( context.getExternalContext().getRequestParameterMap().get("index") );		
		getSegments().remove( index );
		if ( getSegments().isEmpty() ) {
			getSegments().add( null );
		}
	}}