package com.code.aon.ui.commercial.event;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.code.aon.commercial.CommercialActivity;
import com.code.aon.commercial.dao.ICommercialAlias;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.sales.Seller;
import com.code.aon.ui.commercial.controller.CommercialCollectionsController;
import com.code.aon.ui.commercial.controller.ICommercialConstants;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;

public class TargetSearchListener extends ControllerAdapter implements ICommercialConstants {

	private static final Logger LOGGER = Logger.getLogger(TargetSearchListener.class.getName());
	
	private Criteria criteria;
	
	private Date dateFrom;
	
	private Date dateTo;
	
	private Seller seller;
	
	private CommercialActivity activity;
	
	public Date getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(Date dateFrom) {
		this.dateFrom = dateFrom;
	}

	public Date getDateTo() {
		return dateTo;
	}

	public void setDateTo(Date dateTo) {
		this.dateTo = dateTo;
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

	@Override
	public void beforeModelInitialized(ControllerEvent event) throws ControllerListenerException {
		try {
			if ( criteria != getController().getCriteria() ) {			
				completeCriteria();
				criteria = getController().getCriteria();
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error initializing Task Model", e);
		}
	}

	@Override
	public void afterBeanReset(ControllerEvent event) throws ControllerListenerException {
		if ( getController().getTo() != null ) {
			try {			
				init();
			} catch (ManagerBeanException e) {
				LOGGER.log(Level.SEVERE, "Error initializing Task Model", e);
			}
		}
	}
	
	private void init() throws ManagerBeanException {
		this.criteria = null;
		setDateFrom(null);
		setDateTo(null);
		setActivity(null);
		setSeller( new Seller() );
    	CommercialCollectionsController collections = (CommercialCollectionsController) AonUtil.getRegisteredBean(ICommercialConstants.COLLECTIONS_CONTROLLER_NAME);
		collections.refreshActivities();		
	}
	
	private void completeCriteria() throws ManagerBeanException {
		Criteria criteria = getController().getCriteria();
		if (getDateFrom() != null) {
			criteria.addGreaterThanOrEqualExpression("Target.trackings.date", getDateFrom());
		}
		if (getDateTo() != null) {
			criteria.addLessThanOrEqualExpression("Target.trackings.date", getDateTo());
		}
		if ( (getSeller() != null) && (getSeller().getId() != null) ) {
			criteria.addEqualExpression("Target.trackings.seller.id", getSeller().getId());			
		}
		if (getActivity() != null) {
			criteria.addEqualExpression("Target.trackings.activity.id", getActivity().getId());			
		}		
	}
	
}