package com.code.aon.ui.marketing.event;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.ArrayUtils;

import com.code.aon.commercial.CommercialActivity;
import com.code.aon.commercial.enumeration.CommercialTrackingStatus;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.marketing.Question;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.sales.Seller;
import com.code.aon.ui.commercial.controller.CommercialCollectionsController;
import com.code.aon.ui.commercial.controller.ICommercialConstants;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;

public class MarketingTargetSearchListener extends ControllerAdapter implements ICommercialConstants {

	private static final Logger LOGGER = Logger.getLogger(MarketingTargetSearchListener.class.getName());
	
	private Criteria criteria;
	
	private Date trackingDateFrom;
	
	private Date trackingDateTo;

	private Seller seller;
	
	private CommercialActivity activity;
	
	private CommercialTrackingStatus[] trackingStatuses;

	private Date profileDateFrom;
	
	private Date profileDateTo;	

	private Question question;
		
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

	public Question getQuestion() {
		return question;
	}

	public void setQuestion(Question question) {
		this.question = question;
	}
	
	public Date getProfileDateFrom() {
		return profileDateFrom;
	}

	public void setProfileDateFrom(Date profileDateFrom) {
		this.profileDateFrom = profileDateFrom;
	}

	public Date getProfileDateTo() {
		return profileDateTo;
	}

	public void setProfileDateTo(Date profileDateTo) {
		this.profileDateTo = profileDateTo;
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
		setTrackingDateFrom(null);
		setTrackingDateTo(null);
		setActivity(null);
		setTrackingStatuses( new CommercialTrackingStatus[0] );
		setSeller( new Seller() );
		setQuestion( new Question() );
		setProfileDateFrom(null);
		setProfileDateTo(null);
    	CommercialCollectionsController collections = (CommercialCollectionsController) AonUtil.getRegisteredBean(ICommercialConstants.COLLECTIONS_CONTROLLER_NAME);
		collections.refreshActivities();		
	}
	
	private void addEnumToCriteria( Criteria criteria, String alias, Object[] values ) throws ManagerBeanException {
		Expression expToAdd = null;
		for( Object value : values ) {
			if ( expToAdd == null ) {
				expToAdd = ExpressionUtilities.getEqualExpression(alias, value);				
			} else {
				Expression exp  = ExpressionUtilities.getEqualExpression(alias, value);
				expToAdd = ExpressionUtilities.getOrExpression(expToAdd, exp);
			}
		}
		criteria.addExpression(expToAdd);	
	}	
	
	private void completeCriteria() throws ManagerBeanException {
		Criteria criteria = getController().getCriteria();
		if (getTrackingDateFrom() != null) {
			criteria.addGreaterThanOrEqualExpression("MarketingTarget.target.trackings.date", getTrackingDateFrom());
		}
		if (getTrackingDateTo() != null) {
			criteria.addLessThanOrEqualExpression("MarketingTarget.target.trackings.date", getTrackingDateTo());
		}
		if ( (getSeller() != null) && (getSeller().getId() != null) ) {
			criteria.addEqualExpression("MarketingTarget.target.trackings.seller.id", getSeller().getId());			
		}
		if (getActivity() != null) {
			criteria.addEqualExpression("MarketingTarget.target.trackings.activity.id", getActivity().getId());			
		}		
		if (! ArrayUtils.isEmpty(getTrackingStatuses()) ) {
			addEnumToCriteria( criteria, "MarketingTarget.target.trackings.status", getTrackingStatuses() );
		}
		if ( (getQuestion() != null) && (getQuestion().getId() != null) ) {
			criteria.addEqualExpression("MarketingTarget.profiles.question.id", getQuestion().getId());			
		}
		if (getProfileDateFrom() != null) {
			criteria.addGreaterThanOrEqualExpression("MarketingTarget.profiles.lastUpdate", getProfileDateFrom());
		}
		if (getProfileDateTo() != null) {
			criteria.addLessThanOrEqualExpression("MarketingTarget.profiles.lastUpdate", getProfileDateTo());
		}
	}
	
}