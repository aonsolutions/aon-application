package com.code.aon.ui.commercial.event;

import org.apache.commons.lang.ArrayUtils;

import com.code.aon.commercial.CommercialActivity;
import com.code.aon.commercial.dao.ICommercialAlias;
import com.code.aon.commercial.enumeration.CommercialTrackingStatus;
import com.code.aon.commercial.enumeration.TargetStatus;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.seller.Seller;
import com.code.aon.ui.commercial.controller.CommercialCollectionsController;
import com.code.aon.ui.commercial.controller.ICommercialConstants;
import com.code.aon.ui.registry.controller.event.RegistrySearchListener;
import com.code.aon.ui.util.AonUtil;

public class TargetSearchListener extends RegistrySearchListener implements ICommercialConstants {

	private TargetStatus[] targetStatuses;
	
	private Seller seller;
	
	private CommercialActivity activity;
	
	private CommercialTrackingStatus[] trackingStatuses;
	
	public TargetStatus[] getTargetStatuses() {
		return targetStatuses;
	}

	public void setTargetStatuses(TargetStatus[] targetStatuses) {
		this.targetStatuses = targetStatuses;
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
	
	@Override
	protected void init() throws ManagerBeanException {
		TargetStatus[] defaultTargetStatus = {TargetStatus.ACTIVE};
		setTargetStatuses(defaultTargetStatus);
		setActivity(null);
		setTrackingStatuses( new CommercialTrackingStatus[0] );
		setSeller( new Seller() );
    	CommercialCollectionsController collections = (CommercialCollectionsController) AonUtil.getRegisteredBean(ICommercialConstants.COLLECTIONS_CONTROLLER_NAME);
		collections.refreshActivities();
		super.init();
	}
	
	@Override
	protected void completeCriteria() throws ManagerBeanException, ExpressionException {
		Criteria criteria = getController().getCriteria();
		if (!ArrayUtils.isEmpty(getTargetStatuses())) {
			String status = getController().resolveAlias(ICommercialAlias.TARGET_STATUS);
			addEnumToCriteria(criteria, status, getTargetStatuses());
		}
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
		super.completeCriteria();
	}
	
}