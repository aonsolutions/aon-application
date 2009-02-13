package com.code.aon.ui.commercial.controller;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import com.code.aon.commercial.CommercialActivity;
import com.code.aon.commercial.CommercialTracking;
import com.code.aon.commercial.Target;
import com.code.aon.commercial.dao.ICommercialAlias;
import com.code.aon.commercial.enumeration.CommercialTrackingStatus;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.sales.Seller;
import com.code.aon.ui.form.BasicController;

/**
 * Controller used in the offer maintenance.
 */
public class CommercialTrackingController extends BasicController {
	
	private static final Logger LOGGER = Logger.getLogger(CommercialTrackingController.class.getName());
	
	private Date lastDate;
	
	private Seller lastSeller;
	
	private boolean nextAction;
	
	private CommercialTracking next;
	
	private List<SelectItem> activities;

	private Date dateFrom;
	
	private Date dateTo;
	
	private Seller seller;
	
	private Target target;
	
	private CommercialActivity activity;
	
	private boolean statusPending;
	
	private boolean statusClosed;
	
	public Date getLastDate() {
		return lastDate;
	}

	public void setLastDate(Date lastDate) {
		this.lastDate = lastDate;
	}

	public Seller getLastSeller() {
		return lastSeller;
	}

	public void setLastSeller(Seller lastSeller) {
		this.lastSeller = lastSeller;
	}

	public List<SelectItem> getActivities() {
		return activities;
	}	

	@SuppressWarnings("unchecked")
	public void refreshActivities() throws ManagerBeanException {
		activities = new LinkedList<SelectItem>();
		IManagerBean activityBean = BeanManager.getManagerBean(CommercialActivity.class);
		Criteria criteria = new Criteria();
		criteria.addOrder(activityBean.getFieldName(ICommercialAlias.COMMERCIAL_ACTIVITY_NAME));
		Iterator<ITransferObject> iter = activityBean.getList(criteria).iterator();
		while(iter.hasNext()){
			CommercialActivity activity = (CommercialActivity)iter.next();
			SelectItem item = new SelectItem(activity, activity.getName());
			activities.add(item);
		}
	}

	public boolean isNextAction() {
		return nextAction;
	}

	public void setNextAction(boolean nextAction) {
		this.nextAction = nextAction;
	}

	public CommercialTracking getNext() {
		return next;
	}

	public void setNext(CommercialTracking next) {
		this.next = ( next.getId() != null ) ? next : null;
		setNextAction( this.next != null );
	}	

	public void nextActionChanged( ValueChangeEvent event ) {
		Boolean value = (Boolean) event.getNewValue();
		if ( value ) {
			this.next = new CommercialTracking();
			if ( getLastDate() != null ) {
				this.next.setDate( getLastDate() );	
			}
			this.next.setStatus(CommercialTrackingStatus.PENDING);
		} else {
			this.next = null;
		}
	}

	@Override
	public void onEditSearch(ActionEvent event) {
		setDateFrom(null);
		setDateTo(null);
		setSeller( new Seller() );
		setTarget( new Target() );
		setActivity(null);
		initializeStatusFilter();
		super.onEditSearch(event);
	}
	
	private void initializeStatusFilter() {
		setStatusPending(true);
		setStatusClosed(true);
	}
	
	private void completeStatusCriteria() throws ManagerBeanException {
		if (isStatusClosed() || isStatusPending()) {
			Expression expToAdd = null;
			String alias = getFieldName(ICommercialAlias.COMMERCIAL_TRACKING_STATUS);
			if ( isStatusClosed() ) {
				expToAdd = ExpressionUtilities.getEqualExpression(alias,
						CommercialTrackingStatus.CLOSED);
			}
			if ( isStatusPending() ) {
				Expression exp  = ExpressionUtilities.getEqualExpression(alias,
						CommercialTrackingStatus.PENDING);
				if ( expToAdd != null ) {
					expToAdd = ExpressionUtilities.getOrExpression(expToAdd, exp);
				} else {
					expToAdd = exp;
				}
			}
			getCriteria().addExpression(expToAdd);
		}		
	}
	
	public void completeCriteria() throws ManagerBeanException {
		if (getDateFrom() != null) {
			String alias = getFieldName(ICommercialAlias.COMMERCIAL_TRACKING_DATE); 
			getCriteria().addGreaterThanOrEqualExpression(alias, getDateFrom());
		}
		if (getDateTo() != null) {
			String alias = getFieldName(ICommercialAlias.COMMERCIAL_TRACKING_DATE);
			getCriteria().addLessThanOrEqualExpression(alias, getDateTo());
		}
		if ( (getSeller() != null) && (getSeller().getId() != null) ) {
			String alias = getFieldName(ICommercialAlias.COMMERCIAL_TRACKING_SELLER_ID);
			getCriteria().addEqualExpression(alias, getSeller().getId());			
		}
		if ( (getTarget() != null) && (getTarget().getId() != null) ) {
			String alias = getFieldName(ICommercialAlias.COMMERCIAL_TRACKING_TARGET_ID);
			getCriteria().addEqualExpression(alias, getTarget().getId());			
		}
		if (getActivity() != null) {
			String alias = getFieldName(ICommercialAlias.COMMERCIAL_TRACKING_ACTIVITY_ID);
			getCriteria().addEqualExpression(alias, getActivity().getId());			
		}
		completeStatusCriteria();
	}
	
	// -------------------------------------------------
	// Getters y setters para los campos de la búsqueda.
	// -------------------------------------------------
	
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

	public Target getTarget() {
		return target;
	}

	public void setTarget(Target target) {
		this.target = target;
	}

	public CommercialActivity getActivity() {
		return activity;
	}

	public void setActivity(CommercialActivity activity) {
		this.activity = activity;
	}

	public boolean isStatusPending() {
		return statusPending;
	}

	public void setStatusPending(boolean statusPending) {
		this.statusPending = statusPending;
	}

	public boolean isStatusClosed() {
		return statusClosed;
	}

	public void setStatusClosed(boolean statusClosed) {
		this.statusClosed = statusClosed;
	}	
	
}