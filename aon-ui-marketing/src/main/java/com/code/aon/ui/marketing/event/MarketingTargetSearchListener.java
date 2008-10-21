package com.code.aon.ui.marketing.event;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import com.code.aon.commercial.CommercialActivity;
import com.code.aon.commercial.enumeration.CommercialTrackingStatus;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.marketing.Question;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.registry.enumeration.MediaType;
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

	private Date questionDateFrom;
	
	private Date questionDateTo;	
	
	private String questionText;
	
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
	
	public Date getQuestionDateFrom() {
		return questionDateFrom;
	}

	public void setQuestionDateFrom(Date questionDateFrom) {
		this.questionDateFrom = questionDateFrom;
	}

	public Date getQuestionDateTo() {
		return questionDateTo;
	}

	public void setQuestionDateTo(Date questionDateTo) {
		this.questionDateTo = questionDateTo;
	}
	
	public String getQuestionText() {
		return questionText;
	}

	public void setQuestionText(String questionText) {
		this.questionText = questionText;
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
	public void beforeModelInitialized(ControllerEvent event) throws ControllerListenerException {
		try {
			if ( criteria != getController().getCriteria() ) {			
				completeCriteria();
				criteria = getController().getCriteria();
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error initializing Task Model", e);
		} catch (ExpressionException e) {
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
		setQuestionDateFrom(null);
		setQuestionDateTo(null);
		setQuestionText(null);
    	CommercialCollectionsController collections = (CommercialCollectionsController) AonUtil.getRegisteredBean(ICommercialConstants.COLLECTIONS_CONTROLLER_NAME);
		collections.refreshActivities();		
		setMediaTypes( new LinkedList<MediaType>() );
		getMediaTypes().add( null );
	}
	
	private void addEnumToCriteria( Criteria criteria, String alias, Object[] values ) throws ManagerBeanException {
		Expression expToAdd = null;
		for( Object value : values ) {
			if ( value != null ) {
				if ( expToAdd == null ) {
					expToAdd = ExpressionUtilities.getEqualExpression(alias, value);				
				} else {
					Expression exp  = ExpressionUtilities.getEqualExpression(alias, value);
					expToAdd = ExpressionUtilities.getOrExpression(expToAdd, exp);
				}
			}
		}
		if ( expToAdd != null ) {
			criteria.addExpression(expToAdd);
		}
	}	
	
	private void completeCriteria() throws ManagerBeanException, ExpressionException {
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
		if (getQuestionDateFrom() != null) {
			criteria.addGreaterThanOrEqualExpression("MarketingTarget.profiles.date", getQuestionDateFrom());
		}
		if (getQuestionDateTo() != null) {
			criteria.addLessThanOrEqualExpression("MarketingTarget.profiles.date", getQuestionDateTo());
		}
		if (! StringUtils.isEmpty(getQuestionText()) ) {
			Expression expText = ExpressionUtilities.getExpression(getQuestionText(), "MarketingTarget.profiles.text");
			Expression expNumber = null;
			try {
				expNumber = ExpressionUtilities.getExpression(getQuestionText(), "MarketingTarget.profiles.number");
			} catch (ExpressionException ee ) {
				criteria.addExpression( expText );
			}
			criteria.addExpression( ExpressionUtilities.getOrExpression(expText, expNumber) );
		}
		addEnumToCriteria( criteria, "MarketingTarget.target.registry.medias.mediaType", getMediaTypes().toArray() );
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