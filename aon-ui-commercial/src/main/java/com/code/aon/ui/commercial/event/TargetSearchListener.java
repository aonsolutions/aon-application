package com.code.aon.ui.commercial.event;


import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import com.code.aon.commercial.CommercialActivity;
import com.code.aon.commercial.enumeration.CommercialTrackingStatus;
import com.code.aon.commercial.enumeration.TargetStatus;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.seller.Seller;
import com.code.aon.ui.commercial.controller.CommercialCollectionsController;
import com.code.aon.ui.commercial.controller.ICommercialConstants;
import com.code.aon.ui.registry.controller.event.RegistrySearchListener;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class TargetSearchListener extends RegistrySearchListener implements ICommercialConstants {

	private TargetStatus[] targetStatuses;
	
	private Seller seller;
	
	private CommercialActivity activity;
	
	private CommercialTrackingStatus[] trackingStatuses;
	
	private String userName;
		
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
	
	@Deprecated
	public String getUserName() {
		return userName;
	}
	@Deprecated
	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Override
	protected void init() throws ManagerBeanException {
		TargetStatus[] defaultTargetStatus = {TargetStatus.ACTIVE};
		setTargetStatuses(defaultTargetStatus);
		setActivity(null);
		setUserName(null);
		setTrackingStatuses( new CommercialTrackingStatus[0] );
		IManagerBean sellerBean = BeanManager.getManagerBean(Seller.class);
		setSeller( (Seller) sellerBean.createNewTo() );
    	CommercialCollectionsController collections = (CommercialCollectionsController) AonUtil.getRegisteredBean(ICommercialConstants.COLLECTIONS_CONTROLLER_NAME);
		collections.refreshActivities();
		super.init();
	}
	
	@Override
	protected void completeCriteria( Criteria criteria ) throws ManagerBeanException, ExpressionException {
		if (!ArrayUtils.isEmpty(getTargetStatuses())) {
			String status = getController().resolveAlias(IEntityAlias.TARGET_STATUS);
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
		
		
		// ?????????
		if (! StringUtils.isEmpty(getUserName()) ){					
			criteria.addEqualExpression("id", getTargetId());
		}
		// ?????????
		
		super.completeCriteria( criteria );
	}
	
	@SuppressWarnings("unchecked")
	@Deprecated
	private Integer getTargetId() throws ManagerBeanException {
//		String select = "select ec.target "
//			+ "from Ectarget as ec "
//			+ "where ec.login='" + getUserName() + "')))";	
//		Session session = HibernateUtil.getSession(HibernateUtil.getSessionFactoryName());
//		Query query = session.createQuery(select);
//		List<Target> targetList = query.list();
//		if (targetList.size() > 0) {
//			return targetList.get(0).getId();
//		}
		return -1;
	}
}